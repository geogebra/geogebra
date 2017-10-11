// -*- mode:C++ ; compile-command: "g++ -I.. -g -c modpoly.cc" -*-
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

#ifndef _GIAC_MODPOLY_H_
#define _GIAC_MODPOLY_H_
#include "first.h"
#include "global.h"
#include "fraction.h"
#ifdef HAVE_LIBNTL
#include <NTL/ZZXFactoring.h>
#include <NTL/ZZ.h>
#include <NTL/GF2X.h>
#include <NTL/pair_GF2X_long.h>
#include <NTL/GF2XFactoring.h>
#include <NTL/ZZ_pX.h>
#include <NTL/pair_ZZ_pX_long.h>
#include <NTL/ZZ_pXFactoring.h>
#ifdef HAVE_PTHREAD_H
#include <pthread.h>
#endif
#endif // HAVE_LIBNTL

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  template<class T> class tensor;
  typedef tensor<gen> polynome;
  typedef vecteur modpoly;
  typedef vecteur dense_POLY1; // same internal rep but assumes non modular op

  // set env->pn, returns true if size of field <=2^31
  bool normalize_env(environment * env);
  // modular or GF inverse
  gen invenv(const gen & g,environment * env);

  // ***********************
  // Building and converting
  // ***********************
  // Conversion from univariate to multivariate polynomials
  modpoly modularize(const polynome & p,const gen & n,environment * env);
  modpoly modularize(const dense_POLY1 & p,const gen & n,environment * env);
  polynome unmodularize(const modpoly & a);
  bool is_one(const modpoly & p);
  int is_cyclotomic(const modpoly & p,GIAC_CONTEXT);
  int is_cyclotomic(const modpoly & p,double eps);
  modpoly one(); // 1
  modpoly xpower1(); // x
  modpoly xpowerpn(environment * env); // x^modulo
  vecteur x_to_xp(const vecteur & v, int p); // x -> x^p non modular
  gen nrandom(environment * env); // random modular integer
  modpoly random(int i,environment * env); // random univariate polynomial of degree i
  void shiftmodpoly(modpoly & a,int n);   // multiply by x^n
  // high = high*x^n + low, size of low must be < n
  void mergemodpoly(modpoly & high,const modpoly & low,int n); 
  modpoly trim(const modpoly & p,environment * env);
  void trim_inplace(modpoly & p);
  bool trim(modpoly & v); // true if v is empty after trimming
  void rrdm(modpoly & p, int n);   // right redimension poly to degree n

  // ***************
  // Arithmetic
  // ***************
  // !! Do not call Addmodpoly with modpoly slices if new_coord and th/other overlapp
  void Addmodpoly(modpoly::const_iterator th_it,modpoly::const_iterator th_itend,modpoly::const_iterator other_it,modpoly::const_iterator other_itend,environment * env, modpoly & new_coord);
  void addmodpoly(const modpoly & th, const modpoly & other, modpoly & new_coord);
  void addmodpoly(const modpoly & th, const modpoly & other, environment * env,modpoly & new_coord);
  modpoly operator_plus (const modpoly & th,const modpoly & other,environment * env);
  modpoly operator + (const modpoly & th,const modpoly & other);
  void Submodpoly(modpoly::const_iterator th_it,modpoly::const_iterator th_itend,modpoly::const_iterator other_it,modpoly::const_iterator other_itend,environment * env,modpoly & new_coord);
  void submodpoly(const modpoly & th, const modpoly & other, environment * env,modpoly & new_coord);
  void submodpoly(const modpoly & th, const modpoly & other, modpoly & new_coord);
  modpoly operator_minus (const modpoly & th,const modpoly & other,environment * env);
  modpoly operator - (const modpoly & th,const modpoly & other);
  void negmodpoly(const modpoly & th, modpoly & new_coord);
  modpoly operator - (const modpoly & th) ;

  // Warning: mulmodpoly assumes that coeff are integers. 
  // Use operator_times unless you know what you do
  void mulmodpoly(const modpoly & th, const gen & fact, modpoly & new_coord);
  void mulmodpoly(const modpoly & th, const gen & fact, environment * env, modpoly & new_coord);
  void mulmodpoly_naive(modpoly::const_iterator ita,modpoly::const_iterator ita_end,modpoly::const_iterator itb,modpoly::const_iterator itb_end,environment * env,modpoly & new_coord);
  void mulmodpoly_kara_naive(const modpoly & a, const modpoly & b,environment * env,modpoly & new_coord,int seuil_kara);
  // new_coord += a*b in place
  void add_mulmodpoly(const modpoly::const_iterator & ita0,const modpoly::const_iterator & ita_end,const modpoly::const_iterator & itb0,const modpoly::const_iterator & itb_end,environment * env,modpoly & new_coord);
  // modpoly operator * (const modpoly & th, const gen & fact);
  modpoly operator_times (const modpoly & th, const gen & fact,environment * env);
  // commented otherwise int * gen might be interpreted as
  // make a modpoly of size the int and multiply
  modpoly operator_times (const gen & fact,const modpoly & th,environment * env);
  modpoly operator * (const gen & fact,const modpoly & th);
  void mulmodpoly(const modpoly & a, const modpoly & b, environment * env,modpoly & new_coord);
  modpoly operator * (const modpoly & th, const modpoly & other) ;
  modpoly operator_times (const modpoly & th, const modpoly & other,environment * env) ;
  void operator_times (const modpoly & a, const modpoly & b,environment * env,modpoly & new_coord);
  // res=(*it) * ... (*(it_end-1))
  void mulmodpoly(std::vector<modpoly>::const_iterator it,std::vector<modpoly>::const_iterator it_end,environment * env,modpoly & new_coord);
  void mulmodpoly(std::vector<modpoly>::const_iterator * it,int debut,int fin,environment * env,modpoly & pi);

  void divmodpoly(const modpoly & th, const gen & fact, modpoly & new_coord);
  void divmodpoly(const modpoly & th, const gen & fact, environment * env,modpoly & new_coord);
  modpoly operator / (const modpoly & th,const modpoly & other) ;
  bool DivRem(const modpoly & th, const modpoly & other, environment * env,modpoly & quo, modpoly & rem,bool allowrational=true);
  bool DenseDivRem(const modpoly & th, const modpoly & other,modpoly & quo, modpoly & rem,bool fastfalsetest=false);
  // Pseudo division a*th = other*quo + rem
  void PseudoDivRem(const dense_POLY1 & th, const dense_POLY1 & other, dense_POLY1 & quo, dense_POLY1 & rem, gen & a);
  modpoly operator / (const modpoly & th,const gen & fact ) ;
  modpoly operator_div (const modpoly & th,const modpoly & other,environment * env) ;
  modpoly operator % (const modpoly & th,const modpoly & other) ;
  modpoly operator_mod (const modpoly & th,const modpoly & other,environment * env) ;
  // division by ascending power (used for power series)
  dense_POLY1 AscPowDivRem(const dense_POLY1 & num, const dense_POLY1 & den,int order);

  modpoly powmod(const modpoly & p,const gen & n,const modpoly & pmod,environment * env);
  gen cstcoeff(const modpoly & q);
  // p=(X-x)q+p(x), first horner returns p(x), second one compute q as well
  gen horner(const gen & g,const gen & x);
  gen horner(const gen & g,const gen & x);
  gen hornermod(const vecteur & v,const gen & alpha,const gen & modulo);
  int hornermod(const vecteur & v,int alpha,int modulo);

  extern const unary_function_ptr * const  at_horner ;
  gen horner(const modpoly & p,const gen & x,environment * env,bool simp=true);
  gen horner(const modpoly & p,const gen & x);
  gen horner(const modpoly & p,const gen & x,environment * env,modpoly & q);
  gen horner(const modpoly & p,const fraction & f,bool simp);
  gen _horner(const gen & args,GIAC_CONTEXT);
  std::complex<double> horner_newton(const vecteur & p,const std::complex<double> &x,GIAC_CONTEXT); // x-p(x)/p'(x)

  void hornerfrac(const modpoly & p,const gen &num, const gen &den,gen & res,gen & d); // res/d=p(num/den)
  // find bounds for p(interval[l,r]) with p, l and r real and exact
  vecteur horner_interval(const modpoly & p,const gen & l,const gen & r);

  gen symb_horner(const modpoly & p,const gen & x,int d);
  gen symb_horner(const modpoly & p,const gen & x);
  // shift polynomial
  modpoly taylor(const modpoly & p,const gen & x,environment * env=0);
  // split P=Pp-Pn in two parts, Pp positive coeffs and Pn negative coeffs
  void splitP(const vecteur &P,vecteur &Pp,vecteur &Pn);

  gen norm(const dense_POLY1 & q,GIAC_CONTEXT); // max of |coeff|
  modpoly derivative(const modpoly & p);
  modpoly derivative(const modpoly & p,environment * env);
  // integration of p with coeff in *ascending* order, does not add 0
  // if you use it with usual modpoly, you must reverse(p.begin(),p.end())
  // before and after usage, set shift_coeff to 1, and push_back a zero
  modpoly integrate(const modpoly & p,const gen & shift_coeff);

  // ***************
  // GCD and related. Works currently only if modular aritmetic
  // ***************
  modpoly simplify(modpoly & a, modpoly & b,environment * env);
  gen ppz(dense_POLY1 & q);
  gen lgcd(const dense_POLY1 & p); // gcd of coeffs
  gen lgcd(const dense_POLY1 & p,const gen & g);
  // modular gcd
  void modpoly2smallmodpoly(const modpoly & p,std::vector<int> & v,int m);

  bool gcdmodpoly(const modpoly &p,const modpoly & q,environment * env,modpoly &a); 
  // 1-d modular for small modulus<sqrt(RAND_MAX)
  bool gcdsmallmodpoly(const polynome &p,const polynome & q,int m,polynome & d,polynome & dp,polynome & dq,bool compute_cof); 
  void smallmodpoly2modpoly(const std::vector<int> & v,modpoly & p,int m);
  void gcdsmallmodpoly(const std::vector<int> &p,const std::vector<int> & q,int m,std::vector<int> & d);
  void gcdsmallmodpoly(const std::vector<int> &p,const std::vector<int> & q,int m,std::vector<int> & d,std::vector<int> * pcof,std::vector<int> * qcof);
  void gcdsmallmodpoly(const std::vector<int> &p,const std::vector<int> & q,int m,std::vector<int> & d);
  void gcdsmallmodpoly(const modpoly &p,const modpoly & q,int m,modpoly & d);
  void DivRem(const std::vector<int> & th, const std::vector<int> & other,int m,std::vector<int> & quo, std::vector<int> & rem);
  modpoly gcd(const modpoly & a,const modpoly &b,environment * env); 
  // n-var modular gcd
  bool gcd_modular(const polynome &p_orig, const polynome & q_orig, polynome & pgcd,polynome & pcofactor,polynome & qcofactor,bool compute_cofactors);

  bool convert(const polynome &p_orig, const polynome & q_orig,index_t & d,std::vector<hashgcd_U> & vars,std::vector< T_unsigned<gen,hashgcd_U> > & p,std::vector< T_unsigned<gen,hashgcd_U> > & q);
  bool convert(const polynome &p_orig, const polynome & q_orig,index_t & d,std::vector<hashgcd_U> & vars,std::vector< T_unsigned<int,hashgcd_U> > & p,std::vector< T_unsigned<int,hashgcd_U> > & q,int modulo);
  bool modgcd(const polynome &p_orig, const polynome & q_orig, const gen & modulo, polynome & d,polynome & pcofactor,polynome & qcofactor,bool compute_cofactors);
  bool mod_gcd_c(const polynome &p_orig, const polynome & q_orig, const gen & modulo, polynome & d,polynome & pcofactor,polynome & qcofactor,bool compute_cofactors);
  bool mod_gcd(const polynome &p_orig, const polynome & q_orig, const gen & modulo, polynome & pgcd,polynome & pcofactor,polynome & qcofactor,bool compute_cofactors);
  modpoly lcm(const modpoly & a,const modpoly &b,environment * env); 
  bool gcd_modular_algo1(polynome &p,polynome &q,polynome &d,bool compute_cof);
  // p1*u+p2*v=d
  void egcd(const modpoly &p1, const modpoly & p2, environment * env,modpoly & u,modpoly & v,modpoly & d);
  bool egcd_pade(const modpoly & n,const modpoly & x,int l,modpoly & a,modpoly &b,environment * env,bool psron=true);
  // alg extension norme for p_y, alg extension defined by pmini
  bool algnorme(const polynome & p_y,const polynome & pmini,polynome & n);
  void subresultant(const modpoly & P,const modpoly & Q,gen & res);
  int sizeinbase2(const gen & g);
  int sizeinbase2(const vecteur & v);
  // multinomial power by Miller pure recurrence 
  bool miller_pow(const modpoly & p_,unsigned m,modpoly & res);

  // Given [v_0 ... v_(2n-1)] (begin of the recurrence sequence) 
  // return [b_n...b_0] such that b_n*v_{n+k}+...+b_0*v_k=0
  // Example [1,-1,3,3] -> [1,-3,-6]
  vecteur reverse_rsolve(const vecteur & v,bool psron=true);
  // given a and c, find u such that 
  // a[0]*...a[n-1]*u[n]+a[0]*...*a[n-2]*a[n]*u[n-1]+...+a[1]*...*a[n-1]*u[0]=1
  bool egcd(const std::vector<modpoly> & a, environment * env,std::vector<modpoly> & u);
  // same as above
  std::vector<modpoly> egcd(const std::vector<modpoly> & a, environment * env);
  // assuming pmod and qmod are prime together, find r such that
  // r = p mod pmod  and r = q mod qmod
  // hence r = p + A*pmod = q + B*qmod
  // or A*pmod -B*qmod = q - p
  // assuming u*pmod+v*pmod=d we get
  // A=u*(q-p)/d
  dense_POLY1 ichinrem(const dense_POLY1 &p,const dense_POLY1 & q,const gen & pmod,const gen & qmod);
  modpoly chinrem(const modpoly & p,const modpoly & q, const modpoly & pmod, const modpoly & qmod,environment * env);
  int ichinrem_inplace(dense_POLY1 &p,const std::vector<int> & q,const gen & pmod,int qmodval); // 0 error, 1 p changed, 2 p unchanged
  void divided_differences(const vecteur & x,const vecteur & y,vecteur & res,environment * env);
  // in-place modification and exact division if divexact==true
  void divided_differences(const vecteur & x,vecteur & res,environment * env,bool divexact);
  void interpolate(const vecteur & x,const vecteur & y,modpoly & res,environment * env);
  void interpolate_inplace(const vecteur & x,modpoly & res,environment * env);
  void mulpoly_interpolate(const polynome & p,const polynome & q,polynome & res,environment * env);
  bool dotvecteur_interp(const vecteur & a,const vecteur &b,gen & res);
  bool mmult_interp(const matrice & a,const matrice &b,matrice & res);
  bool poly_pcar_interp(const matrice & a,vecteur & p,bool compute_pmin,GIAC_CONTEXT);
  void polymat2matpoly(const vecteur & R,vecteur & res);

  // Fast Fourier Transform, f the poly sum_{j<n} f_j x^j, 
  // and w=[1,omega,...,omega^[m-1]] with m a multiple of n
  // return [f(1),f(omega),...,f(omega^[n-1])
  // WARNING f is given in ascending power
  void fft(const modpoly & f,const modpoly & w ,modpoly & res,environment * env);
  // void fft(std::vector< std::complex<double> >& f,const std::vector< std::complex<double> > & w ,std::vector<std::complex< double> > & res);
  void fft(std::complex<double> * f,int n,const std::complex<double> * w,int m,std::complex< double> * t);
  void fft(const std::vector<int> & f,const std::vector<int> & w ,std::vector<int> & res,int modulo);
  bool fft2mult(const std::vector<int> & a,const std::vector<int> & b,std::vector<int> & res,int modulo,bool reverseatend);

  // Convolution of p and q, omega a n-th root of unity, n=2^k
  // WARNING p0 and q0 are given in ascending power
  void fftconv(const modpoly & p,const modpoly & q,unsigned long k,unsigned long n,const gen & omega,modpoly & pq,environment * env);
  // Convolution of p and q, omega a n-th root of unity, n=2^k
  // p and q are given in descending power order
  void fftconv(const modpoly & p0,const modpoly & q0,unsigned long k,const gen & omega,modpoly & pq,environment * env);
  bool fftmult(const modpoly & p,const modpoly & q,modpoly & pq,int modulo=0);
  modpoly fftmult(const modpoly & p,const modpoly & q);
  // input A with positive int, output fft in A
  // w a 2^n-th root of unity mod p
  void fft2( int *A, int n, int w, int p );
  // float fft, theta should be +/-2*M_PI/n
  void fft2( std::complex<double> * A, int n, double theta );

