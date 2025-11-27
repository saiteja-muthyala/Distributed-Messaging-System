package activitystreamer.server.Repository;

import activitystreamer.server.Control;
import activitystreamer.util.Settings;
import activitystreamer.util.commands.ServerAnnounce;

import java.util.HashMap;

/**
 * Singleton Repository,
 * Holds the data of the user information and the server load
 * May be handles the input and output of the data
 */

public class Repository {
    private static Repository repositoryInstance = new Repository();

    public static Repository getRepositoryInstance() {
        return repositoryInstance;
    }

    // Use the username as the key
    // With no encryption
    private static HashMap<String, UserInformation> userInformations;

    // The key of the hashmap is the id of the server, when receiving a server announce, it must contain an id
    private static HashMap<String, ServerLoad> serverLoads;

    private static String serverSecret;
    private static LockCount lockCount;

    private Repository() {
        userInformations = new HashMap<String, UserInformation>();
        serverLoads = new HashMap<String, ServerLoad>();
        serverSecret = Settings.getSecret();
        lockCount = new LockCount();
    }

    // Singleton pattern
    public static Repository getInstance() {
        if (repositoryInstance == null) {
            // Create an instance
            repositoryInstance = new Repository();
        }

        return repositoryInstance;
    }

    // Getter and setters
    public static HashMap<String, UserInformation> getUserInformations() {
        return userInformations;
    }

    public static HashMap<String, ServerLoad> getServerLoads() {
        return serverLoads;
    }

    public static void setServerLoad(HashMap<String, ServerLoad> serverLoad) {
        Repository.serverLoads = serverLoad;
    }

    public static String getServerSecret() {
        return serverSecret;
    }

    public static void setServerSecret(String serverSecret) {
        // Set the secret only if it doesn't exist
        if (serverSecret == null) {
            Repository.serverSecret = serverSecret;
        }
    }

    public static LockCount getLockCount() {
        return lockCount;
    }

    // Operations
    public static boolean checkServerSecret(String secret) {
        return serverSecret.equals(secret);
    }

    public static void setLockCount(LockCount lockCount) {
        Repository.lockCount = lockCount;
    }

    public boolean checkUserExists(String username) {
        return userInformations.containsKey(username);
    }

    // Check the user and secret exists
    public synchronized boolean checkUserSecretExist(String username, String secret) {
        if (checkUserExists(username)) {
            // Check the username match with the password
            UserInformation user = userInformations.get(username);
            return user.getSecret().equals(secret);
        } else {
            return false;
        }
    }

    public synchronized void updateServerLoad(ServerAnnounce serverAnnounce) {
        if (serverLoads.containsKey(serverAnnounce.getId())) {
            // Just update the loads
            serverLoads.get(serverAnnounce.getId()).setLoad(serverAnnounce.getLoad());
        } else {
            // Create a new object to store the load
            ServerLoad newLoad = new ServerLoad(serverAnnounce.getHostname(), serverAnnounce.getPort(),
                    serverAnnounce.getId(), serverAnnounce.getLoad());
            this.serverLoads.put(serverAnnounce.getId(), newLoad);
        }
    }

    public ServerLoad findServerToRedirect() {
        int currentLoad = Control.getInstance().numberOfClientConnected();
        for (ServerLoad serverLoad : serverLoads.values()) {
            if (serverLoad.getLoad() <= currentLoad - 2) {
                return serverLoad;
            }
        }
        return null;
    }

    public static UserInformation findUserInformationByName(String username) {
        return userInformations.get(username);
    }
}
