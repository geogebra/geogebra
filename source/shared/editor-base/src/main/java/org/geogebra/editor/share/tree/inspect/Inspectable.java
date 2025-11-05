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

/**
 * An object that can be inspected if it has a certain property.
 */
public interface Inspectable {

    /**
     * Traverse and inspect this object.
     *
     * @param inspecting The object that is being inspected.
     * @return true if it has the property check by the inspecting object.
     */
    boolean inspect(Inspecting inspecting);

}
