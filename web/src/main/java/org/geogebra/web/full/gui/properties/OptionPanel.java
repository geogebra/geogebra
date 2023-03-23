package org.geogebra.web.full.gui.properties;

import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.gui.dialog.options.model.PropertyListener;
import org.gwtproject.user.client.ui.Widget;

/**
 * Panel for properties view
 *
 */
public abstract class OptionPanel implements IOptionPanel, PropertyListener {
	private OptionsModel model;
	private Widget widget;

	@Override
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

	@Override
	public Widget getWidget() {
		return widget;
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
	}

	@Override
	public OptionsModel getModel() {
		return model;
	}

	/**
	 * @param model
	 *            options model
	 */
	public void setModel(OptionsModel model) {
		this.model = model;
	}

	@Override
	public abstract void setLabels();
}
