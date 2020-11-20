package com.bg.bearplane.net;

import java.util.concurrent.ConcurrentLinkedQueue;
import com.bg.bearplane.engine.Bearplane;
import com.bg.bearplane.engine.Util;
import com.bg.bearplane.engine.Log;
import com.bg.bearplane.net.packets.Authenticate;
import com.bg.bearplane.net.packets.SimpleClientPacket;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class TCPClient {

	BearNet bearNet;
	public Client client;
	String host;
	Connection conn;
	public boolean connected = false;
	public ConcurrentLinkedQueue<QueuedClientPacket> packetQueue = new ConcurrentLinkedQueue<QueuedClientPacket>();

	public TCPClient() {
		try {
			bearNet = (BearNet) this;
			// Log.set(Log.LEVEL_DEBUG);
			client = new Client(32768, 32768);
			new Thread(client).start();
			BearRegistrar.registerClasses(client);
			client.addListener(new Listener() {

				public void connected(Connection connection) {
					conn = connection;
					connected = true;
					bearNet.clientConnected();
				}

				public void received(Connection connection, Object object) {
					addPacketToQueue(object);
				}

				public void disconnected(Connection connection) {
					disconnect();
				}

			});
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	public void close() {
		client.close();
	}
	
	protected void authenticate(String user, String pass, boolean newAcct) {
		try {
			Authenticate na = new Authenticate();
			na.newAcct = newAcct;
			na.user = user;
			na.pass = Util.encryptPassword(pass);
			na.version = Bearplane.game.getClientVersion();
			sendTCP(na);
		} catch (Exception e) {
			Log.error(e);
			System.exit(0);
		}
	}

	protected void connect(String hostname, int tcpPort, int udpPort) {
		try {
			client.connect(5000, hostname, tcpPort, udpPort);
		} catch (Exception e) {
			disconnect();
		}
	}

	public void sendSimple(int a) {
		SimpleClientPacket p = new SimpleClientPacket();
		p.a = (byte) a;
		sendTCP(p);
	}

	public void sendTCP(Object o) {
		client.sendTCP(o);
	}

	public void disconnect() {
		try {
			connected = false;
			client.close();
			bearNet.clientDisconnected();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processPacketQueue() {
		while (!packetQueue.isEmpty()) {
			QueuedClientPacket p = packetQueue.poll();
			bearNet.processPacket(p.o);
		}
	}

	public void addPacketToQueue(Object object) {
		QueuedClientPacket p = new QueuedClientPacket(object);
		packetQueue.add(p);
	}

}
