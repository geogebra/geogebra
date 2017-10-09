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
#ifndef _GIAC_DERIVE_H
#define _GIAC_DERIVE_H
#include "first.h"
#include "gen.h"
#include "identificateur.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  struct unary_function_ptr;
  gen eval_before_diff(const gen & expr,const gen & variable,GIAC_CONTEXT);
  gen derive(const gen & e,const identificateur & i,GIAC_CONTEXT);
  gen derive(const gen & e,const gen & vars,GIAC_CONTEXT);
  gen derive(const gen & e,const gen & vars,const gen & nderiv,GIAC_CONTEXT);
  gen _derive(const gen & args,GIAC_CONTEXT);
  gen _diff(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_derive ;
  extern const unary_function_ptr * const  at_grad ;
  extern const unary_function_ptr * const  at_function_diff ;
  extern const unary_function_ptr * const  at_implicit_diff ;
  extern const unary_function_ptr * const  at_domain ;
  symbolic symb_derive(const gen & a,const gen & b);
  gen symb_derive(const gen & a,const gen & b,const gen &c);
  gen _function_diff(const gen & g,GIAC_CONTEXT);
  bool depend(const gen & g,const identificateur & i);
  gen _grad(const gen & args,GIAC_CONTEXT);
  gen domain(const gen & f,const gen & x,int mode,GIAC_CONTEXT);
  gen critical(const gen & g,bool extrema_only,GIAC_CONTEXT);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_DERIVE_H
