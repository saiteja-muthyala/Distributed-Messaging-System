package activitystreamer.server.action;

import activitystreamer.server.Connection;
import activitystreamer.util.BroadcastType;
import activitystreamer.util.PerformActionOnCommand;
import activitystreamer.util.Response;
import activitystreamer.util.Settings;
import activitystreamer.util.commands.Authenticate;
import activitystreamer.util.commands.AuthenticationFail;

public class AuthenticateAction implements PerformActionOnCommand<Authenticate> {

    public Response performAction(Authenticate command, Connection con) {
        Response response = null;
        //if the secret doesn't match to the own secret, then return the response
        // with the failed info otherwise return null meaning no reply
        if (!command.getSecret().equals(Settings.getSecret())) {
            String info = "The supplied secret is incorrect: " + command.getSecret();
            response = new Response(Response.CLOSE, BroadcastType.NOT, new AuthenticationFail(info));
        } else {
            // The secret is correct, grant the authentication
            con.setAuthenticated(true);
            con.setServer(true);
        }
        return response;

    }

}
