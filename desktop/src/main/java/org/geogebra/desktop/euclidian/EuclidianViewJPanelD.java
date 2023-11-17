package org.geogebra.desktop.euclidian;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewJPanel;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.awt.GRectangleD;

public class EuclidianViewJPanelD extends JPanel
		implements EuclidianViewJPanel {

	private static final long serialVersionUID = 1L;

	EuclidianView view;
	protected Color bgColor;
	private final GGraphics2DD g2 = new GGraphics2DD(null);

	/**
	 * @param view view
	 * @param addListeners whether to add mouse listeners
	 */
	public EuclidianViewJPanelD(EuclidianView view, boolean addListeners) {
		this.view = view;

		// algebra controller will take care of our key events
		setFocusable(true);

		setLayout(null);
		setMinimumSize(new Dimension(20, 20));

		// register Listener
		if (addListeners) {
			((EuclidianControllerListeners) view.getEuclidianController())
					.addListenersTo(this);
		}

		// enable drop transfers
		setTransferHandler(new EuclidianViewTransferHandler(view));
	}

	public EuclidianViewJPanelD(EuclidianView view) {
		this(view, true);
	}

	@Override
	public Color getBackground() {
		return bgColor;
	}

	@Override
	public void setBackground(Color bgColor) {
		if (bgColor != null) {
			this.bgColor = bgColor;
		}
	}

	@Override
	public void paintChildren(Graphics g) {
		super.paintChildren(g);
	}

	@Override
	public Rectangle getBounds() {
		return GRectangleD.getAWTRectangle(view.getBounds());
	}

	@Override
	public void setToolTipText(String plainText) {
		super.setToolTipText(plainText);
	}

	@Override
	final public void paint(Graphics g) {
		g2.setImpl((Graphics2D) g);
		view.paint(g2);
	}

	public void processMouseEventImpl(MouseEvent e) {
		processMouseEvent(e);
	}
}
