package tfar.locationalinventories.compat;

import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import tfar.locationalinventories.InventoryStorage;

public class TravellersBackPackProxy {

	@CapabilityInject(ITravelersBackpack.class)
	public static final Capability<ITravelersBackpack> TRAVELERS_BACKPACK_CAPABILITY = null;

	public static void transferFrom(EntityPlayer player, InventoryStorage storage) {
		if (player.hasCapability(TRAVELERS_BACKPACK_CAPABILITY,null)) {
			ITravelersBackpack backpack = player.getCapability(TRAVELERS_BACKPACK_CAPABILITY,null);
			if (backpack != null) {
 				storage.travelersbackpack = backpack.getWearable().copy();
			}
		}
	}

	public static void transferTo(InventoryStorage storage, EntityPlayer player) {
		if (player.hasCapability(TRAVELERS_BACKPACK_CAPABILITY,null)) {
			ITravelersBackpack backpack = player.getCapability(TRAVELERS_BACKPACK_CAPABILITY,null);
			if (backpack != null) {
				backpack.setWearable(storage.travelersbackpack.copy());
				backpack.synchronise();
			}
		}
	}
}
