package org.geogebra.common.main;

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

	void terminateMultiuser(String sharingKey);

	void disconnectMultiuser();
}
