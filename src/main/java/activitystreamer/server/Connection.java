package activitystreamer.server;


import activitystreamer.server.Repository.UserInformation;
import activitystreamer.util.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;


public class Connection extends Thread {
    private static final Logger log = LogManager.getLogger();
    private DataInputStream in;
    private DataOutputStream out;
    private BufferedReader inreader;
    private PrintWriter outwriter;
    private boolean open = false;
    private Socket socket;
    private boolean term = false;
    private boolean isServer = false;
    // The authenticated should be set to true when a server has successfully authenticated,
    // or a client has logged in successfully
    private boolean authenticated = false;
    private UserInformation userInformation;

    Connection(Socket socket) throws IOException {
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        inreader = new BufferedReader(new InputStreamReader(in));
        outwriter = new PrintWriter(out, true);
        this.socket = socket;
        open = true;
        start();
    }

    /*
     * returns true if the message was written, otherwise false
     */
    public boolean writeMsg(String msg) {
        if (open) {
            outwriter.println(msg);
            outwriter.flush();
            return true;
        }
        return false;
    }

    public void closeCon() {
        if (open) {
            log.info("closing connection " + Settings.socketAddress(socket));
            term = true;
        }
    }


    public void run() {
        try {
            String data;
            while (!term && (data = inreader.readLine()) != null) {
                term = Control.getInstance().process(this, data);
            }
            log.debug("connection closed to " + Settings.socketAddress(socket));
            Control.getInstance().connectionClosed(this);
            in.close();
            out.close();
            inreader.close();
        } catch (IOException e) {
            log.error("connection " + Settings.socketAddress(socket) + " closed with exception: " + e);
            e.printStackTrace();
            Control.getInstance().connectionClosed(this);
        }
        open = false;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        isServer = server;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public UserInformation getUserInformation() {
        return userInformation;
    }

    public void setUserInformation(UserInformation userInformation) {
        this.userInformation = userInformation;
    }
}
