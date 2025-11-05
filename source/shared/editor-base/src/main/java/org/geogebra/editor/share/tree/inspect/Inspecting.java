/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.tree.inspect;

import org.geogebra.editor.share.tree.Node;

/**
 * An object that looks for a certain property of an object.
 */
public interface Inspecting {

    /**
     * @param node node
     * @return whether the node has this property
     */
    boolean check(Node node);

}
