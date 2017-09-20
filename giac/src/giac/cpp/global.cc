/* -*- compile-command: "g++-3.4 -I.. -g -c global.cc  -DHAVE_CONFIG_H -DIN_GIAC" -*- */

#include "giacPCH.h"

/*  
 *  Copyright (C) 2000,14 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#ifdef HAVE_SSTREAM
#include <sstream>
#else
#include <strstream>
#endif
#include "global.h"
// #include <time.h>
#ifndef BESTA_OS
#include <signal.h>
#endif
#include <math.h>
#ifndef WINDOWS
#include <stdint.h>   // for uintptr_t
#endif
#ifdef HAVE_UNISTD_H
#include <unistd.h>
#endif
#ifdef HAVE_SYS_TYPES_H
#include <sys/types.h>
#endif
#ifdef HAVE_PWD_H
#include <pwd.h>
#endif
#include <string.h>
#include <stdexcept>
#include <algorithm>
#ifndef BESTA_OS
#include <cerrno>
#endif
#include "gen.h"
#include "identificateur.h"
#include "symbolic.h"
#include "sym2poly.h"
#include "plot.h"
#include "rpn.h"
#include "prog.h"
#include "usual.h"
#include "tex.h"
#include "path.h"
#include "input_lexer.h"
#include "giacintl.h"
#ifdef HAVE_LOCALE_H
#include <locale.h>
#endif
#ifdef _HAS_LIMITS
#include <limits>
#endif
#ifndef BESTA_OS
#ifdef WIN32
#ifndef VISUALC
#if !defined(GNUWINCE) && !defined(__MINGW_H)
#include <sys/cygwin.h>
#include <windows.h>
#endif // ndef gnuwince
#endif // ndef visualc
#endif // win32
#endif // ndef bestaos

#if defined VISUALC || defined BESTA_OS
#ifndef RTOS_THREADX
#ifndef BESTA_OS
#include <Windows.h>
#endif // besta_os
#endif // rtos_threadx
#endif // visualc || besta_os

#ifdef BESTA_OS
#include <stdlib.h>
#endif // besta_os

#include <stdio.h>
#include <stdarg.h>

#if defined(FIR)
extern "C" int firvsprintf(char*,const char*, va_list);
#endif

int my_sprintf(char * s, const char * format, ...){
    int z;
    va_list ap;
    va_start(ap,format);
#if defined(FIR) && !defined(FIR_LINUX)
    z = firvsprintf(s, format, ap);
#else
    z = vsprintf(s, format, ap);
#endif
    va_end(ap);
    return z;
}

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  void opaque_double_copy(void * source,void * target){
    *((double *) target) = * ((double *) source);
  }

  double opaque_double_val(const void * source){
    longlong r = * (longlong *)(source) ;
    (* (gen *) (&r)).type = 0;
    return * (double *)(&r); 
  }

  double min_proba_time=10; // in seconds

#ifdef TIMEOUT
#ifndef EMCC
  double time(int ){
    return double(CLOCK())/1000000; // CLOCKS_PER_SEC;
  }
#endif
  time_t caseval_begin,caseval_current;
  double caseval_maxtime=15; // max 15 seconds
  int caseval_n=0,caseval_mod=0,caseval_unitialized=-123454321;
  void control_c(){
#ifdef NSPIRE
    if (on_key_pressed()){ ctrl_c=true; interrupted=true; }
#else
    if (caseval_unitialized!=-123454321){
      caseval_unitialized=-123454321;
      caseval_mod=0;
      caseval_n=0;
      caseval_maxtime=15;
    }
    if (caseval_mod>0){ 
      ++caseval_n; 
      if (caseval_n >=caseval_mod){
	caseval_n=0; 
	caseval_current=time(0); 
#ifdef EMCC
	if (difftime(caseval_current,caseval_begin)>caseval_maxtime)
#else
	if (caseval_current>caseval_maxtime+caseval_begin)
#endif
	  { 
	    CERR << "Timeout" << endl; ctrl_c=true; interrupted=true; 
	    caseval_begin=caseval_current;
	  } 
      } 
    }
#endif // NSPIRE
  }
#endif // TIMEOUT

#ifdef NSPIRE_NEWLIB
  void usleep(int t){
  }
#endif

#if defined VISUALC || defined BESTA_OS
  int R_OK=4;
  int access(const char *path, int mode ){
    // return _access(path, mode );
    return 0;
  }
#ifdef RTOS_THREADX
extern "C" void Sleep(unsigned int miliSecond);
#endif

  void usleep(int t){
#ifdef RTOS_THREADX
    Sleep(t/1000);
#else
    Sleep(int(t/1000.+.5));
#endif
  }
#endif

#ifdef __APPLE__
  int PARENTHESIS_NWAIT=10;
#else
  int PARENTHESIS_NWAIT=100;
#endif

  // FIXME: threads allowed curently disabled
  // otherwise fermat_gcd_mod_2var crashes at puccini
  bool threads_allowed=true,mpzclass_allowed=true;
#ifdef HAVE_LIBPTHREAD
  pthread_mutex_t interactive_mutex = PTHREAD_MUTEX_INITIALIZER;
#endif

  std::vector<aide> * & vector_aide_ptr (){
    static std::vector<aide> * ans = new std::vector<aide>;
    return ans;
  }
  std::vector<std::string> * & vector_completions_ptr (){
    static std::vector<std::string> * ans = new  std::vector<std::string>;
    return ans;
  }
#ifdef NSPIRE_NEWLIB
  const context * context0=new context;
#else
  const context * context0=0;
#endif
  // Global variable when context is 0
  void (*fl_widget_delete_function)(void *) =0;
#ifndef NSPIRE
  ostream & (*fl_widget_archive_function)(ostream &,void *)=0;
  gen (*fl_widget_unarchive_function)(istream &)=0;
#endif
  gen (*fl_widget_updatepict_function)(const gen & g)=0;
  std::string (*fl_widget_texprint_function)(void * ptr)=0;

  static std::vector<const char *> & _last_evaled_function_name_(){
    static std::vector<const char *> * ans = new std::vector<const char *>;
    return *ans;
  }
  std::vector<const char *> & last_evaled_function_name(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_last_evaled_function_name_;
    else
      return _last_evaled_function_name_();
  }

  static vecteur & _last_evaled_arg_(){
    static vecteur * ans = new vecteur ;
    return *ans;
  }
  vecteur & last_evaled_arg(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_last_evaled_arg_;
    else
      return _last_evaled_arg_();
  }

  static int _language_=0; 
  int & language(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_language_;
    else
      return _language_;
  }
  void language(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_language_=b;
    else
      _language_=b;
  }

  static int _max_sum_sqrt_=3; 
  int & max_sum_sqrt(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_max_sum_sqrt_;
    else
      return _max_sum_sqrt_;
  }
  void max_sum_sqrt(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_max_sum_sqrt_=b;
    else
      _max_sum_sqrt_=b;
  }

  static int _max_sum_add_=100000; 
  int & max_sum_add(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_max_sum_add_;
    else
      return _max_sum_add_;
  }

  static int _default_color_=FL_BLACK;
  int & default_color(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_default_color_;
    else
      return _default_color_;
  }
  void default_color(int c,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr)
      contextptr->globalptr->_default_color_=c;
    else
      _default_color_=c;
  }

  static void * _evaled_table_=0;
  void * & evaled_table(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_evaled_table_;
    else
      return _evaled_table_;
  }

  static void * _extra_ptr_=0;
  void * & extra_ptr(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_extra_ptr_;
    else
      return _extra_ptr_;
  }

  static int _spread_Row_=0;
  int & spread_Row(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_spread_Row_;
    else
      return _spread_Row_;
  }
  void spread_Row(int c,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr)
      contextptr->globalptr->_spread_Row_=c;
    else
      _spread_Row_=c;
  }

  static int _spread_Col_=0;
  int & spread_Col(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_spread_Col_;
    else
      return _spread_Col_;
  }
  void spread_Col(int c,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr)
      contextptr->globalptr->_spread_Col_=c;
    else
      _spread_Col_=c;
  }

  static int _printcell_current_row_=0;
  int & printcell_current_row(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_printcell_current_row_;
    else
      return _printcell_current_row_;
  }
  void printcell_current_row(int c,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr)
      contextptr->globalptr->_printcell_current_row_=c;
    else
      _printcell_current_row_=c;
  }

  static int _printcell_current_col_=0;
  int & printcell_current_col(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_printcell_current_col_;
    else
      return _printcell_current_col_;
  }
  void printcell_current_col(int c,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr)
      contextptr->globalptr->_printcell_current_col_=c;
    else
      _printcell_current_col_=c;
  }

  static double _total_time_=0.0;
  double & total_time(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_total_time_;
    else
      return _total_time_;
  }

#if 1
  static double _epsilon_=1e-12;
#else
#ifdef __SGI_CPP_LIMITS
  static double _epsilon_=100*numeric_limits<double>::epsilon();
#else
  static double _epsilon_=1e-12;
#endif
#endif
  double & epsilon(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_epsilon_;
    else
      return _epsilon_;
  }
  void epsilon(double c,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr)
      contextptr->globalptr->_epsilon_=c;
    else
      _epsilon_=c;
  }

  static double _proba_epsilon_=1e-15;
  double & proba_epsilon(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_proba_epsilon_;
    else
      return _proba_epsilon_;
  }

  static bool _expand_re_im_=true; 
  bool & expand_re_im(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_expand_re_im_;
    else
      return _expand_re_im_;
  }
  void expand_re_im(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_expand_re_im_=b;
    else
      _expand_re_im_=b;
  }

  static int _scientific_format_=0; 
  int & scientific_format(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_scientific_format_;
    else
      return _scientific_format_;
  }
  void scientific_format(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_scientific_format_=b;
    else
      _scientific_format_=b;
  }

  static int _decimal_digits_=12; 

  int & decimal_digits(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_decimal_digits_;
    else
      return _decimal_digits_;
  }
  void decimal_digits(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_decimal_digits_=b;
    else
      _decimal_digits_=b;
  }

  static int _xcas_mode_=0; 
  int & xcas_mode(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_xcas_mode_;
    else
      return _xcas_mode_;
  }
  void xcas_mode(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_xcas_mode_=b;
    else
      _xcas_mode_=b;
  }


  static int _integer_format_=0; 
  int & integer_format(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_integer_format_;
    else
      return _integer_format_;
  }
  void integer_format(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_integer_format_=b;
    else
      _integer_format_=b;
  }
  static int _latex_format_=0; 
  int & latex_format(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_latex_format_;
    else
      return _latex_format_;
  }
#ifdef BCD
  static u32 _bcd_decpoint_='.'|('E'<<16)|(' '<<24); 
  u32 & bcd_decpoint(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_bcd_decpoint_;
    else
      return _bcd_decpoint_;
  }

  static u32 _bcd_mantissa_=12+(15<<8); 
  u32 & bcd_mantissa(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_bcd_mantissa_;
    else
      return _bcd_mantissa_;
  }

  static u32 _bcd_flags_=0; 
  u32 & bcd_flags(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_bcd_flags_;
    else
      return _bcd_flags_;
  }

  static bool _bcd_printdouble_=false;
  bool & bcd_printdouble(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_bcd_printdouble_;
    else
      return _bcd_printdouble_;
  }

#endif

  static bool _integer_mode_=true;
  bool & integer_mode(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_integer_mode_;
    else
      return _integer_mode_;
  }

  void integer_mode(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_integer_mode_=b;
    else
      _integer_mode_=b;
  }

  static bool _python_compat_=false;
  bool & python_compat(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_python_compat_;
    else
      return _python_compat_;
  }

  void python_compat(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_python_compat_=b;
    else
      _python_compat_=b;
  }

  static bool _complex_mode_=false; 
  bool & complex_mode(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_complex_mode_;
    else
      return _complex_mode_;
  }

  void complex_mode(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_complex_mode_=b;
    else
      _complex_mode_=b;
  }

  static bool _escape_real_=true; 
  bool & escape_real(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_escape_real_;
    else
      return _escape_real_;
  }

  void escape_real(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_escape_real_=b;
    else
      _escape_real_=b;
  }

  static bool _do_lnabs_=true;
  bool & do_lnabs(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_do_lnabs_;
    else
      return _do_lnabs_;
  }

  void do_lnabs(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_do_lnabs_=b;
    else
      _do_lnabs_=b;
  }

  static bool _eval_abs_=true;
  bool & eval_abs(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_eval_abs_;
    else
      return _eval_abs_;
  }

  void eval_abs(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_eval_abs_=b;
    else
      _eval_abs_=b;
  }

  static bool _eval_equaltosto_=true;
  bool & eval_equaltosto(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_eval_equaltosto_;
    else
      return _eval_equaltosto_;
  }

  void eval_equaltosto(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_eval_equaltosto_=b;
    else
      _eval_equaltosto_=b;
  }

  static bool _all_trig_sol_=false; 
  bool & all_trig_sol(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_all_trig_sol_;
    else
      return _all_trig_sol_;
  }

  void all_trig_sol(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_all_trig_sol_=b;
    else
      _all_trig_sol_=b;
  }

  static bool _try_parse_i_=true; 
  bool & try_parse_i(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_try_parse_i_;
    else
      return _try_parse_i_;
  }

  void try_parse_i(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_try_parse_i_=b;
    else
      _try_parse_i_=b;
  }

  static bool _specialtexprint_double_=false; 
  bool & specialtexprint_double(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_specialtexprint_double_;
    else
      return _specialtexprint_double_;
  }

  void specialtexprint_double(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_specialtexprint_double_=b;
    else
      _specialtexprint_double_=b;
  }

  static bool _atan_tan_no_floor_=false; 
  bool & atan_tan_no_floor(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_atan_tan_no_floor_;
    else
      return _atan_tan_no_floor_;
  }

  void atan_tan_no_floor(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_atan_tan_no_floor_=b;
    else
      _atan_tan_no_floor_=b;
  }

  static bool _keep_acosh_asinh_=false; 
  bool & keep_acosh_asinh(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_keep_acosh_asinh_;
    else
      return _keep_acosh_asinh_;
  }

  void keep_acosh_asinh(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_keep_acosh_asinh_=b;
    else
      _keep_acosh_asinh_=b;
  }

  static bool _keep_algext_=false; 
  bool & keep_algext(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_keep_algext_;
    else
      return _keep_algext_;
  }

  void keep_algext(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_keep_algext_=b;
    else
      _keep_algext_=b;
  }

  static bool _lexer_close_parenthesis_=true; 
  bool & lexer_close_parenthesis(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_lexer_close_parenthesis_;
    else
      return _lexer_close_parenthesis_;
  }

  void lexer_close_parenthesis(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_lexer_close_parenthesis_=b;
    else
      _lexer_close_parenthesis_=b;
  }

  static bool _rpn_mode_=false; 
  bool & rpn_mode(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_rpn_mode_;
    else
      return _rpn_mode_;
  }

  void rpn_mode(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_rpn_mode_=b;
    else
      _rpn_mode_=b;
  }

  static bool _ntl_on_=true; 
  bool & ntl_on(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_ntl_on_;
    else
      return _ntl_on_;
  }

  void ntl_on(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_ntl_on_=b;
    else
      _ntl_on_=b;
  }

  static bool _complex_variables_=false; 
  bool & complex_variables(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_complex_variables_;
    else
      return _complex_variables_;
  }

  void complex_variables(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_complex_variables_=b;
    else
      _complex_variables_=b;
  }

  static bool _increasing_power_=false;
  bool & increasing_power(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_increasing_power_;
    else
      return _increasing_power_;
  }

  void increasing_power(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_increasing_power_=b;
    else
      _increasing_power_=b;
  }

  static vecteur & _history_in_(){
    static vecteur * ans = new vecteur;
    return *ans;
  }
  vecteur & history_in(GIAC_CONTEXT){
    if (contextptr)
      return *contextptr->history_in_ptr;
    else
      return _history_in_();
  }

  static vecteur & _history_out_(){
    static vecteur * ans = new vecteur;
    return *ans;
  }
  vecteur & history_out(GIAC_CONTEXT){
    if (contextptr)
      return *contextptr->history_out_ptr;
    else
      return _history_out_();
  }

  static bool _approx_mode_=false;
  bool & approx_mode(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_approx_mode_;
    else
      return _approx_mode_;
  }

  void approx_mode(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_approx_mode_=b;
    else
      _approx_mode_=b;
  }

  static char _series_variable_name_='h';
  char & series_variable_name(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_series_variable_name_;
    else
      return _series_variable_name_;
  }

  void series_variable_name(char b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_series_variable_name_=b;
    else
      _series_variable_name_=b;
  }

  static unsigned short _series_default_order_=5;
  unsigned short & series_default_order(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_series_default_order_;
    else
      return _series_default_order_;
  }

  void series_default_order(unsigned short b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_series_default_order_=b;
    else
      _series_default_order_=b;
  }

  static int _angle_mode_=0;
  bool angle_radian(GIAC_CONTEXT)
  {
    if(contextptr && contextptr->globalptr)
      return contextptr->globalptr->_angle_mode_ == 0;
    else
      return _angle_mode_ == 0;
  }

  void angle_radian(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_angle_mode_=(b?0:1);
    else
      _angle_mode_=(b?0:1);
  }

  bool angle_degree(GIAC_CONTEXT)
  {
    if(contextptr && contextptr->globalptr)
      return contextptr->globalptr->_angle_mode_ == 1;
    else
      return _angle_mode_ == 1;
  }

  int get_mode_set_radian(GIAC_CONTEXT)
  {
    int mode;
    if(contextptr && contextptr->globalptr)
    {
      mode = contextptr->globalptr->_angle_mode_;
      contextptr->globalptr->_angle_mode_ = 0;
    }
    else
    {
      mode = _angle_mode_;
      _angle_mode_ = 0;
    }
    return mode;
  }

  void angle_mode(int b, GIAC_CONTEXT)
  {
    if(contextptr && contextptr->globalptr)
      contextptr->globalptr->_angle_mode_ = b;
    else
      _angle_mode_ = b;
  }

  int & angle_mode(GIAC_CONTEXT)
  {
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_angle_mode_;
    else
      return _angle_mode_;
  }

  static bool _show_point_=true;
  bool & show_point(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_show_point_;
    else
      return _show_point_;
  }

  void show_point(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_show_point_=b;
    else
      _show_point_=b;
  }

  static int _show_axes_=1;
  int & show_axes(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_show_axes_;
    else
      return _show_axes_;
  }

  void show_axes(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_show_axes_=b;
    else
      _show_axes_=b;
  }

  static bool _io_graph_=false; 
  // DO NOT SET TO true WITH non-zero contexts or fix symadd when points are added
  bool & io_graph(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_io_graph_;
    else
      return _io_graph_;
  }

  void io_graph(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_io_graph_=b;
    else
      _io_graph_=b;
  }

  static bool _variables_are_files_=false;
  bool & variables_are_files(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_variables_are_files_;
    else
      return _variables_are_files_;
  }

  void variables_are_files(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_variables_are_files_=b;
    else
      _variables_are_files_=b;
  }

  static int _bounded_function_no_=0;
  int & bounded_function_no(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_bounded_function_no_;
    else
      return _bounded_function_no_;
  }

  void bounded_function_no(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_bounded_function_no_=b;
    else
      _bounded_function_no_=b;
  }

  static int _series_flags_=0x3;
  int & series_flags(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_series_flags_;
    else
      return _series_flags_;
  }

  void series_flags(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_series_flags_=b;
    else
      _series_flags_=b;
  }

  static int _step_infolevel_=0;
  int & step_infolevel(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_step_infolevel_;
    else
      return _step_infolevel_;
  }

  void step_infolevel(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_step_infolevel_=b;
    else
      _step_infolevel_=b;
  }

  static bool _local_eval_=true;
  bool & local_eval(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_local_eval_;
    else
      return _local_eval_;
  }

  void local_eval(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_local_eval_=b;
    else
      _local_eval_=b;
  }

  static bool _withsqrt_=true;
  bool & withsqrt(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_withsqrt_;
    else
      return _withsqrt_;
  }

  void withsqrt(bool b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_withsqrt_=b;
    else
      _withsqrt_=b;
  }

#ifdef WITH_MYOSTREAM
  my_ostream my_cerr (&CERR);
  static my_ostream * _logptr_= &my_cerr;

  my_ostream * logptr(GIAC_CONTEXT){
    my_ostream * res;
    if (contextptr && contextptr->globalptr )
      res=contextptr->globalptr->_logptr_;
    else
      res= _logptr_;
    return res?res:&my_cerr;
  }
#else
#ifdef NSPIRE
  static nio::console * _logptr_=&CERR;
  nio::console * logptr(GIAC_CONTEXT){
    return &CERR;
  }
#else
  static ostream * _logptr_=&CERR;
  ostream * logptr(GIAC_CONTEXT){
    ostream * res;
    if (contextptr && contextptr->globalptr )
      res=contextptr->globalptr->_logptr_;
    else
      res= _logptr_;
#ifdef EMCC
    return res?res:&COUT;
#else
    return res?res:&CERR;
#endif
  }
#endif
#endif

  void logptr(my_ostream * b,GIAC_CONTEXT){
#ifdef NSPIRE
#else
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_logptr_=b;
    else
      _logptr_=b;
#endif
  }

  thread_param::thread_param(): _kill_thread(false), thread_eval_status(-1), v(6)
#ifdef HAVE_LIBPTHREAD
#ifdef __MINGW_H
			      ,eval_thread(),stackaddr(0)
#else
			      ,eval_thread(0),stackaddr(0)
#endif
#endif
  { 
  }

  thread_param * & context0_thread_param_ptr(){
    static thread_param * ans=new thread_param();
    return ans;
  }

#if 0
  static thread_param & context0_thread_param(){
    return *context0_thread_param_ptr();
  }
#endif

  thread_param * thread_param_ptr(const context * contextptr){
    return (contextptr && contextptr->globalptr)?contextptr->globalptr->_thread_param_ptr:context0_thread_param_ptr();
  }

  bool kill_thread(GIAC_CONTEXT){
    thread_param * ptr= (contextptr && contextptr->globalptr )?contextptr->globalptr->_thread_param_ptr:0;
    return ptr?ptr->_kill_thread:context0_thread_param_ptr()->_kill_thread;
  }

  void kill_thread(bool b,GIAC_CONTEXT){
    thread_param * ptr= (contextptr && contextptr->globalptr )?contextptr->globalptr->_thread_param_ptr:0;
    if (!ptr)
      ptr=context0_thread_param_ptr();
    ptr->_kill_thread=b;
  }


#ifdef HAVE_LIBPTHREAD
  pthread_mutex_t _mutexptr = PTHREAD_MUTEX_INITIALIZER,_mutex_eval_status= PTHREAD_MUTEX_INITIALIZER;
  pthread_mutex_t * mutexptr(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr)
      return contextptr->globalptr->_mutexptr;
    return &_mutexptr;
  }

  bool is_context_busy(GIAC_CONTEXT){
    int concurrent=pthread_mutex_trylock(mutexptr(contextptr));
    bool res=concurrent==EBUSY;
    if (!res)
      pthread_mutex_unlock(mutexptr(contextptr));
    return res;
  }

  int thread_eval_status(GIAC_CONTEXT){
    int res;
    if (contextptr && contextptr->globalptr){
      pthread_mutex_lock(contextptr->globalptr->_mutex_eval_status_ptr);
      res=contextptr->globalptr->_thread_param_ptr->thread_eval_status;
      pthread_mutex_unlock(contextptr->globalptr->_mutex_eval_status_ptr);
    }
    else {
      pthread_mutex_lock(&_mutex_eval_status);
      res=context0_thread_param_ptr()->thread_eval_status;
      pthread_mutex_unlock(&_mutex_eval_status);
    }
    return res;
  }

  void thread_eval_status(int val,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr){
      pthread_mutex_lock(contextptr->globalptr->_mutex_eval_status_ptr);
      contextptr->globalptr->_thread_param_ptr->thread_eval_status=val;
      pthread_mutex_unlock(contextptr->globalptr->_mutex_eval_status_ptr);
    }
    else {
      pthread_mutex_lock(&_mutex_eval_status);
      context0_thread_param_ptr()->thread_eval_status=val;
      pthread_mutex_unlock(&_mutex_eval_status);
    }
  }

#else
  bool is_context_busy(GIAC_CONTEXT){
    return false;
  }

  int thread_eval_status(GIAC_CONTEXT){
    return -1;
  }
  
  void thread_eval_status(int val,GIAC_CONTEXT){
  }

#endif

  static int _eval_level=DEFAULT_EVAL_LEVEL;
  int & eval_level(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_eval_level;
    else
      return _eval_level;
  }

  void eval_level(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_eval_level=b;
    else
      _eval_level=b;
  }

#if 0 // defined(GIAC_HAS_STO_38) || defined(ConnectivityKit)
  static unsigned int _rand_seed=123457;
#else
  static tinymt32_t _rand_seed;
#endif

#if 0 // defined(GIAC_HAS_STO_38) || defined(ConnectivityKit)
  unsigned int & rand_seed(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_rand_seed;
    else
      return _rand_seed;
  }
#else
  tinymt32_t * rand_seed(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return &contextptr->globalptr->_rand_seed;
    else
      return &_rand_seed;
  }
#endif

  void rand_seed(unsigned int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_rand_seed=b;
    else
      _rand_seed=b;
  }

  int std_rand(){
#if 1 // def NSPIRE
    static unsigned int r = 0;
    r = unsigned ((1664525*ulonglong(r)+1013904223)%(ulonglong(1)<<31));
    return r;
#else
    return std::rand();
#endif
  }

  int giac_rand(GIAC_CONTEXT){
#if 0 // defined(GIAC_HAS_STO_38) || defined(ConnectivityKit)
    unsigned int & r = rand_seed(contextptr);
    // r = (2147483629*ulonglong(r)+ 2147483587)% 2147483647;
    r = unsigned ((1664525*ulonglong(r)+1013904223)%(ulonglong(1)<<31));
    return r;
#else
    for (;;){
      unsigned r=tinymt32_generate_uint32(rand_seed(contextptr)) >> 1;
      if (!(r>>31))
	return r;
    }
#endif // tinymt32
  }

  static int _prog_eval_level_val=1;
  int & prog_eval_level_val(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_prog_eval_level_val;
    else
      return _prog_eval_level_val;
  }

  void prog_eval_level_val(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_prog_eval_level_val=b;
    else
      _prog_eval_level_val=b;
  }

  void cleanup_context(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr ){
      contextptr->globalptr->_eval_level=DEFAULT_EVAL_LEVEL;
    }
    eval_level(contextptr)=DEFAULT_EVAL_LEVEL;
    if (!contextptr)
      protection_level=0;
    local_eval(true,contextptr);
  }


  static parser_lexer & _pl(){
    static parser_lexer * ans = new parser_lexer();
    ans->_i_sqrt_minus1_=1;
    return * ans;
  }
  int & lexer_column_number(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_pl._lexer_column_number_;
    else
      return _pl()._lexer_column_number_;
  }
  int & lexer_line_number(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_pl._lexer_line_number_;
    else
      return _pl()._lexer_line_number_;
  }
  void lexer_line_number(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_pl._lexer_line_number_=b;
    else
      _pl()._lexer_line_number_=b;
  }
  void increment_lexer_line_number(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      ++contextptr->globalptr->_pl._lexer_line_number_;
    else
      ++_pl()._lexer_line_number_;
  }

  int & index_status(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_pl._index_status_;
    else
      return _pl()._index_status_;
  }
  void index_status(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_pl._index_status_=b;
    else
      _pl()._index_status_=b;
  }

  int & i_sqrt_minus1(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_pl._i_sqrt_minus1_;
    else
      return _pl()._i_sqrt_minus1_;
  }
  void i_sqrt_minus1(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_pl._i_sqrt_minus1_=b;
    else
      _pl()._i_sqrt_minus1_=b;
  }

  int & opened_quote(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_pl._opened_quote_;
    else
      return _pl()._opened_quote_;
  }
  void opened_quote(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_pl._opened_quote_=b;
    else
      _pl()._opened_quote_=b;
  }

  int & in_rpn(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_pl._in_rpn_;
    else
      return _pl()._in_rpn_;
  }
  void in_rpn(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_pl._in_rpn_=b;
    else
      _pl()._in_rpn_=b;
  }

  int & spread_formula(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_pl._spread_formula_;
    else
      return _pl()._spread_formula_;
  }
  void spread_formula(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_pl._spread_formula_=b;
    else
      _pl()._spread_formula_=b;
  }

  int & initialisation_done(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_pl._initialisation_done_;
    else
      return _pl()._initialisation_done_;
  }
  void initialisation_done(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_pl._initialisation_done_=b;
    else
      _pl()._initialisation_done_=b;
  }

  static std::string & _autoname_(){
#ifdef GIAC_HAS_STO_38
    static string * ans = new string("GA");
#else
    static string * ans = new string("A");
#endif
    return *ans;
  }
  static int _calc_mode_=0; 
  int & calc_mode(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_calc_mode_;
    else
      return _calc_mode_;
  }
  int abs_calc_mode(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return absint(contextptr->globalptr->_calc_mode_);
    else
      return absint(_calc_mode_);
  }
  void calc_mode(int b,GIAC_CONTEXT){
    if ( (b==38 || b==-38) && strcmp(_autoname_().c_str(),"GA")<0)
      autoname("GA",contextptr);
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_calc_mode_=b;
    else
      _calc_mode_=b;
  }

  std::string autoname(GIAC_CONTEXT){
    std::string res;
    if (contextptr && contextptr->globalptr )
      res=contextptr->globalptr->_autoname_;
    else
      res=_autoname_();
    for (;;){
      gen tmp(res,contextptr);
      if (tmp.type==_IDNT){
	gen tmp1=eval(tmp,1,contextptr);
	if (tmp==tmp1)
	  break;
      }
      autoname_plus_plus(res);
    }
    return res;
  }
  std::string autoname(const std::string & s,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_autoname_=s;
    else
      _autoname_()=s;
    return s;
  }

  static std::string & _autosimplify_(){
    static string * ans = new string("regroup");
    return *ans;
  }
  std::string autosimplify(GIAC_CONTEXT){
    std::string res;
    if (contextptr && contextptr->globalptr )
      res=contextptr->globalptr->_autosimplify_;
    else
      res=_autosimplify_();
    return res;
  }
  std::string autosimplify(const std::string & s,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_autosimplify_=s;
    else
      _autosimplify_()=s;
    return s;
  }

  static std::string & _format_double_(){
    static string * ans = new string("");
    return * ans;
  }
  std::string & format_double(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_format_double_;
    else
      return _format_double_();
  }

  std::string comment_s(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_pl._comment_s_;
    else
      return _pl()._comment_s_;
  }
  void comment_s(const std::string & b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_pl._comment_s_=b;
    else
      _pl()._comment_s_=b;
  }

  void increment_comment_s(const std::string & b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_pl._comment_s_ += b;
    else
      _pl()._comment_s_ += b;
  }

  void increment_comment_s(char b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_pl._comment_s_ += b;
    else
      _pl()._comment_s_ += b;
  }

  std::string parser_filename(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_pl._parser_filename_;
    else
      return _pl()._parser_filename_;
  }
  void parser_filename(const std::string & b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_pl._parser_filename_=b;
    else
      _pl()._parser_filename_=b;
  }

  std::string parser_error(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_pl._parser_error_;
    else
      return _pl()._parser_error_;
  }
  void parser_error(const std::string & b,GIAC_CONTEXT){
#ifndef GIAC_HAS_STO_38
    *logptr(contextptr) << b << endl;
#endif
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_pl._parser_error_=b;
    else
      _pl()._parser_error_=b;
  }

  std::string error_token_name(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_pl._error_token_name_;
    else
      return _pl()._error_token_name_;
  }
  void error_token_name(const std::string & b0,GIAC_CONTEXT){
    string b(b0);
    if (b0.size()==2 && b0[0]==-61 && b0[1]==-65)
      b="end of input";
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_pl._error_token_name_=b;
    else
      _pl()._error_token_name_=b;
  }

  int & first_error_line(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_pl._first_error_line_;
    else
      return _pl()._first_error_line_;
  }
  void first_error_line(int b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      contextptr->globalptr->_pl._first_error_line_=b;
    else
      _pl()._first_error_line_=b;
  }

  static gen & _parsed_gen_(){
    static gen * ans = new gen;
    return * ans;
  }
  gen parsed_gen(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return *contextptr->globalptr->_parsed_genptr_;
    else
      return _parsed_gen_();
  }
  void parsed_gen(const gen & b,GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      *contextptr->globalptr->_parsed_genptr_=b;
    else
      _parsed_gen_()=b;
  }

  static logo_turtle & _turtle_(){
    static logo_turtle * ans = new logo_turtle;
    return *ans;
  }
  logo_turtle & turtle(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr )
      return contextptr->globalptr->_turtle_;
    else
      return _turtle_();
  }

  // protect turtle access by a lock
  // turtle changes are mutually exclusive even in different contexts
#ifdef HAVE_LIBPTHREAD
  pthread_mutex_t turtle_mutex = PTHREAD_MUTEX_INITIALIZER;
#endif
  std::vector<logo_turtle> & _turtle_stack_(){
    static std::vector<logo_turtle> * ans = new std::vector<logo_turtle>(1,_turtle_());
#ifdef HAVE_LIBPTHREAD
    ans->reserve(20000);
#endif
    return *ans;
  }
  std::vector<logo_turtle> & turtle_stack(GIAC_CONTEXT){
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_lock(&turtle_mutex);
#endif
    std::vector<logo_turtle> * ans=0;
    if (contextptr && contextptr->globalptr )
      ans=&contextptr->globalptr->_turtle_stack_;
    else
      ans=&_turtle_stack_();
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_unlock(&turtle_mutex);
#endif
    return *ans;
  }

  // Other global variables
#ifdef NSPIRE
  bool secure_run=false;
#else
  bool secure_run=true;
#endif
  bool center_history=false;
  bool in_texmacs=false;
  bool block_signal=false;
  bool CAN_USE_LAPACK = true;
  bool simplify_sincosexp_pi=true;
  int history_begin_level=0; 
  // variable used to avoid copying the whole history between processes 
#ifdef WIN32 // Temporary
  int debug_infolevel=0;
#else
  int debug_infolevel=0;
#endif
#if defined __APPLE__ || defined VISUALC || defined __MINGW_H || defined BESTA_OS || defined NSPIRE || defined NSPIRE_NEWLIB
  int threads=1;
#else
  int threads=sysconf (_SC_NPROCESSORS_ONLN);
#endif
  unsigned short int GIAC_PADIC=50;
  const char cas_suffixe[]=".cas";
#if defined RTOS_THREADX || defined BESTA_OS
#ifdef BESTA_OS
  int LIST_SIZE_LIMIT = 100000 ;
  int FACTORIAL_SIZE_LIMIT = 1000 ;
  int CALL_LAPACK = 1111;
#else
  int LIST_SIZE_LIMIT = 1000 ;
  int FACTORIAL_SIZE_LIMIT = 254 ;
#endif
  int GAMMA_LIMIT = 100 ;
  int NEWTON_DEFAULT_ITERATION=40;
  int TEST_PROBAB_PRIME=25;
  int GCDHEU_MAXTRY=5;
  int GCDHEU_DEGREE=100;
  int DEFAULT_EVAL_LEVEL=5;
  int MODFACTOR_PRIMES =5;
  int NTL_MODGCD=50;
  int HENSEL_QUADRATIC_POWER=25;
  int KARAMUL_SIZE=13;
  int INT_KARAMUL_SIZE=300;
  int FFTMUL_SIZE=100; 
  int FFTMUL_INT_MAXBITS=1024;
  int MAX_ALG_EXT_ORDER_SIZE = 4;
  int MAX_COMMON_ALG_EXT_ORDER_SIZE = 16;
  int TRY_FU_UPRIME=5;
  int SOLVER_MAX_ITERATE=25;
  int MAX_PRINTABLE_ZINT=10000;
  int MAX_RECURSION_LEVEL=9;
  int GBASIS_DETERMINISTIC=20;
  int GBASISF4_MAX_TOTALDEG=1024;
  int GBASISF4_MAXITER=256;
  // int GBASISF4_BUCHBERGER=5;
  const int BUFFER_SIZE=512;
#else
  int CALL_LAPACK=1111;
  int LIST_SIZE_LIMIT = 100000000 ;
#ifdef USE_GMP_REPLACEMENTS
  int FACTORIAL_SIZE_LIMIT = 10000 ;
#else
  int FACTORIAL_SIZE_LIMIT = 10000000 ;
#endif
  int GAMMA_LIMIT = 100 ;
  int NEWTON_DEFAULT_ITERATION=60;
  int TEST_PROBAB_PRIME=25;
  int GCDHEU_MAXTRY=5;
  int GCDHEU_DEGREE=100;
  int DEFAULT_EVAL_LEVEL=25;
  int MODFACTOR_PRIMES =5;
  int NTL_MODGCD=50;
  int HENSEL_QUADRATIC_POWER=25;
  int KARAMUL_SIZE=13;
  int INT_KARAMUL_SIZE=300;
  int FFTMUL_SIZE=100; 
  int FFTMUL_INT_MAXBITS=1024;
#ifdef GIAC_GGB
  int MAX_ALG_EXT_ORDER_SIZE = 3;
#else
  int MAX_ALG_EXT_ORDER_SIZE = 6;
#endif
#if defined EMCC || defined NO_TEMPLATE_MULTGCD
  int MAX_COMMON_ALG_EXT_ORDER_SIZE = 16;
#else
  int MAX_COMMON_ALG_EXT_ORDER_SIZE = 64;
#endif
  int TRY_FU_UPRIME=5;
  int SOLVER_MAX_ITERATE=25;
  int MAX_PRINTABLE_ZINT=1000000;
  int MAX_RECURSION_LEVEL=100;
  int GBASIS_DETERMINISTIC=50;
  int GBASISF4_MAX_TOTALDEG=16384;
  int GBASISF4_MAXITER=1024;
  // int GBASISF4_BUCHBERGER=5;
  const int BUFFER_SIZE=16384;
#endif
  volatile bool ctrl_c=false,interrupted=false;
#ifdef GIAC_HAS_STO_38
  double powlog2float=1e4;
  int MPZ_MAXLOG2=8600; // max 2^8600 about 1K
#else
  double powlog2float=1e8;
  int MPZ_MAXLOG2=80000000; // 100 millions bits
#endif
#ifdef HAVE_LIBNTL
  int PROOT_FACTOR_MAXDEG=300;
#else
  int PROOT_FACTOR_MAXDEG=30;
#endif
  int ABS_NBITS_EVALF=1000;

  // used by WIN32 for the path to the xcas directory
  string & xcasroot(){
    static string * ans=new string;
    return * ans;
  }
  string & xcasrc(){
#ifdef WIN32
    static string * ans=new string("xcas.rc");
#else
    static string * ans=new string(".xcasrc");
#endif
    return *ans;
  }

#if defined HAVE_SIGNAL_H && !defined HAVE_NO_SIGNAL_H
  pid_t parent_id=getpid();
#else
  pid_t parent_id=0;
#endif
  pid_t child_id=0; // child process (to replace by a vector of childs?)

  void ctrl_c_signal_handler(int signum){
    ctrl_c=true;
#if !defined NSPIRE_NEWLIB && !defined WIN32 && !defined BESTA_OS && !defined NSPIRE
    if (child_id)
      kill(child_id,SIGINT);
#endif
#if defined HAVE_SIGNAL_H && !defined HAVE_NO_SIGNAL_H
    cerr << "Ctrl-C pressed (pid " << getpid() << ")" << endl;
#endif
  }
#ifndef NSPIRE
  gen catch_err(const std::runtime_error & error){
    cerr << error.what() << endl;
    debug_ptr(0)->sst_at_stack.clear();
    debug_ptr(0)->current_instruction_stack.clear();
    debug_ptr(0)->args_stack.clear();
    protection_level=0;
    debug_ptr(0)->debug_mode=false;
    return string2gen(string(error.what()),false);
  }
#endif

#if 0
  static vecteur subvect(const vecteur & v,int i){
    int s=v.size();
    if (i<0)
      i=-i;
    vecteur res(v);
    for (;s<i;++s)
      res.push_back(undef);
    return vecteur(res.begin(),res.begin()+i);
  }
#endif

#ifdef HAVE_SIGNAL_H_OLD
  static bool running_file=false;
  static int run_modif_pos;
  bool synchronize_history=true;
  char buf[BUFFER_SIZE];

  // at the beginning the parent process calls make_child
  // this forks, the child process then waits for a SIGUSR1 from
  // the parent that indicate data to evaluate is ready in #cas_entree#
  // When finished the child kills the parent with SIGUSR1
  // The answer is in #cas_sortie#
  // Parent: child_busy is set to true during evaluation
  // and reset to false by SIGUSR1
  // Child uses signal_child for signal trapping and parent uses
  // data_ready
  // SIGUSR2 is used by the child process for intermediate data
  // [Typically a plot instruction]
  // The child kills the parent with SIGUSR2,
  // child_busy remains true but data ready is set
  // Then the child waits for a parent SIGUSR2 signal
  // signal_plot_parent is true for intermediate data, false otherwise
  volatile bool signal_child=true; // true if busy
  volatile bool signal_plot_child=false; // true if child can continue
  volatile bool signal_plot_parent=false; 
  volatile bool child_busy=false,data_ready=false;
  // child sends a SIGUSR1
  void data_signal_handler(int signum){
          // cerr << "Parent called" << endl;
    signal_plot_parent=false;
    child_busy=false;
    data_ready=true;
  }

  /*
  void control_c(){
    if (ctrl_c){
      ctrl_c=false;
      interrupted=true;
      cerr << "Throwing exception" << endl;
      throw(std::runtime_error("Stopped by Ctrl-C"));
    }
  }
  */

  // child sends a SIGUSR2 (intermediate data)
  void plot_signal_handler(int signum){
          // cerr << "Plot_signal_handler Parent called" << endl;
    signal_plot_parent=true;
    child_busy=false;
    data_ready=true;
  }

  void child_signal_handler(int signum){
    // cerr << "Child called" << endl;
    signal_child=true;
  }
  
  void child_plot_done(int signum){
    signal_plot_child=true;
  }
  
  void kill_and_wait_sigusr2(){
    sigset_t mask, oldmask;
    sigemptyset (&mask);
    sigaddset (&mask, SIGUSR2);
    /* Wait for a signal to arrive. */
    sigprocmask (SIG_BLOCK, &mask, &oldmask);
    signal_plot_child=false;
#ifndef WIN32
    kill(parent_id,SIGUSR2);
#endif
    // cerr << "Child ready" << endl;
#ifndef WIN32
    while (!signal_plot_child)
      sigsuspend (&oldmask);
    sigprocmask (SIG_UNBLOCK, &mask, NULL);
#endif
  }

  gen wait_parent(){
    kill_and_wait_sigusr2();
    ifstream child_in(cas_entree_name().c_str());    
    gen res;
    try {
      res=unarchive(child_in,context0);
    }
    catch (std::runtime_error & e){
      res=string2gen(e.what(),false);
    }
    child_in.close();
    return res;
  }

  static pid_t make_child(){ // forks and return child id
#if defined HAVE_NO_SIGNAL_H || defined(DONT_FORK)
#ifdef DONT_FORK
	  child_id = 1;
	  return 1;
#endif // DONT_FORK
	return -1;
#else // HAVE_NO_SIGNAL_H
    running_file=false;
    child_busy=false;
    ctrl_c=false;
    signal(SIGINT,ctrl_c_signal_handler);
    signal(SIGUSR1,data_signal_handler);
    signal(SIGUSR2,plot_signal_handler);
    if (child_id>(pid_t) 0)
      return child_id; // exists
    child_id=fork();
    if (child_id<(pid_t) 0)
      throw(std::runtime_error("Make_child error: Unable to fork"));
    if (!child_id){ // child process, redirect input/output
      sigset_t mask, oldmask;
      sigemptyset (&mask);
      sigaddset (&mask, SIGUSR1);
      signal(SIGUSR1,child_signal_handler);
      signal(SIGUSR2,child_plot_done);
      signal_child=false;
      gen args;
      /* Wait for a signal to arrive. */
      sigprocmask (SIG_BLOCK, &mask, &oldmask);
      signal_child=false;
      for (;;){
	// cerr << "Child ready" << endl;
#ifndef WIN32
	while (!signal_child)
	  sigsuspend (&oldmask);
	sigprocmask (SIG_UNBLOCK, &mask, NULL);
#endif
	// read and evaluate input
	CLOCK_T start, end;
	double elapsed;
	start = CLOCK();
        messages_to_print="";
	ifstream child_in(cas_entree_name().c_str());
	// Unarchive step
	try {
	  child_in >> rpn_mode(context0) >> global_window_ymin >> history_begin_level ;
	  // cerr << args << endl;
	  if (history_begin_level<0){
	    child_in >> synchronize_history;
	    args=unarchive(child_in,context0);
	    if (!synchronize_history){
	      // cerr << "No sync " << endl;
	      history_in(0)[-history_begin_level-1]=args;
	      history_out(0)=subvect(history_out(0),-history_begin_level-1);
	    }
	    else {
	      // cerr << " Sync " << endl;
	      history_in(0)=*args._VECTptr;
	      args=unarchive(child_in,context0);
	      history_out(0)=*args._VECTptr;
	    }
	  }
	  else {
	    args=unarchive(child_in,context0);
	    // cerr << "Lu1 " << args << endl;
	    if (history_begin_level>signed(history_in(context0).size()))
	      history_begin_level=history_in(context0).size();
	    history_in(0)=mergevecteur(subvect(history_in(context0),history_begin_level),*args._VECTptr);
	    args=unarchive(child_in,context0);
	    // cerr << "Lu2 " << args << endl;
	    history_out(0)=mergevecteur(subvect(history_out(context0),history_begin_level),*args._VECTptr);
	    args=unarchive(child_in,context0);
	    // cerr << "Lu3 " << args << endl;
	    history_in(0).push_back(args);
	  }
	}
	catch (std::runtime_error & error ){
	  args = string2gen("Child unarchive error:"+string(error.what()),false);
	}
	child_in.close();
	// cerr << args << endl;
	// output result of evaluation to child_out
	gen args_evaled;
	{ // BEGIN of old try block
	  if (history_begin_level<0){
	    history_begin_level=-history_begin_level-1;
	    int s=history_in(context0).size();
	    block_signal=true;
	    for (int k=history_begin_level;k<s;++k){
	      try {
                if (history_in(context0)[k].is_symb_of_sommet(at_signal) || history_in(context0)[k].is_symb_of_sommet(at_debug))
		  history_out(context0).push_back(eval(history_in(context0)[k]._SYMBptr->feuille,eval_level(context0),context0));
                else
		  history_out(context0).push_back(eval(history_in(context0)[k],eval_level(context0),context0));
	      }
	      catch (std::runtime_error & error){
		history_out(context0).push_back(catch_err(error));
	      }
	    }
	    args=vecteur(history_in(context0).begin()+history_begin_level,history_in(context0).end());
	    args_evaled=vecteur(history_out(context0).begin()+history_begin_level,history_out(context0).end());
	  }
	  else {
	    if ( (args.type!=_VECT) || (args.subtype!=_RUNFILE__VECT) ){
	      if (debug_infolevel>10)
		cerr << "Child eval " << args << endl;
	      try {
		args_evaled=args.eval(1,context0);
	      }
	      catch (std::runtime_error & error){
		args_evaled=catch_err(error);
	      }
	      history_out(context0).push_back(args_evaled);
	      if (debug_infolevel>10)
		cerr << "Child result " << args_evaled << endl;
	    }
	    else {
	      vecteur v;
	      history_in(context0).pop_back();
	      const_iterateur it=args._VECTptr->begin(),itend=args._VECTptr->end();
	      for (;it!=itend;++it){
		if (it->is_symb_of_sommet(at_signal) ||it->is_symb_of_sommet(at_debug) )
		  continue;
		history_in(context0).push_back(*it);
		try {
		  if (it->is_symb_of_sommet(at_debug))
		    args_evaled=it->_SYMBptr->feuille.eval(1,context0);
		  else
		    args_evaled=it->eval(1,context0);
		}
		catch (std::runtime_error & error){
		  args_evaled=catch_err(error);
		}
		// cerr << args_evaled << endl;
		history_out(context0).push_back(args_evaled);
		v.push_back(args_evaled);
		ofstream child_out(cas_sortie_name().c_str());
		archive(child_out,*it,context0);
		archive(child_out,args_evaled,context0);
		child_out << messages_to_print << "ÿ" ;
		child_out.close();
		// cerr << "Signal reads " << res << endl;
		kill_and_wait_sigusr2();
	      }
	      // args_evaled=gen(v,args.subtype);
	      args=0;
	      args_evaled=0;
	    }
	  }
	} // END of old try/catch block
	block_signal=false;
	end = CLOCK();
	elapsed = ((double) (end - start)) / CLOCKS_PER_SEC;
	ofstream child_out(cas_sortie_name().c_str());
	archive(child_out,args,context0) ;
	archive(child_out,args_evaled,context0) ;
	child_out << messages_to_print ;
	int mm=messages_to_print.size();
	if (mm && (messages_to_print[mm-1]!='\n'))
	  child_out << endl;
	child_out << "Time: " << elapsed << "ÿ" ;
	child_out.close();
	// cerr << "Child sending signal to " << parent_id << endl;
	/* Wait for a signal to arrive. */
	sigprocmask (SIG_BLOCK, &mask, &oldmask);
	signal_child=false;
#ifndef WIN32
	kill(parent_id,SIGUSR1);
#endif
      }
    }
    // cerr << "Forking " << parent_id << " " << child_id << endl;
    return child_id;
#endif // HAVE_NO_SIGNAL_H
  }

  static void archive_write_error(){
    cerr << "Archive error on " << cas_entree_name() << endl;
  }
  
  // return true if entree has been sent to evalation by child process
  static bool child_eval(const string & entree,bool numeric,bool is_run_file){
#if defined(HAVE_NO_SIGNAL_H) || defined(DONT_FORK)
    return false;
#else
    if (is_run_file || rpn_mode(context0))
      history_begin_level=0;
    // added signal re-mapping because PARI seems to mess signal on the ipaq
    signal(SIGUSR1,data_signal_handler);
    signal(SIGUSR2,plot_signal_handler);
    if (!child_id)
      child_id=make_child();
    if (child_busy || data_ready)
      return false;
    gen entr;
    CLOCK_T start, end;
    start = CLOCK();
    try {
      ofstream parent_out(cas_entree_name().c_str());
      if (!signal_plot_parent){
	parent_out << rpn_mode(context0) << " " << global_window_ymin << " " << history_begin_level << endl;
	archive(parent_out,vecteur(history_in(context0).begin()+history_begin_level,history_in(context0).end()),context0);
	archive(parent_out,vecteur(history_out(context0).begin()+history_begin_level,history_out(context0).end()),context0);
      }
      if (is_run_file){
	ifstream infile(entree.c_str());
	char c;
	string s;
	while (!infile.eof()){
	  infile.get(c);
	  s += c;
	}
	entr = gen(s,context0);
	if (entr.type!=_VECT)
	  entr=gen(makevecteur(entr),_RUNFILE__VECT);
	else
	  entr.subtype=_RUNFILE__VECT;
      }
      else {
	entr = gen(entree,context0);
	if (numeric)
	  entr = symbolic(at_evalf,gen(entree,context0));
      }
      archive(parent_out,entr,context0);
      if (!parent_out)
	setsizeerr();
      parent_out.close();
      if (!parent_out)
	setsizeerr();
    } catch (std::runtime_error & e){
      archive_write_error();
      return false;
    }
    child_busy=true;
    if (signal_plot_parent){
      // cerr << "child_eval: Sending SIGUSR2 to child" << endl;
      signal_plot_parent=false;
#ifndef WIN32
      kill(child_id,SIGUSR2);
#endif
      return true;
    }
    // cerr << "Sending SIGUSR1 to " << child_id << endl;
#ifndef WIN32
    kill(child_id,SIGUSR1);
#endif
    running_file=is_run_file;
    end = CLOCK();
    // cerr << "# Save time" << double(end-start)/CLOCKS_PER_SEC << endl;
    return true;
#endif /// HAVE_NO_SIGNAL_H
  }

  static bool child_reeval(int history_begin_level){
#if defined(HAVE_NO_SIGNAL_H) || defined(DONT_FORK)
    return false;
#else
    signal(SIGUSR1,data_signal_handler);
    signal(SIGUSR2,plot_signal_handler);
    if (!child_id)
      child_id=make_child();
    if (child_busy || data_ready)
      return false;
    messages_to_print="";
    try {
      ofstream parent_out(cas_entree_name().c_str());
      parent_out << rpn_mode(context0) << " " << global_window_ymin << " " << -1-history_begin_level << " " << synchronize_history << endl;
      if (synchronize_history){
	archive(parent_out,history_in(context0),context0);
	archive(parent_out,vecteur(history_out(context0).begin(),history_out(context0).begin()+history_begin_level),context0);
      }
      else
	archive(parent_out,history_in(context0)[history_begin_level],context0);
      if (!parent_out)
	setsizeerr();
      parent_out.close();
      if (!parent_out)
	setsizeerr();
    } catch (std::runtime_error & e){
      archive_write_error();
      return false;
    }
    child_busy=true;
    running_file=true;
    // erase the part of the history that we are computing again
    if (run_modif_pos<signed(history_in(context0).size()))
      history_in(context0).erase(history_in(context0).begin()+run_modif_pos,history_in(context0).end());
    if (run_modif_pos<signed(history_out(context0).size()))
      history_out(context0).erase(history_out(context0).begin()+run_modif_pos,history_out(context0).end());
#ifndef WIN32
    kill(child_id,SIGUSR1);
#endif
    return true;
#endif /// HAVE_NO_SIGNAL_H
  }

  static void updatePICT(const vecteur & args){
    const_iterateur it=args.begin(),itend=args.end();
    gen sortie;
    for (;it!=itend;++it){
      sortie=*it;
      if (sortie.type==_POINTER_ && sortie.subtype==_FL_WIDGET_POINTER && fl_widget_updatepict_function){
	sortie = fl_widget_updatepict_function(sortie);
	// cerr << "updatepict" << sortie << endl;
      }
      if ( (sortie.type==_SYMB) && equalposcomp(plot_sommets,sortie._SYMBptr->sommet)){
#ifdef WITH_GNUPLOT
	plot_instructions.push_back(sortie);
#endif
	if ((sortie._SYMBptr->feuille.type==_VECT) && (sortie._SYMBptr->feuille._VECTptr->size()==3) && (sortie._SYMBptr->feuille._VECTptr->back().type==_STRNG) && ( ((*sortie._SYMBptr->feuille._VECTptr)[1].type==_VECT) || is_zero((*sortie._SYMBptr->feuille._VECTptr)[1])) ){
	  string lab=*(sortie._SYMBptr->feuille._VECTptr->back()._STRNGptr);
#ifdef WITH_GNUPLOT
	  if (lab.size() && (lab>=PICTautoname)){
	    PICTautoname=lab;
	    PICTautoname_plus_plus();
	  }
#endif
	}
      }
      else {
	if ( ((sortie.type==_SYMB) && (sortie._SYMBptr->sommet==at_erase)) ||
             ((sortie.type==_FUNC) && (*sortie._FUNCptr==at_erase)) ){
#ifdef WITH_GNUPLOT
	  plot_instructions.clear();
#endif
	}
	else {
	  if ( (sortie.type==_VECT) && (sortie._VECTptr->size()) && (sortie._VECTptr->back().type==_SYMB) && (equalposcomp(plot_sommets,sortie._VECTptr->back()._SYMBptr->sommet))){
#ifdef WITH_GNUPLOT
	    plot_instructions.push_back(sortie);
#endif
	    sortie=sortie._VECTptr->back();
	    if ((sortie._SYMBptr->feuille.type==_VECT) && (sortie._SYMBptr->feuille._VECTptr->size()==3) && (sortie._SYMBptr->feuille._VECTptr->back().type==_STRNG)  ){
	      string lab=*(sortie._SYMBptr->feuille._VECTptr->back()._STRNGptr);
#ifdef WITH_GNUPLOT
	      if (lab.size() && (lab>=PICTautoname)){
		PICTautoname=lab;
		PICTautoname_plus_plus();
	      }
#endif
	    } 
	  }
	  else {
#ifdef WITH_GNUPLOT
	    plot_instructions.push_back(zero);
#endif
	  }
	}
      }
    } // end for (;it!=itend;++it)
  } 

  static void signal_child_ok(){
    child_busy=true;
    data_ready=false;
    signal_plot_parent=false;
#ifndef WIN32
    kill(child_id,SIGUSR2);
#endif // WIN32
  }

  static const unary_function_eval * parent_evalonly_sommets_alias[]={*(const unary_function_eval **) &at_widget_size,*(const unary_function_eval **) &at_keyboard,*(const unary_function_eval **) &at_current_sheet,*(const unary_function_eval **) &at_Row,*(const unary_function_eval **) &at_Col,0};
  static const unary_function_ptr & parent_evalonly_sommets=(const unary_function_ptr *) parent_evalonly_sommets_alias;
  static bool update_data(gen & entree,gen & sortie,GIAC_CONTEXT){
    // if (entree.type==_IDNT)
    //   entree=symbolic(at_sto,makevecteur(sortie,entree));
    // discarded sto autoadd otherwise files with many definitions
    // are overwritten
    debug_ptr(contextptr)->debug_mode=false;
    if (signal_plot_parent){
      // cerr << "Child signaled " << entree << " " << sortie << endl;
      if ( entree.type==_SYMB ){
	if ( (entree._SYMBptr->sommet==at_click && entree._SYMBptr->feuille.type==_VECT && entree._SYMBptr->feuille._VECTptr->empty() ) 
	     || (entree._SYMBptr->sommet==at_debug) 
	     ) {
	  debug_ptr(contextptr)->debug_mode=(entree._SYMBptr->sommet==at_debug);
	  // cerr << "Child waiting" << endl;
	  data_ready=false;
	  *debug_ptr(contextptr)->debug_info_ptr=entree._SYMBptr->feuille;
	  debug_ptr(contextptr)->debug_refresh=true;
	  return true;
	}
	if ( entree._SYMBptr->sommet==at_click || entree._SYMBptr->sommet==at_inputform || entree._SYMBptr->sommet==at_interactive ){
	  // cerr << entree << endl;
	  gen res=entree.eval(1,contextptr);
	  // cerr << res << endl;
	  ofstream parent_out(cas_entree_name().c_str());
	  archive(parent_out,res,contextptr);
	  parent_out.close();
	  signal_child_ok();
	  return true;
	}
	// cerr << "Child signaled " << entree << " " << sortie << endl;
      }
      if (sortie.type==_SYMB){
	if (sortie._SYMBptr->sommet==at_SetFold){
	  current_folder_name=sortie._SYMBptr->feuille;
	  signal_child_ok();
	  return false;
	}
	if (sortie._SYMBptr->sommet==at_sto && sortie._SYMBptr->feuille.type==_VECT){
	  vecteur & v=*sortie._SYMBptr->feuille._VECTptr;
	  // cerr << v << endl;
	  if ((v.size()==2) && (v[1].type==_IDNT)){
	    if (v[1]._IDNTptr->value)
	      delete v[1]._IDNTptr->value;
	    v[1]._IDNTptr->value = new gen(v[0]);
	  }
	  signal_child_ok();
	  return false;	  
	}
	if (sortie._SYMBptr->sommet==at_purge){
	  gen & g=sortie._SYMBptr->feuille;
	  if ((g.type==_IDNT) && (g._IDNTptr->value) ){
	    delete g._IDNTptr->value;
	    g._IDNTptr->value=0;
	  }
	  signal_child_ok();
	  return false;
	}
	if ((sortie._SYMBptr->sommet==at_cd) && (sortie._SYMBptr->feuille.type==_STRNG)){
#ifndef HAVE_NO_CWD
	  chdir(sortie._SYMBptr->feuille._STRNGptr->c_str());
#endif
	  signal_child_ok();
	  return false;
	}
	if ( sortie._SYMBptr->sommet==at_insmod || sortie._SYMBptr->sommet==at_rmmod || sortie._SYMBptr->sommet==at_user_operator ){
	  protecteval(sortie,DEFAULT_EVAL_LEVEL,contextptr);
	  signal_child_ok();
	  return false;
	}
	if (sortie._SYMBptr->sommet==at_xyztrange){
	  gen f=sortie._SYMBptr->feuille;
	  if ( (f.type==_VECT) && (f._VECTptr->size()>=12)){
	    protecteval(sortie,2,contextptr);
            signal_child_ok();
	    return false;
	  }
	}
	if (sortie._SYMBptr->sommet==at_cas_setup){
	  gen f=sortie._SYMBptr->feuille;
	  if ( (f.type==_VECT) && (f._VECTptr->size()>=7)){
	    vecteur v=*f._VECTptr;
	    cas_setup(v,contextptr); 
            signal_child_ok();
	    return false;
	  }
	}
      }
      if (entree.type==_SYMB && entree._SYMBptr->sommet==at_signal && sortie.type==_SYMB && equalposcomp(parent_evalonly_sommets,sortie._SYMBptr->sommet) ) {
	gen res=sortie.eval(1,contextptr);
	ofstream parent_out(cas_entree_name().c_str());
	archive(parent_out,res,contextptr);
	parent_out.close();
	signal_child_ok();	
	return false;
      }
    } // end signal_plot_parent
    // cerr << "# Parse time" << double(end-start)/CLOCKS_PER_SEC << endl;
    // see if it's a PICT update
    vecteur args;
    // update history
    if (rpn_mode(contextptr)) {
      if ((sortie.type==_VECT)&& (sortie.subtype==_RPN_STACK__VECT)){
	history_out(contextptr)=*sortie._VECTptr;
	history_in(contextptr)=vecteur(history_out(contextptr).size(),undef);
	int i=erase_pos(contextptr);
	args=vecteur(history_out(contextptr).begin()+i,history_out(contextptr).end());
#ifdef WITH_GNUPLOT
	plot_instructions.clear();
#endif
      }
      else {
	if (entree.type==_FUNC){
	  int s=min(max(entree.subtype,0),(int)history_out(contextptr).size());
	  vecteur v(s);
	  for (int k=s-1;k>=0;--k){
	    v[k]=history_out(contextptr).back();
	    history_out(contextptr).pop_back();
	    history_in(contextptr).pop_back();
	  }
	  entree=symbolic(*entree._FUNCptr,v);
	}
	history_in(contextptr).push_back(entree);
	history_out(contextptr).push_back(sortie);
	int i=erase_pos(contextptr);
	args=vecteur(history_out(contextptr).begin()+i,history_out(contextptr).end());
#ifdef WITH_GNUPLOT
	plot_instructions.clear();
#endif
      }
    }  
    else {
      bool fait=false;
      if (running_file) {
	if (entree.type==_VECT && sortie.type==_VECT) {
	  history_in(contextptr)=mergevecteur(history_in(contextptr),*entree._VECTptr);
	  history_out(contextptr)=mergevecteur(history_out(contextptr),*sortie._VECTptr);
	  fait=true;
	}
	if (is_zero(entree) && is_zero(sortie))
	  fait=true;
      }
      if (!fait){
	if (in_texmacs){
	  COUT << GIAC_DATA_BEGIN << "verbatim:";
	  COUT << "ans(" << history_out(contextptr).size() << ") " << sortie << "\n";
  
	  COUT << GIAC_DATA_BEGIN << "latex:$$ " << gen2tex(entree,contextptr) << "\\quad = \\quad " << gen2tex(sortie,contextptr) << "$$" << GIAC_DATA_END;
	  COUT << "\n";
	  COUT << GIAC_DATA_BEGIN << "channel:prompt" << GIAC_DATA_END;
          COUT << "quest(" << history_out(contextptr).size()+1 << ") ";
          COUT << GIAC_DATA_END;
          fflush (stdout);
	}
	history_in(contextptr).push_back(entree);
	history_out(contextptr).push_back(sortie);
	// for PICT update
	args=vecteur(1,sortie);
      }
      if (running_file){
	// for PICT update
	int i=erase_pos(contextptr);
	args=vecteur(history_out(contextptr).begin()+i,history_out(contextptr).end());
	// CERR << "PICT clear" << endl;
#ifdef WITH_GNUPLOT
	plot_instructions.clear();
#endif
        //running_file=false;
      }
      // now do the update
    }
    updatePICT(args);
    data_ready=false;
    if (signal_plot_parent)
        signal_child_ok();
    return true;
  }

  static void archive_read_error(){
    CERR << "Read error on " << cas_sortie_name() << endl;
    data_ready=false;
#ifndef WIN32
    if (child_id)
      kill(child_id,SIGKILL);
#endif
    child_id=0;
  }

  static bool read_data(gen & entree,gen & sortie,string & message,GIAC_CONTEXT){
    if (!data_ready)
      return false;
    message="";
    try {
      ifstream parent_in(cas_sortie_name().c_str());
      if (!parent_in)
	setsizeerr();
      CLOCK_T start, end;  
      start = CLOCK();
      entree=unarchive(parent_in,contextptr);
      sortie=unarchive(parent_in,contextptr);
      end = CLOCK();
      parent_in.getline(buf,BUFFER_SIZE,'¿');
      if (buf[0]=='\n')
	message += (buf+1);
      else
	message += buf;
      if (!parent_in)
	setsizeerr();
    } catch (std::runtime_error & ){
      archive_read_error();
      return false;
    }
    return update_data(entree,sortie,contextptr);
  }
