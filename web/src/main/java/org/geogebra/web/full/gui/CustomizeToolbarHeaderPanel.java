package org.geogebra.web.full.gui;

import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.resources.client.ResourcePrototype;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * GUI for toolbar customization
 */
public class CustomizeToolbarHeaderPanel extends AuxiliaryHeaderPanel {

	/**
	 * General toolbar id
	 */
	protected static final int GENERAL = -1;
	private AppW app;
	private FlowPanel buttons;
	private int selectedViewId = GENERAL;
	private CustomizeToolbarListener listener;

	/**
	 * Listens to updates of the toolbar
	 */
	public interface CustomizeToolbarListener {
		/**
		 * @param id - toolbar ID
		 */
		void update(int id);
	}

	private static class ViewButton extends ToggleButton {
		private int id;

		public ViewButton(ResourcePrototype img, int viewId) {
			super(img);
			this.id = viewId;
		}

		public int getId() {
			return id;
		}

	}

	/**
	 * @param app
	 *            application
	 * @param gui
	 *            frame
	 */
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
		SvgPerspectiveResources pr = SvgPerspectiveResources.INSTANCE;
		final ToggleButton btnGeneral = new ToggleButton(pr.menu_icon_graphics());
		buttons.add(btnGeneral);
		DockPanelW[] panels = ((GuiManagerW) app.getGuiManager()).getLayout()
		        .getDockManager().getPanels();
		for (DockPanelW panel : panels) {
			final int viewId = panel.getViewId();
			if (panel.canCustomizeToolbar()) {
				Log.debug("[customize] view id for button is " + viewId);
				ResourcePrototype res = null;
				if (viewId == App.VIEW_DATA_ANALYSIS) {
					res = pr.menu_icon_probability();
				} else if (App.isView3D(viewId)) {
					res = pr.menu_icon_graphics3D();
				} else {
					res = panel.getIcon();
				}

				final ViewButton btn = new ViewButton(res, viewId);
				btn.addFastClickHandler(event -> selectAndUpdate(btn, viewId));
				buttons.add(btn);
			}
		}
		btnGeneral.addFastClickHandler(event -> selectAndUpdate(btnGeneral, GENERAL));

		selectedViewId = GENERAL;

		rightPanel.add(buttons);
		add(rightPanel);
	}

	/**
	 * @param btn - clicked button
	 * @param viewId - view ID
	 */
	protected void selectAndUpdate(ToggleButton btn, int viewId) {
		uncheckAll(btn);
		selectedViewId = viewId;
		listener.update(selectedViewId);
	}

	private void uncheckAll(ToggleButton current) {
		for (int i = 0; i < buttons.getWidgetCount(); i++) {
			Widget w = buttons.getWidget(i);
			if (w instanceof ToggleButton && w != current) {
				((ToggleButton) w).setSelected(false);
			}
		}
	}

	private void checkViewButton(int viewId) {
		for (int i = 0; i < buttons.getWidgetCount(); i++) {
			Widget w = buttons.getWidget(i);
			if (w instanceof ViewButton) {
				ViewButton btn = (ViewButton) w;
				btn.setSelected(btn.getId() == viewId);
			}
		}
	}

	/**
	 * @return view ID
	 */
	public int getSelectedViewId() {
		return selectedViewId;
	}

	/**
	 * @param viewId - new view ID
	 */
	public void setSelectedViewId(int viewId) {
		selectedViewId = viewId;
		checkViewButton(viewId);
	}
}
