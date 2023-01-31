package org.geogebra.common.kernel.interval.operators;

import org.geogebra.common.kernel.interval.Interval;

interface UniIntervalOperator {
	Interval exec(Interval interval);
}
