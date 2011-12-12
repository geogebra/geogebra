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
/* This file was modified by GeoGebra Inc. */
package org.apache.commons.math.analysis;

import org.apache.commons.math.FunctionEvaluationException;


/**
 * Base class for {@link UnivariateRealFunction} that can be composed with other functions.
 *
 * @since 2.1
 * @version $Revision: 924453 $ $Date: 2010-03-17 16:05:20 -0400 (Wed, 17 Mar 2010) $
 */
public abstract class ComposableFunction implements UnivariateRealFunction {

    /** The constant function always returning 0. */
    public static final ComposableFunction ZERO = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return 0;
        }
    };

    /** The constant function always returning 1. */
    public static final ComposableFunction ONE = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return 1;
        }
    };

    /** The identity function. */
    public static final ComposableFunction IDENTITY = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return d;
        }
    };

    /** The {@code Math.abs} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction ABS = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.abs(d);
        }
    };

    /** The - operator wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction NEGATE = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return -d;
        }
    };

    /** The invert operator wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction INVERT = new ComposableFunction () {
        /** {@inheritDoc} */
        @Override
        public double value(double d){
            return 1/d;
        }
    };

    /** The {@code Math.sin} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction SIN = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.sin(d);
        }
    };

    /** The {@code Math.sqrt} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction SQRT = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.sqrt(d);
        }
    };

    /** The {@code Math.sinh} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction SINH = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.sinh(d);
        }
    };

    /** The {@code Math.exp} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction EXP = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.exp(d);
        }
    };

    /** The {@code Math.expm1} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction EXPM1 = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.expm1(d);
        }
    };

    /** The {@code Math.asin} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction ASIN = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.asin(d);
        }
    };

    /** The {@code Math.atan} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction ATAN = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.atan(d);
        }
    };

    /** The {@code Math.tan} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction TAN = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.tan(d);
        }
    };

    /** The {@code Math.tanh} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction TANH = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.tanh(d);
        }
    };

    /** The {@code Math.cbrt} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction CBRT = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.cbrt(d);
        }
    };

    /** The {@code Math.ceil} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction CEIL = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.ceil(d);
        }
    };

    /** The {@code Math.floor} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction FLOOR = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.floor(d);
        }
    };

    /** The {@code Math.log} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction LOG = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.log(d);
        }
    };

    /** The {@code Math.log10} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction LOG10 = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.log10(d);
        }
    };

    /** The {@code Math.log1p} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction LOG1P = new ComposableFunction () {
        @Override
        public double value(double d){
            return Math.log1p(d);
        }
    };

    /** The {@code Math.cos} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction COS = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.cos(d);
        }
    };

    /** The {@code Math.abs} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction ACOS = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.acos(d);
        }
    };

    /** The {@code Math.cosh} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction COSH = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.cosh(d);
        }
    };

    /** The {@code Math.rint} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction RINT = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.rint(d);
        }
    };

    /** The {@code Math.signum} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction SIGNUM = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
            return Math.signum(d);
        }
    };

    /** The {@code Math.ulp} method wrapped as a {@link ComposableFunction}. */
    public static final ComposableFunction ULP = new ComposableFunction() {
        /** {@inheritDoc} */
        @Override
        public double value(double d) {
        	return Double.MIN_VALUE; //AR return Math.ulp(d);
        }
    };

    /** Precompose the instance with another function.
     * <p>
     * The composed function h created by {@code h = g.of(f)} is such
     * that {@code h.value(x) == g.value(f.value(x))} for all x.
     * </p>
     * @param f function to compose with
     * @return a new function which computes {@code this.value(f.value(x))}
     * @see #postCompose(UnivariateRealFunction)
     */
    public ComposableFunction of(final UnivariateRealFunction f) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws FunctionEvaluationException {
                return ComposableFunction.this.value(f.value(x));
            }
        };
    }

    /** Postcompose the instance with another function.
     * <p>
     * The composed function h created by {@code h = g.postCompose(f)} is such
     * that {@code h.value(x) == f.value(g.value(x))} for all x.
     * </p>
     * @param f function to compose with
     * @return a new function which computes {@code f.value(this.value(x))}
     * @see #of(UnivariateRealFunction)
     */
    public ComposableFunction postCompose(final UnivariateRealFunction f) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws FunctionEvaluationException {
                return f.value(ComposableFunction.this.value(x));
            }
        };
    }

    /**
     * Return a function combining the instance and another function.
     * <p>
     * The function h created by {@code h = g.combine(f, combiner)} is such that
     * {@code h.value(x) == combiner.value(g.value(x), f.value(x))} for all x.
     * </p>
     * @param f function to combine with the instance
     * @param combiner bivariate function used for combining
     * @return a new function which computes {@code combine.value(this.value(x), f.value(x))}
     */
    public ComposableFunction combine(final UnivariateRealFunction f,
                                      final BivariateRealFunction combiner) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws FunctionEvaluationException {
                return combiner.value(ComposableFunction.this.value(x), f.value(x));
            }
        };
    }

    /**
     * Return a function adding the instance and another function.
     * @param f function to combine with the instance
     * @return a new function which computes {@code this.value(x) + f.value(x)}
     */
    public ComposableFunction add(final UnivariateRealFunction f) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws FunctionEvaluationException {
                return ComposableFunction.this.value(x) + f.value(x);
            }
        };
    }

    /**
     * Return a function adding a constant term to the instance.
     * @param a term to add
     * @return a new function which computes {@code this.value(x) + a}
     */
    public ComposableFunction add(final double a) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws FunctionEvaluationException {
                return ComposableFunction.this.value(x) + a;
            }
        };
    }

    /**
     * Return a function subtracting another function from the instance.
     * @param f function to combine with the instance
     * @return a new function which computes {@code this.value(x) - f.value(x)}
     */
    public ComposableFunction subtract(final UnivariateRealFunction f) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws FunctionEvaluationException {
                return ComposableFunction.this.value(x) - f.value(x);
            }
        };
    }

    /**
     * Return a function multiplying the instance and another function.
     * @param f function to combine with the instance
     * @return a new function which computes {@code this.value(x) * f.value(x)}
     */
    public ComposableFunction multiply(final UnivariateRealFunction f) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws FunctionEvaluationException {
                return ComposableFunction.this.value(x) * f.value(x);
            }
        };
    }

    /**
     * Return a function scaling the instance by a constant factor.
     * @param scaleFactor constant scaling factor
     * @return a new function which computes {@code this.value(x) * scaleFactor}
     */
    public ComposableFunction multiply(final double scaleFactor) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws FunctionEvaluationException {
                return ComposableFunction.this.value(x) * scaleFactor;
            }
        };
    }
    /**
     * Return a function dividing the instance by another function.
     * @param f function to combine with the instance
     * @return a new function which computes {@code this.value(x) / f.value(x)}
     */
    public ComposableFunction divide(final UnivariateRealFunction f) {
        return new ComposableFunction() {
            @Override
            /** {@inheritDoc} */
            public double value(double x) throws FunctionEvaluationException {
                return ComposableFunction.this.value(x) / f.value(x);
            }
        };
    }

    /**
     * Generates a function that iteratively apply instance function on all
     * elements of an array.
     * <p>
     * The generated function behaves as follows:
     * <ul>
     *   <li>initialize result = initialValue</li>
     *   <li>iterate: {@code result = combiner.value(result,
     *   this.value(nextMultivariateEntry));}</li>
     *   <li>return result</li>
     * </ul>
     * </p>
     * @param combiner combiner to use between entries
     * @param initialValue initial value to use before first entry
     * @return a new function that iteratively applie instance function on all
     * elements of an array.
     */
    public MultivariateRealFunction asCollector(final BivariateRealFunction combiner,
                                                final double initialValue) {
        return new MultivariateRealFunction() {
            /** {@inheritDoc} */
            public double value(double[] point)
                throws FunctionEvaluationException, IllegalArgumentException {
                double result = initialValue;
                for (final double entry : point) {
                    result = combiner.value(result, ComposableFunction.this.value(entry));
                }
                return result;
            }
        };
    }

    /**
     * Generates a function that iteratively apply instance function on all
     * elements of an array.
     * <p>
     * Calling this method is equivalent to call {@link
     * #asCollector(BivariateRealFunction, double) asCollector(BivariateRealFunction, 0.0)}.
     * </p>
     * @param combiner combiner to use between entries
     * @return a new function that iteratively applie instance function on all
     * elements of an array.
     * @see #asCollector(BivariateRealFunction, double)
     */
    public  MultivariateRealFunction asCollector(final BivariateRealFunction combiner) {
        return asCollector(combiner, 0.0);
    }

    /**
     * Generates a function that iteratively apply instance function on all
     * elements of an array.
     * <p>
     * Calling this method is equivalent to call {@link
     * #asCollector(BivariateRealFunction, double) asCollector(BinaryFunction.ADD, initialValue)}.
     * </p>
     * @param initialValue initial value to use before first entry
     * @return a new function that iteratively applie instance function on all
     * elements of an array.
     * @see #asCollector(BivariateRealFunction, double)
     * @see BinaryFunction#ADD
     */
    public  MultivariateRealFunction asCollector(final double initialValue) {
        return asCollector(BinaryFunction.ADD, initialValue);
    }

    /**
     * Generates a function that iteratively apply instance function on all
     * elements of an array.
     * <p>
     * Calling this method is equivalent to call {@link
     * #asCollector(BivariateRealFunction, double) asCollector(BinaryFunction.ADD, 0.0)}.
     * </p>
     * @return a new function that iteratively applie instance function on all
     * elements of an array.
     * @see #asCollector(BivariateRealFunction, double)
     * @see BinaryFunction#ADD
     */
    public  MultivariateRealFunction asCollector() {
        return asCollector(BinaryFunction.ADD, 0.0);
    }

    /** {@inheritDoc} */
    public abstract double value(double x) throws FunctionEvaluationException;

}
