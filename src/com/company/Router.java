package com.company;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static com.company.Constants.INFINITY;
import static com.company.NetworkLayerServer.routers;

// Work needed
public class Router {
	private int routerId;
	private int numberOfInterfaces;
	private ArrayList<IPAddress> interfaceAddresses; // List of IP address of all interfaces of the router
	private ArrayList<RoutingTableEntry> routingTable; // Used to implement DVR
	private ArrayList<Integer> neighbourRouterIDs; // Contains both "UP" and "DOWN" state routers
	private Boolean state; // True represents "UP" state and false is for "DOWN" state
	private Map<Integer, IPAddress> gatewayIDtoIP;
	private final double ROUTER_UP_PROBABILITY = 0.8;

	public Router() {
		interfaceAddresses = new ArrayList<>();
		routingTable = new ArrayList<>();
		neighbourRouterIDs = new ArrayList<>();

		// 80% Probability that the router is up
		double p = new Random().nextDouble();
		state = p <= ROUTER_UP_PROBABILITY;

		numberOfInterfaces = 0;
	}

	public Router(int routerId, ArrayList<Integer> neighbourRouters, ArrayList<IPAddress> interfaceAddresses, Map<Integer, IPAddress> gatewayIDtoIP) {
		this.routerId = routerId;
		this.interfaceAddresses = interfaceAddresses;
		this.neighbourRouterIDs = neighbourRouters;
		this.gatewayIDtoIP = gatewayIDtoIP;
		routingTable = new ArrayList<>();

		// 80% Probability that the router is up
		double p = new Random().nextDouble();
		state = p <= ROUTER_UP_PROBABILITY;

		numberOfInterfaces = interfaceAddresses.size();
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append("Router ID: ").append(routerId).append("\n").append("Interfaces: \n");
		for (int i = 0; i < numberOfInterfaces; i++) {
			string.append(interfaceAddresses.get(i).getString()).append("\t");
		}
		string.append("\n" + "Neighbours: \n");
		for (Integer neighbourRouterID : neighbourRouterIDs) {
			string.append(neighbourRouterID).append("\t");
		}
		return string.toString();
	}

	/**
	 * Initialize the distance (hop count) for each router.
	 * For itself, distance=0; for any connected router with state=true, distance=1; otherwise distance=Constants.INFINITY;
	 */
	public void initiateRoutingTable() {
		for (int aRoutersID=1; aRoutersID<=routers.size(); aRoutersID++) {

			double aRoutersDistance;
			int aRoutersGatewayID;

			if (this.routerId==aRoutersID) {
				aRoutersDistance = 0; // Distance of this router
				aRoutersGatewayID = aRoutersID; // ID of its own
			}
			else if (neighbourRouterIDs.contains(aRoutersID) && routers.get(aRoutersID-1).getState()) {
				aRoutersDistance = 1; // Distance of a neighbour router which is up
				aRoutersGatewayID = aRoutersID; // ID of the neighbour
			}
			else {
				aRoutersDistance = INFINITY; // Either the router is not neighbour, or not up, or both
				aRoutersGatewayID = -1; // Gateway is not applicable
			}

			routingTable.add(new RoutingTableEntry(aRoutersID,aRoutersDistance,aRoutersGatewayID));
		}
	}

	/**
	 * Delete all the routingTableEntry
	 */
	public void clearRoutingTable() {

	}

	/**
	 * Update the routing table for this router using the entries of Router neighbour
	 * @param neighbour
	 */
	public boolean updateRoutingTable(Router neighbour) {
		return false;
	}

	public boolean sfUpdateRoutingTable (Router neighbour) {
		return false;
	}

	/**
	 * If the state was up, down it; if state was down, up it
	 */
	public void revertState() {
		state = !state;
		if(state) { initiateRoutingTable(); }
		else { clearRoutingTable(); }
	}

	public int getRouterId() {
		return routerId;
	}

	public void setRouterId(int routerId) {
		this.routerId = routerId;
	}

	public int getNumberOfInterfaces() {
		return numberOfInterfaces;
	}

	public void setNumberOfInterfaces(int numberOfInterfaces) {
		this.numberOfInterfaces = numberOfInterfaces;
	}

	public ArrayList<IPAddress> getInterfaceAddresses() {
		return interfaceAddresses;
	}

	public void setInterfaceAddresses(ArrayList<IPAddress> interfaceAddresses) {
		this.interfaceAddresses = interfaceAddresses;
		numberOfInterfaces = interfaceAddresses.size();
	}

	public ArrayList<RoutingTableEntry> getRoutingTable() {
		return routingTable;
	}

	public void addRoutingTableEntry(RoutingTableEntry entry) {
		this.routingTable.add(entry);
	}

	public ArrayList<Integer> getNeighbourRouterIDs () {
		return neighbourRouterIDs;
	}

	public void setNeighbourRouterIDs (ArrayList<Integer> neighbourRouterIDs) { this.neighbourRouterIDs = neighbourRouterIDs; }

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	public Map<Integer, IPAddress> getGatewayIDtoIP() { return gatewayIDtoIP; }

	public void printRoutingTable() {
		DecimalFormat df = new DecimalFormat("00.00");

		System.out.println("Router #" + routerId);
		System.out.println("DestID\tDistance\tNextHop");
		for (RoutingTableEntry routingTableEntry : routingTable) {
			System.out.println(
					"  " + getFormattedString(String.valueOf(routingTableEntry.getRouterId()), 2) +
					"\t  " + getFormattedString(String.valueOf(routingTableEntry.getDistance()), 4) +
					"\t\t  " + getFormattedString(String.valueOf(routingTableEntry.getGatewayRouterId()), 2)
			);
		}
		System.out.println("-----------------------");
	}

	public String strRoutingTable() {
		StringBuilder string = new StringBuilder("Router" + routerId + "\n");
		string.append("DestID Distance NextHop\n");
		for (RoutingTableEntry routingTableEntry : routingTable) {
			string.append(routingTableEntry.getRouterId()).append(" ").append(routingTableEntry.getDistance()).append(" ").append(routingTableEntry.getGatewayRouterId()).append("\n");
		}

		string.append("-----------------------\n");
		return string.toString();
	}

	private String getFormattedString (String string, int desiredLengthWithLeadingSpaces) {
		if (string.length() >= desiredLengthWithLeadingSpaces) {
			return string;
		}
		else {
			int numberOfLeadingSpaces = desiredLengthWithLeadingSpaces - string.length();
			StringBuilder formattedString = new StringBuilder();
			for (int i=0; i<numberOfLeadingSpaces; i++) formattedString.append(" ");
			formattedString.append(string);
			return formattedString.toString();
		}
	}
}
