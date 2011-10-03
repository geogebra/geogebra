/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;




import geogebra.GeoGebra3D;
import geogebra.euclidian.EuclidianConstants;
import geogebra.io.MyXMLHandler;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Manager3DInterface;
import geogebra.kernel.arithmetic.ExpressionNodeEvaluator;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.MyError;
import geogebra3D.Application3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.io.MyXMLHandler3D;
import geogebra3D.kernel3D.arithmetic.ExpressionNodeEvaluator3D;
import geogebra3D.kernel3D.commands.AlgebraProcessor3D;

import java.util.LinkedHashMap;
import java.util.TreeSet;




/**
 * 
 * Class used for (3D) calculations
 *
 * <h3> How to add a method for creating a {@link GeoElement3D} </h3>
 *
   <ul>
   <li> simply call the element's constructor
   <p>
   <code>
   final public GeoNew3D New3D(String label, ???) { <br> &nbsp;&nbsp;
       GeoNew3D ret = new GeoNew3D(cons, ???); <br> &nbsp;&nbsp;
       // stuff <br> &nbsp;&nbsp;
       ret.setLabel(label); <br> &nbsp;&nbsp;           
       return ret; <br> 
   }
   </code>
   </li>
   <li> use an {@link AlgoElement3D}
   <p>
   <code>
   final public GeoNew3D New3D(String label, ???) { <br> &nbsp;&nbsp;
     AlgoNew3D algo = new AlgoNew3D(cons, label, ???); <br> &nbsp;&nbsp;
	 return algo.getGeo(); <br> 
   }
   </code>
   </li>
   </ul>

 *
 * @author  ggb3D
 * 
 */




