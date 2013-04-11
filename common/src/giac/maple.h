// -*- mode:C++ ; compile-command: "g++ -I.. -g -c maple.cc" -*-
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
#ifndef _GIAC_MAPLE_H
#define _GIAC_MAPLE_H
#include "first.h"
#ifndef HAVE_PNG_H
#undef HAVE_LIBPNG
#endif
#ifdef HAVE_LIBPNG
#include <png.h>
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  class gen;

  gen _about(const gen & g,GIAC_CONTEXT);
  gen _zip(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_zip ;
  gen _accumulate_head_tail(const gen & args,GIAC_CONTEXT);
  gen _divide(const gen & g,GIAC_CONTEXT);
  gen _ratnormal(const gen & g,GIAC_CONTEXT);
  gen _about(const gen & g,GIAC_CONTEXT);
  gen _inverse(const gen & a_orig,GIAC_CONTEXT);
  gen _Inverse(const gen & g,GIAC_CONTEXT);
  gen _igcdex(const gen & a_orig,GIAC_CONTEXT);
  gen _gcdex(const gen & a_orig,GIAC_CONTEXT);
  gen _revlist(const gen & a,GIAC_CONTEXT);
  gen _restart(const gen & args,GIAC_CONTEXT);
  gen _restart_modes(const gen & args,GIAC_CONTEXT);
  gen _time(const gen & a,GIAC_CONTEXT);
  gen _cat(const gen & a_orig,GIAC_CONTEXT);
  gen _pivot(const gen & a_orig,GIAC_CONTEXT);
  gen _rowspace(const gen & g,GIAC_CONTEXT);
  gen _colspace(const gen & g,GIAC_CONTEXT);
  gen _copy(const gen & g,GIAC_CONTEXT);
  gen _row(const gen & g,GIAC_CONTEXT);
  gen _col(const gen & g,GIAC_CONTEXT);
  gen _count(const gen & args,const context * contextptr);
  gen _count_eq(const gen & args,const context * contextptr);
  gen _count_sup(const gen & args,GIAC_CONTEXT);
  gen _count_inf(const gen & args,GIAC_CONTEXT);
  gen _trunc(const gen & args,GIAC_CONTEXT);
  gen _evalc(const gen & g,GIAC_CONTEXT);
  gen _open(const gen & g,GIAC_CONTEXT);
  gen _fopen(const gen & g,GIAC_CONTEXT);
  gen _fprint(const gen & g,GIAC_CONTEXT);
  gen _close(const gen & g,GIAC_CONTEXT);
  gen _blockmatrix(const gen & g,GIAC_CONTEXT);
  gen _delcols(const gen & g,GIAC_CONTEXT);
  gen _delrows(const gen & g,GIAC_CONTEXT);
  gen _JordanBlock(const gen & g,GIAC_CONTEXT);
  gen _companion(const gen & g,GIAC_CONTEXT);
  gen _border(const gen & g,GIAC_CONTEXT);
  gen _pade(const gen & g,GIAC_CONTEXT);
  gen _lhs(const gen & g,GIAC_CONTEXT);
  gen _rhs(const gen & g,GIAC_CONTEXT);
  gen _reverse_rsolve(const gen & g,GIAC_CONTEXT);
  gen fft(const gen & g_orig,int direct,GIAC_CONTEXT);
  gen _fft(const gen & g,GIAC_CONTEXT);
  gen _ifft(const gen & g,GIAC_CONTEXT);
  gen _Resultant(const gen & g,GIAC_CONTEXT);
  gen _Nullspace(const gen & g,GIAC_CONTEXT);
  gen _assign(const gen & g,GIAC_CONTEXT);
  gen _implicitplot3d(const gen & g,GIAC_CONTEXT);
  gen _readwav(const gen & g,GIAC_CONTEXT);
  gen _writewav(const gen & g,GIAC_CONTEXT);
  gen _animate(const gen & g,GIAC_CONTEXT);
  gen _animate3d(const gen & g,GIAC_CONTEXT);
  gen _even(const gen & g,GIAC_CONTEXT);
  gen _odd(const gen & g,GIAC_CONTEXT);
  gen _writergb(const gen & g,GIAC_CONTEXT);
  gen _readrgb(const gen & g,GIAC_CONTEXT);


#ifdef HAVE_LIBPNG
  int write_png(const char *file_name, png_bytep *rows, int w, int h, int colortype, int bitdepth);
#endif
  extern bool (* readrgb_ptr)(const std::string & s,int W,int H,gen & res);

  gen linear_apply(const gen & e,const gen & x,const gen & l,gen & remains, GIAC_CONTEXT, gen (* f)(const gen &,const gen &,const gen &,gen &,const context *));
  // product(P,n=a..b) where the first variable in v is n
  gen product(const polynome & P,const vecteur & v,const gen & n,const gen & a,const gen & b,GIAC_CONTEXT);
  // discrete product primitive of a polynomial P
  gen product(const polynome & P,const vecteur & v,const gen & n,gen & remains,GIAC_CONTEXT);
  gen _seqsolve(const gen & args,GIAC_CONTEXT);
  gen _rsolve(const gen & args,GIAC_CONTEXT);
  gen _array(const gen & g,GIAC_CONTEXT);
  gen _makemod(const gen & args,GIAC_CONTEXT);
  gen _hexprint(const gen & g,GIAC_CONTEXT);
  gen _octprint(const gen & g,GIAC_CONTEXT);
  gen _binprint(const gen & g,GIAC_CONTEXT);

  std::string cprint(const gen & args,GIAC_CONTEXT);

  extern const unary_function_ptr * const  at_accumulate_head_tail ;
  extern const unary_function_ptr * const  at_gcdex ;
  extern const unary_function_ptr * const  at_seqsolve ;
  extern const unary_function_ptr * const  at_array ;
  extern const unary_function_ptr * const  at_test_alias ;
  extern const unary_function_ptr * const  at_binprint ;
  extern const unary_function_ptr * const  at_count;
  extern const unary_function_ptr * const  at_time;

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC


#endif // _GIAC_MAPLE_H
