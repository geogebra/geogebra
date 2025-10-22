package org.geogebra.desktop.gui.toolbar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.ToolTipManager;

import org.geogebra.common.main.App;
import org.geogebra.desktop.awt.GGraphics2DD;

class ToolToggleButton extends JToggleButton
		implements MouseListener, MouseMotionListener, ActionListener {

	private static final long serialVersionUID = 1L;

	private int BORDER = 6;
	private int iconWidth;
	private int iconHeight;
	private GeneralPath gp;
	private boolean showToolTipText = true;
	boolean popupTriangleHighlighting = false;
	boolean popupTriangleClicked = false;
	ModeToggleMenuD menu;

	private static final Color arrowColor = new Color(0, 0, 0, 130);
	// private static final Color selColor = new Color(166, 11, 30,150);
	// private static final Color selColor = new Color(17, 26, 100, 200);
	private static final Color selColor = new Color(0, 0, 153, 200);
	private static final BasicStroke selStroke = new BasicStroke(3f);

	private Timer showMenuTimer;
	private ToolbarD toolbar;
	private App app;

	ToolToggleButton(ModeToggleMenuD menu, ToolbarD toolbar, App app) {
		super();
		this.menu = menu;
		this.toolbar = toolbar;
		this.app = app;
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
	@Override
	public void actionPerformed(ActionEvent e) {

		// don't select mode if popup triangle clicked
		if (!popupTriangleClicked || !menu.isPopupShowing()) {
			app.setMode(Integer.parseInt(e.getActionCommand()));
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
		Dimension dim = new Dimension(iconWidth + 2 * BORDER,
				iconHeight + 2 * BORDER);
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
			if (gp == null) {
				initPath();
			}
			GGraphics2DD.setAntialiasing(g2);

			if (menu.getMouseOverButton() == this
					&& (popupTriangleHighlighting || menu.isPopupShowing())) {

				int x = BORDER + iconWidth + 2;
				int y = BORDER + iconHeight + 1;

				// background glow circle
				g2.setColor(Color.LIGHT_GRAY);
				if (iconWidth <= 32) {
					g2.fillOval(x - 9, y - 9, 12, 12);
				} else if (iconWidth <= 40) {
					g2.fillOval(x - 10, y - 9, 13, 13);
				} else if (iconWidth <= 50) {
					g2.fillOval(x - 11, y - 9, 14, 14);
				} else {
					g2.fillOval(x - 12, y - 12, 15, 15);
				}
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

		if (iconWidth <= 40) {
			gp.moveTo(x - 6, y - 5);
			gp.lineTo(x, y - 5);
			gp.lineTo(x - 3, y);
		} else {
			gp.moveTo(x - 8, y - 7);
			gp.lineTo(x, y - 7);
			gp.lineTo(x - 4, y);
		}

		/*
		 * if (iconWidth > 32) { gp.moveTo(x - 8, y - 7); gp.lineTo(x, y - 7);
		 * gp.lineTo(x - 4, y); } else if (iconWidth > 20) { gp.moveTo(x - 6, y
		 * - 5); gp.lineTo(x, y - 5); gp.lineTo(x - 3, y); } else { gp.moveTo(x
		 * - 4, y - 3); gp.lineTo(x, y - 3); gp.lineTo(x - 2, y); }
		 */
		gp.closePath();
	}

	private boolean popupTriangleClicked(int x, int y) {
		popupTriangleClicked = menu.size > 1 && y > iconHeight - 4;
		return popupTriangleClicked;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!menu.isPopupShowing() && e.getClickCount() == 2) {
			menu.setPopupVisible(true);
		}
	}

	int defaultInitialDelay;

	@Override
	public void mouseEntered(MouseEvent arg0) {

		defaultInitialDelay = ToolTipManager.sharedInstance().getInitialDelay();
		if (toolbar.preventToolTipDelay()) {
			ToolTipManager.sharedInstance().setInitialDelay(0);
		}
		menu.setMouseOverButton(this);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!menu.isPopupShowing()
				&& popupTriangleClicked(e.getX(), e.getY())) {
			menu.setPopupVisible(true);
			this.getModel().setArmed(false);
		} else {
			// Display the menu after a specific amount of time as well, start
			// a timer for this. actionPerformed method stops this timer,
			// which ensures that the menu is displayed after user released the
			// mouse
			if (showMenuTimer == null) {
				showMenuTimer = new Timer(1000, e1 -> {
					menu.setPopupVisible(true);
					showMenuTimer.stop();

				});
				showMenuTimer.setRepeats(false);
			}

			showMenuTimer.start();
		}
	}

	@Override
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

	@Override
	public void mouseReleased(MouseEvent e) {
		// nothing to do
	}

	@Override
	public void doClick() {
		super.doClick();
		if (!hasFocus()) {
			requestFocusInWindow();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (popupTriangleClicked(e.getX(), e.getY())) {
			menu.setPopupVisible(true);
		}
	}

	@Override
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
	public Point getToolTipLocation(MouseEvent event) {

		Point p = new Point();
		switch (app.getToolbarPosition()) {
		case SwingConstants.NORTH:
			p.y = this.getY() + this.getHeight();
			p.x = this.getX();
			break;
		default:
		case SwingConstants.SOUTH:
			p.y = this.getY();
			p.x = this.getX();
			if (tip != null) {
				p.y -= tip.getHeight();
			} else {
				p.y += this.getHeight();
			}

			break;
		case SwingConstants.WEST:
			p.y = this.getY();
			p.x = this.getX() + this.getWidth();
			break;
		case SwingConstants.EAST:
			p.y = this.getY();
			p.x = this.getX();
			if (tip != null) {
				p.x -= tip.getWidth();
			} else {
				p.x += this.getWidth();
			}
			break;
		}

		return p;
	}

}
