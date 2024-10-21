package org.geogebra.desktop.gui.util;

import static org.geogebra.desktop.gui.util.JSVGConstants.BLANK_SVG;
import static org.geogebra.desktop.gui.util.JSVGConstants.HEADER;
import static org.geogebra.desktop.gui.util.JSVGConstants.UNSUPPORTED_SVG;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Objects;

import org.geogebra.common.awt.GColor;
import org.geogebra.desktop.util.ImageManagerD;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import io.sf.carte.echosvg.anim.dom.SAXSVGDocumentFactory;
import io.sf.carte.echosvg.bridge.BridgeContext;
import io.sf.carte.echosvg.bridge.DocumentLoader;
import io.sf.carte.echosvg.bridge.GVTBuilder;
import io.sf.carte.echosvg.bridge.UserAgent;
import io.sf.carte.echosvg.bridge.UserAgentAdapter;
import io.sf.carte.echosvg.gvt.GraphicsNode;

public class JSVGModel implements SVGModel {
	public static final int MAX_TRIES = 2;
	SVGDocument doc;
	String content;
	private int tries = 0;
	GraphicsNode node;
	private int width;
	private int height;
	private boolean tidy = false;

	public JSVGModel(String content) {
		this.content = content;
	}

	public JSVGModel(SVGDocument doc) {
		this.doc = doc;
	}

	public void nextTry() {
		tries++;
	}

	public boolean isMaxTriesReached() {
		return tries > MAX_TRIES;
	}

	public void setDoc(SVGDocument doc) {
		this.doc = doc;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public void build() {
		UserAgent userAgent = new UserAgentAdapter() {
			@Override
			public SVGDocument getBrokenLinkDocument(Element e, String url, String message) {
				SAXSVGDocumentFactory documentFactory = new SAXSVGDocumentFactory();
				try {
					return documentFactory.createSVGDocument(BLANK_SVG);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		};

		DocumentLoader loader = new SVGDocumentLoaderNoError(userAgent);

		BridgeContext ctx = new BridgeContext(userAgent, loader);
		ctx.setDynamicState(BridgeContext.DYNAMIC);
		GVTBuilder builder = new GVTBuilder();
		node = builder.build(ctx, doc);
		SVGSVGElement root = doc.getRootElement();
		checkLinks(root);
		this.width = (int) root.getWidth().getBaseVal().getValue();
		this.height = (int) root.getHeight().getBaseVal().getValue();
	}

	private void checkLinks(Node root) {
		if ("link".equals(root.getLocalName())
				&& root.getAttributes().getNamedItem("href") != null) {
			throw new IllegalStateException();
		}
		Node child = root.getFirstChild();
		while (child != null) {
			checkLinks(child);
			child = child.getNextSibling();
		}
	}

	public void tidyContent() {
		if (!tidy) {
			content = ImageManagerD.fixSVG(content);
			fixHeader();
		}
		tidy = true;
	}


	void fixHeader() {
		int beginIndex = content.indexOf("<svg");
		if (beginIndex == -1) {
			content = UNSUPPORTED_SVG;
			return;
		}
		String body = content.substring(beginIndex);
		content = HEADER + body;
	}

	@Override
	public void setFill(GColor color) {
		doc.getDocumentElement().setAttribute("fill", color.toString());
		build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof JSVGModel)) return false;
		JSVGModel jsvgModel = (JSVGModel) o;
		return  width == jsvgModel.width && height == jsvgModel.height
				&& Objects.equals(content, jsvgModel.content);
	}

	@Override
	public int hashCode() {
		return Objects.hash(content, width, height);
	}

	@Override
	public void paint(Graphics2D g) {
		if (isInvalid()) {
			return;
		}
		node.setComposite(g.getComposite());
		node.paint(g);
	}

	@Override
	public boolean isInvalid() {
		return node == null;
	}

	@Override
	public void setTransform(AffineTransform transform) {
		node.setTransform(transform);
	}

	@Override
	public String getContent() {
		return content;
	}
}
