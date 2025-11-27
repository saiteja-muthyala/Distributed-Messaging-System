package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public class LoginSuccess extends Info {
    public LoginSuccess() {
        super();
    }

    public LoginSuccess(JsonObject jsonObject) throws ValidationException {
        super(jsonObject);
    }

    public LoginSuccess(String info) {
        super(info);
    }

    @Override
    protected String commandName() {
        return "LOGIN_SUCCESS";
    }
}
