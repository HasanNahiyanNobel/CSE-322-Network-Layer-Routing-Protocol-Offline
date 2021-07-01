package com.company;

import java.io.Serializable;

//Done!
public class IPAddress implements Serializable {
	private Short bytes[];
	private String string;

	public IPAddress (String string) {
		bytes = new Short[4];
		this.string = string;
		String[] temp = string.split("\\.");
		for (int i=0; i<4; i++) {
			bytes[i] = Short.parseShort(temp[i]);
		}
	}

	public Short[] getBytes () {
		return bytes;
	}

	public String getString () {
		return string;
	}

	public String getNetworkAddress () {
		StringBuilder stringBuilder = new StringBuilder();
		int firstOctet = Integer.parseInt(bytes[0].toString());

		if (firstOctet < 127) {
			// Class A
			stringBuilder.append(bytes[0]).append(".0.0.0");
		}
		else if (firstOctet < 192) {
			// Class B
			stringBuilder.append(bytes[0]).append(".").append(bytes[1]).append(".0.0");
		}
		else if (firstOctet < 224) {
			// Class C
			stringBuilder.append(bytes[0]).append(".").append(bytes[1]).append(".").append(bytes[2]).append(".0");
		}
		else {
			// Class D or E
			System.out.println("Some terrible error occurred in class IPAddress.");
		}

		return stringBuilder.toString();
	}

	@Override
	public String toString () {
		return string;
	}
}
