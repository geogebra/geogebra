package com.himamis.retex.renderer.share;

public class BoxPosition {
	public final double x;
	public final double y;
	public final double scale;
	public final double baseline;

	public BoxPosition(double x, double y, double scale, double baseline) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.baseline = baseline;
	}

	public BoxPosition withPosition(double x, double y) {
		return new BoxPosition(x, y, this.scale, this.baseline);
	}

	public BoxPosition withX(double x) {
		return new BoxPosition(x, this.y, this.scale, this.baseline);
	}

	public BoxPosition withY(double y) {
		return new BoxPosition(this.x, y, this.scale, this.baseline);
	}

	public BoxPosition withScale(double scale) {
		return new BoxPosition(this.x, this.y, this.scale * scale, this.baseline);
	}
}
