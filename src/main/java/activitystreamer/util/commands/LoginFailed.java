package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public class LoginFailed extends Info {

    public LoginFailed() {
        super();
    }

    public LoginFailed(JsonObject jsonObject) throws ValidationException {
        super(jsonObject);
    }

    public LoginFailed(String info) {
        super(info);
    }

    @Override
    protected String commandName() {
        return "LOGIN_FAILED";
    }
}
