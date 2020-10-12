package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.util.DoubleUtil;

public class SliderModel extends OptionsModel {
	public final static int TEXT_FIELD_FRACTION_DIGITS = 8;

	private ISliderOptionsListener listener;
	private Kernel kernel;
	private boolean widthUnit;
	private boolean includeRandom;
	private GColor blobColor;
	private GColor lineColor;

	public interface ISliderOptionsListener extends PropertyListener {
		void setMinText(final String text);

		void setMaxText(final String text);

		void setWidthText(final String text);

		void setBlobSizeText(final String text);

		void setLineThicknessSizeText(String text);

		void setBlobColor(final GColor color);

		void setLineColor(final GColor color);

		void setWidthUnitText(final String text);

		void selectFixed(boolean value);

		void selectRandom(boolean value);

		void setRandomVisible(boolean value);

		void setSliderDirection(int i);

		@Override
		Object updatePanel(Object[] geos2);
	}

	public SliderModel(App app, ISliderOptionsListener listener) {
		super(app);
		kernel = app.getKernel();
		this.listener = listener;
		includeRandom = false;
	}

	@Override
	protected boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		if (!(geo.isIndependent() && geo.isGeoNumeric())) {
			return false;
		}
		return true;
	}

	protected GeoNumeric getNumericAt(int index) {
		return (GeoNumeric) getObjectAt(index);
	}

	@Override
	public void updateProperties() {
		// check if properties have same values
		GeoNumeric temp, num0 = getNumericAt(0);
		boolean equalMax = true;
		boolean equalMin = true;
		boolean equalWidth = true;
		boolean equalLineThickness = true;
		boolean equalBlobSize = true;
		boolean equalBlobColor = true;
		boolean equalLineColor = true;
		boolean equalSliderFixed = true;
		boolean random = true;
		boolean equalSliderHorizontal = true;
		boolean onlyAngles = true;
		boolean equalPinned = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = getNumericAt(i);

			// we don't check isIntervalMinActive, because we want to display
			// the interval even if it's empty
			if (num0.getIntervalMinObject() == null
					|| temp.getIntervalMinObject() == null
					|| !DoubleUtil.isEqual(num0.getIntervalMin(),
							temp.getIntervalMin())) {
				equalMin = false;
			}
			if (num0.getIntervalMaxObject() == null
					|| temp.getIntervalMaxObject() == null
					|| !DoubleUtil.isEqual(num0.getIntervalMax(),
							temp.getIntervalMax())) {
				equalMax = false;
			}
			if (!DoubleUtil.isEqual(num0.getSliderWidth(), temp.getSliderWidth())) {
				equalWidth = false;
			}
			if (!DoubleUtil.isEqual(num0.getLineThickness(),
					temp.getLineThickness())) {
				equalLineThickness = false;
			}
			if (!DoubleUtil.isEqual(num0.getSliderBlobSize(),
					temp.getSliderBlobSize())) {
				equalBlobSize = false;
			}
			if (num0.getObjectColor() != temp.getObjectColor()) {
				equalBlobColor = false;
			}
			if (num0.getBackgroundColor() != temp.getBackgroundColor()) {
				equalLineColor = false;
			}
			if (num0.isLockedPosition() != temp.isLockedPosition()) {
				equalSliderFixed = false;
			}
			if (num0.isRandom() != temp.isRandom()) {
				random = false;
			}
			if (num0.isSliderHorizontal() != temp.isSliderHorizontal()) {
				equalSliderHorizontal = false;
			}
			if (num0.isPinned() != temp.isPinned()) {
				equalPinned = false;
			}

			if (!(temp instanceof GeoAngle)) {
				onlyAngles = false;
			}
		}

		StringTemplate highPrecision = StringTemplate.printDecimals(
				StringType.GEOGEBRA, TEXT_FIELD_FRACTION_DIGITS, false);
		if (equalMin) {
			GeoElement min0 = GeoElement.as(num0.getIntervalMinObject());
			if (onlyAngles && (min0 == null
					|| (!min0.isLabelSet() && min0.isIndependent()))) {
				listener.setMinText(kernel
						.formatAngle(num0.getIntervalMin(), highPrecision, true)
						.toString());
			} else {
				listener.setMinText(
						num0.getIntervalMinObject().getLabel(highPrecision));
			}
		} else {
			listener.setMinText("");
		}

		if (equalMax) {
			GeoElement max0 = GeoElement.as(num0.getIntervalMaxObject());
			if (onlyAngles && (max0 == null
					|| (!max0.isLabelSet() && max0.isIndependent()))) {
				listener.setMaxText(kernel
						.formatAngle(num0.getIntervalMax(), highPrecision, true)
						.toString());
			} else {
				listener.setMaxText(
						num0.getIntervalMaxObject().getLabel(highPrecision));
			}
		} else {
			listener.setMaxText("");
		}

		widthUnit = false;
		if (equalWidth && equalPinned) {
			listener.setWidthText(
					kernel.format(num0.getSliderWidth(), highPrecision));
			if (num0.isPinned()) {
				widthUnit = true;
			}
		} else {
			listener.setMaxText("");
		}
		if (equalBlobSize) {
			listener.setBlobSizeText(
					kernel.format(num0.getSliderBlobSize(), highPrecision));
		}
		if (equalBlobColor) {
			listener.setBlobColor(num0.getObjectColor());
		}
		if (equalLineColor) {
			listener.setLineColor(num0.getBackgroundColor());
		}
		if (equalLineThickness) {
			listener.setLineThicknessSizeText(
					kernel.format(num0.getLineThickness() / 2.0,
							highPrecision));
		}

		setLabelForWidthUnit();

		if (equalSliderFixed) {
			listener.selectFixed(num0.isLockedPosition());
		}

		if (random) {
			listener.selectRandom(num0.isRandom());
		}

		listener.setRandomVisible(isIncludeRandom());

		if (equalSliderHorizontal) {
			// TODO why doesn't this work when you create a slider
			listener.setSliderDirection(num0.isSliderHorizontal() ? 0 : 1);
		}

	}

	public void setLabelForWidthUnit() {
		listener.setWidthUnitText(
				widthUnit ? app.getLocalization().getMenu("Pixels.short") : "");
	}

	public void applyFixed(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoNumeric num = getNumericAt(i);
			num.setSliderFixed(value);
			num.updateRepaint();
		}
		storeUndoInfo();
	}

	public void applyRandom(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoNumeric num = getNumericAt(i);
			num.setRandom(value);
			num.updateRepaint();
		}
		storeUndoInfo();
	}

	public void applyDirection(int value) {
		boolean isHorizontal = value == 0;
		for (int i = 0; i < getGeosLength(); i++) {
			GeoNumeric num = getNumericAt(i);
			num.setSliderHorizontal(isHorizontal);
			num.updateRepaint();
		}
		storeUndoInfo();
	}

	private void applyExtrema(NumberValue value, boolean isMinimum) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoNumeric num = getNumericAt(i);
			boolean dependsOnListener = false;
			GeoElement geoValue = value.toGeoElement(num.getConstruction());
			if (num.getMinMaxListeners() != null) {
				for (GeoNumeric numListener : num.getMinMaxListeners()) {
					if (geoValue.isChildOrEqual(numListener)) {
						dependsOnListener = true;
					}
				}
			}

			if (dependsOnListener || geoValue.isChildOrEqual(num)) {
				app.showError(Errors.CircularDefinition);
			} else {
				if (isMinimum) {
					num.setIntervalMin(value);
				} else {
					num.setIntervalMax(value);
				}
			}
			num.updateRepaint();

		}
		storeUndoInfo();
	}

	public void applyMin(NumberValue value) {
		applyExtrema(value, true);
	}

	public void applyMax(NumberValue value) {
		applyExtrema(value, false);
	}

	public void applyWidth(double value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoNumeric num = getNumericAt(i);
			num.setSliderWidth(value, true);
			num.updateRepaint();
		}
		storeUndoInfo();
	}

	public void applyTransparency(int value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoNumeric num = getNumericAt(i);
			GColor lineCol = num.getBackgroundColor() == null ? GColor.BLACK
					: num.getBackgroundColor();
			GColor colorWithTransparency = GColor.newColor(lineCol.getRed(),
					lineCol.getGreen(), lineCol.getBlue(), value * 255 / 100);
			num.setBackgroundColor(colorWithTransparency);
			num.updateRepaint();
		}
		storeUndoInfo();
	}

	/**
	 * @param value
	 *            blob size in px
	 */
	public void applyBlobSize(double value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoNumeric num = getNumericAt(i);
			num.setSliderBlobSize(value);
			num.updateRepaint();
		}
		storeUndoInfo();
	}

	/**
	 * @param value
	 *            line thickness in px
	 */
	public void applyLineThickness(double value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoNumeric num = getNumericAt(i);
			num.setLineThickness((int) Math.round(value));
			num.updateRepaint();
		}
		storeUndoInfo();
	}

	/**
	 * @param color
	 *            of blob
	 */
	public void applyBlobColor(GColor color) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoNumeric num = getNumericAt(i);
			num.setObjColor(color);
			num.updateRepaint();
		}
		blobColor = color;
		storeUndoInfo();
	}

	/**
	 * @return color of blob
	 */
	public GColor getBlobColor() {
		return blobColor == null ? GColor.BLACK : blobColor;
	}

	/**
	 * @return color of line
	 */
	public GColor getLineColor() {
		return lineColor == null ? GColor.BLACK : lineColor;
	}

	/**
	 * @param color
	 *            of line
	 */
	public void applyLineColor(GColor color) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoNumeric num = getNumericAt(i);
			num.setBackgroundColor(color);
			num.updateRepaint();
		}
		lineColor = color;
		storeUndoInfo();
	}

	public boolean isIncludeRandom() {
		return includeRandom;
	}

	public void setIncludeRandom(boolean includeRandom) {
		this.includeRandom = includeRandom;
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}
}
