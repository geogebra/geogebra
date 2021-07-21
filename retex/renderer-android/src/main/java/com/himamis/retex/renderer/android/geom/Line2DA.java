package com.himamis.retex.renderer.android.geom;

import com.himamis.retex.renderer.share.platform.geom.Line2D;

import android.graphics.PointF;

public class Line2DA implements Line2D {
	
	private PointF start;
	private PointF end;
	
	public Line2DA(double x1, double y1, double x2, double y2) {
		start = new PointF();
		end = new PointF();
		setLine(x1, y1, x2, y2);
	}

	public void setLine(double x1, double y1, double x2, double y2) {
		start.set((float) x1, (float) y1);
		end.set((float) x2, (float) y2);
	}

	public PointF getStartPoint() {
		return start;
	}
	
	public PointF getEndPoint() {
		return end;
	}

	public double getX1() {
		return start.x;
	}

	public double getY1() {
		return start.y;
	}

	public double getX2() {
		return end.x;
	}

	public double getY2() {
		return end.y;
	}
}
