package org.geogebra.web.web.gui.util;

import java.util.HashMap;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.VerticalPanel;

public class MOWPointStyleButton extends PointStylePopup {

	public MOWPointStyleButton(AppW app, ImageOrText[] data, Integer rows,
			Integer columns, SelectionTable mode, boolean hasTable,
			boolean hasSlider, PointStyleModel model) {
		super(app, data, rows, columns, mode, hasTable, hasSlider, model);

		// Rearranging content.
		VerticalPanel panel = ((ButtonPopupMenu) getMyPopup()).getPanel();
		panel.clear();
		panel.add(sliderPanel);
		panel.add(getMyTable());
	}

	public static MOWPointStyleButton create(AppW app, int mode,
			boolean hasSlider, PointStyleModel model) {

		PointStylePopup.mode = mode;

		pointStyleMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < EuclidianView.getPointStyleLength(); i++) {
			pointStyleMap.put(EuclidianView.getPointStyle(i), i);
		}

		ImageOrText[] pointStyleIcons = new ImageOrText[EuclidianView
				.getPointStyleLength()];
		for (int i = 0; i < EuclidianView.getPointStyleLength(); i++) {
			pointStyleIcons[i] = GeoGebraIconW
					.createPointStyleIcon(EuclidianView.getPointStyle(i));
		}

		return new MOWPointStyleButton(app, pointStyleIcons, 2, -1,
				SelectionTable.MODE_ICON, true, hasSlider, model);
	}

}
