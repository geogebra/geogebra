// -*- mode:C++ ; compile-command: "g++-3.4 -I. -I.. -I../include -DHAVE_CONFIG_H -DIN_GIAC -g -c -Wall identificateur.cc" -*-
#include "giacPCH.h"

/*
 *  Copyright (C) 2000,7 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
using namespace std;
#include <cmath>
#include <fstream>
#include <string>
//#include <unistd.h> // For reading arguments from file
#include "identificateur.h"
#include "gen.h"
#include "sym2poly.h"
#include "rpn.h"
#include "prog.h"
#include "usual.h"
#include "giacintl.h"

#ifdef BESTA_OS
// Local replacement for strdup on BESTA OS.
static char* strdup(char* str)
{
    if ( ! str )
    {
        return str;
    }
    
    int len = strlen(str) + 2;
    char* p = new char[len];
    strcpy(p, str);
    return p;
}
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  // bool variables_are_files=true; // FIXME -> false and change rpn.cc at_VARS
  int protection_level=0; // for local variables in null context

  struct int_string_shortint_bool {
    int i;
    const char * s;
    short int b;
    bool s_dynalloc;
  };

  struct alias_identificateur {
    int * ref_count;
    gen * value;
    const char * id_name;
    vecteur * localvalue;
    short int * quoted;
  };

#ifdef DOUBLEVAL // #ifdef GIAC_GENERIC_CONSTANTS
  const char string_euler_gamma[]="euler_gamma";
  identificateur _IDNT_euler_gamma(string_euler_gamma,(double) .577215664901533);
  gen cst_euler_gamma(_IDNT_euler_gamma);

  const char string_pi[]="pi";
  identificateur & _IDNT_pi(){
    static identificateur * ans=new identificateur(string_pi,(double) M_PI);
    return * ans;
  }
  // identificateur _IDNT_pi(string_pi,(double) M_PI);
  alias_ref_identificateur ref_pi={-1,0,0,string_pi,0,0};
  
  gen cst_pi(_IDNT_pi());

  const char string_infinity[]="infinity";
  identificateur & _IDNT_infinity(){
    static identificateur * ans=new identificateur("infinity");
    return * ans;
  }
  gen unsigned_inf(_IDNT_infinity());
  alias_gen & alias_unsigned_inf = *(alias_gen *) & unsigned_inf;
  alias_ref_identificateur ref_infinity={-1,0,0,string_infinity,0,0};
  
  const char string_undef[]="undef";
  identificateur & _IDNT_undef(){
    static identificateur * ans=new identificateur("undef");
    return * ans;
  }
  gen undef(_IDNT_undef());

#else
  const char string_euler_gamma[]="euler_gamma";
  static const alias_ref_identificateur ref_euler_gamma={-1,0,0,string_euler_gamma,0,0};
  const define_alias_gen(alias_cst_euler_gamma,_IDNT,0,&ref_euler_gamma);
  const gen & cst_euler_gamma = * (gen *) & alias_cst_euler_gamma;

  const char string_pi[]="pi";
  static const alias_identificateur alias_identificateur_pi={0,0,string_pi,0,0};
  const identificateur & _IDNT_pi(){
    return *(const identificateur *) & alias_identificateur_pi;
  }
  const alias_ref_identificateur ref_pi={-1,0,0,string_pi,0,0};
  const define_alias_gen(alias_cst_pi,_IDNT,0,&ref_pi);
  const gen & cst_pi = * (gen *) & alias_cst_pi;

  const char string_infinity[]="infinity";
  static const alias_identificateur alias_identificateur_infinity={0,0,string_infinity,0,0};
  const identificateur & _IDNT_infinity(){
    return * (const identificateur *) &alias_identificateur_infinity;
  }
  const alias_ref_identificateur ref_infinity={-1,0,0,string_infinity,0,0};
  const define_alias_gen(alias_unsigned_inf,_IDNT,0,&ref_infinity);
  const gen & unsigned_inf = * (gen *) & alias_unsigned_inf;

  const char string_undef[]="undef";
  static const alias_identificateur alias_identificateur_undef={0,0,string_undef,0,0};
  const identificateur & _IDNT_undef(){
    return * (const identificateur *) &alias_identificateur_undef;
  }
  static const alias_ref_identificateur ref_undef={-1,0,0,string_undef,0,0};
  const define_alias_gen(alias_undef,_IDNT,0,&ref_undef);
  const gen & undef = * (gen *) & alias_undef;

#endif // GIAC_GENERIC_CONSTANTS

#ifdef GIAC_HAS_STO_38
#if 0
  static const alias_identificateur alias_identificateur_a38={0,0,"A",0,0};
  const identificateur & a__IDNT=* (const identificateur *) &alias_identificateur_a38;
  const alias_ref_identificateur ref_a38={-1,0,0,"A",0,0};
  const define_alias_gen(alias_a38,_IDNT,0,&ref_a38);
//  const gen & a__IDNT_e = * (gen *) & alias_a38;

  static const alias_identificateur alias_identificateur_b38={0,0,"B",0,0};
  const identificateur & b__IDNT=* (const identificateur *) &alias_identificateur_b38;
  const alias_ref_identificateur ref_b38={-1,0,0,"B",0,0};
  const define_alias_gen(alias_b38,_IDNT,0,&ref_b38);
//  const gen & b__IDNT_e = * (gen *) & alias_b38;

  static const alias_identificateur alias_identificateur_c38={0,0,"C",0,0};
  const identificateur & c__IDNT=* (const identificateur *) &alias_identificateur_c38;
  const alias_ref_identificateur ref_c38={-1,0,0,"C",0,0};
  const define_alias_gen(alias_c38,_IDNT,0,&ref_c38);
//  const gen & c__IDNT_e = * (gen *) & alias_c38;

  static const alias_identificateur alias_identificateur_d38={0,0,"D",0,0};
  const identificateur & d__IDNT=* (const identificateur *) &alias_identificateur_d38;
  const alias_ref_identificateur ref_d38={-1,0,0,"D",0,0};
  const define_alias_gen(alias_d38,_IDNT,0,&ref_d38);
//  const gen & d__IDNT_e = * (gen *) & alias_d38;

  static const alias_identificateur alias_identificateur_e38={0,0,"E",0,0};
  const identificateur & e__IDNT=* (const identificateur *) &alias_identificateur_e38;
  const alias_ref_identificateur ref_e38={-1,0,0,"E",0,0};
  const define_alias_gen(alias_e38,_IDNT,0,&ref_e38);
//  const gen & e__IDNT_e = * (gen *) & alias_e38;

  static const alias_identificateur alias_identificateur_f38={0,0,"F",0,0};
  const identificateur & f__IDNT=* (const identificateur *) &alias_identificateur_f38;
  const alias_ref_identificateur ref_f38={-1,0,0,"F",0,0};
  const define_alias_gen(alias_f38,_IDNT,0,&ref_f38);
//  const gen & f__IDNT_e = * (gen *) & alias_f38;

  static const alias_identificateur alias_identificateur_g38={0,0,"G",0,0};
  const identificateur & g__IDNT=* (const identificateur *) &alias_identificateur_g38;
  const alias_ref_identificateur ref_g38={-1,0,0,"G",0,0};
  const define_alias_gen(alias_g38,_IDNT,0,&ref_g38);
//  const gen & g__IDNT_e = * (gen *) & alias_g38;

  static const alias_identificateur alias_identificateur_h38={0,0,"H",0,0};
  const identificateur & h__IDNT=* (const identificateur *) &alias_identificateur_h38;
  const alias_ref_identificateur ref_h38={-1,0,0,"H",0,0};
  const define_alias_gen(alias_h38,_IDNT,0,&ref_h38);
//  const gen & h__IDNT_e = * (gen *) & alias_h38;

  static const alias_identificateur alias_identificateur_i38={0,0,"I",0,0};
  const identificateur & i__IDNT=* (const identificateur *) &alias_identificateur_i38;
  const alias_ref_identificateur ref_i38={-1,0,0,"I",0,0};
  const define_alias_gen(alias_i38,_IDNT,0,&ref_i38);
//  const gen & i__IDNT_e = * (gen *) & alias_i38;

  static const alias_identificateur alias_identificateur_j38={0,0,"J",0,0};
  const identificateur & j__IDNT=* (const identificateur *) &alias_identificateur_j38;
  const alias_ref_identificateur ref_j38={-1,0,0,"J",0,0};
  const define_alias_gen(alias_j38,_IDNT,0,&ref_j38);
//  const gen & j__IDNT_e = * (gen *) & alias_j38;

  static const alias_identificateur alias_identificateur_k38={0,0,"K",0,0};
  const identificateur & k__IDNT=* (const identificateur *) &alias_identificateur_k38;
  const alias_ref_identificateur ref_k38={-1,0,0,"K",0,0};
  const define_alias_gen(alias_k38,_IDNT,0,&ref_k38);
// const gen & k__IDNT_e = * (gen *) & alias_k38;

  static const alias_identificateur alias_identificateur_l38={0,0,"L",0,0};
  const identificateur & l__IDNT=* (const identificateur *) &alias_identificateur_l38;
  const alias_ref_identificateur ref_l38={-1,0,0,"L",0,0};
  const define_alias_gen(alias_l38,_IDNT,0,&ref_l38);
//  const gen & l__IDNT_e = * (gen *) & alias_l38;

  static const alias_identificateur alias_identificateur_m38={0,0,"M",0,0};
  const identificateur & m__IDNT=* (const identificateur *) &alias_identificateur_m38;
  const alias_ref_identificateur ref_m38={-1,0,0,"M",0,0};
  const define_alias_gen(alias_m38,_IDNT,0,&ref_m38);
//  const gen & m__IDNT_e = * (gen *) & alias_m38;

  static const alias_identificateur alias_identificateur_n38={0,0,"N",0,0};
  const identificateur & n__IDNT=* (const identificateur *) &alias_identificateur_n38;
  const alias_ref_identificateur ref_n38={-1,0,0,"N",0,0};
  const define_alias_gen(alias_n38,_IDNT,0,&ref_n38);
//  const gen & n__IDNT_e = * (gen *) & alias_n38;

  static const alias_identificateur alias_identificateur_o38={0,0,"O",0,0};
  const identificateur & o__IDNT=* (const identificateur *) &alias_identificateur_o38;
  const alias_ref_identificateur ref_o38={-1,0,0,"O",0,0};
  const define_alias_gen(alias_o38,_IDNT,0,&ref_o38);
//  const gen & o__IDNT_e = * (gen *) & alias_o38;

  static const alias_identificateur alias_identificateur_p38={0,0,"P",0,0};
  const identificateur & p__IDNT=* (const identificateur *) &alias_identificateur_p38;
  const alias_ref_identificateur ref_p38={-1,0,0,"P",0,0};
  const define_alias_gen(alias_p38,_IDNT,0,&ref_p38);
//  const gen & p__IDNT_e = * (gen *) & alias_p38;

  static const alias_identificateur alias_identificateur_q38={0,0,"Q",0,0};
  const identificateur & q__IDNT=* (const identificateur *) &alias_identificateur_q38;
  const alias_ref_identificateur ref_q38={-1,0,0,"Q",0,0};
  const define_alias_gen(alias_q38,_IDNT,0,&ref_q38);
//  const gen & q__IDNT_e = * (gen *) & alias_q38;

  static const alias_identificateur alias_identificateur_r38={0,0,"R",0,0};
  const identificateur & r__IDNT=* (const identificateur *) &alias_identificateur_r38;
  const alias_ref_identificateur ref_r38={-1,0,0,"R",0,0};
  const define_alias_gen(alias_r38,_IDNT,0,&ref_r38);
//  const gen & r__IDNT_e = * (gen *) & alias_r38;

  static const alias_identificateur alias_identificateur_s38={0,0,"S",0,0};
  const identificateur & s__IDNT=* (const identificateur *) &alias_identificateur_s38;
  const alias_ref_identificateur ref_s38={-1,0,0,"S",0,0};
  const define_alias_gen(alias_s38,_IDNT,0,&ref_s38);
//  const gen & s__IDNT_e = * (gen *) & alias_s38;

  static const alias_identificateur alias_identificateur_t38={0,0,"T",0,0};
  const identificateur & t__IDNT=* (const identificateur *) &alias_identificateur_t38;
  const alias_ref_identificateur ref_t38={-1,0,0,"T",0,0};
  const define_alias_gen(alias_t38,_IDNT,0,&ref_t38);
//  const gen & t__IDNT_e = * (gen *) & alias_t38;

  static const alias_identificateur alias_identificateur_u38={0,0,"U",0,0};
  const identificateur & u__IDNT=* (const identificateur *) &alias_identificateur_u38;
  const alias_ref_identificateur ref_u38={-1,0,0,"U",0,0};
  const define_alias_gen(alias_u38,_IDNT,0,&ref_u38);
//  const gen & u__IDNT_e = * (gen *) & alias_u38;

  static const alias_identificateur alias_identificateur_v38={0,0,"V",0,0};
  const identificateur & v__IDNT=* (const identificateur *) &alias_identificateur_v38;
  const alias_ref_identificateur ref_v38={-1,0,0,"V",0,0};
  const define_alias_gen(alias_v38,_IDNT,0,&ref_v38);
//  const gen & v__IDNT_e = * (gen *) & alias_v38;

  static const alias_identificateur alias_identificateur_w38={0,0,"W",0,0};
  const identificateur & w__IDNT=* (const identificateur *) &alias_identificateur_w38;
  const alias_ref_identificateur ref_w38={-1,0,0,"W",0,0};
  const define_alias_gen(alias_w38,_IDNT,0,&ref_w38);
//  const gen & w__IDNT_e = * (gen *) & alias_w38;

  static const alias_identificateur alias_identificateur_x38={0,0,"X",0,0};
  const identificateur & x__IDNT=* (const identificateur *) &alias_identificateur_x38;
  const alias_ref_identificateur ref_x38={-1,0,0,"X",0,0};
  const define_alias_gen(alias_x38,_IDNT,0,&ref_x38);
//  const gen & x__IDNT_e = * (gen *) & alias_x38;

  static const alias_identificateur alias_identificateur_xx38={0,0,"x",0,0};
  const identificateur & xx__IDNT=* (const identificateur *) &alias_identificateur_xx38;
  const alias_ref_identificateur ref_xx38={-1,0,0,"x",0,0};
  const define_alias_gen(alias_xx38,_IDNT,0,&ref_xx38);
//  const gen & x__IDNT_e = * (gen *) & alias_xx38;

  static const alias_identificateur alias_identificateur_y38={0,0,"Y",0,0};
  const identificateur & y__IDNT=* (const identificateur *) &alias_identificateur_y38;
  const alias_ref_identificateur ref_y38={-1,0,0,"Y",0,0};
  const define_alias_gen(alias_y38,_IDNT,0,&ref_y38);
//  const gen & y__IDNT_e = * (gen *) & alias_y38;

  static const alias_identificateur alias_identificateur_z38={0,0,"Z",0,0};
  const identificateur & z__IDNT=* (const identificateur *) &alias_identificateur_z38;
  const alias_ref_identificateur ref_z38={-1,0,0,"Z",0,0};
  const define_alias_gen(alias_z38,_IDNT,0,&ref_z38);
//  const gen & z__IDNT_e = * (gen *) & alias_z38;

#else
  static const alias_identificateur alias_identificateur_a38={0,0,"a",0,0};
  const identificateur & a__IDNT=* (const identificateur *) &alias_identificateur_a38;
  const alias_ref_identificateur ref_a38={-1,0,0,"a",0,0};
  const define_alias_gen(alias_a38,_IDNT,0,&ref_a38);
//  const gen & a__IDNT_e = * (gen *) & alias_a38;

  static const alias_identificateur alias_identificateur_b38={0,0,"b",0,0};
  const identificateur & b__IDNT=* (const identificateur *) &alias_identificateur_b38;
  const alias_ref_identificateur ref_b38={-1,0,0,"b",0,0};
  const define_alias_gen(alias_b38,_IDNT,0,&ref_b38);
//  const gen & b__IDNT_e = * (gen *) & alias_b38;

  static const alias_identificateur alias_identificateur_c38={0,0,"c",0,0};
  const identificateur & c__IDNT=* (const identificateur *) &alias_identificateur_c38;
  const alias_ref_identificateur ref_c38={-1,0,0,"c",0,0};
  const define_alias_gen(alias_c38,_IDNT,0,&ref_c38);
//  const gen & c__IDNT_e = * (gen *) & alias_c38;

  static const alias_identificateur alias_identificateur_d38={0,0,"d",0,0};
  const identificateur & d__IDNT=* (const identificateur *) &alias_identificateur_d38;
  const alias_ref_identificateur ref_d38={-1,0,0,"d",0,0};
  const define_alias_gen(alias_d38,_IDNT,0,&ref_d38);
//  const gen & d__IDNT_e = * (gen *) & alias_d38;

  static const alias_identificateur alias_identificateur_e38={0,0,"e",0,0};
  const identificateur & e__IDNT=* (const identificateur *) &alias_identificateur_e38;
  const alias_ref_identificateur ref_e38={-1,0,0,"e",0,0};
  const define_alias_gen(alias_e38,_IDNT,0,&ref_e38);
//  const gen & e__IDNT_e = * (gen *) & alias_e38;

  static const alias_identificateur alias_identificateur_f38={0,0,"f",0,0};
  const identificateur & f__IDNT=* (const identificateur *) &alias_identificateur_f38;
  const alias_ref_identificateur ref_f38={-1,0,0,"f",0,0};
  const define_alias_gen(alias_f38,_IDNT,0,&ref_f38);
//  const gen & f__IDNT_e = * (gen *) & alias_f38;

  static const alias_identificateur alias_identificateur_g38={0,0,"g",0,0};
  const identificateur & g__IDNT=* (const identificateur *) &alias_identificateur_g38;
  const alias_ref_identificateur ref_g38={-1,0,0,"g",0,0};
  const define_alias_gen(alias_g38,_IDNT,0,&ref_g38);
//  const gen & g__IDNT_e = * (gen *) & alias_g38;

  static const alias_identificateur alias_identificateur_h38={0,0,"h",0,0};
  const identificateur & h__IDNT=* (const identificateur *) &alias_identificateur_h38;
  const alias_ref_identificateur ref_h38={-1,0,0,"h",0,0};
  const define_alias_gen(alias_h38,_IDNT,0,&ref_h38);
//  const gen & h__IDNT_e = * (gen *) & alias_h38;

  static const alias_identificateur alias_identificateur_i38={0,0,"i",0,0};
  const identificateur & i__IDNT=* (const identificateur *) &alias_identificateur_i38;
  const alias_ref_identificateur ref_i38={-1,0,0,"i",0,0};
  const define_alias_gen(alias_i38,_IDNT,0,&ref_i38);
//  const gen & i__IDNT_e = * (gen *) & alias_i38;

  static const alias_identificateur alias_identificateur_j38={0,0,"j",0,0};
  const identificateur & j__IDNT=* (const identificateur *) &alias_identificateur_j38;
  const alias_ref_identificateur ref_j38={-1,0,0,"j",0,0};
  const define_alias_gen(alias_j38,_IDNT,0,&ref_j38);
//  const gen & j__IDNT_e = * (gen *) & alias_j38;

  static const alias_identificateur alias_identificateur_k38={0,0,"k",0,0};
  const identificateur & k__IDNT=* (const identificateur *) &alias_identificateur_k38;
  const alias_ref_identificateur ref_k38={-1,0,0,"k",0,0};
  const define_alias_gen(alias_k38,_IDNT,0,&ref_k38);
// const gen & k__IDNT_e = * (gen *) & alias_k38;

  static const alias_identificateur alias_identificateur_l38={0,0,"l",0,0};
  const identificateur & l__IDNT=* (const identificateur *) &alias_identificateur_l38;
  const alias_ref_identificateur ref_l38={-1,0,0,"l",0,0};
  const define_alias_gen(alias_l38,_IDNT,0,&ref_l38);
//  const gen & l__IDNT_e = * (gen *) & alias_l38;

  static const alias_identificateur alias_identificateur_m38={0,0,"m",0,0};
  const identificateur & m__IDNT=* (const identificateur *) &alias_identificateur_m38;
  const alias_ref_identificateur ref_m38={-1,0,0,"m",0,0};
  const define_alias_gen(alias_m38,_IDNT,0,&ref_m38);
//  const gen & m__IDNT_e = * (gen *) & alias_m38;

  static const alias_identificateur alias_identificateur_n38={0,0,"n",0,0};
  const identificateur & n__IDNT=* (const identificateur *) &alias_identificateur_n38;
  const alias_ref_identificateur ref_n38={-1,0,0,"n",0,0};
  const define_alias_gen(alias_n38,_IDNT,0,&ref_n38);
//  const gen & n__IDNT_e = * (gen *) & alias_n38;

  static const alias_identificateur alias_identificateur_o38={0,0,"o",0,0};
  const identificateur & o__IDNT=* (const identificateur *) &alias_identificateur_o38;
  const alias_ref_identificateur ref_o38={-1,0,0,"o",0,0};
  const define_alias_gen(alias_o38,_IDNT,0,&ref_o38);
//  const gen & o__IDNT_e = * (gen *) & alias_o38;

  static const alias_identificateur alias_identificateur_p38={0,0,"p",0,0};
  const identificateur & p__IDNT=* (const identificateur *) &alias_identificateur_p38;
  const alias_ref_identificateur ref_p38={-1,0,0,"p",0,0};
  const define_alias_gen(alias_p38,_IDNT,0,&ref_p38);
//  const gen & p__IDNT_e = * (gen *) & alias_p38;

  static const alias_identificateur alias_identificateur_q38={0,0,"q",0,0};
  const identificateur & q__IDNT=* (const identificateur *) &alias_identificateur_q38;
  const alias_ref_identificateur ref_q38={-1,0,0,"q",0,0};
  const define_alias_gen(alias_q38,_IDNT,0,&ref_q38);
//  const gen & q__IDNT_e = * (gen *) & alias_q38;

  static const alias_identificateur alias_identificateur_r38={0,0,"r",0,0};
  const identificateur & r__IDNT=* (const identificateur *) &alias_identificateur_r38;
  const alias_ref_identificateur ref_r38={-1,0,0,"r",0,0};
  const define_alias_gen(alias_r38,_IDNT,0,&ref_r38);
//  const gen & r__IDNT_e = * (gen *) & alias_r38;

  static const alias_identificateur alias_identificateur_s38={0,0,"s",0,0};
  const identificateur & s__IDNT=* (const identificateur *) &alias_identificateur_s38;
  const alias_ref_identificateur ref_s38={-1,0,0,"s",0,0};
  const define_alias_gen(alias_s38,_IDNT,0,&ref_s38);
//  const gen & s__IDNT_e = * (gen *) & alias_s38;

  static const alias_identificateur alias_identificateur_t38={0,0,"t",0,0};
  const identificateur & t__IDNT=* (const identificateur *) &alias_identificateur_t38;
  const alias_ref_identificateur ref_t38={-1,0,0,"t",0,0};
  const define_alias_gen(alias_t38,_IDNT,0,&ref_t38);
//  const gen & t__IDNT_e = * (gen *) & alias_t38;

  static const alias_identificateur alias_identificateur_u38={0,0,"u",0,0};
  const identificateur & u__IDNT=* (const identificateur *) &alias_identificateur_u38;
  const alias_ref_identificateur ref_u38={-1,0,0,"u",0,0};
  const define_alias_gen(alias_u38,_IDNT,0,&ref_u38);
//  const gen & u__IDNT_e = * (gen *) & alias_u38;

  static const alias_identificateur alias_identificateur_v38={0,0,"v",0,0};
  const identificateur & v__IDNT=* (const identificateur *) &alias_identificateur_v38;
  const alias_ref_identificateur ref_v38={-1,0,0,"v",0,0};
  const define_alias_gen(alias_v38,_IDNT,0,&ref_v38);
//  const gen & v__IDNT_e = * (gen *) & alias_v38;

  static const alias_identificateur alias_identificateur_w38={0,0,"w",0,0};
  const identificateur & w__IDNT=* (const identificateur *) &alias_identificateur_w38;
  const alias_ref_identificateur ref_w38={-1,0,0,"w",0,0};
  const define_alias_gen(alias_w38,_IDNT,0,&ref_w38);
//  const gen & w__IDNT_e = * (gen *) & alias_w38;

  static const alias_identificateur alias_identificateur_x38={0,0,"x",0,0};
  const identificateur & x__IDNT=* (const identificateur *) &alias_identificateur_x38;
  const alias_ref_identificateur ref_x38={-1,0,0,"x",0,0};
  const define_alias_gen(alias_x38,_IDNT,0,&ref_x38);
//  const gen & x__IDNT_e = * (gen *) & alias_x38;

  static const alias_identificateur alias_identificateur_xx38={0,0,"x",0,0};
  const identificateur & xx__IDNT=* (const identificateur *) &alias_identificateur_xx38;
  const alias_ref_identificateur ref_xx38={-1,0,0,"x",0,0};
  const define_alias_gen(alias_xx38,_IDNT,0,&ref_xx38);
//  const gen & x__IDNT_e = * (gen *) & alias_xx38;

  static const alias_identificateur alias_identificateur_y38={0,0,"y",0,0};
  const identificateur & y__IDNT=* (const identificateur *) &alias_identificateur_y38;
  const alias_ref_identificateur ref_y38={-1,0,0,"y",0,0};
  const define_alias_gen(alias_y38,_IDNT,0,&ref_y38);
//  const gen & y__IDNT_e = * (gen *) & alias_y38;

  static const alias_identificateur alias_identificateur_z38={0,0,"z",0,0};
  const identificateur & z__IDNT=* (const identificateur *) &alias_identificateur_z38;
  const alias_ref_identificateur ref_z38={-1,0,0,"z",0,0};
  const define_alias_gen(alias_z38,_IDNT,0,&ref_z38);
//  const gen & z__IDNT_e = * (gen *) & alias_z38;
#endif

  static const alias_identificateur alias_identificateur_laplace_var={0,0," s",0,0};
  const identificateur & laplace_var=* (const identificateur *) &alias_identificateur_laplace_var;
  const alias_ref_identificateur ref_laplace_var={-1,0,0," s",0,0};
  const define_alias_gen(alias_laplace_var,_IDNT,0,&ref_laplace_var);
  const gen & laplace_var_e = * (gen *) & alias_laplace_var;

  static const alias_identificateur alias_identificateur_theta38={0,0,"θ",0,0};
  const identificateur & theta__IDNT=* (const identificateur *) &alias_identificateur_theta38;
  const alias_ref_identificateur ref_theta38={-1,0,0,"θ",0,0};
  const define_alias_gen(alias_theta38,_IDNT,0,&ref_theta38);
  const gen & theta__IDNT_e = * (gen *) & alias_theta38;

  static const alias_identificateur alias_identificateur_CST38={0,0,"CST",0,0};
  const identificateur & CST__IDNT=* (const identificateur *) &alias_identificateur_CST38;
  const alias_ref_identificateur ref_CST38={-1,0,0,"CST",0,0};
  const define_alias_gen(alias_CST38,_IDNT,0,&ref_CST38);
  const gen & CST__IDNT_e = * (gen *) & alias_CST38;

  static const alias_identificateur alias_identificateur_at38={0,0,"at",0,0};
  const identificateur & _IDNT_id_at=* (const identificateur *) &alias_identificateur_at38;
  const alias_ref_identificateur ref_at38={-1,0,0,"at",0,0};
  const define_alias_gen(alias_at38,_IDNT,0,&ref_at38);
  const gen & at__IDNT_e = * (gen *) & alias_at38;

#ifdef CAS38_DISABLED
  define_alias_gen(alias_vx38,_IDNT,0,&ref_x38);
#else
  define_alias_gen(alias_vx38,_IDNT,0,&ref_xx38);
#endif
  // gen & vx_var = * (gen *) & alias_vx38;
  gen vx_var(identificateur("x"));

  /* model
  static const alias_identificateur alias_identificateur_zzz38={0,0,"ZZZ",0,0};
  const identificateur & zzz__IDNT=* (const identificateur *) &alias_identificateur_zzz38;
  const alias_ref_identificateur ref_zzz38={-1,0,0,"ZZZ",0,0};
  const define_alias_gen(alias_zzz38,_IDNT,0,&ref_zzz38);
  const gen & zzz__IDNT_e = * (gen *) & alias_zzz38;

  */

