package org.dclayer.simulation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.dclayer.listener.net.OnReceiveListener;
import org.dclayer.net.Data;
import org.dclayer.net.socket.DatagramSocket;

public class SimDatagramSocket implements DatagramSocket {
	
	private InetSocketAddress inetSocketAddress;
	private Router router;
	private OnReceiveListener onReceiveListener;
	
	public SimDatagramSocket(InetSocketAddress inetSocketAddress, Router router) {
		this.inetSocketAddress = inetSocketAddress;
		this.router = router;
		
		router.add(this);
	}

	@Override
	public void setOnReceiveListener(OnReceiveListener onReceiveListener) {
		this.onReceiveListener = onReceiveListener;
	}

	@Override
	public void send(SocketAddress socketAddress, Data data) throws IOException {
		router.send(socketAddress, data, this);
	}
	
	public synchronized void receive(Data data, InetSocketAddress fromInetSocketAddress) {
		onReceiveListener.onReceiveS2S(fromInetSocketAddress, data);
	}
	
	public InetSocketAddress getInetSocketAddress() {
		return inetSocketAddress;
	}

}
