package devarea.fr.web.backend.rest;

import devarea.fr.db.DBManager;
import devarea.fr.db.data.DBMemberChallenge;
import devarea.fr.discord.cache.MemberCache;
import devarea.fr.web.backend.entities.WebValidatedChallenge;
import devarea.fr.web.backend.entities.WebValidatedChallengeCard;
import devarea.fr.web.challenges.ChallengesHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

import static devarea.fr.web.SpringBackend.checkStatus;

@CrossOrigin()
@RestController
@RequestMapping("challenges")
public class ControllerChallenge {


    @GetMapping("activity")
    public static ArrayList<WebValidatedChallengeCard> getChallengesActivity() {
        checkStatus();

        ArrayList<DBMemberChallenge> challenges = DBManager.getChallenges();
        ArrayList<WebValidatedChallengeCard> sorted = new ArrayList<>();

        for (DBMemberChallenge chall : challenges) {
            for (DBMemberChallenge.DBValidatedChallenge validated : chall.getFullChallengesAccomplished()) {

                if (!MemberCache.contain(chall.getId()))
                    continue;

                WebValidatedChallengeCard card = new WebValidatedChallengeCard(MemberCache.get(chall.getId()), WebValidatedChallenge.of(validated));

                if (sorted.size() <= 10 || sorted.get(sorted.size() - 1).getDate() < card.getDate())
                    insertIn(card, sorted);
            }
        }

        return sorted;
    }

    @GetMapping("map")
    public static HashMap<String, String[]> getChallengeMap() {
        checkStatus();

        return ChallengesHandler.getChallengeMap();
    }

    private static void insertIn(final WebValidatedChallengeCard challenge, ArrayList<WebValidatedChallengeCard> list) {
        if (list.isEmpty()) {
            list.add(challenge);
            return;
        }

        for (int i = 0; i < list.size(); i++)
            if (list.get(i).getDate() < challenge.getDate()) {
                list.add(i, challenge);
                break;
            }

        if (list.size() > 10)
            list.remove(list.size() - 1);
    }

}
