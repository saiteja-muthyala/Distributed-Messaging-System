package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public class LockRequest extends UserSecret {

    public LockRequest() {
        super();
    }

    // Used to construct the response from the server
    public LockRequest(String username, String secret) {
        super(username, secret);
    }

    public LockRequest(JsonObject jsonObject) throws ValidationException {
        super(jsonObject);
    }

    @Override
    protected String commandName() {
        return "LOCK_REQUEST";
    }
}
