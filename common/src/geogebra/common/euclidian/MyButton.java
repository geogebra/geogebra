package geogebra.common.euclidian;

import geogebra.common.awt.GFont;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.font.GTextLayout;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoButton.Observer;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.TextProperties;

//import java.awt.Color;

/**
 * Replaces Swing button in DrawButton
 */
public class MyButton implements Observer{

	private GeoButton geoButton;
	private EuclidianView view;
	private int x, y;
	private boolean selected;
	private String text;
	
	private final static int margin = 5;
	private final static int minSize = 24;
	private final static int arcSize = 9;
	private GFont font;
	private boolean pressed, draggedOrContext;
	private float textHeight;
	private float textWidth;

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
	 */
	public void paintComponent(geogebra.common.awt.GGraphics2D g) {
		
		// Graphics2D g2 = geogebra.awt.Graphics2D.getAwtGraphics(g);

		view.setAntialiasing(g);		
	
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
			t = geogebra.common.factories.AwtFactory.prototype.newTextLayout(
					getCaption(), font, g.getFontRenderContext());
			textHeight = t.getAscent() + t.getDescent();
			textWidth = t.getAdvance();			
		} 
		// With fixed size the font are resized if is too big
		if (geoButton.isFixedSize() && 
				((int)textHeight + imgGap + 2 * margin > geoButton.getHeight() || (int)textWidth + 2 * margin > geoButton.getWidth())){
			resize(g,imgGap);
			return;
		}
		
		int currentWidth = Math.max((int) (textWidth + 2 * margin), minSize);
		currentWidth = Math.max(currentWidth, imgWidth + 2 * margin);
		
		
		int currentHeight = Math.max((int) (textHeight + imgHeight + imgGap + 2 * margin),
				minSize);
		
		// For italic is added an inclination of approximately
		// 15 degrees
		double add=0;
		if(font.isItalic()){
			add=(Math.sin(0.26) * t.getDescent()/2);
			//Adaptation for the combination Serif+ italic made ​​by trial
			if (((TextProperties)geoButton).isSerifFont()){
				add = -add * 4;
			}
		}
		//Additional offset for image if button has fixed size		
		int imgStart=0;
		
		//Initial offset for subimage if button has fixed size
		int startX=0;
		int startY=0;
		
		if (!geoButton.isFixedSize()) {
			geoButton.setWidth(currentWidth);
			geoButton.setHeight(currentHeight);
		} else {
			//With fixed size the image is cut if is too big
			if (imgHeight > geoButton.getHeight() - textHeight - imgGap - 2 * margin) {
				startY=imgHeight - (int) (geoButton.getHeight() - textHeight - imgGap - 2 * margin);
				imgHeight = (int) (geoButton.getHeight() - textHeight - imgGap - 2 * margin);
				if( imgHeight <= 0 ){
					geoButton.setFillImage("");
				} else {					
					startY /= 2;
				}
			}
			if (imgWidth > geoButton.getWidth() - 2 * margin) {
				startX=imgWidth-(geoButton.getWidth() - 2 * margin);
				imgWidth = geoButton.getWidth() - 2 * margin;
				startX/=2;
			}
			imgStart=(int)(geoButton.getHeight() - imgHeight -2 * margin - textHeight - imgGap) / 2;
		}
		
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
		g.fillRoundRect(x, y, geoButton.getWidth(), geoButton.getHeight(), arcSize, arcSize);

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
			g.drawImage(geoButton.getFillImage().getSubimage(startX,startY ,imgWidth,imgHeight), null, x + (geoButton.getWidth() - imgWidth) / 2, y
					+ margin + imgStart);
		}

		// draw the text center-aligned to the button
		if (hasText) {
			int xPos = (int) (x + (geoButton.getWidth() - t.getAdvance()+add )/ 2);
			int yPos = (int) (y + margin + imgHeight + imgGap + t.getAscent() + imgStart);
			g.drawString(geoButton.getCaption(StringTemplate.defaultTemplate),
					xPos, yPos);
		}
	}

	private void resize(geogebra.common.awt.GGraphics2D g, int imgGap) {
		//Reduces the font for attempts
		GTextLayout t = null;
		int i=GeoText.getFontSizeIndex(((TextProperties)geoButton).getFontSizeMultiplier());
		while ((int)textHeight + imgGap + 2 * margin > geoButton.getHeight()){
			i--;
			font=font.deriveFont(font.getStyle(),(int)(GeoText.getRelativeFontSize(i)*12));			
			t = geogebra.common.factories.AwtFactory.prototype.newTextLayout(
					getCaption(), font, g.getFontRenderContext());
			textHeight = t.getAscent() + t.getDescent();
			textWidth = t.getAdvance();
		} 
		
		while ((int) textWidth +2 * margin > geoButton.getWidth()){
			i--;
			font = font.deriveFont(font.getStyle(), (int)(GeoText.getRelativeFontSize(i) * 12));
			t = geogebra.common.factories.AwtFactory.prototype.newTextLayout(
					getCaption(), font, g.getFontRenderContext());
			textHeight = t.getAscent() + t.getDescent();
			textWidth = t.getAdvance();
		}	
		geoButton.setFontSizeMultiplier(GeoText.getRelativeFontSize(i));
		setFont(font);
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
		return geogebra.common.factories.AwtFactory.prototype.newRectangle(x,
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

	/**
	 * @return whether the button has fixed size
	 */
	public boolean isFixedSize() {
		return geoButton.isFixedSize();
	}

	/**
	 * @param fixedSize change the button to have fixed size
	 */
	public void setFixedSize(boolean fixedSize) {
		geoButton.setFixedSize(fixedSize);
	}

	public void notifySizeChanged() {
		geoButton.getKernel().notifyRepaint();
	}
	
}