/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package com.kitfox.svg.batik;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 *
 * @author  kitfox
 */
public class GraphicsUtil
{
    
    /** Creates a new instance of GraphicsUtil */
    public GraphicsUtil()
    {
    }
    
    /**
     * Create a new ColorModel with it's alpha premultiplied state matching
     * newAlphaPreMult.
     * @param cm The ColorModel to change the alpha premult state of.
     * @param newAlphaPreMult The new state of alpha premult.
     * @return   A new colorModel that has isAlphaPremultiplied()
     *           equal to newAlphaPreMult.
     */
    public static ColorModel coerceColorModel(ColorModel cm, boolean newAlphaPreMult)
    {
        if (cm.isAlphaPremultiplied() == newAlphaPreMult)
            return cm;
        
        // Easiest way to build proper colormodel for new Alpha state...
        // Eventually this should switch on known ColorModel types and
        // only fall back on this hack when the CM type is unknown.
        WritableRaster wr = cm.createCompatibleWritableRaster(1,1);
        return cm.coerceData(wr, newAlphaPreMult);
    }
    
    /**
     * Coerces data within a bufferedImage to match newAlphaPreMult,
     * Note that this can not change the colormodel of bi so you
     *
     * @param wr The raster to change the state of.
     * @param cm The colormodel currently associated with data in wr.
     * @param newAlphaPreMult The desired state of alpha Premult for raster.
     * @return A new colormodel that matches newAlphaPreMult.
     */
    public static ColorModel coerceData(WritableRaster wr, ColorModel cm, boolean newAlphaPreMult)
    {
        
        // System.out.println("CoerceData: " + cm.isAlphaPremultiplied() +
        //                    " Out: " + newAlphaPreMult);
        if (cm.hasAlpha()== false)
            // Nothing to do no alpha channel
            return cm;
        
        if (cm.isAlphaPremultiplied() == newAlphaPreMult)
            // nothing to do alpha state matches...
            return cm;
        
        // System.out.println("CoerceData: " + wr.getSampleModel());
        
        int [] pixel = null;
        int    bands = wr.getNumBands();
        float  norm;
        if (newAlphaPreMult)
        {
            if (is_BYTE_COMP_Data(wr.getSampleModel()))
                mult_BYTE_COMP_Data(wr);
            else if (is_INT_PACK_Data(wr.getSampleModel(), true))
                mult_INT_PACK_Data(wr);
            else
            {
                norm = 1f/255f;
                int x0, x1, y0, y1, a, b;
                float alpha;
                x0 = wr.getMinX();
                x1 = x0+wr.getWidth();
                y0 = wr.getMinY();
                y1 = y0+wr.getHeight();
                for (int y=y0; y<y1; y++)
                    for (int x=x0; x<x1; x++)
                    {
                        pixel = wr.getPixel(x,y,pixel);
                        a = pixel[bands-1];
                        if ((a >= 0) && (a < 255))
                        {
                            alpha = a*norm;
                            for (b=0; b<bands-1; b++)
                                pixel[b] = (int)(pixel[b]*alpha+0.5f);
                            wr.setPixel(x,y,pixel);
                        }
                    }
            }
        } else
        {
            if (is_BYTE_COMP_Data(wr.getSampleModel()))
                divide_BYTE_COMP_Data(wr);
            else if (is_INT_PACK_Data(wr.getSampleModel(), true))
                divide_INT_PACK_Data(wr);
            else
            {
                int x0, x1, y0, y1, a, b;
                float ialpha;
                x0 = wr.getMinX();
                x1 = x0+wr.getWidth();
                y0 = wr.getMinY();
                y1 = y0+wr.getHeight();
                for (int y=y0; y<y1; y++)
                    for (int x=x0; x<x1; x++)
                    {
                        pixel = wr.getPixel(x,y,pixel);
                        a = pixel[bands-1];
                        if ((a > 0) && (a < 255))
                        {
                            ialpha = 255/(float)a;
                            for (b=0; b<bands-1; b++)
                                pixel[b] = (int)(pixel[b]*ialpha+0.5f);
                            wr.setPixel(x,y,pixel);
                        }
                    }
            }
        }
        
        return coerceColorModel(cm, newAlphaPreMult);
    }
    
    
    public static boolean is_INT_PACK_Data(SampleModel sm,
    boolean requireAlpha)
    {
        // Check ColorModel is of type DirectColorModel
        if(!(sm instanceof SinglePixelPackedSampleModel)) return false;
        
        // Check transfer type
        if(sm.getDataType() != DataBuffer.TYPE_INT)       return false;
        
        SinglePixelPackedSampleModel sppsm;
        sppsm = (SinglePixelPackedSampleModel)sm;
        
        int [] masks = sppsm.getBitMasks();
        if (masks.length == 3)
        {
            if (requireAlpha) return false;
        } else if (masks.length != 4)
            return false;
        
        if(masks[0] != 0x00ff0000) return false;
        if(masks[1] != 0x0000ff00) return false;
        if(masks[2] != 0x000000ff) return false;
        if ((masks.length == 4) &&
        (masks[3] != 0xff000000)) return false;
        
        return true;
    }
    
