package activitystreamer.server;

import activitystreamer.util.Response;
import activitystreamer.util.commands.Command;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Responser {
    private static Responser ourInstance = new Responser();

    public static Responser getInstance() {
        return ourInstance;
    }

    private Responser() {

    }

    public boolean writeResponse(ArrayList<Connection> connections, Connection current, Response response) {
        boolean success = true;

        // No response needs to be sent
        if (response == null) {
            return success;
        }

        if (response.getCommands() != null) {
            for (Command command : response.getCommands()) {
                switch (response.isBroadcast()) {
                    case BOTH:
                        broadCastToOtherServers(connections, current, command);
                        broadCastClients(connections, current, command);
                        break;
                    case CLIENT:
                        broadCastClients(connections, current, command);
                        break;
                    case OTHER_SERVER:
                        broadCastToOtherServers(connections, current, command);
                        break;
                    case NOT:
                        sendCommand(current, command);
                        break;
                    default:
                        break;
                }
            }
        }

        if (response.isCloseAfterResponse()) {
            current.closeCon();
            connections.remove(current);
        }

        return success;
    }

    public boolean writeResponse(Connection current, Response response) {
        return writeResponse(Control.getInstance().getConnections(), current, response);
    }

    public boolean sendCommand(Connection current, Command command) {
        if (command == null) {
            // When the Command is null, don't send it
            return false;
        }
        Gson gson = new Gson();
        String message = gson.toJson(command).toString();
//        Control.getLog().debug("Command class: " + command.getClass());
        boolean success = current.writeMsg(message);
        if (!success) {
            Control.getLog().debug("Fail to write message to host: " + current.getSocket().getLocalAddress()
                    + ", port: " + current.getSocket().getPort() + ",\nmessage: " + message);
        }
        return success;
    }

    public void broadCastClients(ArrayList<Connection> connections, Connection current, Command command) {
        // Convert to JSON string, and send to the connection
        for (Connection con : connections) {
            if (!con.isServer() && con.isAuthenticated()) {
                sendCommand(con, command);
            }
        }
    }

    public void broadCastToOtherServers(ArrayList<Connection> connections, Connection messageSource, Command command) {
        // Do the broadcast to other servers
        // Avoid sending to the current connecting server, since the message comes from that server
        for (Connection currentConnection : connections) {
            if (messageSource.getSocket() != currentConnection.getSocket()
                    && currentConnection.isServer()
                    && currentConnection.isAuthenticated()) {
                sendCommand(currentConnection, command);
//                log.debug("SEND BROADCAST  " + currentConnection.getSocket().getLocalPort() + currentConnection.getSocket().getPort() + currentConnection.getSocket().getLocalAddress());
            }
        }
    }

}
