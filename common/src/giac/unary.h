// -*- mode:C++ ; compile-command: "g++ -I.. -g -c unary.cc" -*-
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
#ifndef _GIAC_UNARY_H
#define _GIAC_UNARY_H
#include "first.h"
#include "gen.h"
#include "index.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  typedef std::string ( * printfunction) (const gen &,const char * funcsommetname ,GIAC_CONTEXT ) ;
  typedef int ( * printfunc) (const gen & arg,const char * funcsommetname,std::string * appendstringptr,GIAC_CONTEXT ) ; // that should be the right way to print

  // print an operator between it's args
  std::string printsommetasoperator(const gen & feuille,const char * sommetstr,GIAC_CONTEXT);
  std::string texprintsommetasoperator(const gen & feuille,const char * sommetstr,GIAC_CONTEXT);


  // unary_function objects are functionnal objects
  // it's an abstract class with derived classes
  // unary_function_ptr is a class used to manage references to dynamically
  // allocated unary_function objects

  class partial_derivative;

  // declaration of taylor for taylor series expansion and of 0
  // Note that direction is always ignored for taylor, but might not 
  // for generic series_expansion
  // shift_coeff is used for semi-regular Taylor expansion, e.g.
  // it will be 1/2 for asin(x) near x=1
  gen taylor(const gen & lim_point,int order,const unary_function_ptr & D, int direction,gen & shift_coeff,GIAC_CONTEXT);
  typedef gen ( * taylortype) (const gen &,const int ,const unary_function_ptr & D,int direction,gen &,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_zero;
  gen apply(const gen & e,const unary_function_ptr & f,GIAC_CONTEXT);
  gen apply(const gen & e,const unary_function_ptr * f,GIAC_CONTEXT);
  gen apply(const gen & e, gen (* f) (const gen &) );
  gen apply(const gen & e, gen (* f) (const gen &,const context *),GIAC_CONTEXT );
  gen apply(const gen & e, const context * contextptr,const gen_op_context & f );
  gen apply(const gen & e1, const gen & e2,gen (* f) (const gen &, const gen &) );
  gen apply(const gen & e1, const gen & e2,const context * contextptr,gen (* f) (const gen &, const gen &,const context *) );
  gen apply1st(const gen & e1, const gen & e2,gen (* f) (const gen &, const gen &) );
  gen apply1st(const gen & e1, const gen & e2,const context * contextptr, gen (* f) (const gen &, const gen &,const context *) );
  gen apply2nd(const gen & e1, const gen & e2,gen (* f) (const gen &, const gen &) );
  gen apply2nd(const gen & e1, const gen & e2,const context * contextptr, gen (* f) (const gen &, const gen &,const context *) );

#ifdef NO_UNARY_FUNCTION_COMPOSE

  class unary_function_eval {
  public:
    const char * s;
    const partial_derivative * D; // By convention D==0 means there is no derivative
    taylortype series_expansion;
    // how to print: gen is the argument of the unary function, string should
    // normally be s
    printfunction printsommet;
    // how to print as a latex formula, 
    printfunction texprint;
    // how to print if translated to C++
    printfunction cprint;
    // the function to apply
    gen_op_context op;
    unsigned index_quoted_function; // bit 0= quoted, bit1-> index of function
    // members functions
    gen operator () (const gen & arg,const context * context_ptr) const { return op(arg,context_ptr); };    
    // constructor
    unary_function_eval(unsigned u,const gen_op_context & myop,const std::string & mys) : s(mys.c_str()),D(0),series_expansion(taylor),printsommet(0),texprint(0),cprint(0),op(myop),index_quoted_function(u) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const char * mys) : s(mys),D(0),series_expansion(taylor),printsommet(0),texprint(0),cprint(0),op(myop),index_quoted_function(u) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD,const std::string & mys) : s(mys.c_str()),D(myD),series_expansion(taylor),printsommet(0),texprint(0),cprint(0),op(myop),index_quoted_function(u) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD,const char * mys) : s(mys),D(myD),series_expansion(taylor),printsommet(0),texprint(0),cprint(0),op(myop),index_quoted_function(u) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD, taylortype mytaylor,const std::string & mys) : s(mys.c_str()),D(myD),series_expansion(mytaylor),printsommet(0),texprint(0),cprint(0),op(myop),index_quoted_function(u) { gen temp; mytaylor(0,-1,this,0,temp,0); };
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD, taylortype mytaylor,const char * mys) : s(mys),D(myD),series_expansion(mytaylor),printsommet(0),texprint(0),cprint(0),op(myop),index_quoted_function(u) { gen temp; mytaylor(0,-1,this,0,temp,0); };
    unary_function_eval(unsigned u,const gen_op_context & myop,const std::string & mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : s(mys.c_str()),D(0),series_expansion(taylor),printsommet(myprintsommet),texprint(mytexprint),cprint(mycprint),op(myop),index_quoted_function(u) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const char * mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : s(mys),D(0),series_expansion(taylor),printsommet(myprintsommet),texprint(mytexprint),cprint(mycprint),op(myop),index_quoted_function(u) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD, const std::string & mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : s(mys.c_str()),D(myD),series_expansion(taylor),printsommet(myprintsommet),texprint(mytexprint),cprint(mycprint),op(myop),index_quoted_function(u) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD, const char * mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : s(mys),D(myD),series_expansion(taylor),printsommet(myprintsommet),texprint(mytexprint),cprint(mycprint),op(myop),index_quoted_function(u) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD, taylortype mytaylor,const std::string & mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : s(mys.c_str()),D(myD),series_expansion(mytaylor),printsommet(myprintsommet),texprint(mytexprint),cprint(mycprint),op(myop),index_quoted_function(u) { gen temp; mytaylor(0,-1,this,0,temp,0); };
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD, taylortype mytaylor,const char * mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : s(mys),D(myD),series_expansion(mytaylor),printsommet(myprintsommet),texprint(mytexprint),cprint(mycprint),op(myop),index_quoted_function(u) { gen temp; mytaylor(0,-1,this,0,temp,0); };
    const char * print(GIAC_CONTEXT) const ;
    void dbgprint() const { std::cout << s << std::endl; };
  };
  std::ostream & operator << (std::ostream & os,const unary_function_eval & o);

  struct alias_unary_function_eval {
    const char * s;
    const unsigned long D; // By convention D==0 means there is no derivative
    taylortype series_expansion;
    // how to print: gen is the argument of the unary function, string should
    // normally be s
    printfunction printsommet;
    // how to print as a latex formula, 
    printfunction texprint;
    // how to print if translated to C++
    printfunction cprint;
    gen_op_context op;
    unsigned index_quoted_function; // bit 0= quoted, bit1-> index of function
  };
