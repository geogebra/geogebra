package geogebra.touch.gui.dialogs;

import geogebra.common.main.Localization;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.elements.StandardButton;
import geogebra.touch.gui.laf.DefaultResources;
import geogebra.touch.model.GuiModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.gwtphonegap.client.camera.PictureCallback;
import com.googlecode.gwtphonegap.client.camera.PictureOptions;

public class ImageDialog extends DialogT {

	private TouchApp app;
	private GuiModel guiModel;
	private VerticalPanel dialogPanel;
	private final Localization loc;
	private Button cancelButton = new Button();
	private Label title = new Label();
	private static DefaultResources LafIcons = TouchEntryPoint.getLookAndFeel()
			.getIcons();

	public ImageDialog(TouchApp app) {
		super(true, false);
		this.app = app;
		this.guiModel = ((TabletGUI) this.app.getTouchGui()).getTouchModel().getGuiModel();
		this.loc = app.getLocalization();
		this.dialogPanel = new VerticalPanel();
		this.setGlassEnabled(true);

		addTitle();
		addInsertChoice();
		addCancelButton();
		this.add(this.dialogPanel);
		this.setStyleName("imageDialog");
	}

	private void addTitle() {
		this.title.setStyleName("title");
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.add(this.title);
		titlePanel.setStyleName("titlePanel");
		this.dialogPanel.add(titlePanel);
	}

	private void addInsertChoice() {
		HorizontalPanel insertChoicePanel = new HorizontalPanel();
		insertChoicePanel.setStyleName("insertChoicePanel");

		// TODO use/search for new icons
		StandardButton insertFromCamera = new StandardButton(LafIcons.document_new());
		insertFromCamera.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				ImageDialog.this.insertPictureFromCamera();
			}
		});

		StandardButton insertFromGallery = new StandardButton(LafIcons.document_open());
		insertFromGallery.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				ImageDialog.this.insertPictureFromGallery();
			}
		});

		insertChoicePanel.add(insertFromCamera);
		insertChoicePanel.add(insertFromGallery);

		this.dialogPanel.add(insertChoicePanel);
	}

	private void addCancelButton() {
		this.cancelButton.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				ImageDialog.this.hide();
			}
		}, ClickEvent.getType());

		HorizontalPanel buttonContainer = new HorizontalPanel();
		buttonContainer.setStyleName("buttonPanel");
		this.cancelButton.addStyleName("last");
		buttonContainer.add(this.cancelButton);

		this.dialogPanel.add(buttonContainer);
	}

	/**
	 * Opens the device-camera and adds the picture (String pictureBase64) to the canvas.
	 */
	void insertPictureFromCamera() {
		this.hide();
		TouchEntryPoint.getPhoneGap().getCamera().getPicture(new PictureOptions(50),
				new PictureCallback() {

			@Override
			public void onSuccess(String pictureBase64) {
				ImageDialog.this.pictureToCanvas(pictureBase64);
			}

			@Override
			public void onFailure(String arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * opens a dialog to choose a saved picture (from Drive, Gallery or Camera-pictures)
	 * and adds picture (String pictureBase64) to canvas.
	 */
	void insertPictureFromGallery() {
		this.hide();
		PictureOptions options = new PictureOptions(25);
		options.setDestinationType(PictureOptions.DESTINATION_TYPE_DATA_URL);
		options.setSourceType(PictureOptions.PICTURE_SOURCE_TYPE_PHOTO_LIBRARY);
		TouchEntryPoint.getPhoneGap().getCamera().getPicture(options,
				new PictureCallback() {

			@Override
			public void onSuccess(String pictureBase64) {
				ImageDialog.this.pictureToCanvas(pictureBase64);
			}

			@Override
			public void onFailure(String arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	/**
	 * adds picture to canvas
	 * @param pictureBase64 string of picture
	 */
	void pictureToCanvas(String pictureBase64) {
		((TabletGUI)this.app.getTouchGui()).getTouchModel().addPictureToCanvas("picture", pictureBase64);
	}

	@Override
	public void show() {
		super.show();
		super.center();
		this.setLabels();
		this.guiModel.setActiveDialog(this);
	}

	@Override
	public void hide() {
		super.hide();
		this.guiModel.setActiveDialog(null);
	}

	public void setLabels() {
		this.cancelButton.setText(this.loc.getMenu("Cancel"));
		this.title.setText(this.loc.getMenu("InsertImageFrom"));
	}
}
