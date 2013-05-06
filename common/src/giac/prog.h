/* -*- mode:C++; compile-command: "g++ -I.. -g -c prog.cc" -*- */
/*
 *  Copyright (C) 2001 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#ifndef _GIAC_PROG_H
#define _GIAC_PROG_H
#include "first.h"
#include "vector.h"
#include <string>
#include <map>
#include "gen.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  extern bool user_screen; 
  extern int user_screen_io_x,user_screen_io_y,user_screen_fontsize;

  struct user_function;
  struct module_info {
    std::vector<user_function> registered_names;
    void * handle;
    module_info():handle(0){};
    module_info(const std::vector<user_function> & r,void * h):registered_names(r),handle(h){};
  } ;
#ifdef HAVE_LIBDL
  typedef std::map< std::string, module_info> modules_tab;
  extern modules_tab giac_modules_tab;
#endif

  gen check_secure(); // in secure mode error
  void set_decimal_digits(int n,GIAC_CONTEXT);
  int digits2bits(int n);
  int bits2digits(int n);
  // debug_info should be a vecteur containing
  // w[0]=function + args, w[2]= res of last evaluation, 
  // w[3] = source, w[4]=current_instruction
  // w[5] = watch vecteur, w[6] = watch values
  gen equaltosame(const gen & a);
  gen sametoequal(const gen & a);    
  int bind(const vecteur & vals,const vecteur & vars,context * & contextptr);
  bool leave(int protect,vecteur & vars,context * & contextptr);

  void increment_instruction(const vecteur & v,GIAC_CONTEXT);
  void increment_instruction(const gen & arg,GIAC_CONTEXT);
  void debug_print(const vecteur & arg,std::vector<std::string> & v,GIAC_CONTEXT);
  void debug_print(const gen & e,std::vector<std::string>  & v,GIAC_CONTEXT);
  std::string indent(GIAC_CONTEXT);
  // Find non local vars
  // res1= list of assignation with =, res2= list of non declared global vars, res3= list of declared global vars, res4=list of functions
  void check_local_assign(const gen & g,const vecteur & prog_args,vecteur & res1,vecteur & res2,vecteur & res3,vecteur & res4,bool testequal,GIAC_CONTEXT);
  // Return the names of variables that are not local in g
  // and the equality that are not used (warning = instead of := )
  std::string check_local_assign(const gen & g,GIAC_CONTEXT);
  symbolic symb_program_sto(const gen & a,const gen & b,const gen & c,const gen & d,bool embedd=false,GIAC_CONTEXT=context0);
  symbolic symb_program(const gen & a,const gen & b,const gen & c,GIAC_CONTEXT);
  symbolic symb_program(const gen & args);
  gen quote_program(const gen & args,GIAC_CONTEXT);
  gen _program(const gen & args,const gen & name,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_program ;
  void adjust_sst_at(const gen & name,GIAC_CONTEXT); //used in symbolic.cc by nr_eval
  void program_leave(const gen & save_debug_info,bool save_sst_mode,debug_struct * dbgptr);

  gen _bloc(const gen & prog,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_bloc ;

  std::string printasfor(const gen & feuille,const char * sommetstr,GIAC_CONTEXT);
  std::string printasifte(const gen & feuille,const char * sommetstr,GIAC_CONTEXT);
  symbolic symb_ifte(const gen & test,const gen & oui, const gen & non);
  gen ifte(const gen & args,bool isifte,const context * contextptr);
  gen _ifte(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ifte ;
  gen _evalb(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_evalb ;
  gen _maple_if(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_maple_if ;

  gen symb_when(const gen & t,const gen & a,const gen & b);
  gen _when(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_when ;

  gen _for(const gen & e,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_for ;

  gen _local(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_local;

  gen _return(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_return;  

  gen _try_catch(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_try_catch;  

  gen _check_type(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_check_type;  

  gen _type(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_type;  
  gen _nop(const gen & a,GIAC_CONTEXT);

  gen _feuille(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_feuille;  
  gen _maple_op(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_maple_op;  

  gen _sommet(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_sommet;  

  gen subsop(const gen & g,const vecteur & v,GIAC_CONTEXT);
  gen subsop(const vecteur & g,const vecteur & v,const gen & sommet,GIAC_CONTEXT);
  gen _maple_subsop(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_maple_subsop;  

  gen _subsop(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_subsop;  

  gen _append(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_append;  

  gen _prepend(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_prepend;  

  gen concat(const gen & g,bool glue_lines,GIAC_CONTEXT);
  gen _concat(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_concat;  
  
  gen _contains(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_contains;  

  gen _select(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_select;  

  gen _remove(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_remove;  

  gen _option(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_option;  

  gen _case(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_case;  

  gen _rand(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_rand;  
  gen rand_interval(const vecteur & v,bool entier,GIAC_CONTEXT);

  gen _srand(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_srand;  

  gen _char(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_char;  

  gen _asc(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_asc;  

  gen _map(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_map;  
  
  gen _apply(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_apply;  
  
  gen _makelist(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_makelist;  
  
  gen symb_interval(const gen & a,const gen & b);
  gen _interval(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_interval;
  gen symb_interval(const gen & a,const gen & b);
  
  gen _comment(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_comment;

  gen _throw(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_throw;

  gen _union(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_union;

  gen _intersect(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_intersect;
  gen _inter(const gen & args,GIAC_CONTEXT);
  extern const alias_type alias_at_inter;
  extern const unary_function_ptr * const  at_inter;

  gen _minus(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_minus;

  gen _dollar(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_dollar;

  gen _makemat(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_makemat;

  gen _compose(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_compose;

  gen _composepow(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_composepow;

  gen _has(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_has;

  gen _args(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_args;

  vecteur lidnt(const gen & args);
  void lidnt(const gen & args,vecteur & res);

  gen _lname(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_lname;
  
  gen _halt(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_halt;

  gen _kill(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_kill;

  gen _cont(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_cont;

  gen _sst(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_sst;

  gen _sst_in(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_sst_in;

  gen _debug(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_debug;

  gen _watch(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_watch;

  gen _rmwatch(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_rmwatch;

  gen _breakpoint(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_breakpoint;

  gen _rmbreakpoint(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_rmbreakpoint;

  void debug_loop(gen &res,GIAC_CONTEXT);

  gen _backquote(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_backquote;

  gen _double_deux_points(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_double_deux_points;

  gen _maple2mupad(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_maple2mupad;

  gen _maple2xcas(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_maple2xcas;

  gen _mupad2maple(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_mupad2maple;

  gen _mupad2xcas(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_mupad2xcas;

  gen _cd(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_cd;

  gen _pwd(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_pwd;

  gen _scientific_format(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_scientific_format;
  gen _integer_format(const gen & g,GIAC_CONTEXT);
  gen giac_eval_level(const gen & g,GIAC_CONTEXT); // can not be declared as _eval_level, conflict
  gen _prog_eval_level(const gen & g,GIAC_CONTEXT);
  gen _with_sqrt(const gen & g,GIAC_CONTEXT);

  gen _xcas_mode(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_xcas_mode;
  extern const unary_function_ptr * const  at_maple_mode;

  gen _all_trig_solutions(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_all_trig_solutions;

  gen _ntl_on(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ntl_on;

  gen _complex_mode(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_complex_mode;

  gen _angle_radian(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_angle_radian;
 
  gen _epsilon(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_epsilon;

  gen _proba_epsilon(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_proba_epsilon;

  gen _complex_variables(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_complex_variables;

  gen _approx_mode(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_approx_mode;

  gen _threads(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_threads;
  gen _threads_allowed(const gen & g,GIAC_CONTEXT);
  gen _mpzclass_allowed(const gen & g,GIAC_CONTEXT);

  gen _cas_setup(const gen & args,GIAC_CONTEXT);
  void parent_cas_setup(GIAC_CONTEXT); // send current cas_setup to parent
  extern const unary_function_ptr * const  at_cas_setup;
  bool cas_setup(const vecteur & v,GIAC_CONTEXT);
  vecteur cas_setup(GIAC_CONTEXT);

  gen _Digits(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Digits;

  gen _insmod(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_insmod;
  gen _xport(const gen & args,GIAC_CONTEXT);

  gen _rmmod(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_rmmod;

  gen _lsmod(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_lsmod;

  gen _virgule(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_virgule;

  bool is_binary(const gen & args);
  bool check_binary(const gen & args,gen & a,gen & b);

  gen _sort(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_sort;

  gen _ans(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ans;

  gen _quest(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_quest;

  std::vector<int> float2continued_frac(double d_orig,double eps);
  gen continued_frac2gen(std::vector<int> v,double d_orig,double eps,GIAC_CONTEXT);
  gen _convert(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_convert;

  gen _deuxpoints(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_deuxpoints;

  gen quote_read(const gen & args,GIAC_CONTEXT); // read in a file and return non evaled
  gen _read(const gen & args,GIAC_CONTEXT); // read in a file and return evaled
  extern const unary_function_ptr * const  at_read;

  gen _write(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_write;

  gen _save_history(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_save_history;

  gen _findhelp(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_findhelp;

  gen _member(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_member;

  gen _tablefunc(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_tablefunc;

  gen _tableseq(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_tableseq;

  gen protecteval(const gen & g,int level,GIAC_CONTEXT);

  gen _nodisp(const gen & args);
  extern const unary_function_ptr * const  at_nodisp;

  gen _unapply(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_unapply;

  gen _makevector(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_makevector;

  gen _matrix(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_matrix;

  gen _makesuite(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_makesuite;

  gen _break(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_break;

  gen _continue(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_continue;

  gen _label(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_label;

  gen _goto(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_goto;

  gen _tilocal(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_tilocal;

  gen inputform_post_analysis(const vecteur & v,const gen & res,GIAC_CONTEXT);
  vecteur inputform_pre_analysis(const gen & g,GIAC_CONTEXT);
  gen _inputform(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_inputform;
  class unary_function_eval;
#ifndef RTOS_THREADX
  extern unary_function_eval __inputform;
#endif

  gen _choosebox(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_choosebox;

  gen _output(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_output;
  gen _input(const gen & args,bool textinput,GIAC_CONTEXT);

  gen _nop(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_nop;

  std::string printastifunction(const gen & feuille,const char * sommetstr,GIAC_CONTEXT);

  gen _Dialog(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Dialog;

  gen _Title(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Title;

  gen _Text(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Text;

  gen _Request(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Request;

  gen _DropDown(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_DropDown;

  gen _Popup(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Popup;

  gen _expr(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_expr;

  gen _string(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_string;

  gen _part(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_part;

  gen _Pause(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Pause;

  gen _Row(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Row;
#ifndef RTOS_THREADX
  extern unary_function_eval __Row;
#endif

  gen _Col(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_Col;
#ifndef RTOS_THREADX
  extern unary_function_eval __Col;
#endif

  gen _DelVar(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_DelVar;

  gen prod(const gen &,const gen &);
  gen somme(const gen &,const gen &);
  gen _pointprod(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_pointprod;

  gen _pointdivision(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_pointdivision;

  gen _pointpow(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_pointpow;

  gen _pourcent(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_pourcent;

  gen _hash(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_hash;

  // used to update IO screen and graph inside progs
  gen _interactive(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_interactive;
#ifdef RTOS_THREADX
  extern const alias_unary_function_eval __interactive;
  // extern const unary_function_eval __interactive;
#else
  extern unary_function_eval __interactive;
#endif

  std::string printassuffix(const gen & feuille,const char * sommetstr,GIAC_CONTEXT);

  // translate TI escape sequence
  std::string tiasc_translate(const std::string & s);
  gen matrix_apply(const gen & a,const gen & b,gen (* f) (const gen &, const gen &) );
  gen matrix_apply(const gen & a,const gen & b,GIAC_CONTEXT,gen (* f) (const gen &, const gen &,GIAC_CONTEXT) );

  // v=[ [idnt,value] ... ]
  // search g in v if found return value
  // else return g evaluated
  // and add g to the list according to add_to_folder
  gen find_in_folder(vecteur & v,const gen & g);
  extern gen current_folder_name; // must be an idnt (or a path)
  gen getfold(const gen & g); // translate 0 to "main"

  gen _ti_semi(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_ti_semi;

#ifdef RTOS_THREADX
  // extern const unary_function_eval __keyboard;
#else
  extern unary_function_eval __keyboard;
#endif
  extern const unary_function_ptr * const  at_keyboard;
  gen widget_size(const gen & g,GIAC_CONTEXT);
  gen keyboard(const gen & g,GIAC_CONTEXT);
#ifndef RTOS_THREADX
  extern unary_function_eval __widget_size;
#endif
  extern const unary_function_ptr * const  at_widget_size;

  gen current_sheet(const gen & g,GIAC_CONTEXT);
#ifndef RTOS_THREADX
  extern unary_function_eval __current_sheet;
#endif
  extern const unary_function_ptr * const  at_current_sheet;

  gen window_switch(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_window_switch;
#ifndef RTOS_THREADX
  extern const unary_function_eval __window_switch;
  extern const unary_function_eval __maple_lib;
#endif
  gen maple_lib(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_maple_lib;
  extern const unary_function_ptr * const  at_maple_root;
  gen maple_root(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_unit;

  struct mksa_unit {
    double coeff;
    float m;
    float kg;
    float s;
    float A;
    float K;
    float mol;
    float cd;
    float E;
  };
  extern const mksa_unit * const unitptr_tab[]; // table of units alpha-sorted
  extern const unsigned unitptr_tab_length;
  extern const char * const unitname_tab[];
  extern const char * const * const unitname_tab_end;
  
  gen symb_unit(const gen & a,const gen & b,GIAC_CONTEXT);
  gen symb_interrogation(const gen & e1,const gen & e3);
  std::string printasDigits(const gen & feuille,const char * sommetstr,GIAC_CONTEXT);
  bool first_ascend_sort(const gen & a,const gen & b);
  bool first_descend_sort(const gen & a,const gen & b);

  extern const unary_function_ptr * const  at_user_operator;
  gen user_operator(const gen & g,GIAC_CONTEXT);
  gen _SetFold(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_SetFold;

  gen simplifier(const gen & g,GIAC_CONTEXT);
  gen _simplifier(const gen & g,GIAC_CONTEXT);
  // Unit management
  gen unitpow(const gen & g,const gen & exponent);
  gen mksa_reduce(const gen & g,GIAC_CONTEXT);
  gen chk_not_unit(const gen & g);
  gen find_or_make_symbol(const std::string & s,bool check38,GIAC_CONTEXT);
  std::map<const char *, const mksa_unit *,ltstr> & unit_conversion_map();
  gen mksa_register(const char * s,const mksa_unit * equiv);
  gen mksa_register_unit(const char * s,const mksa_unit * equiv);
  vecteur mksa_convert(const identificateur & g,GIAC_CONTEXT);
  vecteur mksa_convert(const gen & g,GIAC_CONTEXT);
  gen _ufactor(const gen & g,GIAC_CONTEXT);
  gen _usimplify(const gen & g,GIAC_CONTEXT);

  extern const mksa_unit __m_unit;
  extern const mksa_unit __kg_unit;
  extern const mksa_unit __s_unit;
  extern const mksa_unit __A_unit;
  extern const mksa_unit __K_unit;
  extern const mksa_unit __mol_unit;
  extern const mksa_unit __cd_unit;
  extern const mksa_unit __E_unit;
  extern const mksa_unit __Bq_unit;
  extern const mksa_unit __C_unit;
  extern const mksa_unit __F_unit;
  extern const mksa_unit __Gy_unit;
  extern const mksa_unit __H_unit;
  extern const mksa_unit __Hz_unit;
  extern const mksa_unit __J_unit;
  extern const mksa_unit __mho_unit;
  extern const mksa_unit __N_unit;
  extern const mksa_unit __Ohm_unit;
  extern const mksa_unit __Pa_unit;
  extern const mksa_unit __rad_unit;
  extern const mksa_unit __S_unit;
  extern const mksa_unit __Sv_unit;
  extern const mksa_unit __T_unit;
  extern const mksa_unit __V_unit;
  extern const mksa_unit __W_unit;
  extern const mksa_unit __Wb_unit;
  extern const mksa_unit __molK_unit;
  extern const mksa_unit __st_unit;
  extern const mksa_unit __a_unit;
  extern const mksa_unit __acre_unit;
  extern const mksa_unit __arcmin_unit;
  extern const mksa_unit __arcs_unit;
  extern const mksa_unit __atm_unit;
  extern const mksa_unit __au_unit;
  extern const mksa_unit __Angstrom_unit;
  extern const mksa_unit __micron_unit;
  extern const mksa_unit __b_unit;
  extern const mksa_unit __bar_unit;
  extern const mksa_unit __bbl_unit;
  extern const mksa_unit __buUS;
  extern const mksa_unit __bu;
  extern const mksa_unit __Btu_unit;
  extern const mksa_unit __cal_unit;
  extern const mksa_unit __chain_unit;
  extern const mksa_unit __Curie_unit;
  extern const mksa_unit __ct_unit;
  extern const mksa_unit __deg_unit;
  extern const mksa_unit __d_unit;
  extern const mksa_unit __dB_unit;
  extern const mksa_unit __dyn_unit;
  extern const mksa_unit __erg_unit;
  extern const mksa_unit __eV_unit;
  // extern const mksa_unit __degreeF_unit;
  extern const mksa_unit __Rankine_unit;
  extern const mksa_unit __fath_unit;
  extern const mksa_unit __fm_unit;
  extern const mksa_unit __fbm_unit;
  extern const mksa_unit __fc_unit;
  extern const mksa_unit __Fdy_unit;
  extern const mksa_unit __fermi_unit;
  extern const mksa_unit __flam_unit;
  extern const mksa_unit __ft_unit;
  extern const mksa_unit __ftUS_unit;
  extern const mksa_unit __Gal;
  extern const mksa_unit __g_unit;
  extern const mksa_unit __galUS_unit;
  extern const mksa_unit __galC_unit;
  extern const mksa_unit __galUK_unit;
  extern const mksa_unit __gf_unit;
  extern const mksa_unit __gmol_unit;
  extern const mksa_unit __grad_unit;
  extern const mksa_unit __gon_unit;
  extern const mksa_unit __grain_unit;
  extern const mksa_unit __ha_unit;
  extern const mksa_unit __h_unit;
  extern const mksa_unit __hp_unit;
  extern const mksa_unit __in_unit;
  extern const mksa_unit __inHg_unit;
  extern const mksa_unit __inH2O_unit;
  extern const mksa_unit __j_unit;
  extern const mksa_unit __FF_unit;
  extern const mksa_unit __kip_unit;
  extern const mksa_unit __knot_unit;
  extern const mksa_unit __kph_unit;
  extern const mksa_unit __l_unit;
  extern const mksa_unit __L_unit;
  extern const mksa_unit __lam_unit;
  extern const mksa_unit __lb_unit;
  extern const mksa_unit __lbf_unit;
  extern const mksa_unit __lbmol_unit;
  extern const mksa_unit __lbt_unit;
  extern const mksa_unit __lyr_unit;
  extern const mksa_unit __mi_unit;
  extern const mksa_unit __mil_unit;
  extern const mksa_unit __mile_unit;
  extern const mksa_unit __mille_unit;
  extern const mksa_unit __mn_unit;
  extern const mksa_unit __miUS_unit;
  extern const mksa_unit __mmHg_unit;
  extern const mksa_unit __mph_unit;
  extern const mksa_unit __nmi_unit;
  extern const mksa_unit __oz_unit;
  extern const mksa_unit __ozfl_unit;
  extern const mksa_unit __ozt_unit;
  extern const mksa_unit __ozUK_unit;
  extern const mksa_unit __P_unit;
  extern const mksa_unit __pc_unit;
  extern const mksa_unit __pdl_unit;
  extern const mksa_unit __pk_unit;
  extern const mksa_unit __psi_unit;
  extern const mksa_unit __pt_unit;
  extern const mksa_unit __ptUK_unit;
  extern const mksa_unit __liqpt_unit;
  extern const mksa_unit __qt_unit;
  extern const mksa_unit __R_unit;
  extern const mksa_unit __rd_unit;
  extern const mksa_unit __rod_unit;
  extern const mksa_unit __rem_unit;
  extern const mksa_unit __rpm_unit;
  extern const mksa_unit __sb_unit;
  extern const mksa_unit __slug_unit;
  extern const mksa_unit __St_unit;
  extern const mksa_unit __t_unit;
  extern const mksa_unit __tbsp_unit;
  extern const mksa_unit __tex;
  extern const mksa_unit __therm_unit;
  extern const mksa_unit __ton_unit;
  extern const mksa_unit __tonUK_unit;
  extern const mksa_unit __torr_unit;
  extern const mksa_unit __tr_unit;
  extern const mksa_unit __u_unit;
  extern const mksa_unit __yd_unit;
  extern const mksa_unit __yr_unit;
  extern const mksa_unit __tep_unit;
  extern const mksa_unit __toe_unit;
  extern const mksa_unit __cf_unit;
  extern const mksa_unit __tec_unit;
  extern const mksa_unit __lep_unit;
  extern const mksa_unit __bblep_unit;
  extern const mksa_unit __boe_unit;
  extern const mksa_unit __Wh_unit;
  extern const mksa_unit __tepC_unit;
  extern const mksa_unit __tepgC_unit;
  extern const mksa_unit __tepcC_unit;
  extern const mksa_unit __HFCC_unit;
#ifdef NO_PHYSICAL_CONSTANTS
#define _m_unit mksa_register("_m",&__m_unit)
#define _kg_unit mksa_register("_kg",&__kg_unit)
#define _s_unit mksa_register("_s",&__s_unit)
#define _A_unit mksa_register("_A",&__A_unit)
#define _K_unit mksa_register("_K",&__K_unit)
#define _mol_unit mksa_register("_mol",&__mol_unit)
#define _cd_unit mksa_register("_cd",&__cd_unit)
#define _E_unit mksa_register("_E",&__E_unit)
#define _Bq_unit mksa_register("_Bq",&__Bq_unit)
#define _C_unit mksa_register("_C",&__C_unit)
#define _F_unit mksa_register("_F",&__F_unit)
#define _Gy_unit mksa_register("_Gy",&__Gy_unit)
#define _H_unit mksa_register("_H",&__H_unit)
#define _Hz_unit mksa_register("_Hz",&__Hz_unit)
#define _J_unit mksa_register("_J",&__J_unit)
#define _mho_unit mksa_register("_mho",&__mho_unit)
#define _N_unit mksa_register("_N",&__N_unit)
#define _Ohm_unit mksa_register("_Ohm",&__Ohm_unit)
#define _Pa_unit mksa_register("_Pa",&__Pa_unit)
#define _rad_unit mksa_register("_rad",&__rad_unit)
#define _S_unit mksa_register("_S",&__S_unit)
#define _Sv_unit mksa_register("_Sv",&__Sv_unit)
#define _T_unit mksa_register("_T",&__T_unit)
#define _V_unit mksa_register("_V",&__V_unit)
#define _W_unit mksa_register("_W",&__W_unit)
#define _Wb_unit mksa_register("_Wb",&__Wb_unit)
#define _l_unit mksa_register("_l",&__l_unit)
#define _molK_unit mksa_register("_molK",&__molK_unit)
#define _L_unit mksa_register("_L",&__L_unit)
#else
  extern gen _m_unit;
  extern gen _kg_unit;
  extern gen _s_unit;
  extern gen _A_unit;
  extern gen _K_unit;
  extern gen _mol_unit;
  extern gen _cd_unit;
  extern gen _E_unit;
  // other metric units in m,kg,s,A
  extern gen _Bq_unit;
  extern gen _C_unit;
  extern gen _F_unit;
  extern gen _Gy_unit;
  extern gen _H_unit;
  extern gen _Hz_unit;
  extern gen _J_unit;
  extern gen _mho_unit;
  extern gen _N_unit;
  extern gen _Ohm_unit;
  extern gen _Pa_unit;
  extern gen _r_unit;
  extern gen _S_unit;
  extern gen _st_unit;
  extern gen _Sv_unit;
  extern gen _T_unit;
  extern gen _V_unit;
  extern gen _W_unit;
  extern gen _Wb_unit;
  extern gen _l_unit;
  extern gen _molK_unit;
  // useful non metric units
  extern gen _a_unit;
  extern gen _acre_unit;
  extern gen _arcmin_unit;
  extern gen _arcs_unit;
  extern gen _atm_unit;
  extern gen _au_unit;
  extern gen _angstrom_unit;
  extern gen _b_unit;
  extern gen _bar_unit;
  extern gen _bbl_unit;
  extern gen _Btu_unit;
  extern gen _cal_unit;
  extern gen _chain_unit;
  extern gen _Curie_unit;
  extern gen _ct_unit;
  // extern gen _°_unit;
  extern gen _d_unit;
  extern gen _dB_unit;
  extern gen _dyn_unit;
  extern gen _erg_unit;
  extern gen _eV_unit;
  // extern gen _°F_unit;
  extern gen _fath_unit;
  extern gen _fbm_unit;
  // extern gen _fc_unit;
  extern gen _Fdy_unit;
  extern gen _fermi_unit;
  extern gen _flam_unit;
  extern gen _ft_unit;
  extern gen _ftUS_unit;
  extern gen _g_unit;
  extern gen _gal_unit;
  extern gen _galC_unit;
  extern gen _galUK_unit;
  extern gen _gf_unit;
  extern gen _gmol_unit;
  extern gen _grad_unit;
  extern gen _grain_unit;
  extern gen _ha_unit;
  extern gen _h_unit;
  extern gen _hp_unit;
  extern gen _in_unit;
  extern gen _inHg_unit;
  extern gen _inH2O_unit;
  extern gen _FF_unit;
  extern gen _kip_unit;
  extern gen _knot_unit;
  extern gen _kph_unit;
  extern gen _lam_unit;
  extern gen _lb_unit;
  extern gen _lbf_unit;
  extern gen _lbmol_unit;
  extern gen _lbt_unit;
  extern gen _lyr_unit;
  extern gen _mi_unit;
  extern gen _mil_unit;
  extern gen _min_unit;
  extern gen _miUS_unit;
  extern gen _mmHg_unit;
  extern gen _mph_unit;
  extern gen _nmi_unit;
  extern gen _oz_unit;
  extern gen _ozfl_unit;
  extern gen _ozt_unit;
  extern gen _ozUK_unit;
  extern gen _P_unit;
  extern gen _pc_unit;
  extern gen _pdl_unit;
  extern gen _pk_unit;
  extern gen _psi_unit;
  extern gen _pt_unit;
  extern gen _qt_unit;
  extern gen _R_unit;
  extern gen _rad_unit;
  extern gen _rd_unit;
  extern gen _rem_unit;
  extern gen _rpm_unit;
  extern gen _sb_unit;
  extern gen _slug_unit;
  extern gen _St_unit;
  extern gen _t_unit;
  extern gen _tbsp_unit;
  extern gen _therm_unit;
  extern gen _ton_unit;
  extern gen _tonUK_unit;
  extern gen _torr_unit;
  extern gen _u_unit;
  extern gen _yd_unit;
  extern gen _yr_unit;
  // Physical constants, defined in input_parser anyway
  extern gen cst_hbar;
  extern gen cst_clightspeed;
  extern gen cst_ga;
  extern gen cst_IO;
  extern gen cst_epsilonox;
  extern gen cst_epsilonsi;
  extern gen cst_qepsilon0;
  extern gen cst_epsilon0q;
  extern gen cst_kq;
  extern gen cst_c3;
  extern gen cst_lambdac;
  extern gen cst_f0;
  extern gen cst_lambda0;
  extern gen cst_muN;
  extern gen cst_muB;
  extern gen cst_a0;
  extern gen cst_Rinfinity;
  extern gen cst_Faraday;
  extern gen cst_phi;
  extern gen cst_alpha;
  extern gen cst_mpme;
  extern gen cst_mp;
  extern gen cst_qme;
  extern gen cst_me;
  extern gen cst_qe;
  extern gen cst_hPlanck;
  extern gen cst_G;
  extern gen cst_mu0;
  extern gen cst_epsilon0;
  extern gen cst_sigma;
  extern gen cst_StdP;
  extern gen cst_StdT;
  extern gen cst_Rydberg;
  extern gen cst_Vm;
  extern gen cst_kBoltzmann;
  extern gen cst_NA;
#endif // NO_PHYSICAL_CONSTANTS
  const unary_function_ptr * binary_op_tab();

  extern const unary_function_ptr * const  at_piecewise;
  gen _piecewise(const gen & g,GIAC_CONTEXT);

  extern const unary_function_ptr * const  at_geo2d ;
  extern const unary_function_ptr * const  at_geo3d ;
  extern const unary_function_ptr * const  at_spreadsheet ;
  extern const unary_function_ptr * const  at_sialorssinon;
  extern const unary_function_ptr * const  at_pour;

  std::string print_program_syntax(int maple_mode);
  gen when2piecewise(const gen & g,GIAC_CONTEXT);
  gen when2sign(const gen & g,GIAC_CONTEXT);
  gen piecewise2when(const gen & g,GIAC_CONTEXT);
  gen _geo2d(const gen & g,GIAC_CONTEXT);
  gen symb_double_deux_points(const gen & args);
  std::string printasinnerbloc(const gen & feuille,GIAC_CONTEXT);
  gen symb_bloc(const gen & args);
  gen symb_case(const gen & args);
  gen symb_case(const gen & a,const gen & b);
  gen symb_dollar(const gen & args);
  gen symb_local(const gen & a,const gen & b,GIAC_CONTEXT);
  gen symb_local(const gen & args,GIAC_CONTEXT);
  gen symb_check_type(const gen & args,GIAC_CONTEXT);
  gen symb_findhelp(const gen & args);
  symbolic symb_for(const gen & e);
  symbolic symb_for(const gen & a,const gen & b,const gen & c,const gen & d);
  gen symb_try_catch(const gen & args);
  gen symb_args(const gen & args);
  gen symb_intersect(const gen & args);
  gen symb_union(const gen & args);
  gen symb_minus(const gen & args);
  gen symb_compose(const gen & args);

  // test if m(i) is an array index: that will not be the case if
  // i is an _IDNT or a list of _IDNT
  // AND m is not already defined as an array
  bool is_array_index(const gen & m,const gen & i,GIAC_CONTEXT);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_PROG_H

