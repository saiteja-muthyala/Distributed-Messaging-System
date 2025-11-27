package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public class RegisterFailed extends Info {
    public RegisterFailed() {
        super();
    }

    public RegisterFailed(JsonObject jsonObject) throws ValidationException {
        super(jsonObject);
    }

    public RegisterFailed(String info) {
        super(info);
    }

    @Override
    protected String commandName() {
        return "REGISTER_FAILED";
    }
}
