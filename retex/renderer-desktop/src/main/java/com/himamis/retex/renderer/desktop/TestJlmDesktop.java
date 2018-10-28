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

		String[] texts = {
				"1+2+3 \\\\ 1+\\bgcolor{red}{2+}3 \\\\ 1\\bgcolor{red}{+2}+3 \\\\ 1+\\bgcolor{red}{2}+3",
		};

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