#endif // HAVE_SIGNAL_H_OLD

  string home_directory(){
    string s("/");
    if (getenv("GIAC_HOME"))
      s=getenv("GIAC_HOME");
    else {
      if (getenv("XCAS_HOME"))
	s=getenv("XCAS_HOME");
    }
    if (!s.empty() && s[s.size()-1]!='/')
      s += '/';
    if (s.size()!=1)
      return s;
#ifdef HAVE_NO_HOME_DIRECTORY
    return s;
#else
    if (access("/etc/passwd",R_OK))
      return "";
    uid_t u=getuid();
    passwd * p=getpwuid(u);
    s=p->pw_dir;
    return s+"/";
#endif
  }

  string cas_entree_name(){
    if (getenv("XCAS_TMP"))
      return getenv("XCAS_TMP")+("/#cas_entree#"+print_INT_(parent_id));
#ifdef IPAQ
    return "/tmp/#cas_entree#"+print_INT_(parent_id);
#else
    return home_directory()+"#cas_entree#"+print_INT_(parent_id);
#endif
  }

  string cas_sortie_name(){
    if (getenv("XCAS_TMP"))
      return getenv("XCAS_TMP")+("/#cas_sortie#"+print_INT_(parent_id));
#ifdef IPAQ
    return "/tmp/#cas_sortie#"+print_INT_(parent_id);
#else
    return home_directory()+"#cas_sortie#"+print_INT_(parent_id);
#endif
  }

  void read_config(const string & name,GIAC_CONTEXT,bool verbose){
#ifndef NSPIRE
#if !defined __MINGW_H 
    if (access(name.c_str(),R_OK)) {
      if (verbose)
	CERR << "// Unable to find config file " << name << endl;
      return;
    }
#endif
    ifstream inf(name.c_str());
    if (!inf)
      return;
    vecteur args;
    if (verbose)
      CERR << "// Reading config file " << name << endl;
    readargs_from_stream(inf,args,contextptr);
    gen g(args);
    if (debug_infolevel || verbose)    
      CERR << g << endl;
    g.eval(1,contextptr);
    if (verbose){
      CERR << "// User configuration done" << endl;
      CERR << "// Maximum number of parallel threads " << threads << endl;
      CERR << "Threads allowed " << threads_allowed << endl;
    }
    if (debug_infolevel){
#ifdef HASH_MAP_NAMESPACE
      CERR << "Using hash_map_namespace"<< endl;
#endif
      CERR << "Mpz_class allowed " << mpzclass_allowed << endl;
      // CERR << "Heap multiplication " << heap_mult << endl;
    }
#endif
  }

  // Unix: configuration is read from xcas.rc in the giac_aide_location dir
  // then from the user ~/.xcasrc
  // Win: configuration from $XCAS_ROOT/xcas.rc then from home_dir()+xcasrc
  // or if not available from current dir xcasrc
  void protected_read_config(GIAC_CONTEXT,bool verbose){
#ifndef NO_STDEXCEPT
    try {
#endif
      string s;
#ifdef WIN32
      s=giac::home_directory();
#ifdef GNUWINCE
	  s = xcasroot();
#else
      if (s.size()<2 && getenv("XCAS_ROOT")){
	s=getenv("XCAS_ROOT");
	if (debug_infolevel || verbose)
	  CERR << "Found XCAS_ROOT " << s << endl;
      }
#endif // GNUWINCE
#else
      s=giac_aide_location;
      s=s.substr(0,s.size()-8);
#endif
      if (s.size())
	giac::read_config(s+"/xcas.rc",contextptr,verbose);
      s=giac::home_directory();
      if (s.size()<2)
	s="";
      giac::read_config(s+xcasrc(),contextptr,verbose);
#ifndef NO_STDEXCEPT
    }
    catch (std::runtime_error & e){
      CERR << "Error in config file " << xcasrc() << " " << e.what() << endl;
    }
#endif
  }

  string giac_aide_dir(){
#if defined __MINGW_H || defined NSPIRE
    return xcasroot();
#else
    if (!access((xcasroot()+"aide_cas").c_str(),R_OK)){
      return xcasroot();
    }
    if (xcasroot().size()>4 && xcasroot().substr(xcasroot().size()-4,4)=="bin/"){
      string s(xcasroot().substr(0,xcasroot().size()-4));
      s+="share/giac/";
      if (!access((s+"aide_cas").c_str(),R_OK)){
	return s;
      }
    }
#ifdef __APPLE__
    if (!access("/Applications/usr/share/giac/",R_OK))
      return "/Applications/usr/share/giac/";
    if (getenv("XCAS_ROOT")){
      string s=getenv("XCAS_ROOT");
      return s;
    }
    return "/Applications/usr/share/giac/";
#endif
#ifdef WIN32
    return "/cygdrive/c/xcas/";
#endif
    string s(giac_aide_location); // ".../aide_cas"
    // test if aide_cas is there, if not test at xcasroot() return ""
    if (!access(s.c_str(),R_OK)){
      s=s.substr(0,s.size()-8);
      CERR << "// Giac share root-directory:" << s << endl;
      return s;
    }
    return "";
#endif // __MINGW_H
  }

  std::string absolute_path(const std::string & orig_file){
#ifdef BESTA_OS 
    // BP: FIXME
    return orig_file;
#else
#if (!defined WIN32) || (defined VISUALC)
    if (orig_file[0]=='/')
      return orig_file;
    else
      return giac_aide_dir()+orig_file;
#else
#if !defined GNUWINCE && !defined __MINGW_H
     string res=orig_file;
     const char *_epath;
     _epath = orig_file.c_str()  ;
     /* If we have a POSIX path list, convert to win32 path list */
     if (_epath != NULL && *_epath != 0
         && cygwin_posix_path_list_p (_epath)){
#ifdef x86_64
       int s = cygwin_conv_path (CCP_POSIX_TO_WIN_A , _epath, NULL, 0);
       char * _win32path = (char *) malloc(s);
       cygwin_conv_path(CCP_POSIX_TO_WIN_A,_epath, _win32path,s);
       s=strlen(_win32path);
#else
       char * _win32path = (char *) malloc
	 (cygwin_posix_to_win32_path_list_buf_size (_epath));
       cygwin_posix_to_win32_path_list (_epath, _win32path);
       int s=strlen(_win32path);
#endif
       res.clear();
       for (int i=0;i<s;++i){
	 char ch=_win32path[i];
	 if (ch=='\\' || ch==' ')
	   res += '\\';
	 res += ch;
       }
       free(_win32path);
     }
     return res;
#else
    string file=orig_file,s;
    if (file[0]!='/'){
      file=giac_aide_dir()+file;
    }
    if (file.substr(0,10)=="/cygdrive/" || (file[0]!='/' && file[1]!=':')){
      string s1=xcasroot();
      if (file.substr(0,10)=="/cygdrive/")
	s1=file[10]+(":"+file.substr(11,file.size()-11));
      else {
	// remove /cygdrive/
	if (s1.substr(0,10)=="/cygdrive/")
	  s1=s1[10]+(":"+s1.substr(11,s1.size()-11));
	else
	  s1="c:/xcas/";
	s1 += file;
      }
      string s2;
      int t=s1.size();
      for (int i=0;i<t;++i){
	if (s1[i]=='/')
	  s2+="\\\\";
	else {
	  if (s1[i]==' ')
	    s2+='\\';
	  s2+=s1[i];
	}
      }
      return s2;
    }
#endif // GNUWINCE
#endif // WIN32
    return orig_file;
#endif // BESTA_OS
  }

  bool is_file_available(const char * ch){
    if (!ch)
      return false;
#if !defined __MINGW_H && !defined NSPIRE
    if (access(ch,R_OK))
      return false;
#endif
    return true;
  }

  bool file_not_available(const char * ch){
    return !is_file_available(ch);
  }

  static void add_slash(string & path){
    if (!path.empty() && path[path.size()-1]!='/')
      path += '/';
  }

  bool check_file_path(const string & s){
    int ss=int(s.size()),i;
    for (i=0;i<ss;++i){
      if (s[i]==' ')
	break;
    }
    string name=s.substr(0,i);
    const char * ch=getenv("PATH");
    if (!ch || name[0]=='/')
      return is_file_available(name.c_str());
    string path;
    int l=int(strlen(ch));
    for (i=0;i<l;++i){
      if (ch[i]==':'){
	if (!path.empty()){
	  add_slash(path);
	  if (is_file_available((path+name).c_str()))
	    return true;
	}
	path="";
      }
      else
	path += ch[i];
    }
    add_slash(path);
    return path.empty()?false:is_file_available((path+name).c_str());
  }

  string browser_command(const string & orig_file){
#if defined __MINGW_H || defined NSPIRE
    return "";
#else
    string file=orig_file;
    string s;
    bool url=false;
    if (file.substr(0,4)=="http"){
      url=true;
      s="'"+file+"'";
    }
    else {
      if (file[0]!='/'){
#ifdef WIN32
	file=giac_aide_dir()+file;
#else    
	s=giac_aide_dir();
#endif
      }
      s="file:"+s+file;
    }
    if (debug_infolevel)
      CERR << s << endl;
#ifdef WIN32
    bool with_firefox=false;
    /*
    string firefox="/cygdrive/c/Program Files/Mozilla Firefox/firefox.exe";
    if (getenv("BROWSER")){
      string tmp=getenv("BROWSER");
      if (tmp=="firefox" || tmp=="mozilla"){
	with_firefox=!access(firefox.c_str(),R_OK);
	if (!with_firefox){
	  firefox="/cygdrive/c/Program Files/mozilla.org/Mozilla/mozilla.exe";
	  with_firefox=!access(firefox.c_str(),R_OK);
	}
      }
    }
    */
    if (!url && (file.substr(0,10)=="/cygdrive/" || (file[0]!='/' && file[1]!=':')) ){
      string s1=xcasroot();
      if (file.substr(0,10)=="/cygdrive/")
	s1=file[10]+(":"+file.substr(11,file.size()-11));
      else {
	// remove /cygdrive/
	if (s1.substr(0,10)=="/cygdrive/")
	  s1=s1[10]+(":"+s1.substr(11,s1.size()-11));
	else
	  s1="c:/xcas/";
	s1 += s.substr(5,s.size()-5);
      }
      CERR << "s1=" << s1 << endl;
      string s2;
      if (with_firefox)
	s2=s1;
      else {
	int t=int(s1.size());
	for (int i=0;i<t;++i){
	  if (s1[i]=='/')
	    s2+="\\";
	  else
	    s2+=s1[i];
	}
      }
      CERR << "s2=" << s2 << endl;
      s=s2;
      // s="file:"+s2;
      // s = s.substr(0,5)+"C:\\\\xcas\\\\"+s2;
    }
    // Remove # trailing part of URL
    int ss=int(s.size());
    for (--ss;ss>0;--ss){
      if (s[ss]=='#' || s[ss]=='.' || s[ss]=='/' )
	break;
    }
    if (ss && s[ss]!='.')
      s=s.substr(0,ss);
    s=xcasroot()+"cygstart.exe '"+s+"' &";
    /*
    if (with_firefox){
      s="'"+firefox+"' '"+s+"' &";
    }
    else {
      if (getenv("BROWSER"))
	s=getenv("BROWSER")+(" '"+s+"' &");
      else 
	s="'/cygdrive/c/Program Files/Internet Explorer/IEXPLORE.EXE' '"+s+"' &";
    }
    */
#else
    string browser;
    if (getenv("BROWSER"))
      browser=getenv("BROWSER");
    else {
#ifdef __APPLE__
      browser="open" ; // browser="/Applications/Safari.app/Contents/MacOS/Safari";
      // Remove file: that seems not supported by Safari
      if (!url)
	s = s.substr(5,s.size()-5);
      // Remove # trailing part of URL
      int ss=s.size();
      for (--ss;ss>0;--ss){
	if (s[ss]=='#' || s[ss]=='.' || s[ss]=='/' )
	  break;
      }
      if (ss && s[ss]!='.')
	s=s.substr(0,ss);
#else
      browser="mozilla";
      if (!access("/usr/bin/dillo",R_OK))
	browser="dillo";
      if (!access("/usr/bin/firefox",R_OK))
	browser="firefox";
#endif
    }
    // find binary name
    int bs=browser.size(),i;
    for (i=bs-1;i>=0;--i){
      if (browser[i]=='/')
	break;
    }
    ++i;
    string browsersub=browser.substr(i,bs-i);
    if (s[0]!='\'') s='\''+s+'\'';
    if (browsersub=="mozilla" || browsersub=="mozilla-bin" || browsersub=="firefox" ){
      s="if ! "+browser+" -remote \"openurl("+s+")\" ; then "+browser+" "+s+" & fi &";
    }
    else
      s=browser+" "+s+" &";
#endif
    //if (debug_infolevel)
      CERR << "// Running command:"+ s<<endl;
    return s;
#endif // __MINGW_H
  }

  bool system_browser_command(const string & file){
#ifdef BESTA_OS
    return false;
#else
#ifdef WIN32
    string res=file;
    if (file.size()>4 && file.substr(0,4)!="http" && file.substr(0,4)!="file"){
      if (res[0]!='/')
	res=giac_aide_dir()+res;
      // Remove # trailing part of URL
      int ss=int(res.size());
      for (--ss;ss>0;--ss){
	if (res[ss]=='#' || res[ss]=='.' || res[ss]=='/' )
	  break;
      }
      if (ss && res[ss]!='.')
	res=res.substr(0,ss);
      CERR << res << endl;
#if !defined VISUALC && !defined __MINGW_H && !defined NSPIRE
      /* If we have a POSIX path list, convert to win32 path list */
      const char *_epath;
      _epath = res.c_str()  ;
      if (_epath != NULL && *_epath != 0
	  && cygwin_posix_path_list_p (_epath)){
#ifdef x86_64
	int s = cygwin_conv_path (CCP_POSIX_TO_WIN_A , _epath, NULL, 0);
	char * _win32path = (char *) malloc(s);
	cygwin_conv_path(CCP_POSIX_TO_WIN_A,_epath, _win32path,s);
#else
	char * _win32path = (char *) malloc (cygwin_posix_to_win32_path_list_buf_size (_epath));
	cygwin_posix_to_win32_path_list (_epath, _win32path);
#endif
	res = _win32path;
	free(_win32path);
      }
#endif
    }
    CERR << res << endl;
#if !defined VISUALC && !defined __MINGW_H && !defined NSPIRE
    // FIXME: works under visualc but not using /UNICODE flag
    // find correct flag
    ShellExecute(NULL,NULL,res.c_str(),NULL,NULL,1);
#endif
    return true;
#else
#ifdef BESTA_OS
    return false; // return 1;
#else
    return !system(browser_command(file).c_str());
#endif
#endif
#endif
  }

  vecteur remove_multiples(vecteur & ww){
    vecteur w;
    if (!ww.empty()){
      islesscomplexthanf_sort(ww.begin(),ww.end());
      gen prec=ww[0];
      for (unsigned i=1;i<ww.size();++i){
	if (ww[i]==prec)
	  continue;
	w.push_back(prec);
	prec=ww[i];
      }
      w.push_back(prec);
    }
    return w;
  }

  int equalposcomp(const vector<int> v,int i){
    vector<int>::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it)
      if (*it==i)
	return int(it-v.begin())+1;
    return 0;
  }

  int equalposcomp(const vector<short int> v,int i){
    vector<short int>::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it)
      if (*it==i)
	return int(it-v.begin())+1;
    return 0;
  }

  int equalposcomp(int tab[],int f){
    for (int i=1;*tab!=0;++tab,++i){
      if (*tab==f)
	return i;
    }
    return 0;
  }

  std::string find_lang_prefix(int i){
    switch (i){
    case 1:
      return "fr/";
    case 2:
      return "en/";
    case 3:
      return "es/";
    case 4:
      return "el/";
    case 5:
      return "pt/";
    case 6:
      return "it/";
      /*
    case 7:
      return "tr/";
      break;
      */
    case 8:
      return "zh/";
    case 9:
      return "de/";
      break;
    default:
      return "local/";
    }  
  }

  std::string find_doc_prefix(int i){
    switch (i){
    case 1:
      return "doc/fr/";
      break;
    case 2:
      return "doc/en/";
      break;
    case 3:
      return "doc/es/";
      break;
    case 4:
      return "doc/el/";
      break;
    case 5:
      return "doc/pt/";
      break;
    case 6:
      return "doc/it/";
      break;
      /*
    case 7:
      return "doc/tr/";
      break;
      */
    case 8:
      return "doc/zh/";
      break;
    case 9:
      return "doc/de/";
      break;
    default:
      return "doc/local/";
    }  
  }

  void update_completions(){
    if (vector_completions_ptr()){
      vector_completions_ptr()->clear();
      int n=int(vector_aide_ptr()->size());
      for (int k=0;k<n;++k){
	if (debug_infolevel>10)
	  CERR << "+ " << (*vector_aide_ptr())[k].cmd_name  << endl;
	vector_completions_ptr()->push_back((*vector_aide_ptr())[k].cmd_name);
      }
    }
  }

  void add_language(int i,GIAC_CONTEXT){
    if (!equalposcomp(lexer_localization_vector(),i)){
      lexer_localization_vector().push_back(i);
      update_lexer_localization(lexer_localization_vector(),lexer_localization_map(),back_lexer_localization_map(),contextptr);
#ifndef EMCC
      if (vector_aide_ptr()){
	// add locale command description
	int count;
	string filename=giac_aide_dir()+find_doc_prefix(i)+"aide_cas";
	readhelp(*vector_aide_ptr(),filename.c_str(),count,true);
	// add synonyms
	multimap<string,localized_string>::iterator it,backend=back_lexer_localization_map().end(),itend;
	vector<aide>::iterator jt = vector_aide_ptr()->begin(),jtend=vector_aide_ptr()->end();
	for (;jt!=jtend;++jt){
	  it=back_lexer_localization_map().find(jt->cmd_name);
	  itend=back_lexer_localization_map().upper_bound(jt->cmd_name);
	  if (it!=backend){
	    for (;it!=itend;++it){
	      if (it->second.language==i)
		jt->synonymes.push_back(it->second);
	    }
	  }
	}
	int s = int(vector_aide_ptr()->size());
	for (int j=0;j<s;++j){
	  aide a=(*vector_aide_ptr())[j];
	  it=back_lexer_localization_map().find(a.cmd_name);
	  itend=back_lexer_localization_map().upper_bound(a.cmd_name);
	  if (it!=backend){
	    for (;it!=itend;++it){
	      if (it->second.language==i){
		a.cmd_name=it->second.chaine;
		a.language=it->second.language;
		vector_aide_ptr()->push_back(a);
	      }
	    }
	  }
	}
	CERR << "Added " << vector_aide_ptr()->size()-s << " synonyms" << endl;
	sort(vector_aide_ptr()->begin(),vector_aide_ptr()->end(),alpha_order);
	update_completions();
      }
#endif
    }
  }

  void remove_language(int i,GIAC_CONTEXT){
    if (int pos=equalposcomp(lexer_localization_vector(),i)){
      if (vector_aide_ptr()){
	vector<aide> nv;
	int s=int(vector_aide_ptr()->size());
	for (int j=0;j<s;++j){
	  if ((*vector_aide_ptr())[j].language!=i)
	    nv.push_back((*vector_aide_ptr())[j]);
	}
	*vector_aide_ptr() = nv;
	update_completions();
	vector<aide>::iterator jt = vector_aide_ptr()->begin(),jtend=vector_aide_ptr()->end();
	for (;jt!=jtend;++jt){
	  vector<localized_string> syno;
	  vector<localized_string>::const_iterator kt=jt->synonymes.begin(),ktend=jt->synonymes.end();
	  for (;kt!=ktend;++kt){
	    if (kt->language!=i)
	      syno.push_back(*kt);
	  }
	  jt->synonymes=syno;
	}
      }
      --pos;
      lexer_localization_vector().erase(lexer_localization_vector().begin()+pos);
      update_lexer_localization(lexer_localization_vector(), lexer_localization_map(), back_lexer_localization_map(), contextptr);
	}
  }

  int string2lang(const string & s){
    if (s=="fr")
      return 1;
    if (s=="en")
      return 2;
    if (s=="sp" || s=="es")
      return 3;
    if (s=="el")
      return 4;
    if (s=="pt")
      return 5;
    if (s=="it")
      return 6;
    if (s=="tr")
      return 7;
    if (s=="zh")
      return 8;
    if (s=="de")
      return 9;
    return 0;
  }

  std::string set_language(int i,GIAC_CONTEXT){
#ifdef EMCC
    if (language(contextptr)!=i){
      language(i,contextptr);
      add_language(i,contextptr);
    }
#else
    language(i,contextptr);
    add_language(i,contextptr);
#endif
    return find_doc_prefix(i);
  }

  std::string read_env(GIAC_CONTEXT,bool verbose){
#ifndef RTOS_THREADX
#ifndef BESTA_OS
    if (getenv("GIAC_LAPACK")){
      giac::CALL_LAPACK=atoi(getenv("GIAC_LAPACK"));
      if (verbose)
	CERR << "// Will call lapack if dimension is >=" << CALL_LAPACK << endl;
    }
    if (getenv("GIAC_PADIC")){
      giac::GIAC_PADIC=atoi(getenv("GIAC_PADIC"));
      if (verbose)
	CERR << "// Will use p-adic algorithm if dimension is >=" << giac::GIAC_PADIC << endl;
    }
#endif
#endif
    if (getenv("XCAS_RPN")){
      if (verbose)
	CERR << "// Setting RPN mode" << endl;
      giac::rpn_mode(contextptr)=true;
    }
    if (getenv("GIAC_XCAS_MODE")){
      giac::xcas_mode(contextptr)=atoi(getenv("GIAC_XCAS_MODE"));
      if (verbose)
	CERR << "// Setting maple mode " << giac::xcas_mode(contextptr) << endl;
    }
    if (getenv("GIAC_C")){
      giac::xcas_mode(contextptr)=0;
      if (verbose)
	CERR << "// Setting giac C mode" << endl;
    }
    if (getenv("GIAC_MAPLE")){
      giac::xcas_mode(contextptr)=1;
      if (verbose)
	CERR << "// Setting giac maple mode" << endl;
    }
    if (getenv("GIAC_MUPAD")){
      giac::xcas_mode(contextptr)=2;
      if (verbose)
	CERR << "// Setting giac mupad mode" << endl;
    }
    if (getenv("GIAC_TI")){
      giac::xcas_mode(contextptr)=3;
      if (verbose)
	CERR << "// Setting giac TI mode" << endl;
    }
    if (getenv("GIAC_MONO")){
      if (verbose)
	CERR << "// Threads polynomial * disabled" << endl;
      giac::threads_allowed=false;
    }
    if (getenv("GIAC_MPZCLASS")){
      if (verbose)
	CERR << "// mpz_class enabled" << endl;
      giac::mpzclass_allowed=true;
    }
    if (getenv("GIAC_DEBUG")){
      giac::debug_infolevel=atoi(getenv("GIAC_DEBUG"));
      CERR << "// Setting debug_infolevel to " << giac::debug_infolevel << endl;
    }
    string s;
    if (getenv("LANG"))
      s=getenv("LANG");
    else { // __APPLE__ workaround
#if !defined VISUALC && !defined __MINGW_H && !defined NSPIRE
      if (!strcmp(gettext("File"),"Fich")){
	setenv("LANG","fr_FR.UTF8",1);
	s="fr_FR.UTF8";
      }
      else {
	s="en_US.UTF8";
	setenv("LANG",s.c_str(),1);
      }
#endif
    }
    if (s.size()>=2){
      s=s.substr(0,2);
      int i=string2lang(s);
      if (i){
	language(i,contextptr);
	return find_doc_prefix(i);
      }
    }
    language(0,contextptr);
    return find_doc_prefix(0);
  }

  string cas_setup_string(GIAC_CONTEXT){
    string s("cas_setup(");
    s += print_VECT(cas_setup(contextptr),_SEQ__VECT,contextptr);
    s += "),";
    s += "xcas_mode(";
    s += print_INT_(xcas_mode(contextptr)+(python_compat(contextptr)?256:0));
    s += ")";
    return s;
  }

  string geo_setup_string(){
    return xyztrange(gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,gnuplot_zmin,gnuplot_zmax,gnuplot_tmin,gnuplot_tmax,global_window_xmin,global_window_xmax,global_window_ymin,global_window_ymax,_show_axes_,class_minimum,class_size,
#ifdef WITH_GNUPLOT
		     gnuplot_hidden3d,gnuplot_pm3d
#else
		     1,1
#endif
		     ).print(context0);
  }

  string add_extension(const string & s,const string & ext,const string & def){
    if (s.empty())
      return def+"."+ext;
    int i=int(s.size());
    for (--i;i>0;--i){
      if (s[i]=='.')
	break;
    }
    if (i<=0)
      return s+"."+ext;
    return s.substr(0,i)+"."+ext;
  }

