/* -*- mode:C++ ; compile-command: "g++ -I.. -g -c threaded.cc" -*- */
/*  Multivariate GCD for large data not covered by the heuristic GCD algo
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

#ifndef _GIAC_THREADED_H_
#define _GIAC_THREADED_H_
#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#include "first.h"
/*
#ifndef WIN32
#if defined(__APPLE__) || defined(__FreeBSD__) || defined(VISUALC) | defined(__NetBSD__) 
#else // was #ifndef __APPLE__
#include <sys/sysinfo.h>
#endif
#endif // WIN32
*/
#include <iostream>
#include <algorithm>
#include <stdexcept>
#include "vector.h"
#include <map>
#include "monomial.h"
#ifdef HAVE_PTHREAD_H
#include <pthread.h>
#endif

#ifdef USE_GMP_REPLACEMENTS
#undef HAVE_GMPXX_H
#undef HAVE_LIBMPFR
#endif

#ifdef HAVE_GMPXX_H
#include <gmpxx.h>
#endif

#ifdef HAVE_SYS_TIME_H
#include <time.h>
#else
#define clock_t int
#define clock() 0
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  std::vector<int> operator % (const std::vector<int> & a,int modulo);
  std::vector<int> operator / (const std::vector<int> & v,const std::vector<int> & b);
  std::vector<int> operator % (const std::vector<int> & v,const std::vector<int> & b);
  bool operator > (const std::vector<int> & v,double q);

  class gen;
  class context;
  gen _heap_mult(const gen & g0,const context *);
  gen _modgcd_cachesize(const gen & g0,const context *);
  gen _debug_infolevel(const gen & g0,const context *);
  gen _background(const gen & g,const context *);

  // #define hashgcd_U unsigned int
  typedef unsigned int hashgcd_U; // replace with ulonglong for large index capacity on 32 bit CPU

#ifdef HAVE_GMPXX_H

  inline bool is_zero(const mpz_class & a){
    return a==0;
  }

  mpz_class invmod(const mpz_class & a,int reduce);

  mpz_class smod(const mpz_class & a,int reduce);


  inline void type_operator_times(const mpz_class & a,const mpz_class & b,mpz_class & c){
    // cerr << gen(c) << " = " << gen(a) << "*" << gen(b) << std::endl;
    mpz_mul(c.get_mpz_t(),a.get_mpz_t(),b.get_mpz_t());
    // cerr << gen(c) << std::endl;
  }

  inline void type_operator_reduce(const mpz_class & a,const mpz_class & b,mpz_class & c,int reduce){
    mpz_mul(c.get_mpz_t(),a.get_mpz_t(),b.get_mpz_t());
    if (reduce){
      c %= reduce;
    }
  }

  inline void type_operator_plus_times(const mpz_class & a,const mpz_class & b,mpz_class & c){
    // cerr << gen(c) << " += " << gen(a) << "*" << gen(b) << std::endl;
    // c+=a*b
    mpz_addmul(c.get_mpz_t(),a.get_mpz_t(),b.get_mpz_t());
    // cerr << gen(c) << std::endl;
  }

  inline void type_operator_plus_times_reduce(const mpz_class & a,const mpz_class & b,mpz_class & c,int reduce){
    // c+=a*b % reduce;
    mpz_addmul(c.get_mpz_t(),a.get_mpz_t(),b.get_mpz_t());
    if (reduce){
      c %= reduce;
    }
  }

#endif // HAVE__GMPXX_H

  int invmod(longlong a,int reduce);

#ifdef INT128
  
  inline bool is_zero(const int128_t & a){
    return a==0;
  }

  int invmod(int128_t a,int reduce);

  int128_t smod(int128_t & a,int reduce);

  inline void type_operator_times(const int128_t & a,const int128_t & b,int128_t & c){
    c = a*b;
  }

  inline void type_operator_reduce(const int128_t & a,const int128_t & b,int128_t & c,int reduce){
    c =a*b;
    if (reduce)
      c %= reduce;
  }

  inline void type_operator_plus_times(const int128_t & a,const int128_t & b,int128_t & c){
    c+=a*b;
  }

  inline void type_operator_plus_times_reduce(const int128_t & a,const int128_t & b,int128_t & c,int reduce){
    c +=a*b;
    if (reduce)
      c %= reduce;
  }

#endif


  bool is_zero(const std::vector<int> & v);

  // FIXME: put a #define in index.h if index_t=vector<int> and skip defs here
  // or make them as template
  std::vector<int> operator + (const std::vector<int> & a, const std::vector<int> & b);
  std::vector<int> operator - (const std::vector<int> & a, const std::vector<int> & b);
  std::vector<int> operator - (const std::vector<int> & a);
  inline void type_operator_times(const std::vector<int> & a,const std::vector<int> & b,std::vector<int> & c){
    c.clear();
#ifndef NO_STDEXCEPT
    setsizeerr("type_operator_times ext");
#endif
  }

  inline void type_operator_plus_times(const std::vector<int> & a,const std::vector<int> & b,std::vector<int> & c){
    c.clear();
#ifndef NO_STDEXCEPT
    setsizeerr("type_operator_reduce ext");
#endif
  }


  extern double heap_mult,modgcd_cachesize;
  // -1, -2, -3, force heap chains/multimap/map chains/heap multiplication
  // 0 always hashmap
  // >=1 use heap when product of number of monomials is >= value

  extern bool threads_allowed;
  extern int threads;

  extern int debug_infolevel;
  int invmod(int n,int modulo);
  int smod(int a,int b); // where b is assumed to be positive
  // v <- v*k % m
  void mulmod(std::vector<int> & v,int k,int m);

  inline void longlong2mpz(longlong i,mpz_t *mpzptr){
    int ii((long)i);
    if (i==ii)
      mpz_set_si(*mpzptr,ii);
    else {
      bool signe=(i<0);
      if (signe)
	i=-i;
      unsigned int i1=i>>32;
      unsigned int i2=(unsigned int)i;
      mpz_set_ui(*mpzptr,i1); // was mpz_init_set_ui probably a BUG
      mpz_mul_2exp(*mpzptr,*mpzptr,32);
      mpz_add_ui(*mpzptr,*mpzptr,i2);
      if (signe)
	mpz_neg(*mpzptr,*mpzptr);
    }
  }

  // tmp is an allocated mpz_t 
  inline void mpz2longlong(mpz_t * ptr,mpz_t * tmp,longlong & ans){
    int i=mpz_sgn(*ptr);
    if (i<0)
      mpz_neg(*ptr,*ptr);
    mpz_tdiv_q_2exp(*tmp,*ptr,31);
    ans=mpz_get_ui(*tmp);
    ans <<= 31;
    mpz_tdiv_r_2exp(*tmp,*ptr,31);
    ans += mpz_get_ui(*tmp);
    if (i<0){
      mpz_neg(*ptr,*ptr);
      ans = -ans;
    }
  }

  class my_mpz {
  public:
    mpz_t ptr;
    my_mpz(){ mpz_init(ptr); }
    my_mpz(long l){ mpz_init_set_si(ptr,l); }
    my_mpz(const my_mpz & z){ mpz_init_set(ptr,z.ptr); }
    ~my_mpz() { mpz_clear(ptr); }
    my_mpz operator - () const { my_mpz tmp(*this); mpz_neg(tmp.ptr,tmp.ptr); return tmp;}
    my_mpz & operator = (const my_mpz & a) {mpz_set(ptr,a.ptr); return *this;}
  };

  my_mpz operator % (const my_mpz & a,const my_mpz & b);
  my_mpz operator + (const my_mpz & a,const my_mpz & b);
  my_mpz operator - (const my_mpz & a,const my_mpz & b);
  my_mpz operator * (const my_mpz & a,const my_mpz & b);
  my_mpz operator / (const my_mpz & a,const my_mpz & b);
  my_mpz invmod(const my_mpz & a,int reduce);
  my_mpz smod(const my_mpz & a,int reduce);
  my_mpz operator %= (my_mpz & a,const my_mpz & b);
  my_mpz operator += (my_mpz & a,const my_mpz & b);
  my_mpz operator -= (my_mpz & a,const my_mpz & b);


  inline bool is_zero(const my_mpz & a){
    return !mpz_sgn(a.ptr);
  }

  inline void type_operator_times(const my_mpz & a,const my_mpz & b,my_mpz & c){
    mpz_mul(c.ptr,a.ptr,b.ptr);
    // c=a*b;
  }

  inline void type_operator_reduce(const my_mpz & a,const my_mpz & b,my_mpz & c,int reduce){
    mpz_mul(c.ptr,a.ptr,b.ptr);
    if (reduce)
      mpz_fdiv_r_ui(c.ptr,c.ptr,reduce);
  }

  inline void type_operator_plus_times(const my_mpz & a,const my_mpz & b,my_mpz & c){
    // c+=a*b
    mpz_addmul(c.ptr,a.ptr,b.ptr);
  }

  inline void type_operator_plus_times_reduce(const my_mpz & a,const my_mpz & b,my_mpz & c,int reduce){
    // c+=a*b % reduce;
    mpz_addmul(c.ptr,a.ptr,b.ptr);
    if (reduce)
      mpz_fdiv_r_ui(c.ptr,c.ptr,reduce);
  }

  template<class T,class U>
  struct T_unsigned {
    T g;
    U u;
    T_unsigned(const T & myg,const U & myu): g(myg),u(myu) {};
    T_unsigned(): g(0),u(0) {};
    bool operator == (const T_unsigned<T,U> & tu) const { return g==tu.g && u==tu.u; }
  };

  // warning, < is > so that monomial ordering is ok after back-conversion
  template <class T,class U>
  inline bool operator < (const T_unsigned<T,U> & gu1,const T_unsigned<T,U> & gu2){
    return gu1.u > gu2.u;
  }
  typedef T_unsigned<int,unsigned> int_unsigned;
    
  template<class T,class U>
  std::ostream & operator << (std::ostream & os, const std::vector< T_unsigned<T,U> > & v){  
    typename std::vector< T_unsigned<T,U> >::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      os << "(" << it->g << "," << it->u << "),";
    }
    return os << std::endl;
  }

#ifdef VISUALC
  template<class T>
  class vector_size64:public std::vector<T>{
  };
  template<class T>
  class vector_size32:public std::vector<T>{
  };