#else // GIAC_HAS_STO_38
  identificateur a__IDNT("a");
  gen a__IDNT_e(a__IDNT);
  identificateur b__IDNT("b");
  gen b__IDNT_e(b__IDNT);
  identificateur c__IDNT("c");
  gen c__IDNT_e(c__IDNT);
  identificateur d__IDNT("d");
  gen d__IDNT_e(d__IDNT);
  identificateur e__IDNT("e");
  gen e__IDNT_e(e__IDNT);
  identificateur f__IDNT("f");
  gen f__IDNT_e(f__IDNT);
  identificateur g__IDNT("g");
  gen g__IDNT_e(g__IDNT);
  identificateur h__IDNT("h");
  gen h__IDNT_e(h__IDNT);
  identificateur i__IDNT("i");
  gen i__IDNT_e(i__IDNT);
  identificateur j__IDNT("j");
  gen j__IDNT_e(j__IDNT);
  identificateur k__IDNT("k");
  gen k__IDNT_e(k__IDNT);
  identificateur l__IDNT("l");
  gen l__IDNT_e(l__IDNT);
  identificateur m__IDNT("m");
  gen m__IDNT_e(m__IDNT);
  identificateur n__IDNT("n");
  gen n__IDNT_e(n__IDNT);
  identificateur o__IDNT("o");
  gen o__IDNT_e(o__IDNT);
  identificateur p__IDNT("p");
  gen p__IDNT_e(p__IDNT);
  identificateur q__IDNT("q");
  gen q__IDNT_e(q__IDNT);
  identificateur r__IDNT("r");
  gen r__IDNT_e(r__IDNT);
  identificateur s__IDNT("s");
  gen s__IDNT_e(s__IDNT);
  identificateur t__IDNT("t");
  gen t__IDNT_e(t__IDNT);
  identificateur u__IDNT("u");
  gen u__IDNT_e(u__IDNT);
  identificateur v__IDNT("v");
  gen v__IDNT_e(v__IDNT);
  identificateur w__IDNT("w");
  gen w__IDNT_e(w__IDNT);
  identificateur x__IDNT("x");
  gen x__IDNT_e(x__IDNT);
  identificateur y__IDNT("y");
  gen y__IDNT_e(y__IDNT);
  identificateur z__IDNT("z");
  gen z__IDNT_e(z__IDNT);
  identificateur laplace_var(" s");
  gen laplace_var_e(laplace_var);
  identificateur theta__IDNT("θ");
  gen theta__IDNT_e(theta__IDNT);
  identificateur CST__IDNT("CST");
  gen CST__IDNT_e(CST__IDNT);
  identificateur _IDNT_id_at("id_at");
  gen vx_var(x__IDNT_e);
