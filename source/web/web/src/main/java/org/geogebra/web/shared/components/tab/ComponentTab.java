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

package org.geogebra.web.shared.components.tab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.MulticastEvent;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.ScrollPanel;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.KeyboardEvent;

public class ComponentTab extends FlowPanel implements RequiresResize, SetLabels {
	private final AppW appW;
	private final Localization loc;
	private ScrollPanel scrollPanel;
	private FlowPanel panelContainer;
	private FlowPanel tabList;
	private final String ariaLabel;
	private StandardButton selectedBtn;
	private StandardButton left;
	private StandardButton right;
	private final List<TabData> tabData;
	private final ArrayList<StandardButton> tabButton = new ArrayList<>();
	private int selectedTabIdx = 0;
	private final MulticastEvent<Integer> tabChanged = new MulticastEvent<>();

	/**
	 * Creates a tab component with optional scroll indicator buttons.
	 * @param appW {@link org.geogebra.web.html5.main.AppW}
	 * @param ariaLabel aria-label trans key (title of parent element)
	 * @param initialTab index of initial tab
	 * @param optionTypeName The name (trans key) of the
	 * {@link org.geogebra.common.main.OptionType} that should be selected
	 * @param tabData {@link TabData} including title and panel widget
	 */
	public ComponentTab(AppW appW, String ariaLabel, int initialTab,
			@CheckForNull String optionTypeName, TabData... tabData) {
		this.appW = appW;
		this.loc = appW.getLocalization();
		this.ariaLabel = ariaLabel;
		this.tabData = Arrays.asList(tabData);
		addStyleName("componentTab");
		buildTab(tabData);

		boolean switchedTab = false;
		if (optionTypeName != null) {
			switchedTab = switchToTab(optionTypeName);
		}
		if (!switchedTab && initialTab < tabData.length) {
			switchToTab(initialTab);
		}

		Dom.addEventListener(scrollPanel.getElement(),  "keydown", event -> {
			KeyboardEvent e = (KeyboardEvent) event;
			if ("ArrowLeft".equals(e.code) || "ArrowRight".equals(e.code)) {
				moveTabSelection("ArrowLeft".equals(e.code) ? -1 : 1);
				event.stopPropagation();
			}
		});
	}

	/**
	 * Creates a tab component with optional scroll indicator buttons.
	 * @param appW {@link org.geogebra.web.html5.main.AppW}
	 * @param ariaLabel aria-label trans key (title of parent element)
	 * @param tabData {@link TabData} including title and panel widget
	 */
	public ComponentTab(AppW appW, String ariaLabel, TabData... tabData) {
		this(appW, ariaLabel, 0, null, tabData);
	}

	private void buildTab(TabData... tabData) {
		buildScrollPanel();
		FlowPanel wrapPanel = new FlowPanel();
		wrapPanel.addStyleName("wrapPanel");

		initTabList();
		buildHeaderWithScrollIndicator(wrapPanel, tabList);

		scrollPanel.add(wrapPanel);
		add(scrollPanel);

		initPanelContainer();
		panelContainer.setWidth((tabData.length * 100) + "%");
		fillTabList(tabList, tabData);
		new FocusableWidget(AccessibilityGroup.SETTINGS_TAB_BUTTON,
				AccessibilityGroup.ViewControlId.SETTINGS_VIEW, tabList) {
			@Override
			public void focus(Widget widget) {
				for (int i = 0; i < tabList.getWidgetCount(); i++) {
					if (tabList.getWidget(i) == selectedBtn) {
						tabList.getWidget(i).getElement().focus();
					}
				}
			}
		}.attachTo(appW);
	}

	private void buildScrollPanel() {
		scrollPanel = new ScrollPanel();
		scrollPanel.addScrollHandler(event -> {
			if (left != null) {
				left.setVisible(scrollPanel.getHorizontalScrollPosition() != 0);
			}
			if (right != null) {
				right.setVisible(scrollPanel.getHorizontalScrollPosition()
						!= scrollPanel.getMaximumHorizontalScrollPosition());
			}
		});
		scrollPanel.addStyleName("scrollPanel customScrollbar");
	}

	private void buildHeaderWithScrollIndicator(FlowPanel wrapPanel, FlowPanel tabList) {
		left = buildScrollButton(KeyboardResources.INSTANCE
				.keyboard_arrowLeft_black(), "left");
		left.addFastClickHandler(source ->
				scrollPanel.setHorizontalScrollPosition(getLeftScroll75Percent()));
		left.setVisible(false);

		right = buildScrollButton(KeyboardResources.INSTANCE
				.keyboard_arrowRight_black(), "right");
		right.addFastClickHandler(source ->
				scrollPanel.setHorizontalScrollPosition(getRightScroll75Percent()));
		right.setVisible(false);

		wrapPanel.add(left);
		wrapPanel.add(tabList);
		wrapPanel.add(right);
	}

	private StandardButton buildScrollButton(SVGResource icon, String styleName) {
		StandardButton scrollButton = new StandardButton(24, icon, "");
		scrollButton.addStyleName(styleName);
		scrollButton.setTabIndex(-1);
		return scrollButton;
	}

	private void initTabList() {
		tabList = new FlowPanel();
		AriaHelper.setRole(tabList, "tablist");
		AriaHelper.setLabel(tabList, loc.getMenu(ariaLabel));
		tabList.addStyleName("tabList");
	}