#ifdef HAVE_LIBPTHREAD
  pthread_mutex_t context_list_mutex = PTHREAD_MUTEX_INITIALIZER;
#endif

  vector<context *> & context_list(){
    static vector<context *> * ans=new vector<context *>(1,(context *) 0);
    return *ans;
  }
  context::context() { 
    // CERR << "new context " << this << endl;
    parent=0;
    tabptr=new sym_tab; 
    globalcontextptr=this; previous=0; globalptr=new global; 
    quoted_global_vars=new vecteur;
    rootofs=new vecteur;
    history_in_ptr=new vecteur;
    history_out_ptr=new vecteur;
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_lock(&context_list_mutex);
#endif
    context_list().push_back(this);
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_unlock(&context_list_mutex);
#endif
  }

#ifndef RTOS_THREADX
#if !defined BESTA_OS && !defined NSPIRE
  std::map<std::string,context *> * context_names = new std::map<std::string,context *> ;

  context::context(const string & name) { 
    // CERR << "new context " << this << endl;
    parent=0;
    tabptr=new sym_tab; 
    globalcontextptr=this; previous=0; globalptr=new global; 
    quoted_global_vars=new vecteur;
    rootofs=new vecteur;
    history_in_ptr=new vecteur;
    history_out_ptr=new vecteur;
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_lock(&context_list_mutex);
#endif
    context_list().push_back(this);
    if (context_names)
      (*context_names)[name]=this;
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_unlock(&context_list_mutex);
#endif
  }
