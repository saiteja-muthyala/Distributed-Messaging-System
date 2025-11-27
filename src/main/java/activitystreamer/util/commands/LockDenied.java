package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public class LockDenied extends UserSecret {

    public LockDenied() {
        super();
    }

    public LockDenied(JsonObject jsonObject) throws ValidationException {
        super(jsonObject);
    }

    // Used when creating a lock denied response from the request
    public LockDenied(LockRequest lockRequest) {
        super(lockRequest.getUsername(), lockRequest.getSecret());
    }

    @Override
    protected String commandName() {
        return "LOCK_DENIED";
    }
}
