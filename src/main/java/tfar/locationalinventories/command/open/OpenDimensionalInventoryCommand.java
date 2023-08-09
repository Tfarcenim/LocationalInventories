package tfar.locationalinventories.command.open;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import tfar.locationalinventories.OtherPlayerInventory;
import tfar.locationalinventories.WSD;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class OpenDimensionalInventoryCommand extends CommandBase {
    @Override
    public String getName() {
        return "dimension";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), "locationalinventories.open.dimension");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "locationalinventories.open.dimension.command";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length >= 2) {
            int dim = Integer.parseInt(args[0]);
            EntityPlayerMP otherPlayer = getPlayer(server,sender,args[1]);
            WSD wsd = WSD.getInstance(dim);
            if (!wsd.hasDimensionalInventory) {
                wsd = WSD.getDefaultInstance();
            }

            if (wsd.containsPlayer(otherPlayer.getUniqueID())) {
                sender.sendMessage(new TextComponentString("Can't open active inventory"));
            } else {
                if (sender instanceof EntityPlayerMP) {
                    EntityPlayerMP player = (EntityPlayerMP) sender;
                    String title = otherPlayer.getDisplayName().getFormattedText();
                    OtherPlayerInventory inventoryBasic = new OtherPlayerInventory(title, true, 45, otherPlayer.getUniqueID(), dim, null, player);
                    player.displayGUIChest(inventoryBasic);
                }
            }
        } else {
            throw new WrongUsageException("commands.locationalinventories.open.dimension.usage");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return Collections.emptyList();
        } else if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}
