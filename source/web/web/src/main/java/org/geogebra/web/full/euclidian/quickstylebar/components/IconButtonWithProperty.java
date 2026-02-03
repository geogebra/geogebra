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

package org.geogebra.web.full.euclidian.quickstylebar.components;

import static org.geogebra.web.full.euclidian.quickstylebar.QuickStyleBar.POPUP_MENU_DISTANCE;
import static org.geogebra.web.full.euclidian.quickstylebar.QuickStyleBar.QUICK_STYLE_BAR_HEIGHT;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertySupplier;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.impl.facade.ColorPropertyListFacade;
import org.geogebra.common.properties.impl.facade.FlagListPropertyListFacade;
import org.geogebra.common.properties.impl.facade.NamedEnumeratedPropertyListFacade;
import org.geogebra.common.properties.impl.facade.RangePropertyListFacade;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;
import org.geogebra.common.properties.impl.objects.BorderColorProperty;
import org.geogebra.common.properties.impl.objects.BorderThicknessProperty;
import org.geogebra.common.properties.impl.objects.CellBorderThicknessProperty;
import org.geogebra.common.properties.impl.objects.NotesThicknessProperty;
import org.geogebra.common.properties.impl.objects.TextBackgroundColorProperty;
import org.geogebra.web.full.euclidian.LabelSettingsPanel;
import org.geogebra.web.full.euclidian.LabelValuePanel;
import org.geogebra.web.full.euclidian.quickstylebar.PropertyWidgetAdapter;
import org.geogebra.web.full.gui.toolbar.mow.popupcomponents.ColorChooserPanel;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.general.GeneralIcon;
import org.gwtproject.user.client.ui.FlowPanel;

public class IconButtonWithProperty extends IconButton {
	private final AppW appW;
	private final List<GeoElement> geos;
	private GPopupPanel propertyPopup;
	private SliderWithProperty lineThicknessSlider;
	private final PropertyWidgetAdapter widgetAdapter;
	private PopupColorHandler popupHandler;

	/**
	 * Constructor
	 * @param appW - application
	 * @param className - class name
	 * @param icon - svg resource of button
	 * @param ariaLabel - aria label
	 * @param geos - geo elements
	 * @param closePopupOnAction - weather should close popup after clicking on popup element
	 * @param properties - array of applicable properties
	 */
	public IconButtonWithProperty(AppW appW, String className, IconSpec icon, String ariaLabel,
			List<GeoElement> geos, boolean closePopupOnAction, PropertySupplier... properties) {
		super(appW, icon, ariaLabel, ariaLabel, () -> {}, null);
		this.appW = appW;
		this.geos = geos;
		widgetAdapter = new PropertyWidgetAdapter(appW, closePopupOnAction);
		AriaHelper.setAriaHasPopup(this);

		buildGUI(properties);
		if (className != null) {
			propertyPopup.addStyleName(className);
		}
		addHandlers();
	}

	private void addHandlers() {
		addFastClickHandler((source) -> {
			if (propertyPopup.isShowing()) {
				propertyPopup.hide();
			} else {
				update();
				showPropertyPopup();
			}
			AriaHelper.setAriaExpanded(this, propertyPopup.isShowing());
		});

		propertyPopup.addCloseHandler((event) -> {
			setActive(false);
			AriaHelper.setAriaExpanded(this, false);
		});
	}

	private void buildGUI(PropertySupplier... properties) {
		initPropertyPopup();
		FlowPanel propertyPanel = new FlowPanel();

		for (PropertySupplier property : properties) {
			processProperty(property, propertyPanel);
		}

		propertyPopup.add(propertyPanel);
	}

