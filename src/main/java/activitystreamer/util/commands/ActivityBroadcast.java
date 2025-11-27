package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json.simple.JSONObject;

import java.util.Map;


public class ActivityBroadcast extends Command {
    // If using JsonObject, gson.toJson() will add an unexpected field,
    // use this type instead as a workaround
    protected JSONObject activity;

    public ActivityBroadcast() {
        super();
    }

    // Do not put the activity json object into this constructor, if we need to create an activity message,
    // use new ActivityMessage() then set the activity or call the static method "createActivityMessage(activity)"
    public ActivityBroadcast(JsonObject jsonObject) throws ValidationException {
        super();
        validate(jsonObject);
        setActivity(jsonObject.getAsJsonObject("activity"));
    }

    public static ActivityMessage createActivityMessage(JsonObject activity) {
        ActivityMessage newInstance = new ActivityMessage();
        newInstance.setActivity(activity);
        return newInstance;
    }

    @Override
    protected String commandName() {
        return "ACTIVITY_BROADCAST";
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

    @Override
    public void validate(JsonObject jsonObject) throws ValidationException {
        super.validate(jsonObject);

        if (!jsonObject.has("activity")) {
            throw new ValidationException("No 'activity' field in the request.");
        }
    }
}
