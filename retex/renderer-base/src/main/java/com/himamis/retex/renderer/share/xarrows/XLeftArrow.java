package com.himamis.retex.renderer.share.xarrows;

public class XLeftArrow extends XArrowBox {

	public XLeftArrow(double width) {
		this.commands = "MLLQQQQQLQQQQQQQQLQQ";

		this.width = width < 1 ? 1 : width;
		this.height = 0.366875;
		this.depth = -0.0665625;

		double len = width < 0.91 ? 0 : width - 0.91;
		data = new double[] { 0.910 + len, -0.230, 0.910 + len, -0.230, 0.141,
				-0.230, 0.243, -0.101, 0.209, -0.180, 0.249, -0.084, 0.249,
				-0.088, 0.234, -0.072, 0.249, -0.072, 0.217, -0.086, 0.223,
				-0.072, 0.092, -0.225, 0.174, -0.179, 0.069, -0.237, 0.057,
				-0.250, 0.057, -0.244, 0.067, -0.262, 0.057, -0.256, 0.071,
				-0.264, 0.065, -0.261, 0.218, -0.418, 0.172, -0.312, 0.234,
				-0.428, 0.223, -0.428, 0.249, -0.416, 0.249, -0.428, 0.220,
				-0.356, 0.249, -0.401, 0.141, -0.270, 0.187, -0.304,
				0.910 + len, -0.270, 0.943 + len, -0.250, 0.940 + len, -0.265,
				0.910 + len, -0.230, 0.943 + len, -0.230 };
	}
}