#else
  // "smart" vector, T must be a type of size 64bits, sizeof(T)=8
  // and vector must be of size 192 bits, sizeof=24
  template<class T>
  class vector_size64 {
  public:
    longlong taille;
    T v0;
    T v1;
    vector_size64(){ taille=1;}
    vector_size64(size_t t){ 
      if (t>2){ 
	taille=0; * (longlong *) &v0=0; * (longlong *) &v1=0; 
	std::vector<T> tmp(t); 
	std::vector<T> & v = * (std::vector<T> *) &taille;
	std::swap(tmp,v); 
      }
      else { taille=2*t+1; * (longlong *) &v0=0; * (longlong *) &v1=0;}
    }
    vector_size64(size_t t,const T & elem){ 
      if (t>2){ 
	taille=0; * (longlong *) &v0=0; * (longlong *) &v1=0; 
	std::vector<T> tmp(t,elem); 
	std::vector<T> & v = * (std::vector<T> *) &taille;
	std::swap(tmp,v); 
      }
      else { taille=2*t+1; v0=elem; v1=elem;}
    }
    ~vector_size64(){
      if (taille%2==0){
	std::vector<T> & v = * (std::vector<T> *) &taille;
	std::vector<T> tmp;
	std::swap(tmp,v);
      }
    }
    void push_back(T t){
      if (taille %2){
	if (taille==5){
	  std::vector<T> tmp(4);
	  tmp.front()=v0;
	  tmp[1]=v1;
	  tmp[2]=t;
	  tmp.pop_back();
	  taille=0; * (longlong *) &v0=0; * (longlong *) &v1=0; 
	  std::vector<T> & v = * (std::vector<T> *) &taille;
	  std::swap(tmp,v);
	  return;
	}
	taille += 2;
	if (taille==5)
	  v1=t;
	else 
	  v0=t;
      }
      else {
	std::vector<T> & v = * (std::vector<T> *) &taille;
	v.push_back(t);
      }
    }
    void pop_back(){
      if (taille%2)
	taille -=2;
      else {
	std::vector<T> & v = * (std::vector<T> *) &taille;
	v.pop_back();
      }
    }
    void clear(){
      if (taille%2)
	taille=1;
      else {
	std::vector<T> & v = * (std::vector<T> *) &taille;
	v.clear();
      }
    }
    T operator [] (size_t pos) const {
      if (taille%2)
	return pos?v1:v0;
      else {
	std::vector<T> & v = * (std::vector<T> *) &taille;
	return v[pos];
      }
    }
    typename std::vector<T>::const_iterator begin() const {
      if (taille %2)
	return typename std::vector<T>::const_iterator(&v0);
      else {
	std::vector<T> & v = * (std::vector<T> *) &taille;
	return v.begin();
      }
    }
    typename std::vector<T>::iterator begin() {
      if (taille %2)
	return typename std::vector<T>::iterator(&v0);
      else {
	std::vector<T> & v = * (std::vector<T> *) &taille;
	return v.begin();
      }
    }
    typename std::vector<T>::const_iterator end() const {
      if (taille %2)
	return typename std::vector<T>::const_iterator(&v0+taille/2);
      else {
	std::vector<T> & v = * (std::vector<T> *) &taille;
	return v.end();
      }
    }
    typename std::vector<T>::iterator end() {
      if (taille %2)
	return typename std::vector<T>::iterator(&v0+taille/2);
      else {
	std::vector<T> & v = * (std::vector<T> *) &taille;
	return v.end();
      }
    }
  };

  // vector of type T of size 32 bits (sizeof==4), not too large
  template<class T>
  struct mytab{
    T * tab;
    unsigned int _size;
    unsigned int _capacity;
  };

  template<class T>
  class vector_size32 {
  public:
    struct {
      unsigned int taille;
      T v0;
      T v1;
      T v2;
    };
    vector_size32(){ taille=1;}
    vector_size32(size_t t){ 
      if (t>3){
	mytab<T> * mytabptr = (mytab<T> *) this;
	mytabptr->tab = new T[t];
	mytabptr->_size = mytabptr->_capacity = t;
	int * ptr=(int *)mytabptr->tab;
	for (size_t i=0;i<t;++ptr,++i)
	  *ptr=0;
      }
      else { taille=2*t+1; * (int *) &v0=0; * (longlong *) &v1=0;}
    }
    vector_size32(size_t t,const T & elem){ 
      if (t>3){ 
	mytab<T> * mytabptr = (mytab<T> *) this;
	mytabptr->tab = new T[t];
	mytabptr->_size = mytabptr->_capacity = t;
	T * ptr=mytabptr->tab;
	for (size_t i=0;i<t;++ptr,++i)
	  *ptr=elem;
      }
      else { taille=2*t+1; v2=v1=v0=elem;}
    }
    ~vector_size32(){
      if (taille%2==0){
	mytab<T> * mytabptr = (mytab<T> *) this;
	delete [] mytabptr->tab;
      }
    }
    void push_back(T t){
      if (taille %2){
	if (taille==7){
	  T * ptr = new T[6];
	  *ptr=v0; ++ptr;
	  *ptr=v1; ++ptr;
	  *ptr=v2; ++ptr;
	  *ptr=t;
	  mytab<T> * mytabptr = (mytab<T> *) this;
	  mytabptr->tab = ptr-3;
	  mytabptr->_size = 4;
	  mytabptr->_capacity = 6;
	  return;
	}
	taille += 2;
	if (taille==7)
	  v2=t;
	else { 
	  if (taille==5) v1=t; else v0=t; 
	}
      }
      else {
	mytab<T> * mytabptr = (mytab<T> *) this;
	if (mytabptr->_size>=mytabptr->_capacity){
	  mytabptr->_capacity *=2;
	  T * ptr = new T[mytabptr->_capacity];
	  memcpy(ptr,mytabptr->tab,mytabptr->_size*sizeof(T));
	  delete [] mytabptr->tab;
	  mytabptr->tab=ptr;
	}
	mytabptr->tab[mytabptr->_size]=t;
	++mytabptr->_size;
      }
    }
    void pop_back(){
      if (taille%2)
	taille -=2;
      else {
	mytab<T> * mytabptr = (mytab<T> *) this;
	--mytabptr->_size;
      }
    }
    void clear(){
      if (taille%2)
	taille=1;
      else {
	mytab<T> * mytabptr = (mytab<T> *) this;
	mytabptr->_size=0;
      }
    }
    T operator [] (size_t pos) const {
      if (taille%2)
	return pos?((pos==2)?v2:v1):v0;
      else {
	mytab<T> * mytabptr = (mytab<T> *) this;
	return mytabptr->tab[pos];
      }
    }
    typename std::vector<T>::const_iterator begin() const {
      if (taille %2)
	return typename std::vector<T>::const_iterator(&v0);
      else {
	mytab<T> * mytabptr = (mytab<T> *) this;
	return typename std::vector<T>::const_iterator(mytabptr->tab);
      }
    }
    typename std::vector<T>::iterator begin() {
      if (taille %2)
	return typename std::vector<T>::iterator(&v0);
      else {
	mytab<T> * mytabptr = (mytab<T> *) this;
	return typename std::vector<T>::iterator(mytabptr->tab);
      }
    }
    typename std::vector<T>::const_iterator end() const {
      if (taille %2)
	return typename std::vector<T>::const_iterator(&v0+taille/2);
      else {
	mytab<T> * mytabptr = (mytab<T> *) this;
	return typename std::vector<T>::const_iterator(mytabptr->_tab+mytabptr->_size);
      }
    }
    typename std::vector<T>::iterator end() {
      if (taille %2)
	return typename std::vector<T>::iterator(&v0+taille/2);
      else {
	mytab<T> * mytabptr = (mytab<T> *) this;
	return typename std::vector<T>::iterator(mytabptr->tab+mytabptr->_size);
      }
    }
  };
