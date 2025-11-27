package activitystreamer.util;

import activitystreamer.server.Connection;
import activitystreamer.util.commands.Command;

public interface PerformActionOnCommand<T extends Command> {


    public Response performAction(T command, Connection connection);


}
