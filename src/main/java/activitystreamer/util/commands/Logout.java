package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public class Logout extends Command {

    public Logout() {
        super();
    }

    public Logout(JsonObject jsonObject) throws ValidationException {
        super();
        validate(jsonObject);
    }

    @Override
    public String commandName() {
        return "LOGOUT";
    }
}
