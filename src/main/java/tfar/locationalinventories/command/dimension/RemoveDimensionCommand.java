package tfar.locationalinventories.command.dimension;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import tfar.locationalinventories.WSD;

public class RemoveDimensionCommand extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), "locationalinventories.dimension.remove");
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.locationalinventories.dimension.remove.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int dim = Integer.parseInt(args[0]);
        WSD wsd = WSD.getInstance(dim);
        wsd.removeDimensionalInventory();
        sender.sendMessage(new TextComponentTranslation("Removed Dimensional Inventory from "+dim));
    }

}
