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

package org.apache.commons.math;

import java.util.ListResourceBundle;

/**
 * French localization message resources for the commons-math library.
 * @version $Revision: 927009 $ $Date: 2010-03-24 07:14:07 -0400 (Wed, 24 Mar 2010) $
 * @since 1.2
 */
public class MessagesResources_fr
  extends ListResourceBundle {

  /** Non-translated/translated messages arrays. */
  private static final Object[][] CONTENTS = {

    // org.apache.commons.math.util.MathUtils
    { "must have n >= k for binomial coefficient (n,k), got n = {0}, k = {1}",
      "n doit \u00eatre sup\u00e9rieur ou \u00e9gal \u00e0 k " +
      "pour le coefficient du bin\u00f4me (n,k), or n = {0}, k = {1}" },
    { "must have n >= 0 for binomial coefficient (n,k), got n = {0}",
      "n doit \u00eatre positif pour le coefficient du bin\u00f4me (n,k), or n = {0}" },
    { "must have n >= 0 for n!, got n = {0}",
      "n doit \u00eatre positif pour le calcul de n!, or n = {0}" },
    { "overflow: gcd({0}, {1}) is 2^31",
      "d\u00e9passement de capacit\u00e9 : le PGCD de {0} et {1} vaut 2^31" },
    { "overflow: gcd({0}, {1}) is 2^63",
      "d\u00e9passement de capacit\u00e9 : le PGCD de {0} et {1} vaut 2^63" },
    { "overflow: lcm({0}, {1}) is 2^31",
      "d\u00e9passement de capacit\u00e9 : le MCM de {0} et {1} vaut 2^31" },
    { "overflow: lcm({0}, {1}) is 2^63",
      "d\u00e9passement de capacit\u00e9 : le MCM de {0} et {1} vaut 2^63" },
    { "cannot raise an integral value to a negative power ({0}^{1})",
      "impossible d''\u00e9lever une valeur enti\u00e8re " +
      "\u00e0 une puissance n\u00e9gative ({0}^{1})" },
    { "invalid rounding method {0}, valid methods: {1} ({2}), {3} ({4}), {5} ({6}), {7} ({8}), {9} ({10}), {11} ({12}), {13} ({14}), {15} ({16})",
      "m\u00e9thode d''arondi {0} invalide, m\u00e9thodes valides : {1} ({2}), {3} ({4}), {5} ({6}), {7} ({8}), {9} ({10}), {11} ({12}), {13} ({14}), {15} ({16})" },
    { "Cannot normalize to an infinite value",
      "impossible de normaliser vers une valeur infinie" },
    { "Cannot normalize to NaN",
      "impossible de normaliser vers NaN" },
    { "Array contains an infinite element, {0} at index {1}",
      "le tableau contient l''\u00e9l\u00e9ment infini {0} \u00e0 l''index {1}" },

    // org.apache.commons.math.FunctionEvaluationException
    { "evaluation failed for argument = {0}",
      "erreur d''\u00e9valuation pour l''argument {0}" },

    // org.apache.commons.math.DuplicateSampleAbscissaException
    { "Abscissa {0} is duplicated at both indices {1} and {2}",
      "Abscisse {0} dupliqu\u00e9e aux indices {1} et {2}" },

    // org.apache.commons.math.ConvergenceException
    { "Convergence failed",
      "\u00c9chec de convergence" },

    // org.apache.commons.math.ArgumentOutsideDomainException
    { "Argument {0} outside domain [{1} ; {2}]",
      "Argument {0} hors du domaine [{1} ; {2}]" },

    // org.apache.commons.math.MaxIterationsExceededException
    { "Maximal number of iterations ({0}) exceeded",
      "Nombre maximal d''it\u00e9rations ({0}) d\u00e9pass\u00e9" },

    // org.apache.commons.math.MaxEvaluationsExceededException
    { "Maximal number of evaluations ({0}) exceeded",
      "Nombre maximal d''\u00e9valuations ({0}) d\u00e9pass\u00e9" },

    // org.apache.commons.math.analysis.interpolation.SplineInterpolator
    // org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm
    // org.apache.commons.math.DimensionMismatchException
    // org.apache.commons.math.optimization.LeastSquaresConverter
    // org.apache.commons.math.optimization.direct.DirectSearchOptimizer
    // org.apache.commons.math.optimization.general.AbstractLeastSquaresOptimizer
    // org.apache.commons.math.ode.ContinuousOutputModel
    // org.apache.commons.math.random.UncorrelatedRandomVectorGenerator
    // org.apache.commons.math.stat.regression.AbstractMultipleLinearRegression
    // org.apache.commons.math.stat.inference.ChiSquareTestImpl
    // org.apache.commons.math.analysis.interpolation.MicrosphereInterpolatingFunction
    { "dimension mismatch {0} != {1}",
      "dimensions incompatibles {0} != {1}" },

    // org.apache.commons.math.analysis.interpolation.MicrosphereInterpolatingFunction
    { "no data",
      "aucune donn\u00e9e" },

    // org.apache.commons.math.analysis.interpolation.MicrosphereInterpolator
    { "brightness exponent should be positive or null, but got {0}",
      "l''exposant de brillance devrait \u00eatre positif ou null, or e = {0}" },
    { "number of microsphere elements must be positive, but got {0}",
      "le nombre d''\u00e9l\u00e9ments de la microsph\u00e8re devrait \u00eatre positif, or n = {0}" },

   // org.apache.commons.math.linear.decomposition.NotPositiveDefiniteMatrixException
    { "not positive definite matrix",
      "matrice non d\u00e9finie positive" },

    // org.apache.commons.math.linear.decomposition.NotSymmetricMatrixException
    { "not symmetric matrix",
      "matrice non symm\u00e9trique" },

    // org.apache.commons.math.fraction.FractionConversionException
    { "Unable to convert {0} to fraction after {1} iterations",
      "Impossible de convertir {0} en fraction apr\u00e8s {1} it\u00e9rations" },
    { "Overflow trying to convert {0} to fraction ({1}/{2})",
      "D\u00e9passement de capacit\u00e9 lors de la conversion de {0} en fraction ({1}/{2})" },

    // org.apache.commons.math.fraction.BigFraction
    { "numerator is null",
      "le num\u00e9rateur est null" },
    { "denimonator is null",
      "le d\u00e9nominateur est null" },
    { "denominator must be different from 0",
      "le d\u00e9nominateur doit \u00eatre diff\u00e9rent de 0" },
    { "cannot convert NaN value",
      "les valeurs NaN ne peuvent \u00eatre converties" },
    { "cannot convert infinite value",
      "les valeurs infinies ne peuvent \u00eatre converties" },

    // org.apache.commons.math.fraction.AbstractFormat
    { "denominator format can not be null",
      "le format du d\u00e9nominateur ne doit pas \u00eatre nul" },
    { "numerator format can not be null",
      "le format du num\u00e9rateur ne doit pas \u00eatre nul" },

    // org.apache.commons.math.fraction.FractionFormat
    { "cannot convert given object to a fraction number: {0}",
      "impossible de convertir l''objet sous forme d''un nombre rationnel : {0}" },

    // org.apache.commons.math.fraction.FractionFormat
    // org.apache.commons.math.fraction.BigFractionFormat
    { "unparseable fraction number: \"{0}\"",
      "\u00e9chec d''analyse du nombre rationnel \"{0}\"" },
    { "cannot format given object as a fraction number",
      "impossible de formater l''objet sous forme d''un nombre rationnel" },

    // org.apache.commons.math.fraction.ProperFractionFormat
    // org.apache.commons.math.fraction.ProperBigFractionFormat
    { "whole format can not be null",
      "le format complet ne doit pas \u00eatre nul" },

    // org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils
    { "function is null",
      "la fonction est nulle" },
    { "bad value for maximum iterations number: {0}",
      "valeur invalide pour le nombre maximal d''it\u00e9rations : {0}" },
    { "invalid bracketing parameters:  lower bound={0},  initial={1}, upper bound={2}",
      "param\u00e8tres d''encadrement invalides : borne inf\u00e9rieure = {0}, valeur initiale = {1}, borne sup\u00e9rieure = {2}" },
    { "number of iterations={0}, maximum iterations={1}, initial={2}, lower bound={3}, upper bound={4}," +
        " final a value={5}, final b value={6}, f(a)={7}, f(b)={8}",
      "nombre d''it\u00e9rations = {0}, it\u00e9rations maximum = {1}, valeur initiale = {2}," +
        " borne inf\u00e9rieure = {3}, borne sup\u00e9rieure = {4}," +
        " valeur a finale = {5}, valeur b finale = {6}, f(a) = {7}, f(b) = {8}" },

    // org.apache.commons.math.analysis.solvers.LaguerreSolver
    { "polynomial degree must be positive: degree={0}",
      "le polyn\u00f4me doit \u00eatre de degr\u00e9 positif : degr\u00e9 = {0}" },

    // org.apache.commons.math.analysis.solvers.SecantSolver
    { "function values at endpoints do not have different signs, endpoints: [{0}, {1}], values: [{2}, {3}]",
      "les valeurs de la fonctions aux bornes sont de m\u00eame signe, bornes : [{0}, {1}], valeurs : [{2}, {3}]" },

    // org.apache.commons.math.analysis.interpolation.SplineInterpolator
    // org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm
    { "{0} points are required, got only {1}",
      "{0} sont n\u00e9cessaires, seuls {1} ont \u00e9t\u00e9 fournis" },

    // org.apache.commons.math.analysis.interpolation.SplineInterpolator
    { "points {0} and {1} are not strictly increasing ({2} >= {3})",
      "les points {0} et {1} ne sont pas strictement croissants ({2} >= {3})" },
    { "points {0} and {1} are not strictly decreasing ({2} <= {3})",
      "les points {0} et {1} ne sont pas strictement d\u00e9croissants ({2} <= {3})" },
    { "points {0} and {1} are not increasing ({2} > {3})",
      "les points {0} et {1} ne sont pas croissants ({2} > {3})" },
    { "points {0} and {1} are not decreasing ({2} < {3})",
      "les points {0} et {1} ne sont pas d\u00e9croissants ({2} < {3})" },

    // org.apache.commons.math.analysis.interpolation.LoessInterpolator
    { "bandwidth must be in the interval [0,1], but got {0}",
      "la largeur de bande doit \u00eatre dans l''intervalle [0, 1], alors qu'elle vaut {0}" },
    { "the number of robustness iterations must be non-negative, but got {0}",
      "le nombre d''it\u00e9rations robuste ne peut \u00eatre n\u00e9gatif, alors qu''il est de {0}" },
    { "Loess expects the abscissa and ordinate arrays to be of the same size, " +
      "but got {0} abscissae and {1} ordinatae",
      "la r\u00e9gression Loess n\u00e9cessite autant d''abscisses que d''ordonn\u00e9es, " +
      "mais {0} abscisses et {1} ordonn\u00e9es ont \u00e9t\u00e9 fournies" },
    { "Loess expects at least 1 point",
      "la r\u00e9gression Loess n\u00e9cessite au moins un point" },
    { "the bandwidth must be large enough to accomodate at least 2 points. There are {0} " +
      " data points, and bandwidth must be at least {1}  but it is only {2}",
      "la largeur de bande doit \u00eatre assez grande pour supporter au moins 2 points. Il y a {0}" +
      "donn\u00e9es et la largeur de bande doit \u00eatre au moins de {1}, or elle est seulement de {2}" },
    { "all abscissae must be finite real numbers, but {0}-th is {1}",
      "toutes les abscisses doivent \u00eatre des nombres r\u00e9els finis, mais l''abscisse {0} vaut {1}" },
    { "all ordinatae must be finite real numbers, but {0}-th is {1}",
      "toutes les ordonn\u00e9es doivent \u00eatre des nombres r\u00e9els finis, mais l''ordonn\u00e9e {0} vaut {1}" },
    { "all weights must be finite real numbers, but {0}-th is {1}",
      "tous les poids doivent \u00eatre des nombres r\u00e9els finis, mais le poids {0} vaut {1}" },
    { "the abscissae array must be sorted in a strictly increasing order, " +
      "but the {0}-th element is {1} whereas {2}-th is {3}",
      "les abscisses doivent \u00eatre en ordre strictement croissant, " +
      "mais l''\u00e9l\u00e9ment {0} vaut {1} alors que l''\u00e9l\u00e9ment {2} vaut {3}" },

    // org.apache.commons.math.util.ContinuedFraction
    { "Continued fraction convergents diverged to +/- infinity for value {0}",
      "Divergence de fraction continue \u00e0 l''infini pour la valeur {0}" },
    { "Continued fraction convergents failed to converge for value {0}",
      "\u00c9chec de convergence de fraction continue pour la valeur {0}" },
    { "Continued fraction diverged to NaN for value {0}",
      "Divergence de fraction continue \u00e0 NaN pour la valeur {0}"},

    // org.apache.commons.math.util.DefaultTransformer
    { "Conversion Exception in Transformation, Object is null",
      "Exception de conversion dans une transformation, l''objet est nul" },
    { "Conversion Exception in Transformation: {0}",
      "Exception de conversion dans une transformation : {0}" },

    // org.apache.commons.math.optimization.MultiStartOptimizer
    { "no optimum computed yet",
      "aucun optimum n''a encore \u00e9t\u00e9 calcul\u00e9" },

    // org.apache.commons.math.optimization.direct.DirectSearchOptimizer
    { "simplex must contain at least one point",
      "le simplex doit contenir au moins un point" },
    { "equal vertices {0} and {1} in simplex configuration",
      "sommets {0} et {1} \u00e9gaux dans la configuration du simplex" },

    // org.apache.commons.math.estimation.AbstractEstimation
    { "maximal number of evaluations exceeded ({0})",
      "nombre maximal d''\u00e9valuations d\u00e9pass\u00e9 ({0})" },

    // org.apache.commons.math.optimization.general.AbstractLeastSquaresOptimizer
    { "unable to compute covariances: singular problem",
      "impossible de calculer les covariances : probl\u00e8me singulier"},
    { "no degrees of freedom ({0} measurements, {1} parameters)",
      "aucun degr\u00e9 de libert\u00e9 ({0} mesures, {1} param\u00e8tres)" },

    // org.apache.commons.math.optimization.general.GaussNewtonOptimizer
    { "unable to solve: singular problem",
      "r\u00e9solution impossible : probl\u00e8me singulier" },

    // org.apache.commons.math.optimization.general.LevenbergMarquardtEstimator
    { "cost relative tolerance is too small ({0}), no further reduction in the sum of squares is possible",
      "trop petite tol\u00e9rance relative sur le co\u00fbt ({0}), aucune r\u00e9duction de la somme des carr\u00e9s n''est possible" },
    { "parameters relative tolerance is too small ({0}), no further improvement in the approximate solution is possible",
      "trop petite tol\u00e9rance relative sur les param\u00e8tres ({0}), aucune am\u00e9lioration de la solution approximative n''est possible" },
    { "orthogonality tolerance is too small ({0}), solution is orthogonal to the jacobian",
      "trop petite tol\u00e9rance sur l''orthogonalit\u00e9 ({0}), la solution est orthogonale \u00e0 la jacobienne" },
    { "unable to perform Q.R decomposition on the {0}x{1} jacobian matrix",
      "impossible de calculer la factorisation Q.R de la matrice jacobienne {0}x{1}" },

    // org.apache.commons.math.optimization.general.NonLinearConjugateGradientOptimizer
    { "unable to bracket optimum in line search",
      "impossible d''encadrer l''optimum lors de la recherche lin\u00e9aire" },

    // org.apache.commons.math.optimization.fitting.HarmonicCoefficientsGuesser
    { "unable to first guess the harmonic coefficients",
      "impossible de faire une premi\u00e8re estimation des coefficients harmoniques" },

    // org.apache.commons.math.optimization.fitting.HarmonicCoefficientsGuesser
    { "sample contains {0} observed points, at least {1} are required",
      "l''\u00e9chantillon ne contient que {0} points alors qu''au moins {1} sont n\u00e9cessaires" },

    // org.apache.commons.math.optimization.linear.NoFeasibleSolutionException
    { "no feasible solution",
      "aucune solution r\u00e9alisable" },

    // org.apache.commons.math.optimization.linear.UnboundedSolutionException
    { "unbounded solution",
      "solution non born\u00e9e" },

    // org.apache.commons.math.geometry.CardanEulerSingularityException
    { "Cardan angles singularity",
      "singularit\u00e9 d''angles de Cardan" },
    { "Euler angles singularity",
      "singularit\u00e9 d''angles d''Euler" },

    // org.apache.commons.math.geometry.Rotation
    { "a {0}x{1} matrix cannot be a rotation matrix",
      "une matrice {0}x{1} ne peut pas \u00eatre une matrice de rotation" },
    { "the closest orthogonal matrix has a negative determinant {0}",
      "la matrice orthogonale la plus proche a un d\u00e9terminant n\u00e9gatif {0}" },
    { "unable to orthogonalize matrix in {0} iterations",
      "impossible de rendre la matrice orthogonale en {0} it\u00e9rations" },

    // org.apache.commons.math.ode.nonstiff.AdaptiveStepsizeIntegrator
    { "minimal step size ({0,number,0.00E00}) reached, integration needs {1,number,0.00E00}",
      "pas minimal ({0,number,0.00E00}) atteint, l''int\u00e9gration n\u00e9cessite {1,number,0.00E00}" },
    { "dimensions mismatch: state vector has dimension {0}, absolute tolerance vector has dimension {1}",
      "incompatibilit\u00e9 de dimensions entre le vecteur d''\u00e9tat ({0}), et le vecteur de tol\u00e9rance absolue ({1})" },
    { "dimensions mismatch: state vector has dimension {0}, relative tolerance vector has dimension {1}",
      "incompatibilit\u00e9 de dimensions entre le vecteur d''\u00e9tat ({0}), et le vecteur de tol\u00e9rance relative ({1})" },

    // org.apache.commons.math.ode.nonstiff.AdaptiveStepsizeIntegrator,
    // org.apache.commons.math.ode.nonstiff.RungeKuttaIntegrator
    { "dimensions mismatch: ODE problem has dimension {0}, initial state vector has dimension {1}",
      "incompatibilit\u00e9 de dimensions entre le probl\u00e8me ODE ({0}), et le vecteur d''\u00e9tat initial ({1})" },
    { "dimensions mismatch: ODE problem has dimension {0}, final state vector has dimension {1}",
      "incompatibilit\u00e9 de dimensions entre le probl\u00e8me ODE ({0}), et le vecteur d''\u00e9tat final ({1})" },
    { "too small integration interval: length = {0}",
      "intervalle d''int\u00e9gration trop petit : {0}" },

    // org.apache.commons.math.ode.MultistepIntegrator
    { "{0} method needs at least one previous point",
      "la m\u00e9thode {0} n\u00e9cessite au moins un point pr\u00e9c\u00e9dent" },

    // org.apache.commons.math.ode.ContinuousOutputModel
    // org.apache.commons.math.optimization.direct.DirectSearchOptimizer
    { "unexpected exception caught",
      "exception inattendue lev\u00e9e" },
    { "propagation direction mismatch",
      "directions de propagation incoh\u00e9rentes" },
    { "{0} wide hole between models time ranges",
      "trou de longueur {0} entre les domaines temporels des mod\u00e8les" },

    // org.apache.commons.math.optimization.direct.DirectSearchOptimizer
    { "none of the {0} start points lead to convergence",
      "aucun des {0} points de d\u00e9part n''aboutit \u00e0 une convergence"  },

    // org.apache.commons.math.random.ValueServer
    { "unknown mode {0}, known modes: {1} ({2}), {3} ({4}), {5} ({6}), {7} ({8}), {9} ({10}) and {11} ({12})",
      "mode {0} inconnu, modes connus : {1} ({2}), {3} ({4}), {5} ({6}), {7} ({8}), {9} ({10}) et {11} ({12})" },
    { "digest not initialized",
      "mod\u00e8le empirique non initialis\u00e9" },

    // org.apache.commons.math.random.EmpiricalDistributionImpl
    { "distribution not loaded",
      "aucune distribution n''a \u00e9t\u00e9 charg\u00e9e" },
    { "no bin selected",
      "aucun compartiment s\u00e9lectionn\u00e9" },
    { "input data comes from unsupported datasource: {0}, supported sources: {1}, {2}",
      "les donn\u00e9es d''entr\u00e9e proviennent " +
      "d''une source non support\u00e9e : {0}, sources support\u00e9es : {1}, {2}" },

    // org.apache.commons.math.random.EmpiricalDistributionImpl
    // org.apache.commons.math.random.ValueServer
    { "URL {0} contains no data",
      "l''adresse {0} ne contient aucune donn\u00e9e" },

    // org.apache.commons.math.random.AbstractRandomGenerator
    // org.apache.commons.math.random.BitsStreamGenerator
    { "upper bound must be positive ({0})",
      "la borne sup\u00e9rieure doit \u00eatre positive ({0})" },

    // org.apache.commons.math.random.RandomDataImpl
    { "length must be positive ({0})",
      "la longueur doit \u00eatre positive ({0})" },
    { "upper bound ({0}) must be greater than lower bound ({1})",
      "la borne sup\u00e9rieure ({0}) doit \u00eatre sup\u00e9rieure" +
      " \u00e0 la borne inf\u00e9rieure ({1})" },
    { "permutation k ({0}) exceeds n ({1})",
      "la permutation k ({0}) d\u00e9passe n ({1})" },
    { "permutation k ({0}) must be positive",
      "la permutation k ({0}) doit \u00eatre positive" },
    { "sample size ({0}) exceeds collection size ({1})",
      "la taille de l''\u00e9chantillon ({0}) d\u00e9passe la taille de la collection ({1})" },

    // org.apache.commons.math.linear.decomposition.EigenDecompositionImpl
    { "cannot solve degree {0} equation",
      "impossible de r\u00e9soudre une \u00e9quation de degr\u00e9 {0}" },
    { "eigen decomposition of assymetric matrices not supported yet",
      "la d\u00e9composition en valeurs/vecteurs propres de matrices " +
      "non sym\u00e9triques n''est pas encore disponible" },

    // org.apache.commons.math.linear.decomposition.NonSquareMatrixException
    // org.apache.commons.math.stat.regression.AbstractMultipleLinearRegression
    { "a {0}x{1} matrix was provided instead of a square matrix",
      "une matrice {0}x{1} a \u00e9t\u00e9 fournie \u00e0 la place d''une matrice carr\u00e9e" },

    // org.apache.commons.math.linear.decomposition.SingularMatrixException
    { "matrix is singular",
      "matrice singuli\u00e8re" },

    // org.apache.commons.math.linear.decomposition.SingularValueDecompositionImpl
    { "cutoff singular value is {0}, should be at most {1}",
      "la valeur singuli\u00e8re de coupure vaut {0}, elle ne devrait pas d\u00e9passer {1}" },

    // org.apache.commons.math.linear.decomposition.CholeskyDecompositionImpl
    // org.apache.commons.math.linear.decomposition.EigenDecompositionImpl
    // org.apache.commons.math.linear.decomposition.LUDecompositionImpl
    // org.apache.commons.math.linear.decomposition.QRDecompositionImpl
    // org.apache.commons.math.linear.decomposition.SingularValueDecompositionImpl
    { "dimensions mismatch: got {0}x{1} but expected {2}x{3}",
      "dimensions incoh\u00e9rentes : {0}x{1} \u00e0 la place de {2}x{3}" },

    // org.apache.commons.math.linear.decomposition.CholeskyDecompositionImpl
    // org.apache.commons.math.linear.decomposition.EigenDecompositionImpl
    // org.apache.commons.math.linear.decomposition.LUDecompositionImpl
    // org.apache.commons.math.linear.decomposition.QRDecompositionImpl
    // org.apache.commons.math.linear.decomposition.SingularValueDecompositionImpl
    // org.apache.commons.math.linear.ArrayRealVector
    // org.apache.commons.math.linear.SparseRealVector
    { "vector length mismatch: got {0} but expected {1}",
      "taille de vecteur invalide : {0} au lieu de {1} attendue" },

    // org.apache.commons.math.linear.ArrayRealVector
    // org.apache.commons.math.linear.ArrayFieldVector
    // org.apache.commons.math.linear.SparseRealVector
    { "index {0} out of allowed range [{1}, {2}]",
      "index {0} hors de la plage autoris\u00e9e [{1}, {2}]" },
    { "vector must have at least one element",
      "un vecteur doit comporter au moins un \u00e9l\u00e9ment" },
    { "position {0} and size {1} don't fit to the size of the input array {2}",
      "la position {0} et la taille {1} sont incompatibles avec la taille du tableau d''entr\u00e9e {2}"},

    // org.apache.commons.math.linear.AbstractRealMatrix
    // org.apache.commons.math.linear.AbstractFieldMatrix
    { "invalid row dimension: {0} (must be positive)",
      "nombre de lignes invalide : {0} (doit \u00eatre positif)" },
    { "invalid column dimension: {0} (must be positive)",
      "nombre de colonnes invalide : {0} (doit \u00eatre positif)" },
    { "matrix must have at least one row",
      "une matrice doit comporter au moins une ligne" },
    { "matrix must have at least one column",
      "une matrice doit comporter au moins une colonne" },

    // org.apache.commons.math.linear.AbstractRealMatrix
    // org.apache.commons.math.linear.AbstractFieldMatrix
    // org.apache.commons.math.stat.inference.ChiSquareTestImpl
    { "some rows have length {0} while others have length {1}",
      "certaines lignes ont une longueur de {0} alors que d''autres ont une longueur de {1}" },

    // org.apache.commons.math.linear.MatrixUtils
    { "row index {0} out of allowed range [{1}, {2}]",
      "index de ligne {0} hors de la plage autoris\u00e9e [{1}, {2}]" },
    { "column index {0} out of allowed range [{1}, {2}]",
      "index de colonne {0} hors de la plage autoris\u00e9e [{1}, {2}]" },
    { "initial row {0} after final row {1}",
      "ligne initiale {0} apr\u00e8s la ligne finale {1}" },
    { "initial column {0} after final column {1}",
      "colonne initiale {0} apr\u00e8s la colonne finale {1}" },
    { "empty selected row index array",
      "tableau des indices de lignes s\u00e9lectionn\u00e9es vide" },
    { "empty selected column index array",
      "tableau des indices de colonnes s\u00e9lectionn\u00e9es vide" },
    { "{0}x{1} and {2}x{3} matrices are not addition compatible",
      "les dimensions {0}x{1} et {2}x{3} sont incompatibles pour l'addition matricielle" },
    { "{0}x{1} and {2}x{3} matrices are not subtraction compatible",
      "les dimensions {0}x{1} et {2}x{3} sont incompatibles pour la soustraction matricielle" },
    { "{0}x{1} and {2}x{3} matrices are not multiplication compatible",
      "les dimensions {0}x{1} et {2}x{3} sont incompatibles pour la multiplication matricielle" },

    // org.apache.commons.math.linear.BlockRealMatrix
    { "wrong array shape (block length = {0}, expected {1})",
      "forme de tableau erron\u00e9e (bloc de longueur {0} au lieu des {1} attendus)" },

    // org.apache.commons.math.complex.Complex
    { "cannot compute nth root for null or negative n: {0}",
     "impossible de calculer la racine ni\u00e8me pour n n\u00e9gatif ou nul : {0}" },

   // org.apache.commons.math.complex.ComplexFormat
   { "unparseable complex number: \"{0}\"",
     "\u00e9chec d''analyse du nombre complexe \"{0}\"" },
   { "cannot format a {0} instance as a complex number",
     "impossible de formater une instance de {0} comme un nombre complexe" },
   { "empty string for imaginary character",
     "cha\u00eene vide pour le caract\u00e8 imaginaire" },
   { "null imaginary format",
     "format imaginaire nul" },
   { "null real format",
     "format r\u00e9el nul" },

   // org.apache.commons.math.complex.ComplexUtils
   { "negative complex module {0}",
     "module n\u00e9gatif ({0}) pour un nombre complexe" },

   // org.apache.commons.math.geometry.Vector3DFormat
   { "unparseable 3D vector: \"{0}\"",
     "\u00e9chec d''analyse du vecteur de dimension 3 \"{0}\"" },
   { "cannot format a {0} instance as a 3D vector",
     "impossible de formater une instance de {0} comme un vecteur de dimension 3" },

   // org.apache.commons.math.linear.RealVectorFormat
   { "unparseable real vector: \"{0}\"",
     "\u00e9chec d''analyse du vecteur r\u00e9el \"{0}\"" },
   { "cannot format a {0} instance as a real vector",
     "impossible de formater une instance de {0} comme un vecteur r\u00e9el" },

   // org.apache.commons.math.util.ResizableDoubleArray
   { "the index specified: {0} is larger than the current maximal index {1}",
     "l''index sp\u00e9cifi\u00e9 ({0}) d\u00e9passe l''index maximal courant ({1})" },
   { "elements cannot be retrieved from a negative array index {0}",
     "impossible d''extraire un \u00e9l\u00e9ment \u00e0 un index n\u00e9gatif ({0})" },
   { "cannot set an element at a negative index {0}",
     "impossible de mettre un \u00e9l\u00e9ment \u00e0 un index n\u00e9gatif ({0})" },
   { "cannot substitute an element from an empty array",
     "impossible de substituer un \u00e9l\u00e9ment dans un tableau vide" },
   { "contraction criteria ({0}) smaller than the expansion factor ({1}).  This would " +
     "lead to a never ending loop of expansion and contraction as a newly expanded " +
     "internal storage array would immediately satisfy the criteria for contraction.",
     "crit\u00e8re de contraction ({0}) inf\u00e9rieur au facteur d''extension. Ceci " +
     "induit une boucle infinie d''extensions/contractions car tout tableau de stockage " +
     "fra\u00eechement \u00e9tendu respecte imm\u00e9diatement le crit\u00e8re de contraction."},
   { "contraction criteria smaller than one ({0}).  This would lead to a never ending " +
     "loop of expansion and contraction as an internal storage array length equal " +
     "to the number of elements would satisfy the contraction criteria.",
     "crit\u00e8re de contraction inf\u00e9rieur \u00e0 un ({0}). Ceci induit une boucle " +
     "infinie d''extensions/contractions car tout tableau de stockage de longueur \u00e9gale " +
     "au nombre d''\u00e9l\u00e9ments respecte le crit\u00e8re de contraction." },
   { "expansion factor smaller than one ({0})",
     "facteur d''extension inf\u00e9rieur \u00e0 un ({0})"},
   { "cannot discard {0} elements from a {1} elements array",
     "impossible d''enlever {0} \u00e9l\u00e9ments d''un tableau en contenant {1}"},
   { "cannot discard a negative number of elements ({0})",
     "impossible d''enlever un nombre d''\u00e9l\u00e9ments{0} n\u00e9gatif"},
   { "unsupported expansion mode {0}, supported modes are {1} ({2}) and {3} ({4})",
     "mode d''extension {0} no support\u00e9, les modes support\u00e9s sont {1} ({2}) et {3} ({4})" },
   { "initial capacity ({0}) is not positive",
     "la capacit\u00e9 initiale ({0}) n''est pas positive" },
   { "index ({0}) is not positive",
     "l''indice ({0}) n''est pas positif" },

   // org.apache.commons.math.analysis.polynomials.PolynomialFunction
   // org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm
   { "empty polynomials coefficients array",
     "tableau de coefficients polyn\u00f4miaux vide" },

   // org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm
   { "array sizes should have difference 1 ({0} != {1} + 1)",
     "les tableaux devraient avoir une diff\u00e9rence de taille de 1 ({0} != {1} + 1)" },

   // org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm
   { "identical abscissas x[{0}] == x[{1}] == {2} cause division by zero",
     "division par z\u00e9ro caus\u00e9e par les abscisses identiques x[{0}] == x[{1}] == {2}" },

   // org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction
   { "spline partition must have at least {0} points, got {1}",
     "une partiction spline n\u00e9cessite au moins {0} points, seuls {1} ont \u00e9t\u00e9 fournis" },
   { "knot values must be strictly increasing",
     "les n\u0153uds d''interpolation doivent \u00eatre strictement croissants" },
   { "number of polynomial interpolants must match the number of segments ({0} != {1} - 1)",
     "le nombre d''interpolants polyn\u00f4miaux doit correspondre au nombre de segments ({0} != {1} - 1)" },

   // org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl
   { "function to solve cannot be null",
     "la fonction \u00e0 r\u00e9soudre ne peux pas \u00eatre nulle" },
   { "invalid interval, initial value parameters:  lower={0}, initial={1}, upper={2}",
     "param\u00e8tres de l''intervalle initial invalides : borne inf = {0}, valeur initiale = {1}, borne sup = {2}" },

   // org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl
   // org.apache.commons.math.analysis.solvers.BrentSolver
   { "function values at endpoints do not have different signs.  Endpoints: [{0}, {1}], Values: [{2}, {3}]",
     "les valeurs de la fonction aux bornes n''ont pas des signes diff\u00e9rents. Bornes : [{0}, {1}], valeurs : [{2}, {3}]" },

   // org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl
   // org.apache.commons.math.analysis.integration.UnivariateRealIntegratorImpl
   // org.apache.commons.math.transform.FastFourierTransformer
   { "endpoints do not specify an interval: [{0}, {1}]",
     "les extr\u00e9mit\u00e9s ne constituent pas un intervalle : [{0}, {1}]" },

   // org.apache.commons.math.analysis.solvers.LaguerreSolver
   { "function is not polynomial",
     "la fonction n''est pas p\u00f4lynomiale" },

   // org.apache.commons.math.analysis.solvers.NewtonSolver
   { "function is not differentiable",
     "la fonction n''est pas diff\u00e9rentiable" },

   // org.apache.commons.math.analysis.integration.UnivariateRealIntegratorImpl
   { "invalid iteration limits: min={0}, max={1}",
     "limites d''it\u00e9rations invalides : min = {0}, max = {1}" },

   // org.apache.commons.math.analysis.integration.LegendreGaussIntegrator
   { "{0} points Legendre-Gauss integrator not supported," +
     " number of points must be in the {1}-{2} range",
     "int\u00e9grateur de Legendre-Gauss non support\u00e9 en {0} points, " +
     "le nombre de points doit \u00eatre entre {1} et {2}" },

   // org.apache.commons.math.fraction.Fraction
   { "zero denominator in fraction {0}/{1}",
     "d\u00e9nominateur null dans le nombre rationnel {0}/{1}" },
   { "overflow in fraction {0}/{1}, cannot negate",
     "d\u00e9passement de capacit\u00e9 pour la fraction {0}/{1}, son signe ne peut \u00eatre chang\u00e9" },
   { "overflow, numerator too large after multiply: {0}",
     "d\u00e9passement de capacit\u00e9 pour le num\u00e9rateur apr\u00e8s multiplication : {0}" },
   { "the fraction to divide by must not be zero: {0}/{1}",
     "division par un nombre rationnel nul : {0}/{1}" },
   { "null fraction",
     "fraction nulle" },

   // org.apache.commons.math.geometry.Rotation
   { "zero norm for rotation axis",
     "norme nulle pour un axe de rotation" },
   { "zero norm for rotation defining vector",
     "norme nulle pour un axe de d\u00e9finition de rotation" },

   // org.apache.commons.math.geometry.Vector3D
   // org.apache.commons.math.linear.ArrayRealVector
   { "cannot normalize a zero norm vector",
     "impossible de normer un vecteur de norme nulle" },
   { "zero norm",
     "norme nulle" },

   // org.apache.commons.math.ConvergingAlgorithmImpl
   { "no result available",
     "aucun r\u00e9sultat n''est disponible" },

   // org.apache.commons.math.linear.BigMatrixImpl
   { "first {0} rows are not initialized yet",
     "les {0} premi\u00e8res lignes ne sont pas encore initialis\u00e9es" },
   { "first {0} columns are not initialized yet",
     "les {0} premi\u00e8res colonnes ne sont pas encore initialis\u00e9es" },

   // org.apache.commons.math.stat.Frequency
   { "class ({0}) does not implement Comparable",
     "la classe ({0}) n''implante pas l''interface Comparable" },
   { "instance of class {0} not comparable to existing values",
     "l''instance de la classe {0} n''est pas comparable aux valeurs existantes" },

   // org.apache.commons.math.stat.StatUtils
   { "input arrays must have the same positive length ({0} and {1})",
     "les tableaux d''entr\u00e9e doivent avoir la m\u00eame taille positive ({0} et {1})" },
   { "input arrays must have the same length and at least two elements ({0} and {1})",
     "les tableaux d''entr\u00e9e doivent avoir la m\u00eame taille" +
     " et au moins deux \u00e9l\u00e9ments ({0} et {1})" },

   // org.apache.commons.math.stat.correlation.Covariance
   { "arrays must have the same length and both must have at " +
     "least two elements. xArray has size {0}, yArray has {1} elements",
     "les tableaux doivent avoir la m\u00eame taille " +
     "et comporter au moins deux \u00e9l\u00e9ments. " +
     "xArray a une taille de {0}, yArray a {1} \u00e9l\u00e9ments"},
   { "insufficient data: only {0} rows and {1} columns.",
     "donn\u00e9es insuffisantes : seulement {0} lignes et {1} colonnes." },

   // org.apache.commons.math.stat.correlation.PearsonsCorrelation
   { "covariance matrix is null",
     "la matrice de covariance est nulle" },
   { "invalid array dimensions. xArray has size {0}; yArray has {1} elements",
     "dimensions de tableaux invalides. xArray a une taille de {0}, " +
     "yArray a {1} \u00e9l\u00e9ments" },

   // org.apache.commons.math.stat.descriptive.DescriptiveStatistics
   { "window size must be positive ({0})",
     "la taille de la fen\u00eatre doit \u00eatre positive ({0})" },
   { "percentile implementation {0} does not support {1}",
     "l''implantation de pourcentage {0} ne dispose pas de la m\u00e9thode {1}" },
   { "cannot access {0} method in percentile implementation {1}",
     "acc\u00e8s impossible \u00e0 la m\u00e9thode {0}" +
     " dans l''implantation de pourcentage {1}" },
   { "out of bounds quantile value: {0}, must be in (0, 100]",
     "valeur de quantile {0} hors bornes, doit \u00eatre dans l''intervalle ]0, 100]" },

   // org.apache.commons.math.stat.descriptive.moment.Variance
   // org.apache.commons.math.stat.descriptive.moment.SemiVariance
   // org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic
   // org.apache.commons.math.stat.descriptive.AbstractUnivariateStatistic
   { "input values array is null",
     "le tableau des valeurs d''entr\u00e9es est nul" },

   // org.apache.commons.math.stat.descriptive.AbstractUnivariateStatistic
   { "start position cannot be negative ({0})",
     "la position de d\u00e9part ne peut pas \u00eatre n\u00e9gative" },
   { "length cannot be negative ({0})",
     "la longueur ne peut pas \u00eatre n\u00e9gative" },
   { "subarray ends after array end",
     "le sous-tableau se termine apr\u00e8s la fin du tableau" },

   // org.apache.commons.math.stat.descriptive.moment.GeometricMean
   // org.apache.commons.math.stat.descriptive.MultivariateSummaryStatistics
   // org.apache.commons.math.stat.descriptive.SummaryStatistics
   { "{0} values have been added before statistic is configured",
     "{0} valeurs ont \u00e9t\u00e9 ajout\u00e9es " +
     "avant que la statistique ne soit configur\u00e9e" },

   // org.apache.commons.math.stat.descriptive.moment.Kurtosis
   { "statistics constructed from external moments cannot be incremented",
     "les statistiques bas\u00e9es sur des moments externes ne peuvent pas \u00eatre incr\u00e9ment\u00e9es" },
   { "statistics constructed from external moments cannot be cleared",
     "les statistiques bas\u00e9es sur des moments externes ne peuvent pas \u00eatre remises \u00e0 z\u00e9ro" },

   // org.apache.commons.math.stat.inference.ChiSquareTestImpl
   { "expected array length = {0}, must be at least 2",
     "le tableau des valeurs attendues a une longueur de {0}, elle devrait \u00eatre au moins de 2" },
   { "observed array length = {0}, must be at least 2",
     "le tableau des valeurs observ\u00e9es a une longueur de {0}, elle devrait \u00eatre au moins de 2" },
   { "observed counts are all 0 in first observed array",
     "aucune occurrence dans le premier tableau des observations" },
   { "observed counts are all 0 in second observed array",
     "aucune occurrence dans le second tableau des observations" },
   { "observed counts are both zero for entry {0}",
     "les occurrences observ\u00e9es sont toutes deux nulles pour l'entr\u00e9e {0}" },
   { "invalid row dimension: {0} (must be at least 2)",
     "nombre de lignes invalide : {0} (doit \u00eatre au moins de 2)" },
   { "invalid column dimension: {0} (must be at least 2)",
     "nombre de colonnes invalide : {0} (doit \u00eatre au moins de 2)" },
   { "element {0} is not positive: {1}",
     "l''\u00e9l\u00e9ment {0} n''est pas positif : {1}" },
   { "element {0} is negative: {1}",
     "l''\u00e9l\u00e9ment {0} est n\u00e9gatif : {1}" },
   { "element ({0}, {1}) is negative: {2}",
     "l''\u00e9l\u00e9ment ({0}, {1}) est n\u00e9gatif : {2}" },

   // org.apache.commons.math.stat.inference.OneWayAnovaImpl
   { "two or more categories required, got {0}",
     "deux cat\u00e9gories ou plus sont n\u00e9cessaires, il y en a {0}" },
   { "two or more values required in each category, one has {0}",
     "deux valeurs ou plus sont n\u00e9cessaires pour chaque cat\u00e9gorie, une cat\u00e9gorie en a {0}" },

   // org.apache.commons.math.stat.inference.TTestImpl
   { "insufficient data for t statistic, needs at least 2, got {0}",
     "deux valeurs ou plus sont n\u00e9cessaires pour la statistique t, il y en a {0}" },

   // org.apache.commons.math.stat.inference.ChiSquareTestImpl
   // org.apache.commons.math.stat.inference.TTestImpl
   // org.apache.commons.math.stat.inference.OneWayAnovaImpl
   // org.apache.commons.math.stat.Regression
   { "out of bounds significance level {0}, must be between {1} and {2}",
     "niveau de signification {0} hors domaine, doit \u00eatre entre {1} et {2}" },

   // org.apache.commons.math.stat.regression.AbstractMultipleLinearRegression
   { "not enough data ({0} rows) for this many predictors ({1} predictors)",
     "pas assez de donn\u00e9es ({0} lignes) pour {1} pr\u00e9dicteurs" },

   // org.apache.commons.math.distribution.AbstractContinuousDistribution
   // org.apache.commons.math.distribution.AbstractIntegerDistribution
   // org.apache.commons.math.distribution.ExponentialDistributionImpl
   // org.apache.commons.math.distribution.BinomialDistributionImpl
   // org.apache.commons.math.distribution.CauchyDistributionImpl
   // org.apache.commons.math.distribution.PascalDistributionImpl
   // org.apache.commons.math.distribution.WeibullDistributionImpl
   { "{0} out of [{1}, {2}] range",
     "{0} hors du domaine [{1}, {2}]" },

   // org.apache.commons.math.distribution.AbstractDistribution
   // org.apache.commons.math.distribution.AbstractIntegerDistribution
   { "lower endpoint ({0}) must be less than or equal to upper endpoint ({1})",
     "la borne inf\u00e9rieure ({0}) devrait \u00eatre inf\u00e9rieure " +
     "ou \u00e9gale \u00e0 la borne sup\u00e9rieure ({1})" },

   // org.apache.commons.math.distribution.AbstractContinuousDistribution
   { "Cumulative probability function returned NaN for argument {0} p = {1}",
     "Fonction de probabilit\u00e9 cumulative retourn\u00e9 NaN \u00e0 l''argument de {0} p = {1}" },
   { "This distribution does not have a density function implemented",
     "La fonction de densit\u00e9 pour cette distribution n'a pas \u00e9t\u00e9 mis en oeuvre" },

   // org.apache.commons.math.distribution.AbstractIntegerDistribution
   { "Discrete cumulative probability function returned NaN for argument {0}",
     "Discr\u00e8tes fonction de probabilit\u00e9 cumulative retourn\u00e9 NaN \u00e0 l''argument de {0}" },


   // org.apache.commons.math.distribution.BinomialDistributionImpl
   { "number of trials must be non-negative ({0})",
     "le nombre d''essais ne doit pas \u00eatre n\u00e9gatif ({0})" },

   // org.apache.commons.math.distribution.ExponentialDistributionImpl
   // org.apache.commons.math.random.RandomDataImpl
   { "mean must be positive ({0})",
     "la moyenne doit \u00eatre positive ({0})" },

   // org.apache.commons.math.distribution.FDistributionImpl
   // org.apache.commons.math.distribution.TDistributionImpl
   { "degrees of freedom must be positive ({0})",
     "les degr\u00e9s de libert\u00e9 doivent \u00eatre positifs ({0})" },

   // org.apache.commons.math.distribution.GammaDistributionImpl
   { "alpha must be positive ({0})",
     "alpha doit \u00eatre positif ({0})" },
   { "beta must be positive ({0})",
     "beta doit \u00eatre positif ({0})" },

   // org.apache.commons.math.distribution.HypergeometricDistributionImpl
   { "number of successes ({0}) must be less than or equal to population size ({1})",
     "le nombre de succ\u00e8s doit \u00eatre inf\u00e9rieur " +
     "ou \u00e9gal \u00e0 la taille de la population ({1})" },
   { "sample size ({0}) must be less than or equal to population size ({1})",
     "la taille de l''\u00e9chantillon doit \u00eatre inf\u00e9rieure " +
     "ou \u00e9gale \u00e0 la taille de la population ({1})" },
   { "population size must be positive ({0})",
     "la taille de la population doit \u00eatre positive ({0})" },

   // org.apache.commons.math.distribution.HypergeometricDistributionImpl
   // org.apache.commons.math.random.RandomDataImpl
   { "sample size must be positive ({0})",
     "la taille de l''\u00e9chantillon doit \u00eatre positive ({0})" },

   // org.apache.commons.math.distribution.HypergeometricDistributionImpl
   // org.apache.commons.math.distribution.PascalDistributionImpl
   { "number of successes must be non-negative ({0})",
     "le nombre de succ\u00e8s ne doit pas \u00eatre n\u00e9gatif ({0})" },

   // org.apache.commons.math.distribution.NormalDistributionImpl
   // org.apache.commons.math.random.RandomDataImpl
   { "standard deviation must be positive ({0})",
     "l''\u00e9cart type doit \u00eatre positif ({0})" },

   // org.apache.commons.math.distribution.PoissonDistributionImpl
   // org.apache.commons.math.random.RandomDataImpl
   { "the Poisson mean must be positive ({0})",
     "la moyenne de Poisson doit \u00eatre positive ({0})" },

   // org.apache.commons.math.distribution.WeibullDistributionImpl
   { "shape must be positive ({0})",
     "le facteur de forme doit \u00eatre positif ({0})" },

   // org.apache.commons.math.distribution.WeibullDistributionImpl
   // org.apache.commons.math.distribution.CauchyDistributionImpl
   { "scale must be positive ({0})",
     "l''\u00e9chelle doit \u00eatre positive ({0})" },

   // org.apache.commons.math.distribution.ZipfDistributionImpl
   { "invalid number of elements {0} (must be positive)",
     "nombre d''\u00e9l\u00e9ments {0} invalide (doit \u00eatre positif)" },
   { "invalid exponent {0} (must be positive)",
     "exposant {0} invalide (doit \u00eatre positif)" },

   // org.apache.commons.math.transform.FastHadamardTransformer
   { "{0} is not a power of 2",
     "{0} n''est pas une puissance de 2" },

   // org.apache.commons.math.transform.FastFourierTransformer
   { "cannot compute 0-th root of unity, indefinite result",
     "impossible de calculer la racine z\u00e9roi\u00e8me de l''unit\u00e9, " +
     "r\u00e9sultat ind\u00e9fini" },
   { "roots of unity have not been computed yet",
     "les racines de l''unit\u00e9 n''ont pas encore \u00e9t\u00e9 calcul\u00e9es" },
   { "out of range root of unity index {0} (must be in [{1};{2}])",
     "index de racine de l''unit\u00e9 hors domaine (devrait \u00eatre dans [{1}; {2}])" },
   { "number of sample is not positive: {0}",
     "le nombre d''\u00e9chantillons n''est pas positif : {0}" },
   { "{0} is not a power of 2, consider padding for fix",
     "{0} n''est pas une puissance de 2, ajoutez des \u00e9l\u00e9ments pour corriger" },
   { "some dimensions don't match: {0} != {1}",
     "certaines dimensions sont incoh\u00e9rentes : {0} != {1}" },

   // org.apache.commons.math.transform.FastCosineTransformer
   { "{0} is not a power of 2 plus one",
     "{0} n''est pas une puissance de 2 plus un" },

   // org.apache.commons.math.transform.FastSineTransformer
   { "first element is not 0: {0}",
     "le premier \u00e9l\u00e9ment n''est pas nul : {0}" },

   // org.apache.commons.math.util.OpenIntToDoubleHashMap
   { "map has been modified while iterating",
     "la table d''adressage a \u00e9t\u00e9 modifi\u00e9e pendant l''it\u00e9ration" },
   { "iterator exhausted",
     "it\u00e9ration achev\u00e9e" },

   // org.apache.commons.math.MathRuntimeException
   { "internal error, please fill a bug report at {0}",
     "erreur interne, veuillez signaler l''erreur \u00e0 {0}" }

  };

  /**
   * Simple constructor.
   */
  public MessagesResources_fr() {
  }

  /**
   * Get the non-translated/translated messages arrays from this resource bundle.
   * @return non-translated/translated messages arrays
   */
  @Override
  public Object[][] getContents() {
    return CONTENTS.clone();
  }

}
