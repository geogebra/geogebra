package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Widget;

public class DataCollectionDockPanelW extends DockPanelW {
	
	/**
	 * New panel for data collection.
	 */
	public DataCollectionDockPanelW() {
		super(App.VIEW_DATA_COLLECTION, // view id
				"DataCollection", // view title phrase
				null, // toolbar string
				false, // style bar?
				-1 // menu order
		);
		this.setEmbeddedSize(450);
	}

	@Override
	protected Widget loadComponent() {
		return ((GuiManagerW) app.getGuiManager()).getDataCollectionView();
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return null;
	}
}
