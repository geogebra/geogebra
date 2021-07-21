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
package org.apache.commons.math3.exception.util;

import java.util.Locale;

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
 */
public class LocalizedFormats implements Localizable {

    // CHECKSTYLE: stop MultipleVariableDeclarations
    // CHECKSTYLE: stop JavadocVariable
	public static final LocalizedFormats INSTANCE = new LocalizedFormats();
	public static final LocalizedFormats ARGUMENT_OUTSIDE_DOMAIN = INSTANCE;
	public static final LocalizedFormats ARRAY_SIZE_EXCEEDS_MAX_VARIABLES = INSTANCE;
	public static final LocalizedFormats ARRAY_SIZES_SHOULD_HAVE_DIFFERENCE_1 = INSTANCE;
	public static final LocalizedFormats ARRAY_SUMS_TO_ZERO = INSTANCE;
	public static final LocalizedFormats ASSYMETRIC_EIGEN_NOT_SUPPORTED = INSTANCE;
	public static final LocalizedFormats AT_LEAST_ONE_COLUMN = INSTANCE;
	public static final LocalizedFormats AT_LEAST_ONE_ROW = INSTANCE;
	public static final LocalizedFormats BANDWIDTH = INSTANCE;
	public static final LocalizedFormats BESSEL_FUNCTION_BAD_ARGUMENT = INSTANCE;
	public static final LocalizedFormats BESSEL_FUNCTION_FAILED_CONVERGENCE = INSTANCE;
	public static final LocalizedFormats BINOMIAL_INVALID_PARAMETERS_ORDER = INSTANCE;
	public static final LocalizedFormats BINOMIAL_NEGATIVE_PARAMETER = INSTANCE;
	public static final LocalizedFormats CANNOT_CLEAR_STATISTIC_CONSTRUCTED_FROM_EXTERNAL_MOMENTS = INSTANCE;
	public static final LocalizedFormats CANNOT_COMPUTE_0TH_ROOT_OF_UNITY = INSTANCE;
	public static final LocalizedFormats CANNOT_COMPUTE_BETA_DENSITY_AT_0_FOR_SOME_ALPHA = INSTANCE;
	public static final LocalizedFormats CANNOT_COMPUTE_BETA_DENSITY_AT_1_FOR_SOME_BETA = INSTANCE;
	public static final LocalizedFormats CANNOT_COMPUTE_NTH_ROOT_FOR_NEGATIVE_N = INSTANCE;
	public static final LocalizedFormats CANNOT_DISCARD_NEGATIVE_NUMBER_OF_ELEMENTS = INSTANCE;
	public static final LocalizedFormats CANNOT_FORMAT_INSTANCE_AS_3D_VECTOR = INSTANCE;
	public static final LocalizedFormats CANNOT_FORMAT_INSTANCE_AS_COMPLEX = INSTANCE;
	public static final LocalizedFormats CANNOT_FORMAT_INSTANCE_AS_REAL_VECTOR = INSTANCE;
	public static final LocalizedFormats CANNOT_FORMAT_OBJECT_TO_FRACTION = INSTANCE;
	public static final LocalizedFormats CANNOT_INCREMENT_STATISTIC_CONSTRUCTED_FROM_EXTERNAL_MOMENTS = INSTANCE;
	public static final LocalizedFormats CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR = INSTANCE;
	public static final LocalizedFormats CANNOT_RETRIEVE_AT_NEGATIVE_INDEX = INSTANCE;
	public static final LocalizedFormats CANNOT_SET_AT_NEGATIVE_INDEX = INSTANCE;
	public static final LocalizedFormats CANNOT_SUBSTITUTE_ELEMENT_FROM_EMPTY_ARRAY = INSTANCE;
	public static final LocalizedFormats CANNOT_TRANSFORM_TO_DOUBLE = INSTANCE;
	public static final LocalizedFormats CARDAN_ANGLES_SINGULARITY = INSTANCE;
	public static final LocalizedFormats CLASS_DOESNT_IMPLEMENT_COMPARABLE = INSTANCE;
	public static final LocalizedFormats CLOSE_VERTICES = INSTANCE;
	public static final LocalizedFormats CLOSEST_ORTHOGONAL_MATRIX_HAS_NEGATIVE_DETERMINANT = INSTANCE;
	public static final LocalizedFormats COLUMN_INDEX_OUT_OF_RANGE = INSTANCE;
	public static final LocalizedFormats COLUMN_INDEX = INSTANCE;
	public static final LocalizedFormats CONSTRAINT = INSTANCE;
	public static final LocalizedFormats CONTINUED_FRACTION_INFINITY_DIVERGENCE = INSTANCE;
	public static final LocalizedFormats CONTINUED_FRACTION_NAN_DIVERGENCE = INSTANCE;
	public static final LocalizedFormats CONTRACTION_CRITERIA_SMALLER_THAN_EXPANSION_FACTOR = INSTANCE;
	public static final LocalizedFormats CONTRACTION_CRITERIA_SMALLER_THAN_ONE = INSTANCE;
	public static final LocalizedFormats CONVERGENCE_FAILED = INSTANCE;
	public static final LocalizedFormats CROSSING_BOUNDARY_LOOPS = INSTANCE;
	public static final LocalizedFormats CROSSOVER_RATE = INSTANCE;
	public static final LocalizedFormats CUMULATIVE_PROBABILITY_RETURNED_NAN = INSTANCE;
	public static final LocalizedFormats DIFFERENT_ROWS_LENGTHS = INSTANCE;
	public static final LocalizedFormats DIFFERENT_ORIG_AND_PERMUTED_DATA = INSTANCE;
	public static final LocalizedFormats DIGEST_NOT_INITIALIZED = INSTANCE;
	public static final LocalizedFormats DIMENSIONS_MISMATCH_2x2 = INSTANCE;
	public static final LocalizedFormats DIMENSIONS_MISMATCH_SIMPLE = INSTANCE;
	public static final LocalizedFormats DIMENSIONS_MISMATCH = INSTANCE;
	public static final LocalizedFormats DISCRETE_CUMULATIVE_PROBABILITY_RETURNED_NAN = INSTANCE;
	public static final LocalizedFormats DISTRIBUTION_NOT_LOADED = INSTANCE;
	public static final LocalizedFormats DUPLICATED_ABSCISSA_DIVISION_BY_ZERO = INSTANCE;
	public static final LocalizedFormats EDGE_CONNECTED_TO_ONE_FACET = INSTANCE;
	public static final LocalizedFormats ELITISM_RATE = INSTANCE;
	public static final LocalizedFormats EMPTY_CLUSTER_IN_K_MEANS = INSTANCE;
	public static final LocalizedFormats EMPTY_INTERPOLATION_SAMPLE = INSTANCE;
	public static final LocalizedFormats EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY = INSTANCE;
	public static final LocalizedFormats EMPTY_SELECTED_COLUMN_INDEX_ARRAY = INSTANCE;
	public static final LocalizedFormats EMPTY_SELECTED_ROW_INDEX_ARRAY = INSTANCE;
	public static final LocalizedFormats EMPTY_STRING_FOR_IMAGINARY_CHARACTER = INSTANCE;
	public static final LocalizedFormats ENDPOINTS_NOT_AN_INTERVAL = INSTANCE;
	public static final LocalizedFormats EQUAL_VERTICES_IN_SIMPLEX = INSTANCE;
	public static final LocalizedFormats EULER_ANGLES_SINGULARITY = INSTANCE;
	public static final LocalizedFormats EVALUATION = INSTANCE;
	public static final LocalizedFormats EXPANSION_FACTOR_SMALLER_THAN_ONE = INSTANCE;
	public static final LocalizedFormats FACET_ORIENTATION_MISMATCH = INSTANCE;
	public static final LocalizedFormats FACTORIAL_NEGATIVE_PARAMETER = INSTANCE;
	public static final LocalizedFormats FAILED_BRACKETING = INSTANCE;
	public static final LocalizedFormats FAILED_FRACTION_CONVERSION = INSTANCE;
	public static final LocalizedFormats FIRST_COLUMNS_NOT_INITIALIZED_YET = INSTANCE;
	public static final LocalizedFormats FIRST_ELEMENT_NOT_ZERO = INSTANCE;
	public static final LocalizedFormats FIRST_ROWS_NOT_INITIALIZED_YET = INSTANCE;
	public static final LocalizedFormats FRACTION_CONVERSION_OVERFLOW = INSTANCE;
	public static final LocalizedFormats FUNCTION_NOT_DIFFERENTIABLE = INSTANCE;
	public static final LocalizedFormats FUNCTION_NOT_POLYNOMIAL = INSTANCE;
	public static final LocalizedFormats GCD_OVERFLOW_32_BITS = INSTANCE;
	public static final LocalizedFormats GCD_OVERFLOW_64_BITS = INSTANCE;
	public static final LocalizedFormats HOLE_BETWEEN_MODELS_TIME_RANGES = INSTANCE;
	public static final LocalizedFormats ILL_CONDITIONED_OPERATOR = INSTANCE;
	public static final LocalizedFormats INCONSISTENT_STATE_AT_2_PI_WRAPPING = INSTANCE;
	public static final LocalizedFormats INDEX_LARGER_THAN_MAX = INSTANCE;
	public static final LocalizedFormats INDEX_NOT_POSITIVE = INSTANCE;
	public static final LocalizedFormats INDEX_OUT_OF_RANGE = INSTANCE;
	public static final LocalizedFormats INDEX = INSTANCE;
	public static final LocalizedFormats NOT_FINITE_NUMBER = INSTANCE;
	public static final LocalizedFormats INFINITE_BOUND = INSTANCE;
	public static final LocalizedFormats ARRAY_ELEMENT = INSTANCE;
	public static final LocalizedFormats INFINITE_ARRAY_ELEMENT = INSTANCE;
	public static final LocalizedFormats INFINITE_VALUE_CONVERSION = INSTANCE;
	public static final LocalizedFormats INITIAL_CAPACITY_NOT_POSITIVE = INSTANCE;
	public static final LocalizedFormats INITIAL_COLUMN_AFTER_FINAL_COLUMN = INSTANCE;
	public static final LocalizedFormats INITIAL_ROW_AFTER_FINAL_ROW = INSTANCE;
	public static final LocalizedFormats INPUT_DATA_FROM_UNSUPPORTED_DATASOURCE = INSTANCE;
	public static final LocalizedFormats INSTANCES_NOT_COMPARABLE_TO_EXISTING_VALUES = INSTANCE;
	public static final LocalizedFormats INSUFFICIENT_DATA = INSTANCE;
	public static final LocalizedFormats INSUFFICIENT_DATA_FOR_T_STATISTIC = INSTANCE;
	public static final LocalizedFormats INSUFFICIENT_DIMENSION = INSTANCE;
	public static final LocalizedFormats DIMENSION = INSTANCE;
	public static final LocalizedFormats INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE = INSTANCE;
	public static final LocalizedFormats INSUFFICIENT_ROWS_AND_COLUMNS = INSTANCE;
	public static final LocalizedFormats INTEGRATION_METHOD_NEEDS_AT_LEAST_TWO_PREVIOUS_POINTS = INSTANCE;
	public static final LocalizedFormats INTERNAL_ERROR = INSTANCE;
	public static final LocalizedFormats INVALID_BINARY_DIGIT = INSTANCE;
	public static final LocalizedFormats INVALID_BINARY_CHROMOSOME = INSTANCE;
	public static final LocalizedFormats INVALID_BRACKETING_PARAMETERS = INSTANCE;
	public static final LocalizedFormats INVALID_FIXED_LENGTH_CHROMOSOME = INSTANCE;
	public static final LocalizedFormats INVALID_IMPLEMENTATION = INSTANCE;
	public static final LocalizedFormats INVALID_INTERVAL_INITIAL_VALUE_PARAMETERS = INSTANCE;
	public static final LocalizedFormats INVALID_ITERATIONS_LIMITS = INSTANCE;
	public static final LocalizedFormats INVALID_MAX_ITERATIONS = INSTANCE;
	public static final LocalizedFormats NOT_ENOUGH_DATA_REGRESSION = INSTANCE;
	public static final LocalizedFormats INVALID_REGRESSION_ARRAY = INSTANCE;
	public static final LocalizedFormats INVALID_REGRESSION_OBSERVATION = INSTANCE;
	public static final LocalizedFormats INVALID_ROUNDING_METHOD = INSTANCE;
	public static final LocalizedFormats ITERATOR_EXHAUSTED = INSTANCE;
	public static final LocalizedFormats ITERATIONS = INSTANCE;
	public static final LocalizedFormats LCM_OVERFLOW_32_BITS = INSTANCE;
	public static final LocalizedFormats LCM_OVERFLOW_64_BITS = INSTANCE;
	public static final LocalizedFormats LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE = INSTANCE;
	public static final LocalizedFormats LOESS_EXPECTS_AT_LEAST_ONE_POINT = INSTANCE;
	public static final LocalizedFormats LOWER_BOUND_NOT_BELOW_UPPER_BOUND = INSTANCE;
	public static final LocalizedFormats LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT = INSTANCE;
	public static final LocalizedFormats MAP_MODIFIED_WHILE_ITERATING = INSTANCE;
	public static final LocalizedFormats MULTISTEP_STARTER_STOPPED_EARLY = INSTANCE;
	public static final LocalizedFormats EVALUATIONS = INSTANCE;
	public static final LocalizedFormats MAX_COUNT_EXCEEDED = INSTANCE;
	public static final LocalizedFormats MAX_ITERATIONS_EXCEEDED = INSTANCE;
	public static final LocalizedFormats MINIMAL_STEPSIZE_REACHED_DURING_INTEGRATION = INSTANCE;
	public static final LocalizedFormats MISMATCHED_LOESS_ABSCISSA_ORDINATE_ARRAYS = INSTANCE;
	public static final LocalizedFormats MUTATION_RATE = INSTANCE;
	public static final LocalizedFormats NAN_ELEMENT_AT_INDEX = INSTANCE;
	public static final LocalizedFormats NAN_VALUE_CONVERSION = INSTANCE;
	public static final LocalizedFormats NEGATIVE_BRIGHTNESS_EXPONENT = INSTANCE;
	public static final LocalizedFormats NEGATIVE_COMPLEX_MODULE = INSTANCE;
	public static final LocalizedFormats NEGATIVE_ELEMENT_AT_2D_INDEX = INSTANCE;
	public static final LocalizedFormats NEGATIVE_ELEMENT_AT_INDEX = INSTANCE;
	public static final LocalizedFormats NEGATIVE_NUMBER_OF_SUCCESSES = INSTANCE;
	public static final LocalizedFormats NUMBER_OF_SUCCESSES = INSTANCE;
	public static final LocalizedFormats NEGATIVE_NUMBER_OF_TRIALS = INSTANCE;
	public static final LocalizedFormats NUMBER_OF_INTERPOLATION_POINTS = INSTANCE;
	public static final LocalizedFormats NUMBER_OF_TRIALS = INSTANCE;
	public static final LocalizedFormats NOT_CONVEX = INSTANCE;
	public static final LocalizedFormats NOT_CONVEX_HYPERPLANES = INSTANCE;
	public static final LocalizedFormats ROBUSTNESS_ITERATIONS = INSTANCE;
	public static final LocalizedFormats START_POSITION = INSTANCE;
	public static final LocalizedFormats NON_CONVERGENT_CONTINUED_FRACTION = INSTANCE;
	public static final LocalizedFormats NON_INVERTIBLE_TRANSFORM = INSTANCE;
	public static final LocalizedFormats NON_POSITIVE_MICROSPHERE_ELEMENTS = INSTANCE;
	public static final LocalizedFormats NON_POSITIVE_POLYNOMIAL_DEGREE = INSTANCE;
	public static final LocalizedFormats NON_REAL_FINITE_ABSCISSA = INSTANCE;
	public static final LocalizedFormats NON_REAL_FINITE_ORDINATE = INSTANCE;
	public static final LocalizedFormats NON_REAL_FINITE_WEIGHT = INSTANCE;
	public static final LocalizedFormats NON_SQUARE_MATRIX = INSTANCE;
	public static final LocalizedFormats NORM = INSTANCE;
	public static final LocalizedFormats NORMALIZE_INFINITE = INSTANCE;
	public static final LocalizedFormats NORMALIZE_NAN = INSTANCE;
	public static final LocalizedFormats NOT_ADDITION_COMPATIBLE_MATRICES = INSTANCE;
	public static final LocalizedFormats NOT_DECREASING_NUMBER_OF_POINTS = INSTANCE;
	public static final LocalizedFormats NOT_DECREASING_SEQUENCE = INSTANCE;
	public static final LocalizedFormats NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS = INSTANCE;
	public static final LocalizedFormats NOT_ENOUGH_POINTS_IN_SPLINE_PARTITION = INSTANCE;
	public static final LocalizedFormats NOT_INCREASING_NUMBER_OF_POINTS = INSTANCE;
	public static final LocalizedFormats NOT_INCREASING_SEQUENCE = INSTANCE;
	public static final LocalizedFormats NOT_MULTIPLICATION_COMPATIBLE_MATRICES = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_DEFINITE_MATRIX = INSTANCE;
	public static final LocalizedFormats NON_POSITIVE_DEFINITE_MATRIX = INSTANCE;
	public static final LocalizedFormats NON_POSITIVE_DEFINITE_OPERATOR = INSTANCE;
	public static final LocalizedFormats NON_SELF_ADJOINT_OPERATOR = INSTANCE;
	public static final LocalizedFormats NON_SQUARE_OPERATOR = INSTANCE;
	public static final LocalizedFormats DEGREES_OF_FREEDOM = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_DEGREES_OF_FREEDOM = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_ELEMENT_AT_INDEX = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_EXPONENT = INSTANCE;
	public static final LocalizedFormats NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE = INSTANCE;
	public static final LocalizedFormats BASE = INSTANCE;
	public static final LocalizedFormats EXPONENT = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_LENGTH = INSTANCE;
	public static final LocalizedFormats LENGTH = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_MEAN = INSTANCE;
	public static final LocalizedFormats MEAN = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_NUMBER_OF_SAMPLES = INSTANCE;
	public static final LocalizedFormats NUMBER_OF_SAMPLES = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_PERMUTATION = INSTANCE;
	public static final LocalizedFormats PERMUTATION_SIZE = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_POISSON_MEAN = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_POPULATION_SIZE = INSTANCE;
	public static final LocalizedFormats POPULATION_SIZE = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_ROW_DIMENSION = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_SAMPLE_SIZE = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_SCALE = INSTANCE;
	public static final LocalizedFormats SCALE = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_SHAPE = INSTANCE;
	public static final LocalizedFormats SHAPE = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_STANDARD_DEVIATION = INSTANCE;
	public static final LocalizedFormats STANDARD_DEVIATION = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_UPPER_BOUND = INSTANCE;
	public static final LocalizedFormats NOT_POSITIVE_WINDOW_SIZE = INSTANCE;
	public static final LocalizedFormats NOT_POWER_OF_TWO = INSTANCE;
	public static final LocalizedFormats NOT_POWER_OF_TWO_CONSIDER_PADDING = INSTANCE;
	public static final LocalizedFormats NOT_POWER_OF_TWO_PLUS_ONE = INSTANCE;
	public static final LocalizedFormats NOT_STRICTLY_DECREASING_NUMBER_OF_POINTS = INSTANCE;
	public static final LocalizedFormats NOT_STRICTLY_DECREASING_SEQUENCE = INSTANCE;
	public static final LocalizedFormats NOT_STRICTLY_INCREASING_KNOT_VALUES = INSTANCE;
	public static final LocalizedFormats NOT_STRICTLY_INCREASING_NUMBER_OF_POINTS = INSTANCE;
	public static final LocalizedFormats NOT_STRICTLY_INCREASING_SEQUENCE = INSTANCE;
	public static final LocalizedFormats NOT_SUBTRACTION_COMPATIBLE_MATRICES = INSTANCE;
	public static final LocalizedFormats NOT_SUPPORTED_IN_DIMENSION_N = INSTANCE;
	public static final LocalizedFormats NOT_SYMMETRIC_MATRIX = INSTANCE;
	public static final LocalizedFormats NON_SYMMETRIC_MATRIX = INSTANCE;
	public static final LocalizedFormats NO_BIN_SELECTED = INSTANCE;
	public static final LocalizedFormats NO_CONVERGENCE_WITH_ANY_START_POINT = INSTANCE;
	public static final LocalizedFormats NO_DATA = INSTANCE;
	public static final LocalizedFormats NO_DEGREES_OF_FREEDOM = INSTANCE;
	public static final LocalizedFormats NO_DENSITY_FOR_THIS_DISTRIBUTION = INSTANCE;
	public static final LocalizedFormats NO_FEASIBLE_SOLUTION = INSTANCE;
	public static final LocalizedFormats NO_OPTIMUM_COMPUTED_YET = INSTANCE;
	public static final LocalizedFormats NO_REGRESSORS = INSTANCE;
	public static final LocalizedFormats NO_RESULT_AVAILABLE = INSTANCE;
	public static final LocalizedFormats NO_SUCH_MATRIX_ENTRY = INSTANCE;
	public static final LocalizedFormats NAN_NOT_ALLOWED = INSTANCE;
	public static final LocalizedFormats NULL_NOT_ALLOWED = INSTANCE;
	public static final LocalizedFormats ARRAY_ZERO_LENGTH_OR_NULL_NOT_ALLOWED = INSTANCE;
	public static final LocalizedFormats COVARIANCE_MATRIX = INSTANCE;
	public static final LocalizedFormats DENOMINATOR = INSTANCE;
	public static final LocalizedFormats DENOMINATOR_FORMAT = INSTANCE;
	public static final LocalizedFormats FRACTION = INSTANCE;
	public static final LocalizedFormats FUNCTION = INSTANCE;
	public static final LocalizedFormats IMAGINARY_FORMAT = INSTANCE;
	public static final LocalizedFormats INPUT_ARRAY = INSTANCE;
	public static final LocalizedFormats NUMERATOR = INSTANCE;
	public static final LocalizedFormats NUMERATOR_FORMAT = INSTANCE;
	public static final LocalizedFormats OBJECT_TRANSFORMATION = INSTANCE;
	public static final LocalizedFormats REAL_FORMAT = INSTANCE;
	public static final LocalizedFormats WHOLE_FORMAT = INSTANCE;
	public static final LocalizedFormats NUMBER_TOO_LARGE = INSTANCE;
	public static final LocalizedFormats NUMBER_TOO_SMALL = INSTANCE;
	public static final LocalizedFormats NUMBER_TOO_LARGE_BOUND_EXCLUDED = INSTANCE;
	public static final LocalizedFormats NUMBER_TOO_SMALL_BOUND_EXCLUDED = INSTANCE;
	public static final LocalizedFormats NUMBER_OF_SUCCESS_LARGER_THAN_POPULATION_SIZE = INSTANCE;
	public static final LocalizedFormats NUMERATOR_OVERFLOW_AFTER_MULTIPLY = INSTANCE;
	public static final LocalizedFormats N_POINTS_GAUSS_LEGENDRE_INTEGRATOR_NOT_SUPPORTED = INSTANCE;
	public static final LocalizedFormats OBSERVED_COUNTS_ALL_ZERO = INSTANCE;
	public static final LocalizedFormats OBSERVED_COUNTS_BOTTH_ZERO_FOR_ENTRY = INSTANCE;
	public static final LocalizedFormats BOBYQA_BOUND_DIFFERENCE_CONDITION = INSTANCE;
	public static final LocalizedFormats OUT_OF_BOUNDS_QUANTILE_VALUE = INSTANCE;
	public static final LocalizedFormats OUT_OF_BOUNDS_CONFIDENCE_LEVEL = INSTANCE;
	public static final LocalizedFormats OUT_OF_BOUND_SIGNIFICANCE_LEVEL = INSTANCE;
	public static final LocalizedFormats SIGNIFICANCE_LEVEL = INSTANCE;
	public static final LocalizedFormats OUT_OF_ORDER_ABSCISSA_ARRAY = INSTANCE;
	public static final LocalizedFormats OUT_OF_PLANE = INSTANCE;
	public static final LocalizedFormats OUT_OF_RANGE_ROOT_OF_UNITY_INDEX = INSTANCE;
	public static final LocalizedFormats OUT_OF_RANGE = INSTANCE;
	public static final LocalizedFormats OUT_OF_RANGE_SIMPLE = INSTANCE;
	public static final LocalizedFormats OUT_OF_RANGE_LEFT = INSTANCE;
	public static final LocalizedFormats OUT_OF_RANGE_RIGHT = INSTANCE;
	public static final LocalizedFormats OUTLINE_BOUNDARY_LOOP_OPEN = INSTANCE;
	public static final LocalizedFormats OVERFLOW = INSTANCE;
	public static final LocalizedFormats OVERFLOW_IN_FRACTION = INSTANCE;
	public static final LocalizedFormats OVERFLOW_IN_ADDITION = INSTANCE;
	public static final LocalizedFormats OVERFLOW_IN_SUBTRACTION = INSTANCE;
	public static final LocalizedFormats OVERFLOW_IN_MULTIPLICATION = INSTANCE;
	public static final LocalizedFormats PERCENTILE_IMPLEMENTATION_CANNOT_ACCESS_METHOD = INSTANCE;
	public static final LocalizedFormats PERCENTILE_IMPLEMENTATION_UNSUPPORTED_METHOD = INSTANCE;
	public static final LocalizedFormats PERMUTATION_EXCEEDS_N = INSTANCE;
	public static final LocalizedFormats POLYNOMIAL = INSTANCE;
	public static final LocalizedFormats POLYNOMIAL_INTERPOLANTS_MISMATCH_SEGMENTS = INSTANCE;
	public static final LocalizedFormats POPULATION_LIMIT_NOT_POSITIVE = INSTANCE;
	public static final LocalizedFormats POWER_NEGATIVE_PARAMETERS = INSTANCE;
	public static final LocalizedFormats PROPAGATION_DIRECTION_MISMATCH = INSTANCE;
	public static final LocalizedFormats RANDOMKEY_MUTATION_WRONG_CLASS = INSTANCE;
	public static final LocalizedFormats ROOTS_OF_UNITY_NOT_COMPUTED_YET = INSTANCE;
	public static final LocalizedFormats ROTATION_MATRIX_DIMENSIONS = INSTANCE;
	public static final LocalizedFormats ROW_INDEX_OUT_OF_RANGE = INSTANCE;
	public static final LocalizedFormats ROW_INDEX = INSTANCE;
	public static final LocalizedFormats SAME_SIGN_AT_ENDPOINTS = INSTANCE;
	public static final LocalizedFormats SAMPLE_SIZE_EXCEEDS_COLLECTION_SIZE = INSTANCE;
	public static final LocalizedFormats SAMPLE_SIZE_LARGER_THAN_POPULATION_SIZE = INSTANCE;
	public static final LocalizedFormats SIMPLEX_NEED_ONE_POINT = INSTANCE;
	public static final LocalizedFormats SIMPLE_MESSAGE = INSTANCE;
	public static final LocalizedFormats SINGULAR_MATRIX = INSTANCE;
	public static final LocalizedFormats SINGULAR_OPERATOR = INSTANCE;
	public static final LocalizedFormats SUBARRAY_ENDS_AFTER_ARRAY_END = INSTANCE;
	public static final LocalizedFormats TOO_LARGE_CUTOFF_SINGULAR_VALUE = INSTANCE;
	public static final LocalizedFormats TOO_LARGE_TOURNAMENT_ARITY = INSTANCE;
	public static final LocalizedFormats TOO_MANY_ELEMENTS_TO_DISCARD_FROM_ARRAY = INSTANCE;
	public static final LocalizedFormats TOO_MANY_REGRESSORS = INSTANCE;
	public static final LocalizedFormats TOO_SMALL_COST_RELATIVE_TOLERANCE = INSTANCE;
	public static final LocalizedFormats TOO_SMALL_INTEGRATION_INTERVAL = INSTANCE;
	public static final LocalizedFormats TOO_SMALL_ORTHOGONALITY_TOLERANCE = INSTANCE;
	public static final LocalizedFormats TOO_SMALL_PARAMETERS_RELATIVE_TOLERANCE = INSTANCE;
	public static final LocalizedFormats TRUST_REGION_STEP_FAILED = INSTANCE;
	public static final LocalizedFormats TWO_OR_MORE_CATEGORIES_REQUIRED = INSTANCE;
	public static final LocalizedFormats TWO_OR_MORE_VALUES_IN_CATEGORY_REQUIRED = INSTANCE;
	public static final LocalizedFormats UNABLE_TO_BRACKET_OPTIMUM_IN_LINE_SEARCH = INSTANCE;
	public static final LocalizedFormats UNABLE_TO_COMPUTE_COVARIANCE_SINGULAR_PROBLEM = INSTANCE;
	public static final LocalizedFormats UNABLE_TO_FIRST_GUESS_HARMONIC_COEFFICIENTS = INSTANCE;
	public static final LocalizedFormats UNABLE_TO_ORTHOGONOLIZE_MATRIX = INSTANCE;
	public static final LocalizedFormats UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN = INSTANCE;
	public static final LocalizedFormats UNABLE_TO_SOLVE_SINGULAR_PROBLEM = INSTANCE;
	public static final LocalizedFormats UNBOUNDED_SOLUTION = INSTANCE;
	public static final LocalizedFormats UNKNOWN_MODE = INSTANCE;
	public static final LocalizedFormats UNKNOWN_PARAMETER = INSTANCE;
	public static final LocalizedFormats UNMATCHED_ODE_IN_EXPANDED_SET = INSTANCE;
	public static final LocalizedFormats CANNOT_PARSE_AS_TYPE = INSTANCE;
	public static final LocalizedFormats CANNOT_PARSE = INSTANCE;
	public static final LocalizedFormats UNPARSEABLE_3D_VECTOR = INSTANCE;
	public static final LocalizedFormats UNPARSEABLE_COMPLEX_NUMBER = INSTANCE;
	public static final LocalizedFormats UNPARSEABLE_REAL_VECTOR = INSTANCE;
	public static final LocalizedFormats UNSUPPORTED_EXPANSION_MODE = INSTANCE;
	public static final LocalizedFormats UNSUPPORTED_OPERATION = INSTANCE;
	public static final LocalizedFormats ARITHMETIC_EXCEPTION = INSTANCE;
	public static final LocalizedFormats ILLEGAL_STATE = INSTANCE;
	public static final LocalizedFormats USER_EXCEPTION = INSTANCE;
	public static final LocalizedFormats URL_CONTAINS_NO_DATA = INSTANCE;
	public static final LocalizedFormats VALUES_ADDED_BEFORE_CONFIGURING_STATISTIC = INSTANCE;
	public static final LocalizedFormats VECTOR_LENGTH_MISMATCH = INSTANCE;
	public static final LocalizedFormats VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT = INSTANCE;
	public static final LocalizedFormats WEIGHT_AT_LEAST_ONE_NON_ZERO = INSTANCE;
	public static final LocalizedFormats WRONG_BLOCK_LENGTH = INSTANCE;
	public static final LocalizedFormats WRONG_NUMBER_OF_POINTS = INSTANCE;
	public static final LocalizedFormats NUMBER_OF_POINTS = INSTANCE;
	public static final LocalizedFormats ZERO_DENOMINATOR = INSTANCE;
	public static final LocalizedFormats ZERO_DENOMINATOR_IN_FRACTION = INSTANCE;
	public static final LocalizedFormats ZERO_FRACTION_TO_DIVIDE_BY = INSTANCE;
	public static final LocalizedFormats ZERO_NORM = INSTANCE;
	public static final LocalizedFormats ZERO_NORM_FOR_ROTATION_AXIS = INSTANCE;
	public static final LocalizedFormats ZERO_NORM_FOR_ROTATION_DEFINING_VECTOR = INSTANCE;
	public static final LocalizedFormats ZERO_NOT_ALLOWED = INSTANCE;
	// "zero not allowed here");

