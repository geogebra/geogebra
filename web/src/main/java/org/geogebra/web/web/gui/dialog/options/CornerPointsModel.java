package org.geogebra.web.web.gui.dialog.options;

import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.gui.dialog.options.model.PropertyListener;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.web.web.gui.dialog.options.OptionsTab.CornerPointsPanel;

public class CornerPointsModel extends OptionsModel {
	private CornerPointsPanel listener;

	@Override
	protected boolean isValidAt(int index) {
		return getGeoAt(index) instanceof Locateable;
	}

	@Override
	public void updateProperties() {
		// TODO Auto-generated method stub

	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}

	public void setListener(CornerPointsPanel listener) {
		this.listener = listener;
	};

}
