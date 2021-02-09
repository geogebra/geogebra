package org.geogebra.web.full.euclidian;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel.ILineStyleListener;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.util.LineStylePopup;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

/**
 * Line style popup
 */
public class EuclidianLineStylePopup extends LineStylePopup implements
		ILineStyleListener, SetLabels {
	private LineStyleModel model;

	/**
	 * @param app
	 *            application
	 * @param columns
	 *            number of columns
	 * @param hasSlider
	 *            slider
	 */
	public EuclidianLineStylePopup(AppW app,
			Integer columns, boolean hasSlider) {
		super(app, LineStylePopup.getLineStyleIcons(), -1, columns,
				SelectionTable.MODE_ICON, true, hasSlider,
				LineStylePopup.createLineStyleMap());
		model = new LineStyleModel(app);
		model.setListener(this);
		this.setKeepVisible(false);
		getMySlider().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				model.applyThickness(getSliderValue());
			}
		});
	}

	@Override
	public void update(List<GeoElement> geos) {
		updatePanel(geos.toArray());
	}

	@Override
	public Object updatePanel(Object[] geos) {
		model.setGeos(geos);

		if (!model.hasGeos() || app.getMode() == EuclidianConstants.MODE_FREEHAND_SHAPE) {
			this.setVisible(false);
			return null;
		}

		boolean geosOK = model.checkGeos();
		this.setVisible(geosOK);

		if (geosOK) {
			model.updateProperties();
			GeoElement geo0 = model.getGeoAt(0);
			if (hasSlider()) {
				setSliderValue(geo0.getLineThickness());
			}
			// showTableItem(5, geo0 instanceof GeoFunction);
			selectLineType(geo0.getLineType());

		}
		return this;
	}

	@Override
	public void handlePopupActionEvent() {
		// store the index, won't be the same after split updates the UI
		int selectedIndex = getSelectedIndex();
		if (app.getActiveEuclidianView().getEuclidianController()
				.splitSelectedStrokes(true)) {
			model.setGeos(app.getSelectionManager().getSelectedGeos().toArray());
		}
		model.applyLineTypeFromIndex(selectedIndex);
		getMyPopup().hide();
	}

	@Override
	public void setThicknessSliderValue(int value) {
		getMySlider().setValue(value);
	}

	@Override
	public void setThicknessSliderMinimum(int minimum) {
		getMySlider().setMinimum(minimum);
	}

	@Override
	public void selectCommonLineStyle(boolean equalStyle, int type) {
		selectLineType(type);
	}

	@Override
	public void setLineTypeVisible(boolean value) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setLineStyleHiddenVisible(boolean value) {
		// TODO Auto-generated method stub
	}

	@Override
	public void selectCommonLineStyleHidden(boolean equalStyle, int type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setOpacitySliderValue(int value) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setLineOpacityVisible(boolean value) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setLabels() {
		// Overridden in MOWLineStyleButton
	}

}
