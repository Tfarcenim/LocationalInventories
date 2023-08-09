package tfar.locationalinventories.command.dimension;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

public class DimensionCommand extends CommandTreeBase {

    public DimensionCommand() {
        addSubcommand(new AddDimensionCommand());
        addSubcommand(new RemoveDimensionCommand());
    }

    @Override
    public String getName() {
        return "dimension";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), "locationalinventories.dimension");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "locationalinventories.dimension.command";
    }
}
