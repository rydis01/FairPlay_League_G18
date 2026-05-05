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
    public void gradeCoupon(int userId, int roundId) {
        Coupon userCoupon = couponDAO.getCoupon(userId, roundId);

        if(userCoupon.getGraded()){
            return;
        }

        List <String> correctCoupon = roundDAO.getResultsFromRound(roundId);

        // Jämför användarens tips med de rätta resultaten
        int correctCount = 0;
        Map<Integer, String> tips = userCoupon.getTips();

        for (int i = 0; i < correctCoupon.size(); i++) {
            String userTip = tips.get(i);
            String correctResult = correctCoupon.get(i);

            // Om tippet matchar det rätta resultatet, öka räknaren
            if (userTip != null && userTip.equals(correctResult)) {
                correctCount++;
            }
        }

        userCoupon.setGraded(true);
        userCoupon.setCorrectCount(correctCount);
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

        // 2. Rätta varje spelares kupong
        for (LeagueMember member : members) {
            gradeCoupon(member.getUserId(), roundId);
        }

        // 3. Gruppera spelare efter antal rätt
        Map<Integer, List<Integer>> groups = new HashMap<>();
        for (LeagueMember member : members) {
            Coupon coupon = couponDAO.getCoupon(member.getUserId(), roundId);
            if (coupon != null) {
                int correct = coupon.getCorrectCount();
                groups.computeIfAbsent(correct, k -> new ArrayList<>()).add(member.getUserId());
            }
        }

        // 4. Fördela potten: 8 rätt → 60%, 7 rätt → 30%, 6 rätt → 10%
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
