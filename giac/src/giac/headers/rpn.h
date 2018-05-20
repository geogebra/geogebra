// -*- mode:C++ ; compile-command: "g++ -I.. -g -c rpn.cc" -*-
/*
 *  Copyright (C) 2001,2014 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#ifndef _GIAC_RPN_H
#define _GIAC_RPN_H
#include "first.h"

#include "gen.h"
#include "vecteur.h"
#include <string>
#include <ctype.h>

#undef _ABS // for SunOS

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  std::string printasconstant(const gen & feuille,const char * sommetstr,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_INTERSECT;
  extern const unary_function_ptr * const  at_MINUS;
  extern const unary_function_ptr * const  at_UNION;
  extern const unary_function_ptr * const  at_rpn;
  gen _rpn(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_alg;
  gen _alg(const gen & args,GIAC_CONTEXT);
  std::string enmajuscule(const std::string & s);
  gen _PERCENT(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_PERCENT;
  void roll(int i,vecteur & v);
  void ROLL(int i,GIAC_CONTEXT);
  gen _ROLL(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ROLL;

  void rolld(int i,vecteur & v);
  void ROLLD(int i,GIAC_CONTEXT);
  gen _ROLLD(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ROLLD;

  void stack_swap(vecteur & v);
  void SWAP(GIAC_CONTEXT);
  gen _SWAP(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_SWAP;

  void dup(vecteur & v);
  gen _DUP(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_DUP;

  void over(vecteur & v);
  gen _OVER(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_OVER;

  void pick(int i,vecteur & v);
  gen _PICK(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_PICK;

  void drop(vecteur & v);
  gen _DROP(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_DROP;

  gen _NOP(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_NOP;

  gen _IFTE(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_IFTE;

  gen _RPN_LOCAL(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_RPN_LOCAL;

  gen _RPN_FOR(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_RPN_FOR;

  gen _RPN_WHILE(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_RPN_WHILE;

  gen _RPN_UNTIL(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_RPN_UNTIL;

  gen _RPN_CASE(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_RPN_CASE;

  gen _RCL(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_RCL;

  gen _VARS(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_VARS;

  gen purgenoassume(const gen & args,const context * contextptr);
  gen _purge(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_purge;

  gen _rpn_prog(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_rpn_prog;
  vecteur rpn_eval(const vecteur & prog,vecteur & pile,GIAC_CONTEXT);
  vecteur rpn_eval(const gen & prog,vecteur & pile,GIAC_CONTEXT);

  gen _division(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_division;

  gen _binary_minus(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_binary_minus;
  vecteur tab2vecteur(gen tab[]);

  extern const unary_function_ptr * const  at_SIN ;
  extern const unary_function_ptr * const  at_COS ;
  extern const unary_function_ptr * const  at_TAN ;
  extern const unary_function_ptr * const  at_EXP ;
  extern const unary_function_ptr * const  at_LN ;
  extern const unary_function_ptr * const  at_DELTALIST ;
  extern const unary_function_ptr * const  at_PILIST ;
  extern const unary_function_ptr * const  at_HPSUM ;
  extern const unary_function_ptr * const  at_SIGMALIST ;
  extern const unary_function_ptr * const  at_HPDIFF ;
  extern const unary_function_ptr * const  at_HPINT ;
  extern const unary_function_ptr * const  at_Fahrenheit2Celsius;
  extern const unary_function_ptr * const  at_Celsius2Fahrenheit;
  extern const unary_function_ptr * const  at_polar_complex;
  extern const unary_function_ptr * const  at_ggb_ang;
  extern const unary_function_ptr * const  at_HDigits;
  extern const unary_function_ptr * const  at_HFormat;
  extern const unary_function_ptr * const  at_HComplex;
  extern const unary_function_ptr * const  at_HAngle;
  extern const unary_function_ptr * const  at_HLanguage;
  // extern const unary_function_ptr * const  at_testfunc;
  extern const unary_function_ptr * const  at_FREEZE;
  extern const unary_function_ptr * const  at_LINE;
  extern const unary_function_ptr * const  at_RECT;
  extern const unary_function_ptr * const  at_TEXTOUT;
  extern const unary_function_ptr * const  at_EDITMAT;
  extern const unary_function_ptr * const  at_INT;

#ifdef GIAC_HAS_STO_38
  extern const unary_function_ptr * const  at_INVERT;
  extern const unary_function_ptr * const  at_BLIT;
  extern const unary_function_ptr * const  at_GETPIX;
  extern const unary_function_ptr * const  at_DIMGROB;
  extern const unary_function_ptr * const  at_SUBGROB;
  extern const unary_function_ptr * const  at_GETPIX_P;
  extern const unary_function_ptr * const  at_PIXON_P;
  extern const unary_function_ptr * const  at_PIXOFF_P;
  extern const unary_function_ptr * const  at_LINE_P;
  extern const unary_function_ptr * const  at_RECT_P;
  extern const unary_function_ptr * const  at_INVERT_P;
  extern const unary_function_ptr * const  at_BLIT_P;
  extern const unary_function_ptr * const  at_TEXTOUT_P;
  extern const unary_function_ptr * const  at_DIMGROB_P;
  extern const unary_function_ptr * const  at_ARC_P;
  extern const unary_function_ptr * const  at_SUBGROB_P;
  extern const unary_function_ptr * const  at_GROBH;
  extern const unary_function_ptr * const  at_GROBW;
  extern const unary_function_ptr * const  at_GROBH_P;
  extern const unary_function_ptr * const  at_GROBW_P;
  extern const unary_function_ptr * const  at_ISKEYDOWN;
  extern const unary_function_ptr * const  at_STARTAPP;
  extern const unary_function_ptr * const  at_STARTVIEW;
#endif

  gen _hp38(const gen & args,GIAC_CONTEXT);
  gen _ABS(const gen & args,GIAC_CONTEXT);
  gen _MODULO(const gen & args,GIAC_CONTEXT);
  gen _RANDOM(const gen & g,GIAC_CONTEXT);
  std::string printasRANDOM(const gen & feuille,const char * s,GIAC_CONTEXT);
  gen _MAXREAL(const gen & g,GIAC_CONTEXT);
  gen _MINREAL(const gen & g,GIAC_CONTEXT);
  gen _EXPM1(const gen & g,GIAC_CONTEXT);
  gen _LNP1(const gen & g,GIAC_CONTEXT);
  gen _ADDROW(const gen & args,GIAC_CONTEXT);
  gen _ADDCOL(const gen & args,GIAC_CONTEXT);
  gen _SCALE(const gen & g,GIAC_CONTEXT);
  gen _SCALEADD(const gen & g,GIAC_CONTEXT);
  gen _SWAPCOL(const gen & args,GIAC_CONTEXT);
  gen _SUB(const gen & args,GIAC_CONTEXT);
  gen _RANDMAT(const gen & args,GIAC_CONTEXT);
  gen _REDIM(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_redim;
  gen _REPLACE(const gen & args,GIAC_CONTEXT);
  gen _EIGENVAL(const gen & args,GIAC_CONTEXT);
  gen _EIGENVV(const gen & args,GIAC_CONTEXT);
  gen _SIZE(const gen& args,GIAC_CONTEXT);
  gen _INT(const gen & g,GIAC_CONTEXT);
  gen _HPDIFF(const gen & args,GIAC_CONTEXT);
  gen _HPINT(const gen & args,GIAC_CONTEXT);
  gen _HPSUM(const gen & args,GIAC_CONTEXT);
  gen _TAYLOR(const gen & args,GIAC_CONTEXT);
  gen _POLYFORM(const gen & args,GIAC_CONTEXT);
  gen _IS_LINEAR(const gen & args,GIAC_CONTEXT);
  gen _SVD(const gen & args0,GIAC_CONTEXT);
  gen _SVL(const gen & args0,GIAC_CONTEXT);
  gen _SPECRAD(const gen & args0,GIAC_CONTEXT);
  gen _SPECNORM(const gen & args0,GIAC_CONTEXT);
  gen _COND(const gen & args0,GIAC_CONTEXT);
  gen _rank(const gen & args,GIAC_CONTEXT);
  gen _SCHUR(const gen & args,GIAC_CONTEXT);
  gen _LQ(const gen & args0,GIAC_CONTEXT);
  gen _LU(const gen & args0,GIAC_CONTEXT);
  gen _QR(const gen & args0,GIAC_CONTEXT);
  gen _XPON(const gen & g,GIAC_CONTEXT);
  gen _MANT(const gen & g,GIAC_CONTEXT);
  gen _HMSX(const gen & g0,GIAC_CONTEXT);
  gen _XHMS(const gen & g0,GIAC_CONTEXT);
  gen _DEGXRAD(const gen & g,GIAC_CONTEXT);
  gen _RADXDEG(const gen & g,GIAC_CONTEXT);
  gen _PERCENT(const gen & g,GIAC_CONTEXT);
  gen _PERCENTCHANGE(const gen & g,GIAC_CONTEXT);
  gen _PERCENTTOTAL(const gen & g,GIAC_CONTEXT);
  gen _ITERATE(const gen & args,GIAC_CONTEXT);
//  gen _RECURSE(const gen & args,GIAC_CONTEXT);
  gen _MAKEMAT(const gen & args,GIAC_CONTEXT);
  gen _LSQ(const gen & args,GIAC_CONTEXT);
  gen _idivis(const gen & args0,GIAC_CONTEXT);
  gen _isprime(const gen & args0,GIAC_CONTEXT);
  gen _ithprime(const gen & args0,GIAC_CONTEXT);
  gen _euler(const gen & args0,GIAC_CONTEXT);
  gen _numer(const gen & args0,GIAC_CONTEXT);
  gen _denom(const gen & args0,GIAC_CONTEXT);
  gen _ifactors(const gen & args0,GIAC_CONTEXT);
  gen _binomial_icdf(const gen & args0,GIAC_CONTEXT);
  gen _poisson_icdf(const gen & args0,GIAC_CONTEXT);

  gen symb_RPN_LOCAL(const gen & a,const gen & b);
  gen symb_RPN_FOR(const gen & a,const gen & b);
  gen symb_RPN_WHILE(const gen & a,const gen & b);
  gen symb_RPN_CASE(const gen & a);
  gen symb_RPN_UNTIL(const gen & a,const gen & b);
  gen symb_IFTE(const gen & args);
  gen symb_NOP(const gen & args);
  gen symb_rpn_prog(const gen & args);
  gen _NTHROOT(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_NTHROOT;
  extern gen * rpn_ans() ;
  gen _Ans(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Ans;
  bool is_Ans(const gen & g);
  gen _EXPORT(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_EXPORT;
  gen _VIEWS(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_VIEWS;
  extern const unary_function_ptr * const  at_POLYFORM;
  extern const unary_function_ptr * const  at_colSwap;
  extern const unary_function_ptr * const  at_replace;
  extern const unary_function_ptr * const  at_scale;
  extern const unary_function_ptr * const  at_scaleadd;
  extern const unary_function_ptr * const  at_schur;
  extern const unary_function_ptr * const  at_svl;
  extern const unary_function_ptr * const  at_swapcol;
  extern const unary_function_ptr * const  at_swaprow;
  extern const unary_function_ptr * const  at_mantissa;
  extern const unary_function_ptr * const  at_ldexp;
  extern const unary_function_ptr * const  at_frexp;

  int is_known_name_home_38(const char * idname);
  // 1 and 2 app or program variable, 3 home variable
  int is_known_name_home_38(const char * name_space,const char * idname);
  // add progname qualifier to variables in v and replace in g
  void qualify(gen & g,const vecteur & v,const gen & prog,GIAC_CONTEXT);
  // parse_program description: result is parsed, should be evaled
  // assignation_by_equal: warning for probable misuse of = instead of :=
  // undeclared_global_vars: regular 38 global variables used but not declared
  // declared_global_vars: regular 38 global variables that were declared
  // exported_function_names: list of exported function 
  //                          may contain description strings 
  // exported_variable_names: same as above but variables
  // unknown_exported: function or variable exported but not used
  // unexported: function local to source, re-qualified with progname namespace
  // unexported_declared_global_vars: non regular 38 global variables,
  //                                  re-qualified with progname namespace
  // views: is a matrix: column1 is the function name (no param) or integer, 
  //                     column2 is a comment string
  // errors: exported names that are regular 38 home variables,
  //         and undeclared global variables that are not regular 38 variables
  // return value is 0: ok, -1: invalid VIEWS, >0: #errors
  // int parse_program(const wchar_t * source,const wchar_t * progname,vecteur & assignation_by_equal,vecteur & undeclared_global_vars,vecteur & declared_global_vars,vecteur & exported_function_names,vecteur & exported_variable_names,vecteur & unknown_exported,vecteur & unexported,vecteur & unexported_declared_global_vars,vecteur & views,vecteur & errors,gen & parsed,GIAC_CONTEXT);

  // Prepares app sequence for computing a recurrence relation
  // Valid if 1 sequence is checked and does not depend on other sequences
  // Given expr_un, the expression of UK(N) in terms of UK(N-1) and UK(N-2)
  // write the recurrence relation as UK(N)=subst(expr,vars,[N,UK(N-1),UK(N-2)])
  // Return 0 if expr_un is invalid, 1 if it does not depend on UK(N-2)
  // 2 otherwise
  int seqapp_prepare(const gen & expr_un,gen & expr,vecteur & vars,GIAC_CONTEXT,int seqno=-1);
  // Compute UK(N) for K=k to m, where UK(k) and UK(k+1) are given
  // If the recurrence relation does not depend on UK(N-2), set UK(k+1) to undef
  vecteur seqapp_compute(const gen & expr,const vecteur & vars,const gen & UK_k,const gen &UK_kp1,int k,int m,GIAC_CONTEXT);

  // Prepares app sequence for computing all recurrences relations
  // expr_un should contain the expression for U0(N) to UK(N) for K<=9
  // undef may be used if the sequence is not checked
  // Rewrite the recurrence relation as [U0(N),...,UK(N)]=subst(expr,vars,[N,U0(N-1),U0(N-2),...])
  // Return 0 if expr_un is invalid, -10-val if Uval should be checked
  // Return 1 if it does not depend on UK(N-2), 2 otherwise
  int seqapp_prepare(const vecteur & expr_un,vecteur & expr,vecteur & vars,GIAC_CONTEXT);
  // Compute UK(N) for K=k to m, where UK(k) and UK(k+1) are given
  // If the recurrence relation does not depend on UK(N-2), set UK_kp1 to vecteur(0)
  vecteur seqapp_compute(const vecteur & expr,const vecteur & vars,const vecteur & UK_k,const vecteur &UK_kp1,int k,int m,GIAC_CONTEXT);

  // check if a lowercase commandname should be uppercased
  // returns 0 if not, returns a statically pointer valid up to next call if so
  char * hp38_display_in_maj(const char * s);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_RPN_H
