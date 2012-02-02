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
package org.apache.commons.math.exception.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Enumeration for localized messages formats used in exceptions messages.
 * <p>
 * The constants in this enumeration represent the available
 * formats as localized strings. These formats are intended to be
 * localized using simple properties files, using the constant
 * name as the key and the property value as the message format.
 * The source English format is provided in the constants themselves
 * to serve both as a reminder for developers to understand the parameters
 * needed by each format, as a basis for translators to create
 * localized properties files, and as a default format if some
 * translation is missing.
 * </p>
 * @since 2.2
 * @version $Revision: 1073165 $ $Date: 2011-02-21 23:04:14 +0100 (lun. 21 févr. 2011) $
 */
public enum LocalizedFormats implements Localizable {

    // CHECKSTYLE: stop MultipleVariableDeclarations
    // CHECKSTYLE: stop JavadocVariable

    ARGUMENT_OUTSIDE_DOMAIN("Argument {0} outside domain [{1} ; {2}]"),
    ARRAY_SIZES_SHOULD_HAVE_DIFFERENCE_1("array sizes should have difference 1 ({0} != {1} + 1)"),
    ARRAY_SUMS_TO_ZERO("array sums to zero"),
    ASSYMETRIC_EIGEN_NOT_SUPPORTED("eigen decomposition of assymetric matrices not supported yet"),
    AT_LEAST_ONE_COLUMN("matrix must have at least one column"),
    AT_LEAST_ONE_ROW("matrix must have at least one row"),
    BANDWIDTH_OUT_OF_INTERVAL("bandwidth must be in the interval [0,1], but got {0}"),
    BINOMIAL_INVALID_PARAMETERS_ORDER("must have n >= k for binomial coefficient (n,k), got n = {0}, k = {1}"),
    BINOMIAL_NEGATIVE_PARAMETER("must have n >= 0 for binomial coefficient (n,k), got n = {0}"),
    CANNOT_CLEAR_STATISTIC_CONSTRUCTED_FROM_EXTERNAL_MOMENTS("statistics constructed from external moments cannot be cleared"),
    CANNOT_COMPUTE_0TH_ROOT_OF_UNITY("cannot compute 0-th root of unity, indefinite result"),
    CANNOT_COMPUTE_BETA_DENSITY_AT_0_FOR_SOME_ALPHA("cannot compute beta density at 0 when alpha = {0,number}"),
    CANNOT_COMPUTE_BETA_DENSITY_AT_1_FOR_SOME_BETA("cannot compute beta density at 1 when beta = %.3g"),
    CANNOT_COMPUTE_NTH_ROOT_FOR_NEGATIVE_N("cannot compute nth root for null or negative n: {0}"),
    CANNOT_CONVERT_OBJECT_TO_FRACTION("cannot convert given object to a fraction number: {0}"),
    CANNOT_DISCARD_NEGATIVE_NUMBER_OF_ELEMENTS("cannot discard a negative number of elements ({0})"),
    CANNOT_FORMAT_INSTANCE_AS_3D_VECTOR("cannot format a {0} instance as a 3D vector"),
    CANNOT_FORMAT_INSTANCE_AS_COMPLEX("cannot format a {0} instance as a complex number"),
    CANNOT_FORMAT_INSTANCE_AS_REAL_VECTOR("cannot format a {0} instance as a real vector"),
    CANNOT_FORMAT_OBJECT_TO_FRACTION("cannot format given object as a fraction number"),
    CANNOT_INCREMENT_STATISTIC_CONSTRUCTED_FROM_EXTERNAL_MOMENTS("statistics constructed from external moments cannot be incremented"),
    CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR("cannot normalize a zero norm vector"),
    CANNOT_RETRIEVE_AT_NEGATIVE_INDEX("elements cannot be retrieved from a negative array index {0}"),
    CANNOT_SET_AT_NEGATIVE_INDEX("cannot set an element at a negative index {0}"),
    CANNOT_SUBSTITUTE_ELEMENT_FROM_EMPTY_ARRAY("cannot substitute an element from an empty array"),
    CANNOT_TRANSFORM_TO_DOUBLE("Conversion Exception in Transformation: {0}"),
    CARDAN_ANGLES_SINGULARITY("Cardan angles singularity"),
    CLASS_DOESNT_IMPLEMENT_COMPARABLE("class ({0}) does not implement Comparable"),
    CLOSEST_ORTHOGONAL_MATRIX_HAS_NEGATIVE_DETERMINANT("the closest orthogonal matrix has a negative determinant {0}"),
    COLUMN_INDEX_OUT_OF_RANGE("column index {0} out of allowed range [{1}, {2}]"),
    CONTINUED_FRACTION_INFINITY_DIVERGENCE("Continued fraction convergents diverged to +/- infinity for value {0}"),
    CONTINUED_FRACTION_NAN_DIVERGENCE("Continued fraction diverged to NaN for value {0}"),
    CONTRACTION_CRITERIA_SMALLER_THAN_EXPANSION_FACTOR("contraction criteria ({0}) smaller than the expansion factor ({1}).  This would lead to a never ending loop of expansion and contraction as a newly expanded internal storage array would immediately satisfy the criteria for contraction."),
    CONTRACTION_CRITERIA_SMALLER_THAN_ONE("contraction criteria smaller than one ({0}).  This would lead to a never ending loop of expansion and contraction as an internal storage array length equal to the number of elements would satisfy the contraction criteria."),
    CONVERGENCE_FAILED("convergence failed"),
    CUMULATIVE_PROBABILITY_RETURNED_NAN("Cumulative probability function returned NaN for argument {0} p = {1}"),
    DIFFERENT_ROWS_LENGTHS("some rows have length {0} while others have length {1}"),
    DIGEST_NOT_INITIALIZED("digest not initialized"),
    DIMENSIONS_MISMATCH_2x2("dimensions mismatch: got {0}x{1} but expected {2}x{3}"),
    DIMENSIONS_MISMATCH_SIMPLE("dimensions mismatch {0} != {1}"), /* keep */
    DISCRETE_CUMULATIVE_PROBABILITY_RETURNED_NAN("Discrete cumulative probability function returned NaN for argument {0}"),
    DISTRIBUTION_NOT_LOADED("distribution not loaded"),
    DUPLICATED_ABSCISSA("Abscissa {0} is duplicated at both indices {1} and {2}"),
    EMPTY_CLUSTER_IN_K_MEANS("empty cluster in k-means"),
    EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY("empty polynomials coefficients array"), /* keep */
    EMPTY_SELECTED_COLUMN_INDEX_ARRAY("empty selected column index array"),
    EMPTY_SELECTED_ROW_INDEX_ARRAY("empty selected row index array"),
    EMPTY_STRING_FOR_IMAGINARY_CHARACTER("empty string for imaginary character"),
    ENDPOINTS_NOT_AN_INTERVAL("endpoints do not specify an interval: [{0}, {1}]"),
    EQUAL_VERTICES_IN_SIMPLEX("equal vertices {0} and {1} in simplex configuration"),
    EULER_ANGLES_SINGULARITY("Euler angles singularity"),
    EVALUATION_FAILED("evaluation failed for argument = {0}"),
    EXPANSION_FACTOR_SMALLER_THAN_ONE("expansion factor smaller than one ({0})"),
    FACTORIAL_NEGATIVE_PARAMETER("must have n >= 0 for n!, got n = {0}"),
    FAILED_BRACKETING("number of iterations={0}, maximum iterations={1}, initial={2}, lower bound={3}, upper bound={4}, final a value={5}, final b value={6}, f(a)={7}, f(b)={8}"),
    FAILED_FRACTION_CONVERSION("Unable to convert {0} to fraction after {1} iterations"),
    FIRST_COLUMNS_NOT_INITIALIZED_YET("first {0} columns are not initialized yet"),
    FIRST_ELEMENT_NOT_ZERO("first element is not 0: {0}"),
    FIRST_ROWS_NOT_INITIALIZED_YET("first {0} rows are not initialized yet"),
    FRACTION_CONVERSION_OVERFLOW("Overflow trying to convert {0} to fraction ({1}/{2})"),
    FUNCTION_NOT_DIFFERENTIABLE("function is not differentiable"),
    FUNCTION_NOT_POLYNOMIAL("function is not polynomial"),
    GCD_OVERFLOW_32_BITS("overflow: gcd({0}, {1}) is 2^31"),
    GCD_OVERFLOW_64_BITS("overflow: gcd({0}, {1}) is 2^63"),
    HOLE_BETWEEN_MODELS_TIME_RANGES("{0} wide hole between models time ranges"),
    IDENTICAL_ABSCISSAS_DIVISION_BY_ZERO("identical abscissas x[{0}] == x[{1}] == {2} cause division by zero"),
    INDEX_LARGER_THAN_MAX("the index specified: {0} is larger than the current maximal index {1}"),
    INDEX_NOT_POSITIVE("index ({0}) is not positive"),
    INDEX_OUT_OF_RANGE("index {0} out of allowed range [{1}, {2}]"),
    INFINITE_ARRAY_ELEMENT("Array contains an infinite element, {0} at index {1}"),
    INFINITE_VALUE_CONVERSION("cannot convert infinite value"),
    INITIAL_CAPACITY_NOT_POSITIVE("initial capacity ({0}) is not positive"),
    INITIAL_COLUMN_AFTER_FINAL_COLUMN("initial column {0} after final column {1}"),
    INITIAL_ROW_AFTER_FINAL_ROW("initial row {0} after final row {1}"),
    INPUT_DATA_FROM_UNSUPPORTED_DATASOURCE("input data comes from unsupported datasource: {0}, supported sources: {1}, {2}"),
    INSTANCES_NOT_COMPARABLE_TO_EXISTING_VALUES("instance of class {0} not comparable to existing values"),
    INSUFFICIENT_DATA_FOR_T_STATISTIC("insufficient data for t statistic, needs at least 2, got {0}"),
    INSUFFICIENT_DIMENSION("insufficient dimension {0}, must be at least {1}"),
    INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE("sample contains {0} observed points, at least {1} are required"),
    INSUFFICIENT_ROWS_AND_COLUMNS("insufficient data: only {0} rows and {1} columns."),
    INTEGRATION_METHOD_NEEDS_AT_LEAST_ONE_PREVIOUS_POINT("{0} method needs at least one previous point"),
    INTERNAL_ERROR("internal error, please fill a bug report at {0}"),
    INVALID_BRACKETING_PARAMETERS("invalid bracketing parameters:  lower bound={0},  initial={1}, upper bound={2}"),
    INVALID_INTERVAL_INITIAL_VALUE_PARAMETERS("invalid interval, initial value parameters:  lower={0}, initial={1}, upper={2}"),
    INVALID_ITERATIONS_LIMITS("invalid iteration limits: min={0}, max={1}"),
    INVALID_MAX_ITERATIONS("bad value for maximum iterations number: {0}"),
    INVALID_REGRESSION_ARRAY("input data array length = {0} does not match the number of observations = {1} and the number of regressors = {2}"),
    INVALID_ROUNDING_METHOD("invalid rounding method {0}, valid methods: {1} ({2}), {3} ({4}), {5} ({6}), {7} ({8}), {9} ({10}), {11} ({12}), {13} ({14}), {15} ({16})"),
    ITERATOR_EXHAUSTED("iterator exhausted"),
    LCM_OVERFLOW_32_BITS("overflow: lcm({0}, {1}) is 2^31"),
    LCM_OVERFLOW_64_BITS("overflow: lcm({0}, {1}) is 2^63"),
    LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE("list of chromosomes bigger than maxPopulationSize"),
    LOESS_EXPECTS_AT_LEAST_ONE_POINT("Loess expects at least 1 point"),
    LOWER_BOUND_NOT_BELOW_UPPER_BOUND("lower bound ({0}) must be strictly less than upper bound ({1})"), /* keep */
    LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT("lower endpoint ({0}) must be less than or equal to upper endpoint ({1})"),
    MAP_MODIFIED_WHILE_ITERATING("map has been modified while iterating"),
    MAX_EVALUATIONS_EXCEEDED("maximal number of evaluations ({0}) exceeded"),
    MAX_ITERATIONS_EXCEEDED("maximal number of iterations ({0}) exceeded"),
    MINIMAL_STEPSIZE_REACHED_DURING_INTEGRATION("minimal step size ({0,number,0.00E00}) reached, integration needs {1,number,0.00E00}"),
    MISMATCHED_LOESS_ABSCISSA_ORDINATE_ARRAYS("Loess expects the abscissa and ordinate arrays to be of the same size, but got {0} abscissae and {1} ordinatae"),
    NAN_ELEMENT_AT_INDEX("element {0} is NaN"),
    NAN_VALUE_CONVERSION("cannot convert NaN value"),
    NEGATIVE_BRIGHTNESS_EXPONENT("brightness exponent should be positive or null, but got {0}"),
    NEGATIVE_COMPLEX_MODULE("negative complex module {0}"),
    NEGATIVE_ELEMENT_AT_2D_INDEX("element ({0}, {1}) is negative: {2}"),
    NEGATIVE_ELEMENT_AT_INDEX("element {0} is negative: {1}"),
    NEGATIVE_NUMBER_OF_SUCCESSES("number of successes must be non-negative ({0})"),
    NEGATIVE_NUMBER_OF_TRIALS("number of trials must be non-negative ({0})"),
    NEGATIVE_ROBUSTNESS_ITERATIONS("the number of robustness iterations must be non-negative, but got {0}"),
    START_POSITION("start position ({0})"), /* keep */
    NON_CONVERGENT_CONTINUED_FRACTION("Continued fraction convergents failed to converge for value {0}"),
    NON_POSITIVE_MICROSPHERE_ELEMENTS("number of microsphere elements must be positive, but got {0}"),
    NON_POSITIVE_POLYNOMIAL_DEGREE("polynomial degree must be positive: degree={0}"),
    NON_REAL_FINITE_ABSCISSA("all abscissae must be finite real numbers, but {0}-th is {1}"),
    NON_REAL_FINITE_ORDINATE("all ordinatae must be finite real numbers, but {0}-th is {1}"),
    NON_REAL_FINITE_WEIGHT("all weights must be finite real numbers, but {0}-th is {1}"),
    NON_SQUARE_MATRIX("a {0}x{1} matrix was provided instead of a square matrix"),
    NORMALIZE_INFINITE("Cannot normalize to an infinite value"),
    NORMALIZE_NAN("Cannot normalize to NaN"),
    NOT_ADDITION_COMPATIBLE_MATRICES("{0}x{1} and {2}x{3} matrices are not addition compatible"),
    NOT_DECREASING_NUMBER_OF_POINTS("points {0} and {1} are not decreasing ({2} < {3})"),
    NOT_DECREASING_SEQUENCE("points {3} and {2} are not decreasing ({1} < {0})"), /* keep */
    NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS("not enough data ({0} rows) for this many predictors ({1} predictors)"),
    NOT_ENOUGH_POINTS_IN_SPLINE_PARTITION("spline partition must have at least {0} points, got {1}"),
    NOT_INCREASING_NUMBER_OF_POINTS("points {0} and {1} are not increasing ({2} > {3})"),
    NOT_INCREASING_SEQUENCE("points {3} and {2} are not increasing ({1} > {0})"), /* keep */
    NOT_MULTIPLICATION_COMPATIBLE_MATRICES("{0}x{1} and {2}x{3} matrices are not multiplication compatible"),
    NOT_OVERRIDEN("method not overriden"),
    NOT_POSITIVE_ALPHA("alpha must be positive ({0})"),
    NOT_POSITIVE_BETA("beta must be positive ({0})"),
    NOT_POSITIVE_COLUMNDIMENSION("invalid column dimension: {0} (must be positive)"),
    NOT_POSITIVE_DEFINITE_MATRIX("not positive definite matrix"),
    NOT_POSITIVE_DEGREES_OF_FREEDOM("degrees of freedom must be positive ({0})"),
    NOT_POSITIVE_ELEMENT_AT_INDEX("element {0} is not positive: {1}"),
    NOT_POSITIVE_EXPONENT("invalid exponent {0} (must be positive)"),
    NOT_POSITIVE_LENGTH("length must be positive ({0})"),
    LENGTH("length ({0})"), /* keep */
    NOT_POSITIVE_MEAN("mean must be positive ({0})"),
    MEAN("mean ({0})"), /* keep */
    NOT_POSITIVE_NUMBER_OF_SAMPLES("number of sample is not positive: {0}"),
    NUMBER_OF_SAMPLES("number of samples ({0})"), /* keep */
    NOT_POSITIVE_PERMUTATION("permutation k ({0}) must be positive"),
    PERMUTATION_SIZE("permutation size ({0}"), /* keep */
    NOT_POSITIVE_POISSON_MEAN("the Poisson mean must be positive ({0})"),
    NOT_POSITIVE_POPULATION_SIZE("population size must be positive ({0})"),
    NOT_POSITIVE_ROW_DIMENSION("invalid row dimension: {0} (must be positive)"),
    NOT_POSITIVE_SAMPLE_SIZE("sample size must be positive ({0})"),
    NOT_POSITIVE_SCALE("scale must be positive ({0})"),
    NOT_POSITIVE_SHAPE("shape must be positive ({0})"),
    NOT_POSITIVE_STANDARD_DEVIATION("standard deviation must be positive ({0})"),
    STANDARD_DEVIATION("standard deviation ({0})"), /* keep */
    NOT_POSITIVE_UPPER_BOUND("upper bound must be positive ({0})"),
    NOT_POSITIVE_WINDOW_SIZE("window size must be positive ({0})"),
    NOT_POWER_OF_TWO("{0} is not a power of 2"),
    NOT_POWER_OF_TWO_CONSIDER_PADDING("{0} is not a power of 2, consider padding for fix"),
    NOT_POWER_OF_TWO_PLUS_ONE("{0} is not a power of 2 plus one"),
    NOT_STRICTLY_DECREASING_NUMBER_OF_POINTS("points {0} and {1} are not strictly decreasing ({2} <= {3})"),
    NOT_STRICTLY_DECREASING_SEQUENCE("points {3} and {2} are not strictly decreasing ({1} <= {0})"), /* keep */
    NOT_STRICTLY_INCREASING_KNOT_VALUES("knot values must be strictly increasing"),
    NOT_STRICTLY_INCREASING_NUMBER_OF_POINTS("points {0} and {1} are not strictly increasing ({2} >= {3})"),
    NOT_STRICTLY_INCREASING_SEQUENCE("points {3} and {2} are not strictly increasing ({1} >= {0})"), /* keep */
    NOT_SUBTRACTION_COMPATIBLE_MATRICES("{0}x{1} and {2}x{3} matrices are not subtraction compatible"),
    NOT_SYMMETRIC_MATRIX("not symmetric matrix"),
    NO_BIN_SELECTED("no bin selected"),
    NO_CONVERGENCE_WITH_ANY_START_POINT("none of the {0} start points lead to convergence"),
    NO_DATA("no data"), /* keep */
    NO_DEGREES_OF_FREEDOM("no degrees of freedom ({0} measurements, {1} parameters)"),
    NO_DENSITY_FOR_THIS_DISTRIBUTION("This distribution does not have a density function implemented"),
    NO_FEASIBLE_SOLUTION("no feasible solution"),
    NO_OPTIMUM_COMPUTED_YET("no optimum computed yet"),
    NO_RESULT_AVAILABLE("no result available"),
    NO_SUCH_MATRIX_ENTRY("no entry at indices ({0}, {1}) in a {2}x{3} matrix"),
    NULL_NOT_ALLOWED("null is not allowed"), /* keep */
    COVARIANCE_MATRIX("covariance matrix"), /* keep */
    DENOMINATOR("denominator"), /* keep */
    DENOMINATOR_FORMAT("denominator format"), /* keep */
    FRACTION("fraction"), /* keep */
    FUNCTION("function"), /* keep */
    IMAGINARY_FORMAT("imaginary format"), /* keep */
    INPUT_ARRAY("input array"), /* keep */
    NUMERATOR("numerator"), /* keep */
    NUMERATOR_FORMAT("numerator format"), /* keep */
    OBJECT_TRANSFORMATION("conversion exception in transformation"), /* keep */
    REAL_FORMAT("real format"), /* keep */
    WHOLE_FORMAT("whole format"), /* keep */
    NUMBER_TOO_LARGE("{0} is larger than the maximum ({1})"), /* keep */
    NUMBER_TOO_SMALL("{0} is smaller than the minimum ({1})"), /* keep */
    NUMBER_TOO_LARGE_BOUND_EXCLUDED("{0} is larger than, or equal to, the maximum ({1})"), /* keep */
    NUMBER_TOO_SMALL_BOUND_EXCLUDED("{0} is smaller than, or equal to, the minimum ({1})"), /* keep */
    NUMBER_OF_SUCCESS_LARGER_THAN_POPULATION_SIZE("number of successes ({0}) must be less than or equal to population size ({1})"),
    NUMERATOR_OVERFLOW_AFTER_MULTIPLY("overflow, numerator too large after multiply: {0}"),
    N_POINTS_GAUSS_LEGENDRE_INTEGRATOR_NOT_SUPPORTED("{0} points Legendre-Gauss integrator not supported, number of points must be in the {1}-{2} range"),
    OBSERVED_COUNTS_ALL_ZERO("observed counts are all 0 in observed array {0}"),
    OBSERVED_COUNTS_BOTTH_ZERO_FOR_ENTRY("observed counts are both zero for entry {0}"),
    OUT_OF_BOUNDS_QUANTILE_VALUE("out of bounds quantile value: {0}, must be in (0, 100]"),
    OUT_OF_BOUND_SIGNIFICANCE_LEVEL("out of bounds significance level {0}, must be between {1} and {2}"),
    OUT_OF_ORDER_ABSCISSA_ARRAY("the abscissae array must be sorted in a strictly increasing order, but the {0}-th element is {1} whereas {2}-th is {3}"),
    OUT_OF_RANGE_ROOT_OF_UNITY_INDEX("out of range root of unity index {0} (must be in [{1};{2}])"),
    OUT_OF_RANGE_SIMPLE("{0} out of [{1}, {2}] range"), /* keep */
    OVERFLOW_IN_FRACTION("overflow in fraction {0}/{1}, cannot negate"),
    OVERFLOW_IN_ADDITION("overflow in addition: {0} + {1}"),
    OVERFLOW_IN_SUBTRACTION("overflow in subtraction: {0} - {1}"),
    PERCENTILE_IMPLEMENTATION_CANNOT_ACCESS_METHOD("cannot access {0} method in percentile implementation {1}"),
    PERCENTILE_IMPLEMENTATION_UNSUPPORTED_METHOD("percentile implementation {0} does not support {1}"),
    PERMUTATION_EXCEEDS_N("permutation size ({0}) exceeds permuation domain ({1})"), /* keep */
    POLYNOMIAL_INTERPOLANTS_MISMATCH_SEGMENTS("number of polynomial interpolants must match the number of segments ({0} != {1} - 1)"),
    POPULATION_LIMIT_NOT_POSITIVE("population limit has to be positive"),
    POSITION_SIZE_MISMATCH_INPUT_ARRAY("position {0} and size {1} don't fit to the size of the input array {2}"),
    POWER_NEGATIVE_PARAMETERS("cannot raise an integral value to a negative power ({0}^{1})"),
    PROPAGATION_DIRECTION_MISMATCH("propagation direction mismatch"),
    RANDOMKEY_MUTATION_WRONG_CLASS("RandomKeyMutation works only with RandomKeys, not {0}"),
    ROOTS_OF_UNITY_NOT_COMPUTED_YET("roots of unity have not been computed yet"),
    ROTATION_MATRIX_DIMENSIONS("a {0}x{1} matrix cannot be a rotation matrix"),
    ROW_INDEX_OUT_OF_RANGE("row index {0} out of allowed range [{1}, {2}]"),
    SAME_SIGN_AT_ENDPOINTS("function values at endpoints do not have different signs, endpoints: [{0}, {1}], values: [{2}, {3}]"),
    SAMPLE_SIZE_EXCEEDS_COLLECTION_SIZE("sample size ({0}) exceeds collection size ({1})"), /* keep */
    SAMPLE_SIZE_LARGER_THAN_POPULATION_SIZE("sample size ({0}) must be less than or equal to population size ({1})"),
    SIMPLEX_NEED_ONE_POINT("simplex must contain at least one point"),
    SIMPLE_MESSAGE("{0}"),
    SINGULAR_MATRIX("matrix is singular"),
    SUBARRAY_ENDS_AFTER_ARRAY_END("subarray ends after array end"),
    TOO_LARGE_CUTOFF_SINGULAR_VALUE("cutoff singular value is {0}, should be at most {1}"),
    TOO_MANY_ELEMENTS_TO_DISCARD_FROM_ARRAY("cannot discard {0} elements from a {1} elements array"),
    TOO_SMALL_BANDWIDTH("the bandwidth must be large enough to accomodate at least 2 points. There are {0}  data points, and bandwidth must be at least {1}  but it is only {2}"),
    TOO_SMALL_COST_RELATIVE_TOLERANCE("cost relative tolerance is too small ({0}), no further reduction in the sum of squares is possible"),
    TOO_SMALL_INTEGRATION_INTERVAL("too small integration interval: length = {0}"),
    TOO_SMALL_ORTHOGONALITY_TOLERANCE("orthogonality tolerance is too small ({0}), solution is orthogonal to the jacobian"),
    TOO_SMALL_PARAMETERS_RELATIVE_TOLERANCE("parameters relative tolerance is too small ({0}), no further improvement in the approximate solution is possible"),
    TWO_OR_MORE_CATEGORIES_REQUIRED("two or more categories required, got {0}"),
    TWO_OR_MORE_VALUES_IN_CATEGORY_REQUIRED("two or more values required in each category, one has {0}"),
    UNABLE_TO_BRACKET_OPTIMUM_IN_LINE_SEARCH("unable to bracket optimum in line search"),
    UNABLE_TO_COMPUTE_COVARIANCE_SINGULAR_PROBLEM("unable to compute covariances: singular problem"),
    UNABLE_TO_FIRST_GUESS_HARMONIC_COEFFICIENTS("unable to first guess the harmonic coefficients"),
    UNABLE_TO_ORTHOGONOLIZE_MATRIX("unable to orthogonalize matrix in {0} iterations"),
    UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN("unable to perform Q.R decomposition on the {0}x{1} jacobian matrix"),
    UNABLE_TO_SOLVE_SINGULAR_PROBLEM("unable to solve: singular problem"),
    UNBOUNDED_SOLUTION("unbounded solution"),
    UNKNOWN_MODE("unknown mode {0}, known modes: {1} ({2}), {3} ({4}), {5} ({6}), {7} ({8}), {9} ({10}) and {11} ({12})"),
    UNPARSEABLE_3D_VECTOR("unparseable 3D vector: \"{0}\""),
    UNPARSEABLE_COMPLEX_NUMBER("unparseable complex number: \"{0}\""),
    UNPARSEABLE_FRACTION_NUMBER("unparseable fraction number: \"{0}\""),
    UNPARSEABLE_REAL_VECTOR("unparseable real vector: \"{0}\""),
    UNSUPPORTED_EXPANSION_MODE("unsupported expansion mode {0}, supported modes are {1} ({2}) and {3} ({4})"),
    UNSUPPORTED_OPERATION("unsupported operation"), /* keep */
    USER_EXCEPTION("exception generated in user code"), /* keep */
    URL_CONTAINS_NO_DATA("URL {0} contains no data"),
    VALUES_ADDED_BEFORE_CONFIGURING_STATISTIC("{0} values have been added before statistic is configured"),
    VECTOR_LENGTH_MISMATCH("vector length mismatch: got {0} but expected {1}"),
    VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT("vector must have at least one element"),
    WEIGHT_AT_LEAST_ONE_NON_ZERO("weigth array must contain at least one non-zero value"),
    WRONG_BLOCK_LENGTH("wrong array shape (block length = {0}, expected {1})"),
    WRONG_NUMBER_OF_POINTS("{0} points are required, got only {1}"),
    NUMBER_OF_POINTS("number of points ({0})"), /* keep */
    ZERO_DENOMINATOR("denominator must be different from 0"),
    ZERO_DENOMINATOR_IN_FRACTION("zero denominator in fraction {0}/{1}"),
    ZERO_FRACTION_TO_DIVIDE_BY("the fraction to divide by must not be zero: {0}/{1}"),
    ZERO_NORM("zero norm"),
    ZERO_NORM_FOR_ROTATION_AXIS("zero norm for rotation axis"),
    ZERO_NORM_FOR_ROTATION_DEFINING_VECTOR("zero norm for rotation defining vector"),
    ZERO_NOT_ALLOWED("zero not allowed here");

    // CHECKSTYLE: resume JavadocVariable
    // CHECKSTYLE: resume MultipleVariableDeclarations


    /** Source English format. */
    private final String sourceFormat;

    /** Simple constructor.
     * @param sourceFormat source English format to use when no
     * localized version is available
     */
    private LocalizedFormats(final String sourceFormat) {
        this.sourceFormat = sourceFormat;
    }

    /** {@inheritDoc} */
    public String getSourceString() {
        return sourceFormat;
    }

    /** {@inheritDoc} */
    public String getLocalizedString(final Locale locale) {
        try {
            ResourceBundle bundle =
                    ResourceBundle.getBundle("META-INF/localization/LocalizedFormats", locale);
            if (bundle.getLocale().getLanguage().equals(locale.getLanguage())) {
                // the value of the resource is the translated format
                return bundle.getString(toString());
            }

        } catch (MissingResourceException mre) {
            // do nothing here
        }

        // either the locale is not supported or the resource is unknown
        // don't translate and fall back to using the source format
        return sourceFormat;

    }

}
