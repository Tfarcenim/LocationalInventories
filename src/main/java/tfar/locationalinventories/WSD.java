package tfar.locationalinventories;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class WSD extends WorldSavedData {

    private Map<UUID,InventoryStorage> uuidStorageHashMap = new HashMap<>();

    public boolean hasDimensionalInventory;

    private HashSet<UUID> containedPlayers = new HashSet<>();

    protected List<UUID> exempt = new ArrayList<>();

    private final List<Zone> zoneStorage = new ArrayList<>();

    //this is called via reflection, do not remove
    public WSD(String name) {
        super(name);
    }

    public static WSD getInstance(int dimension) {
        return get(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimension));
    }

    public void addPlayer(UUID player) {
        containedPlayers.add(player);
    }

    public void removePlayer(UUID player) {
        containedPlayers.remove(player);
    }

    public List<Zone> getZoneStorage() {
        return zoneStorage;
    }

    public static WSD getDefaultInstance() {
        return getInstance(0);
    }

    public static WSD get(WorldServer world) {
        MapStorage storage = world.getPerWorldStorage();
        String name = LocationalInventories.MODID+":"+world.provider.getDimension();
        WSD instance = (WSD) storage.getOrLoadData(WSD.class, name);

        if (instance == null) {
            WSD wsd = new WSD(name);
            storage.setData(name, wsd);
            instance = (WSD) storage.getOrLoadData(WSD.class, name);
        }
        return instance;
    }

    public void addZone(Zone zone) {
        zoneStorage.add(zone);
        markDirty();
    }

    public List<String> getZoneNames() {
        return zoneStorage.stream().map(zone -> zone.name).collect(Collectors.toList());
    }

    public boolean removeZoneByName(String name) {
        return zoneStorage.removeIf(zone -> zone.name.equals(name));
    }

    public Zone getZoneByName(String name) {
        return zoneStorage.stream().filter(zone -> zone.name.equals(name)).findFirst().orElse(null);
    }

    public void addDimensionalInventory() {
        hasDimensionalInventory = true;
        markDirty();
    }

    public void removeDimensionalInventory() {
        hasDimensionalInventory = false;
        markDirty();
    }

    public InventoryStorage getStorageForPlayer(UUID player) {
        return uuidStorageHashMap.get(player);
    }

    public InventoryStorage getZoneStorageForPlayer(UUID player,String zoneName) {
        Zone zone = getZoneByName(zoneName);
        InventoryStorage storage = zone.zoneInventoryStorage.get(player);
        return storage;
    }

    public void setStorageForPlayer(UUID player,InventoryStorage storage) {
        uuidStorageHashMap.put(player,storage);
        markDirty();
    }

    public void setZoneStorageForPlayer(UUID player,InventoryStorage storage,String zoneName) {
        Zone zone = getZoneByName(zoneName);
        zone.zoneInventoryStorage.put(player,storage);
        markDirty();
    }

    /**
     * transfers internal dimensional or zone storage to player inventory
     * @param player
     * @param zoneName
     */
    public void syncStorageToInventory(EntityPlayer player, @Nullable String zoneName) {
        if (zoneName == null) {
            Utils.transferToPlayer(getStorageForPlayer(player.getUniqueID()), player);
        } else {
            Zone zone = getZoneByName(zoneName);
            Utils.transferToPlayer(zone.zoneInventoryStorage.get(player.getUniqueID()),player);
        }
    }

    public boolean isExempt(EntityPlayer player) {
        return exempt.contains(player.getUniqueID());
    }

    public void addExemptPlayer(UUID player) {
        exempt.add(player);
        markDirty();
    }

    public void removeExemptPlayer(UUID player) {
        exempt.remove(player);
        markDirty();
    }

    public static void swapDimensions(EntityPlayer player, int from, int to) {
        WSD wsdfrom = getInstance(from);
        WSD wsdto = getInstance(to);
        if (wsdto.hasDimensionalInventory || wsdfrom.hasDimensionalInventory) {

            if (!wsdfrom.hasDimensionalInventory) wsdfrom = getDefaultInstance();
            if (!wsdto.hasDimensionalInventory) wsdto = getDefaultInstance();

            UUID playerUUID = player.getUniqueID();
            //store the data in temp holding
            InventoryStorage fromInventoryStorage = new InventoryStorage();

            Utils.transferFromPlayer(player,fromInventoryStorage);

            //store in old dim
            wsdfrom.uuidStorageHashMap.put(playerUUID, fromInventoryStorage);

            wsdfrom.markDirty();

            //now get new Dim Values

            InventoryStorage newStorage = wsdto.uuidStorageHashMap.get(playerUUID);

            if (newStorage == null) {
                wsdto.uuidStorageHashMap.put(playerUUID, new InventoryStorage());
                newStorage = wsdto.uuidStorageHashMap.get(playerUUID);
            }

            wsdfrom.removePlayer(playerUUID);
            wsdto.addPlayer(playerUUID);

            Utils.transferToPlayer(newStorage,player);

            wsdto.markDirty();
        }
    }

    public boolean containsPlayer(UUID player) {
        return containedPlayers.contains(player);
    }

    public boolean isPlayerInZone(UUID player,String zoneName) {
        return getZoneByName(zoneName).containedPlayers.contains(player);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("data", Constants.NBT.TAG_COMPOUND);
        for (NBTBase nbtBase : list) {
            NBTTagCompound compound = (NBTTagCompound)nbtBase;
            UUID uuid = compound.getUniqueId("uuid");
            InventoryStorage storage = new InventoryStorage();
            storage.deserializeNBT((NBTTagCompound) compound.getTag("inventorystorage"));
            uuidStorageHashMap.put(uuid,storage);
        }

        NBTTagList zoneList = nbt.getTagList("zoneList", Constants.NBT.TAG_COMPOUND);
        for (NBTBase nbtBase : zoneList) {
            Zone zone = new Zone();
            zone.deserializeNBT((NBTTagCompound) nbtBase);
            zoneStorage.add(zone);
        }

        NBTTagList exemptList = nbt.getTagList("exempt", Constants.NBT.TAG_STRING);
        for (NBTBase nbtBase : exemptList) {
            UUID uuid = UUID.fromString(((NBTTagString)nbtBase).getString());
            exempt.add(uuid);
        }

        hasDimensionalInventory = nbt.getBoolean("isDimensionalInventory");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<UUID,InventoryStorage> entry: uuidStorageHashMap.entrySet()) {
            UUID uuid = entry.getKey();
            InventoryStorage storage = entry.getValue();
            NBTTagCompound compound1 = new NBTTagCompound();
            compound1.setUniqueId("uuid",uuid);
            compound1.setTag("inventorystorage",storage.serializeNBT());
            list.appendTag(compound1);
        }

        compound.setTag("data",list);

        NBTTagList zoneList = new NBTTagList();
        for (Zone zone: zoneStorage) {
            zoneList.appendTag(zone.serializeNBT());
        }
        compound.setTag("zoneList",zoneList);

        NBTTagList exemptList = new NBTTagList();
        for (UUID uuid: exempt) {
            zoneList.appendTag(new NBTTagString(uuid.toString()));
        }
        compound.setTag("exempt",exemptList);

        compound.setBoolean("isDimensionalInventory",hasDimensionalInventory);
        return compound;
    }
}
