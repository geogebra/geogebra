/* $Id: ConstraintUtilities.java 5 2010-03-19 15:40:39Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax.internal;

/**
 * Simple utility methods for enforcing various types of data constraints.
 *
 * @author  David McKain
 * @version $Revision: 5 $
 */
public final class ConstraintUtilities {

    /**
     * Checks that the given object is non-null, throwing an
     * IllegalArgumentException if the check fails. If the check succeeds then
     * nothing happens.
     *
     * @param value object to test
     * @param objectName name to give to supplied Object when constructing Exception message.
     *
     * @throws IllegalArgumentException if <tt>value</tt> is null.
     */
    public static void ensureNotNull(Object value, String objectName) {
        if (value==null) {
            throw new IllegalArgumentException(objectName + " must not be null");
        }
    }
}
