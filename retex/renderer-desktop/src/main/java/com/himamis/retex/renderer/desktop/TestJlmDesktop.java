package com.himamis.retex.renderer.desktop;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.himamis.retex.renderer.desktop.graphics.Graphics2DD;
import com.himamis.retex.renderer.share.ColorUtil;
import com.himamis.retex.renderer.share.Configuration;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Image;

public class TestJlmDesktop extends JFrame {

	static {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderDesktop());
		}

	}

	public TestJlmDesktop() {
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		setSize(1000, 1000);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2 = (Graphics2D) g;
		Graphics2DD g2d = new Graphics2DD(g2);

		String[] texts = { "\\Huge{\\overleftarrow{aaaaaaa}}",
				"\\Huge{\\overleftrightarrow{aaaaaaa}}",
				"\\Huge{\\overleftarrow{a}}", "\\Huge{\\overleftrightarrow{a}}",
				"\\Huge{A\\xhookrightarrow{aaaa}}B",
				"\\Huge{A\\xhookrightarrow{aaa}}B",
				"\\Huge{A\\xhookrightarrow{aa}}B",
				"\\Huge{A\\xhookrightarrow{a}}B",
				"\\Huge{A\\xhookrightarrow{}}B",
				"\\Huge{A\\xhookleftarrow{aaaa}}B",
				"\\Huge{A\\xhookleftarrow{aaa}}B",
				"\\Huge{A\\xhookleftarrow{aa}}B",
				"\\Huge{A\\xhookleftarrow{a}}B", "\\Huge{A\\xhookleftarrow{}}B",
				"\\Huge{A\\xmapsto{aaaa}}B", "\\Huge{A\\xmapsto{}}B",
				"\\Huge{A\\xlongequal{aaaa}}B", "\\Huge{A\\xlongequal{}}B",
				"\\Huge{A\\xrightsmallleftharpoons{}}B",
				"\\Huge{A\\xrightsmallleftharpoons{aaaa}}B",
				"\\Huge{A\\xsmallrightleftharpoons{}}B",
				"\\Huge{A\\xsmallrightleftharpoons{aaaa}}B",
				"\\Huge{A\\xrightleftharpoons{}}B",
				"\\Huge{A\\xrightleftharpoons{aaaa}}B",
				"\\Huge{A\\xleftrightharpoons{}}B",
				"\\Huge{A\\xleftrightharpoons{aaaa}}B",
				"\\Huge{\\xrightharpoonup{}}",
				"\\Huge{\\xrightharpoonup{aaaa}}",
				"\\Huge{\\xrightharpoondown{}}",
				"\\Huge{\\xrightharpoondown{aaaa}}",
				"\\Huge{\\xleftharpoondown{}}",
				"\\Huge{\\xleftharpoondown{aaaa}}",
				"\\Huge{\\xleftharpoonup{}}", "\\Huge{\\xleftharpoonup{aaaa}}",
				"\\Huge{\\xleftarrow{}}", "\\Huge{\\xleftarrow{aaaa}}",
				"\\Huge{\\xrightarrow{}}", "\\Huge{\\xrightarrow{aaaa}}",
				"\\Huge{\\xleftrightarrows{}}",
				"\\Huge{A\\xleftrightarrows{aaaa}B}",
				"\\Huge{\\xrightleftarrows{}}",
				"\\Huge{\\xrightleftarrows{aaaa}}",
				"\\Huge{A\\xleftrightarrow{}B}",
				"\\Huge{A\\xleftrightarrow{aaaa}B}", };

		int y = 100;
		for (String text : texts) {
			TeXFormula formula = new TeXFormula(text);
			Image im = formula.createBufferedImage(TeXConstants.STYLE_DISPLAY,
					30, ColorUtil.BLACK, ColorUtil.WHITE);
			g2d.drawImage(im, 100, y);
			y += im.getHeight() + 10;
		}
	}

	public static void main(String[] args) {
		Configuration.getFontMapping();
		TestJlmDesktop s = new TestJlmDesktop();
		s.setVisible(true);
	}

}