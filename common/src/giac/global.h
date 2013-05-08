/* -*- mode:C++;compile-command: "g++-3.4 -I.. -g -c global.cc" -*- */

/* Global definition and constants (see also dispatch.h)
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
#ifndef _GIAC_GLOBAL_H
#define _GIAC_GLOBAL_H
#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#include "first.h"
#define GIAC_CONTEXT const context * contextptr
#define GIAC_CONTEXT0 const context * contextptr=0

#if !defined(HAVE_NO_SYS_TIMES_H) && !defined(BESTA_OS) && !defined(__MINGW_H)
#include <sys/times.h>
#else
#if defined VISUALC || defined BESTA_OS 
typedef long pid_t;
#else // VISUALC
#if !defined(__MINGW_H) && !defined(BESTA_OS)
#include "wince_replacements.h"
#endif
#ifdef __MINGW_H
#include <sys/types.h>
#endif
#endif // VISUALC
#endif // HAVE_NO_SYS_TIMES_H
// #ifndef __APPLE__
#if defined VISUALC || defined BESTA_OS
#include <math.h>
#include <float.h>
#ifndef RTOS_THREADX
#include <io.h>
#endif
#endif
#ifndef WIN32
#include <math.h>
//#define isnan __isnan
//#define isinf __isinf
#endif
// #endif

#ifdef SOFTMATH
#include "softmath.h"
#else
#include <cmath>
#endif

#ifdef _SOFTMATH_H
inline double giac_log(double d){
  return std::giac_gnuwince_log(d);
}
#else
inline double giac_log(double d){
  return std::log(d);
}
#endif


#ifdef HAVE_LIBPTHREAD
#include <semaphore.h>
#endif
#ifdef HAVE_PTHREAD_H
#include <pthread.h>
#endif

#include "vector.h"
#include <string>
#include <cstring>
#include <iostream>
#include <fstream>
#include <map>

#ifdef GNUWINCE
#define SIGINT 2
#else
#ifndef HAVE_NO_SIGNAL_H
#include <signal.h>
#endif
#endif // GNUWINCE

#include <stdexcept>
#include "help.h"
#if !defined(GIAC_HAS_STO_38) && !defined(ConnectivityKit) && !defined(BESTA_OS)
#include "tinymt32.h"
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  double giac_floor(double d);
  double giac_ceil(double d);
  unsigned int utf82unicode(const char * line,wchar_t * wline,unsigned int n);
  unsigned int unicode2utf8(const wchar_t * wline,char * line,unsigned int n);
  // convert position n in utf8-encoded line into the corresponding position
  // in the same string encoded with unicode
  unsigned int utf8pos2unicodepos(const char * line,unsigned int n,bool skip_added_spaces = true);
  unsigned int wstrlen(const char * line, unsigned int n = ~0u);
  unsigned int wstrlen(const wchar_t * wline);
  unsigned int utf8length(const wchar_t * wline);


#if defined VISUALC || defined BESTA_OS
  extern int R_OK;
  int access(const char * ch,int mode);
  void usleep(int );
#endif

  double delta_tms(struct tms tmp1,struct tms tmp2);

#define GIAC_DATA_BEGIN   ((char) 2)
#define GIAC_DATA_END     ((char) 5)
#define GIAC_DATA_ESCAPE  ((char) 27)

  std::vector<aide> * & vector_aide_ptr();
  std::vector<std::string> * & vector_completions_ptr();
  extern void (*fl_widget_delete_function)(void *);
  extern std::ostream & (*fl_widget_archive_function)(std::ostream &,void *);
  extern bool secure_run; // true if used in a non-trusted environment
  extern bool center_history,in_texmacs,block_signal,synchronize_history;
  extern bool threads_allowed;
  extern bool mpzclass_allowed;
  enum { 
    smallint=256, // max small int to make modular oply operations with int
    max_series_expansion_order=64, // max auto order for series expansion
    max_texpand_expansion_order=64,
    series_default_order=20,
    max_numexp=100
  };
  extern const char cas_suffixe[];
  extern const int BUFFER_SIZE;
  extern int history_begin_level;

  extern int debug_infolevel; // ==0 normal value
  // >0 log some informations
  // <0 for internal use
  // ==-1:
  // ==-2: icas.cc reads aide_cas even if STATIC_BUILTIN_LEXER is defined
  //       write static_lexer.h, static_extern.h
  // ==-3:
  // ==-4: write static_lexer.h, static_lexer_.h and static_extern.h
  // ==-5: do not throw on errors
  extern int threads;
  extern unsigned short int GIAC_PADIC;

  extern bool CAN_USE_LAPACK;
#ifndef RTOS_THREADX
#ifndef BESTA_OS
  extern int CALL_LAPACK; // lapack is used if dim of matrix is >= CALL_LAPACK
  // can be changed using shell variable GIAC_LAPACK in icas
#endif
#endif
  extern int FACTORIAL_SIZE_LIMIT;
  extern int LIST_SIZE_LIMIT;
  extern int NEWTON_DEFAULT_ITERATION;
  extern int DEFAULT_EVAL_LEVEL;
  extern int PARENTHESIS_NWAIT;

  extern int TEST_PROBAB_PRIME; // probabilistic primality tests
  extern int GCDHEU_MAXTRY; // maximal number of retry for heuristic algorithms
  extern int GCDHEU_DEGREE; // max degree allowed inside gcdheu
  extern int MODFACTOR_PRIMES; // number of primes used for factorization
  extern int NTL_MODGCD; // lowest degree for NTL univariate modular GCD 
  extern int HENSEL_QUADRATIC_POWER; // above #steps do quadratic Hensel lift
  extern int KARAMUL_SIZE; // Use Karatsuba multiplication if degree is >
  extern int INT_KARAMUL_SIZE; // Use Karatsuba multiplication if degree is >
  extern int FFTMUL_SIZE; // Currently adapted to (x+1)^n
  // Should be lower for larger coeff
  extern int MAX_ALG_EXT_ORDER_SIZE; // x^1/d extension not algebraic if d>
  extern int MAX_COMMON_ALG_EXT_ORDER_SIZE;
  extern int TRY_FU_UPRIME;
  extern int SOLVER_MAX_ITERATE;
  extern int MAX_PRINTABLE_ZINT;
  extern int MAX_RECURSION_LEVEL;
  extern volatile bool ctrl_c,interrupted;
  void ctrl_c_signal_handler(int signum);
  extern const double powlog2float;
  extern const int MPZ_MAXLOG2;

  // void control_c();
  // note that ctrl_c=false was removed, should be done before calling eval
#ifdef RTOS_THREADX
#define control_c() 
#elif BESTA_OS
#define control_c()
#else
  #define control_c() if (ctrl_c) { interrupted = true; std::cerr << "Throwing exception for user interruption." << std::endl; throw(std::runtime_error("Stopped by user interruption.")); }
#endif
  typedef void ( * void_function )();
  // set to non-0 if you want to hook a function call inside control_c()

#ifdef IMMEDIATE_VECTOR
  template <class T> class dbgprint_vector: public std::imvector<T> {
  public:
    // inherited constructors
    dbgprint_vector() : std::imvector<T>::imvector() { };
    dbgprint_vector(const T * b,const T * e) : std::imvector<T>::imvector(b,e) { };
    dbgprint_vector(int i) : std::imvector<T>::imvector(i) { };
    dbgprint_vector(int i,const T & t) : std::imvector<T>::imvector(i,t) { };
    // ~dbgprint_vector() { };
    // inherited destructors
    void dbgprint() const { std::cout << *this << std::endl; }
  };
#else // IMMEDIATE_VECTOR
  template <class T> class dbgprint_vector: public std::vector<T> {
  public:
    // inherited constructors
    dbgprint_vector() : std::vector<T>::vector() { };
#ifndef GIAC_VECTOR
    dbgprint_vector(const typename std::vector<T>::const_iterator & b,const typename std::vector<T>::const_iterator & e) : std::vector<T>::vector(b,e) { };
#endif
    dbgprint_vector(const T * b,const T * e) : std::vector<T>::vector(b,e) { };
    dbgprint_vector(int i) : std::vector<T>::vector(i) { };
    dbgprint_vector(int i,const T & t) : std::vector<T>::vector(i,t) { };
    // ~dbgprint_vector() { };
    // inherited destructors
    void dbgprint() const { std::cout << *this << std::endl; }
  };
#endif // IMMEDIATE_VECTOR
  
  template <class T> class std_matrix: public std::vector< dbgprint_vector<T> > {
  public:
    // inherited constructors
    std_matrix() : std::vector< dbgprint_vector<T> >::vector() { };
    std_matrix(int i) : std::vector< dbgprint_vector<T> >::vector(i) { };
    std_matrix(int i,const dbgprint_vector<T> & v) : std::vector< dbgprint_vector<T> >::vector(i,v) { };
    std_matrix(int i,int j) : std::vector< dbgprint_vector<T> >::vector(i,dbgprint_vector<T>(j)) { };
    std_matrix(int i,int j,const T & t) : std::vector< dbgprint_vector<T> >::vector(i,dbgprint_vector<T>(j,t)) { };
    // ~dbgprint_vector() { };
    // inherited destructors
    std_matrix<T> transpose() const {
      if (std::vector< dbgprint_vector<T> >::empty())
	return *this;
      int n=std::vector< dbgprint_vector<T> >::size();
      int m=std::vector< dbgprint_vector<T> >::front().dbgprint_vector<T>::size();
      std_matrix<T> res(m,n);
      typename std_matrix<T>::const_iterator it=std::vector< dbgprint_vector<T> >::begin();
      for (int i=0;i<n;++i,++it){
	for (int j=0;j<m;++j)
	  res[j][i]=(*it)[j];
      }
      return res;
    }
    std_matrix<T> transconjugate() const {
      if (std::vector< dbgprint_vector<T> >::empty())
	return *this;
      int n=std::vector< dbgprint_vector<T> >::size();
      int m=std::vector< dbgprint_vector<T> >::front().dbgprint_vector<T>::size();
      std_matrix<T> res(m,n);
      typename std_matrix<T>::const_iterator it=std::vector< dbgprint_vector<T> >::begin();
      for (int i=0;i<n;++i,++it){
	for (int j=0;j<m;++j)
	  res[j][i]=conj((*it)[j],0);
      }
      return res;
    }
    void dbgprint() { std::cout << *this << std::endl; }
  };

  struct user_function {
    std::string s;
    int parser_token;
    user_function():s(""),parser_token(-1) {};
    user_function(const std::string & mys,int i):s(mys),parser_token(i){};
  };

  class gen;
  // vecteurs and dense 1-d polynomilas

  typedef dbgprint_vector<gen> vecteur; // debugging support

  class context;
  
  struct debug_struct {
    int indent_spaces;
    vecteur args_stack;
    vecteur debug_breakpoint; // alternate _IDNT and instruction number
    // count 1 for a normal instruction, 3 for ifte, 4 for a for loop
    // breakpoint(_IDNT,int) to set a breakpoint at _IDNT, instruction int
    // rmbreakpoint(int) removes breakpoint number int
    vecteur debug_watch; 
    // the value of each element of debug_watch is signaled
    // to the parent process each time the execution stops
    // watch(_IDNT) to add _IDNT to the watch
    // rmwatch(int) or rmwatch(_IDNT) removes _IDNT
    // halt inside a prog starts debug mode, debug(instruction) 
    // starts prog in SST mode
    // kill reset the protection level, instruction_stack and debug_mode to false
    bool debug_mode;
    bool sst_mode; // true to single step in this function
    bool sst_in_mode; // true to single step inside next instruction
    bool debug_allowed;
    std::vector<int> current_instruction_stack;
    int current_instruction;
    std::vector< std::vector<int> > sst_at_stack;
    std::vector<int> sst_at;
    gen * debug_info_ptr, * fast_debug_info_ptr,* debug_prog_name, * debug_localvars;
    bool debug_refresh;
    context * debug_contextptr;
    debug_struct();
    ~debug_struct();
    debug_struct & operator =(const debug_struct & dbg);
  };

  typedef void (* giac_callback)(const giac::gen & ,void * );

  struct thread_param {
    bool _kill_thread;
    int thread_eval_status;
    giac_callback f;
    void * f_param;
#ifdef HAVE_LIBPTHREAD
    pthread_t eval_thread;
    pthread_attr_t attr;
    size_t stacksize;
    void * stackaddr;
#endif
    giac::vecteur v;
    thread_param();
  };

  extern gen (*fl_widget_unarchive_function)(std::istream &);
  extern std::string (*fl_widget_texprint_function)(void * ptr);
  extern gen (*fl_widget_updatepict_function)(const gen & g);
  // name -> gen table
  typedef std::map<std::string, gen> sym_tab;
  struct ltstr
  {
    bool operator()(const char* s1, const char* s2) const
    {
      return strcmp(s1, s2) < 0;
    }
  };
  
  typedef std::map<const char *, gen,ltstr> map_charptr_gen;

  struct parser_lexer {
    int _index_status_; // 0 if [ -> T_VECT_DISPATCH, 1 if [ -> T_INDEX_BEGIN
    int _opened_quote_; // 1 if we are inside a quote
    int _in_rpn_; // 1 inside RPN expression
    int _lexer_line_number_;
    int _lexer_column_number_;
    int _spread_formula_; // beginning = and meaning of :
    int _initialisation_done_;
    std::string _comment_s_;
    std::string _parser_filename_;
    std::string _parser_error_;
    int _first_error_line_;
    std::string _error_token_name_;
    int _i_sqrt_minus1_;
  };
  std::string gen2string(const gen & g);
  struct logo_turtle {
    double x,y;
    double theta; // theta is given in degrees or radians dep. on angle_mode
    bool visible; // true if turtle visible
    bool mark; // true if moving marks
    bool direct; // true if rond/disque is done in the trigonometric direction
    int color;
    int turtle_length;
    int radius; // 0 nothing, >0 -> draw a plain disk 
    // bit 0-8=radius, bit9-17 angle1, bit 18-26 angle2, bit 27=1 filled  or 0 
    // <0 fill a polygon from previous turtle positions
    std::string s;
    void * widget;
#ifdef IPAQ
    logo_turtle(): x(70),y(70),theta(0),visible(true),mark(true),direct(true),color(0),turtle_length(10),radius(0),widget(0) {}
#else
    logo_turtle(): x(100),y(100),theta(0),visible(true),mark(true),direct(true),color(0),turtle_length(10),radius(0),widget(0) {}
#endif
  };

  // a structure that should contain all global variables
  class global {
  public:
    int _xcas_mode_;
    int _calc_mode_;
    int _decimal_digits_;
    int _scientific_format_;
    int _integer_format_;
    int _latex_format_;
#ifdef BCD
    u32 _bcd_decpoint_;
    u32 _bcd_mantissa_;
    u32 _bcd_flags_;
    bool _bcd_printdouble_;
#endif
    bool _expand_re_im_;
    bool _do_lnabs_;
    bool _eval_abs_;
    bool _integer_mode_;
    bool _complex_mode_;
    bool _complex_variables_;
    bool _increasing_power_;
    bool _approx_mode_;
    bool _variables_are_files_;
    bool _local_eval_;
    bool _withsqrt_;
    bool _show_point_; // show 3-d point 
    bool _io_graph_; // show 2-d point in io
    bool _all_trig_sol_;
    bool _ntl_on_;
    bool _lexer_close_parenthesis_;
    bool _rpn_mode_;
    bool _try_parse_i_;
    bool _specialtexprint_double_;
    int _angle_mode_;
    int _bounded_function_no_;
    int _series_flags_; // bit1= full simplify, bit2=1 for truncation
    int _default_color_;
    double _epsilon_;
    double _proba_epsilon_; // if not 0, probabilistic algo may be used
    // the proba should be less than proba_epsilon for giac to return an answer
    int _show_axes_;
    int _spread_Row_,_spread_Col_;
    int _printcell_current_row_,_printcell_current_col_;
    std::ostream * _logptr_;
    debug_struct * _debug_ptr;
    gen * _parsed_genptr_;
    parser_lexer _pl;
    int _prog_eval_level_val ;
    int _eval_level;
#if defined(GIAC_HAS_STO_38) || defined(ConnectivityKit)
    unsigned int _rand_seed;
#else
    tinymt32_t _rand_seed;
#endif
    thread_param * _thread_param_ptr;
#ifdef HAVE_LIBPTHREAD
    pthread_mutex_t * _mutexptr,* _mutex_eval_status_ptr ;
#endif
    int _language_;
    std::vector<const char *> _last_evaled_function_name_;
    vecteur _last_evaled_arg_;
    int _max_sum_sqrt_;
    int _max_sum_add_;
    logo_turtle _turtle_;
    std::string _autoname_;
    std::string _format_double_;
    std::vector<logo_turtle> _turtle_stack_; 
    double _total_time_;
    void * _evaled_table_;
    global();  
    ~global();
    global & operator = (const global & g);
  };

  // Context type to be used for evaluation without global variables
  // tabptr is the current evaluation context, 
  // it is a global context if globalcontextptr=0, local otherwise
  // previous is the upper local or global evaluation context
  // globalcontextptr points to the current global evaluation context
  // If globalcontextptr=0 and previous=0, we are at the top folder
  // The top level local context should have previous=globalcontextptr
  class context {
  public:
    sym_tab * tabptr ;
    context * globalcontextptr ;
    context * previous ;
    global * globalptr; 
    const context * parent;
    vecteur * quoted_global_vars;
    vecteur * history_in_ptr, * history_out_ptr;
    context();
    context(const context & c);
#ifndef RTOS_THREADX
#ifndef BESTA_OS
    context(const std::string & name);
#endif
#endif
    ~context();
    context * clone() const;
  };

  context * clone_context(const context *);
  void init_context(context * ptr);

  extern const context * context0;
  std::vector<context *> & context_list();
#ifdef HAVE_LIBPTHREAD
  extern pthread_mutex_t context_list_mutex;
#endif
  
#ifndef RTOS_THREADX
#ifndef BESTA_OS
  extern std::map<std::string,context *> * context_names ;
#endif
#endif

  std::vector<const char *> & last_evaled_function_name(GIAC_CONTEXT);
  vecteur & last_evaled_arg(GIAC_CONTEXT);

  bool make_thread(const giac::gen & g,int level,const giac_callback & f,void * f_param,const context * contextptr);

  std::string autoname(GIAC_CONTEXT);
  std::string autoname(const std::string & s,GIAC_CONTEXT);

  std::string & format_double(GIAC_CONTEXT);

  int check_thread(context * contextptr);
  int check_threads(int i=0);

  void * & evaled_table(GIAC_CONTEXT);

  int & xcas_mode(GIAC_CONTEXT);
  void xcas_mode(int b,GIAC_CONTEXT);

  int & calc_mode(GIAC_CONTEXT);
  int abs_calc_mode(GIAC_CONTEXT);
  void calc_mode(int b,GIAC_CONTEXT);

  int & scientific_format(GIAC_CONTEXT);
  void scientific_format(int b,GIAC_CONTEXT);

  int & decimal_digits(GIAC_CONTEXT);
  void decimal_digits(int b,GIAC_CONTEXT);

  int & integer_format(GIAC_CONTEXT);
  void integer_format(int b,GIAC_CONTEXT);
  int & latex_format(GIAC_CONTEXT);
#ifdef BCD
  u32 & bcd_decpoint(GIAC_CONTEXT);
  u32 & bcd_mantissa(GIAC_CONTEXT);
  u32 & bcd_flags(GIAC_CONTEXT);
  bool & bcd_printdouble(GIAC_CONTEXT);
#endif
  bool & expand_re_im(GIAC_CONTEXT);
  void expand_re_im(bool b,GIAC_CONTEXT);

  bool & integer_mode(GIAC_CONTEXT);
  void integer_mode(bool b,GIAC_CONTEXT);

  bool & complex_mode(GIAC_CONTEXT);
  void complex_mode(bool b,GIAC_CONTEXT);

  bool & try_parse_i(GIAC_CONTEXT);
  void try_parse_i(bool b,GIAC_CONTEXT);

  bool & specialtexprint_double(GIAC_CONTEXT);
  void specialtexprint_double(bool b,GIAC_CONTEXT);

  bool & do_lnabs(GIAC_CONTEXT);
  void do_lnabs(bool b,GIAC_CONTEXT);

  bool & eval_abs(GIAC_CONTEXT);
  void eval_abs(bool b,GIAC_CONTEXT);

  bool & complex_variables(GIAC_CONTEXT);
  void complex_variables(bool b,GIAC_CONTEXT);

  bool & increasing_power(GIAC_CONTEXT);
  void increasing_power(bool b,GIAC_CONTEXT);

  bool & approx_mode(GIAC_CONTEXT);
  void approx_mode(bool b,GIAC_CONTEXT);

  vecteur & history_in(GIAC_CONTEXT);
  vecteur & history_out(GIAC_CONTEXT);

  // True if we factor 2nd order polynomials using sqrt
  bool & withsqrt(GIAC_CONTEXT);
  void withsqrt(bool b,GIAC_CONTEXT);

  bool & all_trig_sol(GIAC_CONTEXT);
  void all_trig_sol(bool b,GIAC_CONTEXT);

  bool & ntl_on(GIAC_CONTEXT);
  void ntl_on(bool b,GIAC_CONTEXT);

  bool & lexer_close_parenthesis(GIAC_CONTEXT);
  void lexer_close_parenthesis(bool b,GIAC_CONTEXT);

  bool & rpn_mode(GIAC_CONTEXT);
  void rpn_mode(bool b,GIAC_CONTEXT);

  logo_turtle & turtle(GIAC_CONTEXT);
  std::vector<logo_turtle> & turtle_stack(GIAC_CONTEXT);

  int & angle_mode(GIAC_CONTEXT);
  void angle_radian(bool b,GIAC_CONTEXT);
  bool angle_radian(GIAC_CONTEXT);

  bool & show_point(GIAC_CONTEXT);
  void show_point(bool b,GIAC_CONTEXT);

  int & show_axes(GIAC_CONTEXT);
  void show_axes(int b,GIAC_CONTEXT);

  bool & io_graph(GIAC_CONTEXT);
  void io_graph(bool b,GIAC_CONTEXT);

  bool & variables_are_files(GIAC_CONTEXT);
  void variables_are_files(bool b,GIAC_CONTEXT);

  int & bounded_function_no(GIAC_CONTEXT);
  void bounded_function_no(int b,GIAC_CONTEXT);

  int & series_flags(GIAC_CONTEXT);
  void series_flags(int b,GIAC_CONTEXT);

  bool & local_eval(GIAC_CONTEXT);
  void local_eval(bool b,GIAC_CONTEXT);

  int & default_color(GIAC_CONTEXT);
  void default_color(int c,GIAC_CONTEXT);

  int & spread_Row(GIAC_CONTEXT);
  void spread_Row(int c,GIAC_CONTEXT);

  int & spread_Col(GIAC_CONTEXT);
  void spread_Col(int c,GIAC_CONTEXT);

  int & printcell_current_row(GIAC_CONTEXT);
  void printcell_current_row(int c,GIAC_CONTEXT);

  int & printcell_current_col(GIAC_CONTEXT);
  void printcell_current_col(int c,GIAC_CONTEXT);

  double & total_time(GIAC_CONTEXT);
  double & epsilon(GIAC_CONTEXT);
  void epsilon(double c,GIAC_CONTEXT);
  double & proba_epsilon(GIAC_CONTEXT);

  std::ostream * logptr(GIAC_CONTEXT);
  void logptr(std::ostream *,GIAC_CONTEXT);

  int & eval_level(GIAC_CONTEXT);
  // void eval_level(int b,GIAC_CONTEXT);
  thread_param * thread_param_ptr(const context * contextptr);

#if defined(GIAC_HAS_STO_38) || defined(ConnectivityKit)
  unsigned int & rand_seed(GIAC_CONTEXT);
#else
  tinymt32_t * rand_seed(GIAC_CONTEXT);
#endif
  void rand_seed(unsigned int b,GIAC_CONTEXT);
  int giac_rand(GIAC_CONTEXT);

  int & prog_eval_level_val(GIAC_CONTEXT);
  void prog_eval_level_val(int b,GIAC_CONTEXT);

  int & max_sum_sqrt(GIAC_CONTEXT);
  void max_sum_sqrt(int b,GIAC_CONTEXT);

  int & max_sum_add(GIAC_CONTEXT);

  int & language(GIAC_CONTEXT);
  void language(int b,GIAC_CONTEXT);

  int & lexer_line_number(GIAC_CONTEXT);
  int & lexer_column_number(GIAC_CONTEXT);
  void lexer_line_number(int b,GIAC_CONTEXT);
  void increment_lexer_line_number(GIAC_CONTEXT);

  std::string parser_error(GIAC_CONTEXT);
  void parser_error(const std::string & s,GIAC_CONTEXT);

  int & index_status(GIAC_CONTEXT);
  void index_status(int b,GIAC_CONTEXT);

  int & i_sqrt_minus1(GIAC_CONTEXT);
  void i_sqrt_minus1(int b,GIAC_CONTEXT);

  int & opened_quote(GIAC_CONTEXT);
  void opened_quote(int b,GIAC_CONTEXT);

  int & in_rpn(GIAC_CONTEXT);
  void in_rpn(int b,GIAC_CONTEXT);

  int & spread_formula(GIAC_CONTEXT);
  void spread_formula(int b,GIAC_CONTEXT);

  int & initialisation_done(GIAC_CONTEXT);
  void initialisation_done(int b,GIAC_CONTEXT);

  std::string comment_s(GIAC_CONTEXT);
  void comment_s(const std::string & s,GIAC_CONTEXT);
  void increment_comment_s(const std::string & s,GIAC_CONTEXT);
  void increment_comment_s(char ch,GIAC_CONTEXT);

  std::string parser_filename(GIAC_CONTEXT);
  void parser_filename(const std::string & s,GIAC_CONTEXT);

  int & first_error_line(GIAC_CONTEXT);
  void first_error_line(int s,GIAC_CONTEXT);

  std::string error_token_name(GIAC_CONTEXT);
  void error_token_name(const std::string & s,GIAC_CONTEXT);

  gen parsed_gen(GIAC_CONTEXT);
  void parsed_gen(const gen & g,GIAC_CONTEXT);

  debug_struct * debug_ptr(GIAC_CONTEXT);

  // gen_op is the type of all functions taking 1 or more gen args 
  // and returning 1 arg of type gen
  // gen argument has atomic type for an unary op, 
  // and vecteur type (compttr) otherwise
  typedef gen ( * gen_op ) (const gen & arg);
  typedef gen ( * gen_op_context ) (const gen & arg,const context * context_ptr);

  extern pid_t child_id;
  extern pid_t parent_id;
#ifdef HAVE_SIGNAL_H_OLD
  // all this stuff is obsolete, will be removed soon
  void kill_and_wait_sigusr2(); // called by child for interm. data
  // subprocess evaluation
  // return true if entree has been sent to evalation by child process
  bool child_eval(const std::string & entree,bool numeric,bool is_run_file);
  bool child_reeval(int history_begin_level);
  bool update_data(gen & entree,gen & sortie,GIAC_CONTEXT);
  void updatePICT(const vecteur & args);
  bool read_data(gen & entree,gen & sortie,std::string & message,GIAC_CONTEXT);

  gen wait_parent(); // wait a SIGUSR2 from parent process, return a gen

  pid_t make_child(); // forks and return child id
  extern bool running_file;
  extern volatile bool child_busy;
  extern volatile bool data_ready;
  extern volatile bool signal_plot_child;
  extern volatile bool signal_plot_parent; 
  extern int run_modif_pos;
  // end of obsolete section 
#endif

  void read_config(const std::string & name,GIAC_CONTEXT,bool verbose=true);
  void protected_read_config(GIAC_CONTEXT,bool verbose=true);
  vecteur remove_multiples(vecteur & v); // sort v and return list without multiple occurences
  int equalposcomp(const std::vector<int> v,int i);
  int equalposcomp(const std::vector<short int> v,int i);
  int equalposcomp(int tab[],int f);
  std::string find_doc_prefix(int i);
  std::string find_lang_prefix(int i);
  int string2lang(const std::string & s); // convert "fr" to 1, "es" to 3 etc.
  void update_completions();
  void add_language(int i);
  void remove_language(int i);
  std::string set_language(int i,GIAC_CONTEXT);
  std::string read_env(GIAC_CONTEXT,bool verbose=true); // return doc prefix
  std::string home_directory();
  std::string cas_entree_name();
  std::string cas_sortie_name();
  std::string cas_setup_string(GIAC_CONTEXT);
  std::string geo_setup_string();
  std::string giac_aide_dir(); // PATH to the directory of aide_cas
  bool is_file_available(const char * ch);
  bool file_not_available(const char * ch);
  bool check_file_path(const std::string & s); // true if file is in path
  bool system_browser_command(const std::string & file);
  // convert doc name to an absolute path name
  std::string absolute_path(const std::string & orig_file);
  std::string & xcasrc();
  std::string & xcasroot();
  std::string add_extension(const std::string & s,const std::string & ext,const std::string & def);
  // Maple worksheet translate
  void mws_translate(std::istream & inf,std::ostream & of);
  void in_mws_translate(std::istream & inf,std::ostream & of);
  // TI function/program ASC file translate
  void ti_translate(std::istream & inf,std::ostream & of);
  std::string remove_filename(const std::string & s);
  bool my_isnan(double d);
  bool my_isinf(double d);

  /* launch a new thread for evaluation only,
     no more readqueue, readqueue is done by the "parent" thread
     Ctrl-C will kill the "child" thread
     wait_001 is a function that should wait 0.001 s and update thinks
     for example it could remove idle callback of a GUI
     then call the wait function of the GUI and readd callbacks
  */
  giac::gen thread_eval(const giac::gen & g,int level,giac::context * contextptr,void (* wait_001)(giac::context *));
