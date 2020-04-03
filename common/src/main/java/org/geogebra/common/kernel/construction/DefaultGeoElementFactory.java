package org.geogebra.common.kernel.construction;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.plugin.EuclidianStyleConstants;

import static org.geogebra.common.kernel.ConstructionDefaults.DEFAULT_ANGLE;
import static org.geogebra.common.kernel.ConstructionDefaults.DEFAULT_ANGLE_ALPHA;
import static org.geogebra.common.kernel.ConstructionDefaults.DEFAULT_NUMBER;
import static org.geogebra.common.kernel.ConstructionDefaults.DEFAULT_NUMBER_ALPHA;

/**
 * Creates default GeoElement objects
 */
public class DefaultGeoElementFactory implements GeoElementFactory {

	private Construction construction;
	private GeoElementPropertyInitializer propertyInitializer;

	/**
	 * @param construction construction
	 */
	public DefaultGeoElementFactory(Construction construction) {
		this.construction = construction;
		propertyInitializer = new GeoElementPropertyInitializer(construction);
	}

	@Override
	public GeoNumeric createNumeric() {
		GeoNumeric number = new GeoNumeric(construction);
		number.setLocalVariableLabel("Numeric");
		number.setIntervalMax(GeoNumeric.DEFAULT_SLIDER_MAX);
		number.setIntervalMin(GeoNumeric.DEFAULT_SLIDER_MIN);
		number.setAnimationStep(GeoNumeric.DEFAULT_SLIDER_INCREMENT);
		number.setAutoStep(true);

		number.setAnimationSpeed(GeoNumeric.DEFAULT_SLIDER_SPEED);
		number.setAlphaValue(DEFAULT_NUMBER_ALPHA);
		number.setDefaultGeoType(DEFAULT_NUMBER);
		number.setLineThickness(
				number.isSlider() ? GeoNumeric.DEFAULT_SLIDER_THICKNESS
						: GeoNumeric.DEFAULT_THICKNESS);
		number.setSliderWidth(GeoNumeric.DEFAULT_SLIDER_WIDTH_PIXEL, true);
		number.setSliderBlobSize(GeoNumeric.DEFAULT_SLIDER_BLOB_SIZE);
		number.setSliderFixed(false);
		return number;
	}

	@Override
	public GeoAngle createAngle() {
		GeoAngle angle = new GeoAngle(construction);
		angle.setLocalVariableLabel("Angle");
		angle.setSliderFixed(true);
		angle.setObjColor(colAngle());
		propertyInitializer.setDefaultLineStyle(angle);
		angle.setAlphaValue(DEFAULT_ANGLE_ALPHA);
		angle.setAutoStep(true);
		angle.setArcSize(propertyInitializer.getAngleSize());
		/*
		 * we have to set min/max/increment/speed here because
		 * SetEuclideanVisible takes these from default geo
		 */
		angle.setIntervalMax(GeoAngle.DEFAULT_SLIDER_MAX_ANGLE);
		angle.setIntervalMin(GeoAngle.DEFAULT_SLIDER_MIN_ANGLE);
		angle.setAnimationStep(GeoAngle.DEFAULT_SLIDER_INCREMENT_ANGLE);
		angle.setAnimationSpeed(GeoNumeric.DEFAULT_SLIDER_SPEED);
		angle.setDefaultGeoType(DEFAULT_ANGLE);
		angle.setSliderWidth(GeoNumeric.DEFAULT_SLIDER_WIDTH_PIXEL_ANGLE, true);
		angle.setLineTypeHidden(
				EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN);
		if (construction.getApplication().isUnbundledGeometry()) {
			angle.labelMode = GeoElementND.LABEL_VALUE;
			angle.setAngleStyle(GeoAngle.AngleStyle.NOTREFLEX);
		}
		return angle;
	}

	/** default color for angles */
	private GColor colAngle() {
		return construction.getApplication().isUnbundledOrWhiteboard() ? GColor.BLACK
				: GeoGebraColorConstants.GGB_GREEN;
	}

	@Override
	public GeoElementPropertyInitializer getPropertyInitializer() {
		return propertyInitializer;
	}
}
