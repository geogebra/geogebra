/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoElement.java
 *
 * Created on 30. August 2001, 17:10
 */

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra3D.Test3D;
import geogebra3D.euclidian3D.Drawable3D;


/**
 * Class for describing GeoElement in 3D version.
 * 
   <p>
   
   <h3> How to create a new Element </h3>
   <p>
   
   We'll call here our new element "GeoNew3D"
   
   <p>
   
   <b> In GeoElement3D (this class), create an new constant to identify GeoNew3D </b>
   <p>
   <code> public static final int GEO_CLASS_NEW3D = 30??; </code>
   <p>
   
   <b> Create an new class GeoNew3D </b> 
   <ul>
   <li> It will eventually extend another class (at least GeoElement3D) :
   <p>
   <code>
   final public class GeoNew3D extends ??? {
   </code>
   </li>
   <li> Eclipse will add auto-generated methods ; modify it : 
   <p>
   <code>
    public GeoElement copy() {
        return null;
    }
    <br>
    public int getGeoClassType() {
       return GEO_CLASS_NEW3D;
    }
   <br>
    protected String getTypeString() {
        return "New3D";
    }
   <br>
    public boolean isDefined() {
       return true;
    }
   <br>
    public boolean isEqual(GeoElement Geo) {
       return false;
    }
   <br>
    public void set(GeoElement geo) {

    }
   <br>
    public void setUndefined() {

    }
   <br>
    protected boolean showInAlgebraView() {
        return true;
    }
   <br>
    protected boolean showInEuclidianView() {
       return true;
    }
   <br>
    public String toValueString() {
        return "todo";
    }
    <br>
    protected String getClassName() {
        return "GeoNew3D";
    }
  </code>
  </li>
  <li> Create a constructor <p>
  <code>
    public GeoNew3D(Construction c, ?? args) { <br> &nbsp;&nbsp;
        super(c); // eventually + args <br> &nbsp;&nbsp;
        + stuff <br>
    }
   </code>
   </li>     
   </ul>
   
   	<h3> See </h3> 
	<ul>
	<li> {@link Drawable3D} to create a drawable linked to this new element.
	</li>
	<li> {@link Kernel3D} to add a method to create this new element 
	</li> 
	<li> {@link Test3D#Test3D(Kernel3D, geogebra.euclidian.EuclidianView, geogebra3D.euclidian3D.EuclidianView3D, geogebra3D.Application3D)} to test it
	</li> 
	</ul>
 
 * 
 * 
 *
 * @author  ggb3D
 * 
 */
public abstract class GeoElement3D extends GeoElement implements
		GeoElement3DInterface {

	/** matrix used as orientation by the {@link Drawable3D} */
	private CoordMatrix4x4 m_drawingMatrix = null;
	

	
	/** for some 3D element (like conics, polygons, etc), a 2D GeoElement is linked to (for calculation) */
	private GeoElement geo2D = null;

	
	/** link with drawable3D */
	private Drawable3D drawable3D = null;
		

	
	/********************************************************/

	/** Creates new GeoElement for given construction 
	 * @param c construction*/
	public GeoElement3D(Construction c) {
		super(c);		
	}
	
	/**
	 * it's a 3D GeoElement.
	 * @return true
	 */
	@Override
	public boolean isGeoElement3D(){
		return true;
	}
	
	/** returns a 4x4 matrix for drawing the {@link Drawable3D} 
	 * @return the drawing matrix*/
	public CoordMatrix4x4 getDrawingMatrix(){
		return m_drawingMatrix;
	}
	
	@Override
	abstract public Coords getLabelPosition();
	
	/** sets the 4x4 matrix for drawing the {@link Drawable3D} and the label
	 * @param a_drawingMatrix the drawing matrix*/
	public void setDrawingMatrix(CoordMatrix4x4 a_drawingMatrix){
		this.m_drawingMatrix = a_drawingMatrix;
	}		
	
	// link to 2D GeoElement
    /**
     * return if linked to a 2D GeoElement
     * @return has a 2D GeoElement
     */
    public boolean hasGeoElement2D() {
    	return (geo2D!=null);
    }
    
    /**
     * return the 2D GeoElement linked to
     * @return 2D GeoElement
     */
    public GeoElement getGeoElement2D(){ 
    	return geo2D; 
    }    
    
    /**
     * set the 2D GeoElement linked to
     * @param geo a 2D GeoElement
     */
    public void setGeoElement2D(GeoElement geo){ 
    	this.geo2D = geo;
    }
		
    /** set the alpha value to alpha for openGL
     * @param alpha alpha value
     */
	@Override
	public void setAlphaValue(float alpha) {
		if ( alpha < 0.0f || alpha > 1.0f)
			return;
		alphaValue = alpha;
	}
	


	////////////////////////////
	// for toString()
	
	private StringBuilder sbToString;
	protected StringBuilder getSbToString() {
		if (sbToString == null)
			sbToString = new StringBuilder(50);
		return sbToString;
	}
	

	private StringBuilder sbBuildValueString = new StringBuilder(50);
	protected StringBuilder getSbBuildValueString() {
		if (sbBuildValueString == null)
			sbBuildValueString = new StringBuilder(50);
		return sbBuildValueString;
	}
	
	/////////////////////////////////////////
	// ExpressionValue implementation
	
	@Override
	public boolean isVector3DValue() {
		return false;
	}	

}