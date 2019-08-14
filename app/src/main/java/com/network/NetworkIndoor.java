package com.network;


public class NetworkIndoor {

	public NetworkManager.ClientSocketThread clientSocketThread = null;
	public int networdState = NetworkState.STATE_INIT;
	public boolean isAline = false;

	@Override
	public String toString() {
		return "NetworkIndoor [clientSocketThread=" + clientSocketThread
				+ ", networdState=" + networdState + ", isAline=" + isAline
				+ "]";
	}

}
