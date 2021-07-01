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
		for (int i=0; i<3; i++) {
			stringBuilder.append(bytes[i].toString());
			stringBuilder.append(".");
		}
		stringBuilder.append("0");
		return stringBuilder.toString();
	}

	@Override
	public String toString () {
		return string;
	}
}
