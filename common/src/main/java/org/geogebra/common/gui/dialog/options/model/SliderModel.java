package org.geogebra.common.gui.dialog.options.model;


import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;

public class SliderModel extends OptionsModel {
	public interface ISliderOptionsListener {
		void setMinText(final String text);
		void setMaxText(final String text);
		void setWidthText(final String text);
		void setWidthUnitText(final String text);
		void selectFixed(boolean value);
		void selectRandom(boolean value);
		void setRandomVisible(boolean value);
		void setSliderDirection(int i);

		Object update(Object[] geos2);
		
	}
	
	public final static int TEXT_FIELD_FRACTION_DIGITS = 8;
	
	private ISliderOptionsListener listener;
	private Kernel kernel;
	private App app;
	private boolean widthUnit;
	private boolean includeRandom;
	public SliderModel(App app, ISliderOptionsListener listener) {
		this.app = app;
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

	protected GeoNumeric getNumericAt(int index)  {
		return (GeoNumeric)getObjectAt(index);
	}
	@Override
	public void updateProperties() {
		// check if properties have same values
		GeoNumeric temp, num0 = getNumericAt(0);
		boolean equalMax = true;
		boolean equalMin = true;
		boolean equalWidth = true;
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
					|| !Kernel.isEqual(num0.getIntervalMin(),
							temp.getIntervalMin()))
				equalMin = false;
			if (num0.getIntervalMaxObject() == null
					|| temp.getIntervalMaxObject() == null
					|| !Kernel.isEqual(num0.getIntervalMax(),
							temp.getIntervalMax()))
				equalMax = false;
			if (!Kernel.isEqual(num0.getSliderWidth(), temp.getSliderWidth()))
				equalWidth = false;
			if (num0.isSliderFixed() != temp.isSliderFixed())
				equalSliderFixed = false;
			if (num0.isRandom() != temp.isRandom())
				random = false;
			if (num0.isSliderHorizontal() != temp.isSliderHorizontal())
				equalSliderHorizontal = false;
			if (num0.isPinned() != temp.isPinned())
				equalPinned = false;

			if (!(temp instanceof GeoAngle))
				onlyAngles = false;
		}

		StringTemplate highPrecision = StringTemplate.printDecimals(
				StringType.GEOGEBRA,
				TEXT_FIELD_FRACTION_DIGITS, false);
		if (equalMin) {
			GeoElement min0 = num0.getIntervalMinObject();
			if (onlyAngles
					&& (min0 == null || (!min0.isLabelSet() && min0
							.isIndependent()))) {
				listener.setMinText(kernel.formatAngle(num0.getIntervalMin(),
						highPrecision, true).toString());
			} else
				listener.setMinText(min0.getLabel(highPrecision));
		} else {
			listener.setMinText("");
		}

		if (equalMax) {
			GeoElement max0 = num0.getIntervalMaxObject();
			if (onlyAngles
					&& (max0 == null || (!max0.isLabelSet() && max0
							.isIndependent())))
				listener.setMaxText(kernel.formatAngle(num0.getIntervalMax(),
						highPrecision, true).toString());
			else
				listener.setMaxText(max0.getLabel(highPrecision));
		} else {
			listener.setMaxText("");
		}
		
		widthUnit=false;
		if (equalWidth && equalPinned) {
			listener.setWidthText(kernel.format(num0.getSliderWidth(), highPrecision));		
			if (num0.isPinned())
				widthUnit=true;
		} else {
			listener.setMaxText("");
		}
		
		setLabelForWidthUnit();

		if (equalSliderFixed){
			listener.selectFixed(num0.isSliderFixed());
		}

		if (random) {
			listener.selectRandom(num0.isRandom());
		}

		listener.setRandomVisible(isIncludeRandom());

		if (equalSliderHorizontal) {
			// TODO why doesn't this work when you create a slider
			listener.setSliderDirection(num0.isSliderHorizontal() ? 0
					: 1);
		}

	}
	
	public void setLabelForWidthUnit(){
		listener.setWidthUnitText(widthUnit ? app.getMenu("Pixels.short") : "");
	}

	public void applyFixed(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoNumeric num = getNumericAt(i);
			num.setSliderFixed(value);
			num.updateRepaint();
		}
		
	}
	
	public void applyRandom(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoNumeric num = getNumericAt(i);
			num.setRandom(value);
			num.updateRepaint();
		}
	}

	public void applyDirection(int value) {
		boolean isHorizontal = value == 0;
		for (int i = 0; i < getGeosLength(); i++) {
			GeoNumeric num = getNumericAt(i);
			num.setSliderHorizontal(isHorizontal);
			num.updateRepaint();
		}
	}

	
	private void applyExtrema(NumberValue value, boolean isMinimum) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoNumeric num = getNumericAt(i);
			boolean dependsOnListener = false;
			GeoElement geoValue = value.toGeoElement();
			if (num.getMinMaxListeners() != null)
				for (GeoNumeric numListener : num.getMinMaxListeners()) {
					if (geoValue.isChildOrEqual(numListener)) {
						dependsOnListener = true;
					}
				}
			
			if (dependsOnListener || geoValue.isChildOrEqual(num)) {
				app.showError(app.getLocalization().getError("CircularDefinition"));
			} else {
				if (isMinimum) {
					num.setIntervalMin(value);
				} else {
					num.setIntervalMax(value);
				}
			}
			num.updateRepaint();


		}
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
			num.setSliderWidth(value);
			num.updateRepaint();
		}
	}

	public boolean isIncludeRandom() {
		return includeRandom;
	}

	public void setIncludeRandom(boolean includeRandom) {
		this.includeRandom = includeRandom;
	}

	@Override
	public boolean updatePanel(Object[] geos2) {
		return listener.update(geos2) != null;
	}
}
