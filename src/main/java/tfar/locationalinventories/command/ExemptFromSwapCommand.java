package tfar.locationalinventories.command;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import tfar.locationalinventories.WSD;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ExemptFromSwapCommand extends CommandBase {
    @Override
    public String getName() {
        return "exempt";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), "locationalinventories.exempt");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "locationalinventories.exempt.command";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length >= 1 && args[0].length() > 0) {
            boolean exempt = true;
            if (args.length >= 2) {
                exempt = !"false".equals(args[args.length-1]);
            }

            WSD wsd = WSD.getDefaultInstance();

            GameProfile gameprofile = server.getPlayerProfileCache().getGameProfileForUsername(args[0]);

            if (exempt) {
                wsd.addExemptPlayer(gameprofile.getId());
            } else  {
                wsd.removeExemptPlayer(gameprofile.getId());
            }

            notifyCommandListener(sender, this, "commands.locationalinventories.exempt.success", args[0]);
            } else {
            throw new WrongUsageException("commands.locationalinventories.exempt.usage");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length >= 1) {
            List<String> suggest1 = getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            List<String> suggest2 = getListOfStringsMatchingLastWord(args,"true","false");
            suggest1.addAll(suggest2);
            return suggest1;
        }
        return Collections.emptyList();
    }
}
