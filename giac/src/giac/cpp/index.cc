// -*- mode: C++ ; compile-command: "g++ -I.. -g -O2 -c index.cc" -*-
#include "giacPCH.h"
/*
 *  Copyright (C) 2000,7 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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

using namespace std;
#include "index.h"
#include <cmath>
#include <stdio.h>
#include <stdexcept>
#ifdef DEBUG_SUPPORT
#include "giacintl.h"
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

#ifdef NO_STDEXCEPT
#undef DEBUG_SUPPORT
#endif

#ifdef DEBUG_SUPPORT
  void setsizeerr(const std::string & s);
#endif

  int mygcd(int a,int b){
    if (b)
      return mygcd(b,a%b);
    else
      return a<0?-a:a;
  }

  void swapint(int & a,int & b){
    int tmp=a;
    a=b;
    b=tmp;
  }

  void swapdouble(double & a,double & b){
    double tmp=a;
    a=b;
    b=tmp;
  }

  void index_gcd(const index_t & a,const index_t & b,index_t & res){
    index_t::const_iterator ita=a.begin(),itaend=a.end(),itb=b.begin();
    unsigned s=unsigned(itaend-ita);
    res.resize(s);
    index_t::iterator itres=res.begin();  
#ifdef DEBUG_SUPPORT
    if (s!=b.size())
      setsizeerr(gettext("Error index.cc index_gcd"));
#endif // DEBUG_SUPPORT
    for (;ita!=itaend;++itb,++itres,++ita)
      *itres=giacmin(*ita,*itb);
  }

  index_t index_gcd(const index_t & a,const index_t & b){
    index_t res;
    index_gcd(a,b,res);
    return res;
  }

  index_t index_lcm(const index_t & a,const index_t & b){
    index_t::const_iterator ita=a.begin(),itaend=a.end(),itb=b.begin();
    unsigned s=unsigned(itaend-ita);
    index_t res(s);
    index_t::iterator itres=res.begin();  
#ifdef DEBUG_SUPPORT
    if (s!=b.size())
      setsizeerr(gettext("index.cc index_lcm"));
#endif // DEBUG_SUPPORT
    for (;ita!=itaend;++itb,++itres,++ita)
      *itres=giacmax(*ita,*itb);
    return res;
  }

  void index_lcm(const index_m & a,const index_m & b,index_t & res){
    index_t::const_iterator ita=a.begin(),itaend=a.end(),itb=b.begin();
    unsigned s=unsigned(itaend-ita);
    res.resize(s);
    index_t::iterator itres=res.begin();  
    for (;ita!=itaend;++itb,++itres,++ita)
      *itres=giacmax(*ita,*itb);
  }

  // index and monomial ordering/operations implementation
  void add(const index_t & a, const index_t & b,index_t & res){
    index_t::const_iterator ita=a.begin(),itaend=a.end(),itb=b.begin();
    index_t::iterator itres=res.begin();  
    for (;ita!=itaend;++itb,++itres,++ita)
      *itres=(*ita)+(*itb);
  }

  void add(const index_m & a, const index_m & b,index_t & res){
    index_t::const_iterator ita=a.begin(),itaend=a.end(),itb=b.begin();
    index_t::iterator itres=res.begin();  
    for (;ita!=itaend;++itb,++itres,++ita)
      *itres=(*ita)+(*itb);
  }

  index_t operator + (const index_t & a, const index_t & b){
    index_t::const_iterator ita=a.begin(),itaend=a.end(),itb=b.begin();
    unsigned s=unsigned(itaend-ita);
    index_t res(s);
    index_t::iterator itres=res.begin();  
#ifdef DEBUG_SUPPORT
    if (s!=b.size())
      setsizeerr(gettext("index.cc operator +"));
#endif // DEBUG_SUPPORT
    for (;ita!=itaend;++itb,++itres,++ita)
      *itres=(*ita)+(*itb);
    return res;
  }

  index_t operator - (const index_t & a, const index_t & b){
    index_t res;
    index_t::const_iterator ita=a.begin(),itaend=a.end(),itb=b.begin();
    unsigned s=unsigned(itaend-ita);
#ifdef DEBUG_SUPPORT
    if (s!=b.size())
      setsizeerr(gettext("index.cc operator -"));
#endif // DEBUG_SUPPORT
    res.reserve(s);
    for (;ita!=itaend;++ita,++itb)
      res.push_back((*ita)-(*itb));
    return res;
  }

  index_t operator | (const index_t & a, const index_t & b){
    index_t res;
    index_t::const_iterator ita=a.begin(),itaend=a.end(),itb=b.begin();
    unsigned s=unsigned(itaend-ita);
#ifdef DEBUG_SUPPORT
    if (s!=b.size())
      setsizeerr(gettext("index.cc operator |"));
#endif // DEBUG_SUPPORT
    res.reserve(s);
    for (;ita!=itaend;++ita,++itb)
      res.push_back((*ita) | (*itb));
    return res;
  }

  index_t operator - (const index_t & a){
    index_t res;
    index_t::const_iterator ita=a.begin(),itaend=a.end();
    int s=int(itaend-ita);
    res.reserve(s);
    for (;ita!=itaend;++ita)
      res.push_back(-(*ita));
    return res;
  }

  index_t operator * (const index_t & a, int fois){
    index_t res;
    index_t::const_iterator ita=a.begin(),itaend=a.end();
    res.reserve(itaend-ita);
    for (;ita!=itaend;++ita)
      res.push_back((*ita)*fois);
    return res;
  }

  index_t operator / (const index_t & a, int divisepar){
    index_t res;
    index_t::const_iterator ita=a.begin(),itaend=a.end();
    res.reserve(itaend-ita);
    for (;ita!=itaend;++ita)
      res.push_back((*ita)/divisepar);
    return res;
  }

  int operator / (const index_t & a, const index_t & b){
    index_t::const_iterator ita=a.begin(),itaend=a.end(),itb=b.begin(),itbend=b.end();
#ifdef DEBUG_SUPPORT
    if (itaend-ita!=signed(b.size()))
      setsizeerr(gettext("index.cc operator /"));
#endif // DEBUG_SUPPORT
    for (;ita!=itaend;++ita,++itb){
      if (*itb)
	return *ita / *itb;
    }
    return 0;
  }

  bool all_sup_equal (const index_t & a, const index_t & b){
    index_t::const_iterator ita=a.begin(),itaend=a.end(),itb=b.begin();
#ifdef DEBUG_SUPPORT
    if (itaend-ita!=signed(b.size()))
      setsizeerr(gettext("index.cc operator >="));
#endif // DEBUG_SUPPORT
    for (;ita!=itaend;++ita,++itb){
      if ((*ita)<(*itb))
	return false;
    }
    return true;
  }

  bool all_inf_equal (const index_t & a, const index_t & b){
    index_t::const_iterator ita=a.begin(),itaend=a.end(),itb=b.begin();
#ifdef DEBUG_SUPPORT
    if (itaend-ita!=signed(b.size()))
      setsizeerr(gettext("index.cc operator <="));
#endif // DEBUG_SUPPORT
    for (;ita!=itaend;++ita,++itb){
      if ((*ita)>(*itb))
	return false;
    }
    return true;
  }

  void add_print_INT_(string & s,int i){
    char c[256];
    my_sprintf(c,"%d",i);
    s += c;
  }

  string print_INT_(int i){
    char c[256];
    my_sprintf(c,"%d",i);
    return c;
  }

  string hexa_print_INT_(int i){
    char c[256];
    my_sprintf(c,"%X",i);
    return string("0x")+c;
  }

  string octal_print_INT_(int i){
    char c[256];
    my_sprintf(c,"%o",i);
    return string("0o")+c;
  }

  string binary_print_INT_(int i){
    char c[256];
    mpz_t tmp;
    mpz_init_set_ui(tmp, i);
    mpz_get_str(c, 2, tmp);
    mpz_clear(tmp);
    return string("0b")+c;
  }

  /*
  string print_INT_(int i){
    if (!i)
      return string("0");
    if (i<0)
      return string("-")+print_INT_(-i);      
    int length = (int) std::floor(std::log10((double) i));
    char s[length+2];
    s[length+1]=0;
    for (;length>-1;--length,i/=10)
      s[length]=i%10+'0';
    return s;
  }
  */

  string print_INT_(const vector<short int> & m){
    vector<short int>::const_iterator it=m.begin(),itend=m.end();
    if (it==itend)
      return "";
    string s("[");
    for (;;){
      s += print_INT_(*it);
      ++it;
      if (it==itend){
	s +=']';
	return s;
      }
      else
	s += ',';
    }
  }
  
  string print_INT_(const vector<int> & m){
    vector<int>::const_iterator it=m.begin(),itend=m.end();
    if (it==itend)
      return "";
    string s("[");
    for (;;){
      s += print_INT_(*it);
      ++it;
      if (it==itend)
	return s+']';
      else
	s += ',';
    }
  }
  
