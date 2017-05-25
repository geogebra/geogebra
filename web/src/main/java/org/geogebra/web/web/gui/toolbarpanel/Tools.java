package org.geogebra.web.web.gui.toolbarpanel;

import java.util.ArrayList;

import org.geogebra.common.gui.toolcategorization.ToolCategorization;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.Category;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class Tools extends FlowPanel {

	ToolCategorization mToolCategorization;

	public Tools(AppW app) {
		mToolCategorization = new ToolCategorization(app,
				ToolCategorization.Type.GRAPHING_CALCULATOR, false);
		mToolCategorization.resetTools();
		ArrayList<ToolCategorization.Category> categories = mToolCategorization
				.getCategories();

		for (int i = 0; i < categories.size(); i++) {
			add(new CategoryPanel(categories.get(i)));
		}

	}

	private class CategoryPanel extends FlowPanel {
		private Category category;

		public CategoryPanel(ToolCategorization.Category cat) {
			super();
			category = cat;
			initGui();
		}

		private void initGui() {
			add(new Label(mToolCategorization.getLocalizedHeader(category)));
		}
	}

}
