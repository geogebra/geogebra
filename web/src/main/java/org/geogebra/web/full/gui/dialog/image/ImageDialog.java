package org.geogebra.web.full.gui.dialog.image;

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.MessagePanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;
import org.geogebra.web.shared.components.tab.ComponentTab;
import org.geogebra.web.shared.components.tab.TabData;

public class ImageDialog extends ComponentDialog {

	/**
	 * base dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 */
	public ImageDialog(AppW app, DialogData dialogData) {
		super(app, dialogData, false, true);
		addStyleName("imageDialog");
		buildGUI();
	}

	private void buildGUI() {
		InfoErrorData uploadData = new InfoErrorData(null, "ImageDialog.UploadImageMsg",
				"ImageDialog.Browse");
		ComponentInfoErrorPanel uploadPanel = new ComponentInfoErrorPanel(app.getLocalization(),
				uploadData, MaterialDesignResources.INSTANCE.upload(), this::onBrowse);
		TabData uploadTab = new TabData("Upload", uploadPanel);

		InfoErrorData cameraData = new InfoErrorData(null, "ImageDialog.UploadImageMsg",
				"ImageDialog.Browse");
		ComponentInfoErrorPanel cameraPanel = new ComponentInfoErrorPanel(app.getLocalization(),
				uploadData, MaterialDesignResources.INSTANCE.no_camera(), null);
		TabData cameraTab = new TabData("Upload", cameraPanel);

		MessagePanel panel2 = getErrorPanel(ToolbarSvgResources.INSTANCE.mode_camera_32(),
				"You denied access to the camera", "GeoGebra requires access to your camera."
						+ " Click the camera blocked icon in your browser's address bar.");
		TabData tab2 = new TabData("Camera", panel2);

		ComponentTab tab = new ComponentTab(new ArrayList<>(Arrays.asList(uploadTab, tab2)),
				app.getLocalization());
		addDialogContent(tab);
	}

	private void onBrowse() {
		// TODO browse local storage
	}

	private MessagePanel getErrorPanel(SVGResource img, String title, String text) {
		MessagePanel panel = new MessagePanel();
		panel.setImageUri(img);
		panel.setPanelTitle(title);
		panel.setPanelMessage(text);
		return panel;
	}
}