	// CHECKSTYLE: resume JavadocVariable
	// CHECKSTYLE: resume MultipleVariableDeclarations

    /** Source English format. */
	// private final String sourceFormat;

    /** Simple constructor.
     * @param sourceFormat source English format to use when no
     * localized version is available
     */
	LocalizedFormats(/* final String sourceFormat */) {
		// this.sourceFormat = sourceFormat;
    }

	/** {@inheritDoc} */
    public String getSourceString() {
		return "ApacheMathError";// sourceFormat;
    }

    /** {@inheritDoc} */
    public String getLocalizedString(final Locale locale) {
		// try {
		// final String path =
		// LocalizedFormats.class.getName().replaceAll,//"\\.", "/");
		// ResourceBundle bundle =
		// ResourceBundle.getBundle("assets/" + path, locale);
		// if (bundle.getLocale().getLanguage().equals(locale.getLanguage())) {
		// // the value of the resource is the translated format
		// return bundle.getString(toString());
		// }
		//
		// } catch (MissingResourceException mre) { // NOPMD
		// // do nothing here
		// }

        // either the locale is not supported or the resource is unknown
        // don't translate and fall back to using the source format
		return getSourceString();

    }

	// public static void main(String[] args) {
	// Object[] values = LocalizedFormats.values();
	// for (int i = 0; i < values.length; i++) {
	// System.out.println(
	// "public static final LocalizedFormats " + values[i]
	// + "= INSTANCE;");
	// }
	// }

}
