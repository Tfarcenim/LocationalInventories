package tfar.locationalinventories;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class InventoryStorage implements INBTSerializable<NBTTagCompound> {

    //vanilla
    public NonNullList<ItemStack> mainInventory = NonNullList.withSize(36, ItemStack.EMPTY);
    public NonNullList<ItemStack> armorInventory = NonNullList.withSize(4, ItemStack.EMPTY);
    public NonNullList<ItemStack> offHandInventory = NonNullList.withSize(1, ItemStack.EMPTY);

    //todo baubles etc
    public ItemStack travelersbackpack = ItemStack.EMPTY;
    public NBTTagCompound cosmeticarmorreworked = new NBTTagCompound();
    public List<ItemStack> baubles = NonNullList.withSize(7, ItemStack.EMPTY);

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setTag("mainInventory",serializeList(mainInventory));
        nbt.setTag("armorInventory",serializeList(armorInventory));
        nbt.setTag("offHandInventory",serializeList(offHandInventory));
        if(!travelersbackpack.isEmpty())
        nbt.setTag("travelersbackpack",travelersbackpack.serializeNBT());
        if (!cosmeticarmorreworked.isEmpty()) {
            nbt.setTag("cosmeticarmorreworked",cosmeticarmorreworked);
        }
        if (!baubles.isEmpty()) {
            nbt.setTag("baubles",serializeList(baubles));
        }
        return nbt;
    }

    public NBTTagList serializeList(List<ItemStack> stacks) {
        NBTTagList nbtTagList = new NBTTagList();
        for (ItemStack stack : stacks) {
            nbtTagList.appendTag(stack.serializeNBT());
        }
        return nbtTagList;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        deserializeList(nbt.getTagList("mainInventory", Constants.NBT.TAG_COMPOUND),mainInventory);
        deserializeList(nbt.getTagList("armorInventory", Constants.NBT.TAG_COMPOUND),armorInventory);
        deserializeList(nbt.getTagList("offHandInventory", Constants.NBT.TAG_COMPOUND),offHandInventory);
        if (nbt.hasKey("travelersbackpack")) {
            travelersbackpack = new ItemStack(nbt.getCompoundTag("travelersbackpack"));
        }
        if (nbt.hasKey("cosmeticarmorreworked")) {
            cosmeticarmorreworked = nbt.getCompoundTag("cosmeticarmorreworked");
        }
        if (nbt.hasKey("baubles")) {
            deserializeList(nbt.getTagList("baubles", Constants.NBT.TAG_COMPOUND),baubles);
        }
    }

    public void deserializeList(NBTTagList tagList, List<ItemStack> stacks) {
        int index = 0;
        for (NBTBase nbtBase : tagList) {
            stacks.set(index,new ItemStack((NBTTagCompound) nbtBase));
            index++;
        }
    }

    public InventoryStorage copy() {
        InventoryStorage storage = new InventoryStorage();
        storage.deserializeNBT(this.serializeNBT());
        return storage;
    }
}
