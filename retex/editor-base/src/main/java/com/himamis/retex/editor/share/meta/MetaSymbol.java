/* MetaSymbol.java
 * =========================================================================
 * This file is part of the Mirai Math TN - http://mirai.sourceforge.net
 *
 * Copyright (C) 2008-2009 Bea Petrovicova
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 */
package com.himamis.retex.editor.share.meta;

import com.himamis.retex.renderer.share.DefaultTeXFont;
import com.himamis.retex.renderer.share.platform.font.Font;

/**
 * Meta Model for Greek Symbol.
 *
 * @author Bea Petrovicova
 */
public class MetaSymbol extends MetaCharacter {

    private String description;
    private char code;
    private Font font;

    MetaSymbol(String name, String casName, String texName, char key, char code, char unicode, int fontId, int type) {
        this(name, casName, texName, key, code, unicode, DefaultTeXFont.getFont(fontId), type);
    }

    MetaSymbol(String name, String casName, String texName, char key, char code, char unicode, Font font, int type) {
        super(name, casName, texName, key, unicode, type);
        this.code = code;
        this.font = font;
        this.description = casName;
    }

    /**
     * Abstract font.
     */
    public Font getFont() {
        return font;
    }

    /**
     * ASCII code for symbol.
     */
    public char getCode() {
        return code;
    }

    /**
     * Description.
     */
    public String getDescription() {
        return description;
    }

}
