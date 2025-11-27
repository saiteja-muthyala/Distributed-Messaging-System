package activitystreamer.util.commands;

import activitystreamer.util.exceptions.ValidationException;
import com.google.gson.JsonObject;

public abstract class Command implements SelfValidate {
    protected String command;

    protected abstract String commandName();

    public Command() {
        this.command = this.commandName();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public void validate(JsonObject jsonObject) throws ValidationException {
        if (!jsonObject.has("command")) {
            throw new ValidationException("No 'command' field in the request.");
        }
    }
}