	private StandardButton getTabBtn(int i, String title) {
		StandardButton tabBtn = new StandardButton(loc.getMenu(title));
		tabBtn.addStyleName("tabBtn");
		tabBtn.addStyleName("ripple");
		tabBtn.addStyleName("keyboardFocus");
		AriaHelper.setRole(tabBtn, "tab");
		AriaHelper.setAriaSelected(tabBtn, false);
		tabBtn.addFastClickHandler(source -> switchToTab(i));
		return tabBtn;
	}

	private void initPanelContainer() {
		panelContainer = new FlowPanel();
		panelContainer.addStyleName("panelContainer");
		add(panelContainer);
	}

	private void fillTabList(FlowPanel tabList, TabData... tabData) {
		int i = 0;
		double width = 100.0 / tabData.length;
		for (TabData tab : tabData) {
			StandardButton tabBtn = getTabBtn(i, loc.getMenu(tab.getTabTitle()));
			tabButton.add(tabBtn);
			tabList.add(tabBtn);
			tab.getTabPanel().getElement().getStyle().setWidth(width, Unit.PCT);
			panelContainer.add(tab.getTabPanel());
			i++;
		}
	}

	private int getLeftScroll75Percent() {
		int scroll75 = (int) (scrollPanel.getOffsetWidth() * 0.75);
		return Math.max(scrollPanel.getMinimumHorizontalScrollPosition(),
				scrollPanel.getHorizontalScrollPosition() - scroll75);
	}

	private int getRightScroll75Percent() {
		int scroll75 = (int) (scrollPanel.getOffsetWidth() * 0.75);
		return Math.min(scrollPanel.getMaximumHorizontalScrollPosition(),
				scrollPanel.getHorizontalScrollPosition() + scroll75);
	}

	/**
	 * switch to tab
	 * @param tabIdx - index of tab to switch to
	 */
	public void switchToTab(int tabIdx) {
		if (selectedBtn != null) {
			updateSelection(selectedBtn, false);
		}

		selectedBtn = tabButton.get(tabIdx);
		selectedBtn.getElement().scrollIntoView();
		updateSelection(selectedBtn, true);
		selectedTabIdx = tabIdx;

		panelContainer.addStyleName("transition");
		tabChanged.notifyListeners(tabIdx);
		panelContainer.getElement().getStyle().setRight(tabIdx * 100, Unit.PCT);
	}

	/**
	 * Find tab with given title and switch to it
	 * @param tabTransKey title of searched tab
	 * @return Whether the tab was switched successfully
	 */
	public boolean switchToTab(String tabTransKey) {
		for (int i = 0; i < tabData.size(); i++) {
			if (tabData.get(i).getTabTitle().equals(tabTransKey)) {
				switchToTab(i);
				return true;
			}
		}
		return false;
	}

	private void updateSelection(StandardButton button, boolean selected) {
		Dom.toggleClass(button, "selected", selected);
		AriaHelper.setAriaSelected(button, selected);
		AriaHelper.setTabIndex(button, selected ? 1 : -1);
	}

	private void moveTabSelection(int increment) {
		int newIndex = (getSelectedTabIdx() + tabButton.size() + increment) % tabButton.size();
		updateContentTabIndex(newIndex);
		switchToTab(newIndex);
		tabButton.get(getSelectedTabIdx()).getElement().focus();
	}

	private void updateContentTabIndex(int newIndex) {
		setContentTabIndex(selectedTabIdx, -1);
		setContentTabIndex(newIndex, 1);
	}

	private void setContentTabIndex(int selectedTab, int tabIndex) {
		Widget panel = tabData.get(selectedTab).getTabPanel();
		if (panel instanceof FlowPanel) {
			int nrOfChildren = ((FlowPanel) panel).getWidgetCount();
			for (int i = 0; i < nrOfChildren; i++) {
				AriaHelper.setTabIndex(((FlowPanel) panel).getWidget(i), tabIndex);
			}
		}
	}

	/**
	 * Updates the visibility of the scroll indicators
	 */
	public void updateScrollIndicators() {
		int panelWidth = getOffsetWidth();
		int tabListWidth = tabList.getOffsetWidth();
		if (panelWidth < tabListWidth) {
			left.setVisible(!isScrolledToTheLeft());
			right.setVisible(!isScrolledToTheRight());
		} else {
			left.setVisible(false);
			right.setVisible(false);
		}
	}

	private boolean isScrolledToTheLeft() {
		return scrollPanel.getHorizontalScrollPosition()
				== scrollPanel.getMinimumHorizontalScrollPosition();
	}

	private boolean isScrolledToTheRight() {
		return scrollPanel.getHorizontalScrollPosition()
				== scrollPanel.getMaximumHorizontalScrollPosition();
	}

	@Override
	public void onResize() {
		panelContainer.removeStyleName("transition");
		panelContainer.getElement().getStyle().setRight(selectedTabIdx * getOffsetWidth(),
				Unit.PX);
	}

	@Override
	public void setLabels() {
		AriaHelper.setLabel(tabList, loc.getMenu(ariaLabel));
		for (int i = 0; i < tabData.size(); i++) {
			tabButton.get(i).setLabel(loc.getMenu(tabData.get(i).getTabTitle()));
		}
	}

	public int getSelectedTabIdx() {
		return selectedTabIdx;
	}

	/**
	 * Registers a listener to be notified whenever the active tab changes.
	 * <p>
	 * The listener is invoked after a tab switch occurs and receives the
	 * zero-based index of the newly selected tab.
	 *
	 * @param listener the callback to notify on tab change; must not be {@code null}
	 */
	public void addTabChangedListener(MulticastEvent.Listener<Integer> listener) {
		tabChanged.addListener(listener);
	}
}
