package org.geogebra.web.web.gui;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.util.MyToggleButton2;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class CustomizeToolbarHeaderPanel extends AuxiliaryHeaderPanel {

	public interface CustomizeToolbarListener {
		void update(int id);
	}

	protected static final int GENERAL = -1;
	private AppW app;
	private FlowPanel buttons;
	private int selectedViewId;
	private CustomizeToolbarListener listener;

	CustomizeToolbarHeaderPanel(AppW app, MyHeaderPanel gui) {
		super(app.getLocalization(), gui);
		this.app = app;
		this.listener = (CustomizeToolbarListener) gui;
		createPanelsToolbar();
		setLabels();
	}

	@Override
	public void setLabels() {
		setText(loc.getMenu("Toolbar.Customize"));
	}

	private void createPanelsToolbar() {
		buttons = new FlowPanel();
		buttons.setStyleName("panelRow");
		PerspectiveResources pr = ((ImageFactory) GWT
		        .create(ImageFactory.class)).getPerspectiveResources();
		final MyToggleButton2 btnGeneral = new MyToggleButton2(new NoDragImage(
		        GGWToolBar.safeURI(pr.menu_icon_graphics()), 24));
		buttons.add(btnGeneral);
		DockPanelW[] panels = ((GuiManagerW) app.getGuiManager()).getLayout()
		        .getDockManager().getPanels();
		for (DockPanelW panel : panels) {
			final int viewId = panel.getViewId();
			if (panel.canCustomizeToolbar()) {
				App.debug("[customize] view id for button is " + viewId);
				ResourcePrototype res = null;
				switch (viewId) {
				case App.VIEW_EUCLIDIAN3D:
					res = pr.menu_icon_graphics3D();
					break;
				case App.VIEW_DATA_ANALYSIS:
					res = pr.menu_icon_probability();
					break;
				default:
					res = panel.getIcon();
				}

				final MyToggleButton2 btn = new MyToggleButton2(
				        new NoDragImage(GGWToolBar.safeURI(res), 24));
				btn.addClickHandler(new ClickHandler() {

					public void onClick(ClickEvent event) {
						uncheckAll(btn);
						selectedViewId = viewId;
						listener.update(selectedViewId);
					}
				});
				buttons.add(btn);
			}
		}
		btnGeneral.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				uncheckAll(btnGeneral);
				selectedViewId = GENERAL;
				listener.update(selectedViewId);
			}
		});

		selectedViewId = GENERAL;

		rightPanel.add(buttons);
		add(rightPanel);

	}

	private void uncheckAll(MyToggleButton2 current) {
		for (int i = 0; i < buttons.getWidgetCount(); i++) {
			Widget w = buttons.getWidget(i);
			if (w instanceof MyToggleButton2 && w != current) {
				((MyToggleButton2) w).setValue(false);
			}
		}
	}

	public int getSelectedViewId() {
		return selectedViewId;
	}

}
