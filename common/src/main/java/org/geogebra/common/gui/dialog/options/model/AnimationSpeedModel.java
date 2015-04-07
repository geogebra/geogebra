package org.geogebra.common.gui.dialog.options.model;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;


public class AnimationSpeedModel extends MultipleOptionsModel {
	public interface IAnimationSpeedListener extends IComboListener, ITextFieldListener{}; 

	private boolean showSliders = false;
	private Kernel kernel;
	@Override
	public IAnimationSpeedListener getListener(){
		return (IAnimationSpeedListener)super.getListener();
	}

	public AnimationSpeedModel(App app, IAnimationSpeedListener listener) {
		super(listener);
		kernel = app.getKernel();
	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);

		if(geo.isPointOnPath() || geo.getDefaultGeoType() == ConstructionDefaults.DEFAULT_POINT_ON_PATH){
			if(!geo.isChangeable())
				return false;
		}else if(geo.isGeoNumeric() &&  geo.isIndependent()){
			if(!isShowSliders() || !geo.isChangeable()) //slider  
				return false; 											
		}else{
			return false;
		}
		return true;
	}



	@Override
	public void updateProperties() {
		// check if properties have same values
		GeoElement temp, geo0 = getGeoAt(0);
		boolean equalSpeed = true;
		boolean equalAnimationType = true;

		for (int i = 0; i < getGeosLength(); i++) {
			temp = getGeoAt(i);
			// same object visible value
			if (geo0.getAnimationSpeedObject() != temp.getAnimationSpeedObject()) {
				equalSpeed = false;
			}

			if (geo0.getAnimationType() != temp.getAnimationType()) {
				equalAnimationType = false;
			}
		}

		getListener().setSelectedIndex(equalAnimationType ? 
				geo0.getAnimationType(): -1);

		StringTemplate highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA, AnimationStepModel.TEXT_FIELD_FRACTION_DIGITS,false);

		if (equalSpeed) {
			GeoElement speedObj = geo0.getAnimationSpeedObject();
			GeoNumeric num = kernel.getAlgoDispatcher().getDefaultNumber(geo0.isAngle());
			getListener().setText(speedObj == null ? num.getAnimationSpeedObject().getLabel(highPrecision) : speedObj.getLabel(highPrecision));
		} else
			getListener().setText("");

	}
	@Override
	public List<String> getChoiches(Localization loc) {
		return Arrays.asList(
				"\u21d4 "+loc.getPlain("Oscillating"), // index 0
				"\u21d2 "+loc.getPlain("Increasing"), // index 1
				"\u21d0 "+loc.getPlain("Decreasing"), // index 2
				"\u21d2 "+loc.getPlain("IncreasingOnce")); // index 3);
	}

	public void applyTypeChanges(int value) {
		applyChanges(value);
	}

	public void applySpeedChanges(NumberValue value) {
		for (int i=0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setAnimationSpeedObject(value);
			geo.updateCascade();
		}

		kernel.udpateNeedToShowAnimationButton();
		kernel.notifyRepaint();

	}
	@Override
	protected void apply(int index, int value) {
		GeoElement geo = getGeoAt(index);
		geo.setAnimationType(value);
		geo.updateRepaint();
	}


	@Override
	public int getValueAt(int index) {
		// not used
		return -1;
	}

	public boolean isShowSliders() {
		return showSliders;
	}

	public void setShowSliders(boolean showSliders) {
		this.showSliders = showSliders;
	}

}
