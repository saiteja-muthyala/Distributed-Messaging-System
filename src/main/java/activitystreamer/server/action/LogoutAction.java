package activitystreamer.server.action;

import activitystreamer.server.Connection;
import activitystreamer.util.BroadcastType;
import activitystreamer.util.PerformActionOnCommand;
import activitystreamer.util.Response;
import activitystreamer.util.commands.Logout;

public class LogoutAction implements PerformActionOnCommand<Logout> {

    @Override
    public Response performAction(Logout command, Connection connection) {
        //After receiving LOGOUT, then server close the connection
        return new Response(Response.CLOSE, BroadcastType.NOT);
    }

}
