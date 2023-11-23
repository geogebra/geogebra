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

	void startMultiuser(String sharingKey);

	void terminateMultiuser(Material mat, MaterialCallbackI after);

	void saveAndTerminateMultiuser(Material mat, MaterialCallbackI after);

	void disconnectMultiuser();

	void assign();

	void setAssign(boolean isAssign);

	boolean isAssign();
}
