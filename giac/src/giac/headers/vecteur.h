// -*- mode:C++ ; compile-command: "g++ -I.. -g -c vecteur.cc" -*-
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
#ifndef _GIAC_VECTEUR_H
#define _GIAC_VECTEUR_H
#include "first.h"
#include "index.h"
#include <complex>
#include <iostream>
#ifdef HAVE_LIBGSL
#include <gsl/gsl_vector.h>
#include <gsl/gsl_matrix.h>
#include <gsl/gsl_permutation.h>
#endif // HAVE_LIBGSL

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  const double randrange = 100.0;

  struct unary_function_ptr;
  struct ref_vecteur;
  typedef vecteur matrice; // same type but different name
  typedef vecteur::const_iterator const_iterateur;
  typedef vecteur::iterator iterateur;
  typedef std::complex<double> complex_double;
#if defined(HAVE_LONG_DOUBLE) && !defined(PNACL)
  typedef long double long_double;
#else
  typedef double long_double;
#endif
  typedef std::complex<long_double> complex_long_double;
  double complex_abs(const complex_double & c);
  double complex_long_abs(const complex_long_double & c);

  // make a matrix with free rows 
  // (i.e. it is possible to modify the answer in place)
  matrice makefreematrice(const matrice & m);
  gen freecopy(const gen & g); // this one makes a free copy of a vector, not of a matrix
  // vecteur related functions
  vecteur makevecteur(const gen & a);
  vecteur makevecteur(const gen & a,const gen & b);
  vecteur makevecteur(const gen & a,const gen & b,const gen & c);
  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d);
  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e);
  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f);
  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g);
  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h);
  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i);
  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i,const gen &j);
  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i,const gen &j,const gen & k);
  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i,const gen &j,const gen & k,const gen & l);
  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i,const gen &j,const gen & k,const gen & l,const gen & m);
  vecteur makevecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i,const gen &j,const gen & k,const gen & l,const gen & m,const gen &n);

  gen makesequence(const gen & a);
  gen makesequence(const gen & a,const gen & b);
  gen makesequence(const gen & a,const gen & b,const gen & c);
  gen makesequence(const gen & a,const gen & b,const gen & c,const gen & d);
  gen makesequence(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e);
  gen makesequence(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f);
  gen makesequence(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g);
  gen makesequence(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h);
  gen makesequence(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i);

  ref_vecteur * makenewvecteur(const gen & a);
  ref_vecteur * makenewvecteur(const gen & a,const gen & b);
  ref_vecteur * makenewvecteur(const gen & a,const gen & b,const gen & c);
  ref_vecteur * makenewvecteur(const gen & a,const gen & b,const gen & c,const gen & d);
  ref_vecteur * makenewvecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e);
  ref_vecteur * makenewvecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f);
  ref_vecteur * makenewvecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g);
  ref_vecteur * makenewvecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h);
  ref_vecteur * makenewvecteur(const gen & a,const gen & b,const gen & c,const gen & d,const gen & e,const gen & f,const gen & g,const gen & h,const gen & i);

  // numeric root utilities
  bool francis_schur(std_matrix<gen> & H,int n1,int n2,std_matrix<gen> & P,int maxiter,double eps,bool is_hessenberg,bool complex_schur,bool compute_P,bool no_lapack,GIAC_CONTEXT);

  class matrix_double:public std::vector< std::vector<giac_double> >{    
  public:
    // inherited constructors
    matrix_double() : std::vector< std::vector<giac_double> >() { };
    matrix_double(int i) : std::vector< std::vector<giac_double> >(i) { };
    matrix_double(int i,const std::vector<giac_double> v) : std::vector< std::vector<giac_double> >(i,v) { };
    matrix_double(const matrix_double::const_iterator b,const matrix_double::const_iterator e) : std::vector< std::vector<giac_double> >(b,e) { };
    void dbgprint() const ;
  };
  
  class matrix_complex_double:public std::vector< std::vector<complex_double> >{    
  public:
    // inherited constructors
    matrix_complex_double() : std::vector< std::vector<complex_double> >() { };
    matrix_complex_double(int i) : std::vector< std::vector<complex_double> >(i) { };
    matrix_complex_double(int i,const std::vector<complex_double> v) : std::vector< std::vector<complex_double> >(i,v) { };
    void dbgprint() const ;
  };
  
  bool balanced_eigenvalues(matrix_double & H,vecteur & res,int maxiter,double eps,bool is_hessenberg,GIAC_CONTEXT);
  bool francis_schur(matrix_double & H,int n1,int n2,matrix_double & P,int maxiter,double eps,bool is_hessenberg,bool compute_P);
  bool francis_schur(matrix_complex_double & H,int n1,int n2,matrix_complex_double & P,int maxiter,double eps,bool is_hessenberg,bool compute_P);

  bool matrice2lapack(const matrice & m,double * A,GIAC_CONTEXT);
  void lapack2matrice(double * A,unsigned rows,unsigned cols,matrice & R);
  bool lapack_schur(std_matrix<gen> & H,std_matrix<gen> & P,bool compute_P,vecteur & eigenvalues,GIAC_CONTEXT);
  bool lapack_schur(matrix_double & H,matrix_double & P,bool compute_P,vecteur & eigenvalues);
  bool eigenval2(matrix_double & H,int n2,giac_double & l1, giac_double & l2);
  bool std_matrix_gen2std_matrix_giac_double(const std_matrix<gen> & H,matrix_double & H1,bool crunch);
  bool std_matrix_giac_double2std_matrix_gen(const matrix_double & H,std_matrix<gen> & H1);

  matrice companion(const vecteur & w);
  gen a_root(const vecteur & v,const std::complex<double> & c0,double eps);
  vecteur proot(const vecteur & v);
  vecteur proot(const vecteur & v,double eps);
  vecteur proot(const vecteur & v,double & eps,int & rprec);
  vecteur real_proot(const vecteur & v,double eps,GIAC_CONTEXT);
  gen symb_proot(const gen & e) ;
  gen _proot(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_proot ;
  gen symb_pcoeff(const gen & e) ;
  gen _pcoeff(const gen & e,GIAC_CONTEXT);
  vecteur pcoeff(const vecteur & v);
  extern const unary_function_ptr * const  at_pcoeff ;
  gen symb_peval(const gen & arg1,const gen & arg2) ;
  extern const unary_function_ptr * const  at_peval;
  gen _peval(const gen & e,GIAC_CONTEXT);

  gen spread_convert(const gen & g,int g_row,int g_col,GIAC_CONTEXT);
  vecteur lcell(const gen & g);
  // these int are used to translate relative cells to spreadsheet names
  bool iscell(const gen & g,int & r,int & c,GIAC_CONTEXT);
  std::string printcell(const vecteur & v,GIAC_CONTEXT);
  // given g=cell() or its argument at row i, column j 
  // return 0 if not a cell, 1 if a cell, then compute r and c s.t. g refers to (r,c), 
  // return 2 if g is e.g. A1:B4 compute ref of A1 and B4
  int cell2pos(const gen & g,int i,int j,int & r,int & c,int & r2,int & c2);
  // return cell(r,c) argument at (i,j) with same absolute/relative addressing
  // as g
  gen pos2cell(const gen & g,int i,int j,int r,int c,int r2,int c2);
  // insert nrows/ncols of fill in m, e.g. fill= [0,0,2] for a spreadsheet
  // or ["","",2] or 0 for a matrix
  matrice matrice_insert(const matrice & m,int insert_row,int insert_col,int nrows,int ncols,const gen & fill,GIAC_CONTEXT);
  // erase nrows/ncols
  matrice matrice_erase(const matrice & m,int insert_row,int insert_col,int nrows,int ncols,GIAC_CONTEXT);
  // extract submatrix
  matrice matrice_extract(const matrice & m,int insert_row,int insert_col,int nrows,int ncols);
  void makespreadsheetmatrice(matrice & m,GIAC_CONTEXT);
  matrice extractmatricefromsheet(const matrice & m);
  // eval spreadsheet, compute list of dependances in lc
  void spread_eval(matrice & m,GIAC_CONTEXT);

  gen makesuite(const gen & a);
  gen makesuite(const gen & a,const gen & b);
  gen makesuite_inplace(const gen & a,const gen & b);
  vecteur mergevecteur(const vecteur & v,const vecteur & w);
  vecteur mergeset(const vecteur & v,const vecteur & w);
  int vrows(const vecteur & a);
  void addvecteur(const vecteur & a,const vecteur & b,vecteur & res);
  vecteur addvecteur(const vecteur & a,const vecteur & b);
  void subvecteur(const vecteur & a,const vecteur & b,vecteur & res);
  vecteur subvecteur(const vecteur & a,const vecteur & b);
  vecteur negvecteur(const vecteur & a); // calls negmodpoly
  // Multiplication by a scalar
  void multvecteur(const gen & a,const vecteur & b,vecteur & res);
  vecteur multvecteur(const gen & a,const vecteur & b);
  void divvecteur(const vecteur & b,const gen & a,vecteur & res);
  vecteur divvecteur(const vecteur & b,const gen & a);
  // Matrix times vecteur
  void multmatvecteur(const matrice & a,const vecteur & b,vecteur & res);
  vecteur multmatvecteur(const matrice & a,const vecteur & b);
  void multvecteurmat(const vecteur & a,const matrice & b,vecteur & res);
  vecteur multvecteurmat(const vecteur & a,const matrice & b);
  // Scalar product (does not conjugate, see scalarproduct in misc.h)
  gen dotvecteur(const vecteur & a,const vecteur & b);
  giac_double dotvecteur_double(const std::vector<giac_double> & a,const std::vector<giac_double> & c);
  giac_double dotvecteur(const std::vector<giac_double> & a,const std::vector<giac_double> & c);
  gen dotvecteur(const gen & a,const gen & b);
  gen generalized_dotvecteur(const vecteur & a,const vecteur & b,int pos);
  vecteur generalized_multmatvecteur(const matrice & a,const vecteur & b);
  // Vect product (3-d)
  gen complex2vecteur(const gen & g,GIAC_CONTEXT);
  vecteur cross(const vecteur & v,const vecteur & w,GIAC_CONTEXT);
  gen cross(const gen & g1,const gen & g2,GIAC_CONTEXT);
  // ckmvmult check a and b
  // if a and b are matrices returns matrix product
  // if a is a matrix and b a vecteur return matr*vect
  // otherwise returns the dot product of a and b
  gen ckmultmatvecteur(const vecteur & a,const vecteur & b,GIAC_CONTEXT);
  void multmatvecteur(const matrix_double & H,const std::vector<giac_double> & w,std::vector<giac_double> & v);

  void vecteur2vector_int(const vecteur & v,int modulo,std::vector<int> & res);
  bool vecteur2vectvector_int(const vecteur & v,int modulo,std::vector< std::vector<int> > & res);
  void vector_int2vecteur(const std::vector<int> & v,vecteur & res);
  void vectvector_int2vecteur(const std::vector< std::vector<int> > & v,vecteur & res);
  bool iszero(const std::vector<int> & p);
  
  // matrice related functions
  bool ckmatrix(const matrice & a,bool allow_embedded_vect);
  bool ckmatrix(const matrice & a);
  bool ckmatrix(const gen & a);
  bool ckmatrix(const gen & a,bool);
  bool is_squarematrix(const matrice & a);
  bool is_squarematrix(const gen & a);
  bool is_fully_numeric(const vecteur & v, int withfracint = 0);
  bool is_fully_numeric(const gen & a, int withfracint = 0);
  bool is_exact(const vecteur & v);
  bool is_exact(const gen & g);
  // the following functions do not check that a is indeed a matrix
  int mrows(const matrice & a);
  int mcols(const matrice & a);
  void mdims(const matrice &m,int & r,int & c);
  void mtran(const matrice & a,matrice & res,int ncolres=0);
  matrice mtran(const matrice & a);
  gen _tran(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_tran ;
  void transpose_double(const matrix_double & a,int r0,int r1,int c0,int c1,matrix_double & at);

  // mmult assumes dimensions are correct
  void mmult(const matrice & a,const matrice & b,matrice &res);
  void mmult_atranb(const matrice & a,const matrice & btran,matrice & res);
  matrice mmult(const matrice & a,const matrice & b);
  bool mmultck(const matrice & a,const matrice & b,matrice & res);
  matrice mmultck(const matrice & a,const matrice & b);

  extern int strassen_limit;
  void strassen_mod(bool skip_reduce,bool add,const std::vector< std::vector<int> > & A,const std::vector< std::vector<int> > & Btran,std::vector< std::vector<int> > & C,int p,int arbeg=0,int arend=0,int acbeg=0,int acend=0,int brbeg=0,int brend=0,int bcbeg=0);
  void mmult_mod(const std::vector< std::vector<int> > & A,const std::vector< std::vector<int> > & Btran,std::vector< std::vector<int> > & C,int p,int Ar0=0,int Ar1=0,int Ac0=0,int Ac1=0,int Brbeg=0,int Brend=0,int Bcbeg=0,int Crbeg=0,int Ccbeg=0,bool add=false);

  gen mtrace(const matrice & a);
  gen ckmtrace(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_trace ;

  gen common_deno(const vecteur & v);

  bool convert(const vecteur & v,std::vector<giac_double> & v1,bool crunch);
  bool convert(const vecteur & v,std::vector< complex_double > & v1,bool crunch);
  bool convert(const std::vector<giac_double> & v,vecteur & v1);
  void matrice2std_matrix_gen(const matrice & m,std_matrix<gen> & M);
  void std_matrix_gen2matrice(const std_matrix<gen> & M,matrice & m);
  bool vecteur2index(const vecteur & v,index_t & i);
  void vect_vector_int_2_vect_vecteur(const std::vector< std::vector<int> > & N,std_matrix<gen> & M);
  void vect_vecteur_2_vect_vector_int(const std_matrix<gen> & M,int modulo,std::vector< std::vector<int> > & N);
  bool vecteur2vectvector_int(const vecteur & v,int modulo,std::vector< std::vector<int> > & res);
  void vectvector_int2vecteur(const std::vector< std::vector<int> > & v,vecteur & res);
  int dotvector_int(const std::vector<int> & v,const std::vector<int> & w,int modulo);
  bool multvectvector_int_vector_int(const std::vector< std::vector<int> > & M,const std::vector<int> & v,int modulo,std::vector<int> & Mv);
  void tran_vect_vector_int(const std::vector< std::vector<int> > & N,std::vector< std::vector<int> > & tN);
  void apply_permutation(const std::vector<int> & permutation,const std::vector<int> &x,std::vector<int> & y);
  void vecteur2vector_int(const vecteur & v,int modulo,std::vector<int> & res);
  
  enum matrix_algorithms {
    RREF_GAUSS_JORDAN=0,
    RREF_GUESS=1,
    RREF_BAREISS=2,
    RREF_MODULAR=3,
    RREF_PADIC=4,
    RREF_LAGRANGE=5
  };
  // For approx linear combination, anything ||<eps will be replaced by 0
  void linear_combination(const gen & c1,const vecteur & v1,const gen & c2,const vecteur & v2,const gen & c,const gen & cinv,vecteur & v,double eps,int cstart=0);
  gen exact_div(const gen & a,const gen & b);

  // row reduction from line l and column c to line lmax and column cmax
  // lmax and cmax are not included
  // line are numbered starting from 0
  // if fullreduction is false, reduction occurs under the diagonal only
  // if dont_swap_below !=0, for line numers < dont_swap_below
  // the pivot is searched in the line instead of the column
  // hence no line swap occur
  // convert_internal = false if we do not want conversion to rational fractions
  // algorithm=0 Gauss-Jordan, 1 guess, 2 Bareiss, 3 modular, 4 p-adic
  // rref_or_det_or_lu = 0 for rref, 1 for det, 2 for lu, 3 lu w/o permutation
  // if unsure fullreduction=true, dont_swap_below=0, convert_internal=true
  // algorithm=1, rref_or_det_or_lu=0
  // Returns 0 on failure, 1 on success, 2 if success inverting and no need to remove identity
  int mrref(const matrice & a, matrice & res, vecteur & pivots, gen & det,int l, int lmax, int c,int cmax,
	     int fullreduction,int dont_swap_below,bool convert_internal,int algorithm,int rref_or_det_or_lu,GIAC_CONTEXT);
  // holds temporary work storage for block operation
  struct smallmodrref_temp_t {
    std::vector< std::vector<int> > Ainvtran,Ainv,CAinv;
    std::vector<int> permblock,maxrankblock; 
    vecteur pivblock; 
    std::vector<int> y,y1,y2,y3;
    std::vector<longlong> z,z1,z2,z3;
  };
  bool in_modrref(const matrice & a, std::vector< std::vector<int> > & N,matrice & res, vecteur & pivots, gen & det,int l, int lmax, int c,int cmax,int fullreduction,int dont_swap_below,int modulo,int carac,int rref_or_det_or_lu,const gen & mult_by_det_mod_p,bool inverting,bool no_initial_mod,smallmodrref_temp_t * workptr);
  bool modrref(const matrice & a, matrice & res, vecteur & pivots, gen & det,int l, int lmax, int c,int cmax,
	       int fullreduction,int dont_swap_below,const gen & modulo,bool ckprime,int rref_or_det_or_lu);
  bool mrref(const matrice & a, matrice & res, vecteur & pivots, gen & det,GIAC_CONTEXT);
  bool modrref(const matrice & a, matrice & res, vecteur & pivots, gen & det,const gen& modulo);
  

  // finish full row reduction to echelon form if N is upper triangular
  // this is done from lmax-1 to l
  void smallmodrref_upper(std::vector< std::vector<int> > & N,int l,int lmax,int c,int cmax,int modulo);
  // finish row reduction for matrices with much more columns than rows
  // version adapted for threads parallelization
  // assumes that all columns are reduced in parallel, pivots are searched
  // starting at column 0
  void in_thread_smallmodrref_upper(std::vector< std::vector<int> > & N,int l,int lpivot,int lmax,int c,int cmax,int modulo,int parallel);
  void thread_smallmodrref_upper(std::vector< std::vector<int> > & N,int l,int lmax,int c,int cmax,int modulo,int parallel);
  void free_null_lines(std::vector< std::vector<int> > & N,int l,int lmax,int c,int cmax);
  int smallmodrref_lastpivotcol(const std::vector< std::vector<int> > & K,int lmax);

  void smallmodrref(int nthreads,std::vector< std::vector<int> > & N,vecteur & pivots,std::vector<int> & permutation,std::vector<int> & maxrankcols,longlong & idet,int l, int lmax, int c,int cmax,int fullreduction,int dont_swap_below,int modulo,int rref_or_det_or_lu,bool reset,smallmodrref_temp_t * workptr,bool allow_block,int carac);
  void doublerref(matrix_double & N,vecteur & pivots,std::vector<int> & permutation,std::vector<int> & maxrankcols,double & idet,int l, int lmax, int c,int cmax,int fullreduction,int dont_swap_below,int rref_or_det_or_lu,double eps);
  void modlinear_combination(vecteur & v1,const gen & c2,const vecteur & v2,const gen & modulo,int cstart,int cend=0);
  void modlinear_combination(std::vector<int> & v1,int c2,const std::vector<int> & v2,int modulo,int cstart,int cend,bool pseudo);
  vecteur fracmod(const vecteur & v,const gen & modulo);
  gen modproduct(const vecteur & v, const gen & modulo);
  matrice mrref(const matrice & a,GIAC_CONTEXT);
  gen _rref(const gen & a,GIAC_CONTEXT); // first non 0 elem in row is 1
  extern const unary_function_ptr * const  at_rref ;
  void add_identity(matrice & arref);
  void add_identity(std::vector< std::vector<int> > & arref);
  bool remove_identity(matrice & res);
  bool remove_identity(std::vector< std::vector<int> > & res,int modulo);

  void mdividebypivot(matrice & a,int lastcol,GIAC_CONTEXT); // in-place div by pivots
  void mdividebypivot(matrice & a,int lastcol=-1); // in-place div by pivots
  // if lastcol==-1, divide last col, if lastcol==-2 do not divide last col
  // if lastcol>=0 stop dividing at lastcol
  void midn(int n,matrice & res);
  matrice midn(int n);
  gen _idn(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_idn ;

  gen fieldcoeff(const gen &F);
  vecteur vranm(int n,const gen & f,GIAC_CONTEXT); 
  matrice mranm(int n,int m,const gen & f,GIAC_CONTEXT); // random matrix using f
  gen _ranm(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ranm ;
  gen _randvector(const gen & e,GIAC_CONTEXT);

  bool read_reduction_options(const gen & a_orig,matrice & a,bool & convert_internal,int & algorithm,bool & minor_det,bool & keep_pivot,int & last_col);
  bool minv(const matrice & a,matrice & res,bool convert_internal,int algorithm,GIAC_CONTEXT); /* if unsure, use true for convert_internal and 1 for algorithm */
  bool smallmodinv(const std::vector< std::vector<int> > & a,std::vector< std::vector<int> > & res,int modulo,longlong & det_mod_p);
  bool modinv(const matrice & a,matrice & res,const gen & modulo,gen & det_mod_p);
  // solve a*x=b where a and b have integer coeffs
  // using a p-adic algorithm, n is the precision required
  // c must be the inverse of a mod p 
  vecteur padic_linsolve_c(const matrice & a,const vecteur & b,const matrice & c,unsigned n,const gen & p,unsigned reconstruct=0);
  // solve a*x=b where a and b have integer coeffs using a p-adic algorithm
  // lcmdeno of the answer may be used to give an estimate of the 
  // least divisor element of a if b is random
  // returns 0 if no invertible found, -1 if det==0, 1 otherwise
  int padic_linsolve(const matrice & a,const vecteur & b,vecteur & res,gen & p,gen & det_mod_p,gen & h2,unsigned reconstruct=0,int maxtry=4);
  // a is a matrix with integer coeffs
  // find p such that a mod p has the same rank
  // rankline and rankcols are the lines/cols used for the submatrix
  // asub of max rank, ainv is the inverse of asub mod p
  // return -1 or the rank
  int padic_linsolve_prepare(const matrice & a,gen & p,std::vector<int> & ranklines, std::vector<int> & rankcols,matrice & asub,matrice & ainv,vecteur & compat,vecteur & kernel);
  // solve a prepared non Cramer linear system 
  bool padic_linsolve_solve(const matrice & a,const gen & p,const std::vector<int> & ranklines,const std::vector<int> & rankcols,const matrice & asub,const matrice & ainv,const vecteur & compat,const vecteur & b,vecteur & sol);
  gen _padic_linsolve(const gen & g,GIAC_CONTEXT);

  matrice minv(const matrice & a,GIAC_CONTEXT);
  gen mdet(const matrice & a,GIAC_CONTEXT);
  gen _det(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_det ;
  gen _det_minor(const gen & g,bool convert_internal,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_det_minor ;
  gen det_minor(const matrice & a,vecteur lv,bool convert_internal,GIAC_CONTEXT);
  void sylvester(const vecteur & v1,const vecteur & v2,matrice & res);
  matrice sylvester(const vecteur & v1,const vecteur & v2);
  gen _sylvester(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_sylvester ;

  gen _hessenberg(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_hessenberg ;

  bool balance_krylov(const matrix_double & H,std::vector<giac_double> & d,int niter=5,double cutoff=1e-8);
  bool probabilistic_pmin(const matrice & m,vecteur & w,bool check,GIAC_CONTEXT);
  vecteur mpcar_int(const matrice & A,bool krylov,GIAC_CONTEXT,bool compute_pmin);

  void mod_pcar(std_matrix<gen> & N,vecteur & res,bool compute_pmin);
  bool mod_pcar(const matrice & A,std::vector< std::vector<int> > & N,int modulo,bool & krylov,std::vector<int> & res,GIAC_CONTEXT,bool compute_pmin);
  bool mod_pcar(std::vector< std::vector<int> > & N,int modulo,bool & krylov,std::vector<int> & res,GIAC_CONTEXT,bool compute_pmin);
  vecteur mpcar_hessenberg(const matrice & A,int modulo,GIAC_CONTEXT);
  gen _pcar_hessenberg(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_pcar_hessenberg ;
  void mhessenberg(std::vector< std::vector<int> > & H,std::vector< std::vector<int> > & P,int modulo,bool compute_P);
  void hessenberg(std_matrix<gen> & H,std_matrix<gen> & P,GIAC_CONTEXT);
  void hessenberg_ortho(std_matrix<gen> & H,std_matrix<gen> & P,GIAC_CONTEXT);
  void hessenberg_ortho(std_matrix<gen> & H,std_matrix<gen> & P,int firstrow,int n,bool compute_P,int already_zero,double eps,GIAC_CONTEXT);
  void qr_ortho(std_matrix<gen> & H,std_matrix<gen> & P,bool computeP,GIAC_CONTEXT);
  void hessenberg_schur(std_matrix<gen> & H,std_matrix<gen> & P,int maxiter,double eps,GIAC_CONTEXT);
  gen _hessenberg(const gen & g0,GIAC_CONTEXT);

  vecteur mpcar(const matrice & a,vecteur & B,bool compute_B,GIAC_CONTEXT);
  vecteur mpcar(const matrice & a,vecteur & Bv,bool compute_Bv,bool convert_internal,GIAC_CONTEXT);
  gen _pcar(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_pcar ;

  // if jordan is false, errors for non diagonalizable matrices
  // if jordan is true, d is a matrix, not a vecteur
  bool egv(const matrice & m,matrice & p,vecteur & d, GIAC_CONTEXT, bool jordan,bool rational_jordan_form,bool eigenvalues_only);
  gen symb_egv(const gen & a);
  matrice megv(const matrice & a,GIAC_CONTEXT);
  gen _egv(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_egv ;
  gen _svd(const gen & a,GIAC_CONTEXT);

  gen symb_egvl(const gen & a);
  vecteur megvl(const matrice & a,GIAC_CONTEXT);
  gen _egvl(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_egvl ;

  gen symb_jordan(const gen & a);
  vecteur mjordan(const matrice & a,bool rational_jordan,GIAC_CONTEXT);
  gen _jordan(const gen & a,GIAC_CONTEXT);
  gen jordan(const gen & a,bool rational_jordan,GIAC_CONTEXT);
  gen _rat_jordan(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_jordan ;
  matrice rat_jordan_block(const vecteur & v,int n,bool pseudo);
  gen _rat_jordan_block(const gen &args,GIAC_CONTEXT);
  matrice pseudo_rat_to_rat(const vecteur & v,int n);

  // if g is an expression, replace x by m in g (m assumed to be diagonal)
  matrice diagonal_apply(const gen & g,const gen & x,const matrice & m,GIAC_CONTEXT);
  // apply an analytic function to a square matrix
  matrice analytic_apply(const unary_function_ptr * u,const matrice & m,GIAC_CONTEXT);
  matrice analytic_apply(const gen &ux,const gen & x,const matrice & m,GIAC_CONTEXT);
  matrice matpow(const matrice & m,const gen & n,GIAC_CONTEXT);
  gen _matpow(const gen & a,GIAC_CONTEXT);

  bool mker(const matrice & a,vecteur & v,int algorithm,GIAC_CONTEXT);
  bool mker(const matrice & a,vecteur & v,GIAC_CONTEXT); // algorithm=0
  vecteur mker(const matrice & a,GIAC_CONTEXT);
  gen _ker(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ker ;

  bool mimage(const matrice & a, vecteur & v,GIAC_CONTEXT);
  vecteur mimage(const matrice & a,GIAC_CONTEXT);
  gen _image(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_image ;

  gen symb_cross(const gen & arg1,const gen & arg2);
  gen symb_cross(const gen & a);
  gen _cross(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_cross ;

  gen symb_size(const gen & a);
  gen _size(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_size ;
  bool vecteur2index(const vecteur & v,index_t & i);
#ifdef HAVE_LIBGSL 
  gsl_vector * vecteur2gsl_vector(const vecteur & v,GIAC_CONTEXT); // allocate
  int vecteur2gsl_vector(const vecteur & v,gsl_vector * w,GIAC_CONTEXT); // no alloc
  int vecteur2gsl_vector(const_iterateur it,const_iterateur itend,gsl_vector * w,GIAC_CONTEXT);
  vecteur gsl_vector2vecteur(const gsl_vector * v);
  int matrice2gsl_matrix(const matrice & m,gsl_matrix * w,GIAC_CONTEXT);
  gsl_matrix * matrice2gsl_matrix(const matrice & m,GIAC_CONTEXT);
  matrice gsl_matrix2matrice(const gsl_matrix * v);
  vecteur gsl_permutation2vecteur(const gsl_permutation * p,GIAC_CONTEXT);
#endif // HAVE_LIBGSL
  
  bool mlu(const matrice & a0,vecteur & P,matrice & L,matrice & U,GIAC_CONTEXT);
  gen lu(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_lu ;
  gen qr(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_qr ;
  matrice thrownulllines(const matrice & res);

  extern const unary_function_ptr * const  at_lll_reduce ;
  extern const unary_function_ptr * const  at_lll ;

  gen _cholesky(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_cholesky ;
  gen _svd(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_svd ;
  gen _basis(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_basis ;
  gen _ibasis(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ibasis ;

  // inert function used for internal representation in spreadsheets
  gen _cell(const gen & a,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_cell ;

  int alphaposcell(const std::string & s,int & r);
  gen l2norm(const vecteur & v,GIAC_CONTEXT);
  matrice gramschmidt(const matrice & m,bool normalize,GIAC_CONTEXT);
  matrice gramschmidt(const matrice & m,matrice & r,bool normalize,GIAC_CONTEXT);
  // lll decomposition of m
  matrice lll(const matrice & M,matrice & L,matrice & O,matrice &A,GIAC_CONTEXT);
  matrice lll(const matrice & m,GIAC_CONTEXT);
  gen _lll(const gen & g,GIAC_CONTEXT);


  void matrice2std_matrix_gen(const matrice & m,std_matrix<gen> & M);
  void std_matrix_gen2matrice(const std_matrix<gen> & M,matrice & m);
  void std_matrix_gen2matrice_destroy(std_matrix<gen> & M,matrice & m);

  bool is_integer_vecteur(const vecteur & m,bool intonly=false);
  bool is_integer_matrice(const matrice & m,bool intonly=false);
  bool is_mod_vecteur(const vecteur & m,std::vector<int> & v,int & p);
  bool is_mod_matrice(const matrice & m,std::vector< std::vector<int> > & M,int & p);

  typedef void (*bezout_fonction)(const gen & a,const gen & b,gen & u,gen &v,gen &d);

  struct environment;
  bool ismith(const matrice & Aorig, matrice & U,matrice & A,matrice & V,GIAC_CONTEXT);
  bool smith(const std_matrix<gen> & Aorig,std_matrix<gen> & U,std_matrix<gen> & A,std_matrix<gen> & V,environment * env,GIAC_CONTEXT);
  bool ihermite(const matrice & Aorig, matrice & U,matrice & A,GIAC_CONTEXT);
  bool hermite(const std_matrix<gen> & Aorig,std_matrix<gen> & U,std_matrix<gen> & A,environment * env,GIAC_CONTEXT);
  gen _ihermite(const gen & g,GIAC_CONTEXT);
  gen _ismith(const gen & g,GIAC_CONTEXT);
#if !defined NSPIRE && !defined FXCG
  gen _csv2gen(const gen & g,GIAC_CONTEXT);
  matrice csv2gen(std::istream & i,char sep,char nl,char decsep,char eof,GIAC_CONTEXT);
#endif

  // find index i of x in v that is i such that v[i] <= x < v[i+1]
  // where v[-1]=-inf, and v[v.size()]=+inf
  int dichotomy(const std::vector<giac_double> & v,double x);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_VECTEUR_H
