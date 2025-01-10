package org.geogebra.common.euclidian.plot.implicit;

import static org.geogebra.common.kernel.implicit.GeoImplicitCurve.interpolate;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.implicit.MarchingConfig;
import org.geogebra.common.kernel.implicit.MarchingRect;

public enum BernsteinMarchingConfig implements MarchingConfig {
	/**
	 * All corners are inside / outside
	 */
	T0000(0),

	/**
	 * only bottom left corner is inside / outside
	 */
	T0001(1) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{
					moveTo(r.x1(), interpolate(r.bottomLeft(), r.topLeft(), r.y2(),
							r.y1())),
					new MyPoint(interpolate(r.bottomLeft(), r.bottomRight(), r.x1(), r.x2()),
							r.y2(), SegmentType.LINE_TO)};
		}
	},

	/**
	 * bottom right corner is inside / outside
	 */
	T0010(2) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{
					moveTo(r.x2(), interpolate(r.bottomRight(), r.topRight(), r.y2(),
							r.y1())),
					new MyPoint(interpolate(r.bottomRight(), r.bottomLeft(), r.x2(), r.x1()),
							r.y2(), SegmentType.LINE_TO)};
		}
	},

	/**
	 * both corners at the bottom are inside / outside
	 */
	T0011(3) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{
					moveTo(r.x1(), interpolate(r.topLeft(), r.bottomLeft(), r.y1(),
							r.y2())),
					new MyPoint(r.x2(),
							interpolate(r.topRight(), r.bottomRight(), r.y1(), r.y2()),
							SegmentType.LINE_TO)};
		}
	},

	/**
	 * top left corner is inside / outside
	 */
	T0100(4) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{
					moveTo(r.x2(), interpolate(r.topRight(), r.bottomRight(), r.y1(),
							r.y2())),
					new MyPoint(interpolate(r.topRight(), r.topLeft(), r.x2(), r.x1()),
							r.y1(), SegmentType.LINE_TO)
			};
		}
	},

	T0101(5) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{
					moveTo(r.x1(), interpolate(r.topLeft(), r.bottomLeft(), r.y1(), r.y2())),
					lineTo(interpolate(r.bottomLeft(), r.bottomRight(), r.x1(), r.x2()), r.y2()),
					moveTo(r.x2(), interpolate(r.topRight(), r.bottomRight(), r.y1(), r.y2())),
					lineTo(interpolate(r.topLeft(), r.topRight(), r.x1(), r.x2()), r.y1())
			};
		}

	},
	/**
	 * both the corners at the left are inside / outside
	 */
	T0110(6) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{
					moveTo(interpolate(r.topLeft(), r.topRight(), r.x1(), r.x2()),
							r.y1()),
					lineTo(interpolate(r.bottomLeft(), r.bottomRight(), r.x1(), r.x2()),
							r.y2())};
		}
	},
	/**
	 * only top left corner is inside / outside
	 */
	T0111(7) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{moveTo(r.x1(),
					interpolate(r.topLeft(), r.bottomLeft(), r.y1(), r.y2())),
					new MyPoint(interpolate(r.topLeft(), r.topRight(), r.x1(), r.x2()),
							r.y1(), SegmentType.LINE_TO)};
		}
	},

	T1000(8) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return T0111.getPoints(r);
		}
	},

	T1001(9) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return T0110.getPoints(r);
		}

	},
	/**
	 * opposite corners are inside / outside. NOTE: This configuration is
	 * regarded as invalid
	 */
	T1010(10) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{
					moveTo(r.x1(), interpolate(r.topLeft(), r.bottomLeft(), r.y1(), r.y2())),
					lineTo(interpolate(r.topLeft(), r.topRight(), r.x1(), r.x2()), r.y1()),
					moveTo(r.x2(), interpolate(r.topRight(), r.bottomRight(), r.y1(), r.y2())),
					lineTo(interpolate(r.bottomLeft(), r.bottomRight(), r.x1(), r.x2()), r.y2()),
			};
		}
	},

	T1011(11) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{moveTo(r.x2(),
					interpolate(r.topRight(), r.bottomRight(), r.y1(), r.y2())),
					new MyPoint(interpolate(r.topLeft(), r.topRight(), r.x1(), r.x2()),
							r.y1(), SegmentType.LINE_TO)};
		}

	},

	T1100(12) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return T0011.getPoints(r);
		}
	},

	T1101(13) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{
					moveTo(r.x2(), interpolate(r.bottomRight(), r.topRight(), r.y2(),
							r.y1())),
					new MyPoint(interpolate(r.bottomRight(), r.bottomLeft(), r.x2(), r.x1()),
							r.y2(), SegmentType.LINE_TO)};
		}

		@Override
		public GColor color() {
			return GColor.DARK_RED;
		}
	},

	T1110(14) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{
					moveTo(r.x1(), interpolate(r.bottomLeft(), r.topLeft(), r.y2(),
							r.y1())),
					new MyPoint(interpolate(r.bottomLeft(), r.bottomRight(), r.x1(), r.x2()),
							r.y2(), SegmentType.LINE_TO)};
		}
	},

	T1111(15) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{
					moveTo(interpolate(r.topLeft(), r.topRight(), r.x1(), r.x2()),
							interpolate(r.topRight(), r.bottomRight(), r.y1(), r.y2())),
					lineTo(interpolate(r.topLeft(), r.topRight(), r.x1(), r.x2()),
							interpolate(r.topRight(), r.bottomRight(), r.y1(), r.y2()))
			};
		}
	},

	/**
	 * invalid configuration. expression value is undefined / infinity for at
	 * least one of the corner
	 */
	T_INV(-1),

	EMPTY(0) {
		@Override
		public String toString() {
			return "-";
		}
	},

	VALID(255);

	private final int flag;

	private static Map<Integer, BernsteinMarchingConfig> map = new HashMap<>();

	private static MyPoint moveTo(double x, double y) {
		return new MyPoint(x, y, SegmentType.MOVE_TO);
	}

	private static MyPoint lineTo(double x, double y) {
		return new MyPoint(x, y, SegmentType.LINE_TO);
	}

	static {
		for (BernsteinMarchingConfig config : values()) {
			map.put(config.flag, config);
		}
	}

	BernsteinMarchingConfig(int flag) {
		this.flag = flag;
	}

	@Override
	public int flag() {
		return flag;
	}

	@Override
	public MyPoint[] getPoints(MarchingRect r) {
		return null;
	}

	@Override
	public boolean isValid() {
		return this == VALID;
	}

	@Override
	public boolean isInvalid() {
		return this == T_INV;
	}

	@Override
	public boolean isEmpty() {
		return this == EMPTY;
	}

	public static BernsteinMarchingConfig fromFlag(int config) {
		return map.getOrDefault(config, T_INV);
	}

	private static double minAbs(double a, double b) {
		return Math.min(Math.abs(a), Math.abs(b));
	}

	public GColor color() {
		return GColor.BLACK;
	}
}