package geogebra.euclidian;

import geogebra.common.euclidian.EuclidianView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class EuclidianViewJPanel extends JPanel implements geogebra.common.euclidian.EuclidianViewJPanel {

	private static final long serialVersionUID = 1L;
	
	EuclidianView view;

	public EuclidianViewJPanel(EuclidianView view) {
		this.view = view;

		// algebra controller will take care of our key events
		setFocusable(true);

		setLayout(null);
		setMinimumSize(new Dimension(20, 20));

		// register Listener
		((EuclidianControllerListeners) view.getEuclidianController()).addListenersTo(this);


		// enable drop transfers
		setTransferHandler(new EuclidianViewTransferHandler(view));
	}

	protected Color bgColor;

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

	public int temporaryWidth = -1;
	public int temporaryHeight = -1;

	@Override
	public int getWidth() {
		return (temporaryWidth > 0) ? temporaryWidth : super.getWidth();
	}

	@Override
	public int getHeight() {
		return (temporaryHeight > 0) ? temporaryHeight : super.getHeight();
	}

	@Override
	public Rectangle getBounds() {
		return geogebra.awt.GRectangleD.getAWTRectangle(view.getBounds());
	}

	@Override
	public void setToolTipText(String plain) {
		super.setToolTipText(plain);
	}

	private geogebra.awt.GGraphics2DD g2 = new geogebra.awt.GGraphics2DD(null);
	@Override
	final public void paint(Graphics g) {
		g2.setImpl((java.awt.Graphics2D)g);
		view.paint(g2);
	}
	
	public void processMouseEventImpl(MouseEvent e) {
		processMouseEvent(e);
	}
}
