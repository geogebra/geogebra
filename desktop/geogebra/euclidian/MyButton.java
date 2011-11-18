package geogebra.euclidian;

import geogebra.kernel.geos.GeoButton;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JButton;

public class MyButton extends JButton {

	private GeoButton geoButton;
	private EuclidianView view;

	public MyButton(GeoButton button, EuclidianView view) {
		this.geoButton = button;
		this.view = view;
	}
	@Override
	protected void paintBorder(Graphics g) {
		//super.paintBorder(g);
	}


	@Override
	protected void paintComponent(Graphics g) {
		//super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		EuclidianView.setAntialiasing(g2);

		g2.setColor(view.getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());

		GradientPaint p;

		Color bg = geoButton.getBackgroundColor(), bg2;
		if (bg == null) bg = Color.LIGHT_GRAY;
		if (isSelected()) {
			bg2 = bg;
			bg = bg.darker();
		} else {
			bg2 = bg.brighter();
		}
		p = new GradientPaint(0, 0, bg2 , 0, getHeight(), bg);


		Paint oldPaint = g2.getPaint();

		g2.setPaint(p);


		g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight() / 3, getHeight() / 3);

		g2.setPaint(oldPaint);

		g2.setColor(Color.black);

		g2.setStroke(EuclidianView.standardStroke);           	
		g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight() / 3, getHeight() / 3);

		g2.setColor(geoButton.getObjectColor());
		
		Font f = getFont();
		
		g2.setFont(f);

		FontMetrics metrics = getFontMetrics(f);

		int spareHeight = getHeight() - metrics.getHeight();

		int spareWidth = getWidth() - metrics.stringWidth(geoButton.getCaption());
		this.setForeground(Color.WHITE);

		// center the label on the button
		g.drawString(geoButton.getCaption(), spareWidth/2, metrics.getAscent() + spareHeight/2 );
	}
}
