package org.dclayer.simulation;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;

import org.dclayer.meta.Log;
import org.dclayer.net.Data;
import org.dclayer.threadswitch.ThreadEnvironment;
import org.dclayer.threadswitch.ThreadExecutor;
import org.dclayer.threadswitch.ThreadSwitch;

public class Router implements ThreadExecutor<Router.PacketHolder> {
	
	public static class PacketHolder {
		Data data;
		SimDatagramSocket dstSocket;
		SimDatagramSocket srcSocket;
	}
	
	private HashMap<InetSocketAddress, SimDatagramSocket> endpoints = new HashMap<>();
	
	private ThreadSwitch<PacketHolder> threadSwitch = new ThreadSwitch<>(PacketHolder.class);
	
	private Log.Output output;
	
	public Router(Log.Output output) {
		this.output = output;
	}
	
	public synchronized void add(SimDatagramSocket simDatagramSocket) {
		endpoints.put(simDatagramSocket.getInetSocketAddress(), simDatagramSocket);
	}
	
	public void send(SocketAddress socketAddress, Data sendData, SimDatagramSocket srcSimDatagramSocket) {
		
		SimDatagramSocket dstSimDatagramSocket;
		
		synchronized(this) {
			dstSimDatagramSocket = endpoints.get(socketAddress);
		}
		
		if(dstSimDatagramSocket != null) {
			
			if(output != null) output.println(String.format("Router: sending to %s (source address %s)", dstSimDatagramSocket.getInetSocketAddress(), srcSimDatagramSocket.getInetSocketAddress()));
			
			Data data = sendData.copy();
			
			ThreadEnvironment<PacketHolder> env = threadSwitch.get();
			
			env.getObject().data = data;
			env.getObject().dstSocket = dstSimDatagramSocket;
			env.getObject().srcSocket = srcSimDatagramSocket;
			
			env.exec(this);
			
		} else {
			if(output != null) output.println(String.format("Router: could not find %s (source address %s)", socketAddress, srcSimDatagramSocket.getInetSocketAddress()));
		}
		
	}
	
	@Override
	public void exec(ThreadEnvironment<PacketHolder> threadEnvironment, PacketHolder packetHolder) {
		packetHolder.dstSocket.receive(packetHolder.data, packetHolder.srcSocket.getInetSocketAddress());
	}
	
}
