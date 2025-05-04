package org.geogebra.common.main;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;

/**
 * Class to handle material sharing.
 *
 * @author laszlo
 *
 */
public interface ShareController {

	/** Share with dialog */
	void share();

	/** Share natively */
	void getBase64();

	/**
	 * Start multiuser session
	 * @param sharingKey session ID, coincides with resource sharing key
	 */
	void startMultiuser(String sharingKey);

	/**
	 * Terminate multiuser session
	 * @param mat resource
	 * @param after callback
	 */
	void terminateMultiuser(Material mat, MaterialCallbackI after);

	/**
	 * Save local copy and terminate multiuser
	 * @param mat resource
	 * @param after callback
	 */
	void saveAndTerminateMultiuser(Material mat, MaterialCallbackI after);

	void disconnectMultiuser();

	void assign();

	/**
	 * @param isAssign whether the resource is shared for assignment
	 */
	void setAssign(boolean isAssign);

	boolean isAssign();
}
