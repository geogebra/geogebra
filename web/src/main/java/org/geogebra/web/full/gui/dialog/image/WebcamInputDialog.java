package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.webcam.WebcamDialogInterface;
import org.geogebra.web.shared.DialogBoxW;

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
public class WebcamInputDialog extends DialogBoxW implements WebcamDialogInterface,
	ClickHandler {

	private AppW appW;
	private FlowPanel mainPanel;
	private SimplePanel inputPanel;
	private WebCamInputPanel webcamInputPanel;
	private FlowPanel buttonPanel;
	private Button takePictureBtn;
	private Button closeBtn;

	/**
	 * @param app
	 *            application
	 */
	public WebcamInputDialog(AppW app) {
		super(app.getPanel(), app);
		this.appW = app;
		initGUI();
		initActions();
	}

	private void initGUI() {
		mainPanel = new FlowPanel();
		inputPanel = new SimplePanel();
		inputPanel.setStyleName("mowCameraSimplePanel");
		// dialog content panel
		webcamInputPanel = new WebCamInputPanel(appW, this);
		inputPanel.add(webcamInputPanel);
		// add button panel
		takePictureBtn = new Button("");
		takePictureBtn.setEnabled(true);
		closeBtn = new Button("");
		closeBtn.addStyleName("cancelBtn");
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		buttonPanel.add(takePictureBtn);
		buttonPanel.add(closeBtn);
		// build content
		add(mainPanel);
		mainPanel.add(inputPanel);
		mainPanel.add(buttonPanel);
		// style of dialog
		addStyleName("GeoGebraPopup");
		addStyleName("camera");
		setGlassEnabled(true);
	}

	private void initActions() {
		takePictureBtn.addClickHandler(this);
		closeBtn.addClickHandler(this);
		if (Browser.isMobile()) {
			this.setAutoHideEnabled(true);
		}
	}

	/**
	 * set button labels and dialog title
	 */
	public void setLabels() {
		Localization loc = appW.getLocalization();
		getCaption().setText(loc.getMenu("Camera"));
		takePictureBtn.setText(loc.getMenu("takepicture")); // screenshot
		closeBtn.setText(loc.getMenu("Close")); // close
	}

	@Override
	protected void onWindowResize() {
		resize();
	}

	@Override
	public void resize() {
		if (!isShowing()) {
			return;
		}
		double width = webcamInputPanel.getVideoWidth();
		double height = webcamInputPanel.getVideoHeight();
		double ratio = height / width;
		if (appW.getHeight() < appW.getWidth()) {
			height = appW.getHeight() / 2.5;
			width = height / ratio;
			if (width < 250) {
				width = 250;
				height = width * ratio;
			}
		} else {
			width = Math.max(250, appW.getWidth() / 2.5);
			height = width * ratio;
		}
		inputPanel.setHeight(height + "px");
		inputPanel.setWidth(width + "px");
		center();
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source == takePictureBtn) {
			String data = webcamInputPanel.getImageDataURL();
			String name = "webcam";
			if (data != null && !webcamInputPanel.isStreamEmpty()) {
				appW.imageDropHappened(name, data);
			}
		} else if (source == closeBtn) {
			hide();
		}
	}

	@Override
	public void hide() {
		if (this.webcamInputPanel != null) {
			this.webcamInputPanel.stopVideo();
		}
		appW.getImageManager().setPreventAuxImage(false);
		appW.getGuiManager().setMode(EuclidianConstants.MODE_SELECT_MOW,
				ModeSetter.TOOLBAR);
		super.hide();
	}

	@Override
	public void hide(boolean autoClosed, boolean setFocus) {
		super.hide(autoClosed, setFocus);
		appW.getGuiManager().setMode(EuclidianConstants.MODE_SELECT_MOW,
				ModeSetter.TOOLBAR);
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

	@Override
	public void onCameraSuccess() {
		// not used
	}

	@Override
	public void onCameraError() {
		// not used
	}
	
	@Override
	public void showAndResize() {
		show();
		resize();
		center();
	}
}
