package geogebra3D.io;

import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.io.MyXMLHandler;
import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.LevelOfDetail;
import geogebra.main.Application;
import geogebra3D.Application3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.kernel3D.GeoPoint3D;

import java.awt.Color;
import java.util.LinkedHashMap;



/**
 * Class extending MyXMLHandler for 3D 
 * 
 * @author ggb3D
 * 
 *
 */
public class MyXMLHandler3D extends MyXMLHandler {

	/** See Kernel3D for using the constructor
	 * @param kernel
	 * @param cons
	 */
	public MyXMLHandler3D(Kernel kernel, Construction cons) {
		super(kernel, cons);
	}
	
	
	
	
	// ====================================
	// <euclidianView3D> only used in 3D
	// ====================================
	/** only used in MyXMLHandler3D
	 * @param eName
	 * @param attrs
	 */
	protected void startEuclidianView3DElement(String eName, LinkedHashMap<String, String> attrs) {
		
		boolean ok = true;
		EuclidianView3D ev = ((Application3D) app).getEuclidianView3D();

		switch (eName.charAt(0)) {
		
		case 'a':
			if (eName.equals("axesColor")) {
				//ok = handleAxesColor(ev, attrs);
				break;
			} else if (eName.equals("axis")) {
				ok = handleAxis(ev, attrs);
				Application.debug("TODO: add EuclidianSettings for 3D");
				break;
			}

			
		case 'b':
			if (eName.equals("bgColor")) {
				ok = handleBgColor(ev, attrs);
				break;
			}
			

		case 'c':
			if (eName.equals("coordSystem")) {
				ok = handleCoordSystem3D(ev, attrs);
				break;
			}

		case 'g':
			if (eName.equals("grid")) {
				ok = handleGrid(ev, attrs);
				break;
			} 
			/*
			else if (eName.equals("gridColor")) {
				ok = handleGridColor(ev, attrs);
				break;
			}
			 */
			
		case 'p':
			if (eName.equals("plate")) {
				ok = handlePlate(ev, attrs);
				break;
			} else if (eName.equals("plane")) {
				ok = handlePlane(ev, attrs);
				break;
			}
			
			/*

		case 's':
			if (eName.equals("size")) {
				ok = handleEvSize(ev, attrs);
				break;
			}
			*/

		default:
			System.err.println("unknown tag in <euclidianView3D>: " + eName);
		}

		if (!ok)
			System.err.println("error in <euclidianView3D>: " + eName);
	}
	
	
	
