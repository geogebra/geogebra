// -*- mode: C++ ; compile-command: "g++ -I.. -g -O2 -c index.cc" -*-
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
#ifndef _GIAC_INDEX_H_
#define _GIAC_INDEX_H_
#include "first.h"
#include "vector.h"
#include <iostream>
#include <string>

#pragma anon_unions

#if defined UNORDERED_MAP && !defined(__APPLE__) && !defined(VISUALC)
#include <tr1/unordered_map>
#define HASH_MAP_NAMESPACE std::tr1
#define hash_map unordered_map
#else // UNORDERED_MAP

#if (defined(VISUALC) || defined(BESTA_OS))
#undef HASH_MAP
#undef EXT_HASH_MAP
#endif

#ifdef HASH_MAP
#include <hash_map>
#ifndef HASH_MAP_NAMESPACE
#ifndef VISUALC 
#define HASH_MAP_NAMESPACE std
#endif // VISUALC
#endif // HASH_MAP_NAMESPACE
#endif

#ifdef EXT_HASH_MAP
#include <ext/hash_map>
#ifndef HASH_MAP_NAMESPACE
#define HASH_MAP_NAMESPACE __gnu_cxx
#endif
#endif

#endif // UNORDERED_MAP

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  typedef short int deg_t;
  typedef std::vector<deg_t> index_t;

  int mygcd(int a,int b);
  void swapint(int & a,int & b);
  void swapdouble(double & a,double & b);

  // index type for tensors

  index_t operator + (const index_t & a, const index_t & b);
  index_t operator - (const index_t & a, const index_t & b);
  index_t operator | (const index_t & a, const index_t & b);
  index_t operator - (const index_t & a);
  index_t operator * (const index_t & a, int fois);
  inline index_t operator * (int fois,const index_t & a){ return a*fois; }
  index_t operator / (const index_t & a, int divisepar);
  int operator / (const index_t & a, const index_t & b);
  // >= and <= are *partial* ordering on index_t
  // they return TRUE if and only if >= or <= is true for *all* coordinates
  bool all_sup_equal (const index_t & a, const index_t & b);
  inline bool operator >= (const index_t & a, const index_t & b){ return all_sup_equal(a,b); }
  bool all_inf_equal (const index_t & a, const index_t & b);
  inline bool operator <= (const index_t & a, const index_t & b){ return all_inf_equal(a,b); }
  index_t index_gcd(const index_t & a,const index_t & b);
  index_t index_lcm(const index_t & a,const index_t & b);
  inline index_t index_min(const index_t & a,const index_t & b){ return index_gcd(a,b); }
  inline index_t index_max(const index_t & a,const index_t & b){ return index_lcm(a,b); }
  void dbgprint(const index_t & i);
  std::string print_INT_(int i);
  std::string hexa_print_INT_(int i);
  std::string octal_print_INT_(int i);
  std::string binary_print_INT_(int i);
  std::string print_INT_(const std::vector<int> & v);
  std::string print_INT_(const std::vector<short int> & v);

  template <class T> T pow(const std::vector<T> & x, const index_t & n );

  // total degree of a std::vector
  template <class T> T total_degree(const std::vector<T> & v1);

  // two ordering function over indices: lex ordering and total order then lex
  template <class T>
  bool lex_is_greater(const std::vector<T> & v1, const std::vector<T> & v2);
  template <class T>
  bool total_revlex_is_greater(const std::vector<T> & v1, const std::vector<T> & v2);
  template <class T>
  bool total_lex_is_greater(const std::vector<T> & v1, const std::vector<T> & v2);

  index_t mergeindex(const index_t & i,const index_t & j);
  // permutation inverse
  std::vector<int> inverse(const std::vector<int> & p);
  // transposition
  std::vector<int> transposition(int i,int j,int size);
  bool has(const index_t & p,int r);
  // zero?
  bool is_zero(const index_t & p);

  template <class T> T pow(const std::vector<T> & x, const index_t & n ){
    assert(x.size()==n.size());
    typename std::vector<T>::const_iterator itx=x.begin();
    index_t::const_iterator itn=n.begin();
    T res(1);
    for (;itx!=x.end();++itx,++itn){
      res=res*pow(*itx,*itn);
    }
    return res;
  }

  template <class T> T total_degree(const std::vector<T> & v1){
    T i=0;
    for (typename std::vector<T>::const_iterator it=v1.begin();it!=v1.end();++it)
      i=i+(*it);
    return(i);
  }

  template <class T>
  bool lex_is_greater(const std::vector<T> & v1, const std::vector<T> & v2){
    assert(v1.size()==v2.size());
    typename std::vector<T>::const_iterator it1=v1.begin(),it1end=v1.end();
    typename std::vector<T>::const_iterator it2=v2.begin();
    for (;it1!=it1end;++it2,++it1){
      if ( (*it1)!=(*it2) ){
	if  ( (*it1)>(*it2))
	  return(true);
	else
	  return(false);
      }
    }
    return(true);
  }

  template <class T>
  bool lex_is_strictly_greater(const std::vector<T> & v1, const std::vector<T> & v2){
    assert(v1.size()==v2.size());
    typename std::vector<T>::const_iterator it1=v1.begin(),it1end=v1.end();
    typename std::vector<T>::const_iterator it2=v2.begin();
    for (;it1!=it1end;++it2,++it1){
      if ( (*it1)!=(*it2) ){
	if  ( (*it1)>(*it2))
	  return true;
	else
	  return false;
      }
    }
    return false;
  }

  bool lex_is_strictly_greater_deg_t(const std::vector<deg_t> & v1, const std::vector<deg_t> & v2);

  template <class T>
  bool total_lex_is_greater(const std::vector<T> & v1, const std::vector<T> & v2){
    T d1=total_degree(v1);
    T d2=total_degree(v2);
    if (d1!=d2){
      if (d1>d2)
	return(true);
      else
	return(false);
    }
    return(lex_is_greater<T>(v1,v2));
  }

  template <class T>
  bool total_revlex_is_greater(const std::vector<T> & v1, const std::vector<T> & v2){
    T d1=total_degree(v1);
    T d2=total_degree(v2);
    if (d1!=d2){
      if (d1>d2)
	return(true);
      else
	return(false);
    }
    // return(!lex_is_strictly_greater<T>(v1,v2)); but starting from end
    typename std::vector<T>::const_iterator it1=v1.end()-1,it1end=v1.begin()-1;
    typename std::vector<T>::const_iterator it2=v2.end()-1;
    for (;it1!=it1end;--it2,--it1){
      if ( *it1 != *it2 )
	return *it1<*it2;
    }
    return true;
  }

  template <class T>
  bool total_revlex_is_strictly_greater(const std::vector<T> & v1, const std::vector<T> & v2){
    return !total_revlex_is_greater<T>(v2,v1);
  }

  //*****************************************
  // class for memory efficient indices
  //*****************************************

  class ref_index_t {
  public:
    int ref_count;
    index_t i;
    ref_index_t():ref_count(1) {}
    ref_index_t(int s):ref_count(1),i(s) {}
    ref_index_t(const index_t & I):ref_count(1),i(I) {}
    ref_index_t(index_t::const_iterator it,index_t::const_iterator itend):ref_count(1),i(it,itend) {}
  };

  // direct access to deg_t in index_m 
  const int POLY_VARS_DIRECT=sizeof(ref_index_t *)/sizeof(deg_t);
  // HAS_POLY_VARS_OTHER defines the number of word (pointer size) for 
  // other deg_t directly encoded. Comment if none
