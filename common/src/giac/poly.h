/* -*- mode:C++  -*-  */
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
#ifndef _GIAC_POLY_H_
#define _GIAC_POLY_H_
#include "first.h"
#include <fstream>
#include "vector.h"
#include <string>
#include "fraction.h"
#include "index.h"
#include "monomial.h"
#include "threaded.h"
#include <algorithm>

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  // the tensor class to represent polynomial
  template <class T> class tensor{
  public:
    // members
    int dim; // number of indices, this is the size of monomial.index
    std::vector< monomial<T> > coord; // sorted list of monomials
    // T zero;
    // functional object sorting function for monomial ordering
    bool (* is_strictly_greater)( const index_m &, const index_m &);
    std::pointer_to_binary_function < const monomial<T> &, const monomial<T> &, bool> m_is_strictly_greater ;
    // constructors
    tensor(const tensor<T> & t) : dim(t.dim), coord(t.coord), is_strictly_greater(t.is_strictly_greater), m_is_strictly_greater(t.m_is_strictly_greater) { }
    tensor(const tensor<T> & t, const std::vector< monomial<T> > & v) : dim(t.dim), coord(v), is_strictly_greater(t.is_strictly_greater), m_is_strictly_greater(t.m_is_strictly_greater) { }
    // warning: this constructor prohibits construction of tensor from a value
    // of type T if this value is an int, except by using tensor<T>(T(int))
    tensor() : dim(0), is_strictly_greater(i_lex_is_strictly_greater), m_is_strictly_greater(std::ptr_fun<const monomial<T> &, const monomial<T> &, bool>(m_lex_is_strictly_greater<T>)) { }
    explicit tensor(int d) : dim(d), is_strictly_greater(i_lex_is_strictly_greater), m_is_strictly_greater(std::ptr_fun<const monomial<T> &, const monomial<T> &, bool>(m_lex_is_strictly_greater<T>)) { }
    explicit tensor(int d,const tensor<T> & t) : dim(d),is_strictly_greater(t.is_strictly_greater), m_is_strictly_greater(t.m_is_strictly_greater)  { }
    tensor(const monomial<T> & v) : dim(v.index.size()), is_strictly_greater(i_lex_is_strictly_greater), m_is_strictly_greater(std::ptr_fun<const monomial<T> &, const monomial<T> &, bool>(m_lex_is_strictly_greater<T>)) { 
      coord.push_back(v);
    }
    tensor(const T & v, int d) : dim(d), is_strictly_greater(i_lex_is_strictly_greater), m_is_strictly_greater(std::ptr_fun<const monomial<T> &, const monomial<T> &, bool>(m_lex_is_strictly_greater<T>)) {
      if (!is_zero(v))
	coord.push_back(monomial<T>(v,0,d));
    }
    tensor(int d,const std::vector< monomial<T> > & c) : dim(d), coord(c), is_strictly_greater(i_lex_is_strictly_greater),m_is_strictly_greater(std::ptr_fun<const monomial<T> &, const monomial<T> &, bool>(m_lex_is_strictly_greater<T>)) { }
    ~tensor() { coord.clear(); }
    // member functions
    // ordering monomials in the tensor
    void tsort(){ sort(coord.begin(),coord.end(),m_is_strictly_greater); }
    int lexsorted_degree() const{ 
      if (!dim)
	return 0;
      if (coord.empty())
	return 0;
      else
	return coord.front().index.front(); 
    }
    int degree(int n) const ;
    int valuation(int n) const ;
    index_t degree() const ;
    int total_degree() const ;
    void reverse() ; // reverse variable ordering
    void append(const tensor<T> &);
    std::vector< tensor<T> > Tcoeffs() const;
    tensor<T> multiplydegrees(int d) const ;
    tensor<T> dividedegrees(int d) const ;
    tensor<T> dividealldegrees(int d) const ;
    void reorder(const std::vector<int> & permutation) ;
    // shift and multiply, shift and divide, shift only
    tensor<T> shift (const index_m & ishift,const T & fois) const ;
    tensor<T> shift (const T & fois,const index_m & ishift) const ;
    tensor<T> shift (const index_m & ishift) const ;
    // tensor<T> operator + (const T & other) const ;
    void TAdd(const tensor<T> &other,tensor<T> & result) const;
    // tensor<T> operator - (const tensor<T> & other) const ;
    void TSub(const tensor<T> &other,tensor<T> & result) const;
    // tensor<T> operator * (const tensor<T> & other) const ;
    // tensor<T> operator * (const T & fact) const ;
    tensor<T> & operator *= (const T & fact) ;
    // tensor<T> operator - () const ;
    // tensor<T> operator / (const tensor<T> & other) const ;
    // tensor<T> operator / (const T & fact) const ;
    tensor<T> & operator /= (const T & fact) ;
    // tensor<T> operator % (const tensor<T> & other) const ;
    bool TDivRem (const tensor<T> & other, tensor<T> & quo, tensor<T> & rem, bool allowrational = true ) const ; // this=quo*other+rem
    bool TDivRemHash(const tensor<T> & b,tensor<T> & quo,tensor<T> & r,bool allowrational=false,int exactquo=0,double qmax=0.0) const ; // same as TDivRem but allowrationnal=false *and* poly with 1 main variable
    bool TDivRem1(const tensor<T> & b,tensor<T> & quo,tensor<T> & r,bool allowrational=false,int exactquo=0) const ; 
    bool Texactquotient (const tensor<T> & other, tensor<T> & quo,bool allowrational=false ) const ; // this=quo*other+rem, rem must be 0
    bool TPseudoDivRem (const tensor<T> & other, tensor<T> & quo, tensor<T> & rem, tensor<T> & a) const ; // a*this=quo*other+rem
    // bool TDivRem (const T & x0, tensor<T> & quo, tensor<T> & rem) const ;
    tensor<T> operator () (const T & x0 ) const;
    // T operator () (const std::vector<T> & x0) const;
    T constant_term() const ;
    T norm() const;
    tensor<T> derivative() const ;
    tensor<T> integrate() const ;
    // insertion of a monomial and reordering
    void insert_monomial(const monomial<T> & c);
    // position corresponding to an index, return -1 if not found
    int position(const index_m & v) const;
    index_m vector_int(int position) const;
    T & operator () ( const index_m & v) ;
    const T & operator () ( const index_m & v) const ;
    tensor<T> untrunc1 (int j=0) const {
      std::vector< monomial<T> > v;
      Untrunc1(this->coord,j,v);
      return tensor<T>(dim+1,v);
    }
    void untruncn (int j=0) {
      Untruncn(this->coord,j);
      ++dim;
    }
    tensor<T> untrunc (int j,int dim) const {
      std::vector< monomial<T> > v;
      Untrunc(this->coord,j,dim,v);
      return tensor<T>(dim,v);
    }
    tensor<T> trunc1 () const {
      assert(dim);
      std::vector< monomial<T> > v;
      Trunc1(this->coord,v);
      return tensor<T>(dim-1,v);
    }
    int gcddeg(int k) const;
    index_t gcddeg() const;
    // printing
    std::string print() const {
      if (coord.empty())
        return "";
      std::string s;
      typename std::vector< monomial<T> > :: const_iterator it=coord.begin(),itend=coord.end();
      for (;;){
        s += it->print();
        ++it;
        if (it==itend)
	  return s;
        s +=  '+' ;
      }
    };
    void dbgprint() const { std::cout << print() << std::endl; }
    void high_order_degree_truncate(int n){
      // suppress terms of order >= n
      typename std::vector< monomial<T> >::iterator it=coord.begin(),itend=coord.end();
      for (;it!=itend;++it){
	if (it->index.front()<n)
	  break;
      }
      if (it!=coord.begin() && it!=itend)
	coord.erase(coord.begin(),it);
    }
    tensor<T> total_degree_truncate(int n) const {
      tensor<T> res(dim);
      // suppress terms of total degree > n
      typename std::vector< monomial<T> >::const_iterator it=coord.begin(),itend=coord.end();
      for (;it!=itend;++it){
	if (sum_degree(it->index)<=n)
	  res.coord.push_back(*it);
      }
      return res;
    }
  };
  template <class T> class Tref_tensor{
  public:
    int ref_count;
    tensor<T> t;
    Tref_tensor<T>(const tensor<T> & P): ref_count(1),t(P) {}
    Tref_tensor<T>(int dim): ref_count(1),t(dim) {}
  };

  // convert p to monomial represented by unsigned integers
  // using the rule [a1 a2 .. an] [deg1 ... degn ] -> 
  // (... (a1*deg2+a2)*deg3 +...)*degn+an
  template<class T,class U>
  void convert(const tensor<T> & p,const index_t & deg,std::vector< T_unsigned<T,U> >  & v){
    typename std::vector< monomial<T> >::const_iterator it=p.coord.begin(),itend=p.coord.end(),itstop;
    v.clear();
    v.reserve(itend-it);
    T_unsigned<T,U> gu;
    U u;
    int nterms;
    index_t::const_iterator itit,itcur,idxcur,idxend,ditbeg=deg.begin(),ditend=deg.end(),dit;
    for (;it!=itend;++it){
      u=0;
      itcur=itit=it->index.begin();
      for (dit=ditbeg;dit!=ditend;++itit,++dit)
	u=u*unsigned(*dit)+unsigned(*itit);
      gu.u=u;
      gu.g=it->value;
      v.push_back(gu);
      // dense poly check 
      --itit;
      nterms=*itit;
      if (nterms<2 || nterms>=itend-it)
	continue;
      itstop=it+nterms;
      if (itstop->index.back())
	continue;
      for (idxcur=itstop->index.begin(),idxend=itstop->index.end()-1;idxcur!=idxend;++idxcur,++itcur){
	if (*idxcur!=*itcur)
	  break;
      }
      if (idxcur!=idxend)
	continue;
      // this part is dense
      for (;it!=itstop;){
	++it;
	gu.g=it->value;
	--gu.u;
	v.push_back(gu);
      }
    }
  }

  template<class T,class U>
  void convert(const std::vector< T_unsigned<T,U> > & v,const index_t & deg,tensor<T> & p){
    typename std::vector< T_unsigned<T,U> >::const_iterator it=v.begin(),itend=v.end();
    index_t::const_reverse_iterator ditbeg=deg.rbegin(),ditend=deg.rend(),dit;
    p.dim=ditend-ditbeg;
    p.coord.clear();
    p.coord.reserve(itend-it);
    U u;
    index_t i(p.dim);
    int k;
    for (;it!=itend;++it){
      u=it->u;
      for (k=p.dim-1,dit=ditbeg;dit!=ditend;++dit,--k){
	i[k]=u % unsigned(*dit);
	u = u/unsigned(*dit);
      }
      p.coord.push_back(monomial<T>(it->g,i));
    }
  }

  template <class T>
  bool operator == (const tensor<T> & p,const tensor<T> & q){
    return p.dim==q.dim && p.coord.size()==q.coord.size() && p.coord==q.coord;
  }

  template <class T> bool tensor_is_strictly_greater(const tensor<T> & p,const tensor<T> & q){
    if (q.coord.empty())
      return true;
    if (p.coord.empty())
      return false;
    return p.m_is_strictly_greater(p.coord.front(),q.coord.front());
  }

  template <class T> bool operator >= (const tensor<T> & p,const tensor<T> & q){
    return tensor_is_strictly_greater(p,q);
  }

  template <class T> bool operator <= (const tensor<T> & p,const tensor<T> & q){
    return tensor_is_strictly_greater(q,p);
  }

  template <class T>
  void tensor<T>::insert_monomial(const monomial<T> & c){
    coord.push_back(c);
    sort(coord.begin(),coord.end(),m_is_strictly_greater);
  }

  template <class T>
  int tensor<T>::degree(int n) const {
    typename std::vector< monomial<T> >::const_iterator it=this->coord.begin();
    typename std::vector< monomial<T> >::const_iterator it_end=this->coord.end();
    int res=0;
    for (;it!=it_end;++it){
      int temp=(it->index)[n];
      if (res<temp)
	res=temp;
    }
    return res;
  }

  template <class T>
  int tensor<T>::total_degree() const {
    typename std::vector< monomial<T> >::const_iterator it=this->coord.begin();
    typename std::vector< monomial<T> >::const_iterator it_end=this->coord.end();
    int res=0;
    for (;it!=it_end;++it){
      int temp=sum_degree(it->index);
      if (res<temp)
	res=temp;
    }
    return res;
  }

  template <class T>
  int tensor<T>::valuation(int n) const {
    typename std::vector< monomial<T> >::const_iterator it=this->coord.begin();
    typename std::vector< monomial<T> >::const_iterator it_end=this->coord.end();
    if (it==it_end)
      return 0;
    int res=(it->index)[n];
    for (;it!=it_end;++it){
      int temp=(it->index)[n];
      if (res>temp)
	res=temp;
    }
    return res;
  }

  template <class T>
  index_t tensor<T>::degree() const {
    typename std::vector< monomial<T> >::const_iterator it=this->coord.begin(),it2;
    typename std::vector< monomial<T> >::const_iterator it_end=this->coord.end();
    index_t res(dim);
    index_t::iterator itresbeg=res.begin(),itresend=res.end(),itres;
    index_t::const_iterator ittemp,ittemp2,ittempend;
    if (//false &&
	is_strictly_greater==i_lex_is_strictly_greater){
      for (;it!=it_end;++it){
	ittemp=it->index.begin();
	for (itres=itresbeg;itres!=itresend;++itres,++ittemp){
	  if (*itres<*ittemp)
	    *itres=*ittemp;
	}
	// check if the polynomial is dense -> skip ?
	--ittemp; // point to xn power
	if (*ittemp<3 || *ittemp>=it_end-it)
	  continue;
	it2=it+(*ittemp); // if dense, point to the last monomial with same x1..xn-1 
	if (it2->index.back()) // last power in xn must be 0
	  continue;
	ittemp=it->index.begin();
	ittemp2=it2->index.begin();
	ittempend=ittemp+dim-1; // check all other powers
	for (;ittemp!=ittempend;++ittemp2,++ittemp){
	  if (*ittemp!=*ittempend)
	    break;
	}
	if (ittemp!=ittempend)
	  continue;
	it=it2;
      }
    }
    else {
      for (;it!=it_end;++it){
	ittemp=it->index.begin();
	for (itres=itresbeg;itres!=itresend;++itres,++ittemp){
	  if (*itres<*ittemp)
	    *itres=*ittemp;
	}
      }
    }
    return res;
  }

  template <class T>
  void tensor<T>::append(const tensor<T> & p){
    if (p.coord.empty())
      return;
    if (this->coord.empty()){
      this->dim=p.dim;
      this->coord=p.coord;
      return;
    }
    if (is_strictly_greater(this->coord.back().index , p.coord.front().index)){
      this->coord.reserve(this->coord.size()+p.coord.size());
      typename std::vector< monomial<T> >::const_iterator it=p.coord.begin();
      typename std::vector< monomial<T> >::const_iterator it_end=p.coord.end();
      for (;it!=it_end;++it)
	this->coord.push_back(*it);
    }
    else
      TAdd(p,*this); // *this=*this+p;
  }

  template <class T>
  tensor<T> tensor<T>::multiplydegrees(int d) const {
    tensor<T> res(dim);
    typename std::vector< monomial<T> >::const_iterator it=coord.begin(),it_end=coord.end();
    for (;it!=it_end;++it){
      index_t i=it->index.iref();
      i.front() *= d;
      res.coord.push_back(monomial<T>(it->value,i));
    }
    return res;
  }

  template <class T>
  tensor<T> tensor<T>::dividedegrees(int d) const {
    tensor<T> res(dim);
    typename std::vector< monomial<T> >::const_iterator it=coord.begin(),it_end=coord.end();
    for (;it!=it_end;++it){
      index_t i=it->index.iref();
      i.front() /= d;
      res.coord.push_back(monomial<T>(it->value,i));
    }
    return res;
  }

  template <class T>
  tensor<T> tensor<T>::dividealldegrees(int d) const {
    tensor<T> res(dim);
    typename std::vector< monomial<T> >::const_iterator it=coord.begin(),it_end=coord.end();
    for (;it!=it_end;++it){
      index_t i=it->index.iref();
      i = i/d;
      res.coord.push_back(monomial<T>(it->value,i));
    }
    return res;
  }

  template <class T> 
  int tensor<T>::position(const index_m & v) const {
    int smax=coord.size()-1;
    int smin=0;
    int s;
    for (;smin<smax;){
      s=(smax+smin)/2; // smin <= s < smax
      index_m vs=coord[s].index;
      if (v==vs)
	break;
      if (is_strictly_greater(v,vs)) // if v > v[s] must start above smin+1
	smax=s-1; // same
      else 
	smin=s+1; // keeps smin <=smax
    }
    s=(smax+smin)/2; // if loop breaked return the correct s, else smin=smax
    index_m vs=coord[s].index;
    if (v==vs)
      return(s);
    else
      return(-1);
  }

  template <class T>  
  index_m tensor<T>::vector_int(int position) const {
    return(coord[position].index);
  }

  template <class T>
  T & tensor<T>::operator () ( const index_m & v) {
    int p=position(v);
    if (p!=-1)
      return coord[p].value;
    coord.push_back(T(0),v);
    p=position(v);
    return coord[p].value;
  }

  template<class T>  
  const T & tensor<T>::operator () ( const index_m & v) const{
    static T myzero(0);
    int p=position(v);
    if (p==-1) {
      return myzero;
    }
    else
      return (coord[p].value);
  }

  template <class T>
  std::ostream & operator << (std::ostream & os, const tensor<T> & t)
  {
    return os << t.print();
  }


  template <class T>
  void lexsort(std::vector < monomial<T> > & v){
    sort(v.begin(),v.end(),std::ptr_fun<const monomial<T> &, const monomial<T> &, bool>(m_lex_is_strictly_greater<T>));
  }



  template <class T>
  int tensor<T>::gcddeg(int k) const {
    assert(k<dim);
    int res=0;
    typename std::vector< monomial<T> >::const_iterator it=coord.begin(),itend=coord.end();
    for (;it!=itend;++it){
      res=mygcd((it->index)[k],res);
      if (res==1)
	break;
    }
    return res;
  }

  template <class T>
  index_t tensor<T>::gcddeg() const {
    typename std::vector< monomial<T> >::const_iterator it=coord.begin(),itend=coord.end();
    assert(itend!=it);
    index_t res(it->index.iref());
    index_t zero(res.size());
    for (;it!=itend;++it){
      res=index_gcd(it->index.iref(),res);
      if (res==zero)
	break;
    }
    return res;
  }

  // univariate Horner evaluation with remainder, rem is the evaluation result
  // as a n-1-dimensional tensor and quo the quotient as a n-dim tensor
  /*
    template <class T>
    bool tensor<T>::TDivRem (const T & x0, tensor<T> & quo, tensor<T> & rem) const
    {
    if (coord.empty()){
    rem=*this;
    quo=*this;
    return true;
    }
    std::vector< monomial<T> > horner_coord(coord);
    tensor<T> add_rem(dim-1), add_quo(dim-1);
    lexsort(horner_coord);
    tensor<T> quotient(*this,new_seq);
    rem.coord.clear();
    quo.coord.clear();
    typename std::vector< monomial<T> >::const_iterator it = horner_coord.begin();
    index_m pui=it->index;
    for (;it!=horner_coord.end();++it){
    if (pui.front()==it->index.front()) { 
    // same external power, add
    add_rem.coord.push_back(it->trunc1());
    add_quo.coord.push_back(*it);
    }
    else {      // different power do an Horner *
    rem=(rem+add_rem)*pow(x0,pui.front()-it->index.front());
    add_rem.coord.clear();
    add_rem.coord.push_back(it->trunc1());
    for (;pui.front()> it->index.front();){
    pui[0]--;
    add_quo.divided_by_x();
    add_quo=add_quo*x0;
    quo=quo+add_quo;
    }
    add_quo.coord.clear();
    pui=it->index;
    }
    }
    rem=(rem+add_rem)*pow(x,pui.front());
    for (;pui.front();){
    pui[0]--;
    add_quo.divided_by_x();
    add_quo=add_quo*x0;
    quo=quo+add_quo;
    }
    // sort remainder and quotient
    rem.tsort();
    quo.tsort();
    return true;
    }
  */

  // univariate Horner evaluation with remainder, rem is the evaluation result
  // as a n-1-dimensional tensor, the quotient is not computed
  template <class T>
  tensor<T> tensor<T>::operator () (const T & x0 ) const
  {
    if (coord.empty())
      return *this;
    if (!dim){
      return *this;
    }
    std::vector< monomial<T> > horner_coord(coord);
    tensor<T> rem(dim-1),add_rem(dim-1) ;
    lexsort(horner_coord);
    typename std::vector< monomial<T> >::const_iterator it = horner_coord.begin();
    index_m pui=(*it).index;
    for (;it!=horner_coord.end();++it){
      if (pui.front()==it->index.front()) { 
	// same external power, add
	add_rem.coord.push_back(it->trunc1());
      }
      else {      // different power do an Horner *
#if 1 // GIAC_VECTOR
	rem.TAdd(add_rem,rem); rem *= pow(x0,pui.front()-it->index.front()); 
#else
	rem =(add_rem+rem)*pow(x0,pui.front()-it->index.front());
#endif
	add_rem.coord.clear();
	add_rem.coord.push_back(it->trunc1());
	pui=it->index;
      }
    }
    rem.TAdd(add_rem,rem); // rem=(add_rem+rem);
    if (pui.front()){
#if 1 // GIAC_VECTOR
      rem *= pow(x0,pui.front());
#else
      rem = rem*pow(x0,pui.front());
#endif
    }
    rem.tsort();
    return rem;
  }


  template <class T>
  void tensor<T>::TAdd (const tensor<T> & other,tensor<T> & result) const {  
    typename std::vector< monomial<T> >::const_iterator a=coord.begin();
    typename std::vector< monomial<T> >::const_iterator a_end=coord.end();
    if (a == a_end) {
      result=other;
      return ;
    }
    typename std::vector< monomial<T> >::const_iterator b=other.coord.begin();
    typename std::vector< monomial<T> >::const_iterator b_end=other.coord.end();
    if (b==b_end){
      result= *this;
      return ;
    }
    Add(a,a_end,b,b_end,result.coord,is_strictly_greater);
  }

  /*
    template <class T>
    tensor<T> tensor<T>::operator + (const tensor<T> & other) const {  
    // Tensor addition
    typename std::vector< monomial<T> >::const_iterator a=coord.begin();
    typename std::vector< monomial<T> >::const_iterator a_end=coord.end();
    if (a == a_end) {
    return other;
    }
    typename std::vector< monomial<T> >::const_iterator b=other.coord.begin();
    typename std::vector< monomial<T> >::const_iterator b_end=other.coord.end();
    if (b==b_end){
    return *this;
    }
    std::vector< monomial<T> > new_coord;
    Add(a,a_end,b,b_end,new_coord,is_strictly_greater);
    return tensor<T>(*this,new_coord);
    }

  */

  template <class T>
  void tensor<T>::TSub (const tensor<T> & other,tensor<T> & result) const {
    typename std::vector< monomial<T> >::const_iterator a=coord.begin();
    typename std::vector< monomial<T> >::const_iterator a_end=coord.end();
    typename std::vector< monomial<T> >::const_iterator b=other.coord.begin();
    typename std::vector< monomial<T> >::const_iterator b_end=other.coord.end();
    if (b == b_end) {
      result= *this;
      return;
    }
    Sub(a,a_end,b,b_end,result.coord,is_strictly_greater);
  }

  /*
    template <class T>
    tensor<T> tensor<T>::operator - (const tensor<T> & other) const {  
    // Tensor addition
    typename std::vector< monomial<T> >::const_iterator a=coord.begin();
    typename std::vector< monomial<T> >::const_iterator a_end=coord.end();
    typename std::vector< monomial<T> >::const_iterator b=other.coord.begin();
    typename std::vector< monomial<T> >::const_iterator b_end=other.coord.end();
    if (b == b_end) {
    return *this;
    }
    std::vector< monomial<T> > new_coord;
    Sub(a,a_end,b,b_end,new_coord,is_strictly_greater);
    return tensor<T>(*this,new_coord);
    }

    template <class T>
    tensor<T> tensor<T>::operator - () const {  
    // Tensor addition
    std::vector< monomial<T> > new_coord;
    typename std::vector< monomial<T> >::const_iterator a = coord.begin();
    typename std::vector< monomial<T> >::const_iterator a_end = coord.end();
    new_coord.reserve(((int) a_end - (int) a )/(sizeof(monomial<T>)));  
    for (;a!=a_end;++a){
    new_coord.push_back(monomial<T>(-(*a).value,(*a).index));
    }
    return tensor<T>(*this,new_coord);
    }

    template <class T>
    tensor<T> tensor<T>::operator * (const tensor<T> & other) const {  
    // Multiplication
    typename std::vector< monomial<T> >::const_iterator ita = coord.begin();
    typename std::vector< monomial<T> >::const_iterator ita_end = coord.end();
    typename std::vector< monomial<T> >::const_iterator itb = other.coord.begin();
    typename std::vector< monomial<T> >::const_iterator itb_end = other.coord.end();
    //  std::cout << coord.size() << " " << (int) ita_end - (int) ita << " " << sizeof(monomial<T>) << std::endl ;
    // first some trivial cases
    if (ita==ita_end)
    return(*this);
    if (itb==itb_end)
    return(other);
    if (is_one(*this))
    return other;
    if (is_one(other))
    return *this;
    // Now look if length a=1 or length b=1, happens frequently
    // think of x^3*y^2*z translated to internal form
    int c1=coord.size();
    if (c1==1)
    return(other.shift(coord.front().index,coord.front().value));
    int c2=other.coord.size();
    if (c2==1)
    return(this->shift(other.coord.front().index,other.coord.front().value));
    std::vector< monomial<T> > new_coord;
    new_coord.reserve(c1+c2); // assumes dense poly (would be c1+c2-1)
    Mul(ita,ita_end,itb,itb_end,new_coord,m_is_strictly_greater);
    return tensor<T>(*this,new_coord);
    }

  */

  template <class T>
  tensor<T> Tpow(const tensor<T> & x,int n){
#ifndef NO_STDEXCEPT
    if (n<0)
      setsizeerr("poly.h/Tpow n<0");
#endif
    if (!n)
      return tensor<T>(T(1),x.dim);
    if (n==1)
      return x;
    if (n==2)
      return x*x;
    if (x.coord.size()==1)
      return tensor<T>(monomial<T>(pow(x.coord.front().value,n),x.coord.front().index*n));
    tensor<T> res(x);
    for (int j=1;j<n;j++)
      res=res*x;
    return res;
    /* 
       Note: contrary to univariate polynomials or integers
       the "fast" powering algorithm is *slower* than the above
       loop for multivariate polynomials (with the current implementation of Mul)
       Indeed a dense poly of deg. aa and d variables may have 
       binomial(aa+d,d) monomials
       hence the last multiplication in the "fast" powering algorithm
       is O(binomial(n/2*deg+d,d)^2)=O(n^(2d))
       as the n multiplications of the above loop are
       O(n*binomial(n*deg+d,d)) = O(n^(d+1))
       tensor<T> temp=Tpow(x,n/2);
       if (n%2)
       return tensor<T>(temp*temp*x);
       else
       return tensor<T>(temp*temp);
    */
  }

  /*
    template <class T>
    tensor<T> tensor<T>::operator / (const tensor<T> & other) const {  
    tensor<T> rem(*this),quo(*this);
    assert( (*this).TDivRem(other,quo,rem) );
    return(quo);
    }

  template <class T>
  tensor<T> tensor<T>::operator % (const tensor<T> & other) const {  
    tensor<T> rem(*this),quo(*this);
    assert( (*this).TDivRem(other,quo,rem) );
    return(rem);
  }
  
  */

  /*
  template <class T>
  tensor<T> tensor<T>::operator * (const T & fact ) const {  
    // Tensor constant multiplication
    if (is_one(fact))
      return *this;
    std::vector< monomial<T> > new_coord;
    if (is_zero(fact))
      return tensor<T>(*this,new_coord);
    typename std::vector< monomial<T> >::const_iterator a = coord.begin();
    typename std::vector< monomial<T> >::const_iterator a_end = coord.end();
    Mul(a,a_end,fact,new_coord);
    return tensor<T>(*this,new_coord);
  }

  inline template <class T>
  tensor<T> operator * (const T & fact ,const tensor<T> & p){
    return p*fact;
  }
  */

  template <class T>
  tensor<T> & tensor<T>::operator *= (const T & fact ) {  
    // Tensor constant multiplication
    if (is_one(fact))
      return *this;
    if (is_zero(fact)){
      coord.clear();
      return *this;
    }
    typename std::vector< monomial<T> >::const_iterator a = coord.begin();
    typename std::vector< monomial<T> >::const_iterator a_end = coord.end();
    Mul<T>(a,a_end,fact,coord);
    return *this;
  }

  /*
  template <class T>
  tensor<T> tensor<T>::operator / (const T & fact ) const {  
    if (is_one(fact))
      return *this;
    std::vector< monomial<T> > new_coord;
    typename std::vector< monomial<T> >::const_iterator a = coord.begin();
    typename std::vector< monomial<T> >::const_iterator a_end = coord.end();
    Div(a,a_end,fact,new_coord);
    return tensor<T>(*this,new_coord);
  }
  */

  template <class T>
  tensor<T> & tensor<T>::operator /= (const T & fact ) {  
    if (is_one(fact))
      return *this;
    typename std::vector< monomial<T> >::const_iterator a = coord.begin();
    typename std::vector< monomial<T> >::const_iterator a_end = coord.end();
    Div<T>(a,a_end,fact,coord);
    return *this;
  }

  template<class T>
  tensor<T> Tnextcoeff(typename std::vector< monomial<T> >::const_iterator & it,const typename std::vector< monomial<T> >::const_iterator & itend){
    if (it==itend)
      return tensor<T>(0);
    int n=it->index.front();
    int d=it->index.size();
    tensor<T> res(d-1);
    for (;(it!=itend) && (it->index.front()==n);++it)
      res.coord.push_back(it->trunc1());
    return res;
  }

  template<class T>
  tensor<T> Tlastcoeff(const typename std::vector< monomial<T> >::const_iterator & itbeg,const typename std::vector< monomial<T> >::const_iterator & itend){
    assert(itbeg!=itend);
    typename std::vector< monomial<T> >::const_iterator it=itend;
    --it;
    int n=it->index.front();
    int d=it->index.size();
    tensor<T> res(d-1);
    for (;;){
      if (it==itbeg)
	break;
      --it;
      if (it->index.front()!=n){
	++it;
	break;
      }
    }
    for (;it!=itend;++it)
      res.coord.push_back(it->trunc1());
    return res;
  }

  template <class T>
  bool tensor<T>::TDivRem1(const tensor<T> & b,tensor<T> & quo,tensor<T> & r,bool allowrational,int exactquo) const {
    const tensor<T> & a=*this;
    quo.coord.clear();
    quo.dim=a.dim;
    r.dim=a.dim;
    r.coord.clear();
    if ( b.dim<=1 || b.coord.size()==1 || a.coord.empty() ){
      return a.TDivRem(b,quo,r,allowrational) && (exactquo?r.coord.empty():true) ;
    }
    /* alternative code
       std::vector< tensor<T> > R=Tcoeffs();
       std::vector< tensor<T> > B=b.Tcoeffs();
       tensor<T> Q;
       int rs=R.size(),bs=B.size(),qs=rs-bs;
       if (rs<bs){
       r=a;
       return true;
       }
       for (int i=0;i<=qs;++i){
       if (!R[i].Texactquotient(B[0],Q,allowrational))
       return false;
       for (int j=i+1;j<i+bs;++j){
       if (!B[j-i].coord.empty())
       R[j]=R[j]-Q*B[j-i];
       }
       Q=Q.untrunc1(qs-i);
       quo.append(Q);
       }
       // convert R[qs+1..rs] to r
       for (int i=qs+1;i<rs;++i){
       R[i]=R[i].untrunc1(rs-i-1);
       r.append(R[i]);
       }
       return true;
    */
    // old code
    typename std::vector< monomial<T> >::const_iterator it=b.coord.begin(),itend=b.coord.end();
    r=a;
    if (b.coord.empty()){
      if (exactquo)
	return r.coord.empty();
      return true;
    }
    int bdeg=it->index.front(),rdeg=r.lexsorted_degree(),qdeg=rdeg-bdeg; // int bval=(itend-1)->index.front();
    tensor<T> b0(Tnextcoeff<T>(it,b.coord.end()));
    tensor<T> q(b0.dim);
    if (it==b.coord.end()){ // bn==b0, b=b0*main var to a power
      it=r.coord.begin();
      itend=r.coord.end();
      while ( it!=itend){
	rdeg=it->index.front();
	if (rdeg<bdeg)
	  break;
	tensor<T> a0(Tnextcoeff<T>(it,itend)),tmp(a0.dim);
	if (!a0.Texactquotient(b0,q,allowrational))
	  return false;
	q=q.untrunc1(rdeg-bdeg);
	quo.append(q);
      }
      r.coord=std::vector< monomial<T> >(it,itend);
      if (exactquo)
	return it==itend;
      return true;
    }
    tensor<T> q1,b1(b0.dim); // subprincipal coeff
    if (it->index.front()==bdeg-1)
      b1=Tnextcoeff<T>(it,b.coord.end()); 
    // if (exactquo) bn=Tlastcoeff<T>(it,itend);
    for ( ;(rdeg=r.lexsorted_degree()) >=bdeg;){
      it=r.coord.begin();
      itend=r.coord.end();
      // compute 2 terms of the quotient in one iteration,
      // this will save one long substraction
      tensor<T> a0(Tnextcoeff<T>(it,itend)),a1(a0.dim);
      if (exactquo && it==itend)
	return false;
      if (!a0.Texactquotient(b0,q,allowrational))
	return false;
      qdeg=rdeg-bdeg;
      if (qdeg){
	if (it!=itend && it->index.front()==rdeg-1)
	  a1=Tnextcoeff<T>(it,itend);
#ifdef GIAC_VECTOR // fix for * on arm compiler
	tensor<T> tmp(q.dim);
	tmp.coord=q.coord*b1.coord;
	a1.TSub(tmp,a1);
#else
	a1.TSub(q*b1,a1); // a1=a1-q*b1; 
#endif
	q=q.untrunc1(qdeg);
	if (!a1.Texactquotient(b0,q1,allowrational))
	  return false;
	q.TAdd(q1.untrunc1(qdeg-1),q); // q=q+q1.untrunc1(qdeg-1);
      }
      else
	q=q.untrunc1(qdeg);	
      /*
      if (exactquo){
	tensor<T> an(Tlastcoeff<T>(it,itend));
	int rval=(itend-1)->index.front();
	if (rval-bval<rdeg-bdeg){
	  if (rval<bval+count) // we must gain one valuation per loop
	    return false;
	  if (!an.Texactquotient(bn,qn,allowrational))
	    return false;
	  q=q+qn.untrunc1(rval-bval);
	  ++count;
	}
      }
      */
      quo.TAdd(q,quo); // quo=quo+q;
#ifdef GIAC_VECTOR
      tensor<T> tmp(q.dim);
      tmp.coord=q.coord*b.coord;
      r.TSub(tmp,r); // r=r-q*b;       
#else
      r.TSub(q*b,r); // r=r-q*b; 
#endif
      if (r.coord.empty())
	return true;
    }
    if (exactquo) return r.coord.empty();
    return true;
  }


  // hashing seems slower than DivRem1 if computation should be done with int
  template <class T>
  bool tensor<T>::TDivRemHash(const tensor<T> & b,tensor<T> & quo,tensor<T> & r,bool allowrational,int exactquo,double qmax) const {
    const tensor<T> & a=*this;
    quo.coord.clear();
    quo.dim=a.dim;
    r.dim=a.dim;
    r.coord.clear();
    int bs=b.coord.size();
    if ( b.dim<=1 || bs==1 || a.coord.empty() ){
      return a.TDivRem(b,quo,r,allowrational) && (exactquo?r.coord.empty():true) ;
    }
    int bdeg=b.coord.front().index.front(),rdeg=lexsorted_degree(),ddeg=rdeg-bdeg;
    if (ddeg>2 && bs>10){
      index_t d1=degree(),d2=b.degree(),d3=b.coord.front().index.iref(),d(dim);
      // i-th degrees of th / other in quotient and remainder
      // are <= i-th degree of th + ddeg*(i-th degree of other - i-th degree of lcoeff of other) 
      double ans=1;
      for (int i=0;i<dim;++i){
	d[i]=d1[i]+(ddeg+1)*(d2[i]-d3[i])+1;
	int j=1;
	// round to newt power of 2
	for (;;j++){
	  if (!(d[i] >>= 1))
	    break;
	}
	d[i] = 1 << j;
	ans = ans*unsigned(d[i]);
	if (ans/RAND_MAX>RAND_MAX)
	  break;
      }
      if (ans<RAND_MAX){
	std::vector< T_unsigned<T,unsigned> > p1,p2,quot,remain;
	std::vector<unsigned> vars(dim);
	vars[dim-1]=1;
	for (int i=dim-2;i>=0;--i){
	  vars[i]=d[i+1]*vars[i+1];
	}
	convert(*this,d,p1);
	convert(b,d,p2);
	if (hashdivrem<T,unsigned>(p1,p2,quot,remain,vars,/* reduce */0,qmax,allowrational)){
	  convert(quot,d,quo);
	  convert(remain,d,r);
	  return true;
	}
	else
	  ans=1e200; // don't make another unsuccessfull division!
      }
      if (ans/RAND_MAX<RAND_MAX){
	std::vector< T_unsigned<T,ulonglong> > p1,p2,quot,remain;
	std::vector<ulonglong> vars(dim);
	vars[dim-1]=1;
	for (int i=dim-2;i>=0;--i){
	  vars[i]=d[i+1]*vars[i+1];
	}
	convert(*this,d,p1);
	convert(b,d,p2);
	if (hashdivrem<T,ulonglong>(p1,p2,quot,remain,vars,/* reduce */0,qmax,allowrational)){
	  convert(quot,d,quo);
	  convert(remain,d,r);
	  return true;
	}
      }
    }
    return TDivRem1(b,quo,r,allowrational,exactquo);
  }


  template<class T>
  std::vector< tensor<T> > tensor<T>::Tcoeffs() const{
    int current_deg=lexsorted_degree();
    std::vector< tensor<T> > v;
    typename std::vector< monomial<T> >::const_iterator it=coord.begin(),itend=coord.end();
    for (;it!=itend;--current_deg){
      if (it->index.front()==current_deg){
	v.push_back(Tnextcoeff<T>(it,itend));
      }
      else
	v.push_back(tensor<T>(dim-1));
    }
    for (;current_deg>=0;--current_deg) v.push_back(tensor<T>(dim-1));
    return v;
  }

  template <class T>
  bool tensor<T>::Texactquotient(const tensor<T> & b,tensor<T> & quo,bool allowrational) const {
    if (coord.empty()){
      quo.dim=dim; quo.coord.clear();
      return true;
    }
    if (*this==b){
      quo=tensor<T>(T(1),dim);
      return true;
    }
    if (dim>1 && !allowrational && lexsorted_degree()==b.lexsorted_degree()){
      if (!Tfirstcoeff(*this).trunc1().Texactquotient(Tfirstcoeff(b).trunc1(),quo,allowrational))
	return false;
      quo=quo.untrunc1();
      if (is_one(quo))
	return false; // already tested above
      return *this==quo*b;
    }
    tensor<T> r(b.dim);
    return this->TDivRem1(b,quo,r,allowrational,1);
  }

  template <class T>
  bool tensor<T>::TDivRem (const tensor<T> & other, tensor<T> & quo, tensor<T> & rem, bool allowrational ) const {  
    int asize=(*this).coord.size();
    if (!asize){
      quo=*this;
      rem=*this; 
      return true;
    }
    int bsize=other.coord.size();
    if (!bsize){
      quo.dim=dim; quo.coord.clear();
      rem=*this;
      return true;
    }
    index_m a_max = (*this).coord.front().index;
    index_m b_max = other.coord.front().index;
    quo.coord.clear();
    quo.dim=this->dim;
    rem.dim=this->dim;
    if (bsize==1){
      rem.coord.clear();
      T b=other.coord.front().value;
      if (b_max==b_max*0){
	if (is_one(b))
	  quo = *this ;
	else {
	  typename std::vector< monomial<T> >::const_iterator itend=coord.end();
	  for (typename std::vector< monomial<T> >::const_iterator it=coord.begin();it!=itend;++it){
	    T q=rdiv(it->value,b);
	    if (!allowrational && has_denominator(q))
	      return false;
	    quo.coord.push_back(monomial<T>(q,it->index)); 
	  }
	}
	return true;
      }
      typename std::vector< monomial<T> >::const_iterator itend=coord.end();
      typename std::vector< monomial<T> >::const_iterator it=coord.begin();
      for (;it!=itend;++it){
	if (!(it->index>=b_max))
	  break;
	T q=rdiv(it->value,b);
	if (!allowrational && has_denominator(q))
	  return false;
	quo.coord.push_back(monomial<T>(q,it->index-b_max)); 
      }
      rem.coord=std::vector< monomial<T> >(it,itend);
      return true;
    }
    rem=*this;
    if ( ! (a_max>=b_max) ){
      // test that the first power of a_max is < to that of b_max
      return (a_max.front()<b_max.front());
    }
    T b=other.coord.front().value;
    while (a_max >= b_max){
      // errors should be trapped here and false returned if error occured
      T q=rdiv(rem.coord.front().value,b);
      if (!allowrational){
	if ( has_denominator(q) || 
	     (!is_zero(q*b - rem.coord.front().value)) )
	  return false;
      }
      // end error trapping
      quo.coord.push_back(monomial<T>(q,a_max-b_max));
      const tensor<T> & temp=other.shift(a_max-b_max,q);
      rem.TSub(temp,rem); // rem = rem-temp;
      if (rem.coord.size())
	a_max=rem.coord.front().index;
      else
	break;
    }
    return(true);    
  }

  template <class T>
  bool tensor<T>::TPseudoDivRem (const tensor<T> & other, tensor<T> & quo, tensor<T> & rem, tensor<T> & a) const {
    int m=this->lexsorted_degree();
    int n=other.lexsorted_degree();
    a.coord.clear();
    a.coord.push_back(monomial<T>(T(1),a.dim));
    rem=*this;
    quo.coord.clear();
    if (m<n)
      return true;
    // a=Tpow(Tfirstcoeff(other),m-n+1);
    // return (a*(*this)).TDivRem1(other,quo,rem);
    index_m ishift(dim);
    tensor<T> b0(Tfirstcoeff(other));
    for (int i=m;i>=n;--i){
#ifdef GIAC_VECTOR
      a.coord *= b0.coord; // a.coord = a.coord*b0.coord; // a=a*b0;
      quo.coord *= b0.coord; // quo.coord = quo.coord*b0.coord; // quo=quo*b0;
#else
      a *= b0; // a=a*b0
      quo *= b0; // quo=quo*b0
#endif
      typename std::vector< monomial<T> >::const_iterator it=rem.coord.begin(),itend=rem.coord.end();
      if (it==itend || it->index.front()!=i){
#ifdef GIAC_VECTOR
	rem.coord *= b0.coord; // rem.coord = rem.coord*b0.coord; // rem=rem*b0;
#else
	rem *= b0; // rem=rem*b0;
#endif
	continue;
      }
      ishift.front()=i-n;
      const tensor<T> & rem0 = Tfirstcoeff(rem).shift(ishift);
      quo.append(rem0);
#ifdef GIAC_VECTOR
      rem.coord = rem.coord*b0.coord; 
      tensor<T> tmp(rem0.dim);
      tmp.coord=rem0.coord*other.coord;
      rem.TSub(tmp,rem); // rem=rem*b0-rem0*other;
#else
      rem=rem*b0-rem0*other;
#endif
    }
    return true;
  }

  template <class T>
  T tensor<T>::constant_term () const {  
    if (!((*this).coord.size()))
      return T(0);
    index_m i=(*this).coord.front().index*0;
    return (*this)(i);
  }

  template <class T>
  tensor<T> tensor<T>::shift (const index_m & i,const T & fois) const {
    tensor<T> res(dim,*this);
    res.coord.reserve(coord.size());
    Shift(this->coord,i,fois,res.coord);
    return res;
  }

  template <class T>
  tensor<T> tensor<T>::shift (const T & fois,const index_m & i) const {
    tensor<T> res(dim,*this);
    res.coord.reserve(coord.size());
    Shift(this->coord,fois,i,res.coord);
    return res;
  }

  template <class T>
  tensor<T> tensor<T>::shift (const index_m & i) const {
    tensor<T> res(dim);
    res.coord.reserve(coord.size());
    Shift(this->coord,i,res.coord);
    return res;
  }

  template <class T>
  void tensor<T>::reverse() {
    typename std::vector< monomial<T> >::const_iterator itend=coord.end();
    for (typename std::vector< monomial<T> >::iterator it=coord.begin();it!=itend;++it)
      (*it).reverse();
    this->tsort();
  }

  template <class T>
  void tensor<T>::reorder(const std::vector<int> & permutation) {
    typename std::vector< monomial<T> >::const_iterator itend=coord.end();
    for (typename std::vector< monomial<T> >::iterator it=coord.begin();it!=itend;++it)
      it->reorder(permutation);
    this->tsort();
  }

  template <class T>
  T tensor<T>::norm() const{
    T res( 0);
    typename std::vector< monomial<T> >::const_iterator itend=coord.end();
    for (typename std::vector< monomial<T> >::const_iterator it=coord.begin();it!=itend;++it)
      res=max(res,it->norm());
    return res;
  }

  template <class T>
  T Tcontent(const tensor<T> & p){
    return Content(p.coord);
  }

  template<class T>
  T Tppz(tensor<T> & p,bool divide=true){
    T n=Tcontent(p);
    if (divide)
      p /= n;
    return T(n);
  }

  template<class T>
  bool Tis_one(const tensor<T> &p){
    if (p.coord.size()!=1)
      return false;
    if (!is_one(p.coord.front().value))
      return false;
    const index_m & i = p.coord.front().index;
    index_t::const_iterator it=i.begin(),itend=i.end();
    for (;it!=itend ;++it){
      if ((*it)!=0)
	return false;
    }
    return true;
  }

  template <class T>
  bool Tis_constant(const tensor<T> & p){
    if (p.coord.size()!=1)
      return false;
    const index_m & i = p.coord.front().index;
    index_t::const_iterator it=i.begin(),itend=i.end();
    for (;it!=itend ;++it){
      if ((*it)!=0)
	return false;
    }
    return true;  
  }

  template<class T>
  bool Tlistmax(const tensor<T> &p,T & n){
    n=T(  1);
    for (typename std::vector< monomial<T> >::const_iterator it=p.coord.begin();it!=p.coord.end() ;++it){
      if (!(it->value.is_cinteger()))
	return false;
      n=max(n,linfnorm(it->value));
    }
    return true;
  }

  template<class T>
  void Tapply(const tensor<T> & p,T (*f)(const T &),tensor<T> & pgcd){
    typename std::vector< monomial<T> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    Apply(it,itend,f,pgcd.coord);
  }

  template<class T>
  tensor<T> Tapply(const tensor<T> &p,T (*f)(const T &)){
    tensor<T> res(p.dim);
    Tapply(p,f,res);
    return res;
  }

  template<class T>
  void Tlgcd(const tensor<T> & p,tensor<T> & pgcd){
    if (!p.dim){
      pgcd=p;
      return ;
    } 
    if (Tis_one(pgcd))
      return;
    pgcd=pgcd.trunc1();
    typename std::vector< monomial<T> >::const_iterator it=p.coord.begin();
    typename std::vector< monomial<T> >::const_iterator itend=p.coord.end();
    // typename std::vector< monomial<T> >::const_iterator itbegin=it;
    for (;it!=itend;){
      if (Tis_one(pgcd))
	break;
      pgcd=gcd(pgcd,Tnextcoeff<T>(it,itend));
    }
    if (pgcd.coord.empty()){
      index_m i;
      for (int j=0;j<p.dim;j++)
	i.push_back(0);
      pgcd.coord.push_back(monomial<T>(T(1),i));
      ++pgcd.dim;
    }
    else
      pgcd=pgcd.untrunc1();
  }

  template<class T>
  tensor<T> Tlgcd(const tensor<T> & p){
    if (p.dim==1){
      const T & c=Tcontent(p);
      return tensor<T>(c,1);
    }
    tensor<T> pgcd(p.dim); // pgcd=0
    Tlgcd(p,pgcd);
    return pgcd;
  }

  template<class T>
  void Tcommonlgcd(const tensor<T> & p,const tensor<T> & q,tensor<T> & pgcd){
    if (!p.dim){
      pgcd=p;
      return ;
    } 
    pgcd=pgcd.trunc1();
    typename std::vector< monomial<T> >::const_iterator it=p.coord.begin();
    typename std::vector< monomial<T> >::const_iterator itend=p.coord.end();
    typename std::vector< monomial<T> >::const_iterator jt=q.coord.begin();
    typename std::vector< monomial<T> >::const_iterator jtend=q.coord.end();
    for (;(it!=itend) && (jt!=jtend);){
      if (Tis_one(pgcd))
	break;
      pgcd=gcd(pgcd,Tnextcoeff<T>(it,itend));
      pgcd=gcd(pgcd,Tnextcoeff<T>(jt,jtend));
    }
    for (;(it!=itend);){
      if (Tis_one(pgcd))
	break;
      pgcd=gcd(pgcd,Tnextcoeff<T>(it,itend));
    }
    for (;(jt!=jtend);){
      if (Tis_one(pgcd))
	break;
      pgcd=gcd(pgcd,Tnextcoeff<T>(jt,jtend));
    }
    if (pgcd.coord.empty()){
      index_m i;
      for (int j=0;j<p.dim;j++)
	i.push_back(0);
      pgcd.coord.push_back(monomial<T>(T(1),i));
    }
    else
      pgcd=pgcd.untrunc1();
  }

  // collect all terms with same 1st Tpower as the 1st power of p
  template<class T>
  tensor<T> Tcarcomp(const tensor<T> & p){
    typename std::vector< monomial<T> >::const_iterator it=p.coord.begin();
    int n=it->index.front();
    tensor<T> res(p.dim);
    for (;n==it->index.front();++it)
      res.coord.push_back(monomial<T>(it->value,it->index));
    return res;
  }

  template<class T>
  tensor<T> Tfirstcoeff(const tensor<T> & p){
    typename std::vector< monomial<T> >::const_iterator it=p.coord.begin();
    typename std::vector< monomial<T> >::const_iterator itend=p.coord.end();
    int n=it->index.front();
    tensor<T> res(p.dim);
    for (;(it!=itend) && (n==it->index.front());++it)
      res.coord.push_back(monomial<T>(it->value,it->index.set_first_zero()));
    return res;
  }

  template<class T>
  void Tswap(tensor<T> * & a, tensor<T> * & b){
    tensor<T> * c =  a;
    a = b ;
    b = c ;
  }

  // polynomial subresultant: compute content and primitive part of the GCD
  // with respect to the other vars
  // gcddeg!=0 if we know the probable degree of the gcd
  template<class T>
  void Tcontentgcd(const tensor<T> &p, const tensor<T> & q, tensor<T> & cont,tensor<T> & prim,int gcddeg){
    if (p.coord.empty()){
      cont=Tlgcd(q);
      prim=q/Tlgcd(q);
      return ;
    }
    if (q.coord.empty()){
      cont=Tlgcd(p);
      prim=p/Tlgcd(p);
      return;
    }
    assert(p.dim==q.dim);
    // set auxiliary polynomials g and h to 1
    tensor<T> g(T(1),p.dim);
    tensor<T> h(g);
    // dp and dq are the "content" of p and q w.r.t. other variables
    tensor<T> dp(Tlgcd(p));
    tensor<T> dq(Tlgcd(q));
    cont=gcd(dp.trunc1(),dq.trunc1()).untrunc1();
    if (!p.dim){
      prim=tensor<T>(T(1),0);
      return ;
    }
    // std::cout << "Cont" << cont << std::endl; 
    tensor<T> a(p.dim),b(p.dim),quo(p.dim),r(p.dim),tmp(p.dim);
    // a and b are the primitive part of p and q
    p.TDivRem1(dp,a,r,true);
    q.TDivRem1(dq,b,r,true);
    while (!a.coord.empty()){
      int n=b.lexsorted_degree();
      int m=a.lexsorted_degree();
      if (!n) {// if b is constant (then b!=0), gcd=original Tlgcd
	prim=tensor<T>(T(1),p.dim);
	return ;
      }
      if (n==gcddeg){
	b.TDivRem1(Tlgcd(b),prim,r,true);
	if (a.Texactquotient(prim,r,true))
	  return;
      }
      int ddeg=m-n;
      if (ddeg<0){
#if defined RTOS_THREADX || defined BESTA_OS
    tensor<T> t(a); a=b; b=t;
#else
	swap(a,b); // exchange a<->b may occur only at the beginning
#endif
      }
      else {
	tensor<T> b0(Tfirstcoeff(b));
	a.TPseudoDivRem(b,quo,r,tmp);
	// (a*Tpow(b0,ddeg+1)).TDivRem1(b,quo,r); // division works always
	if (r.coord.empty())
	  break;
	// remainder is non 0, loop continue: a <- b
	a=b;
	tensor<T> temp(Tpow(h,ddeg));
	// now divides r by g*h^(m-n), result is the new b
	r.TDivRem1(g*temp,b,quo); // quo is the remainder here, not used
	// new g=b0 and new h=b0^(m-n)*h/temp
	if (ddeg==1) // the normal case, remainder deg. decreases by 1 each time
	  h=b0;
	else // not sure if it's better to keep temp or divide by h^(m-n+1)
	  (Tpow(b0,ddeg)*h).TDivRem1(temp,h,quo);
	g=b0;
      }
    }
    // std::cout << "Prim" << b << std::endl;
    b.TDivRem1(Tlgcd(b),prim,r,true);
  }

  template<class T>
  void Tsturm_seq(const giac::tensor<T> &p, giac::tensor<T> & cont,std::vector< tensor<T> > & sturm_seq){
    sturm_seq=std::vector< tensor<T> > (1,p);
    if (p.coord.empty()){
      cont=p;
      return ;
    }
    // set auxiliary polynomials g and h to 1
    tensor<T> g(T(1),p.dim);
    tensor<T> h(g);
    // dp and dq are the "content" of p and q w.r.t. other variables
    cont=Tlgcd(p);
    if (!p.dim)
      return ;
    // std::cout << "Cont" << cont << std::endl; 
    tensor<T> a(p.dim),b(p.dim),quo(p.dim),r(p.dim),tmp(p.dim);
    tensor<T> b0(g);
    std::vector< tensor<T> > sign_error(2,g);
    // a is the primitive part of p and q
    p.TDivRem1(cont,a,r);
    b=p.derivative();
    sturm_seq.push_back(b);
    for (int loop_counter=0;!a.coord.empty();++loop_counter){
      int m=a.lexsorted_degree();
      int n=b.lexsorted_degree();
      int ddeg=m-n; // should be 1 generically
      if (!n) {// if b is constant (then b!=0), gcd=original Tlgcd
	return ;
      }
      b0=Tfirstcoeff(b); 
      a.TPseudoDivRem(b,quo,r,tmp);
      // (a*Tpow(b0,ddeg+1)).TDivRem1(b,quo,r); // division works always
      if (r.coord.empty())
	break;
      // remainder is non 0, loop continue: a <- b
      a=b;
      tensor<T> temp(Tpow(h,ddeg));
      // now divides r by g*h^(m-n) and change sign, result is the new b
      r.TDivRem1(-g*temp,b,quo); // quo is the remainder here, not used
      // Now save b in the Sturm sequence *with sign adjustement*
      // Since we have -g*h^ddeg*r=b0^(ddeg+1)*a % previous b
      // instead of -r=a % previous b for correct sign
      // the sign error on b does not change the sign error on r
      // if ddeg is odd, the sign error on r is sign error on a*sign(g*h)
      // if ddeg is even, the signe error on r is sign error on a*sign(b0*g)
      // Note that if ddeg is odd and the previous ddeg was generic = 1
      // we do not change the sign error at all
      // Note that the sign errors propagate modulo 2 on the indices
      // of the Sturm sequence hence the vector of sign errors of length 2
      if ( ( (ddeg %2)!=0 ) && (h!=g) )
	sign_error[loop_counter %2] = sign_error[loop_counter %2] * h * g ;
      if ( ( (ddeg %2)==0) && (b0!=g) )
	sign_error[loop_counter %2] = sign_error[loop_counter % 2] * b0 * g;
      sturm_seq.push_back(b*sign_error[loop_counter %2]);
      // new g=b0 and new h=b0^(m-n)*h/temp
      if (ddeg==1) // the normal case, remainder deg. decreases by 1 each time
	h=b0;
      else // not sure if it's better to keep temp or divide by h^(m-n+1)
	(Tpow(b0,ddeg)*h).TDivRem1(temp,h,quo);
      g=b0;
    } // end while loop
  }

  // polynomial sub-resultant: return the gcd only
  template<class T>
  tensor<T> Tgcdpsr(const tensor<T> & p, const tensor<T> & q,int gcddeg=0){
    tensor<T> prim(p.dim),cont(p.dim);
    Tcontentgcd(p,q,prim,cont,gcddeg);
    // std::cout << "Prim" << prim << "Cont" << cont << std::endl;
    return prim*cont;
  }

  template<class T>
  tensor<T> Tresultant(const tensor<T> &p, const tensor<T> & q){
    assert(p.dim==q.dim);
    if (p.coord.empty())
      return p;
    if (q.coord.empty())
      return q;
    int m=p.lexsorted_degree();
    int n=q.lexsorted_degree();
    int sign=1;
    tensor<T> ptmp(p),qtmp(q);
    if (n > m) {
      if ( (n*m) % 2)
	sign=-1;
      int tmpint=n; n=m; m=tmpint;
#if defined RTOS_THREADX || defined BESTA_OS
      ptmp=q; qtmp=p; // swap(ptmp,qtmp);
#else
      swap(ptmp,qtmp);
#endif
    }
    // degree(qtmp)=n <= degree(ptmp)=m
    if (!n) // q is cst
      return pow(qtmp,m)*T(sign);
    int ddeg;
    T cp(ppz(ptmp)),cq(ppz(qtmp));
    T res(pow(cq,m)*pow(cp,n));
    tensor<T> g(T(1),p.dim), h(T(1),p.dim);
    while (n){
      if (m*n %2)
	sign=-sign;
      ddeg=m-n;
      tensor<T> tmp1(Tfirstcoeff(qtmp)),tmp2(p.dim),tmp3(pow(h,ddeg)),rem(p.dim);
      (ptmp*pow(tmp1,ddeg+1)).TDivRem1(qtmp,tmp2,rem,false);
      rem.high_order_degree_truncate(n);
      ptmp=qtmp;
      m=n;
      qtmp=rem/(g*tmp3);
      n=qtmp.lexsorted_degree();
      if (ddeg==1)
	h=tmp1;
      else
	h=(h*pow(tmp1,ddeg))/tmp3;
      g=tmp1;
    }
    // q is cst or zero
    if (qtmp.coord.empty())
      return tensor<T>(T(0),p.dim);
    m=ptmp.lexsorted_degree();
    return (pow(qtmp,m)/pow(h,m-1))*(res*T(sign));
  }

  // Bézout identity
  // given p and q, find u and v s.t. u*p+v*q=d where d=gcd(p,q) using PSR algo
  // Iterative algorithm to find u and d, then q=(d-u*p)/v
  template<class T>
  void Tegcdpsr(const tensor<T> &p1, const tensor<T> & p2, tensor<T> & u,tensor<T> & v,tensor<T> & d){
    assert(p1.dim==p2.dim);
    // set auxiliary polynomials g and h to 1
    tensor<T> g(T(1),p1.dim);
    tensor<T> h(g);
    tensor<T> a(p1.dim),b(p1.dim),q(p1.dim),r(p1.dim);
    const tensor<T> & cp1=Tlgcd(p1);
    const tensor<T> & cp2=Tlgcd(p2);
    bool Tswapped=false;
    if (p1.lexsorted_degree()<p2.lexsorted_degree())
      Tswapped=true;
    // initializes a and b to p1, p2
    const tensor<T> & pp1=Tis_one(cp1)?p1:p1/cp1;
    const tensor<T> & pp2=Tis_one(cp2)?p2:p2/cp2;
    if (Tswapped){
      a=pp2;
      b=pp1;
    }
    else {
      a=pp1;
      b=pp2;
    }
    // initializes ua to 1 and ub to 0, the coeff of u in ua*a+va*b=a
    tensor<T> ua(T(1),p1.dim), ub(p1.dim),ur(p1.dim);
    tensor<T> b0pow(p1.dim);
    // loop: ddeg <- deg(a)-deg(b), 
    // TDivRem: b0^(ddeg+1)*a = bq+r 
    // hence ur <- ua*b0^(ddeg+1)-q*ub verifies
    // ur*a+vr*b=r
    // a <- b, b <- r/(g*h^ddeg), ua <- ub and ub<- ur/(g*h^ddeg)
    // g <- b0, h <- b0^(m-n) * h / h^ddeg
    for (;;){
      int n=b.lexsorted_degree();
      int m=a.lexsorted_degree();
      if (!n){ // b is cst !=0 hence is the gcd, ub is valid
	break;
      }
      int ddeg=m-n;
      const tensor<T> & b0=Tfirstcoeff(b);
      // b0pow=Tpow(b0,ddeg+1);
      // (a*b0pow).TDivRem1(b,q,r); // division works always
      a.TPseudoDivRem(b,q,r,b0pow);
      // if r is 0 then b is the gcd and ub the coeff
      if (r.coord.empty())
	break;
      // std::cout << ua*b0pow << std::endl << q*ub << std::endl ;
      (ua*b0pow).TSub(q*ub,ur); // ur=ua*b0pow-q*ub;
      // std::cout << ur << std::endl;
#if defined RTOS_THREADX || defined BESTA_OS
      a=b; 
#else
      swap(a,b); // a=b
#endif
      const tensor<T> & temp=Tpow(h,ddeg);
      // now divides r by g*h^(m-n), result is the new b
      r.TDivRem1(g*temp,b,q); // q is not used anymore
#if defined RTOS_THREADX || defined BESTA_OS
      ua=ub;
#else
      swap(ua,ub); // ua=ub
#endif
      ur.TDivRem1(g*temp,ub,q);
      // std::cout << (b-ub*p1) << "/" << p2 << std::endl;
      // new g=b0 and new h=b0^(m-n)*h/temp
      if (ddeg==1) // the normal case, remainder deg. decreases by 1 each time
	h=b0;
      else // not sure if it's better to keep temp or divide by h^(m-n+1)
	(Tpow(b0,ddeg)*h).TDivRem1(temp,h,q);
      g=b0;
    }
    // ub is valid and b is the gcd, vb=(b-ub*p1)/p2 if not Tswapped
    // vb is stored in ua
    // std::cout << ub << std::endl;
    if (Tswapped){
      (b-ub*pp2).TDivRem1(pp1,ua,r);
      ua *= cp2; // ua=ua*cp2;
      ub *= cp1; // ub=ub*cp1;
      b *= cp1; b *= cp2; // b=b*cp1*cp2;
    }
    else {
      (b-ub*pp1).TDivRem1(pp2,ua,r);
      ua *= cp1; // ua=ua*cp1;
      ub *= cp2; // ub=ub*cp2;
      b *= cp1; b *= cp2; // b=b*cp1*cp2;
    }
    // final simplifications
    q.coord.clear();
    Tlgcd(b,q); // q=Tlgcd(b);
    Tlgcd(ua,q);
    Tlgcd(ub,q);
    b.TDivRem1(q,d,r,true);  // d=b/Tlgcd
    if (Tswapped){
      ub.TDivRem1(q,v,r,true); // v=ub/Tlgcd
      ua.TDivRem1(q,u,r,true); // u=ua/Tlgcd
    }
    else {
      ub.TDivRem1(q,u,r,true); // u=ub/Tlgcd
      ua.TDivRem1(q,v,r,true); // v=ua/Tlgcd
    }
  }

  // seems interesting in dimension 1 only
  template<class T>
  void TegcdTlgcd(const tensor<T> &p1, const tensor<T> & p2, tensor<T> & u,tensor<T> & v,tensor<T> & d){
    assert(p1.dim==p2.dim);
    // p1=cp1*a p2=cp2*b
    // a*u+b*v=d
    // hence multiplying by cp1*cp2 p1*(u*cp2)+p2*(v*cp1)=d*cp1*cp2
    tensor<T> cp1(Tlgcd(p1)), cp2(Tlgcd(p2)),pp1(p1/cp1),pp2(p2/cp2);
    tensor<T> a(p1.dim),b(p1.dim),q(p1.dim),r(p1.dim);
    bool Tswapped=false;
    if (p1.lexsorted_degree()<p2.lexsorted_degree())
      Tswapped=true;
    // initializes a and b to p1, p2
    if (Tswapped){
      a=pp2;
      b=pp1;
    }
    else {
      a=pp1;
      b=pp2;
    }
    // initializes ua to 1 and ub to 0, the coeff of u in ua*a+va*b=a
    tensor<T> ua(T(1),p1.dim), ub(p1.dim),ur(p1.dim);
    // loop: ddeg <- deg(a)-deg(b), 
    // TDivRem: b0^(ddeg+1)*a = bq+r 
    // hence ur <- ua*b0^(ddeg+1)-q*ub verifies
    // ur*a+vr*b=r
    // divide r and ur by their common Tlgcd
    // a <- b, b <- r/Tlgcd, ua <- ub and ub<- ur/Tlgcd
    for (;;){
      int n=b.lexsorted_degree();
      // int m=a.lexsorted_degree();
      if (!n){ // b is cst !=0 hence is the gcd, ub is valid
	break;
      }
      // int ddeg=m-n;
      tensor<T> b0(Tfirstcoeff(b)),b0pow(b0.dim);
      // b0pow=Tpow(b0,ddeg+1);
      // (a*b0pow).TDivRem1(b,q,r,true); // division works always
      a.TPseudoDivRem(b,q,r,b0pow);
      // if r is 0 then b is the gcd and ub the coeff
      if (r.coord.empty())
	break;
      ur=ua*b0pow-q*ub;
      a=b;
      tensor<T> pgcd(Tlgcd(ur));
      Tlgcd(r,pgcd);
      // now divides r by pgcd, result is the new b
      r.TDivRem1(pgcd,b,q,true); // q is not used anymore
      ua=ub;
      ur.TDivRem1(pgcd,ub,q,true);
    }
    // ub is valid and b is the gcd, vb=(b-ub*p1)/p2 if not Tswapped
    // vb is stored in ua
    // std::cout << ub << std::endl;
    if (Tswapped){
      q=b-ub*pp2;
      tensor<T> b0(Tfirstcoeff(pp1));
      int m=q.lexsorted_degree();
      int n=pp1.lexsorted_degree();
      tensor<T> b0pow(Tpow(b0,m-n+1));
      b=b*b0pow;
      ub=ub*b0pow;
      q=q*b0pow;
      q.TDivRem1(pp1,ua,r,true);
      ua=ua*cp2;
      ub=ub*cp1;
      b=b*cp1*cp2;
    }
    else {
      // first multiply b and ub by p1_max_deg^# before doing division
      q=b-ub*pp1;
      tensor<T> b0(Tfirstcoeff(pp2));
      int m=q.lexsorted_degree();
      int n=pp2.lexsorted_degree();
      tensor<T> b0pow(Tpow(b0,m-n+1));
      b=b*b0pow;
      ub=ub*b0pow;
      q=q*b0pow;
      q.TDivRem1(pp2,ua,r,true);
      ua=ua*cp1;
      ub=ub*cp2;
      b=b*cp1*cp2;
    }
    // final simplifications
    q=Tlgcd(ua);
    Tlgcd(ub,q);
    Tlgcd(b,q);
    b.TDivRem1(q,d,r,true);  // d=b/Tlgcd
    if (Tswapped){
      ua.TDivRem1(q,u,r,true); // u=ua/Tlgcd
      ub.TDivRem1(q,v,r,true); // v=ub/Tlgcd
    }
    else {
      ub.TDivRem1(q,u,r,true); // u=ub/Tlgcd
      ua.TDivRem1(q,v,r,true); // v=ua/Tlgcd
    }
  }

  // utility for Bézout identity solving
  template<class T>
  void Tegcdtoabcuv(const tensor<T> & a,const tensor<T> &b, const tensor<T> &c, tensor<T> &u,tensor<T> &v, tensor<T> & d, tensor<T> & C){
    tensor<T> d0(Tfirstcoeff(d));
    int m=c.lexsorted_degree();
    int n=d.lexsorted_degree();
    assert(m>=n); // degree of c must be greater than degree of d
    C=Tpow(d0,m-n+1);
    tensor<T> coverd(a.dim),temp(a.dim);
    (c*C).TDivRem1(d,coverd,temp);
    assert(temp.coord.empty()); // division of c by d must be exact
    // now multiply a*u+b*v=d by coverd -> a*u*coverd+b*v*coverd=c*d0pow
    u *= coverd; // u=u*coverd;
    v *= coverd; // v=v*coverd;
    m=u.lexsorted_degree();
    n=b.lexsorted_degree();
    if (m<n)
      return;
    // then reduces the degree of u, a*u+b*v=c*C
    d0=Tpow(Tfirstcoeff(b),m-n+1); 
    C *= d0; // C=C*d0;
    // now a*u*d0+b*v*d0=c*C
    (u*d0).TDivRem1(b,temp,u); // replace u*d0 -> temp*b+u
    // a*b + b*(a*temp+v*d0) = c*C
    v=a*temp+v*d0;
    return ;
  }

  // given a,b,c, find u, v and a cst C s.t. C*c=a*u+b*v and degree(u)<degree(b)
  // requires an egcd implementation, for example egcdpsr above
  template<class T>
  void Tabcuv(const tensor<T> & a,const tensor<T> &b, const tensor<T> &c, tensor<T> &u,tensor<T> &v, tensor<T> & C){
    tensor<T> d(a.dim);
    Tegcdpsr(a,b,u,v,d); // a*u+b*v=d
    Tegcdtoabcuv(a,b,c,u,v,d,C);
  }

  template<class T>
  tensor<T> tensor<T>::derivative() const{
    if (coord.empty())
      return *this;
    tensor<T> res(dim);
    if (dim==0)
      return res;
    res.coord.reserve(coord.size());
    typename std::vector< monomial<T> >::const_iterator itend=coord.end();
    T tmp;
    for (typename std::vector< monomial<T> >::const_iterator it=coord.begin();it!=itend;++it){
      index_t i= it->index.iref() ;
      T n(i.front());
      i[0]--;
      tmp = it->value*n;
      if (!is_zero(tmp))
	res.coord.push_back(monomial<T>(tmp,i));
    }
    return res;
  }

  template<class T>
  tensor<T> tensor<T>::integrate() const{
    if (coord.empty())
      return *this;
    tensor<T> res(dim);
    res.coord.reserve(coord.size());
    typename std::vector< monomial<T> >::const_iterator itend=coord.end();
    for (typename std::vector< monomial<T> >::const_iterator it=coord.begin();it!=itend;++it){
      index_t i = it->index.iref();
      T n(i.front()+1);
      i[0]++;
      if (!is_zero(n))
	res.coord.push_back(monomial<T>(rdiv(it->value,n),i));
    }
    return res;
  }

  template<class T>
  void Tfracadd(const tensor<T> & n1, const tensor<T> & d1,const tensor<T> & n2, const tensor<T> & d2, tensor<T> & num, tensor<T> & den){
    // std::cout << n1 << "/" << d1 << "+" << n2 << "/" << d2 << "=";
    if (Tis_one(d1)){
      n2.TAdd(n1*d2,num);  //  num=n1*d2+n2;
      den=d2;
      // std::cout << num << "/" << den << std::endl;
      return;
    }
    if (Tis_one(d2)){
      n1.TAdd(n2*d1,num); // num=n2*d1+n1;
      den=d1;
      // std::cout << num << "/" << den << std::endl;
      return;
    }
    // n1/d1+n2/d2 with g=gcd(d1,d2), d1=d1g*g, d2=d2g*g is
    // (n1*d2g+n2*d1g)/g * 1/(d1g*d2g)
    tensor<T> d1g(d1),d2g(d2);
    den=simplify(d1g,d2g);
    (n1*d2g).TAdd(n2*d1g,num);
    simplify(num,den);
    den=den*d1g*d2g;

    /* 
       (n1*d2).TAdd(n2*d1,num); //    num=n1*d2+n2*d1;
       den=d1*d2;
       simplify(num,den);
    */
  }

  template <class T>
  void Tfracmul(const tensor<T> & n1, const tensor<T> & d1,const tensor<T> & n2, const tensor<T> & d2, tensor<T> & num, tensor<T> & den){
    // std::cout << n1 << "/" << d1 << "*" << n2 << "/" << d2 << "=";
    if (Tis_one(d1)){
      num=n1;
      den=d2;
      simplify(num,den);
      num=num*n2;
      // std::cout << num << "/" << den << std::endl;
      return;
    }
    if (Tis_one(d2)){
      num=n2;
      den=d1;
      simplify(num,den);
      num=num*n1;
      // std::cout << num << "/" << den << std::endl;
      return;
    }
    num=n1;
    den=d2;
    simplify(num,den);
    tensor<T> ntemp(n2),dtemp(d1);
    simplify(ntemp,dtemp);
    num=num*ntemp;
    den=den*dtemp;
    // std::cout << num << "/" << den << std::endl;
  }

  /*
    template<class T>
    std::ostream & operator << (std::ostream & os, const std::vector< facteur<T> > & v){
    std::vector< facteur<T> >::const_iterator itend=v.end();
    for (std::vector< facteur<T> >::const_iterator it=v.begin();;){
    os << *it ;
    ++it;
    if (it==itend)
    break;
    else
    os << "*";
    }
    return(os);
    }
  */

  template<class T>
  T Tproduct(const std::vector< facteur<T> > & v){
    assert (!v.empty());
    typename std::vector< facteur<T> >::const_iterator it=v.begin();
    typename std::vector< facteur<T> >::const_iterator itend=v.end();
    T prod(Tpow(it->fact,it->mult));
    ++it;
    for (;it!=itend;++it)
      prod *= it->mult==1?it->fact:Tpow(it->fact,it->mult); //  prod=prod*Tpow(it->fact,it->mult);
    return prod;
  }

  template<class T>
  T Tproduct(typename std::vector< facteur<T> >::const_iterator it,
	     typename std::vector< facteur<T> >::const_iterator itend){
    assert (it!=itend);
    T prod(Tpow(it->fact,it->mult));
    ++it;
    for (;it!=itend;++it)
      prod *= it->mult==1?it->fact:Tpow(it->fact,it->mult); // prod=prod*Tpow(it->fact,it->mult);
    return prod;
  }

  // square-free factorization of a polynomial
  // T must be a 0 characteristic field
  template<class T>
  std::vector< facteur< tensor<T> > > Tsqff_char0(const tensor<T> &p ){
    tensor<T> y(p.derivative()),w(p);
    tensor<T> c(simplify(w,y));
    // p=p_1*p_2^2*...*p_n^n, c=gcd(p,p')=p_2*...*p_n^(n-1), 
    // w=p/c=pi_i p_i, y=p'/c=sum_{i>=1} ip_i'*pi_{j!=i} p_j
    y.TSub(w.derivative(),y); // y=y-w.derivative(); // y= sum_{i>=2} (i-1) p_i' * pi_{j!=i} p_j
    std::vector< facteur< tensor<T> > > v;
    int k=1; // multiplicity counter
    while(!y.coord.empty()){
      // y=sum_{i>=k+1} (i-k) p_i' * pi_{j!=i, j>=k} p_j
      const tensor<T> & g=simplify(w,y);
      if (!Tis_one(g))
	v.push_back(facteur< tensor<T> >(g,k));
      // this push p_k, now w=pi_{i>=k+1} p_i and 
      // y=sum_{i>=k+1} (i-k) p_i' * pi_{j!=i, j>=k+1} p_j
      y.TSub(w.derivative(),y); // y=y-w.derivative();
      // y=sum_{i>=k+1} (i-(k+1)) p_i' * pi_{j!=i, j>=k+1} p_j
      k++;
    }
    if (!Tis_one(w))
      v.push_back(facteur< tensor<T> >(w,k));
    return std::vector< facteur< tensor<T> > > (v);
  }

  // class for partial fraction decomposition
  template<class T>
  class pf{
  public:
    tensor<T> num;
    tensor<T> fact;
    tensor<T> den;
    int mult; // den=cste*fact^mult
    pf(): num(),fact(),den(),mult(0) {}
    pf(const pf & a) : num(a.num), fact(a.fact), den(a.den), mult(a.mult) {}
    pf(const tensor<T> &n, const tensor<T> & d, const tensor<T> & f,int m) : num(n), fact(f), den(d), mult(m) {};
  };

  template<class T>
  std::ostream & operator << (std::ostream & os, const pf<T> & v){
    os << v.num << "/" << v.den ;
    return os;
  }

  template<class T>
  std::ostream & operator << (std::ostream & os, const std::vector< pf<T> > & v){
    typename std::vector< pf<T> >::const_iterator itend=v.end();
    for (typename std::vector< pf<T> >::const_iterator it=v.begin();;){
      os << *it ;
      ++it;
      if (it==itend)
	break;
      else
	os << "+";
    }
    return(os);
  }

  template<class T>
  tensor<T> TsimplifybyTlgcd(tensor<T>& a,tensor<T> &b){
    const tensor<T> & Tlgcdg=gcd(Tlgcd(a),Tlgcd(b));
    if (!Tis_one(Tlgcdg)){
      a=a/Tlgcdg;
      b=b/Tlgcdg;
    }
    return Tlgcdg;
  }

  // utility for Tpartfrac, see below
  template<class T>
  void Tpartfrac(const tensor<T> & num, const tensor<T> & den, /* const tensor<T> & dendiff ,*/const std::vector< facteur< tensor<T> > > & w , int n, int m, std::vector < pf <T> > & pfdecomp ){
    if (m==n)
      return;
    if (m-n==1){
      tensor<T> nums(num), dens(den);
      TsimplifybyTlgcd(nums,dens);
      pfdecomp.push_back(pf<T>(nums,dens,w[n].fact,w[n].mult));    
      return ;
    }
    typename std::vector< facteur< tensor<T> > >::const_iterator it=w.begin()+n; // &w[n];
    typename std::vector< facteur< tensor<T> > >::const_iterator it_end=w.begin()+m; // &w[m];
    /*
    // check if all factors of degree 1 and mult 1
    for (;it!=itend;++it){
      if (it->mult!=1 || it->fact.lexsorted_degree()!=1)
	break;
    }
    if (it==itend){
      // add rem(num,it->fact)/rem(dendiff,it->fact) / it->fact for all factors
      it=w.begin()+n;
      for (;it!=itend;++it){
	
      }
    }
    */
    // split v in 2 parts, then apply recursively Tpartfrac on each part
    it=w.begin()+n;
    int p=(m+n)/2;
    typename std::vector< facteur< tensor<T> > >::const_iterator it_milieu=w.begin()+p; // &w[p];
    const tensor<T> & fn=Tproduct< tensor<T> >(it,it_milieu);
    const tensor<T> & fm=Tproduct< tensor<T> >(it_milieu,it_end);
    // write C*num=u*fn+v*fm
    tensor<T> C(den.dim),u(den.dim),v(den.dim);
    Tabcuv(fn,fm,num,u,v,C);
    C=C*(den/(fn*fm));
    // num/den= (u*fn+v*fm)/(C*fn*fm)=u/(C*fm)+v/(C*fn)
    Tpartfrac(v,C*fn,w,n,p,pfdecomp);
    Tpartfrac(u,C*fm,w,p,m,pfdecomp);
  }

  // if num/den with den=cste*pi_i d_i^{mult_i}
  // Tpartfrac rewrites num/den as
  // sum_{i} pfi.num/pfi.den with pfi.den = cste* pfi.den^pfi.mult
  // num and den are assumed to be prime together
  template<class T>
  void Tpartfrac(const tensor<T> & num, const tensor<T> & den, const std::vector< facteur< tensor<T> > > & v , std::vector < pf <T> > & pfdecomp, tensor<T> & ipnum, tensor<T> & ipden ){
    int n=v.size();
    pfdecomp.reserve(n);
    // compute ip and call Tpartfrac
    tensor<T> rem(num.dim);
    num.TPseudoDivRem(den,ipnum,rem,ipden);
    // ipden*num=den*ipnum+rem hence num/den=ipnum/ipden+rem/(ipden*den)
    const tensor<T> & temp=ipden*den;
    // simplify(rem,temp);
    if (n==1)
      pfdecomp.push_back(pf<T>(rem,temp,v.front().fact,v.front().mult));
    else
      Tpartfrac(rem,temp,/* temp.derivative() ,*/ v,0,n,pfdecomp);
  }

  // reduction of a fraction with multiple poles to single poles by integration
  // by part, use the relation
  // ilaplace(P'/P^(k+1))=laplacevar/k*ilaplace(1/P^k)
  template<class T>
  pf<T> Tlaplace_reduce_pf(const pf<T> & p_cst, tensor<T> & laplacevar ){
    pf<T> p(p_cst);
    assert(p.mult>0);
    if (p.mult==1)
      return p_cst;
    tensor<T> fprime=p.fact.derivative();
    tensor<T> d(fprime.dim),C(fprime.dim),u(fprime.dim),v(fprime.dim);
    Tegcdpsr(p.fact,fprime,u,v,d); // f*u+f'*v=d
    tensor<T> usave(u),vsave(v);
    // int initial_mult=p.mult-1;
    while (p.mult>1){
      Tegcdtoabcuv(p.fact,fprime,p.num,u,v,d,C);
      p.mult--;
      p.den=(p.den/p.fact)*C*T(p.mult);
      p.num=u*T(p.mult)+v.derivative()+v*laplacevar;
      if ( (p.mult % 5)==1) // simplify from time to time
	TsimplifybyTlgcd(p.num,p.den);
      if (p.mult==1)
	break;
      u=usave;
      v=vsave;
    }
    return pf<T>(p);
  }

  // reduction of a fraction with multiple poles to single poles by integration
  // by part, the integrated part is added to intdecomp
  template<class T>
  pf<T> Tintreduce_pf(const pf<T> & p_cst, std::vector< pf<T> > & intdecomp ){
    assert(p_cst.mult>0);
    if (p_cst.mult==1)
      return p_cst;
    pf<T> p(p_cst);
    tensor<T> fprime=p.fact.derivative();
    tensor<T> d(fprime.dim),u(fprime.dim),v(fprime.dim),C(fprime.dim);
    tensor<T> resnum(fprime.dim),resden(T(1),fprime.dim),numtemp(fprime.dim),dentemp(fprime.dim);
    Tegcdpsr(p.fact,fprime,u,v,d); // f*u+f'*v=d
    tensor<T> usave(u),vsave(v);
    int initial_mult=p.mult-1;
    while (p.mult>1){
      Tegcdtoabcuv(p.fact,fprime,p.num,u,v,d,C);
      p.mult--;
      p.den=(p.den/p.fact)*C*T(p.mult);
      p.num=u*T(p.mult)+v.derivative();
      u=-p.den;
      TsimplifybyTlgcd(u,v);
      Tfracadd<T>(resnum,resden,v,u,numtemp,dentemp);
      resnum=numtemp;
      resden=dentemp;
      if ( (p.mult % 5)==1) // simplify from time to time
	TsimplifybyTlgcd(p.num,p.den);
      if (p.mult==1)
	break;
      u=usave;
      v=vsave;
    }
    intdecomp.push_back(pf<T>(resnum,resden,p.fact,initial_mult));
    return pf<T>(p);
  }

  template<class T>
  std::vector<T> makevector(const T & a, const T &b){
    std::vector<T> v;
    v.push_back(a);
    v.push_back(b);
    return v;
  }

  template<class T>
  std::vector<T> makevector(const T & a, const T &b,const T & c){
    std::vector<T> v;
    v.push_back(a);
    v.push_back(b);
    v.push_back(c);
    return v;
  }

  template<class T>
  std::vector<T> makevector(const T & a, const T &b,const T & c,const T & d){
    std::vector<T> v;
    v.push_back(a);
    v.push_back(b);
    v.push_back(c);
    v.push_back(d);
    return v;
  }

  template<class T>
  std::vector<T> merge(const std::vector<T> & a, const std::vector<T> &b){
    std::vector<T> v(a);
    int as=a.size();
    int bs=b.size();
    v.reserve(as+bs);
    typename std::vector<T>::const_iterator it=b.begin(),itend=b.end();
    for (;it!=itend;++it)
      v.push_back(*it);
    return v;
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // ndef _GIAC_POLY_H

