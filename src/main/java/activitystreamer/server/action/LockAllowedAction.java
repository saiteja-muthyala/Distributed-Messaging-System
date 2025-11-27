package activitystreamer.server.action;

import activitystreamer.server.Connection;
import activitystreamer.server.Repository.Repository;
import activitystreamer.server.Repository.UserInformation;
import activitystreamer.server.Responser;
import activitystreamer.util.BroadcastType;
import activitystreamer.util.PerformActionOnCommand;
import activitystreamer.util.Response;
import activitystreamer.util.commands.InvalidMessage;
import activitystreamer.util.commands.LockAllowed;
import activitystreamer.util.commands.RegisterSuccess;
import activitystreamer.util.commands.UserSecret;

public class LockAllowedAction implements PerformActionOnCommand<LockAllowed> {

    private boolean justAddToRepository = false;

    @Override
    public synchronized Response performAction(LockAllowed command, Connection connection) {
        if (!connection.isAuthenticated()) {
            return new Response(Response.CLOSE, BroadcastType.NOT, new InvalidMessage("Unauthenticated server"));
        }

        int numOfServer = Repository.getServerLoads().size();
        processLockAllowed(command);

        if (justAddToRepository) {
            // Send back the message to the sender
            Responser.getInstance().sendCommand(connection, command);
        }

        if (!Repository.getLockCount().isCurrentServerRegisering()) {
            // Propagate the broadcast to other servers
            return new Response(Response.DONT_CLOSE, BroadcastType.OTHER_SERVER, command);
        }
        // Otherwise this server is connected to a client
        if (Repository.getLockCount().getCountOfAllowed() == numOfServer) {
            // this server should send a register success command
            RegisterHelperFunction.writeUserInformation(command);

            Repository.getLockCount().resetCount();

            // Directly send to the client
            boolean success = Responser.getInstance().sendCommand(
                    Repository.getLockCount().getCurrentClientConnection(),
                    new RegisterSuccess(RegisterHelperFunction.getSucessMessage(command.getUsername())));
            Repository.getLockCount().setCurrentServerRegisering(false);
            this.notify();
        }

        return null;
    }

    public synchronized void processLockAllowed(UserSecret lockallowed) {
        // Add to repository if the server by any chance did not add the user into the repository
        // For example, just get authenticated and not
        if (!Repository.getInstance().checkUserExists(lockallowed.getUsername())) {
            UserInformation userInformation = new UserInformation(lockallowed.getUsername(), lockallowed.getSecret());
            Repository.getUserInformations().put(lockallowed.getUsername(), userInformation);
            justAddToRepository = true;
        }

        // updates the repository, add the count if the user information matches
        if (Repository.getLockCount().isCurrentServerRegisering() &&
                Repository.getLockCount().getRegisteringUserInfo()
                        .checkUserMatch(lockallowed.getUsername(), lockallowed.getSecret())) {
            Repository.getLockCount().addCountOfAllowed();
        }
    }
}
