package main;

import client.Client;

public class Main {

	public static void main(String[] args) {
		Client timeTracker = new Client(25523);
		timeTracker.connect();
	}

}
