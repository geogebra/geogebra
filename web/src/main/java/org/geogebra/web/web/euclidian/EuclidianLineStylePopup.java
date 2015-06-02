package org.geogebra.web.web.euclidian;

import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel.ILineStyleListener;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.LineStylePopup;

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
		updatePanel(geos);
	}

	public Object updatePanel(Object[] geos) {
		model.setGeos(geos);

		if (!model.hasGeos()) {
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
			selectLineType(geo0.getLineType());

		}
		return this;
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