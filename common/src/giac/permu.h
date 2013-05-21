// -*- mode:C++ ; compile-command: "g++ -I.. -g -c permu.cc" -*-
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

#ifndef _GIAC_PERMU_H_
#define _GIAC_PERMU_H_
#include "first.h"
#include "global.h"
#include "gen.h"
#include "unary.h"
#include "symbolic.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  gen _trn(const gen & args,GIAC_CONTEXT);

  gen _sizes(const gen & args,GIAC_CONTEXT);
  gen _permuorder(const gen & args,GIAC_CONTEXT);
  gen _randperm(const gen & args,GIAC_CONTEXT);
  gen _is_permu(const gen & args,GIAC_CONTEXT);
  gen _is_cycle(const gen & args,GIAC_CONTEXT);
  gen _cycle2perm(const gen & args,GIAC_CONTEXT);
  gen _p1op2(const gen & args,GIAC_CONTEXT);
  gen _c1oc2(const gen & args,GIAC_CONTEXT);
  gen _c1op2(const gen & args,GIAC_CONTEXT);
  gen _p1oc2(const gen & args,GIAC_CONTEXT);
  gen _cycles2permu(const gen & args,GIAC_CONTEXT);
  gen _permu2cycles(const gen & args,GIAC_CONTEXT);
  gen _perminv(const gen & args,GIAC_CONTEXT);
  gen _cycleinv(const gen & args,GIAC_CONTEXT);
  gen _signature(const gen & args,GIAC_CONTEXT);

  gen square_hadamard_bound(const matrice & m);
  gen l2norm2(const gen & g);
  gen _randperm(const gen & args,GIAC_CONTEXT);

  vecteur vector_double_2_vecteur(const std::vector<double> & v);
  vecteur vectvector_double_2_vecteur(const std::vector< std::vector<double> > & v);
  vecteur vector_int_2_vecteur(const std::vector<int> & v);
  vecteur vector_int_2_vecteur(const std::vector<int> & v,GIAC_CONTEXT);
  std::vector<int> vecteur_2_vector_int(const vecteur & v);
  std::vector< std::vector<int> > vecteur_2_vectvector_int(const vecteur & v);
  vecteur vectvector_int_2_vecteur(const std::vector< std::vector<int> > & v);
  std::vector<int> sizes(const std::vector< std::vector<int> > & v);

  std::vector<int> randperm(const int & n);
  bool is_permu(const vecteur &p,std::vector<int> & p1,GIAC_CONTEXT);
  bool is_cycle(const vecteur & c,std::vector<int> & c1,GIAC_CONTEXT);
  int signature(const std::vector<int> & p) ;
  std::vector< std::vector<int> > permu2cycles(const std::vector<int> & p) ;
  std::vector<int> cycle2permu(const std::vector< std::vector<int> > & c);
  std::vector<int> cycle2perm(const std::vector<int> & c) ;
  std::vector< std::vector<int> > permu2cycles(const std::vector<int> & p) ;
  std::vector<int> cycleinv(const std::vector<int> & c);
  std::vector<int> perminv(const std::vector<int> & p);
  int signature(const std::vector<int> & p);
  std::vector<int> p1op2(const std::vector<int> & p1,const std::vector<int> & p2);
  std::vector<int> c1oc2(const std::vector<int> & c1,const std::vector<int> & c2);
  std::vector<int> c1op2(const std::vector<int> & c1, const std::vector<int> & p2);
  std::vector<int> p1oc2(const std::vector<int> & p1, const std::vector<int> & c2);

  gen _hilbert(const gen & args,GIAC_CONTEXT);
  // arithmetic mean column by column  gen l2norm2(const gen & g);
  gen square_hadamard_bound(const matrice & m);
  gen _hadamard(const gen & args,GIAC_CONTEXT);
  gen _trn(const gen & args,GIAC_CONTEXT);
  gen _syst2mat(const gen & args,GIAC_CONTEXT);
  gen _vandermonde(const gen & args,GIAC_CONTEXT);
  gen _laplacian(const gen & args,GIAC_CONTEXT);
  gen _hessian(const gen & args,GIAC_CONTEXT);
  gen _divergence(const gen & args,GIAC_CONTEXT);
  gen _curl(const gen & args,GIAC_CONTEXT);
  bool find_n_x(const gen & args,int & n,gen & x,gen & a);

  std::vector< std::vector<int> > groupermu(const std::vector<int> & p1,const std::vector<int> & p2) ;
  gen _groupermu(const gen & args,GIAC_CONTEXT);
  gen _nextperm(const gen & args,GIAC_CONTEXT);
  gen _prevperm(const gen & args,GIAC_CONTEXT);
  gen _split(const gen & args,GIAC_CONTEXT);
  gen _sum_riemann(const gen & args,GIAC_CONTEXT);

  vecteur mean(const matrice & m,bool column=true);
  vecteur stddev(const matrice & m,bool column=true,int variance=1);
  matrice ascsort(const matrice & m,bool column=true);

  vecteur hermite(int n);
  gen _divergence(const gen &,GIAC_CONTEXT);  
  gen _hermite(const gen & args,GIAC_CONTEXT);
  gen _laguerre(const gen & args,GIAC_CONTEXT);
  vecteur tchebyshev1(int n);
  gen _tchebyshev1(const gen & args,GIAC_CONTEXT);
  vecteur tchebyshev2(int n);
  gen _tchebyshev2(const gen & args,GIAC_CONTEXT);
  vecteur legendre(int n);
  gen _legendre(const gen & args,GIAC_CONTEXT);
  gen _curl(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_curl;

  gen _permu2mat(const gen & args,GIAC_CONTEXT); // permutation vector -> matrix


#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // NO_NAMESPACE_GIAC

#endif // _GIAC_PERMU_H
