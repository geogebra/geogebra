package geogebra.web.gui.dialog;

import geogebra.common.kernel.geos.GeoPoint;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FileInputDialog extends PopupPanel implements ClickHandler{ 
	protected AppW app;
	protected GeoPoint location;

	protected FileUpload inputWidget;
	protected Button btCancel;

	public FileInputDialog(AppW app, GeoPoint location){
	    super(false, true);
	    this.app = app;
	    this.location = location;
	    //createGUI();
	    setGlassEnabled(true);
	    center();
    }

	protected void createGUI() {

		inputWidget = new FileUpload();
		//addGgbChangeHandler(inputWidget.getElement(), app);

		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);

		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.add(inputWidget);
		centerPanel.add(btCancel);

		setWidget(centerPanel);
	}

	public void onClick(ClickEvent event) {
	    if (event.getSource() == btCancel) {
	    	hideAndFocus();
	    }
    }

	public void hideAndFocus() {
    	hide();
		app.getActiveEuclidianView().requestFocusInWindow();
	}
}
