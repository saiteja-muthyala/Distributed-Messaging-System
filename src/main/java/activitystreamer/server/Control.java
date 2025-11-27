package activitystreamer.server;

import activitystreamer.util.BroadcastType;
import activitystreamer.util.Response;
import activitystreamer.util.Settings;
import activitystreamer.util.commands.Authenticate;
import activitystreamer.util.commands.InvalidMessage;
import activitystreamer.util.commands.ServerAnnounce;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Control extends Thread {
    private static final Logger log = LogManager.getLogger();
    private static ArrayList<Connection> connections;
    private static boolean term = false;
    private static Listener listener;

    protected static Control control = null;

    public static Control getInstance() {
        if (control == null) {
            control = new Control();
        }
        return control;
    }

    public Control() {
        // initialize the connections array
        connections = new ArrayList<Connection>();
        // start a listener
        try {
            listener = new Listener();
        } catch (IOException e1) {
            log.fatal("failed to startup a listening thread: " + e1);
            System.exit(-1);
        }

        start();
    }

    public void initiateConnection() {
        // make a connection to another server if remote hostname is supplied
        if (Settings.getRemoteHostname() != null) {
            try {
                Connection c = outgoingConnection(new Socket(Settings.getRemoteHostname(), Settings.getRemotePort()));
                c.setServer(true);
                c.setAuthenticated(true);
                Authenticate auth = new Authenticate();
                Gson gson = new Gson();
                c.writeMsg(gson.toJson(auth));

            } catch (IOException e) {
                log.error("failed to make connection to " + Settings.getRemoteHostname() + ":" + Settings.getRemotePort() + " :" + e);
//				System.exit(-1);
                log.info("running as a stand alone server");
            }
        }
    }

    /*
     * Processing incoming messages from the connection.
     * Return true if the connection should close.
     */
    public synchronized boolean process(Connection con, String msg) {
        // This is where we handle the incoming message
        System.out.println(msg);


        JsonObject jsonObject;
        Response response;

        // Convert to JSON object, the conversion to Command object is done in the handler
        try {
            jsonObject = (new JsonParser()).parse(msg).getAsJsonObject();
            jsonObject.get("command").getAsString(); // Try if there is null pointer exception
        } catch (JsonSyntaxException e) {
            response = new Response(Response.CLOSE, BroadcastType.NOT, new InvalidMessage("JSON parse error while parsing message"));
            Responser.getInstance().writeResponse(connections, con, response);
            return false;
        } catch (IllegalStateException e) {
            response = new Response(Response.CLOSE, BroadcastType.NOT, new InvalidMessage("The received message does not contain a command"));
            Responser.getInstance().writeResponse(connections, con, response);
            return false;
        } catch (NullPointerException e) {
            response = new Response(Response.CLOSE, BroadcastType.NOT, new InvalidMessage("The received message does not contain a command"));
            Responser.getInstance().writeResponse(connections, con, response);
            return false;
        }
        // Handle the Command object
        response = HandleCommand.getInstance().handleCommand(jsonObject, con);
        // Response to the connection, or broadcast some message
        Responser.getInstance().writeResponse(connections, con, response);

        // If we need to log out, return true
        // But we don't need to return true, according to the failure model, the server would never fail
        return false;
    }

    /*
     * The connection has been closed by the other party.
     */
    public synchronized void connectionClosed(Connection con) {
        if (!term) connections.remove(con);
    }

    /*
     * A new incoming connection has been established, and a reference is returned to it
     */
    public synchronized Connection incomingConnection(Socket s) throws IOException {
        log.debug("incomming connection: " + Settings.socketAddress(s));
        Connection c = new Connection(s);
        connections.add(c);
        return c;

    }

    /*
     * A new outgoing connection has been established, and a reference is returned to it
     */
    public synchronized Connection outgoingConnection(Socket s) throws IOException {
        log.debug("outgoing connection: " + Settings.socketAddress(s));
        Connection c = new Connection(s);
        connections.add(c);
        return c;

    }

    @Override
    public void run() {
        if (Settings.getRemoteHostname() == null && Settings.getSecret() == null) {
            Settings.setSecret(Settings.nextSecret());
            log.info("running as a stand-alone server with secret: " + Settings.getSecret());
        }
        log.info("using activity interval of " + Settings.getActivityInterval() + " milliseconds");
        while (!term) {
            // do something with 5 second intervals in between
            try {
                Thread.sleep(Settings.getActivityInterval());
            } catch (InterruptedException e) {
                log.info("received an interrupt, system is shutting down");
                break;
            }
            if (!term) {
//				log.debug("doing activity");
                term = doActivity();
            }

        }
        log.info("closing " + connections.size() + " connections");
        // clean up
        for (Connection connection : connections) {
            connection.closeCon();
        }
        listener.setTerm(true);
    }

    public boolean doActivity() {
        this.broadcastServerAnnounce();

        return false;
    }

    public final void setTerm(boolean t) {
        term = t;
    }

    public final ArrayList<Connection> getConnections() {
        return connections;
    }

    public void updateServerConnection(Connection con) {
        for (int i = 0; i < connections.size(); i++) {
            Connection currentConnection = connections.get(i);
            if (currentConnection.getSocket() == con.getSocket()) {
                con.setServer(true);
                connections.set(i, con);
            }
        }
    }

    public void updateClientConnection(Connection con) {
        for (int i = 0; i < connections.size(); i++) {
            Connection currentConnection = connections.get(i);
            if (currentConnection.getSocket() == con.getSocket()) {
                con.setServer(false);
                connections.set(i, con);
            }
        }
    }

    public ServerAnnounce getServerLoad() {
        ServerAnnounce serverLoad = new ServerAnnounce(Settings.getLocalHostname(), Settings.getLocalPort());
        serverLoad.setHostname(Settings.getLocalHostname());
        serverLoad.setPort(Settings.getLocalPort());
        serverLoad.setLoad(this.numberOfClientConnected());

        return serverLoad;
    }

    public int numberOfClientConnected() {
        int count = 0;
        for (int i = 0; i < connections.size(); i++) {
            Connection currentConnection = connections.get(i);
            if (!currentConnection.isServer() && currentConnection.isAuthenticated()) {
                count++;
            }
        }

        return count;
    }

    public void broadCastConnectedClients(String msg) {
        // Convert to JSON string, and send to the connection
        ArrayList<Connection> connections = getConnections();
        for (Connection con : connections) {
            if (!con.isServer() && con.isAuthenticated()) {
                con.writeMsg(msg);
            }
        }
    }

    public void broadcastToOtherConnectedServers(Connection messageSource, String msg) {

        for (int i = 0; i < connections.size(); i++) {
            Connection currentConnection = connections.get(i);
            if (messageSource.getSocket() != currentConnection.getSocket()
                    && currentConnection.isServer()
                    && currentConnection.isAuthenticated()) {
                currentConnection.writeMsg(msg);
                log.info("SEND BROADCAST  " + currentConnection.getSocket().getLocalPort() + currentConnection.getSocket().getPort() + currentConnection.getSocket().getLocalAddress());
            }
        }
    }

    public void broadcastServerAnnounce() {
        //calculate server connected
        ServerAnnounce serverAnnounce = this.getServerLoad();
        //looping connection, & ready to broadcast!
        this.broadcastServerAnnounce(serverAnnounce);
    }

    public synchronized void broadcastServerAnnounce(ServerAnnounce announce) {
        Gson gson = new Gson();
        String message = gson.toJson(announce);

        this.broadcastBetweenServers(message);
    }

    public synchronized void broadcastBetweenServers(String msg) {
        for (int i = 0; i < connections.size(); i++) {
            Connection currentConnection = connections.get(i);
            if (currentConnection.isServer() && currentConnection.isAuthenticated()) {
                currentConnection.writeMsg(msg);
                log.info("SEND BROADCAST  " + currentConnection.getSocket().getLocalPort() + currentConnection.getSocket().getPort() + currentConnection.getSocket().getLocalAddress());
            }
        }
    }

    public void sendRedirectMessageToOneClient(Connection con, String msg) {
        con.writeMsg(msg);
        con.closeCon();
    }

    // Helper class to log the file
    public static Logger getLog() {
        return log;
    }
}
