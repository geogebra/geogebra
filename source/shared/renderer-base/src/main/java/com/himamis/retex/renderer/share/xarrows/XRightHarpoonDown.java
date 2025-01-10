package com.himamis.retex.renderer.share.xarrows;

public class XRightHarpoonDown extends XArrowBox {

	public XRightHarpoonDown(double width) {
		this.commands = "MLQQQLQQQQQQQQQQ";

		this.width = width < 1 ? 1 : width;
		this.height = 0.366875;
		this.depth = -0.0665625;

		double len = width < 0.91 ? 0 : width - 0.91;
		data = new double[] { 0.851 + len, -0.230, 0.091, -0.230, 0.056, -0.242,
				0.061, -0.230, 0.055, -0.250, 0.055, -0.245, 0.091, -0.270,
				0.062, -0.269, 0.916 + len, -0.270, 0.943 + len, -0.263,
				0.940 + len, -0.270, 0.944 + len, -0.254, 0.944 + len, -0.260,
				0.935 + len, -0.239, 0.944 + len, -0.244, 0.736 + len, -0.012,
				0.796 + len, -0.155, 0.726 + len, 0.008, 0.729 + len, 0.007,
				0.711 + len, 0.011, 0.722 + len, 0.011, 0.692 + len, 0,
				0.692 + len, 0.011, 0.722 + len, -0.073, 0.692 + len, -0.017,
				0.761 + len, -0.135, 0.739 + len, -0.106, 0.851 + len, -0.230,
				0.798 + len, -0.186 };
	}
}
