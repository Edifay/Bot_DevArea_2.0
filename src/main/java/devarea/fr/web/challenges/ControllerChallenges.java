package devarea.fr.web.challenges;


import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static devarea.fr.web.SpringBackend.checkStatus;
import static devarea.fr.web.challenges.ChallengesHandler.checkKey;
import static devarea.fr.web.challenges.ChallengesHandler.checkSessionId;

@CrossOrigin()
@RestController
@RequestMapping("challenges")
public class ControllerChallenges {

    @GetMapping("create_session")
    public static int createSession(@RequestParam(value = "key") String clientKey) throws IllegalAccessException {
        checkStatus();
        checkKey(clientKey);

        return ChallengesHandler.createNewSession(clientKey);
    }

    @GetMapping("challenges_accomplished")
    public static List<String> getChallengesAccomplished(@RequestParam(value = "key") final String clientKey) throws IllegalAccessException {
        checkStatus();
        checkKey(clientKey);

        return ChallengesHandler.getAccomplishedBy(clientKey);
    }

    @GetMapping("load_challenge")
    public static SimplePacket loadChallenge(@RequestParam final int sessionId, @RequestParam final String challenge) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        checkStatus();
        checkSessionId(sessionId);

        return ChallengesHandler.startChallengeOn(sessionId, challenge);
    }

    @RequestMapping("execute_on_challenge/help")
    public static SimplePacket help(@RequestParam final int sessionId, @RequestBody final SimplePacket packet) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        return new SimplePacket("", "Vous pouvez utiliser la commande 'load : <nom_du_challenge>' pour charger un challenge.");
    }
    @RequestMapping("execute_on_challenge/load")
    public static SimplePacket loadChallenge(@RequestParam final int sessionId, @RequestBody final SimplePacket packet) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        return loadChallenge(sessionId, packet.getData());
    }

    @RequestMapping("execute_on_challenge")
    public static SimplePacket executeOnChallengeEmptyActionV1(@RequestParam final int sessionId, @RequestBody final SimplePacket packet) throws IllegalAccessException {
        return executeOnChallenge(sessionId, null, packet);
    }

    @RequestMapping("execute_on_challenge/")
    public static SimplePacket executeOnChallengeEmptyActionV2(@RequestParam final int sessionId, @RequestBody final SimplePacket packet) throws IllegalAccessException {
        return executeOnChallenge(sessionId, null, packet);
    }

    @RequestMapping("execute_on_challenge/{action}")
    public static SimplePacket executeOnChallenge(@RequestParam final int sessionId, @PathVariable final String action, @RequestBody final SimplePacket packet) throws IllegalAccessException {
        checkStatus();
        checkSessionId(sessionId);

        return ChallengesHandler.executeOnChallenge(sessionId, action, packet);
    }

}
