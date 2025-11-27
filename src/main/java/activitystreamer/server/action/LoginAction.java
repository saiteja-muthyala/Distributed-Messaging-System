package activitystreamer.server.action;


import activitystreamer.server.Connection;
import activitystreamer.server.Control;
import activitystreamer.server.Repository.Repository;
import activitystreamer.server.Repository.ServerLoad;
import activitystreamer.server.Repository.UserInformation;
import activitystreamer.util.PerformActionOnCommand;
import activitystreamer.util.Response;
import activitystreamer.util.commands.Login;
import activitystreamer.util.commands.LoginFailed;
import activitystreamer.util.commands.LoginSuccess;
import activitystreamer.util.commands.Redirect;
import com.google.gson.Gson;

public class LoginAction implements PerformActionOnCommand<Login> {


    public Response performAction(Login command, Connection connection) {
        //0. first after load balancer;

        boolean isValidate = this.validateUserInformation(command);
        if (isValidate) {
            connection.setUserInformation(Repository.findUserInformationByName(command.getUsername()));
            Response response = new Response(new LoginSuccess());

            this.sendRedirect(connection);
            connection.setAuthenticated(true);
            return response;
        } else {
            Response response = new Response((new LoginFailed()));
            return response;
        }
    }

    public void sendRedirect(Connection con) {
        ServerLoad serverLoad = Repository.getInstance().findServerToRedirect();
        Gson gson = new Gson();

        if (serverLoad != null) {
            Redirect redirectMessage = new Redirect(serverLoad.getHostname(), serverLoad.getPort());
            Control.getLog().debug("hostname: " + serverLoad.getHostname());
            String msg = gson.toJson(redirectMessage);
            Control.getLog().debug(msg);
            Control.getInstance().sendRedirectMessageToOneClient(con, msg);
        }
    }

    public boolean validateUserInformation(Login login) {

        boolean isValidated;
        String username = login.getUsername();
        String secret = login.getSecret();

        if (username.equals("anonymous")) {
            // Grant the access
            return true;
        }

        // Otherwise find it in the repository
        UserInformation isFound = Repository.getUserInformations().get(username);

        if (isFound != null) {
            if (isFound.getSecret().equals(secret)) {
                isValidated = true;
            } else
                isValidated = false;

        } else
            isValidated = false;

        return isValidated;

    }

}
