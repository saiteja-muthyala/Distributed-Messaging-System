package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public class Login extends UserSecret {
    public Login() {
        super();
    }

    public Login(JsonObject jsonObject) throws ValidationException {
        super(jsonObject);
    }

    public Login(String username, String secret) {
        super(username, secret);
    }

    @Override
    protected String commandName() {
        return "LOGIN";
    }


}