#ifdef HAVE_LIBPTHREAD
  // pointer to the context mutex so that thread_eval can be locked
  // Check this in wait_001 function if you don't want the main thread to
  // be blocked by a call to thread_eval inside wait_001
  pthread_mutex_t * mutexptr(GIAC_CONTEXT);
  extern pthread_mutex_t interactive_mutex,turtle_mutex;
  
#endif
  // Check if a thread_eval is active
  bool is_context_busy(GIAC_CONTEXT);
  // Check and set the kill thread flag
  bool kill_thread(GIAC_CONTEXT);
  void kill_thread(bool b,GIAC_CONTEXT);
  // Thread eval status = 0 finished, =1 eval, =2 debug_wait_main
  int thread_eval_status(GIAC_CONTEXT);
  void thread_eval_status(int c,GIAC_CONTEXT);

  void clear_prog_status(GIAC_CONTEXT);
  void cleanup_context(GIAC_CONTEXT);

  // count how many bytes are required to save g in a file
  unsigned archive_count(const gen & g,GIAC_CONTEXT);
  // save g in a opened file
  bool archive_save(void * f,const gen & g,size_t writefunc(void const* p, size_t nbBytes,size_t NbElements, void *file),GIAC_CONTEXT, bool noRecurse=false);

  bool archive_save(void * f,const gen & g,GIAC_CONTEXT);
  // restore a gen from an opened file
  gen archive_restore(void * f,size_t readfunc(void * p, size_t nbBytes,size_t NbElements, void *file),GIAC_CONTEXT);
  gen archive_restore(FILE * f,GIAC_CONTEXT);
  void init_geogebra(bool on,GIAC_CONTEXT);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC


#endif // _GIAC_GLOBAL_H
