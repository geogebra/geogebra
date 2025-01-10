package org.geogebra.web.full.gui.util;

import java.util.HashMap;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;

public class PointStylePopup extends PopupMenuButtonW
		implements SetLabels {

	private static final int DEFAULT_SIZE = 4;
	static final HashMap<Integer, Integer> pointStyleMap = new HashMap<>();
	int mode;
	private boolean euclidian3D;

	/**
	 * @param app
	 *            application
	 * @param mode
	 *            mode
	 * @param hasSlider
	 *            whether slider for size should be used
	 * @return point style popup
	 */
	public static PointStylePopup create(AppW app, int mode,
			boolean hasSlider) {
		
		pointStyleMap.clear();
		for (int i = 0; i < EuclidianView.getPointStyleLength(); i++) {
			pointStyleMap.put(EuclidianView.getPointStyle(i), i);
		}

		ImageOrText[] pointStyleIcons = new ImageOrText[EuclidianView
				.getPointStyleLength()];
		for (int i = 0; i < EuclidianView.getPointStyleLength(); i++) {
			pointStyleIcons[i] = GeoGebraIconW
					.createPointStyleIcon(EuclidianView.getPointStyle(i));
		}

		PointStylePopup popup = new PointStylePopup(app, pointStyleIcons, 2,
				SelectionTable.MODE_ICON, true,
				hasSlider);
		popup.mode = mode;
		return popup;
	}

	/**
	 * @param app
	 *            application
	 * @param data
	 *            point style icons
	 * @param rows
	 *            number of rows
	 * @param tableMode
	 *            selection mode
	 * @param hasTable
	 *            whether table is used
	 * @param hasSlider
	 *            whether size slider is used
	 */
	public PointStylePopup(AppW app, ImageOrText[] data, Integer rows, SelectionTable tableMode,
			boolean hasTable, boolean hasSlider) {
		super(app, data, rows, -1, tableMode, hasTable, hasSlider);
		getMyPopup().addStyleName("pointSizeSlider");
		euclidian3D = false;
	}

	@Override
	public void update(List<GeoElement> geos) {
		if (geos.isEmpty()) {
			this.setVisible(false);
			return;
		}
		boolean geosOK = geos.stream().allMatch(PointStyleModel::match);
		this.setVisible(geosOK);

		if (geosOK) {
			getMyTable().setVisible(!euclidian3D);

			PointProperties geo0 = (PointProperties) geos.get(0);
			if (hasSlider()) {
				setSliderValue(geo0.getPointSize());
			}

			setSelectedIndex(pointStyleMap.get(euclidian3D ? 0 : geo0
			        .getPointStyle()));

			this.setKeepVisible(EuclidianConstants.isMoveOrSelectionMode(mode));
		}
	}

	@Override
	public void handlePopupActionEvent() {
		super.handlePopupActionEvent();
	}

	@Override
	public ImageOrText getButtonIcon() {
		if (getSelectedIndex() > -1) {
			return GeoGebraIconW
					.createPointStyleIcon(EuclidianView
							.getPointStyle(this.getSelectedIndex()));
		}
		return new ImageOrText();
	}
	
	@Override
	public int getSliderValue() {
		int val = super.getSliderValue();
		return val == -1 ? DEFAULT_SIZE : val;
	}

	public void setEuclidian3D(boolean euclidian3d) {
		euclidian3D = euclidian3d;
	}

	@Override
	public void setLabels() {
		// Overridden in MowPointStyle
	}

}
