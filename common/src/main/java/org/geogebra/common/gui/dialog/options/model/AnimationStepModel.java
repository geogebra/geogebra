package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;


public class AnimationStepModel extends OptionsModel {
	private ITextFieldListener listener;
	private Kernel kernel;
	private boolean partOfSlider; 

	public final static int TEXT_FIELD_FRACTION_DIGITS = 8;
	
	public AnimationStepModel(ITextFieldListener listener, App app) {
		this.listener = listener;
		kernel = app.getKernel();
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
			if (!Kernel.isEqual(geo0.getAnimationStep(), temp.getAnimationStep()))
				equalStep = false;
			if (!(temp.isGeoAngle()))
				onlyAngles = false;
		}

		//int oldDigits = kernel.getMaximumFractionDigits();
		//kernel.setMaximumFractionDigits(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);
		StringTemplate highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA, TEXT_FIELD_FRACTION_DIGITS,false);

		if (equalStep){
			GeoElement stepGeo = geo0.getAnimationStepObject().toGeoElement();
			if (onlyAngles && (stepGeo == null ||(!stepGeo.isLabelSet() && stepGeo.isIndependent()))) {
				listener.setText(
						kernel.formatAngle(geo0.getAnimationStep(), highPrecision, ((GeoAngle)geo0).getAngleStyle() == AngleStyle.UNBOUNDED).toString());
			} else {
				listener.setText(stepGeo.getLabel(highPrecision));
			}
		} else {
			listener.setText("");
		}

	}
	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		if (!geo.isChangeable() 
				|| geo.isGeoText() 
				|| geo.isGeoImage()
				|| geo.isGeoList()
				|| geo.isGeoBoolean()
				|| geo.isGeoButton()
				|| (!isPartOfSlider() && geo.isGeoNumeric() && geo.isIndependent()) // slider						
				)  
		{				
			return false;
		}
		return true;
	}
	
	public void applyChanges(NumberValue value) {
		if (value != null && !Double.isNaN(value.getDouble())) {
			for (int i = 0; i < getGeosLength(); i++) {
				GeoElement geo = getGeoAt(i);
				geo.setAnimationStep(value);
				geo.updateRepaint();
			}
		}
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
	};

}
