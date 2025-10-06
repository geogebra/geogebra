package org.geogebra.web.shared.components.tab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.MulticastEvent;
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
	private FlowPanel tabList;
	private StandardButton selectedBtn;
	private StandardButton left;
	private StandardButton right;
	private final List<TabData> tabData;
	private final ArrayList<StandardButton> tabButton = new ArrayList<>();
	private int selectedTabIdx = 0;
	private final MulticastEvent<Integer> tabChanged = new MulticastEvent<>();

	/**
	 * Creates a tab component with optional scroll indicator buttons.
	 * @param loc {@link Localization}
	 * @param tabData {@link TabData} including title and panel widget
	 */
	public ComponentTab(Localization loc, TabData... tabData) {
		this.loc = loc;
		this.tabData = Arrays.asList(tabData);
		addStyleName("componentTab");
		buildTab(tabData);
		if (tabData.length > 0) {
			switchToTab(0);
		}
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
		return scrollButton;
	}

	private void initTabList() {
		tabList = new FlowPanel();
		AriaHelper.setRole(tabList, "tablist");
		tabList.addStyleName("tabList");
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
		Scheduler.get().scheduleDeferred(() -> {
			panelContainer.getElement().getStyle()
					.setRight(tabIdx * 100, Unit.PCT);
			tabChanged.notifyListeners(tabIdx);
		});
	}

	private void updateSelection(StandardButton button, boolean selected) {
		Dom.toggleClass(button, "selected", selected);
		AriaHelper.setAriaSelected(button, selected);
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
