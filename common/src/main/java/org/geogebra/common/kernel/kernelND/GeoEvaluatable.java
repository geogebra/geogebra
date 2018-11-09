package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.arithmetic.Functional;

public interface GeoEvaluatable extends Functional, GeoElementND {
	/**
	 * @return table index
	 */
	public int getTableColumn();

	public void setTableColumn(int column);

	public void setPointsVisible(boolean pointsVisible);

	public boolean isPointsVisible();

}