#ifdef NSPIRE
  template<class T> nio::ios_base<T> & operator << (nio::ios_base<T> & os, const index_t & m ){
    return os << ":index_t: " << print_INT_(m) << " " ;
  }
#else
  ostream & operator << (ostream & os, const index_t & m ){
    return os << ":index_t: " << print_INT_(m) << " " ;
  }
#endif

  void dbgprint(const index_t & i){
    COUT << i << endl;
  }

  index_t mergeindex(const index_t & i,const index_t & j){
    index_t res(i);
    index_t::const_iterator it=j.begin(),itend=j.end();
    res.reserve(i.size()+(itend-it));
    for (;it!=itend;++it)
      res.push_back(*it);
    return res;
  }

  // by convention 0 -> 0 for permutations beginning at index 1
  vector<int> inverse(const vector<int> & p){
    vector<int> inv(p);
    int n=int(p.size());
    for (int i=0;i<n;i++){
      inv[p[i]]=i; // that's the definition of inv!!
    }
    return inv;
  }

  // transposition
  vector<int> transposition(int i,int j,int size){
    if (i>j)
      return transposition(j,i,size);
    vector<int> t;
    for (int k=0;k<i;k++)
      t.push_back(k);
    t.push_back(j);
    for (int k=i+1;k<j;k++)
      t.push_back(k);
    t.push_back(i);
    for (int k=j+1;k<size;k++)
      t.push_back(k);
    return t;
  }

  bool has(const index_t & p,int r){
    index_t::const_iterator it=p.begin(),itend=p.end();
    for (;it!=itend;++it){
      if (*it==r)
	return true;
    }
    return false;
  }

  bool is_zero(const index_t & p){
    index_t::const_iterator it=p.begin(),itend=p.end();
    for (;it!=itend;++it){
      if (*it)
	return false;
    }
    return true;
  }

