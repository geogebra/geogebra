package org.geogebra.desktop.main;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author michael
 *
 */
public class GeoGebraPreferencesXML {

	/**
	 * these can get changed by --screenDPI (and maybe by --screenResX,
	 * --screenResY)
	 */
	private static int defaultFontSize = 12;
	private static int defaultWindowX = 800;
	private static int defaultWindowY = 600;

	private static NavigableMap<Integer, Integer> validFontSizes = null;

	/**
	 * calculate the default font size and according to some heuristics
	 * 
	 * @param screenDPI
	 *            eg 96 for regular screen
	 *            https://technet.microsoft.com/en-GB/library/dn528846.aspx
	 * @param screenResX
	 *            horizontal screen size
	 * @param screenResY
	 *            vertical screen size
	 */
	public static void setDefaults(int screenDPI, int screenResX,
			int screenResY) {

		int fontSize = (int) Math.round(screenDPI / 8.0);

		defaultFontSize = getLegalFontSize(fontSize);

		// 96 corresponds to 100%
		// 192 to 200%
		double sf = screenDPI / 96.0;
		defaultWindowX = (int) (800.0 * sf);
		defaultWindowY = (int) (600.0 * sf);
	}

	private static int getLegalFontSize(int fontSize) {

		if (validFontSizes == null) {
			validFontSizes = new ConcurrentSkipListMap<Integer, Integer>();

			int[] fontSizes = { 12, 14, 16, 18, 20, 24, 28, 32, 48 };

			for (int i = 0; i < fontSizes.length; i++) {
				validFontSizes.put(fontSizes[i], i);
			}
		}

		Entry<Integer, Integer> entry = validFontSizes.floorEntry(fontSize);

		if (entry == null) {
			// less than 12, return 12
			entry = validFontSizes.ceilingEntry(fontSize);
		}

		return entry.getKey();
	}

	/**
	 * @return defaults as XML
	 */
	static String getXML() {

		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<geogebra format=\"5.0\" xsi:noNamespaceSchemaLocation=\"http://www.geogebra.org/ggb.xsd\" xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >"
				+ "<gui>" + "<window width=\""

				// dynamic bit!
				+ defaultWindowX
				+ "\" height=\""

				// dynamic bit!
				+ defaultWindowY

				+ "\" />"
				+ "<settings ignoreDocument=\"false\" showTitleBar=\"true\" />"
				+ "<labelingStyle  val=\"0\"/>"
				+ "<mouse reverseWheel=\"true\"/>"
				+ "<font  size=\""

				// dynamic bit!
				+ defaultFontSize

				+ "\"/>"
				+ "<menuFont size=\"-1\"/>"
				+ "<tooltipSettings language=\"\" timeout=\"0\"/>"
				+ "<graphicsSettings javaLatexFonts=\"false\"/>"
				+ "</gui>"
				+ "<euclidianView>"
				+ "<size  width=\"640\" height=\"480\"/>"
				+ "<coordSystem xZero=\"215.0\" yZero=\"315.0\" scale=\"50.0\" yscale=\"50.0\"/>"
				+ "<evSettings axes=\"true\" grid=\"false\" gridIsBold=\"false\" pointCapturing=\"3\" rightAngleStyle=\"2\" checkboxSize=\"26\" gridType=\"0\"/>"
				+ "<bgColor r=\"255\" g=\"255\" b=\"255\"/>"
				+ "<axesColor r=\"0\" g=\"0\" b=\"0\"/>"
				+ "<gridColor r=\"192\" g=\"192\" b=\"192\"/>"
				+ "<lineStyle axes=\"1\" grid=\"0\"/>"
				+ "<axis id=\"0\" show=\"true\" label=\"\" unitLabel=\"\" tickStyle=\"1\" showNumbers=\"true\" axisCross=\"0.0\" positiveAxis=\"false\"/>"
				+ "<axis id=\"1\" show=\"true\" label=\"\" unitLabel=\"\" tickStyle=\"1\" showNumbers=\"true\" axisCross=\"0.0\" positiveAxis=\"false\"/>"
				+ "</euclidianView>" + "<kernel>"
				+ "<continuous val=\"false\"/>" + "<decimals val=\"2\"/>"
				+ "<angleUnit val=\"degree\"/>" + "<algebraStyle val=\"0\"/>"
				+ "<coordStyle val=\"0\"/>"
				+ "<localization digits=\"false\" labels=\"true\"/>"
				+ "<angleFromInvTrig val=\"false\"/>"
				+ "<casSettings timeout=\"5\" expRoots=\"true\"/>"
				+ "</kernel>" + "<scripting blocked=\"false\"/>"
				+ "</geogebra>";
	}

}
