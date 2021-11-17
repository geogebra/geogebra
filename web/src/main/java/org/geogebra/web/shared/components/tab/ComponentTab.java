package org.geogebra.web.shared.components.tab;

import java.util.ArrayList;

import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.view.button.StandardButton;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class ComponentTab extends FlowPanel {
	private Localization loc;
	private ArrayList<TabData> tabData;
	private SimplePanel indicator;
	private StandardButton selectedBtn;
	private ArrayList<StandardButton> tabBtns = new ArrayList<>();


	public ComponentTab(ArrayList<TabData> tabData, Localization loc) {
		this.loc = loc;
		this.tabData = tabData;
		addStyleName("componentTab");
		buildGUI();
	}

	private void buildGUI() {
		indicator = new SimplePanel();
		indicator.addStyleName("indicator");
		add(indicator);

		FlowPanel header = new FlowPanel();
		header.addStyleName("header");
		add(header);

		FlowPanel panelContainer = new FlowPanel();
		panelContainer.addStyleName("panelContainer");
		add(panelContainer);

		for (int i = 0; i < tabData.size(); i++) {
			StandardButton tabBtn = new StandardButton(
					loc.getMenu(tabData.get(i).getTabTitle()));
			tabBtn.addStyleName("tabBtn");
			tabBtn.addStyleName("ripple");
			int tabIdx = i;
			tabBtn.addFastClickHandler(source -> {
				if (selectedBtn != null) {
					selectedBtn.removeStyleName("selected");
				}
				tabBtn.addStyleName("selected");
				selectedBtn = tabBtn;
				switchToTab(tabIdx);
			});
			tabBtns.add(tabBtn);

			header.add(tabBtn);
			panelContainer.add(tabData.get(i).getTabPanel());
		}

	}

	private double calculateLeft(int index) {
		double left = 0;
		for (int i = 0; i < index; i++) {
			left += tabBtns.get(i).getOffsetWidth();
		}
		return left;
	}

	private void switchToTab(int tabIdx) {
		Style indicatorStyle = indicator.getElement().getStyle();
		indicatorStyle.setLeft(calculateLeft(tabIdx), Style.Unit.PX);
		indicatorStyle.setWidth(selectedBtn.getOffsetWidth(), Style.Unit.PX);
	}
}
