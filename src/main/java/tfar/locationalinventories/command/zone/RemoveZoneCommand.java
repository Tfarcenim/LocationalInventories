package tfar.locationalinventories.command.zone;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import tfar.locationalinventories.WSD;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveZoneCommand extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), "locationalinventories.zone.remove");
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.locationalinventories.zone.remove.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String name = args[0];
        WSD wsd = WSD.getInstance(sender.getEntityWorld().provider.getDimension());
        boolean found = wsd.removeZoneByName(name);
        sender.sendMessage(found ? new TextComponentTranslation("Removed Zone named "+name) :
                new TextComponentTranslation("Couldn't find Zone "+name));
    }

    /**
     * Get a list of options for when the user presses the TAB key
     */
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            WSD wsd = WSD.getInstance(sender.getEntityWorld().provider.getDimension());
            return getListOfStringsMatchingLastWord(args, wsd.getZoneStorage().stream().map(zone -> zone.name).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }
}
