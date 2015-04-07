/*
 * SVG Salamander
 * Copyright (c) 2004, Mark McKay
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 *   - Redistributions of source code must retain the above 
 *     copyright notice, this list of conditions and the following
 *     disclaimer.
 *   - Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * Mark McKay can be contacted at mark@kitfox.com.  Salamander and other
 * projects can be found at http://www.kitfox.com
 *
 * Created on April 21, 2005, 10:43 AM
 */

package com.kitfox.svg.app.beans;

import com.kitfox.svg.*;
import java.awt.*;
import java.awt.geom.*;
import java.net.*;
import javax.swing.*;

/**
 *
 * @author  kitfox
 */
public class SVGPanel extends JPanel
{
    public static final long serialVersionUID = 1;
    public static final String PROP_AUTOSIZE = "PROP_AUTOSIZE";

    SVGUniverse svgUniverse = SVGCache.getSVGUniverse();
    
    private boolean antiAlias;
    
//    private String svgPath;
    URI svgURI;

//    private boolean scaleToFit;
    AffineTransform scaleXform = new AffineTransform();

    public static final int AUTOSIZE_NONE = 0;
    public static final int AUTOSIZE_HORIZ = 1;
    public static final int AUTOSIZE_VERT = 2;
    public static final int AUTOSIZE_BESTFIT = 3;
    public static final int AUTOSIZE_STRETCH = 4;
    private int autosize = AUTOSIZE_NONE;
    
    /** Creates new form SVGIcon */
    public SVGPanel()
    {
        initComponents();
    }
        
    public int getSVGHeight()
    {
        if (autosize == AUTOSIZE_VERT || autosize == AUTOSIZE_STRETCH 
                || autosize == AUTOSIZE_BESTFIT)
        {
            return getPreferredSize().height;
        }
        
        SVGDiagram diagram = svgUniverse.getDiagram(svgURI);
        if (diagram == null)
        {
            return 0;
        }
        return (int)diagram.getHeight();
    }
    
    public int getSVGWidth()
    {
        if (autosize == AUTOSIZE_HORIZ || autosize == AUTOSIZE_STRETCH 
                || autosize == AUTOSIZE_BESTFIT)
        {
            return getPreferredSize().width;
        }
        
        SVGDiagram diagram = svgUniverse.getDiagram(svgURI);
        if (diagram == null)
        {
            return 0;
        }
        return (int)diagram.getWidth();
    }
    
    public void paintComponent(Graphics gg)
    {
        super.paintComponent(gg);

        Graphics2D g = (Graphics2D)gg.create();
        paintComponent(g);
        g.dispose();
    }
    
    private void paintComponent(Graphics2D g)
    {
        Object oldAliasHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

        
        SVGDiagram diagram = svgUniverse.getDiagram(svgURI);
        if (diagram == null)
        {
            return;
        }
        
        if (autosize == AUTOSIZE_NONE)
        {
            try
            {
                diagram.render(g);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAliasHint);
            }
            catch (SVGException e)
            {
                throw new RuntimeException(e);
            }
            return;
        }
        
        Dimension dim = getSize();
        final int width = dim.width;
        final int height = dim.height;
            
//        final Rectangle2D.Double rect = new Rectangle2D.Double();
//        diagram.getViewRect(rect);
        
        double diaWidth = diagram.getWidth();
        double diaHeight = diagram.getHeight();
        
        double scaleW = 1;
        double scaleH = 1;
        if (autosize == AUTOSIZE_BESTFIT)
        {
            scaleW = scaleH = (height / diaHeight < width / diaWidth) 
                    ? height / diaHeight : width / diaWidth;
        }
        else if (autosize == AUTOSIZE_HORIZ)
        {
            scaleW = scaleH = width / diaWidth;
        }
        else if (autosize == AUTOSIZE_VERT)
        {
            scaleW = scaleH = height / diaHeight;
        }
        else if (autosize == AUTOSIZE_STRETCH)
        {
            scaleW = width / diaWidth;
            scaleH = height / diaHeight;
        }
        scaleXform.setToScale(scaleW, scaleH);
        
        AffineTransform oldXform = g.getTransform();
        g.transform(scaleXform);

        try
        {
            diagram.render(g);
        }
        catch (SVGException e)
        {
            throw new RuntimeException(e);
        }
        
        g.setTransform(oldXform);
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAliasHint);
    }
    
    public SVGUniverse getSvgUniverse()
    {
        return svgUniverse;
    }

    public void setSvgUniverse(SVGUniverse svgUniverse)
    {
        SVGUniverse old = this.svgUniverse;
        this.svgUniverse = svgUniverse;
        firePropertyChange("svgUniverse", old, svgUniverse);
    }

    public URI getSvgURI()
    {
        return svgURI;
    }

    public void setSvgURI(URI svgURI)
    {
        URI old = this.svgURI;
        this.svgURI = svgURI;
        firePropertyChange("svgURI", old, svgURI);
    }
    
    /**
     * Most resources your component will want to access will be resources on your classpath.  
     * This method will interpret the passed string as a path in the classpath and use
     * Class.getResource() to determine the URI of the SVG.
     */
    public void setSvgResourcePath(String resourcePath) throws SVGException
    {
        URI old = this.svgURI;
        
        try
        {
            svgURI = new URI(getClass().getResource(resourcePath).toString());
//System.err.println("SVGPanel: new URI " + svgURI + " from path " + resourcePath);
            
            firePropertyChange("svgURI", old, svgURI);
            
            repaint();
        }
        catch (Exception e)
        {
            throw new SVGException("Could not resolve path " + resourcePath, e);
//            svgURI = old;
        }
    }
    
    /**
     * If this SVG document has a viewbox, if scaleToFit is set, will scale the viewbox to match the
     * preferred size of this icon
     * @deprecated 
     * @return 
     */
    public boolean isScaleToFit()
    {
        return autosize == AUTOSIZE_STRETCH;
    }
    
    /**
     * @deprecated 
     * @return 
     */
    public void setScaleToFit(boolean scaleToFit)
    {
        setAutosize(AUTOSIZE_STRETCH);
//        boolean old = this.scaleToFit;
//        this.scaleToFit = scaleToFit;
//        firePropertyChange("scaleToFit", old, scaleToFit);
    }
    
    /**
     * @return true if antiAliasing is turned on.
     * @deprecated
     */
    public boolean getUseAntiAlias()
    {
        return getAntiAlias();
    }

    /**
     * @param antiAlias true to use antiAliasing.
     * @deprecated
     */
    public void setUseAntiAlias(boolean antiAlias)
    {
        setAntiAlias(antiAlias);
    }
    
    /**
     * @return true if antiAliasing is turned on.
     */
    public boolean getAntiAlias()
    {
        return antiAlias;
    }

    /**
     * @param antiAlias true to use antiAliasing.
     */
    public void setAntiAlias(boolean antiAlias)
    {
        boolean old = this.antiAlias;
        this.antiAlias = antiAlias;
        firePropertyChange("antiAlias", old, antiAlias);
    }

    /**
     * @return the autosize
     */
    public int getAutosize()
    {
        return autosize;
    }

    /**
     * @param autosize the autosize to set
     */
    public void setAutosize(int autosize)
    {
        int oldAutosize = this.autosize;
        this.autosize = autosize;
        firePropertyChange(PROP_AUTOSIZE, oldAutosize, autosize);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        setLayout(new java.awt.BorderLayout());

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    
}
