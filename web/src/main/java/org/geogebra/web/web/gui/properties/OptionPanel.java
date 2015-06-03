package org.geogebra.web.web.gui.properties;

import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.gui.dialog.options.model.PropertyListener;

import com.google.gwt.user.client.ui.Widget;

public abstract class OptionPanel implements IOptionPanel, PropertyListener {
	OptionsModel model;
	private Widget widget;

	public final OptionPanel update(Object[] geos) {
		return updatePanel(geos);
	}

	public OptionPanel updatePanel(Object[] geos) {
		getModel().setGeos(geos);
		boolean geosOK = getModel().checkGeos();

		if (widget != null) {
			widget.setVisible(geosOK);
		}

		if (!geosOK || widget == null) {
			return null;
		}
		getModel().updateProperties();
		setLabels();
		return this;
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
