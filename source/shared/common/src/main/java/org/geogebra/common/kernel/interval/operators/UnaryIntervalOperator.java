package org.geogebra.common.kernel.interval.operators;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.interval.Interval;

interface UnaryIntervalOperator {
	@MissingDoc
	Interval exec(Interval interval);
}
