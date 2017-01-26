/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package org.geogebra.desktop.gui.color;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.ImageIcon;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.gui.util.PopupMenuButtonD;
import org.geogebra.desktop.main.AppD;

public class ColorPopupMenuButton extends PopupMenuButtonD
		implements ActionListener {

	private static final long serialVersionUID = 1L;

	private AppD app;

	public static final int COLORSET_DEFAULT = 0;
	public static final int COLORSET_BGCOLOR = 1;
	private int colorSetType;
	private GColor[] colorSet;
	private GColor defaultColor;
	private HashMap<GColor, Integer> lookupMap;

	private boolean hasSlider;
	private Dimension iconSize;

	public ColorPopupMenuButton(AppD app, Dimension iconSize, int colorSetType,
			boolean hasSlider) {

		super(app, createDummyIcons(iconSize), -1, 9, iconSize,
				SelectionTable.MODE_ICON);
		this.app = app;
		this.iconSize = iconSize;
		this.colorSetType = colorSetType;
		this.hasSlider = hasSlider;
		colorSet = getColorArray(colorSetType);
		defaultColor = colorSet[0];

		lookupMap = new HashMap<GColor, Integer>();
		for (int i = 0; i < colorSet.length; i++) {
			lookupMap.put((colorSet[i]), i);
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

	public void updateColorTable() {
		getMyTable().populateModel(getColorSwatchIcons(colorSet,
				getSliderValue() / 100f, iconSize, colorSetType));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.hasSlider) {
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
			icon = GeoGebraIconD.createColorSwatchIcon(getSliderValue() / 100f,
					iconSize, GColorD.getAwtColor(defaultColor), null);
		}
		return icon;
	}

	public int getColorIndex(GColor color) {
		int index = -1;

		if (color == null && colorSetType == COLORSET_BGCOLOR) {
			index = colorSet.length - 1;
			return index;
		}

		if (lookupMap.containsKey(color)) {
			index = lookupMap.get(color);
		}

		return index;
	}

	public GColor getSelectedColor() {
		int index = getSelectedIndex();
		if (index <= -1) {
			return defaultColor;
		} else if (colorSetType == COLORSET_BGCOLOR
				&& index > colorSet.length - 1) {
			return null;
		} else {
			return colorSet[index];
		}
	}

	private static GColor[] getColorArray(int colorSetType) {
		return GeoGebraColorConstants.getPopupArray(colorSetType);
	}

	public void setDefaultColor(double alpha, GColor gc) {
		defaultColor = gc;
		if (gc != null) {
			this.setIcon(GeoGebraIconD.createColorSwatchIcon(alpha, iconSize,
					GColorD.getAwtColor(gc), null));
		} else {
			this.setIcon(GeoGebraIconD.createNullSymbolIcon(iconSize.width,
					iconSize.height));
		}
	}

	private String[] getToolTipArray() {
		String[] toolTipArray = new String[colorSet.length];
		for (int i = 0; i < toolTipArray.length; i++) {
			if (colorSet[i] == null) {
				toolTipArray[i] = app.getLocalization().getMenu("Transparent");
			} else {
				toolTipArray[i] = GeoGebraColorConstants
						.getGeogebraColorName(app, colorSet[i]);
			}
		}
		return toolTipArray;
	}

	private static ImageIcon[] getColorSwatchIcons(GColor[] colorArray,
			float alpha, Dimension iconSize, int colorSetType) {
		ImageIcon[] a = new ImageIcon[colorArray.length];
		for (int i = 0; i < colorArray.length; i++) {
			if (colorArray[i] != null) {
				a[i] = GeoGebraIconD.createColorSwatchIcon(alpha, iconSize,
						GColorD.getAwtColor(colorArray[i]), null);
			} else {
				a[i] = GeoGebraIconD.createNullSymbolIcon(iconSize.width,
						iconSize.height);
			}
		}
		return a;
	}

	private static ImageIcon[] createDummyIcons(Dimension iconSize) {

		ImageIcon[] a = new ImageIcon[27];
		for (int i = 0; i < 27; i++) {
			a[i] = GeoGebraIconD.createEmptyIcon(iconSize.width,
					iconSize.height);
		}
		return a;
	}

}
