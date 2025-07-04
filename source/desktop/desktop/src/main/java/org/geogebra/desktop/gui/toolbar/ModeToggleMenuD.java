/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.toolbar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.desktop.main.AppD;

/**
 * JToggle button combined with popup menu for mode selection
 */
public class ModeToggleMenuD extends JPanel {

	private static final long serialVersionUID = 1L;
	ModeToggleButtonGroup bg;
	private final ToolToggleButton tbutton;
	private ToolToggleButton mouseOverButton;
	private final JPopupMenu popMenu;
	private ArrayList<JMenuItem> menuItemList;

	private ActionListener popupMenuItemListener;
	private AppD app;
	int size;

	private final ToolbarD toolbar;

	final static Color bgColor = Color.white;

	/**
	 * @param app application
	 * @param toolbar toolbar
	 * @param bg button group
	 */
	public ModeToggleMenuD(AppD app, ToolbarD toolbar,
			ModeToggleButtonGroup bg) {
		this.app = app;
		this.bg = bg;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.toolbar = toolbar;

		tbutton = new ToolToggleButton(this, toolbar, app);
		tbutton.setAlignmentY(BOTTOM_ALIGNMENT);
		add(tbutton);

		popMenu = new JPopupMenu();
		popMenu.setBackground(bgColor);
		menuItemList = new ArrayList<>();
		popupMenuItemListener = new MenuItemListener();
		size = 0;

	}

	public int getToolsCount() {
		return size;
	}

	public JToggleButton getJToggleButton() {
		return tbutton;
	}

	/**
	 * @param mode mode
	 * @return success
	 */
	public boolean selectMode(int mode) {
		String modeText = mode + "";
		for (int i = 0; i < size; i++) {
			JMenuItem mi = menuItemList.get(i);
			// found item for mode?
			if (mi.getActionCommand().equals(modeText)) {
				selectItem(mi, ModeSetter.DOCK_PANEL);
				return true;
			}
		}
		return false;
	}

	/**
	 * @return first mode
	 */
	public int getFirstMode() {
		if (menuItemList == null || menuItemList.size() == 0) {
			return -1;
		}
		JMenuItem mi = menuItemList.get(0);
		return Integer.parseInt(mi.getActionCommand());
	}

	private void selectItem(JMenuItem mi, ModeSetter ms) {
		// check if the menu item is already selected
		boolean imageDialog = mi.getActionCommand()
				.equals(Integer.toString(EuclidianConstants.MODE_IMAGE));
		if (tbutton.isSelected()
				&& tbutton.getActionCommand().equals(mi.getActionCommand())
				&& !imageDialog) {
			return;
		}

		tbutton.setIcon(mi.getIcon());
		tbutton.setToolTipText(app
				.getToolTooltipHTML(Integer.parseInt(mi.getActionCommand())));
		tbutton.setActionCommand(mi.getActionCommand());
		tbutton.setSelected(true);
		if (imageDialog && ms == ModeSetter.TOOLBAR) {
			tbutton.doClick();
		}
		// tbutton.requestFocus();
	}

	/**
	 * @param mode mode to add
	 */
	public void addMode(int mode) {
		// add menu item to popup menu
		JMenuItem mi = new JMenuItem();
		mi.setFont(app.getPlainFont());
		mi.setBackground(bgColor);

		// tool name as text
		mi.setText(app.getToolName(mode));

		Icon icon = app.getModeIcon(mode);
		String actionText = Integer.toString(mode);
		mi.setIcon(icon);
		mi.setActionCommand(actionText);
		mi.addActionListener(popupMenuItemListener);

		popMenu.add(mi);
		menuItemList.add(mi);
		size++;

		if (size == 1) {
			// init tbutton
			tbutton.setIcon(icon);
			tbutton.setActionCommand(actionText);

			// tooltip: tool name and tool help
			tbutton.setToolTipText(app.getToolTooltipHTML(mode));

			// add button to button group
			bg.add(tbutton);
		}

		app.setComponentOrientation(mi);

	}

	/**
	 * Removes all modes from the toggle menu. Used for the temporary
	 * perspective.
	 * 
	 * @author Florian Sonner
	 * @version 2008-10-22
	 */
	public void clearModes() {
		popMenu.removeAll();
		menuItemList.clear();
		size = 0;
	}

	/**
	 * Add a separator.
	 */
	public void addSeparator() {
		popMenu.addSeparator();
	}

	// sets new mode when item in popup menu is selected
	private class MenuItemListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem item = (JMenuItem) e.getSource();
			if (!(Integer.toString(EuclidianConstants.MODE_IMAGE)
					.equals(item.getActionCommand()))) {
				selectItem(item, ModeSetter.TOOLBAR);
				tbutton.doClick();
			} else {
				tbutton.setSelected(false);
				tbutton.getModel().setRollover(false);
				tbutton.repaint();
				String oldCmd = tbutton.getActionCommand();
				tbutton.setActionCommand(item.getActionCommand());
				tbutton.doClick();
				tbutton.setActionCommand(oldCmd);
			}

		}
	}

	/**
	 * @param bt button
	 */
	public void setMouseOverButton(ToolToggleButton bt) {
		mouseOverButton = bt;
		repaint();
	}

	public JToggleButton getMouseOverButton() {
		return mouseOverButton;
	}

	/**
	 * Show active popup
	 */
	public void mouseOver() {
		// popup menu is showing
		JPopupMenu activeMenu = bg.getActivePopupMenu();
		if (activeMenu != null && activeMenu.isShowing()) {
			setPopupVisible(true);
		}
	}

	/**
	 * shows / hides popup menu
	 * @param flag whether to show
	 */
	public void setPopupVisible(boolean flag) {
		if (flag) {
			bg.setActivePopupMenu(popMenu);
			if (popMenu.isShowing()) {
				return;
			}
			Point locButton = tbutton.getLocationOnScreen();
			Component component = SwingUtilities.getRootPane(tbutton);
			if (component == null) {
				component = app.getMainComponent(); // if geogebrapanel is
			}
													// inside an awt window
			Point locApp = component.getLocationOnScreen();

			if (toolbar.getOrientation() == SwingConstants.HORIZONTAL) {
				tbutton.repaint();

				int offsetx = 0;
				if (app.getLocalization().isRightToLeftReadingOrder()) {
					// needed otherwise popMenu.getWidth() can return 0
					popMenu.setVisible(true);
					offsetx = popMenu.getWidth() - tbutton.getWidth();
				}

				popMenu.show(component, locButton.x - locApp.x - offsetx,
						locButton.y - locApp.y + tbutton.getHeight());
			} else {
				popMenu.show(component,
						locButton.x - locApp.x + tbutton.getWidth(),
						locButton.y - locApp.y + tbutton.getHeight() / 2);
			}
		} else {
			popMenu.setVisible(false);
		}

		tbutton.repaint();
	}

	public boolean isPopupShowing() {
		return popMenu.isShowing();
	}

}
