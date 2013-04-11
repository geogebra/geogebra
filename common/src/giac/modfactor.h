// -*- mode:C++ -*-
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

#ifndef _GIAC_MODFACTOR_H_
#define _GIAC_MODFACTOR_H_
#include "first.h"
#include "global.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  template<class T> class tensor;
  typedef tensor<gen> polynome;
  typedef vecteur modpoly;
  typedef vecteur dense_POLY1; // same internal rep but assumes non modular op

  // **************************************************************
  // factorization utilities
  // to be used to factor a square-free unitary mod polynomial
  // assuming modulo is prime (and not too large, must fit in int)
  // *************************************************************
  void intersect(std::vector<bool>::iterator tab, std::vector<bool>::iterator othertab,int size) ;
  int sigma(const std::vector<bool> & deg);

  // v[i]=x^(p*i) mod q
  // matrix of the v[i] for i=0..jstart or i=0..degree(q) if jstart=0
  void qmatrix(const modpoly & q,environment * env,std::vector<modpoly> & v,int jstart=0);  
  // compute s(x)=r(x^p) mod q using the q-matrix
  void xtoxpowerp(const modpoly & r, const std::vector<modpoly> & v,environment * env,int qsize,modpoly & s);
  // find modular roots and linear factors
  bool roots(const modpoly & q, environment * env,vecteur & v,std::vector<modpoly> & w);
  // Find linear factors of q in Z or Z[i] depending on env->complexe
  int do_linearfind(const polynome & q,environment * env,polynome & qrem,vectpoly & v,vecteur & croots,int & i);
  // find linear factor if NTL not installed
  int linearfind(const polynome & q,environment * env,polynome & qrem,vectpoly & v,int &ithprime);
  // distinct degree modular factorization
  bool ddf(const modpoly & q,const std::vector<modpoly> & qmat,environment *env,std::vector< facteur<modpoly> >& v);
  // split a polynomial ddfactor into factors of same degree i
  bool cantor_zassenhaus(const modpoly & ddfactor,int i,const std::vector<modpoly> & qmat, environment * env,std::vector<modpoly> & v);
  bool cantor_zassenhaus(const std::vector< facteur<modpoly> > & v_in,const std::vector<modpoly> & qmat, environment * env,std::vector<modpoly> & v);
  // number of factors of a ddf factorization
  int nfact(const std::vector< facteur<modpoly> > & v,bool * possible_degrees , int maxdeg);

  // Landau-Mignotte bound
  gen mignotte_bound(const dense_POLY1 & p);
  gen mignotte_bound(const polynome & p);

  // lift factorization from Z/pZ to Z/p^kZ for a sufficiently large k
  // modulo is modified to modulo^k
  bool liftl(environment * env,dense_POLY1 & q,gen &bound,std::vector<modpoly> & v_in,vectpoly & v_out);
  // given a factorization v_in of q in Z/p^kZ find a factorization v_out 
  // over Z, k is the minimal # of factors of v_in to be combined
  void combine(const dense_POLY1 & q, const std::vector<modpoly> & v_in,environment * env,vectpoly & v_out,std::vector<bool> & possible_degrees, int k=1);

  bool factorunivsqff(const polynome & q,environment * env,vectpoly & v,int & ithprime,int debug,int modfactor_primes);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // NO_NAMESPACE_GIAC

#endif // _GIAC_MODFACTOR_H
