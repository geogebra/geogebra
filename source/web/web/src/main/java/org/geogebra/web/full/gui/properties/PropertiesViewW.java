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

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.exam.ExamListener;
import org.geogebra.common.exam.ExamState;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.properties.ObjectSettingsSession;
import org.geogebra.common.properties.PropertyView.TabbedPageSelector;
import org.geogebra.common.properties.PropertyViewFactory;
import org.geogebra.common.properties.factory.PropertiesArray;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.states.State;
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
import org.gwtproject.dom.client.BrowserEvents;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.Widget;
import org.jspecify.annotations.Nullable;

/**
 * @author gabor
 *
 * PropertiesView for Web
 *
 */
public final class PropertiesViewW extends PropertiesView
		implements ExamListener, RequiresResize, SetLabels {
	private static final int DEFAULT_SETTINGS_WIDTH = 400;
	private final FlowPanel wrappedPanel;
	private final @Nullable ComponentSideSheet sideSheet;

	private OptionType optionType;
	private boolean floatingAttached = false;

	private @Nullable ComponentTab settingsTab;
	private @Nullable ObjectSettingsSession objectSettingsSession;
	private State.@Nullable Subscription treeSubscription;

	private final PropertiesPanelAdapter panelAdapter;

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
		panelAdapter = new PropertiesPanelAdapter(app.getLocalization(), app);
		wrappedPanel.addAttachHandler(event -> {
			if (!event.isAttached()) {
				disposeProperties();
				setAttached(false);
				setFloatingAttached(false);
			}
		});
		addEscapeHandler();
		app.setPropertiesView(this);
		wrappedPanel.addStyleName("PropertiesViewW");
		app.setDefaultCursor();
		if (app instanceof AppWFull appWFull) {
			appWFull.getExamEventBus().add(this);
		}
		storeLanguagePropertyOnChange();
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
		rebuildContent(getShowableElements());
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
		// XML redefinition clears views before transferring the session to the rebuilt elements.
		if (!kernel.getConstruction().isFileLoading()) {
			disposeProperties();
		}
	}

	private void disposeProperties() {
		if (treeSubscription != null) {
			treeSubscription.cancel();
			treeSubscription = null;
		}
		if (objectSettingsSession != null) {
			objectSettingsSession.dispose();
			objectSettingsSession = null;
		}
		panelAdapter.reset(false);
		settingsTab = null;
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
		if (isShowing()) {
			doUpdateSelection();
		}
	}

	private boolean isReloading() {
		return kernel.getConstruction().isFileLoading()
				|| kernel.getConstruction().getUndoManager().isReloading();
	}

	private void doUpdateSelection() {
		if (isReloading() || kernel.getConstruction().isRemovingGeoToReplaceIt()) {
			return;
		}
		List<GeoElement> showableElements = getShowableElements();
		if (!showableElements.isEmpty() && optionType != OptionType.OBJECTS) {
			setOptionPanel(OptionType.OBJECTS);
		} else if (showableElements.isEmpty() && optionType == OptionType.OBJECTS) {
			if (app.getActiveEuclidianView().isEuclidianView3D()) {
				setOptionPanel(OptionType.EUCLIDIAN3D);
			} else if (app.getActiveEuclidianView().isDefault2D()) {
				setOptionPanel(
						app.getActiveEuclidianView().getEuclidianViewNo() == 1
								? OptionType.EUCLIDIAN
								: OptionType.EUCLIDIAN2);
			} else {
				setOptionPanel(OptionType.EUCLIDIAN_FOR_PLANE);
			}
		}
		rebuildContent(showableElements);
	}

	private boolean isShowing() {
		return (sideSheet != null && sideSheet.isAttached()) || isAttached();
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
		if (type != OptionType.OBJECTS && objectSettingsSession != null) {
			disposeProperties();
		}
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
	public void mouseReleasedForPropertiesView(boolean creatorMode) {
		// Selection updates already choose the appropriate settings panel.
	}

	@Override
	public void updateSelection(ArrayList<GeoElement> geos) {
		updateSelection();
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
		updateSelection();
	}

	@Override
	public void detachView() {
		kernel.detach(this);
		disposeProperties();
		app.getKernel().getAnimationManager().startAnimation();
		setAttached(false);
	}

	@Override
	public void updatePropertiesView() {
		rebuildContent(getShowableElements());
	}

	/**
	 *
	 * @return GWT panel of this view
	 */
	public Widget getWrappedPanel() {
		return wrappedPanel;
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
		rebuildContent(getShowableElements());
	}

	@Override
	public void updateStyleBar() {
		rebuildContent(getShowableElements());
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
			// make sure content is up-to-date before animation starts
			doUpdateSelection();
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
	 * Close without animation.
	 */
	public void closeImmediately() {
		if (sideSheet == null) {
			return;
		}
		sideSheet.removeFromParent();
		app.getGuiManager().setShowView(false, App.VIEW_PROPERTIES);
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
		// not needed
	}

	private void rebuildContent(List<GeoElement> selection) {
		if ((!isShowing() && !isFloatingAttached()) || isReloading()) {
			return;
		}
		List<GeoElement> showableGeos = optionType == OptionType.OBJECTS ? selection : List.of();
		boolean isScientific = app.getConfig().getVersion() == GeoGebraConstants.Version.SCIENTIFIC;
		boolean showObjectProperties = !showableGeos.isEmpty() && !isScientific;
		if (!showObjectProperties) {
			if (objectSettingsSession != null) {
				disposeProperties();
			}
			showContent(null);
		} else if (objectSettingsSession != null
				&& objectSettingsSession.getElements().equals(showableGeos)) {
			objectSettingsSession.rebuild();
		} else {
			TabbedPageSelector previousTree =
					objectSettingsSession != null ? objectSettingsSession.getTree().get() : null;
			disposeProperties();
			objectSettingsSession = new ObjectSettingsSession(app, showableGeos);
			if (previousTree != null) {
				PropertyViewFactory.restorePresentation(
						previousTree, objectSettingsSession.getTree().get());
			}
			treeSubscription = objectSettingsSession.getTree().subscribe(this::showContent);
			showContent(objectSettingsSession.getTree().get());
		}
	}

	private void showContent(@Nullable TabbedPageSelector tree) {
		if ((!isShowing() && !isFloatingAttached()) || kernel.getConstruction().isFileLoading()) {
			return;
		}
		String titleKey = "Settings";
		if (objectSettingsSession != null) {
			List<GeoElement> elements = objectSettingsSession.getElements();
			titleKey = elements.size() == 1
					? PropertyViewFactory.getObjectSettingsTitleKey(elements.get(0))
					: "Selection";
		}
		boolean keepViewState = settingsTab != null;
		int scrollTop =
				keepViewState ? settingsTab.getSelectedTabPanel().getElement().getScrollTop() : 0;
		int focusedWidget = keepViewState ? panelAdapter.getFocusedWidgetIndex() : -1;
		rebuildTabs(tree, keepViewState);
		if (sideSheet == null) {
			wrappedPanel.clear();
			FlowPanel fixedPanel = new FlowPanel();
			fixedPanel.addStyleName("sideSheet");
			SheetTitlePanel titlePanel = new SheetTitlePanel((AppWFull) app, titleKey, this::close, null);
			fixedPanel.add(titlePanel);
			FlowPanel contentPanel = new FlowPanel();
			contentPanel.addStyleName("contentPanel");
			fixedPanel.add(contentPanel);
			contentPanel.add(settingsTab);
			wrappedPanel.add(fixedPanel);
		} else {
			sideSheet.update(new SideSheetData(titleKey));
			sideSheet.addToContent(settingsTab);
		}
		if (keepViewState) {
			settingsTab.getSelectedTabPanel().getElement().setScrollTop(scrollTop);
			panelAdapter.focusWidget(focusedWidget);
		}
	}

	private void addEscapeHandler() {
		Dom.addEventListener(wrappedPanel.getElement(), BrowserEvents.KEYDOWN, event -> {
			if (DOM.isEscapeKey(event)) {
				close();
			}
		});
	}

	private void rebuildTabs(@Nullable TabbedPageSelector tree, boolean keepViewState) {
		panelAdapter.reset(keepViewState);
		ArrayList<TabData> tabs = new ArrayList<>();
		if (tree != null) {
			for (int index = 0; index < tree.getTabTitles().size(); index++) {
				FlowPanel propertiesPanel = new FlowPanel();
				tree.getPageContents(index)
						.forEach(view -> propertiesPanel.add(panelAdapter.getWidget(view)));
				panelAdapter.addAccessibility(AccessibilityGroup.SETTINGS_ITEM);
				tabs.add(new TabData(tree.getTabTitles().get(index), propertiesPanel));
			}
		} else {
			for (PropertiesArray props : app.getConfig()
					.createPropertiesFactory()
					.createProperties(app, app.getLocalization(), app.appScope.propertiesRegistry)) {
				FlowPanel propertiesPanel = panelAdapter.buildPanel(props);
				tabs.add(new TabData(props.getRawName(), propertiesPanel));
			}
		}
		int initialTab = tree != null
				? tree.getSelectedTabIndex()
				: Math.max(0, ComponentTab.indexOf(tabs, optionType.getName()));
		settingsTab =
				new ComponentTab((AppW) app, "Settings", initialTab, false, tabs.toArray(new TabData[0]));
		settingsTab.addTabChangedListener(idx -> {
			if (tree != null) {
				tree.setSelectedTabIndex(idx);
			} else if (optionType != OptionType.OBJECTS) {
				optionType = OptionType.getByName(tabs.get(idx).getTabTitle());
			}
		});
	}

	private List<GeoElement> getShowableElements() {
		return app.getSelectionManager().getSelectedGeos().stream()
				.filter(this::geoHasPropertiesView)
				.collect(Collectors.toList());
	}

	private void storeLanguagePropertyOnChange() {
		LanguageProperty languageProperty = app.appScope.getLanguageProperty();
		languageProperty.addValueObserver(prop -> {
			LogInOperation loginOperation = app.getLoginOperation();
			if (loginOperation != null) {
				loginOperation.setUserLanguage(languageProperty.getValue());
			}
			((AppW) app).getLAF().storeLanguage(languageProperty.getValue());
		});
	}
}
