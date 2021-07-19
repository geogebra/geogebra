package org.geogebra.common.main.settings;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.XMLBuilder;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;

/**
 * Settings for 3D view
 * 
 * @author mathieu
 *
 */
public class EuclidianSettings3D extends EuclidianSettings {

	private double zscale;

	private double zZero = EuclidianView3DInterface.ZZERO_SCENE_STANDARD;

	private double a = EuclidianView3DInterface.ANGLE_ROT_OZ;
	private double b = EuclidianView3DInterface.ANGLE_ROT_XOY; // angles (in
																// degrees)

	private boolean hadSettingChanged = false;
	private boolean hasSameScales = true;
	private NumberValue zminObject;
	private NumberValue zmaxObject;
	private double xyScale;
	private double yzScale;
	private double zxScale;
	private double maxScale;
	private boolean showPlate = true;
	private int projectionPerspectiveEyeDistance = PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT;
	public static final int EYE_SEP_DEFAULT = 200;

	private int eyeSep = EYE_SEP_DEFAULT;

	public static final double PROJECTION_OBLIQUE_ANGLE_DEFAULT = 30;
	public static final double PROJECTION_OBLIQUE_FACTOR_DEFAULT = 0.5;

	private double projectionObliqueAngle = PROJECTION_OBLIQUE_ANGLE_DEFAULT;
	private double projectionObliqueFactor = PROJECTION_OBLIQUE_FACTOR_DEFAULT;
	private boolean yAxisVertical = false;

	private boolean useLight = true;

	private double rotSpeed;

	private int projection;

	private boolean useClippingCube = false;
	private int clippingReduction = 1;
	private boolean showClippingCube = false;

	private boolean hasColoredAxes = true;
	private boolean updateScaleOrigin;
	private boolean setStandardCoordSystem = true;

	/**
	 * default value for eye distance to the screen for perspective
	 */
	public static final int PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT = 2500;

	/**
	 * @param app
	 *            application
	 */
	public EuclidianSettings3D(App app) {
		super(app);

		setXscale(50);
		setYscale(50);
		setZscale(50);
		xZero = EuclidianView3DInterface.XZERO_SCENE_STANDARD;
		yZero = EuclidianView3DInterface.XZERO_SCENE_STANDARD;
		dimension = 3;
	}

	/**
	 * 
	 * @return true if some setting has been changed
	 */
	public boolean hadSettingChanged() {
		return hadSettingChanged;
	}

	@Override
	protected void settingChanged() {
		super.settingChanged();
		hadSettingChanged = true;
	}

	/**
	 * @param updateScaleOrigin
	 *            flag to determine if origin and scale have to be updated
	 */
	public void setUpdateScaleOrigin(boolean updateScaleOrigin) {
		this.updateScaleOrigin = updateScaleOrigin;
	}

	/**
	 * @return flag updateScaleOrigin
	 */
	public boolean isUpdateScaleOrigin() {
		return updateScaleOrigin;
	}

	/**
	 * @param x
	 *            x-axis scale
	 * @param y
	 *            y-axis scale
	 * @param z
	 *			  z-axis scale
	 */
	public void setXYZscale(double x, double y, double z) {
		setXYZscaleValues(x, y, z);
		settingChanged();
	}

	/**
	 * @param x
	 *            x-axis scale
	 * @param y
	 *            y-axis scale
	 * @param z
	 *			  z-axis scale
	 */
	public void setXYZscaleValues(double x, double y, double z) {
		this.xscale = x;
		this.yscale = y;
		this.zscale = z;
		updateScaleHelpers();
	}

	/**
	 * @param scale
	 *            screen : RW ratio for z-axis
	 */
	public void setZscale(double scale) {
		if (this.zscale != scale) {
			setZscaleValue(scale);
			settingChanged();
		}
	}

	public double getZscale() {
		return zscale;
	}

	@Override
	public void setXscaleValue(double scale) {
		super.setXscaleValue(scale);
		updateScaleHelpers();
	}

	@Override
	public void setYscaleValue(double scale) {
		super.setYscaleValue(scale);
		updateScaleHelpers();
	}

	/**
	 * @param scale
	 *            z-axis scale
	 */
	public void setZscaleValue(double scale) {
		this.zscale = scale;
		updateScaleHelpers();
	}

