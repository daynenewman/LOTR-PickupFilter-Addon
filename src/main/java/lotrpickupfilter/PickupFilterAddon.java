package lotrpickupfilter;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import lotrpickupfilter.client.PickupFilterGuiHandler;
import lotrpickupfilter.common.PickupFilterEvents;
import lotrpickupfilter.common.network.PickupFilterNetwork;
import net.minecraftforge.common.MinecraftForge;

@Mod(
	modid = PickupFilterAddon.MODID,
	name = "LOTR Pickup Filter",
	version = "1.0.0",
	dependencies = "required-after:lotr",
	acceptableRemoteVersions = "*"
)
public class PickupFilterAddon {
	public static final String MODID = "lotrpickupfilter";

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		PickupFilterNetwork.init();
		PickupFilterEvents events = new PickupFilterEvents();
		MinecraftForge.EVENT_BUS.register(events);
		FMLCommonHandler.instance().bus().register(events);
		if (event.getSide().isClient()) {
			MinecraftForge.EVENT_BUS.register(new PickupFilterGuiHandler());
		}
	}
}
