/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.Matrix.Coords;
import geogebra.main.Application;

/**
 * @author Mathieu Blossier
 */
public class RegionParameters {
	
	private double t1, t2;
	
	private boolean isDefined; //tells if parameters have been fed with "real" numbers
	
	private Coords normal; //normal on the region at this place
	
	/** says if the point is on the path defined by the frontier of the region */
	private boolean isOnPath = false;
	
	public RegionParameters() {
		this(Double.NaN,Double.NaN);
	}
	
	public RegionParameters(double t1, double t2) {
		
		setT1(t1);
		setT2(t2);
		
		normal = new Coords(0,0,1,0); //z-direction by default

	}
	
	final public void set(RegionParameters rp) {
		setT1(rp.t1);
		setT2(rp.t2);
	}
	
	/*
	void appendXML(StringBuilder sb) {
		// pathParameter
		sb.append("\t<pathParameter val=\"");
			sb.append(t);
		if (branch > 0) {
			sb.append("\" branch=\"");
			sb.append(branch);
		}		
		if (pathType > -1) {
			sb.append("\" type=\"");
			sb.append(pathType);
		}
		sb.append("\"/>\n");
	}
	*/


	public final double getT1() {
		return t1;
	}

	public final void setT1(double t1) {
		//Application.printStacktrace(""+t1);
		if (isNaN(t1))
			return;
		this.t1 = t1;
		isDefined = true; //assume that setT1() is called before setT2()
	}
	public final double getT2() {
		return t2;
	}

	public final void setT2(double t2) {
		if (isNaN(t2))
			return;
		this.t2 = t2;
	}
	
	private final boolean isNaN(double t){
		if (Double.isNaN(t)){
			isDefined=false;
			return true;
		}
		return false;
	}
	
	
	public void setNormal(Coords normal){
		this.normal = normal;
	}

	public Coords getNormal(){
		return this.normal;
	}
	

	////////////////////////////////////
	// POINT ON PATH
	
	/** set if the point in on the path defined by the frontier of the region
	 * @param isOnPath
	 */
	public final void setIsOnPath(boolean isOnPath){
		this.isOnPath = isOnPath;
	}	
	
	/** says if the point in on the path defined by the frontier of the region
	 * @return true if the point in on the path defined by the frontier of the region
	 */
	public final boolean isOnPath(){
		return isOnPath;
	}
	
	
	
	
	
}
