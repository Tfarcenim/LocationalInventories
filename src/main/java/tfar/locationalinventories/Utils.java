package tfar.locationalinventories;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.math.BlockPos;
import tfar.locationalinventories.compat.ModProxy;

import java.util.List;

public class Utils {
    public static void fill(List<ItemStack> from, List<ItemStack> to) {
        for (int i = 0; i < from.size(); i++) {
            ItemStack stack = from.get(i);
            to.set(i,stack);
        }
    }

    public static NBTTagIntArray serializeBlockPos(BlockPos pos) {
        return new NBTTagIntArray(new int[]{pos.getX(),pos.getY(),pos.getZ()});
    }

    public static BlockPos deserializeBlockPos(int[] array) {
        return new BlockPos(array[0],array[1],array[2]);
    }

    public static void transferToPlayer(InventoryStorage storage, EntityPlayer player) {
        InventoryPlayer inv = player.inventory;
        fill(storage.armorInventory, inv.armorInventory);
        fill(storage.mainInventory, inv.mainInventory);
        fill(storage.offHandInventory, inv.offHandInventory);
        ModProxy.transferToModded(storage, player);
    }

    public static void transferFromPlayer(EntityPlayer player, InventoryStorage storage) {
        InventoryPlayer inv = player.inventory;
        fill(inv.armorInventory, storage.armorInventory);
        fill(inv.mainInventory, storage.mainInventory);
        fill(inv.offHandInventory, storage.offHandInventory);
        ModProxy.transferFromModded(player, storage);
    }

    public static boolean canCreateZones(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean("create_zones");
    }
}
