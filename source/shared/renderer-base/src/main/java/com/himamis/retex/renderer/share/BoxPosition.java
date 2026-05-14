package com.himamis.retex.renderer.share;

public record BoxPosition(double x, double y, double scale, double baseline) {
	
	public static final BoxPosition ZERO = new BoxPosition(0, 0, 1, 0);

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
