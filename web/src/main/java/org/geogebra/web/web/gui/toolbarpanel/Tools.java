package org.geogebra.web.web.gui.toolbarpanel;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.toolcategorization.ToolCategorization;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.Category;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.Type;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author judit Content of tools tab of Toolbar panel.
 */
public class Tools extends FlowPanel {

	ToolCategorization mToolCategorization;
	/**
	 * application
	 */
	AppW app;
	private StandardButton moveButton;

	/**
	 * @param appl
	 *            application
	 */
	public Tools(AppW appl) {
		app = appl;
		this.addStyleName("toolsPanel");
		buildGui();
	}

	/**
	 * Selects MODE_MOVE as mode and changes visual settings accordingly of
	 * this.
	 */
	public void setMoveMode() {
		app.setMode(EuclidianConstants.MODE_MOVE);
		clearSelectionStyle();
		if (moveButton != null) {
			moveButton.getElement().setAttribute("selected", "true");
		}
	}

	public void clearSelectionStyle() {
		for (int i = 0; i < getWidgetCount(); i++) {
			FlowPanel panelTools = ((CategoryPanel) getWidget(i))
					.getToolsPanel();
			for (int j = 0; j < panelTools.getWidgetCount(); j++) {
				panelTools.getWidget(j).getElement().setAttribute("selected",
						"false");
			}
		}
	}

	public void buildGui() {
		this.clear();

		int activePerspective = app.getActivePerspective();

		Type type = ToolCategorization.Type.GRAPHING_CALCULATOR;
		if (activePerspective == Perspective.GRAPHER_3D - 1) {
			type = ToolCategorization.Type.GRAPHER_3D;
		}

		mToolCategorization = new ToolCategorization(app,
				type, false);
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
				StandardButton btn = getButton(tools.get(i));
				toolsPanel.add(btn);
				if (tools.get(i) == EuclidianConstants.MODE_MOVE) {
					moveButton = btn;
				}
			}

			add(toolsPanel);

		}

		FlowPanel getToolsPanel() {
			return toolsPanel;
		}

		private StandardButton getButton(final int mode) {
			NoDragImage im = new NoDragImage(GGWToolBar
					.getImageURL(mode, app));
			final StandardButton btn = new StandardButton(null, "", 32);
			btn.setTitle(app.getLocalization()
					.getMenu(EuclidianConstants.getModeText(mode)));

			btn.getUpFace().setImage(im);

			btn.addFastClickHandler(new FastClickHandler() {

				@Override
				public void onClick(Widget source) {
					app.setMode(mode);
					clearSelectionStyle();
					btn.getElement().setAttribute("selected", "true");
					ToolTipManagerW.sharedInstance().setBlockToolTip(false);
					ToolTipManagerW.sharedInstance().showBottomInfoToolTip(
							app.getToolTooltipHTML(mode),
							app.getGuiManager().getTooltipURL(mode),
							ToolTipLinkType.Help, app,
							app.getAppletFrame().isKeyboardShowing());
					ToolTipManagerW.sharedInstance().setBlockToolTip(true);
				}

			});

			return btn;
		}
	}

}
