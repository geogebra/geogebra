package org.geogebra.web.web.gui.color;

import java.util.HashMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.util.GeoGebraIcon;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.PopupMenuButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class ColorPopupMenuButton extends PopupMenuButton implements ClickHandler {

	private static final int NUM_OF_BUTTONS = 27;
	public static final int COLORSET_DEFAULT = 0;
	public static final int COLORSET_BGCOLOR = 1;
	private int colorSetType;
	private org.geogebra.common.awt.GColor[]  colorSet; 
	private org.geogebra.common.awt.GColor defaultColor;
	private HashMap<String,Integer> lookupMap; 

	private boolean hasSlider;
	private GDimensionW iconSize;

	/**
	 * @param app
	 *            {@link AppW}
	 * @param iconSize
	 *            {@link GDimensionW}
	 * @param colorSetType
	 *            {@code int}
	 * @param hasSlider
	 *            {@code boolean}
	 */
	public ColorPopupMenuButton(AppW app, GDimensionW iconSize, int colorSetType, boolean hasSlider) {

		super(app, createDummyIcons(), -1, 9,
		        org.geogebra.common.gui.util.SelectionTable.MODE_ICON);
		this.app = app;
		this.iconSize = iconSize;
		this.colorSetType = colorSetType;
		this.hasSlider = hasSlider;
		colorSet = getColorArray(colorSetType);
		defaultColor = colorSet[0];

		lookupMap = new HashMap<String,Integer>();
		for(int i = 0; i < colorSet.length; i++) {
			if (colorSet[i] != null){
				lookupMap.put(GColor.getColorString(colorSet[i]), i);
			}
		}

		getMySlider().setMinimum(0);
		getMySlider().setMaximum(100);
		getMySlider().setMajorTickSpacing(25);
		getMySlider().setMinorTickSpacing(5);
		setSliderValue(100);
		setSliderVisible(hasSlider);
		updateColorTable();	
		setKeepVisible(false);
		getMyTable().removeDefaultStyle();
	}

	/**
	 * @param visible
	 *            {@code boolean}
	 */
	protected void setSliderVisible(boolean visible) {
		hasSlider = visible;
		getMySlider().setVisible(hasSlider);
	}

	protected void updateColorTable() {
		getMyTable()
		        .populateModel(
		                getColorSwatchIcons(colorSet, getSliderValue() / 100f,
		                        iconSize));
	}

	@Override
	public ImageOrText getButtonIcon() {
		ImageOrText icon = super.getButtonIcon();
		if (icon == null) {
			icon = GeoGebraIcon.createColorSwatchIcon(getSliderValue() / 100f,
			        defaultColor, null);
		}
		return icon;
	}

	/**
	 * @param color
	 *            {@link GColor}
	 * @return {@code int} the index of the given color
	 */
	protected int getColorIndex(GColor color) {
		int index = -1;

		if(color == null && colorSetType == COLORSET_BGCOLOR){
			index = colorSet.length - 1;
			return index;
		}
				
		if(lookupMap.containsKey(GColor.getColorString(color))){
			index = lookupMap.get(GColor.getColorString(color));
		}

		return index;
	}


	/**
	 * @return the selected {@link GColor color}
	 */
	public org.geogebra.common.awt.GColor getSelectedColor(){
		int index = getSelectedIndex();
		if(index <= -1) {
			return defaultColor;
		} else if (colorSetType == COLORSET_BGCOLOR && index > colorSet.length-1) {
			return null;
		} else {
			return colorSet[index];
		}
	}

	private static org.geogebra.common.awt.GColor[] getColorArray(int colorSetType){
		return GeoGebraColorConstants.getPopupArray(colorSetType);
	}

	protected void setDefaultColor(float alpha, org.geogebra.common.awt.GColor gc) {
		defaultColor = gc;
		if(gc!=null){
			this.setIcon(GeoGebraIcon.createColorSwatchIcon(alpha, gc, null));
			this.getElement().getStyle().setBorderColor(gc.toString());
		}
		else {
			this.setIcon(GeoGebraIcon.createNullSymbolIcon(iconSize.getWidth(), iconSize.getHeight()));
			this.getElement().getStyle().setBorderColor(GColor.BLACK.toString());
		}
	}

	private String[] getToolTipArray(){
		String[] toolTipArray = new String[colorSet.length];
		for(int i=0; i<toolTipArray.length; i++){
			if(colorSet[i]==null) {
				toolTipArray[i] = app.getMenu("Transparent");
			} else {
				toolTipArray[i] = GeoGebraColorConstants.getGeogebraColorName(app, colorSet[i]);
			}
		}
		return toolTipArray;
	}

	private static ImageOrText[] getColorSwatchIcons(
	        org.geogebra.common.awt.GColor[] colorArray, float alpha,
	        GDimensionW iconSize) {
		ImageOrText[] a = new ImageOrText[colorArray.length];
		for(int i = 0; i < colorArray.length; i++)
			if(colorArray[i] != null) {
				a[i] = GeoGebraIcon.createColorSwatchIcon(alpha, colorArray[i],
				        null);
			} else {
				a[i] = GeoGebraIcon.createNullSymbolIcon(iconSize.getWidth(), iconSize.getHeight());
			}
		return a;
	}

	private static ImageOrText[] createDummyIcons() {

		ImageOrText[] a = new ImageOrText[NUM_OF_BUTTONS];
		for (int i = 0; i < NUM_OF_BUTTONS; i++) {
			a[i] = new ImageOrText();
		}
		return a;
	}

	public void onClick(ClickEvent event) {
		if(this.hasSlider) {
			Integer si = getSelectedIndex();
			defaultColor = getSelectedColor();			
			updateColorTable();
			setSelectedIndex(si);
		}
    }
	
	@Override
    protected void fireActionPerformed() {
		if(this.hasSlider) {
			Integer si = getSelectedIndex();
			defaultColor = getSelectedColor();			
			updateColorTable();
			setSelectedIndex(si);
		}
    }
}
