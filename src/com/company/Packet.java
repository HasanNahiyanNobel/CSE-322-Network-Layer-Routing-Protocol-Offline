package com.company;

import java.io.Serializable;

//Done!
public class Packet implements Serializable {

	private String message;
	private String specialMessage;  //ex: "SHOW_ROUTE" request
	private IPAddress destinationIP;
	private IPAddress sourceIP;
	int hopCount;

	public Packet(String message, String specialMessage, IPAddress sourceIP, IPAddress destinationIP) {
		this.message = message;
		this.specialMessage = specialMessage;
		this.sourceIP = sourceIP;
		this.destinationIP = destinationIP;
		hopCount = 0;
	}

	public IPAddress getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP(IPAddress sourceIP) {
		this.sourceIP = sourceIP;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSpecialMessage() {
		return specialMessage;
	}

	public void setSpecialMessage(String specialMessage) {
		this.specialMessage = specialMessage;
	}

	public IPAddress getDestinationIP() {
		return destinationIP;
	}

	public void setDestinationIP(IPAddress destinationIP) {
		this.destinationIP = destinationIP;
	}

}
