// -*- mode:C++ ; compile-command: "g++ -I.. -g -c usual.cc" -*-
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
#ifndef _GIAC_USUAL_H
#define _GIAC_USUAL_H
#include "first.h"
#include <string>

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  bool check_vect_38(const std::string & s); // check if s is a string identifier of a vector, matrix or list

  // Global vectors of tractable functions and equivalents
  std::vector<const unary_function_ptr *> & limit_tractable_functions();
  typedef gen ( * gen_op ) (const gen & arg);
  std::vector<gen_op_context> & limit_tractable_replace();

#ifdef HAVE_SIGNAL_H_OLD
  extern std::string messages_to_print ;
#endif


  // declare here pointers to rational operators 
  // These are global variables but that's normal for functional obj

  class gen;
  struct unary_function_ptr;
  class unary_function_eval;

  gen frac_neg_out(const gen & g,GIAC_CONTEXT);
  gen _constant_one(const gen & args,GIAC_CONTEXT);
  gen _constant_zero(const gen & args,GIAC_CONTEXT);
  gen _rm_a_z(const gen & args,GIAC_CONTEXT);
  gen _rm_all_vars(const gen & args,const context * contextptr);
  bool is_equal(const gen & g);
  gen apply_to_equal(const gen & g,const gen_op & f);
  gen apply_to_equal(const gen & g,gen (* f) (const gen &, GIAC_CONTEXT),GIAC_CONTEXT);
  gen apply_unit(const gen & args,const gen_op_context & f,GIAC_CONTEXT);
  gen _id(const gen & args,GIAC_CONTEXT);
  gen _not(const gen & args,GIAC_CONTEXT);
  gen _neg(const gen & args,GIAC_CONTEXT);
  gen _inv(const gen & args,GIAC_CONTEXT);
  gen ln(const gen & e,GIAC_CONTEXT);
  gen log(const gen & e,GIAC_CONTEXT);
  gen log10(const gen & e,GIAC_CONTEXT);
  gen alog10(const gen & e,GIAC_CONTEXT);
  gen atan(const gen & e0,GIAC_CONTEXT);
  gen exp(const gen & e0,GIAC_CONTEXT);

  // convert a gen to a string, format=0 (normal), 1 (tex)
  std::string gen2string(const gen & g,int format,GIAC_CONTEXT);

  // Demodularize
  gen unmod(const gen & g);
  gen unmodunprod(const gen & g);

  gen double2gen(double d);
  bool is_equal(const gen & g);
  gen apply_to_equal(const gen & g,const gen_op & f);
  gen apply_to_equal(const gen & g,gen (* f) (const gen &, GIAC_CONTEXT),GIAC_CONTEXT);

  std::string print_with_parenthesis_if_required(const gen & g,int format,GIAC_CONTEXT);

  // usual zero args
  extern const unary_function_ptr * const  at_zero;
  extern const unary_function_ptr * const  at_one;
  extern const unary_function_ptr * const  at_id;
  extern const unary_function_ptr * const  at_rm_a_z;
  extern const unary_function_ptr * const  at_rm_all_vars;

  // usual unary function related declarations (extended unary)
  extern const unary_function_ptr * const  at_neg ;
  extern const unary_function_ptr * const  at_inv ;
  extern const unary_function_ptr * const  at_not;
  extern const unary_function_ptr * const  at_exp ;
  extern const unary_function_ptr * const  at_ln ;
  extern const unary_function_ptr * const  at_log10 ;
  extern const unary_function_ptr * const  at_alog10 ;
  extern const unary_function_ptr * const  at_atan ;

  // e = +/- simpl*doubl^d
  void zint2simpldoublpos(const gen & e,gen & simpl,gen & doubl,bool & pos,int d,GIAC_CONTEXT);
  gen sqrt_noabs(const gen & e,GIAC_CONTEXT);
  gen sqrt(const gen & e,GIAC_CONTEXT);
  gen sqrt_mod(const gen & a,const gen & b,bool isprime,GIAC_CONTEXT); // set isprime to true if b is prime
  extern const unary_function_ptr * const  at_sqrt ;

  gen _sq(const gen & e);
  extern const unary_function_ptr * const  at_sq ;

  gen sin(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_sin ;

  gen cos(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_cos ;

  gen tan(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_tan ;

  gen asin(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_asin ;

  gen acos(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_acos ;

  gen sinh(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_sinh ;

  gen cosh(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_cosh ;

  gen tanh(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_tanh ;

  gen asinh(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_asinh ;

  gen acosh(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_acosh ;

  gen atanh(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_atanh ;

  symbolic symb_inv(const gen & e);
  symbolic symb_exp(const gen & e);
  symbolic symb_ln(const gen & e);
  symbolic symb_cos(const gen & e);
  symbolic symb_sin(const gen & e);
  symbolic symb_cosh(const gen & e);
  symbolic symb_sinh(const gen & e);
  symbolic symb_tan(const gen & e);
  symbolic symb_atan(const gen & e);
  symbolic symb_not(const gen & e);
  symbolic symb_asin(const gen & e);

  symbolic symb_quote(const gen & arg);
  gen quote(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_quote ;

  gen unquote(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_unquote ;
  gen re(const gen & a,GIAC_CONTEXT);
  gen im(const gen & a,GIAC_CONTEXT);
  gen conj(const gen & a,GIAC_CONTEXT);
  gen _sign(const gen & g,GIAC_CONTEXT);

  gen order_size(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_order_size ;

  symbolic symb_and(const gen & a,const gen & b);
  gen _and(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_and;
  gen and2(const gen & a,const gen & b);

  symbolic symb_ou(const gen & a,const gen & b);
  gen _ou(const gen & args,GIAC_CONTEXT);
  gen ou2(const gen & a,const gen & b);
  extern const unary_function_ptr * const  at_ou;

  gen _xor(const gen & args,GIAC_CONTEXT);
  gen xor2(const gen & a,const gen & b,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_xor;

  symbolic symb_min(const gen & a,const gen & b);
  gen _min(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_min;

  symbolic symb_max(const gen & a,const gen & b);
  gen _max(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_max;

  gen _gcd(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_gcd;

  gen _lcm(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_lcm;

  gen _egcd(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_egcd;

  gen _iegcd(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_iegcd;

  // check that indice is integral, if required convert float/double to int
  bool is_integral(gen & indice);
  gen Iquo(const gen & a,const gen & b);
  gen _iquo(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_iquo;

  gen _irem(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_irem;

  gen _mods(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_mods;

  gen _quote_pow(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_quote_pow;

  gen _iquorem(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_iquorem;

  gen _smod(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_smod;

  gen _rdiv(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_rdiv;

  gen _is_prime(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_is_prime;
  gen _is_pseudoprime(const gen & args,GIAC_CONTEXT);
  gen nextprime1(const gen & a,GIAC_CONTEXT);

  gen prevprime1(const gen & a,GIAC_CONTEXT);
  gen _nextprime(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_nextprime;

  gen _prevprime(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_prevprime;
  gen _jacobi_symbol(const gen & args,GIAC_CONTEXT);
  gen _legendre_symbol(const gen & args,GIAC_CONTEXT);

  gen _floor(const gen & args,GIAC_CONTEXT);  
  extern const unary_function_ptr * const  at_floor;

  gen _ceil(const gen & args,GIAC_CONTEXT);  
  extern const unary_function_ptr * const  at_ceil;
  gen ceil2floor(const gen & g,GIAC_CONTEXT);

  gen _round(const gen & args,GIAC_CONTEXT);  
  extern const unary_function_ptr * const  at_round;

  gen _print(const gen & args,GIAC_CONTEXT);  
  extern const unary_function_ptr * const  at_print;
#ifndef RTOS_THREADX
  extern const unary_function_eval __print;
#endif

  extern const unary_function_ptr * const  at_jacobi_symbol;

  extern const unary_function_ptr * const  at_legendre_symbol;

  gen ichinrem2(const gen  & a_orig,const gen & b_orig);
  gen _ichinrem(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ichinrem;
  gen double_is_int(const gen & g,GIAC_CONTEXT);

  gen _fracmod(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_fracmod;

  gen _factorial(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_factorial;

  gen _perm(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_perm;

  gen comb(const gen & n,const gen &k,GIAC_CONTEXT);
  gen _comb(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_comb;

  gen _chinrem(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_chinrem;

  extern const unary_function_ptr * const  at_re;

  extern const unary_function_ptr * const  at_im;

  symbolic symb_conj(const gen & e);
  extern const unary_function_ptr * const  at_conj ;

  gen _abs(const gen & args,GIAC_CONTEXT);
  symbolic symb_abs(const gen & e);
  extern const unary_function_ptr * const  at_abs ;

  symbolic symb_arg(const gen & e);
  extern const unary_function_ptr * const  at_arg ;

  extern const unary_function_ptr * const  at_sign;

  gen _cyclotomic(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_cyclotomic ;
  gen _calc_mode(const gen & args,GIAC_CONTEXT);

  extern const unary_function_ptr * const  at_quo ;

  extern const unary_function_ptr * const  at_rem ;

  gen _quorem(const gen & args,GIAC_CONTEXT);
  gen quorem(const gen & a,const gen & b);
  gen _quo(const gen & args,GIAC_CONTEXT);
  gen _rem(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_quorem ;
  extern const unary_function_ptr * const  at_normalmod;

  symbolic symb_sto(const gen & a,gen & b,bool in_place=false);
  symbolic symb_sto(const gen & e);
  extern const unary_function_ptr * const  at_sto ;
  extern const unary_function_ptr * const  at_array_sto ;
  extern const unary_function_ptr * const  at_increment;
  extern const unary_function_ptr * const  at_decrement;
  extern const unary_function_ptr * const  at_multcrement;
  extern const unary_function_ptr * const  at_divcrement;
  gen sto(const gen & a,const gen & b,GIAC_CONTEXT);
  gen sto(const gen & a,const gen & b,bool in_place,GIAC_CONTEXT);  
  gen _sto(const gen & g,const context * contextptr);
  gen _array_sto(const gen & a,const context * contextptr);

  bool is_assumed_real(const gen & g,GIAC_CONTEXT);
  bool is_assumed_integer(const gen & g,GIAC_CONTEXT);
  bool is_numericv(const vecteur & v, int withfracint = 0);
  bool is_numericm(const vecteur & v, int withfracint = 0);
  bool check_vect_38(const std::string & s);
  // check value type for storing value in s using 38 compatibility mode
  bool check_sto_38(gen & value,const char * s);
  extern int (*sto_38)(const gen & value,const char * name_space,const char * idname,gen indice,gen & error,GIAC_CONTEXT);
  extern int (*rcl_38)(gen & value,const char * name_space,const char * idname,gen indice,bool at_of,GIAC_CONTEXT);
  extern int (*is_known_name_38)(const char * name_space,const char * idname);
  extern gen (*of_pointer_38)(const void * appptr,const void * varptr,const gen & args);
  extern gen_op_context * interactive_op_tab;
  // if this pointer is non NULL, it should point to a table of gen_op_context replacing
  // { _input,_inputform,_interactive,_click,_getKey,
  //   _current_sheet,_Row,_Col,_xyztrange,_widget_size}
  // leave an individual pointer to 0 if you don't want to redefine it's action

  gen _increment(const gen & a,const context * contextptr);
  gen _decrement(const gen & a,const context * contextptr);
  gen _multcrement(const gen & a,const context * contextptr);
  gen _divcrement(const gen & a,const context * contextptr);

  // assume format _VECT of subtype _ASSUME__VECT
  // [ _DOUBLE_ , list of intervals, excluded ]
  // [ _INT_, ]
  extern const unary_function_ptr * const  at_assume ;
  gen giac_assume(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_additionally ;
  gen giac_additionally(const gen & a,GIAC_CONTEXT);
  // returns the assumed idnt name
  // used if assumptions are in OR conjonction
  gen assumesymbolic(const gen & a,gen idnt_must_be,GIAC_CONTEXT);
  // v = previous assumptions, a=the real value, direction
  // is positive for [a,+inf[, negative for ]-inf,a]
  // |direction| = 1 (large) or 2 (strict) 
  gen doubleassume_and(const vecteur & v,const gen & a,int direction,bool or_assumption,GIAC_CONTEXT);

  gen _equal(const gen & args,GIAC_CONTEXT);
  gen symb_equal(const gen & a,const gen & b);
  extern const unary_function_ptr * const  at_equal;
  gen symb_same(const gen & a);
  symbolic symb_same(const gen & a,const gen & b);
  gen _same(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_same;

  symbolic symb_inferieur_strict(const gen & a);
  symbolic symb_inferieur_strict(const gen & a,const gen & b);
  gen _inferieur_strict(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_inferieur_strict;

  symbolic symb_inferieur_egal(const gen & a);
  symbolic symb_inferieur_egal(const gen & a,const gen & b);
  gen _inferieur_egal(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_inferieur_egal;

  symbolic symb_superieur_strict(const gen & a);
  symbolic symb_superieur_strict(const gen & a,const gen & b);
  gen _superieur_strict(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_superieur_strict;

  symbolic symb_superieur_egal(const gen & a);
  symbolic symb_superieur_egal(const gen & a,const gen & b);
  gen _superieur_egal(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_superieur_egal;

  int is_inequality(const gen & g);

  gen _different(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_different;

  symbolic symb_of(const gen & a);
  symbolic symb_of(const gen & a,const gen & b);
  gen check_symb_of(const gen & a,const gen & b,GIAC_CONTEXT);
  gen _of(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_of;

  symbolic symb_at(const gen & a);
  symbolic symb_at(const gen & a,const gen & b,GIAC_CONTEXT);
  gen _at(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_at;
  
  gen _table(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_table;
  
  // usual multiargs
  // for multiargs we use _name for the corresponding "unary" function
  // to avoid confusion of pointers since name is used
  symbolic symb_plus(const gen & a,const gen & b);
  gen _plus(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_plus ;

  gen symb_prod(const gen & a,const gen & b);
  gen _prod(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_prod ;

  symbolic symb_pow(const gen & a,const gen & b);
  std::string cprintaspow(const gen & feuille,const char * sommetstr_orig,GIAC_CONTEXT);
#ifndef GIAC_HAS_STO_38
  extern unary_function_eval __pow;
#endif
  gen _pow(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_pow ;
  extern const char _pow_s[];

  gen _powmod(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_powmod ;

  symbolic symb_tran(const gen & a);
  symbolic symb_trace(const gen & a);
  symbolic symb_rref(const gen & a);
  symbolic symb_idn(const gen & e);
  symbolic symb_ranm(const gen & e);
  symbolic symb_det(const gen & a);
  symbolic symb_pcar(const gen & a);
  symbolic symb_ker(const gen & a);
  symbolic symb_image(const gen & a);
  symbolic symb_moyal(const gen & a,const gen & b, const gen &vars,const gen & order);

  extern const unary_function_ptr * const  at_evalf;
  gen _eval(const gen & a,GIAC_CONTEXT);
  gen _evalf(const gen &,GIAC_CONTEXT);

  extern const unary_function_ptr * const  at_eval;
  extern const unary_function_ptr * const  at_evalm;
  symbolic symb_eval(const gen & a);
  symbolic symb_evalf(const gen & a);
  
  symbolic symb_subst(const gen & e);
  gen _subst(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_subst;
  gen _subs(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_subs;

  gen _maple_subs(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_maple_subs;
  gen _ampersand_times(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ampersand_times;

  extern const unary_function_ptr * const  at_version;
  std::string version();
  gen _version(const gen & a,GIAC_CONTEXT);
  
  gen Gamma(const gen & x,GIAC_CONTEXT);
  gen _Gamma(const gen & args,GIAC_CONTEXT);
  double lngamma(double x);
  gen lngamma(const gen & x,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Gamma;
  extern const unary_function_ptr * const  at_lnGamma_minus;
  extern const unary_function_ptr * const  at_erfs;
  extern const unary_function_ptr * const  at_SiCi_f ;
  extern const unary_function_ptr * const  at_SiCi_g ;
  extern const unary_function_ptr * const  at_Si;
  extern const unary_function_ptr * const  at_Ei ;
  extern const unary_function_ptr * const  at_Ei_f ;

  gen Psi(const gen & x,GIAC_CONTEXT);
  gen Psi(const gen & x,int n,GIAC_CONTEXT);
  gen _Psi(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Psi;

  gen Zeta(const gen & x,GIAC_CONTEXT);
  gen _Zeta(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Zeta;
  extern const unary_function_ptr * const  at_Eta;

  gen _erf(const gen & args,GIAC_CONTEXT);
  gen erf(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_erf ;

  gen _erfc(const gen & args,GIAC_CONTEXT);
  gen erfc(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_erfc ;

  extern const unary_function_ptr * const  at_Si;
  extern const unary_function_ptr * const  at_Ci;
  extern const unary_function_ptr * const  at_Ci0;
  gen _Ci(const gen & g,GIAC_CONTEXT);
  gen _Si(const gen & g,GIAC_CONTEXT);
  gen _Ei(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Ei;
  extern const unary_function_ptr * const  at_Ei0;
  extern const unary_function_ptr * const  at_Heaviside ;
  extern const unary_function_ptr * const  at_Dirac ;
  gen _Heaviside(const gen & args,GIAC_CONTEXT);
  gen _Dirac(const gen & args,GIAC_CONTEXT);

#if defined(GIAC_GENERIC_CONSTANTS) // || (defined(VISUALC) && !defined(RTOS_THREADX)) || defined(__x86_64__)
  extern const gen zero;
  extern const gen plus_one;
  extern const gen minus_one;
  extern const gen plus_two;
  extern const gen plus_three;
  extern const gen cst_i;
#else
  extern const alias_gen alias_zero;
  extern const gen & zero;
  extern const alias_gen alias_plus_one;
  extern const gen & plus_one;
  extern const alias_gen alias_plus_two;
  extern const gen & plus_two;
  extern const alias_gen alias_plus_three;
  extern const gen & plus_three;
  extern const alias_gen alias_minus_one;
  extern const gen & minus_one;
  extern const int cst_i_refcount;
  extern const int cst_i_display;
  extern const alias_gen alias_cst_i;
  extern const gen & cst_i;
#endif
  extern const vecteur null_vetor;
  extern const double rad2deg_d;
  extern const double deg2rad_d;
  extern const gen & rad2deg_g;
  extern const gen & deg2rad_g;

#if defined(GIAC_GENERIC_CONSTANTS) // || (defined(VISUALC) && !defined(RTOS_THREADX)) || defined(__x86_64__)
  extern gen cst_two_pi;
  extern gen cst_pi_over_2;
  extern gen plus_inf;
  extern gen minus_inf;
  extern gen plus_one_half;
  extern gen minus_one_half;
  extern gen plus_sqrt3;
  extern gen plus_sqrt2;
  extern gen plus_sqrt6;
  extern gen minus_sqrt3;
  extern gen minus_sqrt2;
  extern gen minus_sqrt6;
  extern gen minus_sqrt3_2;
  extern gen minus_sqrt2_2;
  extern gen minus_sqrt3_3;
  extern gen plus_sqrt2_2;
  extern gen plus_sqrt3_2;
  extern gen plus_sqrt3_3;
  extern gen cos_pi_12;
  extern gen sin_pi_12;
  extern gen minus_cos_pi_12;
  extern gen minus_sin_pi_12;
  extern gen tan_pi_12;
  extern gen minus_tan_pi_12;
  extern gen tan_5pi_12;
  extern gen minus_tan_5pi_12;
  extern gen rad2deg_e;
  extern gen deg2rad_e;
  extern const gen * const table_cos[];
  extern const gen * const table_tan[];
#else
  extern const alias_gen alias_cst_two_pi_tab[];

  extern const alias_ref_vecteur cst_two_pi_refv;
  extern const alias_ref_symbolic cst_two_pi_symb;
  extern const gen & cst_two_pi;

  extern const alias_gen alias_cst_pi_over_2_tab[];
  extern const gen & cst_pi_over_2;
  

  extern const alias_ref_symbolic inv_2_symb;
  extern const alias_gen alias_inv_2;

  extern const alias_ref_symbolic inv_3_symb;
  extern const alias_gen alias_inv_3;

  extern const alias_ref_symbolic inv_4_symb;
  extern const alias_gen alias_inv_4;

  extern const alias_gen alias_cst_pi_over_2_tab[];
  extern const alias_ref_vecteur cst_pi_over_2_refv;
  extern const alias_ref_symbolic cst_pi_over_2_symb ;
  extern const alias_gen alias_cst_pi_over_2;
  extern const gen & cst_pi_over_2 ;

  extern const alias_ref_symbolic plus_inf_symb ;
  extern const alias_gen alias_plus_inf;
  extern const gen & plus_inf ;
  extern const alias_ref_symbolic minus_inf_symb ;
  extern const alias_gen alias_minus_inf;
  extern const gen & minus_inf ;

  extern const alias_ref_fraction plus_one_half_ref;
  extern const alias_gen alias_plus_one_half1;
  extern const alias_gen alias_plus_one_half2;
  
  // extern const alias_ref_fraction plus_one_half_ref;
  extern const alias_gen alias_plus_one_half;
  extern const gen & plus_one_half ;
  extern const alias_ref_symbolic minus_one_half_symb ;
  extern const alias_gen alias_minus_one_half;
  extern const gen & minus_one_half ;
  
  extern const alias_gen alias_plus_sqrt3_tab[];
  extern const alias_ref_vecteur plus_sqrt3_refv;
  extern const alias_ref_symbolic plus_sqrt3_symb ;
  extern const alias_gen alias_plus_sqrt3;
  extern const gen & plus_sqrt3 ;

  extern const alias_gen alias_plus_sqrt2_tab[];
  extern const alias_ref_vecteur plus_sqrt2_refv;
  extern const alias_ref_symbolic plus_sqrt2_symb ;
  extern const alias_gen alias_plus_sqrt2;
  extern const gen & plus_sqrt2 ;

  extern const alias_gen alias_plus_sqrt6_tab[];
  extern const alias_ref_vecteur plus_sqrt6_refv;
  extern const alias_ref_symbolic plus_sqrt6_symb ;
  extern const alias_gen alias_plus_sqrt6;
  extern const gen & plus_sqrt6 ;

  extern const alias_ref_symbolic minus_sqrt2_symb ;
  extern const alias_gen alias_minus_sqrt2;
  extern const gen & minus_sqrt2 ;

  extern const alias_ref_symbolic minus_sqrt3_symb ;
  extern const alias_gen alias_minus_sqrt3;
  extern const gen & minus_sqrt3 ;

  extern const alias_ref_symbolic minus_sqrt6_symb ;
  extern const alias_gen alias_minus_sqrt6;
  extern const gen & minus_sqrt6 ;

  extern const alias_gen alias_minus_sqrt3_2_tab[];
  extern const alias_ref_vecteur minus_sqrt3_2_refv;
  extern const alias_ref_symbolic minus_sqrt3_2_symb ;
  extern const alias_gen alias_minus_sqrt3_2;
  extern const gen & minus_sqrt3_2 ;

  extern const alias_gen alias_minus_sqrt2_2_tab[];
  extern const alias_ref_vecteur minus_sqrt2_2_refv;
  extern const alias_ref_symbolic minus_sqrt2_2_symb ;
  extern const alias_gen alias_minus_sqrt2_2;
  extern const gen & minus_sqrt2_2 ;

  extern const alias_gen alias_minus_sqrt3_3_tab[];
  extern const alias_ref_vecteur minus_sqrt3_3_refv;
  extern const alias_ref_symbolic minus_sqrt3_3_symb ;
  extern const alias_gen alias_minus_sqrt3_3;
  extern const gen & minus_sqrt3_3 ;

  extern const alias_gen alias_plus_sqrt3_2_tab[];
  extern const alias_ref_vecteur plus_sqrt3_2_refv;
  extern const alias_ref_symbolic plus_sqrt3_2_symb ;
  extern const alias_gen alias_plus_sqrt3_2;
  extern const gen & plus_sqrt3_2 ;

  extern const alias_gen alias_plus_sqrt2_2_tab[];
  extern const alias_ref_vecteur plus_sqrt2_2_refv;
  extern const alias_ref_symbolic plus_sqrt2_2_symb ;
  extern const alias_gen alias_plus_sqrt2_2;
  extern const gen & plus_sqrt2_2 ;

  extern const alias_gen alias_plus_sqrt3_3_tab[];
  extern const alias_ref_vecteur plus_sqrt3_3_refv;
  extern const alias_ref_symbolic plus_sqrt3_3_symb ;
  extern const alias_gen alias_plus_sqrt3_3;
  extern const gen & plus_sqrt3_3 ;

  extern const alias_gen alias_cos_pi_12_4_tab[];
  extern const alias_ref_vecteur cos_pi_12_4_refv;
  extern const alias_ref_symbolic cos_pi_12_4_symb ;

  extern const alias_gen alias_cos_pi_12_4;
  extern const alias_gen alias_cos_pi_12_tab1;
  extern const alias_ref_vecteur cos_pi_12_refv;
  extern const alias_ref_symbolic cos_pi_12_symb ;
  extern const alias_gen alias_cos_pi_12;
  extern const gen & cos_pi_12 ;

  extern const alias_gen alias_minus_cos_pi_12_4_tab[];
  extern const alias_ref_vecteur minus_cos_pi_12_4_refv;
  extern const alias_ref_symbolic minus_cos_pi_12_4_symb ;
  extern const alias_gen alias_minus_cos_pi_12_4;
  extern const alias_gen alias_minus_cos_pi_12_tab1;
  extern const alias_ref_vecteur minus_cos_pi_12_refv;
  extern const alias_ref_symbolic minus_cos_pi_12_symb ;
  extern const alias_gen alias_minus_cos_pi_12;
  extern const gen & minus_cos_pi_12 ;

  extern const alias_gen alias_sin_pi_12_4_tab[];
  extern const alias_ref_vecteur sin_pi_12_4_refv;
  extern const alias_ref_symbolic sin_pi_12_4_symb ;
  extern const alias_gen alias_sin_pi_12_4;
  extern const alias_gen alias_sin_pi_12_tab1;
  extern const alias_ref_vecteur sin_pi_12_refv;
  extern const alias_ref_symbolic sin_pi_12_symb ;
  extern const alias_gen alias_sin_pi_12;
  extern const gen & sin_pi_12 ;

  extern const alias_gen alias_minus_sin_pi_12_4_tab[];
  extern const alias_ref_vecteur minus_sin_pi_12_4_refv;
  extern const alias_ref_symbolic minus_sin_pi_12_4_symb ;
  extern const alias_gen alias_minus_sin_pi_12_4;
  extern const alias_gen alias_minus_sin_pi_12_tab1;
  extern const alias_ref_vecteur minus_sin_pi_12_refv;
  extern const alias_ref_symbolic minus_sin_pi_12_symb ;
  extern const alias_gen alias_minus_sin_pi_12;
  extern const gen & minus_sin_pi_12 ;

  extern const alias_gen alias_tan_pi_12_tab[];
  extern const alias_ref_vecteur tan_pi_12_refv;
  extern const alias_ref_symbolic tan_pi_12_symb ;
  extern const alias_gen alias_tan_pi_12;
  extern const gen & tan_pi_12 ;

  extern const alias_gen alias_tan_5pi_12_tab[];
  extern const alias_ref_vecteur tan_5pi_12_refv;
  extern const alias_ref_symbolic tan_5pi_12_symb ;
  extern const alias_gen alias_tan_5pi_12;
  extern const gen & tan_5pi_12 ;

  extern const alias_ref_symbolic minus_tan_pi_12_symb ;
  extern const alias_gen alias_minus_tan_pi_12;
  extern const gen & minus_tan_pi_12 ;

  extern const alias_ref_symbolic minus_tan_5pi_12_symb ;
  extern const alias_gen alias_minus_tan_5pi_12;
  extern const gen & minus_tan_5pi_12 ;

  extern const alias_ref_symbolic inv_pi_symb;
  extern const alias_gen alias_inv_pi;
  extern const gen & cst_inv_pi;

  extern const alias_ref_symbolic inv_180_symb;
  extern const alias_gen alias_inv_180;
  extern const gen & cst_inv_180;

  extern const alias_gen alias_rad2deg_e_tab[];
  extern const alias_ref_vecteur rad2deg_e_refv;
  extern const alias_ref_symbolic rad2deg_e_symb ;
  extern const alias_gen alias_rad2deg_e;
  extern const gen & rad2deg_e ;

  extern const alias_gen alias_deg2rad_e_tab[];
  extern const alias_ref_vecteur deg2rad_e_refv;
  extern const alias_ref_symbolic deg2rad_e_symb ;
  extern const alias_gen alias_deg2rad_e;
  extern const gen & deg2rad_e ;


  extern const gen * const * table_cos;
  extern const gen * const * table_tan;
#endif
  // for subst.cc
  // extern std::vector<unary_function_ptr> sincostan_v;
  // extern std::vector<unary_function_ptr> asinacosatan_v,sinhcoshtanh_v,sincostansinhcoshtanh_v;
  extern const unary_function_ptr * const  exp_tab;
  extern const unary_function_ptr * const  tan_tab;
  extern const unary_function_ptr * const  asin_tab;
  extern const unary_function_ptr * const  acos_tab;
  extern const unary_function_ptr * const  atan_tab;
  extern const unary_function_ptr * const  pow_tab;
  extern const unary_function_ptr * const  Heaviside_tab;
  extern const unary_function_ptr * const  invpowtan_tab;

  extern const gen_op_context halftan_tab[];
  extern const gen_op_context hyp2exp_tab[];
  extern const gen_op_context hypinv2exp_tab[];
  extern const gen_op_context trig2exp_tab[];
  extern const gen_op_context atrig2ln_tab[];
  // extern std::vector< gen_op_context > halftan_v;
  // extern std::vector< gen_op_context > hyp2exp_v;
  // extern std::vector< gen_op_context > trig2exp_v;
  extern const gen_op_context halftan_hyp2exp_tab[];
  extern const gen_op_context exp2sincos_tab[];
  extern const gen_op_context tan2sincos_tab[];
  extern const gen_op_context tan2sincos2_tab[];
  extern const gen_op_context tan2cossin2_tab[];
  extern const gen_op_context asin2acos_tab[];
  extern const gen_op_context asin2atan_tab[];
  extern const gen_op_context acos2asin_tab[];
  extern const gen_op_context acos2atan_tab[];
  extern const gen_op_context atan2asin_tab[];
  extern const gen_op_context atan2acos_tab[];
  extern const gen_op_context atrig2ln_tab[];
  extern const gen_op_context trigcos_tab[];
  extern const gen_op_context trigsin_tab[];
  extern const gen_op_context trigtan_tab[];
  extern const gen_op_context powexpand_tab[];
  extern const gen_op_context exp2power_tab[];
  extern const unary_function_ptr * const  gamma_tab;
  extern const gen_op_context gamma2factorial_tab[];
  extern const unary_function_ptr * const   factorial_tab;
  extern const gen_op_context factorial2gamma_tab[];
  // extern std::vector<unary_function_ptr> sign_floor_ceil_round_tab;
  extern const unary_function_ptr * const  analytic_sommets;
  extern const unary_function_ptr * const  primitive_tab_op;
  extern const unary_function_ptr * const  inverse_tab_op;
  extern const unary_function_ptr * const  inequality_tab;
  extern const unary_function_ptr * const  solve_fcns_tab;
  extern const unary_function_ptr * const  sincostan_tab;
  extern const unary_function_ptr * const  sincostansinhcoshtanh_tab;
  extern const unary_function_ptr * const  asinacosatan_tab;
  extern const unary_function_ptr * const  sinhcoshtanh_tab;
  extern const unary_function_ptr * const  sinhcoshtanhinv_tab;
  extern const unary_function_ptr * const  sign_floor_ceil_round_tab;
  extern const unary_function_ptr * const  reim_op;
  extern const unary_function_ptr * const  limit_tab;
  extern const gen_op_context limit_replace[];

  bool need_parenthesis(const gen & g);
  void prod2frac(const gen & g,vecteur & num,vecteur & den);
  gen vecteur2prod(const vecteur & num);

  gen _multistring(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_multistring;

  std::string unquote(const std::string & s);

  gen Gamma(const gen & x,GIAC_CONTEXT);
  gen _Gamma(const gen & args,GIAC_CONTEXT) ;
  extern const unary_function_ptr * const  at_Psi_minus_ln;
  gen Psi(const gen & x,GIAC_CONTEXT);
  gen Psi(const gen & x,int n,GIAC_CONTEXT);
  gen _Psi(const gen & args,GIAC_CONTEXT) ;
  gen _normalmod(const gen & g,GIAC_CONTEXT);
  // a=expression, x variable, n=number of terms, 
  // compute an approx value of sum((-1)^k*a(k),k,0,+infinity)
  // using Chebychev polynomials
  gen alternate_series(const gen & a,const gen & x,int n,GIAC_CONTEXT);
  gen Eta(const gen & s,int ndiff,GIAC_CONTEXT);
  gen Eta(const gen & s0,GIAC_CONTEXT);
  gen Zeta(const gen & x,int ndiff,GIAC_CONTEXT);
  gen Zeta(const gen & x,GIAC_CONTEXT);
  gen _Zeta(const gen & args,GIAC_CONTEXT) ;
  gen _Eta(const gen & args,GIAC_CONTEXT) ;
  gen _erfs(const gen & g,GIAC_CONTEXT);
  gen erf(const gen & x,GIAC_CONTEXT);
  gen _erf(const gen & args,GIAC_CONTEXT);
  gen erfc(const gen & x,GIAC_CONTEXT);
  gen _erfc(const gen & args,GIAC_CONTEXT);
  gen _SiCi_f(const gen & args,GIAC_CONTEXT);
  gen _SiCi_g(const gen & args,GIAC_CONTEXT);
  gen _Si(const gen & args,GIAC_CONTEXT);
  gen _Ci(const gen & args,GIAC_CONTEXT);
  gen _Ci0(const gen & args,GIAC_CONTEXT);
  gen _Ei_f(const gen & args,GIAC_CONTEXT);
  gen Ei(const gen & args,GIAC_CONTEXT);
  gen Ei(const gen & args,int n,GIAC_CONTEXT);
  gen _Ei(const gen & args,GIAC_CONTEXT);
  gen _Ei0(const gen & args,GIAC_CONTEXT);
  gen Ci_replace0(const gen & g,GIAC_CONTEXT);
  gen Ei_replace0(const gen & g,GIAC_CONTEXT);
  gen _Dirac(const gen & args,GIAC_CONTEXT);
  gen _Heaviside(const gen & args,GIAC_CONTEXT);
  std::string printassubs(const gen & feuille,const char * sommetstr,GIAC_CONTEXT);
  std::string printasmaple_subs(const gen & feuille,const char * sommetstr,GIAC_CONTEXT);
  void fonction_bidon();
  std::string printassto(const gen & feuille,const char * sommetstr,GIAC_CONTEXT);
  
#ifdef STATIC_BUILTIN_LEXER_FUNCTIONS
  extern const alias_unary_function_eval __sto;
#else
  extern const unary_function_eval __sto;
#endif

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_USUAL_H
