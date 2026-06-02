package FairplayLeagueG18.service;

import FairplayLeagueG18.database.CouponDAO;
import FairplayLeagueG18.database.LeagueDAO;
import FairplayLeagueG18.database.MatchDAO;
import FairplayLeagueG18.database.RoundDAO;
import FairplayLeagueG18.model.Coupon;
import FairplayLeagueG18.model.LeagueMember;
import FairplayLeagueG18.model.Match;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hanterar poängberäkning och leaderboards.
 * Ansvarar för att räkna ut poäng efter varje omgång och visa topplistan.
 */
@Service
public class ScoreService {

    // Konstanter för poängberäkning och prispott för att undvika magiska nummer
    private static final int POT_CONTRIBUTION_PER_PLAYER = 100;
    private static final int TIPS_PER_ROUND = 8;
    private static final int TIER_8_PERCENT = 60;
    private static final int TIER_7_PERCENT = 30;
    private static final int TIER_6_PERCENT = 10;

    private final CouponDAO couponDAO;
    private final RoundDAO roundDAO;
    private final LeagueDAO leagueDAO;
    private final MatchDAO matchDAO;

    /**
     * Standardkonstruktor som initierar nödvändiga DAO-klasser.
     */
    public ScoreService() {
        this.couponDAO = new CouponDAO();
        this.roundDAO = new RoundDAO();
        this.leagueDAO = new LeagueDAO();
        this.matchDAO = new MatchDAO();
    }

    /**
     * Rättar en specifik kupong genom att jämföra användarens tips med de faktiska matchresultaten.
     * Uppdaterar kupongens antal rätt och markerar den som rättad i databasen.
     *
     * @param couponId ID för kupongen som ska rättas
     */
    public void gradeCoupon(int couponId) {

        Coupon userCoupon = couponDAO.getCoupon(couponId);

        if (userCoupon == null) {
            System.err.println("Kupong saknas för id: " + couponId);
            return;
        }

        if (userCoupon.getGraded()) {
            return;
        }

        int roundId = userCoupon.getRoundId();
        List<Match> matches = matchDAO.getMatchesByGameweek(roundId);
        Map<Integer, String> tips = userCoupon.getTips();

        int correctCount = 0;

        for (Match m : matches) {
            String userTip = tips.get(m.getId());
            String correct = m.getResult();

            if (userTip != null && userTip.equals(correct)) {
                correctCount++;
            }
        }

        userCoupon.setCorrectCount(correctCount);
        userCoupon.setGraded(true);

        couponDAO.updateCorrectCountCoupon(userCoupon);
    }

    /**
     * Rättar alla kuponger i en omgång och fördelar potten för en specifik liga.
     * Potten baseras på antalet medlemmar (antal spelare × 100).
     * Poängutdelning sker procentuellt: 8 rätt → 60%, 7 rätt → 30%, 6 rätt → 10%.
     *
     * @param roundId  ID för omgången som rättas
     * @param leagueId ID för ligan där potten och poängen ska fördelas
     */
    public void settleRound(int roundId, int leagueId) {

        // 1. Hämta alla medlemmar i ligan och beräkna potten
        List<LeagueMember> members = leagueDAO.getMembersByLeagueIdSortedByScore(leagueId);
        int playerCount = members.size();
        int pot = playerCount * POT_CONTRIBUTION_PER_PLAYER;

        // 2. Hämta alla kuponger för omgången och rätta dem
        Map<Integer, Integer> userToCoupon = couponDAO.getCouponIdsForRound(roundId);

        for (LeagueMember member : members) {
            int userId = member.getUserId();

            if (userToCoupon.containsKey(userId)) {
                int couponId = userToCoupon.get(userId);
                gradeCoupon(couponId);
            }
        }

        // 3. Gruppera användare baserat på antal rätta tips
        Map<Integer, List<Integer>> groups = new HashMap<>();

        for (LeagueMember member : members) {
            int userId = member.getUserId();

            if (userToCoupon.containsKey(userId)) {
                int couponId = userToCoupon.get(userId);
                Coupon coupon = couponDAO.getCoupon(couponId);

                int correct = coupon.getCorrectCount();
                groups.computeIfAbsent(correct, k -> new ArrayList<>()).add(userId);
            }
        }

        // 4. Fördela poäng baserat på definierade procentnivåer
        Map<Integer, Integer> tierPercent = Map.of(
                TIPS_PER_ROUND, TIER_8_PERCENT,
                TIPS_PER_ROUND - 1, TIER_7_PERCENT,
                TIPS_PER_ROUND - 2, TIER_6_PERCENT
        );

        for (Map.Entry<Integer, Integer> tier : tierPercent.entrySet()) {
            int correctNeeded = tier.getKey();
            int percent = tier.getValue();
            List<Integer> winners = groups.getOrDefault(correctNeeded, List.of());

            if (!winners.isEmpty()) {
                int tierPool = pot * percent / 100;
                int share = tierPool / winners.size();

                for (int userId : winners) {
                    leagueDAO.addScoreToMember(leagueId, userId, share);
                }
            }
        }
    }
}