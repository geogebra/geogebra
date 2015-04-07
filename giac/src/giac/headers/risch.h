// -*- mode:C++ ; compile-command: "g++ -I.. -g -c risch.cc" -*-
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
#ifndef _GIAC_RISCH_H
#define _GIAC_RISCH_H
#include "first.h"
#include <string>
#include "vector.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  class gen;
  class identificateur;
  struct unary_function_ptr;
  
  gen risch(const gen & e_orig,const identificateur & x,gen & remains_to_integrate,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_risch;
  gen _risch(const gen & g,GIAC_CONTEXT);

  // integer roots of a polynomial
  vecteur iroots(const polynome & p);
#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC


#endif // _GIAC_RISCH_H
