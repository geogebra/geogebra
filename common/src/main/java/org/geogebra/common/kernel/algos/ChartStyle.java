package org.geogebra.common.kernel.algos;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.properties.FillType;

public class ChartStyle {

	private Map<Integer, HashMap<Integer, Object>> tags = new HashMap<>();

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

	private Object getTag(int i, int numBar) {
		return tags.get(numBar) == null ? null : tags.get(numBar).get(i);
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
		int[] order = new int[]{0x6557d2, 0xe0bf00, 0x3bb4a6, 0xda6a9d, 0x3b1c32, 0xff8c70};
		GColor baseColor = GColor.newColorRGB(order[numBar % 6]);
		double overlay = Math.pow(0.6, Math.floor(numBar / 6.0));
		return GColor.mixColors(GColor.WHITE, baseColor,
				overlay, 255);
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
	 *
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
		HashMap<Integer, Object> hm = tags.get(numBar);
		if (hm != null && hm.get(5) != null) {
			return ((Integer) hm.get(5)).intValue();
		}
		return -1;
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
		Object tag = getTag(6, numBar);
		if (tag != null) {
			return (Integer) tag;
		}
		return -1;
	}

	/**
	 * Appends description of a bar to string builder.
	 *
	 * @param sb
	 *            XML string builder
	 */
	public void barXml(StringBuilder sb, int N) {
		sb.append("\t<tags>\n");
		for (int i = 1; i <= N; i++) {
			if (getBarColor(i) != null) {
				sb.append("\t\t<tag key=\"barColor\"");
				sb.append(" barNumber=\"");
				sb.append(i);
				sb.append("\" value=\"");
				sb.append(GColor.getColorString(getBarColor(i)));
				sb.append("\" />\n");
			}

			double barAlpha = getBarAlpha(i);
			if (barAlpha != -1) {
				sb.append("\t\t<tag key=\"barAlpha\" barNumber=\"");
				sb.append(i);
				sb.append("\" value=\"");
				sb.append(barAlpha);
				sb.append("\"/>\n");
			}
			if (getBarHatchDistance(i) != -1) {
				sb.append("\t\t<tag key=\"barHatchDistance\" barNumber=\"");
				sb.append(i);
				sb.append("\" value=\"");
				sb.append(getBarHatchDistance(i));
				sb.append("\"/>\n");
			}
			if (getBarHatchAngle(i) != -1) {
				sb.append("\t\t<tag key=\"barHatchAngle\" barNumber=\"");
				sb.append(i);
				sb.append("\" value=\"");
				sb.append(getBarHatchAngle(i));
				sb.append("\"/>\n");
			}
			if (getBarFillType(i) != FillType.STANDARD) {
				sb.append("\t\t<tag key=\"barFillType\" barNumber=\"");
				sb.append(i);
				sb.append("\" value=\"");
				sb.append(getBarFillType(i).ordinal());
				sb.append("\"/>\n");
			}
			if (getBarImage(i) != null) {
				sb.append("\t\t<tag key=\"barImage\" barNumber=\"");
				sb.append(i);
				sb.append("\" value=\"");
				sb.append(getBarImage(i));
				sb.append("\"/>\n");
			}
			if (getBarSymbol(i) != null) {
				sb.append("\t\t<tag key=\"barSymbol\" barNumber=\"");
				sb.append(i);
				sb.append("\" value=\"");
				sb.append(getBarSymbol(i));
				sb.append("\"/>\n");
			}
		}
		sb.append("\t</tags>\n");
	}
}
