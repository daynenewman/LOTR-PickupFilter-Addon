package lotrpickupfilter.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import lotrpickupfilter.common.network.PacketPickupFilterSync;
import lotrpickupfilter.common.network.PickupFilterNetwork;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class PickupFilterEvents {
	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event) {
		EntityItem entityItem = event.item;
		if (entityItem != null && PickupFilterData.matches(event.entityPlayer, entityItem.getEntityItem())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.player instanceof EntityPlayerMP) {
			PickupFilterNetwork.network.sendTo(new PacketPickupFilterSync(PickupFilterData.getFilter(event.player)), (EntityPlayerMP) event.player);
		}
	}
}
