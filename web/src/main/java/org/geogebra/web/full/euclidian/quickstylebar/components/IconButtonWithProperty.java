package org.geogebra.web.full.euclidian.quickstylebar.components;

import static org.geogebra.web.full.euclidian.quickstylebar.QuickStylebar.POPUP_MENU_DISTANCE;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.impl.collections.ColorPropertyCollection;
import org.geogebra.common.properties.impl.collections.NamedEnumeratedPropertyCollection;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
import org.geogebra.common.properties.impl.objects.TextFontSizeProperty;
import org.geogebra.web.full.euclidian.quickstylebar.PropertiesIconAdapter;
import org.geogebra.web.full.euclidian.quickstylebar.PropertyWidgetAdapter;
import org.geogebra.web.full.gui.toolbar.mow.popupcomponents.ColorChooserPanel;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;

public class IconButtonWithProperty extends IconButton {
	private final static int PLUS_BUTTON_POSITION = 6;
	private final AppW appW;
	private GPopupPanel propertyPopup;
	private SliderWithProperty lineThicknessSlider;
	private final PropertyWidgetAdapter widgetAdapter;

	/**
	 * Constructor
	 * @param appW - application
	 * @param className - class name
	 * @param icon - svg resource of button
	 * @param ariaLabel - aria label
	 * @param geo - geo element
	 * @param closePopupOnAction - weather should close popup after clicking on popup element
	 * @param properties - array of applicable properties
	 */
	public IconButtonWithProperty(AppW appW, String className, SVGResource icon, String ariaLabel,
			GeoElement geo, boolean closePopupOnAction, Property... properties) {
		super(appW, icon, ariaLabel, ariaLabel, () -> {}, null);
		this.appW = appW;
		widgetAdapter = new PropertyWidgetAdapter(appW, closePopupOnAction);
		AriaHelper.setAriaHasPopup(this);

		buildGUI(geo, properties);
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
				showPropertyPopup();
			}
			AriaHelper.setAriaExpanded(this, propertyPopup.isShowing());
		});

		propertyPopup.addCloseHandler((event) -> {
			setActive(false);
			AriaHelper.setAriaExpanded(this, false);
		});
	}

	private void buildGUI(GeoElement geo, Property... properties) {
		initPropertyPopup();
		FlowPanel propertyPanel = new FlowPanel();

		for (Property property : properties) {
			processProperty(property, propertyPanel, geo);
		}

		propertyPopup.add(propertyPanel);
	}

	private void processProperty(Property property, FlowPanel parent, GeoElement geo) {
		if (property instanceof IconsEnumeratedProperty) {
			FlowPanel enumeratedPropertyButtonPanel = widgetAdapter.getIconListPanel(
					(IconsEnumeratedProperty<?>) property, (index) -> {
						if (lineThicknessSlider != null) {
							lineThicknessSlider.setLineType(index);
						}
						setIcon(PropertiesIconAdapter.getIcon(((IconsEnumeratedProperty<?>)
								property).getValueIcons()[index]));
					});
			parent.add(enumeratedPropertyButtonPanel);
		}

		if (property instanceof RangeProperty) {
			lineThicknessSlider = widgetAdapter.getSliderWidget(
					(RangePropertyCollection<?>) property, geo);
			parent.add(lineThicknessSlider);
		}

		if (property instanceof NamedEnumeratedPropertyCollection
				&& ((NamedEnumeratedPropertyCollection<?, ?>) property).getProperties()[0]
				instanceof TextFontSizeProperty) {
			GPopupMenuW fontSizeMenu = widgetAdapter.getMenuWidget((TextFontSizeProperty)
					((NamedEnumeratedPropertyCollection<?, ?>) property).getProperties()[0]);
			parent.add(fontSizeMenu.getPopupMenu());
		}

		if (property instanceof ColorPropertyCollection<?>) {
			FlowPanel colorPanel = new ColorChooserPanel(appW, getColorList(
					(ColorPropertyCollection<?>) property), color -> {
				((ColorPropertyCollection<?>) property).setValue(color);
				if (lineThicknessSlider != null) {
					lineThicknessSlider.setLineColor(color);
				}
			});
			parent.add(colorPanel);
		}
	}

	private List<GColor> getColorList(ColorPropertyCollection<?> property) {
		List<GColor> newColorList = new ArrayList<>(property.getValues().size() + 1);
		newColorList.addAll(property.getValues());
		newColorList.add(PLUS_BUTTON_POSITION, null);

		return newColorList;
	}

	private void initPropertyPopup() {
		if (propertyPopup == null) {
			propertyPopup = new GPopupPanel(false, appW.getAppletFrame(), appW);
			propertyPopup.setStyleName("quickStyleBarPopup");
		}
	}

	private void showPropertyPopup() {
		appW.closePopups();
		propertyPopup.show();
		propertyPopup.setPopupPosition((int) (getAbsoluteLeft() - appW.getAbsLeft()),
				(int) (getAbsoluteTop() + getOffsetHeight() - appW.getAbsTop())
						+ 2 * POPUP_MENU_DISTANCE);
		appW.registerPopup(propertyPopup);
	}
}
