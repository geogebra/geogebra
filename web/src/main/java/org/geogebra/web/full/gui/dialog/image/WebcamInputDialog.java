package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
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
	private Button takePictureBtn;
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

		webcamInputPanel = new WebCamInputPanel(app1, this);
		inputPanel.add(webcamInputPanel);

		takePictureBtn = new Button("");
		takePictureBtn.setEnabled(true);
		closeBtn = new Button("");
		closeBtn.addStyleName("cancelBtn");
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		buttonPanel.add(takePictureBtn);
		buttonPanel.add(closeBtn);

		add(mainPanel);
		mainPanel.add(inputPanel);
		mainPanel.add(buttonPanel);

		addStyleName("GeoGebraPopup");
		addStyleName("image");
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
		Localization loc = app1.getLocalization();
		getCaption().setText(loc.getMenu("Camera"));
		takePictureBtn.setText(loc.getMenu("takepicture")); // screenshot
		closeBtn.setText(loc.getMenu("Close")); // close
	}

	@Override
	protected void addResizeHandler() {
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				resize();
			}
		});
	}

	/**
	 * resizes the video and its container
	 */
	public void resize() {
		if (!isShowing()) {
			return;
		}
		double width = webcamInputPanel.getVideoWidth();
		double height = webcamInputPanel.getVideoHeight();
		double ratio = height / width;
		if (app1.getHeight() < app1.getWidth()) {
			height = app1.getHeight() / 2.5;
			width = height / ratio;
			if (width < 250) {
				width = 250;
				height = width * ratio;
			}
		} else {
			width = Math.max(250, app1.getWidth() / 2.5);
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
				app1.imageDropHappened(name, data, "");
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
		app1.getImageManager().setPreventAuxImage(false);
		app1.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
				ModeSetter.TOOLBAR);
		super.hide();
	}

	@Override
	public void hide(boolean autoClosed, boolean setFocus) {
		super.hide(autoClosed, setFocus);
		app1.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
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
}
