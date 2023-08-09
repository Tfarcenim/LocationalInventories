package tfar.locationalinventories.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import tfar.locationalinventories.InventoryStorage;

public class ModProxy {

	public static final boolean travellersbackpack = Loader.isModLoaded("travelersbackpack");

	public static final boolean cosmeticarmorreworked = Loader.isModLoaded("cosmeticarmorreworked");

	public static final boolean baubles = Loader.isModLoaded("baubles");


	public static void transferToModded(InventoryStorage storage, EntityPlayer player) {
		if (travellersbackpack) {
			TravellersBackPackProxy.transferTo(storage, player);
		} if (cosmeticarmorreworked) {
			CosmeticArmorReworkedProxy.transferTo(storage, player);
		} if (baubles) {
			BaublesProxy.transferTo(storage,player);
		}
	}

	public static void transferFromModded(EntityPlayer player, InventoryStorage storage) {
		if (travellersbackpack) {
			TravellersBackPackProxy.transferFrom(player, storage);
		} if (cosmeticarmorreworked) {
			CosmeticArmorReworkedProxy.transferFrom(player,storage);
		} if (baubles) {
			BaublesProxy.transferFrom(player, storage);
		}
	}
}
