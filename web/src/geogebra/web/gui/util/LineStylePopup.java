package geogebra.web.gui.util;

import geogebra.common.gui.dialog.options.model.LineStyleModel;
import geogebra.common.gui.util.SelectionTable;
import geogebra.html5.main.AppW;

import java.util.HashMap;

public class LineStylePopup extends PopupMenuButton  {

	private static final int DEFAULT_SIZE = 2;

	private static HashMap<Integer, Integer> lineStyleMap;
	private static int mode;
	private static ImageOrText [] lineStyleIcons = null;
	public static void fillData(int iconHeight) {
		
		
		Integer styleCount = LineStyleModel.getStyleCount();
		setLineStyleIcons(new ImageOrText[styleCount]);
		
		for (int i = 0; i < styleCount; i++)
			getLineStyleIcons()[i] = GeoGebraIcon.createLineStyleIcon(
					i, 2,  geogebra.common.awt.GColor.BLACK, null);
		
		lineStyleMap = new HashMap<Integer, Integer>();
		
		for (int i = 0; i < styleCount; i++)
			lineStyleMap.put(LineStyleModel.getStyleAt(i), i);


	}
	public static LineStylePopup create(AppW app, int iconHeight, int mode, boolean hasSlider) {

		LineStylePopup.setMode(mode);
		fillData(iconHeight);
		return new LineStylePopup(app, getLineStyleIcons(), -1, 5,
				 geogebra.common.gui.util.SelectionTable.MODE_ICON,
				true, hasSlider);
	}

	public LineStylePopup(AppW app, ImageOrText[] data, Integer rows,
			Integer columns, SelectionTable mode,
			boolean hasTable, boolean hasSlider) {
		super(app, data, rows, columns, mode, hasTable, hasSlider);

	}

	
	public void selectLineType(int type) {
		setSelectedIndex(lineStyleMap.get(type));
	}


	@Override
	public ImageOrText getButtonIcon() {
		if (getSelectedIndex() > -1) {
			return GeoGebraIcon.createLineStyleIcon(
					getSelectedIndex(),
					this.getSliderValue(), 
					geogebra.common.awt.GColor.BLACK, null);

		}

		return new ImageOrText();
	}

	@Override 
	public int getSliderValue() {
		int val = super.getSliderValue();
		return val == -1 ? DEFAULT_SIZE : val;
	}
	
	public static ImageOrText [] getLineStyleIcons() {
		return lineStyleIcons;
    }
	public static void setLineStyleIcons(ImageOrText [] lineStyleIcons) {
	    LineStylePopup.lineStyleIcons = lineStyleIcons;
    }
	public static int getMode() {
	    return mode;
    }
	public static void setMode(int mode) {
	    LineStylePopup.mode = mode;
    }
}


