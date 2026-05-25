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

@Service
public class CouponService {

    private final CouponDAO couponDAO;
    private final MatchDAO matchDAO;
    private final RoundDAO roundDAO;

    public CouponService() {
        this.roundDAO = new RoundDAO();
        this.couponDAO = new CouponDAO();
        this.matchDAO = new MatchDAO();

    }

    // Skapa och spara en kupong med alla 8 tips
    public void submitCoupon(int userId, int gameweekId, int leagueId, Map<Integer, String> tips) {

        // Kollar efter dubbletter.
        if (couponDAO.couponExists(userId, gameweekId, leagueId)) {
            System.out.println("Användaren har redan lämnat en kupong för den här omgången.");
            return;
        }

        if (roundDAO.isLocked(gameweekId)) {
            System.out.println("Omgången är låst — deadline har passerat.");
            return;
        }

        // Validera antal tips
        if (tips.size() != 8) {
            System.out.println("En kupong måste ha exakt 8 tips!");
            return;
        }

        // Validera innehåll
        for (String tip : tips.values()) {
            if (!tip.equals("1") && !tip.equals("X") && !tip.equals("2")) {
                System.out.println("Ogiltigt tips: " + tip + ". Måste vara 1, X eller 2.");
                return;
            }
        }

        // Skapa kupongobjekt
        Coupon coupon = new Coupon(userId, gameweekId, leagueId);
        coupon.setTips(tips);

        // Spara kupong + picks
        couponDAO.saveCoupon(coupon);
    }

    // Hämta kupong via Coupon_ID
    public Coupon getCoupon(int couponId) {
        return couponDAO.getCoupon(couponId);
    }


    // Hämta alla kuponger för en användare
    public List<Coupon> getCouponsByUserId(int userId) {
        return couponDAO.getCouponsByUserId(userId);
    }

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

    public List<Integer> get(int gameweekId){
        return couponDAO.getLeagueIdsForGameweek(gameweekId);
    }
}