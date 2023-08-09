package tfar.locationalinventories.compat;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import tfar.locationalinventories.InventoryStorage;

public class BaublesProxy {

    public static void transferFrom(EntityPlayer player, InventoryStorage storage) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < 7; i++) {
            storage.baubles.set(i,baubles.getStackInSlot(i));
        }
    }

    public static void transferTo(InventoryStorage storage, EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < 7; i++) {
            baubles.setStackInSlot(i, storage.baubles.get(i));
        }
    }
}
