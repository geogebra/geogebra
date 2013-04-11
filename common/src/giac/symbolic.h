// -*- mode:C++ ; compile-command: "g++ -I.. -g -c symbolic.cc" -*-
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
#ifndef _GIAC_SYMBOLIC_H
#define _GIAC_SYMBOLIC_H
#include "first.h"
#include <iostream>
#include <string>
#include "vector.h"
#include "unary.h"
#include "gen.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  const unary_function_ptr * archive_function_tab();
  extern int archive_function_tab_length;
  int archive_function_index(const unary_function_ptr & f);
  inline int archive_function_index(const unary_function_ptr * f) { return archive_function_index(*f); };

  std::ostream & operator << (std::ostream & os,const symbolic & s);
  
  int equalposcomp(const unary_function_ptr tab[],const unary_function_ptr & f);
  inline int equalposcomp(const unary_function_ptr tab[],const unary_function_ptr * f){ return equalposcomp(tab,*f);} ;
  int equalposcomp(const std::vector<const unary_function_ptr *> & v,const unary_function_ptr * f);
  int equalposcomp(const const_unary_function_ptr_ptr tab[],const unary_function_ptr & f);
  inline int equalposcomp(const const_unary_function_ptr_ptr tab[],const unary_function_ptr * f){ return equalposcomp(tab,*f);};

  // find the "size" of g but limited by max
  unsigned taille(const gen & g,unsigned max);
  extern bool print_rewrite_prod_inv;
  // try to rewrite arg the argument of a product as a fraction n/d
  bool rewrite_prod_inv(const gen & arg,gen & n,gen & d);
  std::string & add_print(std::string & s,const gen & g,GIAC_CONTEXT);

  // non recursive eval function
  gen nr_eval(const gen & g,int level,const context * ct);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_SYMBOLIC_H
