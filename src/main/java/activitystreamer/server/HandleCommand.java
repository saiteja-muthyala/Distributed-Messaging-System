package activitystreamer.server;

import activitystreamer.server.action.*;
import activitystreamer.util.BroadcastType;
import activitystreamer.util.Response;
import activitystreamer.util.commands.*;
import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;


/**
 * This class is used to handle all the command and distribute task
 * to each function
 */
public class HandleCommand {

    private Response response;
    private static HandleCommand instance = new HandleCommand();

    private HandleCommand() {

    }

    public Response handleCommand(JsonObject command, Connection con) {
        try {
            switch (command.get("command").getAsString()) {

                case "LOGIN":
                    response = new LoginAction().performAction(new Login(command), con);
                    // need to updateClientConnection(con); maybe in performAction
                    // There should be including LOGIN_SUCCESS and LOGIN_FAILED
                    break;

                case "LOGOUT":
                    response = new LogoutAction().performAction(new Logout(command), con);
                    break;
                case "AUTHENTICATE":
                    if (con.isAuthenticated()) {
                        response = new Response(true, BroadcastType.NOT,
                                new InvalidMessage("the server has already authenticated"));
                    } else {
                        response = new AuthenticateAction().performAction(new Authenticate(command), con);
                    }
                    break;
                case "AUTHENTICATION_FAIL":
                    con.setAuthenticated(false);
                    con.closeCon();
                    Control.getLog().info("secret not matched, running as a stand alone server");
                    break;

                case "ACTIVITY_MESSAGE":
                    response = new ActivityMessageAction().performAction(new ActivityMessage(command), con);
                    break;

                case "SERVER_ANNOUNCE":
                    response = new ServerAnnounceAction().performAction(new ServerAnnounce(command), con);
                    break;

                case "ACTIVITY_BROADCAST":
                    response = new ActivityBroadcastAction().performAction(new ActivityBroadcast(command), con);
                    break;

                case "REGISTER":
                    response = new RegisterAction().performAction(new Register(command), con);
                    break;

                case "LOCK_REQUEST":
                    response = new LockRequestAction().performAction(new LockRequest(command), con);
                    break;

                case "LOCK_DENIED":
                    response = new LockDeniedAction().performAction(new LockDenied(command), con);
                    break;

                case "LOCK_ALLOWED":
                    response = new LockAllowedAction().performAction(new LockAllowed(command), con);
                    break;

                case "INVALID_MESSAGE":
                    // Close the connection when received this response
                    response = new Response(Response.CLOSE, BroadcastType.NOT);
                    break;

                default:
                    response = new Response(new InvalidMessage("Command name not supported. Connection closed"));
                    response.setCloseAfterResponse(true);
                    break;
            }
        } catch (ValidationException e) {
            response = new Response(Response.CLOSE, BroadcastType.NOT, new InvalidMessage(
                    "Invalid field or format for '" + command.get("command") + "' command. Connection closed"));
        }
        return response;// response contains 1 or more commands, and some flags indicating other status
    }

    public static HandleCommand getInstance() {
        return instance;
    }

}


