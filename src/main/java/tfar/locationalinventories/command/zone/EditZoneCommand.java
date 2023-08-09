package tfar.locationalinventories.command.zone;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import tfar.locationalinventories.WSD;
import tfar.locationalinventories.Zone;
import tfar.locationalinventories.command.exception.IllegalBoundingBoxException;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static tfar.locationalinventories.command.zone.CreateZoneCommand.isValid;

public class EditZoneCommand extends CommandBase {

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getName() {
		return "edit";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.locationalinventories.zone.edit.usage";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender.canUseCommand(this.getRequiredPermissionLevel(), "locationalinventories.zone.edit");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		String name = args[0];
		WSD wsd = WSD.getInstance(sender.getEntityWorld().provider.getDimension());
		Zone zone = wsd.getZoneByName(name);
		if (zone != null) {
			String op = args[1];
			if ("bb".equals(op) || "boundingbox".equals(op)) {
				BlockPos pos = parseBlockPos(sender, args, 2, false);
				BlockPos pos1 = parseBlockPos(sender, args, 5, false);
				if (!isValid(pos,pos1)) {
					throw new IllegalBoundingBoxException("commands.locationalinventories.zone.create.illegalboundingbox");
				} else {
					BlockPos start = new BlockPos(Math.min(pos.getX(), pos1.getX()), Math.min(pos.getY(), pos1.getY()), Math.min(pos.getZ(), pos1.getZ()));
					BlockPos end = new BlockPos(Math.max(pos.getX(), pos1.getX()), Math.max(pos.getY(), pos1.getY()), Math.max(pos.getZ(), pos1.getZ()));
					zone.start = start;
					zone.end = end;
					sender.sendMessage(new TextComponentTranslation("set bounding box to "+start+" to "+end));
				}
			} else if ("name".equals(op)) {
				sender.sendMessage(new TextComponentTranslation("changed name of zone from " +zone.name +" to "+args[2]));
				zone.name = args[2];
			} else if ("keepInventory".equals(op)) {
				zone.keepInventory = "true".equalsIgnoreCase(args[2]);
				sender.sendMessage(new TextComponentTranslation("set zone keepInventory of "+zone.name+" to " +zone.keepInventory));
			} else if ("showOutline".equals(op)) {
				zone.showOutline = "true".equalsIgnoreCase(args[2]);
			}
			wsd.markDirty();
		}
	}

	/**
	 * Get a list of options for when the user presses the TAB key
	 */
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1) {
			WSD wsd = WSD.getInstance(sender.getEntityWorld().provider.getDimension());
			return getListOfStringsMatchingLastWord(args, wsd.getZoneStorage().stream().map(zone -> zone.name).collect(Collectors.toList()));
		} else if (args.length == 2) {
			return getListOfStringsMatchingLastWord(args, Lists.newArrayList("bb","boundingbox","name","keepInventory","showOutline"));
		} else if (args.length > 2 && args.length <= 5) {
			if ("bb".equals(args[1]) || "boundingbox".equals(args[1])) {
				return getTabCompletionCoordinate(args, 2, targetPos);
			} if (args.length == 3 && "keepInventory".equals(args[1])) {
				return getListOfStringsMatchingLastWord(args, Lists.newArrayList("true","false"));
			} if (args.length == 3 && "showOutline".equals(args[1])) {
				return getListOfStringsMatchingLastWord(args, Lists.newArrayList("true","false"));
			}
		} else if (args.length > 5 && args.length <= 8) {
			if ("bb".equals(args[1]) || "boundingbox".equals(args[1])) {
				return getTabCompletionCoordinate(args, 5, targetPos);
			}
		}
		return Collections.emptyList();
	}
}
