package org.geogebra.web.touch.gui.dialog.image;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.touch.PhoneGapManager;
import org.geogebra.web.web.gui.dialog.image.UploadImageDialog;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwtphonegap.client.camera.PictureCallback;
import com.googlecode.gwtphonegap.client.camera.PictureOptions;

/**
 *
 */
public class ImageInputDialogT extends UploadImageDialog {
	private static final int PREVIEW_HEIGHT = 155;
	private static final int PREVIEW_WIDTH = 213;
	private static final int PICTURE_QUALITY = 25;
	private SimplePanel cameraPanel;
	private SimplePanel picturePanel;
	private Label camera;
	private String pictureFromCameraString = "";
	private String pictureFromFileString = "";
	private FlowPanel filePanel;
	private StandardButton chooseFromFile;
	private PictureOptions options;
	private boolean cameraIsActive;
	private PictureCallback pictureCallback;

	
	/**
	 * @param app {@link App}
	 */
	public ImageInputDialogT(final App app) {
		super((AppW) app, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		this.pictureCallback = new PictureCallback() {
			
			@Override
			public void onSuccess(final String pictureBase64) {
				setPicturePreview(pictureBase64);
			}

			@Override
			public void onFailure(final String arg0) {
				ToolTipManagerW.sharedInstance().showBottomMessage(
						"Couldn't open chosen image", true, (AppW) app);
			}
		};
		
		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)){
			exportJavascriptMethods();
		}
	}

	@Override
	protected void initGUI() {
		super.initGUI();
		listPanel.add(camera = new Label(""));
		
		initFilePanel();
		initCameraPanel();
	}
	
	private void initCameraPanel() {
		cameraPanel = new SimplePanel();
		cameraPanel.setStyleName("inputPanel");
		cameraPanel.setSize(PREVIEW_WIDTH+"px", PREVIEW_HEIGHT+"px");
    }

	private void initFilePanel() {
		this.options = new PictureOptions(ImageInputDialogT.PICTURE_QUALITY);
		this.options.setSourceType(PictureOptions.PICTURE_SOURCE_TYPE_SAVED_PHOTO_ALBUM);//.PICTURE_SOURCE_TYPE_PHOTO_LIBRARY);
		
		filePanel = new FlowPanel();
		filePanel.add(chooseFromFile = new StandardButton(
				app.getLocalization().getMenu("ChooseFromFile")));
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
		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)){
			openFromFileClickedNative();
		}else{
			PhoneGapManager.getPhoneGap().getCamera().getPicture(options, this.pictureCallback);
		}
	}
	
	private native void openFromFileClickedNative() /*-{
		if ($wnd.android) {
			$wnd.android.openFromFileClickedNative();
		}
	}-*/;
	
	@Override
	protected void initActions() {
		super.initActions();
		camera.addClickHandler(this);
	}
	
	@Override
	public void setLabels() {
		super.setLabels();
		//TODO Translation needed
		camera.setText("Camera");
	}
	
	@Override
    public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source == insertBtn) {
			if (location != null && !location.isLabelSet()) {
				location.setLabel(null);
	    	}
			if (this.cameraIsActive && !"".equals(this.pictureFromCameraString)) {
				app.imageDropHappened("devicePicture",
						this.pictureFromCameraString, "", location);
			} else if (!this.cameraIsActive
					&& !"".equals(this.pictureFromFileString)) {
				app.imageDropHappened("devicePicture",
						this.pictureFromFileString, "", location);
			}
	    	hide();
	    } else if (source == cancelBtn) {
	    	hide();
	    } else if (source == upload) {
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
		PictureOptions pictureOptions = new PictureOptions(ImageInputDialogT.PICTURE_QUALITY);
		pictureOptions.setAllowEdit(false);
		pictureOptions.setCorrectOrientation(true);

		if (app.has(Feature.TABLET_WITHOUT_CORDOVA)){
			getCameraPictureNative();
		}else{
			PhoneGapManager.getPhoneGap().getCamera().getPicture(
			pictureOptions,
			this.pictureCallback);
		}
    }

	private native void getCameraPictureNative() /*-{
		if ($wnd.android) {
			$wnd.android.getCameraPictureNative();
		}
	}-*/;

	/**
	 * @param pictureBase64 String
	 */
	void setPicturePreview(String pictureBase64) {
		if (cameraIsActive) {
			this.pictureFromCameraString = "data:image/jpg;base64," + pictureBase64;
			this.cameraPanel.clear();
	        this.cameraPanel.getElement().getStyle().setBackgroundImage("url('" + this.pictureFromCameraString + "')");
		} else {
			this.pictureFromFileString = "data:image/jpg;base64," + pictureBase64;
			this.picturePanel.clear();
	        this.picturePanel.getElement().getStyle().setBackgroundImage("url('" + this.pictureFromFileString + "')");
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
	 */
	public void catchImage(String data) {
		setPicturePreview(data);
	
	}

}
