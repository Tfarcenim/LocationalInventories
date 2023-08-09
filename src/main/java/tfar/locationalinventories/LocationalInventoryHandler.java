package tfar.locationalinventories;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class LocationalInventoryHandler {

	/**
	 * @param e the event that fires when a player changes dimension
	 */
	@SubscribeEvent
	public static void changeDim(PlayerEvent.PlayerChangedDimensionEvent e) {
		EntityPlayer player = e.player;
		if (isNotExempt(player)) WSD.swapDimensions(player, e.fromDim, e.toDim);
	}

	@SubscribeEvent
	public static void outlineBoxes(TickEvent.WorldTickEvent e) {
		if (e.phase == TickEvent.Phase.END) {
			if (e.world.getTotalWorldTime() % 10 == 0) {
				WSD wsd = WSD.getInstance(e.world.provider.getDimension());
				for (Zone zone : wsd.getZoneStorage()) {

					if (zone.showOutline) {
						int x1 = zone.start.getX();
						int y1 = zone.start.getY() + 1;
						int z1 = zone.start.getZ();

						int x2 = zone.end.getX();
						int y2 = zone.end.getY();
						int z2 = zone.end.getZ();

						WorldServer serverWorld = (WorldServer) e.world;

						serverWorld.spawnParticle
								(EnumParticleTypes.REDSTONE, true, x1, y1, z1,
										1, 0.0D, 0.0D, 0.0D, 0);

						serverWorld.spawnParticle
								(EnumParticleTypes.REDSTONE, true, x1, y1, z2,
										1, 0.0D, 0.0D, 0.0D, 0);

						serverWorld.spawnParticle
								(EnumParticleTypes.REDSTONE, true, x1, y2, z1,
										1, 0.0D, 0.0D, 0.0D, 0);

						serverWorld.spawnParticle
								(EnumParticleTypes.REDSTONE, true, x1, y2, z2,
										1, 0.0D, 0.0D, 0.0D, 0);

						serverWorld.spawnParticle
								(EnumParticleTypes.REDSTONE, true, x2, y1, z1,
										1, 0.0D, 0.0D, 0.0D, 0);

						serverWorld.spawnParticle
								(EnumParticleTypes.REDSTONE, true, x2, y1, z2,
										1, 0.0D, 0.0D, 0.0D, 0);

						serverWorld.spawnParticle
								(EnumParticleTypes.REDSTONE, true, x2, y2, z1,
										1, 0.0D, 0.0D, 0.0D, 0);

						serverWorld.spawnParticle
								(EnumParticleTypes.REDSTONE, true, x2, y2, z2,
										1, 0.0D, 0.0D, 0.0D, 0);
					}
				}
			}
		}
	}

	public static boolean tempOverrideKeepInventory;
	public static boolean normalKeepInventory;

	@SubscribeEvent
	public static void onDeath(LivingDeathEvent e) {
		if (e.getEntityLiving() instanceof EntityPlayerMP && isNotExempt((EntityPlayer) e.getEntityLiving())) {
			World world = e.getEntityLiving().world;
			WSD wsd = WSD.getInstance(world.provider.getDimension());
			for (Zone zone : wsd.getZoneStorage()) {
				if (zone.containsPlayer((EntityPlayer) e.getEntityLiving())) {
					tempOverrideKeepInventory = true;
					normalKeepInventory = world.getGameRules().getBoolean("keepInventory");
					world.getGameRules().setOrCreateGameRule("keepInventory", String.valueOf(zone.keepInventory));
				}
			}
		}
	}

	@SubscribeEvent
	public static void login(PlayerEvent.PlayerLoggedInEvent e) {
		int dim = e.player.dimension;
		WSD.getInstance(dim).addPlayer(e.player.getUniqueID());
	}

	@SubscribeEvent
	public static void logout(PlayerEvent.PlayerLoggedOutEvent e) {
		int dim = e.player.dimension;
		WSD.getInstance(dim).removePlayer(e.player.getUniqueID());
	}

	@SubscribeEvent
	public static void onDeath1(PlayerDropsEvent e) {
		if (tempOverrideKeepInventory) {
			tempOverrideKeepInventory = false;
			e.getEntityPlayer().world.getGameRules().setOrCreateGameRule("keepInventory", String.valueOf(normalKeepInventory));
		}
	}

	public static boolean isNotExempt(EntityPlayer player) {
		WSD wsd = WSD.getDefaultInstance();
		return !wsd.isExempt(player);
	}

	/*@SubscribeEvent
	public static void preventIllegalInventoryAccess(PlayerInteractEvent.RightClickBlock e) {
		EntityPlayer player = e.getEntityPlayer();
		World world = player.world;
		if (!world.isRemote) {
			WSD wsd = WSD.getInstance(world.provider.getDimension());
			Zone playerZone = null;
			for (Zone zone : wsd.getZoneStorage()) {
				if (zone.containsPlayer(player)) {
					playerZone = zone;
					break;
				}
			}

			Zone blockZone = null;

			for (Zone zone : wsd.getZoneStorage()) {
				if (zone.containsPos(e.getPos())) {
					blockZone = zone;
					break;
				}
			}

			if (blockZone != playerZone) {
				e.setCanceled(true);
				player.sendMessage(new TextComponentTranslation("Cannot use blocks outside of current zone"));
			}
		}
	}*/

	@SubscribeEvent
	public static void useItem(PlayerInteractEvent.RightClickBlock e) {
		ItemStack stack = e.getItemStack();
		if (e.getHand() == EnumHand.MAIN_HAND && Utils.canCreateZones(stack)) {
			if (!e.getEntityPlayer().world.isRemote){
				NBTTagCompound compound = e.getItemStack().getTagCompound();

				NBTTagCompound display = compound.getCompoundTag("display");
				NBTTagList lore = display.getTagList("Lore", Constants.NBT.TAG_STRING);
				if (lore.tagCount() == 1) {
					lore.appendTag(new NBTTagString(e.getPos().toString()));
					NBTTagIntArray nbtTagIntArray = Utils.serializeBlockPos(e.getPos());
					display.setTag("pos1", nbtTagIntArray);
					e.getEntityPlayer().sendMessage(new TextComponentTranslation("Marked first corner at "+ e.getPos().toString()));
				} else if (lore.tagCount() == 2) {
					BlockPos secondPos = e.getPos();
					BlockPos firstPos = Utils.deserializeBlockPos(display.getIntArray("pos1"));
					BlockPos start = new BlockPos(Math.min(secondPos.getX(), firstPos.getX()), Math.min(secondPos.getY(), firstPos.getY()), Math.min(secondPos.getZ(), firstPos.getZ()));
					BlockPos end = new BlockPos(Math.max(secondPos.getX(), firstPos.getX()), Math.max(secondPos.getY(), firstPos.getY()), Math.max(secondPos.getZ(), firstPos.getZ()));
					Zone zone = new Zone();
					WSD wsd = WSD.get((WorldServer) e.getEntityPlayer().world);
					zone.start = start.add(0,-1,0);
					zone.end = end.add(1,1,1);
					zone.name = getNext(wsd);
					wsd.addZone(zone);
					e.getEntityPlayer().sendMessage(new TextComponentTranslation("Added Zone named " + zone.name));
					lore.removeTag(1);
				}
			}
			e.setCanceled(true);
		}
	}

	public static String getNext(WSD wsd) {
		String s = "zone";
		int i = 0;
		String s1 = s + i;
		while (wsd.getZoneByName(s1) != null) {
			i++;
			s1 = s + i;
		}
		return s1;
	}

	@SubscribeEvent
	public static void tick(TickEvent.PlayerTickEvent e) {
		EntityPlayer player = e.player;
		if (!player.world.isRemote && isNotExempt(player)) {
			int dim = player.dimension;
			WSD leaving = WSD.getInstance(dim);
			if (!leaving.hasDimensionalInventory) leaving = WSD.getDefaultInstance();
			for (Zone zone : leaving.getZoneStorage()) {
				boolean contained = zone.containsPlayer(player);
				boolean alreadyContained = zone.containedPlayers.contains(player.getUniqueID());
				//when player enters a zone and is not new
				if (contained && !alreadyContained) {
					zone.onZoneEnter(player, leaving);
					leaving.markDirty();
				} else if (!contained && alreadyContained) {
					zone.onZoneLeave(player, leaving);
					leaving.markDirty();
				}
			}
		}
	}
}
