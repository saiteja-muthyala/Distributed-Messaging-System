package activitystreamer.server.Repository;

/**
 * Denote an entity of the user information
 */
public class UserInformation {
    private String username;
    private String secret;

    public UserInformation(String username, String secret) {
        this.username = username;
        this.secret = secret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean checkUserMatch(String givenUsername, String givenSecret) {
        return username.equals(givenUsername) && secret.equals(givenSecret);
    }
}
