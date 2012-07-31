/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.gui.color;

import geogebra.common.main.GeoGebraColorConstants;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.main.AppD;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.ImageIcon;

public class ColorPopupMenuButton extends PopupMenuButton implements ActionListener{

	private static final long serialVersionUID = 1L;

	private AppD app;

	public static final int COLORSET_DEFAULT = 0;
	public static final int COLORSET_BGCOLOR = 1;
	private int colorSetType;
	private geogebra.common.awt.GColor[]  colorSet; 
	private geogebra.common.awt.GColor defaultColor;
	private HashMap<Color,Integer> lookupMap; 

	private boolean hasSlider;
	private Dimension iconSize;

	public ColorPopupMenuButton(AppD app, Dimension iconSize, int colorSetType, boolean hasSlider) {

		super(app, createDummyIcons(iconSize), -1, 9, iconSize, geogebra.common.gui.util.SelectionTable.MODE_ICON);
		this.app = app;
		this.iconSize = iconSize;
		this.colorSetType = colorSetType;
		this.hasSlider = hasSlider;
		colorSet = getColorArray(colorSetType);
		defaultColor = colorSet[0];

		lookupMap = new HashMap<Color,Integer>();
		for(int i = 0; i < colorSet.length; i++) {
			lookupMap.put(geogebra.awt.GColorD.getAwtColor(colorSet[i]), i);
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
		addActionListener(this);
	}

	public void updateColorTable(){
		getMyTable().populateModel(getColorSwatchIcons(colorSet, getSliderValue()/100f, iconSize, colorSetType));
	}

	public void actionPerformed(ActionEvent e){
		if(this.hasSlider) {
			Integer si = getSelectedIndex();
			defaultColor = getSelectedColor();			
			updateColorTable();
			setSelectedIndex(si);
		}
	}

	@Override
	public ImageIcon getButtonIcon() {
		ImageIcon icon = super.getButtonIcon();
		if (icon == null && this.hasSlider) {
			icon = GeoGebraIcon.createColorSwatchIcon( getSliderValue()/100f, iconSize, geogebra.awt.GColorD.getAwtColor(defaultColor), null);
		}
		return icon;
	}

	public int getColorIndex(Color color){
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
			this.setIcon(GeoGebraIcon.createColorSwatchIcon( alpha, iconSize, geogebra.awt.GColorD.getAwtColor(gc), null));
		else
			this.setIcon(GeoGebraIcon.createNullSymbolIcon(iconSize.width, iconSize.height));
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

	private static ImageIcon[] getColorSwatchIcons(geogebra.common.awt.GColor[] colorArray, float alpha, Dimension iconSize, int colorSetType){
		ImageIcon[] a = new ImageIcon[colorArray.length];
		for(int i = 0; i < colorArray.length; i++)
			if(colorArray[i] != null) {
				a[i] = GeoGebraIcon.createColorSwatchIcon( alpha,  iconSize, geogebra.awt.GColorD.getAwtColor(colorArray[i]) , null);
			} else {
				a[i] = GeoGebraIcon.createNullSymbolIcon(iconSize.width, iconSize.height);
			}
		return a;
	}

	private static  ImageIcon[] createDummyIcons( Dimension iconSize){

		ImageIcon[] a = new ImageIcon[27];
		for(int i = 0; i < 27; i++) {
			a[i] = GeoGebraIcon.createEmptyIcon(iconSize.width, iconSize.height);
		}
		return a;
	}

}
