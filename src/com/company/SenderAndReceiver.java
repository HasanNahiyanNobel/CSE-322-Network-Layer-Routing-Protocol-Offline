package com.company;

public class SenderAndReceiver {
	IPAddress senderIP;
	IPAddress receiverIP;

	public SenderAndReceiver (IPAddress senderIP, IPAddress receiverIP) {
		this.senderIP = senderIP;
		this.receiverIP = receiverIP;
	}

	public IPAddress getSenderIP () {
		return senderIP;
	}

	public IPAddress getReceiverIP () {
		return receiverIP;
	}
}