#define HAS_POLY_VARS_OTHER 1
#if HAS_POLY_VARS_OTHER
  const int POLY_VARS_OTHER=HAS_POLY_VARS_OTHER*POLY_VARS_DIRECT;
#else
  const int POLY_VARS_OTHER=0;
#endif
  // capacity of deg_t by direct addressing
  const int POLY_VARS=POLY_VARS_DIRECT+POLY_VARS_OTHER-1;

#if defined(GIAC_NO_OPTIMIZATIONS) || ((defined(VISUALC) || defined(__APPLE__)) && !defined(GIAC_VECTOR))
  class index_m {
  public:
    ref_index_t * riptr;
    // construct
    index_m(const index_m & im) { 
      riptr=im.riptr;
      ++riptr->ref_count;
    }
    index_m(const index_t & i){
      riptr=new ref_index_t(i);
    }
    index_m(){ riptr=new ref_index_t; }
    index_m(size_t s){
      riptr=new ref_index_t(s);
    }
    index_m(index_t::const_iterator it,index_t::const_iterator itend){
      riptr=new ref_index_t(it,itend);
    }
    // delete
    ~index_m(){
      --riptr->ref_count;
      if (!riptr->ref_count)
	delete riptr;
    }
    // copy
    const index_m & operator = (const index_m & other){
      --riptr->ref_count;
      if (!riptr->ref_count)
	delete riptr;
      riptr=other.riptr; 
      ++riptr->ref_count;
      return *this;
    }
    
    // members
    index_t iref() const { return riptr->i;} ;
    index_t::iterator begin() { return riptr->i.begin(); }
    index_t::iterator end() { return riptr->i.end(); }
    index_t::reverse_iterator rbegin() { return riptr->i.rbegin(); }
    index_t::reverse_iterator rend() { return riptr->i.rend(); }
    index_t::const_iterator begin() const { return riptr->i.begin(); }
    index_t::const_iterator end() const { return riptr->i.end(); }
    index_t::const_reverse_iterator rbegin() const { return riptr->i.rbegin(); }
    index_t::const_reverse_iterator rend() const { return riptr->i.rend(); }
    deg_t & front() { return *begin(); }
    deg_t front() const { return *begin(); }
    deg_t & back() { return *(end()-1); }
    deg_t back() const { return *(end()-1); }
    deg_t & operator [] (size_t pos) { return *(begin()+pos); }
    deg_t operator [] (size_t pos) const { return *(begin()+pos); }
    void clear() { riptr->i.clear(); }
    void reserve(size_t n) { riptr->i.reserve(n); }
    void push_back(deg_t x) { riptr->i.push_back(x); }
    size_t size() const { return riptr->i.size(); }
    bool is_zero() const ; 
    size_t total_degree() const ;
    friend std::ostream & operator << (std::ostream & os, const index_m & m ){
      os << ":index_m:[ " ;
      for (index_t::const_iterator it=m.begin();it!=m.end();++it)
	os << *it << " ";
      os << "] " ;
      return(os);
    }
    void dbgprint() const {
      std::cout << *this << std::endl;
    }
    // set first index element to 0
    index_m set_first_zero() const { index_t i(riptr->i); i[0]=0; return i; }
  };

