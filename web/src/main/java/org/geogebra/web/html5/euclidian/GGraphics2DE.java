package org.geogebra.web.html5.euclidian;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GComposite;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.web.html5.awt.GFontW;

public class GGraphics2DE implements GGraphics2D {

	public void draw(GShape s) {
		// TODO Auto-generated method stub

	}

	public void drawImage(GBufferedImage img, int x, int y) {
		// TODO Auto-generated method stub

	}

	public void drawImage(MyImage img, int x, int y) {
		// TODO Auto-generated method stub

	}

	public void drawString(String str, int x, int y) {
		// TODO Auto-generated method stub

	}

	public void drawString(String str, double x, double y) {
		// TODO Auto-generated method stub

	}

	public void fill(GShape s) {
		// TODO Auto-generated method stub

	}

	public void setComposite(GComposite comp) {
		// TODO Auto-generated method stub

	}

	public void setPaint(GPaint paint) {
		// TODO Auto-generated method stub

	}

	public void setStroke(GBasicStroke s) {
		// TODO Auto-generated method stub

	}

	public void setRenderingHint(int hintKey, int hintValue) {
		// TODO Auto-generated method stub

	}

	public void translate(double tx, double ty) {
		// TODO Auto-generated method stub

	}

	public void scale(double sx, double sy) {
		// TODO Auto-generated method stub

	}

	public void transform(GAffineTransform Tx) {
		// TODO Auto-generated method stub

	}

	public GComposite getComposite() {
		// TODO Auto-generated method stub
		return null;
	}

	public GColor getBackground() {
		// TODO Auto-generated method stub
		return null;
	}

	public GBasicStroke getStroke() {
		// TODO Auto-generated method stub
		return null;
	}

	public GFontRenderContext getFontRenderContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public GColor getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	public GFont getFont() {
		return new GFontW("serif");
	}

	public void setFont(GFont font) {
		// TODO Auto-generated method stub

	}

	public void setColor(GColor selColor) {
		// TODO Auto-generated method stub

	}

	public void fillRect(int x, int y, int w, int h) {
		// TODO Auto-generated method stub

	}

	public void clearRect(int x, int y, int w, int h) {
		// TODO Auto-generated method stub

	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		// TODO Auto-generated method stub

	}

	public void setClip(GShape shape) {
		// TODO Auto-generated method stub

	}

	public void setClip(GShape shape, boolean saveRestoreContext) {
		// TODO Auto-generated method stub

	}

	public void resetClip() {
		// TODO Auto-generated method stub

	}

	public void drawRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	public void setClip(int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	public void setClip(int x, int y, int width, int height,
			boolean restoreSaveContext) {
		// TODO Auto-generated method stub
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth,
			int arcHeight) {
		// TODO Auto-generated method stub

	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth,
			int arcHeight) {
		// TODO Auto-generated method stub

	}

	public void setAntialiasing() {
		// TODO Auto-generated method stub

	}

	public void setTransparent() {
		// TODO Auto-generated method stub

	}

	public Object setInterpolationHint(
			boolean needsInterpolationRenderingHint) {
		// TODO Auto-generated method stub
		return null;
	}

	public void resetInterpolationHint(Object oldInterpolationHint) {
		// TODO Auto-generated method stub

	}

	public void updateCanvasColor() {
		// TODO Auto-generated method stub

	}

	public void drawStraightLine(double xCrossPix, double d, double xCrossPix2,
			double i) {
		// TODO Auto-generated method stub

	}

	public void saveTransform() {
		// TODO Auto-generated method stub

	}

	public void restoreTransform() {
		// TODO Auto-generated method stub

	}

	public void startGeneralPath() {
		// TODO Auto-generated method stub

	}

	public void addStraightLineToGeneralPath(double x1, double y1, double x2,
			double y2) {
		// TODO Auto-generated method stub

	}

	public void endAndDrawGeneralPath() {
		// TODO Auto-generated method stub

	}

	public void drawImage(MyImage img, int sx, int sy, int sw, int sh, int dx,
			int dy) {
		// TODO Auto-generated method stub

	}

}
