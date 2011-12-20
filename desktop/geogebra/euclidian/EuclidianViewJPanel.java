package geogebra.euclidian;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;

import org.w3c.dom.events.MouseEvent;

public class EuclidianViewJPanel extends JPanel implements geogebra.common.euclidian.EuclidianViewJPanel {

	EuclidianView view;

	public EuclidianViewJPanel(EuclidianView view) {
		this.view = view;
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
		return view.getBounds();
	}

	@Override
	public void setToolTipText(String plain) {
		view.setToolTipText(plain);
	}

	@Override
	final public void paint(Graphics g) {
		view.paint(g);
	}
	
	public void processMouseEventImpl(java.awt.event.MouseEvent e) {
		processMouseEvent(e);
	}
}
