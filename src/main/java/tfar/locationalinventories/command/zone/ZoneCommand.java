package tfar.locationalinventories.command.zone;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

public class ZoneCommand extends CommandTreeBase {

    public ZoneCommand() {
        addSubcommand(new CreateZoneCommand());
        addSubcommand(new RemoveZoneCommand());
        addSubcommand(new EditZoneCommand());
        addSubcommand(new ZoneItemCommand());
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), "locationalinventories.zone");
    }

    @Override
    public String getName() {
        return "zone";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "locationalinventories.zone.command";
    }
}
