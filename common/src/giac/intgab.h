// -*- mode:C++ ; compile-command: "g++ -I.. -g -c intgab.cc" -*-
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
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
#ifndef _GIAC_INTGAB_H
#define _GIAC_INTGAB_H
#include "first.h"
#include <string>

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  class gen;

  class identificateur;
  struct unary_function_ptr;

  bool assume_t_in_ab(const gen & t,const gen & a,const gen & b,bool exclude_a,bool exclude_b,GIAC_CONTEXT);
  // check whether an expression is meromorphic wrt x
  int is_meromorphic(const gen & g,const gen & x,gen &a,gen &b,gen & P,GIAC_CONTEXT);
  // 0 none, 1 even, 2 odd
  int is_even_odd(const gen & f,const gen & x,GIAC_CONTEXT);
  // residue of g at x=a
  gen residue(const gen & g,const gen & x,const gen & a,GIAC_CONTEXT);
  gen _residue(const gen & args,GIAC_CONTEXT);


  // if true put int(g,x=a..b) into res
  bool intgab(const gen & g,const gen & x,const gen & a,const gen & b,gen & res,GIAC_CONTEXT);
  // if true put int(g,x=a..b) into res
  bool sumab(const gen & g,const gen & x,const gen & a,const gen & b,gen & res,bool testi,GIAC_CONTEXT);

  // singular values of g wrt x (in complex plan)
  vecteur singular(const gen & g,const gen & x,GIAC_CONTEXT);
  gen _singular(const gen & args,GIAC_CONTEXT);
  // check whether P has only integer roots
  bool is_admissible_poly(const polynome & P,int & deg,polynome & lcoeff,vecteur & roots,GIAC_CONTEXT);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC


#endif // _GIAC_INTGAB_H
