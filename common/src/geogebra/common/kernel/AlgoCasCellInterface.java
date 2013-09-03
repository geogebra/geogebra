package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoCasCell;

/**
 * Interface for AlgoDependentCasCell, used to separate CAS from the rest of Kernel
 * @author Zbynek
 *
 */
public interface AlgoCasCellInterface {
	/**
	 * @return output CAS cell
	 */
	GeoCasCell getCasCell();

}
