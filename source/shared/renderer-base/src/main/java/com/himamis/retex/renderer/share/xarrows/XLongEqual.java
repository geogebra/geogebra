package com.himamis.retex.renderer.share.xarrows;

public class XLongEqual extends XArrowBox {

	public XLongEqual(double width) {
		this.commands = "MLQQLQQMLQQLQQ";

		this.width = width < 0.6 ? 0.6 : width;
		this.height = 0.366875;
		this.depth = -0.0665625;

		double len = width < 0.667 ? 0 : width - 0.667;
		data = new double[] { 0.687 + len, -0.330, 0.090, -0.330, 0.056, -0.350,
				0.056, -0.330, 0.089, -0.370, 0.056, -0.370, 0.688 + len,
				-0.370, 0.721 + len, -0.350, 0.721 + len, -0.370, 0.687 + len,
				-0.330, 0.721 + len, -0.330, 0.688 + len, -0.130, 0.089, -0.130,
				0.056, -0.150, 0.056, -0.130, 0.090, -0.170, 0.056, -0.170,
				0.687 + len, -0.170, 0.721 + len, -0.150, 0.721 + len, -0.170,
				0.688 + len, -0.130, 0.721 + len, -0.130 };
	}
}
