package activitystreamer.util;

import activitystreamer.util.commands.Command;
import activitystreamer.util.commands.InvalidMessage;

import java.util.ArrayList;

public class Response {
    public static final boolean CLOSE = true;
    public static final boolean DONT_CLOSE = false;

    private boolean closeAfterResponse;
    private BroadcastType broadcast;
    private ArrayList<Command> commands;
    private int size;

    public Response(Command command) {
        if (command instanceof InvalidMessage) {
            this.closeAfterResponse = true;
        } else {
            this.closeAfterResponse = false;
        }
        this.broadcast = BroadcastType.NOT;
        this.commands = new ArrayList<Command>();
        this.size = 0;
        addCommand(command);
    }

    public Response(boolean closeAfterResponse, BroadcastType broadcast) {
        this.closeAfterResponse = closeAfterResponse;
        this.broadcast = broadcast;
        this.size = 0;
        this.commands = null;
    }

    public Response(boolean closeAfterResponse, BroadcastType broadcast, Command command) {
        this.closeAfterResponse = closeAfterResponse;
        this.broadcast = broadcast;
        this.commands = new ArrayList<Command>();
        this.size = 0;
        addCommand(command);
    }

    public Response(boolean closeAfterResponse, BroadcastType broadcast, ArrayList<Command> commands) {
        this.closeAfterResponse = closeAfterResponse;
        this.broadcast = broadcast;
        this.commands = commands;
        this.size = commands == null ? 0 : commands.size();
    }

    public boolean isCloseAfterResponse() {
        return closeAfterResponse;
    }

    public void setCloseAfterResponse(boolean closeAfterResponse) {
        this.closeAfterResponse = closeAfterResponse;
    }

    public BroadcastType isBroadcast() {
        return broadcast;
    }

    public void setBroadcastType(BroadcastType broadcast) {
        this.broadcast = broadcast;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public void addCommand(Command command) {
        commands.add(command);
        size += 1;
    }

    public int getSize() {
        return size;
    }
}
