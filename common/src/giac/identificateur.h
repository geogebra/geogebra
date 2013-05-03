// -*- mode:C++ ; compile-command: "g++ -I.. -g -c identificateur.cc" -*-
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
#ifndef _GIAC_IDENTIFICATEUR_H
#define _GIAC_IDENTIFICATEUR_H
#include "first.h"
#include <string>
#include <iostream>
#include "global.h"
#include "gen.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  extern int protection_level; // for local vars

  const int MAXLENSIZE = 1000000; // max size of a line in files

  // make g identificateurs evaluated as global in null context
  gen global_eval(const gen & g,int level);
  gen global_evalf(const gen & g,int level);
  // return the local value of i, if globalize is true, replace idnt with
  // global idnt in returned value
  gen do_local_eval(const identificateur & i,int level,bool globalize);

  std::ostream & operator << (std::ostream & os,const identificateur & s);

#ifdef DOUBLEVAL // #ifdef GIAC_GENERIC_CONSTANTS_ID
  extern const char string_euler_gamma[];
  extern identificateur _IDNT_euler_gamma;
  extern gen cst_euler_gamma;
  extern const char string_pi[];
  identificateur & _IDNT_pi();
  extern alias_ref_identificateur ref_pi;
  extern gen cst_pi;
  extern const char string_infinity[];
  identificateur & _IDNT_infinity();
  extern alias_ref_identificateur ref_infinity;
  extern gen unsigned_inf;
  extern alias_gen & alias_unsigned_inf;
  extern const char string_undef[];
  identificateur & _IDNT_undef();
  extern gen undef;
#else  
  extern const char string_euler_gamma[];
  extern const gen & cst_euler_gamma;

  extern const char string_pi[];
  const identificateur & _IDNT_pi();
  extern const alias_ref_identificateur ref_pi;
  extern const alias_gen alias_cst_pi;
  extern const gen & cst_pi;

  extern const char string_infinity[];
  const identificateur & _IDNT_infinity();
  extern const alias_gen alias_unsigned_inf;
  extern const gen & unsigned_inf;

  extern const char string_undef[];
  const identificateur & _IDNT_undef();
  extern const alias_ref_identificateur ref_infinity;
  extern const alias_gen alias_undef;
  extern const gen & undef;
#endif

#ifdef GIAC_HAS_STO_38
  extern const identificateur & a__IDNT;
  extern const alias_gen alias_a38;
#define a__IDNT_e (*(gen const *)&alias_a38)
  extern const identificateur & b__IDNT;
  extern const alias_gen alias_b38;
#define b__IDNT_e (*(gen const *)&alias_b38)
  extern const identificateur & c__IDNT;
  extern const alias_gen alias_c38;
#define c__IDNT_e (*(gen const *)&alias_c38)
  extern const identificateur & d__IDNT;
  extern const alias_gen alias_d38;
#define d__IDNT_e (*(gen const *)&alias_d38)
  extern const identificateur & e__IDNT;
  extern const alias_gen alias_e38;
#define e__IDNT_e (*(gen const *)&alias_e38)
  extern const identificateur & f__IDNT;
  extern const alias_gen alias_f38;
#define f__IDNT_e (*(gen const *)&alias_f38)
  extern const identificateur & g__IDNT;
  extern const alias_gen alias_g38;
#define g__IDNT_e (*(gen const *)&alias_g38)
  extern const identificateur & h__IDNT;
  extern const alias_gen alias_h38;
#define h__IDNT_e (*(gen const *)&alias_h38)
  extern const identificateur & i__IDNT;
  extern const alias_gen alias_i38;
#define i__IDNT_e (*(gen const *)&alias_i38)
  extern const identificateur & I__IDNT;
  extern const alias_gen alias_j38;
#define j__IDNT_e (*(gen const *)&alias_j38)
  extern const identificateur & k__IDNT;
  extern const alias_gen alias_k38;
#define k__IDNT_e (*(gen const *)&alias_k38)
  extern const identificateur & l__IDNT;
  extern const alias_gen alias_l38;
#define l__IDNT_e (*(gen const *)&alias_l38)
  extern const identificateur & m__IDNT;
  extern const alias_gen alias_m38;
#define m__IDNT_e (*(gen const *)&alias_m38)
  extern const identificateur & n__IDNT;
  extern const alias_gen alias_n38;
#define n__IDNT_e (*(gen const *)&alias_n38)
  extern const identificateur & o__IDNT;
  extern const alias_gen alias_o38;
