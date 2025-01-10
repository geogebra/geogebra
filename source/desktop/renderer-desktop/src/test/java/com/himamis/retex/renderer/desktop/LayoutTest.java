package com.himamis.retex.renderer.desktop;

import java.awt.Graphics2D;

import javax.swing.JFrame;

import com.himamis.retex.renderer.desktop.font.TextLayoutD;

public class LayoutTest {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setVisible(true);
		Graphics2D g = (Graphics2D) frame.getGraphics();
		for (int i = 0; i < 3000; i++) {
			char c = (char) i;
			TextLayoutD ld = new TextLayoutD(c + "", frame.getFont(),
					g.getFontRenderContext());
			System.out.println(i + " (" + c + ") : "
					+ ld.getBounds().getY() / ld.getBounds().getHeight());
		}
	}
}
