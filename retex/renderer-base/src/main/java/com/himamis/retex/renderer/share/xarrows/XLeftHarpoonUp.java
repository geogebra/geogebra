package com.himamis.retex.renderer.share.xarrows;

public class XLeftHarpoonUp extends XArrowBox {

	public XLeftHarpoonUp(double width) {
		this.commands = "MLQQQQQQQQQQLQQQ";

		this.width = width < 1 ? 1 : width;
		this.height = 0.366875;
		this.depth = -0.0665625;

		double len = width < 0.91 ? 0 : width - 0.91;
		data = new double[] { 0.908 + len, -0.230, 0.083, -0.230, 0.056, -0.236,
				0.059, -0.230, 0.055, -0.245, 0.055, -0.239, 0.066, -0.263,
				0.055, -0.256, 0.262, -0.488, 0.203, -0.344, 0.273, -0.508,
				0.270, -0.507, 0.288, -0.511, 0.277, -0.511, 0.307, -0.500,
				0.307, -0.511, 0.277, -0.427, 0.307, -0.483, 0.238, -0.365,
				0.260, -0.394, 0.148, -0.270, 0.201, -0.314, 0.908 + len,
				-0.270, 0.943 + len, -0.258, 0.938 + len, -0.270, 0.944 + len,
				-0.250, 0.944 + len, -0.255, 0.908 + len, -0.230, 0.937 + len,
				-0.231 };
	}
}
