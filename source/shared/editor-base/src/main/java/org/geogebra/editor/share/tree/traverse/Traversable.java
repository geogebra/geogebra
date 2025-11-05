/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.tree.traverse;

import org.geogebra.editor.share.tree.Node;

/**
 * An object that can be traversed and processed by a Traversing object.
 */
public interface Traversable {

    /**
     * Traverse and process this object.
     * @param traversing The object that processes.
     * @return resulting object
     */
    Node traverse(Traversing traversing);

}
