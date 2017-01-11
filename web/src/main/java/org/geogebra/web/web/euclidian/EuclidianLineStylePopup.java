package org.geogebra.web.web.euclidian;

import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.gui.dialog.options.model.LineStyleModel.ILineStyleListener;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.util.LineStylePopup;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public class EuclidianLineStylePopup extends LineStylePopup implements
		ILineStyleListener {
	LineStyleModel model;

	public EuclidianLineStylePopup(AppW app, int iconHeight, Integer rows,
			Integer columns, SelectionTable mode, boolean hasTable,
			boolean hasSlider) {
		super(app, LineStylePopup.getLineStyleIcons(iconHeight), rows, columns,
				mode, hasTable, hasSlider,
				LineStylePopup.getLineStyleMap(iconHeight));
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
	public void update(Object[] geos) {
		updatePanel(geos);
	}

	@Override
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
			showTableItem(5, geo0 instanceof GeoFunction);
			selectLineType(geo0.getLineType());

		}
		return this;
	}

	@Override
	public void handlePopupActionEvent() {
		model.applyLineTypeFromIndex(getSelectedIndex());
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

}