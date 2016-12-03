package org.geogebra.web.web.gui;

import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.util.MyToggleButton2;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class CustomizeToolbarHeaderPanel extends AuxiliaryHeaderPanel {

	public interface CustomizeToolbarListener {
		void update(int id);
	}

	protected static final int GENERAL = -1;
	private AppW app;
	private FlowPanel buttons;
	private int selectedViewId = GENERAL;
	private CustomizeToolbarListener listener;

	private static class ViewButton extends MyToggleButton2 {

		private int id;

		public ViewButton(Image img, int viewId) {
			super(img);
			this.id = viewId;
		}

	}
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
				ImgResourceHelper.safeURI(pr.menu_icon_graphics()), 24));
		buttons.add(btnGeneral);
		DockPanelW[] panels = ((GuiManagerW) app.getGuiManager()).getLayout()
		        .getDockManager().getPanels();
		for (DockPanelW panel : panels) {
			final int viewId = panel.getViewId();
			if (panel.canCustomizeToolbar()) {
				Log.debug("[customize] view id for button is " + viewId);
				ResourcePrototype res = null;
				switch (viewId) {
				case App.VIEW_DATA_ANALYSIS:
					res = pr.menu_icon_probability();
					break;
				default:
					if (App.isView3D(viewId)) {
						res = pr.menu_icon_graphics3D();
					} else {
						res = panel.getIcon();
					}
				}

				final ViewButton btn = new ViewButton(
						new NoDragImage(ImgResourceHelper.safeURI(res), 24),
						viewId);

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

	private void checkViewButton(int viewId) {
		for (int i = 0; i < buttons.getWidgetCount(); i++) {
			Widget w = buttons.getWidget(i);
			if (w instanceof ViewButton) {
				ViewButton btn = (ViewButton) w;
				btn.setValue(btn.id == viewId);
			} else if (w instanceof MyToggleButton2) {
				((MyToggleButton2) w).setValue(viewId == -1);
			}
		}
	}

	public int getSelectedViewId() {
		return selectedViewId;
	}

	public void setSelectedViewId(int viewId) {
		selectedViewId = viewId;
		checkViewButton(viewId);
	}
}
