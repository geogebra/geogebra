package com.himamis.retex.editor.share.serializer;

import com.himamis.retex.editor.share.model.MathFormula;

/**
 * Serializes the internal format into String format. See also {@link SerializerAdapter}.
 */
public interface Serializer {

    /**
     * @param formula formula
     * @return serialized formula
     */
    String serialize(MathFormula formula);

}