#ifdef HAVE_LIBNTL
#ifdef HAVE_LIBPTHREAD
  extern pthread_mutex_t ntl_mutex;
#endif
  typedef gen inttype; 

  bool polynome2tab(const polynome & p,int deg,inttype * tab);
  polynome tab2polynome(const inttype * tab,int deg);
  void ininttype2ZZ(const inttype & temp,const inttype & step,NTL::ZZ & z,const NTL::ZZ & zzstep);
  NTL::ZZ inttype2ZZ(const inttype & i);
  void inZZ2inttype(const NTL::ZZ & zztemp,const NTL::ZZ & zzstep,inttype & temp,const inttype & step);
  inttype ZZ2inttype(const NTL::ZZ & z);
  NTL::ZZX tab2ZZX(const inttype * tab,int degree);
  void ZZX2tab(const NTL::ZZX & f,int & degree,inttype * & tab);

  // Z/nZ[X] conversions
  NTL::GF2X modpoly2GF2X(const modpoly & p);
  modpoly GF2X2modpoly(const NTL::GF2X & f);
  NTL::ZZ_pX modpoly2ZZ_pX(const modpoly & p);
  modpoly ZZ_pX2modpoly(const NTL::ZZ_pX & f);

#endif

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // NO_NAMESPACE_GIAC

#endif // _GIAC_MODPOLY_H
