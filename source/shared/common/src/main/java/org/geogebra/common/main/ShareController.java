/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.main;

import org.geogebra.common.annotation.MissingDoc;
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

	@MissingDoc
	void disconnectMultiuser();

	@MissingDoc
	void assign();

	/**
	 * @param isAssign whether the resource is shared for assignment
	 */
	void setAssign(boolean isAssign);

	@MissingDoc
	boolean isAssign();
}
