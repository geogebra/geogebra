package org.geogebra.desktop.gui.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.net.URL;

import javax.swing.Icon;

import org.w3c.dom.svg.SVGDocument;

import io.sf.carte.echosvg.anim.dom.SAXSVGDocumentFactory;
import io.sf.carte.echosvg.bridge.BridgeContext;
import io.sf.carte.echosvg.bridge.DocumentLoader;
import io.sf.carte.echosvg.bridge.GVTBuilder;
import io.sf.carte.echosvg.bridge.UserAgent;
import io.sf.carte.echosvg.bridge.UserAgentAdapter;
import io.sf.carte.echosvg.gvt.GraphicsNode;

public final class JSVGIcon implements Icon {
	enum AutoSize {
		NONE,
		HORIZONTAL,
		VERTICAL,
		BEST_FIT,
		STRETCH;

	}


	enum Interpolation {
		NEAREST_NEIGHBOR,
		BILINEAR,
		BICUBIC;
	}

	private Dimension preferredSize = null;
	private boolean clipToViewbox;
	private AffineTransform scaleXform = new AffineTransform();
	private final GraphicsNode svgIcon;

	private boolean antiAlias;
	private AutoSize autoSize;
	private Interpolation interpolation = Interpolation.NEAREST_NEIGHBOR;

	/**
	 * Method to fetch the SVG icon from an url
	 * @param url the url from which to fetch the SVG icon
	 */
	public JSVGIcon(URL url) throws Exception {
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory();
		SVGDocument doc = f.createSVGDocument(url.toString());
		UserAgent userAgent = new UserAgentAdapter();
		DocumentLoader loader = new DocumentLoader(userAgent);
		BridgeContext ctx = new BridgeContext(userAgent, loader);
		ctx.setDynamicState(BridgeContext.DYNAMIC);
		GVTBuilder builder = new GVTBuilder();
		this.svgIcon = builder.build(ctx, doc);
	}

	/**
	 * Method to paint the icon using Graphics2D. Note that the scaling factors have nothing to do with the zoom
	 * operation, the scaling factors set the size your icon relative to the other objects on your canvas.
	 * @param g the graphics context used for drawing
	 * @param x the X coordinate of the top left corner of the icon
	 * @param y the Y coordinate of the top left corner of the icon
	 * @param scaleX the X scaling to be applied to the icon before drawing
	 * @param scaleY the Y scaling to be applied to the icon before drawing
	 */
	private void paintSvgIcon(Graphics2D g, int x, int y, double scaleX, double scaleY) {
		AffineTransform transform =
				new AffineTransform(scaleX, 0.0, 0.0, scaleY, x, y);
		svgIcon.setTransform(transform);
		svgIcon.paint(g);
	}

	@Override
	public int getIconHeight() {
		if (preferredSize != null
				&& (autoSize == AutoSize.VERTICAL || autoSize == AutoSize.STRETCH
				|| autoSize == AutoSize.BEST_FIT)) {
			return preferredSize.height;
		}

		return getSvgHeight();
	}

	private int getSvgHeight() {
		return (int) svgIcon.getPrimitiveBounds().getHeight();
	}

	@Override
	public int getIconWidth() {
		if (preferredSize != null
				&& (autoSize == AutoSize.HORIZONTAL || autoSize == AutoSize.STRETCH
				|| autoSize == AutoSize.BEST_FIT)) {
			return preferredSize.width;
		}

		return getSvgWidth();
	}

	private int getSvgWidth() {
		return (int) svgIcon.getPrimitiveBounds().getWidth();
	}

	@Override
	public void paintIcon(Component comp, Graphics gg, int x, int y) {
		Graphics2D g = (Graphics2D) gg.create();
		paintIcon(comp, g, x, y);
		g.dispose();
	}

	private void paintIcon(Component comp, Graphics2D g, int x, int y) {
		Object oldAliasHint = g
				.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);

		Object oldInterpolationHint = g
				.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
		switch (interpolation) {
		case NEAREST_NEIGHBOR:
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			break;
		case BILINEAR:
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			break;
		case BICUBIC:
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			break;
		}

		g.translate(x, y);
		if (clipToViewbox) {
			g.setClip(new Rectangle2D.Float(0, 0, getSvgWidth(), getSvgHeight()));
		}

		if (autoSize == AutoSize.NONE) {
			try {
//				g.drawImage(svgIcon.);
				g.translate(-x, -y);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						oldAliasHint);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return;
		}

		final int width = getIconWidth();
		final int height = getIconHeight();

		if (width == 0 || height == 0) {
			return;
		}
		double diaWidth = getSvgWidth();
		double diaHeight = getSvgHeight();

		double scaleW = 1;
		double scaleH = 1;
		switch (autoSize) {

		case NONE:
			break;
		case HORIZONTAL:
			scaleW = scaleH = width / diaWidth;
			break;
		case VERTICAL:
			scaleW = scaleH = height / diaHeight;
			break;
		case BEST_FIT:
			scaleW = scaleH = (height / diaHeight < width / diaWidth)
					? height / diaHeight : width / diaWidth;
			break;
		case STRETCH:
			scaleW = width / diaWidth;
			scaleH = height / diaHeight;
			break;
		}

		scaleXform.setToScale(scaleW, scaleH);

		AffineTransform oldXform = g.getTransform();
		g.transform(scaleXform);

//		try {
//	//		diagram.render(g);
//		} catch (SVGException e) {
//			throw new RuntimeException(e);
//		}

		g.setTransform(oldXform);

		g.translate(-x, -y);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAliasHint);
		if (oldInterpolationHint != null) {
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					oldInterpolationHint);
		}
	}


	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
	}

	public void setAutoSize(AutoSize autoSize) {
		this.autoSize = autoSize;
	}

	public void setPreferredSize(Dimension dimension) {
		this.preferredSize = dimension;
	}
}