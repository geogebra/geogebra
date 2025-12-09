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

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.web.full.gui.images.AppResources;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.user.client.ui.Tree;

/** Helper class to hide default tree images for collapse / expand */
public class TreeImages implements Tree.Resources {

	@Override
	public ImageResource treeClosed() {
		return AppResources.INSTANCE.empty();
	}

	@Override
	public ImageResource treeLeaf() {
		return AppResources.INSTANCE.empty();
	}

	@Override
	public ImageResource treeOpen() {
		return AppResources.INSTANCE.empty();
	}
}