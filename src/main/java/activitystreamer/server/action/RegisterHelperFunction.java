package activitystreamer.server.action;

import activitystreamer.server.Repository.Repository;
import activitystreamer.server.Repository.UserInformation;
import activitystreamer.util.commands.UserSecret;

class RegisterHelperFunction {
    static final String SUCESS_MSG_FORMAT = "register success for %s";
    static final String FAIL_MSG_FORMAT = "%s is already registered with the system";

    static boolean isRegisteredLocally(UserSecret userSecret) {
        return Repository.getInstance().checkUserExists(userSecret.getUsername());
    }

    static boolean writeUserInformation(UserSecret userSecret) {
        UserInformation userInformation = new UserInformation(userSecret.getUsername(), userSecret.getSecret());
        UserInformation result = Repository.getUserInformations().put(userSecret.getUsername(), userInformation);
        return result != null;
    }

    static String getSucessMessage(String username) {
        return String.format(SUCESS_MSG_FORMAT, username);
    }

    static String getFailMessage(String username) {
        return String.format(FAIL_MSG_FORMAT, username);
    }
}
