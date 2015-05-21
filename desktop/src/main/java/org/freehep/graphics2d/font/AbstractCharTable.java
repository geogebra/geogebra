//Copyright 2001-2005 FreeHep
package org.freehep.graphics2d.font;

import org.geogebra.common.main.App;

/**
 * Abstract Character Table, inherited by all the Generated Encoding Tables
 * 
 * @author Simon Fischer
 * @version $Id: AbstractCharTable.java,v 1.6 2009-08-17 21:44:44 murkle Exp $
 */
public abstract class AbstractCharTable implements CharTable {

    public int toEncoding(char unicode) {
        try {
            String name = toName(unicode);
            if (name == null)
                return 0;
            int enc = toEncoding(name);
            if (enc > 255) {
				App.debug("toEncoding() returned illegal value for '"
                        + name + "': " + enc);
                return 0;
            }
            return enc;
        } catch (Exception e) {
            return 0;
        }
    }

    public String toName(char c) {
        return toName(new Character(c));
    }

    public String toName(Integer enc) {
        return toName(enc.intValue());
    }
}
