package geogebra.web.gui.app;

import geogebra.common.main.App;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SpreadsheetView1 extends ResizeComposite {

	App application = null;

	private static SpreadsheetView1UiBinder uiBinder = GWT
	        .create(SpreadsheetView1UiBinder.class);

	interface SpreadsheetView1UiBinder extends UiBinder<VerticalPanel, SpreadsheetView1> {
	}

	@UiField VerticalPanelSmart ancestor;
	@UiField SpreadsheetStyleBarPanel ssbpanel;
	@UiField SpreadsheetPanel sspanel;

	public SpreadsheetView1() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	/**
	 * @return euclidianPanel (needed for wrap for textfields)
	 */
	public SpreadsheetPanel getSpreadsheetPanel() {
		return sspanel;
	}

	public void onResize() {
		App.debug("Resized");
    }
	
	public void onLoad() {
		//getEuclidianPanel().getElement().getStyle().setPosition(Position.RELATIVE);
	}

	public void attachApp(App app) {
	   this.application = app;
	   ssbpanel.attachApp(app);
	   sspanel.attachApp(app);
	}

}