	private void processProperty(PropertySupplier propertySupplier, FlowPanel parent) {
		Property property = propertySupplier.get();
		if (property instanceof IconsEnumeratedProperty) {
			FlowPanel enumeratedPropertyButtonPanel = widgetAdapter.getIconListPanel(
					(IconsEnumeratedProperty<?>) property, propertySupplier, (index) -> {
						if (lineThicknessSlider != null) {
							lineThicknessSlider.setLineType(index);
						}
						setIcon(((AppWFull) appW).getPropertiesIconResource().getImageResource(
								((IconsEnumeratedProperty<?>) property).getValueIcons()[index]));
					});
			parent.add(enumeratedPropertyButtonPanel);
		}

		if (property instanceof NamedEnumeratedPropertyListFacade) {
			GPopupMenuW fontSizeMenu = widgetAdapter.getMenuWidget(
					(NamedEnumeratedPropertyListFacade<?, ?>) property);
			parent.add(fontSizeMenu.getPopupMenu());
		}

		if (property instanceof ColorPropertyListFacade<?>) {
			ColorPropertyListFacade<?> colorProperty =
					(ColorPropertyListFacade<?>) property;
			ColorChooserPanel colorPanel = new ColorChooserPanel(appW,
					colorProperty.getValues(), color -> {
				if (popupHandler != null) {
					ColorPropertyListFacade<?> updatedProperty =
							(ColorPropertyListFacade<?>) propertySupplier.updateAndGet();
					popupHandler.fireActionPerformed(updatedProperty, color);
				}
			});
			if (colorProperty.getFirstProperty() instanceof BorderColorProperty) {
				colorPanel.addStyleName("withMargin");
			}
			colorPanel.updateColorSelection(colorProperty.getFirstProperty().getValue());
			parent.add(colorPanel);

			if (colorProperty.getFirstProperty() instanceof TextBackgroundColorProperty) {
				StandardButton noColorButton = new StandardButton(
						appW.getGeneralIconResource().getImageResource(GeneralIcon.NO_COLOR),
						appW.getLocalization().getMenu("noColor"), 24, 24);
				noColorButton.addStyleName("noColBtn");
				noColorButton.addFastClickHandler(source -> {
					if (popupHandler != null) {
						popupHandler.fireActionPerformed(colorProperty, null);
					}
				});
				parent.add(noColorButton);
			}
		}
		if (property instanceof RangePropertyListFacade<?>) {
			RangePropertyListFacade<?> rangeProperty = (RangePropertyListFacade<?>) property;
			RangeProperty<?> firstProperty = rangeProperty.getFirstProperty();
			if (firstProperty instanceof NotesThicknessProperty) {
				lineThicknessSlider = widgetAdapter.getSliderWidget(rangeProperty,
						propertySupplier, geos.get(0));
				parent.add(lineThicknessSlider);
			} else if (firstProperty instanceof CellBorderThicknessProperty
					|| firstProperty instanceof BorderThicknessProperty) {
				FlowPanel borderThickness = widgetAdapter.getBorderThicknessWidget(
						rangeProperty);
				parent.add(borderThickness);
			} else {
				SliderWithProperty sliderWithProperty = widgetAdapter.getSliderWidget(
						rangeProperty, propertySupplier, geos.get(0));
				parent.add(sliderWithProperty);
			}
		}

		if (property instanceof StringPropertyListFacade<?>) {
			LabelValuePanel labelPanel =
					new LabelValuePanel(appW, (StringPropertyListFacade<?>) property, geos);
			propertyPopup.addCloseHandler(labelPanel);
			parent.add(labelPanel);
		}

		if (property instanceof FlagListPropertyListFacade<?> valuedProperty) {
			LabelSettingsPanel labelStylePanel = widgetAdapter.getLabelPanel(valuedProperty);
			parent.add(labelStylePanel);
		}
	}

	private void initPropertyPopup() {
		if (propertyPopup == null) {
			propertyPopup = new GPopupPanel(false, appW.getAppletFrame(), appW);
			propertyPopup.setStyleName("quickStyleBarPopup");
		}
	}

	private void update() {
		if (lineThicknessSlider != null) {
			lineThicknessSlider.setLineColor(geos.get(0).getObjectColor());
		}
	}

	private void showPropertyPopup() {
		appW.closePopups();
		propertyPopup.show();
		positionPopup();
		appW.registerPopup(propertyPopup);
	}

	private void positionPopup() {
		int left = (int) (getAbsoluteLeft() - appW.getAbsLeft());
		int top = (int) (getAbsoluteTop() + getOffsetHeight() - appW.getAbsTop())
				+ 2 * POPUP_MENU_DISTANCE;

		if (top + propertyPopup.getOffsetHeight() > appW.getHeight()) {
			top = top - propertyPopup.getOffsetHeight()
					- 2 * POPUP_MENU_DISTANCE - QUICK_STYLE_BAR_HEIGHT;
		}
		if (left + propertyPopup.getOffsetWidth() > appW.getWidth()) {
			left = (int) (appW.getWidth() - propertyPopup.getOffsetWidth());
		}

		propertyPopup.setPopupPosition(left, top);
	}

	/**
	 * @param popupHandler {@link PopupColorHandler}
	 */
	public void addPopupHandler(PopupColorHandler popupHandler) {
		this.popupHandler = popupHandler;
	}

	/**
	 * close popup of button if it doesn't contain sliders
	 */
	public void closePopup() {
		if (propertyPopup != null && Dom.querySelectorForElement(
				propertyPopup.getElement(), "[type=range]") == null) {
			propertyPopup.hide();
		}
	}
}