#endif

  class hash_function_unsigned_object {
  public:
    // inline size_t operator () (size_t x) const { return x; }
    inline size_t operator () (int x) const { return x; }
    inline size_t operator () (unsigned x) const { return x; }
    inline size_t operator () (long long unsigned x) const { return x%12345701; }
    inline size_t operator () (long long x) const { return x%12345701; }
    hash_function_unsigned_object() {};
  };

  bool mod_gcd(const std::vector< T_unsigned<int,hashgcd_U> > & p0,const std::vector< T_unsigned<int,hashgcd_U> > & q0,int modulo,std::vector< T_unsigned<int,hashgcd_U> > & pgcd, std::vector< T_unsigned<int,hashgcd_U> > & pcof, std::vector< T_unsigned<int,hashgcd_U> > & qcof,const std::vector<hashgcd_U> & vars, bool compute_cofactors,int nthreads);

  bool mod_gcd(const std::vector< T_unsigned<int,hashgcd_U> > & p_orig,const std::vector< T_unsigned<int,hashgcd_U> > & q_orig,int modulo,std::vector< T_unsigned<int,hashgcd_U> > & d, std::vector< T_unsigned<int,hashgcd_U> > & pcofactor, std::vector< T_unsigned<int,hashgcd_U> > & qcofactor,const std::vector<hashgcd_U> & vars, bool compute_pcofactor,bool compute_qcofactor,int nthreads);

  bool gcd(const std::vector< T_unsigned<gen,hashgcd_U> > & p_orig,const std::vector< T_unsigned<gen,hashgcd_U> > & q_orig,std::vector< T_unsigned<gen,hashgcd_U> > & d, std::vector< T_unsigned<gen,hashgcd_U> > & pcofactor, std::vector< T_unsigned<gen,hashgcd_U> > & qcofactor,const std::vector<hashgcd_U> & vars, bool compute_cofactors,int nthreads=1);

  bool gcd_ext(const std::vector< T_unsigned<gen,hashgcd_U> > & p_orig,const std::vector< T_unsigned<gen,hashgcd_U> > & q_orig,std::vector< T_unsigned<gen,hashgcd_U> > & d, std::vector< T_unsigned<gen,hashgcd_U> > & pcofactor, std::vector< T_unsigned<gen,hashgcd_U> > & qcofactor,const std::vector<hashgcd_U> & vars, bool compute_cofactors,int nthreads=1);

  template<class T,class U>
  void smalladd(const std::vector< T_unsigned<T,U> > & v1,const std::vector< T_unsigned<T,U> > & v2,std::vector< T_unsigned<T,U> > & v){
    if (&v1==&v || &v2==&v){
      std::vector< T_unsigned<T,U> > tmp;
      smalladd(v1,v2,tmp);
      std::swap< std::vector< T_unsigned<T,U> > >(v,tmp);
      return;
    }
    typename std::vector< T_unsigned<T,U> >::const_iterator it1=v1.begin(),it1end=v1.end(),it2=v2.begin(),it2end=v2.end();
    T g;
    v.clear();
    v.reserve((it1end-it1)+(it2end-it2)); // worst case
    for (;it1!=it1end && it2!=it2end;){
      if (it1->u==it2->u){
	g=it1->g+it2->g;
	if (!is_zero(g))
	  v.push_back(T_unsigned<T,U>(g,it1->u));
	++it1;
	++it2;
      }
      else {
	if (it1->u>it2->u){
	  v.push_back(*it1);
	  ++it1;
	}
	else {
	  v.push_back(*it2);
	  ++it2;
	}
      }
    }
    for (;it1!=it1end;++it1)
      v.push_back(*it1);
    for (;it2!=it2end;++it2)
      v.push_back(*it2);
  }

  template<class T,class U>
  void smalladd(const std::vector< T_unsigned<T,U> > & v1,const std::vector< T_unsigned<T,U> > & v2,std::vector< T_unsigned<T,U> > & v,int reduce){
    if (&v1==&v || &v2==&v){
      std::vector< T_unsigned<T,U> > tmp;
      smalladd(v1,v2,tmp,reduce);
      std::swap< std::vector< T_unsigned<T,U> > >(v,tmp);
      return;
    }
    typename std::vector< T_unsigned<T,U> >::const_iterator it1=v1.begin(),it1end=v1.end(),it2=v2.begin(),it2end=v2.end();
    T g;
    v.clear();
    v.reserve((it1end-it1)+(it2end-it2)); // worst case
    for (;it1!=it1end && it2!=it2end;){
      if (it1->u==it2->u){
	g=it1->g+it2->g;
	g=g%reduce;
	if (!is_zero(g))
	  v.push_back(T_unsigned<T,U>(g,it1->u));
	++it1;
	++it2;
      }
      else {
	if (it1->u>it2->u){
	  v.push_back(*it1);
	  ++it1;
	}
	else {
	  v.push_back(*it2);
	  ++it2;
	}
      }
    }
    for (;it1!=it1end;++it1)
      v.push_back(*it1);
    for (;it2!=it2end;++it2)
      v.push_back(*it2);
  }

  template<class T,class U>
  void smallsub(const std::vector< T_unsigned<T,U> > & v1,const std::vector< T_unsigned<T,U> > & v2,std::vector< T_unsigned<T,U> > & v){
    if (&v1==&v || &v2==&v){
      std::vector< T_unsigned<T,U> > tmp;
      smallsub(v1,v2,tmp);
      std::swap< std::vector< T_unsigned<T,U> > >(v,tmp);
      return;
    }
    typename std::vector< T_unsigned<T,U> >::const_iterator it1=v1.begin(),it1end=v1.end(),it2=v2.begin(),it2end=v2.end();
    T g;
    v.clear();
    v.reserve((it1end-it1)+(it2end-it2)); // worst case
    for (;it1!=it1end && it2!=it2end;){
      if (it1->u==it2->u){
	g=it1->g-it2->g;
	if (!is_zero(g))
	  v.push_back(T_unsigned<T,U>(g,it1->u));
	++it1;
	++it2;
      }
      else {
	if (it1->u>it2->u){
	  v.push_back(*it1);
	  ++it1;
	}
	else {
	  v.push_back(T_unsigned<T,U>(-it2->g,it2->u));
	  ++it2;
	}
      }
    }
    for (;it1!=it1end;++it1)
      v.push_back(*it1);
    for (;it2!=it2end;++it2)
      v.push_back(T_unsigned<T,U>(-it2->g,it2->u));
  }

  template<class T,class U,class R>
  void smallsub(const std::vector< T_unsigned<T,U> > & v1,const std::vector< T_unsigned<T,U> > & v2,std::vector< T_unsigned<T,U> > & v,const R & reduce){
    if (&v1==&v || &v2==&v){
      std::vector< T_unsigned<T,U> > tmp;
      smallsub(v1,v2,tmp,reduce);
      std::swap< std::vector< T_unsigned<T,U> > >(v,tmp);
      return;
    }
    typename std::vector< T_unsigned<T,U> >::const_iterator it1=v1.begin(),it1end=v1.end(),it2=v2.begin(),it2end=v2.end();
    T g;
    v.clear();
    v.reserve((it1end-it1)+(it2end-it2)); // worst case
    for (;it1!=it1end && it2!=it2end;){
      if (it1->u==it2->u){
	g=it1->g-it2->g;
	g=g%reduce;
	if (!is_zero(g))
	  v.push_back(T_unsigned<T,U>(g,it1->u));
	++it1;
	++it2;
      }
      else {
	if (it1->u>it2->u){
	  v.push_back(*it1);
	  ++it1;
	}
	else {
	  v.push_back(T_unsigned<T,U>(-it2->g,it2->u));
	  ++it2;
	}
      }
    }
    for (;it1!=it1end;++it1)
      v.push_back(*it1);
    for (;it2!=it2end;++it2)
      v.push_back(T_unsigned<T,U>(-it2->g,it2->u));
  }

  template<class T,class U>
  void smallmult(const T & g,const std::vector< T_unsigned<T,U> > & v1,std::vector< T_unsigned<T,U> > & v){
    if (is_zero(g)){
      v.clear();
      return;
    }
    typename std::vector< T_unsigned<T,U> >::const_iterator it1=v1.begin(),it1end=v1.end();
    if (&v1==&v){
      typename std::vector< T_unsigned<T,U> >::iterator it1=v.begin(),it1end=v.end();
      for (;it1!=it1end;++it1){
	it1->g = g*it1->g;
      }
    }
    else {
      v.clear();
      v.reserve(it1end-it1); // worst case
      for (;it1!=it1end;++it1){
	v.push_back(T_unsigned<T,U>(g*it1->g,it1->u));
      }
    }
  }

  template<class T,class U,class R>
  void smallmult(const T & g,const std::vector< T_unsigned<T,U> > & v1,std::vector< T_unsigned<T,U> > & v,const R & reduce){
    if (is_zero(g)){
      v.clear();
      return;
    }
    typename std::vector< T_unsigned<T,U> >::const_iterator it1=v1.begin(),it1end=v1.end();
    if (&v1==&v){
      typename std::vector< T_unsigned<T,U> >::iterator it1=v.begin(),it1end=v.end();
      for (;it1!=it1end;++it1){
	type_operator_reduce(g,it1->g,it1->g,reduce);
	// it1->g = (g*it1->g) % reduce;
      }
    }
    else {
      v.clear();
      v.reserve(it1end-it1); // worst case
      T res;
      for (;it1!=it1end;++it1){
	type_operator_reduce(g,it1->g,res,reduce);
	v.push_back(T_unsigned<T,U>(res,it1->u));
	// v.push_back(T_unsigned<T,U>((g*it1->g)%reduce,it1->u));
      }
    }
  }

  template<class T,class U>
  void smalldiv(const std::vector< T_unsigned<T,U> > & v1,const T & g,std::vector< T_unsigned<T,U> > & v){
    typename std::vector< T_unsigned<T,U> >::const_iterator it1=v1.begin(),it1end=v1.end();
    if (&v1==&v){
      typename std::vector< T_unsigned<T,U> >::iterator it1=v.begin(),it1end=v.end();
      for (;it1!=it1end;++it1){
	it1->g = it1->g/g;
      }
    }
    else {
      v.clear();
      v.reserve(it1end-it1); // worst case
      for (;it1!=it1end;++it1){
	v.push_back(T_unsigned<T,U>(it1->g/g,it1->u));
      }
    }
  }

  template<class T,class U>
  void smallshift(const std::vector< T_unsigned<T,U> > & v1,U shift,std::vector< T_unsigned<T,U> > & v){
    typename std::vector< T_unsigned<T,U> >::const_iterator it1=v1.begin(),it1end=v1.end();
    if (&v1==&v){
      typename std::vector< T_unsigned<T,U> >::iterator it1=v.begin(),it1end=v.end();
      for (;it1!=it1end;++it1){
	it1->u += shift;
      }
    }
    else {
      v.clear();
      v.reserve(it1end-it1); // worst case
      for (;it1!=it1end;++it1){
	v.push_back(T_unsigned<T,U>(it1->g,it1->u+shift));
      }
    }
  }

  // eval v1 at vars.back()=g
  // should be used with reduce!=0, T=int or longlong 
  // (powmod should be defined for T,U,T)
  // * should not overflow in T, if T=int, reduce must be < 46340
  // this could be fixed using type_operator_... instead of *
  template<class T,class U>
  void smallhorner(const std::vector< T_unsigned<T,U> > & v1,const T & g,const std::vector<U> & vars,std::vector< T_unsigned<T,U> > & v,int reduce){
    typename std::vector< T_unsigned<T,U> >::const_iterator it=v1.begin(),itend=v1.end();
    U deg=vars.back();
    v.clear();
    v.reserve((itend-it)/deg);
    for (;it!=itend;){
      // find smallest possible monomial degree for next element of v
      U minu=(it->u/deg)*deg;
      U prevdiffu=it->u-minu,diffu;
      T tmp;
      for (;it!=itend;++it){
	if (it->u<minu){
	  if (prevdiffu)
	    tmp = tmp*powmod(g,prevdiffu,reduce);
	  prevdiffu=0;
	  tmp = tmp%reduce;
	  v.push_back(T_unsigned<T,U>(tmp,minu));
	  break;
	}
	diffu=it->u-minu;
	if (prevdiffu!=diffu){
	  if (prevdiffu==diffu+1)
	    tmp = tmp*g;
	  else
	    tmp = tmp*powmod(g,prevdiffu-diffu,reduce);
	}
	tmp += it->g;
	tmp = tmp%reduce;
	prevdiffu=diffu;
	if (!diffu){
	  v.push_back(T_unsigned<T,U>(tmp,minu));
	  break;
	}
      }
      if (prevdiffu){
	tmp=tmp*powmod(g,prevdiffu,reduce)%reduce;
	v.push_back(T_unsigned<T,U>(tmp,minu));
      }
    }
  }

  // eval v1 at vars.back()=g
  // should be used with T=gen 
  template<class T,class U>
  void smallhorner(const std::vector< T_unsigned<T,U> > & v1,const T & g,const std::vector<U> & vars,std::vector< T_unsigned<T,U> > & v){
    typename std::vector< T_unsigned<T,U> >::const_iterator it=v1.begin(),itend=v1.end();
    U deg=vars.back();
    v.clear();
    v.reserve((itend-it)/deg);
    for (;it!=itend;){
      // find smallest possible monomial degree for next element of v
      U minu=(it->u/deg)*deg;
      U prevdiffu=it->u-minu,diffu;
      T tmp(0);
      for (;it!=itend;){
	if (it->u<minu){
	  if (prevdiffu)
	    tmp = tmp*pow(g,(unsigned long) prevdiffu);
	  prevdiffu=0;
	  v.push_back(T_unsigned<T,U>(tmp,minu));
	  break;
	}
	diffu=it->u-minu;
	if (prevdiffu!=diffu){
	  if (prevdiffu==diffu+1)
	    tmp = tmp*g;
	  else
	    tmp = tmp*pow(g,(unsigned long)prevdiffu-diffu);
	}
	tmp += it->g;
	++it;
	prevdiffu=diffu;
	if (!diffu){
	  v.push_back(T_unsigned<T,U>(tmp,minu));
	  break;
	}
      }
      if (prevdiffu){
	tmp=tmp*pow(g,(unsigned long)prevdiffu);
	v.push_back(T_unsigned<T,U>(tmp,minu));
      }
    }
  }

  template<class T,class U,class R>
  void smallmult(const std::vector< T_unsigned<T,U> > & v1,const std::vector< T_unsigned<T,U> > & v2,std::vector< T_unsigned<T,U> > & v,const R & reduce,size_t possible_size);

  template<class T,class U>
  void smallmulpoly_interpolate(const std::vector< T_unsigned<T,U> > & v1,const std::vector< T_unsigned<T,U> > & v2,std::vector< T_unsigned<T,U> > & v,const std::vector<U> & vars,const index_t & vdeg,int reduce){
    int dim=vars.size();
    if (dim==1){
      smallmult(v1,v2,v,reduce,0);
      return;
    }
    std::vector<U> vars1=vars;
    vars1.pop_back();
    int s=vdeg[dim-1];
    v.clear();
    std::vector< T_unsigned<T,U> > tmp2,tmp3;
    std::vector< T_unsigned<T,U> > * tab = new std::vector< T_unsigned<T,U> >[s];
    for (int alpha=0;alpha<s;++alpha){
      smallhorner<T,U>(v1,alpha,vars,tmp2,reduce);
      smallhorner<T,U>(v2,alpha,vars,tmp3,reduce);
      smallmulpoly_interpolate<T,U>(tmp2,tmp3,tab[alpha],vars1,vdeg,reduce);
      std::cerr << alpha << ":" << tab[alpha] << std::endl;
    }
    // divided differences
    for (int k=1;k<s;++k){
      std::cerr << k << std::endl;
      for (int j=s-1;j>=k;--j){
	smallsub(tab[j],tab[j-1],tmp2,reduce);
	smallmult(invmod(k,reduce),tmp2,tab[j],reduce);
	std::cerr << tab[j] ;
      }
    }
    // interpolation
    for (int alpha=s-1;alpha>=0;--alpha){
      smallmult<T,U>(-alpha,v,tmp2,reduce); 
      smallshift(v,U(1),v); // multiply v*(x-alpha)
      smalladd<T,U>(v,tmp2,tmp3,reduce);
      smalladd<T,U>(tmp3,tab[alpha],v,reduce);
    }
    delete [] tab;
  }

  template<class T,class U>
  void smallmulpoly_interpolate(const std::vector< T_unsigned<T,U> > & v1,const std::vector< T_unsigned<T,U> > & v2,std::vector< T_unsigned<T,U> > & v,const std::vector<U> & vars,const index_t & vdeg){
    int dim=vars.size();
    if (dim==1){
      smallmult(v1,v2,v,0,0);
      return;
    }
    std::vector<U> vars1=vars;
    vars1.pop_back();
    int s=vdeg[dim-1];
    v.clear();
    std::vector< T_unsigned<T,U> > tmp2,tmp3;
    std::vector< T_unsigned<T,U> > * tab = new std::vector< T_unsigned<T,U> >[s];
    for (int alpha=0;alpha<s;++alpha){
      smallhorner<T,U>(v1,alpha,vars,tmp2);
      smallhorner<T,U>(v2,alpha,vars,tmp3);
      smallmulpoly_interpolate<T,U>(tmp2,tmp3,tab[alpha],vars1,vdeg);
      // std::cerr << tab[alpha] << std::endl;
    }
    // divided differences
    for (int k=1;k<s;++k){
      for (int j=s-1;j>=k;--j){
	smallsub(tab[j],tab[j-1],tmp2);
	smalldiv(tmp2,T(k),tab[j]);
      }
    }
    // interpolation
    for (int alpha=s-1;alpha>=0;--alpha){
      smallmult<T,U>(-alpha,v,tmp2); 
      smallshift(v,U(1),v); // multiply v*(x-alpha)
      smalladd<T,U>(v,tmp2,tmp3);
      smalladd<T,U>(tmp3,tab[alpha],v);
    }
    delete [] tab;
  }

  template<class T,class U>
  void smallmulpoly_interpolate(const std::vector< T_unsigned<T,U> > & v1,const std::vector< T_unsigned<T,U> > & v2,std::vector< T_unsigned<T,U> > & v,const index_t & vdeg,int reduce){
    int dim=vdeg.size();
    std::vector<U> vars(dim);
    vars.back()=vdeg.back();
    for (int i=dim-1;i>0;--i){
      vars[i-1]=vars[i]*vdeg[i-1];
    }
    smallmulpoly_interpolate(v1,v2,v,vars,vdeg,reduce);
  }

  template<class T,class U>
  void smallmulpoly_interpolate(const std::vector< T_unsigned<T,U> > & v1,const std::vector< T_unsigned<T,U> > & v2,std::vector< T_unsigned<T,U> > & v,const index_t & vdeg){
    int dim=vdeg.size();
    std::vector<U> vars(dim);
    vars.back()=vdeg.back();
    for (int i=dim-1;i>0;--i){
      vars[i-1]=vars[i]*vdeg[i-1];
    }
    smallmulpoly_interpolate(v1,v2,v,vars,vdeg);
  }

  template<class U>
  struct u_pair_index {
    U u;
    unsigned i1,i2;
    u_pair_index(unsigned _i1,unsigned _i2,U _u):u(_u),i1(_i1),i2(_i2) {}
    u_pair_index():u(0),i1(0),i2(0) {}
  };
  template<class U>
  inline bool operator < (const u_pair_index<U> & p1,const u_pair_index<U> & p2){
    return p1.u<p2.u;
  }

  template<class U>
  inline bool operator <= (const u_pair_index<U> & p1,const u_pair_index<U> & p2){
    return p1.u<=p2.u;
  }

  template<class T>
  void in_out_heap(T * tab,unsigned size,T value){
    unsigned childindex=2,holeindex=0;
    while (childindex<size){
      // find largest child until end of tab
      register T * ptr=tab+childindex;
      if (*ptr<*(ptr-1)){
	--childindex;
	--ptr;
      }
      *(tab+holeindex)=*ptr;
      holeindex=childindex;
      childindex = (holeindex+1) << 1;
    }
    if (childindex==size){
      --childindex;
      *(tab+holeindex)=*(tab+childindex);
      holeindex=childindex;
    }
    // now the hole is at the bottom, replace it with value and promote value
    *(tab+holeindex)=value;
    // childindex is now the parent
    while (holeindex){
      childindex=(holeindex-1) >> 1;
      if (*(tab+holeindex)<=*(tab+childindex))
	break;
      std::swap<T>(*(tab+childindex),*(tab+holeindex));
      holeindex=childindex;
    }
  }

  template<class U>
  inline bool inverse_order(const U & u1,const U & u2){
    return u1<u2;
  }

  template<class U>
  struct U_unsigned {
    U u;
    unsigned v;
    U_unsigned(U _u,unsigned _v):u(_u),v(_v) {}
    U_unsigned():u(0) {}
  };
  template<class U>
  inline bool operator < (const U_unsigned<U> & p1,const U_unsigned<U> & p2){
    return p1.u<p2.u;
  }

  // Possible improvement for threaded execution:
  // make each j of the k threads compute terms of the product with
  // degree wrt 1st var = j % k
  // at the end merge the results
  // For this, we might mark positions in v1 and v2 where the degree
  // wrt to the 1st var changes
  template<class T,class U,class R>
  void smallmult(const std::vector< T_unsigned<T,U> > & v1,const std::vector< T_unsigned<T,U> > & v2,std::vector< T_unsigned<T,U> > & v,const R & reduce,size_t possible_size){
    if (&v1==&v || &v2==&v){
      std::vector< T_unsigned<T,U> > tmp;
      smallmult(v1,v2,tmp,reduce,possible_size);
      std::swap< std::vector< T_unsigned<T,U> > >(v,tmp);
      return;
    }
    typename std::vector< T_unsigned<T,U> >::const_iterator it1beg=v1.begin(),it1=v1.begin(),it1end=v1.end(),it2beg=v2.begin(),it2,it2end=v2.end();
    T g1,g;
    U u1=it1beg->u,u2=it2beg->u,u;
    v.clear();
    unsigned v1s=it1end-it1beg,v2s=it2end-it2beg;
    double v1v2=double(v1s)*v2s;
    U u12=u1+u2; // size of the array for array multiplication
    // compare u12 and v1v2*ln(v1v2)
    if ( heap_mult>=0 && possible_size && u12<512e6/sizeof(T) && std::log(double(std::min(v1s,v2s)))/std::log(2.0)>1+2*u12/v1v2){
      if (debug_infolevel>20)
	std::cerr << "array multiplication, v1 size " << v1s << " v2 size " << v2s << " u1+u2 " << u12 << std::endl;
      // array multiplication
      T * prod = new T[unsigned(u12+1)];
      for (u=0;u<=u12;++u)
	prod[u]=T(0);
      for (it1=it1beg;it1!=it1end;++it1){
	g1=it1->g;
	u1=it1->u;
	if (is_zero(reduce)){
	  for (it2=it2beg;it2!=it2end;++it2){
	    type_operator_plus_times(g1,it2->g,prod[u1+it2->u]);
	  }
	}
	else {
	  for (it2=it2beg;it2!=it2end;++it2){
	    type_operator_plus_times_reduce(g1,it2->g,prod[u1+it2->u],reduce);
	  }
	}
      }
      int n=0;
      for (u=u12;;--u){
	if (!is_zero(prod[u]))
	  ++n;
	if (!u)
	  break;
      }
      v.reserve(n);
      for (u=u12;;--u){
	if (!is_zero(prod[u]))
	  v.push_back(T_unsigned<T,U>(prod[u],u));
	if (!u)
	  break;
      }
      delete [] prod ;
      return;
    }
    bool use_heap=(heap_mult>0 && v1v2>=heap_mult);
    if (debug_infolevel>20){
      std::cerr << "// " << clock() << "using ";
      if (use_heap)
	std::cerr << "heap";
      else
	std::cerr << heap_mult;
      std::cerr<< " multiplication" << std::endl;
    }
    if (heap_mult<0 || use_heap){
      if (v1s>v2s){
	smallmult(v2,v1,v,reduce,possible_size);
	return;
      }
      if (v1s<128)
	use_heap=false; // use heap instead of heap of chains
      if (heap_mult==-1 || use_heap ){
	// using heap of chains
	// std::vector< vector_size64< std::pair<unsigned,unsigned> > > vindex(v1s);
	std::vector< std::vector< std::pair<unsigned,unsigned> > > vindex(v1s);
	double count1=0,count2=0,total=double(v1s)*v2s;
	U_unsigned<U> * heap = new U_unsigned<U>[v1s] ; // pointers to v2 monomials
	U_unsigned<U> * heap0, *heapbeg=heap,* heapend=heap+v1s;
	for (it1=it1beg,heap0=heap;heap0!=heapend;++heap0,++it1){
	  // vindex[it1-it1beg]=vector_size64< std::pair<unsigned,unsigned> >(1,std::pair<unsigned,unsigned>(it1-it1beg,0));
	  vindex[it1-it1beg]=std::vector< std::pair<unsigned,unsigned> >(1,std::pair<unsigned,unsigned>(it1-it1beg,0));
	  *heap0=U_unsigned<U>(it1->u+u2,it1-it1beg);
	}
	// vector_size64< std::pair<unsigned,unsigned> > nouveau;
	std::vector< std::pair<unsigned,unsigned> > nouveau;
	for (;heapbeg!=heapend;){
	  U topu=heapbeg->u;
	  if (!v.empty() && v.back().u==heapbeg->u){
	    g=v.back().g;
	    v.pop_back();
	  }
	  else
	    g=T(0);
	  std::vector< std::pair<unsigned,unsigned> >::iterator it,itend;
	  nouveau.clear();
	  while (heapend!=heapbeg && topu==heapbeg->u){
	    // add all elements of the top chain	
	    it=vindex[heapbeg->v].begin();
	    itend=vindex[heapbeg->v].end();
	    for (;it!=itend;++it){
	      type_operator_plus_times_reduce((it1beg+it->first)->g,(it2beg+it->second)->g,g,reduce);
	      // increment 2nd poly index of the elements of the top chain
	      ++it->second;
	      if (it->second!=v2s)
		nouveau.push_back(*it);
	    }
	    std::pop_heap(heapbeg,heapend);
	    --heapend;
	  }
	  if (!is_zero(g))
	    v.push_back(T_unsigned<T,U>(g,topu));
	  // erase top node, then push each element of the incremented top chain 
	  it=nouveau.begin();
	  itend=nouveau.end();
	  for (;it!=itend;++it){
	    u=(it1beg+it->first)->u+(it2beg+it->second)->u;
	    // check if u is in the path to the root of the heap
	    unsigned holeindex=heapend-heapbeg,parentindex;
	    if (holeindex && u==heapbeg->u){
	      vindex[heapbeg->v].push_back(*it);
	      ++count1;
	      continue;
	    }
	    bool done=false;
	    while (holeindex){
	      parentindex=(holeindex-1) >> 1;
	      if (u<(heapbeg+parentindex)->u)
		break;
	      if (u==(heapbeg+parentindex)->u){
		done=true;
		++count2;
		vindex[(heapbeg+parentindex)->v].push_back(*it);
		break;
	      }
	      holeindex=parentindex;
	    }
	    if (!done){
	      // not found, add a new node to the heap
	      vindex[heapend->v].clear();
	      vindex[heapend->v].push_back(*it);
	      heapend->u=u;
	      ++heapend;
	      std::push_heap(heapbeg,heapend);
	    }
	  }
	} // end for heapbeg!=heapend
	if (debug_infolevel>20)
	  std::cerr << clock() << " heap_mult, %age of chains" << count1/total << " " << count2/total << " " << std::endl;
	delete [] heap;
	return;
      }
      if (heap_mult==-2){
	// using multimap for f*g, seems slower than heap
	typedef std::multimap< U,std::pair<unsigned,unsigned> > mmap;
	mmap M;
	typename mmap::iterator Mbeg,Mit=M.begin(),Mitbeg,Mend;
	// fill M with (f_i,g_1)
	for (it1=it1end-1;;){
	  Mit=M.insert(Mit,std::pair<U,std::pair<unsigned,unsigned> >(it1->u+u2,std::pair<unsigned,unsigned>(it1-it1beg,0)));
	  if (it1==it1beg)
	    break;
	  --it1;
	}
	// find top degree coefficient of the product then
	// replace top range degree elements or erase them
	for (;!M.empty();){
	  Mend=M.end();
	  Mbeg=M.begin();
	  Mitbeg=Mend;
	  --Mitbeg;
	  U deg=Mitbeg->first;
	  T coeff;
	  for (;Mitbeg!=Mbeg;--Mitbeg){
	    if (Mitbeg->first!=deg)
	      break;
	  }
	  if (Mitbeg->first!=deg)
	    ++Mitbeg;
	  Mit=Mitbeg;
	  type_operator_reduce((it1beg+Mit->second.first)->g,(it2beg+Mit->second.second)->g,coeff,reduce); 
	  for (++Mit;Mit!=Mend;++Mit){
	    type_operator_plus_times_reduce((it1beg+Mit->second.first)->g,(it2beg+Mit->second.second)->g,coeff,reduce); 
	  }
	  if (!is_zero(coeff))
	    v.push_back(T_unsigned<T,U>(coeff,deg));
	  Mit=Mitbeg;
	  std::vector< std::pair<unsigned,unsigned> > vp;
	  for (;Mit!=Mend;++Mit){
	    std::pair<unsigned,unsigned> p=Mit->second;
	    ++p.second;
	    if (p.second<v2s)
	      vp.push_back(p);
	  }
	  M.erase(Mitbeg,Mend);
	  std::vector< std::pair<unsigned,unsigned> > ::iterator vit=vp.begin(),vitend=vp.end();
	  for (;vit!=vitend;++vit)
	    M.insert(std::pair<U,std::pair<unsigned,unsigned> >((it1beg+vit->first)->u+(it2beg+vit->second)->u,*vit));
	}
	return;
      }
      if (heap_mult==-3){ // using map of U -> chains
	typedef std::map< U,std::vector<std::pair<unsigned,unsigned> > > mmap;
	std::vector< std::pair<unsigned,unsigned> >::iterator it,itend;
	mmap M;
	typename mmap::iterator Mit=M.begin();
	// fill M with (f_i,g_1)
	for (it1=it1end-1;;){
	  Mit=M.insert(Mit,std::pair<U,std::vector< std::pair<unsigned,unsigned> > >(it1->u+u2,std::vector< std::pair<unsigned,unsigned> > (1, std::pair<unsigned,unsigned>(it1-it1beg,0) )));
	  if (it1==it1beg)
	    break;
	  --it1;
	}
	while (!M.empty()){
	  Mit=M.end();
	  --Mit;
	  u=Mit->first;
	  it=Mit->second.begin();
	  itend=Mit->second.end();
	  g=T(0);
	  std::vector< std::pair<unsigned,unsigned> > nouveau;
	  for (;it!=itend;++it){
	    type_operator_plus_times_reduce((it1beg+it->first)->g,(it2beg+it->second)->g,g,reduce);
	    ++it->second;
	    if (it->second!=v2s)
	      nouveau.push_back(*it);
	  }
	  if (!is_zero(g))
	    v.push_back(T_unsigned<T,U>(g,u));
	  M.erase(Mit);
	  it=nouveau.begin();
	  itend=nouveau.end();
	  for (;it!=itend;++it){
	    u=(it1beg+it->first)->u+(it2beg+it->second)->u;
	    Mit=M.find(u);
	    if (Mit!=M.end())
	      Mit->second.push_back(std::pair<unsigned,unsigned>(it->first,it->second));
	    else
	      M[u]=std::vector< std::pair<unsigned,unsigned> >(1,std::pair<unsigned,unsigned>(it->first,it->second));
	  }
	}
	return;
      }
      // using heap
      u_pair_index<U> newelem, * heap = new u_pair_index<U>[v1s] ; // pointers to v2 monomials
      u_pair_index<U> * heap0, *heapbeg=heap,* heapend=heap+v1s, * heaplast=heap+v1s-1;
      for (it1=it1beg,heap0=heap;heap0!=heapend;++heap0,++it1){
	*heap0=u_pair_index<U>(it1-it1beg,0,it1->u+u2);
      }
      for (;heapbeg!=heapend;){
	if (!v.empty() && v.back().u==heapbeg->u){
	  type_operator_plus_times_reduce((it1beg+heapbeg->i1)->g,(it2beg+heapbeg->i2)->g,v.back().g,reduce); 
	  if ( is_zero(v.back().g) )
	    v.pop_back();
	}
	else {
	  type_operator_reduce((it1beg+heapbeg->i1)->g,(it2beg+heapbeg->i2)->g,g,reduce); 
	  v.push_back(T_unsigned<T,U>(g,heapbeg->u));
	}
	++heapbeg->i2;
	if (heapbeg->i2==v2s){
	  std::pop_heap(heapbeg,heapend);
	  --heaplast;
	  --heapend;
	}
	else {
	  std::pop_heap(heapbeg,heapend);
	  heaplast->u=(it1beg+heaplast->i1)->u+(it2beg+heaplast->i2)->u;
	  std::push_heap(heapbeg,heapend);
	  // in_out_heap(heapbeg,heapend-heapbeg,newelem);
	}
      }
      delete [] heap;
    } // end if (heapmult)
    else {
#ifdef HASH_MAP_NAMESPACE
      typedef HASH_MAP_NAMESPACE::hash_map< U,T,hash_function_unsigned_object > hash_prod ;
      hash_prod produit(possible_size); // try to avoid reallocation
      // cout << "hash " << clock() << std::endl;
#else
      typedef std::map<U,T> hash_prod;
      // cout << "small map" << std::endl;
      hash_prod produit; 
#endif    
      typename hash_prod::iterator prod_it,prod_itend;
      for (;it1!=it1end;++it1){
	g1=it1->g;
	u1=it1->u;
	if (!is_zero(reduce)){
	  for (it2=it2beg;it2!=it2end;++it2){
	    u=u1+it2->u;
	    prod_it=produit.find(u);
	    if (prod_it==produit.end())
	      type_operator_reduce(g1,it2->g,produit[u],reduce); // g=g1*it2->g; 
	    else 
	      type_operator_plus_times_reduce(g1,it2->g,prod_it->second,reduce); 
	  }
	}
	else {
	  for (it2=it2beg;it2!=it2end;++it2){
	    u=u1+it2->u;
	    prod_it=produit.find(u);
	    if (prod_it==produit.end()){
	      type_operator_times(g1,it2->g,produit[u]); // g=g1*it2->g; 
	    }
	    else {
	      type_operator_plus_times(g1,it2->g,prod_it->second); 
	      // g=g1*it2->g; 
	      // prod_it->second+=g;
	    }
	  }
	}
      }
      T_unsigned<T,U> gu;
      prod_it=produit.begin(),prod_itend=produit.end();
      v.reserve(produit.size());
      for (;prod_it!=prod_itend;++prod_it){
	if (!is_zero(gu.g=prod_it->second)){
	  gu.u=prod_it->first;
	  v.push_back(gu);
	}
      }    
      // std::cerr << "smallmult sort " << clock() << std::endl;
      sort(v.begin(),v.end());
      // std::cerr << "smallmult sort end " << clock() << std::endl;
    } // endif // HEAP_MULT
  }


  template<class T,class U>
  struct threadmult_t {
    const std::vector< T_unsigned<T,U> > * v1ptr ;
    std::vector< typename std::vector< T_unsigned<T,U> >::const_iterator > * v2ptr;
    std::vector< T_unsigned<T,U> > * vptr;
    U degdiv;
    unsigned current_deg;
    unsigned clock;
    int reduce,status;
    bool use_heap;
    T * prod;
    std::vector< vector_size64< std::pair<unsigned,unsigned> > > * vindexptr;
    std::vector< vector_size32< std::pair<unsigned short,unsigned short> > > * vsmallindexptr;
    U_unsigned<U> * heapptr;
  };

