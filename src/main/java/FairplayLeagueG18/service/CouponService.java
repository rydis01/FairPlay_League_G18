package FairplayLeagueG18.service;

import FairplayLeagueG18.database.CouponDAO;
import FairplayLeagueG18.database.MatchDAO;
import FairplayLeagueG18.database.RoundDAO;
import FairplayLeagueG18.model.Coupon;
import FairplayLeagueG18.model.Match;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hanterar affärslogiken för inlämning och hämtning av kuponger.
 * Fungerar som bryggan mellan kontrollern och databaslagret (CouponDAO, MatchDAO, RoundDAO).
 * @author Carl Rydengård & Gustav Johnsson
 */
@Service
public class CouponService {
    private static final int REQUIRED_TIPS_COUNT = 8;

    private final CouponDAO couponDAO;
    private final MatchDAO matchDAO;
    private final RoundDAO roundDAO;

    /**
     * Standardkonstruktor som initierar nödvändiga DAO-klasser.
     */
    public CouponService() {
        this.roundDAO = new RoundDAO();
        this.couponDAO = new CouponDAO();
        this.matchDAO = new MatchDAO();
    }

    /**
     * Validerar och sparar en inlämnad kupong med tips i databasen.
     * Kontrollerar för dubbletter, om omgången är låst och att rätt antal och typ av tips lämnats.
     *
     * @param userId     ID för användaren som lämnar in kupongen
     * @param gameweekId ID för omgången kupongen gäller
     * @param leagueId   ID för ligan kupongen tillhör
     * @param tips       en map med match-ID som nyckel och tipstecken ("1", "X", "2") som värde
     */
    public void submitCoupon(int userId, int gameweekId, int leagueId, Map<Integer, String> tips) {

        // Kollar efter dubbletter.
        if (couponDAO.couponExists(userId, gameweekId, leagueId)) {
            System.err.println("Användaren har redan lämnat en kupong för den här omgången.");
            return;
        }

        if (roundDAO.isLocked(gameweekId)) {
            System.err.println("Omgången är låst — deadline har passerat.");
            return;
        }

        // Validera antal tips
        if (tips.size() != REQUIRED_TIPS_COUNT) {
            System.err.println("En kupong måste ha exakt " + REQUIRED_TIPS_COUNT + " tips!");
            return;
        }

        // Validera innehåll (Yoda-conditions skyddar mot NullPointerException)
        for (String tip : tips.values()) {
            if (!"1".equals(tip) && !"X".equals(tip) && !"2".equals(tip)) {
                System.err.println("Ogiltigt tips: " + tip + ". Måste vara 1, X eller 2.");
                return;
            }
        }

        // Skapa kupongobjekt
        Coupon coupon = new Coupon(userId, gameweekId, leagueId);
        coupon.setTips(tips);

        // Spara kupong + picks
        couponDAO.saveCoupon(coupon);
    }

    /**
     * Hämtar en specifik kupong baserat på dess ID.
     *
     * @param couponId ID för kupongen
     * @return ett Coupon-objekt, eller null om den inte hittas
     */
    public Coupon getCoupon(int couponId) {
        return couponDAO.getCoupon(couponId);
    }

    /**
     * Hämtar alla kuponger som en specifik användare har lämnat in.
     *
     * @param userId ID för användaren
     * @return en lista med användarens Coupon-objekt
     */
    public List<Coupon> getCouponsByUserId(int userId) {
        return couponDAO.getCouponsByUserId(userId);
    }

    /**
     * Hämtar detaljerad information om en kupong, inklusive lagnamn och matchernas faktiska resultat.
     * Används för att rendera kuponghistorik i frontend.
     *
     * @param couponId ID för kupongen
     * @return en map med kupongens detaljer (id, roundId och en lista med tipsdetaljer), eller null om kupongen saknas
     */
    public Map<String, Object> getCouponDetails(int couponId) {

        Coupon coupon = couponDAO.getCoupon(couponId);
        if (coupon == null) return null;

        List<Match> matches = matchDAO.getMatchesByGameweek(coupon.getRoundId());

        List<Map<String, Object>> tipsList = new ArrayList<>();

        for (Match m : matches) {
            String choice = coupon.getTip(m.getId());

            Map<String, Object> tipObj = new HashMap<>();
            tipObj.put("match", m.getHomeTeam() + " – " + m.getAwayTeam());
            tipObj.put("choice", choice);
            tipObj.put("correctResult", m.getResult());

            tipsList.add(tipObj);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", coupon.getId());
        response.put("roundId", coupon.getRoundId());
        response.put("tips", tipsList);

        return response;
    }

    /**
     * Hämtar ID:n för alla ligor som har inlämnade kuponger för en given omgång.
     *
     * @param gameweekId ID för omgången
     * @return en lista med liga-ID:n
     */
    public List<Integer> getLeagueIdsForGameweek(int gameweekId) {
        return couponDAO.getLeagueIdsForGameweek(gameweekId);
    }
}