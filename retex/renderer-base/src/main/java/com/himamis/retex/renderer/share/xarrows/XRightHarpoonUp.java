package com.himamis.retex.renderer.share.xarrows;

public class XRightHarpoonUp extends XArrowBox {

	public XRightHarpoonUp(double width) {
		this.commands = "MLQQQLQQLLLQQQQQQLL";

		this.width = width < 1 ? 1 : width;
		this.height = 0.366875;
		this.depth = -0.0665625;

		double len = width < 0.91 ? 0 : width - 0.91;
		data = new double[] { 0.916 + len, -0.230, 0.091, -0.230, 0.056, -0.242,
				0.061, -0.230, 0.055, -0.250, 0.055, -0.245, 0.091, -0.270,
				0.062, -0.269, 0.851 + len, -0.270, 0.761 + len, -0.365,
				0.795 + len, -0.318, 0.693 + len, -0.494, 0.711 + len, -0.431,
				0.693 + len, -0.496, 0.693 + len, -0.497, 0.692 + len, -0.500,
				0.711 + len, -0.511, 0.692 + len, -0.511, 0.731 + len, -0.503,
				0.726 + len, -0.511, 0.733 + len, -0.497, 0.731 + len, -0.501,
				0.925 + len, -0.268, 0.792 + len, -0.348, 0.944 + len, -0.246,
				0.943 + len, -0.252, 0.933 + len, -0.231, 0.944 + len, -0.232,
				0.932 + len, -0.230, 0.916 + len, -0.230 };
	}
}
