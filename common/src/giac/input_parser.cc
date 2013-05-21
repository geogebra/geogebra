/* A Bison parser, made by GNU Bison 2.3.  */

/* Skeleton implementation for Bison's Yacc-like parsers in C

   Copyright (C) 1984, 1989, 1990, 2000, 2001, 2002, 2003, 2004, 2005, 2006
   Free Software Foundation, Inc.

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA 02110-1301, USA.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* C LALR(1) parser skeleton written by Richard Stallman, by
   simplifying the original so-called "semantic" parser.  */

/* All symbols defined below should begin with yy or YY, to avoid
   infringing on user name space.  This should be done even for local
   variables, as they might otherwise be expanded by user macros.
   There are some unavoidable exceptions within include files to
   define necessary library symbols; they are noted "INFRINGES ON
   USER NAME SPACE" below.  */

/* Identify Bison output.  */
#define YYBISON 1

/* Bison version.  */
#define YYBISON_VERSION "2.3"

/* Skeleton name.  */
#define YYSKELETON_NAME "yacc.c"

/* Pure parsers.  */
#define YYPURE 1

/* Using locations.  */
#define YYLSP_NEEDED 0

/* Substitute the variable and function names.  */
#define yyparse giac_yyparse
#define yylex   giac_yylex
#define yyerror giac_yyerror
#define yylval  giac_yylval
#define yychar  giac_yychar
#define yydebug giac_yydebug
#define yynerrs giac_yynerrs


/* Tokens.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
   /* Put the tokens into the symbol table, so that GDB and other debuggers
      know about them.  */
   enum yytokentype {
     T_NUMBER = 258,
     T_SYMBOL = 259,
     T_LITERAL = 260,
     T_DIGITS = 261,
     T_STRING = 262,
     T_END_INPUT = 263,
     T_EXPRESSION = 264,
     T_UNARY_OP = 265,
     T_OF = 266,
     T_NOT = 267,
     T_TYPE_ID = 268,
     T_VIRGULE = 269,
     T_AFFECT = 270,
     T_MAPSTO = 271,
     T_BEGIN_PAR = 272,
     T_END_PAR = 273,
     T_PLUS = 274,
     T_MOINS = 275,
     T_FOIS = 276,
     T_DIV = 277,
     T_MOD = 278,
     T_POW = 279,
     T_QUOTED_BINARY = 280,
     T_QUOTE = 281,
     T_PRIME = 282,
     T_TEST_EQUAL = 283,
     T_EQUAL = 284,
     T_INTERVAL = 285,
     T_UNION = 286,
     T_INTERSECT = 287,
     T_MINUS = 288,
     T_AND_OP = 289,
     T_COMPOSE = 290,
     T_DOLLAR = 291,
     T_DOLLAR_MAPLE = 292,
     T_INDEX_BEGIN = 293,
     T_VECT_BEGIN = 294,
     T_VECT_DISPATCH = 295,
     T_VECT_END = 296,
     T_SET_BEGIN = 297,
     T_SET_END = 298,
     T_SEMI = 299,
     T_DEUXPOINTS = 300,
     T_DOUBLE_DEUX_POINTS = 301,
     T_IF = 302,
     T_RPN_IF = 303,
     T_ELIF = 304,
     T_THEN = 305,
     T_ELSE = 306,
     T_IFTE = 307,
     T_SWITCH = 308,
     T_CASE = 309,
     T_DEFAULT = 310,
     T_ENDCASE = 311,
     T_FOR = 312,
     T_FROM = 313,
     T_TO = 314,
     T_DO = 315,
     T_BY = 316,
     T_WHILE = 317,
     T_MUPMAP_WHILE = 318,
     T_RPN_WHILE = 319,
     T_REPEAT = 320,
     T_UNTIL = 321,
     T_IN = 322,
     T_START = 323,
     T_BREAK = 324,
     T_CONTINUE = 325,
     T_TRY = 326,
     T_CATCH = 327,
     T_TRY_CATCH = 328,
     T_PROC = 329,
     T_BLOC = 330,
     T_BLOC_BEGIN = 331,
     T_BLOC_END = 332,
     T_RETURN = 333,
     T_LOCAL = 334,
     T_LOCALBLOC = 335,
     T_NAME = 336,
     T_PROGRAM = 337,
     T_NULL = 338,
     T_ARGS = 339,
     T_FACTORIAL = 340,
     T_RPN_OP = 341,
     T_RPN_BEGIN = 342,
     T_RPN_END = 343,
     T_STACK = 344,
     T_GROUPE_BEGIN = 345,
     T_GROUPE_END = 346,
     T_LINE_BEGIN = 347,
     T_LINE_END = 348,
     T_VECTOR_BEGIN = 349,
     T_VECTOR_END = 350,
     T_CURVE_BEGIN = 351,
     T_CURVE_END = 352,
     T_ROOTOF_BEGIN = 353,
     T_ROOTOF_END = 354,
     T_SPOLY1_BEGIN = 355,
     T_SPOLY1_END = 356,
     T_POLY1_BEGIN = 357,
     T_POLY1_END = 358,
     T_MATRICE_BEGIN = 359,
     T_MATRICE_END = 360,
     T_ASSUME_BEGIN = 361,
     T_ASSUME_END = 362,
     T_HELP = 363,
     TI_DEUXPOINTS = 364,
     TI_LOCAL = 365,
     TI_LOOP = 366,
     TI_FOR = 367,
     TI_WHILE = 368,
     TI_STO = 369,
     TI_TRY = 370,
     TI_DIALOG = 371,
     T_PIPE = 372,
     TI_DEFINE = 373,
     TI_PRGM = 374,
     TI_SEMI = 375,
     TI_HASH = 376,
     T_ACCENTGRAVE = 377,
     T_MAPLELIB = 378,
     T_INTERROGATION = 379,
     T_UNIT = 380,
     T_BIDON = 381,
     T_LOGO = 382,
     T_SQ = 383,
     T_CASE38 = 384,
     T_IFERR = 385,
     T_MOINS38 = 386,
     T_NEG38 = 387,
     T_UNARY_OP_38 = 388,
     T_FUNCTION = 389
   };
#endif
/* Tokens.  */
#define T_NUMBER 258
#define T_SYMBOL 259
#define T_LITERAL 260
#define T_DIGITS 261
#define T_STRING 262
#define T_END_INPUT 263
#define T_EXPRESSION 264
#define T_UNARY_OP 265
#define T_OF 266
#define T_NOT 267
#define T_TYPE_ID 268
#define T_VIRGULE 269
#define T_AFFECT 270
#define T_MAPSTO 271
#define T_BEGIN_PAR 272
#define T_END_PAR 273
#define T_PLUS 274
#define T_MOINS 275
#define T_FOIS 276
#define T_DIV 277
#define T_MOD 278
#define T_POW 279
#define T_QUOTED_BINARY 280
#define T_QUOTE 281
#define T_PRIME 282
#define T_TEST_EQUAL 283
#define T_EQUAL 284
#define T_INTERVAL 285
#define T_UNION 286
#define T_INTERSECT 287
#define T_MINUS 288
#define T_AND_OP 289
#define T_COMPOSE 290
#define T_DOLLAR 291
#define T_DOLLAR_MAPLE 292
#define T_INDEX_BEGIN 293
#define T_VECT_BEGIN 294
#define T_VECT_DISPATCH 295
#define T_VECT_END 296
#define T_SET_BEGIN 297
#define T_SET_END 298
#define T_SEMI 299
#define T_DEUXPOINTS 300
#define T_DOUBLE_DEUX_POINTS 301
#define T_IF 302
#define T_RPN_IF 303
#define T_ELIF 304
#define T_THEN 305
#define T_ELSE 306
#define T_IFTE 307
#define T_SWITCH 308
#define T_CASE 309
#define T_DEFAULT 310
#define T_ENDCASE 311
#define T_FOR 312
#define T_FROM 313
#define T_TO 314
#define T_DO 315
#define T_BY 316
#define T_WHILE 317
#define T_MUPMAP_WHILE 318
#define T_RPN_WHILE 319
#define T_REPEAT 320
#define T_UNTIL 321
#define T_IN 322
#define T_START 323
#define T_BREAK 324
#define T_CONTINUE 325
#define T_TRY 326
#define T_CATCH 327
#define T_TRY_CATCH 328
#define T_PROC 329
#define T_BLOC 330
#define T_BLOC_BEGIN 331
#define T_BLOC_END 332
#define T_RETURN 333
#define T_LOCAL 334
#define T_LOCALBLOC 335
#define T_NAME 336
#define T_PROGRAM 337
#define T_NULL 338
#define T_ARGS 339
#define T_FACTORIAL 340
#define T_RPN_OP 341
#define T_RPN_BEGIN 342
#define T_RPN_END 343
#define T_STACK 344
#define T_GROUPE_BEGIN 345
#define T_GROUPE_END 346
#define T_LINE_BEGIN 347
#define T_LINE_END 348
#define T_VECTOR_BEGIN 349
#define T_VECTOR_END 350
#define T_CURVE_BEGIN 351
#define T_CURVE_END 352
#define T_ROOTOF_BEGIN 353
#define T_ROOTOF_END 354
#define T_SPOLY1_BEGIN 355
#define T_SPOLY1_END 356
#define T_POLY1_BEGIN 357
#define T_POLY1_END 358
#define T_MATRICE_BEGIN 359
#define T_MATRICE_END 360
#define T_ASSUME_BEGIN 361
#define T_ASSUME_END 362
#define T_HELP 363
#define TI_DEUXPOINTS 364
#define TI_LOCAL 365
#define TI_LOOP 366
#define TI_FOR 367
#define TI_WHILE 368
#define TI_STO 369
#define TI_TRY 370
#define TI_DIALOG 371
#define T_PIPE 372
#define TI_DEFINE 373
#define TI_PRGM 374
#define TI_SEMI 375
#define TI_HASH 376
#define T_ACCENTGRAVE 377
#define T_MAPLELIB 378
#define T_INTERROGATION 379
#define T_UNIT 380
#define T_BIDON 381
#define T_LOGO 382
#define T_SQ 383
#define T_CASE38 384
#define T_IFERR 385
#define T_MOINS38 386
#define T_NEG38 387
#define T_UNARY_OP_38 388
#define T_FUNCTION 389




/* Copy the first part of user declarations.  */
#line 25 "input_parser.yy"

         #define YYPARSE_PARAM scanner
         #define YYLEX_PARAM   scanner
	 #line 34 "input_parser.yy"

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#include "first.h"
#include <stdexcept>
#include <cstdlib>
#include "index.h"
#include "gen.h"
#define YYSTYPE giac::gen
#define YY_EXTRA_TYPE  const giac::context *
#include "lexer.h"
#include "input_lexer.h"
#include "usual.h"
#include "derive.h"
#include "sym2poly.h"
#include "vecteur.h"
#include "modpoly.h"
#include "alg_ext.h"
#include "prog.h"
#include "rpn.h"
#include "intg.h"
#include "plot.h"
#include "maple.h"

using namespace std;
#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

// It seems there is a bison bug when it reallocates space for the stack
// therefore I redefine YYINITDEPTH to 1000 (max size is YYMAXDEPTH)
// instead of 200
// Feel free to change if you need but then readjust YYMAXDEPTH
#ifdef RTOS_THREADX
#define YYINITDEPTH 100
#define YYMAXDEPTH 101
#else // RTOS_THREADX
// Note that the compilation by bison with -v option generates a file y.output
// to debug the grammar, compile input_parser.yy with bison
// then add yydebug=1 in input_parser.cc at the beginning of yyparse (
#define YYDEBUG 1
#ifdef GNUWINCE
#define YYINITDEPTH 1000
#else 
#define YYINITDEPTH 20000
#define YYMAXDEPTH 100000
#define YYERROR_VERBOSE 1
#endif // GNUWINCE
#endif // RTOS_THREADX


gen polynome_or_sparse_poly1(const gen & coeff, const gen & index){
  if (index.type==_VECT){
    index_t i;
    const_iterateur it=index._VECTptr->begin(),itend=index._VECTptr->end();
    i.reserve(itend-it);
    for (;it!=itend;++it){
      if (it->type!=_INT_)
         return gentypeerr();
      i.push_back(it->val);
    }
    monomial<gen> m(coeff,i);
    return polynome(m);
  }
  else {
    sparse_poly1 res;
    res.push_back(monome(coeff,index));
    return res;
  }
}


/* Enabling traces.  */
#ifndef YYDEBUG
# define YYDEBUG 0
#endif

/* Enabling verbose error messages.  */
#ifdef YYERROR_VERBOSE
# undef YYERROR_VERBOSE
# define YYERROR_VERBOSE 1
#else
# define YYERROR_VERBOSE 0
#endif

/* Enabling the token table.  */
#ifndef YYTOKEN_TABLE
# define YYTOKEN_TABLE 0
#endif

#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef int YYSTYPE;
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
# define YYSTYPE_IS_TRIVIAL 1
#endif



/* Copy the second part of user declarations.  */


/* Line 216 of yacc.c.  */
#line 459 "y.tab.c"

#ifdef short
# undef short
#endif

#ifdef YYTYPE_UINT8
typedef YYTYPE_UINT8 yytype_uint8;
#else
typedef unsigned char yytype_uint8;
#endif

#ifdef YYTYPE_INT8
typedef YYTYPE_INT8 yytype_int8;
#elif (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
typedef signed char yytype_int8;
#else
typedef short int yytype_int8;
#endif

#ifdef YYTYPE_UINT16
typedef YYTYPE_UINT16 yytype_uint16;
#else
typedef unsigned short int yytype_uint16;
#endif

#ifdef YYTYPE_INT16
typedef YYTYPE_INT16 yytype_int16;
#else
typedef short int yytype_int16;
#endif

#ifndef YYSIZE_T
# ifdef __SIZE_TYPE__
#  define YYSIZE_T __SIZE_TYPE__
# elif defined size_t
#  define YYSIZE_T size_t
# elif ! defined YYSIZE_T && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
#  include <stddef.h> /* INFRINGES ON USER NAME SPACE */
#  define YYSIZE_T size_t
# else
#  define YYSIZE_T unsigned int
# endif
#endif

#define YYSIZE_MAXIMUM ((YYSIZE_T) -1)

#ifndef YY_
# if defined YYENABLE_NLS && YYENABLE_NLS
#  if ENABLE_NLS
#   include <libintl.h> /* INFRINGES ON USER NAME SPACE */
#   define YY_(msgid) dgettext ("bison-runtime", msgid)
#  endif
# endif
# ifndef YY_
#  define YY_(msgid) msgid
# endif
#endif

/* Suppress unused-variable warnings by "using" E.  */
#if ! defined lint || defined __GNUC__
# define YYUSE(e) ((void) (e))
#else
# define YYUSE(e) /* empty */
#endif

/* Identity function, used to suppress warnings about constant conditions.  */
#ifndef lint
# define YYID(n) (n)
#else
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static int
YYID (int i)
#else
static int
YYID (i)
    int i;
#endif
{
  return i;
}
#endif

#if ! defined yyoverflow || YYERROR_VERBOSE

/* The parser invokes alloca or malloc; define the necessary symbols.  */

# ifdef YYSTACK_USE_ALLOCA
#  if YYSTACK_USE_ALLOCA
#   ifdef __GNUC__
#    define YYSTACK_ALLOC __builtin_alloca
#   elif defined __BUILTIN_VA_ARG_INCR
#    include <alloca.h> /* INFRINGES ON USER NAME SPACE */
#   elif defined _AIX
#    define YYSTACK_ALLOC __alloca
#   elif defined _MSC_VER
#    include <malloc.h> /* INFRINGES ON USER NAME SPACE */
#    define alloca _alloca
#   else
#    define YYSTACK_ALLOC alloca
#    if ! defined _ALLOCA_H && ! defined _STDLIB_H && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
#     include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
#     ifndef _STDLIB_H
#      define _STDLIB_H 1
#     endif
#    endif
#   endif
#  endif
# endif

# ifdef YYSTACK_ALLOC
   /* Pacify GCC's `empty if-body' warning.  */
#  define YYSTACK_FREE(Ptr) do { /* empty */; } while (YYID (0))
#  ifndef YYSTACK_ALLOC_MAXIMUM
    /* The OS might guarantee only one guard page at the bottom of the stack,
       and a page size can be as small as 4096 bytes.  So we cannot safely
       invoke alloca (N) if N exceeds 4096.  Use a slightly smaller number
       to allow for a few compiler-allocated temporary stack slots.  */
#   define YYSTACK_ALLOC_MAXIMUM 4032 /* reasonable circa 2006 */
#  endif
# else
#  define YYSTACK_ALLOC YYMALLOC
#  define YYSTACK_FREE YYFREE
#  ifndef YYSTACK_ALLOC_MAXIMUM
#   define YYSTACK_ALLOC_MAXIMUM YYSIZE_MAXIMUM
#  endif
#  if (defined __cplusplus && ! defined _STDLIB_H \
       && ! ((defined YYMALLOC || defined malloc) \
	     && (defined YYFREE || defined free)))
#   include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
#   ifndef _STDLIB_H
#    define _STDLIB_H 1
#   endif
#  endif
#  ifndef YYMALLOC
#   define YYMALLOC malloc
#   if ! defined malloc && ! defined _STDLIB_H && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
void *malloc (YYSIZE_T); /* INFRINGES ON USER NAME SPACE */
#   endif
#  endif
#  ifndef YYFREE
#   define YYFREE free
#   if ! defined free && ! defined _STDLIB_H && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
void free (void *); /* INFRINGES ON USER NAME SPACE */
#   endif
#  endif
# endif
#endif /* ! defined yyoverflow || YYERROR_VERBOSE */


#if (! defined yyoverflow \
     && (! defined __cplusplus \
	 || (defined YYSTYPE_IS_TRIVIAL && YYSTYPE_IS_TRIVIAL)))

/* A type that is properly aligned for any stack member.  */
union yyalloc
{
  yytype_int16 yyss;
  YYSTYPE yyvs;
  };

/* The size of the maximum gap between one aligned stack and the next.  */
# define YYSTACK_GAP_MAXIMUM (sizeof (union yyalloc) - 1)

/* The size of an array large to enough to hold all stacks, each with
   N elements.  */
# define YYSTACK_BYTES(N) \
     ((N) * (sizeof (yytype_int16) + sizeof (YYSTYPE)) \
      + YYSTACK_GAP_MAXIMUM)

/* Copy COUNT objects from FROM to TO.  The source and destination do
   not overlap.  */
# ifndef YYCOPY
#  if defined __GNUC__ && 1 < __GNUC__
#   define YYCOPY(To, From, Count) \
      __builtin_memcpy (To, From, (Count) * sizeof (*(From)))
#  else
#   define YYCOPY(To, From, Count)		\
      do					\
	{					\
	  YYSIZE_T yyi;				\
	  for (yyi = 0; yyi < (Count); yyi++)	\
	    (To)[yyi] = (From)[yyi];		\
	}					\
      while (YYID (0))
#  endif
# endif

/* Relocate STACK from its old location to the new one.  The
   local variables YYSIZE and YYSTACKSIZE give the old and new number of
   elements in the stack, and YYPTR gives the new location of the
   stack.  Advance YYPTR to a properly aligned location for the next
   stack.  */
# define YYSTACK_RELOCATE(Stack)					\
    do									\
      {									\
	YYSIZE_T yynewbytes;						\
	YYCOPY (&yyptr->Stack, Stack, yysize);				\
	Stack = &yyptr->Stack;						\
	yynewbytes = yystacksize * sizeof (*Stack) + YYSTACK_GAP_MAXIMUM; \
	yyptr += yynewbytes / sizeof (*yyptr);				\
      }									\
    while (YYID (0))

#endif

/* YYFINAL -- State number of the termination state.  */
#define YYFINAL  147
/* YYLAST -- Last index in YYTABLE.  */
#define YYLAST   12729

/* YYNTOKENS -- Number of terminals.  */
#define YYNTOKENS  135
/* YYNNTS -- Number of nonterminals.  */
#define YYNNTS  28
/* YYNRULES -- Number of rules.  */
#define YYNRULES  237
/* YYNRULES -- Number of states.  */
#define YYNSTATES  558

/* YYTRANSLATE(YYLEX) -- Bison symbol number corresponding to YYLEX.  */
#define YYUNDEFTOK  2
#define YYMAXUTOK   389

#define YYTRANSLATE(YYX)						\
  ((unsigned int) (YYX) <= YYMAXUTOK ? yytranslate[YYX] : YYUNDEFTOK)

/* YYTRANSLATE[YYLEX] -- Bison symbol number corresponding to YYLEX.  */
static const yytype_uint8 yytranslate[] =
{
       0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     1,     2,     3,     4,
       5,     6,     7,     8,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      25,    26,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    39,    40,    41,    42,    43,    44,
      45,    46,    47,    48,    49,    50,    51,    52,    53,    54,
      55,    56,    57,    58,    59,    60,    61,    62,    63,    64,
      65,    66,    67,    68,    69,    70,    71,    72,    73,    74,
      75,    76,    77,    78,    79,    80,    81,    82,    83,    84,
      85,    86,    87,    88,    89,    90,    91,    92,    93,    94,
      95,    96,    97,    98,    99,   100,   101,   102,   103,   104,
     105,   106,   107,   108,   109,   110,   111,   112,   113,   114,
     115,   116,   117,   118,   119,   120,   121,   122,   123,   124,
     125,   126,   127,   128,   129,   130,   131,   132,   133,   134
};

#if YYDEBUG
/* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
   YYRHS.  */
static const yytype_uint16 yyprhs[] =
{
       0,     0,     3,     5,     8,    12,    16,    18,    21,    26,
      30,    33,    35,    37,    44,    51,    58,    65,    74,    78,
      82,    86,    90,    94,    98,   102,   107,   112,   117,   119,
     121,   123,   127,   131,   135,   139,   142,   146,   150,   154,
     158,   162,   166,   170,   174,   178,   182,   185,   188,   191,
     197,   201,   203,   207,   210,   215,   220,   222,   227,   232,
     237,   241,   243,   246,   249,   256,   261,   267,   272,   274,
     279,   281,   285,   289,   294,   296,   299,   301,   305,   307,
     309,   317,   327,   337,   345,   355,   357,   361,   366,   372,
     380,   384,   386,   390,   393,   399,   403,   407,   410,   414,
     418,   422,   426,   430,   432,   436,   441,   448,   455,   459,
     463,   467,   469,   472,   476,   479,   483,   486,   488,   490,
     493,   495,   499,   504,   506,   513,   521,   525,   527,   532,
     540,   549,   558,   567,   577,   587,   598,   603,   609,   616,
     622,   628,   636,   641,   643,   651,   656,   661,   665,   667,
     670,   674,   679,   685,   690,   697,   703,   707,   721,   734,
     747,   756,   760,   763,   766,   774,   788,   798,   804,   810,
     812,   816,   820,   824,   828,   834,   837,   841,   844,   847,
     848,   851,   854,   859,   862,   866,   870,   872,   876,   878,
     882,   886,   888,   892,   894,   896,   897,   899,   900,   902,
     904,   907,   910,   911,   914,   918,   920,   921,   924,   925,
     928,   931,   934,   936,   938,   939,   943,   946,   950,   955,
     957,   961,   967,   974,   976,   979,   981,   984,   985,   989,
     995,   996,   999,  1005,  1006,  1009,  1016,  1024
};

/* YYRHS -- A `-1'-separated list of the rules' RHS.  */
static const yytype_int16 yyrhs[] =
{
     136,     0,    -1,   137,    -1,   138,     8,    -1,   138,    44,
       8,    -1,   138,    44,   137,    -1,     3,    -1,     3,     4,
      -1,     3,     4,    24,   138,    -1,     3,     4,   128,    -1,
       3,     5,    -1,     7,    -1,     9,    -1,   139,    17,   147,
      18,    15,   155,    -1,   139,    17,   147,    18,    15,   138,
      -1,   138,   114,   139,    17,   147,    18,    -1,   138,   114,
     139,    38,   138,    41,    -1,   138,   114,   139,    38,    40,
     138,    41,    41,    -1,   138,   114,   139,    -1,   138,   114,
      10,    -1,   138,   114,    19,    -1,   138,   114,    21,    -1,
     138,   114,    22,    -1,   138,   114,    14,    -1,   138,   114,
     114,    -1,   138,   114,   125,   138,    -1,   139,    17,   147,
      18,    -1,   138,    17,   147,    18,    -1,   139,    -1,     5,
      -1,     6,    -1,     6,    15,   138,    -1,   138,   114,     6,
      -1,   138,    28,   138,    -1,   138,    29,   138,    -1,    29,
     138,    -1,   138,    19,   138,    -1,   138,    20,   138,    -1,
     138,   131,   138,    -1,   138,    21,   138,    -1,   138,    22,
     138,    -1,   138,    24,   138,    -1,   138,    23,   138,    -1,
     138,    30,   138,    -1,   138,    34,   138,    -1,   138,    45,
     138,    -1,    20,   138,    -1,   132,   138,    -1,    19,   138,
      -1,   100,   138,    14,   138,   101,    -1,    98,   138,    99,
      -1,    11,    -1,   138,    15,   138,    -1,    12,   138,    -1,
      84,    17,   138,    18,    -1,    84,    38,   138,    41,    -1,
      84,    -1,    10,    17,   138,    18,    -1,   133,    17,   138,
      18,    -1,    10,    38,   138,    41,    -1,    10,    17,    18,
      -1,    10,    -1,   138,    27,    -1,   138,    85,    -1,    47,
     138,    50,   155,    51,   155,    -1,    47,   138,    50,   155,
      -1,    47,   138,    50,   148,   156,    -1,    52,    17,   138,
      18,    -1,    52,    -1,    82,    17,   138,    18,    -1,    82,
      -1,   138,    16,   155,    -1,   138,    16,   138,    -1,    75,
      17,   138,    18,    -1,    75,    -1,    78,   138,    -1,    78,
      -1,    26,    78,    26,    -1,    69,    -1,    70,    -1,    57,
     139,    67,   138,    60,   148,    77,    -1,    57,   139,   152,
      59,   138,   151,   153,   148,    77,    -1,    57,   139,   152,
     151,    59,   138,    60,   148,    77,    -1,    57,   139,   152,
     151,    60,   148,    77,    -1,    57,   139,   152,   151,    63,
     138,    60,   148,    77,    -1,    57,    -1,    60,   148,    77,
      -1,    65,   148,    66,   138,    -1,    65,   148,    66,   138,
      77,    -1,   130,   148,    50,   148,    51,   148,    77,    -1,
     129,   161,    77,    -1,    13,    -1,    26,    13,    26,    -1,
      37,   138,    -1,   138,    37,   139,    67,   138,    -1,   138,
      37,   138,    -1,   138,    36,   138,    -1,    36,     4,    -1,
     138,    35,   138,    -1,   138,    31,   138,    -1,   138,    32,
     138,    -1,   138,    33,   138,    -1,   138,   117,   138,    -1,
      25,    -1,    26,   138,    26,    -1,   138,    38,   138,    41,
      -1,   138,    38,    40,   138,    41,    41,    -1,    17,   138,
      18,    17,   147,    18,    -1,    17,   138,    18,    -1,    40,
     147,    41,    -1,   138,    14,   138,    -1,    83,    -1,   108,
     138,    -1,   138,   124,   138,    -1,   125,   138,    -1,   138,
     125,   138,    -1,   138,   128,    -1,     1,    -1,   141,    -1,
     127,   138,    -1,   127,    -1,   127,    17,    18,    -1,    80,
      17,   138,    18,    -1,    80,    -1,    47,    17,   138,    18,
     155,   154,    -1,    47,    17,   138,    18,   138,    44,   154,
      -1,    87,   149,    88,    -1,   123,    -1,   123,    38,   138,
      41,    -1,    74,    17,   147,    18,   140,   148,    77,    -1,
      74,   139,    17,   147,    18,   140,   148,    77,    -1,    74,
      17,   147,    18,   140,    76,   148,    77,    -1,   139,    17,
     147,    18,    74,   140,   148,    77,    -1,   139,    17,   147,
      18,    15,    74,   140,   148,    77,    -1,    57,    17,   146,
      44,   146,    44,   146,    18,   155,    -1,    57,    17,   146,
      44,   146,    44,   146,    18,   138,    44,    -1,    57,    17,
     138,    18,    -1,    62,    17,   138,    18,   155,    -1,    62,
      17,   138,    18,   138,    44,    -1,    62,   138,    60,   148,
      77,    -1,    63,   138,    60,   148,    77,    -1,    71,   155,
      72,    17,   138,    18,   155,    -1,    73,    17,   138,    18,
      -1,    73,    -1,    53,    17,   138,    18,    76,   159,    77,
      -1,    54,    17,     4,    18,    -1,    54,   138,   160,    56,
      -1,   122,   150,   122,    -1,    86,    -1,    78,   109,    -1,
     111,   148,   157,    -1,    47,   138,   109,   138,    -1,   115,
     148,    51,   148,   157,    -1,   115,   148,    51,   157,    -1,
     115,   148,   109,    51,   148,   157,    -1,   115,   148,   109,
      51,   157,    -1,   138,   120,   138,    -1,   109,   139,    17,
     147,    18,   119,   148,   109,   110,   147,   109,   148,   157,
      -1,   109,   139,    17,   147,    18,   119,   148,   110,   147,
     109,   148,   157,    -1,   109,   139,    17,   147,    18,   119,
     109,   110,   147,   109,   148,   157,    -1,   109,   139,    17,
     147,    18,   119,   148,   157,    -1,   116,   148,   157,    -1,
     116,   155,    -1,   109,   138,    -1,   118,   139,    17,   147,
      18,    29,   138,    -1,   118,   139,    17,   147,    18,    29,
     119,   109,   110,   147,   109,   148,   157,    -1,   118,   139,
      17,   147,    18,    29,   119,   148,   157,    -1,   112,   147,
     109,   148,   157,    -1,   113,   138,   109,   148,   157,    -1,
       4,    -1,     4,    46,    13,    -1,     4,    46,    10,    -1,
       4,    46,     4,    -1,     4,    46,   133,    -1,     4,    46,
      26,   138,    26,    -1,    46,     4,    -1,     3,    46,     4,
      -1,    13,     4,    -1,   121,   138,    -1,    -1,   140,   142,
      -1,   143,   140,    -1,    89,    17,   138,    18,    -1,    89,
      83,    -1,    79,   144,    44,    -1,    81,   138,    44,    -1,
     145,    -1,   144,    14,   145,    -1,   139,    -1,     4,    15,
     138,    -1,    17,   145,    18,    -1,    10,    -1,    10,    46,
     138,    -1,    13,    -1,     3,    -1,    -1,   138,    -1,    -1,
     138,    -1,   138,    -1,   148,   138,    -1,   148,   162,    -1,
      -1,   150,   149,    -1,   150,    14,   149,    -1,    10,    -1,
      -1,    61,   138,    -1,    -1,    15,   138,    -1,    29,   138,
      -1,    58,   138,    -1,    44,    -1,    60,    -1,    -1,   158,
     138,    44,    -1,   158,   155,    -1,    76,   148,    77,    -1,
      76,   140,   148,    77,    -1,   157,    -1,   158,   148,   157,
      -1,    49,   138,    50,   148,   156,    -1,   109,    49,   138,
      50,   148,   156,    -1,    77,    -1,   109,    77,    -1,    51,
      -1,   109,    51,    -1,    -1,    55,    45,   155,    -1,    54,
       3,    45,   155,   159,    -1,    -1,    55,   148,    -1,    11,
       3,    60,   148,   160,    -1,    -1,    55,   148,    -1,    47,
     138,    50,   148,    77,   161,    -1,    47,   138,    50,   148,
      77,    44,   161,    -1,    44,    -1
};

/* YYRLINE[YYN] -- source line where rule number YYN was defined.  */
static const yytype_uint16 yyrline[] =
{
       0,   186,   186,   194,   195,   196,   199,   200,   201,   202,
     203,   205,   206,   209,   210,   211,   212,   216,   220,   221,
     222,   223,   224,   225,   226,   227,   228,   229,   230,   231,
     232,   233,   234,   235,   236,   237,   243,   245,   246,   247,
     248,   249,   250,   251,   254,   255,   256,   261,   266,   272,
     273,   279,   280,   281,   282,   283,   284,   285,   295,   300,
     304,   311,   314,   315,   316,   317,   318,   321,   322,   323,
     324,   325,   329,   336,   337,   339,   340,   341,   343,   344,
     345,   356,   369,   382,   386,   390,   391,   395,   400,   406,
     410,   411,   415,   416,   417,   418,   419,   420,   421,   422,
     423,   424,   425,   428,   429,   434,   438,   442,   443,   460,
     465,   470,   471,   472,   473,   478,   482,   483,   492,   493,
     494,   495,   496,   500,   501,   504,   509,   510,   511,   512,
     517,   522,   527,   532,   537,   538,   539,   540,   544,   547,
     551,   555,   556,   557,   558,   559,   560,   561,   562,   563,
     564,   565,   566,   567,   568,   569,   570,   571,   575,   579,
     583,   586,   587,   588,   589,   590,   594,   595,   614,   626,
     627,   632,   633,   634,   635,   636,   637,   645,   650,   654,
     655,   656,   660,   661,   664,   667,   670,   671,   678,   679,
     680,   681,   682,   683,   691,   701,   702,   705,   706,   709,
     711,   716,   719,   720,   721,   724,   794,   795,   798,   799,
     800,   801,   804,   805,   808,   809,   810,   814,   817,   824,
     825,   829,   832,   837,   838,   841,   842,   845,   846,   847,
     850,   851,   852,   855,   856,   857,   858,   861
};
#endif

#if YYDEBUG || YYERROR_VERBOSE || YYTOKEN_TABLE
/* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
   First, the terminals, then, starting at YYNTOKENS, nonterminals.  */
static const char *const yytname[] =
{
  "$end", "error", "$undefined", "T_NUMBER", "T_SYMBOL", "T_LITERAL",
  "T_DIGITS", "T_STRING", "T_END_INPUT", "T_EXPRESSION", "T_UNARY_OP",
  "T_OF", "T_NOT", "T_TYPE_ID", "T_VIRGULE", "T_AFFECT", "T_MAPSTO",
  "T_BEGIN_PAR", "T_END_PAR", "T_PLUS", "T_MOINS", "T_FOIS", "T_DIV",
  "T_MOD", "T_POW", "T_QUOTED_BINARY", "T_QUOTE", "T_PRIME",
  "T_TEST_EQUAL", "T_EQUAL", "T_INTERVAL", "T_UNION", "T_INTERSECT",
  "T_MINUS", "T_AND_OP", "T_COMPOSE", "T_DOLLAR", "T_DOLLAR_MAPLE",
  "T_INDEX_BEGIN", "T_VECT_BEGIN", "T_VECT_DISPATCH", "T_VECT_END",
  "T_SET_BEGIN", "T_SET_END", "T_SEMI", "T_DEUXPOINTS",
  "T_DOUBLE_DEUX_POINTS", "T_IF", "T_RPN_IF", "T_ELIF", "T_THEN", "T_ELSE",
  "T_IFTE", "T_SWITCH", "T_CASE", "T_DEFAULT", "T_ENDCASE", "T_FOR",
  "T_FROM", "T_TO", "T_DO", "T_BY", "T_WHILE", "T_MUPMAP_WHILE",
  "T_RPN_WHILE", "T_REPEAT", "T_UNTIL", "T_IN", "T_START", "T_BREAK",
  "T_CONTINUE", "T_TRY", "T_CATCH", "T_TRY_CATCH", "T_PROC", "T_BLOC",
  "T_BLOC_BEGIN", "T_BLOC_END", "T_RETURN", "T_LOCAL", "T_LOCALBLOC",
  "T_NAME", "T_PROGRAM", "T_NULL", "T_ARGS", "T_FACTORIAL", "T_RPN_OP",
  "T_RPN_BEGIN", "T_RPN_END", "T_STACK", "T_GROUPE_BEGIN", "T_GROUPE_END",
  "T_LINE_BEGIN", "T_LINE_END", "T_VECTOR_BEGIN", "T_VECTOR_END",
  "T_CURVE_BEGIN", "T_CURVE_END", "T_ROOTOF_BEGIN", "T_ROOTOF_END",
  "T_SPOLY1_BEGIN", "T_SPOLY1_END", "T_POLY1_BEGIN", "T_POLY1_END",
  "T_MATRICE_BEGIN", "T_MATRICE_END", "T_ASSUME_BEGIN", "T_ASSUME_END",
  "T_HELP", "TI_DEUXPOINTS", "TI_LOCAL", "TI_LOOP", "TI_FOR", "TI_WHILE",
  "TI_STO", "TI_TRY", "TI_DIALOG", "T_PIPE", "TI_DEFINE", "TI_PRGM",
  "TI_SEMI", "TI_HASH", "T_ACCENTGRAVE", "T_MAPLELIB", "T_INTERROGATION",
  "T_UNIT", "T_BIDON", "T_LOGO", "T_SQ", "T_CASE38", "T_IFERR",
  "T_MOINS38", "T_NEG38", "T_UNARY_OP_38", "T_FUNCTION", "$accept",
  "input", "correct_input", "exp", "symbol", "entete", "stack", "local",
  "nom", "suite_symbol", "affectable_symbol", "exp_or_empty", "suite",
  "prg_suite", "rpn_suite", "rpn_token", "step", "from", "loop38_do",
  "else", "bloc", "elif", "ti_bloc_end", "ti_else", "switch", "case",
  "case38", "semi", 0
};
#endif

# ifdef YYPRINT
/* YYTOKNUM[YYLEX-NUM] -- Internal token number corresponding to
   token YYLEX-NUM.  */
static const yytype_uint16 yytoknum[] =
{
       0,   256,   257,   258,   259,   260,   261,   262,   263,   264,
     265,   266,   267,   268,   269,   270,   271,   272,   273,   274,
     275,   276,   277,   278,   279,   280,   281,   282,   283,   284,
     285,   286,   287,   288,   289,   290,   291,   292,   293,   294,
     295,   296,   297,   298,   299,   300,   301,   302,   303,   304,
     305,   306,   307,   308,   309,   310,   311,   312,   313,   314,
     315,   316,   317,   318,   319,   320,   321,   322,   323,   324,
     325,   326,   327,   328,   329,   330,   331,   332,   333,   334,
     335,   336,   337,   338,   339,   340,   341,   342,   343,   344,
     345,   346,   347,   348,   349,   350,   351,   352,   353,   354,
     355,   356,   357,   358,   359,   360,   361,   362,   363,   364,
     365,   366,   367,   368,   369,   370,   371,   372,   373,   374,
     375,   376,   377,   378,   379,   380,   381,   382,   383,   384,
     385,   386,   387,   388,   389
};
# endif

/* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
static const yytype_uint8 yyr1[] =
{
       0,   135,   136,   137,   137,   137,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   140,
     140,   140,   141,   141,   142,   143,   144,   144,   145,   145,
     145,   145,   145,   145,   145,   146,   146,   147,   147,   148,
     148,   148,   149,   149,   149,   150,   151,   151,   152,   152,
     152,   152,   153,   153,   154,   154,   154,   155,   155,   156,
     156,   156,   156,   157,   157,   158,   158,   159,   159,   159,
     160,   160,   160,   161,   161,   161,   161,   162
};

/* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
static const yytype_uint8 yyr2[] =
{
       0,     2,     1,     2,     3,     3,     1,     2,     4,     3,
       2,     1,     1,     6,     6,     6,     6,     8,     3,     3,
       3,     3,     3,     3,     3,     4,     4,     4,     1,     1,
       1,     3,     3,     3,     3,     2,     3,     3,     3,     3,
       3,     3,     3,     3,     3,     3,     2,     2,     2,     5,
       3,     1,     3,     2,     4,     4,     1,     4,     4,     4,
       3,     1,     2,     2,     6,     4,     5,     4,     1,     4,
       1,     3,     3,     4,     1,     2,     1,     3,     1,     1,
       7,     9,     9,     7,     9,     1,     3,     4,     5,     7,
       3,     1,     3,     2,     5,     3,     3,     2,     3,     3,
       3,     3,     3,     1,     3,     4,     6,     6,     3,     3,
       3,     1,     2,     3,     2,     3,     2,     1,     1,     2,
       1,     3,     4,     1,     6,     7,     3,     1,     4,     7,
       8,     8,     8,     9,     9,    10,     4,     5,     6,     5,
       5,     7,     4,     1,     7,     4,     4,     3,     1,     2,
       3,     4,     5,     4,     6,     5,     3,    13,    12,    12,
       8,     3,     2,     2,     7,    13,     9,     5,     5,     1,
       3,     3,     3,     3,     5,     2,     3,     2,     2,     0,
       2,     2,     4,     2,     3,     3,     1,     3,     1,     3,
       3,     1,     3,     1,     1,     0,     1,     0,     1,     1,
       2,     2,     0,     2,     3,     1,     0,     2,     0,     2,
       2,     2,     1,     1,     0,     3,     2,     3,     4,     1,
       3,     5,     6,     1,     2,     1,     2,     0,     3,     5,
       0,     2,     5,     0,     2,     6,     7,     1
};

/* YYDEFACT[STATE-NAME] -- Default rule to reduce with in state
   STATE-NUM when YYTABLE doesn't specify something else to do.  Zero
   means the default is an error.  */
static const yytype_uint8 yydefact[] =
{
       0,   117,     6,   169,    29,    30,    11,    12,    61,    51,
       0,    91,     0,     0,     0,   103,     0,     0,     0,     0,
       0,     0,     0,    68,     0,     0,    85,     0,     0,     0,
       0,    78,    79,     0,   143,     0,    74,     0,   123,    70,
     111,    56,   148,   202,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,   127,     0,     0,
     233,     0,     0,     0,     0,     2,     0,    28,   118,     7,
      10,     0,     0,     0,     0,     0,    53,   177,     0,    48,
      46,    91,     0,     0,    35,    97,    93,   198,     0,   175,
       0,     0,     0,     0,     0,   230,     0,     0,     0,   208,
     199,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,    75,     0,     0,     0,     0,   205,     0,
     202,     0,   183,     0,     0,   112,   163,    28,     0,     0,
       0,     0,     0,   162,     0,   178,     0,     0,   114,     0,
     119,     0,     0,     0,     0,    47,     0,     1,     3,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,    62,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,    63,     0,     0,     0,     0,     0,   116,
       0,     0,     0,     9,   176,   172,   171,   170,     0,   173,
      31,    60,     0,     0,   108,    92,     0,   104,   109,     0,
       0,     0,     0,     0,   169,     0,     0,     0,   196,     0,
       0,     0,     0,     0,   206,   237,    86,   200,   201,     0,
       0,     0,     0,     0,     0,   179,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,   126,   202,   203,     0,
      50,     0,     0,   223,     0,   150,     0,     0,     0,     0,
     161,     0,   147,     0,   121,     0,     0,    90,     0,     0,
     110,    52,    72,    71,     0,    36,    37,    39,    40,    42,
      41,    33,    34,    43,    99,   100,   101,    44,    98,    96,
      95,    28,     0,     0,     4,     5,    45,    32,    19,    23,
      20,    21,    22,    24,     0,    18,   102,   156,   113,   115,
      38,     0,     8,     0,    57,    59,     0,     0,     0,    65,
     151,    67,     0,   145,     0,     0,   146,   136,     0,   209,
     210,   211,     0,     0,     0,     0,     0,     0,     0,    87,
       0,     0,   180,     0,   181,   217,     0,   142,   179,     0,
      73,   122,    69,    54,    55,   204,   182,   110,     0,   224,
       0,     0,     0,   153,     0,     0,   128,     0,     0,    58,
      27,     0,     0,   105,    25,     0,     0,    26,   174,     0,
       0,     0,   214,     0,   225,     0,    66,   219,     0,     0,
     227,     0,   196,     0,     0,   206,   207,     0,     0,     0,
       0,   137,   139,   140,    88,   185,   194,   169,   191,   193,
       0,   188,     0,   186,   218,     0,     0,   179,    49,    26,
     167,   168,   152,     0,   155,     0,     0,     0,    94,     0,
       0,     0,     0,     0,   179,   107,     0,   214,     0,   124,
       0,     0,     0,   226,     0,    64,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,   138,     0,     0,     0,
       0,   184,     0,     0,     0,     0,     0,   154,     0,   233,
       0,   106,    15,     0,    16,   179,    14,    13,     0,   125,
       0,   216,     0,     0,   220,     0,     0,   144,    51,   232,
       0,    80,   212,   213,     0,     0,    83,     0,   189,   192,
     190,   187,   141,     0,   129,     0,     0,     0,     0,   164,
     233,   235,    89,     0,     0,     0,   215,     0,     0,     0,
     228,     0,     0,     0,     0,   131,   130,     0,     0,     0,
     160,     0,     0,   236,    17,     0,   132,   221,     0,   227,
       0,   134,    81,    82,    84,     0,     0,     0,     0,   166,
     133,   222,   229,   135,     0,     0,     0,     0,     0,     0,
       0,     0,   159,     0,   158,     0,   157,   165
};

/* YYDEFGOTO[NTERM-NUM].  */
static const yytype_int16 yydefgoto[] =
{
      -1,    64,    65,   100,    67,   224,    68,   332,   225,   402,
     403,   209,    88,   101,   119,   120,   325,   214,   484,   429,
     107,   376,   377,   378,   438,   207,   143,   218
};

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
#define YYPACT_NINF -452
static const yytype_int16 yypact[] =
{
    7936,  -452,    81,    -8,  -452,    37,  -452,  -452,   162,  -452,
    7936,    67,  7936,  7936,  7936,  -452,  8069,  7936,    83,  7936,
    5808,    93,  8202,    96,   103,  8335,   216,  7936,  8468,  7936,
    7936,  -452,  -452,    24,   107,   221,   112,   887,   116,   118,
    -452,   189,  -452,   138,   -11,  7936,  7936,  7936,  7936,  7936,
    7936,  7936,  7936,  5941,    76,  7936,   138,   121,  7936,  1020,
      41,  7936,  7936,   156,   183,  -452,  9253,   173,  -452,   -21,
    -452,   188,     5,  7936,  6074,  7936,     6,  -452,  9444,     6,
       6,    99,  1552,  9471, 12446,  -452, 10259, 12007,   152,  -452,
    7936,  9326,  7936,  7936,  8601,  9289,   153,    67,  6207,    11,
   12007,  2217,  7936,  9589,  9623,  2350,  2483,   126,  7936,  6340,
     192,  7936,  1153, 12007,  7936,  7936,  7936,  7936,  -452,   155,
      25,  7936,  -452,  9741, 12125, 12007, 12007,   223,  2616,   137,
    9768,  2749,  2616,  -452,   230,   109,   128,  7936,   167,  6473,
   12446,  7936,  7936,   171,  2882,     6,  7936,  -452,  -452,  7936,
    7936,  5941,  6340,  7936,  7936,  7936,  7936,  7936,  7936,  -452,
    7936,  7936,  7936,  7936,  7936,  7936,  7936,  7936,  7936,  7936,
    8734,  6606,  7936,  -452,   267,  7936,  7936,  7936,  7936,  -452,
    7936,  6340,  7936,  -452,  -452,  -452,  -452,  -452,  7936,  -452,
   12185,  -452,  9801,  9919,   236,  -452,   754,  -452,  -452,  9952,
    5941,  7936,  9993, 10111,    14,   252,  7936,   203, 10144,   219,
    7936,  7936,  7936,  7936,    45,  -452,  -452, 12007,  -452, 10185,
    7936,  7936,  7936,  7936,  6739,   187,  3015,   248, 10303,   251,
    6340, 10336, 10377, 10495, 10528, 10555,  -452,   138,  -452, 10673,
    -452,  7936,  6340,  -452,  6872,  -452,  7936,  7936,  7005,  7138,
    -452,  6340,  -452, 10700,  -452, 10733,  3148,  -452,  7936, 10851,
   11096, 12185,   615,  -452,   256, 12579, 12579,   308,   321, 12601,
     336, 12482, 10259,   454, 12502, 12559, 12524, 10067,   109,   266,
   10259,   -13,  5808, 10878,  -452,  -452, 12446,  -452,  -452,  -452,
    -452,  -452,  -452,  -452,  7936,   198, 12152,  9504, 12369,   167,
   12579,   257,   336, 10911,  -452,  -452,  6340,  1286,  1685,   225,
   12007,  -452,   202,  -452,   224,  3281,  -452,  -452,  6207, 12007,
   12007, 12007, 11029,  7936,  7936,   158,  1419,  3414,  3547, 11063,
   11181,    53,  -452,  3680,   200,  -452,  7936,  -452,   187,   269,
    -452,  -452,  -452,  -452,  -452,  -452,  -452, 12333,   273,  -452,
    2616,  2616,  2616,  -452,  7005,   278,  -452,  7936,  3813,  -452,
    -452,  7936, 11214,  -452,   167,  6340,  8867,     9,  -452,   279,
    6340, 11248,   -44,  7936,  -452,  1818,  -452,  -452,  7936,    24,
      88,  7936, 12007,   241,  7936, 11366, 12007,  7936,  7936,  7936,
   11402,  -452,  -452,  -452,  -452,  -452,   153,    38,   253,    67,
      53,  -452,    20,  -452,  -452, 11439,  3946,   187,  -452,    21,
    -452,  -452,  -452,  2616,  -452,   271,  4079,  7936, 12007,   262,
     280,  5808, 11557,  7271,   187,  -452,  9444,   -44,   258,  -452,
    5941, 11590,  7936,  -452,  2616,  -452,   309,   272,   237,  1951,
    7404,  4212,   -17, 11631,  4345, 11749,  -452,  7936,  7936,   298,
      53,  -452,    24,  7936,  4478,  6739,  9000,  -452,  7537,   184,
    4611,  -452,  -452, 11783,  -452,    64, 12185,  -452,  6739,  -452,
   11816,  -452,  7936, 11934,  -452,   274,    24,  -452,   252,  -452,
     302,  -452,  -452,  -452,  7936,  7936,  -452,  7936, 12007, 12302,
    -452,  -452,  -452,  4744,  -452,  4877,  7670,  2084,  9133, 10259,
      41,  -452,  -452,   299,  6739,  5010,  -452,  1685,  7936,    24,
    -452,  5941,  5143,  5276,  5409,  -452,  -452,  7936,  5542,  7936,
    -452,  7803,  2616,  -452,  -452,  5675,  -452,  -452,  1685,    88,
   11971,  -452,  -452,  -452,  -452,   212,  7936,   232,  7936,  -452,
    -452,  -452,  -452,  -452,  7936,   240,  7936,   246,  2616,  7936,
    2616,  7936,  -452,  2616,  -452,  2616,  -452,  -452
};

/* YYPGOTO[NTERM-NUM].  */
static const yytype_int16 yypgoto[] =
{
    -452,  -452,   168,     0,   160,  -220,  -452,  -452,  -452,  -452,
    -389,  -317,    80,   205,  -118,   306,   -18,  -452,  -452,   -63,
     154,  -291,   132,  -351,  -161,   -70,  -451,  -452
};

/* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule which
   number is the opposite.  If zero, do what YYDEFACT says.
   If YYTABLE_NINF, syntax error.  */
#define YYTABLE_NINF -235
static const yytype_int16 yytable[] =
{
      66,   383,   238,   182,   181,   334,   121,   374,   501,   185,
      76,   449,    78,    79,    80,   186,    83,    84,   187,    86,
      87,   430,    91,   152,   423,    95,   210,   482,   103,   104,
     158,   188,   313,   159,   450,   118,   423,   113,    72,   237,
     211,   167,   168,   483,   170,   123,   124,   125,   126,   523,
      87,   130,    73,   447,   361,   135,   396,   397,   138,   140,
      72,   491,   145,   398,   451,   428,   399,    96,     3,   212,
     400,    77,   122,   190,   192,   193,   430,    97,   213,    96,
       3,   109,   113,   424,    72,    69,    70,    85,   141,    97,
     199,   173,   202,   203,    78,   424,   142,    89,   208,    21,
     106,   217,   219,    77,   323,   217,   324,   183,   228,    87,
      21,   231,   126,    92,   232,   233,   234,   235,   406,   345,
      93,   239,    21,   480,   108,   195,   152,    71,   217,   111,
     129,   217,   217,   114,   179,   115,   159,   253,   189,    78,
     456,   255,   436,   437,   217,   223,   259,   170,   118,   260,
     261,   262,    87,   265,   266,   267,   268,   269,   270,   137,
     271,   272,   273,   274,   275,   276,   277,   278,   279,   280,
     283,    66,   286,   146,    55,   296,   297,   298,   299,    74,
     300,    87,   302,   147,   152,    55,    99,   455,   303,   229,
     181,   158,   184,   198,   159,   110,    83,    55,   227,    71,
      75,   310,   167,   168,   468,   170,   116,   133,   127,   230,
     319,   320,   321,   322,   134,   365,   527,   387,   388,    96,
       3,   389,   329,   330,    96,     3,   217,   117,   500,    97,
      87,   141,   264,    98,    97,   105,   366,   541,   109,   142,
     242,   347,    87,   236,   126,   504,   246,   251,   257,   126,
     252,    87,   173,   306,   128,   314,   217,   131,   132,   316,
     245,   301,    21,   318,   250,   336,   144,    21,   223,   338,
      96,     3,   127,   287,   360,   367,   379,   288,   380,   331,
      97,   289,   362,   152,   381,   440,   290,   407,   291,   292,
     158,   409,  -235,   159,   364,   179,   415,   425,   462,   448,
     458,   167,  -235,   461,   170,   263,    87,   371,   217,   433,
     339,   226,   475,    21,   477,   217,   490,   476,   382,   509,
     511,   544,   348,   385,   386,   152,   390,   217,   217,   281,
     156,   355,   158,   217,   295,   159,   405,    55,   152,   285,
     524,   546,    55,   167,   168,   158,   170,   256,   159,   549,
     217,   217,   217,   152,   309,   551,   167,   168,   217,   170,
     158,   418,   136,   159,   469,    87,   422,   442,   542,   479,
     426,   167,     0,   431,   170,   126,     0,     0,     0,     0,
     353,   293,     0,     0,     0,     0,   369,   443,    55,   445,
       0,     0,   294,   173,   179,     0,     0,     0,     0,     0,
       0,     0,     0,     0,   127,   308,   173,     0,     0,   127,
       0,   315,     0,   217,     0,     0,   217,     0,     0,     0,
       0,   463,     0,   466,     0,   327,   328,     0,     0,   333,
     470,     0,   473,   178,   217,     0,   179,     0,     0,   217,
     382,   217,     0,     0,   217,   420,   178,   488,   489,   179,
     369,   350,   351,   352,   217,     0,     0,     0,   499,     0,
     217,   372,     0,   358,   179,     0,     0,     0,     0,     0,
       0,   152,     0,   153,   154,   155,   156,   157,   158,     0,
     391,   159,   410,   411,   412,     0,   414,     0,     0,   167,
     168,   401,   170,   217,     0,   217,   126,   217,     0,     0,
       0,     0,     0,     0,     0,   217,     0,   217,     0,     0,
       0,   530,   217,   217,   217,     0,     0,    87,   126,    87,
       0,   126,   217,     0,     0,   217,     0,     0,   217,     0,
       0,     0,     0,   435,     0,   127,    87,     0,    87,   173,
       0,     0,     0,     0,     0,   457,     0,     0,   217,     0,
     217,     0,     0,   217,     0,   217,     0,     0,     0,   413,
     401,     0,   416,     0,     0,     0,   474,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,   467,     0,   178,
       0,     0,   179,   434,   471,   180,   439,     0,     0,   441,
       0,     0,     0,   444,     0,     0,     0,   535,     0,   537,
       0,     0,     0,     0,     0,     0,   492,     0,     0,     0,
     401,   454,     0,     0,     0,     0,   545,     0,   547,     0,
       0,     0,   460,     0,     0,   110,     0,     0,     0,   520,
     510,  -235,   152,     0,   153,   154,   155,   156,   157,   158,
       0,     0,   159,   160,   161,   162,   163,   164,   165,   166,
     167,   168,   169,   170,   539,     0,   127,     0,   493,     0,
     495,   497,     0,   529,     0,   531,     0,     0,     0,     0,
       0,     0,     0,   505,     0,     0,     0,   507,   127,     0,
     552,   127,   554,     0,     0,   556,     0,   557,     0,   512,
     513,     0,   514,     0,     0,     0,     0,     0,     0,     0,
     173,     0,     0,   522,     0,     0,     0,     0,     0,   525,
       0,     0,     0,   528,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     178,     0,     0,   179,     0,     0,   180,     0,     0,   548,
       0,   550,     0,     0,   553,     1,   555,     2,     3,     4,
       5,     6,   -77,     7,     8,     9,    10,    81,   -77,   -77,
     -77,    12,   -77,    13,    14,   -77,   -77,   -77,   -77,    15,
      16,   -77,   -77,    17,   -77,   -77,   -77,   -77,   -77,   -77,
      18,    19,   -77,     0,    20,   -77,     0,     0,   -77,   -77,
      21,    22,     0,   -77,   -77,   -77,    23,    24,    25,   -77,
     -77,    26,   -77,   -77,    27,   -77,    28,    29,     0,    30,
     -77,   -77,     0,    31,    32,    33,     0,    34,    35,    36,
       0,   -77,    82,     0,    38,     0,    39,    40,    41,   -77,
      42,    43,     0,    44,     0,     0,     0,     0,     0,     0,
       0,     0,    45,   -77,    46,   -77,     0,     0,     0,     0,
       0,     0,    47,    48,   -77,    49,    50,    51,   -77,    52,
      53,   -77,    54,     0,   -77,    55,    56,    57,   -77,    58,
       0,    59,   -77,    60,    61,   -77,    62,    63,     1,     0,
       2,     3,     4,     5,     6,   -76,     7,     8,     9,    10,
      11,   -76,   -76,   -76,    12,   -76,    13,    14,   -76,   -76,
     -76,   -76,    15,    16,   -76,   -76,    17,   -76,   -76,   -76,
     -76,   -76,   -76,    18,    19,   -76,     0,    20,   -76,     0,
       0,   -76,   -76,    21,    22,     0,   -76,   -76,   -76,    23,
      24,    25,   -76,   -76,    26,   -76,   -76,    27,   -76,    28,
      29,     0,    30,   -76,   -76,     0,    31,    32,    33,     0,
      34,    35,    36,     0,   -76,     0,     0,    38,     0,    39,
      40,    41,   -76,    42,    43,     0,    44,     0,     0,     0,
       0,     0,     0,     0,     0,    45,   -76,    46,   -76,     0,
       0,     0,     0,     0,     0,    47,   -76,   -76,    49,    50,
      51,   -76,    52,    53,   -76,    54,     0,   -76,    55,    56,
      57,   -76,    58,     0,    59,   -76,    60,    61,   -76,    62,
      63,     1,     0,     2,     3,     4,     5,     6,  -120,     7,
       8,     9,    10,    11,  -120,  -120,  -120,   139,  -120,    13,
      14,  -120,  -120,  -120,  -120,    15,    16,  -120,  -120,    17,
    -120,  -120,  -120,  -120,  -120,  -120,    18,    19,  -120,     0,
      20,  -120,     0,     0,  -120,  -120,    21,    22,     0,  -120,
    -120,  -120,    23,    24,    25,  -120,  -120,    26,  -120,  -120,
      27,  -120,    28,    29,     0,    30,  -120,  -120,     0,    31,
      32,    33,     0,    34,    35,    36,     0,  -120,  -120,     0,
      38,     0,    39,    40,    41,  -120,    42,    43,     0,    44,
       0,     0,     0,     0,     0,     0,     0,     0,    45,  -120,
      46,  -120,     0,     0,     0,     0,     0,     0,    47,  -120,
    -120,    49,    50,    51,  -120,    52,    53,  -120,    54,     0,
    -120,    55,    56,    57,  -120,    58,     0,     0,  -120,    60,
      61,  -120,    62,    63,     1,     0,     2,     3,     4,     5,
       6,  -149,     7,     8,     9,    10,    11,  -149,  -149,  -149,
      12,  -149,    13,    14,  -149,  -149,  -149,  -149,    15,    16,
    -149,  -149,    17,  -149,  -149,  -149,  -149,  -149,  -149,    18,
      19,  -149,     0,    20,  -149,     0,     0,  -149,  -149,    21,
      22,     0,  -149,  -149,  -149,    23,    24,    25,  -149,  -149,
      26,  -149,  -149,    27,  -149,    28,    29,     0,    30,  -149,
    -149,     0,    31,    32,    33,     0,    34,    35,    36,     0,
    -149,    37,     0,    38,     0,    39,    40,    41,  -149,    42,
      43,     0,    44,     0,     0,     0,     0,     0,     0,     0,
       0,    45,  -149,    46,  -149,     0,     0,     0,     0,     0,
       0,    47,     0,  -149,    49,    50,    51,  -149,    52,    53,
    -149,    54,     0,  -149,    55,    56,    57,  -149,    58,     0,
      59,  -149,    60,    61,  -149,    62,    63,     1,     0,     2,
       3,     4,     5,     6,     0,     7,     8,     9,    10,    11,
    -108,  -108,  -108,   370,     0,    13,    14,  -108,  -108,  -108,
    -108,    15,    16,  -108,  -108,    17,  -108,  -108,  -108,  -108,
    -108,  -108,    18,    19,  -108,     0,    20,     0,     0,     0,
       0,  -108,    21,    22,     0,     0,  -108,     0,    23,    24,
      25,     0,     0,    26,     0,     0,    27,     0,    28,    29,
       0,    30,     0,     0,     0,    31,    32,    33,     0,    34,
      35,    36,   106,     0,    37,     0,    38,     0,    39,    40,
      41,  -108,    42,    43,     0,    44,     0,     0,     0,     0,
       0,     0,     0,     0,    45,     0,    46,     0,     0,     0,
       0,     0,     0,     0,    47,    48,     0,    49,    50,    51,
    -108,    52,    53,  -108,    54,     0,  -108,    55,    56,    57,
    -108,    58,     0,    59,  -108,    60,    61,  -108,    62,    63,
       1,     0,     2,     3,     4,     5,     6,     0,     7,     8,
       9,    10,    11,  -108,  -108,  -108,   370,     0,    13,    14,
    -108,  -108,  -108,  -108,    15,    16,  -108,  -108,    17,  -108,
    -108,  -108,  -108,  -108,  -108,    18,    19,  -108,     0,    20,
       0,     0,     0,     0,  -108,    21,    22,     0,     0,     0,
       0,    23,    24,    25,     0,     0,    26,     0,     0,    27,
       0,    28,    29,     0,    30,     0,     0,     0,    31,    32,
      33,     0,    34,    35,    36,   106,     0,    37,     0,    38,
       0,    39,    40,    41,  -108,    42,    43,     0,    44,     0,
       0,     0,     0,     0,     0,     0,     0,    45,     0,    46,
       0,     0,     0,     0,     0,     0,     0,    47,    48,     0,
      49,    50,    51,  -108,    52,    53,  -108,    54,     0,  -108,
      55,    56,    57,  -108,    58,     0,    59,  -108,    60,    61,
    -108,    62,    63,     1,     0,     2,     3,     4,     5,     6,
       0,     7,     8,     9,    10,    11,   -76,   -76,   -76,    12,
       0,    13,    14,   -76,   -76,   -76,   -76,    15,   196,   -76,
     -76,    17,   -76,   -76,   -76,   -76,   -76,   -76,    18,    19,
     -76,     0,    20,     0,     0,     0,     0,   -76,    21,    22,
       0,     0,     0,     0,    23,    24,    25,     0,     0,    26,
       0,     0,    27,     0,    28,    29,     0,    30,     0,     0,
       0,    31,    32,    33,     0,    34,    35,    36,     0,     0,
      37,     0,    38,     0,    39,    40,    41,   -76,    42,    43,
       0,    44,     0,     0,     0,     0,     0,     0,     0,     0,
      45,     0,    46,     0,     0,     0,     0,     0,     0,     0,
      47,   112,     0,    49,    50,    51,   -76,    52,    53,   -76,
      54,     0,   -76,    55,    56,    57,   -76,    58,     0,    59,
     -76,    60,    61,   -76,    62,    63,     1,     0,     2,     3,
       4,     5,     6,     0,     7,     8,     9,    10,    11,     0,
       0,     0,    12,     0,    13,    14,     0,     0,     0,     0,
      15,    16,     0,     0,    17,     0,     0,     0,     0,     0,
       0,    18,    19,     0,     0,    20,     0,     0,     0,   215,
       0,    21,    22,     0,   373,     0,   374,    23,    24,    25,
       0,     0,    26,     0,     0,    27,     0,    28,    29,     0,
      30,     0,     0,     0,    31,    32,    33,     0,    34,    35,
      36,     0,   243,    37,     0,    38,     0,    39,    40,    41,
       0,    42,    43,     0,    44,     0,     0,     0,     0,     0,
       0,     0,     0,    45,     0,    46,     0,     0,     0,     0,
       0,     0,     0,    47,   375,     0,    49,    50,    51,     0,
      52,    53,     0,    54,     0,     0,    55,    56,    57,     0,
      58,     0,    59,     0,    60,    61,     0,    62,    63,     1,
       0,     2,     3,     4,     5,     6,     0,     7,     8,     9,
      10,    11,     0,     0,     0,    12,     0,    13,    14,     0,
       0,     0,     0,    15,    16,     0,     0,    17,     0,     0,
       0,     0,     0,     0,    18,    19,     0,     0,    20,     0,
       0,     0,     0,     0,    21,    22,     0,   432,     0,   433,
      23,    24,    25,     0,     0,    26,     0,     0,    27,     0,
      28,    29,     0,    30,     0,     0,     0,    31,    32,    33,
       0,    34,    35,    36,     0,   349,    37,     0,    38,     0,
      39,    40,    41,     0,    42,    43,     0,    44,     0,     0,
       0,     0,     0,     0,     0,     0,    45,     0,    46,     0,
       0,     0,     0,     0,     0,     0,    47,    48,     0,    49,
      50,    51,     0,    52,    53,     0,    54,     0,     0,    55,
      56,    57,     0,    58,     0,    59,     0,    60,    61,     0,
      62,    63,     1,     0,     2,     3,     4,     5,     6,     0,
       7,     8,   478,    10,    11,     0,     0,     0,    12,     0,
      13,    14,     0,     0,     0,     0,    15,    16,     0,     0,
      17,     0,     0,     0,     0,     0,     0,    18,    19,     0,
       0,    20,     0,     0,     0,   215,     0,    21,    22,     0,
       0,     0,     0,    23,    24,    25,   206,  -230,    26,     0,
       0,    27,     0,    28,    29,     0,    30,     0,     0,     0,
      31,    32,    33,     0,    34,    35,    36,     0,     0,    37,
       0,    38,     0,    39,    40,    41,     0,    42,    43,     0,
      44,     0,     0,     0,     0,     0,     0,     0,     0,    45,
       0,    46,     0,     0,     0,     0,     0,     0,     0,    47,
      48,     0,    49,    50,    51,     0,    52,    53,     0,    54,
       0,     0,    55,    56,    57,     0,    58,     0,    59,     0,
      60,    61,     0,    62,    63,     1,     0,     2,     3,     4,
       5,     6,     0,     7,     8,     9,    10,    11,     0,     0,
       0,    12,     0,    13,    14,     0,     0,     0,     0,    15,
      16,     0,     0,    17,     0,     0,     0,     0,     0,     0,
      18,    19,     0,     0,    20,     0,     0,     0,   215,     0,
      21,    22,     0,     0,     0,     0,    23,    24,    25,     0,
       0,    26,     0,     0,    27,     0,    28,    29,     0,    30,
       0,     0,     0,    31,    32,    33,     0,    34,    35,    36,
       0,   243,    37,     0,    38,     0,    39,    40,    41,     0,
      42,    43,     0,    44,     0,     0,     0,     0,     0,     0,
       0,     0,    45,     0,    46,     0,     0,     0,     0,     0,
       0,     0,    47,   518,   519,    49,    50,    51,     0,    52,
      53,     0,    54,     0,     0,    55,    56,    57,     0,    58,
       0,    59,     0,    60,    61,     0,    62,    63,     1,     0,
       2,     3,     4,     5,     6,     0,     7,     8,     9,    10,
      11,     0,     0,     0,    12,     0,    13,    14,     0,     0,
       0,     0,    15,    16,     0,     0,    17,     0,     0,     0,
       0,     0,     0,    18,    19,     0,     0,    20,     0,     0,
       0,   215,     0,    21,    22,     0,     0,     0,     0,    23,
      24,    25,     0,     0,    26,     0,     0,    27,     0,    28,
      29,     0,    30,     0,     0,     0,    31,    32,    33,     0,
      34,    35,    36,     0,   216,    37,     0,    38,     0,    39,
      40,    41,     0,    42,    43,     0,    44,     0,     0,     0,
       0,     0,     0,     0,     0,    45,     0,    46,     0,     0,
       0,     0,     0,     0,     0,    47,    48,     0,    49,    50,
      51,     0,    52,    53,     0,    54,     0,     0,    55,    56,
      57,     0,    58,     0,    59,     0,    60,    61,     0,    62,
      63,     1,     0,     2,     3,     4,     5,     6,     0,     7,
       8,     9,    10,    11,     0,     0,     0,    12,     0,    13,
      14,     0,     0,     0,     0,    15,    16,     0,     0,    17,
       0,     0,     0,     0,     0,     0,    18,    19,     0,     0,
      20,     0,     0,     0,   215,     0,    21,    22,     0,     0,
       0,     0,    23,    24,    25,     0,     0,    26,     0,     0,
      27,     0,    28,    29,     0,    30,   222,     0,     0,    31,
      32,    33,     0,    34,    35,    36,     0,     0,    37,     0,
      38,     0,    39,    40,    41,     0,    42,    43,     0,    44,
       0,     0,     0,     0,     0,     0,     0,     0,    45,     0,
      46,     0,     0,     0,     0,     0,     0,     0,    47,    48,
       0,    49,    50,    51,     0,    52,    53,     0,    54,     0,
       0,    55,    56,    57,     0,    58,     0,    59,     0,    60,
      61,     0,    62,    63,     1,     0,     2,     3,     4,     5,
       6,     0,     7,     8,     9,    10,    11,     0,     0,     0,
      12,     0,    13,    14,     0,     0,     0,     0,    15,    16,
       0,     0,    17,     0,     0,     0,     0,     0,     0,    18,
      19,     0,     0,    20,     0,     0,     0,     0,     0,    21,
      22,     0,     0,     0,     0,    23,    24,    25,     0,     0,
      26,     0,     0,    27,     0,    28,    29,     0,    30,     0,
       0,     0,    31,    32,    33,     0,    34,    35,    36,     0,
       0,    37,  -179,    38,   223,    39,    40,    41,     0,    42,
      43,     0,    44,     0,     0,     0,     0,     0,     0,     0,
       0,    45,     0,    46,     0,     0,     0,     0,     0,     0,
       0,    47,    48,     0,    49,    50,    51,     0,    52,    53,
       0,    54,     0,     0,    55,    56,    57,     0,    58,     0,
      59,     0,    60,    61,     0,    62,    63,     1,     0,     2,
       3,     4,     5,     6,     0,     7,     8,     9,    10,    11,
       0,     0,     0,    12,     0,    13,    14,     0,     0,     0,
       0,    15,    16,     0,     0,    17,     0,     0,     0,     0,
       0,     0,    18,    19,     0,     0,    20,     0,     0,     0,
     215,     0,    21,    22,     0,     0,     0,     0,    23,    24,
      25,     0,     0,    26,     0,     0,    27,     0,    28,    29,
       0,    30,     0,     0,     0,    31,    32,    33,     0,    34,
      35,    36,     0,   243,    37,     0,    38,     0,    39,    40,
      41,     0,    42,    43,     0,    44,     0,     0,     0,     0,
       0,     0,     0,     0,    45,     0,    46,     0,     0,     0,
       0,     0,     0,     0,    47,   244,     0,    49,    50,    51,
       0,    52,    53,     0,    54,     0,     0,    55,    56,    57,
       0,    58,     0,    59,     0,    60,    61,     0,    62,    63,
       1,     0,     2,     3,     4,     5,     6,     0,     7,     8,
       9,    10,    11,     0,     0,     0,    12,     0,    13,    14,
       0,     0,     0,     0,    15,    16,     0,     0,    17,     0,
       0,     0,     0,     0,     0,    18,    19,     0,     0,    20,
       0,     0,     0,   215,     0,    21,    22,     0,     0,     0,
     248,    23,    24,    25,     0,     0,    26,     0,     0,    27,
       0,    28,    29,     0,    30,     0,     0,     0,    31,    32,
      33,     0,    34,    35,    36,     0,     0,    37,     0,    38,
       0,    39,    40,    41,     0,    42,    43,     0,    44,     0,
       0,     0,     0,     0,     0,     0,     0,    45,     0,    46,
       0,     0,     0,     0,     0,     0,     0,    47,   249,     0,
      49,    50,    51,     0,    52,    53,     0,    54,     0,     0,
      55,    56,    57,     0,    58,     0,    59,     0,    60,    61,
       0,    62,    63,     1,     0,     2,     3,     4,     5,     6,
       0,     7,     8,     9,    10,    11,     0,     0,     0,    12,
       0,    13,    14,     0,     0,     0,     0,    15,    16,     0,
       0,    17,     0,     0,     0,     0,     0,     0,    18,    19,
       0,     0,    20,     0,     0,     0,   215,     0,    21,    22,
       0,     0,   258,     0,    23,    24,    25,     0,     0,    26,
       0,     0,    27,     0,    28,    29,     0,    30,     0,     0,
       0,    31,    32,    33,     0,    34,    35,    36,     0,     0,
      37,     0,    38,     0,    39,    40,    41,     0,    42,    43,
       0,    44,     0,     0,     0,     0,     0,     0,     0,     0,
      45,     0,    46,     0,     0,     0,     0,     0,     0,     0,
      47,    48,     0,    49,    50,    51,     0,    52,    53,     0,
      54,     0,     0,    55,    56,    57,     0,    58,     0,    59,
       0,    60,    61,     0,    62,    63,     1,     0,     2,     3,
       4,     5,     6,     0,     7,     8,     9,    10,    11,     0,
       0,     0,    12,     0,    13,    14,     0,     0,     0,     0,
      15,    16,     0,     0,    17,     0,     0,     0,     0,     0,
       0,    18,    19,     0,     0,    20,     0,     0,     0,   215,
       0,    21,    22,     0,     0,     0,     0,    23,    24,    25,
       0,     0,    26,     0,     0,    27,     0,    28,    29,     0,
      30,     0,     0,     0,    31,    32,    33,     0,    34,    35,
      36,     0,   335,    37,     0,    38,     0,    39,    40,    41,
       0,    42,    43,     0,    44,     0,     0,     0,     0,     0,
       0,     0,     0,    45,     0,    46,     0,     0,     0,     0,
       0,     0,     0,    47,    48,     0,    49,    50,    51,     0,
      52,    53,     0,    54,     0,     0,    55,    56,    57,     0,
      58,     0,    59,     0,    60,    61,     0,    62,    63,     1,
       0,     2,     3,     4,     5,     6,     0,     7,     8,     9,
      10,    11,     0,     0,     0,    12,     0,    13,    14,     0,
       0,     0,     0,    15,    16,     0,     0,    17,     0,     0,
       0,     0,     0,     0,    18,    19,     0,     0,    20,     0,
       0,     0,   215,     0,    21,    22,     0,     0,     0,     0,
      23,    24,    25,     0,     0,    26,     0,     0,    27,     0,
      28,    29,     0,    30,     0,     0,     0,    31,    32,    33,
       0,    34,    35,    36,     0,  -234,    37,     0,    38,     0,
      39,    40,    41,     0,    42,    43,     0,    44,     0,     0,
       0,     0,     0,     0,     0,     0,    45,     0,    46,     0,
       0,     0,     0,     0,     0,     0,    47,    48,     0,    49,
      50,    51,     0,    52,    53,     0,    54,     0,     0,    55,
      56,    57,     0,    58,     0,    59,     0,    60,    61,     0,
      62,    63,     1,     0,     2,     3,     4,     5,     6,     0,
       7,     8,     9,    10,    11,     0,     0,     0,    12,     0,
      13,    14,     0,     0,     0,     0,    15,    16,     0,     0,
      17,     0,     0,     0,     0,     0,     0,    18,    19,     0,
       0,    20,     0,     0,     0,   215,     0,    21,    22,     0,
       0,     0,     0,    23,    24,    25,     0,  -231,    26,     0,
       0,    27,     0,    28,    29,     0,    30,     0,     0,     0,
      31,    32,    33,     0,    34,    35,    36,     0,     0,    37,
       0,    38,     0,    39,    40,    41,     0,    42,    43,     0,
      44,     0,     0,     0,     0,     0,     0,     0,     0,    45,
       0,    46,     0,     0,     0,     0,     0,     0,     0,    47,
      48,     0,    49,    50,    51,     0,    52,    53,     0,    54,
       0,     0,    55,    56,    57,     0,    58,     0,    59,     0,
      60,    61,     0,    62,    63,     1,     0,     2,     3,     4,
       5,     6,     0,     7,     8,     9,    10,    11,     0,     0,
       0,    12,     0,    13,    14,     0,     0,     0,     0,    15,
      16,     0,     0,    17,     0,     0,     0,     0,     0,     0,
      18,    19,     0,     0,    20,     0,     0,     0,   215,     0,
      21,    22,     0,     0,     0,     0,    23,    24,    25,     0,
       0,    26,     0,     0,    27,     0,    28,    29,     0,    30,
       0,     0,     0,    31,    32,    33,     0,    34,    35,    36,
       0,   392,    37,     0,    38,     0,    39,    40,    41,     0,
      42,    43,     0,    44,     0,     0,     0,     0,     0,     0,
       0,     0,    45,     0,    46,     0,     0,     0,     0,     0,
       0,     0,    47,    48,     0,    49,    50,    51,     0,    52,
      53,     0,    54,     0,     0,    55,    56,    57,     0,    58,
       0,    59,     0,    60,    61,     0,    62,    63,     1,     0,
       2,     3,     4,     5,     6,     0,     7,     8,     9,    10,
      11,     0,     0,     0,    12,     0,    13,    14,     0,     0,
       0,     0,    15,    16,     0,     0,    17,     0,     0,     0,
       0,     0,     0,    18,    19,     0,     0,    20,     0,     0,
       0,   215,     0,    21,    22,     0,     0,     0,     0,    23,
      24,    25,     0,     0,    26,     0,     0,    27,     0,    28,
      29,     0,    30,     0,     0,     0,    31,    32,    33,     0,
      34,    35,    36,     0,   393,    37,     0,    38,     0,    39,
      40,    41,     0,    42,    43,     0,    44,     0,     0,     0,
       0,     0,     0,     0,     0,    45,     0,    46,     0,     0,
       0,     0,     0,     0,     0,    47,    48,     0,    49,    50,
      51,     0,    52,    53,     0,    54,     0,     0,    55,    56,
      57,     0,    58,     0,    59,     0,    60,    61,     0,    62,
      63,     1,     0,     2,     3,     4,     5,     6,     0,     7,
       8,     9,    10,    11,     0,     0,     0,    12,     0,    13,
      14,     0,     0,     0,     0,    15,    16,     0,     0,    17,
       0,     0,     0,     0,     0,     0,    18,    19,     0,     0,
      20,     0,     0,     0,   215,     0,    21,    22,     0,     0,
       0,     0,    23,    24,    25,     0,     0,    26,     0,     0,
      27,     0,    28,    29,     0,    30,     0,     0,     0,    31,
      32,    33,     0,    34,    35,    36,     0,   404,    37,     0,
      38,     0,    39,    40,    41,     0,    42,    43,     0,    44,
       0,     0,     0,     0,     0,     0,     0,     0,    45,     0,
      46,     0,     0,     0,     0,     0,     0,     0,    47,    48,
       0,    49,    50,    51,     0,    52,    53,     0,    54,     0,
       0,    55,    56,    57,     0,    58,     0,    59,     0,    60,
      61,     0,    62,    63,     1,     0,     2,     3,     4,     5,
       6,     0,     7,     8,     9,    10,    11,     0,     0,     0,
      12,     0,    13,    14,     0,     0,     0,     0,    15,    16,
       0,     0,    17,     0,     0,     0,     0,     0,     0,    18,
      19,     0,     0,    20,     0,     0,     0,   215,     0,    21,
      22,     0,     0,     0,   417,    23,    24,    25,     0,     0,
      26,     0,     0,    27,     0,    28,    29,     0,    30,     0,
       0,     0,    31,    32,    33,     0,    34,    35,    36,     0,
       0,    37,     0,    38,     0,    39,    40,    41,     0,    42,
      43,     0,    44,     0,     0,     0,     0,     0,     0,     0,
       0,    45,     0,    46,     0,     0,     0,     0,     0,     0,
       0,    47,    48,     0,    49,    50,    51,     0,    52,    53,
       0,    54,     0,     0,    55,    56,    57,     0,    58,     0,
      59,     0,    60,    61,     0,    62,    63,     1,     0,     2,
       3,     4,     5,     6,     0,     7,     8,     9,    10,    11,
       0,     0,     0,    12,     0,    13,    14,     0,     0,     0,
       0,    15,    16,     0,     0,    17,     0,     0,     0,     0,
       0,     0,    18,    19,     0,     0,    20,     0,     0,     0,
       0,     0,    21,    22,     0,     0,     0,     0,    23,    24,
      25,     0,     0,    26,     0,     0,    27,     0,    28,    29,
       0,    30,     0,     0,     0,    31,    32,    33,     0,    34,
      35,    36,   453,     0,    37,   331,    38,     0,    39,    40,
      41,     0,    42,    43,     0,    44,     0,     0,     0,     0,
       0,     0,     0,     0,    45,     0,    46,     0,     0,     0,
       0,     0,     0,     0,    47,    48,     0,    49,    50,    51,
       0,    52,    53,     0,    54,     0,     0,    55,    56,    57,
       0,    58,     0,    59,     0,    60,    61,     0,    62,    63,
       1,     0,     2,     3,     4,     5,     6,     0,     7,     8,
       9,    10,    11,     0,     0,     0,    12,     0,    13,    14,
       0,     0,     0,     0,    15,    16,     0,     0,    17,     0,
       0,     0,     0,     0,     0,    18,    19,     0,     0,    20,
       0,     0,     0,   215,     0,    21,    22,     0,     0,     0,
       0,    23,    24,    25,     0,     0,    26,     0,     0,    27,
       0,    28,    29,     0,    30,     0,     0,     0,    31,    32,
      33,     0,    34,    35,    36,     0,   459,    37,     0,    38,
       0,    39,    40,    41,     0,    42,    43,     0,    44,     0,
       0,     0,     0,     0,     0,     0,     0,    45,     0,    46,
       0,     0,     0,     0,     0,     0,     0,    47,    48,     0,
      49,    50,    51,     0,    52,    53,     0,    54,     0,     0,
      55,    56,    57,     0,    58,     0,    59,     0,    60,    61,
       0,    62,    63,     1,     0,     2,     3,     4,     5,     6,
       0,     7,     8,     9,    10,    11,     0,     0,     0,    12,
       0,    13,    14,     0,     0,     0,     0,    15,    16,     0,
       0,    17,     0,     0,     0,     0,     0,     0,    18,    19,
       0,     0,    20,     0,     0,     0,   215,     0,    21,    22,
       0,     0,     0,     0,    23,    24,    25,     0,     0,    26,
       0,     0,    27,     0,    28,    29,     0,    30,     0,     0,
       0,    31,    32,    33,     0,    34,    35,    36,     0,   481,
      37,     0,    38,     0,    39,    40,    41,     0,    42,    43,
       0,    44,     0,     0,     0,     0,     0,     0,     0,     0,
      45,     0,    46,     0,     0,     0,     0,     0,     0,     0,
      47,    48,     0,    49,    50,    51,     0,    52,    53,     0,
      54,     0,     0,    55,    56,    57,     0,    58,     0,    59,
       0,    60,    61,     0,    62,    63,     1,     0,     2,     3,
       4,     5,     6,     0,     7,     8,     9,    10,    11,     0,
       0,     0,    12,     0,    13,    14,     0,     0,     0,     0,
      15,    16,     0,     0,    17,     0,     0,     0,     0,     0,
       0,    18,    19,     0,     0,    20,     0,     0,     0,   215,
       0,    21,    22,     0,     0,     0,     0,    23,    24,    25,
       0,     0,    26,     0,     0,    27,     0,    28,    29,     0,
      30,     0,     0,     0,    31,    32,    33,     0,    34,    35,
      36,     0,   486,    37,     0,    38,     0,    39,    40,    41,
       0,    42,    43,     0,    44,     0,     0,     0,     0,     0,
       0,     0,     0,    45,     0,    46,     0,     0,     0,     0,
       0,     0,     0,    47,    48,     0,    49,    50,    51,     0,
      52,    53,     0,    54,     0,     0,    55,    56,    57,     0,
      58,     0,    59,     0,    60,    61,     0,    62,    63,     1,
       0,     2,     3,     4,     5,     6,     0,     7,     8,     9,
      10,    11,     0,     0,     0,    12,     0,    13,    14,     0,
       0,     0,     0,    15,    16,     0,     0,    17,     0,     0,
       0,     0,     0,     0,    18,    19,     0,     0,    20,     0,
       0,     0,   215,     0,    21,    22,     0,     0,     0,     0,
      23,    24,    25,     0,     0,    26,     0,     0,    27,     0,
      28,    29,     0,    30,     0,     0,     0,    31,    32,    33,
       0,    34,    35,    36,     0,   494,    37,     0,    38,     0,
      39,    40,    41,     0,    42,    43,     0,    44,     0,     0,
       0,     0,     0,     0,     0,     0,    45,     0,    46,     0,
       0,     0,     0,     0,     0,     0,    47,    48,     0,    49,
      50,    51,     0,    52,    53,     0,    54,     0,     0,    55,
      56,    57,     0,    58,     0,    59,     0,    60,    61,     0,
      62,    63,     1,     0,     2,     3,     4,     5,     6,     0,
       7,     8,     9,    10,    11,     0,     0,     0,    12,     0,
      13,    14,     0,     0,     0,     0,    15,    16,     0,     0,
      17,     0,     0,     0,     0,     0,     0,    18,    19,     0,
       0,    20,     0,     0,     0,   215,     0,    21,    22,     0,
       0,     0,     0,    23,    24,    25,     0,     0,    26,     0,
       0,    27,     0,    28,    29,     0,    30,     0,     0,     0,
      31,    32,    33,     0,    34,    35,    36,     0,   502,    37,
       0,    38,     0,    39,    40,    41,     0,    42,    43,     0,
      44,     0,     0,     0,     0,     0,     0,     0,     0,    45,
       0,    46,     0,     0,     0,     0,     0,     0,     0,    47,
      48,     0,    49,    50,    51,     0,    52,    53,     0,    54,
       0,     0,    55,    56,    57,     0,    58,     0,    59,     0,
      60,    61,     0,    62,    63,     1,     0,     2,     3,     4,
       5,     6,     0,     7,     8,     9,    10,    11,     0,     0,
       0,    12,     0,    13,    14,     0,     0,     0,     0,    15,
      16,     0,     0,    17,     0,     0,     0,     0,     0,     0,
      18,    19,     0,     0,    20,     0,     0,     0,   215,     0,
      21,    22,     0,     0,     0,     0,    23,    24,    25,     0,
       0,    26,     0,     0,    27,     0,    28,    29,     0,    30,
       0,     0,     0,    31,    32,    33,     0,    34,    35,    36,
       0,   515,    37,     0,    38,     0,    39,    40,    41,     0,
      42,    43,     0,    44,     0,     0,     0,     0,     0,     0,
       0,     0,    45,     0,    46,     0,     0,     0,     0,     0,
       0,     0,    47,    48,     0,    49,    50,    51,     0,    52,
      53,     0,    54,     0,     0,    55,    56,    57,     0,    58,
       0,    59,     0,    60,    61,     0,    62,    63,     1,     0,
       2,     3,     4,     5,     6,     0,     7,     8,     9,    10,
      11,     0,     0,     0,    12,     0,    13,    14,     0,     0,
       0,     0,    15,    16,     0,     0,    17,     0,     0,     0,
       0,     0,     0,    18,    19,     0,     0,    20,     0,     0,
       0,   215,     0,    21,    22,     0,     0,     0,     0,    23,
      24,    25,     0,     0,    26,     0,     0,    27,     0,    28,
      29,     0,    30,     0,     0,     0,    31,    32,    33,     0,
      34,    35,    36,     0,   516,    37,     0,    38,     0,    39,
      40,    41,     0,    42,    43,     0,    44,     0,     0,     0,
       0,     0,     0,     0,     0,    45,     0,    46,     0,     0,
       0,     0,     0,     0,     0,    47,    48,     0,    49,    50,
      51,     0,    52,    53,     0,    54,     0,     0,    55,    56,
      57,     0,    58,     0,    59,     0,    60,    61,     0,    62,
      63,     1,     0,     2,     3,     4,     5,     6,     0,     7,
       8,     9,    10,    11,     0,     0,     0,    12,     0,    13,
      14,     0,     0,     0,     0,    15,    16,     0,     0,    17,
       0,     0,     0,     0,     0,     0,    18,    19,     0,     0,
      20,     0,     0,     0,   215,     0,    21,    22,     0,     0,
       0,     0,    23,    24,    25,     0,     0,    26,     0,     0,
      27,     0,    28,    29,     0,    30,     0,     0,     0,    31,
      32,    33,     0,    34,    35,    36,     0,   526,    37,     0,
      38,     0,    39,    40,    41,     0,    42,    43,     0,    44,
       0,     0,     0,     0,     0,     0,     0,     0,    45,     0,
      46,     0,     0,     0,     0,     0,     0,     0,    47,    48,
       0,    49,    50,    51,     0,    52,    53,     0,    54,     0,
       0,    55,    56,    57,     0,    58,     0,    59,     0,    60,
      61,     0,    62,    63,     1,     0,     2,     3,     4,     5,
       6,     0,     7,     8,     9,    10,    11,     0,     0,     0,
      12,     0,    13,    14,     0,     0,     0,     0,    15,    16,
       0,     0,    17,     0,     0,     0,     0,     0,     0,    18,
      19,     0,     0,    20,     0,     0,     0,   215,     0,    21,
      22,     0,     0,     0,     0,    23,    24,    25,     0,     0,
      26,     0,     0,    27,     0,    28,    29,     0,    30,     0,
       0,     0,    31,    32,    33,     0,    34,    35,    36,     0,
     532,    37,     0,    38,     0,    39,    40,    41,     0,    42,
      43,     0,    44,     0,     0,     0,     0,     0,     0,     0,
       0,    45,     0,    46,     0,     0,     0,     0,     0,     0,
       0,    47,    48,     0,    49,    50,    51,     0,    52,    53,
       0,    54,     0,     0,    55,    56,    57,     0,    58,     0,
      59,     0,    60,    61,     0,    62,    63,     1,     0,     2,
       3,     4,     5,     6,     0,     7,     8,     9,    10,    11,
       0,     0,     0,    12,     0,    13,    14,     0,     0,     0,
       0,    15,    16,     0,     0,    17,     0,     0,     0,     0,
       0,     0,    18,    19,     0,     0,    20,     0,     0,     0,
     215,     0,    21,    22,     0,     0,     0,     0,    23,    24,
      25,     0,     0,    26,     0,     0,    27,     0,    28,    29,
       0,    30,     0,     0,     0,    31,    32,    33,     0,    34,
      35,    36,     0,   533,    37,     0,    38,     0,    39,    40,
      41,     0,    42,    43,     0,    44,     0,     0,     0,     0,
       0,     0,     0,     0,    45,     0,    46,     0,     0,     0,
       0,     0,     0,     0,    47,    48,     0,    49,    50,    51,
       0,    52,    53,     0,    54,     0,     0,    55,    56,    57,
       0,    58,     0,    59,     0,    60,    61,     0,    62,    63,
       1,     0,     2,     3,     4,     5,     6,     0,     7,     8,
       9,    10,    11,     0,     0,     0,    12,     0,    13,    14,
       0,     0,     0,     0,    15,    16,     0,     0,    17,     0,
       0,     0,     0,     0,     0,    18,    19,     0,     0,    20,
       0,     0,     0,   215,     0,    21,    22,     0,     0,     0,
       0,    23,    24,    25,     0,     0,    26,     0,     0,    27,
       0,    28,    29,     0,    30,     0,     0,     0,    31,    32,
      33,     0,    34,    35,    36,     0,   534,    37,     0,    38,
       0,    39,    40,    41,     0,    42,    43,     0,    44,     0,
       0,     0,     0,     0,     0,     0,     0,    45,     0,    46,
       0,     0,     0,     0,     0,     0,     0,    47,    48,     0,
      49,    50,    51,     0,    52,    53,     0,    54,     0,     0,
      55,    56,    57,     0,    58,     0,    59,     0,    60,    61,
       0,    62,    63,     1,     0,     2,     3,     4,     5,     6,
       0,     7,     8,     9,    10,    11,     0,     0,     0,    12,
       0,    13,    14,     0,     0,     0,     0,    15,    16,     0,
       0,    17,     0,     0,     0,     0,     0,     0,    18,    19,
       0,     0,    20,     0,     0,     0,     0,     0,    21,    22,
       0,     0,     0,     0,    23,    24,    25,     0,     0,    26,
       0,     0,    27,     0,    28,    29,     0,    30,     0,     0,
       0,    31,    32,    33,     0,    34,    35,    36,     0,   349,
      37,     0,    38,     0,    39,    40,    41,     0,    42,    43,
       0,    44,     0,     0,     0,     0,     0,     0,     0,     0,
      45,     0,    46,     0,     0,     0,     0,     0,     0,     0,
      47,    48,   536,    49,    50,    51,     0,    52,    53,     0,
      54,     0,     0,    55,    56,    57,     0,    58,     0,    59,
       0,    60,    61,     0,    62,    63,     1,     0,     2,     3,
       4,     5,     6,     0,     7,     8,     9,    10,    11,     0,
       0,     0,    12,     0,    13,    14,     0,     0,     0,     0,
      15,    16,     0,     0,    17,     0,     0,     0,     0,     0,
       0,    18,    19,     0,     0,    20,     0,     0,     0,   215,
       0,    21,    22,     0,     0,     0,     0,    23,    24,    25,
       0,     0,    26,     0,     0,    27,     0,    28,    29,     0,
      30,     0,     0,     0,    31,    32,    33,     0,    34,    35,
      36,     0,   540,    37,     0,    38,     0,    39,    40,    41,
       0,    42,    43,     0,    44,     0,     0,     0,     0,     0,
       0,     0,     0,    45,     0,    46,     0,     0,     0,     0,
       0,     0,     0,    47,    48,     0,    49,    50,    51,     0,
      52,    53,     0,    54,     0,     0,    55,    56,    57,     0,
      58,     0,    59,     0,    60,    61,     0,    62,    63,     1,
       0,     2,     3,     4,     5,     6,     0,     7,     8,     9,
      10,    11,     0,     0,     0,    12,     0,    13,    14,     0,
       0,     0,     0,    15,    16,     0,     0,    17,     0,     0,
       0,     0,     0,     0,    18,    19,     0,     0,    20,  -197,
       0,     0,     0,     0,    21,    22,     0,     0,     0,     0,
      23,    24,    25,     0,     0,    26,     0,     0,    27,     0,
      28,    29,     0,    30,     0,     0,     0,    31,    32,    33,
       0,    34,    35,    36,     0,     0,    37,     0,    38,     0,
      39,    40,    41,     0,    42,    43,     0,    44,     0,     0,
       0,     0,     0,     0,     0,     0,    45,     0,    46,     0,
       0,     0,     0,     0,     0,     0,    47,    48,     0,    49,
      50,    51,     0,    52,    53,     0,    54,     0,     0,    55,
      56,    57,     0,    58,     0,    59,     0,    60,    61,     0,
      62,    63,     1,     0,     2,     3,     4,     5,     6,     0,
       7,     8,     9,    10,    11,     0,     0,     0,    12,     0,
      13,    14,     0,     0,     0,     0,    15,    16,     0,     0,
      17,     0,     0,     0,     0,     0,     0,    18,    19,     0,
       0,    20,     0,     0,     0,     0,     0,    21,    22,     0,
       0,     0,     0,    23,    24,    25,     0,     0,    26,     0,
       0,    27,     0,    28,    29,     0,    30,     0,     0,     0,
      31,    32,    33,     0,    34,    35,    36,   106,     0,    37,
       0,    38,     0,    39,    40,    41,     0,    42,    43,     0,
      44,     0,     0,     0,     0,     0,     0,     0,     0,    45,
       0,    46,     0,     0,     0,     0,     0,     0,     0,    47,
      48,     0,    49,    50,    51,     0,    52,    53,     0,    54,
       0,     0,    55,    56,    57,     0,    58,     0,    59,     0,
      60,    61,     0,    62,    63,     1,     0,     2,     3,     4,
       5,     6,     0,     7,     8,     9,    10,    11,     0,     0,
       0,    12,   191,    13,    14,     0,     0,     0,     0,    15,
      16,     0,     0,    17,     0,     0,     0,     0,     0,     0,
      18,    19,     0,     0,    20,     0,     0,     0,     0,     0,
      21,    22,     0,     0,     0,     0,    23,    24,    25,     0,
       0,    26,     0,     0,    27,     0,    28,    29,     0,    30,
       0,     0,     0,    31,    32,    33,     0,    34,    35,    36,
       0,     0,    37,     0,    38,     0,    39,    40,    41,     0,
      42,    43,     0,    44,     0,     0,     0,     0,     0,     0,
       0,     0,    45,     0,    46,     0,     0,     0,     0,     0,
       0,     0,    47,    48,     0,    49,    50,    51,     0,    52,
      53,     0,    54,     0,     0,    55,    56,    57,     0,    58,
       0,    59,     0,    60,    61,     0,    62,    63,     1,     0,
       2,     3,     4,     5,     6,     0,     7,     8,     9,    10,
      11,     0,     0,     0,    12,     0,    13,    14,     0,     0,
       0,     0,    15,    16,     0,     0,    17,     0,     0,     0,
       0,     0,     0,    18,    19,     0,     0,    20,     0,     0,
       0,  -195,     0,    21,    22,     0,     0,     0,     0,    23,
      24,    25,     0,     0,    26,     0,     0,    27,     0,    28,
      29,     0,    30,     0,     0,     0,    31,    32,    33,     0,
      34,    35,    36,     0,     0,    37,     0,    38,     0,    39,
      40,    41,     0,    42,    43,     0,    44,     0,     0,     0,
       0,     0,     0,     0,     0,    45,     0,    46,     0,     0,
       0,     0,     0,     0,     0,    47,    48,     0,    49,    50,
      51,     0,    52,    53,     0,    54,     0,     0,    55,    56,
      57,     0,    58,     0,    59,     0,    60,    61,     0,    62,
      63,     1,     0,     2,     3,     4,     5,     6,     0,     7,
       8,     9,    10,    11,     0,     0,     0,    12,  -197,    13,
      14,     0,     0,     0,     0,    15,    16,     0,     0,    17,
       0,     0,     0,     0,     0,     0,    18,    19,     0,     0,
      20,     0,     0,     0,     0,     0,    21,    22,     0,     0,
       0,     0,    23,    24,    25,     0,     0,    26,     0,     0,
      27,     0,    28,    29,     0,    30,     0,     0,     0,    31,
      32,    33,     0,    34,    35,    36,     0,     0,    37,     0,
      38,     0,    39,    40,    41,     0,    42,    43,     0,    44,
       0,     0,     0,     0,     0,     0,     0,     0,    45,     0,
      46,     0,     0,     0,     0,     0,     0,     0,    47,    48,
       0,    49,    50,    51,     0,    52,    53,     0,    54,     0,
       0,    55,    56,    57,     0,    58,     0,    59,     0,    60,
      61,     0,    62,    63,     1,     0,     2,     3,     4,     5,
       6,     0,     7,     8,     9,    10,    11,     0,     0,     0,
      12,   254,    13,    14,     0,     0,     0,     0,    15,    16,
       0,     0,    17,     0,     0,     0,     0,     0,     0,    18,
      19,     0,     0,    20,     0,     0,     0,     0,     0,    21,
      22,     0,     0,     0,     0,    23,    24,    25,     0,     0,
      26,     0,     0,    27,     0,    28,    29,     0,    30,     0,
       0,     0,    31,    32,    33,     0,    34,    35,    36,     0,
       0,    37,     0,    38,     0,    39,    40,    41,     0,    42,
      43,     0,    44,     0,     0,     0,     0,     0,     0,     0,
       0,    45,     0,    46,     0,     0,     0,     0,     0,     0,
       0,    47,    48,     0,    49,    50,    51,     0,    52,    53,
       0,    54,     0,     0,    55,    56,    57,     0,    58,     0,
      59,     0,    60,    61,     0,    62,    63,     1,     0,     2,
       3,     4,     5,     6,   284,     7,     8,     9,    10,    11,
       0,     0,     0,    12,     0,    13,    14,     0,     0,     0,
       0,    15,    16,     0,     0,    17,     0,     0,     0,     0,
       0,     0,    18,    19,     0,     0,    20,     0,     0,     0,
       0,     0,    21,    22,     0,     0,     0,     0,    23,    24,
      25,     0,     0,    26,     0,     0,    27,     0,    28,    29,
       0,    30,     0,     0,     0,    31,    32,    33,     0,    34,
      35,    36,     0,     0,    37,     0,    38,     0,    39,    40,
      41,     0,    42,    43,     0,    44,     0,     0,     0,     0,
       0,     0,     0,     0,    45,     0,    46,     0,     0,     0,
       0,     0,     0,     0,    47,    48,     0,    49,    50,    51,
       0,    52,    53,     0,    54,     0,     0,    55,    56,    57,
       0,    58,     0,    59,     0,    60,    61,     0,    62,    63,
       1,     0,     2,     3,     4,     5,     6,     0,     7,     8,
       9,    10,    11,     0,     0,     0,    12,     0,    13,    14,
       0,     0,     0,     0,    15,    16,     0,     0,    17,     0,
       0,     0,     0,     0,     0,    18,    19,     0,     0,    20,
       0,     0,     0,     0,     0,    21,    22,     0,     0,     0,
       0,    23,    24,    25,     0,     0,    26,     0,     0,    27,
       0,    28,    29,     0,    30,     0,     0,     0,    31,    32,
      33,     0,    34,    35,    36,     0,     0,    37,   331,    38,
       0,    39,    40,    41,     0,    42,    43,     0,    44,     0,
       0,     0,     0,     0,     0,     0,     0,    45,     0,    46,
       0,     0,     0,     0,     0,     0,     0,    47,    48,     0,
      49,    50,    51,     0,    52,    53,     0,    54,     0,     0,
      55,    56,    57,     0,    58,     0,    59,     0,    60,    61,
       0,    62,    63,     1,     0,     2,     3,     4,     5,     6,
       0,     7,     8,     9,    10,    11,     0,     0,     0,    12,
       0,    13,    14,     0,     0,     0,     0,    15,    16,     0,
       0,    17,     0,     0,     0,     0,     0,     0,    18,    19,
       0,     0,    20,     0,     0,     0,     0,     0,    21,    22,
       0,     0,     0,     0,    23,    24,    25,     0,     0,    26,
       0,     0,    27,     0,    28,    29,     0,    30,     0,     0,
       0,    31,    32,    33,     0,    34,    35,    36,     0,   349,
      37,     0,    38,     0,    39,    40,    41,     0,    42,    43,
       0,    44,     0,     0,     0,     0,     0,     0,     0,     0,
      45,     0,    46,     0,     0,     0,     0,     0,     0,     0,
      47,    48,     0,    49,    50,    51,     0,    52,    53,     0,
      54,     0,     0,    55,    56,    57,     0,    58,     0,    59,
       0,    60,    61,     0,    62,    63,     1,     0,     2,     3,
       4,     5,     6,     0,     7,     8,     9,    10,    11,     0,
       0,     0,    12,     0,    13,    14,     0,     0,     0,     0,
      15,    16,     0,     0,    17,     0,     0,     0,     0,     0,
       0,    18,    19,     0,     0,    20,     0,     0,     0,     0,
       0,    21,    22,     0,     0,     0,     0,    23,    24,    25,
       0,     0,    26,     0,     0,    27,     0,    28,    29,     0,
      30,     0,     0,     0,    31,    32,    33,     0,    34,    35,
      36,     0,   243,    37,     0,    38,     0,    39,    40,    41,
       0,    42,    43,     0,    44,     0,     0,     0,     0,     0,
       0,     0,     0,    45,     0,    46,     0,     0,     0,     0,
       0,     0,     0,    47,   244,     0,    49,    50,    51,     0,
      52,    53,     0,    54,     0,     0,    55,    56,    57,     0,
      58,     0,    59,     0,    60,    61,     0,    62,    63,     1,
       0,     2,     3,     4,     5,     6,     0,     7,     8,     9,
      10,    11,     0,     0,     0,    12,     0,    13,    14,     0,
       0,     0,     0,    15,    16,     0,     0,    17,     0,     0,
       0,     0,     0,     0,    18,    19,     0,     0,    20,     0,
       0,     0,     0,     0,    21,    22,     0,     0,     0,   354,
      23,    24,    25,     0,     0,    26,     0,     0,    27,     0,
      28,    29,     0,    30,     0,     0,     0,    31,    32,    33,
       0,    34,    35,    36,     0,     0,    37,     0,    38,     0,
      39,    40,    41,     0,    42,    43,     0,    44,     0,     0,
       0,     0,     0,     0,     0,     0,    45,     0,    46,     0,
       0,     0,     0,     0,     0,     0,    47,    48,     0,    49,
      50,    51,     0,    52,    53,     0,    54,     0,     0,    55,
      56,    57,     0,    58,     0,    59,     0,    60,    61,     0,
      62,    63,     1,     0,     2,     3,     4,     5,     6,     0,
       7,     8,     9,    10,    11,     0,     0,     0,    12,     0,
      13,    14,     0,     0,     0,     0,    15,    16,     0,     0,
      17,     0,     0,     0,     0,     0,     0,    18,    19,     0,
       0,    20,     0,     0,     0,     0,     0,    21,    22,     0,
       0,     0,     0,    23,    24,    25,     0,     0,    26,     0,
       0,    27,     0,    28,    29,     0,    30,     0,     0,     0,
      31,    32,    33,     0,    34,   465,    36,   106,     0,    37,
       0,    38,     0,    39,    40,    41,     0,    42,    43,     0,
      44,     0,     0,     0,     0,     0,     0,     0,     0,    45,
       0,    46,     0,     0,     0,     0,     0,     0,     0,    47,
      48,     0,    49,    50,    51,     0,    52,    53,     0,    54,
       0,     0,    55,    56,    57,     0,    58,     0,    59,     0,
      60,    61,     0,    62,    63,     1,     0,     2,     3,     4,
       5,     6,     0,     7,     8,     9,    10,    11,     0,     0,
       0,    12,  -195,    13,    14,     0,     0,     0,     0,    15,
      16,     0,     0,    17,     0,     0,     0,     0,     0,     0,
      18,    19,     0,     0,    20,     0,     0,     0,     0,     0,
      21,    22,     0,     0,     0,     0,    23,    24,    25,     0,
       0,    26,     0,     0,    27,     0,    28,    29,     0,    30,
       0,     0,     0,    31,    32,    33,     0,    34,    35,    36,
       0,     0,    37,     0,    38,     0,    39,    40,    41,     0,
      42,    43,     0,    44,     0,     0,     0,     0,     0,     0,
       0,     0,    45,     0,    46,     0,     0,     0,     0,     0,
       0,     0,    47,    48,     0,    49,    50,    51,     0,    52,
      53,     0,    54,     0,     0,    55,    56,    57,     0,    58,
       0,    59,     0,    60,    61,     0,    62,    63,     1,     0,
       2,     3,     4,     5,     6,     0,     7,     8,     9,    10,
      11,     0,     0,     0,    12,     0,    13,    14,     0,     0,
       0,     0,    15,    16,     0,     0,    17,     0,     0,     0,
       0,     0,     0,    18,    19,     0,     0,    20,     0,     0,
       0,     0,     0,    21,    22,     0,     0,     0,     0,    23,
      24,    25,     0,     0,    26,     0,     0,    27,     0,    28,
      29,     0,    30,     0,     0,     0,    31,    32,    33,     0,
      34,    35,    36,     0,     0,    37,     0,    38,     0,    39,
      40,    41,     0,    42,    43,     0,    44,     0,     0,     0,
       0,     0,     0,     0,     0,    45,     0,    46,     0,     0,
       0,     0,     0,     0,     0,    47,    48,     0,    49,    50,
      51,     0,    52,    53,     0,    54,   498,     0,    55,    56,
      57,     0,    58,     0,    59,     0,    60,    61,     0,    62,
      63,     1,     0,     2,     3,     4,     5,     6,     0,     7,
       8,     9,    10,    11,     0,     0,     0,    12,     0,    13,
      14,     0,     0,     0,     0,    15,    16,     0,     0,    17,
       0,     0,     0,     0,     0,     0,    18,    19,     0,     0,
      20,     0,     0,     0,     0,     0,    21,    22,     0,     0,
       0,     0,    23,    24,    25,     0,     0,    26,     0,     0,
      27,     0,    28,    29,     0,    30,     0,     0,     0,    31,
      32,    33,     0,    34,    35,    36,     0,     0,    37,     0,
      38,     0,    39,    40,    41,     0,    42,    43,     0,    44,
       0,     0,     0,     0,     0,     0,     0,     0,    45,     0,
      46,     0,     0,     0,     0,     0,     0,     0,    47,    48,
     517,    49,    50,    51,     0,    52,    53,     0,    54,     0,
       0,    55,    56,    57,     0,    58,     0,    59,     0,    60,
      61,     0,    62,    63,     1,     0,     2,     3,     4,     5,
       6,     0,     7,     8,     9,    10,    11,     0,     0,     0,
      12,     0,    13,    14,     0,     0,     0,     0,    15,    16,
       0,     0,    17,     0,     0,     0,     0,     0,     0,    18,
      19,     0,     0,    20,     0,     0,     0,     0,     0,    21,
      22,     0,     0,     0,     0,    23,    24,    25,     0,     0,
      26,     0,     0,    27,     0,    28,    29,     0,    30,     0,
       0,     0,    31,    32,    33,     0,    34,    35,    36,     0,
       0,    37,     0,    38,     0,    39,    40,    41,     0,    42,
      43,     0,    44,     0,     0,     0,     0,     0,     0,     0,
       0,    45,     0,    46,     0,     0,     0,     0,     0,     0,
       0,    47,    48,   538,    49,    50,    51,     0,    52,    53,
       0,    54,     0,     0,    55,    56,    57,     0,    58,     0,
      59,     0,    60,    61,     0,    62,    63,     1,     0,     2,
       3,     4,     5,     6,     0,     7,     8,     9,    10,    11,
       0,     0,     0,    12,     0,    13,    14,     0,     0,     0,
       0,    15,    16,     0,     0,    17,     0,     0,     0,     0,
       0,     0,    18,    19,     0,     0,    20,     0,     0,     0,
       0,     0,    21,    22,     0,     0,     0,     0,    23,    24,
      25,     0,     0,    26,     0,     0,    27,     0,    28,    29,
       0,    30,     0,     0,     0,    31,    32,    33,     0,    34,
      35,    36,     0,     0,    37,     0,    38,     0,    39,    40,
      41,     0,    42,    43,     0,    44,     0,     0,     0,     0,
       0,     0,     0,     0,    45,     0,    46,     0,     0,     0,
       0,     0,     0,     0,    47,    48,     0,    49,    50,    51,
       0,    52,    53,     0,    54,     0,     0,    55,    56,    57,
       0,    58,     0,    59,     0,    60,    61,     0,    62,    63,
       1,     0,     2,     3,     4,     5,     6,     0,     7,     8,
       9,    10,    81,     0,     0,     0,    12,     0,    13,    14,
       0,     0,     0,     0,    15,    16,     0,     0,    17,     0,
       0,     0,     0,     0,     0,    18,    19,     0,     0,    20,
       0,     0,     0,     0,     0,    21,    22,     0,     0,     0,
       0,    23,    24,    25,     0,     0,    26,     0,     0,    27,
       0,    28,    29,     0,    30,     0,     0,     0,    31,    32,
      33,     0,    34,    35,    36,     0,     0,    82,     0,    38,
       0,    39,    40,    41,     0,    42,    43,     0,    44,     0,
       0,     0,     0,     0,     0,     0,     0,    45,     0,    46,
       0,     0,     0,     0,     0,     0,     0,    47,    48,     0,
      49,    50,    51,     0,    52,    53,     0,    54,     0,     0,
      55,    56,    57,     0,    58,     0,    59,     0,    60,    61,
       0,    62,    63,     1,     0,     2,     3,     4,     5,     6,
       0,     7,     8,     9,    10,    11,     0,     0,     0,    90,
       0,    13,    14,     0,     0,     0,     0,    15,    16,     0,
       0,    17,     0,     0,     0,     0,     0,     0,    18,    19,
       0,     0,    20,     0,     0,     0,     0,     0,    21,    22,
       0,     0,     0,     0,    23,    24,    25,     0,     0,    26,
       0,     0,    27,     0,    28,    29,     0,    30,     0,     0,
       0,    31,    32,    33,     0,    34,    35,    36,     0,     0,
      37,     0,    38,     0,    39,    40,    41,     0,    42,    43,
       0,    44,     0,     0,     0,     0,     0,     0,     0,     0,
      45,     0,    46,     0,     0,     0,     0,     0,     0,     0,
      47,    48,     0,    49,    50,    51,     0,    52,    53,     0,
      54,     0,     0,    55,    56,    57,     0,    58,     0,    59,
       0,    60,    61,     0,    62,    63,     1,     0,     2,     3,
       4,     5,     6,     0,     7,     8,     9,    10,    11,     0,
       0,     0,    94,     0,    13,    14,     0,     0,     0,     0,
      15,    16,     0,     0,    17,     0,     0,     0,     0,     0,
       0,    18,    19,     0,     0,    20,     0,     0,     0,     0,
       0,    21,    22,     0,     0,     0,     0,    23,    24,    25,
       0,     0,    26,     0,     0,    27,     0,    28,    29,     0,
      30,     0,     0,     0,    31,    32,    33,     0,    34,    35,
      36,     0,     0,    37,     0,    38,     0,    39,    40,    41,
       0,    42,    43,     0,    44,     0,     0,     0,     0,     0,
       0,     0,     0,    45,     0,    46,     0,     0,     0,     0,
       0,     0,     0,    47,    48,     0,    49,    50,    51,     0,
      52,    53,     0,    54,     0,     0,    55,    56,    57,     0,
      58,     0,    59,     0,    60,    61,     0,    62,    63,     1,
       0,     2,     3,     4,     5,     6,     0,     7,     8,     9,
      10,    11,     0,     0,     0,   102,     0,    13,    14,     0,
       0,     0,     0,    15,    16,     0,     0,    17,     0,     0,
       0,     0,     0,     0,    18,    19,     0,     0,    20,     0,
       0,     0,     0,     0,    21,    22,     0,     0,     0,     0,
      23,    24,    25,     0,     0,    26,     0,     0,    27,     0,
      28,    29,     0,    30,     0,     0,     0,    31,    32,    33,
       0,    34,    35,    36,     0,     0,    37,     0,    38,     0,
      39,    40,    41,     0,    42,    43,     0,    44,     0,     0,
       0,     0,     0,     0,     0,     0,    45,     0,    46,     0,
       0,     0,     0,     0,     0,     0,    47,    48,     0,    49,
      50,    51,     0,    52,    53,     0,    54,     0,     0,    55,
      56,    57,     0,    58,     0,    59,     0,    60,    61,     0,
      62,    63,     1,     0,     2,   204,     4,     5,     6,     0,
       7,     8,     9,    10,    11,     0,     0,     0,    12,     0,
      13,    14,     0,     0,     0,     0,    15,    16,     0,     0,
      17,     0,     0,     0,     0,     0,     0,    18,    19,     0,
       0,    20,     0,     0,     0,     0,     0,    21,    22,     0,
       0,     0,     0,    23,    24,    25,     0,     0,    26,     0,
       0,    27,     0,    28,    29,     0,    30,     0,     0,     0,
      31,    32,    33,     0,    34,    35,    36,     0,     0,    37,
       0,    38,     0,    39,    40,    41,     0,    42,    43,     0,
      44,     0,     0,     0,     0,     0,     0,     0,     0,    45,
       0,    46,     0,     0,     0,     0,     0,     0,     0,    47,
      48,     0,    49,    50,    51,     0,    52,    53,     0,    54,
       0,     0,    55,    56,    57,     0,    58,     0,    59,     0,
      60,    61,     0,    62,    63,     1,     0,     2,     3,     4,
       5,     6,     0,     7,     8,     9,    10,    11,     0,     0,
       0,    12,     0,    13,    14,     0,     0,     0,     0,    15,
      16,     0,     0,    17,     0,     0,     0,     0,     0,     0,
      18,    19,     0,     0,   282,     0,     0,     0,     0,     0,
      21,    22,     0,     0,     0,     0,    23,    24,    25,     0,
       0,    26,     0,     0,    27,     0,    28,    29,     0,    30,
       0,     0,     0,    31,    32,    33,     0,    34,    35,    36,
       0,     0,    37,     0,    38,     0,    39,    40,    41,     0,
      42,    43,     0,    44,     0,     0,     0,     0,     0,     0,
       0,     0,    45,     0,    46,     0,     0,     0,     0,     0,
       0,     0,    47,    48,     0,    49,    50,    51,     0,    52,
      53,     0,    54,     0,     0,    55,    56,    57,     0,    58,
       0,    59,     0,    60,    61,     0,    62,    63,     1,     0,
       2,     3,     4,     5,     6,     0,     7,     8,     9,    10,
      11,     0,     0,     0,    12,     0,    13,    14,     0,     0,
       0,     0,    15,    16,     0,     0,    17,     0,     0,     0,
       0,     0,     0,    18,    19,     0,     0,   421,     0,     0,
       0,     0,     0,    21,    22,     0,     0,     0,     0,    23,
      24,    25,     0,     0,    26,     0,     0,    27,     0,    28,
      29,     0,    30,     0,     0,     0,    31,    32,    33,     0,
      34,    35,    36,     0,     0,    37,     0,    38,     0,    39,
      40,    41,     0,    42,    43,     0,    44,     0,     0,     0,
       0,     0,     0,     0,     0,    45,     0,    46,     0,     0,
       0,     0,     0,     0,     0,    47,    48,     0,    49,    50,
      51,     0,    52,    53,     0,    54,     0,     0,    55,    56,
      57,     0,    58,     0,    59,     0,    60,    61,     0,    62,
      63,     1,     0,     2,     3,     4,     5,     6,     0,     7,
       8,     9,    10,    11,     0,     0,     0,    12,     0,    13,
      14,     0,     0,     0,     0,    15,    16,     0,     0,    17,
       0,     0,     0,     0,     0,     0,    18,    19,     0,     0,
      20,     0,     0,     0,     0,     0,    21,    22,     0,     0,
       0,     0,    23,    24,    25,     0,     0,    26,     0,     0,
      27,     0,    28,    29,     0,    30,     0,     0,     0,    31,
      32,    33,     0,    34,    35,    36,     0,     0,    37,     0,
      38,     0,    39,    40,    41,     0,    42,    43,     0,    44,
       0,     0,     0,     0,     0,     0,     0,     0,    45,     0,
      46,     0,     0,     0,     0,     0,     0,     0,    47,   496,
       0,    49,    50,    51,     0,    52,    53,     0,    54,     0,
       0,    55,    56,    57,     0,    58,     0,    59,     0,    60,
      61,     0,    62,    63,     1,     0,     2,     3,     4,     5,
       6,     0,     7,     8,     9,    10,    11,     0,     0,     0,
      12,     0,    13,    14,     0,     0,     0,     0,    15,    16,
       0,     0,    17,     0,     0,     0,     0,     0,     0,    18,
      19,     0,     0,    20,     0,     0,     0,     0,     0,    21,
      22,     0,     0,     0,     0,    23,    24,    25,     0,     0,
      26,     0,     0,    27,     0,    28,    29,     0,    30,     0,
       0,     0,    31,    32,    33,     0,    34,    35,    36,     0,
       0,    37,     0,    38,     0,    39,    40,    41,     0,    42,
      43,     0,    44,     0,     0,     0,     0,     0,     0,     0,
       0,    45,     0,    46,     0,     0,     0,     0,     0,     0,
       0,    47,   521,     0,    49,    50,    51,     0,    52,    53,
       0,    54,     0,     0,    55,    56,    57,     0,    58,     0,
      59,   148,    60,    61,     0,    62,    63,   149,   150,   151,
     152,     0,   153,   154,   155,   156,   157,   158,     0,     0,
     159,   160,   161,   162,   163,   164,   165,   166,   167,   168,
     169,   170,     0,     0,     0,     0,     0,   171,   172,     0,
     205,     0,     0,   149,   150,   151,   152,     0,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   160,   161,   162,
     163,   164,   165,   166,   167,   168,   169,   170,     0,     0,
       0,     0,     0,     0,   172,     0,     0,     0,   173,     0,
     149,   150,   151,   152,   206,   153,   154,   155,   156,   157,
     158,     0,     0,   159,   160,   161,   162,   163,   164,   165,
     166,   167,   168,   169,   170,     0,     0,   174,     0,     0,
     175,   172,     0,   176,   173,     0,   200,   177,   178,     0,
       0,   179,     0,     0,   180,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,   174,     0,     0,   175,     0,     0,   176,
       0,   173,     0,   177,   178,     0,     0,   179,     0,     0,
     180,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,   201,     0,     0,     0,     0,
     174,     0,     0,   175,     0,     0,   176,     0,     0,     0,
     177,   178,     0,     0,   179,     0,     0,   180,   149,   150,
     151,   152,   194,   153,   154,   155,   156,   157,   158,     0,
       0,   159,   160,   161,   162,   163,   164,   165,   166,   167,
     168,   169,   170,     0,     0,   149,   150,   151,   152,   172,
     153,   154,   155,   156,   157,   158,     0,   197,   159,   160,
     161,   162,   163,   164,   165,   166,   167,   168,   169,   170,
       0,     0,     0,     0,     0,     0,   172,     0,   149,     0,
     151,   152,     0,   153,   154,   155,   156,   157,   158,   173,
       0,   159,   160,   161,   162,   163,   164,   165,   166,   167,
     168,   169,   170,     0,     0,     0,     0,     0,     0,   172,
       0,     0,     0,     0,     0,     0,   173,     0,   174,     0,
       0,   175,     0,     0,   176,     0,     0,     0,   177,   178,
       0,     0,   179,     0,     0,   180,     0,     0,     0,     0,
       0,     0,     0,     0,     0,   174,     0,     0,   175,   173,
       0,   176,     0,     0,     0,   177,   178,     0,     0,   179,
       0,     0,   180,   149,   150,   151,   152,     0,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   160,   161,   162,
     163,   164,   165,   166,   167,   168,   169,   170,   177,   178,
       0,     0,   179,     0,   172,   180,     0,   149,   150,   151,
     152,     0,   153,   154,   155,   156,   157,   158,     0,   220,
     159,   160,   161,   162,   163,   164,   165,   166,   167,   168,
     169,   170,     0,     0,     0,     0,     0,     0,   172,     0,
       0,     0,     0,     0,   173,     0,     0,     0,     0,     0,
       0,     0,     0,   221,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,   174,     0,     0,   175,     0,   173,   176,
       0,     0,     0,   177,   178,     0,     0,   179,     0,     0,
     180,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,   174,     0,     0,
     175,     0,     0,   176,     0,     0,     0,   177,   178,     0,
       0,   179,     0,     0,   180,   149,   150,   151,   152,     0,
     153,   154,   155,   156,   157,   158,     0,     0,   159,   160,
     161,   162,   163,   164,   165,   166,   167,   168,   169,   170,
       0,     0,   149,   150,   151,   152,   172,   153,   154,   155,
     156,   157,   158,     0,     0,   159,   160,   161,   162,   163,
     164,   165,   166,   167,   168,   169,   170,     0,     0,     0,
       0,     0,     0,   172,     0,   149,   150,   151,   152,   304,
     153,   154,   155,   156,   157,   158,   173,     0,   159,   160,
     161,   162,   163,   164,   165,   166,   167,   168,   169,   170,
     240,     0,     0,     0,     0,     0,   172,     0,     0,     0,
       0,     0,     0,   173,     0,   174,     0,     0,   175,     0,
       0,   176,     0,     0,     0,   177,   178,     0,     0,   179,
       0,     0,   180,     0,     0,     0,     0,   247,     0,     0,
       0,     0,   174,     0,     0,   175,   173,     0,   176,     0,
       0,     0,   177,   178,     0,     0,   179,     0,     0,   180,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,   174,     0,     0,   175,     0,
       0,   176,     0,     0,     0,   177,   178,     0,     0,   179,
       0,     0,   180,   149,   150,   151,   152,     0,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   160,   161,   162,
     163,   164,   165,   166,   167,   168,   169,   170,     0,     0,
     305,     0,     0,     0,   172,     0,   149,   150,   151,   152,
     307,   153,   154,   155,   156,   157,   158,     0,     0,   159,
     160,   161,   162,   163,   164,   165,   166,   167,   168,   169,
     170,     0,     0,     0,     0,     0,     0,   172,     0,     0,
       0,     0,     0,     0,   173,     0,     0,   149,   150,   151,
     152,   311,   153,   154,   155,   156,   157,   158,     0,     0,
     159,   160,   161,   162,   163,   164,   165,   166,   167,   168,
     169,   170,     0,   174,     0,     0,   175,   173,   172,   176,
       0,     0,     0,   177,   178,     0,     0,   179,     0,     0,
     180,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,   174,     0,     0,   175,
       0,     0,   176,     0,     0,     0,   177,   178,   173,     0,
     179,     0,     0,   180,   152,     0,   153,   154,   155,   156,
     157,   158,     0,     0,   159,   160,   161,   162,   163,   164,
     165,     0,   167,   168,   169,   170,     0,   174,     0,     0,
     175,     0,     0,   176,     0,     0,     0,   177,   178,     0,
       0,   179,     0,     0,   180,   149,   150,   151,   152,   312,
     153,   154,   155,   156,   157,   158,     0,     0,   159,   160,
     161,   162,   163,   164,   165,   166,   167,   168,   169,   170,
       0,     0,   173,     0,     0,     0,   172,     0,   149,   150,
     151,   152,   317,   153,   154,   155,   156,   157,   158,     0,
       0,   159,   160,   161,   162,   163,   164,   165,   166,   167,
     168,   169,   170,     0,     0,     0,     0,     0,     0,   172,
       0,     0,   178,     0,     0,   179,   173,     0,   180,   149,
     150,   151,   152,   326,   153,   154,   155,   156,   157,   158,
       0,     0,   159,   160,   161,   162,   163,   164,   165,   166,
     167,   168,   169,   170,     0,   174,     0,     0,   175,   173,
     172,   176,     0,     0,     0,   177,   178,     0,     0,   179,
       0,     0,   180,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,   174,     0,
       0,   175,     0,     0,   176,     0,     0,     0,   177,   178,
     173,     0,   179,     0,     0,   180,   152,     0,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   160,   161,   162,
     163,   164,   165,     0,   167,   168,     0,   170,     0,   174,
       0,     0,   175,     0,     0,   176,     0,     0,     0,   177,
     178,     0,     0,   179,     0,     0,   180,   149,   150,   151,
     152,   337,   153,   154,   155,   156,   157,   158,     0,     0,
     159,   160,   161,   162,   163,   164,   165,   166,   167,   168,
     169,   170,     0,     0,   173,     0,     0,     0,   172,     0,
     149,   150,   151,   152,   340,   153,   154,   155,   156,   157,
     158,     0,     0,   159,   160,   161,   162,   163,   164,   165,
     166,   167,   168,   169,   170,     0,     0,     0,     0,     0,
       0,   172,     0,     0,   178,     0,     0,   179,   173,     0,
     180,   149,   150,   151,   152,   341,   153,   154,   155,   156,
     157,   158,     0,     0,   159,   160,   161,   162,   163,   164,
     165,   166,   167,   168,   169,   170,     0,   174,     0,     0,
     175,   173,   172,   176,     0,     0,     0,   177,   178,     0,
       0,   179,     0,     0,   180,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     174,     0,     0,   175,     0,     0,   176,     0,     0,     0,
     177,   178,   173,     0,   179,     0,     0,   180,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,   174,     0,     0,   175,     0,     0,   176,     0,     0,
       0,   177,   178,     0,     0,   179,     0,     0,   180,   149,
     150,   151,   152,   342,   153,   154,   155,   156,   157,   158,
       0,     0,   159,   160,   161,   162,   163,   164,   165,   166,
     167,   168,   169,   170,     0,     0,     0,     0,     0,     0,
     172,     0,   149,   150,   151,   152,   343,   153,   154,   155,
     156,   157,   158,     0,     0,   159,   160,   161,   162,   163,
     164,   165,   166,   167,   168,   169,   170,     0,     0,   149,
     150,   151,   152,   172,   153,   154,   155,   156,   157,   158,
     173,     0,   159,   160,   161,   162,   163,   164,   165,   166,
     167,   168,   169,   170,     0,     0,   344,     0,     0,     0,
     172,     0,     0,     0,     0,     0,     0,     0,     0,   174,
       0,     0,   175,   173,     0,   176,     0,     0,     0,   177,
     178,     0,     0,   179,     0,     0,   180,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     173,     0,   174,     0,     0,   175,     0,     0,   176,     0,
       0,     0,   177,   178,     0,     0,   179,     0,     0,   180,
       0,     0,     0,     0,     0,     0,     0,     0,     0,   174,
       0,     0,   175,     0,     0,   176,     0,     0,     0,   177,
     178,     0,     0,   179,     0,     0,   180,   149,   150,   151,
     152,   346,   153,   154,   155,   156,   157,   158,     0,     0,
     159,   160,   161,   162,   163,   164,   165,   166,   167,   168,
     169,   170,     0,     0,   149,   150,   151,   152,   172,   153,
     154,   155,   156,   157,   158,     0,     0,   159,   160,   161,
     162,   163,   164,   165,   166,   167,   168,   169,   170,     0,
       0,   356,     0,     0,     0,   172,     0,   149,   150,   151,
     152,     0,   153,   154,   155,   156,   157,   158,   173,     0,
     159,   160,   161,   162,   163,   164,   165,   166,   167,   168,
     169,   170,     0,     0,     0,     0,     0,     0,   172,     0,
       0,     0,     0,   357,     0,   173,     0,   174,     0,     0,
     175,     0,     0,   176,     0,     0,     0,   177,   178,     0,
       0,   179,     0,     0,   180,     0,     0,     0,     0,     0,
       0,     0,     0,     0,   174,     0,     0,   175,   173,     0,
     176,     0,     0,     0,   177,   178,     0,     0,   179,     0,
       0,   180,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,   174,     0,     0,
     175,     0,     0,   176,     0,     0,     0,   177,   178,     0,
       0,   179,     0,     0,   180,   149,   150,   151,   152,   359,
     153,   154,   155,   156,   157,   158,     0,     0,   159,   160,
     161,   162,   163,   164,   165,   166,   167,   168,   169,   170,
       0,     0,   149,   150,   151,   152,   172,   153,   154,   155,
     156,   157,   158,     0,     0,   159,   160,   161,   162,   163,
     164,   165,   166,   167,   168,   169,   170,     0,     0,   363,
       0,     0,     0,   172,     0,   149,   150,   151,   152,     0,
     153,   154,   155,   156,   157,   158,   173,   368,   159,   160,
     161,   162,   163,   164,   165,   166,   167,   168,   169,   170,
       0,     0,     0,     0,     0,     0,   172,     0,     0,     0,
       0,     0,     0,   173,     0,   174,     0,     0,   175,     0,
       0,   176,     0,     0,     0,   177,   178,     0,     0,   179,
       0,     0,   180,     0,     0,     0,     0,     0,     0,     0,
       0,     0,   174,     0,     0,   175,   173,     0,   176,     0,
       0,     0,   177,   178,     0,     0,   179,     0,     0,   180,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,   174,     0,     0,   175,     0,
       0,   176,     0,     0,     0,   177,   178,     0,     0,   179,
       0,     0,   180,   149,   150,   151,   152,     0,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   160,   161,   162,
     163,   164,   165,   166,   167,   168,   169,   170,     0,     0,
       0,     0,     0,     0,   172,     0,     0,   149,   150,   151,
     152,     0,   153,   154,   155,   156,   157,   158,     0,   384,
     159,   160,   161,   162,   163,   164,   165,   166,   167,   168,
     169,   170,     0,     0,     0,     0,     0,     0,   172,     0,
       0,     0,   151,   152,   173,   153,   154,   155,   156,   157,
     158,     0,     0,   159,   160,   161,   162,   163,   164,   165,
     166,   167,   168,   169,   170,     0,     0,     0,     0,     0,
     394,   172,     0,   174,     0,     0,   175,     0,   173,   176,
       0,     0,     0,   177,   178,     0,     0,   179,     0,     0,
     180,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,   174,     0,     0,
     175,   173,     0,   176,     0,     0,     0,   177,   178,     0,
       0,   179,     0,     0,   180,   149,   150,   151,   152,     0,
     153,   154,   155,   156,   157,   158,     0,     0,   159,   160,
     161,   162,   163,   164,   165,   166,   167,   168,   169,   170,
     177,   178,     0,     0,   179,   395,   172,   180,   149,   150,
     151,   152,     0,   153,   154,   155,   156,   157,   158,     0,
       0,   159,   160,   161,   162,   163,   164,   165,   166,   167,
     168,   169,   170,     0,     0,   419,     0,     0,     0,   172,
       0,     0,   149,   150,   151,   152,   173,   153,   154,   155,
     156,   157,   158,     0,     0,   159,   160,   161,   162,   163,
     164,   165,   166,   167,   168,   169,   170,     0,     0,     0,
       0,     0,   427,   172,     0,   174,     0,     0,   175,   173,
       0,   176,     0,     0,     0,   177,   178,     0,     0,   179,
       0,     0,   180,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,   174,     0,
       0,   175,     0,   173,   176,     0,     0,     0,   177,   178,
       0,     0,   179,     0,     0,   180,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,   174,     0,     0,   175,     0,     0,   176,     0,
       0,     0,   177,   178,     0,     0,   179,     0,     0,   180,
     149,   150,   151,   152,     0,   153,   154,   155,   156,   157,
     158,     0,     0,   159,   160,   161,   162,   163,   164,   165,
     166,   167,   168,   169,   170,     0,     0,     0,     0,     0,
       0,   172,     0,     0,     0,     0,   149,   150,   151,   152,
       0,   153,   154,   155,   156,   157,   158,   324,     0,   159,
     160,   161,   162,   163,   164,   165,   166,   167,   168,   169,
     170,     0,     0,     0,     0,     0,   446,   172,     0,     0,
       0,   173,     0,   149,   150,   151,   152,   452,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   160,   161,   162,
     163,   164,   165,   166,   167,   168,   169,   170,     0,     0,
     174,     0,     0,   175,   172,     0,   176,   173,     0,     0,
     177,   178,     0,     0,   179,     0,     0,   180,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,   174,     0,     0,   175,
       0,     0,   176,     0,   173,     0,   177,   178,     0,     0,
     179,     0,     0,   180,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,   174,     0,     0,   175,     0,     0,   176,
       0,     0,     0,   177,   178,     0,     0,   179,     0,     0,
     180,   149,   150,   151,   152,     0,   153,   154,   155,   156,
     157,   158,     0,     0,   159,   160,   161,   162,   163,   164,
     165,   166,   167,   168,   169,   170,     0,     0,   464,     0,
       0,     0,   172,     0,   149,   150,   151,   152,     0,   153,
     154,   155,   156,   157,   158,     0,     0,   159,   160,   161,
     162,   163,   164,   165,   166,   167,   168,   169,   170,     0,
       0,     0,     0,     0,     0,   172,     0,     0,     0,     0,
     472,     0,   173,     0,     0,   149,   150,   151,   152,     0,
     153,   154,   155,   156,   157,   158,     0,     0,   159,   160,
     161,   162,   163,   164,   165,   166,   167,   168,   169,   170,
       0,   174,     0,     0,   175,   173,   172,   176,     0,     0,
       0,   177,   178,     0,     0,   179,     0,     0,   180,     0,
       0,   485,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,   174,     0,     0,   175,     0,     0,
     176,     0,     0,     0,   177,   178,   173,     0,   179,     0,
       0,   180,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,   174,     0,     0,   175,     0,
       0,   176,     0,     0,     0,   177,   178,     0,     0,   179,
       0,     0,   180,   149,   150,   151,   152,     0,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   160,   161,   162,
     163,   164,   165,   166,   167,   168,   169,   170,     0,     0,
       0,     0,     0,     0,   172,     0,     0,   149,   150,   151,
     152,     0,   153,   154,   155,   156,   157,   158,     0,   487,
     159,   160,   161,   162,   163,   164,   165,   166,   167,   168,
     169,   170,     0,     0,   503,     0,     0,     0,   172,     0,
     149,   150,   151,   152,   173,   153,   154,   155,   156,   157,
     158,     0,     0,   159,   160,   161,   162,   163,   164,   165,
     166,   167,   168,   169,   170,     0,     0,     0,     0,     0,
     506,   172,     0,   174,     0,     0,   175,     0,   173,   176,
       0,     0,     0,   177,   178,     0,     0,   179,     0,     0,
     180,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,   174,     0,     0,
     175,   173,     0,   176,     0,     0,     0,   177,   178,     0,
       0,   179,     0,     0,   180,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     174,     0,     0,   175,     0,     0,   176,     0,     0,     0,
     177,   178,     0,     0,   179,     0,     0,   180,   149,   150,
     151,   152,     0,   153,   154,   155,   156,   157,   158,     0,
       0,   159,   160,   161,   162,   163,   164,   165,   166,   167,
     168,   169,   170,     0,     0,     0,     0,     0,     0,   172,
       0,     0,     0,     0,   508,   149,   150,   151,   152,     0,
     153,   154,   155,   156,   157,   158,     0,     0,   159,   160,
     161,   162,   163,   164,   165,   166,   167,   168,   169,   170,
       0,     0,     0,     0,     0,   543,   172,     0,     0,   173,
       0,   149,   150,   151,   152,     0,   153,   154,   155,   156,
     157,   158,     0,     0,   159,   160,   161,   162,   163,   164,
     165,   166,   167,   168,   169,   170,     0,     0,   174,     0,
       0,   175,   172,     0,   176,     0,   173,     0,   177,   178,
       0,     0,   179,     0,     0,   180,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,   174,     0,     0,   175,     0,
       0,   176,   173,     0,     0,   177,   178,     0,     0,   179,
       0,     0,   180,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,   174,     0,     0,   175,     0,     0,   176,     0,     0,
       0,   177,   178,     0,     0,   179,     0,     0,   180,   241,
     150,   151,   152,     0,   153,   154,   155,   156,   157,   158,
       0,     0,   159,   160,   161,   162,   163,   164,   165,   166,
     167,   168,   169,   170,     0,     0,   149,   150,   151,   152,
     172,   153,   154,   155,   156,   157,   158,     0,     0,   159,
     160,   161,   162,   163,   164,   165,   166,   167,   168,   169,
     170,     0,     0,     0,     0,     0,     0,   172,     0,   149,
    -235,   151,   152,     0,   153,   154,   155,   156,   157,   158,
     173,     0,   159,   160,   161,   162,   163,   164,   165,   166,
     167,   168,   169,   170,     0,     0,     0,     0,     0,     0,
     172,     0,     0,     0,     0,     0,     0,   173,     0,   174,
       0,     0,   175,     0,     0,   176,     0,     0,     0,   177,
     178,     0,     0,   179,     0,     0,   180,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,  -235,
     173,     0,   176,     0,     0,     0,   177,   178,     0,     0,
     179,     0,     0,   180,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,   176,     0,     0,     0,   177,
     178,     0,     0,   179,     0,     0,   180,   150,   151,   152,
       0,   153,   154,   155,   156,   157,   158,     0,     0,   159,
     160,   161,   162,   163,   164,   165,   166,   167,   168,   169,
     170,     0,     0,     0,     0,     0,     0,   172,     0,   151,
     152,     0,   153,   154,   155,   156,   157,   158,     0,     0,
     159,   160,   161,   162,   163,   164,   165,   166,   167,   168,
     169,   170,     0,     0,     0,     0,     0,     0,   172,     0,
       0,     0,     0,     0,     0,   151,   152,   173,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   160,   161,   162,
     163,   164,   165,   166,   167,   168,   169,   170,     0,     0,
       0,     0,     0,     0,   172,     0,   174,     0,   173,   175,
       0,     0,   176,     0,     0,     0,   177,   178,     0,     0,
     179,     0,     0,   180,   408,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,   173,     0,     0,   177,   178,     0,
       0,   179,   151,   152,   180,   153,   154,   155,   156,   157,
     158,     0,     0,   159,   160,   161,   162,   163,   164,   165,
     166,   167,   168,   169,   170,     0,     0,     0,     0,     0,
       0,   172,     0,  -235,   178,     0,     0,   179,     0,   152,
     180,   153,   154,   155,   156,   157,   158,     0,     0,   159,
       0,     0,   162,   163,   164,   165,     0,   167,   168,   152,
     170,   153,   154,   155,   156,   157,   158,     0,     0,   159,
       0,   173,   162,     0,   164,   165,     0,   167,   168,     0,
     170,   152,     0,   153,   154,   155,   156,   157,   158,     0,
       0,   159,     0,     0,   162,     0,   164,  -235,     0,   167,
     168,     0,   170,     0,     0,     0,     0,   173,     0,     0,
       0,   178,     0,     0,   179,     0,   152,   180,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   173,     0,   162,
       0,     0,     0,     0,   167,   168,   152,   170,     0,     0,
     155,   156,   157,   158,     0,     0,   159,   178,     0,   173,
     179,     0,     0,   180,   167,   168,     0,   170,   152,     0,
       0,     0,   155,   156,  -235,   158,     0,   178,   159,     0,
     179,     0,     0,   180,     0,     0,   167,   168,     0,   170,
       0,     0,     0,     0,   173,     0,     0,     0,     0,   178,
       0,     0,   179,     0,     0,   180,     0,     0,     0,     0,
       0,     0,     0,     0,   173,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,   178,     0,   173,   179,     0,     0,
     180,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,   178,     0,     0,   179,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,   178,     0,     0,   179
};

static const yytype_int16 yycheck[] =
{
       0,   318,   120,    24,    17,   225,    17,    51,   459,     4,
      10,   400,    12,    13,    14,    10,    16,    17,    13,    19,
      20,   372,    22,    17,    15,    25,    15,    44,    28,    29,
      24,    26,    18,    27,    14,    10,    15,    37,    46,    14,
      29,    35,    36,    60,    38,    45,    46,    47,    48,   500,
      50,    51,    15,    15,    67,    55,     3,     4,    58,    59,
      46,   450,    62,    10,    44,   109,    13,     3,     4,    58,
      17,     4,    83,    73,    74,    75,   427,    13,    67,     3,
       4,    17,    82,    74,    46,     4,     5,     4,    47,    13,
      90,    85,    92,    93,    94,    74,    55,     4,    98,    46,
      76,   101,   102,     4,    59,   105,    61,   128,   108,   109,
      46,   111,   112,    17,   114,   115,   116,   117,   338,   237,
      17,   121,    46,   440,    17,    26,    17,    46,   128,    17,
      50,   131,   132,    17,   128,    17,    27,   137,   133,   139,
     119,   141,    54,    55,   144,    81,   146,    38,    10,   149,
     150,   151,   152,   153,   154,   155,   156,   157,   158,    38,
     160,   161,   162,   163,   164,   165,   166,   167,   168,   169,
     170,   171,   172,    17,   121,   175,   176,   177,   178,    17,
     180,   181,   182,     0,    17,   121,    26,   407,   188,   109,
      17,    24,     4,    41,    27,    35,   196,   121,    72,    46,
      38,   201,    35,    36,   424,    38,    17,    53,    48,    17,
     210,   211,   212,   213,    54,    17,   507,    59,    60,     3,
       4,    63,   222,   223,     3,     4,   226,    38,    44,    13,
     230,    47,   152,    17,    13,    30,    38,   528,    17,    55,
      17,   241,   242,    88,   244,   465,   109,    17,    77,   249,
     122,   251,    85,    17,    49,     3,   256,    52,    53,    56,
     128,   181,    46,    44,   132,    17,    61,    46,    81,    18,
       3,     4,   112,     6,    18,    18,    51,    10,    76,    79,
      13,    14,   282,    17,    60,    44,    19,    18,    21,    22,
      24,    18,   125,    27,   294,   128,    18,    18,    18,    46,
      29,    35,    36,    41,    38,   151,   306,   307,   308,    51,
     230,   106,     3,    46,    77,   315,    18,    45,   318,    45,
      18,   109,   242,   323,   324,    17,   326,   327,   328,   169,
      22,   251,    24,   333,   174,    27,   336,   121,    17,   171,
      41,   109,   121,    35,    36,    24,    38,   142,    27,   109,
     350,   351,   352,    17,   200,   109,    35,    36,   358,    38,
      24,   361,    56,    27,   427,   365,   366,   385,   529,   439,
     370,    35,    -1,   373,    38,   375,    -1,    -1,    -1,    -1,
     248,   114,    -1,    -1,    -1,    -1,   306,   387,   121,   389,
      -1,    -1,   125,    85,   128,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   244,   200,    85,    -1,    -1,   249,
      -1,   206,    -1,   413,    -1,    -1,   416,    -1,    -1,    -1,
      -1,   421,    -1,   423,    -1,   220,   221,    -1,    -1,   224,
     430,    -1,   432,   125,   434,    -1,   128,    -1,    -1,   439,
     440,   441,    -1,    -1,   444,   365,   125,   447,   448,   128,
     370,   246,   247,   248,   454,    -1,    -1,    -1,   458,    -1,
     460,   307,    -1,   258,   128,    -1,    -1,    -1,    -1,    -1,
      -1,    17,    -1,    19,    20,    21,    22,    23,    24,    -1,
     326,    27,   350,   351,   352,    -1,   354,    -1,    -1,    35,
      36,   331,    38,   493,    -1,   495,   496,   497,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   505,    -1,   507,    -1,    -1,
      -1,   511,   512,   513,   514,    -1,    -1,   517,   518,   519,
      -1,   521,   522,    -1,    -1,   525,    -1,    -1,   528,    -1,
      -1,    -1,    -1,   379,    -1,   375,   536,    -1,   538,    85,
      -1,    -1,    -1,    -1,    -1,   413,    -1,    -1,   548,    -1,
     550,    -1,    -1,   553,    -1,   555,    -1,    -1,    -1,   354,
     400,    -1,   357,    -1,    -1,    -1,   434,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,   423,    -1,   125,
      -1,    -1,   128,   378,   430,   131,   381,    -1,    -1,   384,
      -1,    -1,    -1,   388,    -1,    -1,    -1,   517,    -1,   519,
      -1,    -1,    -1,    -1,    -1,    -1,   452,    -1,    -1,    -1,
     450,   406,    -1,    -1,    -1,    -1,   536,    -1,   538,    -1,
      -1,    -1,   417,    -1,    -1,   465,    -1,    -1,    -1,   497,
     476,    16,    17,    -1,    19,    20,    21,    22,    23,    24,
      -1,    -1,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,   522,    -1,   496,    -1,   453,    -1,
     455,   456,    -1,   509,    -1,   511,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   468,    -1,    -1,    -1,   472,   518,    -1,
     548,   521,   550,    -1,    -1,   553,    -1,   555,    -1,   484,
     485,    -1,   487,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      85,    -1,    -1,   498,    -1,    -1,    -1,    -1,    -1,   504,
      -1,    -1,    -1,   508,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
     125,    -1,    -1,   128,    -1,    -1,   131,    -1,    -1,   544,
      -1,   546,    -1,    -1,   549,     1,   551,     3,     4,     5,
       6,     7,     8,     9,    10,    11,    12,    13,    14,    15,
      16,    17,    18,    19,    20,    21,    22,    23,    24,    25,
      26,    27,    28,    29,    30,    31,    32,    33,    34,    35,
      36,    37,    38,    -1,    40,    41,    -1,    -1,    44,    45,
      46,    47,    -1,    49,    50,    51,    52,    53,    54,    55,
      56,    57,    58,    59,    60,    61,    62,    63,    -1,    65,
      66,    67,    -1,    69,    70,    71,    -1,    73,    74,    75,
      -1,    77,    78,    -1,    80,    -1,    82,    83,    84,    85,
      86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    98,    99,   100,   101,    -1,    -1,    -1,    -1,
      -1,    -1,   108,   109,   110,   111,   112,   113,   114,   115,
     116,   117,   118,    -1,   120,   121,   122,   123,   124,   125,
      -1,   127,   128,   129,   130,   131,   132,   133,     1,    -1,
       3,     4,     5,     6,     7,     8,     9,    10,    11,    12,
      13,    14,    15,    16,    17,    18,    19,    20,    21,    22,
      23,    24,    25,    26,    27,    28,    29,    30,    31,    32,
      33,    34,    35,    36,    37,    38,    -1,    40,    41,    -1,
      -1,    44,    45,    46,    47,    -1,    49,    50,    51,    52,
      53,    54,    55,    56,    57,    58,    59,    60,    61,    62,
      63,    -1,    65,    66,    67,    -1,    69,    70,    71,    -1,
      73,    74,    75,    -1,    77,    -1,    -1,    80,    -1,    82,
      83,    84,    85,    86,    87,    -1,    89,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    98,    99,   100,   101,    -1,
      -1,    -1,    -1,    -1,    -1,   108,   109,   110,   111,   112,
     113,   114,   115,   116,   117,   118,    -1,   120,   121,   122,
     123,   124,   125,    -1,   127,   128,   129,   130,   131,   132,
     133,     1,    -1,     3,     4,     5,     6,     7,     8,     9,
      10,    11,    12,    13,    14,    15,    16,    17,    18,    19,
      20,    21,    22,    23,    24,    25,    26,    27,    28,    29,
      30,    31,    32,    33,    34,    35,    36,    37,    38,    -1,
      40,    41,    -1,    -1,    44,    45,    46,    47,    -1,    49,
      50,    51,    52,    53,    54,    55,    56,    57,    58,    59,
      60,    61,    62,    63,    -1,    65,    66,    67,    -1,    69,
      70,    71,    -1,    73,    74,    75,    -1,    77,    78,    -1,
      80,    -1,    82,    83,    84,    85,    86,    87,    -1,    89,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,    99,
     100,   101,    -1,    -1,    -1,    -1,    -1,    -1,   108,   109,
     110,   111,   112,   113,   114,   115,   116,   117,   118,    -1,
     120,   121,   122,   123,   124,   125,    -1,    -1,   128,   129,
     130,   131,   132,   133,     1,    -1,     3,     4,     5,     6,
       7,     8,     9,    10,    11,    12,    13,    14,    15,    16,
      17,    18,    19,    20,    21,    22,    23,    24,    25,    26,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    -1,    40,    41,    -1,    -1,    44,    45,    46,
      47,    -1,    49,    50,    51,    52,    53,    54,    55,    56,
      57,    58,    59,    60,    61,    62,    63,    -1,    65,    66,
      67,    -1,    69,    70,    71,    -1,    73,    74,    75,    -1,
      77,    78,    -1,    80,    -1,    82,    83,    84,    85,    86,
      87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    98,    99,   100,   101,    -1,    -1,    -1,    -1,    -1,
      -1,   108,    -1,   110,   111,   112,   113,   114,   115,   116,
     117,   118,    -1,   120,   121,   122,   123,   124,   125,    -1,
     127,   128,   129,   130,   131,   132,   133,     1,    -1,     3,
       4,     5,     6,     7,    -1,     9,    10,    11,    12,    13,
      14,    15,    16,    17,    -1,    19,    20,    21,    22,    23,
      24,    25,    26,    27,    28,    29,    30,    31,    32,    33,
      34,    35,    36,    37,    38,    -1,    40,    -1,    -1,    -1,
      -1,    45,    46,    47,    -1,    -1,    50,    -1,    52,    53,
      54,    -1,    -1,    57,    -1,    -1,    60,    -1,    62,    63,
      -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,
      74,    75,    76,    -1,    78,    -1,    80,    -1,    82,    83,
      84,    85,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,   113,
     114,   115,   116,   117,   118,    -1,   120,   121,   122,   123,
     124,   125,    -1,   127,   128,   129,   130,   131,   132,   133,
       1,    -1,     3,     4,     5,     6,     7,    -1,     9,    10,
      11,    12,    13,    14,    15,    16,    17,    -1,    19,    20,
      21,    22,    23,    24,    25,    26,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,    -1,    40,
      -1,    -1,    -1,    -1,    45,    46,    47,    -1,    -1,    -1,
      -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,    60,
      -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,    70,
      71,    -1,    73,    74,    75,    76,    -1,    78,    -1,    80,
      -1,    82,    83,    84,    85,    86,    87,    -1,    89,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,   100,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,   109,    -1,
     111,   112,   113,   114,   115,   116,   117,   118,    -1,   120,
     121,   122,   123,   124,   125,    -1,   127,   128,   129,   130,
     131,   132,   133,     1,    -1,     3,     4,     5,     6,     7,
      -1,     9,    10,    11,    12,    13,    14,    15,    16,    17,
      -1,    19,    20,    21,    22,    23,    24,    25,    26,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,    -1,    40,    -1,    -1,    -1,    -1,    45,    46,    47,
      -1,    -1,    -1,    -1,    52,    53,    54,    -1,    -1,    57,
      -1,    -1,    60,    -1,    62,    63,    -1,    65,    -1,    -1,
      -1,    69,    70,    71,    -1,    73,    74,    75,    -1,    -1,
      78,    -1,    80,    -1,    82,    83,    84,    85,    86,    87,
      -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      98,    -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
     108,   109,    -1,   111,   112,   113,   114,   115,   116,   117,
     118,    -1,   120,   121,   122,   123,   124,   125,    -1,   127,
     128,   129,   130,   131,   132,   133,     1,    -1,     3,     4,
       5,     6,     7,    -1,     9,    10,    11,    12,    13,    -1,
      -1,    -1,    17,    -1,    19,    20,    -1,    -1,    -1,    -1,
      25,    26,    -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,
      -1,    36,    37,    -1,    -1,    40,    -1,    -1,    -1,    44,
      -1,    46,    47,    -1,    49,    -1,    51,    52,    53,    54,
      -1,    -1,    57,    -1,    -1,    60,    -1,    62,    63,    -1,
      65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,
      75,    -1,    77,    78,    -1,    80,    -1,    82,    83,    84,
      -1,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   108,   109,    -1,   111,   112,   113,    -1,
     115,   116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,
     125,    -1,   127,    -1,   129,   130,    -1,   132,   133,     1,
      -1,     3,     4,     5,     6,     7,    -1,     9,    10,    11,
      12,    13,    -1,    -1,    -1,    17,    -1,    19,    20,    -1,
      -1,    -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,
      -1,    -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,
      -1,    -1,    -1,    -1,    46,    47,    -1,    49,    -1,    51,
      52,    53,    54,    -1,    -1,    57,    -1,    -1,    60,    -1,
      62,    63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,
      -1,    73,    74,    75,    -1,    77,    78,    -1,    80,    -1,
      82,    83,    84,    -1,    86,    87,    -1,    89,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,   108,   109,    -1,   111,
     112,   113,    -1,   115,   116,    -1,   118,    -1,    -1,   121,
     122,   123,    -1,   125,    -1,   127,    -1,   129,   130,    -1,
     132,   133,     1,    -1,     3,     4,     5,     6,     7,    -1,
       9,    10,    11,    12,    13,    -1,    -1,    -1,    17,    -1,
      19,    20,    -1,    -1,    -1,    -1,    25,    26,    -1,    -1,
      29,    -1,    -1,    -1,    -1,    -1,    -1,    36,    37,    -1,
      -1,    40,    -1,    -1,    -1,    44,    -1,    46,    47,    -1,
      -1,    -1,    -1,    52,    53,    54,    55,    56,    57,    -1,
      -1,    60,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,
      69,    70,    71,    -1,    73,    74,    75,    -1,    -1,    78,
      -1,    80,    -1,    82,    83,    84,    -1,    86,    87,    -1,
      89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,
      -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,
     109,    -1,   111,   112,   113,    -1,   115,   116,    -1,   118,
      -1,    -1,   121,   122,   123,    -1,   125,    -1,   127,    -1,
     129,   130,    -1,   132,   133,     1,    -1,     3,     4,     5,
       6,     7,    -1,     9,    10,    11,    12,    13,    -1,    -1,
      -1,    17,    -1,    19,    20,    -1,    -1,    -1,    -1,    25,
      26,    -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,
      36,    37,    -1,    -1,    40,    -1,    -1,    -1,    44,    -1,
      46,    47,    -1,    -1,    -1,    -1,    52,    53,    54,    -1,
      -1,    57,    -1,    -1,    60,    -1,    62,    63,    -1,    65,
      -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,    75,
      -1,    77,    78,    -1,    80,    -1,    82,    83,    84,    -1,
      86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,   108,   109,   110,   111,   112,   113,    -1,   115,
     116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,   125,
      -1,   127,    -1,   129,   130,    -1,   132,   133,     1,    -1,
       3,     4,     5,     6,     7,    -1,     9,    10,    11,    12,
      13,    -1,    -1,    -1,    17,    -1,    19,    20,    -1,    -1,
      -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,    -1,
      -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,    -1,
      -1,    44,    -1,    46,    47,    -1,    -1,    -1,    -1,    52,
      53,    54,    -1,    -1,    57,    -1,    -1,    60,    -1,    62,
      63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,
      73,    74,    75,    -1,    77,    78,    -1,    80,    -1,    82,
      83,    84,    -1,    86,    87,    -1,    89,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,
     113,    -1,   115,   116,    -1,   118,    -1,    -1,   121,   122,
     123,    -1,   125,    -1,   127,    -1,   129,   130,    -1,   132,
     133,     1,    -1,     3,     4,     5,     6,     7,    -1,     9,
      10,    11,    12,    13,    -1,    -1,    -1,    17,    -1,    19,
      20,    -1,    -1,    -1,    -1,    25,    26,    -1,    -1,    29,
      -1,    -1,    -1,    -1,    -1,    -1,    36,    37,    -1,    -1,
      40,    -1,    -1,    -1,    44,    -1,    46,    47,    -1,    -1,
      -1,    -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,
      60,    -1,    62,    63,    -1,    65,    66,    -1,    -1,    69,
      70,    71,    -1,    73,    74,    75,    -1,    -1,    78,    -1,
      80,    -1,    82,    83,    84,    -1,    86,    87,    -1,    89,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,
     100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,   109,
      -1,   111,   112,   113,    -1,   115,   116,    -1,   118,    -1,
      -1,   121,   122,   123,    -1,   125,    -1,   127,    -1,   129,
     130,    -1,   132,   133,     1,    -1,     3,     4,     5,     6,
       7,    -1,     9,    10,    11,    12,    13,    -1,    -1,    -1,
      17,    -1,    19,    20,    -1,    -1,    -1,    -1,    25,    26,
      -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,    36,
      37,    -1,    -1,    40,    -1,    -1,    -1,    -1,    -1,    46,
      47,    -1,    -1,    -1,    -1,    52,    53,    54,    -1,    -1,
      57,    -1,    -1,    60,    -1,    62,    63,    -1,    65,    -1,
      -1,    -1,    69,    70,    71,    -1,    73,    74,    75,    -1,
      -1,    78,    79,    80,    81,    82,    83,    84,    -1,    86,
      87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,   108,   109,    -1,   111,   112,   113,    -1,   115,   116,
      -1,   118,    -1,    -1,   121,   122,   123,    -1,   125,    -1,
     127,    -1,   129,   130,    -1,   132,   133,     1,    -1,     3,
       4,     5,     6,     7,    -1,     9,    10,    11,    12,    13,
      -1,    -1,    -1,    17,    -1,    19,    20,    -1,    -1,    -1,
      -1,    25,    26,    -1,    -1,    29,    -1,    -1,    -1,    -1,
      -1,    -1,    36,    37,    -1,    -1,    40,    -1,    -1,    -1,
      44,    -1,    46,    47,    -1,    -1,    -1,    -1,    52,    53,
      54,    -1,    -1,    57,    -1,    -1,    60,    -1,    62,    63,
      -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,
      74,    75,    -1,    77,    78,    -1,    80,    -1,    82,    83,
      84,    -1,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,   113,
      -1,   115,   116,    -1,   118,    -1,    -1,   121,   122,   123,
      -1,   125,    -1,   127,    -1,   129,   130,    -1,   132,   133,
       1,    -1,     3,     4,     5,     6,     7,    -1,     9,    10,
      11,    12,    13,    -1,    -1,    -1,    17,    -1,    19,    20,
      -1,    -1,    -1,    -1,    25,    26,    -1,    -1,    29,    -1,
      -1,    -1,    -1,    -1,    -1,    36,    37,    -1,    -1,    40,
      -1,    -1,    -1,    44,    -1,    46,    47,    -1,    -1,    -1,
      51,    52,    53,    54,    -1,    -1,    57,    -1,    -1,    60,
      -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,    70,
      71,    -1,    73,    74,    75,    -1,    -1,    78,    -1,    80,
      -1,    82,    83,    84,    -1,    86,    87,    -1,    89,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,   100,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,   109,    -1,
     111,   112,   113,    -1,   115,   116,    -1,   118,    -1,    -1,
     121,   122,   123,    -1,   125,    -1,   127,    -1,   129,   130,
      -1,   132,   133,     1,    -1,     3,     4,     5,     6,     7,
      -1,     9,    10,    11,    12,    13,    -1,    -1,    -1,    17,
      -1,    19,    20,    -1,    -1,    -1,    -1,    25,    26,    -1,
      -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,    36,    37,
      -1,    -1,    40,    -1,    -1,    -1,    44,    -1,    46,    47,
      -1,    -1,    50,    -1,    52,    53,    54,    -1,    -1,    57,
      -1,    -1,    60,    -1,    62,    63,    -1,    65,    -1,    -1,
      -1,    69,    70,    71,    -1,    73,    74,    75,    -1,    -1,
      78,    -1,    80,    -1,    82,    83,    84,    -1,    86,    87,
      -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      98,    -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
     108,   109,    -1,   111,   112,   113,    -1,   115,   116,    -1,
     118,    -1,    -1,   121,   122,   123,    -1,   125,    -1,   127,
      -1,   129,   130,    -1,   132,   133,     1,    -1,     3,     4,
       5,     6,     7,    -1,     9,    10,    11,    12,    13,    -1,
      -1,    -1,    17,    -1,    19,    20,    -1,    -1,    -1,    -1,
      25,    26,    -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,
      -1,    36,    37,    -1,    -1,    40,    -1,    -1,    -1,    44,
      -1,    46,    47,    -1,    -1,    -1,    -1,    52,    53,    54,
      -1,    -1,    57,    -1,    -1,    60,    -1,    62,    63,    -1,
      65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,
      75,    -1,    77,    78,    -1,    80,    -1,    82,    83,    84,
      -1,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   108,   109,    -1,   111,   112,   113,    -1,
     115,   116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,
     125,    -1,   127,    -1,   129,   130,    -1,   132,   133,     1,
      -1,     3,     4,     5,     6,     7,    -1,     9,    10,    11,
      12,    13,    -1,    -1,    -1,    17,    -1,    19,    20,    -1,
      -1,    -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,
      -1,    -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,
      -1,    -1,    44,    -1,    46,    47,    -1,    -1,    -1,    -1,
      52,    53,    54,    -1,    -1,    57,    -1,    -1,    60,    -1,
      62,    63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,
      -1,    73,    74,    75,    -1,    77,    78,    -1,    80,    -1,
      82,    83,    84,    -1,    86,    87,    -1,    89,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,   108,   109,    -1,   111,
     112,   113,    -1,   115,   116,    -1,   118,    -1,    -1,   121,
     122,   123,    -1,   125,    -1,   127,    -1,   129,   130,    -1,
     132,   133,     1,    -1,     3,     4,     5,     6,     7,    -1,
       9,    10,    11,    12,    13,    -1,    -1,    -1,    17,    -1,
      19,    20,    -1,    -1,    -1,    -1,    25,    26,    -1,    -1,
      29,    -1,    -1,    -1,    -1,    -1,    -1,    36,    37,    -1,
      -1,    40,    -1,    -1,    -1,    44,    -1,    46,    47,    -1,
      -1,    -1,    -1,    52,    53,    54,    -1,    56,    57,    -1,
      -1,    60,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,
      69,    70,    71,    -1,    73,    74,    75,    -1,    -1,    78,
      -1,    80,    -1,    82,    83,    84,    -1,    86,    87,    -1,
      89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,
      -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,
     109,    -1,   111,   112,   113,    -1,   115,   116,    -1,   118,
      -1,    -1,   121,   122,   123,    -1,   125,    -1,   127,    -1,
     129,   130,    -1,   132,   133,     1,    -1,     3,     4,     5,
       6,     7,    -1,     9,    10,    11,    12,    13,    -1,    -1,
      -1,    17,    -1,    19,    20,    -1,    -1,    -1,    -1,    25,
      26,    -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,
      36,    37,    -1,    -1,    40,    -1,    -1,    -1,    44,    -1,
      46,    47,    -1,    -1,    -1,    -1,    52,    53,    54,    -1,
      -1,    57,    -1,    -1,    60,    -1,    62,    63,    -1,    65,
      -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,    75,
      -1,    77,    78,    -1,    80,    -1,    82,    83,    84,    -1,
      86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,   108,   109,    -1,   111,   112,   113,    -1,   115,
     116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,   125,
      -1,   127,    -1,   129,   130,    -1,   132,   133,     1,    -1,
       3,     4,     5,     6,     7,    -1,     9,    10,    11,    12,
      13,    -1,    -1,    -1,    17,    -1,    19,    20,    -1,    -1,
      -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,    -1,
      -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,    -1,
      -1,    44,    -1,    46,    47,    -1,    -1,    -1,    -1,    52,
      53,    54,    -1,    -1,    57,    -1,    -1,    60,    -1,    62,
      63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,
      73,    74,    75,    -1,    77,    78,    -1,    80,    -1,    82,
      83,    84,    -1,    86,    87,    -1,    89,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,
     113,    -1,   115,   116,    -1,   118,    -1,    -1,   121,   122,
     123,    -1,   125,    -1,   127,    -1,   129,   130,    -1,   132,
     133,     1,    -1,     3,     4,     5,     6,     7,    -1,     9,
      10,    11,    12,    13,    -1,    -1,    -1,    17,    -1,    19,
      20,    -1,    -1,    -1,    -1,    25,    26,    -1,    -1,    29,
      -1,    -1,    -1,    -1,    -1,    -1,    36,    37,    -1,    -1,
      40,    -1,    -1,    -1,    44,    -1,    46,    47,    -1,    -1,
      -1,    -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,
      60,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,
      70,    71,    -1,    73,    74,    75,    -1,    77,    78,    -1,
      80,    -1,    82,    83,    84,    -1,    86,    87,    -1,    89,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,
     100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,   109,
      -1,   111,   112,   113,    -1,   115,   116,    -1,   118,    -1,
      -1,   121,   122,   123,    -1,   125,    -1,   127,    -1,   129,
     130,    -1,   132,   133,     1,    -1,     3,     4,     5,     6,
       7,    -1,     9,    10,    11,    12,    13,    -1,    -1,    -1,
      17,    -1,    19,    20,    -1,    -1,    -1,    -1,    25,    26,
      -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,    36,
      37,    -1,    -1,    40,    -1,    -1,    -1,    44,    -1,    46,
      47,    -1,    -1,    -1,    51,    52,    53,    54,    -1,    -1,
      57,    -1,    -1,    60,    -1,    62,    63,    -1,    65,    -1,
      -1,    -1,    69,    70,    71,    -1,    73,    74,    75,    -1,
      -1,    78,    -1,    80,    -1,    82,    83,    84,    -1,    86,
      87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,   108,   109,    -1,   111,   112,   113,    -1,   115,   116,
      -1,   118,    -1,    -1,   121,   122,   123,    -1,   125,    -1,
     127,    -1,   129,   130,    -1,   132,   133,     1,    -1,     3,
       4,     5,     6,     7,    -1,     9,    10,    11,    12,    13,
      -1,    -1,    -1,    17,    -1,    19,    20,    -1,    -1,    -1,
      -1,    25,    26,    -1,    -1,    29,    -1,    -1,    -1,    -1,
      -1,    -1,    36,    37,    -1,    -1,    40,    -1,    -1,    -1,
      -1,    -1,    46,    47,    -1,    -1,    -1,    -1,    52,    53,
      54,    -1,    -1,    57,    -1,    -1,    60,    -1,    62,    63,
      -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,
      74,    75,    76,    -1,    78,    79,    80,    -1,    82,    83,
      84,    -1,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,   113,
      -1,   115,   116,    -1,   118,    -1,    -1,   121,   122,   123,
      -1,   125,    -1,   127,    -1,   129,   130,    -1,   132,   133,
       1,    -1,     3,     4,     5,     6,     7,    -1,     9,    10,
      11,    12,    13,    -1,    -1,    -1,    17,    -1,    19,    20,
      -1,    -1,    -1,    -1,    25,    26,    -1,    -1,    29,    -1,
      -1,    -1,    -1,    -1,    -1,    36,    37,    -1,    -1,    40,
      -1,    -1,    -1,    44,    -1,    46,    47,    -1,    -1,    -1,
      -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,    60,
      -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,    70,
      71,    -1,    73,    74,    75,    -1,    77,    78,    -1,    80,
      -1,    82,    83,    84,    -1,    86,    87,    -1,    89,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,   100,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,   109,    -1,
     111,   112,   113,    -1,   115,   116,    -1,   118,    -1,    -1,
     121,   122,   123,    -1,   125,    -1,   127,    -1,   129,   130,
      -1,   132,   133,     1,    -1,     3,     4,     5,     6,     7,
      -1,     9,    10,    11,    12,    13,    -1,    -1,    -1,    17,
      -1,    19,    20,    -1,    -1,    -1,    -1,    25,    26,    -1,
      -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,    36,    37,
      -1,    -1,    40,    -1,    -1,    -1,    44,    -1,    46,    47,
      -1,    -1,    -1,    -1,    52,    53,    54,    -1,    -1,    57,
      -1,    -1,    60,    -1,    62,    63,    -1,    65,    -1,    -1,
      -1,    69,    70,    71,    -1,    73,    74,    75,    -1,    77,
      78,    -1,    80,    -1,    82,    83,    84,    -1,    86,    87,
      -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      98,    -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
     108,   109,    -1,   111,   112,   113,    -1,   115,   116,    -1,
     118,    -1,    -1,   121,   122,   123,    -1,   125,    -1,   127,
      -1,   129,   130,    -1,   132,   133,     1,    -1,     3,     4,
       5,     6,     7,    -1,     9,    10,    11,    12,    13,    -1,
      -1,    -1,    17,    -1,    19,    20,    -1,    -1,    -1,    -1,
      25,    26,    -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,
      -1,    36,    37,    -1,    -1,    40,    -1,    -1,    -1,    44,
      -1,    46,    47,    -1,    -1,    -1,    -1,    52,    53,    54,
      -1,    -1,    57,    -1,    -1,    60,    -1,    62,    63,    -1,
      65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,
      75,    -1,    77,    78,    -1,    80,    -1,    82,    83,    84,
      -1,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   108,   109,    -1,   111,   112,   113,    -1,
     115,   116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,
     125,    -1,   127,    -1,   129,   130,    -1,   132,   133,     1,
      -1,     3,     4,     5,     6,     7,    -1,     9,    10,    11,
      12,    13,    -1,    -1,    -1,    17,    -1,    19,    20,    -1,
      -1,    -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,
      -1,    -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,
      -1,    -1,    44,    -1,    46,    47,    -1,    -1,    -1,    -1,
      52,    53,    54,    -1,    -1,    57,    -1,    -1,    60,    -1,
      62,    63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,
      -1,    73,    74,    75,    -1,    77,    78,    -1,    80,    -1,
      82,    83,    84,    -1,    86,    87,    -1,    89,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,   108,   109,    -1,   111,
     112,   113,    -1,   115,   116,    -1,   118,    -1,    -1,   121,
     122,   123,    -1,   125,    -1,   127,    -1,   129,   130,    -1,
     132,   133,     1,    -1,     3,     4,     5,     6,     7,    -1,
       9,    10,    11,    12,    13,    -1,    -1,    -1,    17,    -1,
      19,    20,    -1,    -1,    -1,    -1,    25,    26,    -1,    -1,
      29,    -1,    -1,    -1,    -1,    -1,    -1,    36,    37,    -1,
      -1,    40,    -1,    -1,    -1,    44,    -1,    46,    47,    -1,
      -1,    -1,    -1,    52,    53,    54,    -1,    -1,    57,    -1,
      -1,    60,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,
      69,    70,    71,    -1,    73,    74,    75,    -1,    77,    78,
      -1,    80,    -1,    82,    83,    84,    -1,    86,    87,    -1,
      89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,
      -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,
     109,    -1,   111,   112,   113,    -1,   115,   116,    -1,   118,
      -1,    -1,   121,   122,   123,    -1,   125,    -1,   127,    -1,
     129,   130,    -1,   132,   133,     1,    -1,     3,     4,     5,
       6,     7,    -1,     9,    10,    11,    12,    13,    -1,    -1,
      -1,    17,    -1,    19,    20,    -1,    -1,    -1,    -1,    25,
      26,    -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,
      36,    37,    -1,    -1,    40,    -1,    -1,    -1,    44,    -1,
      46,    47,    -1,    -1,    -1,    -1,    52,    53,    54,    -1,
      -1,    57,    -1,    -1,    60,    -1,    62,    63,    -1,    65,
      -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,    75,
      -1,    77,    78,    -1,    80,    -1,    82,    83,    84,    -1,
      86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,   108,   109,    -1,   111,   112,   113,    -1,   115,
     116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,   125,
      -1,   127,    -1,   129,   130,    -1,   132,   133,     1,    -1,
       3,     4,     5,     6,     7,    -1,     9,    10,    11,    12,
      13,    -1,    -1,    -1,    17,    -1,    19,    20,    -1,    -1,
      -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,    -1,
      -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,    -1,
      -1,    44,    -1,    46,    47,    -1,    -1,    -1,    -1,    52,
      53,    54,    -1,    -1,    57,    -1,    -1,    60,    -1,    62,
      63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,
      73,    74,    75,    -1,    77,    78,    -1,    80,    -1,    82,
      83,    84,    -1,    86,    87,    -1,    89,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,
     113,    -1,   115,   116,    -1,   118,    -1,    -1,   121,   122,
     123,    -1,   125,    -1,   127,    -1,   129,   130,    -1,   132,
     133,     1,    -1,     3,     4,     5,     6,     7,    -1,     9,
      10,    11,    12,    13,    -1,    -1,    -1,    17,    -1,    19,
      20,    -1,    -1,    -1,    -1,    25,    26,    -1,    -1,    29,
      -1,    -1,    -1,    -1,    -1,    -1,    36,    37,    -1,    -1,
      40,    -1,    -1,    -1,    44,    -1,    46,    47,    -1,    -1,
      -1,    -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,
      60,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,
      70,    71,    -1,    73,    74,    75,    -1,    77,    78,    -1,
      80,    -1,    82,    83,    84,    -1,    86,    87,    -1,    89,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,
     100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,   109,
      -1,   111,   112,   113,    -1,   115,   116,    -1,   118,    -1,
      -1,   121,   122,   123,    -1,   125,    -1,   127,    -1,   129,
     130,    -1,   132,   133,     1,    -1,     3,     4,     5,     6,
       7,    -1,     9,    10,    11,    12,    13,    -1,    -1,    -1,
      17,    -1,    19,    20,    -1,    -1,    -1,    -1,    25,    26,
      -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,    36,
      37,    -1,    -1,    40,    -1,    -1,    -1,    44,    -1,    46,
      47,    -1,    -1,    -1,    -1,    52,    53,    54,    -1,    -1,
      57,    -1,    -1,    60,    -1,    62,    63,    -1,    65,    -1,
      -1,    -1,    69,    70,    71,    -1,    73,    74,    75,    -1,
      77,    78,    -1,    80,    -1,    82,    83,    84,    -1,    86,
      87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,   108,   109,    -1,   111,   112,   113,    -1,   115,   116,
      -1,   118,    -1,    -1,   121,   122,   123,    -1,   125,    -1,
     127,    -1,   129,   130,    -1,   132,   133,     1,    -1,     3,
       4,     5,     6,     7,    -1,     9,    10,    11,    12,    13,
      -1,    -1,    -1,    17,    -1,    19,    20,    -1,    -1,    -1,
      -1,    25,    26,    -1,    -1,    29,    -1,    -1,    -1,    -1,
      -1,    -1,    36,    37,    -1,    -1,    40,    -1,    -1,    -1,
      44,    -1,    46,    47,    -1,    -1,    -1,    -1,    52,    53,
      54,    -1,    -1,    57,    -1,    -1,    60,    -1,    62,    63,
      -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,
      74,    75,    -1,    77,    78,    -1,    80,    -1,    82,    83,
      84,    -1,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,   113,
      -1,   115,   116,    -1,   118,    -1,    -1,   121,   122,   123,
      -1,   125,    -1,   127,    -1,   129,   130,    -1,   132,   133,
       1,    -1,     3,     4,     5,     6,     7,    -1,     9,    10,
      11,    12,    13,    -1,    -1,    -1,    17,    -1,    19,    20,
      -1,    -1,    -1,    -1,    25,    26,    -1,    -1,    29,    -1,
      -1,    -1,    -1,    -1,    -1,    36,    37,    -1,    -1,    40,
      -1,    -1,    -1,    44,    -1,    46,    47,    -1,    -1,    -1,
      -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,    60,
      -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,    70,
      71,    -1,    73,    74,    75,    -1,    77,    78,    -1,    80,
      -1,    82,    83,    84,    -1,    86,    87,    -1,    89,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,   100,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,   109,    -1,
     111,   112,   113,    -1,   115,   116,    -1,   118,    -1,    -1,
     121,   122,   123,    -1,   125,    -1,   127,    -1,   129,   130,
      -1,   132,   133,     1,    -1,     3,     4,     5,     6,     7,
      -1,     9,    10,    11,    12,    13,    -1,    -1,    -1,    17,
      -1,    19,    20,    -1,    -1,    -1,    -1,    25,    26,    -1,
      -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,    36,    37,
      -1,    -1,    40,    -1,    -1,    -1,    -1,    -1,    46,    47,
      -1,    -1,    -1,    -1,    52,    53,    54,    -1,    -1,    57,
      -1,    -1,    60,    -1,    62,    63,    -1,    65,    -1,    -1,
      -1,    69,    70,    71,    -1,    73,    74,    75,    -1,    77,
      78,    -1,    80,    -1,    82,    83,    84,    -1,    86,    87,
      -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      98,    -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
     108,   109,   110,   111,   112,   113,    -1,   115,   116,    -1,
     118,    -1,    -1,   121,   122,   123,    -1,   125,    -1,   127,
      -1,   129,   130,    -1,   132,   133,     1,    -1,     3,     4,
       5,     6,     7,    -1,     9,    10,    11,    12,    13,    -1,
      -1,    -1,    17,    -1,    19,    20,    -1,    -1,    -1,    -1,
      25,    26,    -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,
      -1,    36,    37,    -1,    -1,    40,    -1,    -1,    -1,    44,
      -1,    46,    47,    -1,    -1,    -1,    -1,    52,    53,    54,
      -1,    -1,    57,    -1,    -1,    60,    -1,    62,    63,    -1,
      65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,
      75,    -1,    77,    78,    -1,    80,    -1,    82,    83,    84,
      -1,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   108,   109,    -1,   111,   112,   113,    -1,
     115,   116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,
     125,    -1,   127,    -1,   129,   130,    -1,   132,   133,     1,
      -1,     3,     4,     5,     6,     7,    -1,     9,    10,    11,
      12,    13,    -1,    -1,    -1,    17,    -1,    19,    20,    -1,
      -1,    -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,
      -1,    -1,    -1,    -1,    36,    37,    -1,    -1,    40,    41,
      -1,    -1,    -1,    -1,    46,    47,    -1,    -1,    -1,    -1,
      52,    53,    54,    -1,    -1,    57,    -1,    -1,    60,    -1,
      62,    63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,
      -1,    73,    74,    75,    -1,    -1,    78,    -1,    80,    -1,
      82,    83,    84,    -1,    86,    87,    -1,    89,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,   108,   109,    -1,   111,
     112,   113,    -1,   115,   116,    -1,   118,    -1,    -1,   121,
     122,   123,    -1,   125,    -1,   127,    -1,   129,   130,    -1,
     132,   133,     1,    -1,     3,     4,     5,     6,     7,    -1,
       9,    10,    11,    12,    13,    -1,    -1,    -1,    17,    -1,
      19,    20,    -1,    -1,    -1,    -1,    25,    26,    -1,    -1,
      29,    -1,    -1,    -1,    -1,    -1,    -1,    36,    37,    -1,
      -1,    40,    -1,    -1,    -1,    -1,    -1,    46,    47,    -1,
      -1,    -1,    -1,    52,    53,    54,    -1,    -1,    57,    -1,
      -1,    60,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,
      69,    70,    71,    -1,    73,    74,    75,    76,    -1,    78,
      -1,    80,    -1,    82,    83,    84,    -1,    86,    87,    -1,
      89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,
      -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,
     109,    -1,   111,   112,   113,    -1,   115,   116,    -1,   118,
      -1,    -1,   121,   122,   123,    -1,   125,    -1,   127,    -1,
     129,   130,    -1,   132,   133,     1,    -1,     3,     4,     5,
       6,     7,    -1,     9,    10,    11,    12,    13,    -1,    -1,
      -1,    17,    18,    19,    20,    -1,    -1,    -1,    -1,    25,
      26,    -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,
      36,    37,    -1,    -1,    40,    -1,    -1,    -1,    -1,    -1,
      46,    47,    -1,    -1,    -1,    -1,    52,    53,    54,    -1,
      -1,    57,    -1,    -1,    60,    -1,    62,    63,    -1,    65,
      -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,    75,
      -1,    -1,    78,    -1,    80,    -1,    82,    83,    84,    -1,
      86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,   108,   109,    -1,   111,   112,   113,    -1,   115,
     116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,   125,
      -1,   127,    -1,   129,   130,    -1,   132,   133,     1,    -1,
       3,     4,     5,     6,     7,    -1,     9,    10,    11,    12,
      13,    -1,    -1,    -1,    17,    -1,    19,    20,    -1,    -1,
      -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,    -1,
      -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,    -1,
      -1,    44,    -1,    46,    47,    -1,    -1,    -1,    -1,    52,
      53,    54,    -1,    -1,    57,    -1,    -1,    60,    -1,    62,
      63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,
      73,    74,    75,    -1,    -1,    78,    -1,    80,    -1,    82,
      83,    84,    -1,    86,    87,    -1,    89,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,
     113,    -1,   115,   116,    -1,   118,    -1,    -1,   121,   122,
     123,    -1,   125,    -1,   127,    -1,   129,   130,    -1,   132,
     133,     1,    -1,     3,     4,     5,     6,     7,    -1,     9,
      10,    11,    12,    13,    -1,    -1,    -1,    17,    18,    19,
      20,    -1,    -1,    -1,    -1,    25,    26,    -1,    -1,    29,
      -1,    -1,    -1,    -1,    -1,    -1,    36,    37,    -1,    -1,
      40,    -1,    -1,    -1,    -1,    -1,    46,    47,    -1,    -1,
      -1,    -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,
      60,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,
      70,    71,    -1,    73,    74,    75,    -1,    -1,    78,    -1,
      80,    -1,    82,    83,    84,    -1,    86,    87,    -1,    89,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,
     100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,   109,
      -1,   111,   112,   113,    -1,   115,   116,    -1,   118,    -1,
      -1,   121,   122,   123,    -1,   125,    -1,   127,    -1,   129,
     130,    -1,   132,   133,     1,    -1,     3,     4,     5,     6,
       7,    -1,     9,    10,    11,    12,    13,    -1,    -1,    -1,
      17,    18,    19,    20,    -1,    -1,    -1,    -1,    25,    26,
      -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,    36,
      37,    -1,    -1,    40,    -1,    -1,    -1,    -1,    -1,    46,
      47,    -1,    -1,    -1,    -1,    52,    53,    54,    -1,    -1,
      57,    -1,    -1,    60,    -1,    62,    63,    -1,    65,    -1,
      -1,    -1,    69,    70,    71,    -1,    73,    74,    75,    -1,
      -1,    78,    -1,    80,    -1,    82,    83,    84,    -1,    86,
      87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,   108,   109,    -1,   111,   112,   113,    -1,   115,   116,
      -1,   118,    -1,    -1,   121,   122,   123,    -1,   125,    -1,
     127,    -1,   129,   130,    -1,   132,   133,     1,    -1,     3,
       4,     5,     6,     7,     8,     9,    10,    11,    12,    13,
      -1,    -1,    -1,    17,    -1,    19,    20,    -1,    -1,    -1,
      -1,    25,    26,    -1,    -1,    29,    -1,    -1,    -1,    -1,
      -1,    -1,    36,    37,    -1,    -1,    40,    -1,    -1,    -1,
      -1,    -1,    46,    47,    -1,    -1,    -1,    -1,    52,    53,
      54,    -1,    -1,    57,    -1,    -1,    60,    -1,    62,    63,
      -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,
      74,    75,    -1,    -1,    78,    -1,    80,    -1,    82,    83,
      84,    -1,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,   113,
      -1,   115,   116,    -1,   118,    -1,    -1,   121,   122,   123,
      -1,   125,    -1,   127,    -1,   129,   130,    -1,   132,   133,
       1,    -1,     3,     4,     5,     6,     7,    -1,     9,    10,
      11,    12,    13,    -1,    -1,    -1,    17,    -1,    19,    20,
      -1,    -1,    -1,    -1,    25,    26,    -1,    -1,    29,    -1,
      -1,    -1,    -1,    -1,    -1,    36,    37,    -1,    -1,    40,
      -1,    -1,    -1,    -1,    -1,    46,    47,    -1,    -1,    -1,
      -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,    60,
      -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,    70,
      71,    -1,    73,    74,    75,    -1,    -1,    78,    79,    80,
      -1,    82,    83,    84,    -1,    86,    87,    -1,    89,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,   100,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,   109,    -1,
     111,   112,   113,    -1,   115,   116,    -1,   118,    -1,    -1,
     121,   122,   123,    -1,   125,    -1,   127,    -1,   129,   130,
      -1,   132,   133,     1,    -1,     3,     4,     5,     6,     7,
      -1,     9,    10,    11,    12,    13,    -1,    -1,    -1,    17,
      -1,    19,    20,    -1,    -1,    -1,    -1,    25,    26,    -1,
      -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,    36,    37,
      -1,    -1,    40,    -1,    -1,    -1,    -1,    -1,    46,    47,
      -1,    -1,    -1,    -1,    52,    53,    54,    -1,    -1,    57,
      -1,    -1,    60,    -1,    62,    63,    -1,    65,    -1,    -1,
      -1,    69,    70,    71,    -1,    73,    74,    75,    -1,    77,
      78,    -1,    80,    -1,    82,    83,    84,    -1,    86,    87,
      -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      98,    -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
     108,   109,    -1,   111,   112,   113,    -1,   115,   116,    -1,
     118,    -1,    -1,   121,   122,   123,    -1,   125,    -1,   127,
      -1,   129,   130,    -1,   132,   133,     1,    -1,     3,     4,
       5,     6,     7,    -1,     9,    10,    11,    12,    13,    -1,
      -1,    -1,    17,    -1,    19,    20,    -1,    -1,    -1,    -1,
      25,    26,    -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,
      -1,    36,    37,    -1,    -1,    40,    -1,    -1,    -1,    -1,
      -1,    46,    47,    -1,    -1,    -1,    -1,    52,    53,    54,
      -1,    -1,    57,    -1,    -1,    60,    -1,    62,    63,    -1,
      65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,
      75,    -1,    77,    78,    -1,    80,    -1,    82,    83,    84,
      -1,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   108,   109,    -1,   111,   112,   113,    -1,
     115,   116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,
     125,    -1,   127,    -1,   129,   130,    -1,   132,   133,     1,
      -1,     3,     4,     5,     6,     7,    -1,     9,    10,    11,
      12,    13,    -1,    -1,    -1,    17,    -1,    19,    20,    -1,
      -1,    -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,
      -1,    -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,
      -1,    -1,    -1,    -1,    46,    47,    -1,    -1,    -1,    51,
      52,    53,    54,    -1,    -1,    57,    -1,    -1,    60,    -1,
      62,    63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,
      -1,    73,    74,    75,    -1,    -1,    78,    -1,    80,    -1,
      82,    83,    84,    -1,    86,    87,    -1,    89,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,   108,   109,    -1,   111,
     112,   113,    -1,   115,   116,    -1,   118,    -1,    -1,   121,
     122,   123,    -1,   125,    -1,   127,    -1,   129,   130,    -1,
     132,   133,     1,    -1,     3,     4,     5,     6,     7,    -1,
       9,    10,    11,    12,    13,    -1,    -1,    -1,    17,    -1,
      19,    20,    -1,    -1,    -1,    -1,    25,    26,    -1,    -1,
      29,    -1,    -1,    -1,    -1,    -1,    -1,    36,    37,    -1,
      -1,    40,    -1,    -1,    -1,    -1,    -1,    46,    47,    -1,
      -1,    -1,    -1,    52,    53,    54,    -1,    -1,    57,    -1,
      -1,    60,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,
      69,    70,    71,    -1,    73,    74,    75,    76,    -1,    78,
      -1,    80,    -1,    82,    83,    84,    -1,    86,    87,    -1,
      89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,
      -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,
     109,    -1,   111,   112,   113,    -1,   115,   116,    -1,   118,
      -1,    -1,   121,   122,   123,    -1,   125,    -1,   127,    -1,
     129,   130,    -1,   132,   133,     1,    -1,     3,     4,     5,
       6,     7,    -1,     9,    10,    11,    12,    13,    -1,    -1,
      -1,    17,    18,    19,    20,    -1,    -1,    -1,    -1,    25,
      26,    -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,
      36,    37,    -1,    -1,    40,    -1,    -1,    -1,    -1,    -1,
      46,    47,    -1,    -1,    -1,    -1,    52,    53,    54,    -1,
      -1,    57,    -1,    -1,    60,    -1,    62,    63,    -1,    65,
      -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,    75,
      -1,    -1,    78,    -1,    80,    -1,    82,    83,    84,    -1,
      86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,   108,   109,    -1,   111,   112,   113,    -1,   115,
     116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,   125,
      -1,   127,    -1,   129,   130,    -1,   132,   133,     1,    -1,
       3,     4,     5,     6,     7,    -1,     9,    10,    11,    12,
      13,    -1,    -1,    -1,    17,    -1,    19,    20,    -1,    -1,
      -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,    -1,
      -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,    -1,
      -1,    -1,    -1,    46,    47,    -1,    -1,    -1,    -1,    52,
      53,    54,    -1,    -1,    57,    -1,    -1,    60,    -1,    62,
      63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,
      73,    74,    75,    -1,    -1,    78,    -1,    80,    -1,    82,
      83,    84,    -1,    86,    87,    -1,    89,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,
     113,    -1,   115,   116,    -1,   118,   119,    -1,   121,   122,
     123,    -1,   125,    -1,   127,    -1,   129,   130,    -1,   132,
     133,     1,    -1,     3,     4,     5,     6,     7,    -1,     9,
      10,    11,    12,    13,    -1,    -1,    -1,    17,    -1,    19,
      20,    -1,    -1,    -1,    -1,    25,    26,    -1,    -1,    29,
      -1,    -1,    -1,    -1,    -1,    -1,    36,    37,    -1,    -1,
      40,    -1,    -1,    -1,    -1,    -1,    46,    47,    -1,    -1,
      -1,    -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,
      60,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,
      70,    71,    -1,    73,    74,    75,    -1,    -1,    78,    -1,
      80,    -1,    82,    83,    84,    -1,    86,    87,    -1,    89,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,
     100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,   109,
     110,   111,   112,   113,    -1,   115,   116,    -1,   118,    -1,
      -1,   121,   122,   123,    -1,   125,    -1,   127,    -1,   129,
     130,    -1,   132,   133,     1,    -1,     3,     4,     5,     6,
       7,    -1,     9,    10,    11,    12,    13,    -1,    -1,    -1,
      17,    -1,    19,    20,    -1,    -1,    -1,    -1,    25,    26,
      -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,    36,
      37,    -1,    -1,    40,    -1,    -1,    -1,    -1,    -1,    46,
      47,    -1,    -1,    -1,    -1,    52,    53,    54,    -1,    -1,
      57,    -1,    -1,    60,    -1,    62,    63,    -1,    65,    -1,
      -1,    -1,    69,    70,    71,    -1,    73,    74,    75,    -1,
      -1,    78,    -1,    80,    -1,    82,    83,    84,    -1,    86,
      87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,   108,   109,   110,   111,   112,   113,    -1,   115,   116,
      -1,   118,    -1,    -1,   121,   122,   123,    -1,   125,    -1,
     127,    -1,   129,   130,    -1,   132,   133,     1,    -1,     3,
       4,     5,     6,     7,    -1,     9,    10,    11,    12,    13,
      -1,    -1,    -1,    17,    -1,    19,    20,    -1,    -1,    -1,
      -1,    25,    26,    -1,    -1,    29,    -1,    -1,    -1,    -1,
      -1,    -1,    36,    37,    -1,    -1,    40,    -1,    -1,    -1,
      -1,    -1,    46,    47,    -1,    -1,    -1,    -1,    52,    53,
      54,    -1,    -1,    57,    -1,    -1,    60,    -1,    62,    63,
      -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,
      74,    75,    -1,    -1,    78,    -1,    80,    -1,    82,    83,
      84,    -1,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,   113,
      -1,   115,   116,    -1,   118,    -1,    -1,   121,   122,   123,
      -1,   125,    -1,   127,    -1,   129,   130,    -1,   132,   133,
       1,    -1,     3,     4,     5,     6,     7,    -1,     9,    10,
      11,    12,    13,    -1,    -1,    -1,    17,    -1,    19,    20,
      -1,    -1,    -1,    -1,    25,    26,    -1,    -1,    29,    -1,
      -1,    -1,    -1,    -1,    -1,    36,    37,    -1,    -1,    40,
      -1,    -1,    -1,    -1,    -1,    46,    47,    -1,    -1,    -1,
      -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,    60,
      -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,    70,
      71,    -1,    73,    74,    75,    -1,    -1,    78,    -1,    80,
      -1,    82,    83,    84,    -1,    86,    87,    -1,    89,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,   100,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,   109,    -1,
     111,   112,   113,    -1,   115,   116,    -1,   118,    -1,    -1,
     121,   122,   123,    -1,   125,    -1,   127,    -1,   129,   130,
      -1,   132,   133,     1,    -1,     3,     4,     5,     6,     7,
      -1,     9,    10,    11,    12,    13,    -1,    -1,    -1,    17,
      -1,    19,    20,    -1,    -1,    -1,    -1,    25,    26,    -1,
      -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,    36,    37,
      -1,    -1,    40,    -1,    -1,    -1,    -1,    -1,    46,    47,
      -1,    -1,    -1,    -1,    52,    53,    54,    -1,    -1,    57,
      -1,    -1,    60,    -1,    62,    63,    -1,    65,    -1,    -1,
      -1,    69,    70,    71,    -1,    73,    74,    75,    -1,    -1,
      78,    -1,    80,    -1,    82,    83,    84,    -1,    86,    87,
      -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      98,    -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
     108,   109,    -1,   111,   112,   113,    -1,   115,   116,    -1,
     118,    -1,    -1,   121,   122,   123,    -1,   125,    -1,   127,
      -1,   129,   130,    -1,   132,   133,     1,    -1,     3,     4,
       5,     6,     7,    -1,     9,    10,    11,    12,    13,    -1,
      -1,    -1,    17,    -1,    19,    20,    -1,    -1,    -1,    -1,
      25,    26,    -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,
      -1,    36,    37,    -1,    -1,    40,    -1,    -1,    -1,    -1,
      -1,    46,    47,    -1,    -1,    -1,    -1,    52,    53,    54,
      -1,    -1,    57,    -1,    -1,    60,    -1,    62,    63,    -1,
      65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,
      75,    -1,    -1,    78,    -1,    80,    -1,    82,    83,    84,
      -1,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   108,   109,    -1,   111,   112,   113,    -1,
     115,   116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,
     125,    -1,   127,    -1,   129,   130,    -1,   132,   133,     1,
      -1,     3,     4,     5,     6,     7,    -1,     9,    10,    11,
      12,    13,    -1,    -1,    -1,    17,    -1,    19,    20,    -1,
      -1,    -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,
      -1,    -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,
      -1,    -1,    -1,    -1,    46,    47,    -1,    -1,    -1,    -1,
      52,    53,    54,    -1,    -1,    57,    -1,    -1,    60,    -1,
      62,    63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,
      -1,    73,    74,    75,    -1,    -1,    78,    -1,    80,    -1,
      82,    83,    84,    -1,    86,    87,    -1,    89,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,   108,   109,    -1,   111,
     112,   113,    -1,   115,   116,    -1,   118,    -1,    -1,   121,
     122,   123,    -1,   125,    -1,   127,    -1,   129,   130,    -1,
     132,   133,     1,    -1,     3,     4,     5,     6,     7,    -1,
       9,    10,    11,    12,    13,    -1,    -1,    -1,    17,    -1,
      19,    20,    -1,    -1,    -1,    -1,    25,    26,    -1,    -1,
      29,    -1,    -1,    -1,    -1,    -1,    -1,    36,    37,    -1,
      -1,    40,    -1,    -1,    -1,    -1,    -1,    46,    47,    -1,
      -1,    -1,    -1,    52,    53,    54,    -1,    -1,    57,    -1,
      -1,    60,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,
      69,    70,    71,    -1,    73,    74,    75,    -1,    -1,    78,
      -1,    80,    -1,    82,    83,    84,    -1,    86,    87,    -1,
      89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,
      -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,
     109,    -1,   111,   112,   113,    -1,   115,   116,    -1,   118,
      -1,    -1,   121,   122,   123,    -1,   125,    -1,   127,    -1,
     129,   130,    -1,   132,   133,     1,    -1,     3,     4,     5,
       6,     7,    -1,     9,    10,    11,    12,    13,    -1,    -1,
      -1,    17,    -1,    19,    20,    -1,    -1,    -1,    -1,    25,
      26,    -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,
      36,    37,    -1,    -1,    40,    -1,    -1,    -1,    -1,    -1,
      46,    47,    -1,    -1,    -1,    -1,    52,    53,    54,    -1,
      -1,    57,    -1,    -1,    60,    -1,    62,    63,    -1,    65,
      -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,    75,
      -1,    -1,    78,    -1,    80,    -1,    82,    83,    84,    -1,
      86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,   108,   109,    -1,   111,   112,   113,    -1,   115,
     116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,   125,
      -1,   127,    -1,   129,   130,    -1,   132,   133,     1,    -1,
       3,     4,     5,     6,     7,    -1,     9,    10,    11,    12,
      13,    -1,    -1,    -1,    17,    -1,    19,    20,    -1,    -1,
      -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,    -1,
      -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,    -1,
      -1,    -1,    -1,    46,    47,    -1,    -1,    -1,    -1,    52,
      53,    54,    -1,    -1,    57,    -1,    -1,    60,    -1,    62,
      63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,
      73,    74,    75,    -1,    -1,    78,    -1,    80,    -1,    82,
      83,    84,    -1,    86,    87,    -1,    89,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,
     113,    -1,   115,   116,    -1,   118,    -1,    -1,   121,   122,
     123,    -1,   125,    -1,   127,    -1,   129,   130,    -1,   132,
     133,     1,    -1,     3,     4,     5,     6,     7,    -1,     9,
      10,    11,    12,    13,    -1,    -1,    -1,    17,    -1,    19,
      20,    -1,    -1,    -1,    -1,    25,    26,    -1,    -1,    29,
      -1,    -1,    -1,    -1,    -1,    -1,    36,    37,    -1,    -1,
      40,    -1,    -1,    -1,    -1,    -1,    46,    47,    -1,    -1,
      -1,    -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,
      60,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,
      70,    71,    -1,    73,    74,    75,    -1,    -1,    78,    -1,
      80,    -1,    82,    83,    84,    -1,    86,    87,    -1,    89,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,
     100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,   109,
      -1,   111,   112,   113,    -1,   115,   116,    -1,   118,    -1,
      -1,   121,   122,   123,    -1,   125,    -1,   127,    -1,   129,
     130,    -1,   132,   133,     1,    -1,     3,     4,     5,     6,
       7,    -1,     9,    10,    11,    12,    13,    -1,    -1,    -1,
      17,    -1,    19,    20,    -1,    -1,    -1,    -1,    25,    26,
      -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,    36,
      37,    -1,    -1,    40,    -1,    -1,    -1,    -1,    -1,    46,
      47,    -1,    -1,    -1,    -1,    52,    53,    54,    -1,    -1,
      57,    -1,    -1,    60,    -1,    62,    63,    -1,    65,    -1,
      -1,    -1,    69,    70,    71,    -1,    73,    74,    75,    -1,
      -1,    78,    -1,    80,    -1,    82,    83,    84,    -1,    86,
      87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,   108,   109,    -1,   111,   112,   113,    -1,   115,   116,
      -1,   118,    -1,    -1,   121,   122,   123,    -1,   125,    -1,
     127,     8,   129,   130,    -1,   132,   133,    14,    15,    16,
      17,    -1,    19,    20,    21,    22,    23,    24,    -1,    -1,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    -1,    -1,    -1,    -1,    -1,    44,    45,    -1,
      11,    -1,    -1,    14,    15,    16,    17,    -1,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,    -1,    -1,
      -1,    -1,    -1,    -1,    45,    -1,    -1,    -1,    85,    -1,
      14,    15,    16,    17,    55,    19,    20,    21,    22,    23,
      24,    -1,    -1,    27,    28,    29,    30,    31,    32,    33,
      34,    35,    36,    37,    38,    -1,    -1,   114,    -1,    -1,
     117,    45,    -1,   120,    85,    -1,    50,   124,   125,    -1,
      -1,   128,    -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   114,    -1,    -1,   117,    -1,    -1,   120,
      -1,    85,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,
     131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   109,    -1,    -1,    -1,    -1,
     114,    -1,    -1,   117,    -1,    -1,   120,    -1,    -1,    -1,
     124,   125,    -1,    -1,   128,    -1,    -1,   131,    14,    15,
      16,    17,    18,    19,    20,    21,    22,    23,    24,    -1,
      -1,    27,    28,    29,    30,    31,    32,    33,    34,    35,
      36,    37,    38,    -1,    -1,    14,    15,    16,    17,    45,
      19,    20,    21,    22,    23,    24,    -1,    26,    27,    28,
      29,    30,    31,    32,    33,    34,    35,    36,    37,    38,
      -1,    -1,    -1,    -1,    -1,    -1,    45,    -1,    14,    -1,
      16,    17,    -1,    19,    20,    21,    22,    23,    24,    85,
      -1,    27,    28,    29,    30,    31,    32,    33,    34,    35,
      36,    37,    38,    -1,    -1,    -1,    -1,    -1,    -1,    45,
      -1,    -1,    -1,    -1,    -1,    -1,    85,    -1,   114,    -1,
      -1,   117,    -1,    -1,   120,    -1,    -1,    -1,   124,   125,
      -1,    -1,   128,    -1,    -1,   131,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,   117,    85,
      -1,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,   128,
      -1,    -1,   131,    14,    15,    16,    17,    -1,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,   124,   125,
      -1,    -1,   128,    -1,    45,   131,    -1,    14,    15,    16,
      17,    -1,    19,    20,    21,    22,    23,    24,    -1,    60,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    -1,    -1,    -1,    -1,    -1,    -1,    45,    -1,
      -1,    -1,    -1,    -1,    85,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    60,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   114,    -1,    -1,   117,    -1,    85,   120,
      -1,    -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,
     131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,
     117,    -1,    -1,   120,    -1,    -1,    -1,   124,   125,    -1,
      -1,   128,    -1,    -1,   131,    14,    15,    16,    17,    -1,
      19,    20,    21,    22,    23,    24,    -1,    -1,    27,    28,
      29,    30,    31,    32,    33,    34,    35,    36,    37,    38,
      -1,    -1,    14,    15,    16,    17,    45,    19,    20,    21,
      22,    23,    24,    -1,    -1,    27,    28,    29,    30,    31,
      32,    33,    34,    35,    36,    37,    38,    -1,    -1,    -1,
      -1,    -1,    -1,    45,    -1,    14,    15,    16,    17,    18,
      19,    20,    21,    22,    23,    24,    85,    -1,    27,    28,
      29,    30,    31,    32,    33,    34,    35,    36,    37,    38,
      99,    -1,    -1,    -1,    -1,    -1,    45,    -1,    -1,    -1,
      -1,    -1,    -1,    85,    -1,   114,    -1,    -1,   117,    -1,
      -1,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,   128,
      -1,    -1,   131,    -1,    -1,    -1,    -1,   109,    -1,    -1,
      -1,    -1,   114,    -1,    -1,   117,    85,    -1,   120,    -1,
      -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,   117,    -1,
      -1,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,   128,
      -1,    -1,   131,    14,    15,    16,    17,    -1,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,    -1,    -1,
      41,    -1,    -1,    -1,    45,    -1,    14,    15,    16,    17,
      18,    19,    20,    21,    22,    23,    24,    -1,    -1,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,    -1,    -1,    -1,    -1,    -1,    -1,    45,    -1,    -1,
      -1,    -1,    -1,    -1,    85,    -1,    -1,    14,    15,    16,
      17,    18,    19,    20,    21,    22,    23,    24,    -1,    -1,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    -1,   114,    -1,    -1,   117,    85,    45,   120,
      -1,    -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,
     131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,   117,
      -1,    -1,   120,    -1,    -1,    -1,   124,   125,    85,    -1,
     128,    -1,    -1,   131,    17,    -1,    19,    20,    21,    22,
      23,    24,    -1,    -1,    27,    28,    29,    30,    31,    32,
      33,    -1,    35,    36,    37,    38,    -1,   114,    -1,    -1,
     117,    -1,    -1,   120,    -1,    -1,    -1,   124,   125,    -1,
      -1,   128,    -1,    -1,   131,    14,    15,    16,    17,    18,
      19,    20,    21,    22,    23,    24,    -1,    -1,    27,    28,
      29,    30,    31,    32,    33,    34,    35,    36,    37,    38,
      -1,    -1,    85,    -1,    -1,    -1,    45,    -1,    14,    15,
      16,    17,    18,    19,    20,    21,    22,    23,    24,    -1,
      -1,    27,    28,    29,    30,    31,    32,    33,    34,    35,
      36,    37,    38,    -1,    -1,    -1,    -1,    -1,    -1,    45,
      -1,    -1,   125,    -1,    -1,   128,    85,    -1,   131,    14,
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      -1,    -1,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    -1,   114,    -1,    -1,   117,    85,
      45,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,   128,
      -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,
      -1,   117,    -1,    -1,   120,    -1,    -1,    -1,   124,   125,
      85,    -1,   128,    -1,    -1,   131,    17,    -1,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    28,    29,    30,
      31,    32,    33,    -1,    35,    36,    -1,    38,    -1,   114,
      -1,    -1,   117,    -1,    -1,   120,    -1,    -1,    -1,   124,
     125,    -1,    -1,   128,    -1,    -1,   131,    14,    15,    16,
      17,    18,    19,    20,    21,    22,    23,    24,    -1,    -1,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    -1,    -1,    85,    -1,    -1,    -1,    45,    -1,
      14,    15,    16,    17,    18,    19,    20,    21,    22,    23,
      24,    -1,    -1,    27,    28,    29,    30,    31,    32,    33,
      34,    35,    36,    37,    38,    -1,    -1,    -1,    -1,    -1,
      -1,    45,    -1,    -1,   125,    -1,    -1,   128,    85,    -1,
     131,    14,    15,    16,    17,    18,    19,    20,    21,    22,
      23,    24,    -1,    -1,    27,    28,    29,    30,    31,    32,
      33,    34,    35,    36,    37,    38,    -1,   114,    -1,    -1,
     117,    85,    45,   120,    -1,    -1,    -1,   124,   125,    -1,
      -1,   128,    -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
     114,    -1,    -1,   117,    -1,    -1,   120,    -1,    -1,    -1,
     124,   125,    85,    -1,   128,    -1,    -1,   131,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,   114,    -1,    -1,   117,    -1,    -1,   120,    -1,    -1,
      -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,    14,
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      -1,    -1,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    -1,    -1,    -1,    -1,    -1,    -1,
      45,    -1,    14,    15,    16,    17,    18,    19,    20,    21,
      22,    23,    24,    -1,    -1,    27,    28,    29,    30,    31,
      32,    33,    34,    35,    36,    37,    38,    -1,    -1,    14,
      15,    16,    17,    45,    19,    20,    21,    22,    23,    24,
      85,    -1,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    -1,    -1,    41,    -1,    -1,    -1,
      45,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,
      -1,    -1,   117,    85,    -1,   120,    -1,    -1,    -1,   124,
     125,    -1,    -1,   128,    -1,    -1,   131,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      85,    -1,   114,    -1,    -1,   117,    -1,    -1,   120,    -1,
      -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,
      -1,    -1,   117,    -1,    -1,   120,    -1,    -1,    -1,   124,
     125,    -1,    -1,   128,    -1,    -1,   131,    14,    15,    16,
      17,    18,    19,    20,    21,    22,    23,    24,    -1,    -1,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    -1,    -1,    14,    15,    16,    17,    45,    19,
      20,    21,    22,    23,    24,    -1,    -1,    27,    28,    29,
      30,    31,    32,    33,    34,    35,    36,    37,    38,    -1,
      -1,    41,    -1,    -1,    -1,    45,    -1,    14,    15,    16,
      17,    -1,    19,    20,    21,    22,    23,    24,    85,    -1,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    -1,    -1,    -1,    -1,    -1,    -1,    45,    -1,
      -1,    -1,    -1,    50,    -1,    85,    -1,   114,    -1,    -1,
     117,    -1,    -1,   120,    -1,    -1,    -1,   124,   125,    -1,
      -1,   128,    -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   114,    -1,    -1,   117,    85,    -1,
     120,    -1,    -1,    -1,   124,   125,    -1,    -1,   128,    -1,
      -1,   131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,
     117,    -1,    -1,   120,    -1,    -1,    -1,   124,   125,    -1,
      -1,   128,    -1,    -1,   131,    14,    15,    16,    17,    18,
      19,    20,    21,    22,    23,    24,    -1,    -1,    27,    28,
      29,    30,    31,    32,    33,    34,    35,    36,    37,    38,
      -1,    -1,    14,    15,    16,    17,    45,    19,    20,    21,
      22,    23,    24,    -1,    -1,    27,    28,    29,    30,    31,
      32,    33,    34,    35,    36,    37,    38,    -1,    -1,    41,
      -1,    -1,    -1,    45,    -1,    14,    15,    16,    17,    -1,
      19,    20,    21,    22,    23,    24,    85,    26,    27,    28,
      29,    30,    31,    32,    33,    34,    35,    36,    37,    38,
      -1,    -1,    -1,    -1,    -1,    -1,    45,    -1,    -1,    -1,
      -1,    -1,    -1,    85,    -1,   114,    -1,    -1,   117,    -1,
      -1,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,   128,
      -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,   114,    -1,    -1,   117,    85,    -1,   120,    -1,
      -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,   117,    -1,
      -1,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,   128,
      -1,    -1,   131,    14,    15,    16,    17,    -1,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,    -1,    -1,
      -1,    -1,    -1,    -1,    45,    -1,    -1,    14,    15,    16,
      17,    -1,    19,    20,    21,    22,    23,    24,    -1,    60,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    -1,    -1,    -1,    -1,    -1,    -1,    45,    -1,
      -1,    -1,    16,    17,    85,    19,    20,    21,    22,    23,
      24,    -1,    -1,    27,    28,    29,    30,    31,    32,    33,
      34,    35,    36,    37,    38,    -1,    -1,    -1,    -1,    -1,
      77,    45,    -1,   114,    -1,    -1,   117,    -1,    85,   120,
      -1,    -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,
     131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,
     117,    85,    -1,   120,    -1,    -1,    -1,   124,   125,    -1,
      -1,   128,    -1,    -1,   131,    14,    15,    16,    17,    -1,
      19,    20,    21,    22,    23,    24,    -1,    -1,    27,    28,
      29,    30,    31,    32,    33,    34,    35,    36,    37,    38,
     124,   125,    -1,    -1,   128,    44,    45,   131,    14,    15,
      16,    17,    -1,    19,    20,    21,    22,    23,    24,    -1,
      -1,    27,    28,    29,    30,    31,    32,    33,    34,    35,
      36,    37,    38,    -1,    -1,    41,    -1,    -1,    -1,    45,
      -1,    -1,    14,    15,    16,    17,    85,    19,    20,    21,
      22,    23,    24,    -1,    -1,    27,    28,    29,    30,    31,
      32,    33,    34,    35,    36,    37,    38,    -1,    -1,    -1,
      -1,    -1,    44,    45,    -1,   114,    -1,    -1,   117,    85,
      -1,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,   128,
      -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,
      -1,   117,    -1,    85,   120,    -1,    -1,    -1,   124,   125,
      -1,    -1,   128,    -1,    -1,   131,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,   114,    -1,    -1,   117,    -1,    -1,   120,    -1,
      -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,
      14,    15,    16,    17,    -1,    19,    20,    21,    22,    23,
      24,    -1,    -1,    27,    28,    29,    30,    31,    32,    33,
      34,    35,    36,    37,    38,    -1,    -1,    -1,    -1,    -1,
      -1,    45,    -1,    -1,    -1,    -1,    14,    15,    16,    17,
      -1,    19,    20,    21,    22,    23,    24,    61,    -1,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,    -1,    -1,    -1,    -1,    -1,    44,    45,    -1,    -1,
      -1,    85,    -1,    14,    15,    16,    17,    18,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,    -1,    -1,
     114,    -1,    -1,   117,    45,    -1,   120,    85,    -1,    -1,
     124,   125,    -1,    -1,   128,    -1,    -1,   131,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,   117,
      -1,    -1,   120,    -1,    85,    -1,   124,   125,    -1,    -1,
     128,    -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   114,    -1,    -1,   117,    -1,    -1,   120,
      -1,    -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,
     131,    14,    15,    16,    17,    -1,    19,    20,    21,    22,
      23,    24,    -1,    -1,    27,    28,    29,    30,    31,    32,
      33,    34,    35,    36,    37,    38,    -1,    -1,    41,    -1,
      -1,    -1,    45,    -1,    14,    15,    16,    17,    -1,    19,
      20,    21,    22,    23,    24,    -1,    -1,    27,    28,    29,
      30,    31,    32,    33,    34,    35,    36,    37,    38,    -1,
      -1,    -1,    -1,    -1,    -1,    45,    -1,    -1,    -1,    -1,
      50,    -1,    85,    -1,    -1,    14,    15,    16,    17,    -1,
      19,    20,    21,    22,    23,    24,    -1,    -1,    27,    28,
      29,    30,    31,    32,    33,    34,    35,    36,    37,    38,
      -1,   114,    -1,    -1,   117,    85,    45,   120,    -1,    -1,
      -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,    -1,
      -1,    60,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   114,    -1,    -1,   117,    -1,    -1,
     120,    -1,    -1,    -1,   124,   125,    85,    -1,   128,    -1,
      -1,   131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,   117,    -1,
      -1,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,   128,
      -1,    -1,   131,    14,    15,    16,    17,    -1,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,    -1,    -1,
      -1,    -1,    -1,    -1,    45,    -1,    -1,    14,    15,    16,
      17,    -1,    19,    20,    21,    22,    23,    24,    -1,    60,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    -1,    -1,    41,    -1,    -1,    -1,    45,    -1,
      14,    15,    16,    17,    85,    19,    20,    21,    22,    23,
      24,    -1,    -1,    27,    28,    29,    30,    31,    32,    33,
      34,    35,    36,    37,    38,    -1,    -1,    -1,    -1,    -1,
      44,    45,    -1,   114,    -1,    -1,   117,    -1,    85,   120,
      -1,    -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,
     131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,
     117,    85,    -1,   120,    -1,    -1,    -1,   124,   125,    -1,
      -1,   128,    -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
     114,    -1,    -1,   117,    -1,    -1,   120,    -1,    -1,    -1,
     124,   125,    -1,    -1,   128,    -1,    -1,   131,    14,    15,
      16,    17,    -1,    19,    20,    21,    22,    23,    24,    -1,
      -1,    27,    28,    29,    30,    31,    32,    33,    34,    35,
      36,    37,    38,    -1,    -1,    -1,    -1,    -1,    -1,    45,
      -1,    -1,    -1,    -1,    50,    14,    15,    16,    17,    -1,
      19,    20,    21,    22,    23,    24,    -1,    -1,    27,    28,
      29,    30,    31,    32,    33,    34,    35,    36,    37,    38,
      -1,    -1,    -1,    -1,    -1,    44,    45,    -1,    -1,    85,
      -1,    14,    15,    16,    17,    -1,    19,    20,    21,    22,
      23,    24,    -1,    -1,    27,    28,    29,    30,    31,    32,
      33,    34,    35,    36,    37,    38,    -1,    -1,   114,    -1,
      -1,   117,    45,    -1,   120,    -1,    85,    -1,   124,   125,
      -1,    -1,   128,    -1,    -1,   131,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,   117,    -1,
      -1,   120,    85,    -1,    -1,   124,   125,    -1,    -1,   128,
      -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,   114,    -1,    -1,   117,    -1,    -1,   120,    -1,    -1,
      -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,    14,
      15,    16,    17,    -1,    19,    20,    21,    22,    23,    24,
      -1,    -1,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    -1,    -1,    14,    15,    16,    17,
      45,    19,    20,    21,    22,    23,    24,    -1,    -1,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,    -1,    -1,    -1,    -1,    -1,    -1,    45,    -1,    14,
      15,    16,    17,    -1,    19,    20,    21,    22,    23,    24,
      85,    -1,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    -1,    -1,    -1,    -1,    -1,    -1,
      45,    -1,    -1,    -1,    -1,    -1,    -1,    85,    -1,   114,
      -1,    -1,   117,    -1,    -1,   120,    -1,    -1,    -1,   124,
     125,    -1,    -1,   128,    -1,    -1,   131,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   117,
      85,    -1,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,
     128,    -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   120,    -1,    -1,    -1,   124,
     125,    -1,    -1,   128,    -1,    -1,   131,    15,    16,    17,
      -1,    19,    20,    21,    22,    23,    24,    -1,    -1,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,    -1,    -1,    -1,    -1,    -1,    -1,    45,    -1,    16,
      17,    -1,    19,    20,    21,    22,    23,    24,    -1,    -1,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    -1,    -1,    -1,    -1,    -1,    -1,    45,    -1,
      -1,    -1,    -1,    -1,    -1,    16,    17,    85,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,    -1,    -1,
      -1,    -1,    -1,    -1,    45,    -1,   114,    -1,    85,   117,
      -1,    -1,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,
     128,    -1,    -1,   131,   101,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    85,    -1,    -1,   124,   125,    -1,
      -1,   128,    16,    17,   131,    19,    20,    21,    22,    23,
      24,    -1,    -1,    27,    28,    29,    30,    31,    32,    33,
      34,    35,    36,    37,    38,    -1,    -1,    -1,    -1,    -1,
      -1,    45,    -1,   124,   125,    -1,    -1,   128,    -1,    17,
     131,    19,    20,    21,    22,    23,    24,    -1,    -1,    27,
      -1,    -1,    30,    31,    32,    33,    -1,    35,    36,    17,
      38,    19,    20,    21,    22,    23,    24,    -1,    -1,    27,
      -1,    85,    30,    -1,    32,    33,    -1,    35,    36,    -1,
      38,    17,    -1,    19,    20,    21,    22,    23,    24,    -1,
      -1,    27,    -1,    -1,    30,    -1,    32,    33,    -1,    35,
      36,    -1,    38,    -1,    -1,    -1,    -1,    85,    -1,    -1,
      -1,   125,    -1,    -1,   128,    -1,    17,   131,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    85,    -1,    30,
      -1,    -1,    -1,    -1,    35,    36,    17,    38,    -1,    -1,
      21,    22,    23,    24,    -1,    -1,    27,   125,    -1,    85,
     128,    -1,    -1,   131,    35,    36,    -1,    38,    17,    -1,
      -1,    -1,    21,    22,    23,    24,    -1,   125,    27,    -1,
     128,    -1,    -1,   131,    -1,    -1,    35,    36,    -1,    38,
      -1,    -1,    -1,    -1,    85,    -1,    -1,    -1,    -1,   125,
      -1,    -1,   128,    -1,    -1,   131,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    85,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   125,    -1,    85,   128,    -1,    -1,
     131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   125,    -1,    -1,   128,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,   125,    -1,    -1,   128
};

/* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
   symbol of state STATE-NUM.  */
static const yytype_uint8 yystos[] =
{
       0,     1,     3,     4,     5,     6,     7,     9,    10,    11,
      12,    13,    17,    19,    20,    25,    26,    29,    36,    37,
      40,    46,    47,    52,    53,    54,    57,    60,    62,    63,
      65,    69,    70,    71,    73,    74,    75,    78,    80,    82,
      83,    84,    86,    87,    89,    98,   100,   108,   109,   111,
     112,   113,   115,   116,   118,   121,   122,   123,   125,   127,
     129,   130,   132,   133,   136,   137,   138,   139,   141,     4,
       5,    46,    46,    15,    17,    38,   138,     4,   138,   138,
     138,    13,    78,   138,   138,     4,   138,   138,   147,     4,
      17,   138,    17,    17,    17,   138,     3,    13,    17,   139,
     138,   148,    17,   138,   138,   148,    76,   155,    17,    17,
     139,    17,   109,   138,    17,    17,    17,    38,    10,   149,
     150,    17,    83,   138,   138,   138,   138,   139,   148,   147,
     138,   148,   148,   155,   139,   138,   150,    38,   138,    17,
     138,    47,    55,   161,   148,   138,    17,     0,     8,    14,
      15,    16,    17,    19,    20,    21,    22,    23,    24,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,    44,    45,    85,   114,   117,   120,   124,   125,   128,
     131,    17,    24,   128,     4,     4,    10,    13,    26,   133,
     138,    18,   138,   138,    18,    26,    26,    26,    41,   138,
      50,   109,   138,   138,     4,    11,    55,   160,   138,   146,
      15,    29,    58,    67,   152,    44,    77,   138,   162,   138,
      60,    60,    66,    81,   140,   143,   148,    72,   138,   147,
      17,   138,   138,   138,   138,   138,    88,    14,   149,   138,
      99,    14,    17,    77,   109,   157,   109,   109,    51,   109,
     157,    17,   122,   138,    18,   138,   148,    77,    50,   138,
     138,   138,   138,   155,   147,   138,   138,   138,   138,   138,
     138,   138,   138,   138,   138,   138,   138,   138,   138,   138,
     138,   139,    40,   138,     8,   137,   138,     6,    10,    14,
      19,    21,    22,   114,   125,   139,   138,   138,   138,   138,
     138,   147,   138,   138,    18,    41,    17,    18,   148,   155,
     138,    18,    18,    18,     3,   148,    56,    18,    44,   138,
     138,   138,   138,    59,    61,   151,    18,   148,   148,   138,
     138,    79,   142,   148,   140,    77,    17,    18,    18,   147,
      18,    18,    18,    18,    41,   149,    18,   138,   147,    77,
     148,   148,   148,   157,    51,   147,    41,    50,   148,    18,
      18,    67,   138,    41,   138,    17,    38,    18,    26,   147,
      17,   138,   155,    49,    51,   109,   156,   157,   158,    51,
      76,    60,   138,   146,    60,   138,   138,    59,    60,    63,
     138,   155,    77,    77,    77,    44,     3,     4,    10,    13,
      17,   139,   144,   145,    77,   138,   140,    18,   101,    18,
     157,   157,   157,   148,   157,    18,   148,    51,   138,    41,
     147,    40,   138,    15,    74,    18,   138,    44,   109,   154,
     158,   138,    49,    51,   148,   155,    54,    55,   159,   148,
      44,   148,   151,   138,   148,   138,    44,    15,    46,   145,
      14,    44,    18,    76,   148,   140,   119,   157,    29,    77,
     148,    41,    18,   138,    41,    74,   138,   155,   140,   154,
     138,   155,    50,   138,   157,     3,    45,    77,    11,   160,
     146,    77,    44,    60,   153,    60,    77,    60,   138,   138,
      18,   145,   155,   148,    77,   148,   109,   148,   119,   138,
      44,   161,    77,    41,   140,   148,    44,   148,    50,    45,
     155,    18,   148,   148,   148,    77,    77,   110,   109,   110,
     157,   109,   148,   161,    41,   148,    77,   156,   148,   155,
     138,   155,    77,    77,    77,   147,   110,   147,   110,   157,
      77,   156,   159,    44,   109,   147,   109,   147,   148,   109,
     148,   109,   157,   148,   157,   148,   157,   157
};

#define yyerrok		(yyerrstatus = 0)
#define yyclearin	(yychar = YYEMPTY)
#define YYEMPTY		(-2)
#define YYEOF		0

#define YYACCEPT	goto yyacceptlab
#define YYABORT		goto yyabortlab
#define YYERROR		goto yyerrorlab


/* Like YYERROR except do call yyerror.  This remains here temporarily
   to ease the transition to the new meaning of YYERROR, for GCC.
   Once GCC version 2 has supplanted version 1, this can go.  */

#define YYFAIL		goto yyerrlab

#define YYRECOVERING()  (!!yyerrstatus)

#define YYBACKUP(Token, Value)					\
do								\
  if (yychar == YYEMPTY && yylen == 1)				\
    {								\
      yychar = (Token);						\
      yylval = (Value);						\
      yytoken = YYTRANSLATE (yychar);				\
      YYPOPSTACK (1);						\
      goto yybackup;						\
    }								\
  else								\
    {								\
      yyerror (scanner, YY_("syntax error: cannot back up")); \
      YYERROR;							\
    }								\
while (YYID (0))


#define YYTERROR	1
#define YYERRCODE	256


/* YYLLOC_DEFAULT -- Set CURRENT to span from RHS[1] to RHS[N].
   If N is 0, then set CURRENT to the empty location which ends
   the previous symbol: RHS[0] (always defined).  */

#define YYRHSLOC(Rhs, K) ((Rhs)[K])
#ifndef YYLLOC_DEFAULT
# define YYLLOC_DEFAULT(Current, Rhs, N)				\
    do									\
      if (YYID (N))                                                    \
	{								\
	  (Current).first_line   = YYRHSLOC (Rhs, 1).first_line;	\
	  (Current).first_column = YYRHSLOC (Rhs, 1).first_column;	\
	  (Current).last_line    = YYRHSLOC (Rhs, N).last_line;		\
	  (Current).last_column  = YYRHSLOC (Rhs, N).last_column;	\
	}								\
      else								\
	{								\
	  (Current).first_line   = (Current).last_line   =		\
	    YYRHSLOC (Rhs, 0).last_line;				\
	  (Current).first_column = (Current).last_column =		\
	    YYRHSLOC (Rhs, 0).last_column;				\
	}								\
    while (YYID (0))
#endif


/* YY_LOCATION_PRINT -- Print the location on the stream.
   This macro was not mandated originally: define only if we know
   we won't break user code: when these are the locations we know.  */

#ifndef YY_LOCATION_PRINT
# if defined YYLTYPE_IS_TRIVIAL && YYLTYPE_IS_TRIVIAL
#  define YY_LOCATION_PRINT(File, Loc)			\
     fprintf (File, "%d.%d-%d.%d",			\
	      (Loc).first_line, (Loc).first_column,	\
	      (Loc).last_line,  (Loc).last_column)
# else
#  define YY_LOCATION_PRINT(File, Loc) ((void) 0)
# endif
#endif


/* YYLEX -- calling `yylex' with the right arguments.  */

#ifdef YYLEX_PARAM
# define YYLEX yylex (&yylval, YYLEX_PARAM)
#else
# define YYLEX yylex (&yylval)
#endif

/* Enable debugging if requested.  */
#if YYDEBUG

# ifndef YYFPRINTF
#  include <stdio.h> /* INFRINGES ON USER NAME SPACE */
#  define YYFPRINTF fprintf
# endif

# define YYDPRINTF(Args)			\
do {						\
  if (yydebug)					\
    YYFPRINTF Args;				\
} while (YYID (0))

# define YY_SYMBOL_PRINT(Title, Type, Value, Location)			  \
do {									  \
  if (yydebug)								  \
    {									  \
      YYFPRINTF (stderr, "%s ", Title);					  \
      yy_symbol_print (stderr,						  \
		  Type, Value, scanner); \
      YYFPRINTF (stderr, "\n");						  \
    }									  \
} while (YYID (0))


/*--------------------------------.
| Print this symbol on YYOUTPUT.  |
`--------------------------------*/

/*ARGSUSED*/
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_symbol_value_print (FILE *yyoutput, int yytype, YYSTYPE const * const yyvaluep, void * scanner)
#else
static void
yy_symbol_value_print (yyoutput, yytype, yyvaluep, scanner)
    FILE *yyoutput;
    int yytype;
    YYSTYPE const * const yyvaluep;
    void * scanner;
#endif
{
  if (!yyvaluep)
    return;
  YYUSE (scanner);
# ifdef YYPRINT
  if (yytype < YYNTOKENS)
    YYPRINT (yyoutput, yytoknum[yytype], *yyvaluep);
# else
  YYUSE (yyoutput);
# endif
  switch (yytype)
    {
      default:
	break;
    }
}


/*--------------------------------.
| Print this symbol on YYOUTPUT.  |
`--------------------------------*/

#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_symbol_print (FILE *yyoutput, int yytype, YYSTYPE const * const yyvaluep, void * scanner)
#else
static void
yy_symbol_print (yyoutput, yytype, yyvaluep, scanner)
    FILE *yyoutput;
    int yytype;
    YYSTYPE const * const yyvaluep;
    void * scanner;
#endif
{
  if (yytype < YYNTOKENS)
    YYFPRINTF (yyoutput, "token %s (", yytname[yytype]);
  else
    YYFPRINTF (yyoutput, "nterm %s (", yytname[yytype]);

  yy_symbol_value_print (yyoutput, yytype, yyvaluep, scanner);
  YYFPRINTF (yyoutput, ")");
}

/*------------------------------------------------------------------.
| yy_stack_print -- Print the state stack from its BOTTOM up to its |
| TOP (included).                                                   |
`------------------------------------------------------------------*/

#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_stack_print (yytype_int16 *bottom, yytype_int16 *top)
#else
static void
yy_stack_print (bottom, top)
    yytype_int16 *bottom;
    yytype_int16 *top;
#endif
{
  YYFPRINTF (stderr, "Stack now");
  for (; bottom <= top; ++bottom)
    YYFPRINTF (stderr, " %d", *bottom);
  YYFPRINTF (stderr, "\n");
}

# define YY_STACK_PRINT(Bottom, Top)				\
do {								\
  if (yydebug)							\
    yy_stack_print ((Bottom), (Top));				\
} while (YYID (0))


/*------------------------------------------------.
| Report that the YYRULE is going to be reduced.  |
`------------------------------------------------*/

#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_reduce_print (YYSTYPE *yyvsp, int yyrule, void * scanner)
#else
static void
yy_reduce_print (yyvsp, yyrule, scanner)
    YYSTYPE *yyvsp;
    int yyrule;
    void * scanner;
#endif
{
  int yynrhs = yyr2[yyrule];
  int yyi;
  unsigned long int yylno = yyrline[yyrule];
  YYFPRINTF (stderr, "Reducing stack by rule %d (line %lu):\n",
	     yyrule - 1, yylno);
  /* The symbols being reduced.  */
  for (yyi = 0; yyi < yynrhs; yyi++)
    {
      fprintf (stderr, "   $%d = ", yyi + 1);
      yy_symbol_print (stderr, yyrhs[yyprhs[yyrule] + yyi],
		       &(yyvsp[(yyi + 1) - (yynrhs)])
		       		       , scanner);
      fprintf (stderr, "\n");
    }
}

# define YY_REDUCE_PRINT(Rule)		\
do {					\
  if (yydebug)				\
    yy_reduce_print (yyvsp, Rule, scanner); \
} while (YYID (0))

/* Nonzero means print parse trace.  It is left uninitialized so that
   multiple parsers can coexist.  */
int yydebug;
#else /* !YYDEBUG */
# define YYDPRINTF(Args)
# define YY_SYMBOL_PRINT(Title, Type, Value, Location)
# define YY_STACK_PRINT(Bottom, Top)
# define YY_REDUCE_PRINT(Rule)
#endif /* !YYDEBUG */


/* YYINITDEPTH -- initial size of the parser's stacks.  */
#ifndef	YYINITDEPTH
# define YYINITDEPTH 200
#endif

/* YYMAXDEPTH -- maximum size the stacks can grow to (effective only
   if the built-in stack extension method is used).

   Do not make this value too large; the results are undefined if
   YYSTACK_ALLOC_MAXIMUM < YYSTACK_BYTES (YYMAXDEPTH)
   evaluated with infinite-precision integer arithmetic.  */

#ifndef YYMAXDEPTH
# define YYMAXDEPTH 10000
#endif



#if YYERROR_VERBOSE

# ifndef yystrlen
#  if defined __GLIBC__ && defined _STRING_H
#   define yystrlen strlen
#  else
/* Return the length of YYSTR.  */
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static YYSIZE_T
yystrlen (const char *yystr)
#else
static YYSIZE_T
yystrlen (yystr)
    const char *yystr;
#endif
{
  YYSIZE_T yylen;
  for (yylen = 0; yystr[yylen]; yylen++)
    continue;
  return yylen;
}
#  endif
# endif

# ifndef yystpcpy
#  if defined __GLIBC__ && defined _STRING_H && defined _GNU_SOURCE
#   define yystpcpy stpcpy
#  else
/* Copy YYSRC to YYDEST, returning the address of the terminating '\0' in
   YYDEST.  */
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static char *
yystpcpy (char *yydest, const char *yysrc)
#else
static char *
yystpcpy (yydest, yysrc)
    char *yydest;
    const char *yysrc;
#endif
{
  char *yyd = yydest;
  const char *yys = yysrc;

  while ((*yyd++ = *yys++) != '\0')
    continue;

  return yyd - 1;
}
#  endif
# endif

# ifndef yytnamerr
/* Copy to YYRES the contents of YYSTR after stripping away unnecessary
   quotes and backslashes, so that it's suitable for yyerror.  The
   heuristic is that double-quoting is unnecessary unless the string
   contains an apostrophe, a comma, or backslash (other than
   backslash-backslash).  YYSTR is taken from yytname.  If YYRES is
   null, do not copy; instead, return the length of what the result
   would have been.  */
static YYSIZE_T
yytnamerr (char *yyres, const char *yystr)
{
  if (*yystr == '"')
    {
      YYSIZE_T yyn = 0;
      char const *yyp = yystr;

      for (;;)
	switch (*++yyp)
	  {
	  case '\'':
	  case ',':
	    goto do_not_strip_quotes;

	  case '\\':
	    if (*++yyp != '\\')
	      goto do_not_strip_quotes;
	    /* Fall through.  */
	  default:
	    if (yyres)
	      yyres[yyn] = *yyp;
	    yyn++;
	    break;

	  case '"':
	    if (yyres)
	      yyres[yyn] = '\0';
	    return yyn;
	  }
    do_not_strip_quotes: ;
    }

  if (! yyres)
    return yystrlen (yystr);

  return yystpcpy (yyres, yystr) - yyres;
}
# endif

/* Copy into YYRESULT an error message about the unexpected token
   YYCHAR while in state YYSTATE.  Return the number of bytes copied,
   including the terminating null byte.  If YYRESULT is null, do not
   copy anything; just return the number of bytes that would be
   copied.  As a special case, return 0 if an ordinary "syntax error"
   message will do.  Return YYSIZE_MAXIMUM if overflow occurs during
   size calculation.  */
static YYSIZE_T
yysyntax_error (char *yyresult, int yystate, int yychar)
{
  int yyn = yypact[yystate];

  if (! (YYPACT_NINF < yyn && yyn <= YYLAST))
    return 0;
  else
    {
      int yytype = YYTRANSLATE (yychar);
      YYSIZE_T yysize0 = yytnamerr (0, yytname[yytype]);
      YYSIZE_T yysize = yysize0;
      YYSIZE_T yysize1;
      int yysize_overflow = 0;
      enum { YYERROR_VERBOSE_ARGS_MAXIMUM = 5 };
      char const *yyarg[YYERROR_VERBOSE_ARGS_MAXIMUM];
      int yyx;

# if 0
      /* This is so xgettext sees the translatable formats that are
	 constructed on the fly.  */
      YY_("syntax error, unexpected %s");
      YY_("syntax error, unexpected %s, expecting %s");
      YY_("syntax error, unexpected %s, expecting %s or %s");
      YY_("syntax error, unexpected %s, expecting %s or %s or %s");
      YY_("syntax error, unexpected %s, expecting %s or %s or %s or %s");
# endif
      char *yyfmt;
      char const *yyf;
      static char const yyunexpected[] = "syntax error, unexpected %s";
      static char const yyexpecting[] = ", expecting %s";
      static char const yyor[] = " or %s";
      char yyformat[sizeof yyunexpected
		    + sizeof yyexpecting - 1
		    + ((YYERROR_VERBOSE_ARGS_MAXIMUM - 2)
		       * (sizeof yyor - 1))];
      char const *yyprefix = yyexpecting;

      /* Start YYX at -YYN if negative to avoid negative indexes in
	 YYCHECK.  */
      int yyxbegin = yyn < 0 ? -yyn : 0;

      /* Stay within bounds of both yycheck and yytname.  */
      int yychecklim = YYLAST - yyn + 1;
      int yyxend = yychecklim < YYNTOKENS ? yychecklim : YYNTOKENS;
      int yycount = 1;

      yyarg[0] = yytname[yytype];
      yyfmt = yystpcpy (yyformat, yyunexpected);

      for (yyx = yyxbegin; yyx < yyxend; ++yyx)
	if (yycheck[yyx + yyn] == yyx && yyx != YYTERROR)
	  {
	    if (yycount == YYERROR_VERBOSE_ARGS_MAXIMUM)
	      {
		yycount = 1;
		yysize = yysize0;
		yyformat[sizeof yyunexpected - 1] = '\0';
		break;
	      }
	    yyarg[yycount++] = yytname[yyx];
	    yysize1 = yysize + yytnamerr (0, yytname[yyx]);
	    yysize_overflow |= (yysize1 < yysize);
	    yysize = yysize1;
	    yyfmt = yystpcpy (yyfmt, yyprefix);
	    yyprefix = yyor;
	  }

      yyf = YY_(yyformat);
      yysize1 = yysize + yystrlen (yyf);
      yysize_overflow |= (yysize1 < yysize);
      yysize = yysize1;

      if (yysize_overflow)
	return YYSIZE_MAXIMUM;

      if (yyresult)
	{
	  /* Avoid sprintf, as that infringes on the user's name space.
	     Don't have undefined behavior even if the translation
	     produced a string with the wrong number of "%s"s.  */
	  char *yyp = yyresult;
	  int yyi = 0;
	  while ((*yyp = *yyf) != '\0')
	    {
	      if (*yyp == '%' && yyf[1] == 's' && yyi < yycount)
		{
		  yyp += yytnamerr (yyp, yyarg[yyi++]);
		  yyf += 2;
		}
	      else
		{
		  yyp++;
		  yyf++;
		}
	    }
	}
      return yysize;
    }
}
#endif /* YYERROR_VERBOSE */


/*-----------------------------------------------.
| Release the memory associated to this symbol.  |
`-----------------------------------------------*/

/*ARGSUSED*/
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yydestruct (const char *yymsg, int yytype, YYSTYPE *yyvaluep, void * scanner)
#else
static void
yydestruct (yymsg, yytype, yyvaluep, scanner)
    const char *yymsg;
    int yytype;
    YYSTYPE *yyvaluep;
    void * scanner;
#endif
{
  YYUSE (yyvaluep);
  YYUSE (scanner);

  if (!yymsg)
    yymsg = "Deleting";
  YY_SYMBOL_PRINT (yymsg, yytype, yyvaluep, yylocationp);

  switch (yytype)
    {

      default:
	break;
    }
}


/* Prevent warnings from -Wmissing-prototypes.  */

#ifdef YYPARSE_PARAM
#if defined __STDC__ || defined __cplusplus
int yyparse (void *YYPARSE_PARAM);
#else
int yyparse ();
#endif
#else /* ! YYPARSE_PARAM */
#if defined __STDC__ || defined __cplusplus
int yyparse (void * scanner);
#else
int yyparse ();
#endif
#endif /* ! YYPARSE_PARAM */






/*----------.
| yyparse.  |
`----------*/

#ifdef YYPARSE_PARAM
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
int
yyparse (void *YYPARSE_PARAM)
#else
int
yyparse (YYPARSE_PARAM)
    void *YYPARSE_PARAM;
#endif
#else /* ! YYPARSE_PARAM */
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
int
yyparse (void * scanner)
#else
int
yyparse (scanner)
    void * scanner;
#endif
#endif
{
  /* The look-ahead symbol.  */
int yychar;

/* The semantic value of the look-ahead symbol.  */
YYSTYPE yylval;

/* Number of syntax errors so far.  */
int yynerrs;

  int yystate;
  int yyn;
  int yyresult;
  /* Number of tokens to shift before error messages enabled.  */
  int yyerrstatus;
  /* Look-ahead token as an internal (translated) token number.  */
  int yytoken = 0;
#if YYERROR_VERBOSE
  /* Buffer for error messages, and its allocated size.  */
  char yymsgbuf[128];
  char *yymsg = yymsgbuf;
  YYSIZE_T yymsg_alloc = sizeof yymsgbuf;
#endif

  /* Three stacks and their tools:
     `yyss': related to states,
     `yyvs': related to semantic values,
     `yyls': related to locations.

     Refer to the stacks thru separate pointers, to allow yyoverflow
     to reallocate them elsewhere.  */

  /* The state stack.  */
  yytype_int16 yyssa[YYINITDEPTH];
  yytype_int16 *yyss = yyssa;
  yytype_int16 *yyssp;

  /* The semantic value stack.  */
  YYSTYPE yyvsa[YYINITDEPTH];
  YYSTYPE *yyvs = yyvsa;
  YYSTYPE *yyvsp;



#define YYPOPSTACK(N)   (yyvsp -= (N), yyssp -= (N))

  YYSIZE_T yystacksize = YYINITDEPTH;

  /* The variables used to return semantic value and location from the
     action routines.  */
  YYSTYPE yyval;


  /* The number of symbols on the RHS of the reduced rule.
     Keep to zero when no symbol should be popped.  */
  int yylen = 0;

  YYDPRINTF ((stderr, "Starting parse\n"));

  yystate = 0;
  yyerrstatus = 0;
  yynerrs = 0;
  yychar = YYEMPTY;		/* Cause a token to be read.  */

  /* Initialize stack pointers.
     Waste one element of value and location stack
     so that they stay on the same level as the state stack.
     The wasted elements are never initialized.  */

  yyssp = yyss;
  yyvsp = yyvs;

  goto yysetstate;

/*------------------------------------------------------------.
| yynewstate -- Push a new state, which is found in yystate.  |
`------------------------------------------------------------*/
 yynewstate:
  /* In all cases, when you get here, the value and location stacks
     have just been pushed.  So pushing a state here evens the stacks.  */
  yyssp++;

 yysetstate:
  *yyssp = yystate;

  if (yyss + yystacksize - 1 <= yyssp)
    {
      /* Get the current used size of the three stacks, in elements.  */
      YYSIZE_T yysize = yyssp - yyss + 1;

#ifdef yyoverflow
      {
	/* Give user a chance to reallocate the stack.  Use copies of
	   these so that the &'s don't force the real ones into
	   memory.  */
	YYSTYPE *yyvs1 = yyvs;
	yytype_int16 *yyss1 = yyss;


	/* Each stack pointer address is followed by the size of the
	   data in use in that stack, in bytes.  This used to be a
	   conditional around just the two extra args, but that might
	   be undefined if yyoverflow is a macro.  */
	yyoverflow (YY_("memory exhausted"),
		    &yyss1, yysize * sizeof (*yyssp),
		    &yyvs1, yysize * sizeof (*yyvsp),

		    &yystacksize);

	yyss = yyss1;
	yyvs = yyvs1;
      }
#else /* no yyoverflow */
# ifndef YYSTACK_RELOCATE
      goto yyexhaustedlab;
# else
      /* Extend the stack our own way.  */
      if (YYMAXDEPTH <= yystacksize)
	goto yyexhaustedlab;
      yystacksize *= 2;
      if (YYMAXDEPTH < yystacksize)
	yystacksize = YYMAXDEPTH;

      {
	yytype_int16 *yyss1 = yyss;
	union yyalloc *yyptr =
	  (union yyalloc *) YYSTACK_ALLOC (YYSTACK_BYTES (yystacksize));
	if (! yyptr)
	  goto yyexhaustedlab;
	YYSTACK_RELOCATE (yyss);
	YYSTACK_RELOCATE (yyvs);

#  undef YYSTACK_RELOCATE
	if (yyss1 != yyssa)
	  YYSTACK_FREE (yyss1);
      }
# endif
#endif /* no yyoverflow */

      yyssp = yyss + yysize - 1;
      yyvsp = yyvs + yysize - 1;


      YYDPRINTF ((stderr, "Stack size increased to %lu\n",
		  (unsigned long int) yystacksize));

      if (yyss + yystacksize - 1 <= yyssp)
	YYABORT;
    }

  YYDPRINTF ((stderr, "Entering state %d\n", yystate));

  goto yybackup;

/*-----------.
| yybackup.  |
`-----------*/
yybackup:

  /* Do appropriate processing given the current state.  Read a
     look-ahead token if we need one and don't already have one.  */

  /* First try to decide what to do without reference to look-ahead token.  */
  yyn = yypact[yystate];
  if (yyn == YYPACT_NINF)
    goto yydefault;

  /* Not known => get a look-ahead token if don't already have one.  */

  /* YYCHAR is either YYEMPTY or YYEOF or a valid look-ahead symbol.  */
  if (yychar == YYEMPTY)
    {
      YYDPRINTF ((stderr, "Reading a token: "));
      yychar = YYLEX;
    }

  if (yychar <= YYEOF)
    {
      yychar = yytoken = YYEOF;
      YYDPRINTF ((stderr, "Now at end of input.\n"));
    }
  else
    {
      yytoken = YYTRANSLATE (yychar);
      YY_SYMBOL_PRINT ("Next token is", yytoken, &yylval, &yylloc);
    }

  /* If the proper action on seeing token YYTOKEN is to reduce or to
     detect an error, take that action.  */
  yyn += yytoken;
  if (yyn < 0 || YYLAST < yyn || yycheck[yyn] != yytoken)
    goto yydefault;
  yyn = yytable[yyn];
  if (yyn <= 0)
    {
      if (yyn == 0 || yyn == YYTABLE_NINF)
	goto yyerrlab;
      yyn = -yyn;
      goto yyreduce;
    }

  if (yyn == YYFINAL)
    YYACCEPT;

  /* Count tokens shifted since error; after three, turn off error
     status.  */
  if (yyerrstatus)
    yyerrstatus--;

  /* Shift the look-ahead token.  */
  YY_SYMBOL_PRINT ("Shifting", yytoken, &yylval, &yylloc);

  /* Discard the shifted token unless it is eof.  */
  if (yychar != YYEOF)
    yychar = YYEMPTY;

  yystate = yyn;
  *++yyvsp = yylval;

  goto yynewstate;


/*-----------------------------------------------------------.
| yydefault -- do the default action for the current state.  |
`-----------------------------------------------------------*/
yydefault:
  yyn = yydefact[yystate];
  if (yyn == 0)
    goto yyerrlab;
  goto yyreduce;


/*-----------------------------.
| yyreduce -- Do a reduction.  |
`-----------------------------*/
yyreduce:
  /* yyn is the number of a rule to reduce with.  */
  yylen = yyr2[yyn];

  /* If YYLEN is nonzero, implement the default value of the action:
     `$$ = $1'.

     Otherwise, the following line sets YYVAL to garbage.
     This behavior is undocumented and Bison
     users should not rely upon it.  Assigning to YYVAL
     unconditionally makes the parser a bit smaller, and it avoids a
     GCC warning that YYVAL may be used uninitialized.  */
  yyval = yyvsp[1-yylen];


  YY_REDUCE_PRINT (yyn);
  switch (yyn)
    {
        case 2:
#line 186 "input_parser.yy"
    {   const giac::context * contextptr = giac_yyget_extra(scanner);
			    if ((yyvsp[(1) - (1)])._VECTptr->size()==1)
			     parsed_gen((yyvsp[(1) - (1)])._VECTptr->front(),contextptr);
                          else
			     parsed_gen(gen(*(yyvsp[(1) - (1)])._VECTptr,_SEQ__VECT),contextptr);
			 }
    break;

  case 3:
#line 194 "input_parser.yy"
    { (yyval)=vecteur(1,(yyvsp[(1) - (2)])); }
    break;

  case 4:
#line 195 "input_parser.yy"
    { if ((yyvsp[(2) - (3)]).val==1) (yyval)=vecteur(1,symbolic(at_nodisp,(yyvsp[(1) - (3)]))); else (yyval)=vecteur(1,(yyvsp[(1) - (3)])); }
    break;

  case 5:
#line 196 "input_parser.yy"
    { if ((yyvsp[(2) - (3)]).val==1) (yyval)=mergevecteur(makevecteur(symbolic(at_nodisp,(yyvsp[(1) - (3)]))),*(yyvsp[(3) - (3)])._VECTptr); else (yyval)=mergevecteur(makevecteur((yyvsp[(1) - (3)])),*(yyvsp[(3) - (3)])._VECTptr); }
    break;

  case 6:
#line 199 "input_parser.yy"
    {(yyval) = (yyvsp[(1) - (1)]);}
    break;

  case 7:
#line 200 "input_parser.yy"
    {if (is_one((yyvsp[(1) - (2)]))) (yyval)=(yyvsp[(2) - (2)]); else (yyval)=symbolic(at_prod,gen(makevecteur((yyvsp[(1) - (2)]),(yyvsp[(2) - (2)])),_SEQ__VECT));}
    break;

  case 8:
#line 201 "input_parser.yy"
    {if (is_one((yyvsp[(1) - (4)]))) (yyval)=symb_pow((yyvsp[(2) - (4)]),(yyvsp[(4) - (4)])); else (yyval)=symbolic(at_prod,gen(makevecteur((yyvsp[(1) - (4)]),symb_pow((yyvsp[(2) - (4)]),(yyvsp[(4) - (4)]))),_SEQ__VECT));}
    break;

  case 9:
#line 202 "input_parser.yy"
    {(yyval)=symbolic(at_prod,gen(makevecteur((yyvsp[(1) - (3)]),symb_pow((yyvsp[(2) - (3)]),(yyvsp[(3) - (3)]))) ,_SEQ__VECT));}
    break;

  case 10:
#line 203 "input_parser.yy"
    {if (is_one((yyvsp[(1) - (2)]))) (yyval)=(yyvsp[(2) - (2)]); else (yyval)=symbolic(at_prod,gen(makevecteur((yyvsp[(1) - (2)]),(yyvsp[(2) - (2)])) ,_SEQ__VECT));}
    break;

  case 11:
#line 205 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 12:
#line 206 "input_parser.yy"
    { if ((yyvsp[(1) - (1)]).type==_FUNC) (yyval)=symbolic(*(yyvsp[(1) - (1)])._FUNCptr,gen(vecteur(0),_SEQ__VECT)); else (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 13:
#line 209 "input_parser.yy"
    {(yyval) = symb_program_sto((yyvsp[(3) - (6)]),(yyvsp[(3) - (6)])*zero,(yyvsp[(6) - (6)]),(yyvsp[(1) - (6)]),false,giac_yyget_extra(scanner));}
    break;

  case 14:
#line 210 "input_parser.yy"
    {if (is_array_index((yyvsp[(1) - (6)]),(yyvsp[(3) - (6)]),giac_yyget_extra(scanner)) || (abs_calc_mode(giac_yyget_extra(scanner))==38 && (yyvsp[(1) - (6)]).type==_IDNT && strlen((yyvsp[(1) - (6)])._IDNTptr->id_name)==2 && check_vect_38((yyvsp[(1) - (6)])._IDNTptr->id_name))) (yyval)=symbolic(at_sto,gen(makevecteur((yyvsp[(6) - (6)]),symbolic(at_of,gen(makevecteur((yyvsp[(1) - (6)]),(yyvsp[(3) - (6)])) ,_SEQ__VECT))) ,_SEQ__VECT)); else (yyval) = symb_program_sto((yyvsp[(3) - (6)]),(yyvsp[(3) - (6)])*zero,(yyvsp[(6) - (6)]),(yyvsp[(1) - (6)]),true,giac_yyget_extra(scanner));}
    break;

  case 15:
#line 211 "input_parser.yy"
    {if (is_array_index((yyvsp[(3) - (6)]),(yyvsp[(5) - (6)]),giac_yyget_extra(scanner)) || (abs_calc_mode(giac_yyget_extra(scanner))==38 && (yyvsp[(3) - (6)]).type==_IDNT && check_vect_38((yyvsp[(3) - (6)])._IDNTptr->id_name))) (yyval)=symbolic(at_sto,gen(makevecteur((yyvsp[(1) - (6)]),symbolic(at_of,gen(makevecteur((yyvsp[(3) - (6)]),(yyvsp[(5) - (6)])) ,_SEQ__VECT))) ,_SEQ__VECT)); else (yyval) = symb_program_sto((yyvsp[(5) - (6)]),(yyvsp[(5) - (6)])*zero,(yyvsp[(1) - (6)]),(yyvsp[(3) - (6)]),false,giac_yyget_extra(scanner));}
    break;

  case 16:
#line 212 "input_parser.yy"
    { 
         const giac::context * contextptr = giac_yyget_extra(scanner);
         gen g=symb_at((yyvsp[(3) - (6)]),(yyvsp[(5) - (6)]),contextptr); (yyval)=symb_sto((yyvsp[(1) - (6)]),g); 
        }
    break;

  case 17:
#line 216 "input_parser.yy"
    { 
         const giac::context * contextptr = giac_yyget_extra(scanner);
         gen g=symbolic(at_of,gen(makevecteur((yyvsp[(3) - (8)]),(yyvsp[(6) - (8)])) ,_SEQ__VECT)); (yyval)=symb_sto((yyvsp[(1) - (8)]),g); 
        }
    break;

  case 18:
#line 220 "input_parser.yy"
    { if ((yyvsp[(3) - (3)]).type==_IDNT && unit_conversion_map().find((yyvsp[(3) - (3)]).print(context0).c_str()+1) != unit_conversion_map().end()) (yyval)=symbolic(at_convert,gen(makevecteur((yyvsp[(1) - (3)]),symbolic(at_unit,makevecteur(1,(yyvsp[(3) - (3)])))) ,_SEQ__VECT)); else (yyval)=symb_sto((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])); }
    break;

  case 19:
#line 221 "input_parser.yy"
    { (yyval)=symbolic(at_convert,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 20:
#line 222 "input_parser.yy"
    { (yyval)=symbolic(at_convert,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 21:
#line 223 "input_parser.yy"
    { (yyval)=symbolic(at_convert,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 22:
#line 224 "input_parser.yy"
    { (yyval)=symbolic(at_convert,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 23:
#line 225 "input_parser.yy"
    { (yyval)=symbolic(at_time,(yyvsp[(1) - (3)]));}
    break;

  case 24:
#line 226 "input_parser.yy"
    { (yyval)=symbolic(at_POLYFORM,gen(makevecteur((yyvsp[(1) - (3)]),at_eval),_SEQ__VECT));}
    break;

  case 25:
#line 227 "input_parser.yy"
    { (yyval)=symbolic(at_convert,gen(makevecteur((yyvsp[(1) - (4)]),symb_unit(plus_one,(yyvsp[(4) - (4)]),giac_yyget_extra(scanner))),_SEQ__VECT)); opened_quote(giac_yyget_extra(scanner)) &= 0x7ffffffd;}
    break;

  case 26:
#line 228 "input_parser.yy"
    {(yyval) = check_symb_of((yyvsp[(1) - (4)]),(yyvsp[(3) - (4)]),giac_yyget_extra(scanner));}
    break;

  case 27:
#line 229 "input_parser.yy"
    {(yyval) = check_symb_of((yyvsp[(1) - (4)]),(yyvsp[(3) - (4)]),giac_yyget_extra(scanner));}
    break;

  case 28:
#line 230 "input_parser.yy"
    {(yyval) = (yyvsp[(1) - (1)]);}
    break;

  case 29:
#line 231 "input_parser.yy"
    {(yyval) = (yyvsp[(1) - (1)]);}
    break;

  case 30:
#line 232 "input_parser.yy"
    {(yyval) = (yyvsp[(1) - (1)]);}
    break;

  case 31:
#line 233 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (3)])._FUNCptr,(yyvsp[(3) - (3)]));}
    break;

  case 32:
#line 234 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(3) - (3)])._FUNCptr,(yyvsp[(1) - (3)]));}
    break;

  case 33:
#line 235 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT));}
    break;

  case 34:
#line 236 "input_parser.yy"
    {(yyval) = symb_equal((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])); }
    break;

  case 35:
#line 237 "input_parser.yy"
    { 
	if ((yyvsp[(2) - (2)]).type==_SYMB) (yyval)=(yyvsp[(2) - (2)]); else (yyval)=symbolic(at_nop,(yyvsp[(2) - (2)])); 
	(yyval).change_subtype(_SPREAD__SYMB); 
        const giac::context * contextptr = giac_yyget_extra(scanner);
        spread_formula(false,contextptr); 
	}
    break;

  case 36:
#line 243 "input_parser.yy"
    { if ((yyvsp[(1) - (3)]).is_symb_of_sommet(at_plus) && (yyvsp[(1) - (3)])._SYMBptr->feuille.type==_VECT){ (yyvsp[(1) - (3)])._SYMBptr->feuille._VECTptr->push_back((yyvsp[(3) - (3)])); (yyval)=(yyvsp[(1) - (3)]); } else
  (yyval) =symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT));}
    break;

  case 37:
#line 245 "input_parser.yy"
    {(yyval) = symb_plus((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]).type<_IDNT?-(yyvsp[(3) - (3)]):symbolic(at_neg,(yyvsp[(3) - (3)])));}
    break;

  case 38:
#line 246 "input_parser.yy"
    {(yyval) = symb_plus((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]).type<_IDNT?-(yyvsp[(3) - (3)]):symbolic(at_neg,(yyvsp[(3) - (3)])));}
    break;

  case 39:
#line 247 "input_parser.yy"
    {(yyval) =symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT));}
    break;

  case 40:
#line 248 "input_parser.yy"
    {(yyval) =symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT));}
    break;

  case 41:
#line 249 "input_parser.yy"
    {if ((yyvsp[(1) - (3)])==symbolic(at_exp,1)) (yyval)=symbolic(at_exp,(yyvsp[(3) - (3)])); else (yyval) =symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT));}
    break;

  case 42:
#line 250 "input_parser.yy"
    {if ((yyvsp[(2) - (3)]).type==_FUNC) (yyval)=symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT)); else (yyval) = symbolic(at_normalmod,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT));}
    break;

  case 43:
#line 251 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 44:
#line 254 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT));}
    break;

  case 45:
#line 255 "input_parser.yy"
    {(yyval)= symbolic(at_deuxpoints,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT));}
    break;

  case 46:
#line 256 "input_parser.yy"
    { 
					if ((yyvsp[(2) - (2)])==unsigned_inf)
						(yyval) = minus_inf;
					else { if ((yyvsp[(2) - (2)]).type==_INT_) (yyval)=(-(yyvsp[(2) - (2)]).val); else (yyval)=symbolic(at_neg,(yyvsp[(2) - (2)])); }
				}
    break;

  case 47:
#line 261 "input_parser.yy"
    { 
					if ((yyvsp[(2) - (2)])==unsigned_inf)
						(yyval) = minus_inf;
					else { if ((yyvsp[(2) - (2)]).type==_INT_ || (yyvsp[(2) - (2)]).type==_DOUBLE_ || (yyvsp[(2) - (2)]).type==_FLOAT_) (yyval)=-(yyvsp[(2) - (2)]); else (yyval)=symbolic(at_neg,(yyvsp[(2) - (2)])); }
				}
    break;

  case 48:
#line 266 "input_parser.yy"
    {
					if ((yyvsp[(2) - (2)])==unsigned_inf)
						(yyval) = plus_inf;
					else
						(yyval) = (yyvsp[(2) - (2)]);
				}
    break;

  case 49:
#line 272 "input_parser.yy"
    {(yyval) = polynome_or_sparse_poly1((yyvsp[(2) - (5)]),(yyvsp[(4) - (5)]));}
    break;

  case 50:
#line 273 "input_parser.yy"
    { 
           if ( ((yyvsp[(2) - (3)]).type==_SYMB) && ((yyvsp[(2) - (3)])._SYMBptr->sommet==at_deuxpoints) )
             (yyval) = algebraic_EXTension((yyvsp[(2) - (3)])._SYMBptr->feuille._VECTptr->front(),(yyvsp[(2) - (3)])._SYMBptr->feuille._VECTptr->back());
           else (yyval)=(yyvsp[(2) - (3)]);
        }
    break;

  case 51:
#line 279 "input_parser.yy"
    { (yyval)=gen(at_of,2); }
    break;

  case 52:
#line 280 "input_parser.yy"
    {(yyval) = symb_sto((yyvsp[(3) - (3)]),(yyvsp[(1) - (3)]),(yyvsp[(2) - (3)])==at_array_sto); if ((yyvsp[(3) - (3)]).is_symb_of_sommet(at_program)) *logptr(giac_yyget_extra(scanner))<<"// End defining "<<(yyvsp[(1) - (3)])<<endl;}
    break;

  case 53:
#line 281 "input_parser.yy"
    { (yyval) = symbolic(*(yyvsp[(1) - (2)])._FUNCptr,(yyvsp[(2) - (2)]));}
    break;

  case 54:
#line 282 "input_parser.yy"
    {(yyval) = symb_args((yyvsp[(3) - (4)]));}
    break;

  case 55:
#line 283 "input_parser.yy"
    {(yyval) = symb_args((yyvsp[(3) - (4)]));}
    break;

  case 56:
#line 284 "input_parser.yy"
    { (yyval)=symb_args(vecteur(0)); }
    break;

  case 57:
#line 285 "input_parser.yy"
    {
	(yyval) = symbolic(*(yyvsp[(1) - (4)])._FUNCptr,(yyvsp[(3) - (4)]));
        const giac::context * contextptr = giac_yyget_extra(scanner);
	if (*(yyvsp[(1) - (4)])._FUNCptr==at_maple_mode ||*(yyvsp[(1) - (4)])._FUNCptr==at_xcas_mode ){
          xcas_mode(contextptr)=(yyvsp[(3) - (4)]).val;
        }
	if (*(yyvsp[(1) - (4)])._FUNCptr==at_user_operator){
          user_operator((yyvsp[(3) - (4)]),contextptr);
        }
	}
    break;

  case 58:
#line 295 "input_parser.yy"
    {
	if ((yyvsp[(3) - (4)]).type==_VECT && (yyvsp[(3) - (4)])._VECTptr->empty())
          giac_yyerror(scanner,"void argument");
	(yyval) = symbolic(*(yyvsp[(1) - (4)])._FUNCptr,(yyvsp[(3) - (4)]));	
	}
    break;

  case 59:
#line 300 "input_parser.yy"
    { 
          const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval)=symb_at((yyvsp[(1) - (4)]),(yyvsp[(3) - (4)]),contextptr);
        }
    break;

  case 60:
#line 304 "input_parser.yy"
    {
	(yyval) = symbolic(*(yyvsp[(1) - (3)])._FUNCptr,gen(vecteur(0),_SEQ__VECT));
	if (*(yyvsp[(1) - (3)])._FUNCptr==at_rpn)
          rpn_mode(giac_yyget_extra(scanner))=1;
	if (*(yyvsp[(1) - (3)])._FUNCptr==at_alg)
          rpn_mode(giac_yyget_extra(scanner))=0;
	}
    break;

  case 61:
#line 311 "input_parser.yy"
    {
	(yyval) = (yyvsp[(1) - (1)]);
	}
    break;

  case 62:
#line 314 "input_parser.yy"
    {(yyval) = symbolic(at_derive,(yyvsp[(1) - (2)]));}
    break;

  case 63:
#line 315 "input_parser.yy"
    { (yyval)=symbolic(*(yyvsp[(2) - (2)])._FUNCptr,(yyvsp[(1) - (2)])); }
    break;

  case 64:
#line 316 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (6)])._FUNCptr,makevecteur(equaltosame((yyvsp[(2) - (6)])),symb_bloc((yyvsp[(4) - (6)])),symb_bloc((yyvsp[(6) - (6)]))));}
    break;

  case 65:
#line 317 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (4)])._FUNCptr,makevecteur(equaltosame((yyvsp[(2) - (4)])),(yyvsp[(4) - (4)]),0));}
    break;

  case 66:
#line 318 "input_parser.yy"
    {
	(yyval) = symbolic(*(yyvsp[(1) - (5)])._FUNCptr,makevecteur(equaltosame((yyvsp[(2) - (5)])),symb_bloc((yyvsp[(4) - (5)])),(yyvsp[(5) - (5)])));
	}
    break;

  case 67:
#line 321 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (4)])._FUNCptr,(yyvsp[(3) - (4)]));}
    break;

  case 68:
#line 322 "input_parser.yy"
    {(yyval) = (yyvsp[(1) - (1)]);}
    break;

  case 69:
#line 323 "input_parser.yy"
    {(yyval) = symb_program((yyvsp[(3) - (4)]));}
    break;

  case 70:
#line 324 "input_parser.yy"
    {(yyval) = gen(at_program,3);}
    break;

  case 71:
#line 325 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
         (yyval) = symb_program((yyvsp[(1) - (3)]),zero*(yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]),contextptr);
        }
    break;

  case 72:
#line 329 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
             if ((yyvsp[(3) - (3)]).type==_VECT) 
                (yyval) = symb_program((yyvsp[(1) - (3)]),zero*(yyvsp[(1) - (3)]),symb_bloc(makevecteur(at_nop,(yyvsp[(3) - (3)]))),contextptr); 
             else 
                (yyval) = symb_program((yyvsp[(1) - (3)]),zero*(yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]),contextptr);
		}
    break;

  case 73:
#line 336 "input_parser.yy"
    {(yyval) = symb_bloc((yyvsp[(3) - (4)]));}
    break;

  case 74:
#line 337 "input_parser.yy"
    {(yyval) = at_bloc;}
    break;

  case 75:
#line 339 "input_parser.yy"
    { (yyval)=symbolic(*(yyvsp[(1) - (2)])._FUNCptr,(yyvsp[(2) - (2)])); }
    break;

  case 76:
#line 340 "input_parser.yy"
    {(yyval) = gen(*(yyvsp[(1) - (1)])._FUNCptr,0);}
    break;

  case 77:
#line 341 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (3)]);}
    break;

  case 78:
#line 343 "input_parser.yy"
    {(yyval) = symbolic(at_break,zero);}
    break;

  case 79:
#line 344 "input_parser.yy"
    {(yyval) = symbolic(at_continue,zero);}
    break;

  case 80:
#line 345 "input_parser.yy"
    { 
	/*
	  gen kk(identificateur("index"));
	  vecteur v(*$6._VECTptr);
          const giac::context * contextptr = giac_yyget_extra(scanner);
	  v.insert(v.begin(),symb_sto(symb_at($4,kk,contextptr),$2));
	  $$=symbolic(*$1._FUNCptr,makevecteur(symb_sto(xcas_mode(contextptr)!=0,kk),symb_inferieur_strict(kk,symb_size($4)+(xcas_mode(contextptr)!=0)),symb_sto(symb_plus(kk,plus_one),kk),symb_bloc(v))); 
          */
          if ((yyvsp[(7) - (7)]).type==_INT_ && (yyvsp[(7) - (7)]).val && (yyvsp[(7) - (7)]).val!=2 && (yyvsp[(7) - (7)]).val!=9) giac_yyerror(scanner,"missing loop end delimiter");
          (yyval)=symbolic(*(yyvsp[(1) - (7)])._FUNCptr,makevecteur(1,symbolic(*(yyvsp[(1) - (7)])._FUNCptr,makevecteur((yyvsp[(2) - (7)]),(yyvsp[(4) - (7)]))),1,symb_bloc((yyvsp[(6) - (7)]))));
	  }
    break;

  case 81:
#line 356 "input_parser.yy"
    { 
          if ((yyvsp[(9) - (9)]).type==_INT_ && (yyvsp[(9) - (9)]).val && (yyvsp[(9) - (9)]).val!=2 && (yyvsp[(9) - (9)]).val!=9) giac_yyerror(scanner,"missing loop end delimiter");
          gen tmp,st=(yyvsp[(6) - (9)]);  
       if (st==1 && (yyvsp[(4) - (9)])!=1) st=(yyvsp[(4) - (9)]);
          const giac::context * contextptr = giac_yyget_extra(scanner);
	  if (!lidnt(st).empty())
            *logptr(contextptr) << "Warning, step is not numeric " << st << std::endl;
          bool b=has_evalf(st,tmp,1,context0);
          if (!b || is_positive(tmp,context0)) 
             (yyval)=symbolic(*(yyvsp[(1) - (9)])._FUNCptr,makevecteur(symb_sto((yyvsp[(3) - (9)]),(yyvsp[(2) - (9)])),symb_inferieur_egal((yyvsp[(2) - (9)]),(yyvsp[(5) - (9)])),symb_sto(symb_plus((yyvsp[(2) - (9)]),b?abs(st,context0):symb_abs(st)),(yyvsp[(2) - (9)])),symb_bloc((yyvsp[(8) - (9)])))); 
          else 
            (yyval)=symbolic(*(yyvsp[(1) - (9)])._FUNCptr,makevecteur(symb_sto((yyvsp[(3) - (9)]),(yyvsp[(2) - (9)])),symb_superieur_egal((yyvsp[(2) - (9)]),(yyvsp[(5) - (9)])),symb_sto(symb_plus((yyvsp[(2) - (9)]),st),(yyvsp[(2) - (9)])),symb_bloc((yyvsp[(8) - (9)])))); 
        }
    break;

  case 82:
#line 369 "input_parser.yy"
    { 
          if ((yyvsp[(9) - (9)]).type==_INT_ && (yyvsp[(9) - (9)]).val && (yyvsp[(9) - (9)]).val!=2 && (yyvsp[(9) - (9)]).val!=9) giac_yyerror(scanner,"missing loop end delimiter");
         gen tmp,st=(yyvsp[(4) - (9)]); 
        if (st==1 && (yyvsp[(5) - (9)])!=1) st=(yyvsp[(5) - (9)]);
         const giac::context * contextptr = giac_yyget_extra(scanner);
	 if (!lidnt(st).empty())
            *logptr(contextptr) << "Warning, step is not numeric " << st << std::endl;
         bool b=has_evalf(st,tmp,1,context0);
         if (!b || is_positive(tmp,context0)) 
           (yyval)=symbolic(*(yyvsp[(1) - (9)])._FUNCptr,makevecteur(symb_sto((yyvsp[(3) - (9)]),(yyvsp[(2) - (9)])),symb_inferieur_egal((yyvsp[(2) - (9)]),(yyvsp[(6) - (9)])),symb_sto(symb_plus((yyvsp[(2) - (9)]),b?abs(st,context0):symb_abs(st)),(yyvsp[(2) - (9)])),symb_bloc((yyvsp[(8) - (9)])))); 
         else 
           (yyval)=symbolic(*(yyvsp[(1) - (9)])._FUNCptr,makevecteur(symb_sto((yyvsp[(3) - (9)]),(yyvsp[(2) - (9)])),symb_superieur_egal((yyvsp[(2) - (9)]),(yyvsp[(6) - (9)])),symb_sto(symb_plus((yyvsp[(2) - (9)]),st),(yyvsp[(2) - (9)])),symb_bloc((yyvsp[(8) - (9)])))); 
        }
    break;

  case 83:
#line 382 "input_parser.yy"
    { 
          if ((yyvsp[(7) - (7)]).type==_INT_ && (yyvsp[(7) - (7)]).val && (yyvsp[(7) - (7)]).val!=2 && (yyvsp[(7) - (7)]).val!=9) giac_yyerror(scanner,"missing loop end delimiter");
          (yyval)=symbolic(*(yyvsp[(1) - (7)])._FUNCptr,makevecteur(symb_sto((yyvsp[(3) - (7)]),(yyvsp[(2) - (7)])),plus_one,symb_sto(symb_plus((yyvsp[(2) - (7)]),(yyvsp[(4) - (7)])),(yyvsp[(2) - (7)])),symb_bloc((yyvsp[(6) - (7)])))); 
        }
    break;

  case 84:
#line 386 "input_parser.yy"
    { 
          if ((yyvsp[(9) - (9)]).type==_INT_ && (yyvsp[(9) - (9)]).val && (yyvsp[(9) - (9)]).val!=2 && (yyvsp[(9) - (9)]).val!=9 && (yyvsp[(9) - (9)]).val!=8) giac_yyerror(scanner,"missing loop end delimiter");
          (yyval)=symbolic(*(yyvsp[(1) - (9)])._FUNCptr,makevecteur(symb_sto((yyvsp[(3) - (9)]),(yyvsp[(2) - (9)])),(yyvsp[(6) - (9)]),symb_sto(symb_plus((yyvsp[(2) - (9)]),(yyvsp[(4) - (9)])),(yyvsp[(2) - (9)])),symb_bloc((yyvsp[(8) - (9)])))); 
        }
    break;

  case 85:
#line 390 "input_parser.yy"
    {(yyval) = gen(*(yyvsp[(1) - (1)])._FUNCptr,4);}
    break;

  case 86:
#line 391 "input_parser.yy"
    { 
          if ((yyvsp[(3) - (3)]).type==_INT_ && (yyvsp[(3) - (3)]).val && (yyvsp[(3) - (3)]).val!=2 && (yyvsp[(3) - (3)]).val!=9) giac_yyerror(scanner,"missing loop end delimiter");
           vecteur v=makevecteur(zero,plus_one,zero,symb_bloc((yyvsp[(2) - (3)]))); (yyval)=symbolic(*(yyvsp[(1) - (3)])._FUNCptr,v); 
         }
    break;

  case 87:
#line 395 "input_parser.yy"
    { 
        vecteur v=gen2vecteur((yyvsp[(2) - (4)]));
        v.push_back(symb_ifte(equaltosame((yyvsp[(4) - (4)])),symbolic(at_break,zero),0));
	(yyval)=symbolic(*(yyvsp[(1) - (4)])._FUNCptr,makevecteur(zero,1,zero,symb_bloc(v))); 
	}
    break;

  case 88:
#line 400 "input_parser.yy"
    { 
        if ((yyvsp[(5) - (5)]).type==_INT_ && (yyvsp[(5) - (5)]).val && (yyvsp[(5) - (5)]).val!=2 && (yyvsp[(5) - (5)]).val!=9) giac_yyerror(scanner,"missing loop end delimiter");
        vecteur v=gen2vecteur((yyvsp[(2) - (5)]));
        v.push_back(symb_ifte(equaltosame((yyvsp[(4) - (5)])),symbolic(at_break,zero),0));
	(yyval)=symbolic(*(yyvsp[(1) - (5)])._FUNCptr,makevecteur(zero,1,zero,symb_bloc(v))); 
	}
    break;

  case 89:
#line 406 "input_parser.yy"
    {
          if ((yyvsp[(7) - (7)]).type==_INT_ && (yyvsp[(7) - (7)]).val && (yyvsp[(7) - (7)]).val!=4) giac_yyerror(scanner,"missing iferr end delimiter");
           (yyval)=symbolic(at_try_catch,makevecteur(symb_bloc((yyvsp[(2) - (7)])),0,symb_bloc((yyvsp[(4) - (7)])),symb_bloc((yyvsp[(6) - (7)]))));
        }
    break;

  case 90:
#line 410 "input_parser.yy"
    {(yyval)=symbolic(at_piecewise,(yyvsp[(2) - (3)])); }
    break;

  case 91:
#line 411 "input_parser.yy"
    { 
	(yyval)=(yyvsp[(1) - (1)]); 
	// $$.subtype=1; 
	}
    break;

  case 92:
#line 415 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (3)]); /* $$.subtype=1; */ }
    break;

  case 93:
#line 416 "input_parser.yy"
    { (yyval) = symb_dollar((yyvsp[(2) - (2)])); }
    break;

  case 94:
#line 417 "input_parser.yy"
    {(yyval)=symb_dollar(gen(makevecteur((yyvsp[(1) - (5)]),(yyvsp[(3) - (5)]),(yyvsp[(5) - (5)])) ,_SEQ__VECT));}
    break;

  case 95:
#line 418 "input_parser.yy"
    { (yyval) = symb_dollar(gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 96:
#line 419 "input_parser.yy"
    { (yyval) = symb_dollar(gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 97:
#line 420 "input_parser.yy"
    { (yyval)=symb_dollar((yyvsp[(2) - (2)])); }
    break;

  case 98:
#line 421 "input_parser.yy"
    { (yyval) = symb_compose(gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 99:
#line 422 "input_parser.yy"
    { (yyval) = symb_union(gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 100:
#line 423 "input_parser.yy"
    { (yyval) = symb_intersect(gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 101:
#line 424 "input_parser.yy"
    { (yyval) = symb_minus(gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 102:
#line 425 "input_parser.yy"
    { 
	(yyval)=symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); 
	}
    break;

  case 103:
#line 428 "input_parser.yy"
    { (yyval) = (yyvsp[(1) - (1)]); }
    break;

  case 104:
#line 429 "input_parser.yy"
    {if ((yyvsp[(2) - (3)]).type==_FUNC) (yyval)=(yyvsp[(2) - (3)]); else { 
          // const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval)=symb_quote((yyvsp[(2) - (3)]));
          } 
        }
    break;

  case 105:
#line 434 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
	  (yyval) = symb_at((yyvsp[(1) - (4)]),(yyvsp[(3) - (4)]),contextptr);
        }
    break;

  case 106:
#line 438 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
	  (yyval) = symbolic(at_of,gen(makevecteur((yyvsp[(1) - (6)]),(yyvsp[(4) - (6)])) ,_SEQ__VECT));
        }
    break;

  case 107:
#line 442 "input_parser.yy"
    {(yyval) = check_symb_of((yyvsp[(2) - (6)]),(yyvsp[(5) - (6)]),giac_yyget_extra(scanner));}
    break;

  case 108:
#line 443 "input_parser.yy"
    {
	if (abs_calc_mode(giac_yyget_extra(scanner))==38 && (yyvsp[(2) - (3)]).type==_VECT && (yyvsp[(2) - (3)]).subtype==_SEQ__VECT && (yyvsp[(2) - (3)])._VECTptr->size()==2 && ((yyvsp[(2) - (3)])._VECTptr->front().type<=_DOUBLE_ || (yyvsp[(2) - (3)])._VECTptr->front().type==_FLOAT_) && ((yyvsp[(2) - (3)])._VECTptr->back().type<=_DOUBLE_ || (yyvsp[(2) - (3)])._VECTptr->back().type==_FLOAT_)){ 
          const giac::context * contextptr = giac_yyget_extra(scanner);
	  gen a=evalf((yyvsp[(2) - (3)])._VECTptr->front(),1,contextptr),
	      b=evalf((yyvsp[(2) - (3)])._VECTptr->back(),1,contextptr);
	  if ( (a.type==_DOUBLE_ || a.type==_FLOAT_) &&
               (b.type==_DOUBLE_ || b.type==_FLOAT_))
            (yyval)= a+b*cst_i; 
          else (yyval)=(yyvsp[(2) - (3)]);
  	} else {
             if (calc_mode(giac_yyget_extra(scanner))==1 && (yyvsp[(2) - (3)]).type==_VECT && (yyvsp[(1) - (3)])!=_LIST__VECT &&
	     (yyvsp[(2) - (3)]).subtype==_SEQ__VECT && ((yyvsp[(2) - (3)])._VECTptr->size()==2 || (yyvsp[(2) - (3)])._VECTptr->size()==3) )
               (yyval) = gen(*(yyvsp[(2) - (3)])._VECTptr,_GGB__VECT);
             else
               (yyval)=(yyvsp[(2) - (3)]);
           }
	}
    break;

  case 109:
#line 460 "input_parser.yy"
    { (yyval) = gen(*((yyvsp[(2) - (3)])._VECTptr),(yyvsp[(1) - (3)]).val);
	if ((yyvsp[(2) - (3)])._VECTptr->size()==1 && (yyvsp[(2) - (3)])._VECTptr->front().is_symb_of_sommet(at_ti_semi) ) {
	(yyval)=(yyvsp[(2) - (3)])._VECTptr->front();
  }
}
    break;

  case 110:
#line 465 "input_parser.yy"
    { 
         if ((yyvsp[(1) - (3)]).type==_VECT && (yyvsp[(1) - (3)]).subtype==_SEQ__VECT && !((yyvsp[(3) - (3)]).type==_VECT && (yyvsp[(2) - (3)]).subtype==_SEQ__VECT)){ (yyval)=(yyvsp[(1) - (3)]); (yyval)._VECTptr->push_back((yyvsp[(3) - (3)])); }
	 else
           (yyval) = makesuite((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])); 
        }
    break;

  case 111:
#line 470 "input_parser.yy"
    { (yyval)=gen(vecteur(0),_SEQ__VECT); }
    break;

  case 112:
#line 471 "input_parser.yy"
    {(yyval)=symb_findhelp((yyvsp[(2) - (2)]));}
    break;

  case 113:
#line 472 "input_parser.yy"
    { (yyval)=symb_interrogation((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])); }
    break;

  case 114:
#line 473 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval)=symb_unit(plus_one,(yyvsp[(2) - (2)]),contextptr); 
          opened_quote(giac_yyget_extra(scanner)) &= 0x7ffffffd;	
        }
    break;

  case 115:
#line 478 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval)=symb_unit((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]),contextptr); 
          opened_quote(giac_yyget_extra(scanner)) &= 0x7ffffffd;        }
    break;

  case 116:
#line 482 "input_parser.yy"
    { (yyval)=symb_pow((yyvsp[(1) - (2)]),(yyvsp[(2) - (2)])); }
    break;

  case 117:
#line 483 "input_parser.yy"
    { 
        const giac::context * contextptr = giac_yyget_extra(scanner);
#ifdef HAVE_SIGNAL_H_OLD
	messages_to_print += parser_filename(contextptr) + parser_error(contextptr); 
	/* *logptr(giac_yyget_extra(scanner)) << messages_to_print; */
#endif
	(yyval)=undef;
        spread_formula(false,contextptr); 
	}
    break;

  case 118:
#line 492 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 119:
#line 493 "input_parser.yy"
    { (yyval)=symbolic(*(yyvsp[(1) - (2)])._FUNCptr,(yyvsp[(2) - (2)])); }
    break;

  case 120:
#line 494 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (1)])._FUNCptr,gen(vecteur(0),_SEQ__VECT));}
    break;

  case 121:
#line 495 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (3)])._FUNCptr,gen(vecteur(0),_SEQ__VECT));}
    break;

  case 122:
#line 496 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval) = symb_local((yyvsp[(3) - (4)]),contextptr);
        }
    break;

  case 123:
#line 500 "input_parser.yy"
    {(yyval) = gen(at_local,2);}
    break;

  case 124:
#line 501 "input_parser.yy"
    {
	(yyval) = symbolic(*(yyvsp[(1) - (6)])._FUNCptr,makevecteur(equaltosame((yyvsp[(3) - (6)])),symb_bloc((yyvsp[(5) - (6)])),(yyvsp[(6) - (6)])));
	}
    break;

  case 125:
#line 504 "input_parser.yy"
    {
        vecteur v=makevecteur(equaltosame((yyvsp[(3) - (7)])),(yyvsp[(5) - (7)]),(yyvsp[(7) - (7)]));
	// *logptr(giac_yyget_extra(scanner)) << v << endl;
	(yyval) = symbolic(*(yyvsp[(1) - (7)])._FUNCptr,v);
	}
    break;

  case 126:
#line 509 "input_parser.yy"
    { (yyval)=symb_rpn_prog((yyvsp[(2) - (3)])); }
    break;

  case 127:
#line 510 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 128:
#line 511 "input_parser.yy"
    { (yyval)=symbolic(at_maple_lib,makevecteur((yyvsp[(1) - (4)]),(yyvsp[(3) - (4)]))); }
    break;

  case 129:
#line 512 "input_parser.yy"
    { 
          if ((yyvsp[(7) - (7)]).type==_INT_ && (yyvsp[(7) - (7)]).val && (yyvsp[(7) - (7)]).val!=3) giac_yyerror(scanner,"missing func/prog/proc end delimiter");
          const giac::context * contextptr = giac_yyget_extra(scanner);
           (yyval)=symb_program((yyvsp[(3) - (7)]),zero*(yyvsp[(3) - (7)]),symb_local((yyvsp[(5) - (7)]),(yyvsp[(6) - (7)]),contextptr),contextptr); 
        }
    break;

  case 130:
#line 517 "input_parser.yy"
    { 
          if ((yyvsp[(8) - (8)]).type==_INT_ && (yyvsp[(8) - (8)]).val && (yyvsp[(8) - (8)]).val!=3) giac_yyerror(scanner,"missing func/prog/proc end delimiter");
          const giac::context * contextptr = giac_yyget_extra(scanner);
           (yyval)=symb_program_sto((yyvsp[(4) - (8)]),zero*(yyvsp[(4) - (8)]),symb_local((yyvsp[(6) - (8)]),(yyvsp[(7) - (8)]),contextptr),(yyvsp[(2) - (8)]),false,contextptr); 
        }
    break;

  case 131:
#line 522 "input_parser.yy"
    { 
          if ((yyvsp[(8) - (8)]).type==_INT_ && (yyvsp[(8) - (8)]).val && (yyvsp[(8) - (8)]).val!=3) giac_yyerror(scanner,"missing func/prog/proc end delimiter");
          const giac::context * contextptr = giac_yyget_extra(scanner);
         (yyval)=symb_program((yyvsp[(3) - (8)]),zero*(yyvsp[(3) - (8)]),symb_local((yyvsp[(5) - (8)]),(yyvsp[(7) - (8)]),contextptr),contextptr); 
        }
    break;

  case 132:
#line 527 "input_parser.yy"
    { 
          if ((yyvsp[(8) - (8)]).type==_INT_ && (yyvsp[(8) - (8)]).val && (yyvsp[(8) - (8)]).val!=3) giac_yyerror(scanner,"missing func/prog/proc end delimiter");
          const giac::context * contextptr = giac_yyget_extra(scanner);
           (yyval)=symb_program_sto((yyvsp[(3) - (8)]),zero*(yyvsp[(3) - (8)]),symb_local((yyvsp[(6) - (8)]),(yyvsp[(7) - (8)]),contextptr),(yyvsp[(1) - (8)]),false,contextptr); 
        }
    break;

  case 133:
#line 532 "input_parser.yy"
    { 
          if ((yyvsp[(9) - (9)]).type==_INT_ && (yyvsp[(9) - (9)]).val && (yyvsp[(9) - (9)]).val!=3) giac_yyerror(scanner,"missing func/prog/proc end delimiter");
          const giac::context * contextptr = giac_yyget_extra(scanner);
           (yyval)=symb_program_sto((yyvsp[(3) - (9)]),zero*(yyvsp[(3) - (9)]),symb_local((yyvsp[(7) - (9)]),(yyvsp[(8) - (9)]),contextptr),(yyvsp[(1) - (9)]),false,contextptr); 
        }
    break;

  case 134:
#line 537 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (9)])._FUNCptr,makevecteur((yyvsp[(3) - (9)]),equaltosame((yyvsp[(5) - (9)])),(yyvsp[(7) - (9)]),symb_bloc((yyvsp[(9) - (9)]))));}
    break;

  case 135:
#line 538 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (10)])._FUNCptr,makevecteur((yyvsp[(3) - (10)]),equaltosame((yyvsp[(5) - (10)])),(yyvsp[(7) - (10)]),(yyvsp[(9) - (10)])));}
    break;

  case 136:
#line 539 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (4)])._FUNCptr,gen2vecteur((yyvsp[(3) - (4)])));}
    break;

  case 137:
#line 540 "input_parser.yy"
    { 
	vecteur v=makevecteur(zero,equaltosame((yyvsp[(3) - (5)])),zero,symb_bloc((yyvsp[(5) - (5)])));
	(yyval)=symbolic(*(yyvsp[(1) - (5)])._FUNCptr,v); 
	}
    break;

  case 138:
#line 544 "input_parser.yy"
    { 
	(yyval)=symbolic(*(yyvsp[(1) - (6)])._FUNCptr,makevecteur(zero,equaltosame((yyvsp[(3) - (6)])),zero,(yyvsp[(5) - (6)]))); 
	}
    break;

  case 139:
#line 547 "input_parser.yy"
    { 
          if ((yyvsp[(5) - (5)]).type==_INT_ && (yyvsp[(5) - (5)]).val && (yyvsp[(5) - (5)]).val!=9 && (yyvsp[(5) - (5)]).val!=8) giac_yyerror(scanner,"missing loop end delimiter");
	  (yyval)=symbolic(*(yyvsp[(1) - (5)])._FUNCptr,makevecteur(zero,equaltosame((yyvsp[(2) - (5)])),zero,symb_bloc((yyvsp[(4) - (5)])))); 
        }
    break;

  case 140:
#line 551 "input_parser.yy"
    { 
          if ((yyvsp[(5) - (5)]).type==_INT_ && (yyvsp[(5) - (5)]).val && (yyvsp[(5) - (5)]).val!=9 && (yyvsp[(5) - (5)]).val!=8) giac_yyerror(scanner,"missing loop end delimiter");
          (yyval)=symbolic(*(yyvsp[(1) - (5)])._FUNCptr,makevecteur(zero,equaltosame((yyvsp[(2) - (5)])),zero,symb_bloc((yyvsp[(4) - (5)])))); 
        }
    break;

  case 141:
#line 555 "input_parser.yy"
    { (yyval)=symb_try_catch(makevecteur(symb_bloc((yyvsp[(2) - (7)])),(yyvsp[(5) - (7)]),symb_bloc((yyvsp[(7) - (7)]))));}
    break;

  case 142:
#line 556 "input_parser.yy"
    {(yyval)=symb_try_catch(gen2vecteur((yyvsp[(3) - (4)])));}
    break;

  case 143:
#line 557 "input_parser.yy"
    {(yyval)=gen(at_try_catch,3);}
    break;

  case 144:
#line 558 "input_parser.yy"
    { (yyval)=symb_case((yyvsp[(3) - (7)]),(yyvsp[(6) - (7)])); }
    break;

  case 145:
#line 559 "input_parser.yy"
    { (yyval) = symb_case((yyvsp[(3) - (4)])); }
    break;

  case 146:
#line 560 "input_parser.yy"
    { (yyval)=symb_case((yyvsp[(2) - (4)]),(yyvsp[(3) - (4)])); }
    break;

  case 147:
#line 561 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (3)]); }
    break;

  case 148:
#line 562 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 149:
#line 563 "input_parser.yy"
    {(yyval) = gen(*(yyvsp[(1) - (2)])._FUNCptr,0);}
    break;

  case 150:
#line 564 "input_parser.yy"
    { (yyval)=symbolic(*(yyvsp[(1) - (3)])._FUNCptr,makevecteur(zero,plus_one,zero,symb_bloc((yyvsp[(2) - (3)])))); }
    break;

  case 151:
#line 565 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (4)])._FUNCptr,makevecteur(equaltosame((yyvsp[(2) - (4)])),(yyvsp[(4) - (4)]),0));}
    break;

  case 152:
#line 566 "input_parser.yy"
    { (yyval)=symb_try_catch(makevecteur(symb_bloc((yyvsp[(2) - (5)])),at_break,symb_bloc((yyvsp[(4) - (5)])))); }
    break;

  case 153:
#line 567 "input_parser.yy"
    { (yyval)=symb_try_catch(makevecteur(symb_bloc((yyvsp[(2) - (4)])),at_break,0)); }
    break;

  case 154:
#line 568 "input_parser.yy"
    { (yyval)=symb_try_catch(makevecteur(symb_bloc((yyvsp[(2) - (6)])),at_break,symb_bloc((yyvsp[(5) - (6)])))); }
    break;

  case 155:
#line 569 "input_parser.yy"
    { (yyval)=symb_try_catch(makevecteur(symb_bloc((yyvsp[(2) - (5)])),at_break,0)); }
    break;

  case 156:
#line 570 "input_parser.yy"
    { vecteur v1(gen2vecteur((yyvsp[(1) - (3)]))),v3(gen2vecteur((yyvsp[(3) - (3)]))); (yyval)=symbolic(at_ti_semi,makevecteur(v1,v3)); }
    break;

  case 157:
#line 571 "input_parser.yy"
    { 
          const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval)=symb_program_sto((yyvsp[(4) - (13)]),(yyvsp[(4) - (13)])*zero,symb_local((yyvsp[(10) - (13)]),mergevecteur(*(yyvsp[(7) - (13)])._VECTptr,*(yyvsp[(12) - (13)])._VECTptr),contextptr),(yyvsp[(2) - (13)]),false,contextptr); 
	}
    break;

  case 158:
#line 575 "input_parser.yy"
    { 
          const giac::context * contextptr = giac_yyget_extra(scanner);
	(yyval)=symb_program_sto((yyvsp[(4) - (12)]),(yyvsp[(4) - (12)])*zero,symb_local((yyvsp[(9) - (12)]),mergevecteur(*(yyvsp[(7) - (12)])._VECTptr,*(yyvsp[(11) - (12)])._VECTptr),contextptr),(yyvsp[(2) - (12)]),false,contextptr); 
	}
    break;

  case 159:
#line 579 "input_parser.yy"
    { 
          const giac::context * contextptr = giac_yyget_extra(scanner);
	(yyval)=symb_program_sto((yyvsp[(4) - (12)]),(yyvsp[(4) - (12)])*zero,symb_local((yyvsp[(9) - (12)]),(yyvsp[(11) - (12)]),contextptr),(yyvsp[(2) - (12)]),false,contextptr); 
	}
    break;

  case 160:
#line 583 "input_parser.yy"
    { 
	(yyval)=symb_program_sto((yyvsp[(4) - (8)]),(yyvsp[(4) - (8)])*zero,symb_bloc((yyvsp[(7) - (8)])),(yyvsp[(2) - (8)]),false,giac_yyget_extra(scanner)); 
	}
    break;

  case 161:
#line 586 "input_parser.yy"
    { (yyval)=symbolic(*(yyvsp[(1) - (3)])._FUNCptr,(yyvsp[(2) - (3)])); }
    break;

  case 162:
#line 587 "input_parser.yy"
    { (yyval)=symbolic(*(yyvsp[(1) - (2)])._FUNCptr,(yyvsp[(2) - (2)])); }
    break;

  case 163:
#line 588 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (2)]); }
    break;

  case 164:
#line 589 "input_parser.yy"
    { (yyval)=symb_program_sto((yyvsp[(4) - (7)]),(yyvsp[(4) - (7)])*zero,(yyvsp[(7) - (7)]),(yyvsp[(2) - (7)]),false,giac_yyget_extra(scanner));}
    break;

  case 165:
#line 590 "input_parser.yy"
    { 
          const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval)=symb_program_sto((yyvsp[(4) - (13)]),(yyvsp[(4) - (13)])*zero,symb_local((yyvsp[(10) - (13)]),(yyvsp[(12) - (13)]),contextptr),(yyvsp[(2) - (13)]),false,contextptr);
        }
    break;

  case 166:
#line 594 "input_parser.yy"
    { (yyval)=symb_program_sto((yyvsp[(4) - (9)]),(yyvsp[(4) - (9)])*zero,symb_bloc((yyvsp[(8) - (9)])),(yyvsp[(2) - (9)]),false,giac_yyget_extra(scanner)); }
    break;

  case 167:
#line 595 "input_parser.yy"
    {
           vecteur & v=*(yyvsp[(2) - (5)])._VECTptr;
           if ( (v.size()<3) || v[0].type!=_IDNT){
             *logptr(giac_yyget_extra(scanner)) << "Syntax For name,begin,end[,step]" << endl;
             (yyval)=undef;
           }
           else {
             gen pas(plus_one);
             if (v.size()==4)
               pas=v[3];
             gen condition;
             if (is_positive(-pas,0))
               condition=symb_superieur_egal(v[0],v[2]);
            else
               condition=symb_inferieur_egal(v[0],v[2]);
            vecteur w=makevecteur(symb_sto(v[1],v[0]),condition,symb_sto(symb_plus(v[0],pas),v[0]),symb_bloc((yyvsp[(4) - (5)])));
             (yyval)=symbolic(*(yyvsp[(1) - (5)])._FUNCptr,w);
           }
	}
    break;

  case 168:
#line 614 "input_parser.yy"
    { 
	vecteur v=makevecteur(zero,equaltosame((yyvsp[(2) - (5)])),zero,symb_bloc((yyvsp[(4) - (5)])));
	(yyval)=symbolic(*(yyvsp[(1) - (5)])._FUNCptr,v); 
	}
    break;

  case 169:
#line 626 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 170:
#line 627 "input_parser.yy"
    { 
	       gen tmp((yyvsp[(3) - (3)])); 
	       // tmp.subtype=1; 
	       (yyval)=symb_check_type(makevecteur(tmp,(yyvsp[(1) - (3)])),context0); 
          }
    break;

  case 171:
#line 632 "input_parser.yy"
    { (yyval)=symb_double_deux_points(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]))); }
    break;

  case 172:
#line 633 "input_parser.yy"
    { (yyval)=symb_double_deux_points(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]))); }
    break;

  case 173:
#line 634 "input_parser.yy"
    { (yyval)=symb_double_deux_points(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]))); }
    break;

  case 174:
#line 635 "input_parser.yy"
    { (yyval)=symb_double_deux_points(makevecteur((yyvsp[(1) - (5)]),(yyvsp[(4) - (5)]))); }
    break;

  case 175:
#line 636 "input_parser.yy"
    { (yyval)=symb_double_deux_points(makevecteur(0,(yyvsp[(2) - (2)]))); }
    break;

  case 176:
#line 637 "input_parser.yy"
    { (yyval)=symb_double_deux_points(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]))); }
    break;

  case 177:
#line 645 "input_parser.yy"
    { 
	  gen tmp((yyvsp[(1) - (2)])); 
	  // tmp.subtype=1; 
	  (yyval)=symb_check_type(makevecteur(tmp,(yyvsp[(2) - (2)])),context0); 
	  }
    break;

  case 178:
#line 650 "input_parser.yy"
    {(yyval)=symbolic(*(yyvsp[(1) - (2)])._FUNCptr,(yyvsp[(2) - (2)])); }
    break;

  case 179:
#line 654 "input_parser.yy"
    { (yyval)=makevecteur(vecteur(0),vecteur(0)); }
    break;

  case 180:
#line 655 "input_parser.yy"
    { vecteur v1 =gen2vecteur((yyvsp[(1) - (2)])); vecteur v2=gen2vecteur((yyvsp[(2) - (2)])); (yyval)=makevecteur(mergevecteur(gen2vecteur(v1[0]),gen2vecteur(v2[0])),mergevecteur(gen2vecteur(v1[1]),gen2vecteur(v2[1]))); }
    break;

  case 181:
#line 656 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (2)]); }
    break;

  case 182:
#line 660 "input_parser.yy"
    { if ((yyvsp[(3) - (4)]).type==_VECT) (yyval)=gen(*(yyvsp[(3) - (4)])._VECTptr,_RPN_STACK__VECT); else (yyval)=gen(vecteur(1,(yyvsp[(3) - (4)])),_RPN_STACK__VECT); }
    break;

  case 183:
#line 661 "input_parser.yy"
    { (yyval)=gen(vecteur(0),_RPN_STACK__VECT); }
    break;

  case 184:
#line 664 "input_parser.yy"
    { if (!(yyvsp[(1) - (3)]).val) (yyval)=makevecteur((yyvsp[(2) - (3)]),vecteur(0)); else (yyval)=makevecteur(vecteur(0),(yyvsp[(2) - (3)]));}
    break;

  case 185:
#line 667 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (3)]); }
    break;

  case 186:
#line 670 "input_parser.yy"
    { (yyval)=gen(vecteur(1,(yyvsp[(1) - (1)])),_SEQ__VECT); }
    break;

  case 187:
#line 671 "input_parser.yy"
    { 
	       vecteur v=*(yyvsp[(1) - (3)])._VECTptr;
	       v.push_back((yyvsp[(3) - (3)]));
	       (yyval)=gen(v,_SEQ__VECT);
	     }
    break;

  case 188:
#line 678 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 189:
#line 679 "input_parser.yy"
    { (yyval)=symb_sto((yyvsp[(3) - (3)]),(yyvsp[(1) - (3)]),(yyvsp[(2) - (3)])==at_array_sto); }
    break;

  case 190:
#line 680 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (3)]); }
    break;

  case 191:
#line 681 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); *logptr(giac_yyget_extra(scanner)) << "Error: reserved word "<< (yyvsp[(1) - (1)]) <<endl;}
    break;

  case 192:
#line 682 "input_parser.yy"
    { (yyval)=symb_double_deux_points(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]))); *logptr(giac_yyget_extra(scanner)) << "Error: reserved word "<< (yyvsp[(1) - (3)]) <<endl; }
    break;

  case 193:
#line 683 "input_parser.yy"
    { 
  const giac::context * contextptr = giac_yyget_extra(scanner);
  (yyval)=string2gen("_"+(yyvsp[(1) - (1)]).print(contextptr),false); 
  if (!giac::first_error_line(contextptr)){
    giac::first_error_line(giac::lexer_line_number(contextptr),contextptr);
    giac:: error_token_name((yyvsp[(1) - (1)]).print(contextptr)+ " (reserved word)",contextptr);
  }
}
    break;

  case 194:
#line 691 "input_parser.yy"
    { 
  const giac::context * contextptr = giac_yyget_extra(scanner);
  (yyval)=string2gen("_"+(yyvsp[(1) - (1)]).print(contextptr),false);
  if (!giac::first_error_line(contextptr)){
    giac::first_error_line(giac::lexer_line_number(contextptr),contextptr);
    giac:: error_token_name((yyvsp[(1) - (1)]).print(contextptr)+ " reserved word",contextptr);
  }
}
    break;

  case 195:
#line 701 "input_parser.yy"
    { (yyval)=plus_one;}
    break;

  case 196:
#line 702 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 197:
#line 705 "input_parser.yy"
    { (yyval)=gen(vecteur(0),_SEQ__VECT); }
    break;

  case 198:
#line 706 "input_parser.yy"
    { (yyval)=makesuite((yyvsp[(1) - (1)])); }
    break;

  case 199:
#line 709 "input_parser.yy"
    { (yyval) = makevecteur((yyvsp[(1) - (1)])); }
    break;

  case 200:
#line 711 "input_parser.yy"
    { vecteur v(1,(yyvsp[(1) - (2)])); 
			  if ((yyvsp[(1) - (2)]).type==_VECT) v=*((yyvsp[(1) - (2)])._VECTptr); 
			  v.push_back((yyvsp[(2) - (2)])); 
			  (yyval) = v;
			}
    break;

  case 201:
#line 716 "input_parser.yy"
    { (yyval) = (yyvsp[(1) - (2)]);}
    break;

  case 202:
#line 719 "input_parser.yy"
    { (yyval)=vecteur(0); }
    break;

  case 203:
#line 720 "input_parser.yy"
    { (yyval)=mergevecteur(vecteur(1,(yyvsp[(1) - (2)])),*((yyvsp[(2) - (2)])._VECTptr));}
    break;

  case 204:
#line 721 "input_parser.yy"
    { (yyval)=mergevecteur(vecteur(1,(yyvsp[(1) - (3)])),*((yyvsp[(3) - (3)])._VECTptr));}
    break;

  case 205:
#line 724 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 206:
#line 794 "input_parser.yy"
    { (yyval)=plus_one; }
    break;

  case 207:
#line 795 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (2)]); }
    break;

  case 208:
#line 798 "input_parser.yy"
    { (yyval)=plus_one; }
    break;

  case 209:
#line 799 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (2)]); }
    break;

  case 210:
#line 800 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (2)]); }
    break;

  case 211:
#line 801 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (2)]); }
    break;

  case 212:
#line 804 "input_parser.yy"
    { (yyval)=plus_one; }
    break;

  case 213:
#line 805 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 214:
#line 808 "input_parser.yy"
    { (yyval)=0; }
    break;

  case 215:
#line 809 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (3)]); }
    break;

  case 216:
#line 810 "input_parser.yy"
    { (yyval)=symb_bloc((yyvsp[(2) - (2)])); }
    break;

  case 217:
#line 814 "input_parser.yy"
    { 
	(yyval) = (yyvsp[(2) - (3)]);
	}
    break;

  case 218:
#line 817 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval) = symb_local((yyvsp[(2) - (4)]),(yyvsp[(3) - (4)]),contextptr);
         }
    break;

  case 219:
#line 824 "input_parser.yy"
    { if ((yyvsp[(1) - (1)]).type==_INT_ && (yyvsp[(1) - (1)]).val && (yyvsp[(1) - (1)]).val!=4) giac_yyerror(scanner,"missing test end delimiter"); (yyval)=0; }
    break;

  case 220:
#line 825 "input_parser.yy"
    {
          if ((yyvsp[(3) - (3)]).type==_INT_ && (yyvsp[(3) - (3)]).val && (yyvsp[(3) - (3)]).val!=4) giac_yyerror(scanner,"missing test end delimiter");
	(yyval)=symb_bloc((yyvsp[(2) - (3)])); 
	}
    break;

  case 221:
#line 829 "input_parser.yy"
    { 
	  (yyval)=symb_ifte(equaltosame((yyvsp[(2) - (5)])),symb_bloc((yyvsp[(4) - (5)])),(yyvsp[(5) - (5)]));
	  }
    break;

  case 222:
#line 832 "input_parser.yy"
    { 
	  (yyval)=symb_ifte(equaltosame((yyvsp[(3) - (6)])),symb_bloc((yyvsp[(5) - (6)])),(yyvsp[(6) - (6)]));
	  }
    break;

  case 223:
#line 837 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 224:
#line 838 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (2)]); }
    break;

  case 225:
#line 841 "input_parser.yy"
    { (yyval)=0; }
    break;

  case 226:
#line 842 "input_parser.yy"
    { (yyval)=0; }
    break;

  case 227:
#line 845 "input_parser.yy"
    { (yyval)=vecteur(0); }
    break;

  case 228:
#line 846 "input_parser.yy"
    { (yyval)=makevecteur(symb_bloc((yyvsp[(3) - (3)])));}
    break;

  case 229:
#line 847 "input_parser.yy"
    { (yyval)=mergevecteur(makevecteur((yyvsp[(2) - (5)]),symb_bloc((yyvsp[(4) - (5)]))),*((yyvsp[(5) - (5)])._VECTptr));}
    break;

  case 230:
#line 850 "input_parser.yy"
    { (yyval)=vecteur(0); }
    break;

  case 231:
#line 851 "input_parser.yy"
    { (yyval)=vecteur(1,symb_bloc((yyvsp[(2) - (2)]))); }
    break;

  case 232:
#line 852 "input_parser.yy"
    { (yyval)=mergevecteur(makevecteur((yyvsp[(2) - (5)]),symb_bloc((yyvsp[(4) - (5)]))),*((yyvsp[(5) - (5)])._VECTptr));}
    break;

  case 233:
#line 855 "input_parser.yy"
    { (yyval)=vecteur(0); }
    break;

  case 234:
#line 856 "input_parser.yy"
    { (yyval)=vecteur(1,symb_bloc((yyvsp[(2) - (2)]))); }
    break;

  case 235:
#line 857 "input_parser.yy"
    { (yyval)=mergevecteur(makevecteur((yyvsp[(2) - (6)]),symb_bloc((yyvsp[(4) - (6)]))),gen2vecteur((yyvsp[(6) - (6)])));}
    break;

  case 236:
#line 858 "input_parser.yy"
    { (yyval)=mergevecteur(makevecteur((yyvsp[(2) - (7)]),symb_bloc((yyvsp[(4) - (7)]))),gen2vecteur((yyvsp[(7) - (7)])));}
    break;

  case 237:
#line 861 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;


/* Line 1267 of yacc.c.  */
#line 6081 "y.tab.c"
      default: break;
    }
  YY_SYMBOL_PRINT ("-> $$ =", yyr1[yyn], &yyval, &yyloc);

  YYPOPSTACK (yylen);
  yylen = 0;
  YY_STACK_PRINT (yyss, yyssp);

  *++yyvsp = yyval;


  /* Now `shift' the result of the reduction.  Determine what state
     that goes to, based on the state we popped back to and the rule
     number reduced by.  */

  yyn = yyr1[yyn];

  yystate = yypgoto[yyn - YYNTOKENS] + *yyssp;
  if (0 <= yystate && yystate <= YYLAST && yycheck[yystate] == *yyssp)
    yystate = yytable[yystate];
  else
    yystate = yydefgoto[yyn - YYNTOKENS];

  goto yynewstate;


/*------------------------------------.
| yyerrlab -- here on detecting error |
`------------------------------------*/
yyerrlab:
  /* If not already recovering from an error, report this error.  */
  if (!yyerrstatus)
    {
      ++yynerrs;
#if ! YYERROR_VERBOSE
      yyerror (scanner, YY_("syntax error"));
#else
      {
	YYSIZE_T yysize = yysyntax_error (0, yystate, yychar);
	if (yymsg_alloc < yysize && yymsg_alloc < YYSTACK_ALLOC_MAXIMUM)
	  {
	    YYSIZE_T yyalloc = 2 * yysize;
	    if (! (yysize <= yyalloc && yyalloc <= YYSTACK_ALLOC_MAXIMUM))
	      yyalloc = YYSTACK_ALLOC_MAXIMUM;
	    if (yymsg != yymsgbuf)
	      YYSTACK_FREE (yymsg);
	    yymsg = (char *) YYSTACK_ALLOC (yyalloc);
	    if (yymsg)
	      yymsg_alloc = yyalloc;
	    else
	      {
		yymsg = yymsgbuf;
		yymsg_alloc = sizeof yymsgbuf;
	      }
	  }

	if (0 < yysize && yysize <= yymsg_alloc)
	  {
	    (void) yysyntax_error (yymsg, yystate, yychar);
	    yyerror (scanner, yymsg);
	  }
	else
	  {
	    yyerror (scanner, YY_("syntax error"));
	    if (yysize != 0)
	      goto yyexhaustedlab;
	  }
      }
#endif
    }



  if (yyerrstatus == 3)
    {
      /* If just tried and failed to reuse look-ahead token after an
	 error, discard it.  */

      if (yychar <= YYEOF)
	{
	  /* Return failure if at end of input.  */
	  if (yychar == YYEOF)
	    YYABORT;
	}
      else
	{
	  yydestruct ("Error: discarding",
		      yytoken, &yylval, scanner);
	  yychar = YYEMPTY;
	}
    }

  /* Else will try to reuse look-ahead token after shifting the error
     token.  */
  goto yyerrlab1;


/*---------------------------------------------------.
| yyerrorlab -- error raised explicitly by YYERROR.  |
`---------------------------------------------------*/
yyerrorlab:

  /* Pacify compilers like GCC when the user code never invokes
     YYERROR and the label yyerrorlab therefore never appears in user
     code.  */
  if (/*CONSTCOND*/ 0)
     goto yyerrorlab;

  /* Do not reclaim the symbols of the rule which action triggered
     this YYERROR.  */
  YYPOPSTACK (yylen);
  yylen = 0;
  YY_STACK_PRINT (yyss, yyssp);
  yystate = *yyssp;
  goto yyerrlab1;


/*-------------------------------------------------------------.
| yyerrlab1 -- common code for both syntax error and YYERROR.  |
`-------------------------------------------------------------*/
yyerrlab1:
  yyerrstatus = 3;	/* Each real token shifted decrements this.  */

  for (;;)
    {
      yyn = yypact[yystate];
      if (yyn != YYPACT_NINF)
	{
	  yyn += YYTERROR;
	  if (0 <= yyn && yyn <= YYLAST && yycheck[yyn] == YYTERROR)
	    {
	      yyn = yytable[yyn];
	      if (0 < yyn)
		break;
	    }
	}

      /* Pop the current state because it cannot handle the error token.  */
      if (yyssp == yyss)
	YYABORT;


      yydestruct ("Error: popping",
		  yystos[yystate], yyvsp, scanner);
      YYPOPSTACK (1);
      yystate = *yyssp;
      YY_STACK_PRINT (yyss, yyssp);
    }

  if (yyn == YYFINAL)
    YYACCEPT;

  *++yyvsp = yylval;


  /* Shift the error token.  */
  YY_SYMBOL_PRINT ("Shifting", yystos[yyn], yyvsp, yylsp);

  yystate = yyn;
  goto yynewstate;


/*-------------------------------------.
| yyacceptlab -- YYACCEPT comes here.  |
`-------------------------------------*/
yyacceptlab:
  yyresult = 0;
  goto yyreturn;

/*-----------------------------------.
| yyabortlab -- YYABORT comes here.  |
`-----------------------------------*/
yyabortlab:
  yyresult = 1;
  goto yyreturn;

#ifndef yyoverflow
/*-------------------------------------------------.
| yyexhaustedlab -- memory exhaustion comes here.  |
`-------------------------------------------------*/
yyexhaustedlab:
  yyerror (scanner, YY_("memory exhausted"));
  yyresult = 2;
  /* Fall through.  */
#endif

yyreturn:
  if (yychar != YYEOF && yychar != YYEMPTY)
     yydestruct ("Cleanup: discarding lookahead",
		 yytoken, &yylval, scanner);
  /* Do not reclaim the symbols of the rule which action triggered
     this YYABORT or YYACCEPT.  */
  YYPOPSTACK (yylen);
  YY_STACK_PRINT (yyss, yyssp);
  while (yyssp != yyss)
    {
      yydestruct ("Cleanup: popping",
		  yystos[*yyssp], yyvsp, scanner);
      YYPOPSTACK (1);
    }
#ifndef yyoverflow
  if (yyss != yyssa)
    YYSTACK_FREE (yyss);
#endif
#if YYERROR_VERBOSE
  if (yymsg != yymsgbuf)
    YYSTACK_FREE (yymsg);
#endif
  /* Make sure YYID is used.  */
  return YYID (yyresult);
}


#line 868 "input_parser.yy"


#ifndef NO_NAMESPACE_GIAC
} // namespace giac


#endif // ndef NO_NAMESPACE_GIAC
int giac_yyget_column  (yyscan_t yyscanner);

// Error print routine (store error string in parser_error)
int giac_yyerror(yyscan_t scanner,const char *s)
{
  const giac::context * contextptr = giac_yyget_extra(scanner);
  int col= giac_yyget_column(scanner);
  giac::lexer_column_number(contextptr)=col;
  if ( (*giac_yyget_text( scanner )) && (giac_yyget_text( scanner )[0]!=-61) && (giac_yyget_text( scanner )[1]!=-65)){
    std::string txt=giac_yyget_text( scanner );
    parser_error( ":" + giac::print_INT_(giac::lexer_line_number(contextptr)) + ": " + string(s) + " line " + giac::print_INT_(giac::lexer_line_number(contextptr)) + " col " + giac::print_INT_(col) + " at " + txt +"\n",contextptr);
     giac::parsed_gen(giac::string2gen(txt,false),contextptr);
  }
  else {
    parser_error(":" + giac::print_INT_(giac::lexer_line_number(contextptr)) + ": " +string(s) + " at end of input\n",contextptr);
    giac::parsed_gen(giac::undef,contextptr);
  }
  if (!giac::first_error_line(contextptr)){
    giac::first_error_line(giac::lexer_line_number(contextptr),contextptr);
    std::string s=string(giac_yyget_text( scanner ));
    if (s.size()==2 && s[0]==-61 && s[1]==-65)
      s="end of input";
    giac:: error_token_name(s,contextptr);
  }
  return giac::lexer_line_number(contextptr);
}

