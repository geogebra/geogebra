package org.geogebra.common.main.settings;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Settings for 3D view
 * 
 * @author mathieu
 *
 */
public class EuclidianSettings3D extends EuclidianSettings {

	private double zscale;

	private double zZero = EuclidianView3D.ZZERO_SCENE_STANDARD;

	private double a = EuclidianView3D.ANGLE_ROT_OZ;
	private double b = EuclidianView3D.ANGLE_ROT_XOY;// angles (in degrees)

	public EuclidianSettings3D(App app, EuclidianSettings euclidianSettings1) {
		super(app, euclidianSettings1);

		setXscale(50);
		setYscale(50);
		setZscale(50);
		xZero = EuclidianView3D.XZERO_SCENE_STANDARD;
		yZero = EuclidianView3D.XZERO_SCENE_STANDARD;
	}

	private boolean hadSettingChanged = false;

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

	public void setZscaleValue(double scale) {
		this.zscale = scale;
		updateScaleHelpers();
	}

	private boolean hasSameScales = false;
	private double xyScale, yzScale, zxScale, maxScale;

	private void updateScaleHelpers() {
		hasSameScales = true;
		if (!Kernel.isEqual(xscale, yscale)) {
			hasSameScales = false;
		} else if (!Kernel.isEqual(xscale, zscale)) {
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
	 * @param b2
	 */
	public void setRotXYinDegreesFromView(double a2, double b2) {
		this.a = a2;
		this.b = b2;

	}

	public void updateRotXY(EuclidianView3D view) {
		view.setRotXYinDegrees(a, b);
	}

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
	 * @param yZero2
	 * @param zZero2
	 */
	public void updateOriginFromView(double xZero2, double yZero2, double zZero2) {
		this.xZero = xZero2;
		this.yZero = yZero2;
		this.zZero = zZero2;
	}

	public void updateOrigin(EuclidianView3D view) {
		view.setXZero(getXZero());
		view.setYZero(getYZero());
		view.setZZero(getZZero());
	}

	public double getZZero() {
		return zZero;
	}

	private boolean useClippingCube = false;

	public void setUseClippingCube(boolean flag) {

		if (useClippingCube != flag) {
			useClippingCube = flag;
			settingChanged();
		}
	}

	public boolean useClippingCube() {
		return useClippingCube;
	}

	private boolean showClippingCube = false;

	public void setShowClippingCube(boolean flag) {
		if (showClippingCube != flag) {
			showClippingCube = flag;
			settingChanged();
		}
	}

	public boolean showClippingCube() {
		return showClippingCube;
	}

	private int clippingReduction = 1;

	public void setClippingReduction(int value) {
		if (clippingReduction != value) {
			clippingReduction = value;
			settingChanged();
		}
	}

	public int getClippingReduction() {
		return clippingReduction;
	}

	private boolean showPlate = true;

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

	private int projection;

	public void setProjection(int projection) {
		if (this.projection != projection) {
			this.projection = projection;
			settingChanged();
		}
	}

	public int getProjection() {
		return projection;
	}
	
	
	private int projectionPerspectiveEyeDistance = PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT;
	
	/**
	 * default value for eye distance to the screen for perspective
	 */
	public static final int PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT = 2500;
	
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
	 */
	public void setProjectionPerspectiveEyeDistance(int distance) {
		if (projectionPerspectiveEyeDistance != distance){
			projectionPerspectiveEyeDistance = distance;
			settingChanged();
		}
	}
	
	
	
	

	public static final int EYE_SEP_DEFAULT = 200;
	
	private int eyeSep = EYE_SEP_DEFAULT;
	
	public void setEyeSep(int value){
		if (eyeSep != value){
			eyeSep = value;
			settingChanged();
		}
	}
	
	public int getEyeSep() {
		return eyeSep;
	}
	
	
	public static final double PROJECTION_OBLIQUE_ANGLE_DEFAULT = 30;
	public static final double PROJECTION_OBLIQUE_FACTOR_DEFAULT = 0.5;

	
	
	private double projectionObliqueAngle = PROJECTION_OBLIQUE_ANGLE_DEFAULT;
	private double projectionObliqueFactor = PROJECTION_OBLIQUE_FACTOR_DEFAULT;
	
	public void setProjectionObliqueAngle(double value){
		if (projectionObliqueAngle != value){
			projectionObliqueAngle = value;
			settingChanged();
		}
	}
	
	public double getProjectionObliqueAngle() {
		return projectionObliqueAngle;
	}

	public void setProjectionObliqueFactor(double value){
		if (projectionObliqueFactor != value){
			projectionObliqueFactor = value;
			settingChanged();
		}
	}
	
	public double getProjectionObliqueFactor() {
		return projectionObliqueFactor;
	}

	
	

	private boolean yAxisVertical = false;

	/**
	 * 
	 * @return true if y axis is vertical (and not z axis)
	 */
	public boolean getYAxisVertical() {
		return yAxisVertical;
	}

	public void setYAxisVertical(boolean flag) {

		if (yAxisVertical != flag) {
			yAxisVertical = flag;
			settingChanged();
		}

	}
	
	
	private boolean useLight = true;

	private double rotSpeed;
	
	public void setUseLight(boolean flag){
		if (useLight != flag) {
			useLight = flag;
			settingChanged();
		}
	}
	
	public boolean getUseLight(){
		return useLight;
	}

	@Override
	public boolean is3D() {
		return true;
	}
	
	
	@Override
	protected void resetNoFire() {
		
		super.resetNoFire();
		
		xZero = EuclidianView3D.XZERO_SCENE_STANDARD;
		yZero = EuclidianView3D.XZERO_SCENE_STANDARD;
		zZero = EuclidianView3D.ZZERO_SCENE_STANDARD;
		
		a = EuclidianView3D.ANGLE_ROT_OZ;
		b = EuclidianView3D.ANGLE_ROT_XOY;
		
		xscale = EuclidianView.SCALE_STANDARD;
		yscale = EuclidianView.SCALE_STANDARD;
		zscale = EuclidianView.SCALE_STANDARD;
		maxScale = EuclidianView.SCALE_STANDARD;
		
		yAxisVertical = false;
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
		
		
		
	}
	
	/**
	 * set x, y, z scale, don't call settingsChanged()
	 * @param scale scale value
	 */
	public void setScaleNoCallToSettingsChanged(double scale){
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
	 * @return the XML description of 3D view settings
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

		if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D) && !hasSameScales()) {
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
		sb.append(getPointCapturingMode() > EuclidianStyleConstants.POINT_CAPTURING_XML_MAX ? EuclidianStyleConstants.POINT_CAPTURING_DEFAULT
				: getPointCapturingMode());

		sb.append("\" rightAngleStyle=\"");
		sb.append(app.rightAngleStyle);
		// if (asPreference) {
		// sb.append("\" allowShowMouseCoords=\"");
		// sb.append(getAllowShowMouseCoords());
		//
		// sb.append("\" allowToolTips=\"");
		// sb.append(getAllowToolTips());
		//
		// sb.append("\" deleteToolSize=\"");
		// sb.append(getEuclidianController().getDeleteToolSize());
		// }

		// sb.append("\" checkboxSize=\"");
		// sb.append(app.getCheckboxSize()); // Michael Borcherds
		// 2008-05-12

		sb.append("\" gridType=\"");
		sb.append(getGridType()); // cartesian/isometric/polar

		// if (lockedAxesRatio != null) {
		// sb.append("\" lockedAxesRatio=\"");
		// sb.append(lockedAxesRatio);
		// }

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
		sb.append("\t<bgColor r=\"");
		sb.append(backgroundColor.getRed());
		sb.append("\" g=\"");
		sb.append(backgroundColor.getGreen());
		sb.append("\" b=\"");
		sb.append(backgroundColor.getBlue());
		sb.append("\"/>\n");

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
		if (projectionObliqueAngle != EuclidianSettings3D.PROJECTION_OBLIQUE_ANGLE_DEFAULT) {
			sb.append("\" obliqueAngle=\"");
			sb.append(projectionObliqueAngle);
		}
		if (projectionObliqueFactor != EuclidianSettings3D.PROJECTION_OBLIQUE_FACTOR_DEFAULT) {
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

}
