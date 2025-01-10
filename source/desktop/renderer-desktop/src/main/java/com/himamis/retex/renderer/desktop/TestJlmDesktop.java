package com.himamis.retex.renderer.desktop;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.himamis.retex.renderer.desktop.graphics.Graphics2DD;
import com.himamis.retex.renderer.share.Colors;
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
				"\\begin{tabular}{|l|l|l|}\n"
				+ "Rows  & Column 1 & Column 2 \\\\"
				+ "Row 1 & 1234     & 2345     \\\\"
				+ "Row 2 & 3456     & 4567     \\\\"
				+ "Row 3 & 5678     & 6789     \\\\"
				+ "Row 4 & 7890     & 8901     \\\\"
				+ "Row 5 & 9012     & 10000    \\\\"
				+ "\\end{tabular}",
				"\\renewcommand{\\arraystretch}{1.7}"
				+ "\\begin{tabular}{|l|l|l|}"
				+ "Rows  & Column 1 & Column 2 \\\\"
				+ "Row 1 & 1234     & 2345     \\\\"
				+ "Row 2 & 3456     & 4567     \\\\"
				+ "Row 3 & 5678     & 6789     \\\\"
				+ "Row 4 & 7890     & 8901     \\\\"
				+ "Row 5 & 9012     & 10000    \\\\"
				+ "\\end{tabular}",
		};

		int y = 100;
		for (String text : texts) {
			TeXFormula formula = new TeXFormula(text);
			Image im = formula.createBufferedImage(TeXConstants.STYLE_DISPLAY,
					30, Colors.BLACK, Colors.WHITE);
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