/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.algos;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.geos.properties.FillType;

public class ChartStyle {

	private final Map<Integer, HashMap<Integer, Object>> tags = new HashMap<>();
	private final int[] colorOrder;

	public ChartStyle(int[] colorOrder) {
		this.colorOrder = colorOrder;
	}

	private void setIntTag(int i, int angle, int numBar) {
		if (angle == -1) {
			if (tags.containsKey(numBar)) {
				tags.get(numBar).remove(i);
			}
			return;
		}
		if (tags.containsKey(numBar)) {
			tags.get(numBar).put(i, angle);
		} else {
			HashMap<Integer, Object> hm = new HashMap<>();
			hm.put(i, angle);
			tags.put(numBar, hm);
		}
	}

	private void setTag(int key, Object value, int numBar) {
		if (value == null) {
			if (tags.containsKey(numBar)) {
				tags.get(numBar).remove(key);
			}
			return;
		}
		if (tags.containsKey(numBar)) {
			tags.get(numBar).put(key, value);
		} else {
			HashMap<Integer, Object> hm = new HashMap<>();
			hm.put(key, value);
			tags.put(numBar, hm);
		}
	}

	private int getIntTag(int i, int numBar) {
		HashMap<Integer, Object> map = tags.get(numBar);
		return map == null || map.get(i) == null ? -1 : (Integer) map.get(i);
	}

	/**
	 * @param color
	 *            fill color
	 * @param numBar
	 *            bar index
	 */
	public void setBarColor(GColor color, int numBar) {
		setTag(0, color, numBar);
	}

	/**
	 * @param numBar
	 *            bar index
	 * @return fill color
	 */
	public GColor getBarColor(int numBar) {
		HashMap<Integer, Object> hm = tags.get(numBar);
		GColor color = null;
		if (hm != null) {
			color =  (GColor) hm.get(0);
		}
		if (color != null) {
			return color;
		}
		if (colorOrder != null) {
			int colorRgb = colorOrder[(numBar - 1) % colorOrder.length];
			GColor baseColor = GColor.newColorRGB(colorRgb);
			int overlay = (numBar - 1) / colorOrder.length;
			return GColor.mixColors(GColor.WHITE, baseColor,
					Math.pow(0.6, overlay), 255);
		}
		return null;
	}

	/**
	 * @param alpha
	 *            fill opacity
	 * @param numBar
	 *            bar index
	 */
	public void setBarAlpha(double alpha, int numBar) {
		if (alpha == -1) {
			if (tags.containsKey(numBar)) {
				tags.get(numBar).remove(1);
			}
			return;
		}
		if (tags.containsKey(numBar)) {
			tags.get(numBar).put(1, alpha);
		} else {
			HashMap<Integer, Object> hm = new HashMap<>();
			hm.put(1, alpha);
			tags.put(numBar, hm);
		}
	}

	/**
	 * @param numBar
	 *            bar number
	 * @return -1 if not set, otherwise alpha (between 0 and 1)
	 */
	public double getBarAlpha(int numBar) {
		HashMap<Integer, Object> hm = tags.get(numBar);
		if (hm != null && hm.get(1) != null) {
			return (Double) hm.get(1);
		}
		return -1;
	}

	/**
	 * @param fillType
	 *            fill type
	 * @param numBar
	 *            bar index
	 */
	public void setBarFillType(FillType fillType, int numBar) {
		setTag(2, fillType, numBar);
	}

	/**
	 * @param numBar bar index
	 * @return bar fill type
	 */
	public FillType getBarFillType(int numBar) {
		return getBarFillType(numBar, FillType.STANDARD);
	}

	/**
	 * @param numBar
	 *            bar index
	 * @param fallback
	 *            fallback
	 * @return fill type
	 */
	public FillType getBarFillType(int numBar, FillType fallback) {
		HashMap<Integer, Object> hm = tags.get(numBar);
		if (hm != null) {
			if (hm.get(2) == null) {
				return fallback;
			}
			return (FillType) hm.get(2);
		}
		return fallback;
	}

	/**
	 * @param symbol
	 *            fill symbol
	 * @param numBar
	 *            bar index
	 */
	public void setBarSymbol(String symbol, int numBar) {
		setTag(3, symbol, numBar);
	}

	/**
	 * @param numBar
	 *            bar index
	 * @return filling symbol
	 */
	public String getBarSymbol(int numBar) {
		HashMap<Integer, Object> hm = tags.get(numBar);
		if (hm != null) {
			return (String) hm.get(3);
		}
		return null;
	}

	/**
	 * @param image
	 *            image filename
	 * @param numBar
	 *            bar index
	 */
	public void setBarImage(String image, int numBar) {
		setTag(4, image, numBar);
	}

	/**
	 * @param numBar
	 *            bar index
	 * @return image filename
	 */
	public String getBarImage(int numBar) {
		HashMap<Integer, Object> hm = tags.get(numBar);
		if (hm != null) {
			return (String) hm.get(4);
		}
		return null;
	}

	/**
	 * @param distance
	 *            hatch distance
	 * @param numBar
	 *            bar index
	 */
	public void setBarHatchDistance(int distance, int numBar) {
		setIntTag(5, distance, numBar);
	}

	/**
	 * @param numBar
	 *            bar index
	 * @return hatching distance
	 */
	public int getBarHatchDistance(int numBar) {
		return getIntTag(5, numBar);
	}

	/**
	 * @param angle
	 *            hatching angle
	 * @param numBar
	 *            bar index
	 */
	public void setBarHatchAngle(int angle, int numBar) {
		setIntTag(6, angle, numBar);
	}

	/**
	 * @param numBar
	 *            bar index
	 * @return hatching angle
	 */
	public int getBarHatchAngle(int numBar) {
		return getIntTag(6, numBar);
	}

	/**
	 * Appends description of a bar to string builder.
	 *
	 * @param sb XML string builder
	 * @param count number of bars
	 */
	public void barXML(XMLStringBuilder sb, int count) {
		sb.startOpeningTag("tags", 1).endTag();
		for (int i = 1; i <= count; i++) {
			if (getBarColor(i) != null) {
				startTag(sb, "barColor", i)
						.attr("value", GColor.getColorString(getBarColor(i))).endTag();
			}

			double barAlpha = getBarAlpha(i);
			if (barAlpha != -1) {
				startTag(sb, "barAlpha", i).attr("value", barAlpha).endTag();
			}
			if (getBarHatchDistance(i) != -1) {
				startTag(sb, "barHatchDistance", i).attr("value", getBarHatchDistance(i)).endTag();
			}
			if (getBarHatchAngle(i) != -1) {
				startTag(sb, "barHatchAngle", i).attr("value", getBarHatchAngle(i)).endTag();
			}
			if (getBarFillType(i) != FillType.STANDARD) {
				startTag(sb, "barFillType", i).attr("value", getBarFillType(i).ordinal()).endTag();
			}
			if (getBarImage(i) != null) {
				startTag(sb, "barImage", i).attr("value", getBarImage(i)).endTag();
			}
			if (getBarSymbol(i) != null) {
				startTag(sb, "barSymbol", i).attr("value", getBarSymbol(i)).endTag();
			}
		}
		sb.closeTag("tags");
	}

	private XMLStringBuilder startTag(XMLStringBuilder sb, String type, int i) {
		return sb.startTag("tag", 2).attr("key", type).attr("barNumber", i);
	}
}
