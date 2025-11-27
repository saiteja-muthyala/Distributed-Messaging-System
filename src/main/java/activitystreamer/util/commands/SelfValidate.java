package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public interface SelfValidate {
    public void validate(JsonObject jsonObject) throws ValidationException;
}