public class Kernel3D
	extends Kernel {
	
	protected Application3D app3D;
	
	
	public Kernel3D(Application3D app) {
		
		super(app);
		this.app3D = app;
		
		
		
	}
	
	public GeoAxis3D getXAxis3D(){
		return ((Construction3D) cons).getXAxis3D();
	}
	public GeoAxis3D getYAxis3D(){
		return ((Construction3D) cons).getYAxis3D();
	}
	public GeoAxis3D getZAxis3D(){
		return ((Construction3D) cons).getZAxis3D();
	}
	public GeoPlane3DConstant getXOYPlane(){
		return ((Construction3D) cons).getXOYPlane();
	}
	
	
	

    
	/* *******************************************
	 *  Methods for EuclidianView/EuclidianView3D
	 * ********************************************/
    

	public String getModeText(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_VIEW_IN_FRONT_OF:
			return "ViewInFrontOf";
			
		case EuclidianConstants.MODE_PLANE_THREE_POINTS:
			return "PlaneThreePoint";
			
		case EuclidianConstants.MODE_PLANE_POINT_LINE:
			return "PlanePointLine";
			
		case EuclidianConstants.MODE_ORTHOGONAL_PLANE:
			return "OrthogonalPlane";
			
		case EuclidianConstants.MODE_PARALLEL_PLANE:
			return "ParallelPlane";
			
		case EuclidianConstants.MODE_PRISM:
			return "Prism";		
			
		case EuclidianConstants.MODE_RIGHT_PRISM:
			return "RightPrism";		
			
		case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
			return "SpherePointRadius";
						
		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			return "Sphere2";
			
		case EuclidianConstants.MODE_ROTATEVIEW:
			return "RotateView";
			
		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION:
			return "CirclePointRadiusDirection";
			
		case EuclidianConstants.MODE_CIRCLE_AXIS_POINT:
			return "CircleAxisPoint";
			
		default:
			return super.getModeText(mode);
		}
	}
    
    
    

    
	/* *******************************************
	 *  Methods for 3D manager
	 * ********************************************/
	
	protected Manager3DInterface newManager3D(Kernel kernel){
		return new Manager3D(kernel);
	}
	
	/**
	 * Returns whether the variable name "z" may be used. Note that the 3D kernel does not
	 * allow this as it uses "z" in plane equations like 3x + 2y + z = 5.
	 * @return whether z may be used as a variable name
	 */
	public boolean isZvarAllowed(){
		return false;
	}

    
	/* *******************************************
	 *  Methods for MyXMLHandler
	 * ********************************************/

	
	
	
	
	
	/**
	 * creates the 3D construction cons
	 */
	protected void newConstruction(){
		cons = new Construction3D(this);	
	}	
	
	

	public MyXMLHandler newMyXMLHandler(Kernel kernel, Construction cons){
		return new MyXMLHandler3D(kernel, cons);		
	}
	

	protected ExpressionNodeEvaluator newExpressionNodeEvaluator(){
		return new ExpressionNodeEvaluator3D();
	}
	
	
	
	
	
	public Application3D getApplication3D(){
		return app3D;
	}
	

	
	

	/**
	 * @param kernel 
	 * @return a new algebra processor (used for 3D)
	 */
	protected AlgebraProcessor newAlgebraProcessor(Kernel kernel){
		return new AlgebraProcessor3D(kernel);
	}
	
	
	
	
	
	
	/** return all points of the current construction */
	public TreeSet getPointSet(){
		TreeSet t3d = getConstruction().getGeoSetLabelOrder(GeoElement3D.GEO_CLASS_POINT3D);
		TreeSet t = super.getPointSet();
		
		t.addAll(t3d);
		//TODO add super.getPointSet()
		return t;
	}
	
	
	
	
	
	
	
	
	
	
	
	   public int getClassType(String type) throws MyError {    
	    	
	     	
	    	switch (type.charAt(0)) {
	   		case 'p': // point, polygon
				if (type.equals("point3d")){
					return GeoElement.GEO_CLASS_POINT3D;
				}
				else if (type.equals("polygon3d"))
					return GeoElement.GEO_CLASS_POLYGON3D;
				else if (type.equals("polyhedron"))
					return GeoElement.GEO_CLASS_POLYHEDRON;
				
			case 's': // segment 
				if (type.equals("segment3d"))
					return GeoElement.GEO_CLASS_SEGMENT3D;	 
				
	    	}
	    	

	    	
	    	return super.getClassType(type);

	    }
	
	
	
	/**
     * Creates a new GeoElement object for the given type string.
     * @param type: String as produced by GeoElement.getXMLtypeString()
     */
    public GeoElement createGeoElement(Construction cons, String type) throws MyError {    
    	
     	
    	switch (type.charAt(0)) {
   		case 'p': // point, polygon, plane
			if (type.equals("point3d")){
				return new GeoPoint3D(cons);
			}
			else if (type.equals("polygon3d"))
				return new GeoPolygon3D(cons, null);
			else if (type.equals("plane3d")){
				return new GeoPlane3D(cons);
			}
		case 's': // segment 
			if (type.equals("segment3d"))
				return new GeoSegment3D(cons, null, null);	 
		case 'v': // vector 
			if (type.equals("vector3d"))
				return new GeoVector3D(cons);	 
			
    	}
    	

    	
    	return super.createGeoElement(cons,type);

    }

	
	/* *******************************************
	 *  Methods for MyXMLHandler
	 * ********************************************/
	public boolean handleCoords(GeoElement geo, LinkedHashMap<String, String> attrs) {
		
		/*
		Application.debug("attrs =\n"+attrs);		
		Application.debug("attrs(x) = "+attrs.get("x"));
		Application.debug("attrs(y) = "+attrs.get("y"));
		Application.debug("attrs(z) = "+attrs.get("z"));
		Application.debug("attrs(w) = "+attrs.get("w"));
		*/
		
		if (!(geo instanceof GeoCoords4D)) {
			return super.handleCoords(geo, attrs);
		}
		
		

		try {
			double x = Double.parseDouble((String) attrs.get("x"));
			double y = Double.parseDouble((String) attrs.get("y"));
			double z = Double.parseDouble((String) attrs.get("z"));
			double w = Double.parseDouble((String) attrs.get("w"));
			((GeoCoords4D) geo).setCoords(x, y, z, w);
			//Application.debug(geo.getLabel()+": x="+x+", y="+y+", z="+z+", w="+w);
			return true;
		} catch (Exception e) {
			//Application.debug("erreur : "+e);
			return false;
		}
	}	
	
	
	

	


	public GeoPlaneND getDefaultPlane(){
		return app3D.getEuclidianView3D().getxOyPlane();
	}

	

	/////////////////////////////////
	// OVERRIDES KERNEL
	/////////////////////////////////
	public GeoPointND IntersectLines(
			String label,
			GeoLineND g,
			GeoLineND h) {

		if (((GeoElement) g).isGeoElement3D() || ((GeoElement) h).isGeoElement3D())
			return (GeoPointND) getManager3D().Intersect(label, (GeoElement) g, (GeoElement) h);
		else
			return super.IntersectLines(label, g, h);
			
	}

	public GeoPointND[] IntersectConics(
			String[] labels,
			GeoConicND a,
			GeoConicND b) {

		if (((GeoElement) a).isGeoElement3D() || ((GeoElement) b).isGeoElement3D())
			return getManager3D().IntersectConics(labels, a, b);
		else
			return super.IntersectConics(labels, a, b);
	}
	
	

	public GeoLineND OrthogonalLine(
			String label,
			GeoPointND P,
			GeoLineND l, 
			GeoDirectionND direction) {
		return getManager3D().OrthogonalLine3D(label, P, l, direction);
	}

	
	public String getXMLFileFormat(){
		return GeoGebra3D.XML_FILE_FORMAT;
	}
	
	
}