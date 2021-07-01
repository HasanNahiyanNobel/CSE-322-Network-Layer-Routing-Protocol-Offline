package com.company;

import java.util.Random;

import static com.company.NetworkLayerServer.endDevices;
import static com.company.NetworkLayerServer.routers;

// Work needed
public class ServerThread implements Runnable {
	NetworkUtility networkUtility;
	EndDevice endDevice;

	ServerThread (NetworkUtility networkUtility, EndDevice endDevice) {
		this.networkUtility = networkUtility;
		this.endDevice = endDevice;

		System.out.println("Server Ready for client " + NetworkLayerServer.clientCount);

		networkUtility.write(endDevice);

		new Thread(this).start();
	}

	/**
	 * Synchronizes actions with client.
	 */
	@Override
	public void run () {
        /*
	        Tasks:
	        1. Upon receiving a packet and recipient, call deliverPacket(packet)
	        2. If the packet contains "SHOW_ROUTE" request, then fetch the required information and send back to client
	        3. Either send acknowledgement with number of hops or send failure message back to client
        */
		while (networkUtility.getSocket().isConnected()) {
			// Read the packet
			Packet packet = (Packet) networkUtility.read();

			// Assign a random receiver
			int indexOfRandomReceiver = new Random().nextInt(endDevices.size());
			EndDevice randomReceiver = endDevices.get(indexOfRandomReceiver);
			packet.setDestinationIP(randomReceiver.getIpAddress());

			// Deliver the packet
			deliverPacket(packet);
		}
	}


	public Boolean deliverPacket (Packet packet) {
		String sourceNetworkAddress = packet.getSourceIP().getNetworkAddress();
		String destinationNetworkAddress = packet.getDestinationIP().getNetworkAddress();

		Router sourceRouter = null;
		Router destinationRouter = null;

		for (Router router : routers) {
			for (IPAddress ipAddress : router.getInterfaceAddresses()) {
				if (ipAddress.getNetworkAddress().equals(sourceNetworkAddress)) sourceRouter = router;
				if (ipAddress.getNetworkAddress().equals(destinationNetworkAddress)) destinationRouter = router;
			}
		}

		if (sourceRouter==null || destinationRouter==null) {
			System.out.println("Some terrible error occurred in ServerThread.");
		}

		System.out.println(sourceRouter.getRouterId() + "----" + destinationRouter.getRouterId());

        /*
        1. Find the router s which has an interface
                such that the interface and source end device have same network address.
        2. Find the router d which has an interface
                such that the interface and destination end device have same network address.
        3. Implement forwarding, i.e., s forwards to its gateway router x considering d as the destination.
                similarly, x forwards to the next gateway router y considering d as the destination,
                and eventually the packet reaches to destination router d.

            3(a) If, while forwarding, any gateway x, found from routingTable of router r is in down state[x.state==FALSE]
                    (i) Drop packet
                    (ii) Update the entry with distance Constants.INFINITY
                    (iii) Block NetworkLayerServer.stateChanger.t
                    (iv) Apply DVR starting from router r.
                    (v) Resume NetworkLayerServer.stateChanger.t

            3(b) If, while forwarding, a router x receives the packet from router y,
                    but routingTableEntry shows Constants.INFINITY distance from x to y,
                    (i) Update the entry with distance 1
                    (ii) Block NetworkLayerServer.stateChanger.t
                    (iii) Apply DVR starting from router x.
                    (iv) Resume NetworkLayerServer.stateChanger.t

        4. If 3(a) occurs at any stage, packet will be dropped,
            otherwise successfully sent to the destination router
        */
		return false;
	}

	@Override
	public boolean equals (Object object) {
		return super.equals(object); //To change body of generated methods, choose Tools | Templates.
	}
}
