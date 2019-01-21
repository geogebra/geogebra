package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.HeaderView;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Csilla
 * 
 *         Settings view of scientific calculator
 *
 */
public class ScientificSettingsView extends MyHeaderPanel implements FastClickHandler {

	private AppW app;
	private HeaderView headerView;
	private boolean isOpen = false;
	private Localization localization;

	/**
	 * Build and style settings view for sci calc
	 */
	public ScientificSettingsView(AppW app) {
		this.addStyleName("scientificSettingsView");
		this.app = app;
		isOpen = true;
		localization = app.getLocalization();
		createHeader();
	}
	
	private void createHeader() {
		headerView = new HeaderView(app);
		headerView.setCaption(localization.getMenu("Settings"));
		StandardButton backButton = headerView.getBackButton();
		backButton.addFastClickHandler(this);
		
		setHeaderWidget(headerView);
	}

	@Override
	public void onClick(Widget source) {
		if (source == headerView.getBackButton()) {
			close();
		}
	}

	@Override
	public AppW getApp() {
		return app;
	}

	/**
	 * @return true if settings view is open
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * @param isOpen
	 *            true if open settings, false otherwise
	 */
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	@Override
	public void resizeTo(int width, int height) {
		// TODO handle resize
	}

}
