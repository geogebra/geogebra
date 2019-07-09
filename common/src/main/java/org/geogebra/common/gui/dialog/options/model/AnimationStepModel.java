package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.util.DoubleUtil;

public class AnimationStepModel extends OptionsModel {
	private ITextFieldListener listener;
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
	public void updateProperties() {

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
				listener.setText(app.getKernel()
						.formatAngle(geo0.getAnimationStep(), highPrecision,
								((GeoAngle) geo0)
										.getAngleStyle() == AngleStyle.UNBOUNDED)
						.toString());
			} else {
				boolean autostep = false;
				if (geo0.isGeoNumeric()) {
					autostep = ((GeoNumeric) geo0).isAutoStep();
				}
				listener.setText(autostep ? "" : step.getLabel(highPrecision));
			}
		} else {
			listener.setText("");
		}

	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		if (!geo.isPointerChangeable() || geo.isGeoText() || geo.isGeoImage()
				|| geo.isGeoList() || geo.isGeoBoolean() || geo.isGeoButton()
				|| (!isPartOfSlider() && geo.isGeoNumeric()
						&& geo.isIndependent()) // slider
		) {
			return false;
		}
		return true;
	}

	public void applyChanges(String text) {
		NumberValue value = text.length() == 0 ? null
				: app.getKernel().getAlgebraProcessor().evaluateToNumeric(text,
						ErrorHelper.silent());
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
			geo.updateRepaint();
		}
		storeUndoInfo();
	}

	public boolean isPartOfSlider() {
		return partOfSlider;
	}

	public void setPartOfSlider(boolean partOfSlider) {
		this.partOfSlider = partOfSlider;
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}

	/**
	 * Set the listener of the model
	 * @param listener listener for the textfield
	 */
	public void setListener(ITextFieldListener listener) {
		this.listener = listener;
	}

}
