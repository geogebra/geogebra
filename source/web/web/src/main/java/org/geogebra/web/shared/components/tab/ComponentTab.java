package org.geogebra.web.shared.components.tab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.ScrollPanel;

public class ComponentTab extends FlowPanel implements RequiresResize, SetLabels {
	private final Localization loc;
	private ScrollPanel scrollPanel;
	private FlowPanel panelContainer;
	private StandardButton selectedBtn;
	private StandardButton left;
	private StandardButton right;
	private final List<TabData> tabData;
	private final ArrayList<StandardButton> tabButton = new ArrayList<>();
	private int selectedTabIdx = 0;

	/**
	 * Creates a tab component without scroll indicator buttons.
	 * @param loc {@link Localization}
	 * @param tabData {@link TabData} including title and panel widget
	 */
	public ComponentTab(Localization loc, TabData... tabData) {
		this(loc, false, tabData);
	}

	/**
	 * Creates a tab component with optional scroll indicator buttons.
	 * @param loc {@link Localization}
	 * @param addScrollButton whether scroll indicator buttons should be added or not
	 * @param tabData {@link TabData} including title and panel widget
	 */
	public ComponentTab(Localization loc, boolean addScrollButton, TabData... tabData) {
		this.loc = loc;
		this.tabData = Arrays.asList(tabData);
		addStyleName("componentTab");
		buildTab(addScrollButton, tabData);
		switchToTab(0);
	}

	private void buildTab(boolean addScrollButton, TabData... tabData) {
		buildScrollPanel();
		FlowPanel wrapPanel = new FlowPanel();
		wrapPanel.addStyleName("wrapPanel");

		FlowPanel tabList = initTabList();

		if (addScrollButton) {
			buildHeaderWithScrollIndicator(wrapPanel, tabList);
		} else {
			wrapPanel.add(tabList);
		}

		scrollPanel.add(wrapPanel);
		add(scrollPanel);

		addPanelContainer();
		panelContainer.setWidth((tabData.length * 100) + "%");
		fillTabList(tabList, tabData);
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

		wrapPanel.add(left);
		wrapPanel.add(tabList);
		wrapPanel.add(right);
	}

	private StandardButton buildScrollButton(SVGResource icon, String styleName) {
		StandardButton scrollButton = new StandardButton(24, icon, "");
		scrollButton.addStyleName(styleName);
		return scrollButton;
	}

	private FlowPanel initTabList() {
		FlowPanel tabList = new FlowPanel();
		AriaHelper.setRole(tabList, "tablist");
		tabList.addStyleName("tabList");
		return tabList;
	}

	private StandardButton getTabBtn(int i, String title) {
		StandardButton tabBtn = new StandardButton(loc.getMenu(title));
		tabBtn.addStyleName("tabBtn");
		tabBtn.addStyleName("ripple");
		AriaHelper.setRole(tabBtn, "tab");
		AriaHelper.setAriaSelected(tabBtn, false);
		tabBtn.addFastClickHandler(source -> switchToTab(i));
		return tabBtn;
	}

	private void addPanelContainer() {
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
		Scheduler.get().scheduleDeferred(() -> panelContainer.getElement().getStyle()
				.setRight(tabIdx * 100, Unit.PCT));
	}

	private void updateSelection(StandardButton button, boolean selected) {
		Dom.toggleClass(button, "selected", selected);
		AriaHelper.setAriaSelected(button, selected);
	}

	@Override
	public void onResize() {
		panelContainer.removeStyleName("transition");
		panelContainer.getElement().getStyle().setRight(selectedTabIdx * getOffsetWidth(),
				Unit.PX);
	}

	@Override
	public void setLabels() {
		for (int i = 0; i < tabData.size(); i++) {
			tabButton.get(i).setLabel(loc.getMenu(tabData.get(i).getTabTitle()));
		}
	}

	public int getSelectedTabIdx() {
		return selectedTabIdx;
	}
}