	/**
	 * @return the yminObject
	 */
	public GeoNumeric getZminObject() {
		return (GeoNumeric) zminObject;
	}

	/**
	 * @param zminObjectNew
	 *            the zminObject to set
	 * @param callsc
	 *            whether settingChanged should be called
	 */
	public void setZminObject(NumberValue zminObjectNew, boolean callsc) {
		this.zminObject = zminObjectNew;
		if (callsc) {
			settingChanged();
		}
	}

	/**
	 * @return the ymaxObject
	 */
	public GeoNumeric getZmaxObject() {
		return (GeoNumeric) zmaxObject;
	}

	/**
	 * @param zmaxObjectNew
	 *            the zmaxObject to set
	 * @param callsc
	 *            whether settingChanged should be called
	 */
	public void setZmaxObject(NumberValue zmaxObjectNew, boolean callsc) {
		this.zmaxObject = zmaxObjectNew;
		if (callsc) {
			settingChanged();
		}
	}

	private void updateScaleHelpers() {
		hasSameScales = true;
		if (!DoubleUtil.isEqual(xscale, yscale)) {
			hasSameScales = false;
		} else if (!DoubleUtil.isEqual(xscale, zscale)) {
			hasSameScales = false;
		}

		maxScale = xscale;
		if (yscale > maxScale) {
			maxScale = yscale;
		}
		if (zscale > maxScale) {
			maxScale = zscale;
		}

		xyScale = xscale * yscale;
		yzScale = yscale * zscale;
		zxScale = zscale * xscale;
	}

	public double getMaxScale() {
		return maxScale;
	}

	/**
	 * 
	 * @return x scale * y scale
	 */
	public double getXYscale() {
		return xyScale;
	}

	/**
	 * 
	 * @return y scale * z scale
	 */
	public double getYZscale() {
		return yzScale;
	}

	/**
	 * 
	 * @return z scale * x scale
	 */
	public double getZXscale() {
		return zxScale;
	}

	/**
	 * 
	 * @return true if scales are equals on x,y,z
	 */
	public boolean hasSameScales() {
		return hasSameScales;
	}

	/**
	 * @param a2
	 *            xy rotation
	 * @param b2
	 *            xz rotation
	 */
	public void setRotXYinDegrees(double a2, double b2) {
		if (this.a != a2 || this.b != b2) {
			this.a = a2;
			this.b = b2;
			settingChanged();
		}
	}

	/**
	 * we won't call settingChanged() here since it's called from view
	 * 
	 * @param a2
	 *            OZ rotation
	 * @param b2
	 *            xOy rotation
	 */
	public void setRotXYinDegreesFromView(double a2, double b2) {
		this.a = a2;
		this.b = b2;
	}

	public void updateRotXY(EuclidianView3DInterface view) {
		view.setRotXYinDegrees(a, b);
	}

	/**
	 * @param xZero2
	 *            x-coord of the origin
	 * @param yZero2
	 *            y-coord of the origin
	 * @param zZero2
	 *            z-coord of the origin
	 */
	public void updateOrigin(double xZero2, double yZero2, double zZero2) {
		if (this.xZero != xZero2 || this.yZero != yZero2
				|| this.zZero != zZero2) {
			this.xZero = xZero2;
			this.yZero = yZero2;
			this.zZero = zZero2;
			settingChanged();
		}
	}

	/**
	 * we won't call settingChanged() here since it's called from view
	 * 
	 * @param xZero2
	 *            origin x-coord
	 * @param yZero2
	 *            origin y-coord
	 * @param zZero2
	 *            origin z-coord
	 */
	public void updateOriginFromView(double xZero2, double yZero2,
			double zZero2) {
		this.xZero = xZero2;
		this.yZero = yZero2;
		this.zZero = zZero2;
	}

	/**
	 * @param view
	 *            view to be updated
	 */
	public void updateOrigin(EuclidianView3DInterface view) {
		view.setXZero(getXZero());
		view.setYZero(getYZero());
		view.setZZero(getZZero());
	}

	public double getZZero() {
		return zZero;
	}

	/**
	 * @param flag
	 *            whether to use clipping
	 */
	public void setUseClippingCube(boolean flag) {
		if (useClippingCube != flag) {
			useClippingCube = flag;
			settingChanged();
		}
	}

