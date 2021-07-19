package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.draw.CanvasDrawable;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoButton.Observer;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.StyleSettings;
import org.geogebra.common.util.StringUtil;

/**
 * Replaces Swing button in DrawButton
 */
public class MyButton implements Observer {

	private GeoButton geoButton;
	private EuclidianView view;
	private StyleSettings styleSettings;

	private int x;
	private int y;
	private boolean selected;
	private String text;

	private final static int minSize = 24;

	private GFont font;
	private boolean pressed;
	private boolean draggedOrContext;
	private double textHeight;
	private double textWidth;
	private boolean firstCall = true;
	private final ButtonHighlightArea halo;

	private final static int MARGIN_TOP = 6;
	private final static int MARGIN_BOTTOM = 5;
	private final static int MARGIN_LEFT = 10;
	private final static int MARGIN_RIGHT = 10;

	/**
	 * @param button
	 *            geo for this button
	 * @param view
	 *            view
	 */
	public MyButton(GeoButton button, EuclidianView view) {
		this.geoButton = button;
		this.view = view;
		this.styleSettings = view.getApplication().getSettings().getStyle();

		this.x = 20;
		this.y = 20;
		geoButton.setObserver(this);
		halo = new ButtonHighlightArea(this);
	}

	private String getCaption() {
		if (geoButton.getFillImage() == null) {
			return geoButton.getCaption(StringTemplate.defaultTemplate);
		}
		return geoButton.getCaptionDescription(StringTemplate.defaultTemplate);
	}

	/**
	 * Paint this on given graphics
	 * 
	 * @param g
	 *            graphics
	 * @param multiplier
	 *            font size multiplier
	 * @param mayResize
	 *            whether we can resize fonts
	 */
	public void paintComponent(GGraphics2D g, double multiplier,
			boolean mayResize) {

		boolean latex = CanvasDrawable.isLatexString(getCaption());

		g.setAntialiasing();

		font = font.deriveFont(geoButton.getFontStyle(),
				(int) (multiplier * 12));
		g.setFont(font);

		boolean hasText = geoButton.isLabelVisible() && getCaption().length() > 0;

		int imgHeight = 0;
		int imgWidth = 0;
		int imgGap = 0;
		textHeight = 0;
		textWidth = 0;

		if (geoButton.getFillImage() != null) {
			imgHeight = geoButton.getFillImage().getHeight();
			imgWidth = geoButton.getFillImage().getWidth();
			if (hasText) {
				imgGap = 4;
			}
		}
		GTextLayout t = null;
		// get dimensions
		if (hasText) {
			if (latex) {
				GDimension d = CanvasDrawable.measureLatex(
						view.getApplication(), geoButton, font, getCaption(),
						getSerif());
				textHeight = d.getHeight();
				textWidth = d.getWidth();
			} else {
				t = AwtFactory.getPrototype().newTextLayout(getCaption(), font,
						g.getFontRenderContext());
				textHeight = t.getAscent() + t.getDescent();
				textWidth = t.getAdvance();
			}
		}
		// With fixed size the font are resized if is too big
		if (mayResize && (geoButton.isFixedSize() && ((int) textHeight + imgGap
				+ (MARGIN_TOP + MARGIN_BOTTOM) > getHeight()
				|| (int) textWidth
						+ (MARGIN_LEFT + MARGIN_RIGHT) > getWidth()))) {
			resize(g, imgGap);
			return;
		}

		int currentWidth = Math.max((int) (textWidth
				+ (MARGIN_LEFT + MARGIN_RIGHT)),
				minSize);
		currentWidth = Math.max(currentWidth,
				imgWidth + (MARGIN_LEFT + MARGIN_RIGHT));

		int currentHeight = Math.max((int) (textHeight + imgHeight + imgGap
				+ (MARGIN_TOP + MARGIN_BOTTOM)),
				minSize);

		// Initial offset for subimage if button has fixed size
		int startX = 0;
		int startY = 0;
		double widthCorrection = 0;
		if (!geoButton.isFixedSize()) {
			// Some combinations of style, serif / sans and letters
			// overflow from the drawing if the text is extra large
			if (geoButton.getFontStyle() >= 2) {
				widthCorrection = Math.sin(0.50) * t.getDescent();
				currentWidth += (int) widthCorrection;
			}
			if (geoButton.isSerifFont()) {
				currentWidth += currentWidth / 10;
			}
			if (geoButton.isSerifFont() && geoButton.getFontStyle() >= 2) {
				widthCorrection = -widthCorrection;
				currentWidth += currentWidth / 4;
			}
			geoButton.setWidth(currentWidth);
			geoButton.setHeight(currentHeight);
		} else {
			// With fixed size the image is cut if is too big
			if (imgHeight > getHeight() - textHeight - imgGap) {
				startY = (int) (imgHeight - (getHeight() - textHeight - imgGap)) / 2;
				imgHeight = (int) (getHeight() - textHeight - imgGap);
			}
			if (imgWidth > getWidth()) {
				startX = (imgWidth - getWidth()) / 2;
				imgWidth = getWidth();
			}
		}

		// Starting position of the image
		int imgStart = (int) (getHeight() - imgHeight - textHeight - imgGap) / 2;

		// prepare colors and paint
		g.setColor(view.getBackgroundCommon());
		GColor bg = geoButton.getBackgroundColor();
		// background not set by user
		if (bg == null) {
			bg = GColor.WHITE;
		}

		GColor paint;

		// change background color on mouse click
		if (pressed && !geoButton.getKernel().getApplication().isExporting()) {
			if (bg.equals(GColor.WHITE)) {
				paint = GColor.LIGHTEST_GRAY;
			} else {
				paint = bg.darker();
			}
		} else {
			paint = bg;
		}

		int arcSize = (int) Math.round(Math.min(getWidth(), getHeight())
				* styleSettings.getButtonRouding());

		int shadowSize = 0;

		// fill background

		if (styleSettings.getButtonShadows()) {
			shadowSize = (int) (getHeight() * 0.1);
			g.setPaint(paint.slightlyDarker());
			g.fillRoundRect(x, y, getWidth() + (int) widthCorrection - 1,
					getHeight() - 1, arcSize, arcSize);
		}

		if (isSelected()) {
			halo.draw(g, widthCorrection, arcSize);
		}

		g.setPaint(paint);
		g.setStroke(EuclidianStatic.getDefaultStroke());
		g.fillRoundRect(x, y, getWidth() + (int) widthCorrection - 1,
				getHeight() - 1 - shadowSize, arcSize, arcSize);

		if (styleSettings.getButtonBorderColor() != null) {
			g.setColor(styleSettings.getButtonBorderColor());
		} else {
			if (bg.equals(GColor.WHITE)) {
				g.setColor(GColor.BLACK);
			} else {
				g.setColor(isSelected() ? bg.darker().darker() : bg.darker());
			}
		}

		// draw border
		g.drawRoundRect(x, y, getWidth() + (int) widthCorrection - 1,
				getHeight() - 1 - shadowSize, arcSize, arcSize);

		// prepare to draw text
		g.setColor(geoButton.getObjectColor());

		MyImage im = geoButton.getFillImage();
		// draw image
		if (im != null) {
			GGeneralPath path = getClipRectangle(arcSize / 2., widthCorrection);
			g.setClip(path);

			if (im.isSVG()) {
				imgHeight = (int) (getHeight() - textHeight - imgGap);
				imgStart = 0;
				drawSVG(im, g, getWidth(), imgHeight);
			} else {
				g.drawImage(im, startX, startY, imgWidth, imgHeight,
						x + (getWidth() - imgWidth) / 2,
						y + imgStart, imgWidth, imgHeight);

			}
			g.resetClip();
		}

		// draw the text center-aligned to the button
		if (hasText) {
			drawText(g, t, imgStart + imgGap + imgHeight, latex, widthCorrection,
					shadowSize);
		}
	}

