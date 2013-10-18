package geogebra.touch.gui.laf;

import geogebra.common.euclidian.EuclidianController;

public class MsZoomer {
	
	private final EuclidianController tc;
	public MsZoomer(EuclidianController tc){
		this.tc = tc;
	}
	
	public void pointersUp(){
		this.tc.setExternalHandling(false);
	}

	public void twoPointersDown(double x1,double y1, double x2, double y2){
		this.tc.setExternalHandling(true);
		this.tc.twoTouchStart(x1,y1,x2,y2);
	}

	public void twoPointersMove(double x1,double y1, double x2, double y2){
		this.tc.twoTouchMove(x1,y1,x2,y2);
	}
}
