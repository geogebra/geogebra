/* -*- mode:C++ ; compile-command: "g++ -I.. -g -c misc.cc" -*-
 *
 *  Copyright (C) 2000 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

#ifndef _GIAC_MISC_H_
#define _GIAC_MISC_H_
#include "first.h"
#include "global.h"
#include "gen.h"
#include "unary.h"
#include "symbolic.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  gen _scalar_product(const gen & args,GIAC_CONTEXT);
  gen _compare(const gen & args,GIAC_CONTEXT);
  gen _preval(const gen & args,GIAC_CONTEXT);
  vecteur divided_differences(const vecteur & x,const vecteur & y);
  gen _lagrange(const gen & args,GIAC_CONTEXT);
  gen _reorder(const gen & args,GIAC_CONTEXT);
  gen _adjoint_matrix(const gen & args,GIAC_CONTEXT);
  gen _equal2diff(const gen & args,GIAC_CONTEXT);
  gen _equal2list(const gen & args,GIAC_CONTEXT);
  gen _rank(const gen & args,GIAC_CONTEXT);
  gen _csc(const gen & args,GIAC_CONTEXT);
  gen _cot(const gen & args,GIAC_CONTEXT);
  gen _acsc(const gen & args,GIAC_CONTEXT);
  gen _ibpu(const gen & args,GIAC_CONTEXT) ;
  gen _changebase(const gen & args,GIAC_CONTEXT);
  gen _epsilon2zero(const gen & args,GIAC_CONTEXT);
  gen _suppress(const gen & args,GIAC_CONTEXT);
  gen _valuation(const gen & args,GIAC_CONTEXT);
  gen _degree(const gen & args,GIAC_CONTEXT);
  gen _lcoeff(const gen & args,GIAC_CONTEXT);
  gen _tcoeff(const gen & args,GIAC_CONTEXT);
  gen _sqrfree(const gen & args,GIAC_CONTEXT);
  gen _truncate(const gen & args,GIAC_CONTEXT);
  gen _canonical_form(const gen & args,GIAC_CONTEXT);
  gen _fcoeff(const gen & args,GIAC_CONTEXT);
  gen _froot(const gen & args,GIAC_CONTEXT);
  gen _roots(const gen & g,GIAC_CONTEXT);
  gen _divpc(const gen & args,GIAC_CONTEXT);
  gen _ptayl(const gen & args,GIAC_CONTEXT);
  gen _dfc(const gen & g_orig,GIAC_CONTEXT);
  gen _dfc2f(const gen & g,GIAC_CONTEXT);
  gen _float2rational(const gen & g,GIAC_CONTEXT);
  gen _gramschmidt(const gen & g,GIAC_CONTEXT);
  gen _pmin(const gen & g,GIAC_CONTEXT);
  gen _potential(const gen & g,GIAC_CONTEXT);
  gen _vpotential(const gen & g,GIAC_CONTEXT);
  gen _poly2symb(const gen & g,GIAC_CONTEXT);
  gen _symb2poly(const gen & g,GIAC_CONTEXT);
  gen _exp2trig(const gen & g,GIAC_CONTEXT);
  gen _nrows(const gen & g,GIAC_CONTEXT);
  gen _ncols(const gen & g,GIAC_CONTEXT);
  gen _l2norm(const gen & g0,GIAC_CONTEXT);
  gen _normalize(const gen & a,GIAC_CONTEXT);
  gen _lgcd(const gen & g,GIAC_CONTEXT);
  gen _float(const gen & g,GIAC_CONTEXT);
  gen _hold(const gen & g,GIAC_CONTEXT);
  gen _eigenvals(const gen & g,GIAC_CONTEXT);
  gen _Gcd(const gen & g,GIAC_CONTEXT);
  gen _Gcdex(const gen & g,GIAC_CONTEXT);
  gen _Factor(const gen & g,GIAC_CONTEXT);
  gen _Rref(const gen & g,GIAC_CONTEXT);
  gen _Rank(const gen & g,GIAC_CONTEXT);
  gen _Det(const gen & g,GIAC_CONTEXT);
  gen _Quo(const gen & g,GIAC_CONTEXT);
  gen _Rem(const gen & g,GIAC_CONTEXT);
  gen _Int(const gen & g,GIAC_CONTEXT);
  gen _divisors(const gen & g,GIAC_CONTEXT);
  gen _maxnorm(const gen & g0,GIAC_CONTEXT);
  gen _l1norm(const gen & g0,GIAC_CONTEXT);
  gen _dotprod(const gen & g,GIAC_CONTEXT);
  gen _diag(const gen & g,GIAC_CONTEXT);
  gen _input(const gen & args,GIAC_CONTEXT);
  gen _textinput(const gen & args,GIAC_CONTEXT);
  gen _primpart(const gen & g,GIAC_CONTEXT);
  gen _content(const gen & g,GIAC_CONTEXT);
  gen _coeff(const gen & g,GIAC_CONTEXT);
  gen _chrem(const gen & g,GIAC_CONTEXT);
  gen _genpoly(const gen & g,GIAC_CONTEXT);
  gen _median(const gen & g,GIAC_CONTEXT);
  gen _quartile1(const gen & g,GIAC_CONTEXT);
  gen _quartile3(const gen & g,GIAC_CONTEXT);
  gen _quantile(const gen & g,GIAC_CONTEXT);
  gen _quartiles(const gen & g,GIAC_CONTEXT);
  gen _moustache(const gen & g_orig,GIAC_CONTEXT);
  gen _mean(const gen & g,GIAC_CONTEXT);
  gen _stdDev(const gen & g,GIAC_CONTEXT);
  gen _stddev(const gen & g,GIAC_CONTEXT);
  gen _variance(const gen & g,GIAC_CONTEXT);
  gen _covariance_correlation(const gen & g,GIAC_CONTEXT);
  gen _covariance(const gen & g,GIAC_CONTEXT);
  gen _correlation(const gen & g,GIAC_CONTEXT);
  gen _interval2center(const gen & g,GIAC_CONTEXT);
  gen function_regression(const gen & g,const gen & u1,const gen & u2,gen & a,gen &b,double & xmin,double & xmax,gen & correl2,GIAC_CONTEXT);
  gen _linear_regression(const gen & g,GIAC_CONTEXT);
  gen _exponential_regression(const gen & g,GIAC_CONTEXT);
  gen _power_regression(const gen & g,GIAC_CONTEXT);
  gen regression_plot_attributs(const gen & g,vecteur & attributs,bool & eq,bool & r,GIAC_CONTEXT);
  gen _linear_regression_plot(const gen & g,GIAC_CONTEXT);
  gen _exponential_regression_plot(const gen & g,GIAC_CONTEXT);
  gen _logarithmic_regression_plot(const gen & g,GIAC_CONTEXT);
  gen _power_regression_plot(const gen & g,GIAC_CONTEXT);
  gen _polynomial_regression(const gen & g,GIAC_CONTEXT);
  gen _polynomial_regression_plot(const gen & g,GIAC_CONTEXT);
  gen _logistic_regression(const gen & g,GIAC_CONTEXT);
  gen _logistic_regression_plot(const gen & g,GIAC_CONTEXT);
  gen _linear_interpolate(const gen & g,GIAC_CONTEXT);
  gen _parabolic_interpolate(const gen & g,GIAC_CONTEXT);
  gen _center2interval(const gen & g,GIAC_CONTEXT);
  gen _histogram(const gen & g,GIAC_CONTEXT);
  gen _cumulated_frequencies(const gen & g,GIAC_CONTEXT);
  gen _classes(const gen & g,GIAC_CONTEXT);
  gen _listplot(const gen & g,GIAC_CONTEXT);
  gen _scatterplot(const gen & g,GIAC_CONTEXT);
  gen _polygonplot(const gen & g,GIAC_CONTEXT);
  gen _polygonscatterplot(const gen & g,GIAC_CONTEXT);
  gen _diagramme_batons(const gen & g,GIAC_CONTEXT);
  gen _camembert(const gen & g,GIAC_CONTEXT);
  gen cross_prod(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT);
  gen _convexhull(const gen & g,GIAC_CONTEXT);
  matrice simplex_reduce(const matrice & m_orig,vecteur & bfs,gen & optimum,bool max_pb,bool choose_first,GIAC_CONTEXT);
  gen _simplex_reduce(const gen & g,GIAC_CONTEXT);
  gen _spline(const gen & g,GIAC_CONTEXT);
  gen giac_bitand(const gen & a,const gen & b);
  gen giac_bitor(const gen & a,const gen & b);
  gen giac_bitxor(const gen & a,const gen & b);
  gen giac_hamdist(const gen & a,const gen & b);
  gen _bitand(const gen & g,GIAC_CONTEXT);
  gen _bitor(const gen & g,GIAC_CONTEXT);
  gen _bitxor(const gen & g,GIAC_CONTEXT);
  gen _hamdist(const gen & g,GIAC_CONTEXT);
  gen _plotarea(const gen & g,GIAC_CONTEXT);
  gen _add_language(const gen & args,GIAC_CONTEXT);
  gen _remove_language(const gen & args,GIAC_CONTEXT);
  gen _show_language(const gen & args,GIAC_CONTEXT);
  gen _os_version(const gen & args,GIAC_CONTEXT);

  extern const unary_function_ptr * const  at_normalize;
  void aplatir(const matrice & m,vecteur & v,bool full=false);

  vecteur gen2continued_fraction(const gen & g,int n,GIAC_CONTEXT);
  gen float2rational(double d_orig,double eps,GIAC_CONTEXT);

  extern const unary_function_ptr * const  at_preval;
  extern const unary_function_ptr * const  at_dotprod;
  extern const unary_function_ptr * const  at_mean;
  extern const unary_function_ptr * const  at_median;
  extern const unary_function_ptr * const  at_stddev;
  extern const unary_function_ptr * const  at_variance;
  extern const unary_function_ptr * const  at_lagrange;
  extern const unary_function_ptr * const  at_reorder;
  extern const unary_function_ptr * const  at_adjoint_matrix;
  extern const unary_function_ptr * const  at_equal2diff;
  extern const unary_function_ptr * const  at_rank;
  extern const unary_function_ptr * const  at_diag;
  extern const unary_function_ptr * const  at_sec;
  extern const unary_function_ptr * const  at_csc;
  extern const unary_function_ptr * const  at_cot;
  extern const unary_function_ptr * const  at_asec;
  extern const unary_function_ptr * const  at_acsc;
  extern const unary_function_ptr * const  at_acot;
  gen _sec(const gen & args,GIAC_CONTEXT);
  gen _asec(const gen & args,GIAC_CONTEXT);
  gen _acsc(const gen & args,GIAC_CONTEXT);
  gen _csc(const gen & args,GIAC_CONTEXT);
  gen _acot(const gen & args,GIAC_CONTEXT);
  gen _cot(const gen & args,GIAC_CONTEXT);

  extern const unary_function_ptr * const  at_ibpu;
  extern const unary_function_ptr * const  at_changebase;
  extern const unary_function_ptr * const  at_epsilon2zero;
  extern const unary_function_ptr * const  at_suppress;
  extern const unary_function_ptr * const  at_froot;
  extern const unary_function_ptr * const  at_fcoeff;
  extern const unary_function_ptr * const  at_truncate;
  extern const unary_function_ptr * const  at_divpc;
  extern const unary_function_ptr * const  at_ptayl;
  extern const unary_function_ptr * const  at_float2rational;
  extern const unary_function_ptr * const  at_gramschmidt;
  extern const unary_function_ptr * const  at_pmin;
  extern const unary_function_ptr * const  at_potential;
  extern const unary_function_ptr * const  at_vpotential;
  extern const unary_function_ptr * const  at_symb2poly1;
  extern const unary_function_ptr * const  at_poly12symb;
  extern const unary_function_ptr * const  at_exp2trig;
  extern const unary_function_ptr * const  at_nrows;
  extern const unary_function_ptr * const  at_ncols;
  extern const unary_function_ptr * const  at_l2norm;
  extern const unary_function_ptr * const  at_input;
#ifdef RTOS_THREADX
  // extern const unary_function_eval __input;
  extern const alias_unary_function_eval __input;
#else
  extern unary_function_eval __input;
#endif
  extern const unary_function_ptr * const  at_histogram;
  extern const unary_function_ptr * const  at_bitand;
  extern const unary_function_ptr * const  at_bitor;
  extern const unary_function_ptr * const  at_bitxor;
  extern const unary_function_ptr * const  at_hamdist;
  matrice effectifs(const vecteur & v,double class_minimum,double class_size,GIAC_CONTEXT);
  // scalar product <a|b> (unlike dotproduct, takes the conjugate of coordinates of a)
  gen scalarproduct(const vecteur & a,const vecteur & b,GIAC_CONTEXT);
  // solution of A*x=b_orig with initial guess x0 up to precision eps
  // A must be hermitian or real symmetric
  // This function could be optimized if A, b_orig, x0 has double/complex<double> coefficients
  gen conjugate_gradient(const matrice & A,const vecteur & b_orig,const vecteur & x0,double eps,GIAC_CONTEXT);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // NO_NAMESPACE_GIAC

#endif // _GIAC_MISC_H
