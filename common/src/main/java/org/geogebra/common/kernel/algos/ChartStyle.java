package org.geogebra.common.kernel.algos;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.awt.GColor;
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
	 * @param sb
	 *            XML string builder
	 */
	public void barXml(StringBuilder sb, int N) {
		sb.append("\t<tags>\n");
		for (int i = 1; i <= N; i++) {
			if (getBarColor(i) != null) {
				appendTag(sb, "barColor", i, GColor.getColorString(getBarColor(i)));
			}

			double barAlpha = getBarAlpha(i);
			if (barAlpha != -1) {
				appendTag(sb, "barAlpha", i, barAlpha);
			}
			if (getBarHatchDistance(i) != -1) {
				appendTag(sb, "barHatchDistance", i, getBarHatchDistance(i));
			}
			if (getBarHatchAngle(i) != -1) {
				appendTag(sb, "barHatchAngle", i, getBarHatchAngle(i));
			}
			if (getBarFillType(i) != FillType.STANDARD) {
				appendTag(sb, "barFillType", i, getBarFillType(i).ordinal());
			}
			if (getBarImage(i) != null) {
				appendTag(sb, "barImage", i, getBarImage(i));
			}
			if (getBarSymbol(i) != null) {
				appendTag(sb, "barSymbol", i, getBarSymbol(i));
			}
		}
		sb.append("\t</tags>\n");
	}

	private void appendTag(StringBuilder sb, String type, int i, Object val) {
		sb.append("\t\t<tag key=\"").append(type);
		sb.append("\" barNumber=\"").append(i);
		sb.append("\" value=\"").append(val);
		sb.append("\"/>\n");
	}
}
