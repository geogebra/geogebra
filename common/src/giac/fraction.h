// -*- mode:C++ -*-

/* Class for fractions. The base class <T> must provide
   arithmetic operations +,-,*, pow for positive powers
   void simplify( T & a, T & b) to simplify a and b
   bool is_one(const T & a) returns true if a==1 
   T(1) returns the 1 polynomial */
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

#ifndef _GIAC_FRACTION_H_
#define _GIAC_FRACTION_H_
#include "first.h"
#include <iostream>

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

template <class T> class Tfraction {
public:
  T num;
  T den;
  Tfraction<T> (const T & n,const T & d) : num(n),den(d) {};
  Tfraction<T> (const T & n) : num(n),den(T(1)) {}; // does not work properly??
  Tfraction<T> (const Tfraction<T> & f) : num(f.num), den(f.den) {};
  Tfraction<T> normal() const;
  void dbgprint();
};

template <class T> class Tref_fraction {
public:
  int ref_count;
  Tfraction<T> f;
  Tref_fraction(const Tfraction<T> & F):ref_count(1),f(F) {}
};

template <class T> Tfraction<T> Tfraction<T>::normal() const {
  T n(num),d(den);
  simplify(n,d);
  if (is_minus_one(d)){
    n=-n;
    d=-d;
  }
  if (is_sq_minus_one(d)){
    n=-d*n;
    d=1;
  }
  return Tfraction<T>(n,d);
}

template <class T>  Tfraction<T> operator + (const Tfraction<T> & a,const Tfraction<T> & b){
  if (is_one(a.den))
    return(Tfraction<T> (a.num+b));
  if (is_one(b.den))
    return(Tfraction<T> (b.num+a));
  T da(a.den),db(b.den);
  T den=simplify3(da,db);
  T num=(a.num*db+b.num*da);
  if (is_zero(num))
    return Tfraction<T>(num,1);
  simplify3(num,den);
  den=den*da*db;
  return Tfraction<T> (num,den);
}

template <class T>  Tfraction<T> operator + (const Tfraction<T> & a,const T & b){
  return Tfraction<T>(a.num+a.den*b,a.den);
}

template <class T>  Tfraction<T> operator + (const T & a,const Tfraction<T> & b){
  return Tfraction<T>(a*b.den+b.num,b.den);
}

template <class T>  Tfraction<T> operator - (const Tfraction<T> & a,const Tfraction<T> & b){
  if (is_one(a.den))
    return(Tfraction<T> (a.num-b));
  if (is_one(b.den))
    return(Tfraction<T> (-b.num+a));
  T da(a.den),db(b.den);
  T den=simplify3(da,db);
  T num=(a.num*db-b.num*da);
  if (is_zero(num))
    return Tfraction<T>(num,1);
  simplify3(num,den);
  den=den*da*db;
  return Tfraction<T> (num,den);
}

template <class T>  Tfraction<T> operator - (const Tfraction<T> & a,const T & b){
  return Tfraction<T>(a.num-a.den*b,a.den);
}

template <class T>  Tfraction<T> operator - (const T & a,const Tfraction<T> & b){
  return Tfraction<T>(a*b.den-b.num,b.den);
}

template <class T>  Tfraction<T> operator * (const Tfraction<T> & a,const Tfraction<T> & b) {
  if (is_one(a.den))
    return(Tfraction<T> (a.num*b));
  if (is_one(b.den))
    return(Tfraction<T> (b.num*a));
  Tfraction<T> f1(a.num,b.den);
  simplify(f1.num,f1.den);
  Tfraction<T> f2(b.num,a.den);
  simplify(f2.num,f2.den);
  return Tfraction<T>(f1.num*f2.num,f1.den*f2.den);
}

template <class T>  Tfraction<T> operator * (const Tfraction<T> & a,const T & b){
  if (is_one(a.den))
    return Tfraction<T>(a.num*b,a.den);
  T nume(b),deno(a.den);
  simplify(nume,deno);
  return Tfraction<T>(a.num*nume,deno);
  /*
  Tfraction<T> f2(b,a.den);
  simplify(f2.num,f2.den);
  return Tfraction<T>(a.num*f2.num,f2.den);
  */
}

template <class T>  Tfraction<T> operator * (const T & a,const Tfraction<T> & b){
  if (is_one(b.den))
    return Tfraction<T>(a*b.num,b.den);
  T nume(a),deno(b.den);
  simplify(nume,deno);
  return Tfraction<T>(nume*b.num,deno);
  /*
  Tfraction<T> f1(a,b.den);
  simplify(f1.num,f1.den);
  return Tfraction<T>(f1.num*b.num,f1.den);
  */
}

template <class T>  Tfraction<T> operator / (const Tfraction<T> & a,const Tfraction<T> & b){
  if (is_one(a.den))
    return(Tfraction<T> (a.num/b));
  if (is_one(b.den))
    return(Tfraction<T> (b.num/a));
  Tfraction<T> f1(a.num,b.num);
  simplify(f1.num,f1.den);
  Tfraction<T> f2(b.den,a.den);
  simplify(f2.num,f2.den);
  return Tfraction<T>(f1.num*f2.num,f1.den*f2.den);
}

template <class T>  Tfraction<T> operator / (const Tfraction<T> & a,const T & b){
  Tfraction<T> f2(a.num,b);
  simplify(f2.num,f2.den);
  return Tfraction<T>(f2.num,f2.den*a.den);
}

template <class T>  Tfraction<T> operator / (const T & a,const Tfraction<T> & b){
  Tfraction<T> f2(a,b.num);
  simplify(f2.num,f2.den);
  return Tfraction<T>(f2.num*b.den,f2.den);
}

template <class T> Tfraction<T> pow (const Tfraction<T> & p,int n){
  if (!n)
    return Tfraction<T>(T(1),T(1));
  if (n>0){
    if (is_one(p.den))
      return Tfraction<T>(pow(p.num,n),p.den);
    else
      return Tfraction<T>(pow(p.num,n),pow(p.den,n));
  }
  return Tfraction<T>(pow(Tfraction<T>(p.den,p.num),-n));
}


template <class T> 
std::ostream & operator << (std::ostream & os, const Tfraction<T> & f ){
  os << f.num << "/" << f.den << " " ;
  return os;
}

template <class T> 
void Tfraction<T>::dbgprint() {
  std::cout << num << "/" << den << " " ;
}


  // factorization will be a std::vector of facteur, 
  // each facteur being a polynomial
  // and it's multiplicity
  template<class T>
  class facteur {
  public:
    T fact;
    int mult;
    facteur():fact(1),mult(0) {}
    facteur(const facteur & f) : fact(f.fact), mult(f.mult) {}
    facteur(const T & f, int m) : fact(f),mult(m) {}
    friend std::ostream & operator << (std::ostream & os, const facteur<T> & m ){
      return os << ":facteur:!" << m.fact << "!" << "^" << m.mult  ;
    }
    void dbgprint() const {
      std::cout << *this << std::endl;
    }
  };

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // ndef _GIAC_FRACTION_H_
