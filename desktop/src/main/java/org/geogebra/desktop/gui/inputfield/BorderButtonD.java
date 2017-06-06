package org.geogebra.desktop.gui.inputfield;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.border.AbstractBorder;

/**
 * Extended Border class that adds simulated buttons to the right border of a
 * JTextField. See MyTextField and AutoCompleteTextField for usage.
 * 
 * @author G. Sturr
 *
 */
public class BorderButtonD extends AbstractBorder
		implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;

	private Component borderOwner;

	public static final String cmdSuffix = "BorderButtonAction";
	private ImageIcon[] icon;
	private static final int hGap = 7;

	private boolean[] isVisibleIcon;
	private boolean[] isMouseOverIcon;
	private Rectangle[] iconRect;
	private ActionListener[] al;
	private Cursor otherCursor = Cursor.getDefaultCursor();
	private boolean isMouseOverIconRegion = false;

	private int maxIconCount = 4;

	/**************************************
	 * Constructs a BorderButton
	 * 
	 * @param borderOwner
	 */
	public BorderButtonD(Component borderOwner) {

		this.borderOwner = borderOwner;

		// register our mouseListener, making sure it is the first one added
		MouseListener[] ml = borderOwner.getMouseListeners();
		for (int i = 0; i < ml.length; i++) {
			borderOwner.removeMouseListener(ml[i]);
		}
		borderOwner.addMouseListener(this);
		for (int i = 0; i < ml.length; i++) {
			borderOwner.addMouseListener(ml[i]);
		}

		// register mouseMotionListener
		borderOwner.addMouseMotionListener(this);

		icon = new ImageIcon[maxIconCount];
		isVisibleIcon = new boolean[maxIconCount];
		isMouseOverIcon = new boolean[maxIconCount];
		iconRect = new Rectangle[maxIconCount];
		al = new ActionListener[maxIconCount];

		for (int i = 0; i < maxIconCount; i++) {
			icon[i] = new ImageIcon();
			iconRect[i] = new Rectangle();
			isMouseOverIcon[i] = false;
			// need default visibility = false so that focus lost/gained
			// visibility works
			isVisibleIcon[i] = false;
		}
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int w,
			int h) {

		int offset = hGap;
		int xStart = x + w - getTotalInsetWidth();
		int yMid = y + h / 2;

		for (int i = 0; i < icon.length; i++) {
			if (icon[i] != null && isVisibleIcon[i]) {
				g.drawImage(icon[i].getImage(), xStart + offset,
						yMid - icon[i].getIconHeight() / 2, null);

				iconRect[i].x = xStart + offset;
				iconRect[i].y = yMid - icon[i].getIconHeight() / 2;
				iconRect[i].width = icon[i].getIconWidth();
				iconRect[i].height = icon[i].getIconHeight();

				offset += icon[i].getIconWidth() + hGap;
			}
		}
	}

	public void setBorderButton(int index, ImageIcon icon,
			ActionListener listener) {
		if (index < 0 || index > maxIconCount) {
			return;
		}
		this.icon[index] = icon;
		al[index] = listener;
	}

	public void setIconVisible(int index, boolean isVisible) {
		if (index < 0 || index > maxIconCount) {
			return;
		}
		isVisibleIcon[index] = isVisible;
		this.borderOwner.validate();
	}

	public boolean isIconVisible(int index) {
		return isVisibleIcon[index];
	}

	private int getTotalInsetWidth() {
		int insetWidth = 0;
		for (int i = 0; i < icon.length; i++) {
			if (isVisibleIcon[i]) {
				insetWidth += icon[i].getIconWidth() + hGap;
			}
		}
		return insetWidth;
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(0, 0, 0, getTotalInsetWidth());
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	private void swapCursor() {
		Cursor tmp = borderOwner.getCursor();
		borderOwner.setCursor(otherCursor);
		otherCursor = tmp;
	}

	// =============================================
	// Mouse Listeners
	// =============================================

	@Override
	public void mouseDragged(MouseEvent e) {
		// only care about mouse move and press
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		boolean isOver = e.getPoint().x > iconRect[0].x;
		if (isMouseOverIconRegion != isOver) {
			isMouseOverIconRegion = isOver;
			swapCursor();
		}

		for (int i = 0; i < iconRect.length; i++) {
			isOver = iconRect[i].contains(e.getPoint());
			if (isMouseOverIcon[i] != isOver) {
				isMouseOverIcon[i] = isOver;
				borderOwner.repaint();
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// only care about mouse move and press
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// only care about mouse move and press
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// only care about mouse move and press
	}

	@Override
	public void mousePressed(MouseEvent e) {
		for (int i = 0; i < iconRect.length; i++) {
			if (isMouseOverIcon[i]) {
				e.consume();
				ActionEvent ae = new ActionEvent(this,
						ActionEvent.ACTION_PERFORMED, i + cmdSuffix);
				al[i].actionPerformed(ae);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		for (int i = 0; i < iconRect.length; i++) {
			if (isMouseOverIcon[i]) {
				e.consume();
			}
		}
	}

}
