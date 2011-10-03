/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;



/**
 *
 * @author  Markus
 * @version 
 */
public final class DrawText extends Drawable {
	 
	//private static final int SELECTION_DIAMETER_ADD = 4;     
	//private static final int SELECTION_OFFSET = SELECTION_DIAMETER_ADD / 2;
	       
    private GeoText text;    
    private boolean isVisible, isLaTeX;     
    private int fontSize = -1;
    private int fontStyle = -1;
    private boolean serifFont;
    private Font textFont;
    private GeoPointND loc; // text location
    
    //private Image eqnImage;
    private int oldXpos, oldYpos;
    private boolean needsBoundingBoxOld;
    
    /** Creates new DrawText 
     * @param view 
     * @param text */
    public DrawText(EuclidianView view, GeoText text) {      
    	this.view = view;          
        this.text = text;
        geo = text;
        
        textFont = view.getApplication().getPlainFont().deriveFont(Font.PLAIN, view.fontSize);
        
        // this is needed as (bold) LaTeX texts are created with isLaTeX = false at this stage
        updateStrokes(text);
          
        update();
    }
    
    final public void update() {     
        isVisible = geo.isEuclidianVisible();       				 
        if (!isVisible 
        		&& !text.isNeedsUpdatedBoundingBox()) // needed for Corner[Element[text
        	return;          
        
        if (isLaTeX) // needed eg for \sqrt
        	updateStrokes(text);
        
        if (isLaTeX) // needed eg for \sqrt
        	updateStrokes(text);
        
        String newText = text.getTextString();
        boolean textChanged = labelDesc == null || 
        	!labelDesc.equals(newText) || 
        	isLaTeX != text.isLaTeX() ||
        	text.isNeedsUpdatedBoundingBox() != needsBoundingBoxOld;
		labelDesc = newText;
		isLaTeX = text.isLaTeX();	
		needsBoundingBoxOld = text.isNeedsUpdatedBoundingBox();
				
        		
        // compute location of text		
		if (text.isAbsoluteScreenLocActive()) {
			xLabel = text.getAbsoluteScreenLocX();
			yLabel = text.getAbsoluteScreenLocY(); 
		} else {
			loc = text.getStartPoint();
	        if (loc == null) {
				xLabel = (int) view.xZero;
				yLabel = (int) view.yZero;
	        } else {
	        	if (!loc.isDefined()) {
	        		isVisible = false;
	        		return;
	        	}

	        	//looks if it's on view     	
	        	Coords p = view.getCoordsForView(loc.getInhomCoordsInD(3));
	        	if (!Kernel.isZero(p.getZ())){
	        		isVisible = false;
	        		return;
	        	}
	        	
				xLabel = view.toScreenCoordX(p.getX());
				yLabel = view.toScreenCoordY(p.getY());        	
	        }
	        xLabel += text.labelOffsetX;
			yLabel += text.labelOffsetY; 
			
		}        
		
		boolean positionChanged = xLabel != oldXpos || yLabel != oldYpos;
		oldXpos = xLabel;
		oldYpos = yLabel;
		
		/*
		// use hotEqn for LaTeX
		if (isLaTeX && eqn == null && !JarManager.JSMATHTEX_LOADED) {
				eqn = new sHotEqn();
				eqn.setDoubleBuffered(false);
				eqn.setEditable(false);	
				eqn.removeMouseListener(eqn);
				eqn.removeMouseMotionListener(eqn);				
				eqn.setDebug(false);
				eqn.setOpaque(false);	
				//eqn.setFont(view.getFont());		
				
				eqn.setFontname(Application.STANDARD_FONT_NAME);
				setEqnFontSize();																												
				view.add(eqn);					
		}*/
		
		boolean fontChanged = doUpdateFontSize();				
			/*
		// avoid unnecessary updates of LaTeX equation
		if (isLaTeX) {
			
			if (JarManager.JSMATHTEX_LOADED)
			{
				TeXFormula formula;
				Icon icon;
				try{
					formula = new TeXFormula(labelDesc);
					icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,fontSize);
				}
				catch (Exception e)
				{
					formula = new TeXFormula("LaTeXerror");
					icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,fontSize);
					
				}

				labelRectangle.setBounds(xLabel, yLabel, icon.getIconWidth(), icon.getIconHeight());										
			}
			else
			{
				eqn = new sHotEqn();
				eqn.setDoubleBuffered(false);
				eqn.setEditable(false);	
				eqn.removeMouseListener(eqn);
				eqn.removeMouseMotionListener(eqn);				
				eqn.setDebug(false);
				eqn.setOpaque(false);	
				
				eqn.setFontname(Application.STANDARD_FONT_NAME);
				setEqnFontSize();																												
				eqn.setForeground(geo.getObjectColor());		
				eqn.setBackground(view.getBackground());
	
//			// set equation 
//			eqn.setEquation(labelDesc);
//			
//			// draw equation once to get it's image and size
//			eqn.paintComponent(view.getTempGraphics2D());
//			eqnImage = eqn.getImage();
//			eqnSize = eqn.getSize();
				
				eqnSize = eqn.getSizeof(labelDesc);

				// set bounding box of text component
				labelRectangle.setBounds(xLabel, yLabel, eqnSize.width, eqnSize.height);										
				eqn.setBounds(labelRectangle);		
			}
		} 
		else if (text.isNeedsUpdatedBoundingBox()) {
			// ensure that bounding box gets updated by drawing text once
			drawMultilineText(view.getTempGraphics2D());	
		}*/
		
		
		
		if (text.isNeedsUpdatedBoundingBox() && 
			(textChanged || positionChanged || fontChanged )) 
		{									
			// ensure that bounding box gets updated by drawing text once
			if (isLaTeX) 
				drawMultilineLaTeX(view.getTempGraphics2D(textFont), textFont, geo.getObjectColor(),view.getBackground());
			else 
				drawMultilineText(view.getTempGraphics2D(textFont));	
			
			// Michael Borcherds 2007-11-26 BEGIN update corners for Corner[] command
			double xRW = view.toRealWorldCoordX(labelRectangle.x);
			double yRW = view.toRealWorldCoordY(labelRectangle.y);		
						
			text.setBoundingBox(xRW, yRW,
					labelRectangle.width * view.invXscale,
					-labelRectangle.height * view.invYscale);	
		}
    }
    
