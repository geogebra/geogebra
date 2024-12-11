package org.geogebra.common.io.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Helper class to convert strings like "CA/G" into perspectives
 * 
 * @author Zbynek
 *
 */
public class PerspectiveDecoder {

	private static final double GEOMETRY_PORTRAIT_RATIO = 0.6;
	private static final int INPUT_ROW_HEIGHT = 80;
	private static final int AV_ROWS_IN_PORTRAIT = 5;
	private static final double MIN_TOOLBAR_WIDTH = 240;
	private static final double MAX_TOOLBAR_WIDTH = 380;
	private static final double DEFAULT_TOOLBAR_RATIO = 0.4;

	private static Map<String, DockPanelData> viewCodes = new HashMap<>();

	static {
		viewCodes
				.put("G",
						new DockPanelData(App.VIEW_EUCLIDIAN, null, true,
								false, false, AwtFactory.getPrototype()
										.newRectangle(100, 100, 600, 400),
								"1", 500));
		viewCodes
				.put("A",
						new DockPanelData(App.VIEW_ALGEBRA, null, false,
								false, false, AwtFactory.getPrototype()
										.newRectangle(100, 100, 250, 400),
								"3,3", 200));
		viewCodes
				.put("Algebra",
						new DockPanelData(App.VIEW_ALGEBRA, null, false,
								false, false, AwtFactory.getPrototype()
										.newRectangle(100, 100, 250, 400),
								"3,3", 200));
		viewCodes
				.put("Tools",
						new DockPanelData(App.VIEW_TOOLS, null, false,
								false, false, AwtFactory.getPrototype()
								.newRectangle(100, 100, 250, 400),
								"3,3", 200));
		viewCodes
				.put("Table",
						new DockPanelData(App.VIEW_TABLE, null, false,
								false, false, AwtFactory.getPrototype()
								.newRectangle(100, 100, 250, 400),
								"3,3", 200));
		viewCodes
				.put("SP",
						new DockPanelData(App.VIEW_SIDE_PANEL, null, false,
								false, false, AwtFactory.getPrototype()
								.newRectangle(100, 100, 250, 400),
								"3,3", 200));
		viewCodes
				.put("S",
						new DockPanelData(App.VIEW_SPREADSHEET, null, true,
								false, false, AwtFactory.getPrototype()
										.newRectangle(100, 100, 600, 400),
								"3", 300));
		viewCodes
				.put("C",
						new DockPanelData(App.VIEW_CAS, null, false,
								false, false, AwtFactory.getPrototype()
										.newRectangle(100, 100, 600, 400),
								"3,1", 300));
		viewCodes
				.put("P",
						new DockPanelData(App.VIEW_PROPERTIES, null, false,
								false, true, AwtFactory.getPrototype()
										.newRectangle(100, 100, 700, 550),
								"1,1", 400));

		viewCodes
				.put("L",
						new DockPanelData(App.VIEW_CONSTRUCTION_PROTOCOL, null,
								false,
								false, true, AwtFactory.getPrototype()
										.newRectangle(100, 100, 700, 550),
								"1,1", 400));
		viewCodes
				.put("D",
						new DockPanelData(App.VIEW_EUCLIDIAN2, null, false,
								false, true, AwtFactory.getPrototype()
										.newRectangle(100, 100, 700, 550),
								"1,1", 400));
		viewCodes
				.put("T",
						new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false,
								false, true, AwtFactory.getPrototype()
										.newRectangle(100, 100, 700, 550),
								"1,1", 400));
		viewCodes
				.put("B",
						new DockPanelData(App.VIEW_PROBABILITY_CALCULATOR, null,
								false,
								false, true, AwtFactory.getPrototype()
										.newRectangle(100, 100, 700, 550),
								"1,1", 400));
		viewCodes
				.put("R",
						new DockPanelData(App.VIEW_DATA_ANALYSIS, null, false,
								false, true, AwtFactory.getPrototype()
										.newRectangle(100, 100, 700, 550),
								"1,1", 400));
		viewCodes
				.put("F",
						new DockPanelData(App.VIEW_FUNCTION_INSPECTOR, null,
								false,
								false, true, AwtFactory.getPrototype()
										.newRectangle(100, 100, 700, 550),
								"1,1", 400));
	}

	/**
	 * @param app
	 *            application
	 * @param width
	 *            applet width
	 * @return preferred ratio of left panel for AV perspective
	 */
	public static double landscapeRatio(App app, double width) {

		double ratio = DEFAULT_TOOLBAR_RATIO;
		if (ratio * width < MIN_TOOLBAR_WIDTH) {
			return Math.min(MIN_TOOLBAR_WIDTH / width, 1);
		}
		if (ratio * width > MAX_TOOLBAR_WIDTH) {
			return MAX_TOOLBAR_WIDTH / width;
		}
		return ratio;
	}

	/**
	 * @param height
	 *            applet height
	 * @param graphing
	 *            whether this is for graphing
	 * @return preferred ratio for AV perspective in portrait mode.
	 */
	public static double portraitRatio(double height, boolean graphing) {
		if (graphing) {
			double avHeight = AV_ROWS_IN_PORTRAIT * INPUT_ROW_HEIGHT;
			return 1 - avHeight / height;
		}
		return GEOMETRY_PORTRAIT_RATIO;
	}

	/**
	 * 
	 * @param code
	 *            views encoded as G,A,S,C,P,L,D,T
	 * @param parser
	 *            parser
	 * @param defToolbar
	 *            toolbar definition string
	 * @return decoded perspective
	 */
	public static Perspective decode(String code, Parser parser,
			String defToolbar, Layout layout) {
		if (code.length() == 0 || code.startsWith("search:")) {
			return null;
		}
		Perspective defaultPerspective = getDefaultPerspective(code, layout);
		if (defaultPerspective != null) {
			return defaultPerspective;
		}
		StringBuilder longCode = new StringBuilder();
		for (int i = 0; i < code.length(); i++) {
			longCode.append(code.charAt(i));
			longCode.append(" ");
		}
		ExpressionValue expr;
		try {
			expr = parser.parseGeoGebraExpression(longCode.toString()).wrap();
		} catch (ParseException e) {
			Log.debug(e);
			return null;
		}
		ArrayList<DockPanelData> panelList = new ArrayList<>();
		ArrayList<DockSplitPaneData> splitList = new ArrayList<>();
		buildPerspective(expr.unwrap(), "", "", panelList, splitList, 1, 0.8);

		if (splitList.isEmpty()) {
			splitList.add(new DockSplitPaneData("", 1.0, 1));
		}
		DockSplitPaneData[] spData = new DockSplitPaneData[splitList.size()];
		splitList.toArray(spData);

		DockPanelData[] dpData = new DockPanelData[panelList.size()];
		panelList.toArray(dpData);

		return new Perspective(0, spData, dpData, defToolbar, true, false, true,
				true, true, InputPosition.algebraView);
	}

	/**
	 * @param code default ID as string
	 * @return default perspective with given defaultID
	 */
	public static Perspective getDefaultPerspective(String code, Layout layout) {
		for (int i = 0; i < layout.getDefaultPerspectivesLength(); i++) {
			Perspective defaultPerspective = layout.getDefaultPerspectives(i);
			if (defaultPerspective != null && code.equals(defaultPerspective.getDefaultID() + "")) {
				return defaultPerspective;
			}
		}
		Log.error("Perspective not found " + code);
		return null;
	}

	private static void buildPerspective(ExpressionValue expr, String panelPath,
			String splitPath, ArrayList<DockPanelData> panelList,
			ArrayList<DockSplitPaneData> splitList, double totalWidth,
			double totalHeight) {

		if (expr instanceof Variable) {
			String code = ((Variable) expr)
					.getName(StringTemplate.defaultTemplate);
			if (viewCodes.get(code) != null) {
				viewCodes.get(code).makeVisible();
				viewCodes.get(code).setLocation(
						panelPath.length() > 0 ? panelPath.substring(1) : "");
				panelList.add(viewCodes.get(code));
			}
		} else if (expr instanceof ExpressionNode) {
			ExpressionNode en = (ExpressionNode) expr;
			boolean horizontal = ((ExpressionNode) expr)
					.getOperation() == Operation.MULTIPLY;

			double ratio = size(en.getLeft(), horizontal)
					/ size(en, horizontal);
			double height1 = totalHeight;
			double width1 = totalWidth;
			double height2 = totalHeight;
			double width2 = totalWidth;
			if (horizontal) {
				width1 = width1 * ratio;
				width2 = width2 * (1 - ratio);
			} else {
				height1 = height1 * ratio;
				height2 = height2 * (1 - ratio);
			}

			splitList.add(new DockSplitPaneData(
					splitPath.length() > 0 ? splitPath.substring(1) : "",
					horizontal ? width1 : height1, horizontal ? 1 : 0));
			buildPerspective(en.getRight().unwrap(),
					panelPath + (horizontal ? ",1" : ",2"), splitPath + ",1",
					panelList, splitList, width2, height2);
			buildPerspective(en.getLeft().unwrap(),
					panelPath + (horizontal ? ",3" : ",0"), splitPath + ",0",
					panelList, splitList, width1, height1);
		} else {
			Log.error("Wrong type" + expr.getValueType());
		}
	}

	private static double size(ExpressionValue expr, boolean horizontal) {
		if (expr instanceof Variable && horizontal) {
			String name = ((Variable) expr)
					.getName(StringTemplate.defaultTemplate);
			if ("A".equals(name) || "C".equals(name)) {
				return 0.5;
			}
		}
		if (!(expr instanceof ExpressionNode)) {
			return 1;
		}
		ExpressionNode en = (ExpressionNode) expr;
		if (en.getOperation() != Operation.NO_OPERATION
				&& en.getRight() != null) {
			return size(en.getLeft(), horizontal)
					+ size(en.getRight(), horizontal);
		}
		return size(en.getLeft(), horizontal);

	}

	/**
	 * @param app
	 *            application
	 * @param code
	 *            eg "+A" to open Algebra View
	 */
	public static void decodeSimple(App app, String code) {
		boolean open = code.startsWith("+");
		String viewShortName = code.substring(1);
		DockPanelData view = viewCodes.get(viewShortName);
		if (view != null) {
			app.getGuiManager().setShowView(open, view.getViewId());
		} else {
			Log.error("id '" + viewShortName + "' doesn't exist");
		}
	}

	/**
	 * Checks allowed views, asssumes we're in an unbundled app
	 * @param viewId view ID
	 * @param forcedPerspective perspective number (as string)
	 * @return which views are allowed in the perspective
	 */
	public static boolean isAllowed(int viewId, String forcedPerspective) {
		if (StringUtil.empty(forcedPerspective)) {
			return true;
		}
		if (String.valueOf(Perspective.GRAPHER_3D).equals(forcedPerspective)) {
			return viewId == App.VIEW_ALGEBRA || viewId == App.VIEW_EUCLIDIAN3D;
		}
		if (String.valueOf(Perspective.PROBABILITY).equals(forcedPerspective)) {
			return viewId == App.VIEW_ALGEBRA || viewId == App.VIEW_PROBABILITY_CALCULATOR;
		}
		return viewId == App.VIEW_ALGEBRA || viewId == App.VIEW_EUCLIDIAN;
	}
}
