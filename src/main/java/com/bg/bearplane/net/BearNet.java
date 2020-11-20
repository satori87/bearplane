package com.bg.bearplane.net;

public interface BearNet {
	
	public void processPacket(Object object);
	public void clientConnected();
	public void clientDisconnected();
	
}
