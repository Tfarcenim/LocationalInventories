package tfar.locationalinventories.command.exception;

import net.minecraft.command.CommandException;

public class IllegalBoundingBoxException extends CommandException {
    public IllegalBoundingBoxException(String message, Object... objects) {
        super(message, objects);
    }
}
