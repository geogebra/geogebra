package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FileInputDialog extends PopupPanel implements ClickHandler {
	protected AppW app;
	protected GeoPoint location;

	private FileUpload inputWidget;
	protected Button btCancel;

	public FileInputDialog(AppW app, GeoPoint location) {
		super(false, true);
		this.app = app;
		this.location = location;
		// createGUI();
		addStyleName("GeoGebraPopup");
		setGlassEnabled(true);
		center();
	}

	protected void createGUI() {

		setInputWidget(new FileUpload());
		// addGgbChangeHandler(inputWidget.getElement(), app);

		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);

		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.add(getInputWidget());
		centerPanel.add(btCancel);

		setWidget(centerPanel);
	}

	public void onClick(ClickEvent event) {
		if (event.getSource() == btCancel) {
			hideAndFocus();
		}
	}

	public native JavaScriptObject getNativeHideAndFocus() /*-{
		return this.@org.geogebra.web.web.gui.dialog.FileInputDialog::hideAndFocus()();
	}-*/;

	public void hideAndFocus() {
		hide();
		app.getActiveEuclidianView().requestFocusInWindow();
	}

	public FileUpload getInputWidget() {
		return inputWidget;
	}

	public void setInputWidget(FileUpload inputWidget) {
		this.inputWidget = inputWidget;
	}
}