	public boolean useClippingCube() {
		return useClippingCube;
	}

	/**
	 * @param flag
	 *            whether to show clipping cube
	 */
	public void setShowClippingCube(boolean flag) {
		if (showClippingCube != flag) {
			showClippingCube = flag;
			settingChanged();
		}
	}

	public boolean showClippingCube() {
		return showClippingCube;
	}

	/**
	 * Change clipping cube size.
	 * 
	 * @param value
	 *            GeoClippingCube3D.REDUCTION_*
	 */
	public void setClippingReduction(int value) {
		if (clippingReduction != value) {
			clippingReduction = value;
			settingChanged();
		}
	}

	public int getClippingReduction() {
		return clippingReduction;
	}

	/**
	 * @param flag
	 *            whether to show xOy plate
	 */
	public void setShowPlate(boolean flag) {
		if (showPlate != flag) {
			showPlate = flag;
			settingChanged();
		}
	}

	public boolean getShowPlate() {
		return showPlate;
	}

	/**
	 * toggle visibility of the plane
	 */
	public void togglePlane() {
		showPlate = !showPlate;
		settingChanged();

	}

	/**
	 * @param projection
	 *            projection index
	 */
	public void setProjection(int projection) {
		if (this.projection != projection) {
			this.projection = projection;
			settingChanged();
		}
	}

	public int getProjection() {
		return projection;
	}

	/**
	 * 
	 * @return eye distance to the screen for perspective
	 */
	public int getProjectionPerspectiveEyeDistance() {
		return projectionPerspectiveEyeDistance;
	}

	/**
	 * set the near distance regarding eye distance to the screen for
	 * perspective (in pixels)
	 * 
	 * @param distance
	 *            eye distance
	 */
	public void setProjectionPerspectiveEyeDistance(int distance) {
		if (projectionPerspectiveEyeDistance != distance) {
			projectionPerspectiveEyeDistance = distance;
			settingChanged();
		}
	}

	/**
	 * @param value
	 *            eye separation
	 */
	public void setEyeSep(int value) {
		if (eyeSep != value) {
			eyeSep = value;
			settingChanged();
		}
	}

	public int getEyeSep() {
		return eyeSep;
	}

	/**
	 * @param value
	 *            oblique projection angle
	 */
	public void setProjectionObliqueAngle(double value) {
		if (projectionObliqueAngle != value) {
			projectionObliqueAngle = value;
			settingChanged();
		}
	}

	public double getProjectionObliqueAngle() {
		return projectionObliqueAngle;
	}

	/**
	 * @param value
	 *            oblique projection factor
	 */
	public void setProjectionObliqueFactor(double value) {
		if (projectionObliqueFactor != value) {
			projectionObliqueFactor = value;
			settingChanged();
		}
	}

	public double getProjectionObliqueFactor() {
		return projectionObliqueFactor;
	}

	/**
	 * 
	 * @return true if y axis is vertical (and not z axis)
	 */
	public boolean getYAxisVertical() {
		return yAxisVertical;
	}

	/**
	 * @param flag
	 *            whether yAxis should be vertical
	 */
	public void setYAxisVertical(boolean flag) {
		if (yAxisVertical != flag) {
			yAxisVertical = flag;
			settingChanged();
		}
	}

	/**
	 * @param flag
	 *            whether to use light
	 */
	public void setUseLight(boolean flag) {
		if (useLight != flag) {
			useLight = flag;
			settingChanged();
		}
	}

	public boolean getUseLight() {
		return useLight;
	}

	@Override
	public boolean is3D() {
		return true;
	}

	/**
	 * set orientation, position, scaling to standard view
	 */
	public void setStandardView() {
		xZero = EuclidianView3DInterface.XZERO_SCENE_STANDARD;
		yZero = EuclidianView3DInterface.XZERO_SCENE_STANDARD;
		zZero = EuclidianView3DInterface.ZZERO_SCENE_STANDARD;

		a = EuclidianView3DInterface.ANGLE_ROT_OZ;
		b = EuclidianView3DInterface.ANGLE_ROT_XOY;

		xscale = EuclidianView.SCALE_STANDARD;
		yscale = EuclidianView.SCALE_STANDARD;
		zscale = EuclidianView.SCALE_STANDARD;
		maxScale = EuclidianView.SCALE_STANDARD;

		yAxisVertical = false;
	}

