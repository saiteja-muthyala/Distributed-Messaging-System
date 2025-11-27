package activitystreamer.client;

import activitystreamer.client.action.RedirectAction;
import activitystreamer.util.Settings;
import activitystreamer.util.commands.*;
import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;

public class ClientSkeleton extends Thread {
    private static final Logger log = LogManager.getLogger();
    private static ClientSkeleton clientSolution;
    private TextFrame textFrame;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private BufferedReader inreader;
    private PrintWriter outwriter;

    private static boolean term = false;

    public static ClientSkeleton getInstance() {
        if (clientSolution == null) {
            clientSolution = new ClientSkeleton();
        }
        return clientSolution;
    }

    public ClientSkeleton() {
        textFrame = new TextFrame();
        if (Settings.getRemoteHostname() != null && Settings.getRemotePort() != 0) {

            try {
                resetSocket();
            } catch (IOException e) {

            }
        }

        start();
    }

    public void resetSocket() throws IOException {

        socket = new Socket(Settings.getRemoteHostname(), Settings.getRemotePort());
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        inreader = new BufferedReader(new InputStreamReader(in));
        outwriter = new PrintWriter(out, true);
    }


    @SuppressWarnings("unchecked")
    public void sendActivityObject(JSONObject activityObj) {
        ActivityMessage activityMsg = new ActivityMessage();

        // Change the type from JSONObject to Gson.JsonObject
        log.debug(activityObj.toJSONString());
        JsonObject gsonActivityObj = new JsonParser().parse(activityObj.toJSONString()).getAsJsonObject();
        log.debug(gsonActivityObj.toString());
        activityMsg.setUsername(Settings.getUsername());
        activityMsg.setSecret(Settings.getSecret());

        Gson gson = new Gson();
        activityMsg.setActivity(gsonActivityObj);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("key", "value");

        log.debug("Json Obj:  " + gson.toJson(jsonObj));
        log.debug("Json Tree: " + gson.toJsonTree(jsonObj));

        this.sendCommand(activityMsg);
    }

    public void writeMsg(String msg) {
        try {
            outwriter.println(msg);
            outwriter.flush();
        } catch (NullPointerException e) {
            log.error("Failed to write message, connection does not exist");
        }
    }

    public void disconnect() {
        // Send a LOGOUT command to the server
        sendCommand(new Logout());

        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {

        }
    }


    public void run() {
        // First access to the server
        connectToServer();
        try {
            String data;
            while (!term && (data = inreader.readLine()) != null) {
                term = this.process(data.toString());
            }

            in.close();
        } catch (IOException e) {

        }
    }

    private void sendCommand(Command command) {
        Gson gson = new Gson();
        String str = gson.toJson(command);

        log.debug(str);
        writeMsg(str);
    }

    public void connectToServer() {
        UserSecret command = generateCommandFromSettings();

        log.info(String.format("connect to server using username: %s, secret: %s",
                command.getUsername(), command.getSecret()));

        sendCommand(command);
    }

    private UserSecret generateCommandFromSettings() {
        UserSecret out;
        String username;

        if (Settings.getUsername().equals("anonymous") && Settings.getSecret() == null) {
            // Login as anonymous
            Login login = new Login();
            login.setUsername(Settings.getUsername());
            out = login;
        } else {
            username = Settings.getUsername();
            String secret;
            if (Settings.getSecret() == null) {
                // Register by the given username, generate the a random secret
                secret = Settings.nextSecret();

                Register register = new Register();
                register.setUsername(username);
                register.setSecret(secret);

                out = register;

                // Set the secret so that when this method is called next time, it will try to log in
                Settings.setSecret(secret);
            } else {
                // Login by the given username
                secret = Settings.getSecret();

                Login login = new Login();
                login.setUsername(username);
                login.setSecret(secret);

                out = login;
            }
        }
        return out;
    }

    public synchronized boolean process(String msg) {
        Gson gson = new Gson();
        JSONObject jsonObjectForTextFrame = gson.fromJson(msg, JSONObject.class);
        this.textFrame.setOutputText(jsonObjectForTextFrame);

        JsonObject jsonObject = (new JsonParser()).parse(msg).getAsJsonObject();

        switch (jsonObject.get("command").getAsString()) {
            case "REDIRECT":
                log.info("Client received redirect");
                try {
                    RedirectAction.performAction(new Redirect(jsonObject));
                } catch (ValidationException e) {
                    e.printStackTrace();
                }
                break;
            case "REGISTER_SUCCESS":
                log.info("Registered successfully, try to login");
                connectToServer();
                break;
        }


        return false;
    }
}