#endif
#endif

  context::context(const context & c) { 
    *this = c;
  }

  context * context::clone() const{
    context * ptr = new context;
    *ptr->globalptr = *globalptr;
    return ptr;
  }

  void init_context(context * ptr){
    if (!ptr){
      CERR << "init_context on null context" << endl;
      return;
    }
     ptr->globalptr->_xcas_mode_=_xcas_mode_;
#ifdef GIAC_HAS_STO_38
     ptr->globalptr->_calc_mode_=-38;
#else
     ptr->globalptr->_calc_mode_=_calc_mode_;
#endif
     ptr->globalptr->_decimal_digits_=_decimal_digits_;
     ptr->globalptr->_scientific_format_=_scientific_format_;
     ptr->globalptr->_integer_format_=_integer_format_;
     ptr->globalptr->_integer_mode_=_integer_mode_;
     ptr->globalptr->_latex_format_=_latex_format_;
#ifdef BCD
     ptr->globalptr->_bcd_decpoint_=_bcd_decpoint_;
     ptr->globalptr->_bcd_mantissa_=_bcd_mantissa_;
     ptr->globalptr->_bcd_flags_=_bcd_flags_;
     ptr->globalptr->_bcd_printdouble_=_bcd_printdouble_;
#endif
     ptr->globalptr->_expand_re_im_=_expand_re_im_;
     ptr->globalptr->_do_lnabs_=_do_lnabs_;
     ptr->globalptr->_eval_abs_=_eval_abs_;
     ptr->globalptr->_eval_equaltosto_=_eval_equaltosto_;
     ptr->globalptr->_complex_mode_=_complex_mode_;
     ptr->globalptr->_escape_real_=_escape_real_;
     ptr->globalptr->_try_parse_i_=_try_parse_i_;
     ptr->globalptr->_specialtexprint_double_=_specialtexprint_double_;
     ptr->globalptr->_atan_tan_no_floor_=_atan_tan_no_floor_;
     ptr->globalptr->_keep_acosh_asinh_=_keep_acosh_asinh_;
     ptr->globalptr->_keep_algext_=_keep_algext_;
     ptr->globalptr->_python_compat_=_python_compat_;
     ptr->globalptr->_complex_variables_=_complex_variables_;
     ptr->globalptr->_increasing_power_=_increasing_power_;
     ptr->globalptr->_approx_mode_=_approx_mode_;
     ptr->globalptr->_series_variable_name_=_series_variable_name_;
     ptr->globalptr->_series_default_order_=_series_default_order_;
     ptr->globalptr->_autosimplify_=_autosimplify_();
     ptr->globalptr->_angle_mode_=_angle_mode_;
     ptr->globalptr->_variables_are_files_=_variables_are_files_;
     ptr->globalptr->_bounded_function_no_=_bounded_function_no_;
     ptr->globalptr->_series_flags_=_series_flags_; // bit1= full simplify, bit2=1 for truncation
     ptr->globalptr->_step_infolevel_=_step_infolevel_; // bit1= full simplify, bit2=1 for truncation
     ptr->globalptr->_local_eval_=_local_eval_;
     ptr->globalptr->_default_color_=_default_color_;
     ptr->globalptr->_epsilon_=_epsilon_<=0?1e-12:_epsilon_;
     ptr->globalptr->_proba_epsilon_=_proba_epsilon_;
     ptr->globalptr->_withsqrt_=_withsqrt_;
     ptr->globalptr->_show_point_=_show_point_; // show 3-d point 
     ptr->globalptr->_io_graph_=_io_graph_; // show 2-d point in io
     ptr->globalptr->_show_axes_=_show_axes_;
     ptr->globalptr->_spread_Row_=_spread_Row_;
     ptr->globalptr->_spread_Col_=_spread_Col_;
     ptr->globalptr->_printcell_current_row_=_printcell_current_row_;
     ptr->globalptr->_printcell_current_col_=_printcell_current_col_;
     ptr->globalptr->_all_trig_sol_=_all_trig_sol_;
     ptr->globalptr->_lexer_close_parenthesis_=_lexer_close_parenthesis_;
     ptr->globalptr->_rpn_mode_=_rpn_mode_;
     ptr->globalptr->_ntl_on_=_ntl_on_;
     ptr->globalptr->_prog_eval_level_val =_prog_eval_level_val ;
     ptr->globalptr->_eval_level=_eval_level;
     ptr->globalptr->_rand_seed=_rand_seed;
     ptr->globalptr->_language_=_language_;
     ptr->globalptr->_max_sum_sqrt_=_max_sum_sqrt_;      
     ptr->globalptr->_max_sum_add_=_max_sum_add_;   
     
  }

  context * clone_context(const context * contextptr) {
    context * ptr = new context;
    if (contextptr){
      *ptr->globalptr = *contextptr->globalptr;
      *ptr->tabptr = *contextptr->tabptr;
    }
    else {
      init_context(ptr);
    }
    return ptr;
  }

  context::~context(){
    // CERR << "delete context " << this << endl;
    if (!previous){
      if (history_in_ptr)
	delete history_in_ptr;
      if (history_out_ptr)
	delete history_out_ptr;
      if (quoted_global_vars)
	delete quoted_global_vars;
      if (rootofs)
	delete rootofs;
      if (globalptr)
	delete globalptr;
      if (tabptr)
	delete tabptr;
#ifdef HAVE_LIBPTHREAD
      pthread_mutex_lock(&context_list_mutex);
#endif
      int s=int(context_list().size());
      for (int i=s-1;i>0;--i){
	if (context_list()[i]==this){
	  context_list().erase(context_list().begin()+i);
	  break;
	}
      }
#ifndef RTOS_THREADX
#if !defined BESTA_OS && !defined NSPIRE
      if (context_names){
	map<string,context *>::iterator it=context_names->begin(),itend=context_names->end();
	for (;it!=itend;++it){
	  if (it->second==this){
	    context_names->erase(it);
	    break;
	  }
	}
      }
#endif
#endif
#ifdef HAVE_LIBPTHREAD
      pthread_mutex_unlock(&context_list_mutex);
#endif
    }
  }

#ifndef CLK_TCK
#define CLK_TCK 1
#endif