#ifdef HAVE_PTHREAD_H

  template<class T,class U> void * do_threadmult(void * ptr){
    threadmult_t<T,U> * argptr = (threadmult_t<T,U> *) ptr;
    argptr->status=1;
    argptr->clock=clock();
    int reduce=argptr->reduce;
    const std::vector< T_unsigned<T,U> > * v1 = argptr->v1ptr;
    std::vector< typename std::vector< T_unsigned<T,U> >::const_iterator >  * v2ptr = argptr->v2ptr;
    std::vector< T_unsigned<T,U> > & v = *argptr->vptr;
    typename std::vector< T_unsigned<T,U> >::const_iterator it1=v1->begin(),it1beg=v1->begin(),it1end=v1->end(),it2beg,it2,it2end;
    T g1,g;
    U u1,u2,u,degdiv=argptr->degdiv;
    int d1=0,d2,v2deg=v2ptr->size()-2,d=argptr->current_deg;
    T * prod=argptr->prod;
    if (prod){
      for (;it1!=it1end;++it1){
	u1=it1->u;
	d1=u1/degdiv;
	if (d1>d)
	  continue;
	d2=v2deg-(d-d1);
	if (d2<0) // degree of d1 incompatible
	  break;
	g1=it1->g;
	it2beg=(*v2ptr)[d2];
	it2end=(*v2ptr)[d2+1];
#if 1
	if ((it1+3)<it1end && (it1+3)->u/degdiv == d1){
	  // 4 monomials have same main degree: make the product simult
	  T * prod0=prod+u1, * prod1=prod+(it1+1)->u,*prod2=prod+(it1+2)->u,*prod3=prod+(it1+3)->u;
	  T g1_1=(it1+1)->g,g1_2=(it1+2)->g,g1_3=(it1+3)->g,g2;
	  if (is_zero(reduce)){
	    for (it2=it2beg;it2!=it2end;++it2){
	      u2=it2->u;
	      g2=it2->g;
	      type_operator_plus_times(g1,g2,prod0[u2]); 
	      type_operator_plus_times(g1_1,g2,prod1[u2]); 
	      type_operator_plus_times(g1_2,g2,prod2[u2]); 
	      type_operator_plus_times(g1_3,g2,prod3[u2]); 
	    }
	  }
	  else {
	    for (it2=it2beg;it2!=it2end;++it2){
	      u2=it2->u;
	      g2=it2->g;
	      type_operator_plus_times_reduce(g1,g2,prod0[u2],reduce); 
	      type_operator_plus_times_reduce(g1_1,g2,prod1[u2],reduce); 
	      type_operator_plus_times_reduce(g1_2,g2,prod2[u2],reduce); 
	      type_operator_plus_times_reduce(g1_3,g2,prod3[u2],reduce); 
	    }
	  }
	  it1 += 3;
	  continue;
	}
#endif
	T * prod0 = prod+u1;
	if (is_zero(reduce)){
	  for (it2=it2beg;it2!=it2end;++it2){
	    type_operator_plus_times(g1,it2->g,prod0[it2->u]); 
	    // prod_it->second += g; 
	  }
	}
	else {
	  for (it2=it2beg;it2!=it2end;++it2){
	    type_operator_plus_times_reduce(g1,it2->g,prod0[it2->u],reduce); 
	  }
	}
      }
      argptr->clock = clock() - argptr->clock;
      argptr->status=2;
      return &v; // not used, all is stored in prod
    }
    // thread-heap multiplication
    if (//false
	argptr->use_heap
	){
      // using heap of chains
      // int v1s=it1end-it1,v2s=it2end-it2;
      std::vector< vector_size64< std::pair<unsigned,unsigned> > > * vindexptr=argptr->vindexptr;
      std::vector< vector_size32< std::pair<unsigned short,unsigned short> > > * vsmallindexptr=argptr->vsmallindexptr;
      bool smallindex=vsmallindexptr;
      U_unsigned<U> * heap = argptr->heapptr ; // pointers to v2 monomials
      U_unsigned<U> * heap0, *heapbeg=heap,* heapend;
      // initial fill of the heap
      int count=0;
      for (it1=it1beg,heap0=heap;it1!=it1end;++it1){
	u1=it1->u;
	d1=u1/degdiv;
	if (d1>d) // first partial degree too large
	  continue;
	d2=v2deg-(d-d1);
	if (d2<0) // first partial degree too small
	  continue;
	it2=(*v2ptr)[d2]; // first monomial of second poly having a compatible partial degree
	it2end=(*v2ptr)[d2+1];
	if (it2==it2end)
	  continue;
	u2=it2->u;
	if (int(u2/degdiv+d1)!=d)
	  continue;
	if (smallindex){
	  (*vsmallindexptr)[count].clear();
	  (*vsmallindexptr)[count].push_back(std::pair<unsigned short,unsigned short>(it1-it1beg,0));
	}
	else {
	  (*vindexptr)[count].clear();
	  (*vindexptr)[count].push_back(std::pair<unsigned,unsigned>(it1-it1beg,0));
	}
	*heap0=U_unsigned<U>(u1+u2,count);
	++heap0;
	++count;
      }
      heapend=heap0;
      std::make_heap(heapbeg,heapend);
      if (smallindex){
	vector_size32< std::pair<unsigned short,unsigned short> > nouveau;
	for (;heapbeg!=heapend;){
	  U topu=heapbeg->u;
	  if (!v.empty() && v.back().u==heapbeg->u){
	    g=v.back().g;
	    v.pop_back();
	  }
	  else
	    g=0;
	  std::vector< std::pair<unsigned short,unsigned short> >::iterator it,itend;
	  vector_size32< std::pair<unsigned short,unsigned short> > * vptr;
	  nouveau.clear();
	  while (heapend!=heapbeg && topu==heapbeg->u){
	    // add all elements of the top chain	
	    vptr = &(*vsmallindexptr)[heapbeg->v];
	    it=vptr->begin();
	    itend=vptr->end();
	    for (;it!=itend;++it){
	      it1=it1beg+it->first;
	      u1=it1->u;
	      d1=u1/degdiv;
	      d2=v2deg-(d-d1);
	      it2beg=(*v2ptr)[d2];
	      it2=it2beg+it->second;
	      type_operator_plus_times_reduce(it1->g,it2->g,g,reduce);
	      // increment 2nd poly index of the elements of the top chain
	      ++it->second;
	      // check if it is still with a compatible partial degree
	      if (it->second+it2beg-(*v2ptr)[d2+1]<0)
		nouveau.push_back(*it);
	    }
	    std::pop_heap(heapbeg,heapend);
	    --heapend;
	  }
	  if (g != 0)
	    v.push_back(T_unsigned<T,U>(g,topu));
	  // erase top node, then push each element of the incremented top chain 
	  it=nouveau.begin();
	  itend=nouveau.end();
	  for (;it!=itend;++it){
	    it1=it1beg+it->first;
	    u1=it1->u;
	    d1=u1/degdiv;
	    d2=v2deg-(d-d1);
	    it2beg=(*v2ptr)[d2];
	    u=u1+(it2beg+it->second)->u;
	    // check if u is in the path to the root of the heap
	    unsigned holeindex=heapend-heapbeg,parentindex;
	    if (holeindex && u==heapbeg->u){
	      vptr=&(*vsmallindexptr)[heapbeg->v];
	      vptr->push_back(*it);
	      continue;
	    }
	    bool done=false;
	    while (holeindex){
	      parentindex=(holeindex-1) >> 1;
	      if (u<(heapbeg+parentindex)->u)
		break;
	      if (u==(heapbeg+parentindex)->u){
		done=true;
		vptr=&(*vsmallindexptr)[(heapbeg+parentindex)->v];
		vptr->push_back(*it);
		break;
	      }
	      holeindex=parentindex;
	    }
	    if (!done){
	      // not found, add a new node to the heap
	      vptr=&(*vsmallindexptr)[heapend->v];
	      vptr->clear();
	      vptr->push_back(*it);
	      heapend->u=u;
	      ++heapend;
	      std::push_heap(heapbeg,heapend);
	    }
	  }
	} // end for heapbeg!=heapend
      } // end smallindex
      else {
	vector_size64< std::pair<unsigned,unsigned> > nouveau;
	for (;heapbeg!=heapend;){
	  U topu=heapbeg->u;
	  if (!v.empty() && v.back().u==heapbeg->u){
	    g=v.back().g;
	    v.pop_back();
	  }
	  else
	    g=0;
	  std::vector< std::pair<unsigned,unsigned> >::iterator it,itend;
	  vector_size64< std::pair<unsigned,unsigned> > * vptr;
	  nouveau.clear();
	  while (heapend!=heapbeg && topu==heapbeg->u){
	    // add all elements of the top chain	
	    vptr = &(*vindexptr)[heapbeg->v];
	    it=vptr->begin();
	    itend=vptr->end();
	    for (;it!=itend;++it){
	      it1=it1beg+it->first;
	      u1=it1->u;
	      d1=u1/degdiv;
	      d2=v2deg-(d-d1);
	      it2beg=(*v2ptr)[d2];
	      it2=it2beg+it->second;
	      type_operator_plus_times_reduce(it1->g,it2->g,g,reduce);
	      // increment 2nd poly index of the elements of the top chain
	      ++it->second;
	      // check if it is still with a compatible partial degree
	      if (it->second+it2beg-(*v2ptr)[d2+1]<0)
		nouveau.push_back(*it);
	    }
	    std::pop_heap(heapbeg,heapend);
	    --heapend;
	  }
	  if (g != 0)
	    v.push_back(T_unsigned<T,U>(g,topu));
	  // erase top node, then push each element of the incremented top chain 
	  it=nouveau.begin();
	  itend=nouveau.end();
	  for (;it!=itend;++it){
	    it1=it1beg+it->first;
	    u1=it1->u;
	    d1=u1/degdiv;
	    d2=v2deg-(d-d1);
	    it2beg=(*v2ptr)[d2];
	    u=u1+(it2beg+it->second)->u;
	    // check if u is in the path to the root of the heap
	    unsigned holeindex=heapend-heapbeg,parentindex;
	    if (holeindex && u==heapbeg->u){
	      vptr=&(*vindexptr)[heapbeg->v];
	      vptr->push_back(*it);
	      continue;
	    }
	    bool done=false;
	    while (holeindex){
	      parentindex=(holeindex-1) >> 1;
	      if (u<(heapbeg+parentindex)->u)
		break;
	      if (u==(heapbeg+parentindex)->u){
		done=true;
		vptr=&(*vindexptr)[(heapbeg+parentindex)->v];
		vptr->push_back(*it);
		break;
	      }
	      holeindex=parentindex;
	    }
	    if (!done){
	      // not found, add a new node to the heap
	      vptr=&(*vindexptr)[heapend->v];
	      vptr->clear();
	      vptr->push_back(*it);
	      heapend->u=u;
	      ++heapend;
	      std::push_heap(heapbeg,heapend);
	    }
	  }
	} // end for heapbeg!=heapend
      } // end els (smallindex)
      argptr->clock = clock() - argptr->clock;
      argptr->status=2;
      return &v;
    }
#ifdef HASH_MAP_NAMESPACE
    typedef HASH_MAP_NAMESPACE::hash_map< U,T,hash_function_unsigned_object > hash_prod ;
    hash_prod produit; // try to avoid reallocation
#else
    typedef std::map<U,T> hash_prod;
    // cout << "small map" << std::endl;
    hash_prod produit; 
#endif    
    typename hash_prod::iterator prod_it,prod_itend;
    for (;it1!=it1end;++it1){
      u1=it1->u;
      d1=u1/degdiv;
      if (d1>d)
	continue;
      d2=v2deg-(d-d1);
      if (d2<0) // degree of d1 incompatible
	break;
      g1=it1->g;
      it2beg=(*v2ptr)[d2];
      it2end=(*v2ptr)[d2+1];
      if (reduce){
	for (it2=it2beg;it2!=it2end;++it2){
	  u2=it2->u;
	  u=u1+u2;
	  prod_it=produit.find(u);
	  if (prod_it==produit.end())
	    type_operator_reduce(g1,it2->g,produit[u],reduce); // g=g1*it2->g; 
	  else 
	    type_operator_plus_times_reduce(g1,it2->g,prod_it->second,reduce); 
	}
      }
      else {
	for (it2=it2beg;it2!=it2end;++it2){
	  u2=it2->u;
	  u=u1+u2;
	  prod_it=produit.find(u);
	  if (prod_it==produit.end()){
	    type_operator_times(g1,it2->g,g); // g=g1*it2->g; 
	    produit[u]=g;
	  }
	  else 
	    type_operator_plus_times(g1,it2->g,prod_it->second); 
	  // prod_it->second += g; 
	}
      }
    }
    T_unsigned<T,U> gu;
    prod_it=produit.begin(),prod_itend=produit.end();
    v.clear();
    v.reserve(produit.size());
    for (;prod_it!=prod_itend;++prod_it){
      if (!is_zero(gu.g=prod_it->second)){
	gu.u=prod_it->first;
	v.push_back(gu);
      }
    }    
    // std::cerr << "do_threadmult end " << clock() << std::endl;
    sort(v.begin(),v.end());
    // std::cerr << "do_threadmult sort end " << clock() << std::endl;
    argptr->clock = clock() - argptr->clock;
    argptr->status = 2;
    return &v;
  }

  template<class T,class U>
  bool threadmult(const std::vector< T_unsigned<T,U> > & v1,const std::vector< T_unsigned<T,U> > & v2,std::vector< T_unsigned<T,U> > & v,U degdiv,int reduce,size_t possible_size=100){
    if (!threads_allowed)
      return false;
    v.clear();
    if (v1.empty() || v2.empty())
      return true;
    size_t v1si=v1.size(),v2si=v2.size();
    double v1s=double(v1si),v2s=double(v2si);
    if (v2si<v1si)
      return threadmult<T,U>(v2,v1,v,degdiv,reduce,possible_size);
    unsigned threads_time=0;

    int nthreads=threads;

    if (nthreads<2) 
      ; // return false;
    double v1v2=v1s*v2s;
    T * prod =0;
    U u1=v1.front().u, u2=v2.front().u;
    U u12=u1+u2; // size of the array for array multiplication
    // compare u12 and v1v2*ln(v1v2)
    if ( heap_mult>=0 && possible_size>100 && u12<512e6/sizeof(T) && u12<v1v2*std::log(double(possible_size))*2){
      if (debug_infolevel>20)
	std::cerr << "array multiplication, v1 size " << v1s << " v2 size " << v2s << " u1+u2 " << u12 << std::endl;
      // array multiplication
      prod = new T[u12+1];
      for (U u=0;u<=u12;++u)
	prod[u]=T(0);
    }
    // if array multiplication is faster, set prod
    bool use_heap = (heap_mult<0) || (heap_mult>0 && v1v2>heap_mult);
    if (!prod && use_heap && nthreads<2) 
      return false;  
    if (debug_infolevel>20){
      std::cerr << "// " << clock() << "using threaded " ;
      if (use_heap)
	std::cerr << "heap";
      else 
	std::cerr << "hash";
      std::cerr << " multiplication" << std::endl;
    }
    unsigned d2=v2.front().u/degdiv,deg1v=v1.front().u/degdiv+d2;
    int cur_deg=-1,prev_deg=d2;
    // initialize iterators to the degree beginning
    std::vector< typename std::vector< T_unsigned<T,U> >::const_iterator > v2it;
    typename std::vector< T_unsigned<T,U> >::const_iterator it=v2.begin(),itend=v2.end();
    for (v2it.push_back(it);it!=itend;++it){
      cur_deg=it->u/degdiv;
      if (cur_deg==prev_deg)
	continue;
      for (int i=prev_deg-1;i>=cur_deg;--i){
	v2it.push_back(it);
	if (!i)
	  break;
      }
      prev_deg=cur_deg;
    }
    for (int i=prev_deg-1;i>=0;--i)
      v2it.push_back(it);
    v2it.push_back(it);
    // degree of product wrt to the main variable
    // will launch deg1v+1 threads to compute each degree
    pthread_t tab[deg1v+1];
    threadmult_t<T,U> arg[deg1v+1];
    possible_size=0;
    int res=0;
    int i=deg1v;
    bool smallindex=(v1s<65535 && v2s<65535);
    if (
	// true || 
	nthreads==1){
      U_unsigned<U> *heapptr=new U_unsigned<U>[v1si];
      std::vector< vector_size64< std::pair<unsigned,unsigned> > >* vindexptr=smallindex?0:(new std::vector< vector_size64< std::pair<unsigned,unsigned> > >(v1si));
      std::vector< vector_size32< std::pair<unsigned short,unsigned short> > >* vsmallindexptr=smallindex?(new std::vector< vector_size32< std::pair<unsigned short,unsigned short> > >(v1si)):0;
      for (;i>=0;--i){
	arg[i].v1ptr=&v1;
	arg[i].v2ptr=&v2it;
	arg[i].vptr = new std::vector< T_unsigned<T,U> >;
	if (i!=int(deg1v))
	  arg[i].vptr->reserve(arg[i+1].vptr->size());
	arg[i].degdiv=degdiv;
	arg[i].current_deg=i;
	arg[i].reduce=reduce;
	arg[i].status=0;
	arg[i].prod=prod;
	if (use_heap){
	  arg[i].use_heap=use_heap;
	  arg[i].heapptr=heapptr;
	  arg[i].vindexptr=vindexptr;
	  arg[i].vsmallindexptr=vsmallindexptr;
	}
	else {
	  arg[i].use_heap=false;
	  arg[i].heapptr=0;
	  arg[i].vindexptr=0;
	  arg[i].vsmallindexptr=0;
	}
	if (debug_infolevel>30)
	  std::cerr << "Computing degree " << i << " " << clock() << std::endl;
	do_threadmult<T,U>(&arg[i]);
	threads_time += arg[i].clock;
	possible_size += arg[i].vptr->size();	
      }
      delete [] heapptr;
      delete vindexptr;
    }
    else {
      std::vector<int> in_progress;
      // create initials threads
      for (int j=0;i>=0 && j<nthreads;--i,++j){
	in_progress.push_back(i);
	arg[i].v1ptr=&v1;
	arg[i].v2ptr=&v2it;
	arg[i].vptr = new std::vector< T_unsigned<T,U> >;
	arg[i].degdiv=degdiv;
	arg[i].current_deg=i;
	arg[i].reduce=reduce;
	arg[i].status=0;
	arg[i].prod=prod;
	if (use_heap){
	  arg[i].use_heap=use_heap;
	  arg[i].heapptr=new U_unsigned<U>[v1si];
	  arg[i].vindexptr=smallindex?0:(new std::vector< vector_size64< std::pair<unsigned,unsigned> > >(v1si));
	  arg[i].vsmallindexptr=smallindex?(new std::vector< vector_size32< std::pair<unsigned short,unsigned short> > >(v1si)):0;
	}
	else {
	  arg[i].use_heap=false;
	  arg[i].heapptr=0;
	  arg[i].vindexptr=0;
	  arg[i].vsmallindexptr=0;
	}
	res=pthread_create(&tab[i],(pthread_attr_t *) NULL,do_threadmult<T,U>,(void *) &arg[i]);
	if (res){
	  // should cancel previous threads and delete created arg[i].vptr
	  return false;
	}
      } // end initial thread creation
      // now wait for each thread and create replacement threads
      while (1){
	int nb=in_progress.size();
	int todo=0,towait=0;
	for (int j=0;j<nb;++j){
	  int k=in_progress[j];
	  if (k>=0){
	    int concurrent=arg[k].status;
	    if (concurrent==2){
	      void * ptr;
	      pthread_join(tab[k],&ptr);
	      threads_time += arg[k].clock;
	      possible_size += arg[k].vptr->size();
	      if (i>=0){
		arg[i].v1ptr=&v1;
		arg[i].v2ptr=&v2it;
		arg[i].vptr = new std::vector< T_unsigned<T,U> >;
		arg[i].vptr->reserve(arg[k].vptr->size());
		arg[i].degdiv=degdiv;
		arg[i].current_deg=i;
		arg[i].reduce=reduce;
		arg[i].status=0;
		arg[i].use_heap=use_heap;
		arg[i].prod=prod;
		arg[i].heapptr=arg[k].heapptr;
		arg[i].vindexptr=arg[k].vindexptr;
		arg[i].vsmallindexptr=arg[k].vsmallindexptr;
		res=pthread_create(&tab[i],(pthread_attr_t *) NULL,do_threadmult<T,U>,(void *) &arg[i]);
		if (res){
		  // should cancel previous threads and delete created arg[i].vptr
		  return false;
		}
		in_progress[j]=i;
		--i;
		++todo;
	      } // end if (i>=0)
	      else 
		in_progress[j]=-1;
	    } // if (concurrent!=2)
	    else
	      ++towait;
	  } // end if (k>=0)
	} // end for (j=0;j<=nb;++j)
	if (!todo && !towait)
	  break;
	if (towait)
	  usleep(1);
      } // end while(1)
      if (use_heap){
	i=deg1v;
	for (int j=0;i>=0 && j<nthreads;--i,++j){
	  delete [] arg[i].heapptr;
	  if (arg[i].vindexptr) delete arg[i].vindexptr;
	  if (arg[i].vsmallindexptr) delete arg[i].vsmallindexptr;
	}
      }
    } // end else of if (nthreads==1)
    if (debug_infolevel>30)
      std::cerr << "Begin copy " << clock() << std::endl;
    // store to v
    if (prod){
      int n=0;
      for (U u=u12;;--u){
	if (!is_zero(prod[u]))
	  ++n;
	if (!u)
	  break;
      }
      v.reserve(n);
      for (U u=u12;;--u){
	if (!is_zero(prod[u]))
	  v.push_back(T_unsigned<T,U>(prod[u],u));
	if (!u)
	  break;
      }
      delete [] prod ;      
      for (int i=deg1v;i>=0;--i){
	delete arg[i].vptr;
      }
    }
    else {
      v.reserve(possible_size);
      for (int i=deg1v;i>=0;--i){
	typename std::vector< T_unsigned<T,U> >::const_iterator it=arg[i].vptr->begin(),itend=arg[i].vptr->end();
	for (;it!=itend;++it){
	  v.push_back(*it);
	}
	delete arg[i].vptr;
      }
      /* 
      v=std::vector< T_unsigned<T,U> >(possible_size);
      typename std::vector< T_unsigned<T,U> >::const_iterator jt=v.begin();
      for (int i=deg1v;i>=0;--i){
	typename std::vector< T_unsigned<T,U> >::const_iterator it=arg[i].vptr->begin(),itend=arg[i].vptr->end();
#ifdef VISUALC
	for (;it!=itend;++jt,++it){
	  *jt=*it;
	}
#else
	memcpy(*(void **) &jt,*(void **)&it,(itend-it)*sizeof(T_unsigned<T,U>));
	jt += (itend-it);
#endif
	delete arg[i].vptr;
      }
      */
    }
    if (debug_infolevel>30)
      std::cerr << "End copy " << clock() << std::endl;
    return true;
  }