#define Defineunary_function_eval(name, u, func, pardev, str, print, index)  \
  static alias_unary_function_eval const unary##name= { str, pardev, NULL, print, NULL, NULL, func, index }

  class partial_derivative {
  public:
    const unary_function_ptr df;
    unary_function_ptr operator () (int i) const { return df ; }
    partial_derivative(const unary_function_ptr & mydf ) : df(mydf) {}
    partial_derivative(const unary_function_ptr * mydf ) : df(*mydf) {}
    partial_derivative(gen (* mydf) (const gen & args,const context * contextptr) ) ; 
  };

  typedef partial_derivative partial_derivative_onearg;

#else // NO_UNARY_FUNCTION_COMPOSE
  class unary_function_abstract {
  public:
    const char * s;
    const partial_derivative * D; // By convention D==0 means there is no derivative
    taylortype series_expansion;
    // how to print: gen is the argument of the unary function, string should
    // normally be s
    printfunction printsommet;
    // how to print as a latex formula, 
    printfunction texprint;
    // how to print if translated to C++
    printfunction cprint;
    unsigned index_quoted_function; // bit 0= quoted, bit1-> index of function
    // members functions
    virtual gen operator () (const gen & arg,const context * context_ptr) const { return 0;} ;
    virtual unary_function_abstract * recopie() const ;
    const char * print(GIAC_CONTEXT) const ;
    void dbgprint() const { std::cout << s << std::endl; };
    // constructors
    unary_function_abstract(unsigned u) : D(0),series_expansion(taylor),printsommet(0),texprint(0),cprint(0),index_quoted_function(u) {};
    unary_function_abstract(unsigned u,const std::string & mys): s(mys.c_str()),D(0),series_expansion(0),printsommet(0),texprint(0),cprint(0),index_quoted_function(u) {};
    unary_function_abstract(unsigned u,const char * mys): s(mys),D(0),series_expansion(0),printsommet(0),texprint(0),cprint(0),index_quoted_function(u) {};
    unary_function_abstract(unsigned u,const std::string & mys,const partial_derivative * myD): s(mys.c_str()),D(myD),series_expansion(taylor),printsommet(0),texprint(0),cprint(0),index_quoted_function(u) {};
    unary_function_abstract(unsigned u,const char * mys,const partial_derivative * myD): s(mys),D(myD),series_expansion(taylor),printsommet(0),texprint(0),cprint(0),index_quoted_function(u) {};
    unary_function_abstract(unsigned u,const std::string & mys,const partial_derivative * myD,printfunction myprintsommet, printfunction mytexprint,printfunction mycprint): s(mys.c_str()),D(myD),series_expansion(taylor),printsommet(myprintsommet),texprint(mytexprint),cprint(mycprint),index_quoted_function(u) {};
    unary_function_abstract(unsigned u,const char * mys,const partial_derivative * myD,printfunction myprintsommet, printfunction mytexprint,printfunction mycprint): s(mys),D(myD),series_expansion(taylor),printsommet(myprintsommet),texprint(mytexprint),cprint(mycprint),index_quoted_function(u) {};
    // if preprocessing is needed for f,mytaylor for ordre==-1 should 
    // push back in a global std::vector f and it's substitution
    unary_function_abstract(unsigned u,const std::string & mys,const partial_derivative * myD,taylortype mytaylor): s(mys.c_str()),D(myD),series_expansion(mytaylor),printsommet(0),texprint(0),cprint(0),index_quoted_function(u) { gen temp; mytaylor(0,-1,this,0,temp,0); };
    unary_function_abstract(unsigned u,const char * mys,const partial_derivative * myD,taylortype mytaylor): s(mys),D(myD),series_expansion(mytaylor),printsommet(0),texprint(0),cprint(0),index_quoted_function(u) { gen temp; mytaylor(0,-1,this,0,temp,0); };
    unary_function_abstract(unsigned u,const std::string & mys,const partial_derivative * myD,taylortype mytaylor,printfunction myprintsommet,printfunction mytexprint,printfunction mycprint): s(mys.c_str()),D(myD),series_expansion(mytaylor),printsommet(myprintsommet),texprint(mytexprint),cprint(mycprint),index_quoted_function(u) { gen temp; mytaylor(0,-1,this,0,temp,0);};
    unary_function_abstract(unsigned u,const char * mys,const partial_derivative * myD,taylortype mytaylor,printfunction myprintsommet,printfunction mytexprint,printfunction mycprint): s(mys),D(myD),series_expansion(mytaylor),printsommet(myprintsommet),texprint(mytexprint),cprint(mycprint),index_quoted_function(u) { gen temp; mytaylor(0,-1,this,0,temp,0);};
    unary_function_abstract(unsigned u,const std::string & mys,printfunction myprintsommet,printfunction mytexprint,printfunction mycprint): s(mys.c_str()),D(0),printsommet(myprintsommet),texprint(mytexprint),cprint(mycprint),index_quoted_function(u) {};
    unary_function_abstract(unsigned u,const char * mys,printfunction myprintsommet,printfunction mytexprint,printfunction mycprint): s(mys),D(0),printsommet(myprintsommet),texprint(mytexprint),cprint(mycprint),index_quoted_function(u) {};
    virtual ~unary_function_abstract() {};
  };

  std::ostream & operator << (std::ostream & os,const unary_function_abstract & o);

  class unary_function_unary : public unary_function_abstract {
  public:
    gen_op op;
    // members
    gen operator () (const gen & arg,const context * context_ptr) const { return op(arg); };    
    // constructor
    unary_function_unary(unsigned u,const gen_op & myop,const std::string & mys) : unary_function_abstract(u,mys),op(myop) {};
    unary_function_unary(unsigned u,const gen_op & myop,const char * mys) : unary_function_abstract(u,mys),op(myop) {};
    unary_function_unary(unsigned u,const gen_op & myop,const partial_derivative * myD,const std::string & mys) : unary_function_abstract(u,mys,myD),op(myop) {};
    unary_function_unary(unsigned u,const gen_op & myop,const partial_derivative * myD,const char * mys) : unary_function_abstract(u,mys,myD),op(myop) {};
    unary_function_unary(unsigned u,const gen_op & myop,const partial_derivative * myD, taylortype mytaylor,const std::string & mys) : unary_function_abstract(u,mys,myD,mytaylor),op(myop) {};
    unary_function_unary(unsigned u,const gen_op & myop,const partial_derivative * myD, taylortype mytaylor,const char * mys) : unary_function_abstract(u,mys,myD,mytaylor),op(myop) {};
    unary_function_unary(unsigned u,const gen_op & myop,const std::string & mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : unary_function_abstract(u,mys,myprintsommet,mytexprint,mycprint),op(myop) {};
    unary_function_unary(unsigned u,const gen_op & myop,const char * mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : unary_function_abstract(u,mys,myprintsommet,mytexprint,mycprint),op(myop) {};
    unary_function_unary(unsigned u,const gen_op & myop,const partial_derivative * myD, const std::string & mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : unary_function_abstract(u,mys,myD,myprintsommet,mytexprint,mycprint),op(myop) {};
    unary_function_unary(unsigned u,const gen_op & myop,const partial_derivative * myD, const char * mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : unary_function_abstract(u,mys,myD,myprintsommet,mytexprint,mycprint),op(myop) {};
    unary_function_unary(unsigned u,const gen_op & myop,const partial_derivative * myD, taylortype mytaylor,const std::string & mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : unary_function_abstract(u,mys,myD,mytaylor,myprintsommet,mytexprint,mycprint),op(myop) {};
    unary_function_unary(unsigned u,const gen_op & myop,const partial_derivative * myD, taylortype mytaylor,const char * mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : unary_function_abstract(u,mys,myD,mytaylor,myprintsommet,mytexprint,mycprint),op(myop) {};
    virtual unary_function_unary * recopie() const ;
  };

  std::ostream & operator << (std::ostream & os,const unary_function_unary & o);

  // like unary functions, but requiring evaluation inside
  class unary_function_eval : public unary_function_abstract {
  public:
    gen_op_context op;
    // members
    gen operator () (const gen & arg,const context * context_ptr) const { return op(arg,context_ptr); };    
    // constructor
    unary_function_eval(unsigned u,const gen_op_context & myop,const std::string & mys) : unary_function_abstract(u,mys),op(myop) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const char * mys) : unary_function_abstract(u,mys),op(myop) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD,const std::string & mys) : unary_function_abstract(u,mys,myD),op(myop) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD,const char * mys) : unary_function_abstract(u,mys,myD),op(myop) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD, taylortype mytaylor,const std::string & mys) : unary_function_abstract(u,mys,myD,mytaylor),op(myop) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD, taylortype mytaylor,const char * mys) : unary_function_abstract(u,mys,myD,mytaylor),op(myop) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const std::string & mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : unary_function_abstract(u,mys,myprintsommet,mytexprint,mycprint),op(myop) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const char * mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : unary_function_abstract(u,mys,myprintsommet,mytexprint,mycprint),op(myop) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD, const std::string & mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : unary_function_abstract(u,mys,myD,myprintsommet,mytexprint,mycprint),op(myop) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD, const char * mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : unary_function_abstract(u,mys,myD,myprintsommet,mytexprint,mycprint),op(myop) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD, taylortype mytaylor,const std::string & mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : unary_function_abstract(u,mys,myD,mytaylor,myprintsommet,mytexprint,mycprint),op(myop) {};
    unary_function_eval(unsigned u,const gen_op_context & myop,const partial_derivative * myD, taylortype mytaylor,const char * mys,printfunction myprintsommet,printfunction mytexprint=0,printfunction mycprint=0) : unary_function_abstract(u,mys,myD,mytaylor,myprintsommet,mytexprint,mycprint),op(myop) {};
    virtual unary_function_eval * recopie() const ;
  };

  // Aliases to construct unary_function_eval or unary_function_unary
  // at compile-time
  struct alias_unary_function_eval {
    const void * vtabptr; 
    const char * s;
    const partial_derivative * D; // By convention D==0 means there is no derivative
    taylortype series_expansion;
    // how to print: gen is the argument of the unary function, string should
    // normally be s
    printfunction printsommet;
    // how to print as a latex formula, 
    printfunction texprint;
    // how to print if translated to C++
    printfunction cprint;
    gen_op_context op;    
    unsigned index_quoted_function; // bit 0= quoted, bit1-> index of function
  };

  struct alias_unary_function_unary {
    void * vtabptr; 
    const char * s;
    const partial_derivative * D; // By convention D==0 means there is no derivative
    taylortype series_expansion;
    // how to print: gen is the argument of the unary function, string should
    // normally be s
    printfunction printsommet;
    // how to print as a latex formula, 
    printfunction texprint;
    // how to print if translated to C++
    printfunction cprint;
    gen_op_context op;    
    unsigned index_quoted_function; // bit 0= quoted, bit1-> index of function
  };

  // composition of functions
  class unary_function_compose : public unary_function_abstract {
  public:
    std::vector<unary_function_ptr> op_v;
    // redefinition of () to call each element of op_v instead of calling op
    gen operator () (const gen & arg,const context * context_ptr ) const ;
    // constructors
    unary_function_compose(unsigned u,const std::vector<unary_function_ptr> & v) ;
    virtual unary_function_compose * recopie() const ;
  };

  std::ostream & operator << (std::ostream & os,const unary_function_compose & p);

  // a function returning a list of unary_functions
  class unary_function_list : public unary_function_abstract {
  public:
    std::vector<unary_function_ptr> op_l;
    // redefinition of () to call each element of op_v and make a list
    gen operator () (const gen & arg ,const context * context_ptr) const ;
    // constructors
    unary_function_list(unsigned u,const std::vector<unary_function_ptr> & v) ;
    virtual unary_function_list * recopie() const ;
  };

  std::ostream & operator << (std::ostream & os,const unary_function_list & p);

  // we can now code for example x -> x + sin(x) as the composition of
  // the unary_function __plus and the unary_function_list (identity,sin)
  // but x -> 2*x + sin(x) requires coding constants function
  class unary_function_constant : public unary_function_abstract {
  public:
    gen constant ;
    // redefinition of () to return the constant
    gen operator () (const gen & arg,const context * context_ptr ) const { return constant; } ;
    // constructors
    unary_function_constant(unsigned u,const gen & e) : unary_function_abstract(u,* new std::string(e.print(0))),constant(e) {} ;
    virtual unary_function_constant * recopie() const ;
  };

  std::ostream & operator << (std::ostream & os,const unary_function_constant & c);

  class unary_function_innerprod : public unary_function_abstract {
  public:
    std::vector<int> i;
    // redefinition of () to remove indices at position in i from arg std::vector
    gen operator () (const gen & arg ,const context * context_ptr) const ;
    // constructors
    unary_function_innerprod(unsigned u,const std::vector<int> & myi) : unary_function_abstract(u,* new std::string("\\")+print_INT_(myi)),i(myi) {} ;
    virtual unary_function_innerprod * recopie() const ;
  };

  class unary_function_user : public unary_function_abstract {
  public:
    gen f ;
    // redefinition of () to return the constant
    gen operator () (const gen & arg ,const context * context_ptr) const { return f(arg,context_ptr); } ;
    // constructors
    unary_function_user(unsigned u,const gen & e,const std::string & s) : unary_function_abstract(u,s,&printsommetasoperator,&texprintsommetasoperator,0),f(e) {} ;
    unary_function_user(unsigned u,const gen & e,const std::string & s,printfunction myprintsommet, printfunction mytexprint,printfunction mycprint) : unary_function_abstract(u,s,myprintsommet,mytexprint,mycprint),f(e) {} ;
    virtual unary_function_user * recopie() const ;
  };

  std::ostream & operator << (std::ostream & os,const unary_function_innerprod & i);
  
  // d(i) returns the partial derivatives with respect to the i-th arg
  // df(x_1(t),...,x_n(t))/dt= Sigma(df/dx_i * dx_i/dt)
  // Example: for the sum +, df/dx_i=1 for all i
  // for the product *, df/dx_i is the product of all components except i-th
  // For a unary operator d(1) is the usual derivative
  class partial_derivative {
  public:
    virtual unary_function_ptr operator () (int i) const { return *at_zero; }
    virtual ~partial_derivative() { }
  };

  class partial_derivative_multiargs : public partial_derivative {
  public:
    unary_function_ptr (* d )(int i) ; 
    virtual unary_function_ptr operator () (int i) const { return d(i); }
    partial_derivative_multiargs(unary_function_ptr ( * myd) (int i)) : d(myd) {}
    virtual ~partial_derivative_multiargs() { }
  };

  class partial_derivative_onearg : public partial_derivative {
  public:
    const unary_function_ptr df;
    virtual unary_function_ptr operator () (int i) const { return df ; }
    partial_derivative_onearg(const unary_function_ptr & mydf ) : df(mydf) {}
    partial_derivative_onearg(const unary_function_ptr * mydf ) : df(*mydf) {}
#ifndef NO_UNARY_FUNCTION_COMPOSE
    partial_derivative_onearg(gen (* mydf) (const gen & args) ) ; 
#endif
    partial_derivative_onearg(gen (* mydf) (const gen & args,const context * contextptr) ) ; 
    virtual ~partial_derivative_onearg() {};
  };
#endif // NO_UNARY_FUNCTION_COMPOSE


#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_UNARY_H
