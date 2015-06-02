package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.EuclidianStyleConstants;

public class LineStyleModel extends OptionsModel {
	public interface ILineStyleListener {
		void setThicknessSliderValue(int value);

		void setThicknessSliderMinimum(int minimum);

		void setOpacitySliderValue(int value);

		void selectCommonLineStyle(boolean equalStyle, int type);

		void setLineTypeVisible(boolean value); 
		
		void setLineOpacityVisible(boolean value);

		Object updatePanel(Object[] geos2);
	}

	private ILineStyleListener listener;
	private boolean lineTypeEnabled;
	private boolean lineOpacityEnabled;

	private static Integer[] lineStyleArray=null;

	public static void initStyleArray() {
		if (lineStyleArray == null) {
			lineStyleArray = getLineTypes();
		}

	}
	public LineStyleModel(ILineStyleListener listener) {
		this.listener = listener;

	}

	private static final Integer[] getLineTypes() {
		Integer[] ret = { new Integer(EuclidianStyleConstants.LINE_TYPE_FULL),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DOTTED),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED) };
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
		if (listener != null) {
			listener.setThicknessSliderValue(geo0.getLineThickness());
			// allow polygons to have thickness 0
			listener.setThicknessSliderMinimum(maxMinimumThickness());
			int opacity = (int) ((geo0.getLineOpacity() / 255.0f) * 100);
			listener.setOpacitySliderValue(opacity);
			listener.setLineTypeVisible(lineTypeEnabled);
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

	}

	public void applyThickness(int value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setLineThickness(value);
			geo.updateVisualStyleRepaint();
		}
	}

	public void applyLineType(int type) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setLineType(type);
			geo.updateVisualStyleRepaint();
		}
	}

	public void applyOpacity(int value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setLineOpacity(value);
			geo.updateVisualStyleRepaint();
		}
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
		lineOpacityEnabled = true;
		for (int i = 0; i < getGeosLength(); i++) {
			if (!isValidAt(i)) {
				geosOK = false;

				break;
			}
			
			GeoElement geo = getGeoAt(i);
			if ((geo instanceof GeoNumeric) && ((GeoNumeric)geo).isSlider() ) {
				lineTypeEnabled = false;
				lineOpacityEnabled = false;
			}
		}
		return geosOK;
	}

	@Override
	public boolean updatePanel(Object[] geos2) {
		return listener.updatePanel(geos2) != null;
	}
}
