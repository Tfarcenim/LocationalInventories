package tfar.locationalinventories.command.open;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

public class OpenInventoryCommand extends CommandTreeBase {

    public OpenInventoryCommand() {
        addSubcommand(new OpenZoneInventoryCommand());
        addSubcommand(new OpenDimensionalInventoryCommand());
    }

    @Override
    public String getName() {
        return "open";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "open";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), "locationalinventories.open");
    }

}