#else // VISUALC
  class index_m {
  public:
    union {
      ref_index_t * riptr;
      struct {
	deg_t taille; 
	deg_t direct[POLY_VARS_DIRECT-1]; 
      };
    };
#ifdef HAS_POLY_VARS_OTHER
    deg_t other[POLY_VARS_OTHER];
#endif
    // construct
    index_m(const index_m & im) { 
      if ( im.taille % 2){
	* (unsigned long *) & taille = * (unsigned long *) &im.taille;
#if (HAS_POLY_VARS_OTHER==1)
	* (unsigned long *) other = * (unsigned long *) im.other;	
#endif
#if (HAS_POLY_VARS_OTHER==2)
	* (unsigned long *) other = * (unsigned long *) im.other;	
	* (((unsigned long *) other)+1) = * (((unsigned long *) im.other)+1);	
#endif
#if (HAS_POLY_VARS_OTHER>2)
	unsigned long * target = (unsigned long *) other, * end = target + POLY_VARS_OTHER/(sizeof(unsigned long)/sizeof(deg_t));
	const unsigned long * source = (unsigned long *) im.other;
	for (;target!=end;++target,++source)
	  *target=*source;
#endif
      } else {
	riptr=im.riptr;
	++riptr->ref_count;
      }
    }
    index_m(const index_t & i){
      int s=i.size();
      if (s<=POLY_VARS){
	taille=2*s+1;
	deg_t * target=direct,*end=direct+s;
	index_t::const_iterator source=i.begin();
	for (;target!=end;++source,++target){
	  *target=*source;
	}
      }
      else {
	// taille = 0;
	riptr=new ref_index_t(i);
      }
    }
    index_m(){ taille =1; }
    index_m(size_t s){
      if (int(s)<=POLY_VARS){
	riptr=0;
	taille=2*s+1;
#if (HAS_POLY_VARS_OTHER==1)
	* (unsigned long *) other =0;
#endif
#if (HAS_POLY_VARS_OTHER==2)
	* (unsigned long *) other =0;
	* (((unsigned long *) other)+1) =0;
#endif
#if (HAS_POLY_VARS_OTHER>2)
	unsigned long * target = (unsigned long *) other ;
	unsigned long * end = target + POLY_VARS_OTHER/(sizeof(unsigned long)/sizeof(deg_t));
	for (;target!=end;++target)
	  *target = 0;
#endif
      }
      else {
	// taille=0;
	riptr=new ref_index_t(s);
      }
    }
    index_m(index_t::const_iterator it,index_t::const_iterator itend){
      if (itend-it<=POLY_VARS){
	taille=2*(itend-it)+1;
	deg_t * target = direct;
	for (;it!=itend;++it,++target){
	  *target=*it;
	}
      }
      else {
	// taille=0;
	riptr=new ref_index_t(it,itend);
      }
    }
    // ptr[0] must be 2*size+1
    index_m(deg_t * ptr){
      /*
#ifdef DEBUG_SUPPORT
      if (ptr[0]/2>POLY_VARS)
	setsizeerr("Error index.h, size too large for direct access");
#endif
      */
      unsigned long * source = (unsigned long *) ptr;
      *(unsigned long *) &taille = *(unsigned long *) source;
#if (HAS_POLY_VARS_OTHER==1)
      ++source;
      * (unsigned long *) other = *source;
#endif
#if (HAS_POLY_VARS_OTHER==2)
      ++source;
      * (unsigned long *) other = *source;
      ++source;
      * (((unsigned long *) other)+1) = *source;
#endif
#if (HAS_POLY_VARS_OTHER>2)
      unsigned long * target = (unsigned long *) other ;
      unsigned long * end = target + POLY_VARS_OTHER/(sizeof(unsigned long)/sizeof(deg_t));
      for (++source;target!=end;++source,++target)
	*target = *source;
#endif
    }
    // delete
    ~index_m(){
      if ( (taille % 2) == 0){
	--riptr->ref_count;
	if (!riptr->ref_count)
	  delete riptr;
      }
    }
    // copy
    const index_m & operator = (const index_m & other){
      if ( (taille % 2) == 0){
	--riptr->ref_count;
	if (!riptr->ref_count)
	  delete riptr;
      }
      if ( (other.taille % 2) == 0){
	riptr=other.riptr; 
	++riptr->ref_count;
      }
      else {
	* (unsigned long *) &taille = * (unsigned long *) &other.taille;
#if (HAS_POLY_VARS_OTHER==1)
	* (unsigned long *) this->other = * (unsigned long *) other.other;
#endif
#if (HAS_POLY_VARS_OTHER==2)
	* (unsigned long *) this->other = * (unsigned long *) other.other;
	* (((unsigned long *) this->other)+1) = * (((unsigned long *) other.other)+1);
#endif
#if (HAS_POLY_VARS_OTHER>2)
	const unsigned long * source = (unsigned long * ) other.other;
	unsigned long * target = (unsigned long *) this->other; 
	unsigned long * end = target + POLY_VARS_OTHER/(sizeof(unsigned long)/sizeof(deg_t));
	for (;target!=end;++source,++target)
	  * target = * source;
#endif
      }
      return *this;
    }
    
    // members
    index_t iref() const ;
    index_t::iterator begin() ; 
    index_t::iterator end() ; 
    index_t::const_iterator begin() const; 
    index_t::const_iterator end() const; 
    deg_t & front() { return *begin(); }
    deg_t front() const { return *begin(); }
    deg_t & back() { return *(end()-1); }
    deg_t back() const { return *(end()-1); }
    deg_t & operator [] (size_t pos) { return *(begin()+pos); }
    deg_t operator [] (size_t pos) const { return *(begin()+pos); }
    void clear() ;
    void reserve(size_t n);
    void push_back(deg_t x);
    size_t size() const ;
    bool is_zero() const ;
    size_t total_degree() const ;
    friend std::ostream & operator << (std::ostream & os, const index_m & m ){
      os << ":index_m:[ " ;
      for (index_t::const_iterator it=m.begin();it!=m.end();++it)
	os << *it << " ";
      os << "] " ;
      return(os);
    }
    void dbgprint() const {
      std::cout << *this << std::endl;
    }
    // set first index element to 0
    index_m set_first_zero() const;
  };
