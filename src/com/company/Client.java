package com.company;

import java.util.Random;

import static com.company.NetworkLayerServer.endDeviceMap;
import static com.company.NetworkLayerServer.endDevices;
import static java.lang.System.exit;

//Work needed
public class Client {
	public static void main (String[] args) throws InterruptedException {
		System.out.println(generateRandomString(10));
		exit(0);

		NetworkUtility networkUtility = new NetworkUtility("127.0.0.1", 4444);
		System.out.println("Connected to server");

		EndDevice endDevice = endDeviceMap.get(networkUtility.getInetAddress());

		for (int i=0; i<100; i++) {
			String message;

			int indexOfRandomReceiver = new Random().nextInt(endDevices.size());
			EndDevice randomReceiver = endDevices.get(indexOfRandomReceiver);

		}
        
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
	        9.            Send the message and recipient IP address to server and a special request "SHOW_ROUTE"
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
