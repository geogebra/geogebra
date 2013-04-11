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
#ifndef _GIAC_LIN_H
#define _GIAC_LIN_H
#include "first.h"
#include "vector.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  class gen;
  bool contains(const gen & e,const unary_function_ptr & mys);
  inline bool contains(const gen & e,const unary_function_ptr * mys){ return contains(e,*mys); }
  void compress(vecteur & res,GIAC_CONTEXT);

  gen prod_expand(const gen & a,const gen & b,GIAC_CONTEXT);

  gen unlin(vecteur & v,GIAC_CONTEXT);
  void convolution(const gen & coeff, const gen & arg,const vecteur & w,vecteur & res,GIAC_CONTEXT);
  void convolution(const vecteur & v,const vecteur & w, vecteur & res,GIAC_CONTEXT);
  void convolutionpower(const vecteur & v,int k,vecteur & res,GIAC_CONTEXT);
  void lin(const gen & e,vecteur & v,GIAC_CONTEXT);

  symbolic symb_lin(const gen & e);
  gen _lin(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_lin ;
  gen lnexpand(const gen & e,GIAC_CONTEXT);

  vecteur tchebycheff(int n,bool first_kind=true);
  gen tunlin(vecteur & v,GIAC_CONTEXT);
  void tconvolution(const gen & coeff, const gen & arg,const vecteur & w,vecteur & res,GIAC_CONTEXT);
  void tconvolution(const vecteur & v,const vecteur & w, vecteur & res,GIAC_CONTEXT);
  void tconvolutionpower(const vecteur & v,int k,vecteur & res,GIAC_CONTEXT);
  void tlin(const gen & e,vecteur & v,GIAC_CONTEXT);

  symbolic symb_tlin(const gen & e);
  gen _tlin(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_tlin ;

  symbolic symb_texpand(const gen & e);
  gen _texpand(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_texpand ;

  symbolic symb_expand(const gen & e);
  gen expand(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_expand ;
  vecteur tchebycheff(int n,bool first_kind);
  gen ln_expand(const gen & e0,GIAC_CONTEXT);
  gen symhorner(const vecteur & v, const gen & e);
  gen _texpand(const gen & args,GIAC_CONTEXT);
  gen expexpand(const gen & e,GIAC_CONTEXT);
  gen lnexpand(const gen & e,GIAC_CONTEXT);
  gen trigexpand(const gen & e,GIAC_CONTEXT);


#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC


#endif // _GIAC_LIN_H
