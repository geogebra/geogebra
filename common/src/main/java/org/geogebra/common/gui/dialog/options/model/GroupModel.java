package org.geogebra.common.gui.dialog.options.model;

import java.util.ArrayList;
import java.util.List;

public class GroupModel extends OptionsModel {
	private List<OptionsModel> models = new ArrayList<OptionsModel>();

	@Override
	protected boolean isValidAt(int index) {
		for (OptionsModel model : models) {
			if (model.isValidAt(index)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void updateProperties() {
		for (OptionsModel model : models) {
			model.updateProperties();
		}

	}

	public void add(OptionsModel model) {
		models.add(model);
	}

	@Override
	public boolean updateMPanel(Object[] geos2) {
		boolean enabled = false;
		for (OptionsModel model : models) {
			enabled = model.updateMPanel(geos2) || enabled;
		}
		return enabled;
	}

}
