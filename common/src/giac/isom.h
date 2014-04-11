// -*- mode:C++ ; compile-command: "g++ -I.. -g -c isom.cc " -*- 
/*
 *  Copyright (C) 2001,2014 R. De Graeve, Institut Fourier, 38402 St Martin d'Heres
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

#ifndef _GIAC_ISOM_H
#define _GIAC_ISOM_H
#include "first.h"
#include "gen.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  vecteur isom(const vecteur & M,GIAC_CONTEXT);
  gen _isom(const gen & args,GIAC_CONTEXT);
  gen _mkisom(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_isom ;

  vecteur mkisom(const gen & n,int b,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_mkisom;

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_ISOM_H
