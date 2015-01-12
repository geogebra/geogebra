package geogebra.phone.gui.view.material;

import geogebra.html5.main.AppW;
import geogebra.phone.gui.view.AbstractViewPanel;
import geogebra.phone.gui.view.material.browser.MaterialListPanelP;
import geogebra.web.gui.browser.MaterialListPanel;

import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * A Panel with a scrollable {@link MaterialListPanelP}
 * 
 * @see AbstractViewPanel
 */
public class BrowseViewPanel extends AbstractViewPanel {

	private MaterialListPanelP materialListPanel;

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public BrowseViewPanel(final AppW app) {
		super(app);
		materialListPanel = new MaterialListPanelP(app);
		ScrollPanel scrollPanel = new ScrollPanel(materialListPanel);
		add(scrollPanel);
	}

	@Override
	protected String getViewPanelStyleName() {
		return "materialViewPanel";
	}

	/**
	 * @return {@link MaterialListPanel}
	 */
	public MaterialListPanel getMaterialPanel() {
		return materialListPanel;
	}

	@Override
	public void onResize() {
		super.onResize();
		if (this.materialListPanel != null) {
			this.materialListPanel.onResize();
		}
	}
}