#else // PTHREAD
  template<class T,class U>
  bool threadmult(const std::vector< T_unsigned<T,U> > & v1,const std::vector< T_unsigned<T,U> > & v2,std::vector< T_unsigned<T,U> > & v,U degdiv,int reduce,size_t possible_size=100){
    return false;
  }

#endif // PTHREAD

  // note that U may be of type vector of int or an int
  // + is used to multiply monomials and - to divide
  // / should return the quotient of the main variable exponent
  // > should return true if a monomial has main degree >
  // vars is the list of monomials x,y,z,etc. as translated in U type
  // quo_only==2 means we know that b divides a and we search the cofactor
  // quo_only==1 means we want to check that b divides a
  // quo_only==-1 means compute quotient first using heap div then r=a-b*q
  // quo_only==-2 means compute quotient only using heap div
  // if quo_only is >= 2 or <=-2, r is not computed
  template<class T,class U,class R>
  int hashdivrem(const std::vector< T_unsigned<T,U> > & a,const std::vector< T_unsigned<T,U> > & b,std::vector< T_unsigned<T,U> > & q,std::vector< T_unsigned<T,U> > & r,const std::vector<U> & vars,const R & reduce,double qmax,bool allowrational,int quo_only=0){
    // std::cerr << "hashdivrem dim " << vars.size() << " clock " << clock() << std::endl;
    q.clear();
    r.clear();
    if (a.empty()){
      return 1;
    }
    if (b.empty()){
      r=a;
      return 1;
    }
    U mainv=vars.front();
    unsigned mainvar=0;
    for (; (mainv >>= 1) ;++mainvar)
      ;
    U bu=b.front().u;
    int bdeg=int(bu >> mainvar);
    int adeg=int(a.front().u >> mainvar),rdeg;
    if (adeg<bdeg){
      r=a;
      return 1;
    }
    U rstop=U(bdeg) << mainvar;
    typename std::vector< T_unsigned<T,U> >::iterator it1,it1end;
    typename std::vector< T_unsigned<T,U> >::const_iterator it2,it2end,itbbeg=b.begin(),itbend=b.end(),ita=a.begin(),itaend=a.end(),cit,citend;
    T binv=b.front().g;
    if (!is_zero(reduce))
      binv=invmod(binv,reduce);
    if (b.size()==1){
      for (cit=a.begin(),citend=a.end();cit!=citend;++cit){
	if (rstop>cit->u)
	  break;
	if (cit->u<b.front().u)
	  return 0;
	T qn;
	// qn=reduce?smod(cit->g*binv,reduce):cit->g/binv;
	if (!is_zero(reduce))
	  type_operator_reduce(cit->g,binv,qn,reduce);
	else
	  qn=cit->g/binv;
	if (qmax!=0 && qn>qmax)
	  return -1;
	if (is_zero(reduce) && !allowrational && !is_zero(cit->g % binv))
	  return 0;
	q.push_back(T_unsigned<T,U>(qn,cit->u-b.front().u));
      }
      for (;cit!=citend;++cit)
	r.push_back(*cit);
      // std::cerr << "hashdivrem end dim " << vars.size() << " clock " << clock() << std::endl;
      return 1;
    }
    unsigned as=a.size(),bs=b.size();
    double v1v2=double(as)*bs;
    // FIXME, if bdeg==0
    if (!quo_only && bdeg && as>=a.front().u/25. // array div disabled, probably too much memory used
	&& heap_mult>=0 && a.front().u < 512e6/sizeof(T)){
      U umax=a.front().u,u;
      if (debug_infolevel>20)
	std::cerr << "array division, a size " << a.size() << " b size " << b.size() << " u " << umax << std::endl;
      // array division
      T * rem = new T[unsigned(umax+1)];
      for (u=0;u<=umax;++u)
	rem[u]=T(0);
      // find maincoeff of b
      std::vector< T_unsigned<T,U> > lcoeffb;
      for (cit=b.begin(),citend=b.end();cit!=citend;++cit){
	register U u=cit->u;
	if (rstop>u)
	  break;
	lcoeffb.push_back(T_unsigned<T,U>(cit->g,u-rstop));
      }
      // fill rem with a
      for (cit=a.begin(),citend=a.end();cit!=citend;++cit){
	rem[cit->u]=cit->g;
      }
      std::vector< T_unsigned<T,U> > maincoeff,quo,tmp;
      for (rdeg=adeg;rdeg>=bdeg;--rdeg){
	U ushift=U(rdeg) << mainvar;
	maincoeff.clear();
	quo.clear();
	tmp.clear();
	// find leading coeff of rem
	for (;umax>=ushift;--umax){
	  T & g=rem[umax];
	  if (!is_zero(g))
	    maincoeff.push_back(T_unsigned<T,U>(g,umax-ushift));
	}
	if (maincoeff.empty()) 
	  continue;
	ushift=U(rdeg-bdeg) << mainvar;
	// divide maincoeff by lcoeff(b)
	// this is done by recursion except when univariate
	if (vars.size()==1){
	  if (lcoeffb.size()!=1 || maincoeff.size()!=1)
	    return 0;
	  T res;
	  if (!is_zero(reduce))
	    type_operator_reduce(maincoeff.front().g,binv,res,reduce);// res=smod(maincoeff.front().g*binv,reduce);
	  else {
	    res=maincoeff.front().g/binv;
	    if (qmax && res>qmax){
	      delete [] rem;
	      return -1;
	    }
	    if (!allowrational && !is_zero(maincoeff.front().g%binv) ){
	      delete [] rem;
	      return 0;
	    }
	  }
	  quo.push_back(T_unsigned<T,U>(res,maincoeff.front().u+ushift));
	  q.push_back(quo.back());
	}
	else {
	  int recdivres=hashdivrem(maincoeff,lcoeffb,quo,tmp,std::vector<U>(vars.begin()+1,vars.end()),reduce,qmax,allowrational);
	  if (recdivres!=1){
	    delete [] rem;
	    return recdivres;
	  }
	  if (!tmp.empty()){
	    delete [] rem;
	    return 0;
	  }
	  for (it1=quo.begin(),it1end=quo.end();it1!=it1end;++it1){
	    it1->u += ushift;
	    q.push_back(*it1);
	  }
	}
	// rem -= quo*b
	for (cit=itbbeg;cit!=itbend;++cit){
	  T g1=-cit->g;
	  U u,u1=cit->u;
	  // int deg1=u1/mainvar;
	  if (!is_zero(reduce)){
	    for (it2=quo.begin(),it2end=quo.end();it2!=it2end;++it2){
	      u=u1+it2->u;
	      register int deg = int(u >> mainvar); // deg=deg1+it2->u/mainvar;
	      if (deg<rdeg){
		type_operator_plus_times_reduce(g1,it2->g,rem[u],reduce); 
	      }
	    }
	  }
	  else {
	    for (it2=quo.begin(),it2end=quo.end();it2!=it2end;++it2){
	      u=u1+it2->u;
	      register int deg=int(u >> mainvar);
	      if (deg<rdeg){
		type_operator_plus_times(g1,it2->g,rem[u]);	      
	      }
	    }
	  }
	}
	// end rem -= quo*b
      }
      // move rem to r
      for (;;--umax){
	T & g=rem[umax];
	if (!is_zero(g))
	  r.push_back(T_unsigned<T,U>(g,umax));
	if (umax==0)
	  break;
      }
      delete [] rem;
      return 1;
    }
    bool use_heap=(heap_mult>0 
		   && v1v2>=heap_mult
		   );
    if (heap_mult<0 || use_heap || quo_only){
      if (debug_infolevel>20)
	std::cerr << "heap division, a size " << a.size() << " b size " << b.size() << " vars " << vars << std::endl;
      // heap division:
      // ita an iterator on a, initial value a.begin()
      // a heap with the current state of q*b, initialized to empty heap
      // loop:
      // compare degree of the iterator in a and heap top
      // if both are < rstop (degree of b) break
      // if equal: add monomial of a to heap top coeff, move a iterator
      // if a>: add term to q and to the heap by mult/dividing by binv
      // if heap >: ++ heaptop b iterator, add term to q and to the heap
      // after division loop:
      // finish the heap multiplication into r
      //
      // vindex[i] is the heap chain corresponding to index i
      // it contains pairs of index in g and q
      std::vector< std::vector< std::pair<unsigned,unsigned> > > vindex(bs);
      U_unsigned<U> * heap = new U_unsigned<U>[bs], * heapbeg =heap, * heapend=heap ;
      T g;
      U heapu,u;
      // qnouveau is used to store new pairs of products g*q when the monomial in q is not yet known
      std::vector< std::pair<unsigned,unsigned> > qnouveau(bs);
      std::vector< std::pair<unsigned,unsigned> > nouveau;
      // initialize qnouveau to fill the heap when first quotient term computed
      for (unsigned int i=0;i<bs;++i){
	qnouveau[i]=std::pair<unsigned,unsigned>(i,0);
	*(heap+i)=U_unsigned<U>(0,i);
      }
      for (;;){
	g=T(0);
	// compare current position in a with heap top
	if (heapbeg!=heapend){
	  heapu=heapbeg->u;
	  if (ita!=itaend && ita->u>heapu){
	    if (ita->u>bu){
	      heapu=ita->u;
	      g=ita->g;
	      ++ita;
	    }
	  }
	  else {
	    if (heapu>=bu){
	      // find all pairs having heapu as monomial
	      std::vector< std::pair<unsigned,unsigned> >::iterator it,itend;
	      nouveau.clear();
	      while (heapend!=heapbeg && heapu==heapbeg->u){
		nouveau.clear();
		// add all elements of the top chain	
		it=vindex[heapbeg->v].begin();
		itend=vindex[heapbeg->v].end();
		for (;it!=itend;++it){
		  type_operator_plus_times_reduce((itbbeg+it->first)->g,(q.begin()+it->second)->g,g,reduce);
		  // increment 2nd poly index of the elements of the top chain
		  ++it->second;
		  if (it->second<q.size())
		    nouveau.push_back(*it);
		  else // wait for computation of a new term of a before adding to the heap 
		    qnouveau.push_back(*it);
		}
		// erase top node, 
		std::pop_heap(heapbeg,heapend);
		--heapend;
		// push each element of the incremented top chain 
		it=nouveau.begin();
		itend=nouveau.end();
		for (;it!=itend;++it){
		  u=(itbbeg+it->first)->u+(q.begin()+it->second)->u;
		  // check if u is in the path to the root of the heap
		  unsigned holeindex=heapend-heapbeg,parentindex;
		  if (holeindex && u==heapbeg->u){
		    vindex[heapbeg->v].push_back(*it);
		    continue;
		  }
		  bool done=false;
		  while (holeindex){
		    parentindex=(holeindex-1) >> 1;
		    if (u<(heapbeg+parentindex)->u)
		      break;
		    if (u==(heapbeg+parentindex)->u){
		      done=true;
		      vindex[(heapbeg+parentindex)->v].push_back(*it);
		      break;
		    }
		    holeindex=parentindex;
		  }
		  if (!done){
		    // not found, add a new node to the heap
		    vindex[heapend->v].clear();
		    vindex[heapend->v].push_back(*it);
		    heapend->u=u;
		    ++heapend;
		    std::push_heap(heapbeg,heapend);
		  }
		} // end adding incremented pairs from nouveau
	      } // end loop on monomial of the heap having the same u
	      if (heapu==ita->u){
		g = ita->g -g ;
		++ita;
	      }
	      else
		g=-g;
	      if (is_zero(g))
		continue;
	    } // end if (heapu>=bu)
	  } // end else ita->u>heapu
	} // if heap non empty
	else {
	  if (ita!=itaend && (heapu=ita->u)>bu){
	    g=ita->g;
	    ++ita;
	  } 
	} // end if (!heap.empty())
	if (is_zero(g))
	  break;
	// g=reduce?smod(g*binv,reduce):g/binv;
	if (!is_zero(reduce))
	  type_operator_reduce(g,binv,g,reduce);
	else
	  g=g/binv;
	if (qmax && g>qmax){
	  delete [] heap;
	  return -1;
	}
	if (is_zero(reduce) && !allowrational && !is_zero(g % binv)){
	  delete [] heap;
	  return 0;
	}
	// new quotient term
	q.push_back(T_unsigned<T,U>(g,heapu-bu));
	// explore qnouveau and add terms to the heap
	std::vector< std::pair<unsigned,unsigned> >::iterator it,itend;
	it=qnouveau.begin();
	itend=qnouveau.end();
	for (;it!=itend;++it){
	  if (!it->first) // leading term of b already taken in account
	    continue;
	  u=(itbbeg+it->first)->u+(q.begin()+it->second)->u;
	  // check if u is in the path to the root of the heap
	  unsigned holeindex=heapend-heapbeg,parentindex;
	  if (holeindex && u==heapbeg->u){
	    vindex[heapbeg->v].push_back(*it);
	    continue;
	  }
	  bool done=false;
	  while (holeindex){
	    parentindex=(holeindex-1) >> 1;
	    if (u<(heapbeg+parentindex)->u)
	      break;
	    if (u==(heapbeg+parentindex)->u){
	      done=true;
	      vindex[(heapbeg+parentindex)->v].push_back(*it);
	      break;
	    }
	    holeindex=parentindex;
	  }
	  if (!done){
	    // not found, add a new node to the heap
	    vindex[heapend->v].clear();
	    vindex[heapend->v].push_back(*it);
	    heapend->u=u;
	    ++heapend;
	    std::push_heap(heapbeg,heapend);
	  }
	} // end adding incremented pairs from qnouveau
	qnouveau.clear();
      } // for (;;)
      // r still empty
      if (debug_infolevel>20)
	std::cerr << "Finished computing quotient, size " << q.size() << " " << clock() << std::endl ;
      if (quo_only>=2 || quo_only<=-2){
	delete [] heap;
	return 1;
      }
      // do not define QUO_ONLY unless R==int
#ifdef QUO_ONLY
      if (quo_only==-1 && is_zero(reduce) 
	  && q.size()*double(b.size())>10000 
	  ){
	if (debug_infolevel>20)
	  std::cerr << "Computing q*b, size " << q.size() << " " << b.size() << std::endl ;
	if (debug_infolevel>30)
	  std::cerr << "Computing b*q, " << b << " * " << q << " " << vars.front() << " " <<a.size() << std::endl ;
	std::vector< T_unsigned<T,U> > bq;
	if (!threadmult(b,q,bq,vars.front(),0,a.size()))
	  smallmult(b,q,bq,reduce,as);
	smallsub(a,bq,r);
	delete [] heap;
	return 1;
      }
#endif
      // now q is computed, combine a and remaining product to r
      for (;heapbeg!=heapend;){
	heapu=heapbeg->u;
	if (ita!=itaend){
	  if (ita->u>heapu){
	    r.push_back(*ita);
	    ++ita;
	    continue;
	  }
	  if (ita->u<heapu)
	    g=T(0);
	  else {
	    g=-ita->g; // opposite sign since we neg at the end
	    ++ita;
	  }
	} // ita!=itaend
	// add all terms from the heap with same monomial
	while (heapbeg!=heapend && heapbeg->u==heapu){
	  qnouveau.clear();
	  std::vector< std::pair<unsigned,unsigned> >::iterator it,itend;
	  it=vindex[heapbeg->v].begin();
	  itend=vindex[heapbeg->v].end();
	  for (;it!=itend;++it){
	    type_operator_plus_times_reduce((itbbeg+it->first)->g,(q.begin()+it->second)->g,g,reduce);
	    // increment 2nd poly index of the elements of the top chain
	    ++it->second;
	    if (it->second<q.size())
	      qnouveau.push_back(*it);
	  }
	  // erase top node, 
	  std::pop_heap(heapbeg,heapend);
	  --heapend;
	  // push each element of the incremented top chain 
	  it=qnouveau.begin();
	  itend=qnouveau.end();
	  for (;it!=itend;++it){
	    u=(itbbeg+it->first)->u+(q.begin()+it->second)->u;
	    // check if u is in the path to the root of the heap
	    unsigned holeindex=heapend-heapbeg,parentindex;
	    if (holeindex && u==heapbeg->u){
	      vindex[heapbeg->v].push_back(*it);
	      continue;
	    }
	    bool done=false;
	    while (holeindex){
	      parentindex=(holeindex-1) >> 1;
	      if (u<(heapbeg+parentindex)->u)
		break;
	      if (u==(heapbeg+parentindex)->u){
		done=true;
		vindex[(heapbeg+parentindex)->v].push_back(*it);
		break;
	      }
	      holeindex=parentindex;
	    }
	    if (!done){
	      // not found, add a new node to the heap
	      vindex[heapend->v].clear();
	      vindex[heapend->v].push_back(*it);
	      heapend->u=u;
	      ++heapend;
	      std::push_heap(heapbeg,heapend);
	    }
	  } // end adding incremented pairs from nouveau
	} // end while heapbeg!=heapend && heapbeg->u==heapu
	// add -g to r
	if (!is_zero(g))
	  r.push_back(T_unsigned<T,U>(-g,heapu));
      } // end for (heapbeg!=heapend)
      for (;ita!=itaend;++ita)
	r.push_back(*ita);
      delete [] heap;
      return 1;
    }    
#ifdef HASH_MAP_NAMESPACE
    typedef HASH_MAP_NAMESPACE::hash_map< U,T,hash_function_unsigned_object> hash_prod ;
    std::vector< hash_prod > produit(adeg+1);
#else
    typedef std::map<U,T> hash_prod;
    std::vector< hash_prod > produit(adeg+1); 
#endif    
    typename hash_prod::iterator prod_it,prod_itend;
    hash_prod * hashptr;
    // find maincoeff of b
    std::vector< T_unsigned<T,U> > lcoeffb;
    for (cit=b.begin(),citend=b.end();cit!=citend;++cit){
      register U u=cit->u;
      if (rstop>u)
	break;
      lcoeffb.push_back(T_unsigned<T,U>(cit->g,u-rstop));
    }
    // copy a to remainder
    for (cit=a.begin(),citend=a.end();cit!=citend;++cit){ 
      U u=cit->u; 
      produit[unsigned(u >> mainvar)][u]=cit->g; 
    }
    for (rdeg=adeg;rdeg>=bdeg;--rdeg){
      if (debug_infolevel>20)
	std::cerr << "hashdivrem degree " << rdeg << " " << clock() << std::endl;
      if (produit[rdeg].empty())
	continue;
      // find degree of remainder and main coeff
      std::vector< T_unsigned<T,U> > maincoeff,quo,tmp;
      U ushift=U(rdeg) << mainvar;
      for (prod_it=produit[rdeg].begin(),prod_itend=produit[rdeg].end();prod_it!=prod_itend;++prod_it){
	if (!is_zero(prod_it->second))
	  maincoeff.push_back(T_unsigned<T,U>(prod_it->second,prod_it->first-ushift));
      }
      if (maincoeff.empty()) 
	continue;
      sort(maincoeff.begin(),maincoeff.end());
      ushift=U(rdeg-bdeg) << mainvar;
      // divide maincoeff by lcoeff(b)
      // this is done by recursion except when univariate
      if (vars.size()==1){
	if (lcoeffb.size()!=1 || maincoeff.size()!=1)
	  return 0;
	T res;
	if (!is_zero(reduce))
	  type_operator_reduce(maincoeff.front().g,binv,res,reduce);// res=smod(maincoeff.front().g*binv,reduce);
	else {
	  res=maincoeff.front().g/binv;
	  if (qmax && res>qmax)
	    return -1;
	  if (!allowrational && !is_zero(maincoeff.front().g%binv) )
	    return 0;
	}
	quo.push_back(T_unsigned<T,U>(res,maincoeff.front().u+ushift));
	q.push_back(quo.back());
      }
      else {
	int recdivres=hashdivrem(maincoeff,lcoeffb,quo,tmp,std::vector<U>(vars.begin()+1,vars.end()),reduce,qmax,allowrational);
	if (recdivres!=1)
	  return recdivres;
	if (!tmp.empty())
	  return 0;
	for (it1=quo.begin(),it1end=quo.end();it1!=it1end;++it1){
	  it1->u += ushift;
	  q.push_back(*it1);
	}
      }
      // remainder -= quo*b
      for (cit=itbbeg;cit!=itbend;++cit){
	T g1=-cit->g;
	U u,u1=cit->u;
	// int deg1=u1/mainvar;
	if (!is_zero(reduce)){
	  for (it2=quo.begin(),it2end=quo.end();it2!=it2end;++it2){
	    u=u1+it2->u;
	    register int deg = int(u >> mainvar); // deg=deg1+it2->u/mainvar;
	    if (deg<rdeg){
	      hashptr = &produit[deg];
	      prod_it=hashptr->find(u);
	      if (prod_it==hashptr->end())
		//(*hashptr)[u]=(g1*it2->g)%reduce; 
		type_operator_reduce(g1,it2->g,(*hashptr)[u],reduce); 
	      else {
		// prod_it->second += g1*it2->g;
		// prod_it->second %= reduce;
		type_operator_plus_times_reduce(g1,it2->g,prod_it->second,reduce); 
		if (is_zero(prod_it->second)) hashptr->erase(prod_it);
	      }
	    }
	  }
	}
	else {
	  for (it2=quo.begin(),it2end=quo.end();it2!=it2end;++it2){
	    u=u1+it2->u;
	    register int deg=int(u >> mainvar);
	    if (deg<rdeg){
	      hashptr = &produit[deg];
	      prod_it=hashptr->find(u);
	      if (prod_it==hashptr->end()){
		type_operator_times(g1,it2->g,(*hashptr)[u]); 
	      }
	      else {
		type_operator_plus_times(g1,it2->g,prod_it->second);	      
		if (is_zero(prod_it->second)) hashptr->erase(prod_it);
	      }
	    }
	  }
	}
      }
      // end rem -= quo*b
    }
    // copy remainder to r and sort
    unsigned rsize=0;
    for (int i=0;i<bdeg;++i)
      rsize += produit[i].size();
    r.reserve(rsize);
    T_unsigned<T,U> gu;
    for (int i=bdeg-1;i>=0;--i){
      for (prod_it=produit[i].begin(),prod_itend=produit[i].end();prod_it!=prod_itend;++prod_it){
	if (!is_zero(gu.g=prod_it->second)){
	  gu.u=prod_it->first;
	  r.push_back(gu);
	}
      }    
    }
    // IMPROVE: might do partial sort 
    sort(r.begin(),r.end());
    return 1;
  }



  template<class T,class U>
  bool is_content_trivially_1(const typename std::vector< T_unsigned<T,U> > & v,U mainvar){
#ifdef HASH_MAP_NAMESPACE
    typedef HASH_MAP_NAMESPACE::hash_map< U,T,hash_function_unsigned_object > hash_prod ;
    hash_prod produit; // try to avoid reallocation
    // cout << "hash " << clock() << std::endl;
#else
    typedef std::map<U,T> hash_prod;
    // cout << "small map" << std::endl;
    hash_prod produit; 
#endif
    U outer_index,inner_index;
    typename std::vector< T_unsigned<T,U> >::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      outer_index=it->u % mainvar;
      inner_index=it->u/mainvar;
      typename hash_prod::iterator jt=produit.find(outer_index),jtend=produit.end();
      if (jt==jtend){
	if (inner_index==0)
	  return true;
	produit[outer_index]=it->g;
      }
      else
	jt->second += it->g;
    }
    return false;
  }

  template<class T,class U>
  T peval_x1_xn(
		typename std::vector< T_unsigned<T,U> >::const_iterator it,
		typename std::vector< T_unsigned<T,U> >::const_iterator itend,
		const typename std::vector<T> & v,
		const typename std::vector<U> & vars,
		const T & reduce){
    if (vars.empty())
      return it->g;
    int dim=vars.size()-1,nterms;
    if (dim!=int(v.size())){
#ifndef NO_STDEXCEPT
      throw(std::runtime_error("Invalid dimension"));
#endif
      return T(0);
    }
    U mainvar=vars.front(),var2=vars.back(),uend,u,prevu;
    T x=v.back();
    typename std::vector< T_unsigned<T,U> >::const_iterator itstop;
    typename std::vector<U>::const_iterator jtbeg=vars.begin(),jtend=vars.end(),jt;
    ++jtbeg;
    --jtend;
    typename std::vector<T>::const_iterator ktbeg=v.begin(),kt;
    T ans=0,total=0;
    for (;it!=itend;){
      // group monomials with the same x1..xn-1
      prevu = it->u % mainvar;
      if (dim==1)
	uend=0;
      else {
	const U & varxn=vars[dim-1];
	uend =(prevu/varxn)*varxn;
      }
      nterms = (prevu-uend)/var2;
      ans = it->g;
      if (nterms>2 && itend-it>nterms && (itstop=it+nterms)->u % mainvar==uend){
	// dense case
	for (;it!=itstop;){
	  ++it;
	  ans = (ans*x+it->g)%reduce;	  
	}
	++it;
      }
      else {
	for (++it;it!=itend;++it){
	  u = it->u %mainvar;
	  if (u<uend)
	    break;
	  if (prevu-u==var2)
	    ans = (ans*x+it->g)%reduce;
	  else
	    ans = (ans*powmod(x,(prevu-u)/var2,reduce)+it->g)%reduce;
	  prevu=u;
	}
	ans = (ans*powmod(x,(prevu-uend)/var2,reduce))%reduce;
      }
      for (jt=jtbeg,kt=ktbeg;jt!=jtend;++jt,++kt){
	// px=px*powmod(*kt,u / *jt,modulo);
	ans = (ans*powmod(*kt,uend / *jt,reduce))%reduce;
	uend = uend % *jt;
      }
      total = (total+ans) % reduce;
    }
    return total;
  }

  // eval p at x2...xn
  template<class T,class U>
  void peval_x2_xn(const typename std::vector< T_unsigned<T,U> > & p,
		   const typename std::vector<T> & v,
		   const std::vector<U> & vars,
		   std::vector< T_unsigned<T,U> > & res,
		   const T & reduce){
    U mainvar=vars.front(),deg1;
    res.clear();
    typename std::vector< T_unsigned<T,U> >::const_iterator it=p.begin(),itend=p.end(),it2;
    for (;it!=itend;){
      deg1=(it->u/mainvar)*mainvar;
      for (it2=it;it2!=itend;++it2){
	if (it2->u<deg1)
	  break;
      }
      T tmp=peval_x1_xn<T,U>(it,it2,v,vars,reduce);
      it=it2;
      if (!is_zero(tmp))
	res.push_back(T_unsigned<T,U>(tmp,deg1));
    }
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // NO_NAMESPACE_GIAC

#endif // _GIAC_THREADED_H
