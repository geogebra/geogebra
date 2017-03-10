package org.geogebra.web.web.gui;

import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.util.MyToggleButtonW;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * GUI for toolbar customization
 * 
 * @author Laszlo
 *
 */
public class CustomizeToolbarHeaderPanel extends AuxiliaryHeaderPanel {

	/**
	 * Listens to updates of the toolbar
	 */
	public interface CustomizeToolbarListener {
		/**
		 * @param id
		 *            toolbar ID
		 */
		void update(int id);
	}

	/**
	 * General toolbar id
	 */
	protected static final int GENERAL = -1;
	private AppW app;
	private FlowPanel buttons;
	private int selectedViewId = GENERAL;
	private CustomizeToolbarListener listener;

	private static class ViewButton extends MyToggleButtonW {

		private int id;

		public ViewButton(Image img, int viewId) {
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
		PerspectiveResources pr = ((ImageFactory) GWT
		        .create(ImageFactory.class)).getPerspectiveResources();
		final MyToggleButtonW btnGeneral = new MyToggleButtonW(new NoDragImage(
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

					@Override
					public void onClick(ClickEvent event) {
						selectAndUpdate(btn, viewId);
					}
				});


				buttons.add(btn);
			}
		}
		btnGeneral.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				selectAndUpdate(btnGeneral, GENERAL);

			}
		});

		selectedViewId = GENERAL;

		rightPanel.add(buttons);
		add(rightPanel);

	}

	/**
	 * @param btn
	 *            clicked button
	 * @param viewId
	 *            view ID
	 */
	protected void selectAndUpdate(MyToggleButtonW btn, int viewId) {
		uncheckAll(btn);
		selectedViewId = viewId;
		listener.update(selectedViewId);

	}

	private void uncheckAll(MyToggleButtonW current) {
		for (int i = 0; i < buttons.getWidgetCount(); i++) {
			Widget w = buttons.getWidget(i);
			if (w instanceof MyToggleButtonW && w != current) {
				((MyToggleButtonW) w).setValue(false);
			}
		}
	}

	private void checkViewButton(int viewId) {
		for (int i = 0; i < buttons.getWidgetCount(); i++) {
			Widget w = buttons.getWidget(i);
			if (w instanceof ViewButton) {
				ViewButton btn = (ViewButton) w;
				btn.setValue(btn.getId() == viewId);
			} else if (w instanceof MyToggleButtonW) {
				((MyToggleButtonW) w).setValue(viewId == -1);
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
	 * @param viewId
	 *            new view ID
	 */
	public void setSelectedViewId(int viewId) {
		selectedViewId = viewId;
		checkViewButton(viewId);
	}
}
