package com.himamis.retex.renderer.share;

import java.util.ArrayList;

import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

public class CursorBox extends Box {
	private static final int DIAMETER = 10;
	public static double startX, startY;
	private Box content;
	public static boolean touchSelection = true;

	public CursorBox(Box content, float coeff) {
		this.content = content;
		this.children.add(content);
		this.width = 0;
		this.height = content.height * coeff;
		this.depth = content.depth * coeff;
	}
	@Override
	public void draw(Graphics2DInterface g2, float x, float y) {
		content.draw(g2, (float) (x - content.width * 0.5), y);


		CursorBox.startX = g2.getTransform().getScaleX() * x
				+ g2.getTransform().getShearX() * y
				+ g2.getTransform().getTranslateX();
		CursorBox.startY = g2.getTransform().getScaleY() * y
				+ g2.getTransform().getShearY() * x
				+ g2.getTransform().getTranslateY();





	}

	@Override
	public int getLastFontId() {
		// TODO Auto-generated method stub
		return content.getLastFontId();
	}

	@Override
	public void getPath(float x, float y, ArrayList<Integer> list) {
		super.getPath(x, y, list);

	}
}
