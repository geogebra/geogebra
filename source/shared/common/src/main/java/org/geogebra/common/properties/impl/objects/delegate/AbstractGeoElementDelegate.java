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

package org.geogebra.common.properties.impl.objects.delegate;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;

public abstract class AbstractGeoElementDelegate implements GeoElementDelegate {

	protected final GeoElement element;

	/**
	 * Create a new AbstractGeoElementDelegate
	 * @param element element
	 * @throws NotApplicablePropertyException if not applicable
	 */
	public AbstractGeoElementDelegate(GeoElement element) throws NotApplicablePropertyException {
		this.element = element;
		checkIsApplicable();
	}

	@Override
	public GeoElement getElement() {
		return element;
	}

	@Override
	public void checkIsApplicable() throws NotApplicablePropertyException {
		if (!checkIsApplicable(element)) {
			throw new NotApplicablePropertyException(element, this);
		}
	}

	protected abstract boolean checkIsApplicable(GeoElement element);

	protected boolean isApplicableToGeoList(GeoList list) {
		for (int i = 0; i < list.size(); i++) {
			if (!checkIsApplicable(list.get(i))) {
				return false;
			}
		}
		return list.elements().allMatch(this::checkIsApplicable);
	}

	protected static boolean isTextOrInput(GeoElement element) {
		return element instanceof GeoText || element instanceof GeoInputBox;
	}
}
