// -*- mode:C++ -*-
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

#ifndef _GIAC_IFACTOR_H_
#define _GIAC_IFACTOR_H_
#include "first.h"
#include "global.h"
#include "gen.h"
#include "unary.h"
#include "symbolic.h"
#include "identificateur.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  inline unsigned sizeinbase2(unsigned n){
    unsigned i=0;
    for (;n;++i){
      n >>= 1;
    }
    return i;
  }

  extern const short int giac_primes[];
  gen _ithprime(const gen & args,GIAC_CONTEXT);
  gen _ifactors(const gen & args,GIAC_CONTEXT);
  gen _maple_ifactors(const gen & args,GIAC_CONTEXT);

  symbolic symb_ifactor(const gen & args);
  vecteur ifactors(const gen & n0,GIAC_CONTEXT);
  gen ifactors(const gen & args,int maplemode,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ifactors;
  extern const unary_function_ptr * const  at_maple_ifactors;

  vecteur factors(const gen & g,const gen & x,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_factors;
  gen _factors(const gen & args,GIAC_CONTEXT);

  gen _divis(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_divis ;

  gen idivis(const gen & n,GIAC_CONTEXT);
  gen _idivis(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_idivis ;
  int modulo(const mpz_t & a,unsigned b);

  vecteur pfacprem(gen & n,bool addlast,GIAC_CONTEXT);

  gen ifactor(const gen & n,GIAC_CONTEXT);
  gen _ifactor(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ifactor ;

  gen euler(const gen & e,GIAC_CONTEXT);
  gen _euler(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_euler;

  gen pa2b2(const gen & p,GIAC_CONTEXT);
  gen _pa2b2(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_pa2b2 ;
 
  gen _propfrac(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_propfrac ;

  gen iabcuv(const gen & a,const gen & b,const gen & c);
  gen _iabcuv(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_iabcuv ;

  gen abcuv(const gen & a,const gen & b,const gen & c,const gen & x,GIAC_CONTEXT);
  gen _abcuv(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_abcuv ;

  gen simp2(const gen & a,const gen & b,GIAC_CONTEXT);
  gen _simp2(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_simp2 ;

  gen fxnd(const gen & a);
  gen _fxnd(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_fxnd ;

  extern const unary_function_ptr * const  at_ithprime ;

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // NO_NAMESPACE_GIAC

#endif // _GIAC_IFACTOR_H
