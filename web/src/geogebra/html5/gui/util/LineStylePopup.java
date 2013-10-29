package geogebra.html5.gui.util;

import geogebra.common.gui.dialog.options.model.LineStyleModel;
import geogebra.common.gui.util.SelectionTable;
import geogebra.html5.awt.GDimensionW;
import geogebra.web.gui.util.GeoGebraIcon;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.main.AppW;

import java.util.HashMap;

import com.google.gwt.canvas.dom.client.ImageData;

public class LineStylePopup extends PopupMenuButton  {

	private static final int DEFAULT_SIZE = 2;

	private static HashMap<Integer, Integer> lineStyleMap;
	private static int mode;
	private static GDimensionW lineStyleIconSize;
	private static ImageData [] lineStyleIcons = null;
	public static void fillData(int iconHeight) {
		
		setLineStyleIconSize(new GDimensionW(80, iconHeight));
		
		Integer styleCount = LineStyleModel.getStyleCount();
		setLineStyleIcons(new ImageData[styleCount]);
		
		for (int i = 0; i < styleCount; i++)
			getLineStyleIcons()[i] = GeoGebraIcon.createLineStyleIcon(
					LineStyleModel.getStyleAt(i), 2, getLineStyleIconSize(), geogebra.common.awt.GColor.BLACK, null);
		
		lineStyleMap = new HashMap<Integer, Integer>();
		
		for (int i = 0; i < styleCount; i++)
			lineStyleMap.put(LineStyleModel.getStyleAt(i), i);


	}
	public static LineStylePopup create(AppW app, int iconHeight, int mode, boolean hasSlider) {

		LineStylePopup.setMode(mode);
		fillData(iconHeight);
		return new LineStylePopup((AppW) app, getLineStyleIcons(), -1, 1,
				getLineStyleIconSize(), geogebra.common.gui.util.SelectionTable.MODE_ICON,
				true, hasSlider);
	}

	private GDimensionW iconSize;

	public LineStylePopup(AppW app, Object[] data, Integer rows,
			Integer columns, GDimensionW iconSize, SelectionTable mode,
			boolean hasTable, boolean hasSlider) {
		super(app, data, rows, columns, iconSize, mode, hasTable, hasSlider);
		this.iconSize = iconSize;

	}

	
	public void selectLineType(int type) {
		setSelectedIndex(lineStyleMap.get(type));
	}


	@Override
	public ImageData getButtonIcon() {
		if (getSelectedIndex() > -1) {
			return GeoGebraIcon.createLineStyleIcon(
					LineStyleModel.getStyleAt(getSelectedIndex()),
					this.getSliderValue(), iconSize,
					geogebra.common.awt.GColor.BLACK, null);

		}

		return GeoGebraIcon.createEmptyIcon(iconSize.getWidth(),
				iconSize.getHeight());
	}

	@Override 
	public int getSliderValue() {
		int val = super.getSliderValue();
		return val == -1 ? DEFAULT_SIZE : val;
	}
	public static GDimensionW getLineStyleIconSize() {
	    return lineStyleIconSize;
    }
	public static void setLineStyleIconSize(GDimensionW lineStyleIconSize) {
	    LineStylePopup.lineStyleIconSize = lineStyleIconSize;
    }
	public static ImageData [] getLineStyleIcons() {
		return lineStyleIcons;
    }
	public static void setLineStyleIcons(ImageData [] lineStyleIcons) {
	    LineStylePopup.lineStyleIcons = lineStyleIcons;
    }
	public static int getMode() {
	    return mode;
    }
	public static void setMode(int mode) {
	    LineStylePopup.mode = mode;
    }
}


