package org.geogebra.web.touch.gui.dialog.image;

import org.geogebra.common.main.App;
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
	private final int PICTURE_QUALITY = 25;
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
		this.options = new PictureOptions(this.PICTURE_QUALITY);
		this.options.setSourceType(PictureOptions.PICTURE_SOURCE_TYPE_SAVED_PHOTO_ALBUM);//.PICTURE_SOURCE_TYPE_PHOTO_LIBRARY);
		
		filePanel = new FlowPanel();
		filePanel.add(chooseFromFile = new StandardButton(app.getMenu("ChooseFromFile")));
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

	void openFromFileClicked() {
		PhoneGapManager.getPhoneGap().getCamera().getPicture(options, this.pictureCallback);
	}
	
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
			if (!loc.isLabelSet()) {
	    		loc.setLabel(null);
	    	}
			if (this.cameraIsActive && !this.pictureFromCameraString.equals("")) {
		    	app.imageDropHappened("devicePicture", this.pictureFromCameraString, "", loc);
			} else if (!this.cameraIsActive && !this.pictureFromFileString.equals("")) {
				app.imageDropHappened("devicePicture", this.pictureFromFileString, "", loc);
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
		if (this.pictureFromFileString != null && !this.pictureFromFileString.equals("")) {
			imageAvailable();
		} else {
			imageUnavailable();
		}
		this.cameraIsActive = false;
		this.upload.addStyleDependentName("highlighted");
		this.camera.removeStyleDependentName("highlighted");
		this.inputPanel.setWidget(this.filePanel);
	}
	
	protected void cameraClicked() {
		if (this.pictureFromCameraString != null && !this.pictureFromCameraString.equals("")) {
			imageAvailable();
		} else {
			imageUnavailable();
		}
		this.cameraIsActive = true;
		this.camera.addStyleDependentName("highlighted");
		this.upload.removeStyleDependentName("highlighted");
		this.inputPanel.setWidget(this.cameraPanel);
		PhoneGapManager.getPhoneGap().getCamera().getPicture(new PictureOptions(this.PICTURE_QUALITY), this.pictureCallback);
    }

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

}
