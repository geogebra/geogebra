package org.geogebra.web.web.gui.util;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.euclidian.EuclidianLineStylePopup;
import org.geogebra.web.web.gui.color.ColorPopupMenuButton;

/**
 * a version of StyleBarW that also includes the buttons for color, line style
 * and point style and (parts of) their handling.
 */
public abstract class StyleBarW2 extends StyleBarW implements PopupMenuHandler {

	protected ColorPopupMenuButton btnColor;
	protected EuclidianLineStylePopup btnLineStyle;
	protected PointStylePopup btnPointStyle;

	protected boolean needUndo = false;

	public StyleBarW2(AppW app, int viewID) {
		super(app, viewID);
	}

	protected void createLineStyleBtn(int mode) {
		LineStylePopup.setMode(mode);
		LineStylePopup.fillData(ICON_HEIGHT);
		btnLineStyle = new EuclidianLineStylePopup(app,
				LineStylePopup.getLineStyleIcons(), -1, 5,
				org.geogebra.common.gui.util.SelectionTable.MODE_ICON, true, true);

		btnLineStyle.getMySlider().setMinimum(1);
		btnLineStyle.getMySlider().setMaximum(13);
		btnLineStyle.getMySlider().setMajorTickSpacing(2);
		btnLineStyle.getMySlider().setMinorTickSpacing(1);
		btnLineStyle.getMySlider().setPaintTicks(true);
		btnLineStyle.addPopupHandler(this);
	}

	protected void createPointStyleBtn(int mode) {
		btnPointStyle = PointStylePopup.create(app, ICON_HEIGHT, mode, true,
				new PointStyleModel());

		btnPointStyle.getMySlider().setMinimum(1);
		btnPointStyle.getMySlider().setMaximum(9);
		btnPointStyle.getMySlider().setMajorTickSpacing(2);
		btnPointStyle.getMySlider().setMinorTickSpacing(1);
		btnPointStyle.getMySlider().setPaintTicks(true);
		btnPointStyle.addPopupHandler(this);
	}

	/**
	 * process the action performed
	 * 
	 * @param source
	 * @param targetGeos
	 */
	protected boolean processSource(Object source,
			ArrayList<GeoElement> targetGeos) {

		if (source == btnColor) {
			GColor color = btnColor.getSelectedColor();
			float alpha = btnColor.getSliderValue() / 100.0f;
			needUndo = EuclidianStyleBarStatic.applyColor(targetGeos, color,
					alpha, app);
		} else if (source == btnLineStyle) {
			// if (btnLineStyle.getSelectedValue() != null) {
			// if (EuclidianView.isPenMode(mode)) {
			// /*
			// * ec.getPen().setPenLineStyle(
			// * lineStyleArray[btnLineStyle.getSelectedIndex()]);
			// * ec.getPen().setPenSize(btnLineStyle.getSliderValue());
			// */
			// // App.debug("Not MODE_PEN in EuclidianStyleBar yet");
			// } else {
			// // handled by the popup itself
			// // int lineSize = btnLineStyle.getSliderValue();
			// // needUndo =
			// // EuclidianStyleBarStatic.applyLineStyle(targetGeos,
			// // selectedIndex, lineSize);
			// }
			// }
		} else if (source == btnPointStyle) {
			if (btnPointStyle.getSelectedValue() != null) {
				int pointStyleSelIndex = btnPointStyle.getSelectedIndex();
				int pointSize = btnPointStyle.getSliderValue();
				needUndo = EuclidianStyleBarStatic.applyPointStyle(targetGeos,
						pointStyleSelIndex, pointSize);
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * @param actionButton
	 *            runs programatically the action performed event.
	 */
	@Override
	public void fireActionPerformed(PopupMenuButton actionButton) {
		handleEventHandlers(actionButton);
	}

	protected abstract void handleEventHandlers(Object source);

}
