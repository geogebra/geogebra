/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.properties.ui.panel;

import static org.geogebra.common.properties.PropertyView.*;

import org.geogebra.common.awt.MyImage;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.image.UploadImagePanel;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ImageManagerW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;

public class ImagePickerPanel extends FlowPanel {
	private final AppW appW;
	private final ImagePicker imagePicker;
	private StandardButton fileChooser;
	private FlowPanel customIconButtonPanel;
	private Image imagePreview;
	private Label imageName;

	/**
	 * Builds an image picker panel with choose file button, or selected image preview
	 * with edit and delete buttons.
	 * @param appW {@link AppW}
	 * @param imagePicker {@link ImagePicker}
	 */
	public ImagePickerPanel(AppW appW, ImagePicker imagePicker) {
		this.appW = appW;
		this.imagePicker = imagePicker;

		buildImagePicker();
	}

	private void buildImagePicker() {
		fileChooser = new StandardButton(appW.getLocalization()
				.getMenu(imagePicker.getChooseFromFileLabel()));
		fileChooser.addStyleName("materialOutlinedButton");
		fileChooser.addFastClickHandler(event -> UploadImagePanel.getUploadButton(appW,
				this::uploadImageUpdateUI).click());
		add(fileChooser);

		buildFileEditPanel();
	}

	private void buildFileEditPanel() {
		customIconButtonPanel = new FlowPanel();
		customIconButtonPanel.addStyleName("customIconButtonPanel");

		imagePreview = new Image();
		imagePreview.addStyleName("imagePreview");
		imageName = BaseWidgetFactory.INSTANCE.newPrimaryText("", "imageName");
		customIconButtonPanel.add(imagePreview);
		customIconButtonPanel.add(imageName);

		IconButton editButton = new IconButton(appW, () -> UploadImagePanel.getUploadButton(appW,
				this::uploadImageUpdateUI).click(),
				new ImageIconSpec(MaterialDesignResources.INSTANCE.edit_black()), "edit");
		customIconButtonPanel.add(editButton);

		IconButton deleteButton = new IconButton(appW, () -> {
			imagePicker.clearImage();
			updateCustomIconPanelVisibility(true);
		}, new ImageIconSpec(MaterialDesignResources.INSTANCE.delete_black()), "delete");
		customIconButtonPanel.add(deleteButton);

		add(customIconButtonPanel);
		updateCustomIconPanelVisibility(true);
	}

	private void uploadImageUpdateUI(String name, String data) {
		uploadImage(name, data);
		updateCustomIconPanelVisibility(false);
	}

	private void uploadImage(String name, String data) {
		String filePath = ImageManagerW.getMD5FileName(name, data);
		appW.getImageManager().addExternalImage(filePath, data);
		appW.getImageManager().triggerSingleImageLoading(filePath, appW.getKernel());
		MyImage myImage = appW.getImageManager().getExternalImage(filePath);
		if (myImage != null) {
			imagePicker.setImage(myImage, filePath);
			updatePreview(data);
		}
	}

	/**
	 * Update image preview.
	 * @param previewUrl image preview URL
	 */
	public void updatePreview(String previewUrl) {
		imagePreview.setUrl(previewUrl);
		imageName.setText(imagePicker.getFileName());
	}

	/**
	 * Updates the visibility of file chooser button/custom file panel.
	 * @param fileChooserShown whether the choose file button should be shows,
	 * or the custom file panel
	 */
	public void updateCustomIconPanelVisibility(boolean fileChooserShown) {
		fileChooser.setVisible(fileChooserShown);
		customIconButtonPanel.setVisible(!fileChooserShown);
	}
}
