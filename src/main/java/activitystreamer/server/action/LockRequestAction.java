package activitystreamer.server.action;

import activitystreamer.server.Connection;
import activitystreamer.server.Responser;
import activitystreamer.util.BroadcastType;
import activitystreamer.util.PerformActionOnCommand;
import activitystreamer.util.Response;
import activitystreamer.util.commands.*;

import java.util.ArrayList;

public class LockRequestAction implements PerformActionOnCommand<LockRequest> {

    @Override
    public Response performAction(LockRequest command, Connection connection) {
        if (!connection.isAuthenticated()) {
            return new Response(Response.CLOSE, BroadcastType.NOT, new InvalidMessage("Unauthenticated server"));
        }

        return processLockRequest(connection, command);
    }

    private synchronized Response processLockRequest(Connection current, LockRequest lockrequest) {

        boolean isRegistered = RegisterHelperFunction.isRegisteredLocally(lockrequest);

        ArrayList<Command> commands = new ArrayList<Command>();
        commands.add(lockrequest);

        if (isRegistered) {
            // Send back the lock denied command
            LockDenied lockDenied = new LockDenied(lockrequest);
            Responser.getInstance().sendCommand(current, lockDenied);
            // Broadcast to other servers as well
            commands.add(lockDenied);
        } else {
            RegisterHelperFunction.writeUserInformation(lockrequest);
            // Send back the lock allowed command
            LockAllowed lockAllowed = new LockAllowed(lockrequest);
            Responser.getInstance().sendCommand(current, lockAllowed);
            // Broadcast to other servers as well
            commands.add(lockAllowed);
        }
        // Propagates the lock request and also broadcasts its lock allowed/denied request
        return new Response(Response.DONT_CLOSE, BroadcastType.OTHER_SERVER, commands);
    }
}
