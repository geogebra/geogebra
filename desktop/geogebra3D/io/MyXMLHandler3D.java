package geogebra3D.io;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.io.MyXMLHandler;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoLevelOfDetail;
import geogebra.common.kernel.kernelND.GeoPlaneND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.main.AppD;
import geogebra3D.Application3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.kernel3D.GeoPoint3D;

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
	@Override
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
				AppD.debug("TODO: add EuclidianSettings for 3D");
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
			}else if (eName.equals("clipping")) {
				ok = handleClipping(ev, attrs);
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

		if (!ok) {
			System.err.println("error in <euclidianView3D>: " + eName);
		}
	}
	
	@Override
	protected void startGeoElement(String eName, LinkedHashMap<String, String> attrs) {
		if (geo == null) {
			App.debug("no element set for <" + eName + ">");
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
			App.debug("error in <element>: " + eName);
		}
	}
	
	private static boolean handleCoordSystem3D(EuclidianView3D ev, LinkedHashMap<String, String> attrs) {
		try {
			double xZero = Double.parseDouble(attrs.get("xZero"));
			double yZero = Double.parseDouble(attrs.get("yZero"));
			double zZero = Double.parseDouble(attrs.get("zZero"));
			
			double scale = Double.parseDouble(attrs.get("scale"));
			// TODO yScale, zScale

			double xAngle = Double.parseDouble(attrs.get("xAngle"));
			double zAngle = Double.parseDouble(attrs.get("zAngle"));
			

			ev.setScale(scale);
			//ev.setXZero(xZero);ev.setYZero(yZero);ev.setZZero(zZero);
			ev.setRotXYinDegrees(zAngle, xAngle);
			ev.setZeroFromXML(xZero,yZero,zZero);
			
			ev.updateMatrix();
			ev.setViewChanged();
			ev.setWaitForUpdate();	
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean handleFading(LinkedHashMap<String, String> attrs) {
		try {
			float fading = Float.parseFloat(attrs.get("val"));			
			((GeoPlaneND) geo).setFading(fading);			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleLevelOfDetail(LinkedHashMap<String, String> attrs) {
		try {
			int lod = Integer.parseInt(attrs.get("val"));			
			((GeoLevelOfDetail) geo).getLevelOfDetail().setValue(lod);
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
			String strShowPlate = attrs.get("show");

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
			String strShowGrid = attrs.get("show");

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
	
	/** handles plane attributes (show grid) for EuclidianView3D
	 * @param ev
	 * @param attrs
	 * @return true if all is done ok
	 */
	protected boolean handleClipping(EuclidianView3D ev, LinkedHashMap<String, String> attrs) {
		try {
			String strUseClipping = attrs.get("use");
			if (strUseClipping != null) {
				boolean useClipping = parseBoolean(strUseClipping);
				ev.setUseClippingCube(useClipping);
			}
			String strShowClipping = attrs.get("show");
			if (strShowClipping != null) {
				boolean showClipping = parseBoolean(strShowClipping);
				ev.setShowClippingCube(showClipping);
			}			
			String strSizeClipping = attrs.get("size");
			if (strSizeClipping != null) {
				int sizeClipping = Integer.parseInt(strSizeClipping);
				ev.setClippingReduction(sizeClipping);
			}			
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	/** create absolute start point (coords expected) */
	@Override
	protected GeoPointND handleAbsoluteStartPoint(LinkedHashMap<String, String> attrs) {
		double x = Double.parseDouble(attrs.get("x"));
		double y = Double.parseDouble(attrs.get("y"));
		double z = Double.parseDouble(attrs.get("z"));
		
		String wStr = attrs.get("w");
		GeoPointND p;
		if (wStr != null) {		
			// 3D
			double w = Double.parseDouble(wStr);
			p = new GeoPoint3D(cons);
			p.setCoords(x, y, z, w);
		} else {
			// 2D
			p = new GeoPoint(cons);
			p.setCoords(x, y, z);			
		}
		return p;
	}
	
	private static boolean handleBgColor(EuclidianViewInterfaceCommon ev, LinkedHashMap<String, String> attrs) {
		
		AppD.debug("TODO: remove this");
		
		geogebra.common.awt.GColor col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		ev.setBackground(col);
		return true;
	}
	
	private static geogebra.common.awt.GColor handleColorAttrs(LinkedHashMap<String, String> attrs) {
		try {
			int red = Integer.parseInt(attrs.get("r"));
			int green = Integer.parseInt(attrs.get("g"));
			int blue = Integer.parseInt(attrs.get("b"));
			return new geogebra.awt.GColorD(red, green, blue);
		} catch (Exception e) {
			return null;
		}
	}
	
	protected boolean handleAxis(EuclidianViewInterfaceCommon ev, LinkedHashMap<String, String> attrs) {
		
		AppD.debug("TODO: remove this");
		
		try {
			int axis = Integer.parseInt(attrs.get("id"));
			String strShowAxis = attrs.get("show");
			String label = attrs.get("label");
			String unitLabel = attrs.get("unitLabel");
			boolean showNumbers = parseBoolean(attrs.get("showNumbers"));

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
			String strTickDist = attrs.get("tickDistance");
			if (strTickDist != null) {
				double tickDist = Double.parseDouble(strTickDist);
				ev.setAxesNumberingDistance(tickDist, axis);
			}

			// tick style
			String strTickStyle = attrs.get("tickStyle");
			if (strTickStyle != null) {
				int tickStyle = Integer.parseInt(strTickStyle);
				//ev.getAxesTickStyles()[axis] = tickStyle;
				ev.setAxisTickStyle(axis, tickStyle);
			} else {
				// before v3.0 the default tickStyle was MAJOR_MINOR
				//ev.getAxesTickStyles()[axis] = EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR_MINOR;
				ev.setAxisTickStyle(axis, EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR_MINOR);
			}
			
			
			// axis crossing
			String axisCross = attrs.get("axisCross");
			if (axisCross != null) {
				double ac = Double.parseDouble(axisCross);
				ev.setAxisCross(axis,ac);
			}

			// positive direction only
			String posAxis = attrs.get("positiveAxis");
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
