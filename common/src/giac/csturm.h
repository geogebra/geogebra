// -*- mode:C++ ; compile-command: "g++ -I.. -g -c csturm.cc" -*-
/*
 *  Copyright (C) 2007 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#ifndef _GIAC_CSTURM_H
#define _GIAC_CSTURM_H
#include "first.h"
#include <complex>
#include <iostream>
#include "modpoly.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  // P(x)->P(a*x)
  void change_scale(modpoly & p,const gen & l);
  modpoly linear_changevar(const modpoly & p,const gen & a,const gen & b);
  modpoly inv_linear_changevar(const modpoly & p,const gen & a,const gen & b);
  // find rectangle limits, defined by boundaries complex a and b
  void ab2a0b0a1b1(const gen & a,const gen & b,gen & a0,gen & b0,gen & a1,gen & b1,GIAC_CONTEXT);
  // Find one complex root inside a0,b0->a1,b1, return false if not found
  // algo: Newton method in exact mode starting from center
  bool newton_complex_1root(const modpoly & P,const gen & a0,const gen & b0,const gen & a1,const gen & b1,vecteur & complexroots,double eps);
  // round x to n bits precision
  void round2(gen & x,int n);

  // compute Sturm sequence of r0 and r1,
  // returns gcd (without content)
  // and compute list of quotients, coeffP, coeffR
  // such that coeffR*r_(k+2) = Q_k*r_(k+1) - coeffP_k*r_k
  gen csturm_seq(modpoly & r0,modpoly & r1,vecteur & listquo,vecteur & coeffP, vecteur & coeffR,GIAC_CONTEXT);

  // number of complex roots of p in a..b, returns -1 if a factor
  // has been found
  int csturm_square(const gen & p,const gen & a,const gen & b,gen& pgcd,GIAC_CONTEXT);

  // Find complex roots of P in a0,b0 -> a1,b1
  // Returns false if a factor of P has been found (->in pgcd)
  bool complex_roots(const modpoly & P,const gen & a0,const gen & b0,const gen & a1,const gen & b1,vecteur & roots,gen & pgcd,double eps);
  // find roots of polynomial P at precision eps using complex Sturm sequences
  // P must have numeric coefficients, in Q[i]
  vecteur complex_roots(const modpoly & P,const gen & a0,const gen & b0,const gen & a1,const gen & b1,bool complexe,double eps);

  vecteur keep_in_rectangle(const vecteur & croots,const gen A0,const gen & B0,const gen & A1,const gen & B1,bool embed,GIAC_CONTEXT);

  gen complexroot(const gen & g,bool complexe,GIAC_CONTEXT);
  gen _complexroot(const gen & g,GIAC_CONTEXT);
  gen _realroot(const gen & g,GIAC_CONTEXT);
  vecteur crationalroot(polynome & p,bool complexe);
  gen _crationalroot(const gen & g,GIAC_CONTEXT);
  gen _rationalroot(const gen & g,GIAC_CONTEXT);

  vecteur symb2poly_num(const gen & g,GIAC_CONTEXT);
  // isolate and find real roots of P at precision eps between a and b
  // returns a list of intervals or of rationals
  bool vas(const modpoly & P,const gen & a,const gen &b,double eps,vecteur & vasres,bool with_mult,GIAC_CONTEXT);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_CSTURM_H
