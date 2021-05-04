package com.himamis.retex.renderer.android.graphics;

import com.himamis.retex.renderer.share.platform.graphics.BasicStroke;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.GraphicsFactory;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.Transform;

public class GraphicsFactoryAndroid extends GraphicsFactory {

	@Override
	public BasicStroke createBasicStroke(double width, int cap, int join,
			double miterLimit) {
		return new BasicStrokeA(width, miterLimit, cap, join);
	}

	@Override
	public Color createColor(int red, int green, int blue, int alpha) {
		return new ColorA(red, green, blue, alpha);
	}

	@Override
	public Image createImage(int width, int height, int type) {
		return new ImageA(width, height, type);
	}

	@Override
	public Transform createTransform() {
		return new TransformA();
	}

}
