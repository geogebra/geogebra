// GenericsNote: Converted.
/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections15.functors;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

/**
 * Transformer implementation that returns the value held in a specified map
 * using the input parameter as a key.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public final class MapTransformer <I,O> implements Transformer<I, O>, Serializable {

    /**
     * Serial version UID
     */
    static final long serialVersionUID = 862391807045468939L;

    /**
     * The map of data to lookup in
     */
    private final Map<I, O> iMap;

    /**
     * Factory to create the transformer.
     * <p/>
     * If the map is null, a transformer that always returns null is returned.
     *
     * @param map the map, not cloned
     * @return the transformer
     */
    public static <I,O> Transformer<I, O> getInstance(Map<I, O> map) {
        if (map == null) {
            return ConstantTransformer.NULL_INSTANCE;
        }
        return new MapTransformer<I, O>(map);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param map the map to use for lookup, not cloned
     */
    private MapTransformer(Map<I, O> map) {
        super();
        iMap = map;
    }

    /**
     * Transforms the input to result by looking it up in a <code>Map</code>.
     *
     * @param input the input object to transform
     * @return the transformed result
     */
    public O transform(I input) {
        return iMap.get(input);
    }

    /**
     * Gets the map to lookup in.
     *
     * @return the map
     * @since Commons Collections 3.1
     */
    public Map<I, O> getMap() {
        return iMap;
    }

}
