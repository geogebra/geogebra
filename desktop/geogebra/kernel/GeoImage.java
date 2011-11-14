/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Image with given filename and corners
 */
public class GeoImage extends GeoElement 
implements Locateable, AbsoluteScreenLocateable,
		   PointRotateable, Mirrorable, Translateable, Dilateable, MatrixTransformable,Transformable {
	 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private String imageFileName = ""; // image file
	private GeoPoint [] corners; // corners of the image
	//private BufferedImage image;	
	private int pixelWidth, pixelHeight;
	private boolean inBackground, defined;
	private boolean hasAbsoluteLocation;
	private boolean interpolate=true;
	
	// for absolute screen location
	private int screenX, screenY;
	private boolean hasAbsoluteScreenLocation = false;	
	
	// corner points for transformations
	private GeoPoint [] tempPoints;
	
	private static Vector<GeoImage> instances = new Vector<GeoImage>();
	
	/**
	 * Creates new image
	 * @param c construction
	 */
	public GeoImage(Construction c) {
		super(c);
		
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		//http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		setAlphaValue(1f);
		//setAlgebraVisible(false); // don't show in algebra view		
		setAuxiliaryObject(true);
		
		// three corners of the image: first, second and fourth
		corners = new GeoPoint[3]; 			
				
		instances.add(this);	
		defined = true;
	}  

	/**
	 * Creates new labeled image
	 * @param c construction
	 * @param label
	 * @param fileName path to the image
	 */
	public GeoImage(Construction c, String label, String fileName) {
		this(c);
		setImageFileName(fileName);
		setLabel(label);
	}  
	
	/**
	 * Copy constructor
	 * @param img source image
	 */
	public GeoImage(GeoImage img) {
		this(img.cons);
		set(img);				
	}

	public GeoElement copy() {
		return new GeoImage(this);
	}
	
    public int getRelatedModeID() {
    	switch (this.image.getType()){
    	case 5:
    		return EuclidianConstants.MODE_IMAGE;
    	case 6:
    		return EuclidianConstants.MODE_PEN;
    	default:
    		return -1;	
    	}
    }
	
	private void initTempPoints() {
		if (tempPoints == null) {
			//	temp corner points for transformations and absolute location
			tempPoints = new GeoPoint[3];
	    	for (int i = 0; i < tempPoints.length; i++) {
	    		tempPoints[i] = new GeoPoint(cons);    		
	    	}	    	
		}
		
		if (corners[0] == null)
			corners[0] = tempPoints[0];
	}

	public void set(GeoElement geo) {
		GeoImage img = (GeoImage) geo;
		setImageFileName(img.imageFileName);
		
		// macro output: don't set corners
		if (cons != geo.cons && isAlgoMacroOutput()) 
			return;
		
		// location settings
		hasAbsoluteScreenLocation = img.hasAbsoluteScreenLocation;
			
		if (hasAbsoluteScreenLocation) {
			screenX = img.screenX;
			screenY = img.screenY;			
		}
		else {
			hasAbsoluteLocation = true;
			for (int i=0; i < corners.length; i++) {
				if (img.corners[i] == null) {
					corners[i] = null;
				} else {
					initTempPoints();
					
					tempPoints[i].setCoords(img.corners[i]);
					corners[i] = tempPoints[i];
				}
			}
		}
		
		//interpolation settings
		interpolate=img.interpolate;
		defined = img.defined;
	}
	
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);
		
		if (geo.isGeoImage()) {
			inBackground = ((GeoImage)geo).inBackground;
		}
	}
	
	/**
	 * Reloads images from internal image cache	 
	 */
	public static void updateInstances() {
		for (int i=instances.size()-1; i >= 0 ; i--) {
			GeoImage geo = (GeoImage) instances.get(i);
			geo.setImageFileName(geo.imageFileName);
			geo.updateCascade();
		}		
	}
	
	public boolean showToolTipText() {
		return !inBackground && super.showToolTipText();
	}
	/**
	 * True for background images
	 * @return true for background images
	 */
	final public boolean isInBackground() {
		return inBackground;
	}
	/**
	 * Switch to background image (or vice versa)
	 * @param flag true to make it background image
	 */
	public void setInBackground(boolean flag) {
		inBackground = flag;		
	}
	
	/**
	 * Tries to load the image using the given fileName.
	 * @param fileName
	 */
	public void setImageFileName(String fileName) {	
		if (fileName.equals(this.imageFileName))
			return;
				
		this.imageFileName = fileName;
														
		image = app.getExternalImage(fileName);	
		if (image != null) {
			pixelWidth = image.getWidth();
			pixelHeight = image.getHeight();
		} else {
			pixelWidth = 0;
			pixelHeight = 0;
		}
		// Michael Borcherds 2007-12-10 MD5 code moved to Application.java
	}
	
	//final public BufferedImage getFillImage() {
	//	return image;
	//}		
	
	public void setStartPoint(GeoPointND p) throws CircularDefinitionException {    
		setCorner((GeoPoint) p, 0);
	}
	
	public void removeStartPoint(GeoPointND p) {    
		for (int i=0; i < corners.length; i++) {
			if (corners[i] == p)
				setCorner(null, i);
		}
	}
	
	public void setStartPoint(GeoPointND p, int number) throws CircularDefinitionException {
		setCorner((GeoPoint) p, number);
	}
	
	/**
	 * Sets the startpoint without performing any checks.
	 * This is needed for macros.	 
	 */
	public void initStartPoint(GeoPointND p, int number) {
		corners[number] = (GeoPoint) p;
	}
	
	/**
	 * Sets a corner of this image. 
	 * @param p corner point
	 * @param number 0, 1 or 2 (first, second and fourth corner)
	 */
	public void setCorner(GeoPoint p, int number)  {
		// macro output uses initStartPoint() only
		if (isAlgoMacroOutput()) return; 
		
		if (corners[0] == null && number > 0) return;
		
		// check for circular definition
		if (isParentOf(p))
			//throw new CircularDefinitionException();
			return;		

		// set new location	
		if (p == null) {
			//	remove old dependencies
			if (corners[number] != null) 
				corners[number].getLocateableList().unregisterLocateable(this);	
						
			// copy old first corner as absolute position
			if (number == 0 && corners[0] != null) {
				GeoPoint temp = new GeoPoint(cons);
				temp.setCoords(corners[0]);
				corners[0] = temp;
			} else
				corners[number] = null;
		} else {
			// check if this point is already available
			for (int i=0; i < corners.length; i++) {
				if (p == corners[i])
					return;
			}
			
			// remove old dependencies
			if (corners[number] != null) 
				corners[number].getLocateableList().unregisterLocateable(this);		
			
			corners[number] = p;
			//	add new dependencies
			corners[number].getLocateableList().registerLocateable(this);								
		}					
		
		// absolute screen position should be deactivated
		setAbsoluteScreenLocActive(false);	
		updateHasAbsoluteLocation();				
	}
	
	/**
	 * Sets hasAbsoluteLocation flag to true iff all corners
	 * are absolute start points (i.e. independent and unlabeled). 
	 */
	private void updateHasAbsoluteLocation() {
		hasAbsoluteLocation = true;
			
		for (int i=0; i < corners.length; i++) {
			if (!(corners[i] == null || corners[i].isAbsoluteStartPoint())) {
				hasAbsoluteLocation = false;
				return;
			}						
		}
	}		
	
	public void doRemove() {
		instances.remove(this);		
		
		// remove background image
		if (inBackground) {
			inBackground = false;
			notifyUpdate();
		}
		
		super.doRemove();		
		for (int i=0; i < corners.length; i++) {
			// tell corner	
			if (corners[i] != null) corners[i].getLocateableList().unregisterLocateable(this);
		}		
	}
	
	public GeoPoint getStartPoint() {
		return corners[0];
	}
	
	public GeoPoint [] getStartPoints() {
		return corners;
	}
	/**
	 * Returns n-th corner point
	 * @param number 1 for boottom left, others clockwise
	 * @return corner point
	 */
	final public GeoPoint getCorner(int number) {
		return corners[number];
	}
	
	final public boolean hasAbsoluteLocation() {
		return hasAbsoluteLocation;
	}	

	/**
	 * 
	 * @return true if the image wants to be interpolated
	 */
	final public boolean isInterpolate(){
		return interpolate;
	}
	
	/**
	 * sets if the image want to be interpolated
	 * @param flag
	 */
	final public void setInterpolate(boolean flag){
		interpolate=flag;
	}
	
	public void setWaitForStartPoint() {
		// this can be ignored for an image 
		// as the position of its startpoint
		// is irrelevant for the rest of the construction
	}
	
	
	final public boolean isDefined() {
		if(!defined) return false;
		for (int i=0; i < corners.length; i++) {
			if (corners[i] != null  && !corners[i].isDefined())
					return false;
		}
		return true;
	}

	/**
	 * makes image invisible
	 * needed for Sequence's cached images
 	*/
	public void setUndefined() {
		defined = false;		
	}

	public String toValueString() {
		return toString();
	}
	
	public String toString() {				
		return label == null ? app.getPlain("Image") : label;
	}	

	public boolean showInAlgebraView() {
		return true;
	}

	protected boolean showInEuclidianView() {		
		return image != null && isDefined();
	}

	public String getClassName() {
		return "GeoImage";
	}
	
	protected String getTypeString() {
		return "Image";
	}
	
    public int getGeoClassType() {
    	return GEO_CLASS_IMAGE;
    }
	
	/**
	 * Returns whether this image can be 
	 * moved in Euclidian View.
	 */
	final public boolean isMoveable() {		
		return (hasAbsoluteScreenLocation || hasAbsoluteLocation) && isChangeable();
	}
	
	/**
	 * Returns whether this image can be 
	 * rotated in Euclidian View.
	 */
	final public boolean isRotateMoveable() {
		return !hasAbsoluteScreenLocation && hasAbsoluteLocation && isChangeable();
	}
	
	/**
	 * Returns whether this image can be fixed.
	 *
	public boolean isFixable() {
		return (hasAbsoluteScreenLocation || hasAbsoluteLocation) && isIndependent();
	}*/
	
	public boolean isFillable() {
		return true;
	}

	public boolean isNumberValue() {
		return false;
	}

	public boolean isVectorValue() {
		return false;
	}
	
	public boolean isGeoImage() {
		return true;
	}

	public boolean isPolynomialInstance() {
		return false;
	}
	
	public boolean isTextValue() {
		return false;
	}

	/**
	* returns all class-specific xml tags for getXML
	*/
	protected void getXMLtags(StringBuilder sb) {  		   	
			   	
	   	// name of image file
		sb.append("\t<file name=\"");
// Michael Borcherds 2007-12-10 this line restored (not needed now MD5 code put in the correct place)
		sb.append(imageFileName);
		sb.append("\"/>\n");
		
	 	// name of image file
		sb.append("\t<inBackground val=\"");
		sb.append(inBackground);
		sb.append("\"/>\n");
		
		
		// image has to be interpolated
		if (!isInterpolate())
    		sb.append("\t<interpolate val=\"false\"/>\n");

		// locateion of image
		if (hasAbsoluteScreenLocation) {
			sb.append(getXMLabsScreenLoc());			
		} 
		else {
			// store location of corners		
			for (int i=0; i < corners.length; i++) {
				if (corners[i] != null) {
					sb.append(getCornerPointXML(i));
				}
			}
		}
		
		getAuxiliaryXML(sb);
		
//	   	sb.append(getXMLvisualTags());
//	   	sb.append(getBreakpointXML());
		super.getXMLtags(sb);
 
   	}
   	
   	private String getXMLabsScreenLoc() {
   		StringBuilder sb = new StringBuilder();
   		
   		sb.append("\t<absoluteScreenLocation x=\"");
   		sb.append(screenX);
   		sb.append("\" y=\"");
   		sb.append(screenY);
   		sb.append("\"/>");
   		return sb.toString();
   	}
   	
    private String getCornerPointXML(int number) {
    	StringBuilder sb = new StringBuilder();    	
		sb.append("\t<startPoint number=\"");
		sb.append(number);
		sb.append("\"");
		
    	if (corners[number].isAbsoluteStartPoint()) {		
			sb.append(" x=\"" + corners[number].x + "\"");
			sb.append(" y=\"" + corners[number].y + "\"");
			sb.append(" z=\"" + corners[number].z + "\"");			
    	} else {
			sb.append(" exp=\"");
			boolean oldValue = kernel.isPrintLocalizedCommandNames();
			kernel.setPrintLocalizedCommandNames(false);
			sb.append(Util.encodeXML(corners[number].getLabel()));
			kernel.setPrintLocalizedCommandNames(oldValue);
			sb.append("\"");			    	
    	}
		sb.append("/>\n");
		return sb.toString();
    }
    

	public void setAbsoluteScreenLoc(int x, int y) {
		screenX = x;
		screenY = y;		
	}

	public int getAbsoluteScreenLocX() {	
		return screenX;
	}

	public int getAbsoluteScreenLocY() {		
		return screenY;
	}
	
	public void setRealWorldLoc(double x, double y) {
		GeoPoint loc = getStartPoint();
		if (loc == null) {
			loc = new GeoPoint(cons);	
			setCorner(loc, 0);
		}				
		loc.setCoords(x, y, 1.0);		
	}
	
	public double getRealWorldLocX() {
		if (corners[0] == null)
			return 0;
		else
			return corners[0].inhomX;
	}
	
	public double getRealWorldLocY() {
		if (corners[0] == null)
			return 0;
		else
			return corners[0].inhomY;
	}
	
	public void setAbsoluteScreenLocActive(boolean flag) {
		hasAbsoluteScreenLocation = flag;	
		if (flag) {
			// remove startpoints
			for (int i=0; i < 3; i++) {
				if (corners[i] != null) {
					corners[i].getLocateableList().unregisterLocateable(this);						
				}
			}	
			corners[1] = null;
			corners[2] = null;
		}
	}

	public boolean isAbsoluteScreenLocActive() {	
		return hasAbsoluteScreenLocation;
	}
	
	public boolean isAbsoluteScreenLocateable() {
		return isIndependent();
	}
	
	
	/* **************************************
	 * Transformations 
	 * **************************************/
	
	/**
	 * Calculates the n-th corner point of this image in real world
	 * coordinates. Note: if this image
	 * has an absolute screen location, result is set to undefined.
	 * 
	 * @param result here the result is stored.
	 * @param n number of the corner point (1, 2, 3 or 4) 
	 */
	public void calculateCornerPoint(GeoPoint result, int n) {		
		if (hasAbsoluteScreenLocation) {
			result.setUndefined();
			return;
		}
		
		if (corners[0] == null)
			initTempPoints();
		
		switch (n) {
			case 1: // get A
				result.setCoords(corners[0]);
				break;
			
			case 2: // get B
				getInternalCornerPointCoords(tempCoords, 1);
				result.setCoords(tempCoords[0], tempCoords[1], 1.0);
				break;
				
			case 3: // get C
				double [] b = new double[2];
				double [] d = new double[2];
				getInternalCornerPointCoords(b, 1);
				getInternalCornerPointCoords(d, 2);
				result.setCoords(d[0] + b[0] - corners[0].inhomX,
								 d[1] + b[1] - corners[0].inhomY,
								 1.0);
				break;
				
			case 4: // get D
				getInternalCornerPointCoords(tempCoords, 2);
				result.setCoords(tempCoords[0], tempCoords[1], 1.0);
				break;
				
			default:
				result.setUndefined();
		}	
	}
		
	// coords is the 2d result array for (x, y); n is 0, 1, or 2
	private double [] tempCoords = new double[2];
	private void getInternalCornerPointCoords(double [] coords, int n) {		
		GeoPoint A = corners[0];
		GeoPoint B = corners[1];
		GeoPoint D = corners[2];
		
		double xscale = kernel.getXscale();
		double yscale = kernel.getYscale();
		double width = pixelWidth;
		double height = pixelHeight;
		
		// different scales: change height
		if (xscale != yscale) {
			height = height * yscale / xscale;
		}
		
		switch (n) {
			case 0: // get A
				coords[0] = A.inhomX;
				coords[1] = A.inhomY;
				break;
			
			case 1: // get B
				if (B != null) {
					coords[0] = B.inhomX;
					coords[1] = B.inhomY;				
				} else { // B is not defined
					if (D == null) { 
						// B and D are not defined
						coords[0] = A.inhomX + width / xscale;
						coords[1] = A.inhomY;							
					} else {
						// D is defined, B isn't
						double nx = D.inhomY - A.inhomY;
						double ny = A.inhomX - D.inhomX;
						double factor = width / height;
						coords[0] = A.inhomX + factor * nx;
						coords[1] = A.inhomY + factor * ny;										
					}					
				}
				break;							
				
			case 2: // D
				if (D != null) {
					coords[0] = D.inhomX;
					coords[1] = D.inhomY;
				} else { // D is not defined
					if (B == null) { 
						// B and D are not defined
						coords[0] = A.inhomX;
						coords[1] = A.inhomY + height / yscale;										 
					} else {
						// B is defined, D isn't
						double nx = A.inhomY - B.inhomY;
						double ny = B.inhomX - A.inhomX;
						double factor = height / width;
						coords[0] = A.inhomX + factor * nx;
						coords[1] = A.inhomY + factor * ny;										 
					}					
				}
				break;
				
			default:
				coords[0] = Double.NaN;
				coords[1] = Double.NaN;
		}					
	}
	
	private boolean initTransformPoints() {
    	if (hasAbsoluteScreenLocation || !hasAbsoluteLocation) 
    		return false;    	    
    	
    	initTempPoints();
    	calculateCornerPoint(tempPoints[0], 1);
    	calculateCornerPoint(tempPoints[1], 2);
    	calculateCornerPoint(tempPoints[2], 4);    	
    	return true;
    }	
	

    /**
     * rotate this image by angle phi around (0,0)
     */
    final public void rotate(NumberValue phiValue) {
    	if (!initTransformPoints()) return;
    	
    	// calculate the new corner points
    	for (int i=0; i < corners.length; i++) {
    		tempPoints[i].rotate(phiValue);    		
    		corners[i] = tempPoints[i];
    	}
    }
    
    /**
     * rotate this image by angle phi around Q
     */    
    final public void rotate(NumberValue phiValue, GeoPoint Q) {
    	if (!initTransformPoints()) return;
    	
    	// calculate the new corner points
    	for (int i=0; i < corners.length; i++) {
    		tempPoints[i].rotate(phiValue, Q);    	
    		corners[i] = tempPoints[i];    			
    	}      
    }
     
	public void mirror(GeoPoint Q) {
		if (!initTransformPoints()) return;
    	
    	// calculate the new corner points
    	for (int i=0; i < corners.length; i++) {
    		tempPoints[i].mirror(Q);    	
    		corners[i] = tempPoints[i];    			
    	}     
	}

	public void matrixTransform(double a,double b,double c, double d) {
		if (!initTransformPoints()) return;
    	
    	// calculate the new corner points
    	for (int i=0; i < corners.length; i++) {
    		GeoVec2D vec = tempPoints[i].getVector();
    		vec.matrixTransform(a, b, c, d);    
    		if (corners[i] == null) corners[i] = new GeoPoint(cons);
    		corners[i].setCoords(vec);    			
    	}     
	}
	
	public boolean isMatrixTransformable() { 
		return true;
	}

	public void mirror(GeoLine g) {
		if (!initTransformPoints()) return;
    	
    	// calculate the new corner points
    	for (int i=0; i < corners.length; i++) {
    		tempPoints[i].mirror(g);    	
    		corners[i] = tempPoints[i];    			
    	}  
	}

	public void translate(Coords v) {
		if (!initTransformPoints()) return;
    	
    	// calculate the new corner points
    	for (int i=0; i < corners.length; i++) {
    		tempPoints[i].translate(v);    	
    		corners[i] = tempPoints[i];    			
    	}  
	}	
	
	final public boolean isTranslateable() {
		return true;
	}

	public void dilate(NumberValue r, GeoPoint S) {
		if (!initTransformPoints()) return;
    	
    	// calculate the new corner points
    	for (int i=0; i < corners.length; i++) {
    		tempPoints[i].dilate(r, S);    	
    		corners[i] = tempPoints[i];    			
    	}  
	}

	// Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		// return false if it's a different type
		if (!geo.isGeoImage()) return false;

		// check sizes
		if (((GeoImage)geo).pixelWidth != this.pixelWidth) return false;
		if (((GeoImage)geo).pixelHeight != this.pixelHeight) return false;
		
		String md5A=this.imageFileName.substring(0, this.imageFileName.indexOf(File.separator));
		String md5B=((GeoImage)geo).imageFileName.substring(0, ((GeoImage)geo).imageFileName.indexOf(File.separator));
		// MD5 checksums equal, so images almost certainly identical
		if (md5A.equals(md5B)) return true;
		return false;
	}
	
	public boolean isAlwaysFixed() {
		return false;
	}	

	public boolean isVector3DValue() {		
		return false;
	}
	
	public boolean hasMoveableInputPoints(EuclidianViewInterface view) {
		
		if (hasAbsoluteLocation()) return false;
		
		for (int i = 0 ; i < corners.length ; i++) {
			if (corners[i] != null && !corners[i].isMoveable(view)) return false;
		}
		return true;
	}
	
	private ArrayList<GeoPoint> al = null;

	/**
	 * Returns all free parent points of this GeoElement.	 
	 */
	public ArrayList<GeoPoint> getFreeInputPoints(EuclidianViewInterface view) {		
			if (hasAbsoluteLocation()) return null;
			
			if (al == null) al = new ArrayList<GeoPoint>();
			else al.clear();
			
			for (int i = 0 ; i < corners.length ; i++) {
				if (corners[i] != null) al.add(corners[i]);
			}
			
			return al;
	}
	
	final public boolean isAuxiliaryObjectByDefault() {
		return true;
	}

	final public boolean isAlgebraViewEditable() {
		return false;
	}

	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		for (int i=0; i < corners.length; i++) {
    		GeoVec2D vec = tempPoints[i].getVector();
    		vec.matrixTransform(a00, a01, a02, a10,a11,a12,a20,a21,a22);    
    		if (corners[i] == null) corners[i] = new GeoPoint(cons);
    		corners[i].setCoords(vec);    			
    	}
		
	}


}
