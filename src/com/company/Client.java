package com.company;

import java.util.Random;

//Work needed
public class Client {
	public static void main (String[] args) throws InterruptedException {
		final int NUMBER_OF_PACKETS_TO_BE_SENT = 100;

		NetworkUtility networkUtility = new NetworkUtility("127.0.0.1", 4444);
		System.out.println("Connected to server");

		EndDevice endDevice = (EndDevice) networkUtility.read();

		for (int i=0; i<NUMBER_OF_PACKETS_TO_BE_SENT; i++) {
			String message = generateRandomString(10);
			String specialMessage;

			if (i==20) specialMessage = "SHOW_ROUTE";
			else specialMessage = null;

			Packet packet = new Packet(message, specialMessage, endDevice.getIpAddress(), null);
			System.out.println("Writing packet.");
			networkUtility.write(packet);
			Thread.sleep(100);
		}

		System.out.println("Delivered " + NUMBER_OF_PACKETS_TO_BE_SENT + " packets to server.");

		while (true) {}
        
        /*
        Tasks:
	        1. Receive EndDevice configuration from server
	        2. Receive active client list from server
	        3. for(int i=0;i<100;i++)
	        4. {
	        5.      Generate a random message
	        6.      Assign a random receiver from active client list
	        7.      if(i==20)
	        8.      {
	        9.              Send the message and recipient IP address to server
	                        and
	                        a special request "SHOW_ROUTE"
	        10.           Display routing path, hop count and routing table of each router [You need to receive
	                            all the required info from the server in response to "SHOW_ROUTE" request]
	        11.     }
	        12.     else
	        13.     {
	        14.           Simply send the message and recipient IP address to server.
	        15.     }
	        16.     If server can successfully send the message, client will get an acknowledgement along with hop count
	                    Otherwise, client will get a failure message [dropped packet]
	        17. }
	        18. Report average number of hops and drop rate
        */
	}

	/**
	 * Generates a random string of given length from characters a to z.
	 * @param length Length of the generated string.
	 * @return Generated random string.
	 */
	private static String generateRandomString (int length) {
		StringBuilder stringBuilder = new StringBuilder();
		int startCharIndex = 97;
		int endCharIndex = 122;

		while (stringBuilder.length() < length) {
			int randomCharIndex = new Random().nextInt(endCharIndex + 1 - startCharIndex) + startCharIndex;
			Character character = (char) randomCharIndex;
			stringBuilder.append(character);
		}

		return stringBuilder.toString();
	}
}
