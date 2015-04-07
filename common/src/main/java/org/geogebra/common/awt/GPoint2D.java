package org.geogebra.common.awt;

public abstract class GPoint2D {
	public static class Double extends GPoint2D {

		public double x;
		public double y;

		public Double(double x, double y) {
			setLocation(x, y);
		}

		public Double() {
			setLocation(0, 0);
		}

		public void setLocation(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}

		@Override
		public void setX(double x) {
			this.x = x;
		}

		@Override
		public void setY(double y) {
			this.y = y;
		}

		@Override
		public double distance(GPoint2D q) {
			return Math.sqrt(GPoint2D.distanceSq(getX(), getY(), q.getX(),
					q.getY()));
		}

		@Override
		public double distance(double x1, double y1) {
			return Math.sqrt(GPoint2D.distanceSq(getX(), getY(), x1, y1));
		}

	}

	public abstract double getX();

	public abstract double distance(double x, double y);

	public abstract double getY();

	public abstract void setX(double x);

	public abstract void setY(double y);

	public abstract double distance(GPoint2D q);

	public static double distanceSq(double x1, double y1, double x2, double y2) {
		double d_x = x2 - x1;
		double d_y = y2 - y1;
		return d_x * d_x + d_y * d_y;
	}

	public void setLocation(double x, double y) {
		setX(x);
		setY(y);

	}
}
