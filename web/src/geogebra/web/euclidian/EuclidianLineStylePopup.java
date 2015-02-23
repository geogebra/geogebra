package geogebra.web.euclidian;

import geogebra.common.gui.dialog.options.model.LineStyleModel;
import geogebra.common.gui.dialog.options.model.LineStyleModel.ILineStyleListener;
import geogebra.common.gui.util.SelectionTable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.html5.main.AppW;
import geogebra.web.gui.util.ImageOrText;
import geogebra.web.gui.util.LineStylePopup;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public class EuclidianLineStylePopup extends LineStylePopup implements
		ILineStyleListener {
	LineStyleModel model;

	public EuclidianLineStylePopup(AppW app, ImageOrText[] data, Integer rows,
			Integer columns, SelectionTable mode, boolean hasTable,
			boolean hasSlider) {
		super(app, data, rows, columns, mode, hasTable, hasSlider);
		model = new LineStyleModel(this);
		this.setKeepVisible(false);
		getMySlider().addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				model.applyThickness(getSliderValue());
			}
		});
	}

	@Override
	public void update(Object[] geos) {
		model.setGeos(geos);

		if (!model.hasGeos()) {
			this.setVisible(false);
			return;
		}

		boolean geosOK = model.checkGeos();
		this.setVisible(geosOK);

		if (geosOK) {
			model.updateProperties();
			GeoElement geo0 = model.getGeoAt(0);
			if (hasSlider()) {
				setSliderValue(geo0.getLineThickness());
			}
			selectLineType(geo0.getLineType());

		}
	}

	@Override
	public void handlePopupActionEvent() {
		model.applyLineTypeFromIndex(getSelectedIndex());
		getMyPopup().hide();
	}

	public void setThicknessSliderValue(int value) {
		getMySlider().setValue(value);
	}

	public void setThicknessSliderMinimum(int minimum) {
		getMySlider().setMinimum(minimum);
	}

	public void selectCommonLineStyle(boolean equalStyle, int type) {
		selectLineType(type);
	}

	public void setLineTypeVisible(boolean value) {
		// TODO Auto-generated method stub
	}

	public void setOpacitySliderValue(int value) {
		// TODO Auto-generated method stub
	}

	public void setLineOpacityVisible(boolean value) {
		// TODO Auto-generated method stub
	}

}