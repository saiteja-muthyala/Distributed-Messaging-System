package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public class Redirect extends Command {
    protected String hostname;

    protected int port;

    public Redirect() {
        super();
    }

    public Redirect(JsonObject jsonObject) throws ValidationException {
        super();
        validate(jsonObject);
        hostname = jsonObject.get("hostname").getAsString();
        port = jsonObject.get("port").getAsInt();
    }

    public Redirect(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    protected String commandName() {
        return "REDIRECT";
    }

    @Override
    public void validate(JsonObject jsonObject) throws ValidationException {
        super.validate(jsonObject);
        if (!jsonObject.has("hostname")) {
            throw new ValidationException("No 'hostname' field in the request.");
        }

        if (!jsonObject.has("port")) {
            throw new ValidationException("No 'hostname' field in the request.");
        } else {
            // The port must be an integer
            int p = Integer.parseInt(jsonObject.get("port").getAsString());
        }
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}