package activitystreamer.util;

public enum BroadcastType {
    BOTH,   // Both broadcast to the CLIENTs and the OTHER_SERVERs
    OTHER_SERVER, // Only broadcast to the other servers (which exclude the server that sends the incoming message)
    CLIENT, // Only broadcast to the clients
    NOT     // Don't broadcast at all
}
