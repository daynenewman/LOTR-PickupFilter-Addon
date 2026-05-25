package lotrpickupfilter.common.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import lotrpickupfilter.PickupFilterAddon;

public class PickupFilterNetwork {
	public static SimpleNetworkWrapper network;

	public static void init() {
		network = NetworkRegistry.INSTANCE.newSimpleChannel(PickupFilterAddon.MODID);
		network.registerMessage(PacketPickupFilterSync.ClientHandler.class, PacketPickupFilterSync.class, 0, Side.CLIENT);
		network.registerMessage(PacketPickupFilterUpdate.ServerHandler.class, PacketPickupFilterUpdate.class, 1, Side.SERVER);
	}
}
