package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.debug.Log;

public class LineStyleModel extends OptionsModel {
	public interface ILineStyleListener extends PropertyListener {
		void setThicknessSliderValue(int value);

		void setThicknessSliderMinimum(int minimum);

		void setOpacitySliderValue(int value);

		void selectCommonLineStyle(boolean equalStyle, int type);

		void setLineTypeVisible(boolean value); 
		
		void setLineStyleHiddenVisible(boolean value);

		void selectCommonLineStyleHidden(boolean equalStyle, int type);

		void setLineOpacityVisible(boolean value);
	}

	private ILineStyleListener listener;

	public void setListener(ILineStyleListener listener) {
		this.listener = listener;
	}

	private boolean lineTypeEnabled;
	private boolean lineStyleHiddenEnabled;
	private boolean lineOpacityEnabled;

	private static Integer[] lineStyleArray=null;

	public static void initStyleArray() {
		if (lineStyleArray == null) {
			lineStyleArray = getLineTypes();
		}

	}

	public LineStyleModel(App app) {
		super(app);
	}


	private static final Integer[] getLineTypes() {
		Integer[] ret = {
				Integer.valueOf(EuclidianStyleConstants.LINE_TYPE_FULL),
				Integer.valueOf(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG),
				Integer.valueOf(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT),
				Integer.valueOf(EuclidianStyleConstants.LINE_TYPE_DOTTED),
				Integer.valueOf(
						EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED),
				Integer.valueOf(EuclidianStyleConstants.LINE_TYPE_POINTWISE) };
		return ret;
	}

	public static final Integer getStyleAt(int i) {
		initStyleArray();
		return lineStyleArray[i];
	}

	public static final Integer getStyleCount() {
		initStyleArray();
		return lineStyleArray.length;
	}
	private int maxMinimumThickness() {

		if (!hasGeos()) {
			return 1;
		}

		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement testGeo = getGeoAt(i)
					.getGeoElementForPropertiesDialog();
			if (testGeo.getMinimumLineThickness() == 1) {
				return 1;
			}
		}

		return 0;

	}


	@Override
	public void updateProperties() {
		GeoElement temp, geo0 = getGeoAt(0);
		Log.debug("geo0 = " + geo0 + ", lineTypeEnabled="+lineTypeEnabled);
		if (listener != null) {
			listener.setThicknessSliderValue(geo0.getLineThickness());
			// allow polygons to have thickness 0
			listener.setThicknessSliderMinimum(maxMinimumThickness());
			int opacity = (int) ((geo0.getLineOpacity() / 255.0f) * 100);
			listener.setOpacitySliderValue(opacity);
			listener.setLineTypeVisible(lineTypeEnabled);
			listener.setLineStyleHiddenVisible(lineStyleHiddenEnabled);
			listener.setLineOpacityVisible(lineOpacityEnabled);
		}
		// check if geos have same line style
		if (lineTypeEnabled) {
			boolean equalStyle = true;
			int type0 = geo0.getLineType();
			for (int i = 1; i < getGeosLength(); i++) {
				temp = getGeoAt(i);
				// same style?
				if (type0 != temp.getLineType())
					equalStyle = false;
			}

			if (listener != null) {
				listener.selectCommonLineStyle(equalStyle, type0);
			}
		}

		// check if geos have same line style
		if (lineStyleHiddenEnabled) {
			boolean equalStyle = true; 
			int type0 = geo0.getLineTypeHidden();
			for (int i = 1; i < getGeosLength(); i++) { 
				temp = getGeoAt(i); 
				// same style? 
				if (type0 != temp.getLineTypeHidden())
					equalStyle = false; 
			} 

			if (listener != null) { 
				listener.selectCommonLineStyleHidden(equalStyle, type0);
			} 		
		}



	}

	public void applyThickness(int value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setLineThickness(value);
			geo.updateVisualStyleRepaint(GProperty.LINE_STYLE);
		}
		storeUndoInfo();
	}

	public void applyLineType(int type) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setLineType(type);
			geo.updateVisualStyleRepaint(GProperty.LINE_STYLE);
		}
		storeUndoInfo();
	}

	public void applyLineStyleHidden(int type) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setLineTypeHidden(type);
			geo.updateVisualStyleRepaint(GProperty.LINE_STYLE);
		}
		storeUndoInfo();
	}


	public void applyOpacity(int value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setLineOpacity(value);
			geo.updateVisualStyleRepaint(GProperty.LINE_STYLE);
		}
		storeUndoInfo();
	}

	public void applyLineTypeFromIndex(int index) {
		applyLineType(lineStyleArray[index]);
	}


	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index)
				.getGeoElementForPropertiesDialog();
		return (geo.showLineProperties() || geo.isNumberValue()) && !geo.isGeoBoolean()
				&& !((geo instanceof GeoNumeric) && 
						((GeoNumeric)geo).isSlider());
	}

	@Override
	public boolean checkGeos() {
		boolean geosOK = true;
		lineTypeEnabled = true;
		lineStyleHiddenEnabled = true;
		lineOpacityEnabled = true;
		for (int i = 0; i < getGeosLength(); i++) {
			if (!isValidAt(i)) {
				geosOK = false;

				break;
			}
			
			GeoElement geo = getGeoAt(i);
			if (i == 0) {
				lineStyleHiddenEnabled = geo.getKernel().getApplication()
						.isEuclidianView3Dinited();
			}
			if ((geo instanceof GeoNumeric) && ((GeoNumeric)geo).isSlider() ) {
				lineTypeEnabled = false;
				lineStyleHiddenEnabled = false;
				lineOpacityEnabled = false;
			}
		}
		return geosOK;
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}
}
