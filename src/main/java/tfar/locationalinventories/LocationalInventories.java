package tfar.locationalinventories;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import tfar.locationalinventories.command.ModCommand;

@Mod(modid = LocationalInventories.MODID, name = LocationalInventories.NAME, version = LocationalInventories.VERSION,acceptableRemoteVersions = "*")
public class LocationalInventories {
    public static final String MODID = "locationalinventories";
    public static final String NAME = "Locational Inventories";
    public static final String VERSION = "@VERSION@";

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent evt) {
        evt.registerServerCommand(new ModCommand());
    }

}
