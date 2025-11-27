package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public class LockAllowed extends UserSecret {
    public LockAllowed() {
        super();
    }

    public LockAllowed(JsonObject jsonObject) throws ValidationException {
        super(jsonObject);
    }

    // Used when creating a lock allowed response from the request
    public LockAllowed(LockRequest lockRequest) {
        super(lockRequest.getUsername(), lockRequest.getSecret());
    }

    @Override
    protected String commandName() {
        return "LOCK_ALLOWED";
    }
}
