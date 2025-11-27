package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public class RegisterSuccess extends Info {

    public RegisterSuccess() {
        super();
    }

    public RegisterSuccess(JsonObject jsonObject) throws ValidationException {
        super(jsonObject);
        validate(jsonObject);
    }

    public RegisterSuccess(String info) {
        super(info);
    }

    @Override
    protected String commandName() {
        return "REGISTER_SUCCESS";
    }
}
