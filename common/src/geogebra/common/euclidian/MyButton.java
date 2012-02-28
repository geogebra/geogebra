package geogebra.common.euclidian;

import geogebra.common.awt.Font;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.font.TextLayout;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoButton;


//import java.awt.Color;

/**
 * Replaces Swing button in DrawButton
 */
public class MyButton {

	private GeoButton geoButton;
	private AbstractEuclidianView view;
	private int x,y,width,height;
	private boolean selected;
	private String text;

	private Font font;

	private boolean pressed,draggedOrContext;
	/**
	 * @param button geo for this button
	 * @param view view
	 */
	public MyButton(GeoButton button, AbstractEuclidianView view) {
		this.geoButton = button;
		this.view = view;
		this.x=20;
		this.y=20;
		this.width=40;
		this.height=30;
	}

	
	
	
	/**
	 * Paint this on given graphics
	 * @param g graphics
	 */
	protected void paintComponent(geogebra.common.awt.Graphics2D g) {

		//Graphics2D g2 = geogebra.awt.Graphics2D.getAwtGraphics(g);

		view.setAntialiasing(g);
		
		g.setFont(font);
		TextLayout t= geogebra.common.factories.AwtFactory.prototype.newTextLayout(geoButton.getCaption(StringTemplate.defaultTemplate), font, g.getFontRenderContext());
		
		float textHeight = t.getAscent() + t.getDescent();
		
		width=Math.max((int)(t.getAdvance() * 1.1),24);
		
		height=Math.max((int)(textHeight * 1.1),24);
		
		int spareHeight = height - (int)(textHeight);

		int spareWidth = width - (int)(t.getAdvance());
		
		g.setColor(view.getBackgroundCommon());
		g.fillRect(x, y, getWidth(), getHeight());

		geogebra.common.awt.Paint p;

		geogebra.common.awt.Color bg = geoButton
				.getBackgroundColor(), bg2;
		if (bg == null)
			bg = geogebra.common.awt.Color.lightGray;
		if (isSelected()) {
			bg2 = bg;
			bg = bg.darker();
		} else {
			bg2 = bg.brighter();
		}
		if(!pressed){
			p = geogebra.common.factories.AwtFactory.prototype.newGradientPaint(x, y, bg2, x, y+getHeight(), bg);
		}else{
			p = geogebra.common.factories.AwtFactory.prototype.newGradientPaint(x, y, bg, x, y+getHeight(), bg2);
		}
		geogebra.common.awt.Paint oldPaint = g.getPaint();

		g.setPaint(p);

		g.fillRoundRect(x, y, getWidth(), getHeight(), getHeight() / 3,
				getHeight() / 3);

		g.setPaint(oldPaint);

		g.setColor(geogebra.common.awt.Color.black);

		g.setStroke(EuclidianStatic.getDefaultStroke());
		g.drawRoundRect(x, y, getWidth() - 1, getHeight() - 1,
				getHeight() / 3, getHeight() / 3);

		g.setColor(geoButton.getObjectColor());




		this.setForeground(geogebra.common.awt.Color.white);

		// center the label on the button
		g.drawString(geoButton.getCaption(StringTemplate.defaultTemplate), x + spareWidth / 2,
				y + t.getAscent() + spareHeight / 2);
	}


	/**
	 * @param white color 
	 */
	private void setForeground(geogebra.common.awt.Color white) {
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
	 * @param labelRectangle new bounds
	 */
	public void setBounds(Rectangle labelRectangle) {
		x=(int)labelRectangle.getMinX();
		y=(int)labelRectangle.getMinY();
		width = (int)labelRectangle.getWidth();
		height = (int)labelRectangle.getHeight();
		
	}
	
	/**
	 * @return boundsof this button
	 */
	public Rectangle getBounds(){
		return geogebra.common.factories.AwtFactory.prototype.newRectangle(x,y,width,height);
	}

	/**
	 * @param selected new selected flag
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
	 * @param labelDesc text for this button
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
	 * @param font new font
	 */
	public void setFont(Font font) {
		this.font = font;
		
	}




	/**
	 * @param b new pressed flag
	 */
	public void setPressed(boolean b) {
		if(b){
			draggedOrContext = false;
		}
		//releasing
		else if(!draggedOrContext){
			geoButton.runScripts(null);
		}
			
		pressed = b;
	}

	/**
	 * @param b new "dragged or context menu" flag
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