	@Override
	protected void resetNoFire() {

		super.resetNoFire();
		zminObject = null;
		zmaxObject = null;
		setStandardView();

		useLight = true;

		clippingReduction = 1;
		useClippingCube = false;
		showClippingCube = false;

		showPlate = true;

		projection = 0;

		eyeSep = EYE_SEP_DEFAULT;
		projectionPerspectiveEyeDistance = PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT;

		projectionObliqueAngle = PROJECTION_OBLIQUE_ANGLE_DEFAULT;
		projectionObliqueFactor = PROJECTION_OBLIQUE_FACTOR_DEFAULT;

		hasColoredAxes = true;

	}

	/**
	 * set x, y, z scale, don't call settingsChanged()
	 * 
	 * @param scale
	 *            scale value
	 */
	public void setScaleNoCallToSettingsChanged(double scale) {
		this.xscale = scale;
		this.yscale = scale;
		this.zscale = scale;
	}

	public void setRotSpeed(double d) {
		this.rotSpeed = d;
	}

	public double getRotSpeed() {
		return rotSpeed;
	}

	/**
	 * returns settings in XML format, read by xml handlers
	 * 
	 * @param sb
	 *            xml builder
	 * @param asPreference
	 *            whether this is for preferences
	 *
	 * @see org.geogebra.common.io.MyXMLHandler
	 * @see org.geogebra.common.geogebra3D.io.MyXMLHandler3D
	 */
	public void getXML(StringBuilder sb, boolean asPreference) {

		// Application.debug("getXML: "+a+","+b);

		// if (true) return "";

		sb.append("<euclidianView3D>\n");

		// coord system
		sb.append("\t<coordSystem");

		sb.append(" xZero=\"");
		sb.append(getXZero());
		sb.append("\"");
		sb.append(" yZero=\"");
		sb.append(getYZero());
		sb.append("\"");
		sb.append(" zZero=\"");
		sb.append(getZZero());
		sb.append("\"");

		sb.append(" scale=\"");
		sb.append(getXscale());
		sb.append("\"");

		if (!hasSameScales()) {
			sb.append(" yscale=\"");
			sb.append(getYscale());
			sb.append("\"");

			sb.append(" zscale=\"");
			sb.append(getZscale());
			sb.append("\"");
		}

		sb.append(" xAngle=\"");
		sb.append(b);
		sb.append("\"");
		sb.append(" zAngle=\"");
		sb.append(a);
		sb.append("\"");

		sb.append("/>\n");

		// ev settings
		sb.append("\t<evSettings axes=\"");
		sb.append(getShowAxis(0) || getShowAxis(1) || getShowAxis(2));

		sb.append("\" grid=\"");
		sb.append(getShowGrid());
		sb.append("\" gridIsBold=\""); //
		sb.append(gridIsBold); // Michael Borcherds 2008-04-11
		sb.append("\" pointCapturing=\"");

		// make sure POINT_CAPTURING_STICKY_POINTS isn't written to XML
		sb.append(
				getPointCapturingMode() > EuclidianStyleConstants.POINT_CAPTURING_XML_MAX
						? EuclidianStyleConstants.POINT_CAPTURING_DEFAULT
						: getPointCapturingMode());

		sb.append("\" rightAngleStyle=\"");
		sb.append(app.rightAngleStyle);

		sb.append("\" gridType=\"");
		sb.append(getGridType()); // cartesian/isometric/polar

		sb.append("\"/>\n");
		// end ev settings

		// axis settings
		for (int i = 0; i < 3; i++) {
			addAxisXML(i, sb);

		}

		// xOy plane settings
		sb.append("\t<plate show=\"");
		sb.append(showPlate);
		sb.append("\"/>\n");
		//
		// sb.append("\t<grid show=\"");
		// sb.append(getxOyPlane().isGridVisible());
		// sb.append("\"/>\n");

		// background color
		sb.append("\t<bgColor");
		XMLBuilder.appendRGB(sb, backgroundColor);
		sb.append("/>\n");

		// y axis is up
		if (getYAxisVertical()) {
			sb.append("\t<yAxisVertical val=\"true\"/>\n");
		}

		// use light
		if (!getUseLight()) {
			sb.append("\t<light val=\"false\"/>\n");
		}

		// clipping cube
		sb.append("\t<clipping use=\"");
		sb.append(useClippingCube());
		sb.append("\" show=\"");
		sb.append(showClippingCube());
		sb.append("\" size=\"");
		sb.append(getClippingReduction());
		sb.append("\"/>\n");

		// projection
		sb.append("\t<projection type=\"");
		sb.append(getProjection());
		getXMLForStereo(sb);
		if (!DoubleUtil.isEqual(projectionObliqueAngle,
				EuclidianSettings3D.PROJECTION_OBLIQUE_ANGLE_DEFAULT)) {
			sb.append("\" obliqueAngle=\"");
			sb.append(projectionObliqueAngle);
		}
		if (!DoubleUtil.isEqual(projectionObliqueFactor,
				EuclidianSettings3D.PROJECTION_OBLIQUE_FACTOR_DEFAULT)) {
			sb.append("\" obliqueFactor=\"");
			sb.append(projectionObliqueFactor);
		}

		sb.append("\"/>\n");

		// axes label style
		int style = getAxisFontStyle();
		if (style == GFont.BOLD || style == GFont.ITALIC
				|| style == GFont.BOLD + GFont.ITALIC) {
			sb.append("\t<labelStyle axes=\"");
			sb.append(style);
			sb.append("\"/>\n");
		}

		// end
		sb.append("</euclidianView3D>\n");

	}

