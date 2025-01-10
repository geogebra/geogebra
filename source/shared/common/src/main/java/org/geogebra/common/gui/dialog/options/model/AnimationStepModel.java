package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.util.DoubleUtil;

public class AnimationStepModel extends TextPropertyModel {

	private boolean partOfSlider;

	public final static int TEXT_FIELD_FRACTION_DIGITS = 8;

	/**
	 * Constructor
	 *
	 * @param app application
	 */
	public AnimationStepModel(App app) {
		super(app);
	}

	@Override
	public String getText() {

		// check if properties have same values
		GeoElement temp, geo0 = getGeoAt(0);
		boolean equalStep = true;
		boolean onlyAngles = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = getGeoAt(i);
			// same object visible value
			if (!DoubleUtil.isEqual(geo0.getAnimationStep(),
					temp.getAnimationStep())) {
				equalStep = false;
			}
			if (!(temp.isGeoAngle())) {
				onlyAngles = false;
			}
		}

		// int oldDigits = kernel.getMaximumFractionDigits();
		// kernel.setMaximumFractionDigits(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);
		StringTemplate highPrecision = StringTemplate.printDecimals(
				StringType.GEOGEBRA, TEXT_FIELD_FRACTION_DIGITS, false);

		if (equalStep) {
			NumberValue step = geo0.getAnimationStepObject();
			GeoElement stepGeo = GeoElement.as(step);
			if (onlyAngles && (stepGeo == null
					|| (!stepGeo.isLabelSet() && stepGeo.isIndependent()))) {
				return app.getKernel()
						.formatAngle(geo0.getAnimationStep(), highPrecision,
								((GeoAngle) geo0)
										.getAngleStyle() == AngleStyle.UNBOUNDED)
						.toString();
			} else {
				boolean autostep = false;
				if (geo0.isGeoNumeric()) {
					autostep = ((GeoNumeric) geo0).isAutoStep();
				}
				return autostep ? "" : step.getLabel(highPrecision);
			}
		} else {
			return "";
		}

	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		return !(!geo.isPointerChangeable() || geo.isGeoText() || geo.isGeoImage()
				|| geo.isGeoList() || geo.isGeoBoolean() || geo.isGeoButton()
				|| (!isPartOfSlider() && geo.isGeoNumeric()
						&& geo.isIndependent()));
	}

	@Override
	public void applyChanges(GeoNumberValue value, String str) {
		boolean notDefined = value == null || Double.isNaN(value.getDouble());

		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (geo.isGeoNumeric()) {
				((GeoNumeric) geo).setAutoStep(notDefined);
				if (!notDefined) {
					geo.setAnimationStep(value);
				}
			} else if (value != null) {
				geo.setAnimationStep(value);
			}
			geo.updateVisualStyleRepaint(GProperty.COMBINED);
		}
		storeUndoInfo();
	}

	@Override
	public String getTitle() {
		return "AnimationStep";
	}

	public boolean isPartOfSlider() {
		return partOfSlider;
	}

	public void setPartOfSlider(boolean partOfSlider) {
		this.partOfSlider = partOfSlider;
	}

}
