package org.geogebra.common.main;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;

/**
 * @author michael
 *
 */
public class GeoGebraPreferencesXML {

	/**
	 * these can get changed by --screenDPI (and maybe by --screenResX,
	 * --screenResY)
	 */
	private static int defaultFontSize = 16;
	private static int defaultWindowX = 800;
	private static int defaultWindowY = 600;

	/** Angle unit: radians */
	public final static String ANGLE_RADIANT_XML_NAME = "radiant";
	/** Angle unit: degrees */
	public final static String ANGLE_DEGREE_XML_NAME = "degree";
	/** Angle unit: degrees, minutes, seconds */
	public final static String ANGLE_DEGREES_MINUTES_SECONDS_XML_NAME = "degreesMinutesSeconds";

	/**
	 * @param app
	 *            application
	 * @return defaults as XML
	 */
	public static String getXML(App app) {

		int rightAngleStyle = app.getLocalization().getRightAngleStyle();
		boolean showAxes = app.getConfig().showAxesOnFileNew();
		boolean showGrid = app.getConfig().showGridOnFileNew();

		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<geogebra format=\"5.0\" xsi:noNamespaceSchemaLocation=\"http://www.geogebra.org/ggb.xsd\" xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >"
				+ "<gui>"

				+ "<window width=\""

				// dynamic bit!
				+ defaultWindowX + "\" height=\""

				// dynamic bit!
				+ defaultWindowY

				+ "\" />"
				+ "<settings ignoreDocument=\"false\" showTitleBar=\"true\" />"
				+ "<labelingStyle val=\""

				+ app.getConfig().getDefaultLabelingStyle().getValue()
				+ "\"/>"
				+ "<font  size=\""

				// dynamic bit!
				+ defaultFontSize

				+ "\"/>" + "<menuFont size=\"-1\"/>"
				+ "<tooltipSettings language=\"\" timeout=\"0\"/>" + "</gui>"
				+ "<euclidianView>"

				+ "<size width=\"640\" height=\"480\"/>"
				+ "<coordSystem xZero=\"215.0\" yZero=\"315.0\" scale=\"50.0\" yscale=\"50.0\"/>"
				+ "<evSettings axes=\"" + showAxes + "\" grid=\"" + showGrid
				+ "\" gridIsBold=\"false\" pointCapturing=\"3\" rightAngleStyle=\""

				// dynamic
				+ rightAngleStyle

				+ "\" checkboxSize=\"26\" gridType=\""
				+ +EuclidianView.GRID_CARTESIAN_WITH_SUBGRID + "\"/>"
				+ "<bgColor r=\"255\" g=\"255\" b=\"255\"/>"
				+ getAxesColorTag()
				+ "<gridColor r=\"192\" g=\"192\" b=\"192\"/>"
				+ "<lineStyle axes=\"1\" grid=\"0\"/>"
				+ "<axis id=\"0\" show=\"" + showAxes
				+ "\" label=\"\" unitLabel=\"\" tickStyle=\"1\" showNumbers=\"true\""
				+ " axisCross=\"0.0\" positiveAxis=\"false\"/>"
				+ "<axis id=\"1\" show=\"" + showAxes
				+ "\" label=\"\" unitLabel=\"\" tickStyle=\"1\" showNumbers=\"true\""
				+ " axisCross=\"0.0\" positiveAxis=\"false\"/>"
				+ "</euclidianView>"

				+ "<euclidianView3D>\r\n"
				+ "  <coordSystem xZero=\"0.0\" yZero=\"0.0\" zZero=\"-1.5\""
				+ "  scale=\"50.0\" xAngle=\"20.0\" zAngle=\"-60.0\"/>\r\n"
				+ "  <evSettings axes=\"true\" grid=\"false\" gridIsBold=\"false\""
				+ " pointCapturing=\"3\" rightAngleStyle=\"1\" gridType=\"3\"/>\r\n"
				+ "  <axis id=\"0\" show=\"true\" label=\"x\" unitLabel=\"\""
				+ " tickStyle=\"1\" showNumbers=\"true\"/>\r\n"
				+ "  <axis id=\"1\" show=\"true\" label=\"y\" unitLabel=\"\""
				+ " tickStyle=\"1\" showNumbers=\"true\"/>\r\n"
				+ "  <axis id=\"2\" show=\"true\" label=\"z\" unitLabel=\"\""
				+ " tickStyle=\"1\" showNumbers=\"true\"/>\r\n"
				+ "  <plate show=\"true\"/>\r\n"
				+ "  <bgColor r=\"255\" g=\"255\" b=\"255\"/>\r\n"
				+ "  <clipping use=\"false\" show=\"false\" size=\"1\"/>\r\n"
				+ "  <projection type=\"0\"/>\r\n" + "</euclidianView3D>"

				+ "<kernel>" + "<continuous val=\"false\"/>"

				+ "<decimals val=\""
				+ app.getConfig().getDefaultPrintDecimals()
				+ "\"/>"

				+ "<angleUnit val=\""
				+ getDefaultAngleUnit(app)
				+ "\"/>"

				+ "<algebraStyle val=\""

				// dynamic bit!
				+ app.getConfig().getDefaultAlgebraStyle()

				+ "\"/>"

				+ "<coordStyle val=\"0\"/>"
				+ "<localization digits=\"false\" labels=\"true\"/>"
				+ "<angleFromInvTrig val=\"false\"/>"
				+ "<casSettings timeout=\"5\" expRoots=\"true\"/>"

				+ "</kernel>"
				+ "<algebraView><mode val=\"3\"/></algebraView>"
				+ "<scripting blocked=\"false\"/>" + "</geogebra>";
	}

	private static String getAxesColorTag() {
		GColor color = GColor.DEFAULT_AXES_COLOR;
		return  "<axesColor "
				+ "r=\"" + color.getRed() + "\" "
				+ "g=\"" + color.getGreen() + "\" "
				+ "b=\"" + color.getBlue() + "\" "
				+ "/> ";
	}

	private static String getDefaultAngleUnit(App app) {
		switch (app.getConfig().getDefaultAngleUnit()) {
			case Kernel.ANGLE_RADIANT:
				return ANGLE_RADIANT_XML_NAME;
			case Kernel.ANGLE_DEGREES_MINUTES_SECONDS:
				return ANGLE_DEGREES_MINUTES_SECONDS_XML_NAME;
			case Kernel.ANGLE_DEGREE:
			default:
				return ANGLE_DEGREE_XML_NAME;
		}
	}

	/**
	 * @param fontSize
	 *            font size
	 */
	public static void setDefaultFontSize(int fontSize) {
		defaultFontSize = fontSize;

	}

	/**
	 * @param width
	 *            window width
	 */
	public static void setDefaultWindowX(int width) {
		defaultWindowX = width;
	}

	/**
	 * @param height
	 *            whindow height
	 */
	public static void setDefaultWindowY(int height) {
		defaultWindowY = height;

	}
}
