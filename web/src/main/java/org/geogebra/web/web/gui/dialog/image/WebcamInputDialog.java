package org.geogebra.web.web.gui.dialog.image;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.DialogBoxW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Input Dialog for Webcam / Document Camera
 * 
 * @author Alicia
 */
public class WebcamInputDialog extends DialogBoxW implements ClickHandler {

	private AppW app1;
	private FlowPanel mainPanel;
	private SimplePanel inputPanel;
	private WebCamInputPanel webcamInputPanel;
	private FlowPanel buttonPanel;
	private Button screenshotBtn;
	private Button closeBtn;

	/**
	 * @param app
	 *            application
	 */
	public WebcamInputDialog(AppW app) {
		super(app.getPanel(), app);
		this.app1 = app;

		initGUI();
		initActions();
	}

	private void initGUI() {
		mainPanel = new FlowPanel();
		inputPanel = new SimplePanel();
		inputPanel.setStyleName("mowCameraSimplePanel");

		screenshotBtn = new Button("");
		screenshotBtn.setEnabled(true);
		closeBtn = new Button("");
		closeBtn.addStyleName("cancelBtn");
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		buttonPanel.add(screenshotBtn);
		buttonPanel.add(closeBtn);

		add(mainPanel);
		mainPanel.add(inputPanel);
		mainPanel.add(buttonPanel);

		addStyleName("GeoGebraPopup");
		addStyleName("image");
		setGlassEnabled(true);
	}

	private void initActions() {
		screenshotBtn.addClickHandler(this);
		closeBtn.addClickHandler(this);
	}

	/**
	 * adds the webcam input panel which starts the video
	 */
	public void initVideo() {
		webcamInputPanel = new WebCamInputPanel(app1, this);
		inputPanel.add(webcamInputPanel);
	}
	/**
	 * set button labels and dialog title
	 */
	public void setLabels() {
		Localization loc = app1.getLocalization();
		getCaption().setText(loc.getMenu("Camera"));
		screenshotBtn.setText(loc.getMenu("screenshot")); // screenshot
		closeBtn.setText(loc.getMenu("Close")); // close
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source == screenshotBtn) {
			String data = webcamInputPanel.getImageDataURL();
			String name = "webcam";
			if (data != null && !webcamInputPanel.isStreamEmpty()) {
				app1.imageDropHappened(name, data, "");
			}
		} else if (source == closeBtn) {
			app1.getImageManager().setPreventAuxImage(false);
			app1.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
					ModeSetter.TOOLBAR);
			hide();
		}
	}

	@Override
	public void hide() {
		if (this.webcamInputPanel != null) {
			this.webcamInputPanel.stopVideo();
		}
		super.hide();
	}

	@Override
	public void center() {
		super.center();
		setLabels();
	}

	/**
	 * starts the video
	 */
	public void startVideo() {
		webcamInputPanel.startVideo();
	}
}
