package activitystreamer.server.action;

import activitystreamer.server.Connection;
import activitystreamer.server.Control;
import activitystreamer.util.BroadcastType;
import activitystreamer.util.PerformActionOnCommand;
import activitystreamer.util.Response;
import activitystreamer.util.commands.ActivityBroadcast;
import activitystreamer.util.commands.ActivityMessage;
import activitystreamer.util.commands.AuthenticationFail;
import activitystreamer.util.commands.InvalidMessage;
import com.google.gson.JsonObject;


public class ActivityMessageAction implements PerformActionOnCommand<ActivityMessage> {
    @Override
    public Response performAction(ActivityMessage command, Connection connection) {
        if (!connection.isAuthenticated()) {
            return new Response(new InvalidMessage("The user has not logged in."));
        }
        // if the username is not "anonymous", check that the username and secret match with the logged in
        if (!command.getUsername().equals("anonymous")) {
            boolean matched = connection.getUserInformation().checkUserMatch(command.getUsername(), command.getSecret());
            if (!matched) {
                return new Response(Response.CLOSE, BroadcastType.NOT, new AuthenticationFail("Provided user information mismatched. Connection closed."));
            }
        }

        ActivityBroadcast activityBroadcast = processActivity(command);
        ;
        Control.getLog().debug(activityBroadcast.getActivity().toString());
        return new Response(Response.DONT_CLOSE, BroadcastType.BOTH, activityBroadcast);
    }

    public ActivityBroadcast processActivity(ActivityMessage activityMessage) {
        // Process the activity message
        JsonObject activity = activityMessage.getActivity();
        String fieldname = "authenticated_user";

        // Overwrite the contents if the field exists
        if (activity.has(fieldname)) {
            activity.remove(fieldname);
        }

        activity.addProperty(fieldname, activityMessage.getUsername());
        Control.getLog().debug(activity.toString());
        // Create an activity broadcast command
        ActivityBroadcast activityBroadcast = new ActivityBroadcast();
        activityBroadcast.setActivity(activity);

        return activityBroadcast;
    }
}
