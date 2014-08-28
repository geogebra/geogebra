package geogebra.common.main.settings;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;



/**
 * Settings for 3D view
 * @author mathieu
 *
 */
public class EuclidianSettings3D extends EuclidianSettings{
	
	private double zscale;	

	private double zZero;


	private double a, b;

	public EuclidianSettings3D(EuclidianSettings euclidianSettings1) {
		super(euclidianSettings1);
	}
	
	public void setZscale(double scale) {
		this.zscale = scale;
		
	}
	
	public double getZscale(){
		return zscale;
	}
	
	public void setRotXYinDegrees(double a2, double b2) {
		this.a = a2;
		this.b = b2;
		
	}
	
	

	public void updateOrigin(double xZero2, double yZero2, double zZero2) {
		this.xZero = xZero2;
		this.yZero = yZero2;
		this.zZero = zZero2;
	}
	
	public void updateOrigin(EuclidianView3D view) {
		view.setXZero(getXZero());
		view.setYZero(getYZero());
		view.setZZero(getZZero());
	}
	

	public double getZZero(){
		return zZero;
	}
	
	public void updateRotXY(EuclidianView3D view){
		view.setRotXYinDegrees(a, b);
	}
	
	
	private boolean useClippingCube;
	
	public void setUseClippingCube(boolean flag) {

		useClippingCube = flag;
	}
	
	public boolean useClippingCube() {
		return useClippingCube;
	}
	

	private boolean showClippingCube = true;
	
	public void setShowClippingCube(boolean flag) {
		showClippingCube = flag;
	}
	
	public boolean showClippingCube() {
		return showClippingCube;
	}
	
	private int clippingReduction;
	
	public void setClippingReduction(int value) {
		clippingReduction = value;
	}
	
	public int getClippingReduction(){
		return clippingReduction;
	}

}
