package org.geogebra.web.web.gui.util;

import java.util.HashMap;

import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.web.html5.main.AppW;

public class LineStylePopup extends PopupMenuButton  {

	private static final int DEFAULT_SIZE = 2;

	private static HashMap<Integer, Integer> lineStyleMap;
	private int mode;
	private static ImageOrText [] lineStyleIcons = null;
	public static void fillData(int iconHeight) {
		
		
		Integer styleCount = LineStyleModel.getStyleCount();
		setLineStyleIcons(new ImageOrText[styleCount]);
		
		for (int i = 0; i < styleCount; i++)
			getLineStyleIcons()[i] = GeoGebraIcon.createLineStyleIcon(i);
		
		lineStyleMap = new HashMap<Integer, Integer>();
		
		for (int i = 0; i < styleCount; i++)
			lineStyleMap.put(LineStyleModel.getStyleAt(i), i);


	}
	public static LineStylePopup create(AppW app, int iconHeight, int mode, boolean hasSlider) {


		fillData(iconHeight);
		LineStylePopup ret = new LineStylePopup(app, getLineStyleIcons(), -1,
				6,
				 org.geogebra.common.gui.util.SelectionTable.MODE_ICON,
				true, hasSlider);
		ret.setMode(mode);
		return ret;
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
			return GeoGebraIcon.createLineStyleIcon(getSelectedIndex());

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

	public int getMode() {
	    return mode;
    }

	public void setMode(int mode) {
		this.mode = mode;
    }
}


