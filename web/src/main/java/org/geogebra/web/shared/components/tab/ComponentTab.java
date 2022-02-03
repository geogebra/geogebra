package org.geogebra.web.shared.components.tab;

import java.util.ArrayList;

import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.view.button.StandardButton;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;

public class ComponentTab extends FlowPanel implements RequiresResize {
	private Localization loc;
	private SimplePanel indicator;
	private FlowPanel panelContainer;
	private StandardButton selectedBtn;
	private ArrayList<StandardButton> tabBtns = new ArrayList<>();
	private int selectedTabIdx = 0;

	/**
	 * tab component constructor
	 * @param tabData - data of tab including title and panel widget
	 * @param loc - localization
	 */
	public ComponentTab(ArrayList<TabData> tabData, Localization loc) {
		this.loc = loc;
		addStyleName("componentTab");
		buildGUI(tabData);
	}

	private void buildGUI(ArrayList<TabData> tabData) {
		indicator = new SimplePanel();
		indicator.addStyleName("indicator");
		add(indicator);

		FlowPanel header = new FlowPanel();
		header.addStyleName("header");
		add(header);

		panelContainer = new FlowPanel();
		panelContainer.addStyleName("panelContainer");
		add(panelContainer);

		for (int i = 0; i < tabData.size(); i++) {
			StandardButton tabBtn = getTabBtn(i, tabData.get(i).getTabTitle());
			tabBtns.add(tabBtn);
			header.add(tabBtn);
			panelContainer.add(tabData.get(i).getTabPanel());
		}
	}

	private StandardButton getTabBtn(int i, String title) {
		StandardButton tabBtn = new StandardButton(loc.getMenu(title));
		tabBtn.addStyleName("tabBtn");
		tabBtn.addStyleName("ripple");
		int tabIdx = i;
		tabBtn.addFastClickHandler(source -> switchToTab(tabIdx));
		return tabBtn;
	}

	private double calculateLeft(int index) {
		double left = 0;
		for (int i = 0; i < index; i++) {
			left += tabBtns.get(i).getOffsetWidth();
		}
		return left;
	}

	/**
	 * switch to tab
	 * @param tabIdx - index of tab to switch to
	 */
	public void switchToTab(int tabIdx) {
		if (selectedBtn != null) {
			selectedBtn.removeStyleName("selected");
		}
		tabBtns.get(tabIdx).addStyleName("selected");
		selectedBtn = tabBtns.get(tabIdx);
		selectedTabIdx = tabIdx;

		Style indicatorStyle = indicator.getElement().getStyle();
		indicatorStyle.setLeft(calculateLeft(tabIdx), Style.Unit.PX);
		indicatorStyle.setWidth(selectedBtn.getOffsetWidth(), Style.Unit.PX);

		panelContainer.addStyleName("transition");
		Scheduler.get().scheduleDeferred(() -> panelContainer.getElement().getStyle()
				.setRight(tabIdx * getOffsetWidth(), Style.Unit.PX));
	}

	public FlowPanel getPanelContainer() {
		return panelContainer;
	}

	@Override
	public void onResize() {
		panelContainer.removeStyleName("transition");
		panelContainer.getElement().getStyle().setRight(selectedTabIdx * getOffsetWidth(),
				Style.Unit.PX);
	}
}