	private void drawSVG(MyImage im, GGraphics2D g, double width, double height) {
		// SVG is scaled to the button size rather than cropped
		double sx = im.getWidth() / width;
		double sy = im.getHeight() / height;

		g.saveTransform();

		g.scale(1 / sx, 1 / sy);
		g.translate(x * sx, y * sy);

		g.drawImage(im, 0, 0);

		g.restoreTransform();
	}

	private GGeneralPath getClipRectangle(double r, double widthCorrection) {
		GGeneralPath path = AwtFactory.getPrototype().newGeneralPath();
		double K = 4.0 / 3 * (Math.sqrt(2) - 1);

		double right = x + getWidth() + (int) widthCorrection - 1;
		double bottom = y + getHeight();

		path.moveTo(x + r, y);
		path.lineTo(right - r, y);
		path.curveTo(right + r * (K - 1), y, right, y + r * (1 - K),
				right, y + r);
		path.lineTo(right, bottom - r);
		path.curveTo(right, bottom + r * (K - 1), right + r * (K - 1),
				bottom, right - r, bottom);
		path.lineTo(x + r, bottom);
		path.curveTo(x + r * (1 - K), bottom, x, bottom + r * (K - 1),
				x, bottom - r);
		path.lineTo(x, y + r);
		path.curveTo(x, y + r * (1 - K), x + r * (1 - K), y, x + r, y);

		path.closePath();
		return path;
	}

