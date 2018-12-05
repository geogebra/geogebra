// -*- mode:C++ ; compile-command: "g++ -I.. -g -c moyal.cc" -*-
/*
 *  Copyright (C) 2000,2014 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
#ifndef _GIAC_MOYAL_H
#define _GIAC_MOYAL_H
#include "first.h"
#include <complex>

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  class gen;
  gen incomplete_beta(double a,double b,double p,bool regularize=true);
  gen lower_incomplete_gamma(double s,double z,bool regularize,GIAC_CONTEXT); // lower incomplete


  gen moyal(const gen & a,const gen & b,const gen & vars,const gen & order);
  gen _moyal(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_moyal ;

  gen Airy_Ai(const gen & a,const gen & b,const gen & vars,const gen & order);
  gen _Airy_Ai(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Airy_Ai ;

  gen Airy_Bi(const gen & a,const gen & b,const gen & vars,const gen & order);
  gen _Airy_Bi(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Airy_Bi ;

#ifndef USE_GMP_REPLACEMENTS
  gen randdiscrete(const vecteur &m, GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_discreted ;
#endif

  double randNorm(GIAC_CONTEXT);
  void randnorm2(double & r1,double & r2,GIAC_CONTEXT);
  gen _randNorm(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_randNorm ;
  extern const unary_function_ptr * const  at_randnormald ;

  double exp_rand(GIAC_CONTEXT);
  gen _randexp(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_randexp ;
  gen randpoisson(double lambda,GIAC_CONTEXT);
  gen randbinomial(int n,double P,GIAC_CONTEXT);
  double randchisquare(int k,GIAC_CONTEXT);
  double randstudent(int k,GIAC_CONTEXT);
  double randfisher(int k1,int k2,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_randchisquare ;
  extern const unary_function_ptr * const  at_randchisquared ;
  extern const unary_function_ptr * const  at_randstudent ;
  extern const unary_function_ptr * const  at_randstudentd ;
  extern const unary_function_ptr * const  at_randfisherd ;
  extern const unary_function_ptr * const  at_fisher ;
  extern const unary_function_ptr * const  at_fisherd ;
  extern const unary_function_ptr * const  at_randfisher ;
  extern const unary_function_ptr * const  at_cauchyd ;
  extern const unary_function_ptr * const  at_cauchy ;
  extern const unary_function_ptr * const  at_cauchy_cdf ;
  extern const unary_function_ptr * const  at_cauchy_icdf ;
  extern const unary_function_ptr * const  at_multinomial ;
  extern const unary_function_ptr * const  at_randmultinomial;

  gen _UTPN(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_UTPN ;

  gen UTPC(const gen & n_orig,const gen & x0,GIAC_CONTEXT);
  gen _UTPC(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_UTPC ;

  gen UTPT(const gen & n_orig,const gen & x0,GIAC_CONTEXT);
  gen _UTPT(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_UTPT ;

  gen UTPF(const gen & num,const gen & den,const gen & x0,GIAC_CONTEXT);
  gen _UTPF(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_UTPF ;

  gen binomial(const gen & n,const gen & k,const gen & p,GIAC_CONTEXT);
  gen _binomial(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_binomial ;
  extern const unary_function_ptr * const  at_BINOMIAL ;
  gen _negbinomial(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_negbinomial ;

  gen binomial_cdf(const gen & n,const gen &p,const gen & x0,const gen & x,GIAC_CONTEXT);
  gen _binomial_cdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_binomial_cdf ;

  gen binomial_icdf(const gen & n,const gen &p,const gen & x_orig,GIAC_CONTEXT);
  gen _binomial_icdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_binomial_icdf ;

  gen poisson(const gen & m,const gen & k,GIAC_CONTEXT);
  gen _poisson(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_poisson ;
  extern const unary_function_ptr * const  at_POISSON ;

  double poisson_cdf(double lambda,double x);
  gen poisson_cdf(const gen & n,const gen & x,GIAC_CONTEXT);
  gen _poisson_cdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_poisson_cdf ;

  gen poisson_icdf(const gen & m,const gen & t_orig,GIAC_CONTEXT);
  gen _poisson_icdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_poisson_icdf ;

  gen _normald(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_normald ;
  extern const unary_function_ptr * const  at_NORMALD ;
  gen _normal_cdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_normal_cdf ;

  gen _normal_icdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_normal_icdf ;

  gen student(const gen & n,const gen & x,GIAC_CONTEXT);
  gen _student(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_student ;
  extern const unary_function_ptr * const  at_studentd ;

  gen student_cdf(const gen & dof,const gen & x1,const gen & x2,GIAC_CONTEXT);
  gen _student_cdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_student_cdf ;

  gen student_icdf(const gen & m,const gen & t_orig,GIAC_CONTEXT);
  gen _student_icdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_student_icdf ;

  gen geometric(const gen & p,const gen & k,GIAC_CONTEXT);
  gen _geometric(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_geometric ;
  gen geometric_cdf(const gen & p,const gen & k,GIAC_CONTEXT);
  gen _geometric_cdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_geometric_cdf ;
  gen geometric_icdf(const gen & p,const gen & k,GIAC_CONTEXT);
  gen _geometric_icdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_geometric_icdf ;
  gen _randgeometric(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_randgeometric ;
  extern const unary_function_ptr * const  at_uniformd ;
  extern const unary_function_ptr * const  at_uniform ;
  extern const unary_function_ptr * const  at_uniform_cdf ;
  extern const unary_function_ptr * const  at_uniform_icdf ;
  extern const unary_function_ptr * const  at_uniformd_cdf ;
  extern const unary_function_ptr * const  at_uniformd_icdf ;

  extern const unary_function_ptr * const  at_exponentiald ;
  extern const unary_function_ptr * const  at_exponential ;
  extern const unary_function_ptr * const  at_exponential_cdf ;
  extern const unary_function_ptr * const  at_exponential_icdf ;
  extern const unary_function_ptr * const  at_exponentiald_cdf ;
  extern const unary_function_ptr * const  at_exponentiald_icdf ;

  gen chisquare(const gen & n,const gen & x,GIAC_CONTEXT);
  gen _chisquare(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_chisquare ;
  extern const unary_function_ptr * const  at_chisquared ;

  gen chisquare_cdf(const gen & dof,const gen & x1,const gen & x2,GIAC_CONTEXT);
  gen _chisquare_cdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_chisquare_cdf ;

  gen chisquare_icdf(const gen & m,const gen & t_orig,GIAC_CONTEXT);
  gen _chisquare_icdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_chisquare_icdf ;

  gen snedecor(const gen & a,const gen & b,const gen & x,GIAC_CONTEXT);
  gen _snedecor(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_snedecor ;

  gen snedecor_cdf(const gen & ndof,const gen & ddof,const gen & x,GIAC_CONTEXT);
  gen _snedecor_cdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_snedecor_cdf ;

  gen snedecor_icdf(const gen & num,const gen & den,const gen & t_orig,GIAC_CONTEXT);
  gen _snedecor_icdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_snedecor_icdf ;

  gen _randweibulld(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_randweibulld ;
  extern const unary_function_ptr * const  at_weibulld ;

  extern const unary_function_ptr * const  at_wilcoxont ;
  extern const unary_function_ptr * const  at_wilcoxonp ;
  extern const unary_function_ptr * const  at_wilcoxons ;

  gen Beta(const gen & a,const gen& b,GIAC_CONTEXT);
  gen _Beta(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Beta ;

  gen _betad(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_betad ;

  gen _betad_cdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_betad_cdf ;

  gen _betad_icdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_betad_icdf ;
  extern const unary_function_ptr * const  at_randbetad ;
  gen _randbetad(const gen & args,GIAC_CONTEXT);

  gen _gammad(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_gammad ;

  gen _gammad_cdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_gammad_cdf ;

  gen _gammad_icdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_gammad_icdf ;
  double rgamma(double a, double scale,GIAC_CONTEXT);
  gen _randgammad(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_randgammad ;

  gen _kolmogorovd(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_kolmogorovd ;

  gen _kolmogorovt(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_kolmogorovt ;

  gen _negbinomial_icdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_negbinomial_icdf ;

  // return 0 if not distrib
  // 1 normal, 2 binomial, 3 negbinomial, 4 poisson, 5 student, 
  // 6 fisher, 7 cauchy, 8 weibull, 9 betad, 10 gammad, 11 chisquare
  int is_distribution(const gen & args);
  bool is_discrete_distribution(int nd);
  int distrib_nargs(int nd); // number of args for the distribution
  bool distrib_support(int nd,gen & a,gen &b,bool truncate);
  // icdf and cdf function corresponding to the codes above
  gen icdf(int n);
  gen cdf(int n);
  gen distribution(int nd);

  int giacmax(const std::vector<int> & X);
  int giacmin(const std::vector<int> & X);
  void effectif(const std::vector<int> & x,std::vector<int> & eff,int m);
  void somme(const std::vector<int> & x,const std::vector<int> &y,std::vector<int> & z);

  double upper_incomplete_gammad(double s,double z,bool regularize);
  gen _upper_incomplete_gamma(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_upper_incomplete_gamma ;

  gen _polygamma(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_polygamma ;

  // kind=0: BesselI, =1 BesselJ, =2 BesselK, =3 BesselY
  gen Bessel(const gen & g,int kind,GIAC_CONTEXT);

  gen _BesselI(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_BesselI ;

  gen _BesselJ(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_BesselJ ;

  gen _BesselK(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_BesselK ;

  gen _BesselY(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_BesselY ;

  gen _harmonic(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_harmonic ;

  gen _constants_catalog(const gen & g,GIAC_CONTEXT);
  std::complex<double> LambertW(std::complex<double> x,int n=0);
#ifdef HAVE_LIBMPFR
  gen LambertW(const gen & z,int n);
#endif

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC


#endif // _GIAC_MOYAL_H
