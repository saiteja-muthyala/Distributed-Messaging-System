package activitystreamer.server.Repository;

import activitystreamer.server.Connection;

public class LockCount {

    private int countOfAllowed = 0;
    private int countOfDenied = 0;

    private boolean isCurrentServerRegisering = false;
    private Connection currentClientConnection = null;

    private UserInformation registeringUserInfo = null;


    public int getCountOfAllowed() {
        return countOfAllowed;
    }

    public void setCountOfAllowed(int countOfAllowed) {
        this.countOfAllowed = countOfAllowed;
    }

    public int getCountOfDenied() {
        return countOfDenied;
    }

    public void setCountOfDenied(int countOfDenied) {
        this.countOfDenied = countOfDenied;
    }

    public boolean isCurrentServerRegisering() {
        return isCurrentServerRegisering;
    }

    public synchronized void setCurrentServerRegisering(boolean currentServerRegisering) {
        isCurrentServerRegisering = currentServerRegisering;
    }

    public Connection getCurrentClientConnection() {
        return currentClientConnection;
    }

    public synchronized void setCurrentClientConnection(Connection currentClientConnection) {
        this.currentClientConnection = currentClientConnection;
    }

    public void addCountOfAllowed() {
        countOfAllowed = countOfAllowed + 1;
    }

    public void addCountOfDenied() {
        countOfDenied = countOfDenied + 1;
    }

    public void resetCount() {
        countOfAllowed = 0;
        countOfDenied = 0;

        setCurrentServerRegisering(false);
        setRegisteringUserInfo(null);
    }

    public UserInformation getRegisteringUserInfo() {
        return registeringUserInfo;
    }

    public void setRegisteringUserInfo(UserInformation registeringUserInfo) {
        this.registeringUserInfo = registeringUserInfo;
    }
}

