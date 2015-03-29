package org.dclayer.simulation;

import org.dclayer.DCLService;
import org.dclayer.net.lla.LLA;

public class SimulatedService {
	
	private DCLService dclService;
	private LLA localLLA;
	
	public SimulatedService(DCLService dclService, LLA localLLA) {
		this.dclService = dclService;
		this.localLLA = localLLA;
	}
	
	public DCLService getDCLService() {
		return dclService;
	}
	
	public LLA getLocalLLA() {
		return localLLA;
	}

}
