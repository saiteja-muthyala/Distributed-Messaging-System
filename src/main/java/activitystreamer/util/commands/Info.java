package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public abstract class Info extends Command {
    protected String info;

    public Info() {
        super();
    }

    // Used for creating outgoing responses
    public Info(String info) {
        this.info = info;
    }

    // Used for unmarshalling from incoming json messages
    public Info(JsonObject jsonObject) throws ValidationException {
        super();
        validate(jsonObject);
        setInfo(jsonObject.get("info").getAsString());
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public void validate(JsonObject jsonObject) throws ValidationException {
        super.validate(jsonObject);

        if (!jsonObject.has("info")) {
            throw new ValidationException("No 'info' field in the request.");
        }
    }
}
