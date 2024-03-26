package org.geogebra.web.full.gui.util;

import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;

/**
 * line style popup
 */
public class LineStylePopup extends PopupMenuButtonW  {

	private static final int DEFAULT_SIZE = 2;

	/**
	 * @return array of line style icons
	 */
	public static ImageOrText[] getLineStyleIcons() {
		int styleCount = LineStyleModel.getStyleCount();
		ImageOrText[] lineStyleIcons0 = new ImageOrText[styleCount];
		for (int i = 0; i < styleCount; i++) {
			lineStyleIcons0[i] = GeoGebraIconW.createLineStyleIcon(i);
		}
		return lineStyleIcons0;
	}

	/**
	 * @param app
	 *            application
	 * @return line style popup
	 */
	public static LineStylePopup create(AppW app) {
		ImageOrText[] lineStyleIcons0 = getLineStyleIcons();
		return new LineStylePopup(app, lineStyleIcons0, -1,
				LineStyleModel.getStyleCount(), SelectionTable.MODE_ICON);
	}

	/**
	 * @param app
	 *            application
	 * @param data
	 *            image
	 * @param rows
	 *            nr of rows
	 * @param columns
	 *            nr of columns
	 * @param mode
	 *            mode
	 * @param hasTable
	 *            true if popup has table
	 * @param hasSlider
	 *            rue if popup has slider
	 */
	public LineStylePopup(AppW app, ImageOrText[] data, Integer rows,
			Integer columns, SelectionTable mode,
			boolean hasTable, boolean hasSlider) {
		super(app, data, rows, columns, mode, hasTable, hasSlider);
	}

	private LineStylePopup(AppW app, ImageOrText[] data, Integer rows,
			Integer columns, SelectionTable mode) {
		super(app, data, rows, columns, mode);
	}

	/**
	 * @param type
	 *            line type
	 */
	public void selectLineType(int type) {
		setSelectedIndex(LineStyleModel.indexOfLineType(type));
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
}
