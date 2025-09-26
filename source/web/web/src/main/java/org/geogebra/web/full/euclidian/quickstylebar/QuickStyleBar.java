package org.geogebra.web.full.euclidian.quickstylebar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.stylebar.StylebarPositioner;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.main.undo.UndoActionObserver;
import org.geogebra.common.main.undo.UndoActionType;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertySupplier;
import org.geogebra.common.properties.PropertyWrapper;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.euclidian.quickstylebar.components.IconButtonWithProperty;
import org.geogebra.web.full.gui.ContextMenuGeoElementW;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.EventUtil;
import org.geogebra.web.html5.util.TestHarness;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.FlowPanel;

import com.google.gwt.core.client.Scheduler;

import elemental2.dom.Event;

/**
 * Quick style bar containing IconButtons with dynamic position
 */
public class QuickStyleBar extends FlowPanel implements EuclidianStyleBar {
	private final EuclidianView ev;
	private final StylebarPositioner stylebarPositioner;
	private final List<IconButton> quickButtons = new ArrayList<>();
	public final static int POPUP_MENU_DISTANCE = 8;
	public final static int QUICK_STYLE_BAR_HEIGHT = 48;
	private final PropertyWrapper propertyWrapper;
	private @CheckForNull ContextMenuGeoElementW contextMenu;
	GeoElementPropertiesFactory geoElementPropertiesFactory;

	/**
	 * @param ev - parent view
	 */
	public QuickStyleBar(EuclidianView ev, AppWFull app) {
		this.ev = ev;
		this.stylebarPositioner = new StylebarPositioner(ev.getApplication());
		this.propertyWrapper = new PropertyWrapper(ev.getApplication());
		geoElementPropertiesFactory = app.getGeoElementPropertiesFactory();
		addStyleName("quickStylebar");
		addHandlers();
		buildGUI();
		TestHarness.setAttr(this, "dynamicStyleBar");
	}

