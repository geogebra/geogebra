package org.geogebra.web.web.gui.view.dataCollection;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.MyCJButton;
import org.geogebra.web.web.main.AppWapplication;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * 
 * 
 */
public class DataCollectionNavigation extends FlowPanel {
	
	/** Application */
	AppW app;
	
	/**
	 * 
	 * @param app
	 *            {@link AppW}
	 */
	public DataCollectionNavigation(AppW app) {
		this.app = app;
		this.addStyleName("dataCollectionNav");
		
		addPlayStopButton();
		addSettingsButton();
	}
	
	private void addPlayStopButton() {
		final ToggleButton startStopButton = new ToggleButton(new Image(
				AppResources.INSTANCE.nav_play()), new Image(
				AppResources.INSTANCE.nav_pause()));
		startStopButton.addStyleName("startStopButton");
		startStopButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (startStopButton.isDown()) {
					((AppWapplication) app).getDataCollection().start();
				} else {
					((AppWapplication) app).getDataCollection().stop();
				}
			}
		});
		this.add(startStopButton);
	}

	private void addSettingsButton() {
		MyCJButton settingsButton = new MyCJButton();
		ImageOrText icon = new ImageOrText();
		icon.setUrl(GuiResources.INSTANCE.menu_icon_options().getSafeUri().asString());
		settingsButton.setIcon(icon);
		settingsButton.addStyleName("settingsButton");
		settingsButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//taken from ViewMenuW
				app.getGuiManager().setShowView(
						!app.getGuiManager()
								.showView(AppW.VIEW_DATA_COLLECTION),
						AppW.VIEW_DATA_COLLECTION);
			}
		});
		this.add(settingsButton);
	}
}
