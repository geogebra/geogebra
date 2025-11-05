/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.serializer;

import org.geogebra.editor.share.tree.Formula;

/**
 * Serializes the internal format into String format. See also {@link SerializerAdapter}.
 */
public interface Serializer {

    /**
     * @param formula formula
     * @return serialized formula
     */
    String serialize(Formula formula);

}
