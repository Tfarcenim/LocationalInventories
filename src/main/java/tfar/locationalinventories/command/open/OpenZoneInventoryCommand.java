package tfar.locationalinventories.command.open;

import com.mojang.authlib.GameProfile;
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
import tfar.locationalinventories.Zone;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OpenZoneInventoryCommand extends CommandBase {
    @Override
    public String getName() {
        return "zone";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), "locationalinventories.open.zone");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "locationalinventories.open.zone.command";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length >= 3) {
            int dim = Integer.parseInt(args[0]);
            String zoneName = args[1];
            EntityPlayerMP otherPlayer = getPlayer(server,sender,args[2]);
            WSD wsd = WSD.getInstance(dim);
            if (!wsd.hasDimensionalInventory) {
                wsd = WSD.getDefaultInstance();
            }
            Zone zone = wsd.getZoneByName(zoneName);
            if (zone.containsPlayer(otherPlayer)) {
                sender.sendMessage(new TextComponentString("Can't open active inventory"));
            } else {
                if (sender instanceof EntityPlayerMP) {
                    EntityPlayerMP player = (EntityPlayerMP) sender;
                    String title = otherPlayer.getDisplayName().getFormattedText();
                    OtherPlayerInventory inventoryBasic = new OtherPlayerInventory(title, true, 45, otherPlayer.getUniqueID(), dim, zoneName, player);
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
            WSD wsd = WSD.getInstance(sender.getEntityWorld().provider.getDimension());
            return getListOfStringsMatchingLastWord(args, wsd.getZoneStorage().stream().map(zone -> zone.name).collect(Collectors.toList()));
        }  else if (args.length == 3) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}
