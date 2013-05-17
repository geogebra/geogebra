// Copyright 2001-2006, FreeHEP.
package org.freehep.graphicsio.svg;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.freehep.graphicsio.font.FontTable;

/**
 * A table to remember which glyphs were used while writing a svg file.
 * Entries are added by calling {@link #addGlyphs(String, java.awt.Font)}.
 * The final SVG tag for the <defs> section is generated using {@link #toString()}.
 * Use {@link #normalize(java.util.Map)} for referencing embedded glyphs
 * in <text> tags.
 *
 * @author Steffen Greiffenberg
 * @version $Id: SVGFontTable.java,v 1.6 2009-11-11 17:05:23 hohenwarter Exp $
 */
public class SVGFontTable {

    /**
     * Stores fonts and a glyph-hashtable. The font key ist normalized using
     * {@link #untransform(java.awt.Font)}
     */
    private Hashtable/*<Font, Hashtable<String, SVGGlyph>*/ glyphs =
        new Hashtable/*<Font, Hashtable<String SVGGlyph>>*/();

    /**
     * creates a glyph for the string character
     *
     * @param c
     * @param font
     * @return unique font name
     */
    private SVGGlyph addGlyph(int c, Font font) {
        // is the font stored?
        Hashtable/*<String, SVGGlyph>*/ glyphs = getGlyphs(font);

        // does a glyph allready exist?
        SVGGlyph result = (SVGGlyph) glyphs.get(String.valueOf(c));

        // create a new one?
        if (result == null) {
            // create and store the SVG Glyph
            result = createGlyph(c, font);
            glyphs.put(String.valueOf(c), result);
        }

        return result;
    }

    /**
     * @param c
     * @param font
     * @return GlyphVector using a default rendering context
     */
    private SVGGlyph createGlyph(int c, Font font) {
        GlyphVector glyphVector = font.createGlyphVector(
            // flipping is done by SVGGlyph
            new FontRenderContext(null, true, true),
            // unicode to char
            String.valueOf((char) c));

        // create and store the SVG Glyph
        return new SVGGlyph(
            glyphVector.getGlyphOutline(0),
            c,
            glyphVector.getGlyphMetrics(0));
    }

    /**
     * creates the glyph for the string
     *
     * @param string
     * @param font
     */
    protected void addGlyphs(String string, Font font) {
        font = untransform(font);

        // add characters
        for (int i = 0; i < string.length(); i ++) {
            addGlyph(string.charAt(i), font);
        }
    }

    /**
     * @param font
     * @return glyph vectors for font
     */
    private Hashtable/*<String SVGGlyph>*/ getGlyphs(Font font) {
        // derive a default font for the font table
        font = untransform(font);

        Hashtable/*<String SVGGlyph>*/ result =
            (Hashtable/*<String SVGGlyph>*/) glyphs.get(font);
        if (result == null) {
            result = new Hashtable/*<String SVGGlyph>*/();
            glyphs.put(font, result);
        }
        return result;
    }

    /**
     * creates the font entry:
     * <PRE>
     * <font>
     * <glyph ... />
     * ...
     * </font>
     * </PRE>
     *
     * @return string representing the entry
     */
    public String toString() {
        StringBuffer result = new StringBuffer();

        Enumeration/*<Font>*/ fonts = this.glyphs.keys();
        while (fonts.hasMoreElements()) {
            Font font = (Font) fonts.nextElement();

            // replace font family for svg
            Map /*<TextAttribute, ?>*/ attributes = font.getAttributes();

            // Dialog -> Helvetica
            normalize(attributes);

            // familiy
            result.append("<font id=\"");
            result.append(attributes.get(TextAttribute.FAMILY));
            result.append("\">\n");

            // font-face
            result.append("<font-face font-family=\"");
            result.append(attributes.get(TextAttribute.FAMILY));
            result.append("\" ");

            // bold
            if (TextAttribute.WEIGHT_BOLD.equals(attributes.get(TextAttribute.WEIGHT))) {
                result.append("font-weight=\"bold\" ");
            } else {
                result.append("font-weight=\"normal\" ");
            }

            // italic
            if (TextAttribute.POSTURE_OBLIQUE.equals(attributes.get(TextAttribute.POSTURE))) {
                result.append("font-style=\"italic\" ");
            } else {
                result.append("font-style=\"normal\" ");
            }

            // size
            Float size = (Float) attributes.get(TextAttribute.SIZE);
            result.append("font-size=\"");
            result.append(SVGGraphics2D.fixedPrecision(size.floatValue()));
            result.append("\" ");

            // number of coordinate units on the em square,
            // the size of the design grid on which glyphs are laid out
            result.append("units-per-em=\"");
            result.append(SVGGraphics2D.fixedPrecision(SVGGlyph.FONT_SIZE));
            result.append("\" ");

            TextLayout tl = new TextLayout("By", font, new FontRenderContext(new AffineTransform(), true, true));

            // The maximum unaccented height of the font within the font coordinate system.
            // If the attribute is not specified, the effect is as if the attribute were set
            // to the difference between the units-per-em value and the vert-origin-y value
            // for the corresponding font.
            result.append("ascent=\"");
            result.append(tl.getAscent());
            result.append("\" ");

            // The maximum unaccented depth of the font within the font coordinate system.
            // If the attribute is not specified, the effect is as if the attribute were set
            // to the vert-origin-y value for the corresponding font.
            result.append("descent=\"");
            result.append(tl.getDescent());
            result.append("\" ");

            // For horizontally oriented glyph layouts, indicates the alignment
            // coordinate for glyphs to achieve alphabetic baseline alignment.
            // result.append("alphabetic=\"0\"

            // close "<font-face"
            result.append("/>\n");

            // missing glyph
            SVGGlyph glyph = createGlyph(font.getMissingGlyphCode(), font);
            result.append("<missing-glyph ");
            result.append(glyph.getHorizontalAdvanceXString());
            result.append(" ");
            result.append(glyph.getPathString());
            result.append("/>\n");

            // regular glyphs
            Iterator glyphs = getGlyphs(font).values().iterator();
            while (glyphs.hasNext()) {
                result.append(glyphs.next().toString());
                result.append("\n");
            }

            // close "<font>"
            result.append("</font>\n");
        }

        return result.toString();
    }