#endif // GIAC_HAS_STO_38

  const gen * const tab_one_letter_idnt[]={&a__IDNT_e,&b__IDNT_e,&c__IDNT_e,&d__IDNT_e,&e__IDNT_e,&f__IDNT_e,&g__IDNT_e,&h__IDNT_e,&i__IDNT_e,&j__IDNT_e,&k__IDNT_e,&l__IDNT_e,&m__IDNT_e,&n__IDNT_e,&o__IDNT_e,&p__IDNT_e,&q__IDNT_e,&r__IDNT_e,&s__IDNT_e,&t__IDNT_e,&u__IDNT_e,&v__IDNT_e,&w__IDNT_e,&x__IDNT_e,&y__IDNT_e,&z__IDNT_e};

  identificateur::identificateur(){
    int_string_shortint_bool * ptr = new int_string_shortint_bool;
    ptr->i=1;
    ptr->b=0;
    ptr->s_dynalloc=true;
#ifdef GIAC_HAS_STO_38
    string tmp=string("_"+print_INT_(rand()));
#else
    string tmp=string(" "+print_INT_(rand()));
#endif
    int l=tmp.size();
    char * c = new char[l+1];
    strcpy(c,tmp.c_str());
    ptr->s=c;
    ref_count = &ptr->i ;
    value = NULL;
    quoted = &ptr->b ;
    localvalue = 0;
    id_name = ptr->s ;
  }

  identificateur::identificateur(const string & s){
    bool b=strchr(s.c_str(),' ');
    int_string_shortint_bool * ptr = new int_string_shortint_bool;
    ptr->i=1;
    ptr->b=0;
    ptr->s_dynalloc=true;
    char * c = new char[s.size()+(b?3:1)];
    ptr->s=strcpy(c,b?('`'+s+'`').c_str():s.c_str());
#ifdef GIAC_HAS_STO_38
    for (;*c;++c){
      if (*c==' ')
	*c='_';
    }
#endif
    ref_count = &ptr->i ;
    value = NULL;
    quoted = &ptr->b ;
    localvalue = 0;
    id_name = ptr->s ;
  }

  identificateur::identificateur(const string & s,const gen & e){
    bool b=strchr(s.c_str(),' ');
    int_string_shortint_bool * ptr = new int_string_shortint_bool;
    ptr->i=1;
    ptr->b=0;
    ptr->s_dynalloc=true;
    char * c = new char[s.size()+(b?3:1)];
    ptr->s=strcpy(c,b?('`'+s+'`').c_str():s.c_str());
    ref_count = &ptr->i ;
    quoted = &ptr->b ;
    localvalue = 0;
    id_name = ptr->s ;
    value = new gen(e);
  }

  identificateur::identificateur(const char * s){
    if (strchr(s,' ')){
      ref_count=0;
      string S(s);
#ifdef GIAC_HAS_STO_38
      for (unsigned i=0;i<S.size();++i){
	if (S[i]==' '){
	  S[i]='_';
	}
      }
#endif
      *this=identificateur(S);
      return;
    }
    int_string_shortint_bool * ptr = new int_string_shortint_bool;
    ptr->i=1;
    ptr->b=0;
    ptr->s=s;
    ptr->s_dynalloc=false;
    ref_count = &ptr->i ;
    value = NULL;
    quoted = &ptr->b ;
    localvalue = 0;
    id_name = ptr->s ;
  }

  identificateur::identificateur(const char * s,const gen & e){
    if (strchr(s,' ')){
      ref_count=0;
      *this=identificateur(string(s),e);
      return;
    }
    int_string_shortint_bool * ptr = new int_string_shortint_bool;
    ptr->i=1;
    ptr->b=0;
    ptr->s=s;
    ptr->s_dynalloc=false;
    ref_count = &ptr->i ;
    quoted = &ptr->b ;
    localvalue = 0;
    id_name = ptr->s ;
    value = new gen(e);
  }

  identificateur::identificateur(const identificateur & s){
    ref_count=s.ref_count;
    if (ref_count)
      ++(*ref_count);
    value=s.value;
    quoted=s.quoted;
    localvalue=s.localvalue;
    id_name=s.id_name;
  }

  identificateur::~identificateur(){
    if (ref_count){
      --(*ref_count);
      if (!(*ref_count)){
	int_string_shortint_bool * ptr = (int_string_shortint_bool *) ref_count;
	if (ptr->s_dynalloc)
	  delete [] ptr->s;
	delete ptr;
	if (value)
	  delete value;
	if (localvalue)
	  delete localvalue;
      }
    }
  }

  void identificateur::MakeCopyOfNameIfNotLocal() {
    int_string_shortint_bool * ptr = (int_string_shortint_bool *) ref_count;
    if (ptr->s_dynalloc) return;
    id_name = ptr->s= strdup(ptr->s);
    ptr->s_dynalloc= true;
  }

  identificateur & identificateur::operator =(const identificateur & s){
    if (ref_count){
      --(*ref_count);
      if (!(*ref_count)){
	int_string_shortint_bool * ptr = (int_string_shortint_bool *) ref_count;
	if (ptr->s_dynalloc)
	  delete [] ptr->s;
	delete ptr;
	if (value)
	  delete value;
	if (localvalue)
	  delete localvalue;
      }
    }
    ref_count=s.ref_count;
    if (ref_count)
      ++(*ref_count);
    value=s.value;
    quoted=s.quoted;
    localvalue=s.localvalue;
    id_name=s.id_name;
    return *this;
  }

  static gen globalize(const gen & g){
    gen tmp(g);
    switch (tmp.type){
    case _IDNT:
      tmp.subtype=_GLOBAL__EVAL;
      break;
    case _VECT:
      tmp=apply(tmp,globalize);
      break;
    case _SYMB:
      if (tmp._SYMBptr->sommet!=at_program)
	tmp=symbolic(tmp._SYMBptr->sommet,globalize(tmp._SYMBptr->feuille));
      break;
    }
    return tmp;
  }

  // make g identificateurs evaluated as global
  gen global_eval(const gen & g,int level){
    if (g.type<_IDNT)
      return g;
    bool save_local_eval=local_eval(context0);
    local_eval(false,context0);
    gen tmp;
#ifndef NO_STDEXCEPT
    try {
#endif
      tmp=g.eval(level,context0);
#ifndef NO_STDEXCEPT
    }
    catch (std::runtime_error & e){
      cerr << e.what() << endl;
      // eval_level(level,contextptr);
    }
#endif
    local_eval(save_local_eval,context0);
    return globalize(tmp);
  }

  bool check_not_assume(const gen & not_evaled,gen & evaled, bool evalf_after,const context * contextptr);

  // make g identificateurs evaluated as global
  gen global_evalf(const gen & g,int level){
    if (g.type<_IDNT)
      return g;
    bool save_local_eval=local_eval(context0);
    local_eval(false,context0);
    gen tmp;
#ifndef NO_STDEXCEPT
    try {
#endif
      tmp=g.eval(level,context0);
      if (tmp.type==_IDNT){
	gen evaled(tmp._IDNTptr->eval(level,tmp,context0));
	if (check_not_assume(tmp,evaled,true,context0))
	  tmp=evaled;
      }
#ifndef NO_STDEXCEPT
    }
    catch (std::runtime_error & e){
      cerr << e.what() << endl;
      // eval_level(level,contextptr);
    }
#endif
    local_eval(save_local_eval,context0);
    return globalize(tmp);
  }

  gen _prod(const gen & args,GIAC_CONTEXT);
  static bool eval_38(int level,const gen & orig,gen & res,const char * s,GIAC_CONTEXT){
    if (rcl_38 && rcl_38(res,0,s,undef,false,contextptr)){
      return true;
    }
    return false;
    size_t ss=strlen(s);
#ifdef ASPEN_GEOMETRY
    if (
	(ss>1 && s[0]=='G')
#ifndef CAS38_DISABLED
	|| (ss==1 && s[0]>='a' && s[0]<='z')
#endif
	){ // checking for a geometry global variables
      if (contextptr){
	sym_tab::const_iterator it=contextptr->globalcontextptr->tabptr->find(s);
	if (it!=contextptr->globalcontextptr->tabptr->end()){
	  res=it->second;
	  return true;
	}
      }
      res=orig;
      return false;
    }
#endif
    if (calc_mode(contextptr)!=38 || !strcmp(s,string_pi) || !strcmp(s,string_euler_gamma) || !strcmp(s,string_infinity) || !strcmp(s,string_undef)){
      res=orig;
      return false;
    }
    if (ss<=1){
      if (s[0]>'Z'){
	res=orig;
	return false;
      }
      res=0.0;
      return true;
    }
    if (ss==2 && s[1]<='9') {
      res=orig;
      gen tmp,evaled;
      switch(s[0]){
      case 'C': case 'L':
	res=gen(vecteur(0),_LIST__VECT);
	break;
      case 'E': case 'H': /* case 'S': */
	return false;
      case 'G': // FIXME: grob
	return false;
      case 'F': case 'R': case 'U': case 'X': case 'Y':
	if (calc_mode(contextptr)==38)
	  res=gensizeerr(gettext("Function not defined"));
	return true;
      case 'M':
	res=makevecteur(makevecteur(0));
	break;
      case 'V':
	res=makevecteur(0);
	break;	
      case 'Z':
	res=0.0;
	break;
      case 'i':
	res=(s[1]-'0')*cst_i;
	break;
      case 'e':
	res=(s[1]-'0')*std::exp(1.0);
	break;
      default:
	tmp=identificateur(string(1,s[0]));
	if (tmp._IDNTptr->in_eval(1,tmp,evaled,contextptr))
	  res=(s[1]-'0')*evaled;
	else
	  res=0.0;
      }
      return true;
    }
    char ch;
    for (size_t i=0;i<ss;++i){
      ch=s[i];
      if ( (ch>'Z' && ch!='i' && ch!='e')|| ch<'0'){
	res=orig;
	return false;
      }
    }
    // all chars are regular 38 characters, split as a product
    vecteur args;
    gen g;
    for (size_t i=0;i<ss;++i){
      ch=s[i];
      if (ch=='C' || (ch>='E' && ch<='H') || ch=='L' || ch=='M' || ch=='R' 
	  /* || ch=='S' */
	  || ch=='U' || ch=='V' || (ch>='X' && ch<='Z')){
	string name;
	name += ch;
	char c=0;
	if (i<ss-1)
	  c=s[i+1];
	if (c>='0' && c<='9'){
	  name += c;
	  ++i;
	}
	g=identificateur(name);
	g=g.eval(level,contextptr);
	args.push_back(g);
      }
      else {
	string coeff;
	for (++i;i<ss;++i){
	  if (s[i]>32 && isalpha(s[i])){
	    --i;
	    break;
	  }
	  coeff += s[i];
	}
	if (coeff.empty())
	  g=1;
	else
	  g=atof(coeff.c_str());
	if (ch=='i')
	  g=g*cst_i;
	else {
	  if (ch=='e')
	    g=std::exp(1.0)*g;
	  else {
	    coeff="";
	    coeff += ch;
	    gen tmp=identificateur(coeff),evaled;
	    if (tmp._IDNTptr->in_eval(1,tmp,evaled,contextptr))
	      g=g*evaled;
	    else
	      g=0.0;
	  }
	}
	args.push_back(g);
      }
    }
    res=_prod(args,contextptr);
    return true;
  }

  gen identificateur::eval(int level,const gen & orig,const context * contextptr) {
    if (!ref_count)
      return orig;
    // cerr << "idnt::eval " << *this << " " << level << endl;
    if (level<=0){
      if (level==0) 
	return orig;
      if (contextptr){
	sym_tab::const_iterator it=contextptr->tabptr->find(id_name),itend=contextptr->tabptr->end();
	if (it!=itend)
	  return it->second;
	if (abs_calc_mode(contextptr)==38){
	  gen evaled;
	  if (eval_38(level,orig,evaled,id_name,contextptr))
	    return evaled;
	}
	return orig;
      }
      else {
	if (!localvalue || localvalue->empty())
	  return orig;
	iterateur jtend=localvalue->end();
	return (protection_level>(jtend-2)->val)?localvalue->back():orig;
      }
    }
    --level;
    gen evaled;
    if (in_eval(level,orig,evaled,contextptr))
      return evaled;
    else
      return *this;
    /*
    int save_level=eval_level(contextptr);
    eval_level(level,contextptr);
    gen res=in_eval(level,contextptr);
    eval_level(save_level,contextptr);
    return res;
    */
  }

  // if globalize is true, use global value in eval
  gen do_local_eval(const identificateur & i,int level,bool globalize) {
    if (!i.localvalue)
      return i;
    gen res;
    iterateur jtend=i.localvalue->end();
    if (protection_level>(jtend-2)->val)
      res=i.localvalue->back();
    else {
      for (iterateur jt=i.localvalue->begin();;){
	if (jt==jtend)
	  break;
	--jtend;
	--jtend;
	if (protection_level>jtend->val){
	  ++jtend;
	  ++jtend;
	  break;
	}
      }
      i.localvalue->erase(jtend,i.localvalue->end());
      if (!i.localvalue->empty())
	res=i.localvalue->back();
    }
    return globalize?global_eval(res,level):res; 
  }

  bool identificateur::in_eval(int level,const gen & orig,gen & evaled,const context * contextptr, bool No38Lookup) {
    // if (!ref_count) return false; // does not work for cst ref identificateur
    if (contextptr){
      const context * cur=contextptr;
      for (;cur->previous;cur=cur->previous){
	sym_tab::const_iterator it=cur->tabptr->find(id_name);
	if (it!=cur->tabptr->end()){
	  if (!it->second.in_eval(level,evaled,contextptr->globalcontextptr))
	    evaled=it->second;
	  return true;
	}
      }
      // now at global level
      // check for quoted
      if (cur->quoted_global_vars && !cur->quoted_global_vars->empty() && equalposcomp(*cur->quoted_global_vars,orig)) 
	return false;
      sym_tab::const_iterator it=cur->tabptr->find(id_name);
      if (it==cur->tabptr->end()){
        if (No38Lookup) return false;
	if (sto_38 && abs_calc_mode(contextptr)==38)
	  return eval_38(level,orig,evaled,id_name,contextptr);
	return false;
      }
      else {
	if (!it->second.in_eval(level,evaled,contextptr->globalcontextptr))
	  evaled=it->second;
	return true;
      }
      if (!No38Lookup && sto_38){ //  && abs_calc_mode(contextptr)==38)
	if (eval_38(level,orig,evaled,id_name,contextptr))
	  return true;
      }
    }
    if (local_eval(contextptr) && localvalue && !localvalue->empty()){
      evaled=do_local_eval(*this,level,true);
      return true;
    }
    if (quoted && *quoted & 1)
      return false;
    if (current_folder_name.type==_IDNT && current_folder_name._IDNTptr->value && current_folder_name._IDNTptr->value->type==_VECT){
      evaled=find_in_folder(*current_folder_name._IDNTptr->value->_VECTptr,orig);
      return (evaled!=orig);
    }
    if (value){
      evaled=value->eval(level,contextptr);
      return true;
    }
    // look in current directory for a value
    if ( secure_run || (!variables_are_files(contextptr)) 
#ifndef __MINGW_H
	 || (access((name()+string(cas_suffixe)).c_str(),R_OK))
#endif
	 ){
      evaled=orig;
      if (!local_eval(contextptr))
	evaled.subtype=_GLOBAL__EVAL;
      return true;
    }
    // set current value
    ifstream inf((name()+string(cas_suffixe)).c_str());
    evaled=read1arg_from_stream(inf,contextptr);
    if (child_id)
      return true;
    value = new gen(evaled);
    evaled=evaled.eval(level,contextptr);
    return true;
  }
  
  void identificateur::push(int protection,const gen & e){
    if (!localvalue)
      localvalue=new vecteur;
    localvalue->push_back(protection);
    localvalue->push_back(e);
  }

  const char * identificateur::print (GIAC_CONTEXT) const{
    if (!strcmp(id_name,string_pi)){
      if (abs_calc_mode(contextptr)==38)
	return "π";
      switch (xcas_mode(contextptr)){
      case 1:
	return "Pi";
      case 2:
	return "PI";
      default:
	return string_pi;
      }
    }
    // index != sqrt(-1) wich has different notations
    if (xcas_mode(contextptr)==0){
      if (strcmp(id_name,"i")==0)
	return "i_i";
    }
    else {
      if (strcmp(id_name,"I")==0)
	return "i_i";
    }
    /*
    if (!localvalue->empty())
      return string("_") + *name ;        
    if (value)
      return string("~") + *name ;
    else
    */
    return id_name  ;
  }

  ostream & operator << (ostream & os,const identificateur & s) { return os << s.print(context0);}

  int removecomments(const char * ss,char * ss2){
    int j=0,k=0;
    for (;ss[j];j++){
      if (ss[j]=='#'){
	ss2[k]=char(0); // end ss2 string
	break;
      }
      if (ss[j]>31){ // supress control chars
	ss2[k]=ss[j];
	k++;
      }
    }
    return k;
  }

  void identificateur::unassign(){
    if (value){
      delete(value);
      value = NULL;
    }
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
