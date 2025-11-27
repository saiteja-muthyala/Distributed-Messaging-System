package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public class Register extends UserSecret {

    public Register() {
        super();
    }

    public Register(JsonObject jsonObject) throws ValidationException {
        super(jsonObject);
    }

    @Override
    protected String commandName() {
        return "REGISTER";
    }
}
