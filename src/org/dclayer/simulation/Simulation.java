package org.dclayer.simulation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.dclayer.DCLService;
import org.dclayer.meta.Log;
import org.dclayer.net.lla.InetSocketLLA;
import org.dclayer.net.lla.LLA;
import org.dclayer.net.lla.database.LLADatabase;
import org.dclayer.net.network.NetworkType;

public class Simulation {
	
	private Router router;
	int n = 0;
	
	public Simulation() {
		this(null);
	}
	
	public Simulation(Log.Output routerOutput) {
		this.router = new Router(routerOutput);
	}
	
	public SimulatedService add(NetworkType networkType, LLA... databaseLLAs) {
		return add(null, networkType, databaseLLAs);
	}
	
	public SimulatedService add(Log.Output output, NetworkType networkType, LLA... databaseLLAs) {
		
		n++;
		
		InetSocketAddress inetSocketAddress;
		try {
			inetSocketAddress = new InetSocketAddress(InetAddress.getByName(
					String.format("%d.%d.%d.%d", (n>>24)&0xFF, (n>>16)&0xFF, (n>>8)&0xFF, n&0xFF)),
					n);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
		
		LLADatabase llaDatabase = new LLADatabase();
		for(LLA lla : databaseLLAs) {
			llaDatabase.store(lla);
		}
		
		SimDatagramSocket s2sDatagramSocket = new SimDatagramSocket(inetSocketAddress, router);
		SimStreamSocket a2sStreamSocket = new SimStreamSocket();
		
		DCLService dclService;
		try {
			dclService = new DCLService(s2sDatagramSocket, a2sStreamSocket, llaDatabase);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		LLA localLLA = new InetSocketLLA(inetSocketAddress);
		dclService.setLocalLLA(localLLA); // TODO remove
		
		dclService.join(networkType);
		
		if(output != null) {
			
			Log.setOutput(dclService, output);
			
		} else {
			
			Log.setOutput(dclService, new Log.Output() {
				@Override
				public void println(String string) {
					
				}
			});
			
		}
		
		return new SimulatedService(dclService, localLLA);
		
	}
	
}
