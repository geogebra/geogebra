package org.geogebra.deslktop.gui.util;


import org.geogebra.desktop.gui.util.JSVGImage;
import org.junit.Test;

public class SvgLoadTest  {
	private static final String content = "<svg>\n"
			+ "<rect x=\"20\" y=\"50\" width=\"100\" height=\"50\" \n"
			+ "    fill=\"yellow\" stroke=\"navy\" stroke-width=\"5\"  /> \n"
			+ "<rect x=\"150\" y=\"50\" width=\"100\" height=\"50\" rx=\"10\" \n"
			+ "    fill=\"green\" /> \n"
			+ "<g transform=\"translate(270 80) rotate(-30)\"> \n"
			+ "  <rect x=\"0\" y=\"0\" width=\"100\" height=\"50\" rx=\"10\" \n"
			+ "      fill=\"none\" stroke=\"purple\" stroke-width=\"3\" /> \n"
			+ "</g> \n"
			+ "<circle cx=\"70\" cy=\"220\" r=\"50\" \n"
			+ "    fill=\"red\" stroke=\"blue\" stroke-width=\"5\"  /> \n"
			+ "<g transform=\"translate(175 220)\"> \n"
			+ "  <ellipse rx=\"75\" ry=\"50\" fill=\"yellow\"  /> \n"
			+ "</g> \n"
			+ "<ellipse transform=\"translate(300 220) rotate(-30)\" \n"
			+ "    rx=\"75\" ry=\"50\" fill=\"none\" stroke=\"blue\" stroke-width=\"10\"  /> \n"
			+ "<g stroke=\"green\" > \n"
			+ "  <line x1=\"450\" y1=\"120\" x2=\"550\" y2=\"20\" stroke-width=\"5\"  /> \n"
			+ "  <line x1=\"550\" y1=\"120\" x2=\"650\" y2=\"20\" stroke-width=\"10\"  /> \n"
			+ "  <line x1=\"650\" y1=\"120\" x2=\"750\" y2=\"20\" stroke-width=\"15\"  /> \n"
			+ "  <line x1=\"750\" y1=\"120\" x2=\"850\" y2=\"20\" stroke-width=\"20\"  /> \n"
			+ "  <line x1=\"850\" y1=\"120\" x2=\"950\" y2=\"20\" stroke-width=\"25\"  /> \n"
			+ "</g> \n"
			+ "<polyline fill=\"none\" stroke=\"blue\" stroke-width=\"5\"  \n"
			+ "    points=\"450,250 \n"
			+ "            475,250 475,220 500,220 500,250 \n"
			+ "            525,250 525,200 550,200 550,250 \n"
			+ "            575,250 575,180 600,180 600,250 \n"
			+ "            625,250 625,160 650,160 650,250 \n"
			+ "            675,250\" /> \n"
			+ "<polygon fill=\"lime\" stroke=\"blue\" stroke-width=\"10\"  \n"
			+ "    points=\"800,150 900,180 900,240 800,270 700,240 700,180\" />\n"
			+ "</svg>\n";

	@Test
	public void name() {
		JSVGImage jsvgImage = JSVGImage.fromContent(content);
	}
}
