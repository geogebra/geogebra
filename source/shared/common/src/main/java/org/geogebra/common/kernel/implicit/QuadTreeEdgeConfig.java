package org.geogebra.common.kernel.implicit;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;

public enum QuadTreeEdgeConfig implements MarchingConfig {
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
			return new MyPoint[]{new MyPoint(r.x1(),
					GeoImplicitCurve.interpolate(r.bottomLeft(), r.topLeft(), r.y2(),
							r.y1()), SegmentType.MOVE_TO),
					new MyPoint(
							GeoImplicitCurve.interpolate(r.bottomLeft(), r.bottomRight(), r.x1(),
									r.x2()),
							r.y2(), SegmentType.LINE_TO)};
		}

		@Override
		public double getQ1(MarchingRect r) {
			return minAbs(r.bottomLeft(), r.topLeft());
		}

		@Override
		public double getQ2(MarchingRect r) {
			return minAbs(r.bottomLeft(), r.bottomRight());
		}
	},

	/**
	 * bottom right corner is inside / outside
	 */
	T0010(2) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{new MyPoint(r.x2(),
					GeoImplicitCurve.interpolate(r.bottomRight(), r.topRight(), r.y2(),
							r.y1()), SegmentType.MOVE_TO),
					new MyPoint(
							GeoImplicitCurve.interpolate(r.bottomRight(), r.bottomLeft(), r.x2(),
									r.x1()),
							r.y2(), SegmentType.LINE_TO)};
		}

		@Override
		public double getQ1(MarchingRect r) {
			return minAbs(r.bottomRight(), r.topRight());
		}

		@Override
		public double getQ2(MarchingRect r) {
			return minAbs(r.bottomRight(), r.bottomLeft());
		}
	},

	/**
	 * both corners at the bottom are inside / outside
	 */
	T0011(3) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{new MyPoint(r.x1(),
					GeoImplicitCurve.interpolate(r.topLeft(), r.bottomLeft(), r.y1(),
							r.y2()), SegmentType.MOVE_TO),
					new MyPoint(r.x2(),
							GeoImplicitCurve.interpolate(r.topRight(), r.bottomRight(), r.y1(),
									r.y2()),
							SegmentType.LINE_TO)};
		}

		@Override
		public double getQ1(MarchingRect r) {
			return minAbs(r.topLeft(), r.bottomLeft());
		}

		@Override
		public double getQ2(MarchingRect r) {
			return minAbs(r.topRight(), r.bottomRight());
		}
	},

	/**
	 * top left corner is inside / outside
	 */
	T0100(4) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{
					new MyPoint(r.x2(),
							GeoImplicitCurve.interpolate(r.topRight(), r.bottomRight(), r.y1(),
									r.y2()), SegmentType.MOVE_TO),
					new MyPoint(
							GeoImplicitCurve.interpolate(r.topRight(), r.topLeft(), r.x2(), r.x1()),
							r.y1(), SegmentType.LINE_TO)
			};
		}

		@Override
		public double getQ1(MarchingRect r) {
			return minAbs(r.topRight(), r.bottomRight());
		}

		@Override
		public double getQ2(MarchingRect r) {
			return minAbs(r.topRight(), r.topLeft());
		}
	},

	/**
	 * opposite corners are inside / outside. NOTE: This configuration is
	 * regarded as invalid
	 */
	T0101(5),

	/**
	 * both the corners at the left are inside / outside
	 */
	T0110(6) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{new MyPoint(
					GeoImplicitCurve.interpolate(r.topLeft(), r.topRight(), r.x1(), r.x2()),
					r.y1(), SegmentType.MOVE_TO),
					new MyPoint(GeoImplicitCurve.interpolate(r.bottomLeft(), r.bottomRight(),
							r.x1(), r.x2()), r.y2(), SegmentType.LINE_TO)};
		}

		@Override
		public double getQ1(MarchingRect r) {
			return minAbs(r.topLeft(), r.topRight());
		}

		@Override
		public double getQ2(MarchingRect r) {
			return minAbs(r.bottomLeft(), r.bottomRight());
		}
	},

	/**
	 * only top left corner is inside / outside
	 */
	T0111(7) {
		@Override
		public MyPoint[] getPoints(MarchingRect r) {
			return new MyPoint[]{new MyPoint(r.x1(),
					GeoImplicitCurve.interpolate(r.topLeft(), r.bottomLeft(), r.y1(), r.y2()),
					SegmentType.MOVE_TO),
					new MyPoint(GeoImplicitCurve.interpolate(r.topLeft(), r.topRight(), r.x1(),
							r.x2()), r.y1(), SegmentType.LINE_TO)};
		}

		@Override
		public double getQ1(MarchingRect r) {
			return minAbs(r.bottomLeft(), r.topLeft());
		}

		@Override
		public double getQ2(MarchingRect r) {
			return minAbs(r.topLeft(), r.topRight());
		}
	},

	/**
	 * invalid configuration. expression value is undefined / infinity for at
	 * least one of the corner
	 */
	T_INV(-1),

	EMPTY(0),

	VALID(10);

	private final int flag;

	private static Map<Integer, QuadTreeEdgeConfig> map = new HashMap<>();

	static {
		for (QuadTreeEdgeConfig config : QuadTreeEdgeConfig.values()) {
			map.put(config.flag, config);
		}
	}

	QuadTreeEdgeConfig(int flag) {
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

	public double getQ1(MarchingRect r) {
		return 0;
	}

	public double getQ2(MarchingRect r) {
		return 0;
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

	public static QuadTreeEdgeConfig fromFlag(int config) {
		return map.getOrDefault(config, T_INV);
	}

	private static double minAbs(double a, double b) {
		return Math.min(Math.abs(a), Math.abs(b));
	}

}