package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.web.full.gui.util.VerticalSeparator;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UploadImageDialog extends ComponentDialog {
	protected SimplePanel inputPanel;
	protected UploadImagePanel uploadImagePanel;
	protected VerticalPanel listPanel;
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
	public UploadImageDialog(AppW app,
			int previewWidth, int previewHeight) {
		super(app,
				new DialogData("Image", "Cancel", "OK"),
				false, true);
		this.previewWidth = previewWidth;
		this.previewHeight = previewHeight;
		addStyleName("image");
		buildContent();
		app.addInsertImageCallback(this::hide);
	}

	protected void buildContent() {
		HorizontalPanel contentPanel = new HorizontalPanel();
		listPanel = new VerticalPanel();
		contentPanel.add(listPanel);
		listPanel.add(upload = new Label(app.getLocalization().getMenu("File")));
		upload.addClickHandler(clickEvent -> uploadClicked());
		listPanel.setSpacing(10);

		contentPanel.add(new VerticalSeparator(225));
		contentPanel.setSpacing(5);
		VerticalPanel imagePanel = new VerticalPanel();
		contentPanel.add(imagePanel);

		imagePanel.add(inputPanel = new SimplePanel());
		inputPanel.setHeight("180px");
		inputPanel.setWidth("240px");

		uploadImagePanel = new UploadImagePanel(this, previewWidth,
				previewHeight);
		setPosBtnDisabled(true);
		addDialogContent(contentPanel);
	}

	protected void uploadClicked() {
		upload.addStyleDependentName("highlighted");
		inputPanel.setWidget(uploadImagePanel);
	}

	protected void imageAvailable() {
		setPosBtnDisabled(false);
	}

	protected void imageUnavailable() {
		setPosBtnDisabled(true);
	}

	/**
	 * @param loc {@link GeoPoint}
	 */
	public void setLocation(GeoPoint loc) {
		this.location = loc;
	}

	public UploadImagePanel getUploadImgPanel() {
		return uploadImagePanel;
	}

	@Override
	public void center() {
		super.center();
		if (defaultToUpload) {
			uploadClicked();
		}
	}
}