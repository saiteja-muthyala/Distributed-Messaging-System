package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public class ServerAnnounce extends Command {

    private String hostname;
    private int port;
    private int load;

    private String id;

    public ServerAnnounce() {
        super();
    }

    public ServerAnnounce(JsonObject jsonObject) throws ValidationException {
        super();
        validate(jsonObject);
        this.port = jsonObject.get("port").getAsInt();
        this.hostname = jsonObject.get("hostname").getAsString();
        this.load = jsonObject.get("load").getAsInt();
    }

    public ServerAnnounce(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        String hash = hostname + port;
        this.id = Integer.toString(hash.hashCode());
    }

    public ServerAnnounce(String hostname, int port, int load) {
        this(hostname, port);
        this.load = load;
    }

    @Override
    protected String commandName() {
        return "SERVER_ANNOUNCE";
    }

    @Override
    public void validate(JsonObject jsonObject) throws ValidationException {
        super.validate(jsonObject);
        if (!jsonObject.has("hostname")) {
            throw new ValidationException("No 'hostname' field in the request.");
        }
        if (!jsonObject.has("port")) {
            throw new ValidationException("No 'port' field in the request.");
        }
        if (!jsonObject.has("load")) {
            throw new ValidationException("No 'load' field in the request.");
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

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
