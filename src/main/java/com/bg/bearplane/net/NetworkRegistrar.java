package com.bg.bearplane.net;

import com.esotericsoftware.kryonet.EndPoint;

public interface NetworkRegistrar {
	
	public void registerClasses(EndPoint endPoint);

}