#ifndef HAVE_NO_SYS_TIMES_H
   double delta_tms(struct tms tmp1,struct tms tmp2){
#if defined(HAVE_SYSCONF) && !defined(EMCC)
     return double( tmp2.tms_utime+tmp2.tms_stime+tmp2.tms_cutime+tmp2.tms_cstime-(tmp1.tms_utime+tmp1.tms_stime+tmp1.tms_cutime+tmp1.tms_cstime) )/sysconf(_SC_CLK_TCK);
#else
    return double( tmp2.tms_utime+tmp2.tms_stime+tmp2.tms_cutime+tmp2.tms_cstime-(tmp1.tms_utime+tmp1.tms_stime+tmp1.tms_cutime+tmp1.tms_cstime) )/CLK_TCK;
#endif
   }
#endif /// HAVE_NO_SYS_TIMES_H

  string remove_filename(const string & s){
    int l=int(s.size());
    for (;l;--l){
      if (s[l-1]=='/')
	break;
    }
    return s.substr(0,l);
  }

#ifdef HAVE_LIBPTHREAD
  static void * in_thread_eval(void * arg){
    pthread_setcancelstate(PTHREAD_CANCEL_ENABLE,NULL);
    pthread_setcanceltype(PTHREAD_CANCEL_ASYNCHRONOUS,NULL);
    vecteur *v = (vecteur *) arg;
    context * contextptr=(context *) (*v)[2]._POINTER_val;
    thread_param * ptr =thread_param_ptr(contextptr);
    pthread_attr_getstacksize(&ptr->attr,&ptr->stacksize);
    ptr->stackaddr=(void *) ((uintptr_t) &ptr-ptr->stacksize);
#ifndef __MINGW_H
    struct tms tmp1,tmp2;
    times(&tmp1);
#else
    int beg=CLOCK();
#endif
    gen g = (*v)[0];
    g = giac::protecteval(g,(*v)[1].val,contextptr);
    try {
#ifndef __MINGW_H
      times(&tmp2);
      double dt=delta_tms(tmp1,tmp2);
      total_time(contextptr) += dt;
      (*v)[4]=dt;
#else
      int end=CLOCK();
      (*v)[4]=end-beg;
#endif
      (*v)[5]=g;
    } catch (std::runtime_error & e){
    }
    ptr->stackaddr=0;
    thread_eval_status(0,contextptr);
    pthread_exit(0);
  }

  // create a new thread for evaluation of g at level level in context
  bool make_thread(const giac::gen & g,int level,const giac_callback & f,void * f_param,const context * contextptr){
    if (is_context_busy(contextptr))
      return false;
    thread_param * ptr =thread_param_ptr(contextptr);
    if (!ptr || ptr->v.size()!=6)
      return false;
    pthread_mutex_lock(mutexptr(contextptr));
    ptr->v[0]=g;
    ptr->v[1]=level;
    ptr->v[2]=gen((void *)contextptr,_CONTEXT_POINTER);
    ptr->f=f;
    ptr->f_param=f_param;
    thread_eval_status(1,contextptr);
    pthread_attr_init(&ptr->attr);
    int cres=pthread_create (&ptr->eval_thread, &ptr->attr, in_thread_eval,(void *)&ptr->v);
    if (cres){
      thread_eval_status(0,contextptr);
      pthread_mutex_unlock(mutexptr(contextptr));
    }
    return !cres;
  }

  // check if contextptr has a running evaluation thread
  // if not returns -1
  // if evaluation is not finished return 1
  // if evaluation is finished, clear mutex lock and 
  // call the thread_param_ptr callback function with the evaluation value
  // and returns 0
  // otherwise returns status, 2=debug, 3=wait click
  int check_thread(context * contextptr){
    if (!is_context_busy(contextptr))
      return -1;
    int status=thread_eval_status(contextptr);
    if (status!=0 && !kill_thread(contextptr))
      return status;
    thread_param tp = *thread_param_ptr(contextptr);
    if (status==0){
      // unsigned thread_return_value=0;
      // void * ptr_return=&thread_return_value;
      // pthread_join(eval_thread,&ptr_return);
      if (
#ifdef __MINGW_H
	  1
#else
	  tp.eval_thread
#endif
	  ){
	giac_callback f=tp.f;
	gen arg_callback=tp.v[5];
	void * param_callback=tp.f_param;
	double tt=tp.v[4]._DOUBLE_val;
	pthread_join(tp.eval_thread,0); 
	pthread_mutex_unlock(mutexptr(contextptr));
	// double tt=double(tp.v[4].val)/CLOCKS_PER_SEC;
	if (tt>0.4)
	  (*logptr(contextptr)) << gettext("\nEvaluation time: ") << tt << endl;
	if (f)
	  f(arg_callback,param_callback);
	else
	  (*logptr(contextptr)) << arg_callback << endl;
	return 0;
      }
    }
    if (kill_thread(contextptr)){
      kill_thread(false,contextptr);
      thread_eval_status(0,contextptr);
      clear_prog_status(contextptr);
      cleanup_context(contextptr);
      if (tp.f)
	tp.f(string2gen("Aborted",false),tp.f_param);
#ifndef __MINGW_H
      *logptr(contextptr) << gettext("Thread ") << tp.eval_thread << " has been cancelled" << endl;
#endif
      try {
	pthread_cancel(tp.eval_thread) ;
      } catch (...){
      }
      pthread_mutex_unlock(mutexptr(contextptr));
      return -1;
    }
    return status;
  }

  // check contexts in context_list starting at index i, 
  // returns at first context with status >= 2
  // return value is -2 (invalid range), -1 (ok) or context number
  int check_threads(int i){
    int s,ans=-1;
    context * cptr;
    if (// i>=s || 
	i<0)
      return -2;
    for (;;++i){
      pthread_mutex_lock(&context_list_mutex);
      s=context_list().size();
      if (i<s)
	cptr=context_list()[i];
      pthread_mutex_unlock(&context_list_mutex);
      if (i>=s)
	break;
      int res=check_thread(cptr);
      if (res>1){
	ans=i;
	break;
      }
    }
    return ans;
  }

  giac::gen thread_eval(const giac::gen & g_,int level,context * contextptr,void (* wait_0001)(context *) ){
    gen g=equaltosto(g_,contextptr);
    /* launch a new thread for evaluation only,
       no more readqueue, readqueue is done by the "parent" thread
       Ctrl-C will kill the "child" thread
       wait_001 is a function that should wait 0.001 s and update thinks
       for example it could remove idle callback of a GUI
       then call the wait function of the GUI and readd callbacks
    */
    pthread_t eval_thread;
    giac::vecteur v(6);
    v[0]=g;
    v[1]=level;
    v[2]=gen(contextptr,_CONTEXT_POINTER);
    pthread_mutex_lock(mutexptr(contextptr));
    thread_eval_status(1,contextptr);
    int cres=pthread_create (&eval_thread, (pthread_attr_t *) NULL, in_thread_eval,(void *)&v);
    if (!cres){
      for (;;){
	int eval_status=thread_eval_status(contextptr);
	if (!eval_status)
	  break;
	wait_0001(contextptr);
	if (kill_thread(contextptr)){
	  kill_thread(false,contextptr);
	  clear_prog_status(contextptr);
	  cleanup_context(contextptr);
#ifndef __MINGW_H
	  *logptr(contextptr) << gettext("Cancel thread ") << eval_thread << endl;
#endif
#ifdef NO_STDEXCEPT
	  pthread_cancel(eval_thread) ;
#else
	  try {
	    pthread_cancel(eval_thread) ;
	  } catch (...){
	  }
#endif
	  pthread_mutex_unlock(mutexptr(contextptr));
	  return undef;
	}
      }
      // unsigned thread_return_value=0;
      // void * ptr=&thread_return_value;
      pthread_join(eval_thread,0); // pthread_join(eval_thread,&ptr);
      // Restore pointers and return v[3]
      pthread_mutex_unlock(mutexptr(contextptr));
      // double tt=double(v[4].val)/CLOCKS_PER_SEC;
      double tt=v[4]._DOUBLE_val;
      if (tt>0.1)
	(*logptr(contextptr)) << gettext("Evaluation time: ") << tt << endl;
      return v[5];
    }
    pthread_mutex_unlock(mutexptr(contextptr));
    return giac::protecteval(g,level,contextptr);
  }
#else

  bool make_thread(const giac::gen & g,int level,const giac_callback & f,void * f_param,context * contextptr){
    return false;
  }

  int check_thread(context * contextptr){
    return -1;
  }

  int check_threads(int i){
    return -1;
  }

  giac::gen thread_eval(const giac::gen & g,int level,context * contextptr,void (* wait_001)(context * )){
    return giac::protecteval(g,level,contextptr);
  }
#endif // HAVE_LIBPTHREAD

  debug_struct::debug_struct():indent_spaces(0),debug_mode(false),sst_mode(false),sst_in_mode(false),debug_allowed(true),current_instruction(-1),debug_refresh(false){
    debug_info_ptr=new gen;
    fast_debug_info_ptr=new gen;
    debug_prog_name=new gen;
    debug_localvars=new gen;
    debug_contextptr=0;
  }
  
  debug_struct::~debug_struct(){
    delete debug_info_ptr;
    delete fast_debug_info_ptr;
    delete debug_prog_name;
    delete debug_localvars;
  }

  debug_struct & debug_struct::operator =(const debug_struct & dbg){
    indent_spaces=dbg.indent_spaces;
    args_stack=dbg.args_stack;
    debug_breakpoint=dbg.debug_breakpoint;
    debug_watch=dbg.debug_watch ;
    debug_mode=dbg.debug_mode;
    sst_mode=dbg.sst_mode ;
    sst_in_mode=dbg.sst_in_mode ;
    debug_allowed=dbg.debug_allowed;
    current_instruction_stack=dbg.current_instruction_stack;
    current_instruction=dbg.current_instruction;
    sst_at_stack=dbg.sst_at_stack;
    sst_at=dbg.sst_at;
    if (debug_info_ptr)
      delete debug_info_ptr;
    debug_info_ptr=new gen(dbg.debug_info_ptr?*dbg.debug_info_ptr:0) ;
    if (fast_debug_info_ptr)
      delete fast_debug_info_ptr;
    fast_debug_info_ptr= new gen(dbg.fast_debug_info_ptr?*dbg.fast_debug_info_ptr:0);
    if (debug_prog_name)
      delete debug_prog_name;
    debug_prog_name=new gen(dbg.debug_prog_name?*dbg.debug_prog_name:0);
    if (debug_localvars)
      delete debug_localvars;
    debug_localvars=new gen(dbg.debug_localvars?*dbg.debug_localvars:0);
    debug_refresh=dbg.debug_refresh;
    debug_contextptr=dbg.debug_contextptr;
    return *this;
  }

  static debug_struct & _debug_data(){
    static debug_struct * ans = new debug_struct;
    return *ans;
  }

  debug_struct * debug_ptr(GIAC_CONTEXT){
    if (contextptr && contextptr->globalptr)
      return contextptr->globalptr->_debug_ptr;
    return &_debug_data();
  }

  void clear_prog_status(GIAC_CONTEXT){
    debug_struct * ptr=debug_ptr(contextptr);
    if (ptr){
      ptr->args_stack.clear();
      ptr->debug_mode=false;
      ptr->sst_at_stack.clear();
      if (!contextptr)
	protection_level=0;
    }
  }


  global::global() : _xcas_mode_(0), 
		     _calc_mode_(0),_decimal_digits_(12),
		     _scientific_format_(0), _integer_format_(0), _latex_format_(0), 
#ifdef BCD
		     _bcd_decpoint_('.'|('E'<<16)|(' '<<24)),_bcd_mantissa_(12+(15<<8)), _bcd_flags_(0),_bcd_printdouble_(false),
#endif
		     _expand_re_im_(true), _do_lnabs_(true), _eval_abs_(true),_eval_equaltosto_(true),_integer_mode_(true),_complex_mode_(false), _escape_real_(true),_complex_variables_(false), _increasing_power_(false), _approx_mode_(false), _variables_are_files_(false), _local_eval_(true), 
		     _withsqrt_(true), 
		     _show_point_(true),  _io_graph_(true),
		     _all_trig_sol_(false),
#ifdef WITH_MYOSTREAM
		     _ntl_on_(true),
		     _lexer_close_parenthesis_(true),_rpn_mode_(false),_try_parse_i_(true),_specialtexprint_double_(false),_atan_tan_no_floor_(false),_keep_acosh_asinh_(false),_keep_algext_(false),_python_compat_(false),_angle_mode_(0), _bounded_function_no_(0), _series_flags_(0x3),_step_infolevel_(0),_default_color_(FL_BLACK), _epsilon_(1e-12), _proba_epsilon_(1e-15),  _show_axes_(1),_spread_Row_ (-1), _spread_Col_ (-1),_logptr_(&my_CERR),_prog_eval_level_val(1), _eval_level(DEFAULT_EVAL_LEVEL), _rand_seed(123457),_max_sum_sqrt_(3),_max_sum_add_(100000),_total_time_(0),_evaled_table_(0),_extra_ptr_(0),_series_variable_name_('h'),_series_default_order_(5),
#else
		     _ntl_on_(true),
		     _lexer_close_parenthesis_(true),_rpn_mode_(false),_try_parse_i_(true),_specialtexprint_double_(false),_atan_tan_no_floor_(false),_keep_acosh_asinh_(false),_keep_algext_(false),_python_compat_(false),_angle_mode_(0), _bounded_function_no_(0), _series_flags_(0x3),_step_infolevel_(0),_default_color_(FL_BLACK), _epsilon_(1e-12), _proba_epsilon_(1e-15),  _show_axes_(1),_spread_Row_ (-1), _spread_Col_ (-1), 
#ifdef EMCC
		     _logptr_(&COUT), 
#else
		     _logptr_(&CERR), 
#endif
		     _prog_eval_level_val(1), _eval_level(DEFAULT_EVAL_LEVEL), _rand_seed(123457),_max_sum_sqrt_(3),_max_sum_add_(100000),_total_time_(0),_evaled_table_(0),_extra_ptr_(0),_series_variable_name_('h'),_series_default_order_(5)
#endif
  { 
    _pl._i_sqrt_minus1_=1;
    _turtle_stack_.push_back(_turtle_);
    _debug_ptr=new debug_struct;
    _thread_param_ptr=new thread_param;
    _parsed_genptr_=new gen;
#ifdef GIAC_HAS_STO_38
    _autoname_="GA";
#else
    _autoname_="A";
#endif
    _autosimplify_="regroup";
    _format_double_="";
#ifdef HAVE_LIBPTHREAD
    _mutexptr = new pthread_mutex_t;
    pthread_mutex_init(_mutexptr,0);
    _mutex_eval_status_ptr = new pthread_mutex_t;
    pthread_mutex_init(_mutex_eval_status_ptr,0);
#endif
  }

  global & global::operator = (const global & g){
     _xcas_mode_=g._xcas_mode_;
     _calc_mode_=g._calc_mode_;
     _decimal_digits_=g._decimal_digits_;
     _scientific_format_=g._scientific_format_;
     _integer_format_=g._integer_format_;
     _integer_mode_=g._integer_mode_;
     _latex_format_=g._latex_format_;
#ifdef BCD
     _bcd_decpoint_=g._bcd_decpoint_;
     _bcd_mantissa_=g._bcd_mantissa_;
     _bcd_flags_=g._bcd_flags_;
     _bcd_printdouble_=g._bcd_printdouble_;
#endif
     _expand_re_im_=g._expand_re_im_;
     _do_lnabs_=g._do_lnabs_;
     _eval_abs_=g._eval_abs_;
     _eval_equaltosto_=g._eval_equaltosto_;
     _complex_mode_=g._complex_mode_;
     _escape_real_=g._escape_real_;
     _complex_variables_=g._complex_variables_;
     _increasing_power_=g._increasing_power_;
     _approx_mode_=g._approx_mode_;
     _series_variable_name_=g._series_variable_name_;
     _series_default_order_=g._series_default_order_;
     _angle_mode_=g._angle_mode_;
     _atan_tan_no_floor_=g._atan_tan_no_floor_;
     _keep_acosh_asinh_=g._keep_acosh_asinh_;
     _keep_algext_=g._keep_algext_;
     _python_compat_=g._python_compat_;
     _variables_are_files_=g._variables_are_files_;
     _bounded_function_no_=g._bounded_function_no_;
     _series_flags_=g._series_flags_; // bit1= full simplify, bit2=1 for truncation, bit3=?, bit4=1 do not convert back SPOL1 to symbolic expression
     _step_infolevel_=g._step_infolevel_; // bit1= full simplify, bit2=1 for truncation
     _local_eval_=g._local_eval_;
     _default_color_=g._default_color_;
     _epsilon_=g._epsilon_;
     _proba_epsilon_=g._proba_epsilon_;
     _withsqrt_=g._withsqrt_;
     _show_point_=g._show_point_; // show 3-d point 
     _io_graph_=g._io_graph_; // show 2-d point in io
     _show_axes_=g._show_axes_;
     _spread_Row_=g._spread_Row_;
     _spread_Col_=g._spread_Col_;
     _printcell_current_row_=g._printcell_current_row_;
     _printcell_current_col_=g._printcell_current_col_;
     _all_trig_sol_=g._all_trig_sol_;
     _ntl_on_=g._ntl_on_;
     _prog_eval_level_val =g._prog_eval_level_val ;
     _eval_level=g._eval_level;
     _rand_seed=g._rand_seed;
     _language_=g._language_;
     _max_sum_sqrt_=g._max_sum_sqrt_;
     _max_sum_add_=g._max_sum_add_;
     _turtle_=g._turtle_;
     _turtle_stack_=g._turtle_stack_;
     _autoname_=g._autoname_;
     _format_double_=g._format_double_;
     _extra_ptr_=g._extra_ptr_;
     return *this;
  }

  global::~global(){
    delete _parsed_genptr_;
    delete _thread_param_ptr;
    delete _debug_ptr;
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_destroy(_mutexptr);
    delete _mutexptr;
    pthread_mutex_destroy(_mutex_eval_status_ptr);
    delete _mutex_eval_status_ptr;
#endif
  }

#ifdef __APPLE__
  bool my_isnan(double d){
#if 1 // TARGET_OS_IPHONE
    return isnan(d);
#else
    return __isnand(d);
#endif
  }

  bool my_isinf(double d){
#if 1  // TARGET_OS_IPHONE
    return isinf(d);
#else
    return __isinfd(d);
#endif
  }

#else // __APPLE__
  bool my_isnan(double d){
#if defined VISUALC || defined BESTA_OS
#ifndef RTOS_THREADX
    return _isnan(d)!=0;
#else
    return isnan(d);
#endif
#else
#if defined(FIR_LINUX) || defined(FIR_ANDROID)
    return std::isnan(d);
#else
    return isnan(d);
#endif
#endif
  }

  bool my_isinf(double d){
#if defined VISUALC || defined BESTA_OS
    double x=0.0;
    return d==1.0/x || d==-1.0/x;
#else
#if defined(FIR_LINUX) || defined(FIR_ANDROID)
    return std::isinf(d);
#else
    return isinf(d);
#endif
#endif
  }

#endif // __APPLE__

  double giac_floor(double d){
    double maxdouble=longlong(1)<<30;
    if (d>=maxdouble || d<=-maxdouble)
      return std::floor(d);
    if (d>0)
      return int(d);
    double k=int(d);
    if (k==d)
      return k;
    else
      return k-1;
  }
  double giac_ceil(double d){
    double maxdouble=longlong(1)<<54;
    if (d>=maxdouble || d<=-maxdouble)
      return d;
    if (d<0)
      return double(longlong(d));
    double k=double(longlong(d));
    if (k==d)
      return k;
    else
      return k+1;
  }



/* --------------------------------------------------------------------- */
/*
 * Copyright 2001-2004 Unicode, Inc.
 * 
 * Disclaimer
 * 
 * This source code is provided as is by Unicode, Inc. No claims are
 * made as to fitness for any particular purpose. No warranties of any
 * kind are expressed or implied. The recipient agrees to determine
 * applicability of information provided. If this file has been
 * purchased on magnetic or optical media from Unicode, Inc., the
 * sole remedy for any claim will be exchange of defective media
 * within 90 days of receipt.
 * 
 * Limitations on Rights to Redistribute This Code
 * 
 * Unicode, Inc. hereby grants the right to freely use the information
 * supplied in this file in the creation of products supporting the
 * Unicode Standard, and to make copies of this file in any form
 * for internal or external distribution as long as this notice
 * remains attached.
 */

/* ---------------------------------------------------------------------

    Conversions between UTF-16 and UTF-8. Source code file.
    Author: Mark E. Davis, 1994.
    Rev History: Rick McGowan, fixes & updates May 2001.
    Sept 2001: fixed const & error conditions per
    mods suggested by S. Parent & A. Lillich.
    June 2002: Tim Dodd added detection and handling of incomplete
    source sequences, enhanced error detection, added casts
    to eliminate compiler warnings.
    July 2003: slight mods to back out aggressive FFFE detection.
    Jan 2004: updated switches in from-UTF8 conversions.
    Oct 2004: updated to use UNI_MAX_LEGAL_UTF32 in UTF-32 conversions.
    Jan 2013: Jean-Yves Avenard adapted to only calculate size if 
    destination pointer are null

------------------------------------------------------------------------ */


static const int halfShift  = 10; /* used for shifting by 10 bits */

static const UTF32 halfBase = 0x0010000UL;
static const UTF32 halfMask = 0x3FFUL;

#define UNI_SUR_HIGH_START  (UTF32)0xD800
#define UNI_SUR_HIGH_END    (UTF32)0xDBFF
#define UNI_SUR_LOW_START   (UTF32)0xDC00
#define UNI_SUR_LOW_END     (UTF32)0xDFFF

/* --------------------------------------------------------------------- */

/*
 * Index into the table below with the first byte of a UTF-8 sequence to
 * get the number of trailing bytes that are supposed to follow it.
 * Note that *legal* UTF-8 values can't have 4 or 5-bytes. The table is
 * left as-is for anyone who may want to do such conversion, which was
 * allowed in earlier algorithms.
 */
static const char trailingBytesForUTF8[256] = {
    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1, 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
    2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2, 3,3,3,3,3,3,3,3,4,4,4,4,5,5,5,5
};

/*
 * Magic values subtracted from a buffer value during UTF8 conversion.
 * This table contains as many values as there might be trailing bytes
 * in a UTF-8 sequence.
 */
static const UTF32 offsetsFromUTF8[6] = { 0x00000000UL, 0x00003080UL, 0x000E2080UL, 
             0x03C82080UL, 0xFA082080UL, 0x82082080UL };

/*
 * Once the bits are split out into bytes of UTF-8, this is a mask OR-ed
 * into the first byte, depending on how many bytes follow.  There are
 * as many entries in this table as there are UTF-8 sequence types.
 * (I.e., one byte sequence, two byte... etc.). Remember that sequencs
 * for *legal* UTF-8 will be 4 or fewer bytes total.
 */
static const UTF8 firstByteMark[7] = { 0x00, 0x00, 0xC0, 0xE0, 0xF0, 0xF8, 0xFC };

/* --------------------------------------------------------------------- */

/* The interface converts a whole buffer to avoid function-call overhead.
 * Constants have been gathered. Loops & conditionals have been removed as
 * much as possible for efficiency, in favor of drop-through switches.
 * (See "Note A" at the bottom of the file for equivalent code.)
 * If your compiler supports it, the "isLegalUTF8" call can be turned
 * into an inline function.
 */

/* --------------------------------------------------------------------- */

