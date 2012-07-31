package geogebra.web.gui.color;

import geogebra.common.awt.GColor;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.web.awt.GDimensionW;
import geogebra.web.gui.util.GeoGebraIcon;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.main.AppW;

import java.util.HashMap;

import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class ColorPopupMenuButton extends PopupMenuButton implements ClickHandler {
	
	private AppW app;

	public static final int COLORSET_DEFAULT = 0;
	public static final int COLORSET_BGCOLOR = 1;
	private int colorSetType;
	private geogebra.common.awt.GColor[]  colorSet; 
	private geogebra.common.awt.GColor defaultColor;
	private HashMap<GColor,Integer> lookupMap; 

	private boolean hasSlider;
	private GDimensionW iconSize;

	public ColorPopupMenuButton(AppW app, GDimensionW iconSize, int colorSetType, boolean hasSlider) {

		super(app, createDummyIcons(iconSize), -1, 9, iconSize, geogebra.common.gui.util.SelectionTable.MODE_ICON);
		this.app = app;
		this.iconSize = iconSize;
		this.colorSetType = colorSetType;
		this.hasSlider = hasSlider;
		colorSet = getColorArray(colorSetType);
		defaultColor = colorSet[0];

		lookupMap = new HashMap<GColor,Integer>();
		for(int i = 0; i < colorSet.length; i++) {
			lookupMap.put(colorSet[i], i);
		}

		setToolTipArray(getToolTipArray());

		getMyTable().setUseColorSwatchBorder(true);
		getMySlider().setMinimum(0);
		getMySlider().setMaximum(100);
		getMySlider().setMajorTickSpacing(25);
		getMySlider().setMinorTickSpacing(5);
		setSliderValue(100);
		getMySlider().setVisible(hasSlider);

		updateColorTable();	
		//addActionListener(this);
	}

	public void updateColorTable(){
		getMyTable().populateModel(getColorSwatchIcons(colorSet, getSliderValue()/100f, iconSize, colorSetType));
	}

	@Override
	public ImageData getButtonIcon() {
		ImageData icon = super.getButtonIcon();
		if (icon == null && this.hasSlider) {
			icon = GeoGebraIcon.createColorSwatchIcon( getSliderValue()/100f, iconSize, defaultColor, null);
		}
		return icon;
	}

	public int getColorIndex(GColor color){
		int index = -1;

		if(color == null && colorSetType == COLORSET_BGCOLOR){
			index = colorSet.length - 1;
			return index;
		}

		if(lookupMap.containsKey(color)){
			index = lookupMap.get(color);
		}

		return index;
	}


	public geogebra.common.awt.GColor getSelectedColor(){
		int index = getSelectedIndex();
		if(index <= -1) {
			return defaultColor;
		} else if (colorSetType == COLORSET_BGCOLOR && index > colorSet.length-1) {
			return null;
		} else {
			return colorSet[index];
		}
	}

	private static geogebra.common.awt.GColor[] getColorArray(int colorSetType){
		return GeoGebraColorConstants.getPopupArray(colorSetType);
	}

	public void setDefaultColor(float alpha, geogebra.common.awt.GColor gc) {
		defaultColor = gc;
		if(gc!=null)
			this.setIcon(GeoGebraIcon.createColorSwatchIcon( alpha, iconSize,gc, null));
		else
			this.setIcon(GeoGebraIcon.createNullSymbolIcon(iconSize.getWidth(), iconSize.getHeight()));
	}

	/*public void setIcon(CanvasElement ic) {
	    super.setIcon(ic);
    }*/

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

	private static ImageData[] getColorSwatchIcons(geogebra.common.awt.GColor[] colorArray, float alpha, GDimensionW iconSize, int colorSetType){
		ImageData[] a = new ImageData[colorArray.length];
		for(int i = 0; i < colorArray.length; i++)
			if(colorArray[i] != null) {
				a[i] = GeoGebraIcon.createColorSwatchIcon( alpha,  iconSize, colorArray[i] , null);
			} else {
				a[i] = GeoGebraIcon.createNullSymbolIcon(iconSize.getWidth(), iconSize.getHeight());
			}
		return a;
	}

	private static  ImageData[] createDummyIcons( GDimensionW iconSize){

		ImageData[] a = new ImageData[27];
		for(int i = 0; i < 27; i++) {
			a[i] = GeoGebraIcon.createEmptyIcon(iconSize.getWidth(), iconSize.getHeight());
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
