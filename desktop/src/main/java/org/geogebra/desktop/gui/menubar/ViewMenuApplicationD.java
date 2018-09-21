package org.geogebra.desktop.gui.menubar;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import org.geogebra.common.main.OptionType;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.layout.DockPanelD;
import org.geogebra.desktop.gui.layout.LayoutD;
import org.geogebra.desktop.gui.layout.panels.ConstructionProtocolDockPanel;
import org.geogebra.desktop.gui.virtualkeyboard.VirtualKeyboardD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.KeyboardSettings;
import org.geogebra.desktop.util.GuiResourcesD;

@SuppressWarnings("serial")
public class ViewMenuApplicationD extends ViewMenuD {

	private AbstractAction showKeyboardAction, showAlgebraInputAction;

	private JCheckBoxMenuItem cbShowKeyboard, cbShowInputBar;

	private ShowViewAction[] showViews;
	private JCheckBoxMenuItem[] cbViews;

	private AbstractAction showLayoutOptionsAction;
	/*
	 * Checkbox for construction protocol view.
	 */
	private JCheckBoxMenuItem cbConsprot;

	/**
	 * The "View" menu for the application.
	 */
	public ViewMenuApplicationD(AppD app, LayoutD layout) {
		super(app, layout);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Initialize the menu items.
	 */
	@Override
	protected void initItems() {

		if (!initialized) {
			return;
		}
		initViewItems(this);

		// show/hide keyboard
		if (!app.isApplet()) {
			cbShowKeyboard = new JCheckBoxMenuItem(showKeyboardAction);
			cbShowKeyboard.setIcon(app.getMenuIcon(GuiResourcesD.KEYBOARD));
			KeyboardSettings kbs = (KeyboardSettings) app.getSettings()
					.getKeyboard();
			if (kbs.isShowKeyboardOnStart()) {
				cbShowKeyboard.setSelected(true);
				VirtualKeyboardD vk = ((GuiManagerD) app.getGuiManager())
						.getVirtualKeyboard();
				vk.setVisible(true);
			}
			add(cbShowKeyboard);
		}

		cbShowInputBar = new JCheckBoxMenuItem(showAlgebraInputAction);
		app.setEmptyIcon(cbShowInputBar);
		add(cbShowInputBar);

		addSeparator();

		add(showLayoutOptionsAction);

		addSeparator();

		super.initItems();
	}

	/**
	 * Initialize the actions, which used by applet only (and not by
	 * application).
	 */
	@Override
	protected void initActions() {
		initViewActions();

		// display the layout options dialog
		showLayoutOptionsAction = new AbstractAction(
				loc.getMenu("Layout") + " ...",
				app.getMenuIcon(GuiResourcesD.VIEW_PROPERTIES_16)) {
			public static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.getDialogManager().showPropertiesDialog(OptionType.LAYOUT,
						null);
			}
		};

		showKeyboardAction = new AbstractAction(loc.getMenu("Keyboard")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				if (AppD.isVirtualKeyboardActive()
						&& !((GuiManagerD) app.getGuiManager())
								.showVirtualKeyboard()) {

					// if keyboard is active but hidden, just show it
					((GuiManagerD) app.getGuiManager()).toggleKeyboard(true);
					update();

				} else {

					AppD.setVirtualKeyboardActive(
							!AppD.isVirtualKeyboardActive());
					((GuiManagerD) app.getGuiManager())
							.toggleKeyboard(AppD.isVirtualKeyboardActive());
					update();
				}

			}
		};

