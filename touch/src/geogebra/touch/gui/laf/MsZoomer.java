package geogebra.touch.gui.laf;

import geogebra.common.euclidian.EuclidianController;

public class MsZoomer {
	
	private EuclidianController tc;
	public MsZoomer(EuclidianController tc){
		this.tc = tc;
	}
	
	public void zoom(double d){
		tc.zoomInOut(d, 1);
	}
}
