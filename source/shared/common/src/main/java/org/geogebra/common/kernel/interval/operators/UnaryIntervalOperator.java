package org.geogebra.common.kernel.interval.operators;

import org.geogebra.common.kernel.interval.Interval;

interface UnaryIntervalOperator {
	Interval exec(Interval interval);
}
