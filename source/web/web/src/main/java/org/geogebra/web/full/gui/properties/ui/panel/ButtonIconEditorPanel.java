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
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;

public class ButtonIconEditorPanel extends FlowPanel implements ConfigurationUpdateDelegate {
	private final AppW appW;
	private final ButtonIconEditor buttonIconEditor;
	private final ImagePicker imagePickerProperty;
	private IconButtonPanel iconButtonPanel;
	private ImagePickerPanel imagePickerPanel;

	/**
	 * Create the panel for buttons icon settings
	 * @param appW {@link AppW}
	 * @param buttonIconEditor {@link ButtonIconEditor}
	 */
	public ButtonIconEditorPanel(AppW appW, ButtonIconEditor buttonIconEditor) {
		addStyleName("buttonIconEditor");
		this.appW = appW;
		this.buttonIconEditor = buttonIconEditor;
		imagePickerProperty = buttonIconEditor.getTrailingImagePicker();
		createButtonIconEditor();
		buttonIconEditor.setConfigurationUpdateDelegate(this);
	}

	private void createButtonIconEditor() {
		iconButtonPanel = new IconButtonPanel(appW, buttonIconEditor.getLeadingIconButtonRow(),
				false, this::updateImage);
		add(iconButtonPanel);

		imagePickerPanel = new ImagePickerPanel(appW, imagePickerProperty);
		add(imagePickerPanel);
	}

	private void uploadImage(String name, String data) {
		String filePath = ImageManagerW.getMD5FileName(name, data);
		appW.getImageManager().addExternalImage(filePath, data);
		appW.getImageManager().triggerSingleImageLoading(filePath, appW.getKernel());
		MyImage myImage = appW.getImageManager().getExternalImage(filePath);
		if (myImage != null) {
			imagePickerProperty.setImage(myImage, filePath);
			imagePickerPanel.updatePreview(data);
		}
	}

	/**
	 * Update image to the selected one from default icons.
	 */
	public void updateImage() {
		int index = buttonIconEditor.getSelectedIndex();
		PropertyResource propertyResource = buttonIconEditor.getIconAt(index);
		ImageIconSpec imageIconSpec = (ImageIconSpec) ((AppWFull) appW)
				.getPropertiesIconResource().getImageResource(propertyResource);
		SVGResource svg = imageIconSpec.getImage();
		uploadImage(svg.getName() + ".svg", svg.getSafeUri().asString());
		imagePickerPanel.updateCustomIconPanelVisibility(true);
	}

	@Override
	public void configurationUpdated() {
		if (buttonIconEditor.getLeadProperty().getValue()) {
			buttonIconEditor.setSelectedIndex(0);
			iconButtonPanel.deselectAllBut(0, 0);
			updateImage();
		} else {
			buttonIconEditor.setDefaultIcon("");
		}
	}
}