unsigned int ConvertUTF16toUTF8 (
    const UTF16* sourceStart, const UTF16* sourceEnd, 
    UTF8* targetStart, UTF8* targetEnd, ConversionFlags flags) {
    ConversionResult result = conversionOK;
    const UTF16* source = sourceStart;
    UTF8* target = targetStart;
    UTF32 ch;
    while ((source < sourceEnd) && (ch = *source)) {
    unsigned short bytesToWrite = 0;
    const UTF32 byteMask = 0xBF;
    const UTF32 byteMark = 0x80; 
    const UTF16* oldSource = source; /* In case we have to back up because of target overflow. */
    source++;
    /* If we have a surrogate pair, convert to UTF32 first. */
    if (ch >= UNI_SUR_HIGH_START && ch <= UNI_SUR_HIGH_END) {
        /* If the 16 bits following the high surrogate are in the source buffer... */
        UTF32 ch2;
        if ((source < sourceEnd) && (ch2 = *source)) {
        /* If it's a low surrogate, convert to UTF32. */
        if (ch2 >= UNI_SUR_LOW_START && ch2 <= UNI_SUR_LOW_END) {
            ch = ((ch - UNI_SUR_HIGH_START) << halfShift)
            + (ch2 - UNI_SUR_LOW_START) + halfBase;
            ++source;
        } else if (flags == strictConversion) { /* it's an unpaired high surrogate */
            --source; /* return to the illegal value itself */
            result = sourceIllegal;
            break;
        }
        } else { /* We don't have the 16 bits following the high surrogate. */
        --source; /* return to the high surrogate */
        result = sourceExhausted;
        break;
        }
    } else if (flags == strictConversion) {
        /* UTF-16 surrogate values are illegal in UTF-32 */
        if (ch >= UNI_SUR_LOW_START && ch <= UNI_SUR_LOW_END) {
        --source; /* return to the illegal value itself */
        result = sourceIllegal;
        break;
        }
    }
    /* Figure out how many bytes the result will require */
    if (ch < (UTF32)0x80) {      bytesToWrite = 1;
    } else if (ch < (UTF32)0x800) {     bytesToWrite = 2;
    } else if (ch < (UTF32)0x10000) {   bytesToWrite = 3;
    } else if (ch < (UTF32)0x110000) {  bytesToWrite = 4;
    } else {       bytesToWrite = 3;
                        ch = UNI_REPLACEMENT_CHAR;
    }

    target += bytesToWrite;
    if ((uintptr_t)target > (uintptr_t)targetEnd) {
        source = oldSource; /* Back up source pointer! */
        target -= bytesToWrite; result = targetExhausted; break;
    }
    switch (bytesToWrite) { /* note: everything falls through. */
        case 4: target--; if (targetStart) { *target = (UTF8)((ch | byteMark) & byteMask); ch >>= 6; }
        case 3: target--; if (targetStart) { *target = (UTF8)((ch | byteMark) & byteMask); ch >>= 6; }
        case 2: target--; if (targetStart) { *target = (UTF8)((ch | byteMark) & byteMask); ch >>= 6; }
        case 1: target--; if (targetStart) { *target =  (UTF8)(ch | firstByteMark[bytesToWrite]); }
    }
    target += bytesToWrite;
    }

    unsigned int length = int(target - targetStart);
    return length;
}

/* --------------------------------------------------------------------- */

/*
 * Utility routine to tell whether a sequence of bytes is legal UTF-8.
 * This must be called with the length pre-determined by the first byte.
 * If not calling this from ConvertUTF8to*, then the length can be set by:
 *  length = trailingBytesForUTF8[*source]+1;
 * and the sequence is illegal right away if there aren't that many bytes
 * available.
 * If presented with a length > 4, this returns false.  The Unicode
 * definition of UTF-8 goes up to 4-byte sequences.
 */

static Boolean isLegalUTF8(const UTF8 *source, int length) {
    UTF8 a;
    const UTF8 *srcptr = source+length;
    switch (length) {
    default: return false;
    /* Everything else falls through when "true"... */
    case 4: if ((a = (*--srcptr)) < 0x80 || a > 0xBF) return false;
    case 3: if ((a = (*--srcptr)) < 0x80 || a > 0xBF) return false;
    case 2: if ((a = (*--srcptr)) > 0xBF) return false;

    switch (*source) {
        /* no fall-through in this inner switch */
        case 0xE0: if (a < 0xA0) return false; break;
        case 0xED: if (a > 0x9F) return false; break;
        case 0xF0: if (a < 0x90) return false; break;
        case 0xF4: if (a > 0x8F) return false; break;
        default:   if (a < 0x80) return false;
    }

    case 1: if (*source >= 0x80 && *source < 0xC2) return false;
    }
    if (*source > 0xF4) return false;
    return true;
}

/* --------------------------------------------------------------------- */

/*
 * Exported function to return whether a UTF-8 sequence is legal or not.
 * This is not used here; it's just exported.
 */
Boolean isLegalUTF8Sequence(const UTF8 *source, const UTF8 *sourceEnd) {
    int length = trailingBytesForUTF8[*source]+1;
    if (source+length > sourceEnd) {
    return false;
    }
    return isLegalUTF8(source, length);
}

/* --------------------------------------------------------------------- */

unsigned int ConvertUTF8toUTF16 (
    const UTF8* sourceStart, const UTF8* sourceEnd, 
    UTF16* targetStart, UTF16* targetEnd, ConversionFlags flags) {
    ConversionResult result = conversionOK;
    const UTF8* source = sourceStart;
    UTF16* target = targetStart;
    while (source < sourceEnd && *source) {
    UTF32 ch = 0;
    unsigned short extraBytesToRead = trailingBytesForUTF8[*source];
    if (source + extraBytesToRead >= sourceEnd) {
        result = sourceExhausted; break;
    }
    /* Do this check whether lenient or strict */
    if (! isLegalUTF8(source, extraBytesToRead+1)) {
        result = sourceIllegal;
        break;
    }
    /*
     * The cases all fall through. See "Note A" below.
     */
    switch (extraBytesToRead) {
        case 5: ch += *source++; ch <<= 6; /* remember, illegal UTF-8 */
        case 4: ch += *source++; ch <<= 6; /* remember, illegal UTF-8 */
        case 3: ch += *source++; ch <<= 6;
        case 2: ch += *source++; ch <<= 6;
        case 1: ch += *source++; ch <<= 6;
        case 0: ch += *source++;
    }
    ch -= offsetsFromUTF8[extraBytesToRead];

    if ((uintptr_t)target >= (uintptr_t)targetEnd) {
        source -= (extraBytesToRead+1); /* Back up source pointer! */
        result = targetExhausted; break;
    }
    if (ch <= UNI_MAX_BMP) { /* Target is a character <= 0xFFFF */
        /* UTF-16 surrogate values are illegal in UTF-32 */
        if (ch >= UNI_SUR_HIGH_START && ch <= UNI_SUR_LOW_END) {
        if (flags == strictConversion) {
            source -= (extraBytesToRead+1); /* return to the illegal value itself */
            result = sourceIllegal;
            break;
        } else {
          if (targetStart)
            *target = UNI_REPLACEMENT_CHAR;
          target++;
        }
        } else {
          if (targetStart)
            *target = (UTF16)ch; /* normal case */
          target++;
        }
    } else if (ch > UNI_MAX_UTF16) {
        if (flags == strictConversion) {
        result = sourceIllegal;
        source -= (extraBytesToRead+1); /* return to the start */
        break; /* Bail out; shouldn't continue */
        } else {
        *target++ = UNI_REPLACEMENT_CHAR;
        }
    } else {
        /* target is a character in range 0xFFFF - 0x10FFFF. */
        if ((uintptr_t)target + 1 >= (uintptr_t)targetEnd) {
        source -= (extraBytesToRead+1); /* Back up source pointer! */
        result = targetExhausted; break;
        }
        ch -= halfBase;
        if (targetStart)
        {
          *target++ = (UTF16)((ch >> halfShift) + UNI_SUR_HIGH_START);
          *target++ = (UTF16)((ch & halfMask) + UNI_SUR_LOW_START);
        }
        else
          target += 2;
    }
    }

    unsigned int length = unsigned(target - targetStart);
    return length;
}

  unsigned int utf82unicode(const char * line, wchar_t * wline, unsigned int n){
    if (!line){
      if (wline) wline[0]=0;
      return 0;
    }

    unsigned int j = ConvertUTF8toUTF16 (
      (const UTF8*) line,((line + n) < line) ? (const UTF8*)~0 : (const UTF8*)(line + n),
      (UTF16*)wline, (UTF16*)~0,
      lenientConversion);

    if (wline) wline[j] = 0;

    return j;
  }

    // convert position n in utf8-encoded line into the corresponding position 
  // in the same string encoded with unicode 
  unsigned int utf8pos2unicodepos(const char * line,unsigned int n,bool skip_added_spaces){
    if (!line) return 0;
    unsigned int i=0,j=0,c;
    for (;i<n;i++){
      c=line[i];
      if (!c)
	return j;
      if ( (c & 0xc0) == 0x80)
	continue;
      if (c < 128){ // 0/xxxxxxx/
	j++;
	continue;
      }
      if ( (c & 0xe0) == 0xc0) { // 2 char code 110/xxxxx/ 10/xxxxxx/
	i++;
	c = (c & 0x1f) << 6 | (line[i] & 0x3f);
	j++;
	continue;
      } 
      if ( (c & 0xf0) == 0xe0) { // 3 char 1110/xxxx/ 10/xxxxxx/ 10/xxxxxx/
	i++;
	c = (c & 0x0f) << 6 | (line[i] & 0x3f);
	i++;
	c = c << 6 | (line[i] & 0x3f);
	j++;
	if (skip_added_spaces) {
	  unsigned int masked = c & 0xff00; // take care of spaces that were added
	  if (masked>=0x2000 && masked<0x2c00)
	    j -= 2;
	}
	continue;
      } 
      if ( (c & 0xf8) == 0xf0) { // 4 char 11110/xxx/ 10/xxxxxx/ 10/xxxxxx/ 10/xxxxxx/
	i++;
	c = (c & 0x07) << 6 | (line[i] & 0x3f);
	i++;
	c = c << 6 | (line[i] & 0x3f);
	i++;
	c = c << 6 | (line[i] & 0x3f);
	j++;
	continue;
      } 
      // FIXME complete for 5 and 6 char
      c = 0xfffd;
      j++;
    }
    return j;
  }

  unsigned int wstrlen(const char * line, unsigned int n){
    if (!line) return 0;
    return utf82unicode(line, NULL, n);
  }

  // convert UTF8 string to unicode, allocate memory with new
  wchar_t * utf82unicode(const char * idname){
    if (!idname)
      return 0;
    int l=int(strlen(idname));
    wchar_t * wname=new wchar_t[l+1];
    utf82unicode(idname,wname,l);
    return wname;
  }

#ifdef NSPIRE
  unsigned wcslen(const wchar_t * c){
    unsigned i=0;
    for (;*c;++i)
      ++c;
    return i;
  }
#endif

  char * unicode2utf8(const wchar_t * idname){
    if (!idname)
      return 0;
    int l=int(wcslen(idname));
    char * name=new char[4*l+1];
    unicode2utf8(idname,name,l);
    return name;
  }

  unsigned int wstrlen(const wchar_t * wline){
    if (!wline)
      return 0;
    unsigned int i=0;
    for (;*wline;wline++){ i++; }
    return i;
  }

  // return length required to translate from unicode to UTF8
  unsigned int utf8length(const wchar_t * wline){
    return unicode2utf8(wline,0,wstrlen(wline));
  }

  unsigned int unicode2utf8(const wchar_t * wline,char * line,unsigned int n){
    if (!wline){
      if (line) line[0]=0;
      return 0;
    }

    unsigned int j = ConvertUTF16toUTF8(
      (UTF16*)wline, ((wline + n) < wline) ? (const UTF16*)~0 : (const UTF16*)(wline + n),
      (UTF8*)line, (UTF8*)-1,
      lenientConversion);

    if (line) line[j]=0;

    return j;
  }

  // Binary archive format for a gen:
  // 8 bytes=the gen itself (i.e. type, subtype, etc.)
  // Additionnally for pointer types
  // 4 bytes = total size of additionnal data
  // _CPLX: both real and imaginary parts
  // _FRAC: numerator and denominator
  // _MOD: 2 gens
  // _REAL, _ZINT: long int/real binary archive
  // _VECT: 4 bytes = #rows #cols (#cols=0 if not a matrix) + list of elements
  // _SYMB: feuille + sommet
  // _FUNC: 2 bytes = -1 + string or index
  // _IDNT or _STRNG: the name
  // count number of bytes required to save g in a file
  static size_t countfunction(void const* p, size_t nbBytes,size_t NbElements, void *file)
  {
    (*(unsigned *)file)+= unsigned(nbBytes*NbElements);
    return nbBytes*NbElements;
  }
  unsigned archive_count(const gen & g,GIAC_CONTEXT){
    unsigned size= 0;
    archive_save((void*)&size, g, countfunction, contextptr, true);
    return size;
  }

  /*
  unsigned archive_count(const gen & g,GIAC_CONTEXT){
    if (g.type<=_DOUBLE_ || g.type==_FLOAT_)
      return sizeof(gen);
    if (g.type==_CPLX)
      return sizeof(gen)+sizeof(unsigned)+archive_count(*g._CPLXptr,contextptr)+archive_count(*(g._CPLXptr+1),contextptr);
    if (g.type==_REAL || g.type==_ZINT)
      return sizeof(gen)+sizeof(unsigned)+g.print(contextptr).size();
    if (g.type==_FRAC)
      return sizeof(gen)+sizeof(unsigned)+archive_count(g._FRACptr->num,contextptr)+archive_count(g._FRACptr->den,contextptr);
    if (g.type==_MOD)
      return sizeof(gen)+sizeof(unsigned)+archive_count(*g._MODptr,contextptr)+archive_count(*(g._MODptr+1),contextptr);
    if (g.type==_VECT){
      unsigned res=sizeof(gen)+sizeof(unsigned)+4;
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it)
	res += archive_count(*it,contextptr);
      return res;
    }
    if (g.type==_SYMB){
      if (archive_function_index(g._SYMBptr->sommet)) // ((equalposcomp(archive_function_tab(),g._SYMBptr->sommet))
	return sizeof(gen)+sizeof(unsigned)+sizeof(short)+archive_count(g._SYMBptr->feuille,contextptr);
      return sizeof(gen)+sizeof(unsigned)+sizeof(short)+archive_count(g._SYMBptr->feuille,contextptr)+strlen(g._SYMBptr->sommet.ptr()->s);
    }
    if (g.type==_IDNT)
      return sizeof(gen)+sizeof(unsigned)+strlen(g._IDNTptr->id_name);
    if (g.type==_FUNC){
      if (archive_function_index(*g._FUNCptr)) // (equalposcomp(archive_function_tab(),*g._FUNCptr))
	return sizeof(gen)+sizeof(unsigned)+sizeof(short);
      return sizeof(gen)+sizeof(unsigned)+sizeof(short)+strlen(g._FUNCptr->ptr()->s);
    }
    return sizeof(gen)+sizeof(unsigned)+strlen(g.print().c_str()); // not handled
  }
  */

#define DBG_ARCHIVE 0

  bool archive_save(void * f,const gen & g,size_t writefunc(void const* p, size_t nbBytes,size_t NbElements, void *file),GIAC_CONTEXT, bool noRecurse){
    // write the gen first
    writefunc(&g,sizeof(gen),1,f);
    if (g.type<=_DOUBLE_ || g.type==_FLOAT_)
      return true;
    // heap allocated object, find size
    unsigned size=0;
    if (!noRecurse) size=archive_count(g,contextptr);
    writefunc(&size,sizeof(unsigned),1,f);
    if (g.type==_CPLX)
      return archive_save(f,*g._CPLXptr,writefunc,contextptr,noRecurse) && archive_save(f,*(g._CPLXptr+1),writefunc,contextptr,noRecurse);
    if (g.type==_MOD)
      return archive_save(f,*g._MODptr,writefunc,contextptr,noRecurse) && archive_save(f,*(g._MODptr+1),writefunc,contextptr,noRecurse);
    if (g.type==_FRAC)
      return archive_save(f,g._FRACptr->num,writefunc,contextptr,noRecurse) && archive_save(f,g._FRACptr->den,writefunc,contextptr,noRecurse);
    if (g.type==_VECT){
      unsigned short rows=g._VECTptr->size(),cols=0;
      if (ckmatrix(g))
	cols=g._VECTptr->front()._VECTptr->size();
      writefunc(&rows,sizeof(short),1,f);
      writefunc(&cols,sizeof(short),1,f);
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it){
	if (!archive_save(f,*it,writefunc,contextptr,noRecurse))
	  return false;
      }
      return true;
    }
    if (g.type==_IDNT){
#if DBG_ARCHIVE
      std::ofstream ofs;
      ofs.open ("e:\\tmp\\logsave", std::ofstream::out | std::ofstream::app);
      ofs << "IDNT " << g << endl;
      ofs.close();
#endif
      // fprintf(f,"%s",g._IDNTptr->id_name);
      writefunc(g._IDNTptr->id_name,1,strlen(g._IDNTptr->id_name),f);
      return true;
    }
    if (g.type==_SYMB){
      if (!archive_save(f,g._SYMBptr->feuille,writefunc,contextptr,noRecurse))
	return false;
#if DBG_ARCHIVE
      std::ofstream ofs;
      ofs.open ("e:\\tmp\\logsave", std::ofstream::out | std::ofstream::app);
      ofs << "SYMB " << g << endl;
      ofs.close();
#endif
      short i=archive_function_index(g._SYMBptr->sommet); // equalposcomp(archive_function_tab(),g._SYMBptr->sommet);
      writefunc(&i,sizeof(short),1,f);
      if (i)
	return true;
      // fprintf(f,"%s",g._SYMBptr->sommet.ptr()->s);
      writefunc(g._SYMBptr->sommet.ptr()->s,1,strlen(g._SYMBptr->sommet.ptr()->s),f);
      return true;
    }
    if (g.type==_FUNC){
      short i=archive_function_index(*g._FUNCptr); // equalposcomp(archive_function_tab(),*g._FUNCptr);
      writefunc(&i,sizeof(short),1,f);
      if (!i){
	// fprintf(f,"%s",g._FUNCptr->ptr()->s);
	writefunc(g._FUNCptr->ptr()->s,1,strlen(g._FUNCptr->ptr()->s),f);
      }
      return true;
    }
    string s;
    if (g.type==_ZINT)
      s=hexa_print_ZINT(*g._ZINTptr);
    else
      s=g.print(contextptr);
    // fprintf(f,"%s",s.c_str());
    writefunc(s.c_str(),1,s.size(),f);
    return true;
    //return false;
  }


  bool archive_save(void * f,const gen & g,GIAC_CONTEXT){
    return archive_save(f,g,(size_t (*)(void const* p, size_t nbBytes,size_t NbElements, void *file))fwrite,contextptr);
  }

#ifdef GIAC_HAS_STO_38
  // return true/false to tell if s is recognized. return the appropriate gen if true
  int lexerCompare(void const *a, void const *b) { 
    charptr_gen_unary const * ptr=(charptr_gen_unary const *)b;
    const char * aptr=(const char *) a;
    return strcmp(aptr, ptr->s); 
  }

  bool casbuiltin(const char *s, gen &g){
    // binary search in builtin_lexer_functions
#if 1 // def SMARTPTR64
    int n=builtin_lexer_functions_number; 
    charptr_gen_unary const * f = (charptr_gen_unary const *)bsearch(s, builtin_lexer_functions, n, sizeof(builtin_lexer_functions[0]), lexerCompare);
    if (f != NULL) {
      g = 0;
      int pos = int(f - builtin_lexer_functions);
      size_t val = builtin_lexer_functions_[pos];
      unary_function_ptr * at_val = (unary_function_ptr *)val;
      g = at_val;
      if (builtin_lexer_functions[pos]._FUNC_%2){
#ifdef SMARTPTR64
	unary_function_ptr tmp=*at_val;
	tmp._ptr+=1;
	g=tmp;
#else
	g._FUNC_ +=1;
#endif // SMARTPTR64
      }
      return true;
    }
#else
    charptr_gen_unary const * f = (charptr_gen_unary const *)bsearch(s, builtin_lexer_functions, builtin_lexer_functions_number, sizeof(builtin_lexer_functions[0]), lexerCompare);
    if (f != NULL) {
      g = gen(0);
      int pos=f - builtin_lexer_functions;
      *(size_t *)(&g) = builtin_lexer_functions_[pos] + f->_FUNC_;
      g = gen(*g._FUNCptr);
      return true;
    }
#endif
    return false;
  }

