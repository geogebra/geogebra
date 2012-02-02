package geogebra.common.awt;

public abstract class Point2D {
	public static class Double extends Point2D{
		
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
		public double distance(Point2D q) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double distance(double x, double y) {
			return Point2D.distanceSq(getX(), getY(), x, y);
		}
		
	}
	
	public abstract double getX();
	public abstract double distance(double x, double y);
	public abstract double getY();
	public abstract void setX(double x);
	public abstract void setY(double y);
	public abstract double distance(Point2D q);
	public static double distanceSq(double x1, double y1, double x2, double y2) {
        x2 -= x1;
        y2 -= y1;
        return x2 * x2 + y2 * y2;
    }
}
