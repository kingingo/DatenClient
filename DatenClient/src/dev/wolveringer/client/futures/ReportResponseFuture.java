package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketReportResponse;
import dev.wolveringer.report.ReportEntity;

public class ReportResponseFuture extends PacketResponseFuture<ReportEntity[]>{
	private UUID packetUUID;
	public ReportResponseFuture(Client client, Packet handeling) {
		super(client, handeling);
		this.packetUUID = handeling.getPacketUUID();
	}

	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketReportResponse)
			if(((PacketReportResponse) packet).getRequestPacket().equals(packetUUID))
				done(((PacketReportResponse) packet).getEntities());
	}

}
