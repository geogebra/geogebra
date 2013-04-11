// -*- mode:C++ ; compile-command: "g++ -I.. -g -c intg.cc" -*-
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
#ifndef _GIAC_INTG_H
#define _GIAC_INTG_H
#include "first.h"
#include <string>

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  class gen;
  class identificateur;
  struct unary_function_ptr;
  gen complex_subst(const gen & e,const vecteur & substin,const vecteur & substout,GIAC_CONTEXT);
  gen complex_subst(const gen & e,const gen & x,const gen & newx,GIAC_CONTEXT);
  vecteur lvarxwithinv(const gen &e,const gen & x,GIAC_CONTEXT);
  bool is_constant_wrt(const gen & e,const gen & x,GIAC_CONTEXT);
  bool is_linear_wrt(const gen & e,const gen &x,gen & a,gen & b,GIAC_CONTEXT);
  bool is_quadratic_wrt(const gen & e,const gen &x,gen & a,gen & b,gen & c,GIAC_CONTEXT);
  gen linear_apply(const gen & e,const gen & x,gen & remains, GIAC_CONTEXT, gen (* f)(const gen &,const gen &,gen &,const context *));
  gen lnabs(const gen & x,GIAC_CONTEXT);
  gen surd(const gen & c,int n,GIAC_CONTEXT);
  gen _surd(const gen & args,GIAC_CONTEXT);
  gen invexptoexpneg(const gen& g,GIAC_CONTEXT);
  bool is_rewritable_as_f_of(const gen & fu,const gen & u,gen & fx,const gen & gen_x,GIAC_CONTEXT);

  gen firstcoefftrunc(const gen & e);
  // special version of lvarx that does not remove cst powers
  vecteur lvarxpow(const gen &e,const gen & x);

  // Check for hypergeometric e, if true
  // write e(x+1)/e(x) as P(n+1)/P(n)*Q(x)/R(x+1) 
  bool is_hypergeometric(const gen & e,const identificateur &x,vecteur &v,polynome & P,polynome & Q,polynome & R,GIAC_CONTEXT);
  // Write a fraction A/B as E[P]/P*Q/E[R] where E[P]=subst(P,x,x+1)
  // and Q and all positive shifts of R are prime together
  void AB2PQR(const polynome & A,const polynome & B,polynome & P,polynome & Q, polynome & R);
  polynome taylor(const polynome & P,const gen & g);

  // int(ratfrac,x=-inf..inf)
  bool intgab_ratfrac(const gen & e,const gen & x,gen & value,GIAC_CONTEXT);
  // integr. of rat. fcns.
  gen integrate_gen_rem(const gen & e, const gen & x, gen & remains_to_integrate,GIAC_CONTEXT);
  gen integrate_id_rem(const gen & e, const gen & x, gen & remains_to_integrate,GIAC_CONTEXT);
  gen linear_integrate(const gen & e,const gen & x,gen & remains_to_integrate,GIAC_CONTEXT);
  gen integrate_id(const gen & e,const identificateur & x,GIAC_CONTEXT);
  gen integrate_gen(const gen & e,const gen & f,GIAC_CONTEXT);
  gen _integrate(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_integrate ;
  double rombergo(const gen & f,const gen & x, const gen & a, const gen & b, int n,GIAC_CONTEXT);
  double rombergt(const gen & f,const gen & x, const gen & a, const gen & b, int n,GIAC_CONTEXT);
  gen romberg(const gen & f0,const gen & x0,const gen & a,const gen &b,const gen & eps,int nmax,GIAC_CONTEXT);
  gen symb_romberg(const gen & a,const gen & b);
  gen _romberg(const gen & args,GIAC_CONTEXT);
  gen ggb_var(const gen & f); // return vx_var or the first var in f if vx_var not there
  extern const unary_function_ptr * const  at_romberg;

  // remove quote inside a maple-like sum/product argument vector
  // returns true if maple syntax was used
  bool maple_sum_product_unquote(vecteur & v,GIAC_CONTEXT);
  // replace x=a..b by x,a,b in the second vector arg
  bool adjust_int_sum_arg(vecteur & v,int & s);
  bool rational_sum(const gen & e,const gen & x,gen & res,gen& remains_to_sum,bool allow_psi,GIAC_CONTEXT);
  gen prodsum(const gen & g,bool isprod);  
  polynome taylor(const polynome & P,const gen & g);
  bool gosper(const polynome & P,const polynome & Q,const polynome & R,polynome & Y,gen & deno,GIAC_CONTEXT);
  bool is_hypergeometric(const gen & e,const identificateur &x,vecteur &v,polynome & P,polynome & Q,polynome & R,GIAC_CONTEXT);
  gen sum(const gen & e,const gen & x,gen & remains_to_sum,GIAC_CONTEXT);
  gen sum_loop(const gen & e,const gen & x,int i,int j,GIAC_CONTEXT);
  gen sum(const gen & e,const gen & x,const gen & a,const gen &b,GIAC_CONTEXT);
  gen _sum(const gen & args,GIAC_CONTEXT) ;
  gen _Sum(const gen & args,GIAC_CONTEXT) ;
  gen bernoulli(const gen & x);
  gen _bernoulli(const gen & args,GIAC_CONTEXT) ;
  vecteur double2vecteur(const double * y,int dim);

  // type=0 for seq, 1 for prod, 2 for sum
  gen seqprod(const gen & g,int type,GIAC_CONTEXT);
  bool rational_sum(const gen & e,const gen & x,gen & res,gen& remains_to_sum,bool allow_psi,GIAC_CONTEXT);
  gen sum(const gen & e,const gen & x,gen & remains_to_sum,GIAC_CONTEXT);
  gen sum(const gen & e,const gen & x,const gen & a,const gen &b,GIAC_CONTEXT);
  gen _sum(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_sum;
  gen sum_loop(const gen & e,const gen & x,int i,int j,GIAC_CONTEXT);

  void decompose_plus(const vecteur & arg,const gen & x,vecteur & non_constant,gen & plus_constant,GIAC_CONTEXT);
  void decompose_prod(const vecteur & arg,const gen & x,vecteur & non_constant,gen & prod_constant,GIAC_CONTEXT);

  gen bernoulli(const gen & x);
  gen _bernoulli(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_bernoulli;

  // solve dy/dt=f(t,y) with initial value y(t0)=y0 to final value t1
  // returns by default y[t1] or a std::vector of [t,y[t]]
  // if return_curve is true stop as soon as y is outside ymin,ymax
  // f is eitheir a prog (t,y) -> f(t,y) or a comp [f(t,y) t y]
  gen odesolve(const gen & t0orig,const gen & t1orig,const gen & f,const gen & y0orig,double tstep,bool return_curve,double * ymin,double * ymax,int maxstep,GIAC_CONTEXT);
  gen _odesolve(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_odesolve;

  gen preval(const gen & f,const gen & x,const gen & a,const gen & b,GIAC_CONTEXT);
  gen _ibpdv(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ibpdv;  

  gen fourier_an(const gen & f,const gen & x,const gen & T,const gen & n,const gen & a,GIAC_CONTEXT);
  gen fourier_bn(const gen & f,const gen & x,const gen & T,const gen & n,const gen & a,GIAC_CONTEXT);
  gen fourier_cn(const gen & f,const gen & x,const gen & T,const gen & n,const gen & a,GIAC_CONTEXT);


  gen _fourier_an(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_fourier_an ;

  gen _fourier_bn(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_fourier_bn ;
   
  gen _fourier_cn(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_fourier_cn ;


#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC


#endif // _GIAC_INTG_H
