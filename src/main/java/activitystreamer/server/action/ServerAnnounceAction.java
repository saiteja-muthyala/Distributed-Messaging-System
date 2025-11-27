package activitystreamer.server.action;

import activitystreamer.server.Connection;
import activitystreamer.server.Repository.Repository;
import activitystreamer.util.BroadcastType;
import activitystreamer.util.PerformActionOnCommand;
import activitystreamer.util.Response;
import activitystreamer.util.commands.ServerAnnounce;

public class ServerAnnounceAction implements PerformActionOnCommand<ServerAnnounce> {
    @Override
    public Response performAction(ServerAnnounce command, Connection connection) {

        Repository.getInstance().updateServerLoad(command);

        return new Response(Response.DONT_CLOSE, BroadcastType.OTHER_SERVER, command);
    }
}
