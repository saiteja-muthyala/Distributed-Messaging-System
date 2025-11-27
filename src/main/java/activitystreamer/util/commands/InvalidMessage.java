package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public class InvalidMessage extends Info {
    public InvalidMessage() {
        super();
    }

    public InvalidMessage(JsonObject jsonObject) throws ValidationException {
        super(jsonObject);
    }

    public InvalidMessage(String info) {
        super(info);
    }

    @Override
    protected String commandName() {
        return "INVALID_MESSAGE";
    }
}
