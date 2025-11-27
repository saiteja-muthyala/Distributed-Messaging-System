package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json.simple.JSONObject;

import java.util.Map;

public class ActivityMessage extends UserSecret {
    protected JSONObject activity;

    public ActivityMessage() {
        super();
    }

    // Do not put the activity json object into this constructor, if we need to create an activity message,
    // use new ActivityMessage() then set the activity or call the static method "createActivityMessage(activity)"
    public ActivityMessage(JsonObject incomingJsonObject) throws ValidationException {
        super(incomingJsonObject);
        validate(incomingJsonObject);
        setActivity(incomingJsonObject.getAsJsonObject("activity"));
    }

    public static ActivityMessage createActivityMessage(JsonObject activity) {
        ActivityMessage newInstance = new ActivityMessage();
        newInstance.setActivity(activity);
        return newInstance;
    }

    public void setActivity(JsonObject activity) {
        this.activity = new JSONObject();
        for (Map.Entry<String, JsonElement> element : activity.entrySet()) {
            this.activity.put(element.getKey(), element.getValue().getAsString());
        }
    }

    public void setActivity(JSONObject activity) {
        this.activity = activity;
    }

    public JsonObject getActivity() {
        return new Gson().toJsonTree(activity).getAsJsonObject();
    }
//    public JsonPrimitive getActivity() {
//        return activity;
//    }

    @Override
    protected String commandName() {
        return "ACTIVITY_MESSAGE";
    }

    @Override
    public void validate(JsonObject jsonObject) throws ValidationException {
        super.validate(jsonObject);

        if (!jsonObject.has("activity")) {
            throw new ValidationException("No 'activity' field in the request.");
        }
    }
}
