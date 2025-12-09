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

package org.geogebra.keyboard.base.model;

import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.ResourceType;

/**
 * Modifies properties (resource, action, visual style) of a key.
 */
public interface KeyModifier {

    /**
     * @param resourceName original resource name
     * @param resourceType original resource type
     * @return new resource name
     */
    String modifyResourceName(String resourceName, ResourceType resourceType);

    /**
     * @param actionName original action name
     * @param actionType original action type
     * @return new action name
     */
    String modifyActionName(String actionName, ActionType actionType);

    /**
     * @param background original background
     * @param actionType original action type
     * @param actionName original action name
     * @return new background
     */
    Background modifyBackground(Background background, ActionType actionType, String actionName);
}
