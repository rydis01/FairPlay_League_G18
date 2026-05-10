package FairplayLeagueG18.service;

import FairplayLeagueG18.database.CouponDAO;
import FairplayLeagueG18.database.LeagueDAO;
import FairplayLeagueG18.database.RoundDAO;
import FairplayLeagueG18.model.Coupon;
import FairplayLeagueG18.model.LeagueMember;
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
    private CouponDAO couponDAO;
    private RoundDAO roundDAO;
    private LeagueDAO leagueDAO;

    public ScoreService() {
        this.couponDAO = new CouponDAO();
        this.roundDAO = new RoundDAO();
        this.leagueDAO = new LeagueDAO();

    }

    // Rätta en kupong och returnera antalet rätt
    public void gradeCoupon(int couponId) {

        // 1. Hämta kupongen
        Coupon userCoupon = couponDAO.getCoupon(couponId);

        if (userCoupon == null) {
            System.out.println("Kupong saknas");
            return;
        }

        if (userCoupon.getGraded()) {
            return;
        }

        int roundId = userCoupon.getRoundId();

        List<String> correctResults = roundDAO.getResultsFromRound(roundId);

        Map<Integer, String> tips = userCoupon.getTips();

        int correctCount = 0;

        for (int i = 1; i <= correctResults.size(); i++) {
            String userTip = tips.get(i);
            String correct = correctResults.get(i - 1);

            if (userTip != null && userTip.equals(correct)) {
                correctCount++;
            }
        }

        userCoupon.setCorrectCount(correctCount);
        userCoupon.setGraded(true);

        couponDAO.updateCorrectCountCoupon(userCoupon);
    }
    // Rättar alla kuponger i en omgång och fördelar potten för en liga
    // Pott = antal spelare × 100
    // 8 rätt → 60%, 7 rätt → 30%, 6 rätt → 10%
    public void settleRound(int roundId, int leagueId) {

        // 1. Hämta alla medlemmar i ligan
        List<LeagueMember> members = leagueDAO.getMembersByLeagueIdSortedByScore(leagueId);
        int playerCount = members.size();
        int pot = playerCount * 100;

        // 2. Hämta alla kuponger för omgången
        Map<Integer, Integer> userToCoupon = couponDAO.getCouponIdsForRound(roundId);

        for (LeagueMember member : members) {
            int userId = member.getUserId();

            if (userToCoupon.containsKey(userId)) {
                int couponId = userToCoupon.get(userId);
                gradeCoupon(couponId);
            }
        }

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

        Map<Integer, Integer> tierPercent = Map.of(8, 60, 7, 30, 6, 10);

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
