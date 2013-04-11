// -*- mode:C++ ; compile-command: "g++ -I.. -g -c moyal.cc" -*-
/*
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
#ifndef _GIAC_MOYAL_H
#define _GIAC_MOYAL_H
#include "first.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  class gen;
  gen incomplete_beta(int a,int b,double p,bool regularize=true);
  gen lower_incomplete_gamma(double s,double z,bool regularize=true); // lower incomplete


  gen moyal(const gen & a,const gen & b,const gen & vars,const gen & order);
  gen _moyal(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_moyal ;

  gen Airy_Ai(const gen & a,const gen & b,const gen & vars,const gen & order);
  gen _Airy_Ai(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Airy_Ai ;

  gen Airy_Bi(const gen & a,const gen & b,const gen & vars,const gen & order);
  gen _Airy_Bi(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Airy_Bi ;

  gen randNorm();
  gen _randNorm(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_randNorm ;

  gen _randexp(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_randexp ;

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

  gen binomial_cdf(const gen & n,const gen &p,const gen & x0,const gen & x,GIAC_CONTEXT);
  gen _binomial_cdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_binomial_cdf ;

  gen binomial_icdf(const gen & n,const gen &p,const gen & x_orig,GIAC_CONTEXT);
  gen _binomial_icdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_binomial_icdf ;

  gen poisson(const gen & m,const gen & k,GIAC_CONTEXT);
  gen _poisson(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_poisson ;

  gen poisson_cdf(const gen & n,const gen & x,GIAC_CONTEXT);
  gen _poisson_cdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_poisson_cdf ;

  gen poisson_icdf(const gen & m,const gen & t_orig,GIAC_CONTEXT);
  gen _poisson_icdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_poisson_icdf ;

  gen _normald(const gen & g,GIAC_CONTEXT);
  gen _normal_cdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_normal_cdf ;

  gen _normal_icdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_normal_icdf ;

  gen student(const gen & n,const gen & x,GIAC_CONTEXT);
  gen _student(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_student ;

  gen student_cdf(const gen & dof,const gen & x1,const gen & x2,GIAC_CONTEXT);
  gen _student_cdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_student_cdf ;

  gen student_icdf(const gen & m,const gen & t_orig,GIAC_CONTEXT);
  gen _student_icdf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_student_icdf ;

  gen chisquare(const gen & n,const gen & x,GIAC_CONTEXT);
  gen _chisquare(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_chisquare ;

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

  gen Beta(const gen & a,const gen& b,GIAC_CONTEXT);
  gen _Beta(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Beta ;

  gen _lower_incomplete_gamma(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_lower_incomplete_gamma ;
  gen upper_incomplete_gamma(double s,double z,bool regularize);
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

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC


#endif // _GIAC_MOYAL_H
