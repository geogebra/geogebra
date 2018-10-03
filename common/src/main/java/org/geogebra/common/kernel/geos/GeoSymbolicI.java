package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.arithmetic.AssignmentType;

public interface GeoSymbolicI {

	void setError(String key);

	void setAssignmentType(AssignmentType assignmentType);

	void computeOutput();

}
