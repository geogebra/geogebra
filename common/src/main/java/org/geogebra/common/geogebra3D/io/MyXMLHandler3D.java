package org.geogebra.common.geogebra3D.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.main.settings.EuclidianSettingsForPlane;
import org.geogebra.common.io.MyXMLHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Class extending MyXMLHandler for 3D
 * 
 * @author ggb3D
 * 
 *
 */
public class MyXMLHandler3D extends MyXMLHandler {

	private HashMap<EuclidianSettings3D, String> zmin = new HashMap<>();
	private HashMap<EuclidianSettings3D, String> zmax = new HashMap<>();

	/**
	 * See Kernel3D for using the constructor
	 * 
	 * @param kernel
	 *            kernel
	 * @param cons
	 *            construction
	 */
	public MyXMLHandler3D(Kernel kernel, Construction cons) {
		super(kernel, cons);
	}

	// ====================================
	// <euclidianView3D> only used in 3D
	// ====================================
	/**
	 * only used in MyXMLHandler3D
	 * 
	 * @param eName
	 *            element name
	 * @param attrs
	 *            attributes
	 */
	@Override
	protected void startEuclidianView3DElement(String eName,
			LinkedHashMap<String, String> attrs) {

		// must do this first
		if (evSet == null) {
			evSet = app.getSettings().getEuclidian(3);
		}

		// make sure eg is reset the first time (for each EV) we get the
		// settings
		// "viewNumber" not stored for EV1 so we need to do this here
		if (resetEVsettingsNeeded) {
			resetEVsettingsNeeded = false;
			evSet.reset();
		}

		boolean ok = true;
		// EuclidianView3DInterface ev = app.getEuclidianView3D();

		switch (eName) {
		case "axesColor":
			// ok = handleAxesColor(ev, attrs);
			break;
		case "axis":
			ok = handleAxis(evSet, attrs);
			break;
		case "axesColored":
		    ok = handleColoredAxes((EuclidianSettings3D) evSet, attrs);
			break;
		case "bgColor":
			ok = handleBgColor(evSet, attrs);
			break;
		case "coordSystem":
			ok = handleCoordSystem3D((EuclidianSettings3D) evSet, attrs);
			break;
		case "clipping":
			ok = handleClipping((EuclidianSettings3D) evSet, attrs);
			break;
		case "evSettings":
			ok = handleEvSettings(evSet, attrs);
			break;
		case "grid":
			ok = handleGrid(evSet, attrs);
			break;
		case "light":
			ok = handleLight((EuclidianSettings3D) evSet, attrs);
			break;
		case "labelStyle":
			ok = handleLabelStyle(evSet, attrs);
			break;
		case "plate":
		case "plane":
			ok = handlePlate((EuclidianSettings3D) evSet, attrs);
			break;
		case "projection":
			ok = handleProjection((EuclidianSettings3D) evSet, attrs);
			break;
		case "yAxisVertical":
			ok = handleYAxisIsUp((EuclidianSettings3D) evSet, attrs);
			break;
		default:
			Log.error("unknown tag in <euclidianView3D>: " + eName);
		}

		if (!ok) {
			Log.error("error in <euclidianView3D>: " + eName);
		}
	}

