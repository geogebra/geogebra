/* ExternalFontManager.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2018 DENIZET Calixte
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
 * Linking this library statically or dynamically with other modules
 * is making a combined work based on this library. Thus, the terms
 * and conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce
 * an executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under terms
 * of your choice, provided that you also meet, for each linked independent
 * module, the terms and conditions of the license of that module.
 * An independent module is a module which is not derived from or based
 * on this library. If you modify this library, you may extend this exception
 * to your version of the library, but you are not obliged to do so.
 * If you do not wish to do so, delete this exception statement from your
 * version.
 *
 */

package com.himamis.retex.renderer.share;

import java.util.HashMap;
import java.util.Map;

import com.himamis.retex.renderer.share.character.Character;

/*
TODO: pas terrible cette classe: on ne peut pas faire du multithread a cause de cette classe: si un thread fait une association LATIN->fontFoo et un autre thread fait LATIN->fontBar alors ca aura mauvais effet...
*/

public class ExternalFontManager {

	public static final class FontSSSF {

		private final String sansserif;
		private final String serif;

		public FontSSSF(final String sansserif, final String serif) {
			this.sansserif = sansserif;
			this.serif = serif;
		}

		public String getSS() {
			return sansserif;
		}

		public String getSF() {
			return serif;
		}
	}

	private final Map<Character.UnicodeBlock, FontSSSF> map = new HashMap<>();
	private FontSSSF latin = null;

	private static final ExternalFontManager instance = new ExternalFontManager();

	private ExternalFontManager() {
	}

	public static ExternalFontManager get() {
		return instance;
	}

	public boolean isRegisteredBlock(final Character.UnicodeBlock block) {
		if (block.equals(Character.UnicodeBlock.BASIC_LATIN)) {
			return latin != null;
		}
		return map.containsKey(block);
	}

	public FontSSSF getExternalFont(final Character.UnicodeBlock block) {
		if (block.equals(Character.UnicodeBlock.BASIC_LATIN)) {
			return latin;
		}
		FontSSSF info = map.get(block);
		if (info == null) {
			info = new FontSSSF("SansSerif", "Serif");
			map.put(block, info);
		}

		return info;
	}

	// TODO: pas terrible cette fonction... (pareil pr le nom FontSSSF)
	public FontSSSF getFont(final Character.UnicodeBlock block) {
		if (block.equals(Character.UnicodeBlock.BASIC_LATIN)) {
			return latin;
		}
		return map.get(block);
	}

	public FontSSSF getBasicLatinFont() {
		return latin;
	}

	public void registerExternalFont(final Character.UnicodeBlock block,
			final String sansserif, final String serif) {
		if (sansserif == null && serif == null) {
			if (block.equals(Character.UnicodeBlock.BASIC_LATIN)) {
				latin = null;
			} else {
				map.remove(block);
			}
		} else {
			FontSSSF f = new FontSSSF(sansserif, serif);
			if (block.equals(Character.UnicodeBlock.BASIC_LATIN)) {
				latin = f;
			} else {
				map.put(block, f);
			}
		}
	}

	public void put(final Character.UnicodeBlock block, final FontSSSF f) {
		if (block.equals(Character.UnicodeBlock.BASIC_LATIN)) {
			latin = f;
		} else {
			map.put(block, f);
		}
	}

	public void registerExternalFont(Character.UnicodeBlock block,
			String fontName) {
		registerExternalFont(block, fontName, fontName);
	}

}
