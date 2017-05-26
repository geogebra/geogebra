package org.geogebra.web.web.gui.toolbarpanel;

import java.util.ArrayList;

import org.geogebra.common.gui.toolcategorization.ToolCategorization;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.Category;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Tools extends FlowPanel {

	ToolCategorization mToolCategorization;
	AppW app;

	public Tools(AppW appl) {
		app = appl;
		this.addStyleName("toolsPanel");
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
		private FlowPanel toolsPanel;

		public CategoryPanel(ToolCategorization.Category cat) {
			super();
			category = cat;
			initGui();
		}

		private void initGui() {
			add(new Label(mToolCategorization.getLocalizedHeader(category)));

			toolsPanel = new FlowPanel();
			ArrayList<Integer> tools = mToolCategorization.getTools(
					mToolCategorization.getCategories().indexOf(category));

			for (int i = 0; i < tools.size(); i++) {
				toolsPanel.add(getButton(tools.get(i)));
			}

			add(toolsPanel);

		}

		private FlowPanel getToolsPanel() {
			return toolsPanel;
		}

		private StandardButton getButton(final int mode) {
			NoDragImage im = new NoDragImage(GGWToolBar
					.getImageURL(mode, app));
			final StandardButton btn = new StandardButton(null, "", 32);
			btn.getUpFace().setImage(im);

			btn.addFastClickHandler(new FastClickHandler() {

				public void onClick(Widget source) {
					app.setMode(mode);
					clearSelectionStyle();
					btn.getElement().setAttribute("selected", "true");
				}

			});

			return btn;
		}

		public void clearSelectionStyle() {
			for (int i = 0; i < Tools.this.getWidgetCount(); i++) {
				CategoryPanel categoryPanel = (CategoryPanel) Tools.this
						.getWidget(i);
				for (int j = 0; j < categoryPanel.getWidgetCount(); j++) {
					categoryPanel.getToolsPanel().getWidget(j).getElement()
							.setAttribute("selected", "false");
				}
			}
		}
	}

}
