//package com.himamis.retex.renderer.web;
//
//import com.google.gwt.junit.client.GWTTestCase;
//import com.himamis.retex.renderer.share.ColorUtil;
//import com.himamis.retex.renderer.share.TeXConstants;
//import com.himamis.retex.renderer.share.TeXFormula;
//import com.himamis.retex.renderer.share.TeXIcon;
//import com.himamis.retex.renderer.share.platform.FactoryProvider;
//import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
//import com.himamis.retex.renderer.share.platform.graphics.Image;
//import com.himamis.retex.renderer.share.platform.graphics.Insets;
//import com.himamis.retex.renderer.web.FactoryProviderGWT;
//import com.himamis.retex.renderer.web.graphics.ImageW;
//
//public class GwtTestSimple extends GWTTestCase {
//
//	public void testSetupTeXIcon() {
//		FactoryProvider.INSTANCE = new FactoryProviderGWT();
//
//		String latex = "\\forall\\varepsilon\\in\\mathbb{R}_+^*\\ \\exists\\eta>0\\ |x-x_0|\\leq\\eta\\Longrightarrow|f(x)-f(x_0)|\\leq\\varepsilon\\\\";
//		TeXFormula formula = null;
//		try {
//			formula = new TeXFormula(latex);
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		TeXIcon icon = formula.new TeXIconBuilder()
//				.setStyle(TeXConstants.STYLE_DISPLAY).setSize(20).build();
//
//		icon.setInsets(new Insets(5, 5, 5, 5));
//		Image image = new ImageW(icon.getIconWidth(), icon.getIconHeight(),
//				Image.TYPE_INT_ARGB);
//		Graphics2DInterface g2 = image.createGraphics2D();
//		g2.setColor(ColorUtil.WHITE);
//		g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
//		icon.paintIcon(null, g2, 0, 0);
//		assertEquals(true, true);
//	}
//
//	@Override
//	public String getModuleName() {
//		return "com.himamis.retex.renderer.web.JLaTeXMathGWT";
//	}
//
//}