	private void getXMLForStereo(StringBuilder sb) {
		int eyeDistance = projectionPerspectiveEyeDistance;
		if (eyeDistance != EuclidianSettings3D.PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT) {
			sb.append("\" distance=\"");
			sb.append(eyeDistance);
		}
		int sep = getEyeSep();
		if (sep != EuclidianSettings3D.EYE_SEP_DEFAULT) {
			sb.append("\" separation=\"");
			sb.append(sep);
		}
	}

	@Override
	public boolean setShowAxes(boolean flag) {
		boolean changed = setShowAxisNoFireSettingChanged(0, flag);
		changed = setShowAxisNoFireSettingChanged(1, flag) || changed;
		changed = setShowAxisNoFireSettingChanged(2, flag) || changed;
		if (changed) {
			settingChanged();
		}
		return changed;
	}

	/**
	 * 
	 * @param flag
	 *            iff axes are rgb colored
	 * @return true if it has changed
	 */
	public boolean setHasColoredAxes(boolean flag) {
		if (hasColoredAxes != flag) {
			hasColoredAxes = flag;
			settingChanged();
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return iff axes are rgb colored
	 */
	public boolean getHasColoredAxes() {
		return hasColoredAxes;
	}

	/**
	 * @param xZero
	 *            x-coord of the origin
	 * @param yZero
	 *            y-coord of the origin
	 * @param zZero
	 *            z-coord of the origin
	 * @param xscale
	 *            x scale
	 * @param yscale
	 *            y scale
	 * @param zscale
	 *            z scale
	 * @param fire
	 *            whether to notify listeners
	 */
	public void setCoordSystem(double xZero, double yZero, double zZero, double xscale,
			double yscale, double zscale, boolean fire) {
		if (Double.isNaN(xscale) || (xscale < Kernel.MAX_DOUBLE_PRECISION)
				|| (xscale > Kernel.INV_MAX_DOUBLE_PRECISION)) {
			return;
		}
		if (Double.isNaN(yscale) || (yscale < Kernel.MAX_DOUBLE_PRECISION)
				|| (yscale > Kernel.INV_MAX_DOUBLE_PRECISION)) {
			return;
		}
		if (Double.isNaN(zscale) || (zscale < Kernel.MAX_DOUBLE_PRECISION)
				|| (zscale > Kernel.INV_MAX_DOUBLE_PRECISION)) {
			return;
		}

		this.xZero = xZero;
		this.yZero = yZero;
		this.zZero = zZero;
		this.xscale = xscale;
		this.yscale = yscale;
		this.zscale = zscale;
		updateScaleHelpers();

		if (fire) {
			settingChanged();
		}
	}

	public boolean isSetStandardCoordSystem() {
		return setStandardCoordSystem;
	}

	public void setSetStandardCoordSystem(boolean setStandardCoordSystem) {
		this.setStandardCoordSystem = setStandardCoordSystem;
	}
}
