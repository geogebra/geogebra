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

package org.geogebra.web.full.gui.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;

import org.geogebra.common.exam.ExamListener;
import org.geogebra.common.exam.ExamState;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.web.full.gui.components.sideSheet.ComponentSideSheet;
import org.geogebra.web.full.gui.components.sideSheet.SheetTitlePanel;
import org.geogebra.web.full.gui.components.sideSheet.SideSheetData;
import org.geogebra.web.full.gui.properties.ui.PropertiesPanelAdapter;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.euclidian.FontLoader;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.tab.ComponentTab;
import org.geogebra.web.shared.components.tab.TabData;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.KeyboardEvent;
import jsinterop.base.Js;

/**
 * @author gabor
 * 
 * PropertiesView for Web
 *
 */
public class PropertiesViewW extends PropertiesView
		implements ExamListener, RequiresResize, SetLabels {
	private static final int DEFAULT_SETTINGS_WIDTH = 400;
	private final FlowPanel wrappedPanel;
	private final @CheckForNull ComponentSideSheet sideSheet;

	private OptionType optionType;
	private boolean floatingAttached = false;

	private ComponentTab settingsTab;
	private PropertiesPanelAdapter adapter;
	private boolean objectPropertiesVisible;

	/**
	 * 
	 * @param app
	 *            app
	 * @param optionType
	 *            initial options type
	 */
	public PropertiesViewW(AppW app, OptionType optionType) {
		super(app);
		app.setWaitCursor();
		this.optionType = optionType;
		if (app.isUnbundledOrWhiteboard()) {
			this.sideSheet = new ComponentSideSheet(app, new SideSheetData("Settings"));
			this.wrappedPanel = sideSheet;
		} else {
			sideSheet = null;
			wrappedPanel = new FlowPanel();
		}
		rebuildContent();
		addEscapeHandler();
		app.setPropertiesView(this);
		wrappedPanel.addStyleName("PropertiesViewW");
		app.setDefaultCursor();
		if (app instanceof AppWFull appWFull) {
			appWFull.getExamEventBus().add(this);
		}
		// does not do anything if webfont path is empty
		FontLoader.loadAllBundled(app.getAppletParameters().getParamWebfontsUrl());
	}

	@Override
	public void add(GeoElement geo) {
		// ignore, handled by selection update
	}

	@Override
	public void remove(GeoElement geo) {
		// ignore, handled by selection update
	}

	@Override
	public void rename(GeoElement geo) {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(GeoElement geo) {
		// do nothing
	}

	@Override
    public void updateVisualStyle(GeoElement geo, GProperty prop) {
        // do nothing
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		rebuildContent();
	}

	@Override
	public void repaintView() {
		// nothing on repaint
	}

	@Override
	public void reset() {
		// do nothing
	}

	@Override
	public void clearView() {
		// do nothing
	}

	@Override
	public void setMode(int mode, ModeSetter m) {
		// do nothing
	}

	@Override
	public int getViewID() {
		return 0;
	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	@Override
	public void updateSelection() {
		List<GeoElement> showableElements = getShowableElements();
		if (!showableElements.isEmpty() && optionType != OptionType.OBJECTS) {
			setOptionPanel(OptionType.OBJECTS);
		} else if (showableElements.isEmpty()) {
			if (app.getActiveEuclidianView().isEuclidianView3D()) {
				setOptionPanel(OptionType.EUCLIDIAN3D);
			} else if (app.getActiveEuclidianView().isDefault2D()) {
				setOptionPanel(app.getActiveEuclidianView().getEuclidianViewNo() == 1
					? OptionType.EUCLIDIAN : OptionType.EUCLIDIAN2);
			} else {
				setOptionPanel(OptionType.EUCLIDIAN_FOR_PLANE);
			}
		}
		rebuildContent();
	}

	@Override
	protected void setOptionPanelWithoutCheck(OptionType type) {
		int sType = 0;
		setOptionPanel(type, sType);
	}

	@Override
	protected void setObjectsToolTip() {
		// styleBar.setObjectsToolTip();
	}

	@Override
	protected void setSelectedTab(OptionType type) {
		// do nothing ?
	}

	@Override
	protected void updateObjectPanelSelection(ArrayList<GeoElement> geos) {
		// do nothing
	}

	@Override
	public void setOptionPanel(OptionType type, int subType) {
		optionType = type;
		onResize();
		if (settingsTab != null) {
			settingsTab.switchToTab(type.getName());
		}
	}

	/**
	 * @return selected option type
	 */
	public OptionType getOptionType() {
		return optionType;
	}

	@Override
	public void mousePressedForPropertiesView() {
		// do nothing
	}

	@Override
	public void updateSelection(ArrayList<GeoElement> geos) {
		if (!geos.isEmpty() && optionType != OptionType.OBJECTS) {
			setOptionPanel(OptionType.OBJECTS);
		}
		rebuildContent();
	}

	@Override
	protected void updateTitleBar() {
		rebuildContent();
	}

	@Override
	public void attachView() {
		if (isAttached()) {
			return;
		}

		clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);
		app.getKernel().getAnimationManager().stopAnimation();
		setAttached(true);
	}

	@Override
	public void detachView() {
		kernel.detach(this);
		clearView();
		app.getKernel().getAnimationManager().startAnimation();
		setAttached(false);
	}

	@Override
	public void updatePropertiesView() {
		rebuildContent();
	}

	/**
	 * 
	 * @return GWT panel of this view
	 */
	public Widget getWrappedPanel() {
		return wrappedPanel;
	}

	/**
	 * Rebuild GUI for the new font size
	 */
	public void updateFonts() {
		rebuildContent();
	}

	@Override
	public void onResize() {
		if (settingsTab != null) {
			settingsTab.updateScrollIndicators();
		}
	}

	@Override
	public boolean suggestRepaint() {
		return false;
	}

	@Override
    public void setLabels() {
		if (settingsTab != null) {
			settingsTab.setLabels();
		}
		rebuildContent();
    }

	@Override
	public void updateStyleBar() {
		rebuildContent();
	}

	/**
	 * Opens floating settings, assumes it's available for current app.
	 */
	public void open() {
		if (!isFloatingAttached()) {
			setFloatingAttached(true);
		}
		((AppWFull) app).centerAndResizeViews();
		if (sideSheet != null) {
			sideSheet.show();
			sideSheet.focus();
		}
	}

	/**
	 * Closes floating settings.
	 */
	public void close() {
		if (sideSheet == null) {
			app.getGuiManager().setShowView(false, App.VIEW_PROPERTIES);
			return;
		}
		sideSheet.close(() -> {
			app.getGuiManager().setShowView(false, App.VIEW_PROPERTIES);
			setFloatingAttached(false);
		});
	}

	/**
	 * @return true is settings panel is floating
	 */
	public boolean isFloatingAttached() {
		return floatingAttached;
	}

	/**
	 * @param floatingAttached
	 *            true if settings panel is floating
	 */
	public void setFloatingAttached(boolean floatingAttached) {
		this.floatingAttached = floatingAttached;
		if (floatingAttached) {
			kernel.attach(this);
		} else {
			kernel.detach(this);
		}
	}

	/**
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public void resize(double width, double height) {
		wrappedPanel.setPixelSize((int) Math.min(width, DEFAULT_SETTINGS_WIDTH), (int) height);
		onResize();
	}

	@Override
	public void examStateChanged(ExamState newState) {
		// not needed
	}

	private void rebuildContent() {
		List<GeoElement> showableGeos = optionType == OptionType.OBJECTS
				? getShowableElements() : List.of();
		List<PropertiesArray> propLists;
		String titleKey;
		boolean showObjectProperties = !showableGeos.isEmpty();
		if (showObjectProperties) {
			GeoElementPropertiesFactory propertiesFactory =
					((AppWFull) app).getGeoElementPropertiesFactory();
			propLists = propertiesFactory.createProperties(
					app.getKernel().getAlgebraProcessor(),
					app.getLocalization(),
					app.getImageManager(),
					app.getEventDispatcher().availableTypes().contains(ScriptType.JAVASCRIPT),
					showableGeos);
			titleKey = showableGeos.size() == 1
					? showableGeos.get(0).getTypeString() : "Selection";
		} else {
			titleKey = "Settings";
			propLists = app.getConfig().createPropertiesFactory().createProperties(
					app, app.getLocalization(), app.appScope.propertiesRegistry);
		}
		rebuildTabs(propLists, showObjectProperties);
		if (sideSheet == null) {
			wrappedPanel.clear();
			FlowPanel fixedPanel = new FlowPanel();
			fixedPanel.addStyleName("sideSheet");
			SheetTitlePanel titlePanel = new SheetTitlePanel((AppWFull) app, titleKey,
					this::close, null);
			fixedPanel.add(titlePanel);
			FlowPanel contentPanel = new FlowPanel();
			contentPanel.addStyleName("contentPanel");
			fixedPanel.add(contentPanel);
			contentPanel.add(settingsTab);
			wrappedPanel.add(fixedPanel);
			return;
		}
		sideSheet.update(new SideSheetData(titleKey));
		sideSheet.addToContent(settingsTab);
		this.objectPropertiesVisible = showObjectProperties;
	}

	private void addEscapeHandler() {
		Dom.addEventListener(wrappedPanel.getElement(), "keydown", event -> {
			KeyboardEvent kbd = Js.uncheckedCast(event);
			if ("Escape".equals(kbd.code)) {
				close();
			}
		});
	}

	private void rebuildTabs(List<PropertiesArray> propLists, boolean showObjectProperties) {
		adapter = new PropertiesPanelAdapter(app.getLocalization(),
				(AppW) app);
		ArrayList<TabData> tabs = new ArrayList<>();
		for (PropertiesArray props : propLists) {
			FlowPanel propertiesPanel = adapter.buildPanel(props);
			tabs.add(new TabData(props.getRawName(), propertiesPanel));
		}
		int oldTab = -1;
		if (settingsTab != null && objectPropertiesVisible == showObjectProperties) {
			oldTab = settingsTab.getSelectedTabIdx();
		}
		settingsTab = new ComponentTab((AppW) app, "Settings",
				oldTab != -1 && oldTab < tabs.size() ? oldTab : 0,
				optionType.getName(), tabs.toArray(new TabData[0]));
	}

	private List<GeoElement> getShowableElements() {
		return app.getSelectionManager().getSelectedGeos().stream()
				.filter(geo -> !geo.isMeasurementTool() && !geo.isSpotlight())
				.collect(Collectors.toList());
	}
}
