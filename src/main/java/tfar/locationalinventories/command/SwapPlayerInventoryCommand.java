package tfar.locationalinventories.command;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import tfar.locationalinventories.InventoryStorage;
import tfar.locationalinventories.Utils;
import tfar.locationalinventories.WSD;
import tfar.locationalinventories.Zone;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class SwapPlayerInventoryCommand extends CommandBase {
    @Override
    public String getName() {
        return "swap";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.getRequiredPermissionLevel(), "locationalinventories.swap");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.locationalinventories.dimension.add.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 6) {
            throw new CommandException("insufficient args");
        }
        if (args.length == 6) {
            swapDimToDim(server, sender, args);
        }
        if (args.length == 7) {
            swapDimToZone(server, sender, args);
        }
        if (args.length == 8) {
            swapZoneToZone(server, sender, args);
        }
    }

    public void swapDimToDim(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        int dimFrom = Integer.parseInt(args[1]);
        int dimTo = Integer.parseInt(args[4]);

        GameProfile fromProfile = server.getPlayerProfileCache().getGameProfileForUsername(args[2]);
        if (fromProfile == null) {
            throw new CommandException("commands.locationalinventories.swap.no_player.failed", args[2]);
        }

        GameProfile toProfile = server.getPlayerProfileCache().getGameProfileForUsername(args[5]);
        if (toProfile == null) {
            throw new CommandException("commands.locationalinventories.swap.no_player.failed", args[5]);
        }
        //now swap


        WSD wsdFrom = WSD.getInstance(dimFrom);

        if (!wsdFrom.hasDimensionalInventory) {
            wsdFrom = WSD.getDefaultInstance();
        }

        WSD wsdTo = WSD.getInstance(dimTo);
        if (!wsdTo.hasDimensionalInventory) {
            wsdTo = WSD.getDefaultInstance();
        }


        //first, transfer current player inventory to current dim storage
        InventoryStorage storageFrom = wsdFrom.getStorageForPlayer(fromProfile.getId());
        InventoryStorage storageTo = wsdTo.getStorageForPlayer(toProfile.getId());

        EntityPlayer fromPlayer = server.getPlayerList().getPlayerByUsername(fromProfile.getName());
        EntityPlayer toPlayer = server.getPlayerList().getPlayerByUsername(toProfile.getName());

        if (wsdFrom.containsPlayer(fromProfile.getId())) {
            Utils.transferFromPlayer(fromPlayer, storageFrom);
        }

        if (wsdTo.containsPlayer(toProfile.getId())) {
            Utils.transferFromPlayer(toPlayer, storageTo);
        }

        //swap the 2 inventories

        InventoryStorage fromCopy = storageFrom.copy();

        wsdFrom.setStorageForPlayer(fromProfile.getId(), storageTo.copy());

        wsdTo.setStorageForPlayer(toProfile.getId(), fromCopy.copy());

        if (wsdFrom.containsPlayer(fromPlayer.getUniqueID())) {
            wsdFrom.syncStorageToInventory(fromPlayer, null);
        }

        if (wsdFrom.containsPlayer(fromPlayer.getUniqueID())) {
            wsdTo.syncStorageToInventory(toPlayer, null);
        }
    }

    public void swapDimToZone(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        boolean dimToZone = "dimension".equals(args[0]);

        int dimFrom = Integer.parseInt(args[1]);
        if (dimToZone) {
            int dimTo = Integer.parseInt(args[4]);

            String zoneName = args[5];


            GameProfile fromProfile = server.getPlayerProfileCache().getGameProfileForUsername(args[2]);
            if (fromProfile == null) {
                throw new CommandException("commands.locationalinventories.swap.no_player.failed", args[2]);
            }

            GameProfile toProfile = server.getPlayerProfileCache().getGameProfileForUsername(args[6]);
            if (toProfile == null) {
                throw new CommandException("commands.locationalinventories.swap.no_player.failed", args[6]);
            }
            //now swap

            WSD wsdFrom = WSD.getInstance(dimFrom);

            if (!wsdFrom.hasDimensionalInventory) {
                wsdFrom = WSD.getDefaultInstance();
            }

            WSD wsdTo = WSD.getInstance(dimTo);

            Zone zone = wsdTo.getZoneByName(zoneName);

            if (zone == null) {
                throw new CommandException("commands.locationalinventories.swap.no_zone.failed", zoneName);
            }

            InventoryStorage fromStorage = wsdFrom.getStorageForPlayer(fromProfile.getId());

            InventoryStorage toStorage = wsdTo.getZoneStorageForPlayer(toProfile.getId(), zoneName);

            EntityPlayer fromPlayer = server.getPlayerList().getPlayerByUsername(fromProfile.getName());
            EntityPlayer toPlayer = server.getPlayerList().getPlayerByUsername(toProfile.getName());

            if (wsdFrom.containsPlayer(fromProfile.getId())) {
                Utils.transferFromPlayer(fromPlayer, fromStorage);
            }

            if (wsdTo.isPlayerInZone(toProfile.getId(),zoneName)) {
                Utils.transferFromPlayer(toPlayer, toStorage);
            }

            //swap the 2 inventories

            wsdFrom.setStorageForPlayer(fromProfile.getId(), toStorage.copy());

            wsdTo.setZoneStorageForPlayer(toProfile.getId(), fromStorage.copy(),zoneName);

            if (wsdFrom.containsPlayer(fromPlayer.getUniqueID())) {
                wsdFrom.syncStorageToInventory(fromPlayer, null);
            }

            if (wsdTo.isPlayerInZone(toPlayer.getUniqueID(),zoneName)) {
                wsdTo.syncStorageToInventory(toPlayer, zoneName);
            }

        } else {
            int dimTo = Integer.parseInt(args[5]);

            String zoneName = args[3];
            GameProfile fromProfile = server.getPlayerProfileCache().getGameProfileForUsername(args[3]);
            if (fromProfile == null) {
                throw new CommandException("commands.locationalinventories.swap.no_player.failed", args[3]);
            }

            GameProfile toProfile = server.getPlayerProfileCache().getGameProfileForUsername(args[6]);
            if (toProfile == null) {
                throw new CommandException("commands.locationalinventories.swap.no_player.failed", args[6]);
            }
            //now swap

            WSD wsdFrom = WSD.getInstance(dimFrom);

            if (!wsdFrom.hasDimensionalInventory) {
                wsdFrom = WSD.getDefaultInstance();
            }

            WSD wsdTo = WSD.getInstance(dimTo);

            Zone zone = wsdTo.getZoneByName(zoneName);

            if (zone == null) {
                throw new CommandException("commands.locationalinventories.swap.no_zone.failed", zoneName);
            }

            InventoryStorage fromStorage = wsdFrom.getStorageForPlayer(fromProfile.getId());

            InventoryStorage toStorage = wsdTo.getZoneStorageForPlayer(toProfile.getId(), zoneName);

            EntityPlayer fromPlayer = server.getPlayerList().getPlayerByUsername(fromProfile.getName());
            EntityPlayer toPlayer = server.getPlayerList().getPlayerByUsername(toProfile.getName());

            if (wsdFrom.containsPlayer(fromProfile.getId())) {
                Utils.transferFromPlayer(fromPlayer, fromStorage);
            }

            if (wsdTo.containsPlayer(toProfile.getId())) {
                Utils.transferFromPlayer(toPlayer, toStorage);
            }

            //swap the 2 inventories

            wsdFrom.setZoneStorageForPlayer(fromProfile.getId(), toStorage.copy(),zoneName);

            wsdTo.setStorageForPlayer(toProfile.getId(), fromStorage.copy());

            if (wsdFrom.isPlayerInZone(fromPlayer.getUniqueID(),zoneName)) {
                wsdFrom.syncStorageToInventory(fromPlayer, zoneName);
            }
            if (wsdTo.containsPlayer(toPlayer.getUniqueID())) {
                wsdTo.syncStorageToInventory(toPlayer, null);
            }
        }
    }

    //li swap zone 0 testZone Dev zone 1 testZone Dev1

    //args
    //0 zone
    //1 first dim id
    //2 zone name
    //3 Player 1 Name
    //4 zone
    //5 second dim id
    //6 zone name
    //7 Player 2 name
    public void swapZoneToZone(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        int dimFrom = Integer.parseInt(args[1]);
        int dimTo = Integer.parseInt(args[5]);

        String zone1Name = args[2];
        String zone2Name = args[6];


        GameProfile fromProfile = server.getPlayerProfileCache().getGameProfileForUsername(args[3]);
        if (fromProfile == null) {
            throw new CommandException("commands.locationalinventories.swap.no_player.failed", args[3]);
        }

        GameProfile toProfile = server.getPlayerProfileCache().getGameProfileForUsername(args[7]);
        if (toProfile == null) {
            throw new CommandException("commands.locationalinventories.swap.no_player.failed", args[7]);
        }
        //now swap

        WSD wsdFrom = WSD.getInstance(dimFrom);
        WSD wsdTo = WSD.getInstance(dimTo);

        Zone zone = wsdTo.getZoneByName(zone2Name);

        if (zone == null) {
            throw new CommandException("commands.locationalinventories.swap.no_zone.failed", zone2Name);
        }

        InventoryStorage fromStorage = wsdFrom.getZoneStorageForPlayer(fromProfile.getId(),zone1Name);

        InventoryStorage toStorage = wsdTo.getZoneStorageForPlayer(toProfile.getId(), zone2Name);

        EntityPlayer fromPlayer = server.getPlayerList().getPlayerByUsername(fromProfile.getName());
        EntityPlayer toPlayer = server.getPlayerList().getPlayerByUsername(toProfile.getName());

        if (wsdFrom.containsPlayer(fromProfile.getId())) {
            Utils.transferFromPlayer(fromPlayer, fromStorage);
        }

        if (wsdTo.containsPlayer(toProfile.getId())) {
            Utils.transferFromPlayer(toPlayer, toStorage);
        }

        //swap the 2 inventories

        wsdFrom.setZoneStorageForPlayer(fromProfile.getId(), toStorage.copy(),zone1Name);

        wsdTo.setZoneStorageForPlayer(toProfile.getId(), fromStorage.copy(),zone2Name);

        if (wsdFrom.isPlayerInZone(fromProfile.getId(),zone1Name)) {
            wsdFrom.syncStorageToInventory(fromPlayer, zone1Name);
        }
        if (wsdTo.isPlayerInZone(toProfile.getId(),zone2Name)) {
            wsdTo.syncStorageToInventory(toPlayer, zone2Name);
        }
    }

    //usage
    //li swap dimension 0 Dev dimension 1 Dev1
    //li swap dimension 0 Dev zone 1 testZone Dev1
    //li swap zone 0 testZone Dev dimension 1 Dev1
    //li swap zone 0 testZone Dev zone 1 testZone Dev1

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "dimension", "zone");
        }
        if (args.length == 2) {
            //this needs to be the dimension ids so return empty here
            return Collections.emptyList();
        }
        if (args.length == 3) {
            if ("dimension".equals(args[0])) {
                //this needs to be a player
                return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            } else if ("zone".equals(args[0])) {
                //get the zone name
                //the 1 arg should be dimension id
                return getListOfStringsMatchingLastWord(args, WSD.getInstance(Integer.parseInt(args[1])).getZoneNames());
            }
        }
        if (args.length == 4) {
            if ("dimension".equals(args[0])) {
                return getListOfStringsMatchingLastWord(args, "dimension", "zone");
            } else if ("zone".equals(args[0])) {
                return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            }
        }
        if (args.length == 5) {
            if ("dimension".equals(args[0])) {
                //needs to be a dim id so return empty
                return Collections.emptyList();
            } else if ("zone".equals(args[0])) {
                return getListOfStringsMatchingLastWord(args, "dimension", "zone");
            }
        } else if (args.length == 6) {
            if ("dimension".equals(args[0])) {
                if ("dimension".equals(args[3])) {
                    return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
                } else if ("zone".equals(args[3])) {
                    return getListOfStringsMatchingLastWord(args, WSD.getInstance(Integer.parseInt(args[4])).getZoneNames());
                }
                //needs to be a dim id so return empty
                return Collections.emptyList();
            } else if ("zone".equals(args[0])) {
                return Collections.emptyList();
            }
        } else if (args.length == 7) {
            if ("dimension".equals(args[0])) {
                if ("dimension".equals(args[3])) {

                } else if ("zone".equals(args[3])) {
                    //4 should be dim id
                    return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
                }
                //needs to be a dim id so return empty
                return Collections.emptyList();
            } else if ("zone".equals(args[0])) {
                if ("dimension".equals(args[4])) {
                    return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
                } else if ("zone".equals(args[4])) {
                    //4 should be dim id
                    return getListOfStringsMatchingLastWord(args, WSD.getInstance(Integer.parseInt(args[5])).getZoneNames());
                }
                //needs to be a dim id so return empty
                return Collections.emptyList();
            }
        } else if (args.length == 8) {
            if ("zone".equals(args[0])) {
                if ("zone".equals(args[4])) {
                    return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
                }
            }
        }
        return Collections.emptyList();
    }
}
