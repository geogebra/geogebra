/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.analysis;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.util.FastMath;



/**
 * Base class for {@link BivariateRealFunction} that can be composed with other functions.
 *
 * @since 2.1
 * @version $Revision: 1073498 $ $Date: 2011-02-22 21:57:26 +0100 (mar. 22 févr. 2011) $
 * @deprecated in 2.2
 */
@Deprecated
public abstract class BinaryFunction implements BivariateRealFunction {

    /** The + operator method wrapped as a {@link BinaryFunction}. */
    public static final BinaryFunction ADD = new BinaryFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double x, double y) {
            return x + y;
        }
    };

    /** The - operator method wrapped as a {@link BinaryFunction}. */
    public static final BinaryFunction SUBTRACT = new BinaryFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double x, double y) {
            return x - y;
        }
    };

    /** The * operator method wrapped as a {@link BinaryFunction}. */
    public static final BinaryFunction MULTIPLY = new BinaryFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double x, double y) {
            return x * y;
        }
    };

    /** The / operator method wrapped as a {@link BinaryFunction}. */
    public static final BinaryFunction DIVIDE = new BinaryFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double x, double y) {
            return x / y;
        }
    };

    /** The {@code FastMath.pow} method wrapped as a {@link BinaryFunction}. */
    public static final BinaryFunction POW = new BinaryFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double x, double y) {
            return FastMath.pow(x, y);
        }
    };

    /** The {@code FastMath.atan2} method wrapped as a {@link BinaryFunction}. */
    public static final BinaryFunction ATAN2 = new BinaryFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double x, double y) {
            return FastMath.atan2(x, y);
        }
    };

    /** {@inheritDoc} */
    public abstract double value(double x, double y) throws FunctionEvaluationException;

    /** Get a composable function by fixing the first argument of the instance.
     * @param fixedX fixed value of the first argument
     * @return a function such that {@code f.value(y) == value(fixedX, y)}
     */
    public ComposableFunction fix1stArgument(final double fixedX) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws FunctionEvaluationException {
                return BinaryFunction.this.value(fixedX, x);
            }
        };
    }

    /** Get a composable function by fixing the second argument of the instance.
     * @param fixedY fixed value of the second argument
     * @return a function such that {@code f.value(x) == value(x, fixedY)}
     */
    public ComposableFunction fix2ndArgument(final double fixedY) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws FunctionEvaluationException {
                return BinaryFunction.this.value(x, fixedY);
            }
        };
    }

}
