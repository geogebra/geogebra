package geogebra.web.euclidian;

import com.google.gwt.canvas.dom.client.TextMetrics;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;
import geogebra.common.awt.Graphics2D;
import geogebra.common.awt.Point;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.Shape;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.web.awt.FontRenderContext;
import geogebra.web.awt.font.TextLayout;
import geogebra.web.main.Application;

public class EuclidianStatic extends geogebra.common.euclidian.EuclidianStatic {

	@Override
	protected Rectangle doDrawMultilineLaTeX(AbstractApplication app,
	        Graphics2D tempGraphics, GeoElement geo, Graphics2D g2, Font font,
	        Color fgColor, Color bgColor, String labelDesc, int xLabel,
	        int yLabel, boolean serif) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	@Override
	protected Point doDrawIndexedString(AbstractApplication app, Graphics2D g3,
	        String str, float xPos, float yPos, boolean serif) {
		Graphics2D g2 =  g3;
		Font g2font = g2.getFont();
		g2font = ((Application) app).getFontCanDisplay(str, serif, g2font.getStyle(),
				g2font.getSize());
		Font indexFont = getIndexFont(g2font);
		Font font = g2font;
		TextLayout layout;
		FontRenderContext frc = (FontRenderContext) g2.getFontRenderContext();

		int indexOffset = indexFont.getSize() / 2;
		float maxY = 0;
		int depth = 0;
		float x = xPos;
		float y = yPos;
		int startPos = 0;
		if (str == null)
			return null;
		int length = str.length();

		for (int i = 0; i < length; i++) {
			switch (str.charAt(i)) {
			case '_':
				// draw everything before _
				if (i > startPos) {
					font = (depth == 0) ? g2font : indexFont;
					y = yPos + depth * indexOffset;
					if (y > maxY)
						maxY = y;
					String tempStr = str.substring(startPos, i);
					layout = new TextLayout(tempStr, font, frc);
					g2.setFont(font);
					g2.drawString(tempStr, x, y);
					x += layout.getAdvance();
				}
				startPos = i + 1;
				depth++;

				// check if next character is a '{' (beginning of index with
				// several chars)
				if (startPos < length && str.charAt(startPos) != '{') {
					font = (depth == 0) ? g2font : indexFont;
					y = yPos + depth * indexOffset;
					if (y > maxY)
						maxY = y;
					String tempStr = str.substring(startPos, startPos + 1);
					layout = new TextLayout(tempStr, font, frc);
					g2.setFont(font);
					g2.drawString(tempStr, x, y);
					x += layout.getAdvance();
					depth--;
				}
				i++;
				startPos++;
				break;

			case '}': // end of index with several characters
				if (depth > 0) {
					if (i > startPos) {
						font = (depth == 0) ? g2font : indexFont;
						y = yPos + depth * indexOffset;
						if (y > maxY)
							maxY = y;
						String tempStr = str.substring(startPos, i);
						layout = new TextLayout(tempStr, font, frc);
						g2.setFont(font);
						g2.drawString(tempStr, x, y);
						x += layout.getAdvance();
					}
					startPos = i + 1;
					depth--;
				}
				break;
			}
		}

		if (startPos < length) {
			font = (depth == 0) ? g2font : indexFont;
			y = yPos + depth * indexOffset;
			if (y > maxY)
				maxY = y;
			String tempStr = str.substring(startPos);
			layout = new TextLayout(tempStr, font, frc);
			g2.setFont(font);
			g2.drawString(tempStr, x, y);
			x += layout.getAdvance();
		}
		g2.setFont(g2font);
		return new geogebra.common.awt.Point(Math.round(x - xPos), Math.round(maxY - yPos));
	}


	private static Font getIndexFont(Font f) {
		// index font size should be at least 8pt
		int newSize = Math.max((int) (f.getSize() * 0.9), 8);
		return f.deriveFont(f.getStyle(), newSize);
	}

	
	@Override
	protected void doFillWithValueStrokePure(Shape shape, Graphics2D g3) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	@Override
	protected Rectangle doDrawMultiLineText(AbstractApplication app,
	        String labelDesc, int xLabel, int yLabel, Graphics2D g2,
	        boolean serif) {
		int lines = 0;
		int fontSize = g2.getFont().getSize();
		float lineSpread = fontSize * 1.5f;

		Font font = g2.getFont();
		//font = ((Application) app).getFontCanDisplay(labelDesc, serif, font.getStyle(),
			//	font.getSize());

		FontRenderContext frc = (FontRenderContext) g2.getFontRenderContext();
		int xoffset = 0;

		// draw text line by line
		int lineBegin = 0;
		int length = labelDesc.length();
		for (int i = 0; i < length - 1; i++) {
			if (labelDesc.charAt(i) == '\n') {
				// end of line reached: draw this line
				g2.drawString(labelDesc.substring(lineBegin, i), xLabel, yLabel
						+ lines * lineSpread);

				int width = (int) textWidth(labelDesc.substring(lineBegin, i),
						font, frc);
				if (width > xoffset)
					xoffset = width;

				lines++;
				lineBegin = i + 1;
			}
		}

		float ypos = yLabel + lines * lineSpread;
		g2.drawString(labelDesc.substring(lineBegin), xLabel, ypos);

		int width = (int) textWidth(labelDesc.substring(lineBegin), font, frc);
		if (width > xoffset)
			xoffset = width;

		// Michael Borcherds 2008-06-10
		// changed setLocation to setBounds (bugfix)
		// and added final float textWidth()
		// labelRectangle.setLocation(xLabel, yLabel - fontSize);
		int height = (int) ((lines + 1) * lineSpread);

		return AwtFactory.prototype.newRectangle(xLabel - 3, yLabel - fontSize - 3, xoffset + 6,
				height + 6);
		// labelRectangle.setBounds(xLabel, yLabel - fontSize, xoffset, height
		// );

	}

	private int textWidth(String substring, Font font, FontRenderContext frc) {
		return frc.measureText(substring, ((geogebra.web.awt.Font) font).getFullFontString());
    }

	@Override
	protected void doDrawWithValueStrokePure(Shape shape, Graphics2D g2) {
		g2.draw(shape);
		// TODO can we emulate somehow the "pure stroke" behavior?

	}

}
