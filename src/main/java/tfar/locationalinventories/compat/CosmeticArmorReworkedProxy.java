package tfar.locationalinventories.compat;

import lain.mods.cos.api.CosArmorAPI;
import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraft.entity.player.EntityPlayer;
import tfar.locationalinventories.InventoryStorage;

public class CosmeticArmorReworkedProxy {

	public static void transferFrom(EntityPlayer player, InventoryStorage storage) {
		CAStacksBase caStacksBase = CosArmorAPI.getCAStacks(player.getUniqueID());
		storage.cosmeticarmorreworked = caStacksBase.serializeNBT();
	}

	public static void transferTo(InventoryStorage storage, EntityPlayer player) {
		CAStacksBase caStacksBase = CosArmorAPI.getCAStacks(player.getUniqueID());
			caStacksBase.deserializeNBT(storage.cosmeticarmorreworked);
	}
}
