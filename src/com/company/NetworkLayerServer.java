package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.company.Constants.INFINITY;
import static java.lang.System.exit;

//Work needed
public class NetworkLayerServer {

	static int clientCount = 0;
	static ArrayList<Router> routers = new ArrayList<>();
	static RouterStateChanger stateChanger = null;
	static Map<IPAddress,Integer> clientInterfaces = new HashMap<>(); //Each map entry represents number of client end devices connected to the interface
	static Map<IPAddress, EndDevice> endDeviceMap = new HashMap<>();
	static ArrayList<EndDevice> endDevices = new ArrayList<>();
	static Map<Integer, Integer> deviceIDtoRouterID = new HashMap<>();
	static Map<IPAddress, Integer> interfaceToRouterID = new HashMap<>();
	static Map<Integer, Router> routerMap = new HashMap<>();

	public static void main (String[] args) {

		//Task: Maintain an active client list

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

		initRoutingTables(); //Initialize routing tables for all routers

		DVR(1); //Update routing table using distance vector routing until convergence
		exit(0); // TODO: Remove this halt.
		simpleDVR(1);
		stateChanger = new RouterStateChanger();//Starts a new thread which turns on/off routers randomly depending on parameter Constants.LAMBDA

		while(true) {
			try {
				Socket socket = serverSocket.accept();
				System.out.println("Client" + (clientCount + 1) + " attempted to connect");
				EndDevice endDevice = getClientDeviceSetup();
				clientCount++;
				endDevices.add(endDevice);
				endDeviceMap.put(endDevice.getIpAddress(),endDevice);
				new ServerThread(new NetworkUtility(socket), endDevice);
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

		printRoutersToFile("Log1BeforeDVR.txt");
		int debugIntTemp = 0;

		while (true) {
			boolean atLeastOneUpdateOccurred = false;

			for (Router router : routers) {
				System.out.println("Processing router #" + router.getRouterId()); // TODO: Remove this debug line
				for (RoutingTableEntry routingTableEntry : router.getRoutingTable()) {
					double neighbourDistance = routingTableEntry.getDistance();
					System.out.print("\t\tneighbour #" + routingTableEntry.getRouterId() + "...."); // TODO: Remove this debug line
					if (neighbourDistance == INFINITY || neighbourDistance == 0) {
						// Not a neighbour or the router itself; got to do nothing.
						System.out.println("not updating; cause: " + (neighbourDistance==0 ? "0" : "INFINITY")); // TODO: Remove this debug line
						continue;
					}
					System.out.println("updating; (distance,gatewayID)=("+neighbourDistance+","+routingTableEntry.getGatewayRouterId()+")"); // TODO: Remove this debug line
					int neighbourID = routingTableEntry.getRouterId();
					Router neighbourRouter = routers.get(neighbourID - 1);
					atLeastOneUpdateOccurred = router.updateRoutingTable(neighbourRouter);
				}
			}

			debugIntTemp++;
			if (!atLeastOneUpdateOccurred) break;
		}

		System.out.println("Looped for #" + debugIntTemp + " time(s)."); // TODO: Remove this debug line
		printRoutersToFile("Log2AfterDVR.txt");
	}

	public static synchronized void simpleDVR (int startingRouterId) {

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
			if(i == r) {
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