    protected static void mult_INT_PACK_Data(WritableRaster wr)
    {
        // System.out.println("Multiply Int: " + wr);
        
        SinglePixelPackedSampleModel sppsm;
        sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        
        final int width = wr.getWidth();
        
        final int scanStride = sppsm.getScanlineStride();
        DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        final int base
        = (db.getOffset() +
        sppsm.getOffset(wr.getMinX()-wr.getSampleModelTranslateX(),
        wr.getMinY()-wr.getSampleModelTranslateY()));
        int n=0;
        // Access the pixel data array
        final int pixels[] = db.getBankData()[0];
        for (int y=0; y<wr.getHeight(); y++)
        {
            int sp = base + y*scanStride;
            final int end = sp + width;
            while (sp < end)
            {
                int pixel = pixels[sp];
                int a = pixel>>>24;
                if ((a>=0) && (a<255))
                {
                    pixels[sp] = ((a << 24) |
                    ((((pixel&0xFF0000)*a)>>8)&0xFF0000) |
                    ((((pixel&0x00FF00)*a)>>8)&0x00FF00) |
                    ((((pixel&0x0000FF)*a)>>8)&0x0000FF));
                }
                sp++;
            }
        }
    }
    
    protected static void divide_INT_PACK_Data(WritableRaster wr)
    {
        // System.out.println("Divide Int");
        
        SinglePixelPackedSampleModel sppsm;
        sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        
        final int width = wr.getWidth();
        
        final int scanStride = sppsm.getScanlineStride();
        DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        final int base
        = (db.getOffset() +
        sppsm.getOffset(wr.getMinX()-wr.getSampleModelTranslateX(),
        wr.getMinY()-wr.getSampleModelTranslateY()));
        int pixel, a, aFP, n=0;
        // Access the pixel data array
        final int pixels[] = db.getBankData()[0];
        for (int y=0; y<wr.getHeight(); y++)
        {
            int sp = base + y*scanStride;
            final int end = sp + width;
            while (sp < end)
            {
                pixel = pixels[sp];
                a = pixel>>>24;
                if (a<=0)
                {
                    pixels[sp] = 0x00FFFFFF;
                }
                else if (a<255)
                {
                    aFP = (0x00FF0000/a);
                    pixels[sp] =
                    ((a << 24) |
                    (((((pixel&0xFF0000)>>16)*aFP)&0xFF0000)    ) |
                    (((((pixel&0x00FF00)>>8) *aFP)&0xFF0000)>>8 ) |
                    (((((pixel&0x0000FF))    *aFP)&0xFF0000)>>16));
                }
                sp++;
            }
        }
    }
    
    public static boolean is_BYTE_COMP_Data(SampleModel sm)
    {
        // Check ColorModel is of type DirectColorModel
        if(!(sm instanceof ComponentSampleModel))    return false;
        
        // Check transfer type
        if(sm.getDataType() != DataBuffer.TYPE_BYTE) return false;
        
        return true;
    }
    
    protected static void mult_BYTE_COMP_Data(WritableRaster wr)
    {
        // System.out.println("Multiply Int: " + wr);
        
        ComponentSampleModel csm;
        csm = (ComponentSampleModel)wr.getSampleModel();
        
        final int width = wr.getWidth();
        
        final int scanStride = csm.getScanlineStride();
        final int pixStride  = csm.getPixelStride();
        final int [] bandOff = csm.getBandOffsets();
        
        DataBufferByte db = (DataBufferByte)wr.getDataBuffer();
        final int base
        = (db.getOffset() +
        csm.getOffset(wr.getMinX()-wr.getSampleModelTranslateX(),
        wr.getMinY()-wr.getSampleModelTranslateY()));
        
        
        int a=0;
        int aOff = bandOff[bandOff.length-1];
        int bands = bandOff.length-1;
        int b, i;
        
        // Access the pixel data array
        final byte pixels[] = db.getBankData()[0];
        for (int y=0; y<wr.getHeight(); y++)
        {
            int sp = base + y*scanStride;
            final int end = sp + width*pixStride;
            while (sp < end)
            {
                a = pixels[sp+aOff]&0xFF;
                if (a!=0xFF)
                    for (b=0; b<bands; b++)
                    {
                        i = sp+bandOff[b];
                        pixels[i] = (byte)(((pixels[i]&0xFF)*a)>>8);
                    }
                sp+=pixStride;
            }
        }
    }
    
    protected static void divide_BYTE_COMP_Data(WritableRaster wr)
    {
        // System.out.println("Multiply Int: " + wr);
        
        ComponentSampleModel csm;
        csm = (ComponentSampleModel)wr.getSampleModel();
        
        final int width = wr.getWidth();
        
        final int scanStride = csm.getScanlineStride();
        final int pixStride  = csm.getPixelStride();
        final int [] bandOff = csm.getBandOffsets();
        
        DataBufferByte db = (DataBufferByte)wr.getDataBuffer();
        final int base
        = (db.getOffset() +
        csm.getOffset(wr.getMinX()-wr.getSampleModelTranslateX(),
        wr.getMinY()-wr.getSampleModelTranslateY()));
        
        
        int a=0;
        int aOff = bandOff[bandOff.length-1];
        int bands = bandOff.length-1;
        int b, i;
        // Access the pixel data array
        final byte pixels[] = db.getBankData()[0];
        for (int y=0; y<wr.getHeight(); y++)
        {
            int sp = base + y*scanStride;
            final int end = sp + width*pixStride;
            while (sp < end)
            {
                a = pixels[sp+aOff]&0xFF;
                if (a==0)
                {
                    for (b=0; b<bands; b++)
                        pixels[sp+bandOff[b]] = (byte)0xFF;
                } else if (a<255)
                {
                    int aFP = (0x00FF0000/a);
                    for (b=0; b<bands; b++)
                    {
                        i = sp+bandOff[b];
                        pixels[i] = (byte)(((pixels[i]&0xFF)*aFP)>>>16);
                    }
                }
                sp+=pixStride;
            }
        }
    }
    
    
}
