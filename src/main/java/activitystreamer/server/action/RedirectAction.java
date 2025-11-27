package activitystreamer.server.action;

import activitystreamer.server.Connection;
import activitystreamer.util.PerformActionOnCommand;
import activitystreamer.util.Response;
import activitystreamer.util.commands.Redirect;

public class RedirectAction implements PerformActionOnCommand<Redirect> {

    public Response performAction(Redirect command, Connection connection) {

        //retrive the hostname and port number from other server, then construct json to client
        return null;

    }


}
