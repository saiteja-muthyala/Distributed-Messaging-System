package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public abstract class UserSecret extends Command {

    protected String username;
    protected String secret;

    public UserSecret() {
        super();
    }

    // Used to construct the response from the server
    public UserSecret(String username, String secret) {
        super();
        this.username = username;
        this.secret = secret;
    }

    // Used to construct the command object from incoming message
    public UserSecret(JsonObject jsonObject) throws ValidationException {
        super();
        validate(jsonObject);
        setUsername(jsonObject.get("username").getAsString());
        if (!username.equals("anonymous")) {
            setSecret(jsonObject.get("secret").getAsString());
        }
    }

    public String getSecret() {
        return secret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public void validate(JsonObject jsonObject) throws ValidationException {
        super.validate(jsonObject);

        if (!jsonObject.has("username")) {
            throw new ValidationException("No 'username' field in the request.");
        }

        if (!jsonObject.has("secret")) {
            if (jsonObject.get("username").getAsString().equals("anonymous")) {
                // If it is anonymous, then it does not need a secret
                return;
            }
            throw new ValidationException("No 'secret' field in the request.");
        }
    }
}