    /**
     * creates a font based on the parameter. The size will be {@link SVGGlyph.FONT_SIZE}
     * and transformation will be removed. Example:<BR>
     * <code>java.awt.Font[family=SansSerif,name=SansSerif,style=plain,size=30]</code><BR>
     * will result to:<BR>
     * <code>java.awt.Font[family=SansSerif,name=SansSerif,style=plain,size=100]</code><BR><BR>
     *
     * This method does not substitute font name or family.
     *
     * @param font
     * @return font based on the parameter
     */
    private Font untransform(Font font) {
        // replace font family
        Map /*<TextAttribute, ?>*/ attributes = font.getAttributes();

        // set default font size
        attributes.put(TextAttribute.SIZE, new Float(SVGGlyph.FONT_SIZE));

        // remove font transformation
        attributes.remove(TextAttribute.TRANSFORM);
        attributes.remove(TextAttribute.SUPERSCRIPT);

        return new Font(attributes);
    }

    /**
     * font replacements makes SVG in AdobeViewer look better, firefox replaces
     * all font settings, even the family fame
     */
    private static final Properties replaceFonts = new Properties();
    static {
        replaceFonts.setProperty("dialog", "Helvetica");
        replaceFonts.setProperty("dialoginput", "Courier New");
        // FIXME: works well on windows, others?
        // "TimesRoman" is not valid under Firefox 1.5
        replaceFonts.setProperty("serif", "Times");
        replaceFonts.setProperty("timesroman", "Times");
        replaceFonts.setProperty("sansserif", "Helvetica");
        // FIXME: works well on windows, others?
        // "Courier" is not valid under Firefox 1.5
        replaceFonts.setProperty("monospaced", "Courier New");
        // FIXME: replacement for zapfdingbats?
        replaceFonts.setProperty("zapfdingbats", "Wingdings");
    }

    /**
     * Replaces TextAttribute.FAMILY by values of replaceFonts. When a
     * font created using the result of this method the transformation would be:
     *
     * <code>java.awt.Font[family=SansSerif,name=SansSerif,style=plain,size=30]</code><BR>
     * will result to:<BR>
     * <code>java.awt.Font[family=SansSerif,name=Helvetica,style=plain,size=30]</code><BR><BR>
     *
     * Uses {@link FontTable#normalize(java.util.Map)} first.
     *
     * @param attributes with font name to change
     */
    public static void normalize(Map /*<TextAttribute, ?>*/ attributes) {
        // dialog.bold -> Dialog with TextAttribute.WEIGHT_BOLD
        FontTable.normalize(attributes);

        // get replaced font family name (Yes it's right, not the name!)
        String family = replaceFonts.getProperty(
            ((String) attributes.get(TextAttribute.FAMILY)).toLowerCase());
        if (family == null) {
            family = (String) attributes.get(TextAttribute.FAMILY);
        }
        
        // replace the family (Yes it's right, not the name!) in the attributes
        attributes.put(TextAttribute.FAMILY, family);
    }
}
