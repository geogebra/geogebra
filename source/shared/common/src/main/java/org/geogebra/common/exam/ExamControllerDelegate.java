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

package org.geogebra.common.exam;

import javax.annotation.CheckForNull;

import org.geogebra.common.move.ggtapi.models.Material;

/**
 * The idea of this delegate is to "externalize" any functionality that does not fit into
 * the {@link ExamController} itself (either because it's platform-specific behaviour,
 * or is functionality outside the responsibility of the ExamController).
 *
 * @apiNote The `exam` prefix is intended to clearly identify the functions (in the implementing
 * class) as being tied to exam mode.
 */
public interface ExamControllerDelegate {

	/**
	 * Clear (reset) all subapps
	 */

	void examClearApps();

	/**
	 * Clear the clipboard.
	 */
	void examClearClipboard();

	/**
	 * Set the material as the active material in the current subapp.
	 *
	 * @param material A material.
	 */
	void examSetActiveMaterial(@CheckForNull Material material);

	/**
	 * @return The current active material, or null.
	 */
	@CheckForNull Material examGetActiveMaterial();
}
