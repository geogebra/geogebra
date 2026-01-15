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

import static org.geogebra.common.GeoGebraConstants.SCIENTIFIC_APPCODE;

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
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.web.full.gui.components.sideSheet.ComponentSideSheet;
import org.geogebra.web.full.gui.components.sideSheet.SideSheetData;
import org.geogebra.web.full.gui.dialog.options.OptionPanelW;
import org.geogebra.web.full.gui.dialog.options.OptionsAlgebraW;
import org.geogebra.web.full.gui.dialog.options.OptionsCASW;
import org.geogebra.web.full.gui.dialog.options.OptionsDefaultsW;
import org.geogebra.web.full.gui.dialog.options.OptionsEuclidianW;
import org.geogebra.web.full.gui.dialog.options.OptionsGlobalW;
import org.geogebra.web.full.gui.dialog.options.OptionsLayoutW;
import org.geogebra.web.full.gui.dialog.options.OptionsObjectW;
import org.geogebra.web.full.gui.dialog.options.OptionsSpreadsheetW;
import org.geogebra.web.full.gui.properties.ui.PropertiesPanelAdapter;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CSSEvents;
import org.geogebra.web.html5.util.PersistablePanel;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;
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
	// option panels
	private OptionsDefaultsW defaultsPanel;
	private OptionsEuclidianW euclidianPanel;
	private OptionsEuclidianW euclidianPanel2;
	private OptionsEuclidianW euclidianPanel3D;
	private OptionsSpreadsheetW spreadsheetPanel;
	private OptionsCASW casPanel;
	private OptionsLayoutW layoutPanel;
	private OptionsAlgebraW algebraPanel;
	private OptionsGlobalW globalPanel;
	
	private PropertiesStyleBarW styleBar;

	private FlowPanel contentsPanel;
	private OptionType optionType;
	private boolean floatingAttached = false;

	private @CheckForNull ComponentSideSheet sideSheet;
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
		this.wrappedPanel = app.isUnbundledOrWhiteboard()
				? new PersistablePanel() : new FlowPanel();
		app.setPropertiesView(this);
		app.setWaitCursor();

		this.optionType = optionType;
		initGUI();
		app.setDefaultCursor();
		if (app instanceof AppWFull) {
			((AppWFull) app).getExamEventBus().add(this);
		}
	}

	private void initGUI() {
		wrappedPanel.addStyleName("PropertiesViewW");
		wrappedPanel.clear();
		contentsPanel = new FlowPanel();
		contentsPanel.addStyleName("contentsPanel");
		if (needsSideSheet()) {
			SideSheetData data = new SideSheetData("Settings");
			sideSheet = new ComponentSideSheet((AppW) app, data, this::close);
			rebuildSettingsSideSheet();
		} else {
			sideSheet = null;
			wrappedPanel.add(contentsPanel);
			wrappedPanel.add(getStyleBar().getWrappedPanel());

			setOptionPanel(optionType, 0);
		}
	}

	/**
	 * @return the style bar for this view.
	 */
	public PropertiesStyleBarW getStyleBar() {
		if (styleBar == null) {
			styleBar = newPropertiesStyleBar();
		}
		return styleBar;
	}

	/**
	 * @return properties stylebar
	 */
	protected PropertiesStyleBarW newPropertiesStyleBar() {
		return new PropertiesStyleBarW(this, app);
	}

	/**
	 * Returns the option panel for the given type. If the panel does not exist,
	 * a new one is constructed
	 * 
	 * @param type
	 *            panel type
	 * @param subType
	 *            tab number for given panel
	 * @return options panel
	 */
	public OptionPanelW getOptionPanel(OptionType type, int subType) {
		if (styleBar != null) {
			styleBar.updateGUI();
		}
		switch (type) {
		case GLOBAL:
			if (globalPanel == null) {
				globalPanel = new OptionsGlobalW((AppW) app);
			}
			return globalPanel;

		case DEFAULTS:
			if (defaultsPanel == null) {
				defaultsPanel = new OptionsDefaultsW();
			}
			return defaultsPanel;

		case CAS:
			if (casPanel == null) {
				casPanel = new OptionsCASW((AppW) app);
			}
			return casPanel;

		case EUCLIDIAN:
			if (euclidianPanel == null) {
				euclidianPanel = new OptionsEuclidianW((AppW) app, app.getActiveEuclidianView());
				euclidianPanel.setLabels();
				euclidianPanel.setView(((AppW) app).getEuclidianView1());
			}
			return euclidianPanel;

		case EUCLIDIAN2:
			if (euclidianPanel2 == null) {
				euclidianPanel2 = new OptionsEuclidianW((AppW) app,
						((AppW) app).getEuclidianView2(1));
				euclidianPanel2.setLabels();
				euclidianPanel2.setView(((AppW) app).getEuclidianView2(1));
			}
			return euclidianPanel2;

		case EUCLIDIAN3D:
			if (euclidianPanel3D == null) {
				euclidianPanel3D = new OptionsEuclidianW((AppW) app, app.getEuclidianView3D());
				euclidianPanel3D.setLabels();
			}
			return euclidianPanel2;

		case SPREADSHEET:
			if (spreadsheetPanel == null) {
				spreadsheetPanel = new OptionsSpreadsheetW((AppW) app);
			}
			return spreadsheetPanel;

		case ALGEBRA:
			if (algebraPanel == null) {
				algebraPanel = new OptionsAlgebraW((AppW) app);
			}
			return algebraPanel;

		case LAYOUT:
			if (layoutPanel == null) {
				layoutPanel = new OptionsLayoutW();
			}
			layoutPanel.getWrappedPanel().setStyleName("layoutPanel");
			return layoutPanel;

		case OBJECTS:
			if (getObjectPanel() == null) {
				setObjectPanel(new OptionsObjectW((AppW) app, false, this::updatePropertiesView));
			}
			getObjectPanel().selectTab(subType);
			return getObjectPanel();
		}
		return null;
	}

	@Override
	protected OptionsObjectW getObjectPanel() {
		return super.getObjectPanel() != null
				? (OptionsObjectW) super.getObjectPanel() : null;
	}

	private OptionsEuclidianW getEuclidianPanel() {
		return euclidianPanel;
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
		if (geo.isLabelSet()) {
			OptionsObjectW panel = getObjectPanel();
			if (panel != null) {
				panel.updateIfInSelection(geo);
			}
		}
	}

	@Override
	public void updateVisualStyle(GeoElement geo, GProperty prop) {
		if (geo.isLabelSet()) {
			updatePropertiesGUI();
		}
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		updatePropertiesGUI();
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
		updatePropertiesGUI();
	}

	@Override
	protected void setOptionPanelWithoutCheck(OptionType type) {
		int sType = 0;
		if (type == OptionType.OBJECTS && this.getObjectPanel() != null) {
			MultiRowsTabPanel tabPanel = this.getObjectPanel()
					.getTabPanel();
			sType = tabPanel.getTabBar().getSelectedTab();

		}
		setOptionPanel(type, sType);
	}

	@Override
	protected void setObjectsToolTip() {
		// styleBar.setObjectsToolTip();
	}

	@Override
	protected void setSelectedTab(OptionType type) {
		switch (type) {
		case EUCLIDIAN:
			euclidianPanel.setSelectedTab(getSelectedTab());
			break;
		case EUCLIDIAN2:
			euclidianPanel2.setSelectedTab(getSelectedTab());
			break;
		default:
			// do nothing
			break;
		}
	}

	@Override
	protected void updateObjectPanelSelection(ArrayList<GeoElement> geos) {
		if (getObjectPanel() == null) {
			return;
		}
		getObjectPanel().updateSelection(geos);
		updateTitleBar();
		setObjectsToolTip();
	}

	@Override
	public void setOptionPanel(OptionType type, int subType) {
		optionType = type;
		contentsPanel.clear();
		OptionPanelW optionPanel = getOptionPanel(type, subType);
		Widget wPanel = optionPanel.getWrappedPanel();
		contentsPanel.add(wPanel);
		if (wPanel != null) {
			onResize();
		}
		if (styleBar != null) {
			styleBar.selectButton(type);
		}
		if (sideSheet != null && settingsTab != null) {
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
		if (getObjectPanel() == null) {
			return;
		}
		getObjectPanel().forgetGeoAdded();
	}

	@Override
	public void updateSelection(ArrayList<GeoElement> geos) {
		if (!geos.isEmpty() && optionType != OptionType.OBJECTS) {
			setOptionPanel(OptionType.OBJECTS);
		}
		updatePropertiesGUI();
	}

	private void updatePropertiesGUI() {
		if ((sideSheet != null) ^ needsSideSheet()) {
			initGUI();
		}

		OptionsObjectW panel = getObjectPanel();
		if (panel != null) {
			panel.updateGUI();
			if (optionType == OptionType.OBJECTS) {
				if (!panel.getWrappedPanel().isVisible()) {
					setOptionPanel(OptionType.EUCLIDIAN);
				}
			}
		}
		if (getEuclidianPanel() != null) {
			getEuclidianPanel().updateGUI();
		}

		if (styleBar != null) {
			styleBar.updateGUI();
		}

		if (PreviewFeature.isAvailable(PreviewFeature.SETTINGS_VIEW)) {
			rebuildSettingsSideSheet();
		}
	}

	@Override
	protected void updateTitleBar() {
		updatePropertiesGUI();
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
		updatePropertiesGUI();
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
		updatePropertiesGUI();
	}

	@Override
	public void onResize() {
		int width = getWrappedPanel().getOffsetWidth() - 40;
		int height = getWrappedPanel().getOffsetHeight();
		if (height > 0 && width > 0) {
			contentsPanel.setWidth(width + "px");
		} else if (app.isUnbundledOrWhiteboard() && width == -40
				&& getWrappedPanel() != null) {
			contentsPanel.setWidth("460px");
		}
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
		if (globalPanel != null) {
			globalPanel.setLabels();
		}
		if (euclidianPanel != null) {
			euclidianPanel.setLabels();
		}
		if (euclidianPanel2 != null) {
			euclidianPanel2.setLabels();
		}
		if (euclidianPanel3D != null) {
			euclidianPanel3D.setLabels();
		}
		if (spreadsheetPanel != null) {
			spreadsheetPanel.setLabels();
		}
		if (casPanel != null) {
			casPanel.setLabels();
		}
		if (algebraPanel != null) {
			algebraPanel.setLabels();
		}
		if (sideSheet != null) {
			sideSheet.setLabels();
		}
		if (settingsTab != null) {
			settingsTab.setLabels();
		}
		if (needsSideSheet()) {
			rebuildSettingsSideSheet();
		}
    }

	@Override
	public void updateStyleBar() {
		if (styleBar != null) {
			styleBar.updateGUI();
		}
		if (needsSideSheet()) {
			rebuildSettingsSideSheet();
		}
	}

	private boolean needsSideSheet() {
		return PreviewFeature.isAvailable(PreviewFeature.SETTINGS_VIEW)
				|| SCIENTIFIC_APPCODE.equals(app.getConfig().getSubAppCode())
				|| SCIENTIFIC_APPCODE.equals(app.getConfig().getAppCode());
	}

	/**
	 * Opens floating settings, assumes it's available for current app.
	 */
	public void open() {
		if (!isFloatingAttached()) {
			wrappedPanel.setVisible(true);
			wrappedPanel.addStyleName("floatingSettings");
			((AppWFull) app).getAppletFrame().add(wrappedPanel);
			setFloatingAttached(true);
		}
		((AppWFull) app).centerAndResizeViews();
		wrappedPanel.removeStyleName("animateOut");
		wrappedPanel.addStyleName("animateIn");
		if (sideSheet != null) {
			sideSheet.focus();
		}
	}

	/**
	 * Closes floating settings.
	 */
	public void close() {
		if (!app.isUnbundledOrWhiteboard()) {
			app.getGuiManager().setShowView(false, App.VIEW_PROPERTIES);
			return;
		}
		wrappedPanel.removeStyleName("animateIn");
		wrappedPanel.addStyleName("animateOut");
		CSSEvents.runOnAnimation(this::onFloatingSettingsClose,
				wrappedPanel.getElement(), "animateOut");
	}

	/**
	 * Callback for animation in floating mode
	 */
	protected void onFloatingSettingsClose() {
		app.getGuiManager().setShowView(false, App.VIEW_PROPERTIES);
		((AppWFull) app).getAppletFrame().remove(wrappedPanel);
		setFloatingAttached(false);
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
		if (newState == ExamState.IDLE || newState == ExamState.ACTIVE) {
			setObjectPanel(new OptionsObjectW((AppW) app, false, this::updatePropertiesView));
		}
	}

	private void rebuildSettingsSideSheet() {
		List<GeoElement> showableGeos = optionType == OptionType.OBJECTS
				? getShowableElements() : List.of();
		if (sideSheet == null) {
			return;
		}
		wrappedPanel.clear();
		sideSheet.clearContent();
		List<PropertiesArray> propLists;
		boolean showObjectProperties = !showableGeos.isEmpty();
		if (showObjectProperties) {
			GeoElementPropertiesFactory propertiesFactory =
					((AppWFull) app).getGeoElementPropertiesFactory();
			propLists = propertiesFactory.createStructuredProperties(
					app.getKernel().getAlgebraProcessor(),
					app.getLocalization(),
					showableGeos);
			sideSheet.setTitleTransKey(
					showableGeos.size() == 1 ? showableGeos.get(0).getTypeString() : "Selection");
		} else {
			sideSheet.setTitleTransKey("Settings");
			propLists = app.getConfig().createPropertiesFactory().createProperties(
					app, app.getLocalization(), GlobalScope.propertiesRegistry);
		}
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
				tabs.toArray(new TabData[0]));
		sideSheet.addToContent(settingsTab);
		this.objectPropertiesVisible = showObjectProperties;
		wrappedPanel.add(sideSheet);

		Dom.addEventListener(wrappedPanel.getElement(), "keydown", event -> {
			KeyboardEvent kbd = Js.uncheckedCast(event);
			if ("Escape".equals(kbd.code)) {
				sideSheet.onClose();
			}
		});
	}

	private List<GeoElement> getShowableElements() {
		return app.getSelectionManager().getSelectedGeos().stream()
				.filter(geo -> !geo.isMeasurementTool() && !geo.isSpotlight())
				.collect(Collectors.toList());
	}
}
