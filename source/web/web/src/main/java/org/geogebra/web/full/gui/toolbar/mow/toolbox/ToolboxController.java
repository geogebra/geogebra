package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventListener;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.html5.main.AppW;

public class ToolboxController {
	private final AppW appW;
	private final NotesToolbox toolbox;
	
	/**
	 * Controller
	 * @param appW - application
	 * @param toolbox - toolbox
	 */
	public ToolboxController(AppW appW, NotesToolbox toolbox) {
		this.appW = appW;
		this.toolbox = toolbox;
	}

	/**
	 * turn spotlight on
	 */
	public void switchSpotlightOn() {
		DockPanelW dp = (DockPanelW) appW.getGuiManager().getLayout().getDockManager()
				.getPanel(App.VIEW_EUCLIDIAN);
		dp.getComponent().addStyleName("graphicsWithSpotlight");
		appW.getActiveEuclidianView().getEuclidianController().spotlightOn();
		initSpotlightOff();
		toolbox.disableNonSpotlightButtons();
		appW.hideMenu();
		appW.closePopups();
	}

	private void initSpotlightOff() {
		appW.getEventDispatcher().addEventListener(new EventListener() {
			@Override
			public void sendEvent(Event evt) {
				if (evt.getType() == EventType.HIDE_SPOTLIGHT) {
					EuclidianView view = appW.getActiveEuclidianView();
					DockPanelW dp = (DockPanelW) appW.getGuiManager().getLayout().getDockManager()
							.getPanel(App.VIEW_EUCLIDIAN);
					dp.getComponent().removeStyleName("graphicsWithSpotlight");
					view.clearSpotlight();
					toolbox.forEach(widget -> widget.removeStyleName("disabled"));
					toolbox.switchSpotlightOff();
					appW.getEventDispatcher().removeEventListener(this);
				}
			}

			@Override
			public void reset() {
				// not needed
			}
		});
	}

	/**
	 * switch spotlight off
	 */
	public void switchSpotlightOff() {
		if (appW.getActiveEuclidianView() != null) {
			appW.getActiveEuclidianView().getEuclidianController().spotlightOff();
		}
	}
}