	private boolean handleCoordSystem3D(EuclidianSettings3D evs,
			LinkedHashMap<String, String> attrs) {
		if (attrs.get("xZero") != null) {
			try {
				double xZero = parseDoubleNaN(attrs.get("xZero"));
				double yZero = parseDoubleNaN(attrs.get("yZero"));
				double zZero = parseDoubleNaN(attrs.get("zZero"));

				double scale = Double.parseDouble(attrs.get("scale"));
				double yscale = scale, zscale = scale;
				String yScaleString = attrs.get("yscale");
				if (yScaleString != null) {
					yscale = StringUtil.parseDouble(yScaleString);
				}
				String zScaleString = attrs.get("zscale");
				if (zScaleString != null) {
					zscale = StringUtil.parseDouble(zScaleString);
				}

				double xAngle = StringUtil.parseDouble(attrs.get("xAngle"));
				double zAngle = StringUtil.parseDouble(attrs.get("zAngle"));

				evs.setXscale(scale);
				evs.setYscale(yscale);
				evs.setZscale(zscale);
				evs.setRotXYinDegrees(zAngle, xAngle);
				evs.updateOrigin(xZero, yZero, zZero);

				getXmin().put(evs, null);
				getXmax().put(evs, null);
				getYmin().put(evs, null);
				getYmax().put(evs, null);
				zmin.put(evs, null);
				zmax.put(evs, null);

				evs.setUpdateScaleOrigin(false);
				evs.setSetStandardCoordSystem(false);

				return true;
			} catch (RuntimeException e) {
				return false;
			}
		}
		try {
			getXmin().put(evs, attrs.get("xMin"));
			getXmax().put(evs, attrs.get("xMax"));
			getYmin().put(evs, attrs.get("yMin"));
			getYmax().put(evs, attrs.get("yMax"));
			zmin.put(evs, attrs.get("zMin"));
			zmax.put(evs, attrs.get("zMax"));
			double xAngle = StringUtil.parseDouble(attrs.get("xAngle"));
			double zAngle = StringUtil.parseDouble(attrs.get("zAngle"));

			evs.setSetStandardCoordSystem(false);
			evs.setUpdateScaleOrigin(true);

			evs.setRotXYinDegrees(zAngle, xAngle);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * handles plane attributes (show plate) for EuclidianView3D
	 * 
	 * @param evs
	 *            settings
	 * @param attrs
	 *            attributes
	 * @return true if all is done ok
	 */
	protected boolean handlePlate(EuclidianSettings3D evs,
			LinkedHashMap<String, String> attrs) {
		try {
			String strShowPlate = attrs.get("show");

			// show the plane
			if (strShowPlate != null) {
				boolean showPlate = parseBoolean(strShowPlate);
				evs.setShowPlate(showPlate);
			}
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * handles if axes are colored in EuclidianView3D
	 * 
	 * @param evs
	 *            settings
	 * @param attrs
	 *            attributes
	 * @return true if all is done ok
	 */
	static private boolean handleColoredAxes(EuclidianSettings3D evs,
			LinkedHashMap<String, String> attrs) {
		try {
			String strHasColoredAxes = attrs.get("val");
			Log.debug("strHasColoredAxes = " + strHasColoredAxes);

			// show the plane
			if (strHasColoredAxes != null) {
				boolean hasColoredAxes = parseBoolean(strHasColoredAxes);
				Log.debug("hasColoredAxes = " + hasColoredAxes);
				evs.setHasColoredAxes(hasColoredAxes);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * handles plane attributes (show plate) for EuclidianView3D
	 * 
	 * @param evs
	 *            settings
	 * @param attrs
	 *            attributes
	 * @return true if all is done ok
	 */
	protected boolean handleYAxisIsUp(EuclidianSettings3D evs,
			LinkedHashMap<String, String> attrs) {
		try {
			String strYAxisVertical = attrs.get("val");

			// show the plane
			if (strYAxisVertical != null) {
				boolean yAxisVertical = parseBoolean(strYAxisVertical);
				evs.setYAxisVertical(yAxisVertical);
			}
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * handles light attributes for EuclidianView3D
	 * 
	 * @param evs
	 *            settings
	 * @param attrs
	 *            attributes
	 * @return true if all is done ok
	 */
	protected boolean handleLight(EuclidianSettings3D evs,
			LinkedHashMap<String, String> attrs) {
		try {
			String strLight = attrs.get("val");

			// show the plane
			if (strLight != null) {
				boolean light = parseBoolean(strLight);
				evs.setUseLight(light);
			}
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * handles plane attributes (show grid) for EuclidianView3D
	 * 
	 * @param evs
	 *            euclidian settings
	 * @param attrs
	 *            attributes
	 * @return true if all is done ok
	 */
	@Override
	protected boolean handleGrid(EuclidianSettings evs,
			LinkedHashMap<String, String> attrs) {
		// distX, distY
		super.handleGrid(evs, attrs);

		try {
			String strShowGrid = attrs.get("show");

			// show the plane
			if (strShowGrid != null) {
				boolean showGrid = parseBoolean(strShowGrid);
				evs.showGrid(showGrid);
			}
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param evs
	 *            settings
	 * @param attrs
	 *            attributes
	 * @return true if all is done ok
	 */
	protected boolean handleClipping(EuclidianSettings3D evs,
			LinkedHashMap<String, String> attrs) {
		try {
			String strUseClipping = attrs.get("use");
			if (strUseClipping != null) {
				boolean useClipping = parseBoolean(strUseClipping);
				evs.setUseClippingCube(useClipping);
			}
			String strShowClipping = attrs.get("show");
			if (strShowClipping != null) {
				boolean showClipping = parseBoolean(strShowClipping);
				evs.setShowClippingCube(showClipping);
			}
			String strSizeClipping = attrs.get("size");
			if (strSizeClipping != null) {
				int sizeClipping = Integer.parseInt(strSizeClipping);
				evs.setClippingReduction(sizeClipping);
			}
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * handles projection attribute
	 * 
	 * @param evs
	 *            settings
	 * @param attrs
	 *            attributes
	 * @return true if all is done ok
	 */
	protected boolean handleProjection(EuclidianSettings3D evs,
			LinkedHashMap<String, String> attrs) {
		try {
			String strType = attrs.get("type");
			if (strType != null) {
				int type = Integer.parseInt(strType);
				evs.setProjection(type);
			}
			String strDistance = attrs.get("distance");
			if (strDistance != null) {
				int distance = Integer.parseInt(strDistance);
				evs.setProjectionPerspectiveEyeDistance(distance);
			}
			String strSep = attrs.get("separation");
			if (strSep != null) {
				int sep = Integer.parseInt(strSep);
				evs.setEyeSep(sep);
			}
			String strAngle = attrs.get("obliqueAngle");
			if (strAngle != null) {
				double angle = Double.parseDouble(strAngle);
				evs.setProjectionObliqueAngle(angle);
			}
			String strFactor = attrs.get("obliqueFactor");
			if (strFactor != null) {
				double factor = Double.parseDouble(strFactor);
				evs.setProjectionObliqueFactor(factor);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/** create absolute start point (coords expected) */
	@Override
	protected GeoPointND handleAbsoluteStartPoint(
			LinkedHashMap<String, String> attrs) {
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

	@Override
	protected void startEuclidianViewElementCheckViewId(String eName,
			LinkedHashMap<String, String> attrs) {
		if ("viewId".equals(eName)) {
			String plane = attrs.get("plane");
			evSet = app.getSettings().getEuclidianForPlane(plane);
			if (evSet == null) {
				evSet = new EuclidianSettingsForPlane(app);
				app.getSettings().setEuclidianSettingsForPlane(plane, evSet);
			}
			((EuclidianSettingsForPlane) evSet).setFromLoadFile(true);
		}
	}

	@Override
	protected boolean startEuclidianViewElementSwitch(String eName,
			LinkedHashMap<String, String> attrs) {
		if ("transformForPlane".equals(eName)) {
			return handleTransformForPlane((EuclidianSettingsForPlane) evSet, attrs);
		}

		return super.startEuclidianViewElementSwitch(eName, attrs);
	}

	private static boolean handleTransformForPlane(EuclidianSettingsForPlane ev,
			LinkedHashMap<String, String> attrs) {
		try {
			ev.setTransformForPlane(Boolean.parseBoolean(attrs.get("mirror")),
					Integer.parseInt(attrs.get("rotate")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	protected void processEvSizes() {
		super.processEvSizes();
		ArrayList<EuclidianSettings3D> eSet = new ArrayList<>(
				zmin.keySet());
		for (EuclidianSettings3D ev : eSet) {
			if (zmin.get(ev) == null) {
				ev.setZminObject(null, true);
			} else {
				NumberValue n = getNumber(zmin.get(ev));
				ev.setZminObject(n, true);
			}
		}
		for (EuclidianSettings3D ev : eSet) {
			if (zmax.get(ev) == null) {
				ev.setZmaxObject(null, true);
			} else {
				NumberValue n = getNumber(zmax.get(ev));
				ev.setZmaxObject(n, true);
			}
		}
	}

}