#if defined(GIAC_NO_OPTIMIZATIONS) || ((defined(VISUALC) || defined(__APPLE__)) && !defined(GIAC_VECTOR)) || defined __clang__ // || defined NSPIRE
  bool operator == (const index_m & i1, const index_m & i2){
    if (i1.riptr==i2.riptr)
      return true;
    return (i1.riptr->i==i2.riptr->i);
  }

  int sum_degree_from(const index_m & v1,int start){
    index_t & i1=v1.riptr->i;
    index_t::const_iterator it = i1.begin()+start,itend = i1.end();
    int i=0;
    for (;it!=itend;++it)
      i += *it;
    return i;
  }

#else
  index_t index_m::iref() const { 
    if ( (taille % 2)==0)
      return riptr->i;
    return index_t(direct,direct+taille/2);
  }

  index_t::iterator index_m::begin() { 
    if ( (taille % 2)==0)
      return riptr->i.begin(); 
    return index_t::iterator((giac::deg_t *) direct);
  }

  index_t::iterator index_m::end() { 
    if ( (taille % 2)==0)
      return riptr->i.end(); 
    return index_t::iterator((giac::deg_t *) direct + taille/2) ;
  }

  index_t::const_iterator index_m::begin() const { 
    if ( (taille % 2)==0)
      return riptr->i.begin(); 
    return index_t::const_iterator((giac::deg_t *) direct);
  }

  index_t::const_iterator index_m::end() const { 
    if ( (taille % 2)==0)
      return riptr->i.end(); 
    return index_t::const_iterator((giac::deg_t *) direct + taille/2 );
  }

  void index_m::clear() { 
    if ( (taille % 2)==0)
      riptr->i.clear(); 
    else
      taille=1;
  }

  void index_m::reserve(size_t n) { 
    if (int(n)>POLY_VARS){
      if ( taille % 2)
	// alloc a true vector with correct size, copy into
	riptr = new ref_index_t(begin(),end()); 
      // taille=0;
      riptr->i.reserve(n); 
    }
  }

  void index_m::push_back(deg_t x){ 
    if ( taille % 2){
      int pos = taille /2 ;
      taille += 2;
      if (pos<POLY_VARS){
	direct[pos]=x;
	return;
      }
      riptr = new ref_index_t(index_t::iterator((giac::deg_t *)direct),index_t::iterator((giac::deg_t *)direct+pos)); 
      // taille = 0;
    }
    riptr->i.push_back(x); 
  }

  size_t index_m::size() const { 
    if (taille % 2)
      return taille/2;
    else
      return riptr->i.size(); 
  }

  index_m index_m::set_first_zero() const {
    if ( (taille % 2) == 0){
      index_t i(riptr->i);
      assert(i.size());
      i[0]=0;
      return index_m(i);
    }
    index_m copie(*this);
    copie.direct[0]=0;
    return copie;
  }

  bool operator == (const index_m & i1, const index_m & i2){
    if (((i1.taille % 2))==0){
      if (i1.riptr==i2.riptr)
	return true;
#if 0 // def x86_64
      const index_t & i1t=i1.riptr->i;
      const index_t & i2t=i2.riptr->i;
      int n=i1t.size();
      if (n!=i2.size()) return false;
      const ulonglong * ptr1=(const ulonglong *)&i1t.front(),* ptr1end=ptr1+n/4,*ptr2=(const ulonglong *)&i2t.front();
      for (;ptr1!=ptr1end;++ptr2,++ptr1){
	if (*ptr1!=*ptr2)
	  return false;
      }
      const deg_t * i1ptr=(const deg_t *) ptr1,*i1end=i1ptr+n%4,* i2ptr= (const deg_t *) ptr2;    
      for (;i1ptr!=i1end;++i2ptr,++i1ptr){
	if (*i1ptr!=*i2ptr)
	  return false;
      }
      return true;
#else
      return (i1.riptr->i==i2.riptr->i);
#endif
    }
    if (i1.taille!=i2.taille)
      return false;
    const deg_t * i1ptr=i1.direct, *i1end=i1ptr+i1.taille/2,* i2ptr=i2.direct;
    for (;i1ptr!=i1end;++i2ptr,++i1ptr){
      if (*i1ptr!=*i2ptr)
	return false;
    }
    return true;
  }

  int sum_degree_from(const index_m & v1,int start){
    index_t::const_iterator it,itend;
    if ( (v1.taille % 2)==0){
      index_t & i=v1.riptr->i;
      it = i.begin()+start;
      itend = i.end();
    }
    else {
      it = index_t::const_iterator((giac::deg_t *) v1.direct);
      itend = it + v1.taille/2;
      it += start;
    }
    int i=0;
    for (;it!=itend;++it)
      i += *it;
    return i;
  }

