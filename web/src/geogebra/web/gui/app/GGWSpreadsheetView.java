package geogebra.web.gui.app;

import geogebra.common.main.App;
import geogebra.web.gui.view.spreadsheet.SpreadsheetView;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Arpad Fekete
 * 
 * Top level GUI for the spreadsheet view (imitation of GGWGraphicsView) 
 *
 */
public class GGWSpreadsheetView extends ResizeComposite {

	App application = null;
	
	private static GGWSpreadsheetViewUiBinder uiBinder = GWT
	        .create(GGWSpreadsheetViewUiBinder.class);

	interface GGWSpreadsheetViewUiBinder extends UiBinder<Widget, GGWSpreadsheetView> {
	}
	
	/**
	 * 
	 */
	@UiField AbsolutePanel tempsheet0;
	SpreadsheetView1 spreadsheetview = null;

	public GGWSpreadsheetView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void onResize() {
		//App.debug("Resized");
		if (application != null) {
			SpreadsheetView spreadsheet = (SpreadsheetView)application.getGuiManager().getSpreadsheetView();
			spreadsheet.getScrollPanel().setWidth(this.getOffsetWidth()+"px");
			spreadsheet.getScrollPanel().setHeight(
				(this.getOffsetHeight() -
					(((SpreadsheetView)application.getGuiManager().getSpreadsheetView()).
				getSpreadsheetStyleBar()).getOffsetHeight())+"px");
		}
    }

	public void attachApp(App app) {
	   this.application = app;
	   spreadsheetview = new SpreadsheetView1();
	   tempsheet0.add(spreadsheetview);
	   spreadsheetview.attachApp(app);

	   onResize();
	}
}
