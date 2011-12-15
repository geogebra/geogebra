/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * @author Denis M. Kishenko
 */
// This file was later modified by GeoGebra Inc.

package java.awt.geom;

public interface PathIterator {

    public static final int WIND_EVEN_ODD = 0;
    public static final int WIND_NON_ZERO = 1;

    public static final int SEG_MOVETO  = 0;
    public static final int SEG_LINETO  = 1;
    public static final int SEG_QUADTO  = 2;
    public static final int SEG_CUBICTO = 3;
    public static final int SEG_CLOSE   = 4;

    public int getWindingRule();

    public boolean isDone();

    public void next();

    public int currentSegment(float[] coords);

    public int currentSegment(double[] coords);

}
