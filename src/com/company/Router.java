package com.company;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

// Work needed
public class Router {
	private int routerId;
	private int numberOfInterfaces;
	private ArrayList<IPAddress> interfaceAddresses; // List of IP address of all interfaces of the router
	private ArrayList<RoutingTableEntry> routingTable; // Used to implement DVR
	private ArrayList<Integer> neighborRouterIDs; // Contains both "UP" and "DOWN" state routers
	private Boolean state; // True represents "UP" state and false is for "DOWN" state
	private Map<Integer, IPAddress> gatewayIDtoIP;
	private final double ROUTER_UP_PROBABILITY = 0.8;

	public Router() {
		interfaceAddresses = new ArrayList<>();
		routingTable = new ArrayList<>();
		neighborRouterIDs = new ArrayList<>();

		// 80% Probability that the router is up
		double p = new Random().nextDouble();
		state = p <= ROUTER_UP_PROBABILITY;

		numberOfInterfaces = 0;
	}

	public Router(int routerId, ArrayList<Integer> neighborRouters, ArrayList<IPAddress> interfaceAddresses, Map<Integer, IPAddress> gatewayIDtoIP) {
		this.routerId = routerId;
		this.interfaceAddresses = interfaceAddresses;
		this.neighborRouterIDs = neighborRouters;
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
		string.append("\n" + "Neighbors: \n");
		for (Integer neighborRouterID : neighborRouterIDs) {
			string.append(neighborRouterID).append("\t");
		}
		return string.toString();
	}

	/**
	 * Initialize the distance (hop count) for each router.
	 * For itself, distance=0; for any connected router with state=true, distance=1; otherwise distance=Constants.INFINITY;
	 */
	public void initiateRoutingTable() {

	}

	/**
	 * Delete all the routingTableEntry
	 */
	public void clearRoutingTable() {

	}

	/**
	 * Update the routing table for this router using the entries of Router neighbor
	 * @param neighbor
	 */
	public boolean updateRoutingTable(Router neighbor) {
		return false;
	}

	public boolean sfUpdateRoutingTable (Router neighbor) {
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

	public ArrayList<Integer> getNeighborRouterIDs() {
		return neighborRouterIDs;
	}

	public void setNeighborRouterIDs(ArrayList<Integer> neighborRouterIDs) { this.neighborRouterIDs = neighborRouterIDs; }

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	public Map<Integer, IPAddress> getGatewayIDtoIP() { return gatewayIDtoIP; }

	public void printRoutingTable() {
		System.out.println("Router " + routerId);
		System.out.println("DestID Distance NextHop");
		for (RoutingTableEntry routingTableEntry : routingTable) {
			System.out.println(routingTableEntry.getRouterId() + " " + routingTableEntry.getDistance() + " " + routingTableEntry.getGatewayRouterId());
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

}
