package geogebra.common.euclidian;

import geogebra.common.awt.GFont;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.font.GTextLayout;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoButton;

//import java.awt.Color;

/**
 * Replaces Swing button in DrawButton
 */
public class MyButton {

	private GeoButton geoButton;
	private EuclidianView view;
	private int x, y, width, height;
	private boolean selected;
	private String text;

	private final static int margin = 5;
	private final static int minSize = 24;
	private final static int arcSize = 9;

	private GFont font;

	private boolean pressed, draggedOrContext;

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
		this.width = 40;
		this.height = 30;
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
	 */
	protected void paintComponent(geogebra.common.awt.GGraphics2D g) {

		// Graphics2D g2 = geogebra.awt.Graphics2D.getAwtGraphics(g);

		view.setAntialiasing(g);
		g.setFont(font);

		boolean hasText = getCaption().length() > 0;

		int imgHeight = 0;
		int imgWidth = 0;
		int imgGap = 0;
		float textHeight = 0;
		float textWidth = 0;
		if (geoButton.getFillImage() != null) {
			imgHeight = geoButton.getFillImage().getHeight();
			imgWidth = geoButton.getFillImage().getWidth();
			if (hasText)
				imgGap = 4;
		}
		GTextLayout t = null;

		// get dimensions
		if (hasText) {
			t = geogebra.common.factories.AwtFactory.prototype.newTextLayout(
					getCaption(), font, g.getFontRenderContext());
			textHeight = t.getAscent() + t.getDescent();
			textWidth = t.getAdvance();
		}
		width = Math.max((int) (textWidth + 2 * margin), minSize);
		width = Math.max(width, imgWidth + 2 * margin);
		height = Math.max((int) (textHeight + imgHeight + imgGap + 2 * margin),
				minSize);

		// prepare colors and paint
		g.setColor(view.getBackgroundCommon());
		geogebra.common.awt.GPaint p;
		geogebra.common.awt.GColor bg = geoButton.getBackgroundColor(), bg2;
		if (bg == null)
			bg = geogebra.common.awt.GColor.lightGray;
		if (isSelected()) {
			bg2 = bg;
			bg = bg.darker();
		} else {
			bg2 = bg.brighter();
		}
		if (!pressed) {
			p = geogebra.common.factories.AwtFactory.prototype
					.newGradientPaint(x, y, bg2, x, y + getHeight(), bg);
		} else {
			p = geogebra.common.factories.AwtFactory.prototype
					.newGradientPaint(x, y, bg, x, y + getHeight(), bg2);
		}
		geogebra.common.awt.GPaint oldPaint = g.getPaint();

		// =======================================
		// Drawing
		// =======================================

		// background color

		g.setPaint(p);
		g.fillRoundRect(x, y, getWidth(), getHeight(), arcSize, arcSize);

		// draw border
		g.setPaint(oldPaint);
		g.setColor(geogebra.common.awt.GColor.DARK_GRAY);
		g.setStroke(EuclidianStatic.getDefaultStroke());
		g.drawRoundRect(x, y, getWidth() - 1, getHeight() - 1, arcSize, arcSize);

		// prepare to draw text
		g.setColor(geoButton.getObjectColor());
		this.setForeground(geogebra.common.awt.GColor.white);

		// draw image
		if (geoButton.getFillImage() != null) {
			g.drawImage(geoButton.getFillImage(), null, x + (width - imgWidth) / 2, y
					+ margin);
		}

		// draw the text center-aligned to the button
		if (hasText) {
			int xPos = (int) (x + (width - t.getAdvance()) / 2);
			int yPos = (int) (y + margin + imgHeight + imgGap + t.getAscent());
			g.drawString(geoButton.getCaption(StringTemplate.defaultTemplate),
					xPos, yPos);
		}

	}

	/**
	 * @param white
	 *            color
	 */
	private void setForeground(geogebra.common.awt.GColor white) {
		// TODO Auto-generated method stub

	}

	private boolean isSelected() {
		return selected;
	}

	/**
	 * @return width in pixels
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return height in pixels
	 */
	public int getHeight() {
		return height;
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
		width = (int) labelRectangle.getWidth();
		height = (int) labelRectangle.getHeight();

	}

	/**
	 * @return bounds of this button
	 */
	public GRectangle getBounds() {
		return geogebra.common.factories.AwtFactory.prototype.newRectangle(x,
				y, width, height);
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
			
			// make sure that Input Boxes lose focus (and so update) before running scripts
			geoButton.getKernel().getApplication().getActiveEuclidianView().requestFocusInWindow();

			geoButton.getKernel().getApplication().runScripts(geoButton, null);
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
}
