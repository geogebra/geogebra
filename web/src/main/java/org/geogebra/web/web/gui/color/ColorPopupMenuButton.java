package org.geogebra.web.web.gui.color;

import java.util.HashMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.util.GeoGebraIconW;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.PopupMenuButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class ColorPopupMenuButton extends PopupMenuButton
		implements ClickHandler {

	private static final int NUM_OF_BUTTONS = 27;
	public static final int COLORSET_DEFAULT = 0;
	public static final int COLORSET_BGCOLOR = 1;
	private int colorSetType;
	private GColor[] colorSet;
	private GColor defaultColor;
	private HashMap<String, Integer> lookupMap;

	private boolean enableTable;
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
	public ColorPopupMenuButton(AppW app, GDimensionW iconSize,
			int colorSetType, boolean hasSlider) {

		super(app, createDummyIcons(10), -1, 5, SelectionTable.MODE_ICON);
		this.app = app;
		this.iconSize = iconSize;
		this.colorSetType = colorSetType;
		this.hasSlider = hasSlider;
		colorSet = GeoGebraColorConstants.getSimplePopupArray(colorSetType);
		defaultColor = colorSet[0];

		lookupMap = new HashMap<String, Integer>();
		for (int i = 0; i < colorSet.length; i++) {
			if (colorSet[i] != null) {
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
		showSlider(hasSlider);
	}

	protected void updateColorTable() {
		getMyTable().populateModel(getColorSwatchIcons(colorSet,
				getSliderValue() / 100f, iconSize));
	}

	@Override
	public ImageOrText getButtonIcon() {
		ImageOrText icon = super.getButtonIcon();
		if (icon == null) {
			icon = GeoGebraIconW.createColorSwatchIcon(getSliderValue() / 100f,
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

		if (color == null && colorSetType == COLORSET_BGCOLOR) {
			index = colorSet.length - 1;
			return index;
		}

		if (lookupMap.containsKey(GColor.getColorString(color))) {
			index = lookupMap.get(GColor.getColorString(color));
		}

		return index;
	}

	/**
	 * @return the selected {@link GColor color}
	 */
	public GColor getSelectedColor() {
		if (!enableTable) {
			return null;
		}
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

	protected void setDefaultColor(float alpha, GColor gc) {
		defaultColor = gc;
		if (gc != null) {
			this.setIcon(GeoGebraIconW.createColorSwatchIcon(alpha, gc, null));
			this.getElement().getStyle().setBorderColor(gc.toString());
		} else {
			this.setIcon(GeoGebraIconW.createNullSymbolIcon(iconSize.getWidth(),
					iconSize.getHeight()));
			this.getElement().getStyle()
					.setBorderColor(GColor.BLACK.toString());
		}
	}

	private String[] getToolTipArray() {
		String[] toolTipArray = new String[colorSet.length];
		for (int i = 0; i < toolTipArray.length; i++) {
			if (colorSet[i] == null) {
				toolTipArray[i] = app.getMenu("Transparent");
			} else {
				toolTipArray[i] = GeoGebraColorConstants
						.getGeogebraColorName(app, colorSet[i]);
			}
		}
		return toolTipArray;
	}

	private static ImageOrText[] getColorSwatchIcons(GColor[] colorArray,
			float alpha, GDimensionW iconSize) {
		ImageOrText[] a = new ImageOrText[colorArray.length];
		for (int i = 0; i < colorArray.length; i++)
			if (colorArray[i] != null) {
				a[i] = GeoGebraIconW.createColorSwatchIcon(alpha, colorArray[i],
						null);
			} else {
				a[i] = new ImageOrText(AppResources.INSTANCE.more());
			}
		return a;
	}

	private static ImageOrText[] createDummyIcons(int count) {

		ImageOrText[] a = new ImageOrText[count];
		for (int i = 0; i < count; i++) {
			a[i] = new ImageOrText();
		}
		return a;
	}

	public void onClick(ClickEvent event) {
		if (this.hasSlider) {
			Integer si = getSelectedIndex();
			defaultColor = getSelectedColor();
			updateColorTable();
			setSelectedIndex(si);
		}
	}

	@Override
	protected void fireActionPerformed() {
		if (this.hasSlider) {
			Integer si = getSelectedIndex();
			defaultColor = getSelectedColor();
			updateColorTable();
			setSelectedIndex(si);
		}
	}

	public boolean isEnableTable() {
		return enableTable;
	}

	public void setEnableTable(boolean enableTable) {
		this.enableTable = enableTable;
		getMyTable().setVisible(enableTable);
	}

}
