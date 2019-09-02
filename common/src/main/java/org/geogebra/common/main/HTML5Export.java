package org.geogebra.common.main;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.util.StringUtil;

/**
 * Creates HTML5 page with interactive applet.
 */
public class HTML5Export {

	/**
	 * @param app
	 *            appplication
	 * @return complete HTML embed code
	 */
	public static String getFullString(App app) {
		StringBuilder sb = new StringBuilder();
		GuiManagerInterface gui = app.getGuiManager();
		sb.append("<!DOCTYPE html>\n");
		sb.append("<html>\n");
		sb.append("<head>\n");

		// make sure scaling works
		sb.append(
				"<meta name=viewport content=\"width=device-width,initial-scale=1\">\n");

		// make sure translation files loaded OK
		sb.append("<meta charset=\"utf-8\"/>\n");

		if (app.has(Feature.TUBE_BETA)) {
			sb.append(
					"<script src=\"https://beta.geogebra.org/scripts/deployggb.js\"></script>\n\n");

		} else {
			sb.append(
					"<script src=\"https://cdn.geogebra.org/apps/deployggb.js\"></script>\n\n");

		}
		sb.append("</head>\n");
		sb.append("<body>\n");

		sb.append("<div id=\"ggbApplet\"></div>\n\n");

		sb.append("<script>\n");

		sb.append("var parameters = {\n");
		sb.append("\"id\": \"ggbApplet\",\n");
		sb.append("\"width\":" + (int) app.getWidth() + ",\n");
		sb.append("\"height\":" + (int) app.getHeight() + ",\n");
		sb.append("\"showMenuBar\":" + app.showMenuBar + ",\n");
		sb.append("\"showAlgebraInput\":" + app.showAlgebraInput + ",\n");

		sb.append("\"showToolBar\":" + app.showToolBar + ",\n");
		if (app.showToolBar) {
			if (gui != null) {
				sb.append("\"customToolBar\":\"");
				sb.append(gui.getToolbarDefinition());
				sb.append("\",\n");
			}
			sb.append("\"showToolBarHelp\":" + app.showToolBarHelp + ",\n");

		}
		sb.append("\"showResetIcon\":false,\n");
		sb.append("\"enableLabelDrags\":false,\n");
		sb.append("\"enableShiftDragZoom\":true,\n");
		sb.append("\"enableRightClick\":false,\n");
		sb.append("\"errorDialogsActive\":false,\n");
		sb.append("\"useBrowserForJS\":false,\n");
		sb.append("\"allowStyleBar\":false,\n");
		sb.append("\"preventFocus\":false,\n");
		sb.append("\"showZoomButtons\":true,\n");
		sb.append("\"capturingThreshold\":3,\n");

		sb.append("// add code here to run when the applet starts\n");
		sb.append("\"appletOnLoad\":function(api){");
		sb.append(" /* api.evalCommand('Segment((1,2),(3,4))');*/ },\n");

		sb.append("\"showFullscreenButton\":true,\n");
		sb.append("\"scale\":1,\n");
		sb.append("\"disableAutoScale\":false,\n");
		sb.append("\"allowUpscale\":false,\n");
		sb.append("\"clickToLoad\":false,\n");
		sb.append("\"appName\":\"" + app.getConfig().getAppCode() + "\",\n");
		sb.append("\"showSuggestionButtons\":true,\n");
		sb.append("\"buttonRounding\":0.7,\n");
		sb.append("\"buttonShadows\":false,\n");
		sb.append(
				"\"language\":\"" + app.getLocalization().getLanguage()
						+ "\",\n");

		sb.append(
				"// use this instead of ggbBase64 to load a material from geogebra.org\n");
		sb.append("// \"material_id\":\"RHYH3UQ8\",\n");

		sb.append("// use this instead of ggbBase64 to load a .ggb file\n");
		sb.append("// \"filename\":\"myfile.ggb\",\n");

		sb.append("\"ggbBase64\":\"");
		// don't include preview bitmap
		sb.append(app.getGgbApi().getBase64(false));
		sb.append("\",\n};\n");

		// eg var views =
		// {"is3D":1,"AV":0,"SV":0,"CV":0,"EV2":0,"CP":0,"PC":0,"DA":0,"FI":0,"PV":0,"macro":0};

		sb.append("// is3D=is 3D applet using 3D view, AV=Algebra View,");
		sb.append(" SV=Spreadsheet View, CV=CAS View, EV2=Graphics View 2,");
		sb.append(" CP=Construction Protocol, PC=Probability Calculator");
		sb.append(" DA=Data Analysis, FI=Function Inspector, macro=Macros\n");

		sb.append("var views = {");
		sb.append("'is3D': ");
		Construction construction = app.getKernel().getConstruction();
		boolean useWeb3D = construction.requires3D();
		sb.append(useWeb3D ? "1" : "0");
		if (gui != null) {
			sb.append(",'AV': ");
			sb.append(gui.hasAlgebraView() && gui.getAlgebraView().isShowing()
					? "1" : "0");
			sb.append(",'SV': ");
			sb.append(gui.hasSpreadsheetView()
					&& gui.getSpreadsheetView().isShowing() ? "1" : "0");
			sb.append(",'CV': ");
			sb.append((gui.hasCasView() ? "1" : "0"));
			sb.append(",'EV2': ");
			sb.append((app.hasEuclidianView2(1) ? "1" : "0"));
			sb.append(",'CP': ");
			sb.append(gui.isUsingConstructionProtocol() ? "1" : "0");
			sb.append(",'PC': ");
			sb.append(gui.hasProbabilityCalculator() ? "1" : "0");
			sb.append(",'DA': ");
			sb.append(gui.hasDataAnalysisView() ? "1" : "0");
			sb.append(",'FI': ");
			sb.append(
					app.getDialogManager().hasFunctionInspector() ? "1" : "0");
		}
		// TODO
		sb.append(",'macro': 0");
		sb.append("};\n");

		sb.append("var applet = new GGBApplet(parameters, '5.0', views);\n");

		// String codeBase = kernel.kernelHas3DObjects() ? "web3d" : "web";
		// sb.append("applet.setHTML5Codebase('http://cdn.geogebra.org/apps/latest/"
		// + codeBase + "/');\n");
		sb.append("window.onload = function() {applet.inject('ggbApplet')};\n");

		String GeoGebra_loading = app.convertImageToDataURIIfPossible(
				GeoGebraConstants.GEOGEBRA_LOADING_PNG);
		String applet_play = app.convertImageToDataURIIfPossible(
				GeoGebraConstants.APPLET_PLAY_PNG);

		sb.append("applet.setPreviewImage('");
		sb.append(getPreviewImage(app));
		sb.append("','");
		sb.append(GeoGebra_loading);
		sb.append("','");
		sb.append(applet_play);
		sb.append("');\n");

		sb.append("</script>\n");
		sb.append("</body>\n");
		sb.append("</html>\n");

		return sb.toString();
	}

	private static Object getPreviewImage(App app) {
		GBufferedImage preview = app.getActiveEuclidianView().getExportImage(1);
		String base64 = null;
		// eg 3D View in web
		if (preview != null) {
			base64 = preview.getBase64();
		}
		if (base64 != null) {
			return base64;
		}
		// dummy (but valid) gif
		return StringUtil.gifMarker + "R0lGODlhAQABAAAAADs=";
	}

}
