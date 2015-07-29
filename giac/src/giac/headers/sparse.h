// -*- mode:C++ ; compile-command: "g++ -I.. -g -c sparse.cc" -*-
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
#ifndef _GIAC_SPARSE_H
#define _GIAC_SPARSE_H
#include "first.h"
#include "index.h"
#include "gen.h"
#include <complex>
#include <iostream>

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  // sparse matrices, not too sparse
  struct smatrix {
    matrice m;
    std::vector< std::vector<int> > pos;
    smatrix(const matrice & m_,const std::vector< std::vector<int> > & v):m(m_),pos(v) {};
    smatrix(){};
    void dbgprint() const;
    int size() const { return giacmin(int(m.size()),int(pos.size())); }
    int ncols() const;
  };

  struct fmatrix {
    std::vector< std::vector<giac_double> > m;
    std::vector< std::vector<int> > pos;
    fmatrix(const std::vector< std::vector<giac_double> > & m_,const std::vector< std::vector<int> > & v):m(m_),pos(v) {};
    fmatrix(){};
    void dbgprint() const;
    int size() const { return giacmin(int(m.size()),int(pos.size())); }
    int ncols() const;
  };

  bool convert(const gen_map & d,smatrix & s);
  bool convert(const smatrix & s,gen_map & d);
  bool convert(const gen_map & g,vecteur & res);
  bool convert(const vecteur & m,gen_map & res);
  bool convert(const gen_map & d,fmatrix & s);
  bool convert(const fmatrix & s,gen_map & d);
  bool convert(const vecteur & source,std::vector<giac_double> & target);
  vecteur convert(const std::vector<giac_double> & v);

  void sparse_trim(const gen_map & d,gen_map &c);
  bool need_sparse_trim(const gen_map & d);

  void sparse_add(const gen_map & a,const gen_map & b,gen_map & c);
  void sparse_neg(gen_map & c);
  void sparse_sub(const gen_map & a,const gen_map & b,gen_map & c);
  void sparse_mult(const gen & x,gen_map & c);
  void sparse_div(gen_map & c,const gen & x);
  bool sparse_mult(const gen_map & a,const gen_map & b,gen_map & c);
  void sparse_trn(const gen_map & c,gen_map & t,bool trn,GIAC_CONTEXT);
  void map_apply(const gen_map & a,gen_map & t,GIAC_CONTEXT,gen (* f) (const gen &,GIAC_CONTEXT) );
  void map_apply(const gen_map & a,const unary_function_ptr & f,gen_map & t,GIAC_CONTEXT);
  // returns false if dimension do not match
  bool sparse_mult(const gen_map & a,const vecteur & b,gen_map & c);
  bool sparse_mult(const vecteur & a,const gen_map & b,gen_map & c);

  bool sparse_lu(const gen_map & a,std::vector<int> & p,gen_map & l,gen_map & u_);

  void sparse_mult(const smatrix & a,const vecteur & b,vecteur & c);
  void sparse_mult(const vecteur & v,const smatrix & a,vecteur & c);
  void sparse_mult(const std::vector<double> & v,const fmatrix & m,std::vector<double> & c);
  void sparse_mult(const fmatrix & a,const std::vector<giac_double> & b,std::vector<giac_double> & c);
  double l2norm(const std::vector<giac_double> & v);
  void addvecteur(const std::vector<giac_double> & a,const std::vector<giac_double> & b,std::vector<giac_double> & c);
  void subvecteur(const std::vector<giac_double> & a,const std::vector<giac_double> & b,std::vector<giac_double> & c);
  void multvecteur(double x,const std::vector<giac_double> & a,std::vector<giac_double> & c);
  void multvecteur(double x,std::vector<giac_double> & c);
  std::vector<giac_double> multvecteur(double x,const std::vector<giac_double> & b);
  std::vector<giac_double> addvecteur(const std::vector<giac_double> & a,const std::vector<giac_double> & b);
  std::vector<giac_double> subvecteur(const std::vector<giac_double> & a,const std::vector<giac_double> & b);


  // solve triangular lower inf system l*y=b
  bool sparse_linsolve_l(const gen_map & l,const vecteur & b,vecteur & y);

  // solve triangular upper system u*x=b
  bool sparse_linsolve_u(const gen_map & u,const vecteur & b,vecteur & x);

  bool is_sparse_matrix(const gen & g,int & nrows,int & ncols,int & n);
  bool is_sparse_matrix(const gen_map & m,int & nrows,int & ncols,int & n);
  bool is_sparse_vector(const gen & g,int & nrows,int & n);
  bool is_sparse_vector(const gen_map & g,int & nrows,int & n);

  gen sparse_conjugate_gradient(const smatrix & A,const vecteur & b_orig,const vecteur & x0,double eps,int maxiter,GIAC_CONTEXT);

  gen sparse_conjugate_gradient(const gen_map & A,const vecteur & b_orig,const vecteur & x0,double eps,int maxiter,GIAC_CONTEXT);

  gen sparse_jacobi_linsolve(const smatrix & A,const vecteur & b_orig,const vecteur & x0,double eps,int maxiter,GIAC_CONTEXT);

  gen sparse_jacobi_linsolve(const gen_map & A,const vecteur & b_orig,const vecteur & x0,double eps,int maxiter,GIAC_CONTEXT);

  std::vector<giac_double> sparse_jacobi_linsolve(const fmatrix & A,const std::vector<giac_double> & b_orig,const std::vector<giac_double> & x0,double eps,int maxiter,GIAC_CONTEXT);

  std::vector<giac_double> sparse_gauss_seidel_linsolve(const fmatrix & A,const std::vector<giac_double> & b_orig,const std::vector<giac_double> & x0,double omega,double eps,int maxiter,GIAC_CONTEXT);

  gen sparse_gauss_seidel_linsolve(const smatrix & A,const vecteur & b_orig,const vecteur & x0,double omega,double eps,int maxiter,GIAC_CONTEXT);

  gen sparse_gauss_seidel_linsolve(const gen_map & A,const vecteur & b_orig,const vecteur & x0,double omega,double eps,int maxiter,GIAC_CONTEXT);


#ifndef NO_NAMESPACE_GIAC
}
#endif // ndef NO_NAMESPACE_GIAC
#endif // _GIAC_SPARSE_H
