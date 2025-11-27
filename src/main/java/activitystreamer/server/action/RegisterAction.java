package activitystreamer.server.action;


import activitystreamer.server.Connection;
import activitystreamer.server.Control;
import activitystreamer.server.Repository.Repository;
import activitystreamer.server.Repository.UserInformation;
import activitystreamer.util.BroadcastType;
import activitystreamer.util.PerformActionOnCommand;
import activitystreamer.util.Response;
import activitystreamer.util.commands.*;


/* search local repository, if found, failed;
else broadcast to all connected server,
search local repository for connected server, if not found, write to their M,
waiting for response, while lock allowed, keep looping, nar
*/


public class RegisterAction implements PerformActionOnCommand<Register> {

    private static final String SUCESS_MSG_FORMAT = "register success for %s";
    private static final String FAIL_MSG_FORMAT = "%s is already registered with the system";

    private static Connection registeringClientConnection = null;
    private static boolean currentServerIsRegistering = false;

    public static Connection getRegisteringClientConnection() {
        return registeringClientConnection;
    }

    public static boolean isCurrentServerRegistering() {
        return currentServerIsRegistering;
    }

    // switch Register, Lock_request, Lock_denied, Lock_allowed; return response
    @Override
    public synchronized Response performAction(Register command, Connection connection) {
        if (Repository.getLockCount().isCurrentServerRegisering()) {
            // Wait for the current register finished
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Repository.getLockCount().resetCount();
        Repository.getLockCount().setCurrentServerRegisering(true);
        Repository.getLockCount().setCurrentClientConnection(connection);
        Repository.getLockCount().setRegisteringUserInfo(new UserInformation(command.getUsername(), command.getSecret()));
        return processRegister(command);
    }

    public Response processRegister(UserSecret register) {
        if (register.getUsername().equals("anonymous")) {
            return new Response((new InvalidMessage("The username \"anonymous\" cannnot be used for registration")));
        }

        if (RegisterHelperFunction.isRegisteredLocally(register)) {
            return new Response(new RegisterFailed(String.format(FAIL_MSG_FORMAT, register.getUsername())));
        } else if (Repository.getServerLoads().size() == 0) {
            // If there are no other servers connected, register the user directly
            RegisterHelperFunction.writeUserInformation(register);
            return new Response(new RegisterSuccess(String.format(SUCESS_MSG_FORMAT, register.getUsername())));
        } else {
            // Send lock requests to other servers
            LockRequest lockRequest = new LockRequest(register.getUsername(), register.getSecret());
            Response lockrequestResponse = new Response(Response.DONT_CLOSE, BroadcastType.OTHER_SERVER, lockRequest);
            Control.getLog().debug("Sending lock request");
            return lockrequestResponse;
        }
    }


}


