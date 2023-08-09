package tfar.locationalinventories.command;

import com.google.common.collect.Lists;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;
import tfar.locationalinventories.LocationalInventories;
import tfar.locationalinventories.command.dimension.DimensionCommand;
import tfar.locationalinventories.command.open.OpenInventoryCommand;
import tfar.locationalinventories.command.zone.ZoneCommand;

import java.util.List;

public class ModCommand extends CommandTreeBase {

    public ModCommand() {
        addSubcommand(new ZoneCommand());
        addSubcommand(new DimensionCommand());
        addSubcommand(new ExemptFromSwapCommand());
        addSubcommand(new SwapPlayerInventoryCommand());
        addSubcommand(new OpenInventoryCommand());
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getName() {
        return LocationalInventories.MODID;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.locationalinventories.usage";
    }

    @Override
    public List<String> getAliases() {
        return Lists.newArrayList("li");
    }
}