	private void buildGUI() {
		List<GeoElement> activeGeoList = stylebarPositioner.createActiveGeoList();
		if (activeGeoList.isEmpty()) {
			return;
		}
		Property imageOpacityProperty = geoElementPropertiesFactory.createImageOpacityProperty(
				getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList, null, false, imageOpacityProperty);

		addCropButton();

		PropertiesArray colorWithOpacityProperty = geoElementPropertiesFactory
				.createNotesColorWithOpacityProperties(getApp().getLocalization(), activeGeoList);
		addColorPropertyButton(activeGeoList, UndoActionType.STYLE,
				colorWithOpacityProperty.getProperties());

		PropertySupplier colorProperty = propertyWrapper.withStrokeSplitting(
				(geos) -> geoElementPropertiesFactory.createObjectColorProperty(
				getApp().getLocalization(), geos), activeGeoList);
		addColorPropertyButton(activeGeoList, UndoActionType.STYLE, colorProperty);

		Property textBackgroundColorProperty = geoElementPropertiesFactory
				.createTextBackgroundColorProperty(getApp().getLocalization(), activeGeoList);
		addColorPropertyButton(activeGeoList, UndoActionType.STYLE_OR_TABLE_CONTENT,
				textBackgroundColorProperty);

		PropertiesArray pointStyleProperty = geoElementPropertiesFactory
				.createPointStyleExtendedProperties(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList, "pointStyle", true,
				pointStyleProperty.getProperties());

		if (getApp().isWhiteboardActive()) {
			Property fillingStyleProperty = geoElementPropertiesFactory
					.createFillingStyleProperty(getApp().getLocalization(), activeGeoList);
			addPropertyPopupButton(activeGeoList, null, false, fillingStyleProperty);
		}

		List<PropertySupplier> lineStylePropertyWithSplit = new ArrayList<>();
		lineStylePropertyWithSplit.add(propertyWrapper.withStrokeSplitting(geos ->
				geoElementPropertiesFactory.createLineStyleProperty(
						getApp().getLocalization(), geos), activeGeoList));
		lineStylePropertyWithSplit.add(propertyWrapper.withStrokeSplitting(geos ->
				geoElementPropertiesFactory.createNotesThicknessProperty(
						getApp().getLocalization(), geos), activeGeoList));

		addPropertyPopupButton(activeGeoList, null, false,
				lineStylePropertyWithSplit.stream()
						.filter(Objects::nonNull).toArray(PropertySupplier[]::new));

		Property segmentStartProperty = geoElementPropertiesFactory
				.createSegmentStartProperty(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList, "segmentStyle", true, segmentStartProperty);

		Property segmentEndProperty = geoElementPropertiesFactory
				.createSegmentEndProperty(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList, "segmentStyle", true, segmentEndProperty);

		PropertiesArray cellBorderProperty = geoElementPropertiesFactory
				.createCellBorderStyleProperties(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList, "cellBorderStyle", true,
				UndoActionType.STYLE_OR_TABLE_CONTENT, cellBorderProperty.getProperties());

		PropertiesArray objectBorderProperty = geoElementPropertiesFactory
				.createObjectBorderProperties(getApp().getLocalization(), activeGeoList);
		addColorPropertyButton(activeGeoList, UndoActionType.STYLE,
				objectBorderProperty.getProperties());

		addDivider();

		Property fontColorProperty = geoElementPropertiesFactory.createTextFontColorProperty(
				getApp().getLocalization(), activeGeoList);
		addColorPropertyButton(activeGeoList, UndoActionType.STYLE_OR_CONTENT,
				fontColorProperty);

		Property fontSizeProperty = geoElementPropertiesFactory.createTextFontSizeProperty(
				getApp().getLocalization(), activeGeoList, ev);
		addPropertyPopupButton(activeGeoList, "gwt-PopupPanel contextSubMenu", true,
				UndoActionType.STYLE_OR_CONTENT, fontSizeProperty);

		BooleanProperty boldProperty = geoElementPropertiesFactory
				.createBoldProperty(getApp().getLocalization(), activeGeoList);
		addTextFormatPropertyButton(activeGeoList, boldProperty);

		BooleanProperty italicProperty = geoElementPropertiesFactory
				.createItalicProperty(getApp().getLocalization(), activeGeoList);
		addTextFormatPropertyButton(activeGeoList, italicProperty);

		BooleanProperty underlineProperty = geoElementPropertiesFactory
				.createUnderlineProperty(getApp().getLocalization(), activeGeoList);
		addTextFormatPropertyButton(activeGeoList, underlineProperty);

		Property horizontalAlignmentProperty = geoElementPropertiesFactory
				.createHorizontalAlignmentProperty(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList, null, true,
				UndoActionType.STYLE_OR_CONTENT, horizontalAlignmentProperty);

		Property verticalAlignmentProperty = geoElementPropertiesFactory
				.createVerticalAlignmentProperty(getApp().getLocalization(), activeGeoList);
		addPropertyPopupButton(activeGeoList, null, true,
				UndoActionType.STYLE_OR_TABLE_CONTENT, verticalAlignmentProperty);

		if (!getApp().isWhiteboardActive()) {
			PropertiesArray labelProperties = geoElementPropertiesFactory
					.createLabelProperties(getApp().getLocalization(), activeGeoList);
			addPropertyPopupButton(activeGeoList, "labelStyle", true,
					labelProperties.getProperties());
		}

		addDivider();

		addDeleteButton();
		addContextMenuButton();
	}

	private void addColorPropertyButton(List<GeoElement> geos, UndoActionType undoFiler,
			PropertySupplier... properties) {
		if (properties.length == 0 || properties[0] == null || properties[0].get() == null) {
			return;
		}
		Property firstProperty = properties[0].get();
		propertyWrapper.addUndoActionObserver(properties, geos, undoFiler);
		IconButtonWithProperty colorButton = new IconButtonWithProperty(getApp(), "colorStyle",
				PropertiesIconAdapter.getIcon(firstProperty), firstProperty.getName(),
				geos, true, properties);

		setPopupHandlerWithUndoAction(colorButton);
		styleAndRegisterButton(colorButton);
	}