	private void drawText(GGraphics2D g, GTextLayout t, int imgEnd,
			boolean latex, double add, int shadowSize) {
		int xPos = latex ? (int) (x + (getWidth() - textWidth) / 2)
				: (int) (x + (getWidth() - t.getAdvance() + add) / 2);

		int yPos;
		if (geoButton.getFillImage() == null) {
			yPos = latex
					? (int) (y + (getHeight() - textHeight) / 2)
					: (int) (y + (getHeight() + t.getAscent()) / 2);

			yPos -= shadowSize / 2;
		} else {
			yPos = latex ? y + imgEnd : (int) (y + t.getAscent() + imgEnd);
		}

		if (latex) {
			App app = view.getApplication();
			g.setPaint(GColor.BLACK);

			String caption = getCaption();

			app.getDrawEquation().drawEquation(app, geoButton, g, xPos, yPos,
					caption, font, getSerif(), geoButton.getObjectColor(),
					geoButton.getBackgroundColor(), false, false,
					view.getCallBack(geoButton, firstCall));
			firstCall = false;
		} else {
			g.drawString(geoButton.getCaption(StringTemplate.defaultTemplate),
					xPos, yPos);
		}
	}

	private void resize(GGraphics2D g, int imgGap) {
		boolean latex = CanvasDrawable.isLatexString(getCaption());

		// Reduces the font for attempts
		int i = GeoText.getFontSizeIndex(
				((TextProperties) geoButton).getFontSizeMultiplier());
		while (i > 0
				&& (textHeight + imgGap + (MARGIN_TOP + MARGIN_BOTTOM) > getHeight()
				|| textWidth + (MARGIN_LEFT + MARGIN_RIGHT) > getWidth())) {
			i--;
			font = font.deriveFont(font.getStyle(),
					(int) (GeoText.getRelativeFontSize(i) * 12));
			if (latex) {
				GDimension d = CanvasDrawable.measureLatex(
						view.getApplication(), geoButton, font, getCaption(),
						getSerif());
				textHeight = d.getHeight();
				textWidth = d.getWidth();
			} else {
				GTextLayout t = AwtFactory.getPrototype().newTextLayout(getCaption(), font,
						g.getFontRenderContext());
				textHeight = t.getAscent() + t.getDescent();
				textWidth = t.getAdvance();
			}
		}

		double ret = GeoText.getRelativeFontSize(i);
		paintComponent(g, ret, false);
	}

	private boolean getSerif() {
		boolean serif = geoButton.isSerifFont();

		if (!serif) {
			serif = StringUtil.startsWithFormattingCommand(getCaption());
		}

		return serif;
	}

	private boolean isSelected() {
		return selected;
	}

	/**
	 * @return width in pixels
	 */
	public int getWidth() {
		return geoButton.getWidth();
	}

	/**
	 * @return height in pixels
	 */
	public int getHeight() {
		return geoButton.getHeight();
	}

	/**
	 * Resizes and moves the button
	 * 
	 * @param labelRectangle
	 *            new bounds
	 */
	public void setBounds(GRectangle labelRectangle) {
		x = (int) labelRectangle.getMinX();
		y = (int) labelRectangle.getMinY();
		geoButton.setWidth(labelRectangle.getWidth());
		geoButton.setHeight(labelRectangle.getHeight());
	}

	/**
	 * @return bounds of this button
	 */
	public GRectangle getBounds() {
		return AwtFactory.getPrototype().newRectangle(x, y, getWidth(),
				getHeight());
	}

	/**
	 * @param selected
	 *            new selected flag
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return x-coord
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return y-coord
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param labelDesc
	 *            text for this button
	 */
	public void setText(String labelDesc) {
		text = labelDesc;
	}

	/**
	 * @return text of this button
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param font
	 *            new font
	 */
	public void setFont(GFont font) {
		this.font = font;
		geoButton.getKernel().notifyRepaint();
	}

	/**
	 * @param b
	 *            new pressed flag
	 */
	public void setPressed(boolean b) {
		if (b) {
			draggedOrContext = false;
		}

		pressed = b;
	}

	/**
	 * @param b
	 *            new "dragged or context menu" flag
	 */
	public void setDraggedOrContext(boolean b) {
		draggedOrContext = b;
	}

	/**
	 * @return "dragged or context menu" flag
	 */
	public boolean getDraggedOrContext() {
		return draggedOrContext;
	}

	/**
	 * @return whether the button has fixed size
	 */
	public boolean isFixedSize() {
		return geoButton.isFixedSize();
	}

	/**
	 * @param fixedSize
	 *            change the button to have fixed size
	 */
	public void setFixedSize(boolean fixedSize) {
		geoButton.setFixedSize(fixedSize);
	}

	@Override
	public void notifySizeChanged() {
		geoButton.getKernel().notifyRepaint();
	}

	/**
	 * @return associated GeoButton
	 */
	public GeoElement getButton() {
		return geoButton;
	}

}