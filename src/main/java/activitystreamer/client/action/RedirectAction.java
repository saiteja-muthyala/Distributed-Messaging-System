package activitystreamer.client.action;

import activitystreamer.client.ClientSkeleton;
import activitystreamer.util.Settings;
import activitystreamer.util.commands.Redirect;

import java.io.IOException;

public class RedirectAction {
    public static void performAction(Redirect command) {
        Settings.setRemoteHostname(command.getHostname());
        Settings.setRemotePort(command.getPort());

        ClientSkeleton.getInstance().disconnect();

        try {
            ClientSkeleton.getInstance().resetSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientSkeleton.getInstance().connectToServer();
    }
}
