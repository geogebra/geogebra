// -*- mode:C++ ; compile-command: "g++ -I.. -g -c desolve.cc" -*-
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
#ifndef _GIAC_DESOLVE_H
#define _GIAC_DESOLVE_H
#include "first.h"
#include "gen.h"
#include "identificateur.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  // f must be a vector obtained using factors
  // x, y are 2 idnt
  // xfact and yfact should be initialized to 1
  // return true if f=xfact*yfact where xfact depends on x and yfact on y only
  bool separate_variables(const gen & f,const gen & x,const gen & y,gen & xfact,gen & yfact,GIAC_CONTEXT);

  gen laplace(const gen & f,const gen & x,const gen & s,GIAC_CONTEXT);
  gen _laplace(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_laplace ;
  polynome cstcoeff(const polynome & p);
  gen diffeq_constante(int i,GIAC_CONTEXT);

  gen ilaplace(const gen & f,const gen & x,const gen & s,GIAC_CONTEXT);
  gen _ilaplace(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ilaplace ;

  gen desolve(const gen & f,const gen & x,const gen & y,int & ordre,vecteur & parameters,GIAC_CONTEXT);
  gen _desolve(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_desolve ;
  gen ztrans(const gen & f,const gen & x,const gen & s,GIAC_CONTEXT);
  gen _ztrans(const gen & args,GIAC_CONTEXT);
  gen invztrans(const gen & f,const gen & x,const gen & s,GIAC_CONTEXT);
  gen _invztrans(const gen & args,GIAC_CONTEXT);

  gen integrate_without_lnabs(const gen & e,const gen & x,GIAC_CONTEXT);


#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_DESOLVE_H
