package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.company.Constants.*;

//Work needed
public class NetworkLayerServer {

	static int clientCount = 0;
	static int totalNumberOfDVRs = 0;
	static ArrayList<Router> routers = new ArrayList<>();
	static RouterStateChanger stateChanger = null;
	static Map<IPAddress,Integer> clientInterfaces = new HashMap<>(); //Each map entry represents number of client end devices connected to the interface
	static Map<IPAddress, EndDevice> endDeviceMap = new HashMap<>();
	static ArrayList<EndDevice> endDevices = new ArrayList<>();
	static Map<Integer, Integer> deviceIDtoRouterID = new HashMap<>();
	static Map<IPAddress, Integer> interfaceToRouterID = new HashMap<>();
	static Map<Integer, Router> routerMap = new HashMap<>();

	public static void main (String[] args) {
		if (DEBUG_DVR_MODE) {
			// Clear the debug log file and write current date-time
			try {
				PrintWriter pw = new PrintWriter(DVR_LOOP_LOG_PATH);
				pw.write("Debug log of: " + new Date() + "\n\n");
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Task: Maintain an active client list

		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(4444);
		} catch (IOException ex) {
			Logger.getLogger(NetworkLayerServer.class.getName()).log(Level.SEVERE, null, ex);
		}

		System.out.println("Server Ready: " + serverSocket.getInetAddress().getHostAddress());
		System.out.println("Creating router topology");

		readTopology();
		printRouters();

		initRoutingTables(); // Initialize routing tables for all routers

		printRoutersToFile("RoutingTablesBeforeFirstDVR.txt");

		DVR(4); // Update routing table using distance vector routing until convergence
		//simpleDVR(4);

		stateChanger = new RouterStateChanger(); // Starts a new thread which turns on/off routers randomly depending on parameter Constants.LAMBDA

		while(true) {
			try {
				Socket socket = serverSocket.accept();
				System.out.println("Client #" + (clientCount + 1) + " attempted to connect");
				EndDevice endDevice = getClientDeviceSetup();
				clientCount++;
				endDevices.add(endDevice);
				endDeviceMap.put(endDevice.getIpAddress(), endDevice);
				ServerThread serverThread = new ServerThread(new NetworkUtility(socket), endDevice);
			} catch (IOException ex) {
				Logger.getLogger(NetworkLayerServer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public static void initRoutingTables () {
		for (Router router : routers) {
			router.initiateRoutingTable();
		}
	}

	public static synchronized void DVR (int startingRouterId) {
		/**
		 * pseudocode
		 */
        /*
        while(convergence) {
            //convergence means no change in any routingTable before and after executing the following for loop
            for each router r <starting from the router with routerId = startingRouterId, in any order> {
                1. T <- getRoutingTable of the router r
                2. N <- find routers which are the active neighbors of the current router r
                3. Update routingTable of each router t in N using the
                   routing table of r [Hint: Use t.updateRoutingTable(r)]
            }
        }
        */

		int totalNumberOfIterationsInDVR = 0;

		System.out.println("DVR started from router #" + startingRouterId); // TODO: Remove this debug line
		while (true) {
			if (DEBUG_DVR_MODE) appendStringToFile("Starting DVR loop #" + (totalNumberOfDVRs+1) + "." + (totalNumberOfIterationsInDVR+1) + "------------------------------------------------------------------------\n", DVR_LOOP_LOG_PATH);
			boolean atLeastOneUpdateOccurred = false;

			for (int i=0; i<routers.size(); i++) {
				Router router = routers.get((i + startingRouterId - 1) % routers.size());
				if (DEBUG_DVR_MODE) appendStringToFile("Processing router #" + router.getRouterId() + "\n", DVR_LOOP_LOG_PATH);
				for (RoutingTableEntry routingTableEntry : router.getRoutingTable()) {
					double neighbourDistance = routingTableEntry.getDistance();
					if (DEBUG_DVR_MODE) appendStringToFile("\t\tneighbour #" + routingTableEntry.getRouterId() + "....", DVR_LOOP_LOG_PATH);

					if ((Math.abs(neighbourDistance-INFINITY)<EPSILON) || neighbourDistance == 0) {
						// Not a neighbour or the router itself; got to do nothing.
						if (DEBUG_DVR_MODE) appendStringToFile("not updating; cause: " + (neighbourDistance==0 ? "same router\n" : "infinite distance\n"), DVR_LOOP_LOG_PATH);
						continue;
					}

					if (DEBUG_DVR_MODE) appendStringToFile("updating; (distance,gatewayID)=("+neighbourDistance+","+routingTableEntry.getGatewayRouterId()+")\n", DVR_LOOP_LOG_PATH);

					int neighbourID = routingTableEntry.getRouterId();
					Router neighbourRouter = routers.get(neighbourID - 1);

					if (!atLeastOneUpdateOccurred) {
						atLeastOneUpdateOccurred = router.sfUpdateRoutingTable(neighbourRouter);
					}
					else {
						router.sfUpdateRoutingTable(neighbourRouter);
					}
				}
			}
			if (DEBUG_DVR_MODE) appendStringToFile("\n\n", DVR_LOOP_LOG_PATH);
			totalNumberOfIterationsInDVR++;
			if (!atLeastOneUpdateOccurred) break;
		}
		System.out.println("DVR ended from router #" + startingRouterId + ", after loop(s) #" + totalNumberOfIterationsInDVR); // TODO: Remove this debug line
		printRoutersToFile("RoutingTablesAfterLastDVR.txt");
		totalNumberOfDVRs++;
	}

	public static synchronized void simpleDVR (int startingRouterId) {
		int totalNumberOfIterationsInDVR = 0;

		System.out.println("DVR started from router #" + startingRouterId); // TODO: Remove this debug line
		while (true) {
			if (DEBUG_DVR_MODE) appendStringToFile("Starting DVR loop #" + (totalNumberOfDVRs+1) + "." + (totalNumberOfIterationsInDVR+1) + "------------------------------------------------------------------------\n", DVR_LOOP_LOG_PATH);
			boolean atLeastOneUpdateOccurred = false;

			for (int i=0; i<routers.size(); i++) {
				Router router = routers.get((i + startingRouterId - 1) % routers.size());
				if (DEBUG_DVR_MODE) appendStringToFile("Processing router #" + router.getRouterId() + "\n", DVR_LOOP_LOG_PATH);
				for (RoutingTableEntry routingTableEntry : router.getRoutingTable()) {
					double neighbourDistance = routingTableEntry.getDistance();
					if (DEBUG_DVR_MODE) appendStringToFile("\t\tneighbour #" + routingTableEntry.getRouterId() + "....", DVR_LOOP_LOG_PATH);

					if ((Math.abs(neighbourDistance-INFINITY)<EPSILON) || neighbourDistance == 0) {
						// Not a neighbour or the router itself; got to do nothing.
						if (DEBUG_DVR_MODE) appendStringToFile("not updating; cause: " + (neighbourDistance==0 ? "same router\n" : "infinite distance\n"), DVR_LOOP_LOG_PATH);
						continue;
					}

					if (DEBUG_DVR_MODE) appendStringToFile("updating; (distance,gatewayID)=("+neighbourDistance+","+routingTableEntry.getGatewayRouterId()+")\n", DVR_LOOP_LOG_PATH);

					int neighbourID = routingTableEntry.getRouterId();
					Router neighbourRouter = routers.get(neighbourID - 1);

					if (!atLeastOneUpdateOccurred) {
						atLeastOneUpdateOccurred = router.updateRoutingTable(neighbourRouter);
					}
					else {
						router.updateRoutingTable(neighbourRouter);
					}
				}
			}
			if (DEBUG_DVR_MODE) appendStringToFile("\n\n", DVR_LOOP_LOG_PATH);
			totalNumberOfIterationsInDVR++;
			if (!atLeastOneUpdateOccurred) break;
		}
		System.out.println("DVR ended from router #" + startingRouterId + ", after loop(s) #" + totalNumberOfIterationsInDVR); // TODO: Remove this debug line
		printRoutersToFile("RoutingTablesAfterLastDVR.txt");
		totalNumberOfDVRs++;
	}

	public static EndDevice getClientDeviceSetup () {
		Random random = new Random(System.currentTimeMillis());
		int r = Math.abs(random.nextInt(clientInterfaces.size()));

		System.out.println("Size: " + clientInterfaces.size() + "\n" + r);

		IPAddress ip = null;
		IPAddress gateway = null;

		int i = 0;
		for (Map.Entry<IPAddress, Integer> entry : clientInterfaces.entrySet()) {
			IPAddress key = entry.getKey();
			Integer value = entry.getValue();
			if (i == r) {
				gateway = key;
				ip = new IPAddress(gateway.getBytes()[0] + "." + gateway.getBytes()[1] + "." + gateway.getBytes()[2] + "." + (value+2));
				value++;
				clientInterfaces.put(key, value);
				deviceIDtoRouterID.put(endDevices.size(), interfaceToRouterID.get(key));
				break;
			}
			i++;
		}

		EndDevice device = new EndDevice(ip, gateway, endDevices.size());

		System.out.println("Device : " + ip + "::::" + gateway);
		return device;
	}

	public static void printRouters () {
		for(int i = 0; i < routers.size(); i++) {
			System.out.println("------------------\n" + routers.get(i));
		}
	}

	public static String strRouters () {
		String string = "";
		for (int i = 0; i < routers.size(); i++) {
			string += "\n------------------\n" + routers.get(i).getRoutingTableAsString();
		}
		string += "\n\n";
		return string;
	}

	private static void printRoutersToFile (String fileName) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
			for (Router router : routers) bw.write(router.getRoutingTableAsString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static synchronized void appendStringToFile (String string, String filePath) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath,true));
			bw.write(string);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readTopology () {
		Scanner inputFile = null;
		try {
			inputFile = new Scanner(new File("Topology.txt"));
			//skip first 27 lines
			int skipLines = 27;
			for(int i = 0; i < skipLines; i++) {
				inputFile.nextLine();
			}

			//start reading contents
			while(inputFile.hasNext()) {
				inputFile.nextLine();
				int routerId;
				ArrayList<Integer> neighborRouters = new ArrayList<>();
				ArrayList<IPAddress> interfaceAddresses = new ArrayList<>();
				Map<Integer, IPAddress> interfaceIDtoIP = new HashMap<>();

				routerId = inputFile.nextInt();

				int count = inputFile.nextInt();
				for(int i = 0; i < count; i++) {
					neighborRouters.add(inputFile.nextInt());
				}
				count = inputFile.nextInt();
				inputFile.nextLine();

				for(int i = 0; i < count; i++) {
					String string = inputFile.nextLine();
					IPAddress ipAddress = new IPAddress(string);
					interfaceAddresses.add(ipAddress);
					interfaceToRouterID.put(ipAddress, routerId);

					/**
					 * First interface is always client interface
					 */
					if(i == 0) {
						//client interface is not connected to any end device yet
						clientInterfaces.put(ipAddress, 0);
					}
					else {
						interfaceIDtoIP.put(neighborRouters.get(i - 1), ipAddress);
					}
				}
				Router router = new Router(routerId, neighborRouters, interfaceAddresses, interfaceIDtoIP);
				routers.add(router);
				routerMap.put(routerId, router);
			}


		} catch (FileNotFoundException ex) {
			Logger.getLogger(NetworkLayerServer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
