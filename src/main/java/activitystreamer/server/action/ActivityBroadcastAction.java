package activitystreamer.server.action;

import activitystreamer.server.Connection;
import activitystreamer.util.BroadcastType;
import activitystreamer.util.PerformActionOnCommand;
import activitystreamer.util.Response;
import activitystreamer.util.commands.ActivityBroadcast;
import activitystreamer.util.commands.InvalidMessage;

public class ActivityBroadcastAction implements PerformActionOnCommand<ActivityBroadcast> {

    @Override
    public Response performAction(ActivityBroadcast command, Connection connection) {
        if (!connection.isAuthenticated()) {
            return new Response(Response.CLOSE, BroadcastType.NOT, new InvalidMessage());
        } else {
            // Broadcast the messages to its clients and other servers
            return new Response(Response.DONT_CLOSE, BroadcastType.BOTH, command);
        }
    }
}
