package activitystreamer.server.Repository;

/**
 * Denote an entity of the load of a server
 */
public class ServerLoad {

    private String hostname;
    private int port;
    private String id;
    private int load;

    public ServerLoad(String hostname, int port, String id, int load) {
        this.hostname = hostname;
        this.port = port;
        this.id = id;
        this.load = load;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public void setId(String id) {
        // It is not the command object, so don't need to generate the hash by itself
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
