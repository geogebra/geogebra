package geogebra.web.gui.app;

import geogebra.common.main.App;
import geogebra.web.gui.view.spreadsheet.SpreadsheetView;
import geogebra.web.main.AppW;

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
	SpreadsheetView spreadsheet = null;

	/**
	 * 	Wrapper for the two EuclidianView (one is active only)
	 */
	public GGWSpreadsheetView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void attachApp(App app) {
	   this.application = app;

	   // get the spreadsheet from the app
	   spreadsheet = ((AppW)app).getGuiManager().getSpreadsheetView();

	   tempsheet.add(spreadsheet);
	   spreadsheet.getScrollPanel().setWidth(this.getOffsetWidth()+"px");
	   // 80 is a temporary estimation
	   spreadsheet.getScrollPanel().setHeight((this.getOffsetHeight()-80)+"px");
	}
}
