package org.geogebra.web.web.gui.util;

import java.util.HashMap;

import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.web.html5.main.AppW;

public class LineStylePopup extends PopupMenuButtonW  {

	private static final int DEFAULT_SIZE = 2;

	private int mode;
	private ImageOrText[] lineStyleIcons = null;

	public static HashMap<Integer, Integer> getLineStyleMap(int iconHeight) {

		Integer styleCount = LineStyleModel.getStyleCount();
		
		HashMap<Integer, Integer> lineStyleMap0 = new HashMap<Integer, Integer>();

		for (int i = 0; i < styleCount; i++) {
			lineStyleMap0.put(LineStyleModel.getStyleAt(i), i);
		}

		return lineStyleMap0;

	}

	public static ImageOrText[] getLineStyleIcons(int iconHeight) {

		Integer styleCount = LineStyleModel.getStyleCount();
		ImageOrText[] lineStyleIcons0 = new ImageOrText[styleCount];
		
		for (int i = 0; i < styleCount; i++) {
			lineStyleIcons0[i] = GeoGebraIconW.createLineStyleIcon(i);
		}
		
		return lineStyleIcons0;

	}
	public static LineStylePopup create(AppW app, int iconHeight, int mode, boolean hasSlider) {

		ImageOrText[] lineStyleIcons0 = getLineStyleIcons(iconHeight);

		HashMap<Integer, Integer> lineStyleMap0 = getLineStyleMap(iconHeight);

		LineStylePopup ret = new LineStylePopup(app, lineStyleIcons0, -1,
				6, SelectionTable.MODE_ICON,
				true, hasSlider, lineStyleMap0);
		ret.setMode(mode);
		// ret.fillData(iconHeight);
		return ret;
	}

	public LineStylePopup(AppW app, ImageOrText[] data, Integer rows,
			Integer columns, SelectionTable mode,
			boolean hasTable, boolean hasSlider,
			HashMap<Integer, Integer> lineStyleMap0) {
		super(app, data, rows, columns, mode, hasTable, hasSlider,
				lineStyleMap0);

	}

	
	public void selectLineType(int type) {
		setSelectedIndex(getLineStyleMap().get(type));
	}


	private HashMap<Integer, Integer> getLineStyleMap() {
		return lineStyleMap;
	}

	@Override
	public ImageOrText getButtonIcon() {
		if (getSelectedIndex() > -1) {
			return GeoGebraIconW.createLineStyleIcon(getSelectedIndex());

		}

		return new ImageOrText();
	}

	@Override 
	public int getSliderValue() {
		int val = super.getSliderValue();
		return val == -1 ? DEFAULT_SIZE : val;
	}

	public void setMode(int mode) {
		this.mode = mode;
    }
}


