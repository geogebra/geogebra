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
 * Created on January 26, 2004, 4:34 AM
 */

package com.kitfox.svg.xml;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class ColorTable {

	static final Map colorTable;
	static {
		HashMap table = new HashMap();

		// We really should be interpreting the currentColor keyword as
		// a reference to the referring node's color, but this quick hack
		// will stop the program from crashing.
		table.put("currentcolor", new Color(0x0));

		table.put("aliceblue", new Color(0xf0f8ff));
		table.put("antiquewhite", new Color(0xfaebd7));
		table.put("aqua", new Color(0x00ffff));
		table.put("aquamarine", new Color(0x7fffd4));
		table.put("azure", new Color(0xf0ffff));
		table.put("beige", new Color(0xf5f5dc));
		table.put("bisque", new Color(0xffe4c4));
		table.put("black", new Color(0x000000));
		table.put("blanchedalmond", new Color(0xffebcd));
		table.put("blue", new Color(0x0000ff));
		table.put("blueviolet", new Color(0x8a2be2));
		table.put("brown", new Color(0xa52a2a));
		table.put("burlywood", new Color(0xdeb887));
		table.put("cadetblue", new Color(0x5f9ea0));
		table.put("chartreuse", new Color(0x7fff00));
		table.put("chocolate", new Color(0xd2691e));
		table.put("coral", new Color(0xff7f50));
		table.put("cornflowerblue", new Color(0x6495ed));
		table.put("cornsilk", new Color(0xfff8dc));
		table.put("crimson", new Color(0xdc143c));
		table.put("cyan", new Color(0x00ffff));
		table.put("darkblue", new Color(0x00008b));
		table.put("darkcyan", new Color(0x008b8b));
		table.put("darkgoldenrod", new Color(0xb8860b));
		table.put("darkgray", new Color(0xa9a9a9));
		table.put("darkgreen", new Color(0x006400));
		table.put("darkkhaki", new Color(0xbdb76b));
		table.put("darkmagenta", new Color(0x8b008b));
		table.put("darkolivegreen", new Color(0x556b2f));
		table.put("darkorange", new Color(0xff8c00));
		table.put("darkorchid", new Color(0x9932cc));
		table.put("darkred", new Color(0x8b0000));
		table.put("darksalmon", new Color(0xe9967a));
		table.put("darkseagreen", new Color(0x8fbc8f));
		table.put("darkslateblue", new Color(0x483d8b));
		table.put("darkslategray", new Color(0x2f4f4f));
		table.put("darkturquoise", new Color(0x00ced1));
		table.put("darkviolet", new Color(0x9400d3));
		table.put("deeppink", new Color(0xff1493));
		table.put("deepskyblue", new Color(0x00bfff));
		table.put("dimgray", new Color(0x696969));
		table.put("dodgerblue", new Color(0x1e90ff));
		table.put("feldspar", new Color(0xd19275));
		table.put("firebrick", new Color(0xb22222));
		table.put("floralwhite", new Color(0xfffaf0));
		table.put("forestgreen", new Color(0x228b22));
		table.put("fuchsia", new Color(0xff00ff));
		table.put("gainsboro", new Color(0xdcdcdc));
		table.put("ghostwhite", new Color(0xf8f8ff));
		table.put("gold", new Color(0xffd700));
		table.put("goldenrod", new Color(0xdaa520));
		table.put("gray", new Color(0x808080));
		table.put("green", new Color(0x008000));
		table.put("greenyellow", new Color(0xadff2f));
		table.put("honeydew", new Color(0xf0fff0));
		table.put("hotpink", new Color(0xff69b4));
		table.put("indianred", new Color(0xcd5c5c));
		table.put("indigo", new Color(0x4b0082));
		table.put("ivory", new Color(0xfffff0));
		table.put("khaki", new Color(0xf0e68c));
		table.put("lavender", new Color(0xe6e6fa));
		table.put("lavenderblush", new Color(0xfff0f5));
		table.put("lawngreen", new Color(0x7cfc00));
		table.put("lemonchiffon", new Color(0xfffacd));
		table.put("lightblue", new Color(0xadd8e6));
		table.put("lightcoral", new Color(0xf08080));
		table.put("lightcyan", new Color(0xe0ffff));
		table.put("lightgoldenrodyellow", new Color(0xfafad2));
		table.put("lightgrey", new Color(0xd3d3d3));
		table.put("lightgreen", new Color(0x90ee90));
		table.put("lightpink", new Color(0xffb6c1));
		table.put("lightsalmon", new Color(0xffa07a));
		table.put("lightseagreen", new Color(0x20b2aa));
		table.put("lightskyblue", new Color(0x87cefa));
		table.put("lightslateblue", new Color(0x8470ff));
		table.put("lightslategray", new Color(0x778899));
		table.put("lightsteelblue", new Color(0xb0c4de));
		table.put("lightyellow", new Color(0xffffe0));
		table.put("lime", new Color(0x00ff00));
		table.put("limegreen", new Color(0x32cd32));
		table.put("linen", new Color(0xfaf0e6));
		table.put("magenta", new Color(0xff00ff));
		table.put("maroon", new Color(0x800000));
		table.put("mediumaquamarine", new Color(0x66cdaa));
		table.put("mediumblue", new Color(0x0000cd));
		table.put("mediumorchid", new Color(0xba55d3));
		table.put("mediumpurple", new Color(0x9370d8));
		table.put("mediumseagreen", new Color(0x3cb371));
		table.put("mediumslateblue", new Color(0x7b68ee));
		table.put("mediumspringgreen", new Color(0x00fa9a));
		table.put("mediumturquoise", new Color(0x48d1cc));
		table.put("mediumvioletred", new Color(0xc71585));
		table.put("midnightblue", new Color(0x191970));
		table.put("mintcream", new Color(0xf5fffa));
		table.put("mistyrose", new Color(0xffe4e1));
		table.put("moccasin", new Color(0xffe4b5));
		table.put("navajowhite", new Color(0xffdead));
		table.put("navy", new Color(0x000080));
		table.put("oldlace", new Color(0xfdf5e6));
		table.put("olive", new Color(0x808000));
		table.put("olivedrab", new Color(0x6b8e23));
		table.put("orange", new Color(0xffa500));
		table.put("orangered", new Color(0xff4500));
		table.put("orchid", new Color(0xda70d6));
		table.put("palegoldenrod", new Color(0xeee8aa));
		table.put("palegreen", new Color(0x98fb98));
		table.put("paleturquoise", new Color(0xafeeee));
		table.put("palevioletred", new Color(0xd87093));
		table.put("papayawhip", new Color(0xffefd5));
		table.put("peachpuff", new Color(0xffdab9));
		table.put("peru", new Color(0xcd853f));
		table.put("pink", new Color(0xffc0cb));
		table.put("plum", new Color(0xdda0dd));
		table.put("powderblue", new Color(0xb0e0e6));
		table.put("purple", new Color(0x800080));
		table.put("red", new Color(0xff0000));
		table.put("rosybrown", new Color(0xbc8f8f));
		table.put("royalblue", new Color(0x4169e1));
		table.put("saddlebrown", new Color(0x8b4513));
		table.put("salmon", new Color(0xfa8072));
		table.put("sandybrown", new Color(0xf4a460));
		table.put("seagreen", new Color(0x2e8b57));
		table.put("seashell", new Color(0xfff5ee));
		table.put("sienna", new Color(0xa0522d));
		table.put("silver", new Color(0xc0c0c0));
		table.put("skyblue", new Color(0x87ceeb));
		table.put("slateblue", new Color(0x6a5acd));
		table.put("slategray", new Color(0x708090));
		table.put("snow", new Color(0xfffafa));
		table.put("springgreen", new Color(0x00ff7f));
		table.put("steelblue", new Color(0x4682b4));
		table.put("tan", new Color(0xd2b48c));
		table.put("teal", new Color(0x008080));
		table.put("thistle", new Color(0xd8bfd8));
		table.put("tomato", new Color(0xff6347));
		table.put("turquoise", new Color(0x40e0d0));
		table.put("violet", new Color(0xee82ee));
		table.put("violetred", new Color(0xd02090));
		table.put("wheat", new Color(0xf5deb3));
		table.put("white", new Color(0xffffff));
		table.put("whitesmoke", new Color(0xf5f5f5));
		table.put("yellow", new Color(0xffff00));
		table.put("yellowgreen", new Color(0x9acd32));

		colorTable = Collections.unmodifiableMap(table);
	}

	static ColorTable singleton = new ColorTable();

	/** Creates a new instance of ColorTable */
	protected ColorTable() {
		// buildColorList();
	}

	static public ColorTable instance() {
		return singleton;
	}

	public Color lookupColor(String name) {
		Object obj = colorTable.get(name.toLowerCase());
		if (obj == null) {
			return null;
		}

		return (Color) obj;
	}

	public static Color parseColor(String val) {
		Color retVal = null;

		if ("".equals(val)) {
			return null;
		}

		if (val.charAt(0) == '#') {
			String hexStrn = val.substring(1);

			if (hexStrn.length() == 3) {
				hexStrn = "" + hexStrn.charAt(0) + hexStrn.charAt(0)
						+ hexStrn.charAt(1) + hexStrn.charAt(1)
						+ hexStrn.charAt(2) + hexStrn.charAt(2);
			}
			int hexVal = parseHex(hexStrn);

			retVal = new Color(hexVal);
		} else {
			final String number = "\\s*(((\\d+)(\\.\\d*)?)|(\\.\\d+))(%)?\\s*";
			final Matcher rgbMatch = Pattern.compile(
					"rgb\\(" + number + "," + number + "," + number + "\\)",
					Pattern.CASE_INSENSITIVE).matcher("");

			rgbMatch.reset(val);
			if (rgbMatch.matches()) {
				float rr = Float.parseFloat(rgbMatch.group(1));
				float gg = Float.parseFloat(rgbMatch.group(7));
				float bb = Float.parseFloat(rgbMatch.group(13));
				rr /= "%".equals(rgbMatch.group(6)) ? 100 : 255;
				gg /= "%".equals(rgbMatch.group(12)) ? 100 : 255;
				bb /= "%".equals(rgbMatch.group(18)) ? 100 : 255;
				retVal = new Color(rr, gg, bb);
			} else {
				Color lookupCol = ColorTable.instance().lookupColor(val);
				if (lookupCol != null) {
					retVal = lookupCol;
				}
			}
		}

		return retVal;
	}

	public static int parseHex(String val) {
		int retVal = 0;

		for (int i = 0; i < val.length(); i++) {
			retVal <<= 4;

			char ch = val.charAt(i);
			if (ch >= '0' && ch <= '9') {
				retVal |= ch - '0';
			} else if (ch >= 'a' && ch <= 'z') {
				retVal |= ch - 'a' + 10;
			} else if (ch >= 'A' && ch <= 'Z') {
				retVal |= ch - 'A' + 10;
			} else {
				throw new RuntimeException();
			}
		}

		return retVal;
	}

}