#define o__IDNT_e (*(gen const *)&alias_o38)
  extern const identificateur & p__IDNT;
  extern const alias_gen alias_p38;
#define p__IDNT_e (*(gen const *)&alias_p38)
  extern const identificateur & q__IDNT;
  extern const alias_gen alias_q38;
#define q__IDNT_e (*(gen const *)&alias_q38)
  extern const identificateur & r__IDNT;
  extern const alias_gen alias_r38;
#define r__IDNT_e (*(gen const *)&alias_r38)
  extern const identificateur & s__IDNT;
  extern const alias_gen alias_s38;
#define s__IDNT_e (*(gen const *)&alias_s38)
  extern const identificateur & t__IDNT;
  extern const alias_gen alias_t38;
#define t__IDNT_e (*(gen const *)&alias_t38)
  extern const identificateur & u__IDNT;
  extern const alias_gen alias_u38;
#define u__IDNT_e (*(gen const *)&alias_u38)
  extern const identificateur & v__IDNT;
  extern const alias_gen alias_v38;
#define v__IDNT_e (*(gen const *)&alias_v38)
  extern const identificateur & w__IDNT;
  extern const alias_gen alias_w38;
#define w__IDNT_e (*(gen const *)&alias_w38)
  extern const identificateur & x__IDNT;
  extern const alias_gen alias_x38;
#define x__IDNT_e (*(gen const *)&alias_x38)
  extern const identificateur & y__IDNT;
  extern const alias_gen alias_y38;
#define y__IDNT_e (*(gen const *)&alias_y38)
  extern const identificateur & z__IDNT;
  extern const alias_gen alias_z38;
#define z__IDNT_e (*(gen const *)&alias_z38)
  extern const identificateur & theta__IDNT;
  extern const gen & theta__IDNT_e;
  extern const identificateur & _IDNT_id_at;
  extern const identificateur & CST__IDNT;
  extern const identificateur & laplace_var;

  extern const gen & CST__IDNT_e;
  // extern gen & vx_var; 
  // commented otherwise can not make assign/assumptions on vx_var
  // if must uncomment, check extern gen vx_var declaration after endif
#else
  extern identificateur a__IDNT;
  extern gen a__IDNT_e;
  extern identificateur b__IDNT;
  extern gen b__IDNT_e;
  extern identificateur c__IDNT;
  extern gen c__IDNT_e;
  extern identificateur d__IDNT;
  extern gen d__IDNT_e;
  extern identificateur e__IDNT;
  extern gen e__IDNT_e;
  extern identificateur f__IDNT;
  extern gen f__IDNT_e;
  extern identificateur g__IDNT;
  extern gen g__IDNT_e;
  extern identificateur h__IDNT;
  extern gen h__IDNT_e;
  extern identificateur i__IDNT;
  extern gen i__IDNT_e;
  extern identificateur I__IDNT;
  extern gen j__IDNT_e;
  extern identificateur k__IDNT;
  extern gen k__IDNT_e;
  extern identificateur l__IDNT;
  extern gen l__IDNT_e;
  extern identificateur m__IDNT;
  extern gen m__IDNT_e;
  extern identificateur n__IDNT;
  extern gen n__IDNT_e;
  extern identificateur o__IDNT;
  extern gen o__IDNT_e;
  extern identificateur p__IDNT;
  extern gen p__IDNT_e;
  extern identificateur q__IDNT;
  extern gen q__IDNT_e;
  extern identificateur r__IDNT;
  extern gen r__IDNT_e;
  extern identificateur s__IDNT;
  extern gen s__IDNT_e;
  extern identificateur t__IDNT;
  extern gen t__IDNT_e;
  extern identificateur u__IDNT;
  extern gen u__IDNT_e;
  extern identificateur v__IDNT;
  extern gen v__IDNT_e;
  extern identificateur w__IDNT;
  extern gen w__IDNT_e;
  extern identificateur x__IDNT;
  extern gen x__IDNT_e;
  extern identificateur y__IDNT;
  extern gen y__IDNT_e;
  extern identificateur z__IDNT;
  extern gen z__IDNT_e;
  extern identificateur laplace_var;
  extern identificateur theta__IDNT;
  extern gen theta__IDNT_e;
  extern identificateur _IDNT_id_at;
  extern identificateur CST__IDNT;
  extern gen CST__IDNT_e;
#endif
  extern gen vx_var;
  extern const gen * const tab_one_letter_idnt[];
  // small utility to remove #...
  int removecomments(const char * ss,char * ss2);


#ifndef NO_NAMESPACE_GIAC
}
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_IDENTIFICATEUR_H
