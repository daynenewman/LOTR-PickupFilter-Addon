package lotrpickupfilter.common.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import lotrpickupfilter.client.PickupFilterClientData;
import lotrpickupfilter.common.PickupFilterData;
import net.minecraft.nbt.NBTTagCompound;

import java.util.LinkedHashSet;
import java.util.Set;

public class PacketPickupFilterSync implements IMessage {
	private NBTTagCompound data = new NBTTagCompound();

	public PacketPickupFilterSync() {
	}

	public PacketPickupFilterSync(Set<String> filter) {
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

	public static class ClientHandler implements IMessageHandler<PacketPickupFilterSync, IMessage> {
		@Override
		public IMessage onMessage(PacketPickupFilterSync message, MessageContext ctx) {
			PickupFilterClientData.filter = new LinkedHashSet<String>(PickupFilterData.readFilter(message.data));
			return null;
		}
	}
}
