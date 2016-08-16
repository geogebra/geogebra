package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
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

//import java.awt.Color;

/**
 * Replaces Swing button in DrawButton
 */
public class MyButton implements Observer {

	private GeoButton geoButton;
	private EuclidianView view;
	private int x, y;
	private boolean selected;
	private String text;

	private final static int margin = 10;
	private final static int minSize = 24;
	private final static int arcSize = 10;
	private GFont font;
	private boolean pressed, draggedOrContext;
	private float textHeight;
	private float textWidth;

	private final static float marginTopMultiplier = 0.6f;
	private final static float marginBottomMultiplier = 0.5f;
	private final static float marginLeftMultiplier = 1f;
	private final static float marginRightMultiplier = 1f;

	/**
	 * @param button
	 *            geo for this button
	 * @param view
	 *            view
	 */
	public MyButton(GeoButton button, EuclidianView view) {
		this.geoButton = button;
		this.view = view;
		this.x = 20;
		this.y = 20;
		geoButton.setObserver(this);
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
	 */
	public void paintComponent(GGraphics2D g,
			double multiplier) {

		boolean latex = CanvasDrawable.isLatexString(getCaption());

		g.setAntialiasing();

		font = font.deriveFont(geoButton.getFontStyle(),
				(int) (multiplier * 12));
		g.setFont(font);

		boolean hasText = getCaption().length() > 0;

		int imgHeight = 0;
		int imgWidth = 0;
		int imgGap = 0;
		textHeight = 0;
		textWidth = 0;

		if (geoButton.getFillImage() != null) {
			imgHeight = geoButton.getFillImage().getHeight();
			imgWidth = geoButton.getFillImage().getWidth();
			if (hasText)
				imgGap = 4;
		}
		GTextLayout t = null;
		// get dimensions
		if (hasText) {
			if (latex) {
				GDimension d = CanvasDrawable.measureLatex(
						view.getApplication(), g, geoButton, font,
						getCaption());
				textHeight = d.getHeight();
				textWidth = d.getWidth();
			} else {
				t = AwtFactory.prototype.newTextLayout(getCaption(), font,
								g.getFontRenderContext());
				textHeight = t.getAscent() + t.getDescent();
				textWidth = t.getAdvance();
			}
		}
		// With fixed size the font are resized if is too big
		if (geoButton.isFixedSize()
				&& ((int) textHeight + imgGap
						+ (marginTopMultiplier + marginBottomMultiplier)
						* margin > geoButton.getHeight() || (int) textWidth
						+ (marginLeftMultiplier + marginRightMultiplier)
						* margin > geoButton.getWidth())) {
			resize(g, imgGap);
			return;
		}

		int currentWidth = Math
				.max((int) (textWidth + (marginLeftMultiplier + marginRightMultiplier)
						* margin), minSize);
		currentWidth = Math
				.max(currentWidth,
						(int) (imgWidth + (marginLeftMultiplier + marginRightMultiplier)
								* margin));

		int currentHeight = Math
				.max((int) (textHeight + imgHeight + imgGap + (marginTopMultiplier + marginBottomMultiplier)
						* margin), minSize);

		// Additional offset for image if button has fixed size
		int imgStart = 0;

		// Initial offset for subimage if button has fixed size
		int startX = 0;
		int startY = 0;
		double add = 0;
		if (!geoButton.isFixedSize()) {
			// Some combinations of style, serif / sans and letters
			// overflow from the drawing if the text is extra large
			if (geoButton.getFontStyle() >= 2) {
				add = Math.sin(0.50) * t.getDescent();
				currentWidth += (int) add;
			}
			if (geoButton.isSerifFont()) {
				currentWidth += currentWidth / 10;
			}
			if (geoButton.isSerifFont() && geoButton.getFontStyle() >= 2) {
				add = -add;
				currentWidth += currentWidth / 4;
			}
			geoButton.setWidth(currentWidth);
			geoButton.setHeight(currentHeight);
		} else {
			// With fixed size the image is cut if is too big
			if (imgHeight > geoButton.getHeight() - textHeight - imgGap
					- (marginTopMultiplier + marginBottomMultiplier) * margin) {
				startY = imgHeight
						- (int) (geoButton.getHeight() - textHeight - imgGap - (marginTopMultiplier + marginBottomMultiplier)
								* margin);
				imgHeight = (int) (geoButton.getHeight() - textHeight - imgGap - (marginTopMultiplier + marginBottomMultiplier)
						* margin);
				if (imgHeight <= 0) {
					geoButton.setFillImage("");
				} else {
					startY /= 2;
				}
			}
			if (imgWidth > geoButton.getWidth()
					- (marginLeftMultiplier + marginRightMultiplier) * margin) {
				startX = (int) (imgWidth - (geoButton.getWidth() - (marginLeftMultiplier + marginRightMultiplier)
						* margin));
				imgWidth = (int) (geoButton.getWidth() - (marginLeftMultiplier + marginRightMultiplier)
						* margin);
				startX /= 2;
			}
			imgStart = (int) (geoButton.getHeight() - imgHeight
					- (marginTopMultiplier + marginBottomMultiplier) * margin
					- textHeight - imgGap) / 2;
		}


		// prepare colors and paint
		g.setColor(view.getBackgroundCommon());
		GColor bg = geoButton.getBackgroundColor();
		// background not set by user
		if (bg == null) {
			bg = GColor.WHITE;
		}

		// change background color on mouse click
		if (pressed) {
			if (bg.compareTo(GColor.WHITE) == 0) {
				g.setPaint(GColor.LIGHTEST_GRAY);
			} else {
				g.setPaint(bg.darker());
			}
		} else {
			g.setPaint(bg);
		}
		// fill background
		g.fillRoundRect(x, y, geoButton.getWidth() + (int) add - 1,
				geoButton.getHeight() - 1, arcSize, arcSize);

		// change border on mouseover
		if (isSelected()) {
			// default button design
			if (bg.compareTo(GColor.WHITE) == 0) {
				// color for inner border
				g.setColor(GColor.GRAY);
				// inner border
				g.drawRoundRect(x + 1, y + 1, getWidth() + (int) add - 3,
						getHeight() - 3, arcSize - 3, arcSize - 3);
				// color for outer border
				g.setColor(GColor.BLACK);

				// user adjusted design
			} else {
				// color for inner border
				g.setColor(bg.darker());
				// inner border
				g.drawRoundRect(x + 1, y + 1, getWidth() + (int) add - 3,
						getHeight() - 3, arcSize - 3, arcSize - 3);
				// color for outer border
				g.setColor(bg.darker().darker());
			}

			// border color
		} else {
			// default button design
			if (bg.compareTo(GColor.WHITE) == 0) {
				g.setColor(GColor.BLACK);

				// user adjusted design
			} else {
				g.setColor(bg.darker());
			}
		}


		// draw border
		g.setStroke(EuclidianStatic.getDefaultStroke());
		g.drawRoundRect(x, y, getWidth() + (int) add - 1, getHeight() - 1,
				arcSize, arcSize);

		// prepare to draw text
		g.setColor(geoButton.getObjectColor());
		this.setForeground(GColor.WHITE);

		// draw image
		if (geoButton.getFillImage() != null) {

			geoButton.getFillImage().drawSubimage(startX, startY, imgWidth,
					imgHeight, g,
					x + (geoButton.getWidth() - imgWidth) / 2, (int) (y
							+ marginTopMultiplier * margin + imgStart));
		}

		// draw the text center-aligned to the button
		if (hasText) {
			int xPos = latex
					? (int) (x + (geoButton.getWidth() - textWidth) / 2)
					: (int) (x + (geoButton.getWidth() - t.getAdvance() + add)
							/ 2);
			// int yPos = (int) (y + marginTopMultiplier * margin + imgHeight +
			// imgGap + t.getAscent() + imgStart);

			if (geoButton.getFillImage() == null) {
				imgStart = (int) (geoButton.getHeight()
						- (marginTopMultiplier + marginBottomMultiplier)
						* margin - textHeight) / 2;
			}

			int yPos = latex
					? (int) (y + (geoButton.getHeight() - textHeight) / 2)
							+ imgHeight + imgGap + imgStart
					: (int) (y + marginTopMultiplier * margin + imgHeight
							+ imgGap + t.getAscent() + imgStart);

			if (geoButton.getFillImage() != null) {
				yPos = latex ? y + imgHeight + imgGap + imgStart
						: (int) (y + marginTopMultiplier * margin + imgHeight
								+ imgGap + t.getAscent() + imgStart);
			}

			if (latex) {
				App app = view.getApplication();
				g.setPaint(GColor.BLACK);

				app.getDrawEquation()
						.drawEquation(app, geoButton, g, xPos, yPos,
								geoButton.getCaption(
										StringTemplate.defaultTemplate),
								font, false, geoButton.getObjectColor(),
								geoButton.getBackgroundColor(), false, false,
								null);
			} else {
				g.drawString(
						geoButton.getCaption(StringTemplate.defaultTemplate),
						xPos, yPos);

			}
		}
	}

	private void resize(GGraphics2D g, int imgGap) {
		boolean latex = CanvasDrawable.isLatexString(getCaption());

		// Reduces the font for attempts
		GTextLayout t = null;
		int i = GeoText.getFontSizeIndex(((TextProperties) geoButton)
				.getFontSizeMultiplier());
		while (i > 0
				&& (int) textHeight + imgGap
				+ (marginTopMultiplier + marginBottomMultiplier)
						* margin > geoButton.getHeight()) {
			i--;
			font = font.deriveFont(font.getStyle(),
					(int) (GeoText.getRelativeFontSize(i) * 12));
			if (latex) {
				GDimension d = CanvasDrawable.measureLatex(
						view.getApplication(), g, geoButton, font,
						getCaption());
				textHeight = d.getHeight();
				textWidth = d.getWidth();

			} else {
				t = AwtFactory.prototype.newTextLayout(getCaption(), font,
								g.getFontRenderContext());
				textHeight = t.getAscent() + t.getDescent();
				textWidth = t.getAdvance();
			}
		}

		while (i > 0
				&& (int) textWidth
				+ (marginLeftMultiplier + marginRightMultiplier)
						* margin > geoButton.getWidth()) {
			i--;
			font = font.deriveFont(font.getStyle(),
					(int) (GeoText.getRelativeFontSize(i) * 12));
			if (latex) {
				GDimension d = CanvasDrawable.measureLatex(
						view.getApplication(), g, geoButton, font,
						getCaption());
				textHeight = d.getHeight();
				textWidth = d.getWidth();

			} else {
				t = AwtFactory.prototype.newTextLayout(getCaption(), font,
								g.getFontRenderContext());
				textHeight = t.getAscent() + t.getDescent();
				textWidth = t.getAdvance();
			}
		}
		double ret = GeoText.getRelativeFontSize(i);
		paintComponent(g, ret);

	}

	/**
	 * @param white
	 *            color
	 */
	private void setForeground(GColor white) {
		// TODO Auto-generated method stub

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
		geoButton.setWidth((int) labelRectangle.getWidth());
		geoButton.setHeight((int) labelRectangle.getHeight());

	}

	/**
	 * @return bounds of this button
	 */
	public GRectangle getBounds() {
		return AwtFactory.prototype.newRectangle(x,
				y, geoButton.getWidth(), geoButton.getHeight());
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
		// releasing
		else if (!draggedOrContext) {

			// make sure that Input Boxes lose focus (and so update) before
			// running scripts
			// geoButton.getKernel().getApplication().getActiveEuclidianView().requestFocusInWindow();

			// now called from EuclidianController
			// geoButton.getKernel().getApplication().runScripts(geoButton,
			// null);
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