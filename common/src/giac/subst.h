// -*- mode:C++ ; compile-command: "g++ -I.. -g -c subst.cc" -*-
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
#ifndef _GIAC_SUBST_H
#define _GIAC_SUBST_H
#include "first.h"
#include "gen.h"
#include "identificateur.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  gen checkanglemode(GIAC_CONTEXT);

  polynome gen2poly(const gen & g,int s); 
  gen degtorad(const gen & g,GIAC_CONTEXT);
  gen radtodeg(const gen & g,GIAC_CONTEXT);
  int findpos(const vecteur & v,const gen & g);

  // find symbolic vars in g that have u has sommet
  vecteur lop(const gen & g,const unary_function_ptr & u);
  vecteur lop(const gen & g,const std::vector<const unary_function_ptr * > & v);
  vecteur lop(const gen & g,const unary_function_ptr * v);
  vecteur loptab(const gen & g,const unary_function_ptr * v);
  // One substitution
  vecteur subst(const vecteur & v,const gen & i,const gen & newi,bool quotesubst,GIAC_CONTEXT);
  gen subst(const gen & e,const gen & i,const gen & newi,bool quotesubst,GIAC_CONTEXT);
  sparse_poly1 subst(const sparse_poly1 & v,const gen & i,const gen & newi,bool quotesubst,GIAC_CONTEXT);
  // Multi substitutions
  vecteur subst(const vecteur & v,const vecteur & i,const vecteur & newi,bool quotesubst,GIAC_CONTEXT);
  gen subst(const gen & e,const vecteur & i,const vecteur & ewi,bool quotesubst,GIAC_CONTEXT);
  vecteur sortsubst(const vecteur & v,const vecteur & i,const vecteur & newi,bool quotesubst,GIAC_CONTEXT); // assumes that i is sorted
  gen sortsubst(const gen & e,const vecteur & i,const vecteur & newi,bool quotesubst,GIAC_CONTEXT); // assumes that i is sorted

  gen quotesubst(const gen & e,const gen & i,const gen & newi,GIAC_CONTEXT);
  gen gen_feuille(const gen & g);
  // Quick check if e contains some ptr of v
  bool has_op(const gen & e,const unary_function_ptr * v);
  // Quick check if e contains v
  bool has_op(const gen & e,const unary_function_ptr & u);

  template<class T>
    int equalposcomp(const std::vector<T> & v, const T & w){
    int n=1;
    for (typename std::vector<T>::const_iterator it=v.begin();it!=v.end();++it){
      if ((*it)==w)
	return n;
      else
	n++;
    }
    return 0;
  }
  typedef gen (* gen_op_context) (const gen &,GIAC_CONTEXT);
  gen subst(const gen & e,const std::vector<const unary_function_ptr *> & v,const std::vector< gen_op_context > & w,bool quotesubst,GIAC_CONTEXT);
  gen subst(const gen & e,const unary_function_ptr * v,const gen_op_context * w,bool quotesubst,GIAC_CONTEXT);

  gen subst(const gen & e,const std::vector<const unary_function_ptr *> & v,const std::vector< gen (*) (const gen &) > & w,bool quotesubst,GIAC_CONTEXT);

  gen halftan(const gen & e,GIAC_CONTEXT);
  gen _halftan(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_halftan;
  gen shift_phase(const gen & e,GIAC_CONTEXT);
  gen _shift_phase(const gen & args,GIAC_CONTEXT);

  gen hyp2exp(const gen & e,GIAC_CONTEXT);
  gen _hyp2exp(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_hyp2exp;

  gen sincos(const gen & e,GIAC_CONTEXT);
  gen _sincos(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_sincos;

  gen trig2exp(const gen & e,GIAC_CONTEXT);
  gen _trig2exp(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_trig2exp;

  gen halftan_hyp2exp(const gen & e,GIAC_CONTEXT);
  gen _halftan_hyp2exp(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_halftan_hyp2exp;

  gen rewrite_hyper(const gen & e,GIAC_CONTEXT);

  gen asin2acos(const gen & e,GIAC_CONTEXT);
  gen _asin2acos(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_asin2acos;

  gen asin2atan(const gen & e,GIAC_CONTEXT);
  gen _asin2atan(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_asin2atan;

  gen acos2asin(const gen & e,GIAC_CONTEXT);
  gen _acos2asin(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_acos2asin;

  gen acos2atan(const gen & e,GIAC_CONTEXT);
  gen _acos2atan(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_acos2atan;

  gen atan2acos(const gen & e,GIAC_CONTEXT);
  gen _atan2acos(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_atan2acos;

  extern const unary_function_ptr * const  at_atrig2ln;

  gen atan2asin(const gen & e,GIAC_CONTEXT);
  gen _atan2asin(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_atan2asin;
  bool is_rational(const gen & g);
  vecteur as_linear_combination(const gen & g,vecteur & wrt,GIAC_CONTEXT);
  bool is_unit(const gen & g);
  bool is_algebraic_extension(const gen & g);
  vecteur rlvar(const gen &e,bool alg);

  // rewrite vars of e in terms of exp/ln if s1 resp. s2 is > 1
  // and simplify
  gen tsimplify_noexpln(const gen & e,int s1,int s2,GIAC_CONTEXT);
  gen tsimplify_common(const gen & e,GIAC_CONTEXT);

  gen tsimplify(const gen & e,GIAC_CONTEXT);
  gen _tsimplify(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_tsimplify;

  gen simplify(const gen & e,GIAC_CONTEXT);
  gen _simplify(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_simplify;

  gen trigcos(const gen & e,GIAC_CONTEXT);
  gen _trigcos(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_trigcos;

  gen trigsin(const gen & e,GIAC_CONTEXT);
  gen _trigsin(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_trigsin;

  gen trigtan(const gen & e,GIAC_CONTEXT);
  gen _trigtan(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_trigtan;

  gen tan2sincos(const gen & e,GIAC_CONTEXT);
  gen _tan2sincos(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_tan2sincos;

  gen tan2sincos2(const gen & e,GIAC_CONTEXT);
  gen _tan2sincos2(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_tan2sincos2;

  gen tan2cossin2(const gen & e,GIAC_CONTEXT);
  gen _tan2cossin2(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_tan2cossin2;

  gen tcollect(const gen & e,GIAC_CONTEXT);
  gen _tcollect(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_tcollect;

  gen lncollect(const gen & e,GIAC_CONTEXT);
  gen _lncollect(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_lncollect;

  gen _powexpand(const gen & args,GIAC_CONTEXT);
  gen powexpand(const gen & e,GIAC_CONTEXT);
  gen exp2pow(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_powexpand;

  gen _exp2pow(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_exp2pow;
  gen _pow2exp(const gen & e,GIAC_CONTEXT);

  gen pow2expln(const gen & e,GIAC_CONTEXT);
  gen simplifyfactorial(const gen & g,GIAC_CONTEXT);
  gen pow2expln(const gen & e,const identificateur & x,GIAC_CONTEXT);
  gen gamma2factorial(const gen & g,GIAC_CONTEXT);
  gen gammatofactorial(const gen & g,GIAC_CONTEXT);
  gen factorial2gamma(const gen & g,GIAC_CONTEXT);
  gen factorialtogamma(const gen & g,GIAC_CONTEXT);

  gen factor_xn(const gen & args,const gen & x,GIAC_CONTEXT);
  gen factor_xn(const gen & args,GIAC_CONTEXT);
  gen _factor_xn(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_factor_xn;


  gen Heavisidetosign(const gen & args,GIAC_CONTEXT);
  gen _Heavisidetosign(const gen & args,GIAC_CONTEXT);
  gen expln2trig(const gen & g,GIAC_CONTEXT);
  gen _mult_conjugate(const gen & g0,GIAC_CONTEXT);
  gen _mult_c_conjugate(const gen & g0,GIAC_CONTEXT);
  gen _combine(const gen & args,const context * contextptr);
  gen _rectangular2polar(const gen & args,const context * contextptr);
  gen _polar2rectangular(const gen & args,const context * contextptr);

  gen sin2tan2(const gen & e,GIAC_CONTEXT);
  gen cos2tan2(const gen & e,GIAC_CONTEXT);
  gen tan2tan2(const gen & e,GIAC_CONTEXT);
  gen sinh2exp(const gen & e,GIAC_CONTEXT);
  gen cosh2exp(const gen & e,GIAC_CONTEXT);
  gen tanh2exp(const gen & e,GIAC_CONTEXT);
  gen inv_test_exp(const gen & e,GIAC_CONTEXT);
  gen sin2exp(const gen & e,GIAC_CONTEXT);
  gen cos2exp(const gen & e,GIAC_CONTEXT);
  gen tan2exp(const gen & e,GIAC_CONTEXT);
  gen exp2sincos(const gen & e,GIAC_CONTEXT);
  gen tantosincos(const gen & e,GIAC_CONTEXT);
  gen tantosincos2(const gen & e,GIAC_CONTEXT);
  gen tantocossin2(const gen & e,GIAC_CONTEXT);
  gen asintoacos(const gen & e,GIAC_CONTEXT);
  gen acostoasin(const gen & e,GIAC_CONTEXT);
  gen asintoatan(const gen & e,GIAC_CONTEXT);
  gen atantoasin(const gen & e,GIAC_CONTEXT);
  gen acostoatan(const gen & e,GIAC_CONTEXT);
  gen atantoacos(const gen & e,GIAC_CONTEXT);
  gen trigcospow(const gen & g,GIAC_CONTEXT);
  gen trigsinpow(const gen & g,GIAC_CONTEXT);
  gen trigtanpow(const gen & g,GIAC_CONTEXT);
  gen powtopowexpand(const gen & g,GIAC_CONTEXT);
  gen exptopower(const gen & g,GIAC_CONTEXT);
  gen asin2ln(const gen & g_orig,GIAC_CONTEXT);
  gen acos2ln(const gen & g_orig,GIAC_CONTEXT);
  gen atan2ln(const gen & g_orig,GIAC_CONTEXT);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_SUBST_H
