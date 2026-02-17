package com.himamis.retex.renderer.share.platform.graphics.stubs;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GShape;

import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Image;

public class GraphicsStub implements Graphics2DInterface {

	private GBasicStroke stroke;
	private GColor color;
	private Font font;
	private List<GAffineTransform> transformList = new ArrayList<>();
	private GAffineTransform currentTransform;

	public GraphicsStub() {
		transformList = new ArrayList<>();
		reset();
	}

	public void reset() {
		transformList.clear();
		currentTransform = AwtFactory.getPrototype().newAffineTransform();
		font = new FontStub();
		color = GColor.BLACK;
		stroke = AwtFactory.getPrototype().newBasicStroke(1);
	}

	@Override
	public void setStroke(GBasicStroke stroke) {
		this.stroke = stroke;
	}

	@Override
	public GBasicStroke getStroke() {
		return stroke;
	}

	@Override
	public void setColor(GColor color) {
		this.color = color;
	}

	@Override
	public GColor getColor() {
		return color;
	}

	@Override
	public GAffineTransform getTransform() {
		return currentTransform;
	}

	@Override
	public void saveTransform() {
		transformList.add(currentTransform.duplicate());
	}

	@Override
	public void restoreTransform() {
		if (transformList.size() > 0) {
			currentTransform = transformList.remove(transformList.size() - 1);
		}
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public void setFont(Font font) {
		this.font = font;
	}

	@Override
	public void fillRect(double x, double y, double width, double height) {
		// stub
	}

	@Override
	public void fill(GShape rectangle) {
		// stub
	}

	@Override
	public void draw(GShape rectangle) {
		// stub
	}

	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		// stub
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		// stub
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		// stub
	}

	@Override
	public void translate(double x, double y) {
		currentTransform.translate(x, y);
	}

	@Override
	public void scale(double x, double y) {
		currentTransform.scale(x, y);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		currentTransform.rotate(theta, x, y);
	}

	@Override
	public void rotate(double theta) {
		currentTransform.rotate(theta);
	}

	@Override
	public void drawImage(Image image, int x, int y) {
		// stub
	}

	@Override
	public void drawImage(Image image, GAffineTransform transform) {
		// stub
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		// stub
		return null;
	}

	@Override
	public void setRenderingHint(int key, int value) {
		// stub
	}

	@Override
	public int getRenderingHint(int key) {
		// stub
		return 0;
	}

	@Override
	public void dispose() {
		// stub
	}
}
