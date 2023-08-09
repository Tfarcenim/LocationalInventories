package tfar.locationalinventories;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

import static tfar.locationalinventories.Utils.*;

public class Zone implements INBTSerializable<NBTTagCompound> {

    public Set<UUID> containedPlayers = new HashSet<>();
    public boolean keepInventory = false;
    public boolean showOutline = true;
    public BlockPos start;
    public BlockPos end;
    public String name;
    public Map<UUID, InventoryStorage> zoneInventoryStorage = new HashMap<>();

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList nbtTagList = new NBTTagList();
        for (UUID uuid : containedPlayers) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setUniqueId("uuid", uuid);
            nbtTagList.appendTag(compound);
        }
        nbt.setTag("containedPlayers", nbtTagList);
        nbt.setBoolean("keepInventory", keepInventory);
        nbt.setBoolean("showOutline", showOutline);
        nbt.setTag("start", Utils.serializeBlockPos(start));
        nbt.setTag("end", Utils.serializeBlockPos(end));
        nbt.setString("name", name);
        NBTTagList nbtList = new NBTTagList();
        for (Map.Entry<UUID, InventoryStorage> entry : zoneInventoryStorage.entrySet()) {
            UUID uuid = entry.getKey();
            InventoryStorage storage = entry.getValue();
            NBTTagCompound compound1 = new NBTTagCompound();
            compound1.setUniqueId("uuid", uuid);
            compound1.setTag("inventorystorage", storage.serializeNBT());
            nbtList.appendTag(compound1);
        }
        nbt.setTag("data", nbtList);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

        keepInventory = nbt.getBoolean("keepInventory");
        showOutline = nbt.getBoolean("showOutline");
        start = Utils.deserializeBlockPos(nbt.getIntArray("start"));
        end = Utils.deserializeBlockPos(nbt.getIntArray("end"));
        name = nbt.getString("name");

        NBTTagList uuidList = nbt.getTagList("containedPlayers", Constants.NBT.TAG_COMPOUND);
        for (NBTBase nbtBase : uuidList) {
            UUID uuid = ((NBTTagCompound) nbtBase).getUniqueId("uuid");
            containedPlayers.add(uuid);
        }

        NBTTagList list = nbt.getTagList("data", Constants.NBT.TAG_COMPOUND);
        for (NBTBase nbtBase : list) {
            NBTTagCompound compound = (NBTTagCompound) nbtBase;
            UUID uuid = compound.getUniqueId("uuid");
            InventoryStorage storage = new InventoryStorage();
            storage.deserializeNBT((NBTTagCompound) compound.getTag("inventorystorage"));
            zoneInventoryStorage.put(uuid, storage);
        }
    }

    public void onZoneEnter(EntityPlayer player, WSD leaving) {

        UUID playerUUID = player.getUniqueID();
        containedPlayers.add(playerUUID);
        //store the data in temp holding
        InventoryStorage fromInventoryStorage = leaving.getStorageForPlayer(playerUUID);



        if (fromInventoryStorage == null) {
            fromInventoryStorage = new InventoryStorage();
        }

        transferFromPlayer(player, fromInventoryStorage);

        //store in global wsd
        leaving.setStorageForPlayer(playerUUID, fromInventoryStorage);

        leaving.removePlayer(playerUUID);

        //now get zone inv

        InventoryStorage zoneStorage = zoneInventoryStorage.get(playerUUID);

        if (zoneStorage == null) {
            zoneInventoryStorage.put(playerUUID, new InventoryStorage());
            zoneStorage = zoneInventoryStorage.get(playerUUID);
        }

        transferToPlayer(zoneStorage, player);

        player.sendMessage(new TextComponentString("now entering " + name + " zone"));
    }

    public void onZoneLeave(EntityPlayer player, WSD entering) {

        UUID playerUUID = player.getUniqueID();
        containedPlayers.remove(playerUUID);
        //store the data in temp holding
        InventoryStorage fromZoneStorage = zoneInventoryStorage.get(playerUUID);

        transferFromPlayer(player, fromZoneStorage);

        //store in zone
        zoneInventoryStorage.put(playerUUID, fromZoneStorage);

        entering.addPlayer(playerUUID);

        //now get wsd inv

        InventoryStorage dimStorage = entering.getStorageForPlayer(playerUUID);

        if (dimStorage == null) {
            entering.setStorageForPlayer(playerUUID, new InventoryStorage());
            dimStorage = entering.getStorageForPlayer(playerUUID);
        }

        transferToPlayer(dimStorage, player);

        player.sendMessage(new TextComponentString("now leaving " + name + " zone"));
    }

    public boolean containsPlayer(EntityPlayer player) {
        return player.posX > start.getX() && player.posY > start.getY() && player.posZ > start.getZ() &&
                player.posX < end.getX() && player.posY < end.getY() && player.posZ < end.getZ();
    }

    public boolean containsPos(BlockPos pos) {
        return pos.getX() >= start.getX() && pos.getY() > start.getY() && pos.getZ() > start.getZ() &&
                pos.getX() < end.getX() && pos.getY() < end.getY() && pos.getZ() < end.getZ();
    }
}
