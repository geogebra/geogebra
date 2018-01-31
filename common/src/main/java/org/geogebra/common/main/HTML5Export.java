package org.geogebra.common.main;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.util.StringUtil;

public class HTML5Export {

	public static String getFullString(App app) {
		StringBuilder sb = new StringBuilder();
		GuiManagerInterface gui = app.getGuiManager();
		sb.append("<!DOCTYPE html>\n");
		sb.append("<html>\n");
		sb.append("<head>\n");
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
		sb.append("\"preventFocus\":false,\n");
		sb.append("\"showZoomButtons\":true,\n");
		sb.append("\"showFullscreenButton\":true,\n");
		sb.append("\"scale\":1,\n");
		sb.append("\"disableAutoScale\":false,\n");
		sb.append("\"clickToLoad\":false,\n");
		sb.append("\"appName\":\"" + app.getVersion().getAppName() + "\",\n");
		sb.append("\"showSuggestionButtons\":true,\n");
		sb.append("\"buttonRounding\":0.7,\n");
		sb.append("\"buttonShadows\":false,\n");
		sb.append(
				"\"language\":\"" + app.getLocalization().getLanguage()
						+ "\",\n");

		sb.append(
				"// use this instead of ggbBase64 to load a material from geogebra.org\n");
		sb.append("// \"material_id\":12345,\n");

		sb.append("// use this instead of ggbBase64 to load a .ggb file\n");
		sb.append("// \"filename\":\"myfile.ggb\",\n");

		sb.append("\"ggbBase64\":\"");
		// don't include preview bitmap
		sb.append(app.getGgbApi().getBase64(false));
		sb.append("\"};\n");

		// eg var views =
		// {"is3D":1,"AV":0,"SV":0,"CV":0,"EV2":0,"CP":0,"PC":0,"DA":0,"FI":0,"PV":0,"macro":0};

		sb.append(
				"// is3D=is 3D applet using 3D view, AV=Algebra View, SV=Spreadsheet View, CV=CAS View, EV2=Graphics View 2, CP=Construction Protocol, PC=Probability Calculator, DA=Data Analysis, FI=Function Inspector, PV=Python, macro=Macro View\n");
		sb.append("var views = {");
		sb.append("'is3D': ");
		sb.append(app.getKernel().getConstruction().has3DObjects() ? "1" : "0");
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
		// sb.append("applet.setHTML5Codebase('http://web.geogebra.org/5.0/"
		// + codeBase + "/');\n");
		sb.append("window.onload = function() {applet.inject('ggbApplet')};\n");

		String GeoGebra_loading = app.convertImageToDataURIIfPossible(
				GeoGebraConstants.GEOGEBRA_LOADING_PNG);
		String applet_play = app.convertImageToDataURIIfPossible(
				GeoGebraConstants.APPLET_PLAY_PNG);
		// String GeoGebra_loading =
		// "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAWgAAAA+CAMAAAAxm3G5AAACPVBMVEUAAAD////Ly8v///+UlJT////////////z8/P///+wsLDi4uJubm78/Pz4+Pj///////////96enrs7Oy+vr7////////////////X19eGhoaUlJT///////+hoaH///////////////////////////////////////9vb2////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////96enqHh4f////////////////////////////////////////////////////////////////////////////////////////////////////////////////////Gxsb///////+0tLT///+ampofHx/X19fb29sBAQEHBwcODg7l5eWJiYn///////9DQ0O9vb2jo6P29vaqqqpwcHBJSUnLy8uAgIBFRUU0NDR/f3+enp4yMjIpKSmdnZ3u7u6qqqrKysoFBQV1dXUkJCT///9lZWWYmP8AAACoqKhMTID29vbPz89ubm4KCg8GBgYdHTDw8PDf398TEyDs7OwmJkA/Pz9ycsBfX6CRkZGPj/B8fNCfn5+BgYF4eHg5OWAwMFA4ODgqKioQEBCFheBycr9NTU0wMDBeXl4YGBj6+vpCQnBoaGhXV1d7e89VVZBpabB5IoDqAAAAk3RSTlMA/b/50Pzw98/7w8Px49ja7fPkyMAJ5+TpwNnP9gTI9ahHsfLFlPEi1/DSzUQGAr9sUBYNGBHct97KmmfU0LShe0A3HqWLcY9+eFhKKSDj2FU9u2M6Gw8L4MfCdSa5gKyXh2BNhFo0MS0rE+td/qqd/p7+/fz9/PTn/PwQB/37yPPu6Ofm9/Dv7eXk49/d3djDoJ3CiGq2AAAXqElEQVR42u1cBXcbRxDW6nR3OsmyJNsCO6pBZtmO7ZiZGWJK4thO4nCapE2ZmZnkXCkpMzO3v623vEdu2r6m7WunfW0iLcx8Ozs7MzsrjzM1VB89lu7rjEciyURNxUDTSpvnb6aWocGOcCSqSpKaKk+2N040t/7p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";
		// String applet_play =
		// "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFAAAABQCAMAAAC5zwKfAAAAhFBMVEUAAABmZmb///9qampsbGz+/v6qqqpvb29ubm6enp6rq6t3d3d1dXVxcXH6+vqCgoJ+fn719fXv7+/n5+eTk5N0dHSXl5eNjY2KioqIiIji4uLe3t6UlJSQkJDr6+vW1tbCwsK5ubmioqK7u7uzs7OmpqaFhYXk5OR7e3vNzc3Jycmvr6833NujAAAAAXRSTlMAQObYZgAAAlhJREFUWMPVmNuW2jAMRS2Ogk0JCZBwvzPATNv//7/SdrqyBoxl2U/dH7DXUeSLYvP/czrPAPAdgG+DTNsCRKAvMJKlQwLIA4BWb2st02tAQ13OFcloSncUR2wnmGKZG5k3gKIBRN8H6YAQ0pGacchHKawEnxo4wadn5vdZSmbr37rpYOcRgjKA4NMzevQtBaF68TBlgogOQ5Wavxww/ta1I4e0iOQBx6poTmwpFtf5BuShnPTuNH2GvmbrPeF7f6jWu1jlLNhi/lb0PpVNbaH6iqW3gH7R+8fk+9BSBG3XEn/Cjv0RHB+RpIR3iubMiBS2QWGn3LSOo64DhEvuqN5XFhERSUrYMT3MHAVgSehRnsCCUCj5ieYMDpa8lRM+dmduERigakHoYbKvnV+4vAuXJJTsVR4An7C+C8dSQj9rfnE+LJ0o9PMT/jYPZonCDfuF1zJR+PZC2E8UNsNX1/MtRVhtxnh1Oiz0y6ZYDxgv1+FCnbC5gkMDslI4PQp7WSecrJ+ulxxhNV1YiOchYoXFumUmEoUc1+Vi3xdvvrqbNcWE0x/MJGGihZPDeASKFQ6lkqtp7UAypfkEwYTV+9ZBN34hlHB/YdYO7lePEdvi776wluLAxQQjYvO7Fx8WKUP2HPSMPV1KBkWzFP8bmUmBe3gYoEyQ9Gcmr8GOEWXB5pFzXkbzTEkZ1J1HXbTc4fzGIOPVRv6A+VXDBOBEXyAjSMfYSOTEy30QgjNRrEBRsOaBOIKb0TAWwmFrtOwgFJvAyuLBCiKUA5PDrp4xs3PsmO1wrpXl8wsCuyePXN5O7AAAAABJRU5ErkJggg==";

		// dummy (but valid) gif
		String previewImage = StringUtil.gifMarker + "R0lGODlhAQABAAAAADs=";

		GBufferedImage preview = app.getActiveEuclidianView().getExportImage(1);

		String base64 = preview.getBase64();

		if (base64 != null) {
			previewImage = base64;
		}

		sb.append("applet.setPreviewImage('");
		sb.append(previewImage);
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

}