	protected void startGeoElement(String eName, LinkedHashMap<String, String> attrs) {
		if (geo == null) {
			System.err.println("no element set for <" + eName + ">");
			return;
		}

		boolean ok = true;
		switch (eName.charAt(0)) {
		case 'f':
			if (eName.equals("fading")) {
				ok = handleFading(attrs);
				break;
			}
		case 'l':
			if (eName.equals("levelOfDetail")) {
				ok = handleLevelOfDetail(attrs);
				break;
			}
			
		default:
			super.startGeoElement(eName, attrs);
		}

		if (!ok) {
			System.err.println("error in <element>: " + eName);
		}
	}
	
	
	
	
	private boolean handleCoordSystem3D(EuclidianView3D ev, LinkedHashMap<String, String> attrs) {
		try {
			double xZero = Double.parseDouble((String) attrs.get("xZero"));
			double yZero = Double.parseDouble((String) attrs.get("yZero"));
			double zZero = Double.parseDouble((String) attrs.get("zZero"));
			
			double scale = Double.parseDouble((String) attrs.get("scale"));
			// TODO yScale, zScale

			double xAngle = Double.parseDouble((String) attrs.get("xAngle"));
			double zAngle = Double.parseDouble((String) attrs.get("zAngle"));
			

			ev.setScale(scale);
			ev.setXZero(xZero);ev.setYZero(yZero);ev.setZZero(zZero);
			ev.setRotXYinDegrees(zAngle, xAngle);
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	private boolean handleFading(LinkedHashMap<String, String> attrs) {
		try {
			float fading = Float.parseFloat((String) attrs.get("val"));			
			((GeoPlaneND) geo).setFading(fading);			
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	private boolean handleLevelOfDetail(LinkedHashMap<String, String> attrs) {
		try {
			int lod = Integer.parseInt((String) attrs.get("val"));			
			((LevelOfDetail) geo).setLevelOfDetail(lod);			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	
	/** handles plane attributes for EuclidianView3D
	 * @param ev
	 * @param attrs
	 * @return true if all is done ok
	 * @deprecated
	 */
	protected boolean handlePlane(EuclidianView3D ev, LinkedHashMap<String, String> attrs) {
		
		return handlePlate(ev, attrs);
		/*
		// <plane show="false"/>
		try {
			String strShowPlane = (String) attrs.get("show");

			// show the plane
			if (strShowPlane != null) {
				boolean showPlane = parseBoolean(strShowPlane);
				ev.setShowPlane(showPlane);
			}
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		*/
	}
	
	
	/** handles plane attributes (show plate) for EuclidianView3D
	 * @param ev
	 * @param attrs
	 * @return true if all is done ok
	 */
	protected boolean handlePlate(EuclidianView3D ev, LinkedHashMap<String, String> attrs) {
		try {
			String strShowPlate = (String) attrs.get("show");

			// show the plane
			if (strShowPlate != null) {
				boolean showPlate = parseBoolean(strShowPlate);
				ev.setShowPlate(showPlate);
			}
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	
	/** handles plane attributes (show grid) for EuclidianView3D
	 * @param ev
	 * @param attrs
	 * @return true if all is done ok
	 */
	protected boolean handleGrid(EuclidianView3D ev, LinkedHashMap<String, String> attrs) {
		try {
			String strShowGrid = (String) attrs.get("show");

			// show the plane
			if (strShowGrid != null) {
				boolean showGrid = parseBoolean(strShowGrid);
				ev.setShowGrid(showGrid);
			}
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	/** create absolute start point (coords expected) */
	protected GeoPointND handleAbsoluteStartPoint(LinkedHashMap<String, String> attrs) {
		double x = Double.parseDouble((String) attrs.get("x"));
		double y = Double.parseDouble((String) attrs.get("y"));
		double z = Double.parseDouble((String) attrs.get("z"));
		double w = Double.parseDouble((String) attrs.get("w"));
		GeoPoint3D p = new GeoPoint3D(cons);
		p.setCoords(x, y, z, w);
		return p;
	}
	
	
	private boolean handleBgColor(EuclidianViewInterface ev, LinkedHashMap<String, String> attrs) {
		
		Application.debug("TODO: remove this");
		
		Color col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		ev.setBackground(col);
		return true;
	}
	
	private Color handleColorAttrs(LinkedHashMap<String, String> attrs) {
		try {
			int red = Integer.parseInt((String) attrs.get("r"));
			int green = Integer.parseInt((String) attrs.get("g"));
			int blue = Integer.parseInt((String) attrs.get("b"));
			return new Color(red, green, blue);
		} catch (Exception e) {
			return null;
		}
	}
	
	protected boolean handleAxis(EuclidianViewInterface ev, LinkedHashMap<String, String> attrs) {
		
		Application.debug("TODO: remove this");
		
		try {
			int axis = Integer.parseInt((String) attrs.get("id"));
			String strShowAxis = (String) attrs.get("show");
			String label = (String) attrs.get("label");
			String unitLabel = (String) attrs.get("unitLabel");
			boolean showNumbers = parseBoolean((String) attrs.get("showNumbers"));

			// show this axis
			if (strShowAxis != null) {
				boolean showAxis = parseBoolean(strShowAxis);
				ev.setShowAxis(axis, showAxis, true);
			}

			// set label
			ev.setAxisLabel(axis, label);
			/*
			if (label != null && label.length() > 0) {
				String[] labels = ev.getAxesLabels();
				labels[axis] = label;
				ev.setAxesLabels(labels);
			}
			*/

			// set unitlabel
			if (unitLabel != null && unitLabel.length() > 0) {
				String[] unitLabels = ev.getAxesUnitLabels();
				unitLabels[axis] = unitLabel;
				ev.setAxesUnitLabels(unitLabels);
			}

			// set showNumbers
			ev.setShowAxisNumbers(axis, showNumbers);
			/*
			boolean showNums[] = ev.getShowAxesNumbers();
			showNums[axis] = showNumbers;
			ev.setShowAxesNumbers(showNums);
			*/

			// check if tickDistance is given
			String strTickDist = (String) attrs.get("tickDistance");
			if (strTickDist != null) {
				double tickDist = Double.parseDouble(strTickDist);
				ev.setAxesNumberingDistance(tickDist, axis);
			}

			// tick style
			String strTickStyle = (String) attrs.get("tickStyle");
			if (strTickStyle != null) {
				int tickStyle = Integer.parseInt(strTickStyle);
				//ev.getAxesTickStyles()[axis] = tickStyle;
				ev.setAxisTickStyle(axis, tickStyle);
			} else {
				// before v3.0 the default tickStyle was MAJOR_MINOR
				//ev.getAxesTickStyles()[axis] = EuclidianView.AXES_TICK_STYLE_MAJOR_MINOR;
				ev.setAxisTickStyle(axis, EuclidianView.AXES_TICK_STYLE_MAJOR_MINOR);
			}
			
			
			// axis crossing
			String axisCross = (String) attrs.get("axisCross");
			if (axisCross != null) {
				double ac = Double.parseDouble(axisCross);
				ev.setAxisCross(axis,ac);
			}

			// positive direction only
			String posAxis = (String) attrs.get("positiveAxis");
			if (posAxis != null) {
				boolean isPositive = Boolean.parseBoolean(posAxis);
				ev.setPositiveAxis(axis,isPositive);
			}
			
				
			
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}


	

}
