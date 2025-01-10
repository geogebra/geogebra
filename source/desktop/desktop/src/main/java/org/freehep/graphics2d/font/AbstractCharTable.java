//Copyright 2001-2005 FreeHep
package org.freehep.graphics2d.font;

import org.geogebra.common.util.debug.Log;

/**
 * Abstract Character Table, inherited by all the Generated Encoding Tables
 * 
 * @author Simon Fischer
 * @version $Id: AbstractCharTable.java,v 1.6 2009-08-17 21:44:44 murkle Exp $
 */
public abstract class AbstractCharTable implements CharTable {

	@Override
	public int toEncoding(char unicode) {
		try {
			String name = toName(unicode);
			if (name == null) {
				return 0;
			}
			int enc = toEncoding(name);
			if (enc > 255) {
				Log.debug("toEncoding() returned illegal value for '" + name
						+ "': " + enc);
				return 0;
			}
			return enc;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public String toName(char c) {
		return toName(new Character(c));
	}

	@Override
	public String toName(Integer enc) {
		return toName(enc.intValue());
	}
}
