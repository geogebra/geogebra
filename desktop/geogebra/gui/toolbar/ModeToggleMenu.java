/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui.toolbar;

import geogebra.main.AppD;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;

/**
 * JToggle button combined with popup menu for mode selection
 */
public class ModeToggleMenu extends JPanel {

	private static final long serialVersionUID = 1L;
	ModeToggleButtonGroup bg;
	private MyJToggleButton tbutton, mouseOverButton;
	private JPopupMenu popMenu;
	private ArrayList<JMenuItem> menuItemList;

	private ActionListener popupMenuItemListener;
	private AppD app;
	int size;
	
	private ToolbarD toolbar;

	final static Color bgColor = Color.white;

	public ModeToggleMenu(AppD app, ToolbarD toolbar,
			ModeToggleButtonGroup bg) {
		this.app = app;
		this.bg = bg;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.toolbar = toolbar;

		tbutton = new MyJToggleButton(this, toolbar);
		tbutton.setAlignmentY(BOTTOM_ALIGNMENT);
		add(tbutton);

		popMenu = new JPopupMenu();
		popMenu.setBackground(bgColor);
		menuItemList = new ArrayList<JMenuItem>();
		popupMenuItemListener = new MenuItemListener();
		size = 0;
		
	}

	public int getToolsCount() {
		return size;
	}

	public JToggleButton getJToggleButton() {
		return tbutton;
	}

	public boolean selectMode(int mode) {
		String modeText = mode + "";

		for (int i = 0; i < size; i++) {
			JMenuItem mi = menuItemList.get(i);
			// found item for mode?
			if (mi.getActionCommand().equals(modeText)) {
				selectItem(mi);
				return true;
			}
		}
		return false;
	}

	public int getFirstMode() {
		if (menuItemList == null || menuItemList.size() == 0) {
			return -1;
		}
		JMenuItem mi = menuItemList.get(0);
		return Integer.parseInt(mi.getActionCommand());
	}

	private void selectItem(JMenuItem mi) {
		// check if the menu item is already selected
		if (tbutton.isSelected()
				&& tbutton.getActionCommand() == mi.getActionCommand()) {
			return;
		}

		tbutton.setIcon(mi.getIcon());
		tbutton.setToolTipText(app.getToolTooltipHTML(Integer.parseInt(mi
				.getActionCommand())));
		tbutton.setActionCommand(mi.getActionCommand());
		tbutton.setSelected(true);
		// tbutton.requestFocus();
	}

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

	public void addSeparator() {
		popMenu.addSeparator();
	}

	// sets new mode when item in popup menu is selected
	private class MenuItemListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			JMenuItem item = (JMenuItem) e.getSource();
			selectItem(item);
			tbutton.doClick();
		}
	}

	public void setMouseOverButton(MyJToggleButton bt) {
		mouseOverButton = bt;
		repaint();
	}

	public JToggleButton getMouseOverButton() {
		return mouseOverButton;
	}

	public void mouseOver() {
		// popup menu is showing
		JPopupMenu activeMenu = geogebra.javax.swing.GPopupMenuD.getImpl(bg.getActivePopupMenu());
		if (activeMenu != null && activeMenu.isShowing()) {
			setPopupVisible(true);
		}
	}

	// shows popup menu
	public void setPopupVisible(boolean flag) {
		if (flag) {
			bg.setActivePopupMenu(new geogebra.javax.swing.GPopupMenuD(popMenu));
			if (popMenu.isShowing())
				return;
			Point locButton = tbutton.getLocationOnScreen();
			Component component = SwingUtilities.getRootPane(tbutton);
			if (component == null)
				component = app.getMainComponent(); // if geogebrapanel is
													// inside an awt window
			Point locApp = component.getLocationOnScreen();
			
			
			if (toolbar.getOrientation() == SwingConstants.HORIZONTAL) {
				tbutton.repaint();
				
				int offsetx = 0;
				if (app.isRightToLeftReadingOrder()) {				
					// needed otherwise popMenu.getWidth() can return 0
					popMenu.setVisible(true);
					offsetx = popMenu.getWidth() - tbutton.getWidth();
				}

				popMenu.show(component, locButton.x - locApp.x - offsetx, locButton.y
						- locApp.y + tbutton.getHeight());
			} else {
				popMenu.show(component, locButton.x - locApp.x + tbutton.getWidth(), locButton.y
						- locApp.y + tbutton.getHeight()/2);
			}
		} else {
			popMenu.setVisible(false);
		}

		tbutton.repaint();
	}

	public boolean isPopupShowing() {
		return popMenu.isShowing();
	}

	public void setMode(int mode) {
		app.setMode(mode);
	}

}

