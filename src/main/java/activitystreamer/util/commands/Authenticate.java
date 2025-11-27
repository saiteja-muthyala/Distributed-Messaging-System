package activitystreamer.util.commands;

import activitystreamer.util.Settings;
import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public class Authenticate extends Command {
    protected String secret;

    public Authenticate() {
        super();
        secret = Settings.getSecret();
    }

    public Authenticate(JsonObject jsonObject) throws ValidationException {
        super();
        validate(jsonObject);
        setSecret(jsonObject.get("secret").getAsString());
    }

    @Override
    protected String commandName() {
        return "AUTHENTICATE";
    }

    @Override
    public void validate(JsonObject jsonObject) throws ValidationException {
        super.validate(jsonObject);

        if (!jsonObject.has("secret")) {
            throw new ValidationException("No 'secret' field in the request.");
        }

        final int NUMBER_OF_FIELDS = 2;
        if (jsonObject.entrySet().size() > NUMBER_OF_FIELDS) {
            throw new ValidationException("There are fields that are not required in the request.");
        }
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

}
