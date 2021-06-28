package com.company;

// Work needed
public class ServerThread implements Runnable {
	NetworkUtility networkUtility;
	EndDevice endDevice;

	ServerThread (NetworkUtility networkUtility, EndDevice endDevice) {
		this.networkUtility = networkUtility;
		this.endDevice = endDevice;
		System.out.println("Server Ready for client " + NetworkLayerServer.clientCount);
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
		networkUtility.write(endDevice);
	}


	public Boolean deliverPacket (Packet packet) {
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
