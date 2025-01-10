package com.himamis.retex.renderer.share.xarrows;

public class XLeftHarpoonDown extends XArrowBox {

	public XLeftHarpoonDown(double width) {
		this.commands = "MLLQQQQQQQQQQLLLLLQQQ";

		this.width = width < 1 ? 1 : width;
		this.height = 0.366875;
		this.depth = -0.0665625;

		double len = width < 0.91 ? 0 : width - 0.91;
		data = new double[] { 0.908, -0.230, 0.908, -0.230, 0.148, -0.230,
				0.238, -0.135, 0.204, -0.182, 0.306, -0.006, 0.288, -0.069,
				0.307, 0, 0.307, -0.001, 0.288, 0.011, 0.307, 0.011, 0.269,
				0.003, 0.273, 0.011, 0.267, -0.002, 0.268, 0.002, 0.140, -0.184,
				0.219, -0.115, 0.065, -0.238, 0.106, -0.213, 0.055, -0.255,
				0.055, -0.244, 0.066, -0.270, 0.055, -0.268, 0.067, -0.270,
				0.069, -0.270, 0.070, -0.270, 0.083, -0.270, 0.908 + len,
				-0.270, 0.943 + len, -0.258, 0.938 + len, -0.270, 0.944 + len,
				-0.250, 0.944 + len, -0.255, 0.908 + len, -0.230, 0.937 + len,
				-0.231 };
	}
}
