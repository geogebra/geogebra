package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog for selecting files
 */
public class FileInputDialog extends GPopupPanel implements ClickHandler {

	private FileUpload inputWidget;
	private Button btCancel;

	/**
	 * @param app
	 *            application
	 */
	public FileInputDialog(AppW app) {
		super(false, true, app.getPanel(), app);
		// createGUI();
		addStyleName("GeoGebraPopup");
		setGlassEnabled(true);
		if (app.isUnbundledOrWhiteboard()) {
			btCancel.addStyleName("dialogBtn");
			setStyleName("MaterialDialogBox");
		}
	}

	/**
	 * Build the UI.
	 */
	protected void createGUI() {
		setInputWidget(new FileUpload());
		// addGgbChangeHandler(inputWidget.getElement(), app);

		btCancel = new Button(app.getLocalization().getMenu("Cancel"));
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);
		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.add(getInputWidget());
		centerPanel.add(btCancel);

		setWidget(centerPanel);
	}

	@Override
	public final void onClick(ClickEvent event) {
		if (event.getSource() == btCancel) {
			hideAndFocus();
		}
	}

	/**
	 * Hide this and focus app
	 */
	public void hideAndFocus() {
		hide();
		app.getActiveEuclidianView().requestFocusInWindow();
	}

	/**
	 * @return input widget
	 */
	public FileUpload getInputWidget() {
		return inputWidget;
	}

	private void setInputWidget(FileUpload inputWidget) {
		this.inputWidget = inputWidget;
	}
}