#endif // VISUALC

  bool index_m::is_zero() const {
    index_t::const_iterator it=begin(),itend=end();
    for (;it!=itend;++it){
      if (*it)
	return false;
    }
    return true;
  }

  size_t index_m::total_degree() const { 
    size_t i=0;
    for (index_t::const_iterator it=begin();it!=end();++it)
      i=i+(*it);
    return i;
  }

  
  index_m operator + (const index_m & a, const index_m & b){
    const deg_t * ita=&*a.begin(), * itb=&*b.begin();
    int s=int(a.size());
    const deg_t * itaend=ita+s;
#ifdef DEBUG_SUPPORT
    if (s!=signed(b.size()))
      setsizeerr(gettext("index.cc index_m operator +"));
#endif // DEBUG_SUPPORT
    index_m res(s);
    deg_t * it=(deg_t*)&*res.begin();
#if 0 // def x86_64
    ulonglong * target=(ulonglong *) &*it;
    const ulonglong * ptr1=(const ulonglong *) &*ita,* ptr1end=ptr1+s/(sizeof(ulonglong)/sizeof(deg_t));
    const ulonglong * ptr2=(const ulonglong *) &*itb;
    for (;ptr1!=ptr1end;++target,++ptr2,++ptr1){
      *target=*ptr1+*ptr2;
    }
    ita=(const deg_t*)&*ptr1;
    itb=(const deg_t*)&*ptr2;
    it=(deg_t*)&*target;
#endif
    for (;ita!=itaend;++it,++itb,++ita)
      *it = (*ita)+(*itb);
    return res;
  }

  index_m operator - (const index_m & a, const index_m & b){
    index_t::const_iterator ita=a.begin();
    index_t::const_iterator itaend=a.end();
    index_t::const_iterator itb=b.begin();
    int s=int(itaend-ita);
#ifdef DEBUG_SUPPORT
    if (s!=signed(b.size()))
      setsizeerr(gettext("index.cc index_m operator -"));
#endif // DEBUG_SUPPORT
    index_m res(s);
    index_t::iterator it=res.begin();
    for (;ita!=itaend;++it,++itb,++ita)
      *it = (*ita)-(*itb);
    return res;
  }

  index_m operator * (const index_m & a, int fois){
    index_t::const_iterator ita=a.begin(),itaend=a.end();
    index_m res(itaend-ita);
    index_t::iterator it=res.begin();
    for (;ita!=itaend;++it,++ita)
      *it = (*ita)*fois;
    return res;
  }

  index_m operator / (const index_m & a, int divisepar){
    index_t::const_iterator ita=a.begin(),itaend=a.end();
    index_m res(itaend-ita);
    index_t::iterator it=res.begin();
    for (;ita!=itaend;++it,++ita)
      *it = (*ita)/divisepar;
    return res;
  }

  bool operator != (const index_m & i1, const index_m & i2){
    return !(i1==i2);
  }

  // >= and <= are *partial* ordering on index_t
  // they return TRUE if and only if >= or <= is true for *all* coordinates
  bool operator >= (const index_m & a, const index_m & b){
    index_t::const_iterator ita=a.begin(),itaend=a.end();
    index_t::const_iterator itb=b.begin();
#ifdef DEBUG_SUPPORT
    if (itaend-ita!=signed(b.size()))
      setsizeerr(gettext("index.cc index_m operator >="));
#endif
    for (;ita!=itaend;++ita,++itb){
      if ((*ita)<(*itb))
	return false;
    }
    return true;
  }

  bool operator <= (const index_m & a, const index_m & b){
    index_t::const_iterator ita=a.begin(),itaend=a.end();
    index_t::const_iterator itb=b.begin();
#ifdef DEBUG_SUPPORT
    if (itaend-ita!=signed(b.size()))
      setsizeerr(gettext("index.cc index_m operator >="));
#endif
    for (;ita!=itaend;++ita,++itb){
      if ((*ita)>(*itb))
	return false;
    }
    return true;
  }

  bool equal(const index_m & a,const index_t &b){
    index_t::const_iterator ita=a.begin(),itaend=a.end();
    index_t::const_iterator itb=b.begin();
    for (;ita!=itaend;++itb,++ita){
      if (*ita!=*itb)
	return false;
    }
    return true;
  }

  int sum_degree(const index_m & v1){
    int i=0;
    index_t::const_iterator it=v1.begin(),itend=v1.end();
    for (;it!=itend;++it)
      i += *it;
    return i;
  }

  bool i_lex_is_greater(const index_m & v1, const index_m & v2){
    index_t::const_iterator it1=v1.begin();
    index_t::const_iterator it2=v2.begin();
    index_t::const_iterator it1end=v1.end();
#ifdef DEBUG_SUPPORT
    if (it1end-it1!=signed(v2.size()))
      setsizeerr(gettext("index.cc index_m i_lex_is_greater"));
#endif
    for (;it1!=it1end;++it1){
      if ( (*it1)!=(*it2) ){
	if  ( (*it1)>(*it2))
	  return(true);
	else
	  return(false);
      }
      ++it2;
    }
    return(true);
  }

  bool lex_is_strictly_greater_deg_t(const std::vector<deg_t> & v1, const std::vector<deg_t> & v2){
    assert(v1.size()==v2.size());
    std::vector<deg_t>::const_iterator it1=v1.begin(),it1end=v1.end();
    std::vector<deg_t>::const_iterator it2=v2.begin();
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

  bool i_lex_is_strictly_greater(const index_m & v1, const index_m & v2){
    index_t::const_iterator it1=v1.begin();
    index_t::const_iterator it2=v2.begin();
    index_t::const_iterator it1end=v1.end();
#ifdef DEBUG_SUPPORT
    if (it1end-it1!=signed(v2.size()))
      setsizeerr(gettext("index.cc index_m i_lex_is_greater"));
#endif
    for (;it1!=it1end;++it1){
      if ( (*it1)!=(*it2) ){
	if  ( (*it1)>(*it2))
	  return(true);
	else
	  return(false);
      }
      ++it2;
    }
    return(false);
  }

  /*
  bool i_revlex_is_greater(const index_m & v1, const index_m & v2){
    return revlex_is_greater(*v1.iptr,*v2.iptr);
  }
  */

  bool i_total_lex_is_greater(const index_m & v1, const index_m & v2){
    int d1=sum_degree(v1);
    int d2=sum_degree(v2);
    if (d1!=d2){
      if (d1>d2)
	return(true);
      else
	return(false);
    }
    return(i_lex_is_greater(v1,v2));
  }

  bool i_total_lex_is_strictly_greater(const index_m & v1, const index_m & v2){ 
    return !i_total_lex_is_greater(v2,v1); 
  }

  bool i_total_revlex_is_greater(const index_m & v1, const index_m & v2){
    int d1=sum_degree(v1);
    int d2=sum_degree(v2);
    if (d1!=d2){
      if (d1>d2)
	return(true);
      else
	return(false);
    }
    // find order with variables reversed then reverse order
    // return !i_lex_is_strictly_greater(v1,v2);
    index_t::const_iterator it1=v1.end()-1;
    index_t::const_iterator it2=v2.end()-1;
    index_t::const_iterator it1end=v1.begin()-1;
#ifdef DEBUG_SUPPORT
    if (it1-it1end!=signed(v2.size()))
      setsizeerr(gettext("index.cc index_m i_total_revlex_is_greater"));
#endif
    for (;it1!=it1end;--it1){
      if ( *it1 != *it2 )
	return *it1<*it2;
      --it2;
    }
    return true;
  }

  // revlex on 1st 3 vars, then revlex on remaining vars
  bool i_3var_is_greater(const index_m & v1, const index_m & v2){
    index_t::const_iterator it1=v1.begin();
    index_t::const_iterator it2=v2.begin();
    int d1=*it1+*(it1+1)+*(it1+2);
    int d2=*it2+*(it2+1)+*(it2+2);
    if (d1!=d2)
      return d1>=d2;
    if (*(it1+2)!=*(it2+2))
      return *(it1+2)<=*(it2+2);
    if (*(it1+1)!=*(it2+1))
      return *(it1+1)<=*(it2+1);
    if (*it1!=*it2) v1.dbgprint(); // instantiate
    d1=sum_degree_from(v1,3); 
    d2=sum_degree_from(v2,3);
    if (d1!=d2)
      return d1>=d2;
    index_t::const_iterator it1end=it1+2;
    it1 = v1.end()-1;
    it2 = v2.end()-1;
    for (;it1!=it1end;--it1,--it2){
      if (*it1!=*it2)
	return *it1<=*it2;
    }
    return true;
  }

  // revlex on 1st 7 vars, then revlex on remaining vars
  bool i_7var_is_greater(const index_m & v1, const index_m & v2){
    index_t::const_iterator it1=v1.begin();
    index_t::const_iterator it2=v2.begin();
    int d1=*it1+*(it1+1)+*(it1+2)+*(it1+3)+*(it1+4)+*(it1+5)+*(it1+6);
    int d2=*it2+*(it2+1)+*(it2+2)+*(it2+3)+*(it2+4)+*(it2+5)+*(it2+6);
    if (d1!=d2)
      return d1>=d2;
    if (*(it1+6)!=*(it2+6))
      return *(it1+6)<=*(it2+6);
    if (*(it1+5)!=*(it2+5))
      return *(it1+5)<=*(it2+5);
    if (*(it1+4)!=*(it2+4))
      return *(it1+4)<=*(it2+4);
    if (*(it1+3)!=*(it2+3))
      return *(it1+3)<=*(it2+3);
    if (*(it1+2)!=*(it2+2))
      return *(it1+2)<=*(it2+2);
    if (*(it1+1)!=*(it2+1))
      return *(it1+1)<=*(it2+1);
    d1=sum_degree_from(v1,7); 
    d2=sum_degree_from(v2,7);
    if (d1!=d2)
      return d1>=d2;
    index_t::const_iterator it1end=it1+6;
    it1 = v1.end()-1;
    it2 = v2.end()-1;
    for (;it1!=it1end;--it1,--it2){
      if (*it1!=*it2)
	return *it1<=*it2;
    }
    return true;
  }

  // revlex on 1st 11 vars, then revlex on remaining vars
  bool i_11var_is_greater(const index_m & v1, const index_m & v2){
    index_t::const_iterator it1=v1.begin();
    index_t::const_iterator it2=v2.begin();
    int d1=*it1+*(it1+1)+*(it1+2)+
      *(it1+3)+*(it1+4)+*(it1+5)+*(it1+6)+
      *(it1+7)+*(it1+8)+*(it1+9)+*(it1+10);
    int d2=*it2+*(it2+1)+*(it2+2)+
      *(it2+3)+*(it2+4)+*(it2+5)+*(it2+6)+
      *(it2+7)+*(it2+8)+*(it2+9)+*(it2+10);
    if (d1!=d2)
      return d1>=d2;
    if (*(it1+10)!=*(it2+10))
      return *(it1+10)<=*(it2+10);
    if (*(it1+9)!=*(it2+9))
      return *(it1+9)<=*(it2+9);
    if (*(it1+8)!=*(it2+8))
      return *(it1+8)<=*(it2+8);
    if (*(it1+7)!=*(it2+7))
      return *(it1+7)<=*(it2+7);
    if (*(it1+6)!=*(it2+6))
      return *(it1+6)<=*(it2+6);
    if (*(it1+5)!=*(it2+5))
      return *(it1+5)<=*(it2+5);
    if (*(it1+4)!=*(it2+4))
      return *(it1+4)<=*(it2+4);
    if (*(it1+3)!=*(it2+3))
      return *(it1+3)<=*(it2+3);
    if (*(it1+2)!=*(it2+2))
      return *(it1+2)<=*(it2+2);
    if (*(it1+1)!=*(it2+1))
      return *(it1+1)<=*(it2+1);
    d1=sum_degree_from(v1,11); 
    d2=sum_degree_from(v2,11);
    if (d1!=d2)
      return d1>=d2;
    index_t::const_iterator it1end=it1+10;
    it1 = v1.end()-1;
    it2 = v2.end()-1;
    for (;it1!=it1end;--it1,--it2){
      if (*it1!=*it2)
	return *it1<=*it2;
    }
    return true;
  }

  int nvar_total_degree(const index_m & v1,int n){
    index_t::const_iterator it1=v1.begin(),it1l=it1+n;
    int d1;
    for (d1=0;it1<it1l;++it1){
      d1 += *it1;
    }
    return d1;
  }

  // revlex on 1st n vars, then revlex on remaining vars
  bool i_nvar_is_greater(const index_m & v1, const index_m & v2,int n,bool sametdeg){
    int d1,d2;
    index_t::const_iterator it1beg=v1.begin(),it1=it1beg,it1end=it1+n;
    index_t::const_iterator it2=v2.begin();
    if (sametdeg){
      it1 += n; it2 += n;
    }
    else {
      for (d1=0,d2=0;it1<it1end;++it2,++it1){
	d1 += *it1;
	d2 += *it2;
      }
      if (d1!=d2)
	return d1>=d2;
    }
    for (--it2,--it1;it1!=it1beg;--it2,--it1){
      if (*it1!=*it2)
	return *it1<=*it2;
    }
    it1end=v1.end();
    for (d1=0,d2=0,it1+=n,it2+=n;it1!=it1end;++it2,++it1){
      d1 += *it1;
      d2 += *it2;
    }
    if (d1!=d2)
      return d1>=d2;
    it1 = it1end-1;
    it2 = v2.end()-1;
    it1end=it1beg+n-1;
    for (;it1!=it1end;--it2,--it1){
      if (*it1!=*it2)
	return *it1<=*it2;
    }
    return true;
  }

  // revlex on 1st 16 vars, then revlex on remaining vars
  bool i_16var_is_greater(const index_m & v1, const index_m & v2){
    return i_nvar_is_greater(v1,v2,16,false);
  }

  // revlex on 1st 32 vars, then revlex on remaining vars
  bool i_32var_is_greater(const index_m & v1, const index_m & v2){
    return i_nvar_is_greater(v1,v2,32,false);
  }

  // revlex on 1st 64 vars, then revlex on remaining vars
  bool i_64var_is_greater(const index_m & v1, const index_m & v2){
    return i_nvar_is_greater(v1,v2,64,false);
  }

  bool i_total_revlex_is_strictly_greater(const index_m & v1, const index_m & v2){ 
    return !i_total_revlex_is_greater(v2,v1); 
  }

  bool disjoint(const index_m & a,const index_m & b){
    index_t::const_iterator it=a.begin(),itend=a.end(),jt=b.begin();
    for (;it!=itend;++jt,++it){
      if (*it && *jt)
	return false;
    }
    return true;
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
