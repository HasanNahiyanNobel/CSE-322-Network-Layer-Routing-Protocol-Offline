package com.company;

import java.util.ArrayList;

public class RoutingPath {
	ArrayList<Integer> routerIDs;

	RoutingPath () {
		routerIDs = null;
	}

	RoutingPath (int routerID) {
		routerIDs.add(routerID);
	}

	RoutingPath (RoutingPath routingPath) {
		this.routerIDs = routingPath.getRouterIDs();
	}

	public ArrayList<Integer> getRouterIDs () {
		return routerIDs;
	}

	public int getHopCount () {
		return routerIDs.size()-1;
	}

	public void addRouter (int routerID) {
		routerIDs.add(routerID);
	}

	@Override
	public String toString () {
		return "RoutingPath{" +
				"routerIDs=" + routerIDs +
				'}';
	}
}
