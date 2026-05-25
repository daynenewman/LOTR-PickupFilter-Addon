package lotrpickupfilter.common.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import lotrpickupfilter.common.PickupFilterData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Set;

public class PacketPickupFilterUpdate implements IMessage {
	private NBTTagCompound data = new NBTTagCompound();

	public PacketPickupFilterUpdate() {
	}

	public PacketPickupFilterUpdate(Set<String> filter) {
		data = PickupFilterData.writeFilter(filter);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		data = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, data);
	}

	public static class ServerHandler implements IMessageHandler<PacketPickupFilterUpdate, IMessage> {
		@Override
		public IMessage onMessage(PacketPickupFilterUpdate message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			Set<String> filter = PickupFilterData.readFilter(message.data);
			PickupFilterData.setFilter(player, filter);
			return new PacketPickupFilterSync(filter);
		}
	}
}
