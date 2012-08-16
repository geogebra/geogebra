/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui.editor;

import java.awt.Color;
import java.awt.Font;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * 
 * @author Calixte DENIZET
 *
 */
public abstract class ViewContext implements ViewFactory {

	/**
     * Contains the colors of the different tokens
     */
    public Color[] tokenColors;

    /**
     * The font to use
     */
    public Font tokenFont;
    
    
    /**
     * @param font	Font to be set
     */
    public void setTokenFont(Font font){
    	tokenFont = font;
    }

    /**
     * Contains the attributes (underline or stroke) of the different tokens
     */
    public int[] tokenAttrib;

    /**
     * @return the view to use to render the document
     */
    public abstract View getCurrentView();
    
    /**
	 * {@inheritDoc}
     */
    public abstract View create(Element elem);
}