#endif

  // restore a gen from an opened file
  gen archive_restore(void * f,size_t readfunc(void * p, size_t nbBytes,size_t NbElements, void *file),GIAC_CONTEXT){
    gen g;
    if (!readfunc(&g,sizeof(gen),1,f))
      return undef;
    if (g.type<=_DOUBLE_ || g.type==_FLOAT_)
      return g;
    unsigned char t=g.type;
    signed char s=g.subtype;
    g.type=0; // required to avoid destructor of g to mess up the pointer part
    unsigned size;
    if (!readfunc(&size,sizeof(unsigned),1,f))
      return undef;
    if (t==_CPLX || t==_MOD || t==_FRAC){
      gen g1=archive_restore(f,readfunc,contextptr);
      gen g2=archive_restore(f,readfunc,contextptr);
      if (t==_CPLX)
	return g1+cst_i*g2;
      if (t==_FRAC)
	return fraction(g1,g2);
      if (t==_MOD)
	return makemodquoted(g1,g2);
    }
    size -= sizeof(gen)+sizeof(unsigned); // adjust for gen size and length
    if (t==_VECT){
      unsigned short rows,cols;
      if (!readfunc(&rows,sizeof(unsigned short),1,f))
	return undef;
      if (!readfunc(&cols,sizeof(unsigned short),1,f))
	return undef;
//      if (!rows) return undef;
      vecteur v(rows);
      for (int i=0;i<rows;++i)
	v[i]=archive_restore(f,readfunc,contextptr);
      return gen(v,s);
    }
    if (t==_IDNT){
      char * ch=new char[size+1];
      ch[size]=0;
      if (readfunc(ch,1,size,f)!=size){
	delete [] ch;
	return undef;
      }
      string sch(ch); gen res;
      delete [] ch;
      lock_syms_mutex();
      sym_string_tab::const_iterator it=syms().find(sch),itend=syms().end();
      if (it!=itend)
	res=it->second;
      else {
	res=identificateur(sch);
	syms()[sch]=res;
      }
      unlock_syms_mutex();  
#if DBG_ARCHIVE
      std::ofstream ofs;
      ofs.open ("e:\\tmp\\logrestore", std::ofstream::out | std::ofstream::app);
      ofs << "IDNT " << res << endl;
      ofs.close();
#endif
      return res;
    }
    if (t==_SYMB){
      gen fe=archive_restore(f,readfunc,contextptr);
      short index;
      if (!readfunc(&index,sizeof(short),1,f))
	return undef;
      if (index>0){
	const unary_function_ptr * aptr=archive_function_tab();
	if (index<archive_function_tab_length){
	  g=symbolic(aptr[index-1],fe);
	}
	else {
#if DBG_ARCHIVE
	  std::ofstream ofs;
	  ofs.open ("e:\\tmp\\logrestore", std::ofstream::out | std::ofstream::app);
	  ofs << "archive_restore error _SYMB index " << index << endl;
	  ofs.close();
#endif
	  g=fe; // ERROR
	}
      }
      else {
	gen res;
	size -= archive_count(fe,contextptr)+sizeof(short);
	char * ch=new char[size+3];
	if (0 && abs_calc_mode(contextptr)==38){
	  ch[0]='\'';
	  ch[size+1]='\'';
	  ch[size+2]=0;
	  if (readfunc(ch+1,1,size,f)!=size){
	    delete [] ch;
	    return undef;
	  }
	}
	else {
	  ch[size]=0;
	  if (readfunc(ch,1,size,f)!=size){
	    delete [] ch;
	    return undef;
	  }
#ifdef NSPIRE
	  if (builtin_lexer_functions_()){
	    std::pair<charptr_gen *,charptr_gen *> p=equal_range(builtin_lexer_functions_begin(),builtin_lexer_functions_end(),std::pair<const char *,gen>(ch,0),tri);
	    if (p.first!=p.second && p.first!=builtin_lexer_functions_end()){
	      res = p.first->second;
	      res.subtype=1;
	      res=gen(int((*builtin_lexer_functions_())[p.first-builtin_lexer_functions_begin()]+p.first->second.val));
	      res=gen(*res._FUNCptr);
	    }
	  }
#else	  
	  if (builtin_lexer_functions_){
#ifdef GIAC_HAS_STO_38
	    if (!casbuiltin(ch,res)){
#if DBG_ARCHIVE
	      std::ofstream ofs;
	      ofs.open ("e:\\tmp\\logrestore", std::ofstream::out | std::ofstream::app);
	      ofs << "archive_restore error _SYMB " << ch << endl;
	      ofs.close();
#endif
	      res=0;
	    }
#else
	    std::pair<charptr_gen *,charptr_gen *> p=equal_range(builtin_lexer_functions_begin(),builtin_lexer_functions_end(),std::pair<const char *,gen>(ch,0),tri);
	    if (p.first!=p.second && p.first!=builtin_lexer_functions_end()){
	      res = p.first->second;
	      res.subtype=1;
	      res=gen(int(builtin_lexer_functions_[p.first-builtin_lexer_functions_begin()]+p.first->second.val));
	      res=gen(*res._FUNCptr);
	    }
#endif
	  }
#endif 
	}
	if (is_zero(res)){
#if DBG_ARCHIVE
	  std::ofstream ofs;
	  ofs.open ("e:\\tmp\\logrestore", std::ofstream::out | std::ofstream::app);
	  ofs << "archive_restore error _SYMB 0 " << ch << endl;
	  ofs.close();
#endif
	  res=gen(ch,contextptr);
	}
	delete [] ch;
	if (res.type!=_FUNC){
	  return undef;
	}
	g=symbolic(*res._FUNCptr,fe);
      }
      g.subtype=s;
#if DBG_ARCHIVE
      std::ofstream ofs;
      ofs.open ("e:\\tmp\\logrestore", std::ofstream::out | std::ofstream::app);
      ofs << "SYMB " << g << endl;
      ofs.close();
#endif
      return g;
    }
    if (t==_FUNC){
      short index;
      if (!readfunc(&index,sizeof(short),1,f))
	return undef;
      if (index>0)
	g = archive_function_tab()[index-1];
      else {
	size -= sizeof(short);
	char * ch=new char[size+1];
	ch[size]=0;
	if (readfunc(ch,1,size,f)!=size){
	  delete [] ch;
	  return undef;
	}
	g = gen(ch,contextptr);
	delete [] ch;
	if (g.type!=_FUNC)
	  return undef;
      }
      g.subtype=s;
      return g;
    }
    char * ch=new char[size+1];
    ch[size]=0;
    if (readfunc(ch,1,size,f)!=size){
      delete [] ch;
      return undef;
    }
    gen res;
    if (t==_STRNG)
      res=string2gen(ch,true);
    else
      res=gen(ch,contextptr);
    delete [] ch;
    return res;
  }

  gen archive_restore(FILE * f,GIAC_CONTEXT){
    return archive_restore(f,(size_t (*)(void * p, size_t nbBytes,size_t NbElements, void *file))fread,contextptr);
  }

  void init_geogebra(bool on,GIAC_CONTEXT){
    setlocale(LC_NUMERIC,"POSIX");
    _decimal_digits_=on?13:12;
    _all_trig_sol_=on;
    _withsqrt_=!on;
    _calc_mode_=on?1:0;
    _eval_equaltosto_=on?0:1;
    eval_equaltosto(on?0:1,contextptr);
    decimal_digits(on?13:12,contextptr);
    all_trig_sol(on,contextptr);
    withsqrt(!on,contextptr);
    calc_mode(on?1:0,contextptr);
    powlog2float=3e4;
    MPZ_MAXLOG2=33300;
#ifdef TIMEOUT
    //caseval_maxtime=5;
    caseval_n=0;
    caseval_mod=10;
#endif
  }

  vecteur giac_current_status(bool save_history,GIAC_CONTEXT){
    // cas and geo config
    vecteur res;
    if (abs_calc_mode(contextptr)==38)
      res.push_back(cas_setup(contextptr));
    else
      res.push_back(symbolic(at_cas_setup,cas_setup(contextptr)));
    res.push_back(xyztrange(gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,gnuplot_zmin,gnuplot_zmax,gnuplot_tmin,gnuplot_tmax,global_window_xmin,global_window_xmax,global_window_ymin,global_window_ymax,show_axes(contextptr),class_minimum,class_size,
#ifdef WITH_GNUPLOT
			    gnuplot_hidden3d,gnuplot_pm3d
#else
			    1,1
#endif
			    ));
    if (abs_calc_mode(contextptr)==38)
      res.back()=res.back()._SYMBptr->feuille;
    // session
    res.push_back(save_history?history_in(contextptr):vecteur(0));
    res.push_back(save_history?history_out(contextptr):vecteur(0));
    // user variables
    if (contextptr && contextptr->tabptr){
      sym_tab::const_iterator jt=contextptr->tabptr->begin(),jtend=contextptr->tabptr->end();
      for (;jt!=jtend;++jt){
	gen a=jt->second;
	gen b=identificateur(jt->first);
	res.push_back(symb_sto(a,b));
      }
    }
    else {
      lock_syms_mutex();  
      sym_string_tab::const_iterator it=syms().begin(),itend=syms().end();
      for (;it!=itend;++it){
	gen id=it->second;
	if (id.type==_IDNT && id._IDNTptr->value)
	  res.push_back(symb_sto(*id._IDNTptr->value,id));
      }
      unlock_syms_mutex();  
    }
    if (abs_calc_mode(contextptr)==38)
      res.push_back(xcas_mode(contextptr));
    else
      res.push_back(symbolic(at_xcas_mode,xcas_mode(contextptr)));
    return res;
  }

  bool unarchive_session(const gen & g,int level,const gen & replace,GIAC_CONTEXT,bool with_history){
    int l;
    if (g.type!=_VECT || (l=int(g._VECTptr->size()))<4)
      return false;
    vecteur v=*g._VECTptr;
    if (v[2].type!=_VECT || v[3].type!=_VECT || (v[2]._VECTptr->size()!=v[3]._VECTptr->size() && v[2]._VECTptr->size()!=v[3]._VECTptr->size()+1))
      return false;
    if (v[2]._VECTptr->size()==v[3]._VECTptr->size()+1)
      v[2]._VECTptr->pop_back();
#ifndef DONT_UNARCHIVE_HISTORY
    history_in(contextptr)=*v[2]._VECTptr;
    history_out(contextptr)=*v[3]._VECTptr;
#ifndef GNUWINCE
    if (v[0].type==_VECT)
      _cas_setup(v[0],contextptr);
    else
      protecteval(v[0],eval_level(contextptr),contextptr); 
    if (v[1].type==_VECT)
      _xyztrange(v[1],contextptr);
    else
      protecteval(v[1],eval_level(contextptr),contextptr); 
#endif
#endif
    // restore variables
    for (int i=4;i<l;++i)
      protecteval(v[i],eval_level(contextptr),contextptr); 
    // restore xcas_mode
    if (v.back().type==_INT_)
      xcas_mode(v.back().val,contextptr);
    if (!with_history)
      return true;
    // eval replace if level>=0
    if (level<0 || level>=l){
      history_in(contextptr).push_back(replace);
      history_out(contextptr).push_back(protecteval(replace,eval_level(contextptr),contextptr)); 
    }
    else {
      history_in(contextptr)[level]=replace;
      for (int i=level;i<l;++i)
	history_out(contextptr)[i]=protecteval(history_in(contextptr)[i],eval_level(contextptr),contextptr); 
    }
    return true;
  }

  const char * const do_not_autosimplify[]={
    "Factor",
    "Gcd",
    "Int",
    "POLYFORM",
    "Quo",
    "Quorem",
    "Rem",
    "animate",
    "animation",
    "archive",
    "autosimplify",
    "canonical_form",
    "cfactor",
    "cpartfrac",
    "curve",
    "developper",
    "diff",
    "domain",
    "element",
    "evalc",
    "expand",
    "expexpand",
    "factor",
    "factoriser",
    "factoriser_entier",
    "factoriser_sur_C",
    "ifactor",
    "lncollect",
    "lnexpand",
    "mathml",
    "mult_c_conjugate",
    "mult_conjugate",
    "nodisp",
    "normal",
    "op",
    "partfrac",
    "plotfield",
    "plotfunc",
    "plotparam",
    "plotpolar",
    "pow2exp",
    "powexpand",
    "propfrac",
    "quote",
    "regroup",
    "reorder",
    "series",
    "simplifier",
    "simplify",
    "tabvar",
    "taylor",
    "texpand",
    "trace",
    "trigexpand",
    0
  };

  int dichotomic_search(const char * const * tab,unsigned tab_size,const char * s){
    int beg=0,end=tab_size,cur,test;
    // string index is always >= begin and < end
    for (;;){
      cur=(beg+end)/2;
      test=strcmp(s,tab[cur]);
      if (!test)
	return cur;
      if (cur==beg)
	return -1;
      if (test>0)
	beg=cur;
      else
	end=cur;
    }
    return -1;
  }

  gen add_autosimplify(const gen & g,GIAC_CONTEXT){
    if (g.type==_VECT)
      return apply(g,add_autosimplify,contextptr);
    if (g.type==_SYMB){
      if (g._SYMBptr->sommet==at_program)
	return g;
#ifdef GIAC_HAS_STO_38
      const char * c=g._SYMBptr->sommet.ptr()->s;
#else
      string ss=unlocalize(g._SYMBptr->sommet.ptr()->s);
      const char * c=ss.c_str();
#endif
#if 1
      if (dichotomic_search(do_not_autosimplify,sizeof(do_not_autosimplify)/sizeof(char*)-1,c)!=-1)
	return g;
#else
      const char ** ptr=do_not_autosimplify;
      for (;*ptr;++ptr){
	if (!strcmp(*ptr,c))
	  return g;
      }
#endif
    }
    std::string s=autosimplify(contextptr);
    if (s.size()<1 || s=="'nop'")
      return g;
    gen a(s,contextptr);
    if (a.type==_FUNC)
      return symbolic(*a._FUNCptr,g);
    if (a.type>=_IDNT)
      return symb_of(a,g);
    return g;
  }

  bool csv_guess(const char * data,int count,char & sep,char & nl,char & decsep){
    bool ans=true;
    int nb[256],pointdecsep=0,commadecsep=0; 
    for (int i=0;i<256;++i)
      nb[i]=0;
    // count occurence of each char
    // and detect decimal separator between . or ,
    for (int i=1;i<count-1;++i){
      if (data[i]=='[' || data[i]==']')
	ans=false;
      ++nb[(unsigned char) data[i]];
      if (data[i-1]>='0' && data[i-1]<='9' && data[i+1]>='0' && data[i+1]<='9'){
	if (data[i]=='.')
	  ++pointdecsep;
	if (data[i]==',')
	  ++commadecsep;
      }
    }
    decsep=commadecsep>pointdecsep?',':'.';
    // detect nl (ctrl-M or ctrl-J)
    nl=nb[10]>nb[13]?10:13;
    // find in control characters and : ; the most used (except 10/13)
    int nbmax=0,imax=-1;
    for (int i=0;i<60;++i){
      if (i==10 || i==13 || (i>=' ' && i<='9') )
	continue;
      if (nb[i]>nbmax){
	imax=i;
	nbmax=nb[i];
      }
    }
    // compare . with , (44)
    if (nb[unsigned(',')] && nb[unsigned(',')]>=nbmax){
      imax=',';
      nbmax=nb[unsigned(',')];
    }
    if (nbmax && nbmax>=nb[unsigned(nl)] && imax!=decsep)
      sep=imax;
    else
      sep=' ';
    return ans;
  }

  void (*my_gprintf)(unsigned special,const string & format,const vecteur & v,GIAC_CONTEXT)=0;


#ifdef EMCC
  static void newlinestobr(string &s,const string & add){
    int l=int(add.size());
    for (int i=0;i<l;++i){
      if (add[i]=='\n')
	s+="<br>";
      else
	s+=add[i];
    }
  }
#else
  static void newlinestobr(string &s,const string & add){
    s+=add;
  }
#endif

  void gprintf(unsigned special,const string & format,const vecteur & v,GIAC_CONTEXT){
    return gprintf(special,format,v,step_infolevel(contextptr),contextptr);
  }

  void gprintf(unsigned special,const string & format,const vecteur & v,int step_info,GIAC_CONTEXT){
    if (step_info==0)
      return;
    if (my_gprintf){
      my_gprintf(special,format,v,contextptr);
      return;
    }
    string s;
    int pos=0;
#ifdef EMCC
    *logptr(contextptr) << char(2) << endl; // start mixed text/mathml
#endif
    for (unsigned i=0;i<v.size();++i){
      int p=int(format.find("%gen",pos));
      if (p<0 || p>=int(format.size()))
	break;
      newlinestobr(s,format.substr(pos,p-pos));
#ifdef EMCC
      gen tmp;
      if (v[i].is_symb_of_sommet(at_pnt))
	tmp=_svg(v[i],contextptr);
      else
	tmp=_mathml(makesequence(v[i],1),contextptr);
      s = s+((tmp.type==_STRNG)?(*tmp._STRNGptr):v[i].print(contextptr));
#else
      s += v[i].print(contextptr);
#endif
      pos=p+4;
    }
    newlinestobr(s,format.substr(pos,format.size()-pos));
    *logptr(contextptr) << s << endl;
#ifdef EMCC
    *logptr(contextptr) << char(3) << endl; // end mixed text/mathml
    *logptr(contextptr) << endl;
#endif
  }

  void gprintf(const string & format,const vecteur & v,GIAC_CONTEXT){
    gprintf(step_nothing_special,format,v,contextptr);
  }

  void gprintf(const string & format,const vecteur & v,int step_info,GIAC_CONTEXT){
    gprintf(step_nothing_special,format,v,step_info,contextptr);
  }

  // moved from input_lexer.ll for easier debug
  const char invalid_name[]="Invalid name";

#ifdef USTL    
  // void update_lexer_localization(const std::vector<int> & v,ustl::map<std::string,std::string> &lexer_map,ustl::multimap<std::string,giac::localized_string> &back_lexer_map){}
