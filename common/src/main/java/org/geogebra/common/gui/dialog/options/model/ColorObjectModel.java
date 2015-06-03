package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.util.StringUtil;


public class ColorObjectModel extends OptionsModel {
	public interface IColorObjectListener extends PropertyListener {

		void updateChooser(boolean equalObjColor,
				boolean equalObjColorBackground, boolean allFillable,
				boolean hasBackground,
				boolean hasAlpha);

		void updatePreview(GColor col, float alpha);

		boolean isBackgroundColorSelected();


		void updateNoBackground(GeoElement geo, GColor col, float alpha,
				boolean updateAlphaOnly, boolean allFillable);

	}

	private static final long serialVersionUID = 1L;
	private boolean allFillable;
	private boolean hasBackground;
	private boolean hasImageGeo;
	private IColorObjectListener listener;
	private Kernel kernel;
	private App app;

	public ColorObjectModel(App app) {
		this.app = app;
		kernel = app.getKernel();
	}

	public void setListener(IColorObjectListener listener) {
		this.listener = listener;
	}

	@Override
	public void updateProperties() {

		GeoElement geo0 = getGeoAt(0);


		// check geos for similar properties

		boolean equalObjColor = true;
		boolean equalObjColorBackground = true;
		hasImageGeo = geo0.isGeoImage();
		allFillable = geo0.isFillable();
		hasBackground = geo0.hasBackgroundColor();

		GeoElement temp;
		for (int i = 1; i < getGeosLength(); i++) {
			temp = getGeoAt(i);
			// same object color
			if (!geo0.getObjectColor().equals(temp.getObjectColor())) {
				equalObjColor = false;
			}
			// has fill color
			if (!temp.isFillable()) {
				allFillable = false;
			}
			// has background
			if (!temp.hasBackgroundColor()) {
				hasBackground = false;
			}
			// has image geo
			if (temp.isGeoImage()) {
				hasImageGeo = true;
			}
		}

		if (hasBackground) {
			equalObjColorBackground = true;

			if (geo0.getBackgroundColor() == null)
				// test for all null background color
				for (int i = 1; i < getGeosLength(); i++) {
					temp = getGeoAt(i);
					if (temp.getBackgroundColor() != null) {
						equalObjColorBackground = false;
						break;
					}
				}
			else
				// test for all same background color
				for (int i = 1; i < getGeosLength(); i++) {
					temp = getGeoAt(i);
					// same background color
					if (!geo0.getBackgroundColor().equals(
							temp.getBackgroundColor())) {
						equalObjColorBackground = false;
						break;
					}
				}
		}
		
		listener.updateChooser(equalObjColor, equalObjColorBackground, allFillable, hasBackground,
				hasOpacity(geo0));
	}

	protected boolean hasOpacity(GeoElement geo) {
		boolean hasOpacity = true;
		if (geo instanceof GeoButton) {
			hasOpacity = false;
		}
		return hasOpacity;
	}
	
	/**
	 * Sets color of selected GeoElements
	 */
	private void updateColor(GColor col, float alpha, boolean updateAlphaOnly) {
		if (col == null || getGeos() == null) {
			return;
		}	

		listener.updatePreview(col, alpha);
		
		GeoElement geo;
		for (int i = 0; i < getGeosLength(); i++) {
			geo = getGeoAt(i);

			if (hasBackground && listener.isBackgroundColorSelected()) {
				geo.setBackgroundColor(col);
			} else {
				listener.updateNoBackground(geo,col,alpha,updateAlphaOnly, allFillable);
			}
			
			geo.updateVisualStyle();
		}
		kernel.notifyRepaint();
	}


	/**
	 * Sets the background color of selected GeoElements to null
	 */
	public void clearBackgroundColor() {

		GeoElement geo;
		for (int i = 0; i < getGeosLength(); i++) {
			geo = getGeoAt(i);
			geo.setBackgroundColor(null);
			geo.updateVisualStyle();
		}
		kernel.notifyRepaint();
	}

	@Override
	public boolean checkGeos() {
		return true;
	}

	/**
	 * Listens for color chooser state changes
	 */
	public void applyChanges(GColor color, float alpha, boolean alphaOnly) {

		updateColor(color, alpha, alphaOnly);

	}

	public boolean hasImageGeo() {
		return hasImageGeo;
	}

	public boolean hasBackground() {
		return hasBackground;
	}

	@Override
	protected boolean isValidAt(int index) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static String getColorAsString(App app, GColor color) {
		String result = "";
		int blue = color.getBlue();
		String rgbDec = color.getRed() + ", " + color.getGreen() + ", " + blue;
		String name = GeoGebraColorConstants.getGeogebraColorName(app, color);
		if (name != null) {
			result = name + " " + rgbDec;
		} else {
			result = rgbDec;
		}
		
		result +=  " (" + StringUtil.toHtmlColor(color) + ")";
		return result;
	}
	
	/*
	 * public static String getColorAsString(App app, GColor color) {
	 * ColorObjectModel.app = app; return getColorAsString(color); }
	 */

	public void setSequential(boolean b) {
		GeoElement geo;
		for (int i = 0; i < getGeosLength(); i++) {
			geo = getGeoAt(i);
			geo.setSequentialColor(b);
			geo.updateVisualStyle();
		}
		kernel.notifyRepaint();

	}

	public boolean isSequentialColor() {
		for (int i = 0; i < getGeosLength(); i++) {
			if (!getGeoAt(i).isSequentialColor()) {
				return false;
			}
		}
		return getGeosLength() > 0;
	}

	public boolean hasDefaultGeos() {
		for (int i = 0; i < getGeosLength(); i++) {
			if (getGeoAt(i).isDefaultGeo()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	};
}