    final public void draw(Graphics2D g2) { 
        if (isVisible) {
        	
        	Color bg = geo.getBackgroundColor();
        	
        	if (bg != null) {
        		
        		// needed to calculate labelRectangle
    			if (isLaTeX) drawMultilineLaTeX(view.getTempGraphics2D(textFont), textFont, geo.getObjectColor(), view.getBackground());
    			else drawMultilineText(view.getTempGraphics2D(textFont));   
    			
				g2.setStroke(objStroke);
				g2.setPaint(bg);
				g2.fill(labelRectangle);       
        	}
        	
			if (isLaTeX) {
        		g2.setPaint(geo.getObjectColor());				
    			g2.setFont(textFont);    			
    			g2.setStroke(objStroke); // needed eg for \sqrt
    			drawMultilineLaTeX(g2, textFont, geo.getObjectColor(),bg != null ? bg : view.getBackground());       
        	} 
        	else {
        		g2.setPaint(geo.getObjectColor());				
    			g2.setFont(textFont);    	
    			drawMultilineText(g2);   
        	}   
			  
			// draw label rectangle
			if (geo.doHighlighting()) {
				g2.setStroke(objStroke);
				g2.setPaint(Color.lightGray);
				g2.draw(labelRectangle);         
			}   							
        }
    }
    /*
	final public void drawEquation(Graphics2D g2, int x, int y)
	{
		if (!JarManager.JSMATHTEX_LOADED)
		{
			eqn = new sHotEqn(labelDesc);
			eqn.setDoubleBuffered(false);
			eqn.setEditable(false);	
			eqn.removeMouseListener(eqn);
			eqn.removeMouseMotionListener(eqn);				
			eqn.setDebug(false);
			eqn.setOpaque(false);	
			eqn.setFontname(Application.STANDARD_FONT_NAME);
			setEqnFontSize();																												
			eqn.paintComponent(g2,xLabel,yLabel);					
		}
		else
		{
   	 // TEST CODE FOR JMathTeX
			TeXFormula formula;
			Icon icon;
			try{
				formula = new TeXFormula(labelDesc);
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,fontSize);
			}
			catch (Exception e)
			{
				formula = new TeXFormula("LaTeXerror");
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,fontSize);
				
			}
			icon.paintIcon(new JLabel(), g2, x, y); // component can't be null
		}

	}*/
   
    /**
     * was this object clicked at? (mouse pointer
     * location (x,y) in screen coords)
     */
    final public boolean hit(int x, int y) {
		return super.hitLabel(x, y);				      
    }
    
    final public boolean isInside(Rectangle rect) {
    	return rect.contains(labelRectangle);  
    }
    
    /**
     * Returns false     
     */ 
	public boolean hitLabel(int x, int y) {
		return false;
	}
    
    final public GeoElement getGeoElement() {
        return geo;
    }    
    
    final public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 

	private boolean doUpdateFontSize() {
		// text's font size is relative to the global font size
		int newFontSize = Math.max(4, view.fontSize + text.getFontSize());		
		int newFontStyle = text.getFontStyle();	
		boolean newSerifFont = text.isSerifFont();
		
		if (textFont.canDisplayUpTo(text.getTextString()) != -1 ||
				fontSize !=newFontSize || fontStyle != newFontStyle || newSerifFont != serifFont) {					
			super.updateFontSize();
			
			fontSize = newFontSize;
			fontStyle = newFontStyle;
			serifFont = newSerifFont;
						
			//if (isLaTeX) {
			//	//setEqnFontSize();				
			//} else {				
				Application app = view.getApplication();
				textFont = app.getFontCanDisplay(text.getTextString(), serifFont, fontStyle, fontSize);				
			//}	
				return true;
		}			
		
		return false;
	}
	
	/*
	private void setEqnFontSize() {		
		
		// hot eqn may only have even font sizes from 10 to 28
		int size = (fontSize / 2) * 2; 
		if (size < 10) 
			size = 10;
		else if (size > 28) 
			size = 28;
		
		eqn.setFontname(serifFont ? "Serif" : "SansSerif");
		eqn.setFontsizes(size, size - 2, size - 4, size - 6);
		eqn.setFontStyle(fontStyle);
		
	}
	*/
	
    /**
	 * Returns the bounding box of this Drawable in screen coordinates.	 
	 */
	final public Rectangle getBounds() {		
		if (!geo.isDefined() || ((GeoText)geo).isAbsoluteScreenLocActive() || !geo.isEuclidianVisible())
			return null;
		else 
			return labelRectangle;	
	}
}


