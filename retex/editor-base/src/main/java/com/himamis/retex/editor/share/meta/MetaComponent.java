/* MetaComponent.java
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

/**
 * Meta Model of Math Component.
 *
 * @author Bea Petrovicova
 */
public class MetaComponent {

    private String name, casName, texName;
    private char key, unicode;

    MetaComponent(String name, String casName, String texName, char key, char unicode) {
        this.name = name;
        this.casName = casName;
        this.texName = texName;
        this.key = key;
        this.unicode = unicode;
    }

    /**
     * Uid name.
     */
    public String getName() {
        return name;
    }

    /**
     * Cas name.
     */
    public String getCasName() {
        return casName;
    }

    /**
     * TeX name.
     */
    public String getTexName() {
        return texName;
    }

    /**
     * Input Key.
     */
    public char getKey() {
        return key;
    }

    /**
     * Unicode char.
     */
    public char getUnicode() {
        return unicode;
    }
}
