// -*- mode:C++ ; compile-command: "g++ -I.. -g -c solve.cc" -*-
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
#ifndef _GIAC_SOLVE_H
#define _GIAC_SOLVE_H
#include "first.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  std::string print_intvar_counter(GIAC_CONTEXT);
  std::string print_realvar_counter(GIAC_CONTEXT);
  gen _reset_solve_counter(const gen & args,const context * contextptr);
  void set_merge(vecteur & v,const vecteur & w);

  gen equal2diff(const gen & g); // rewrite = as -
  vecteur protect_sort(const vecteur & res,GIAC_CONTEXT);
  vecteur find_singularities(const gen & e,const identificateur & x,int cplxmode,GIAC_CONTEXT);
  vecteur protect_find_singularities(const gen & e,const identificateur & x,int cplxmode,GIAC_CONTEXT);
  // isolate_mode & 1 is complex_mode, isolate_mode & 2 is 0 for principal sol
  vecteur solve(const gen & e,const identificateur & x,int isolate_mode,GIAC_CONTEXT);
  vecteur solve(const gen & e,const gen & x,int isolate_mode,GIAC_CONTEXT);
  vecteur solve(const vecteur & v,bool complex_mode,GIAC_CONTEXT); // v is a 1-d dense polynomial
  void solve(const gen & e,const identificateur & x,vecteur &v,int isolate_mode,GIAC_CONTEXT);
  void in_solve(const gen & e,const identificateur & x,vecteur &v,int isolate_mode,GIAC_CONTEXT);
  // modular roots, modulo p, p supposed to be prime
  // dogcd should be set to true except if you have already done gcd with x^p-x
  bool modpolyroot(const vecteur & a,const gen & p,vecteur & v,bool dogcd,GIAC_CONTEXT);

  gen solvepostprocess(const gen & g,const gen & x,GIAC_CONTEXT);
  // convert solutions to an expression
  gen _solve(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_solve ;
  gen in_fsolve(vecteur & v,GIAC_CONTEXT);
  gen _fsolve(const gen & args,GIAC_CONTEXT);
  // also sets iszero to -2 if endpoints have same sign, -1 if err or undef
  // 1 if zero found, 2 if sign reversal (no undef),
  // set iszero to 0 on entry if only one root
  // set to -1 or positive if you want many sign reversals 
  // -1 means no step specified, positive means nstep specified
  vecteur bisection_solver(const gen & equation,const gen & var,const gen & a0,const gen &b0,int & iszero,GIAC_CONTEXT);
  // FIXME: implement msolve without GSL 
  // gen msolve(const gen & f,const vecteur & vars,const vecteur & g,int method,double eps,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_fsolve ;
  vecteur sxa(const vecteur & sl,const vecteur & x,GIAC_CONTEXT);
  vecteur linsolve(const vecteur & sl,const vecteur & x,GIAC_CONTEXT);
  gen symb_linsolve(const gen & syst,const gen & vars);
  gen _linsolve(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_linsolve ;
  
  /*
  gen newtona(const gen & f, const gen & x, const gen & arg,int niter1, int niter2, double eps1,double eps2,double prefact1,double prefact2, int & b);
  gen newton(const gen & f, const gen & x,const gen & guess,int niter1=5,int niter2=50,double eps1=1e-3,double eps2=1e-12,double prefact1=0.5,double prefact2=1.0);
  */
  
  gen newton(const gen & f, const gen & x,const gen & guess,int niter,double eps1,double eps2,GIAC_CONTEXT);
  gen _newton(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_newton ;

  bool has_num_coeff(const vecteur & v);
  bool has_num_coeff(const polynome & p);
  bool has_num_coeff(const gen & e);
  bool has_mod_coeff(const vecteur & v,gen & modulo);
  bool has_mod_coeff(const polynome & p,gen & modulo);
  bool has_mod_coeff(const gen & e,gen & modulo);

  polynome spoly(const polynome & p,const polynome & q,environment * env);
  polynome reduce(const polynome & p,const polynome * it,const polynome * itend,environment * env);
  polynome reduce(const polynome & p,const vectpoly & v,environment * env);
  void reduce(vectpoly & res,environment * env);
  void change_monomial_order(polynome & p,const gen & order);
  vectpoly gbasis(const vectpoly & v,const gen & order=_TDEG_ORDER,bool with_cocoa=true,bool with_f5=false,environment * env=0);
  gen remove_equal(const gen & f);
  vecteur remove_equal(const_iterateur it,const_iterateur itend);
  vecteur gsolve(const vecteur & eq_orig,const vecteur & var,bool complexmode,GIAC_CONTEXT);
  bool vecteur2vector_polynome(const vecteur & eq_in,const vecteur & l,vectpoly & eqp);

  gen _greduce(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_greduce ;

  gen _gbasis(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_gbasis ;
  gen _in_ideal(const gen & args,GIAC_CONTEXT);

  double nan();
  gen remove_and(const gen & g,const unary_function_ptr * u);
  vecteur solvepreprocess(const gen & args,bool complex_mode,GIAC_CONTEXT);

  bool is_idnt_function38(const gen & g);
  vecteur lidnt_solve(const gen & g);
  vecteur lidnt_function38(const gen & g);
  // Find zero or extrema of equation for variable near guess in real mode
  // For polynomial input, returns all zeros or extrema
  // type=0 for zeros, =1 for extrema
  // returns 0 if zero(s) were found, 1 if extrema found, 2 if sign reversal found
  vecteur solve_zero_extremum(const gen & equation,const gen & variable,const gen & guess,int & type,GIAC_CONTEXT);
  vecteur solve_zero_extremum(const gen & equation0,const gen & variable,const gen & guess,double xmin, double xmax,int & type,GIAC_CONTEXT);
  // returns 0 for 0 solution, 1 for 1 solution, 2 for infinity solution
  // -1 on error
  int aspen_linsolve(const matrice & m,GIAC_CONTEXT);
  // returns 0 for 0 solution, 1 for 1 solution, 2 for infinity solution
  // -1 on error
  int aspen_linsolve_2x2(const gen & a,const gen &b,const gen &c,
			 const gen &d,const gen & e,const gen & f,GIAC_CONTEXT);
  // returns 0 for 0 solution, 1 for 1 solution, 2 for infinity solution
  // -1 on error
  int aspen_linsolve_3x3(const gen & a,const gen &b,const gen &c,const gen &d,
			 const gen & e,const gen &f,const gen & g,const gen &h,
			 const gen & i,const gen & j,const gen &k,const gen &l,GIAC_CONTEXT);

  // minimization of f under constraints using cobyla algorithm
  // returns an error or the vecteur of coordinates of variables
  // and sets min_value to f at this point
  gen fmin_cobyla(const gen & f,const vecteur & constraints,const vecteur & variables,const vecteur & guess,const gen & eps0,const gen & maxiter0,GIAC_CONTEXT);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_SOLVE_H

#if !defined(GIAC_HAS_STO_38) && !defined(ConnectivityKit)
/* cobyla : contrained optimization by linear approximation */

/*
 * Copyright (c) 1992, Michael J. D. Powell (M.J.D.Powell@damtp.cam.ac.uk)
 * Copyright (c) 2004, Jean-Sebastien Roy (js@jeannot.org)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * This software is a C version of COBYLA2, a contrained optimization by linear
 * approximation package developed by Michael J. D. Powell in Fortran.
 * 
 * The original source code can be found at :
 * http://plato.la.asu.edu/topics/problems/nlores.html
 */

/* $Jeannot: cobyla.h,v 1.10 2004/04/18 09:51:37 js Exp $ */

#ifndef _COBYLA_
#define _COBYLA_

/*
 * Verbosity level
 */
typedef enum {
  COBYLA_MSG_NONE = 0, /* No messages */
  COBYLA_MSG_EXIT = 1, /* Exit reasons */
  COBYLA_MSG_ITER = 2, /* Rho and Sigma changes */
  COBYLA_MSG_INFO = 3, /* Informational messages */
} cobyla_message;

/*
 * Possible return values for cobyla
 */
typedef enum
{
  COBYLA_MINRC     = -2, /* Constant to add to get the rc_string */
  COBYLA_EINVAL    = -2, /* N<0 or M<0 */
  COBYLA_ENOMEM    = -1, /* Memory allocation failed */
  COBYLA_NORMAL    =  0, /* Normal return from cobyla */
  COBYLA_MAXFUN    =  1, /* Maximum number of function evaluations reach */
  COBYLA_ROUNDING  =  2, /* Rounding errors are becoming damaging */
  COBYLA_USERABORT =  3  /* User requested end of minimization */
} cobyla_rc;

/*
 * Return code strings
 * use cobyla_rc_string[rc - COBYLA_MINRC] to get the message associated with
 * return code rc.
 */
extern const char *cobyla_rc_string[6];

/*
 * A function as required by cobyla
 * state is a void pointer provided to the function at each call
 *
 * n     : the number of variables
 * m     : the number of constraints
 * x     : on input, then vector of variables (should not be modified)
 * f     : on output, the value of the function
 * con   : on output, the value of the constraints (vector of size m)
 * state : on input, the value of the state variable as provided to cobyla
 *
 * COBYLA will try to make all the values of the constraints positive.
 * So if you want to input a constraint j such as x[i] <= MAX, set:
 *   con[j] = MAX - x[i]
 * The function must returns 0 if no error occurs or 1 to immediately end the
 * minimization.
 *
 */
typedef int cobyla_function(int n, int m, double *x, double *f, double *con,
  void *state);

/*
 * cobyla : minimize a function subject to constraints
 *
 * n         : number of variables (>=0)
 * m         : number of constraints (>=0)
 * x         : on input, initial estimate ; on output, the solution
 * rhobeg    : a reasonable initial change to the variables
 * rhoend    : the required accuracy for the variables
 * message   : see the cobyla_message enum
 * maxfun    : on input, the maximum number of function evaluations
 *             on output, the number of function evaluations done
 * calcfc    : the function to minimize (see cobyla_function)
 * state     : used by function (see cobyla_function)
 *
 * The cobyla function returns a code defined in the cobyla_rc enum.
 *
 */
extern int cobyla(int n, int m, double *x, double rhobeg, double rhoend,
  int message, int *maxfun, cobyla_function *calcfc, void *state);


#endif /* _COBYLA_ */
#endif // GIAC_HAS_STO_38