#else
  vecteur * keywords_vecteur_ptr(){
    static vecteur v;
    return &v;
  }

  static void in_update_lexer_localization(istream & f,int lang,const std::vector<int> & v,std::map<std::string,std::string> &lexer_map,std::multimap<std::string,giac::localized_string> &back_lexer_map,GIAC_CONTEXT){
    char * line = (char *)malloc(1024);
    std::string giac_kw,local_kw;
    size_t l;
    for (;;){
      f.getline(line,1023,'\n');
      l=strlen(line);
      if (f.eof()){
	break;
      }
      if (l>3 && line[0]!='#'){
	if (line[l-1]=='\n')
	  --l;
	// read giac keyword
	size_t j;
	giac_kw="";
	for (j=0;j<l;++j){
	  if (line[j]==' ')
	    break;
	  giac_kw += line[j];
	}
	// read corresponding local keywords
	local_kw="";
	vecteur * keywordsptr=keywords_vecteur_ptr();
	for (++j;j<l;++j){
	  if (line[j]==' '){
	    if (!local_kw.empty()){
#ifdef EMCC
	      gen localgen(gen(local_kw,contextptr));
	      keywordsptr->push_back(localgen);
	      sto(gen(giac_kw,contextptr),localgen,contextptr);
#else
	      lexer_map[local_kw]=giac_kw;
	      back_lexer_map.insert(pair<string,localized_string>(giac_kw,localized_string(lang,local_kw)));
#endif
	    }
	    local_kw="";
	  }
	  else
	    local_kw += line[j];
	}
	if (!local_kw.empty()){
#ifdef EMCC
	  gen localgen(gen(local_kw,contextptr));
	  keywordsptr->push_back(localgen);
	  sto(gen(giac_kw,contextptr),localgen,contextptr);
#else
	  lexer_map[local_kw]=giac_kw;
	  back_lexer_map.insert(pair<string,localized_string>(giac_kw,localized_string(lang,local_kw)));
#endif
	}
      }
    }
    free(line);
  }
	    
  void update_lexer_localization(const std::vector<int> & v,std::map<std::string,std::string> &lexer_map,std::multimap<std::string,giac::localized_string> &back_lexer_map,GIAC_CONTEXT){
    lexer_map.clear();
    back_lexer_map.clear();
    int s=int(v.size());
    for (int i=0;i<s;++i){
      int lang=v[i];
      if (lang>=1 && lang<=4){
	std::string doc=find_doc_prefix(lang);
	std::string file=giac::giac_aide_dir()+doc+"keywords";
	//COUT << "keywords " << file << endl;
	ifstream f(file.c_str());
	if (f.good()){
	  in_update_lexer_localization(f,lang,v,lexer_map,back_lexer_map,contextptr);
	  // COUT << "// Using keyword file " << file << endl;
	} // if (f)
	else {
	  if (lang==1){
#ifdef HAVE_SSTREAM
      istringstream f(
#else
      istrstream f(
#endif
		   "# enter couples\n# giac_keyword translation\n# for example, to define integration as a translation for integrate \nintegrate integration\neven est_pair\nodd est_impair\n# geometry\nbarycenter barycentre\nisobarycenter isobarycentre\nmidpoint milieu\nline_segments aretes\nmedian_line mediane\nhalf_line demi_droite\nparallel parallele\nperpendicular perpendiculaire\ncommon_perpendicular perpendiculaire_commune\nenvelope enveloppe\nequilateral_triangle triangle_equilateral\nisosceles_triangle triangle_isocele\nright_triangle triangle_rectangle\nlocus lieu\ncircle cercle\nconic conique\nreduced_conic conique_reduite\nquadric quadrique\nreduced_quadric quadrique_reduite\nhyperbola hyperbole\ncylinder cylindre\nhalf_cone demi_cone\nline droite\nplane plan\nparabola parabole\nrhombus losange\nsquare carre\nhexagon hexagone\npyramid pyramide\nquadrilateral quadrilatere\nparallelogram parallelogramme\northocenter orthocentre\nexbisector exbissectrice\nparallelepiped parallelepipede\npolyhedron polyedre\ntetrahedron tetraedre\ncentered_tetrahedron tetraedre_centre\ncentered_cube cube_centre\noctahedron octaedre\ndodecahedron dodecaedre\nicosahedron icosaedre\nbisector bissectrice\nperpen_bisector mediatrice\naffix affixe\naltitude hauteur\ncircumcircle circonscrit\nexcircle exinscrit\nincircle inscrit\nis_prime est_premier\nis_equilateral est_equilateral\nis_rectangle est_rectangle\nis_parallel est_parallele\nis_perpendicular est_perpendiculaire\nis_orthogonal est_orthogonal\nis_collinear est_aligne\nis_concyclic est_cocyclique\nis_element est_element\nis_included est_inclus\nis_coplanar est_coplanaire\nis_isosceles est_isocele\nis_square est_carre\nis_rhombus est_losange\nis_parallelogram est_parallelogramme\nis_conjugate est_conjugue\nis_harmonic_line_bundle est_faisceau_droite\nis_harmonic_circle_bundle est_faisceau_cercle\nis_inside est_dans\narea aire\nperimeter perimetre\ndistance longueur\ndistance2 longueur2\nareaat aireen\nslopeat penteen\nangleat angleen\nperimeterat perimetreen\ndistanceat distanceen\nareaatraw aireenbrut\nslopeatraw penteenbrut\nangleatraw angleenbrut\nperimeteratraw perimetreenbrut\ndistanceatraw distanceenbrut\nextract_measure extraire_mesure\ncoordinates coordonnees\nabscissa abscisse\nordinate ordonnee\ncenter centre\nradius rayon\npowerpc puissance\nvertices sommets\npolygon polygone\nisopolygon isopolygone\nopen_polygon polygone_ouvert\nhomothety homothetie\nsimilarity similitude\n# affinity affinite\nreflection symetrie\nreciprocation polaire_reciproque\nscalar_product produit_scalaire\n# solid_line ligne_trait_plein\n# dash_line ligne_tiret\n# dashdot_line ligne_tiret_point\n# dashdotdot_line ligne_tiret_pointpoint\n# cap_flat_line ligne_chapeau_plat\n# cap_round_line ligne_chapeau_rond\n# cap_square_line ligne_chapeau_carre\n# line_width_1 ligne_epaisseur_1\n# line_width_2 ligne_epaisseur_2\n# line_width_3 ligne_epaisseur_3\n# line_width_4 ligne_epaisseur_4\n# line_width_5 ligne_epaisseur_5\n# line_width_6 ligne_epaisseur_6\n# line_width_7 ligne_epaisseur_7\n# line_width_8 ligne_epaisseur_8\n# rhombus_point point_losange\n# plus_point point_plus\n# square_point point_carre\n# cross_point point_croix\n# triangle_point point_triangle\n# star_point point_etoile\n# invisible_point point_invisible\ncross_ratio birapport\nradical_axis axe_radical\npolar polaire\npolar_point point_polaire\npolar_coordinates coordonnees_polaires\nrectangular_coordinates coordonnees_rectangulaires\nharmonic_conjugate conj_harmonique\nharmonic_division div_harmonique\ndivision_point point_div\n# harmonic_division_point point_division_harmonique\ndisplay affichage\nvertices_abc sommets_abc\nvertices_abca sommets_abca\nline_inter inter_droite\nsingle_inter inter_unique\ncolor couleur\nlegend legende\nis_harmonic est_harmonique\nbar_plot diagramme_batons\nbarplot diagrammebatons\nhistogram histogramme\nprism prisme\nis_cospherical est_cospherique\ndot_paper papier_pointe\ngrid_paper papier_quadrille\nline_paper papier_ligne\ntriangle_paper papier_triangule\nvector vecteur\nplotarea tracer_aire\nplotproba graphe_probabiliste\nmult_c_conjugate mult_conjugue_C\nmult_conjugate mult_conjugue\ncanonical_form forme_canonique\nibpu integrer_par_parties_u\nibpdv integrer_par_parties_dv\nwhen quand\nslope pente\ntablefunc table_fonction\ntableseq table_suite\nfsolve resoudre_numerique\ninput saisir\nprint afficher\nassume supposons\nabout domaine\nbreakpoint point_arret\nwatch montrer\nrmwatch ne_plus_montrer\nrmbreakpoint suppr_point_arret\nrand alea\nInputStr saisir_chaine\nOx_2d_unit_vector vecteur_unitaire_Ox_2d\nOy_2d_unit_vector vecteur_unitaire_Oy_2d\nOx_3d_unit_vector vecteur_unitaire_Ox_3d\nOy_3d_unit_vector vecteur_unitaire_Oy_3d\nOz_3d_unit_vector vecteur_unitaire_Oz_3d\nframe_2d repere_2d\nframe_3d repere_3d\nrsolve resoudre_recurrence\nassume supposons\ncumulated_frequencies frequences_cumulees\nfrequencies frequences\nnormald loi_normale\nregroup regrouper\nosculating_circle cercle_osculateur\ncurvature courbure\nevolute developpee\nsort trier\n");
	    in_update_lexer_localization(f,1,v,lexer_map,back_lexer_map,contextptr);
	  }
	  else
	    CERR << "// Unable to find keyword file " << file << endl;
	}
      }
    }
  }
#endif

#ifndef NSPIRE

#include "input_parser.h" 

    bool has_special_syntax(const char * s){
#ifdef USTL
      ustl::pair<charptr_gen *,charptr_gen *> p=
	ustl::equal_range(builtin_lexer_functions_begin(),builtin_lexer_functions_end(),
		    std::pair<const char *,gen>(s,0),
		    tri);
#else
      std::pair<charptr_gen *,charptr_gen *> p=
	equal_range(builtin_lexer_functions_begin(),builtin_lexer_functions_end(),
		    std::pair<const char *,gen>(s,0),
		    tri);
#endif
      if (p.first!=p.second && p.first!=builtin_lexer_functions_end())
	return (p.first->second.subtype!=T_UNARY_OP-256);
      map_charptr_gen::const_iterator i = lexer_functions().find(s);
      if (i==lexer_functions().end())
	return false;
      return (i->second.subtype!=T_UNARY_OP-256);
    }
    
    bool lexer_functions_register(const unary_function_ptr & u,const char * s,int parser_token){
      map_charptr_gen::const_iterator i = lexer_functions().find(s);
      if (i!=lexer_functions().end())
	return false;
      if (doing_insmod)
	registered_lexer_functions().push_back(user_function(s,parser_token));
      if (!builtin_lexer_functions_sorted){
#ifndef STATIC_BUILTIN_LEXER_FUNCTIONS
#ifdef NSPIRE_NEWLIB
	builtin_lexer_functions_begin()[builtin_lexer_functions_number]=std::pair<const char *,gen>(s,gen(u));
#else
	builtin_lexer_functions_begin()[builtin_lexer_functions_number].first=s;
	builtin_lexer_functions_begin()[builtin_lexer_functions_number].second.type=0;
	builtin_lexer_functions_begin()[builtin_lexer_functions_number].second=gen(u);
#endif
	if (parser_token==1)
	  builtin_lexer_functions_begin()[builtin_lexer_functions_number].second.subtype=T_UNARY_OP-256;
	else
	  builtin_lexer_functions_begin()[builtin_lexer_functions_number].second.subtype=parser_token-256;
	builtin_lexer_functions_number++;
#endif
      }
      else {
	lexer_functions()[s] = gen(u);
	if (parser_token==1)
	  lexer_functions()[s].subtype=T_UNARY_OP-256;
	else
	  lexer_functions()[s].subtype=parser_token-256;
      }
      // If s is a library function name (with ::), update the library
      int ss=int(strlen(s)),j=0;
      for (;j<ss-1;++j){
	if (s[j]==':' && s[j+1]==':')
	  break;
      }
      if (j<ss-1){
	string S(s);
	string libname=S.substr(0,j);
	string funcname=S.substr(j+2,ss-j-2);
#ifdef USTL
	ustl::map<std::string,std::vector<string> >::iterator it=library_functions().find(libname);
#else
	std::map<std::string,std::vector<string> >::iterator it=library_functions().find(libname);
#endif
	if (it!=library_functions().end())
	  it->second.push_back(funcname);
	else
	  library_functions()[libname]=vector<string>(1,funcname);
      }
      return true;
    }

    bool lexer_function_remove(const vector<user_function> & v){
      vector<user_function>::const_iterator it=v.begin(),itend=v.end();
      map_charptr_gen::const_iterator i,iend;
      bool ok=true;
      for (;it!=itend;++it){
	i = lexer_functions().find(it->s.c_str());
	iend=lexer_functions().end();
	if (i==iend)
	  ok=false;
	else
	  lexer_functions().erase(it->s.c_str());
      }
      return ok;
    }

    int find_or_make_symbol(const string & s,gen & res,void * scanner,bool check38,GIAC_CONTEXT){
      int tmpo=opened_quote(contextptr);
      if (tmpo & 2)
	check38=false;
      if (s.size()==1){
#ifdef GIAC_HAS_STO_38
	if (0 && s[0]>='a' && s[0]<='z'){
	  index_status(contextptr)=1; 
	  res=*tab_one_letter_idnt[s[0]-'a'];
	  return T_SYMBOL;
	}
	if (check38 && s[0]>='a' && s[0]<='z' && calc_mode(contextptr)==38)
	  giac_yyerror(scanner,invalid_name);
#else
	if (s[0]>='a' && s[0]<='z'){
	  if (check38 && calc_mode(contextptr)==38)
	    giac_yyerror(scanner,invalid_name);
	  index_status(contextptr)=1; 
	  res=*tab_one_letter_idnt[s[0]-'a'];
	  return T_SYMBOL;
	}
#endif
	switch (s[0]){
	case '+':
	  res=at_plus;
	  return T_UNARY_OP;
	case '-':
	  res=at_neg;
	  return T_UNARY_OP;
	case '*':
	  res=at_prod;
	  return T_UNARY_OP;
	case '/':
	  res=at_division;
	  return T_UNARY_OP;
	case '^':
	  res=at_pow;
	  return T_UNARY_OP;
	}
      }
      string ts(s);
#ifdef USTL
      ustl::map<std::string,std::string>::const_iterator trans=lexer_localization_map().find(ts);
      if (trans!=lexer_localization_map().end())
	ts=trans->second;
      ustl::map<std::string,std::vector<string> >::const_iterator j=lexer_translator().find(ts);
      if (j!=lexer_translator().end() && !j->second.empty())
	ts=j->second.back();
      ustl::pair<charptr_gen *,charptr_gen *> p=ustl::equal_range(builtin_lexer_functions_begin(),builtin_lexer_functions_end(),std::pair<const char *,gen>(ts.c_str(),0),tri);
#else
      std::map<std::string,std::string>::const_iterator trans=lexer_localization_map().find(ts);
      if (trans!=lexer_localization_map().end())
	ts=trans->second;
      std::map<std::string,std::vector<string> >::const_iterator j=lexer_translator().find(ts);
      if (j!=lexer_translator().end() && !j->second.empty())
	ts=j->second.back();
      std::pair<charptr_gen *,charptr_gen *> p=equal_range(builtin_lexer_functions_begin(),builtin_lexer_functions_end(),std::pair<const char *,gen>(ts.c_str(),0),tri);
#endif
      if (p.first!=p.second && p.first!=builtin_lexer_functions_end()){
	if (p.first->second.subtype==T_TO-256)
	  res=plus_one;
	else
	  res = p.first->second;
	res.subtype=1;
	if (builtin_lexer_functions_){
#ifdef NSPIRE
	  res=gen(int((*builtin_lexer_functions_())[p.first-builtin_lexer_functions_begin()]+p.first->second.val));
	  res=gen(*res._FUNCptr);	  
#else
#ifndef NSPIRE_NEWLIB
	  res=0;
	  int pos=int(p.first-builtin_lexer_functions_begin());
	  size_t val=builtin_lexer_functions_[pos];
	  unary_function_ptr * at_val=(unary_function_ptr *)val;
	  res=at_val;
#ifdef GIAC_HAS_STO_38
	  if (builtin_lexer_functions[pos]._FUNC_%2){
#ifdef SMARTPTR64
	    unary_function_ptr tmp=*at_val;
	    tmp._ptr+=1;
	    res=tmp;
#else
	    res._FUNC_ +=1;
#endif // SMARTPTR64
	  }
#endif // GIAC_HAS_STO_38
#else // keep this code, required for the nspire otherwise evalf(pi)=reboot
	  res=gen(int(builtin_lexer_functions_[p.first-builtin_lexer_functions_begin()]+p.first->second.val));
	  res=gen(*res._FUNCptr);
#endif
#endif
	}
	index_status(contextptr)=(p.first->second.subtype==T_UNARY_OP-256);
	int token=p.first->second.subtype;
	token += (token<0)?512:256 ;	
	return token;
      }
      lexer_tab_int_type tst={ts.c_str(),0,0,0,0};
#ifdef USTL
      ustl::pair<const lexer_tab_int_type *,const lexer_tab_int_type *> pp = ustl::equal_range(lexer_tab_int_values,lexer_tab_int_values_end,tst,tri1);
#else
      std::pair<const lexer_tab_int_type *,const lexer_tab_int_type *> pp = equal_range(lexer_tab_int_values,lexer_tab_int_values_end,tst,tri1);
#endif
      if (pp.first!=pp.second && pp.first!=lexer_tab_int_values_end){
	index_status(contextptr)=pp.first->status;
	res=int(pp.first->value);
	res.subtype=pp.first->subtype;
	return pp.first->return_value;
      }
      map_charptr_gen::const_iterator i = lexer_functions().find(ts.c_str());
      if (i!=lexer_functions().end()){
	if (i->second.subtype==T_TO-256)
	  res=plus_one;
	else
	  res = i->second;
	res.subtype=1;
	index_status(contextptr)=(i->second.subtype==T_UNARY_OP-256);
	return i->second.subtype+256 ;
      }
      lock_syms_mutex();
      sym_string_tab::const_iterator i2 = syms().find(s),i2end=syms().end();
      if (i2 == i2end) {
	unlock_syms_mutex();  
	const char * S = s.c_str();
	// std::CERR << "lexer new" << s << endl;
	if (check38 && calc_mode(contextptr)==38 && strcmp(S,string_pi) && strcmp(S,string_euler_gamma) && strcmp(S,string_infinity) && strcmp(S,string_undef) && S[0]!='G'&& (!is_known_name_38 || !is_known_name_38(0,S))){
	  // detect invalid names and implicit multiplication 
	  size_t ss=strlen(S);
	  vecteur args;
	  for (size_t i=0;i<ss;++i){
	    char ch=S[i];
	    if (ch=='C' || (ch>='E' && ch<='H') || ch=='L' || ch=='M' || ch=='R'
		/* || ch=='S' */
		|| ch=='U' || ch=='V' || (ch>='X' && ch<='Z') ){
	      string name;
	      name += ch;
	      char c=0;
	      if (i<ss-1)
		c=s[i+1];
	      if (c>='0' && c<='9'){
		name += c;
		++i;
	      }
	      res = identificateur(name);
	      lock_syms_mutex();
	      syms()[name] = res;
	      unlock_syms_mutex();
	      args.push_back(res);
	    }
	    else {
	      string coeff;
	      for (++i;i<ss;++i){
		// up to next alphabetic char
		if (s[i]>32 && isalpha(s[i])){
		  --i;
		  break;
		}
		if (scanner && (s[i]<0 || s[i]>'z')){
		  giac_yyerror(scanner,invalid_name);
		  res=undef;
		  return T_SYMBOL;
		}
		coeff += s[i];
	      }
	      if (coeff.empty())
		res=1;
	      else
		res=atof(coeff.c_str());
	      if (ch=='i')
		res=res*cst_i;
	      else {
		if (ch=='e')
		  res=std::exp(1.0)*res;
		else {
		  // Invalid ident name, report error
		  if ( (ch>'Z' || ch<0) && scanner){
		    giac_yyerror(scanner,invalid_name);
		    res=undef;
		    return T_SYMBOL;
		  }
		  coeff=string(1,ch);
		  gen tmp = identificateur(coeff);
		  // syms()[coeff.c_str()]=tmp;
		  res=res*tmp;
		}
	      }
	      args.push_back(res);
	    }
	  }
	  if (args.size()==1)
	    res=args.front();
	  else 
	    res=_prod(args,contextptr);
	  lock_syms_mutex();
	  syms()[s]=res;
	  unlock_syms_mutex();
	  return T_SYMBOL;
	} // end 38 compatibility mode
	res = identificateur(s);
	lock_syms_mutex();
	syms()[s] = res;
	unlock_syms_mutex();
	return T_SYMBOL;
      } // end if ==syms.end()
      res = i2->second;
      unlock_syms_mutex();  
      return T_SYMBOL;
    }

  // Add to the list of predefined symbols
  void set_lexer_symbols(const vecteur & l,GIAC_CONTEXT){
    if (initialisation_done(contextptr))
      return;
    initialisation_done(contextptr)=true;
    const_iterateur it=l.begin(),itend=l.end();
    for (; it!=itend; ++it) {
      if (it->type!=_IDNT)
	continue;
      lock_syms_mutex();
      sym_string_tab::const_iterator i = syms().find(it->_IDNTptr->id_name),iend=syms().end();
      if (i==iend)
	syms()[it->_IDNTptr->name()] = *it;
      unlock_syms_mutex();  
    }
  }

  static string remove_comment(const string & s,const string &pattern,bool replace){
    string res(s);
    for (;;){
      int pos1=res.find(pattern);
      if (pos1<0 || pos1+3>=int(res.size()))
	break;
      int pos2=res.find(pattern,pos1+3);
      if (pos2<0 || pos2+3>=int(res.size()))
	break;
      if (replace)
	res=res.substr(0,pos1)+'"'+res.substr(pos1+3,pos2-pos1-3)+'"'+res.substr(pos2+3,res.size()-pos2-3);
      else
	res=res.substr(0,pos1)+res.substr(pos2+3,res.size()-pos2-3);
    }
    return res;
  }

  struct int_string {
    int decal;
    std::string endbloc;
    int_string():decal(0){}
    int_string(int i,string s):decal(i),endbloc(s){}
  };

  static bool instruction_at(const string & s,int pos,int shift){
    if (pos && isalphan(s[pos-1]))
      return false;
    if (pos+shift<int(s.size()) && isalphan(s[pos+shift]))
      return false;
    return true;
  }

  void convert_python(string & cur){
    for (int pos=1;pos<int(cur.size());++pos){
      char prevch=cur[pos-1],curch=cur[pos];
      if (curch==':' && (prevch=='[' || prevch==',')){
	cur.insert(cur.begin()+pos,'0');
	continue;
      }
      if (curch==']' && (prevch==':' || prevch==',')){
	cur.insert(cur.begin()+pos,'-');
	cur.insert(cur.begin()+pos,'1');
	continue;
      }
      if (curch=='%'){
	cur.insert(cur.begin()+pos+1,'/');
	++pos;
	continue;
      }
      if (curch=='=' && prevch!='>' && prevch!='<' && prevch!='!' && prevch!=':' && prevch!='=' && prevch!='+' && prevch!='-' && prevch!='*' && prevch!='/' && (pos==int(cur.size())-1 || cur[pos+1]!='=')){
	cur.insert(cur.begin()+pos,':');
	++pos;
	continue;
      }
      if (prevch=='/' && curch=='/')
	cur[pos]='%';
    }
  }

  // detect Python like syntax: 
  // remove """ """ docstrings and ''' ''' comments
  // cut string in lines, remove comments at the end (search for #)
  // warning don't take care of # inside strings
  // if a line of s ends with a :
  // search for matching def/for/if/else/while
  // stores matching end keyword in a stack as a vector<[int,string]>
  // int is the number of white spaces at the start of the next line
  // def ... : -> function [ffunction]
  // for ... : -> for ... do [od]
  // while ... : -> while ... do [od]
  // if ...: -> if ... then [fi]
  // else: -> else [nothing in stack]
  // elif ...: -> elif ... then [nothing in stack]
  // ? support for try except
  std::string python2xcas(const std::string & s_orig,GIAC_CONTEXT){
    // quick check for python-like syntax: search line ending with :
    int first=0,sss=s_orig.size();
    first=s_orig.find("maple_mode");
    if (first>=0 && first<sss)
      return s_orig;
    first=s_orig.find("xcas_mode");
    if (first>=0 && first<sss)
      return s_orig;
    for (first=0;first<sss;){
      int pos=s_orig.find(":]");
      if (pos>=0 && pos<sss)
	break;
      pos=s_orig.find("[:");
      if (pos>=0 && pos<sss)
	break;
      pos=s_orig.find(",:");
      if (pos>=0 && pos<sss)
	break;
      pos=s_orig.find(":,");
      if (pos>=0 && pos<sss)
	break;
      first=s_orig.find(':',first);
      if (first<0 || first>=sss)
	return s_orig; // not Python like
      pos=s_orig.find("lambda");
      if (pos>=0 && pos<sss)
	break;
      int endl=s_orig.find('\n',first);
      if (endl<0 || endl>=sss)
	endl=sss;
      ++first;
      if (first<endl && (s_orig[first]==';' || s_orig[first]=='=')) 
	continue; // ignore :;
      // search for line finishing with : (or with # comment)
      for (;first<endl;++first){
	char ch=s_orig[first];
	if (ch!=' '){
	  if (ch=='#')
	    first=endl;
	  break;
	}
      }
      if (first==endl) 
	break;
    }
    // probably Python-like
    bool pythonmode=false;
    string res(s_orig);
    if (res.size()>18 && res.substr(0,17)=="add_autosimplify(" 
	&& res[res.size()-1]==')'
	)
      res=res.substr(17,res.size()-18);
    res=remove_comment(res,"\"\"\"",false);
    res=remove_comment(res,"'''",true);
    vector<int_string> stack;
    string s,cur; 
    for (;res.size();){
      int pos=res.find('\n');
      if (pos<0 || pos>=int(res.size())){
	cur=res; res="";
      }
      else {
	cur=res.substr(0,pos); // without \n
	res=res.substr(pos+1,res.size()-pos-1);
      }
      // detect comment (outside of a string) and lambda expr:expr
      bool instring=false,chkfrom=true;
      for (pos=0;pos<int(cur.size());++pos){
	char ch=cur[pos];
	if (ch==' ' || ch==char(9))
	  continue;
	if (ch=='"'){
	  chkfrom=false;
	  instring=!instring;
	}
	if (instring) continue;
	if (ch=='#'){
	  cur=cur.substr(0,pos);
	  pythonmode=true;
	  break;
	}
	// skip from * import *
	if (chkfrom && ch=='f' && pos+15<int(cur.size()) && cur.substr(pos,5)=="from "){
	  chkfrom=false;
	  int posi=cur.find(" import ");
	  if (posi>pos+5 && posi<int(cur.size())){
	    cur=cur.substr(0,pos);
	    pythonmode=true;
	    break;
	  }
	}
	chkfrom=false;
	if (ch=='l' && pos+6<int(cur.size()) && cur.substr(pos,6)=="lambda" && instruction_at(cur,pos,6)){
	  int posdot=cur.find(':',pos);
	  if (posdot>pos+7 && posdot<int(cur.size())-1 && cur[posdot+1]!='='){
	    pythonmode=true;
	    cur=cur.substr(0,pos)+cur.substr(pos+6,posdot-pos-6)+"->"+cur.substr(posdot+1,cur.size()-posdot-1);
	  }
	}
      }
      // detect : at end of line
      for (pos=int(cur.size())-1;pos>=0;--pos){
	if (cur[pos]!=' ' && cur[pos]!=char(9))
	  break;
      }
      if (pos<0) continue;
      // count whitespaces, compare to stack
      int ws=0;
      int cs=cur.size();
      for (ws=0;ws<cs;++ws){
	if (cur[ws]!=' ' && cur[ws]!=char(9))
	  break;
      }
      if (cur[pos]==':'){
	// detect else or elif
	int progpos=cur.find("else");
	if (progpos>=0 && progpos<cs && instruction_at(cur,progpos,4)){
	  pythonmode=true;
	  s += cur.substr(0,pos)+"\n";
	  continue;
	}
	progpos=cur.find("elif");
	if (progpos>=0 && progpos<cs && instruction_at(cur,progpos,4)){
	  pythonmode=true;
	  cur=cur.substr(0,pos);
	  convert_python(cur);
	  s += cur+" then\n";
	  continue;
	}
      }
      if (!stack.empty()){ 
	int indent=stack.back().decal;
	if (ws<=indent){
	  // remove last \n and add explicit endbloc delimiters from stack
	  int ss=s.size();
	  bool nl= ss && s[ss-1]=='\n';
	  if (nl)
	    s=s.substr(0,ss-1);
	  while (!stack.empty() && stack.back().decal>=ws){
	    s += ' '+stack.back().endbloc+';';
	    stack.pop_back();
	  }
	  if (nl)
	    s += '\n';
	}
      }
      if (cur[pos]==':'){
	// detect matching programming structure
	int progpos=cur.find("if");
	if (progpos>=0 && progpos<cs && instruction_at(cur,progpos,2)){
	  pythonmode=true;
	  cur=cur.substr(0,pos);
	  convert_python(cur);
	  s += cur +" then\n";
	  stack.push_back(int_string(ws,"fi"));
	  continue;
	}
	progpos=cur.find("for");
	if (progpos>=0 && progpos<cs && instruction_at(cur,progpos,3)){
	  pythonmode=true;
	  cur=cur.substr(0,pos);
	  convert_python(cur);
	  s += cur+" do\n";
	  stack.push_back(int_string(ws,"od"));
	  continue;
	}
	progpos=cur.find("while");
	if (progpos>=0 && progpos<cs && instruction_at(cur,progpos,5)){
	  pythonmode=true;
	  cur=cur.substr(0,pos);
	  convert_python(cur);
	  s += cur +" do\n";
	  stack.push_back(int_string(ws,"od"));
	  continue;
	}
	progpos=cur.find("def");
	if (progpos>=0 && progpos<cs && instruction_at(cur,progpos,3)){
	  pythonmode=true;
	  s += cur.substr(0,progpos)+"function"+cur.substr(progpos+3,pos-progpos-3)+"\n";
	  stack.push_back(int_string(ws,"ffunction"));
	  continue;
	}
      }
      else {
	// normal line add ; at end
	if (pythonmode && pos>=0 && cur[pos]!=';')
	  cur = cur +';';
	if (pythonmode)
	  convert_python(cur);
	cur = cur +'\n';
	s = s+cur;
      }
    }
    while (!stack.empty()){
      s += ' '+stack.back().endbloc+';';
      stack.pop_back();
    }
    if (pythonmode){
      char ch;
      while ((ch=s[s.size()-1])==';' || (ch=='\n'))
	s=s.substr(0,s.size()-1);
      s += "\n:;";
      *logptr(contextptr) << "// Python-like syntax, check string delimiters \"\" and declare local variables.\nTranslated to Xcas as:\n" << s << endl;
    }
    return s;
  }
  
    std::string translate_at(const char * ch){
      if (!strcmp(ch,"ΔLIST"))
	return "DELTALIST";
      if (!strcmp(ch,"ΠLIST"))
	return "PILIST";
      if (!strcmp(ch,"ΣLIST"))
	return "SIGMALIST";
      if (!strcmp(ch,"∫"))
	return "HPINT";
      if (!strcmp(ch,"∂"))
	return "HPDIFF";
      if (!strcmp(ch,"Σ"))
	return "HPSUM";
      if (!strcmp(ch,"∑"))
	return "HPSUM";
      string res;
      for (;*ch;++ch){
        if (*ch=='%')
          res +="PERCENT";
        else
          res += *ch;
      }
      return res;
    }
    
    bool builtin_lexer_functions_sorted = false;

    map_charptr_gen & lexer_functions(){
      static map_charptr_gen * ans=0;
      if (!ans){
	ans = new map_charptr_gen;
	doing_insmod=false;
	builtin_lexer_functions_sorted=false;
      }
      return * ans;
    }


#ifdef STATIC_BUILTIN_LEXER_FUNCTIONS

    const charptr_gen_unary builtin_lexer_functions[] ={
#if defined(GIAC_HAS_STO_38) && defined(CAS38_DISABLED)
#include "static_lexer_38.h"
#else
#include "static_lexer.h"
#endif
    };

    const unsigned builtin_lexer_functions_number=sizeof(builtin_lexer_functions)/sizeof(charptr_gen_unary);
    // return true/false to tell if s is recognized. return the appropriate gen if true
#ifdef NSPIRE
    vector<size_t> * builtin_lexer_functions_(){
      static vector<size_t> * res=0;
      if (res) return res;
      res = new vector<size_t>;
      res->reserve(builtin_lexer_functions_number+1);
#include "static_lexer_at.h"
      return res;
    }
#else
    // Array added because GH compiler stores builtin_lexer_functions in RAM
    const size_t builtin_lexer_functions_[]={
#if defined(GIAC_HAS_STO_38) && defined(CAS38_DISABLED)
#include "static_lexer_38_.h"
#else
#include "static_lexer_.h"
#endif
    };
#endif

#ifdef SMARTPTR64
    charptr_gen * builtin_lexer_functions64(){
      static charptr_gen * ans=0;
      if (!ans){
	ans = new charptr_gen[builtin_lexer_functions_number];
	for (unsigned i=0;i<builtin_lexer_functions_number;i++){
	  charptr_gen tmp; tmp.first=builtin_lexer_functions[i].s; tmp.second=builtin_lexer_functions[i]._FUNC_;
	  tmp.second.subtype=builtin_lexer_functions[i].subtype;
	  ans[i]=tmp;
	}
      }
      return ans;
    }

    charptr_gen * builtin_lexer_functions_begin(){
      return (charptr_gen *) builtin_lexer_functions64();
    }
#else
    charptr_gen * builtin_lexer_functions_begin(){
      return (charptr_gen *) builtin_lexer_functions;
    }
#endif // SMARTPTR64

    charptr_gen * builtin_lexer_functions_end(){
      return builtin_lexer_functions_begin()+builtin_lexer_functions_number;
    }

#else
    unsigned builtin_lexer_functions_number;
    charptr_gen * builtin_lexer_functions(){
      static charptr_gen * ans=0;
      if (!ans){
	ans = new charptr_gen[1600];
	builtin_lexer_functions_number=0;
      }
      return ans;
    }

    charptr_gen * builtin_lexer_functions_begin(){
      return builtin_lexer_functions();
    }

    charptr_gen * builtin_lexer_functions_end(){
      return builtin_lexer_functions()+builtin_lexer_functions_number;
    }

    const size_t * const builtin_lexer_functions_=0;

#endif

#endif // NSPIRE

  gen make_symbolic(const gen & op,const gen & args){
    return symbolic(*op._FUNCptr,args);
  }

  // optional, call it just before exiting
  int release_globals(){
#ifndef VISUALC
    delete normal_sin_pi_12_ptr_();
    delete normal_cos_pi_12_ptr_();
#endif
#ifndef STATIC_BUILTIN_LEXER_FUNCTIONS
    if (debug_infolevel)
      CERR << "releasing " << builtin_lexer_functions_number << " functions" << endl;
    for (int i=0;i<builtin_lexer_functions_number;++i){
#ifdef SMARTPTR64
      if (debug_infolevel)
	CERR << builtin_lexer_functions_begin()[i].first << endl; 
      delete (ref_unary_function_ptr *) (* ((ulonglong * ) &builtin_lexer_functions_begin()[i].second) >> 16);
#endif
    }
#endif
    delete &registered_lexer_functions();
    delete &lexer_functions();
    delete &library_functions();
    delete &lexer_translator();
    delete &back_lexer_localization_map();
    delete &lexer_localization_map();
    delete &lexer_localization_vector();
    delete &syms();
    delete &unit_conversion_map();
    delete &xcasrc();
    //delete &usual_units();
    if (vector_aide_ptr()) delete vector_aide_ptr();
    delete &symbolic_rootof_list();
    delete &proot_list();
    delete &galoisconj_list();
    return 0;
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
