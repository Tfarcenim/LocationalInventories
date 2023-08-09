package tfar.locationalinventories.command.zone;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import tfar.locationalinventories.WSD;
import tfar.locationalinventories.Zone;
import tfar.locationalinventories.command.exception.IllegalBoundingBoxException;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CreateZoneCommand extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), "locationalinventories.zone.create");
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.locationalinventories.zone.create.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 8) {
            throw new WrongUsageException("commands.locationalinventories.zone.create.usage");
        } else {
            BlockPos pos = parseBlockPos(sender, args, 0, false);
            BlockPos pos1 = parseBlockPos(sender, args, 3, false);
            if (!isValid(pos,pos1)) {
                throw new IllegalBoundingBoxException("commands.locationalinventories.zone.create.illegalboundingbox");
            } else {
                BlockPos start = new BlockPos(Math.min(pos.getX(),pos1.getX()),Math.min(pos.getY(),pos1.getY()),Math.min(pos.getZ(),pos1.getZ()));
                BlockPos end = new BlockPos(Math.max(pos.getX(),pos1.getX()),Math.max(pos.getY(),pos1.getY()),Math.max(pos.getZ(),pos1.getZ()));
                Zone zone = new Zone();
                zone.start = start;
                zone.end = end;
                zone.keepInventory = "true".equalsIgnoreCase(args[6]);
                zone.name = args[7];
                WSD wsd = WSD.getInstance(sender.getEntityWorld().provider.getDimension());
                wsd.addZone(zone);
                sender.sendMessage(new TextComponentTranslation("Added Zone named "+zone.name));
            }
        }
    }

    public static boolean isValid(BlockPos pos1, BlockPos pos2) {
        return pos1.getX() != pos2.getX() && pos1.getY() != pos2.getY() && pos1.getZ() != pos2.getZ();
    }

    /**
     * Get a list of options for when the user presses the TAB key
     */
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {

        if (args.length > 0 && args.length <= 3) {
            return getTabCompletionCoordinate(args, 0, targetPos);
        }
        else if (args.length > 3 && args.length <= 6) {
            return getTabCompletionCoordinate(args, 3, targetPos);
        } else if (args.length == 7) {
            return getListOfStringsMatchingLastWord(args, "true", "false");
        }
        return Collections.emptyList();
    }
}
