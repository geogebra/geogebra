package geogebra.web.gui.app;

import geogebra.common.main.App;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Arpad Fekete
 * 
 * Top level GUI for the spreadsheet view (imitation of GGWGraphicsView) 
 *
 */
public class GGWSpreadsheetView extends Composite {

	App application = null;
	
	private static GGWSpreadsheetViewUiBinder uiBinder = GWT
	        .create(GGWSpreadsheetViewUiBinder.class);

	interface GGWSpreadsheetViewUiBinder extends UiBinder<Widget, GGWSpreadsheetView> {
	}
	
	/**
	 * 
	 */
	@UiField AbsolutePanel tempsheet;

	/**
	 * 	Wrapper for the two EuclidianView (one is active only)
	 */
	public GGWSpreadsheetView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void attachApp(App app) {
	   this.application = app;
	   //eview1.attachApp(app);
	}
}
