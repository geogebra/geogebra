package org.geogebra.web.touch.gui.dialog.image;

import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.dialog.image.UploadImageDialog;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * image input dialog for touch
 */
public class ImageInputDialogT extends UploadImageDialog implements ClickHandler {
	private static final int PREVIEW_HEIGHT = 155;
	private static final int PREVIEW_WIDTH = 213;
	private SimplePanel cameraPanel;
	private SimplePanel picturePanel;
	private Label camera;
	private String pictureFromCameraString = "";
	private String pictureFromFileString = "";
	private FlowPanel filePanel;
	private boolean cameraIsActive;

	/**
	 * @param app
	 *            {@link App}
	 */
	public ImageInputDialogT(final AppW app) {
		super(app, PREVIEW_WIDTH, PREVIEW_HEIGHT);

		exportJavascriptMethods();

		setOnNegativeAction(() -> app.getImageManager().setPreventAuxImage(false));
		setOnPositiveAction(this::positiveAction);
	}

	private void positiveAction() {
		if (location != null && !location.isLabelSet()) {
			location.setLabel(null);
		}
		if (this.cameraIsActive
				&& !"".equals(this.pictureFromCameraString)) {
			((AppW) app).imageDropHappened("devicePicture",
					this.pictureFromCameraString);
		} else if (!this.cameraIsActive
				&& !"".equals(this.pictureFromFileString)) {
			((AppW) app).imageDropHappened("devicePicture",
					this.pictureFromFileString);
		}
	}

	@Override
	protected void buildContent() {
		super.buildContent();
		listPanel.add(camera = new Label(app.getLocalization().getMenu("Camera")));

		initFilePanel();
		initCameraPanel();
	}

	private void initCameraPanel() {
		cameraPanel = new SimplePanel();
		cameraPanel.setStyleName("inputPanel");
		cameraPanel.setSize(PREVIEW_WIDTH + "px", PREVIEW_HEIGHT + "px");
	}

	private void initFilePanel() {
		filePanel = new FlowPanel();
		StandardButton chooseFromFile;
		filePanel.add(chooseFromFile = new StandardButton(
				((AppW) app).getLocalization().getMenu("ChooseFromFile")));
		chooseFromFile.addStyleName("gwt-Button");
		chooseFromFile.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				openFromFileClicked();
			}
		});

		filePanel.add(picturePanel = new SimplePanel());
		picturePanel.setStyleName("inputPanel");
		picturePanel.setSize(PREVIEW_WIDTH + "px", PREVIEW_HEIGHT + "px");
	}

	/**
	 * Callback for file open button
	 */
	void openFromFileClicked() {
		openFromFileClickedNative();
	}

	private native void openFromFileClickedNative() /*-{
		if ($wnd.android) {
			$wnd.android.openFromFileClickedNative();
		}
	}-*/;

	protected void initActions() {
		upload.addClickHandler(this);
		camera.addClickHandler(this);
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source == upload) {
			uploadClicked();
		} else if (source == camera) {
			cameraClicked();
		}
	}

	@Override
	protected void uploadClicked() {
		if (this.pictureFromFileString != null
				&& !"".equals(this.pictureFromFileString)) {
			imageAvailable();
		} else {
			imageUnavailable();
		}
		this.cameraIsActive = false;
		this.upload.addStyleDependentName("highlighted");
		this.camera.removeStyleDependentName("highlighted");
		this.inputPanel.setWidget(this.filePanel);
	}

	/**
	 * Callback for camera button
	 */
	protected void cameraClicked() {
		if (this.pictureFromCameraString != null
				&& !"".equals(this.pictureFromCameraString)) {
			imageAvailable();
		} else {
			imageUnavailable();
		}
		this.cameraIsActive = true;
		this.camera.addStyleDependentName("highlighted");
		this.upload.removeStyleDependentName("highlighted");
		this.inputPanel.setWidget(this.cameraPanel);
		getCameraPictureNative();
	}

	private native void getCameraPictureNative() /*-{
		if ($wnd.android) {
			$wnd.android.getCameraPictureNative();
		}
	}-*/;

	/**
	 * @param pictureBase64
	 *            String
	 */
	void setPicturePreview(String pictureBase64) {
		if (cameraIsActive) {
			this.pictureFromCameraString = StringUtil.jpgMarker + pictureBase64;
			this.cameraPanel.clear();
			this.cameraPanel.getElement().getStyle().setBackgroundImage(
					"url('" + this.pictureFromCameraString + "')");
		} else {
			this.pictureFromFileString = StringUtil.jpgMarker + pictureBase64;
			this.picturePanel.clear();
			this.picturePanel.getElement().getStyle().setBackgroundImage(
					"url('" + this.pictureFromFileString + "')");
		}

		imageAvailable();
	}

	@Override
	public void hide() {
		super.hide();
		this.cameraPanel.getElement().getStyle().setBackgroundImage("none");
		this.picturePanel.getElement().getStyle().setBackgroundImage("none");
		this.pictureFromCameraString = "";
		this.pictureFromFileString = "";
	}

	private native void exportJavascriptMethods() /*-{
		var that = this;
		$wnd.imageInputDialogT_catchImage = $entry(function(data) {
			that.@org.geogebra.web.touch.gui.dialog.image.ImageInputDialogT::catchImage(Ljava/lang/String;)(data);
		});
	}-*/;

	/**
	 * this method is called through js (see exportGeoGebraAndroidMethods())
	 * 
	 * @param data
	 *            image data
	 */
	public void catchImage(String data) {
		setPicturePreview(data);

	}

}