	private void addTextFormatPropertyButton(List<GeoElement> geos,
			BooleanProperty property) {
		if (property == null || !(geos.get(0) instanceof HasTextFormatter
				|| geos.get(0) instanceof TextProperties)) {
			return;
		}
		property.addValueObserver(new UndoActionObserver(geos, UndoActionType.STYLE_OR_CONTENT));
		IconButton toggleButton = new IconButton(getApp(), null,
				new ImageIconSpec(PropertiesIconAdapter.getIcon(property)), property.getName());
		toggleButton.setActive(property.getValue());
		addFastClickHandlerWithUndoContentAction(toggleButton, property);
		styleAndRegisterButton(toggleButton);
	}

	protected void addFastClickHandlerWithUndoContentAction(IconButton btn,
			BooleanProperty property) {
		btn.addFastClickHandler(ignore -> {
			getApp().closePopups();
			property.setValue(!btn.isActive());
			btn.setActive(!btn.isActive());
		});
	}

	protected void setPopupHandlerWithUndoAction(IconButtonWithProperty iconButton) {
		iconButton.addPopupHandler((property, value) -> {
			getApp().closePopups();
			property.setValue(value);
		});
	}

	private void addPropertyPopupButton(List<GeoElement> geos, String className,
			boolean closePopupOnAction, PropertySupplier... properties) {
		addPropertyPopupButton(geos, className, closePopupOnAction,
				UndoActionType.STYLE, properties);
	}

	private void addPropertyPopupButton(List<GeoElement> geos, String className,
			boolean closePopupOnAction, UndoActionType undoType, PropertySupplier... properties) {
		if (properties.length == 0 || properties[0] == null || properties[0].get() == null) {
			return;
		}
		Property firstProperty = properties[0].get();
		propertyWrapper.addUndoActionObserver(properties, geos, undoType);
		IconButton button = new IconButtonWithProperty(getApp(), className,
				PropertiesIconAdapter.getIcon(firstProperty), firstProperty.getName(), geos,
				closePopupOnAction, properties);
		styleAndRegisterButton(button);
	}

	private void addDivider() {
		if (getElement().hasChildNodes() && !isLastElemDivider()) {
			add(BaseWidgetFactory.INSTANCE.newDivider(true));
		}
	}

	private boolean isLastElemDivider() {
		String lastElemClassName = getChildren() != null
				? getChildren().get(getChildren().size() - 1).getStyleName() : "";
		return lastElemClassName.contains("divider");
	}

	private void addDeleteButton() {
		IconButton deleteButton = new IconButton(getApp(),
				() -> {
					getApp().closePopups();
					getApp().splitAndDeleteSelectedObjects();
				},
				new ImageIconSpec(MaterialDesignResources.INSTANCE.delete_black()), "Delete");
		styleAndRegisterButton(deleteButton);
	}

	private void addCropButton() {
		if (!(isImageGeoSelected() && getApp().isWhiteboardActive()
				&& ev.getMode() != EuclidianConstants.MODE_SELECT)) {
			return;
		}

		IconButton cropButton = new IconButton(getApp(), null,
				new ImageIconSpec(MaterialDesignResources.INSTANCE.crop_black()), "stylebar.Crop");
		cropButton.setActive(ev.getBoundingBox() != null && ev.getBoundingBox().isCropBox());
		cropButton.addFastClickHandler((source) -> {
			getApp().closePopups();
			cropButton.setActive(!cropButton.isActive());
			ev.getEuclidianController().updateBoundingBoxFromSelection(cropButton.isActive());
			ev.repaintView();
		});
		styleAndRegisterButton(cropButton);
	}

