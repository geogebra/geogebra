package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.util.VerticalSeparator;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class UploadImageDialog extends DialogBoxW
		implements ClickHandler, SetLabels {

	protected HorizontalPanel mainPanel;
	protected VerticalPanel listPanel;
	protected VerticalPanel imagePanel;
	protected FlowPanel bottomPanel;
	protected SimplePanel inputPanel;
	protected UploadImagePanel uploadImagePanel;
	protected AppW appw;
	protected Button insertBtn;
	protected Button cancelBtn;
	protected Label upload;
	protected GeoPoint location;
	protected boolean defaultToUpload = true;

	int previewHeight;
	int previewWidth;

	/**
	 * @param app
	 *            application
	 * @param previewWidth
	 *            preview width
	 * @param previewHeight
	 *            preview height
	 */
	public UploadImageDialog(AppW app, int previewWidth, int previewHeight) {
		super(app.getPanel(), app);
		this.appw = app;
		this.previewWidth = previewWidth;
		this.previewHeight = previewHeight;
		app.addInsertImageCallback(new Runnable() {

			@Override
			public void run() {
				UploadImageDialog.this.hide();
			}
		});
		initGUI();
		initActions();
	}

	protected void initGUI() {
		add(mainPanel = new HorizontalPanel());

		mainPanel.add(listPanel = new VerticalPanel());
		listPanel.add(upload = new Label(""));
		// listPanel.add(webcam = new Label(""));
		listPanel.setSpacing(10);

		mainPanel.add(new VerticalSeparator(225));
		mainPanel.setSpacing(5);
		mainPanel.add(imagePanel = new VerticalPanel());

		imagePanel.add(inputPanel = new SimplePanel());
		inputPanel.setHeight("180px");
		inputPanel.setWidth("240px");

		uploadImagePanel = new UploadImagePanel(this, previewWidth,
				previewHeight);
		imagePanel.add(bottomPanel = new FlowPanel());

		bottomPanel.add(insertBtn = new Button(""));
		bottomPanel.add(cancelBtn = new Button(""));
		insertBtn.setEnabled(false);

		cancelBtn.addStyleName("cancelBtn");

		bottomPanel.setStyleName("DialogButtonPanel");
		addStyleName("GeoGebraPopup");
		addStyleName("image");
		setGlassEnabled(true);
	}

	protected void initActions() {
		insertBtn.addClickHandler(this);
		cancelBtn.addClickHandler(this);
		upload.addClickHandler(this);
	}

	@Override
	public void setLabels() {
		Localization loc = appw.getLocalization();
		getCaption().setText(loc.getMenu("Image"));
		upload.setText(loc.getMenu("File"));
		insertBtn.setText(loc.getMenu("OK"));
		cancelBtn.setText(loc.getMenu("Cancel"));
	}

	protected void uploadClicked() {
		upload.addStyleDependentName("highlighted");
		inputPanel.setWidget(uploadImagePanel);
	}

	protected void imageAvailable() {
		insertBtn.setEnabled(true);
		insertBtn.removeStyleName("button-up-disabled");
	}

	protected void imageUnavailable() {
		insertBtn.setEnabled(false);
		insertBtn.addStyleName("button-up-disabled");
	}

	/**
	 * @param loc {@link GeoPoint}
	 */
	public void setLocation(GeoPoint loc) {
		this.location = loc;
	}

	@Override
	public void center() {
		super.center();
		if (defaultToUpload) {
			setLabels();
			uploadClicked();
		}
	}

}
