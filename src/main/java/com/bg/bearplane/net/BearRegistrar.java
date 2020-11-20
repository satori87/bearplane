package com.bg.bearplane.net;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.bg.bearplane.net.packets.Authenticate;
import com.bg.bearplane.net.packets.DisconnectError;
import com.bg.bearplane.net.packets.Logon;
import com.bg.bearplane.net.packets.PingPacket;
import com.bg.bearplane.net.packets.SimpleClientPacket;
import com.bg.bearplane.net.packets.SimpleServerPacket;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;


public class BearRegistrar {
	public static void registerClasses(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(String[].class);
		kryo.register(byte[].class);
		kryo.register(Integer[].class);
		kryo.register(Integer.class);
		kryo.register(int[].class);
		kryo.register(PingPacket.class);
		kryo.register(Authenticate.class);
		kryo.register(DisconnectError.class);
		kryo.register(Logon.class);
		kryo.register(Object.class);
		kryo.register(HashMap.class);
		kryo.register(Color.class);
		kryo.register(SimpleClientPacket.class);
		kryo.register(SimpleServerPacket.class);
		kryo.register(ArrayList.class);
	}
}