	private void addContextMenuButton() {
		IconButton contextMenuBtn = new IconButton(getApp(), null,
				new ImageIconSpec(MaterialDesignResources.INSTANCE.more_vert_black()), "More");

		contextMenuBtn.addFastClickHandler((event) -> {
			contextMenu = createContextMenu(contextMenuBtn);
			getApp().closePopups();
			GPopupMenuW popupMenu = contextMenu.getWrappedPopup();
			if (popupMenu.isMenuShown()) {
				popupMenu.hideMenu();
			} else {
				popupMenu.show(contextMenuBtn, 0, getOffsetHeight() + POPUP_MENU_DISTANCE);
				getApp().registerPopup(popupMenu.getPopupPanel());
			}

			contextMenuBtn.setActive(popupMenu.isMenuShown());
			getApp().hideKeyboard();
		});

		styleAndRegisterButton(contextMenuBtn);
	}

	private ContextMenuGeoElementW createContextMenu(IconButton contextMenuBtn) {
		ContextMenuGeoElementW contextMenu = ((GuiManagerW) getApp().getGuiManager())
				.getPopupMenu(ev.getEuclidianController().getAppSelectedGeos());
		GPopupPanel popupPanel = contextMenu.getWrappedPopup().getPopupPanel();
		popupPanel.addAutoHidePartner(getElement());
		popupPanel.addCloseHandler(closeEvent -> {
			contextMenuBtn.deactivate();
			contextMenu.getWrappedPopup().hideMenu();

		});

		return contextMenu;
	}

	private void styleAndRegisterButton(IconButton button) {
		button.addStyleName("small");
		quickButtons.add(button);
		add(button);
	}

	private AppW getApp() {
		return (AppW) ev.getApplication();
	}

	private void addHandlers() {
		ev.getApplication().getSelectionManager()
				.addSelectionListener((geo, addToSelection) -> {
					if (addToSelection) {
						return;
					}
					updateStyleBar();
				});
		// stop propagation of start/end events for pointer types so that they're not killed in EV
		EventUtil.stopPointer(getElement());
		ClickStartHandler.initDefaults(asWidget(), false, true);
		// with Apple Pen specifically, this needs to be done for touchmove as well
		getApp().getGlobalHandlers().addEventListener(getElement(),
				"touchmove", Event::stopPropagation);
	}

	@Override
	public void setMode(int mode) {
		// nothing for now
	}

	@Override
	public void setLabels() {
		quickButtons.forEach(SetLabels::setLabels);
		if (contextMenu != null) {
			contextMenu.update();
		}
	}

	@Override
	public void restoreDefaultGeo() {
		// nothing for now
	}

	@Override
	public void updateStyleBar() {
		if (!isVisible()) {
			return;
		}

		clear();
		buildGUI();
		// update from slider may trigger temporarily removing geos; use deferred here to
		// avoid closing of the StyleBar
		Scheduler.get().scheduleDeferred(() -> {
			GPoint position = stylebarPositioner.getPositionForStyleBar(getOffsetWidth(),
					getOffsetHeight());
			if (position != null) {
				getElement().getStyle().setLeft(position.x, Unit.PX);
				getElement().getStyle().setTop(position.y, Unit.PX);
			} else {
				setVisible(false);
				closeQuickStyleBarPopups();
			}
		});
	}

	@Override
	public void updateButtonPointCapture(int mode) {
		// nothing for now
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		if (!isVisible()) {
			return;
		}

		closeQuickStyleBarPopups();
		updateStyleBar();
	}

	@Override
	public int getPointCaptureSelectedIndex() {
		return 0;
	}

	@Override
	public void updateGUI() {
		// nothing for now
	}

	@Override
	public void hidePopups() {
		// nothing for now
	}

	@Override
	public void resetFirstPaint() {
		// nothing for now
	}

	@Override
	public void reinit() {
		// nothing for now
	}

	private boolean isImageGeoSelected() {
		return ev.getEuclidianController().getAppSelectedGeos().size() == 1
				&& ev.getEuclidianController().getAppSelectedGeos().get(0).isGeoImage();
	}

	private void closeQuickStyleBarPopups() {
		for (IconButton button : quickButtons) {
			if (button instanceof IconButtonWithProperty) {
				((IconButtonWithProperty) button).closePopup();
			}
		}
	}
}