#endif // VISUALC

#ifdef HASH_MAP_NAMESPACE
  inline size_t index_hash_function(const index_t & v){
    index_t::const_iterator it=v.begin(),itend=v.end();
    size_t res=0;
    if (itend-it>16)
      itend=it+16;
    if (itend-it>8){
      for (;it!=itend;++it)
	res = (res << 2) | *it;
    }
    else {
      for (;it!=itend;++it)
	res = (res << 4) | *it;
    }
    return res;
  }

  /*
  inline size_t index_hash_function(const vector<int> & v){
    vector<int>::const_iterator it=v.begin(),itend=v.end();
    if (itend-it>16)
      itend=it+16;
    size_t res=0,decal=32/(itend-it);
    for (;;){
      --itend;
      res = (res << decal) | *itend;
      if (itend==it)
	return res;
    }
  }
  */

  class hash_function_object {
  public:
    size_t operator () (const index_t & v) const { return index_hash_function(v); }
    hash_function_object() {};
  };

  typedef HASH_MAP_NAMESPACE::hash_map< index_t,index_m,hash_function_object > hash_index ;  

  // extern std::vector<hash_index> global_hash_index;

#endif

  index_m operator + (const index_m & a, const index_m & b);
  index_m operator - (const index_m & a, const index_m & b);
  index_m operator * (const index_m & a, int fois);
  inline index_m operator * (int fois,const index_m & a){ return a*fois; }
  index_m operator / (const index_m & a, int divisepar);
  inline int operator / (const index_m & a,const index_m & b){ return a.iref() / b.iref();}
  bool operator == (const index_m & i1, const index_m & i2);
  bool operator != (const index_m & i1, const index_m & i2);
  bool operator >= (const index_m & a, const index_m & b);
  bool operator <= (const index_m & a, const index_m & b);
  int sum_degree(const index_m & v1);
  inline int total_degree(const index_m & v1){ return sum_degree(v1); }
  bool i_lex_is_greater(const index_m & v1, const index_m & v2);
  bool i_lex_is_strictly_greater(const index_m & v1, const index_m & v2);
  bool i_total_revlex_is_greater(const index_m & v1, const index_m & v2);
  bool i_total_revlex_is_strictly_greater(const index_m & v1, const index_m & v2);
  bool i_total_lex_is_greater(const index_m & v1, const index_m & v2);
  bool i_total_lex_is_strictly_greater(const index_m & v1, const index_m & v2);

  template <class T> T pow(const std::vector<T> & x, const index_m & n ){
    assert(x.size()==n.size());
    typename std::vector<T>::const_iterator itx=x.begin();
    index_t::const_iterator itn=n.begin();
    T res(1);
    for (;itx!=x.end();++itx,++itn){
      res=res*pow(*itx,*itn);
    }
    return res;
  }

  void index_lcm(const index_m & a,const index_m & b,index_t & res);
  bool disjoint(const index_m & a,const index_m & b);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // ndef _GIAC_INDEX_H_