class MyJToggleButton extends JToggleButton implements MouseListener,
		MouseMotionListener, ActionListener {

	private static final long serialVersionUID = 1L;

	private int BORDER = 6;
	private int iconWidth, iconHeight;
	private GeneralPath gp;
	private boolean showToolTipText = true;
	boolean popupTriangleHighlighting = false;
	boolean popupTriangleClicked = false;
	ModeToggleMenu menu;

	private static final Color arrowColor = new Color(0, 0, 0, 130);
	// private static final Color selColor = new Color(166, 11, 30,150);
	// private static final Color selColor = new Color(17, 26, 100, 200);
	private static final Color selColor = new Color(0, 0, 153, 200);
	private static final BasicStroke selStroke = new BasicStroke(3f);

	private Timer showMenuTimer;
	private ToolbarD toolbar;
	
	MyJToggleButton(ModeToggleMenu menu, ToolbarD toolbar) {
		super();
		this.menu = menu;
		this.toolbar = toolbar;
		// add own listeners
		addMouseListener(this);
		addMouseMotionListener(this);
		addActionListener(this);
	}

	@Override
	public String getToolTipText() {
		if (showToolTipText) {
			return super.getToolTipText();
		}
		return null;
	}

	// set mode
	public void actionPerformed(ActionEvent e) {

		// don't select mode if popup triangle clicked
		if (!popupTriangleClicked || !menu.isPopupShowing()) {
			menu.setMode(Integer.parseInt(e.getActionCommand()));
		}
	}

	@Override
	public void setIcon(Icon icon) {
		super.setIcon(icon);
		iconWidth = icon.getIconWidth();
		iconHeight = icon.getIconHeight();
		BORDER = (int) Math.round(icon.getIconWidth() * 0.1875); // 6 pixel
																	// border
																	// for 32
																	// pixel
																	// icon
		Dimension dim = new Dimension(iconWidth + 2 * BORDER, iconHeight + 2
				* BORDER);
		setPreferredSize(dim);
		setMinimumSize(dim);
		setMaximumSize(dim);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Stroke oldStroke = g2.getStroke();

		super.paint(g2);

		if (isSelected()) {
			g2.setColor(selColor);
			g2.setStroke(selStroke);
			g2.drawRect(BORDER - 1, BORDER - 1, iconWidth + 1, iconHeight + 1);

			g2.setStroke(oldStroke);
		}

		// draw little arrow (for popup menu)
		if (menu.size > 1) {
			if (gp == null)
				initPath();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			if (menu.getMouseOverButton() == this
					&& (popupTriangleHighlighting || menu.isPopupShowing())) {
				
				int x = BORDER + iconWidth + 2;
				int y = BORDER + iconHeight + 1;
				
				// background glow circle
				g2.setColor(Color.LIGHT_GRAY);
				g2.fillOval(x-9, y-9, 12, 12);
				
				g2.setColor(Color.red);
				g2.fill(gp);
				g2.setColor(Color.black);
				g2.draw(gp);
			} else {
				g2.setColor(Color.white);
				g2.fill(gp);
				g2.setColor(arrowColor);
				g2.draw(gp);
			}
		}
	}

	private void initPath() {
		gp = new GeneralPath();
		int x = BORDER + iconWidth + 2;
		int y = BORDER + iconHeight + 1;

		if (iconWidth > 20) {
			gp.moveTo(x - 6, y - 5);
			gp.lineTo(x, y - 5);
			gp.lineTo(x - 3, y);
		} else {
			gp.moveTo(x - 4, y - 3);
			gp.lineTo(x, y - 3);
			gp.lineTo(x - 2, y);
		}
		gp.closePath();
	}

	private boolean popupTriangleClicked(int x, int y) {
		popupTriangleClicked = (menu.size > 1 && y > iconHeight - 4);
		return popupTriangleClicked;
	}

	public void mouseClicked(MouseEvent e) {
		if (!menu.isPopupShowing() && e.getClickCount()==2) {
			menu.setPopupVisible(true);
		}
	}

	 int defaultInitialDelay;

	public void mouseEntered(MouseEvent arg0) {

		defaultInitialDelay = ToolTipManager.sharedInstance().getInitialDelay();
		if (toolbar.preventToolTipDelay()) {
			ToolTipManager.sharedInstance().setInitialDelay(0);
		}
		menu.setMouseOverButton(this);
	}

	public void mousePressed(MouseEvent e) {
		if (!menu.isPopupShowing() && popupTriangleClicked(e.getX(), e.getY())) {
			menu.setPopupVisible(true);
			this.getModel().setArmed(false);
		} else {
			// Display the menu after a specific amount of time as well, start
			// a timer for this. The mouseReleased method stops this timer,
			// which ensures that the menu is not displayed if the user stopped
			// to press this button.
			if (showMenuTimer == null) {
				showMenuTimer = new Timer(1000, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						menu.setPopupVisible(true);
					}
				});
				showMenuTimer.setRepeats(false);
			}

			showMenuTimer.start();
		}
	}

	public void mouseExited(MouseEvent arg0) {
		menu.setMouseOverButton(null);
		if (popupTriangleHighlighting) {
			popupTriangleHighlighting = false;
			repaint();
		}

		// Stop the timer to show the menu if the user is no longer
		// hovering over this button.
		if (showMenuTimer != null && showMenuTimer.isRunning()) {
			showMenuTimer.stop();
		}
		
		ToolTipManager.sharedInstance().setInitialDelay(defaultInitialDelay);
	}

	public void mouseReleased(MouseEvent e) {
		if (menu.isPopupShowing() && !popupTriangleClicked(e.getX(), e.getY())) {
			menu.setPopupVisible(false);
		}

		// Stop the timer to show the menu if the user released his mouse. In
		// case
		// the timer already executed his action this has no effect anyway.
		if (showMenuTimer != null && showMenuTimer.isRunning()) {
			showMenuTimer.stop();
		}
	}

	@Override
	public void doClick() {
		super.doClick();
		if (!hasFocus())
			requestFocusInWindow();
	}

	public void mouseDragged(MouseEvent e) {
		if (popupTriangleClicked(e.getX(), e.getY())) {
			menu.setPopupVisible(true);
		}
	}

	public void mouseMoved(MouseEvent e) {
		menu.mouseOver();
		showToolTipText = !menu.isPopupShowing();

		// highlight popup menu triangle
		if (menu.size > 1
				&& popupTriangleHighlighting != popupTriangleClicked(e.getX(),
						e.getY())) {
			popupTriangleHighlighting = !popupTriangleHighlighting;
			repaint();
		}
	}

	
	private JToolTip tip;

	@Override
	public JToolTip createToolTip() {
		tip = super.createToolTip();
		tip.setBorder(BorderFactory.createCompoundBorder(tip.getBorder(),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		return tip;
	}
	
	 @Override
	public Point getToolTipLocation(MouseEvent event){
		 Point p = new Point();
		 p.y = this.getY()+this.getHeight();
		 p.x = this.getX();
		 return  p;
	 }
	
}
