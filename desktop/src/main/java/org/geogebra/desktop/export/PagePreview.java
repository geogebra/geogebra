package org.geogebra.desktop.export;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import org.geogebra.desktop.euclidian.EuclidianViewD;
import org.geogebra.desktop.main.AppD;

class PagePreview extends JPanel {

	private static final long serialVersionUID = 1L;

	protected int m_w;
	protected int m_h;
	protected Printable target;
	protected PageFormat format;
	protected int pageIndex;
	protected double scale = 1.0;
	protected BufferedImage img;

	private int targetIndex;

	private AppD app;

	public PagePreview(Printable target, PageFormat format, int pageIndex,
			int targetIndex, AppD app) {
		this.target = target;
		this.format = format;
		this.app = app;
		this.pageIndex = pageIndex;
		this.targetIndex = targetIndex;
		m_w = (int) format.getWidth();
		m_h = (int) format.getHeight();

		setBackground(Color.white);
		setBorder(new MatteBorder(1, 1, 2, 2, Color.black));
		// update();
	}

	public int getTarget() {
		return targetIndex;
	}

	public void setPageFormat(PageFormat format) {
		this.format = format;
		m_w = (int) (format.getWidth() * scale);
		m_h = (int) (format.getHeight() * scale);
		update();
	}

	public PageFormat getPageFormat() {
		return format;
	}

	public void setScale(int scale) {
		double newScale = scale / 100.0;
		if (newScale != this.scale) {
			this.scale = newScale;
			m_w = (int) (format.getWidth() * this.scale);
			m_h = (int) (format.getHeight() * this.scale);
			update();
		}
	}

	@Override
	public Dimension getPreferredSize() {
		Insets ins = getInsets();
		return new Dimension(m_w + ins.left + ins.right, m_h + ins.top
				+ ins.bottom);
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	private void updateBufferedImage() {
		img = new BufferedImage(m_w, m_h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();
		g2.setColor(getBackground());
		g2.fillRect(0, 0, m_w, m_h);
		if (scale != 1.0)
			g2.scale(scale, scale);
		try {
			String scaleStr = null;
			if (!(target instanceof EuclidianViewD)) {

				int height = EuclidianViewD.printTitle(g2, scaleStr,
						this.format, this.app);
				g2.setTransform(new AffineTransform());
				if (scale != 1.0) {
					g2.scale(scale, scale);
				}
				if (height > 0) {
					g2.translate(0, height + 20);
				}
				if (target instanceof PrintGridable) {
					((PrintGridable) target).setTitleOffset(height);
				}
			}
			target.print(g2, format, pageIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update() {
		try {
			updateBufferedImage();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(img, 0, 0, this);
		paintBorder(g);
	}
}