package devarea.fr.web.challenges;

public class ChallengeErrorException extends Exception {

    private String message;

    public ChallengeErrorException(String s) {
        this.message = s;
    }


    @Override
    public String getMessage() {
        return message;
    }
}