		showAlgebraInputAction = new AbstractAction(loc.getMenu("InputField")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.setShowAlgebraInput(!app.showAlgebraInput(), true);
				app.updateContentPane();
			}
		};

		super.initActions();

	}

	private void initViewActions() {
		if (!initialized) {
			return;
		}
		DockPanelD[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanelD.MenuOrderComparator());
		int viewsInMenu = 0;

		// count visible views first..
		for (DockPanelD panel : dockPanels) {
			// skip panels with negative order by design
			if (panel.getMenuOrder() < 0) {
				continue;
			}
			++viewsInMenu;
		}

		// construct array with menu items
		showViews = new ShowViewAction[viewsInMenu];
		{
			int i = 0;
			ShowViewAction action;

			for (DockPanelD panel : dockPanels) {
				// skip panels with negative order by design
				if (panel.getMenuOrder() < 0) {
					continue;
				}

				action = new ShowViewAction(panel);

				showViews[i] = action;
				++i;
			}
		}
	}

	private class ShowViewAction extends AbstractAction {

		private DockPanelD panel;
		private int viewId;
		private JCheckBoxMenuItem cb;

		public ShowViewAction(DockPanelD panel) {
			super(app.getLocalization().getMenu(panel.getViewTitle()));
			this.panel = panel;
			viewId = panel.getViewId();
		}

		public void setCheckBox(JCheckBoxMenuItem cb) {
			this.cb = cb;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {

			((GuiManagerD) app.getGuiManager()).setShowView(
					!((GuiManagerD) app.getGuiManager()).showView(viewId),
					viewId);

			// ensure check box is correctly selected/unselected for case where
			// hide aborted
			cb.setSelected(panel.isVisible());

		}

	}

	@Override
	public void update() {
		if (!initialized) {
			return;
		}

		updateViews();

		cbShowInputBar.setSelected(app.showAlgebraInput());

		if (cbShowKeyboard != null) {
			cbShowKeyboard.setSelected(AppD.isVirtualKeyboardActive());
		}

		// cbShowHandwriting.setSelected(Application.isHandwritingRecognitionActive());
		// cbShowHandwritingAutoAdd.setSelected(Application.isHandwritingRecognitionAutoAdd());
		// cbShowHandwritingTimedAdd.setSelected(Application.isHandwritingRecognitionTimedAdd());
		// cbShowHandwritingTimedRecognise.setSelected(Application.isHandwritingRecognitionTimedRecognise());

		// enable menus if necessary
		// menuInput.setEnabled(app.showAlgebraInput());
		// menuToolBar.setEnabled(app.showToolBar());

	}

	private void updateViews() {

		if (!initialized) {
			return;
		}

		DockPanelD[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanelD.MenuOrderComparator());

		// update views
		{
			int i = 0;

			for (DockPanelD panel : dockPanels) {
				// skip panels with negative order by design
				if (panel.getMenuOrder() < 0) {
					continue;
				}

				if (cbViews[i] == null) {
					return;
				}

				cbViews[i].setSelected(((GuiManagerD) app.getGuiManager())
						.showView(panel.getViewId()));
				++i;
			}
		}
	}

	private void initViewItems(JMenu menu) {
		if (!initialized) {
			return;
		}
		DockPanelD[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanelD.MenuOrderComparator());
		int viewsInMenu = 0;

		// count visible views first..
		for (DockPanelD panel : dockPanels) {
			// skip panels with negative order by design
			if (panel.getMenuOrder() < 0) {
				continue;
			}
			++viewsInMenu;
		}

		// construct array with menu items
		cbViews = new JCheckBoxMenuItem[viewsInMenu];
		{
			int i = 0;
			JCheckBoxMenuItem cb;

			for (DockPanelD panel : dockPanels) {
				// skip panels with negative order by design
				if (panel.getMenuOrder() < 0) {
					continue;
				}

				cb = new JCheckBoxMenuItem(showViews[i]);
				showViews[i].setCheckBox(cb);
				cb.setIcon(panel.getIcon());

				if (panel.hasMenuShortcut()) {
					setMenuShortCutShiftAccelerator(cb,
							panel.getMenuShortcut());
				}

				menu.add(cb);
				cbViews[i] = cb;
				if (panel instanceof ConstructionProtocolDockPanel) {
					cbConsprot = cb;
				}
				++i;
			}
		}
	}

	/**
	 * Checkbox of Construction protocol view will be checked if visible is
	 * true. Otherwise won't be checked.
	 */
	public void updateCPView(boolean selected) {
		if (cbConsprot != null) {
			cbConsprot.setSelected(selected);
		}
	}

}
