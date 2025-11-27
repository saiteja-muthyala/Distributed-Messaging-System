package activitystreamer.server.action;

import activitystreamer.server.Connection;
import activitystreamer.server.Repository.Repository;
import activitystreamer.server.Repository.UserInformation;
import activitystreamer.server.Responser;
import activitystreamer.util.BroadcastType;
import activitystreamer.util.PerformActionOnCommand;
import activitystreamer.util.Response;
import activitystreamer.util.commands.InvalidMessage;
import activitystreamer.util.commands.LockDenied;
import activitystreamer.util.commands.RegisterFailed;
import activitystreamer.util.commands.UserSecret;

import java.util.HashMap;

public class LockDeniedAction implements PerformActionOnCommand<LockDenied> {
    @Override
    public synchronized Response performAction(LockDenied command, Connection connection) {
        if (!connection.isAuthenticated()) {
            return new Response(Response.CLOSE, BroadcastType.NOT, new InvalidMessage("Unauthenticated server"));
        }

        processLockDeniedAndRemove(command);
        if (!Repository.getLockCount().isCurrentServerRegisering()) {
            // Propagate the broadcast to other servers
            return new Response(Response.DONT_CLOSE, BroadcastType.OTHER_SERVER, command);
        }

        // Otherwise this server has a client that is registering, need to check if they are the same client
        boolean userMatched = Repository.getLockCount().getRegisteringUserInfo()
                .checkUserMatch(command.getUsername(), command.getSecret());

        // this server should send a register failed command
        if (userMatched && Repository.getLockCount().getCountOfDenied() > 0) {
            Repository.getLockCount().resetCount();
            // Directly send to the client
            Response failureResponse = new Response(Response.CLOSE, BroadcastType.NOT, new RegisterFailed(
                    String.format(RegisterHelperFunction.getFailMessage(command.getUsername()))));
            Responser.getInstance().writeResponse(Repository.getLockCount().getCurrentClientConnection(), failureResponse);
            Repository.getLockCount().setCurrentServerRegisering(false);
            this.notify();
        }

        // Do not need to reply to the server
        return null;
    }

    public synchronized void processLockDeniedAndRemove(UserSecret lockdenied) {
        // Removed the specified user from the repository
        HashMap<String, UserInformation> userInformations = Repository.getUserInformations();

        if (userInformations.containsKey(lockdenied.getUsername())) {
            UserInformation userInfo = userInformations.get(lockdenied.getUsername());
            if (userInfo.checkUserMatch(lockdenied.getUsername(), lockdenied.getSecret())) {
                // Remove the wrong user information
                userInformations.remove(userInfo);
            }
        }
        // update the repository
        Repository.getLockCount().addCountOfDenied();
    }
}
