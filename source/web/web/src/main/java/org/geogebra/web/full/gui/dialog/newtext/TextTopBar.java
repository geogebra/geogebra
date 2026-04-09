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

package org.geogebra.web.full.gui.dialog.newtext;

import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.dialog.handler.TextStyle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.objects.FontSizeProperty.FontSize;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.euclidian.quickstylebar.components.IconButtonWithProperty;
import org.geogebra.web.full.gui.dialog.text.TextEditPanel;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.gwtproject.user.client.ui.FlowPanel;

public class TextTopBar extends FlowPanel {
	private final AppWFull appW;
	private final GeoText geoText;
	private final GeoElementPropertiesFactory propertiesFactory;
	private final TextStyle textStyle;
	private final Runnable previewUpdater;
	private NamedEnumeratedPropertyListFacade<?, ?> fontSizeProperty;
	private InsertPopup insertPopup;

	/**
	 * @param appW {@link AppWFull}
	 * @param geoText text element
	 * @param textEditPanel {@link TextEditPanel}
	 * @param previewUpdater runnable to update the preview panel
	 */
	public TextTopBar(AppWFull appW, GeoText geoText, TextEditPanel textEditPanel,
			Runnable previewUpdater) {
		this.appW = appW;
		this.geoText = geoText;
		propertiesFactory = appW.getGeoElementPropertiesFactory();
		this.textStyle = new TextStyle();
		textStyle.setLatex(geoText.isLaTeX());
		this.previewUpdater = previewUpdater;
		addStyleName("textTopBar");
		createTopBar(textEditPanel);
	}

	private void createTopBar(TextEditPanel textEditPanel) {
		Localization loc = appW.getLocalization();
		List<GeoElement> geoList = List.of(geoText);

		Property backgroundColorProperty
				= propertiesFactory.createTextBackgroundColorProperty(loc, geoList);
		add(createIconButtonWithProperty(backgroundColorProperty, geoList, textStyle::setBgColor));

		add(BaseWidgetFactory.INSTANCE.newDivider(true));

		Property fontColorProperty = propertiesFactory.createTextFontColorProperty(loc, geoList);
		add(createIconButtonWithProperty(fontColorProperty, geoList, textStyle::setFontColor));

		fontSizeProperty = propertiesFactory.createFontSizeProperty(loc, geoList);
		IconButtonWithProperty fontSizeButton = new IconButtonWithProperty(
				appW, "gwt-PopupPanel contextSubMenu",
				appW.getPropertiesIconResource().getImageResource(fontSizeProperty),
				fontSizeProperty.getName(),
				geoList, true, fontSizeProperty);
		fontSizeButton.addStyleName("small");
		add(fontSizeButton);

		BooleanProperty boldProperty = propertiesFactory.createBoldProperty(loc, geoList);
		add(createIconToggleButton(boldProperty, textStyle::setBold));

		BooleanProperty italicProperty = propertiesFactory.createItalicProperty(loc, geoList);
		add(createIconToggleButton(italicProperty, textStyle::setItalic));

		BooleanProperty serifProperty = propertiesFactory.createSerifProperty(loc, geoList);
		add(createIconToggleButton(serifProperty, textStyle::setSerif));

		add(BaseWidgetFactory.INSTANCE.newDivider(true));

		IconButton insertButton = new IconButton(appW, () -> {},
				new ImageIconSpec(MaterialDesignResources.INSTANCE.add_black()), "Insert");
		insertButton.addFastClickHandler(source -> {
			if (insertPopup == null) {
				insertPopup = new InsertPopup(appW, textEditPanel, previewUpdater);
				insertPopup.addCloseHandler(event -> insertButton.setActive(false));
			}
			toggleInsertPopup(insertButton);
		});
		add(insertButton);
	}

	private void toggleInsertPopup(IconButton insertButton) {
		if (insertPopup.isShowing()) {
			insertPopup.hide();
		} else {
			appW.closePopups();
			appW.registerPopup(insertPopup);
			insertPopup.show();
			int left = (int) (insertButton.getAbsoluteLeft() - appW.getAbsLeft());
			int top = (int) (insertButton.getAbsoluteTop() + insertButton.getOffsetHeight()
					- appW.getAbsTop());
			insertPopup.setPopupPosition(left, top);
		}
		insertButton.setActive(insertPopup.isShowing());
	}

	private IconButtonWithProperty createIconButtonWithProperty(Property property,
			List<GeoElement> geos, Consumer<GColor> consumer) {
		IconButtonWithProperty iconButtonWithProperty = new IconButtonWithProperty(appW,
				"colorStyle",
				appW.getPropertiesIconResource().getImageResource(property),
				property.getName(),
				geos, true, property);
		iconButtonWithProperty.addPopupHandler((valuedProperty, value) -> {
			appW.closePopups();
			valuedProperty.setValue(value);
			consumer.accept(value);
			previewUpdater.run();
		});
		iconButtonWithProperty.addStyleName("small");
		return iconButtonWithProperty;
	}

	private IconButton createIconToggleButton(BooleanProperty property,
			Consumer<Boolean> consumer) {
		IconButton toggleButton = new IconButton(appW, null,
				appW.getPropertiesIconResource().getImageResource(property), property.getName());
		toggleButton.setActive(property.getValue());
		toggleButton.addFastClickHandler(ignore -> {
			appW.closePopups();
			property.setValue(!toggleButton.isActive());
			toggleButton.setActive(!toggleButton.isActive());
			consumer.accept(property.getValue());
			previewUpdater.run();
		});
		return toggleButton;
	}

	/**
	 * @return {@link TextStyle}
	 */
	public TextStyle getTextStyle() {
		textStyle.setTextSize((FontSize) fontSizeProperty.getValue());
		return textStyle;
	}
}
