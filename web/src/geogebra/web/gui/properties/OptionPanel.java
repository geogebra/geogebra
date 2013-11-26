package geogebra.web.gui.properties;

import geogebra.common.gui.dialog.options.model.OptionsModel;

import com.google.gwt.user.client.ui.Widget;

public abstract class OptionPanel {
	OptionsModel model;
	private Widget widget;

	public boolean update(Object[] geos) {
		getModel().setGeos(geos);
		if (!setupPanel()) {
			return false;
		}
		getModel().updateProperties();
		setLabels();
		return true;
	}

	protected boolean setupPanel() {
		boolean result = false;
		
		if (!(getModel().checkGeos())) {
			if (widget != null) {
				widget.setVisible(false);
			}
			return false;
		}
		if (widget != null) {
			widget.setVisible(true);
			result = true;
		}
		
		return result;
	}
	public Widget getWidget() {
		return widget;
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
	}

	public OptionsModel getModel() {
		return model;
	}

	public void setModel(OptionsModel model) {
		this.model = model;
	}

	public abstract void setLabels();
}
