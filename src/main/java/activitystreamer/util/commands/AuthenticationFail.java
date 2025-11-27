package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public class AuthenticationFail extends Info {
    public AuthenticationFail() {
        super();
    }

    public AuthenticationFail(String info) {
        super(info);
    }

    public AuthenticationFail(JsonObject jsonObject) throws ValidationException {
        super(jsonObject);
    }

    @Override
    protected String commandName() {
        return "AUTHENTICATION_FAIL";
    }
}
