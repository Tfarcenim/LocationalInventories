package tfar.locationalinventories;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;
import java.util.UUID;

public class OtherPlayerInventory extends InventoryBasic {
    private final UUID otherPlayer;
    private final int dimension;
    @Nullable
    private final String zone;
    private final EntityPlayerMP opener;
    private boolean open;

    public OtherPlayerInventory(String title, boolean customName, int slotCount, UUID otherPlayer, int dimension, @Nullable String zone, EntityPlayerMP opener) {
        super(title, customName, slotCount);
        this.otherPlayer = otherPlayer;
        this.dimension = dimension;
        this.zone = zone;
        this.opener = opener;
    }

    @Override
    public void markDirty() {
        if (open) {
            super.markDirty();
            saveInventory();
        }
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        super.closeInventory(player);
        saveInventory();
    }

    public void saveInventory() {
        WSD wsd = WSD.getInstance(dimension);
        InventoryStorage storage;
        if (zone == null) {
            if (!wsd.hasDimensionalInventory) {
                wsd = WSD.getDefaultInstance();
            }
            storage = wsd.getStorageForPlayer(otherPlayer);
        } else {
            Zone zone = wsd.getZoneByName(this.zone);
            storage = zone.zoneInventoryStorage.getOrDefault(otherPlayer, new InventoryStorage());
        }

        NonNullList<ItemStack> mainInventory = storage.mainInventory;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = this.getStackInSlot(i);
            mainInventory.set(i, stack.copy());
        }
        NonNullList<ItemStack> armorInventory = storage.armorInventory;
        for (int i = 36; i < 40; i++) {
            ItemStack stack = this.getStackInSlot(i);
            armorInventory.set(i-36, stack.copy());
        }
        ItemStack offhand = this.getStackInSlot(40);
        storage.offHandInventory.set(0, offhand.copy());
        wsd.markDirty();
    }

    @Override
    public void openInventory(EntityPlayer player) {
        super.openInventory(player);

        WSD wsd = WSD.getInstance(dimension);
        InventoryStorage storage;
        if (zone == null) {
            if (!wsd.hasDimensionalInventory) {
                wsd = WSD.getDefaultInstance();
            }
            storage = wsd.getStorageForPlayer(otherPlayer);

        } else {
            Zone zone = wsd.getZoneByName(this.zone);
            storage = zone.zoneInventoryStorage.getOrDefault(otherPlayer, new InventoryStorage());
        }

        NonNullList<ItemStack> mainInventory = storage.mainInventory;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mainInventory.get(i);
            this.setInventorySlotContents(i, stack.copy());
        }

        NonNullList<ItemStack> armorInventory = storage.armorInventory;
        for (int i = 0; i < 4; i++) {
            ItemStack stack = armorInventory.get(i);
            this.setInventorySlotContents(i + 36, stack.copy());
        }

        NonNullList<ItemStack> offHandInventory = storage.offHandInventory;
        this.setInventorySlotContents(40, offHandInventory.get(0).copy());
        open = true;
    }
}
