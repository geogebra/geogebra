/* A Bison parser, made by GNU Bison 2.5.  */

/* Bison implementation for Yacc-like parsers in C
   
      Copyright (C) 1984, 1989-1990, 2000-2011 Free Software Foundation, Inc.
   
   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   
   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

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
#define YYBISON_VERSION "2.5"

/* Skeleton name.  */
#define YYSKELETON_NAME "yacc.c"

/* Pure parsers.  */
#define YYPURE 1

/* Push parsers.  */
#define YYPUSH 0

/* Pull parsers.  */
#define YYPULL 1

/* Using locations.  */
#define YYLSP_NEEDED 0

/* Substitute the variable and function names.  */
#define yyparse         giac_yyparse
#define yylex           giac_yylex
#define yyerror         giac_yyerror
#define yylval          giac_yylval
#define yychar          giac_yychar
#define yydebug         giac_yydebug
#define yynerrs         giac_yynerrs


/* Copy the first part of user declarations.  */

/* Line 268 of yacc.c  */
#line 24 "input_parser.yy"

         #define YYPARSE_PARAM scanner
         #define YYLEX_PARAM   scanner
	 
/* Line 268 of yacc.c  */
#line 33 "input_parser.yy"

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#include "first.h"
#include <stdexcept>
#include <cstdlib>
#include "giacPCH.h"
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
#if defined RTOS_THREADX || defined NSPIRE
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
#define YYINITDEPTH 1000
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


/* Line 268 of yacc.c  */
#line 160 "y.tab.c"

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
     T_FUNCTION = 389,
     T_IMPMULT = 390
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
#define T_IMPMULT 390




#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef int YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
#endif


/* Copy the second part of user declarations.  */


/* Line 343 of yacc.c  */
#line 472 "y.tab.c"

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
YYID (int yyi)
#else
static int
YYID (yyi)
    int yyi;
#endif
{
  return yyi;
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
#    if ! defined _ALLOCA_H && ! defined EXIT_SUCCESS && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
#     include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
#     ifndef EXIT_SUCCESS
#      define EXIT_SUCCESS 0
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
#  if (defined __cplusplus && ! defined EXIT_SUCCESS \
       && ! ((defined YYMALLOC || defined malloc) \
	     && (defined YYFREE || defined free)))
#   include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
#   ifndef EXIT_SUCCESS
#    define EXIT_SUCCESS 0
#   endif
#  endif
#  ifndef YYMALLOC
#   define YYMALLOC malloc
#   if ! defined malloc && ! defined EXIT_SUCCESS && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
void *malloc (YYSIZE_T); /* INFRINGES ON USER NAME SPACE */
#   endif
#  endif
#  ifndef YYFREE
#   define YYFREE free
#   if ! defined free && ! defined EXIT_SUCCESS && (defined __STDC__ || defined __C99__FUNC__ \
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
  yytype_int16 yyss_alloc;
  YYSTYPE yyvs_alloc;
};

/* The size of the maximum gap between one aligned stack and the next.  */
# define YYSTACK_GAP_MAXIMUM (sizeof (union yyalloc) - 1)

/* The size of an array large to enough to hold all stacks, each with
   N elements.  */
# define YYSTACK_BYTES(N) \
     ((N) * (sizeof (yytype_int16) + sizeof (YYSTYPE)) \
      + YYSTACK_GAP_MAXIMUM)

# define YYCOPY_NEEDED 1

/* Relocate STACK from its old location to the new one.  The
   local variables YYSIZE and YYSTACKSIZE give the old and new number of
   elements in the stack, and YYPTR gives the new location of the
   stack.  Advance YYPTR to a properly aligned location for the next
   stack.  */
# define YYSTACK_RELOCATE(Stack_alloc, Stack)				\
    do									\
      {									\
	YYSIZE_T yynewbytes;						\
	YYCOPY (&yyptr->Stack_alloc, Stack, yysize);			\
	Stack = &yyptr->Stack_alloc;					\
	yynewbytes = yystacksize * sizeof (*Stack) + YYSTACK_GAP_MAXIMUM; \
	yyptr += yynewbytes / sizeof (*yyptr);				\
      }									\
    while (YYID (0))

#endif

#if defined YYCOPY_NEEDED && YYCOPY_NEEDED
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
#endif /* !YYCOPY_NEEDED */

/* YYFINAL -- State number of the termination state.  */
#define YYFINAL  147
/* YYLAST -- Last index in YYTABLE.  */
#define YYLAST   12971

/* YYNTOKENS -- Number of terminals.  */
#define YYNTOKENS  136
/* YYNNTS -- Number of nonterminals.  */
#define YYNNTS  28
/* YYNRULES -- Number of rules.  */
#define YYNRULES  244
/* YYNRULES -- Number of states.  */
#define YYNSTATES  576

/* YYTRANSLATE(YYLEX) -- Bison symbol number corresponding to YYLEX.  */
#define YYUNDEFTOK  2
#define YYMAXUTOK   390

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
     125,   126,   127,   128,   129,   130,   131,   132,   133,   134,
     135
};

#if YYDEBUG
/* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
   YYRHS.  */
static const yytype_uint16 yyprhs[] =
{
       0,     0,     3,     5,     8,    12,    16,    18,    21,    26,
      30,    33,    39,    47,    49,    51,    58,    65,    72,    79,
      88,    92,    96,   100,   104,   108,   112,   116,   121,   126,
     131,   133,   135,   137,   141,   146,   150,   154,   158,   164,
     168,   171,   175,   179,   183,   187,   191,   195,   199,   203,
     207,   211,   214,   217,   220,   226,   230,   232,   236,   239,
     244,   249,   251,   256,   261,   266,   270,   272,   275,   278,
     285,   290,   296,   301,   303,   308,   310,   314,   318,   323,
     325,   328,   330,   334,   336,   338,   346,   356,   366,   374,
     384,   386,   391,   397,   405,   409,   411,   415,   418,   424,
     428,   432,   435,   439,   443,   447,   451,   455,   457,   461,
     466,   473,   480,   484,   488,   492,   494,   497,   501,   504,
     508,   511,   513,   515,   518,   520,   524,   529,   531,   538,
     546,   550,   552,   557,   565,   574,   584,   593,   602,   612,
     622,   633,   638,   644,   651,   657,   663,   671,   676,   678,
     686,   691,   696,   700,   702,   705,   709,   714,   720,   725,
     732,   738,   742,   756,   769,   782,   791,   795,   798,   801,
     809,   823,   833,   839,   845,   847,   851,   855,   859,   863,
     869,   872,   876,   879,   882,   883,   886,   889,   894,   897,
     901,   905,   907,   911,   913,   917,   921,   925,   929,   931,
     935,   937,   939,   940,   942,   943,   945,   947,   950,   953,
     954,   957,   961,   963,   964,   967,   968,   971,   974,   977,
     979,   981,   982,   986,   989,   993,   998,  1000,  1004,  1010,
    1017,  1019,  1022,  1024,  1027,  1028,  1032,  1038,  1039,  1042,
    1048,  1049,  1052,  1059,  1067
};

/* YYRHS -- A `-1'-separated list of the rules' RHS.  */
static const yytype_int16 yyrhs[] =
{
     137,     0,    -1,   138,    -1,   139,     8,    -1,   139,    44,
       8,    -1,   139,    44,   138,    -1,     3,    -1,     3,     4,
      -1,     3,     4,    24,     3,    -1,     3,     4,   128,    -1,
       3,     5,    -1,     3,    10,    17,   139,    18,    -1,     3,
      10,    17,   139,    18,    24,     3,    -1,     7,    -1,     9,
      -1,   140,    17,   148,    18,    15,   156,    -1,   140,    17,
     148,    18,    15,   139,    -1,   139,   114,   140,    17,   148,
      18,    -1,   139,   114,   140,    38,   139,    41,    -1,   139,
     114,   140,    38,    40,   139,    41,    41,    -1,   139,   114,
     140,    -1,   139,   114,    10,    -1,   139,   114,    19,    -1,
     139,   114,    21,    -1,   139,   114,    22,    -1,   139,   114,
      14,    -1,   139,   114,   114,    -1,   139,   114,   125,   139,
      -1,   140,    17,   148,    18,    -1,   139,    17,   148,    18,
      -1,   140,    -1,     5,    -1,     6,    -1,     6,    15,   139,
      -1,     6,    17,   139,    18,    -1,     6,    17,    18,    -1,
     139,   114,     6,    -1,   139,    28,   139,    -1,   139,    28,
     140,    28,   139,    -1,   139,    29,   139,    -1,    29,   139,
      -1,   139,    19,   139,    -1,   139,    20,   139,    -1,   139,
     131,   139,    -1,   139,    21,   139,    -1,   139,    22,   139,
      -1,   139,    24,   139,    -1,   139,    23,   139,    -1,   139,
      30,   139,    -1,   139,    34,   139,    -1,   139,    45,   139,
      -1,    20,   139,    -1,   132,   139,    -1,    19,   139,    -1,
     100,   139,    14,   139,   101,    -1,    98,   139,    99,    -1,
      11,    -1,   139,    15,   139,    -1,    12,   139,    -1,    84,
      17,   139,    18,    -1,    84,    38,   139,    41,    -1,    84,
      -1,    10,    17,   139,    18,    -1,   133,    17,   139,    18,
      -1,    10,    38,   139,    41,    -1,    10,    17,    18,    -1,
      10,    -1,   139,    27,    -1,   139,    85,    -1,    47,   139,
      50,   156,    51,   156,    -1,    47,   139,    50,   156,    -1,
      47,   139,    50,   149,   157,    -1,    52,    17,   139,    18,
      -1,    52,    -1,    82,    17,   139,    18,    -1,    82,    -1,
     139,    16,   156,    -1,   139,    16,   139,    -1,    75,    17,
     139,    18,    -1,    75,    -1,    78,   139,    -1,    78,    -1,
      26,    78,    26,    -1,    69,    -1,    70,    -1,    57,   140,
      67,   139,    60,   149,    77,    -1,    57,   140,   153,    59,
     139,   152,   154,   149,    77,    -1,    57,   140,   153,   152,
      59,   139,    60,   149,    77,    -1,    57,   140,   153,   152,
      60,   149,    77,    -1,    57,   140,   153,   152,    63,   139,
      60,   149,    77,    -1,    57,    -1,    65,   149,    66,   139,
      -1,    65,   149,    66,   139,    77,    -1,   130,   149,    50,
     149,    51,   149,    77,    -1,   129,   162,    77,    -1,    13,
      -1,    26,    13,    26,    -1,    37,   139,    -1,   139,    37,
     140,    67,   139,    -1,   139,    37,   139,    -1,   139,    36,
     139,    -1,    36,     4,    -1,   139,    35,   139,    -1,   139,
      31,   139,    -1,   139,    32,   139,    -1,   139,    33,   139,
      -1,   139,   117,   139,    -1,    25,    -1,    26,   139,    26,
      -1,   139,    38,   139,    41,    -1,   139,    38,    40,   139,
      41,    41,    -1,    17,   139,    18,    17,   148,    18,    -1,
      17,   139,    18,    -1,    40,   148,    41,    -1,   139,    14,
     139,    -1,    83,    -1,   108,   139,    -1,   139,   124,   139,
      -1,   125,   139,    -1,   139,   125,   139,    -1,   139,   128,
      -1,     1,    -1,   142,    -1,   127,   139,    -1,   127,    -1,
     127,    17,    18,    -1,    80,    17,   139,    18,    -1,    80,
      -1,    47,    17,   139,    18,   156,   155,    -1,    47,    17,
     139,    18,   139,    44,   155,    -1,    87,   150,    88,    -1,
     123,    -1,   123,    38,   139,    41,    -1,    74,    17,   148,
      18,   141,   149,    77,    -1,    74,   140,    17,   148,    18,
     141,   149,    77,    -1,    74,   140,    17,   148,    18,    76,
     141,   149,    77,    -1,    74,    17,   148,    18,   141,    76,
     149,    77,    -1,   140,    17,   148,    18,    74,   141,   149,
      77,    -1,   140,    17,   148,    18,    15,    74,   141,   149,
      77,    -1,    57,    17,   147,    44,   147,    44,   147,    18,
     156,    -1,    57,    17,   147,    44,   147,    44,   147,    18,
     139,    44,    -1,    57,    17,   139,    18,    -1,    62,    17,
     139,    18,   156,    -1,    62,    17,   139,    18,   139,    44,
      -1,    62,   139,    60,   149,    77,    -1,    63,   139,    60,
     149,    77,    -1,    71,   156,    72,    17,   139,    18,   156,
      -1,    73,    17,   139,    18,    -1,    73,    -1,    53,    17,
     139,    18,    76,   160,    77,    -1,    54,    17,     4,    18,
      -1,    54,   139,   161,    56,    -1,   122,   151,   122,    -1,
      86,    -1,    78,   109,    -1,   111,   149,   158,    -1,    47,
     139,   109,   139,    -1,   115,   149,    51,   149,   158,    -1,
     115,   149,    51,   158,    -1,   115,   149,   109,    51,   149,
     158,    -1,   115,   149,   109,    51,   158,    -1,   139,   120,
     139,    -1,   109,   140,    17,   148,    18,   119,   149,   109,
     110,   148,   109,   149,   158,    -1,   109,   140,    17,   148,
      18,   119,   149,   110,   148,   109,   149,   158,    -1,   109,
     140,    17,   148,    18,   119,   109,   110,   148,   109,   149,
     158,    -1,   109,   140,    17,   148,    18,   119,   149,   158,
      -1,   116,   149,   158,    -1,   116,   156,    -1,   109,   139,
      -1,   118,   140,    17,   148,    18,    29,   139,    -1,   118,
     140,    17,   148,    18,    29,   119,   109,   110,   148,   109,
     149,   158,    -1,   118,   140,    17,   148,    18,    29,   119,
     149,   158,    -1,   112,   148,   109,   149,   158,    -1,   113,
     139,   109,   149,   158,    -1,     4,    -1,     4,    46,    13,
      -1,     4,    46,    10,    -1,     4,    46,     4,    -1,     4,
      46,   133,    -1,     4,    46,    26,   139,    26,    -1,    46,
       4,    -1,     3,    46,     4,    -1,    13,     4,    -1,   121,
     139,    -1,    -1,   141,   143,    -1,   144,   141,    -1,    89,
      17,   139,    18,    -1,    89,    83,    -1,    79,   145,    44,
      -1,    81,   139,    44,    -1,   146,    -1,   145,    14,   146,
      -1,   140,    -1,     4,    15,   139,    -1,     4,    29,   139,
      -1,     4,    45,   139,    -1,    17,   146,    18,    -1,    10,
      -1,    10,    46,   139,    -1,    13,    -1,     3,    -1,    -1,
     139,    -1,    -1,   139,    -1,   139,    -1,   149,   139,    -1,
     149,   163,    -1,    -1,   151,   150,    -1,   151,    14,   150,
      -1,    10,    -1,    -1,    61,   139,    -1,    -1,    15,   139,
      -1,    29,   139,    -1,    58,   139,    -1,    44,    -1,    60,
      -1,    -1,   159,   139,    44,    -1,   159,   156,    -1,    76,
     149,    77,    -1,    76,   141,   149,    77,    -1,   158,    -1,
     159,   149,   158,    -1,    49,   139,    50,   149,   157,    -1,
     109,    49,   139,    50,   149,   157,    -1,    77,    -1,   109,
      77,    -1,    51,    -1,   109,    51,    -1,    -1,    55,    45,
     156,    -1,    54,     3,    45,   156,   160,    -1,    -1,    55,
     149,    -1,    11,     3,    60,   149,   161,    -1,    -1,    55,
     149,    -1,    47,   139,    50,   149,    77,   162,    -1,    47,
     139,    50,   149,    77,    44,   162,    -1,    44,    -1
};

/* YYRLINE[YYN] -- source line where rule number YYN was defined.  */
static const yytype_uint16 yyrline[] =
{
       0,   187,   187,   195,   196,   197,   200,   201,   202,   203,
     204,   205,   206,   208,   209,   212,   213,   214,   215,   219,
     223,   224,   225,   226,   227,   228,   229,   230,   231,   232,
     233,   234,   235,   236,   237,   238,   239,   240,   241,   242,
     243,   249,   251,   252,   253,   254,   255,   256,   257,   260,
     261,   262,   267,   272,   278,   279,   285,   286,   287,   288,
     289,   290,   291,   301,   306,   310,   317,   320,   321,   322,
     323,   324,   327,   328,   329,   330,   331,   335,   342,   343,
     345,   346,   347,   349,   350,   351,   369,   382,   395,   399,
     403,   408,   413,   419,   423,   424,   428,   429,   430,   431,
     432,   433,   434,   435,   436,   437,   438,   441,   442,   447,
     451,   455,   456,   473,   482,   488,   489,   490,   491,   496,
     500,   501,   510,   511,   512,   513,   514,   518,   519,   522,
     527,   528,   529,   530,   535,   540,   545,   550,   555,   560,
     561,   562,   563,   567,   570,   574,   578,   579,   580,   581,
     582,   583,   584,   585,   586,   587,   588,   589,   590,   591,
     592,   593,   594,   598,   602,   606,   609,   610,   611,   612,
     613,   617,   618,   637,   649,   650,   655,   656,   657,   658,
     659,   660,   668,   673,   677,   678,   679,   683,   684,   687,
     690,   693,   694,   701,   702,   703,   704,   705,   706,   707,
     708,   716,   726,   727,   730,   731,   734,   736,   741,   744,
     745,   746,   749,   819,   820,   823,   824,   825,   826,   829,
     830,   833,   834,   835,   839,   842,   849,   850,   854,   857,
     862,   863,   866,   867,   870,   871,   872,   875,   876,   877,
     880,   881,   882,   883,   886
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
  "T_MOINS38", "T_NEG38", "T_UNARY_OP_38", "T_FUNCTION", "T_IMPMULT",
  "$accept", "input", "correct_input", "exp", "symbol", "entete", "stack",
  "local", "nom", "suite_symbol", "affectable_symbol", "exp_or_empty",
  "suite", "prg_suite", "rpn_suite", "rpn_token", "step", "from",
  "loop38_do", "else", "bloc", "elif", "ti_bloc_end", "ti_else", "switch",
  "case", "case38", "semi", 0
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
     385,   386,   387,   388,   389,   390
};
# endif

/* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
static const yytype_uint8 yyr1[] =
{
       0,   136,   137,   138,   138,   138,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   139,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   140,   140,   140,   140,   140,   140,
     140,   140,   140,   140,   141,   141,   141,   142,   142,   143,
     144,   145,   145,   146,   146,   146,   146,   146,   146,   146,
     146,   146,   147,   147,   148,   148,   149,   149,   149,   150,
     150,   150,   151,   152,   152,   153,   153,   153,   153,   154,
     154,   155,   155,   155,   156,   156,   157,   157,   157,   157,
     158,   158,   159,   159,   160,   160,   160,   161,   161,   161,
     162,   162,   162,   162,   163
};

/* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
static const yytype_uint8 yyr2[] =
{
       0,     2,     1,     2,     3,     3,     1,     2,     4,     3,
       2,     5,     7,     1,     1,     6,     6,     6,     6,     8,
       3,     3,     3,     3,     3,     3,     3,     4,     4,     4,
       1,     1,     1,     3,     4,     3,     3,     3,     5,     3,
       2,     3,     3,     3,     3,     3,     3,     3,     3,     3,
       3,     2,     2,     2,     5,     3,     1,     3,     2,     4,
       4,     1,     4,     4,     4,     3,     1,     2,     2,     6,
       4,     5,     4,     1,     4,     1,     3,     3,     4,     1,
       2,     1,     3,     1,     1,     7,     9,     9,     7,     9,
       1,     4,     5,     7,     3,     1,     3,     2,     5,     3,
       3,     2,     3,     3,     3,     3,     3,     1,     3,     4,
       6,     6,     3,     3,     3,     1,     2,     3,     2,     3,
       2,     1,     1,     2,     1,     3,     4,     1,     6,     7,
       3,     1,     4,     7,     8,     9,     8,     8,     9,     9,
      10,     4,     5,     6,     5,     5,     7,     4,     1,     7,
       4,     4,     3,     1,     2,     3,     4,     5,     4,     6,
       5,     3,    13,    12,    12,     8,     3,     2,     2,     7,
      13,     9,     5,     5,     1,     3,     3,     3,     3,     5,
       2,     3,     2,     2,     0,     2,     2,     4,     2,     3,
       3,     1,     3,     1,     3,     3,     3,     3,     1,     3,
       1,     1,     0,     1,     0,     1,     1,     2,     2,     0,
       2,     3,     1,     0,     2,     0,     2,     2,     2,     1,
       1,     0,     3,     2,     3,     4,     1,     3,     5,     6,
       1,     2,     1,     2,     0,     3,     5,     0,     2,     5,
       0,     2,     6,     7,     1
};

/* YYDEFACT[STATE-NAME] -- Default reduction number in state STATE-NUM.
   Performed when YYTABLE doesn't specify something else to do.  Zero
   means the default is an error.  */
static const yytype_uint8 yydefact[] =
{
       0,   121,     6,   174,    31,    32,    13,    14,    66,    56,
       0,    95,     0,     0,     0,   107,     0,     0,     0,     0,
       0,     0,     0,    73,     0,     0,    90,     0,     0,     0,
      83,    84,     0,   148,     0,    79,     0,   127,    75,   115,
      61,   153,   209,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,   131,     0,     0,   240,
       0,     0,     0,     0,     2,     0,    30,   122,     7,    10,
       0,     0,     0,     0,     0,     0,     0,    58,   182,     0,
      53,    51,    95,     0,     0,    40,   101,    97,   205,     0,
     180,     0,     0,     0,     0,     0,   237,     0,     0,     0,
     215,     0,     0,     0,   206,     0,     0,     0,     0,     0,
       0,     0,     0,    80,     0,     0,     0,     0,   212,     0,
     209,     0,   188,     0,     0,   116,   168,    30,     0,     0,
       0,     0,     0,   167,     0,   183,     0,     0,   118,     0,
     123,     0,     0,     0,     0,    52,     0,     1,     3,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,    67,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,    68,     0,     0,     0,     0,     0,   120,
       0,     0,     0,     9,     0,   181,   177,   176,   175,     0,
     178,    33,    35,     0,    65,     0,     0,   112,    96,     0,
     108,   113,     0,     0,     0,     0,     0,   174,     0,     0,
       0,   203,     0,     0,     0,     0,     0,   213,     0,     0,
       0,   244,     0,   207,   208,     0,     0,   184,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,   130,   209,
     210,     0,    55,     0,     0,   230,     0,   155,     0,     0,
       0,     0,   166,     0,   152,     0,   125,     0,     0,    94,
       0,     0,   114,    57,    77,    76,     0,    41,    42,    44,
      45,    47,    46,    37,    30,    39,    48,   103,   104,   105,
      49,   102,   100,    99,    30,     0,     0,     4,     5,    50,
      36,    21,    25,    22,    23,    24,    26,     0,    20,   106,
     161,   117,   119,    43,     0,     8,     0,     0,    34,    62,
      64,     0,     0,     0,    70,   156,    72,     0,   150,     0,
       0,   151,   141,     0,   216,   217,   218,     0,     0,     0,
       0,     0,     0,     0,    91,     0,     0,   185,     0,   186,
     224,     0,   147,   184,     0,    78,   126,    74,    59,    60,
     211,   187,   114,     0,   231,     0,     0,     0,   158,     0,
       0,   132,     0,     0,    63,    29,     0,     0,     0,   109,
      27,     0,     0,    28,    11,   179,     0,     0,     0,   221,
       0,   232,     0,    71,   226,     0,     0,   234,     0,   203,
       0,     0,   213,   214,     0,     0,     0,     0,   142,   144,
     145,    92,   190,   201,   174,   198,   200,     0,   193,     0,
     191,   225,     0,     0,   184,    54,    28,   172,   173,   157,
       0,   160,     0,     0,     0,    38,    98,     0,     0,     0,
       0,     0,   184,     0,   111,     0,   221,     0,   128,     0,
       0,     0,   233,     0,    69,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,   143,     0,     0,     0,     0,
       0,     0,   189,     0,     0,     0,   184,     0,     0,   159,
       0,   240,     0,   110,    17,     0,    18,   184,    16,    15,
       0,    12,   129,     0,   223,     0,     0,   227,     0,     0,
     149,    56,   239,     0,    85,   219,   220,     0,     0,    88,
       0,   194,   195,   196,   199,   197,   192,   146,     0,   133,
       0,     0,     0,     0,     0,   169,   240,   242,    93,     0,
       0,     0,   222,     0,     0,     0,   235,     0,     0,     0,
       0,   136,     0,   134,     0,     0,     0,   165,     0,     0,
     243,    19,     0,   137,   228,     0,   234,     0,   139,    86,
      87,    89,   135,     0,     0,     0,     0,   171,   138,   229,
     236,   140,     0,     0,     0,     0,     0,     0,     0,     0,
     164,     0,   163,     0,   162,   170
};

/* YYDEFGOTO[NTERM-NUM].  */
static const yytype_int16 yydefgoto[] =
{
      -1,    63,    64,   104,    66,   226,    67,   337,   227,   409,
     410,   212,    89,   105,   119,   120,   330,   217,   497,   438,
     107,   383,   384,   385,   447,   210,   143,   224
};

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
#define YYPACT_NINF -486
static const yytype_int16 yypact[] =
{
    8100,  -486,   269,   -13,  -486,   109,  -486,  -486,    54,  -486,
    8100,    74,  8100,  8100,  8100,  -486,  8233,  8100,    82,  8100,
    5839,    92,  8366,   103,   117,  8499,    64,  8632,  8100,  8100,
    -486,  -486,    60,   126,   183,   166,   918,   177,   180,  -486,
      62,  -486,   138,   -14,  8100,  8100,  8100,  8100,  8100,  8100,
    8100,  8100,  5972,    84,  8100,   138,   160,  8100,  1051,   -21,
    8100,  8100,   184,   202,  -486,  9417,   190,  -486,   -22,  -486,
     201,   215,     5,  8100,  6105,  6238,  8100,   398,  -486,  9608,
     398,   398,    33,  1583,  9635, 11419,  -486, 10601, 12330,   179,
    -486,  8100,  9490,  8100,  8100,  8765,  9453,   181,    74,  6371,
      75,  8100,  9753,  9787, 12330,  2248,  2381,   154,  8100,  6504,
     213,  8100,  1184, 12330,  8100,  8100,  8100,  8100,  -486,   159,
      56,  8100,  -486,  9905, 12448, 12330, 12330,   225,  2514,   139,
    9932,  2647,  2514,  -486,   238,    65,   143,  8100, 10177,  6637,
   11419,  8100,  8100,   195,  2780,   398,  8100,  -486,  -486,  8100,
    8100,  5972,  6504,  8100,  8100,  8100,  8100,  8100,  8100,  -486,
    8100,  8100,  8100,  8100,  8100,  8100,  8100,  8100,  8100,  8100,
    8898,  6770,  8100,  -486,   608,  8100,  8100,  8100,  8100,  -486,
    8100,  6504,   254,  -486,  8100,  -486,  -486,  -486,  -486,  8100,
    -486, 12508,  -486,  9965,  -486, 10083, 10110,   243,  -486,   785,
    -486,  -486, 10143,  5972,  8100, 10261, 10294,    17,   258,  8100,
     211, 10335,   232,  8100,  8100,  8100,  8100,   147, 10453,  8100,
    8100,  -486,  8100, 12330,  -486,  8100,  6903,   196,  2913,   265,
   10486,   266,  6504, 10527, 10645, 10678, 10719, 10837,  -486,   138,
    -486, 10870,  -486,  8100,  6504,  -486,  7036,  -486,  8100,  8100,
    7169,  7302,  -486,  6504,  -486, 10897,  -486, 11015,  3046,  -486,
    8100, 11052,  9668, 12508, 12692,  -486,   268,   480,   480,   283,
   10215,   656,   186, 12769,   101, 10601,   214, 12789, 12840, 12811,
   10409,    65,  1723, 10601,    35,  5839, 11089,  -486,  -486, 11419,
    -486,  -486,  -486,  -486,  -486,  -486,  -486,  8100,    81, 12475,
     629, 11267, 10177,   480,   270,  -486, 11207, 11234,  -486,  -486,
    -486,  6504,  1317,  1716,   236, 12330,  -486,   207,  -486,   230,
    3179,  -486,  -486,  6371, 12330, 12330, 12330, 11352,  8100,  8100,
     132,  1450,  3312,  3445, 11386, 11504,    38,  -486,  3578,   212,
    -486,  8100,  -486,   196,   274,  -486,  -486,  -486,  -486,  -486,
    -486,  -486, 12656,   275,  -486,  2514,  2514,  2514,  -486,  7169,
     276,  -486,  8100,  3711,  -486,  -486,  8100,  8100, 11537,  -486,
   10177,  6504,  9031,   -10,   271,  -486,   278,  6504, 11571,   -44,
    8100,  -486,  1849,  -486,  -486,  8100,    60,   119,  8100, 12330,
     257,  8100, 11689, 12330,  8100,  8100,  8100, 11725,  -486,  -486,
    -486,  -486,  -486,   181,   194,   252,    74,    38,  -486,     9,
    -486,  -486, 11762,  3844,    59,  -486,     6,  -486,  -486,  -486,
    2514,  -486,   273,  3977,  8100, 12769, 12330,   262,   288,  5839,
   11880,  7435,   196,   305,  -486,  9608,   -44,   279,  -486,  5972,
   11913,  8100,  -486,  2514,  -486,   306,   272,   239,  1982,  7568,
    4110,   -20, 11954,  4243, 12072,  -486,  8100,  8100,  8100,  8100,
     304,    38,  -486,    60,  8100,  4376,   196,  6903,  9164,  -486,
    7701,   135,  4509,  -486,  -486, 12106,  -486,    26, 12508,  -486,
    6903,  -486,  -486, 12139,  -486,  8100, 12257,  -486,   280,    60,
    -486,   258,  -486,   308,  -486,  -486,  -486,  8100,  8100,  -486,
    8100, 12330, 12625, 12625, 12625,  -486,  -486,  -486,  4642,  -486,
    6903,  4775,  7834,  2115,  9297, 10601,   -21,  -486,  -486,   286,
    6903,  4908,  -486,  1716,  8100,    60,  -486,  5972,  5041,  5174,
    5307,  -486,  5440,  -486,  8100,  5573,  8100,  -486,  7967,  2514,
    -486,  -486,  5706,  -486,  -486,  1716,   119, 12294,  -486,  -486,
    -486,  -486,  -486,   226,  8100,   227,  8100,  -486,  -486,  -486,
    -486,  -486,  8100,   234,  8100,   235,  2514,  8100,  2514,  8100,
    -486,  2514,  -486,  2514,  -486,  -486
};

/* YYPGOTO[NTERM-NUM].  */
static const yytype_int16 yypgoto[] =
{
    -486,  -486,   163,     0,   228,  -221,  -486,  -486,  -486,  -486,
    -399,  -322,    36,   218,  -116,   282,   -46,  -486,  -486,   -88,
      61,  -485,   131,  -347,  -197,   -98,  -460,  -486
};

/* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule which
   number is the opposite.  If YYTABLE_NINF, syntax error.  */
#define YYTABLE_NINF -242
static const yytype_int16 yytable[] =
{
      65,   390,   182,   121,   240,   431,   339,   381,   460,   186,
      77,   517,    79,    80,    81,   187,    84,    85,   188,    87,
      88,   431,    92,   461,   495,    96,   141,   102,   103,    97,
       3,   189,   439,    72,   142,   318,   113,    78,   544,    98,
     496,   403,   404,   109,   123,   124,   125,   126,   405,    88,
     130,   406,   181,   462,   135,   407,   540,   138,   140,   198,
     559,   145,   506,    72,   432,   437,   118,    97,     3,   122,
     239,    75,    21,   191,   193,   195,   196,    98,    78,   116,
     432,    99,   152,   113,    21,   129,    86,    97,     3,   439,
     213,   202,    76,   205,   206,    79,    90,    98,   371,   211,
     117,   218,   367,   170,   214,   223,   183,   225,   230,    88,
      21,   233,   126,   133,   234,   235,   236,   237,   181,   372,
      93,   241,   413,   350,    73,   468,    74,   493,   223,   366,
      21,   223,   223,   215,    94,   466,   106,   255,   190,    79,
     225,   257,   216,   108,   223,   231,   261,    54,   118,   262,
     263,   264,    88,   267,   268,   269,   270,   271,   272,    54,
     273,   275,   276,   277,   278,   279,   280,   281,   282,   283,
     286,    65,   289,   445,   446,   299,   300,   301,   302,   516,
     303,    88,   141,   111,   306,    54,    97,     3,   266,   307,
     142,   394,   395,   467,   114,   396,    98,   115,   137,    84,
     109,   146,   147,   152,   315,    54,   328,   181,   329,   456,
     158,   480,   265,   324,   325,   326,   327,   304,   184,   185,
     201,   167,   334,   457,   170,   335,   229,    71,   223,    21,
     232,   152,    88,   153,   154,   155,   156,   157,   158,   458,
      72,   159,   244,   352,    88,   510,   126,   238,   248,   167,
     168,   126,   170,    88,   100,   253,   520,   305,   223,   247,
     311,   319,   110,   252,   314,   254,   128,   321,   344,   131,
     132,   173,   259,    68,    69,   127,   323,   225,   144,    70,
     353,   134,   341,   387,   343,   368,   365,   386,   373,   360,
     388,   336,   414,   416,   422,   433,   434,   370,   459,   173,
     152,   449,   470,   473,    54,   156,   474,   158,   481,   488,
     159,    88,   378,   223,   179,    71,   490,   489,   167,   168,
     223,   170,   505,   389,   228,   525,   527,   541,   392,   393,
     442,   397,   223,   223,   288,   562,   564,   136,   223,   178,
     127,   412,   179,   567,   569,   180,   451,   376,   482,   560,
     492,     0,     0,     0,     0,   223,   223,   223,     0,     0,
     258,     0,     0,   223,     0,     0,   425,   426,   173,     0,
       0,    88,   430,   379,     0,     0,     0,   435,     0,     0,
     440,   358,   126,     0,     0,     0,     0,     0,   274,     0,
       0,     0,   398,     0,   452,     0,   454,   284,     0,     0,
       0,     0,   298,     0,     0,     0,     0,   428,   178,     0,
       0,   179,     0,   376,     0,   152,     0,     0,     0,     0,
     223,   313,   158,   223,     0,   159,     0,   320,     0,   475,
       0,   478,     0,   167,   168,     0,   170,   332,   333,   483,
       0,   486,     0,   223,   338,     0,     0,   444,   223,   389,
     223,     0,     0,   223,     0,     0,   501,   502,   503,   504,
       0,     0,     0,     0,     0,   223,   355,   356,   357,     0,
     515,     0,   223,     0,   127,     0,     0,     0,   363,   127,
       0,     0,     0,   173,     0,     0,   417,   418,   419,     0,
     421,     0,   479,     0,     0,     0,     0,   152,     0,     0,
     484,   155,   156,   157,   158,     0,     0,   159,   223,     0,
       0,   223,   126,   223,     0,   167,   168,     0,   170,     0,
       0,   223,     0,   223,   507,     0,   179,   547,   223,   223,
     223,     0,   223,     0,    88,   126,    88,     0,   126,   223,
       0,     0,   223,     0,     0,   223,     0,     0,     0,     0,
     526,   469,     0,     0,    88,     0,    88,     0,     0,     0,
       0,     0,     0,     0,   408,   173,   223,     0,   223,     0,
     553,   223,   555,   223,   487,     0,     0,   420,     0,     0,
     423,     0,     0,     0,     0,     0,   546,     0,   548,     0,
     563,     0,   565,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,   443,     0,   178,   448,     0,   179,   450,
     127,    97,     3,   453,   290,     0,     0,     0,   291,     0,
       0,    98,   292,     0,     0,     0,     0,   293,     0,   294,
     295,   465,     0,     0,     0,   408,     0,     0,     0,     0,
       0,     0,   472,   149,   537,   151,   152,     0,   153,   154,
     155,   156,   157,   158,    21,     0,   159,   160,   161,   162,
     163,   164,   165,   166,   167,   168,   169,   170,     0,     0,
     557,     0,     0,   152,   172,     0,     0,   155,   156,  -242,
     158,     0,   508,   159,     0,   511,   513,     0,     0,   408,
       0,   167,   168,     0,   170,     0,     0,   570,   521,   572,
       0,     0,   574,   523,   575,   110,     0,     0,     0,     0,
       0,     0,     0,     0,   173,   528,   529,     0,   530,     0,
       0,     0,   296,     0,     0,     0,     0,     0,   532,    54,
       0,     0,   539,   297,     0,     0,     0,     0,   542,     0,
     127,   173,   545,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,   177,   178,     0,     0,   179,     0,     0,
     180,     0,     0,   127,     0,     0,   127,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     566,   178,   568,     0,   179,   571,     1,   573,     2,     3,
       4,     5,     6,   -82,     7,     8,     9,    10,    82,   -82,
     -82,   -82,    12,   -82,    13,    14,   -82,   -82,   -82,   -82,
      15,    16,   -82,   -82,    17,   -82,   -82,   -82,   -82,   -82,
     -82,    18,    19,   -82,     0,    20,   -82,     0,     0,   -82,
     -82,    21,    22,     0,   -82,   -82,   -82,    23,    24,    25,
     -82,   -82,    26,   -82,   -82,   -82,   -82,    27,    28,     0,
      29,   -82,   -82,     0,    30,    31,    32,     0,    33,    34,
      35,     0,   -82,    83,     0,    37,     0,    38,    39,    40,
     -82,    41,    42,     0,    43,     0,     0,     0,     0,     0,
       0,     0,     0,    44,   -82,    45,   -82,     0,     0,     0,
       0,     0,     0,    46,    47,   -82,    48,    49,    50,   -82,
      51,    52,   -82,    53,     0,   -82,    54,    55,    56,   -82,
      57,     0,    58,   -82,    59,    60,   -82,    61,    62,     1,
       0,     2,     3,     4,     5,     6,   -81,     7,     8,     9,
      10,    11,   -81,   -81,   -81,    12,   -81,    13,    14,   -81,
     -81,   -81,   -81,    15,    16,   -81,   -81,    17,   -81,   -81,
     -81,   -81,   -81,   -81,    18,    19,   -81,     0,    20,   -81,
       0,     0,   -81,   -81,    21,    22,     0,   -81,   -81,   -81,
      23,    24,    25,   -81,   -81,    26,   -81,   -81,   -81,   -81,
      27,    28,     0,    29,   -81,   -81,     0,    30,    31,    32,
       0,    33,    34,    35,     0,   -81,     0,     0,    37,     0,
      38,    39,    40,   -81,    41,    42,     0,    43,     0,     0,
       0,     0,     0,     0,     0,     0,    44,   -81,    45,   -81,
       0,     0,     0,     0,     0,     0,    46,   -81,   -81,    48,
      49,    50,   -81,    51,    52,   -81,    53,     0,   -81,    54,
      55,    56,   -81,    57,     0,    58,   -81,    59,    60,   -81,
      61,    62,     1,     0,     2,     3,     4,     5,     6,  -124,
       7,     8,     9,    10,    11,  -124,  -124,  -124,   139,  -124,
      13,    14,  -124,  -124,  -124,  -124,    15,    16,  -124,  -124,
      17,  -124,  -124,  -124,  -124,  -124,  -124,    18,    19,  -124,
       0,    20,  -124,     0,     0,  -124,  -124,    21,    22,     0,
    -124,  -124,  -124,    23,    24,    25,  -124,  -124,    26,  -124,
    -124,  -124,  -124,    27,    28,     0,    29,  -124,  -124,     0,
      30,    31,    32,     0,    33,    34,    35,     0,  -124,  -124,
       0,    37,     0,    38,    39,    40,  -124,    41,    42,     0,
      43,     0,     0,     0,     0,     0,     0,     0,     0,    44,
    -124,    45,  -124,     0,     0,     0,     0,     0,     0,    46,
    -124,  -124,    48,    49,    50,  -124,    51,    52,  -124,    53,
       0,  -124,    54,    55,    56,  -124,    57,     0,     0,  -124,
      59,    60,  -124,    61,    62,     1,     0,     2,     3,     4,
       5,     6,  -154,     7,     8,     9,    10,    11,  -154,  -154,
    -154,    12,  -154,    13,    14,  -154,  -154,  -154,  -154,    15,
      16,  -154,  -154,    17,  -154,  -154,  -154,  -154,  -154,  -154,
      18,    19,  -154,     0,    20,  -154,     0,     0,  -154,  -154,
      21,    22,     0,  -154,  -154,  -154,    23,    24,    25,  -154,
    -154,    26,  -154,  -154,  -154,  -154,    27,    28,     0,    29,
    -154,  -154,     0,    30,    31,    32,     0,    33,    34,    35,
       0,  -154,    36,     0,    37,     0,    38,    39,    40,  -154,
      41,    42,     0,    43,     0,     0,     0,     0,     0,     0,
       0,     0,    44,  -154,    45,  -154,     0,     0,     0,     0,
       0,     0,    46,     0,  -154,    48,    49,    50,  -154,    51,
      52,  -154,    53,     0,  -154,    54,    55,    56,  -154,    57,
       0,    58,  -154,    59,    60,  -154,    61,    62,     1,     0,
       2,     3,     4,     5,     6,     0,     7,     8,     9,    10,
      11,  -112,  -112,  -112,   377,     0,    13,    14,  -112,  -112,
    -112,  -112,    15,    16,  -112,  -112,    17,  -112,  -112,  -112,
    -112,  -112,  -112,    18,    19,  -112,     0,    20,     0,     0,
       0,     0,  -112,    21,    22,     0,     0,  -112,     0,    23,
      24,    25,     0,     0,    26,     0,     0,     0,     0,    27,
      28,     0,    29,     0,     0,     0,    30,    31,    32,     0,
      33,    34,    35,   106,     0,    36,     0,    37,     0,    38,
      39,    40,  -112,    41,    42,     0,    43,     0,     0,     0,
       0,     0,     0,     0,     0,    44,     0,    45,     0,     0,
       0,     0,     0,     0,     0,    46,    47,     0,    48,    49,
      50,  -112,    51,    52,  -112,    53,     0,  -112,    54,    55,
      56,  -112,    57,     0,    58,  -112,    59,    60,  -112,    61,
      62,     1,     0,     2,     3,     4,     5,     6,     0,     7,
       8,     9,    10,    11,  -112,  -112,  -112,   377,     0,    13,
      14,  -112,  -112,  -112,  -112,    15,    16,  -112,  -112,    17,
    -112,  -112,  -112,  -112,  -112,  -112,    18,    19,  -112,     0,
      20,     0,     0,     0,     0,  -112,    21,    22,     0,     0,
       0,     0,    23,    24,    25,     0,     0,    26,     0,     0,
    -112,     0,    27,    28,     0,    29,     0,     0,     0,    30,
      31,    32,     0,    33,    34,    35,   106,     0,    36,     0,
      37,     0,    38,    39,    40,  -112,    41,    42,     0,    43,
       0,     0,     0,     0,     0,     0,     0,     0,    44,     0,
      45,     0,     0,     0,     0,     0,     0,     0,    46,    47,
       0,    48,    49,    50,  -112,    51,    52,  -112,    53,     0,
    -112,    54,    55,    56,  -112,    57,     0,    58,  -112,    59,
      60,  -112,    61,    62,     1,     0,     2,     3,     4,     5,
       6,     0,     7,     8,     9,    10,    11,   -81,   -81,   -81,
      12,     0,    13,    14,   -81,   -81,   -81,   -81,    15,   199,
     -81,   -81,    17,   -81,   -81,   -81,   -81,   -81,   -81,    18,
      19,   -81,     0,    20,     0,     0,     0,     0,   -81,    21,
      22,     0,     0,     0,     0,    23,    24,    25,     0,     0,
      26,     0,     0,     0,     0,    27,    28,     0,    29,     0,
       0,     0,    30,    31,    32,     0,    33,    34,    35,     0,
       0,    36,     0,    37,     0,    38,    39,    40,   -81,    41,
      42,     0,    43,     0,     0,     0,     0,     0,     0,     0,
       0,    44,     0,    45,     0,     0,     0,     0,     0,     0,
       0,    46,   112,     0,    48,    49,    50,   -81,    51,    52,
     -81,    53,     0,   -81,    54,    55,    56,   -81,    57,     0,
      58,   -81,    59,    60,   -81,    61,    62,     1,     0,     2,
       3,     4,     5,     6,     0,     7,     8,     9,    10,    11,
       0,     0,     0,    12,     0,    13,    14,     0,     0,     0,
     152,    15,    16,     0,     0,    17,     0,   158,     0,     0,
     159,     0,    18,    19,     0,     0,    20,     0,   167,  -242,
     221,   170,    21,    22,     0,   380,     0,   381,    23,    24,
      25,     0,     0,    26,     0,     0,     0,     0,    27,    28,
       0,    29,     0,     0,     0,    30,    31,    32,     0,    33,
      34,    35,     0,   245,    36,     0,    37,     0,    38,    39,
      40,     0,    41,    42,     0,    43,     0,     0,   173,     0,
       0,     0,     0,     0,    44,     0,    45,     0,     0,     0,
       0,     0,     0,     0,    46,   382,     0,    48,    49,    50,
       0,    51,    52,     0,    53,     0,     0,    54,    55,    56,
       0,    57,     0,    58,     0,    59,    60,     0,    61,    62,
       1,   179,     2,     3,     4,     5,     6,     0,     7,     8,
       9,    10,    11,     0,     0,     0,    12,     0,    13,    14,
       0,     0,     0,     0,    15,    16,     0,     0,    17,     0,
       0,     0,     0,     0,     0,    18,    19,     0,     0,    20,
       0,     0,     0,     0,     0,    21,    22,     0,   441,     0,
     442,    23,    24,    25,     0,     0,    26,     0,     0,     0,
       0,    27,    28,     0,    29,     0,     0,     0,    30,    31,
      32,     0,    33,    34,    35,     0,   354,    36,     0,    37,
       0,    38,    39,    40,     0,    41,    42,     0,    43,     0,
       0,     0,     0,     0,     0,     0,     0,    44,     0,    45,
       0,     0,     0,     0,     0,     0,     0,    46,    47,     0,
      48,    49,    50,     0,    51,    52,     0,    53,     0,     0,
      54,    55,    56,     0,    57,     0,    58,     0,    59,    60,
       0,    61,    62,     1,     0,     2,     3,     4,     5,     6,
       0,     7,     8,   491,    10,    11,     0,     0,     0,    12,
       0,    13,    14,     0,     0,     0,     0,    15,    16,     0,
       0,    17,     0,     0,     0,     0,     0,     0,    18,    19,
       0,     0,    20,     0,     0,     0,   221,     0,    21,    22,
       0,     0,     0,     0,    23,    24,    25,   209,  -237,    26,
       0,     0,     0,     0,    27,    28,     0,    29,     0,     0,
       0,    30,    31,    32,     0,    33,    34,    35,     0,     0,
      36,     0,    37,     0,    38,    39,    40,     0,    41,    42,
       0,    43,     0,     0,     0,     0,     0,     0,     0,     0,
      44,     0,    45,     0,     0,     0,     0,     0,     0,     0,
      46,    47,     0,    48,    49,    50,     0,    51,    52,     0,
      53,     0,     0,    54,    55,    56,     0,    57,     0,    58,
       0,    59,    60,     0,    61,    62,     1,     0,     2,     3,
       4,     5,     6,     0,     7,     8,     9,    10,    11,     0,
       0,     0,    12,     0,    13,    14,     0,     0,     0,     0,
      15,    16,     0,     0,    17,     0,     0,     0,     0,     0,
       0,    18,    19,     0,     0,    20,     0,     0,     0,   221,
       0,    21,    22,     0,     0,     0,     0,    23,    24,    25,
       0,     0,    26,     0,     0,     0,     0,    27,    28,     0,
      29,     0,     0,     0,    30,    31,    32,     0,    33,    34,
      35,     0,   245,    36,     0,    37,     0,    38,    39,    40,
       0,    41,    42,     0,    43,     0,     0,     0,     0,     0,
       0,     0,     0,    44,     0,    45,     0,     0,     0,     0,
       0,     0,     0,    46,   535,   536,    48,    49,    50,     0,
      51,    52,     0,    53,     0,     0,    54,    55,    56,     0,
      57,     0,    58,     0,    59,    60,     0,    61,    62,     1,
       0,     2,     3,     4,     5,     6,     0,     7,     8,     9,
      10,    11,     0,     0,     0,    12,     0,    13,    14,     0,
       0,     0,     0,    15,    16,     0,     0,    17,     0,     0,
       0,     0,     0,     0,    18,    19,     0,     0,    20,     0,
       0,     0,   221,     0,    21,    22,     0,     0,     0,     0,
      23,    24,    25,     0,     0,    26,     0,     0,     0,     0,
      27,    28,     0,    29,   222,     0,     0,    30,    31,    32,
       0,    33,    34,    35,     0,     0,    36,     0,    37,     0,
      38,    39,    40,     0,    41,    42,     0,    43,     0,     0,
       0,     0,     0,     0,     0,     0,    44,     0,    45,     0,
       0,     0,     0,     0,     0,     0,    46,    47,     0,    48,
      49,    50,     0,    51,    52,     0,    53,     0,     0,    54,
      55,    56,     0,    57,     0,    58,     0,    59,    60,     0,
      61,    62,     1,     0,     2,     3,     4,     5,     6,     0,
       7,     8,     9,    10,    11,     0,     0,     0,    12,     0,
      13,    14,     0,     0,     0,     0,    15,    16,     0,     0,
      17,     0,     0,     0,     0,     0,     0,    18,    19,     0,
       0,    20,     0,     0,     0,     0,     0,    21,    22,     0,
       0,     0,     0,    23,    24,    25,     0,     0,    26,     0,
       0,     0,     0,    27,    28,     0,    29,     0,     0,     0,
      30,    31,    32,     0,    33,    34,    35,     0,     0,    36,
    -184,    37,   225,    38,    39,    40,     0,    41,    42,     0,
      43,     0,     0,     0,     0,     0,     0,     0,     0,    44,
       0,    45,     0,     0,     0,     0,     0,     0,     0,    46,
      47,     0,    48,    49,    50,     0,    51,    52,     0,    53,
       0,     0,    54,    55,    56,     0,    57,     0,    58,     0,
      59,    60,     0,    61,    62,     1,     0,     2,     3,     4,
       5,     6,     0,     7,     8,     9,    10,    11,     0,     0,
       0,    12,     0,    13,    14,     0,     0,     0,     0,    15,
      16,     0,     0,    17,     0,     0,     0,     0,     0,     0,
      18,    19,     0,     0,    20,     0,     0,     0,   221,     0,
      21,    22,     0,     0,     0,     0,    23,    24,    25,     0,
       0,    26,     0,     0,     0,     0,    27,    28,     0,    29,
       0,     0,     0,    30,    31,    32,     0,    33,    34,    35,
       0,   245,    36,     0,    37,     0,    38,    39,    40,     0,
      41,    42,     0,    43,     0,     0,     0,     0,     0,     0,
       0,     0,    44,     0,    45,     0,     0,     0,     0,     0,
       0,     0,    46,   246,     0,    48,    49,    50,     0,    51,
      52,     0,    53,     0,     0,    54,    55,    56,     0,    57,
       0,    58,     0,    59,    60,     0,    61,    62,     1,     0,
       2,     3,     4,     5,     6,     0,     7,     8,     9,    10,
      11,     0,     0,     0,    12,     0,    13,    14,     0,     0,
       0,     0,    15,    16,     0,     0,    17,     0,     0,     0,
       0,     0,     0,    18,    19,     0,     0,    20,     0,     0,
       0,   221,     0,    21,    22,     0,     0,     0,   250,    23,
      24,    25,     0,     0,    26,     0,     0,     0,     0,    27,
      28,     0,    29,     0,     0,     0,    30,    31,    32,     0,
      33,    34,    35,     0,     0,    36,     0,    37,     0,    38,
      39,    40,     0,    41,    42,     0,    43,     0,     0,     0,
       0,     0,     0,     0,     0,    44,     0,    45,     0,     0,
       0,     0,     0,     0,     0,    46,   251,     0,    48,    49,
      50,     0,    51,    52,     0,    53,     0,     0,    54,    55,
      56,     0,    57,     0,    58,     0,    59,    60,     0,    61,
      62,     1,     0,     2,     3,     4,     5,     6,     0,     7,
       8,     9,    10,    11,     0,     0,     0,    12,     0,    13,
      14,     0,     0,     0,     0,    15,    16,     0,     0,    17,
       0,     0,     0,     0,     0,     0,    18,    19,     0,     0,
      20,     0,     0,     0,   221,     0,    21,    22,     0,     0,
     260,     0,    23,    24,    25,     0,     0,    26,     0,     0,
       0,     0,    27,    28,     0,    29,     0,     0,     0,    30,
      31,    32,     0,    33,    34,    35,     0,     0,    36,     0,
      37,     0,    38,    39,    40,     0,    41,    42,     0,    43,
       0,     0,     0,     0,     0,     0,     0,     0,    44,     0,
      45,     0,     0,     0,     0,     0,     0,     0,    46,    47,
       0,    48,    49,    50,     0,    51,    52,     0,    53,     0,
       0,    54,    55,    56,     0,    57,     0,    58,     0,    59,
      60,     0,    61,    62,     1,     0,     2,     3,     4,     5,
       6,     0,     7,     8,     9,    10,    11,     0,     0,     0,
      12,     0,    13,    14,     0,     0,     0,     0,    15,    16,
       0,     0,    17,     0,     0,     0,     0,     0,     0,    18,
      19,     0,     0,    20,     0,     0,     0,   221,     0,    21,
      22,     0,     0,     0,     0,    23,    24,    25,     0,     0,
      26,     0,     0,     0,     0,    27,    28,     0,    29,     0,
       0,     0,    30,    31,    32,     0,    33,    34,    35,     0,
     340,    36,     0,    37,     0,    38,    39,    40,     0,    41,
      42,     0,    43,     0,     0,     0,     0,     0,     0,     0,
       0,    44,     0,    45,     0,     0,     0,     0,     0,     0,
       0,    46,    47,     0,    48,    49,    50,     0,    51,    52,
       0,    53,     0,     0,    54,    55,    56,     0,    57,     0,
      58,     0,    59,    60,     0,    61,    62,     1,     0,     2,
       3,     4,     5,     6,     0,     7,     8,     9,    10,    11,
       0,     0,     0,    12,     0,    13,    14,     0,     0,     0,
       0,    15,    16,     0,     0,    17,     0,     0,     0,     0,
       0,     0,    18,    19,     0,     0,    20,     0,     0,     0,
     221,     0,    21,    22,     0,     0,     0,     0,    23,    24,
      25,     0,     0,    26,     0,     0,     0,     0,    27,    28,
       0,    29,     0,     0,     0,    30,    31,    32,     0,    33,
      34,    35,     0,  -241,    36,     0,    37,     0,    38,    39,
      40,     0,    41,    42,     0,    43,     0,     0,     0,     0,
       0,     0,     0,     0,    44,     0,    45,     0,     0,     0,
       0,     0,     0,     0,    46,    47,     0,    48,    49,    50,
       0,    51,    52,     0,    53,     0,     0,    54,    55,    56,
       0,    57,     0,    58,     0,    59,    60,     0,    61,    62,
       1,     0,     2,     3,     4,     5,     6,     0,     7,     8,
       9,    10,    11,     0,     0,     0,    12,     0,    13,    14,
       0,     0,     0,     0,    15,    16,     0,     0,    17,     0,
       0,     0,     0,     0,     0,    18,    19,     0,     0,    20,
       0,     0,     0,   221,     0,    21,    22,     0,     0,     0,
       0,    23,    24,    25,     0,  -238,    26,     0,     0,     0,
       0,    27,    28,     0,    29,     0,     0,     0,    30,    31,
      32,     0,    33,    34,    35,     0,     0,    36,     0,    37,
       0,    38,    39,    40,     0,    41,    42,     0,    43,     0,
       0,     0,     0,     0,     0,     0,     0,    44,     0,    45,
       0,     0,     0,     0,     0,     0,     0,    46,    47,     0,
      48,    49,    50,     0,    51,    52,     0,    53,     0,     0,
      54,    55,    56,     0,    57,     0,    58,     0,    59,    60,
       0,    61,    62,     1,     0,     2,     3,     4,     5,     6,
       0,     7,     8,     9,    10,    11,     0,     0,     0,    12,
       0,    13,    14,     0,     0,     0,     0,    15,    16,     0,
       0,    17,     0,     0,     0,     0,     0,     0,    18,    19,
       0,     0,    20,     0,     0,     0,   221,     0,    21,    22,
       0,     0,     0,     0,    23,    24,    25,     0,     0,    26,
       0,     0,     0,     0,    27,    28,     0,    29,     0,     0,
       0,    30,    31,    32,     0,    33,    34,    35,     0,   399,
      36,     0,    37,     0,    38,    39,    40,     0,    41,    42,
       0,    43,     0,     0,     0,     0,     0,     0,     0,     0,
      44,     0,    45,     0,     0,     0,     0,     0,     0,     0,
      46,    47,     0,    48,    49,    50,     0,    51,    52,     0,
      53,     0,     0,    54,    55,    56,     0,    57,     0,    58,
       0,    59,    60,     0,    61,    62,     1,     0,     2,     3,
       4,     5,     6,     0,     7,     8,     9,    10,    11,     0,
       0,     0,    12,     0,    13,    14,     0,     0,     0,     0,
      15,    16,     0,     0,    17,     0,     0,     0,     0,     0,
       0,    18,    19,     0,     0,    20,     0,     0,     0,   221,
       0,    21,    22,     0,     0,     0,     0,    23,    24,    25,
       0,     0,    26,     0,     0,     0,     0,    27,    28,     0,
      29,     0,     0,     0,    30,    31,    32,     0,    33,    34,
      35,     0,   400,    36,     0,    37,     0,    38,    39,    40,
       0,    41,    42,     0,    43,     0,     0,     0,     0,     0,
       0,     0,     0,    44,     0,    45,     0,     0,     0,     0,
       0,     0,     0,    46,    47,     0,    48,    49,    50,     0,
      51,    52,     0,    53,     0,     0,    54,    55,    56,     0,
      57,     0,    58,     0,    59,    60,     0,    61,    62,     1,
       0,     2,     3,     4,     5,     6,     0,     7,     8,     9,
      10,    11,     0,     0,     0,    12,     0,    13,    14,     0,
       0,     0,     0,    15,    16,     0,     0,    17,     0,     0,
       0,     0,     0,     0,    18,    19,     0,     0,    20,     0,
       0,     0,   221,     0,    21,    22,     0,     0,     0,     0,
      23,    24,    25,     0,     0,    26,     0,     0,     0,     0,
      27,    28,     0,    29,     0,     0,     0,    30,    31,    32,
       0,    33,    34,    35,     0,   411,    36,     0,    37,     0,
      38,    39,    40,     0,    41,    42,     0,    43,     0,     0,
       0,     0,     0,     0,     0,     0,    44,     0,    45,     0,
       0,     0,     0,     0,     0,     0,    46,    47,     0,    48,
      49,    50,     0,    51,    52,     0,    53,     0,     0,    54,
      55,    56,     0,    57,     0,    58,     0,    59,    60,     0,
      61,    62,     1,     0,     2,     3,     4,     5,     6,     0,
       7,     8,     9,    10,    11,     0,     0,     0,    12,     0,
      13,    14,     0,     0,     0,     0,    15,    16,     0,     0,
      17,     0,     0,     0,     0,     0,     0,    18,    19,     0,
       0,    20,     0,     0,     0,   221,     0,    21,    22,     0,
       0,     0,   424,    23,    24,    25,     0,     0,    26,     0,
       0,     0,     0,    27,    28,     0,    29,     0,     0,     0,
      30,    31,    32,     0,    33,    34,    35,     0,     0,    36,
       0,    37,     0,    38,    39,    40,     0,    41,    42,     0,
      43,     0,     0,     0,     0,     0,     0,     0,     0,    44,
       0,    45,     0,     0,     0,     0,     0,     0,     0,    46,
      47,     0,    48,    49,    50,     0,    51,    52,     0,    53,
       0,     0,    54,    55,    56,     0,    57,     0,    58,     0,
      59,    60,     0,    61,    62,     1,     0,     2,     3,     4,
       5,     6,     0,     7,     8,     9,    10,    11,     0,     0,
       0,    12,     0,    13,    14,     0,     0,     0,     0,    15,
      16,     0,     0,    17,     0,     0,     0,     0,     0,     0,
      18,    19,     0,     0,    20,     0,     0,     0,     0,     0,
      21,    22,     0,     0,     0,     0,    23,    24,    25,     0,
       0,    26,     0,     0,     0,     0,    27,    28,     0,    29,
       0,     0,     0,    30,    31,    32,     0,    33,    34,    35,
     464,     0,    36,   336,    37,     0,    38,    39,    40,     0,
      41,    42,     0,    43,     0,     0,     0,     0,     0,     0,
       0,     0,    44,     0,    45,     0,     0,     0,     0,     0,
       0,     0,    46,    47,     0,    48,    49,    50,     0,    51,
      52,     0,    53,     0,     0,    54,    55,    56,     0,    57,
       0,    58,     0,    59,    60,     0,    61,    62,     1,     0,
       2,     3,     4,     5,     6,     0,     7,     8,     9,    10,
      11,     0,     0,     0,    12,     0,    13,    14,     0,     0,
       0,     0,    15,    16,     0,     0,    17,     0,     0,     0,
       0,     0,     0,    18,    19,     0,     0,    20,     0,     0,
       0,   221,     0,    21,    22,     0,     0,     0,     0,    23,
      24,    25,     0,     0,    26,     0,     0,     0,     0,    27,
      28,     0,    29,     0,     0,     0,    30,    31,    32,     0,
      33,    34,    35,     0,   471,    36,     0,    37,     0,    38,
      39,    40,     0,    41,    42,     0,    43,     0,     0,     0,
       0,     0,     0,     0,     0,    44,     0,    45,     0,     0,
       0,     0,     0,     0,     0,    46,    47,     0,    48,    49,
      50,     0,    51,    52,     0,    53,     0,     0,    54,    55,
      56,     0,    57,     0,    58,     0,    59,    60,     0,    61,
      62,     1,     0,     2,     3,     4,     5,     6,     0,     7,
       8,     9,    10,    11,     0,     0,     0,    12,     0,    13,
      14,     0,     0,     0,     0,    15,    16,     0,     0,    17,
       0,     0,     0,     0,     0,     0,    18,    19,     0,     0,
      20,     0,     0,     0,   221,     0,    21,    22,     0,     0,
       0,     0,    23,    24,    25,     0,     0,    26,     0,     0,
       0,     0,    27,    28,     0,    29,     0,     0,     0,    30,
      31,    32,     0,    33,    34,    35,     0,   494,    36,     0,
      37,     0,    38,    39,    40,     0,    41,    42,     0,    43,
       0,     0,     0,     0,     0,     0,     0,     0,    44,     0,
      45,     0,     0,     0,     0,     0,     0,     0,    46,    47,
       0,    48,    49,    50,     0,    51,    52,     0,    53,     0,
       0,    54,    55,    56,     0,    57,     0,    58,     0,    59,
      60,     0,    61,    62,     1,     0,     2,     3,     4,     5,
       6,     0,     7,     8,     9,    10,    11,     0,     0,     0,
      12,     0,    13,    14,     0,     0,     0,     0,    15,    16,
       0,     0,    17,     0,     0,     0,     0,     0,     0,    18,
      19,     0,     0,    20,     0,     0,     0,   221,     0,    21,
      22,     0,     0,     0,     0,    23,    24,    25,     0,     0,
      26,     0,     0,     0,     0,    27,    28,     0,    29,     0,
       0,     0,    30,    31,    32,     0,    33,    34,    35,     0,
     499,    36,     0,    37,     0,    38,    39,    40,     0,    41,
      42,     0,    43,     0,     0,     0,     0,     0,     0,     0,
       0,    44,     0,    45,     0,     0,     0,     0,     0,     0,
       0,    46,    47,     0,    48,    49,    50,     0,    51,    52,
       0,    53,     0,     0,    54,    55,    56,     0,    57,     0,
      58,     0,    59,    60,     0,    61,    62,     1,     0,     2,
       3,     4,     5,     6,     0,     7,     8,     9,    10,    11,
       0,     0,     0,    12,     0,    13,    14,     0,     0,     0,
       0,    15,    16,     0,     0,    17,     0,     0,     0,     0,
       0,     0,    18,    19,     0,     0,    20,     0,     0,     0,
     221,     0,    21,    22,     0,     0,     0,     0,    23,    24,
      25,     0,     0,    26,     0,     0,     0,     0,    27,    28,
       0,    29,     0,     0,     0,    30,    31,    32,     0,    33,
      34,    35,     0,   509,    36,     0,    37,     0,    38,    39,
      40,     0,    41,    42,     0,    43,     0,     0,     0,     0,
       0,     0,     0,     0,    44,     0,    45,     0,     0,     0,
       0,     0,     0,     0,    46,    47,     0,    48,    49,    50,
       0,    51,    52,     0,    53,     0,     0,    54,    55,    56,
       0,    57,     0,    58,     0,    59,    60,     0,    61,    62,
       1,     0,     2,     3,     4,     5,     6,     0,     7,     8,
       9,    10,    11,     0,     0,     0,    12,     0,    13,    14,
       0,     0,     0,     0,    15,    16,     0,     0,    17,     0,
       0,     0,     0,     0,     0,    18,    19,     0,     0,    20,
       0,     0,     0,   221,     0,    21,    22,     0,     0,     0,
       0,    23,    24,    25,     0,     0,    26,     0,     0,     0,
       0,    27,    28,     0,    29,     0,     0,     0,    30,    31,
      32,     0,    33,    34,    35,     0,   518,    36,     0,    37,
       0,    38,    39,    40,     0,    41,    42,     0,    43,     0,
       0,     0,     0,     0,     0,     0,     0,    44,     0,    45,
       0,     0,     0,     0,     0,     0,     0,    46,    47,     0,
      48,    49,    50,     0,    51,    52,     0,    53,     0,     0,
      54,    55,    56,     0,    57,     0,    58,     0,    59,    60,
       0,    61,    62,     1,     0,     2,     3,     4,     5,     6,
       0,     7,     8,     9,    10,    11,     0,     0,     0,    12,
       0,    13,    14,     0,     0,     0,     0,    15,    16,     0,
       0,    17,     0,     0,     0,     0,     0,     0,    18,    19,
       0,     0,    20,     0,     0,     0,   221,     0,    21,    22,
       0,     0,     0,     0,    23,    24,    25,     0,     0,    26,
       0,     0,     0,     0,    27,    28,     0,    29,     0,     0,
       0,    30,    31,    32,     0,    33,    34,    35,     0,   531,
      36,     0,    37,     0,    38,    39,    40,     0,    41,    42,
       0,    43,     0,     0,     0,     0,     0,     0,     0,     0,
      44,     0,    45,     0,     0,     0,     0,     0,     0,     0,
      46,    47,     0,    48,    49,    50,     0,    51,    52,     0,
      53,     0,     0,    54,    55,    56,     0,    57,     0,    58,
       0,    59,    60,     0,    61,    62,     1,     0,     2,     3,
       4,     5,     6,     0,     7,     8,     9,    10,    11,     0,
       0,     0,    12,     0,    13,    14,     0,     0,     0,     0,
      15,    16,     0,     0,    17,     0,     0,     0,     0,     0,
       0,    18,    19,     0,     0,    20,     0,     0,     0,   221,
       0,    21,    22,     0,     0,     0,     0,    23,    24,    25,
       0,     0,    26,     0,     0,     0,     0,    27,    28,     0,
      29,     0,     0,     0,    30,    31,    32,     0,    33,    34,
      35,     0,   533,    36,     0,    37,     0,    38,    39,    40,
       0,    41,    42,     0,    43,     0,     0,     0,     0,     0,
       0,     0,     0,    44,     0,    45,     0,     0,     0,     0,
       0,     0,     0,    46,    47,     0,    48,    49,    50,     0,
      51,    52,     0,    53,     0,     0,    54,    55,    56,     0,
      57,     0,    58,     0,    59,    60,     0,    61,    62,     1,
       0,     2,     3,     4,     5,     6,     0,     7,     8,     9,
      10,    11,     0,     0,     0,    12,     0,    13,    14,     0,
       0,     0,     0,    15,    16,     0,     0,    17,     0,     0,
       0,     0,     0,     0,    18,    19,     0,     0,    20,     0,
       0,     0,   221,     0,    21,    22,     0,     0,     0,     0,
      23,    24,    25,     0,     0,    26,     0,     0,     0,     0,
      27,    28,     0,    29,     0,     0,     0,    30,    31,    32,
       0,    33,    34,    35,     0,   543,    36,     0,    37,     0,
      38,    39,    40,     0,    41,    42,     0,    43,     0,     0,
       0,     0,     0,     0,     0,     0,    44,     0,    45,     0,
       0,     0,     0,     0,     0,     0,    46,    47,     0,    48,
      49,    50,     0,    51,    52,     0,    53,     0,     0,    54,
      55,    56,     0,    57,     0,    58,     0,    59,    60,     0,
      61,    62,     1,     0,     2,     3,     4,     5,     6,     0,
       7,     8,     9,    10,    11,     0,     0,     0,    12,     0,
      13,    14,     0,     0,     0,     0,    15,    16,     0,     0,
      17,     0,     0,     0,     0,     0,     0,    18,    19,     0,
       0,    20,     0,     0,     0,   221,     0,    21,    22,     0,
       0,     0,     0,    23,    24,    25,     0,     0,    26,     0,
       0,     0,     0,    27,    28,     0,    29,     0,     0,     0,
      30,    31,    32,     0,    33,    34,    35,     0,   549,    36,
       0,    37,     0,    38,    39,    40,     0,    41,    42,     0,
      43,     0,     0,     0,     0,     0,     0,     0,     0,    44,
       0,    45,     0,     0,     0,     0,     0,     0,     0,    46,
      47,     0,    48,    49,    50,     0,    51,    52,     0,    53,
       0,     0,    54,    55,    56,     0,    57,     0,    58,     0,
      59,    60,     0,    61,    62,     1,     0,     2,     3,     4,
       5,     6,     0,     7,     8,     9,    10,    11,     0,     0,
       0,    12,     0,    13,    14,     0,     0,     0,     0,    15,
      16,     0,     0,    17,     0,     0,     0,     0,     0,     0,
      18,    19,     0,     0,    20,     0,     0,     0,   221,     0,
      21,    22,     0,     0,     0,     0,    23,    24,    25,     0,
       0,    26,     0,     0,     0,     0,    27,    28,     0,    29,
       0,     0,     0,    30,    31,    32,     0,    33,    34,    35,
       0,   550,    36,     0,    37,     0,    38,    39,    40,     0,
      41,    42,     0,    43,     0,     0,     0,     0,     0,     0,
       0,     0,    44,     0,    45,     0,     0,     0,     0,     0,
       0,     0,    46,    47,     0,    48,    49,    50,     0,    51,
      52,     0,    53,     0,     0,    54,    55,    56,     0,    57,
       0,    58,     0,    59,    60,     0,    61,    62,     1,     0,
       2,     3,     4,     5,     6,     0,     7,     8,     9,    10,
      11,     0,     0,     0,    12,     0,    13,    14,     0,     0,
       0,     0,    15,    16,     0,     0,    17,     0,     0,     0,
       0,     0,     0,    18,    19,     0,     0,    20,     0,     0,
       0,   221,     0,    21,    22,     0,     0,     0,     0,    23,
      24,    25,     0,     0,    26,     0,     0,     0,     0,    27,
      28,     0,    29,     0,     0,     0,    30,    31,    32,     0,
      33,    34,    35,     0,   551,    36,     0,    37,     0,    38,
      39,    40,     0,    41,    42,     0,    43,     0,     0,     0,
       0,     0,     0,     0,     0,    44,     0,    45,     0,     0,
       0,     0,     0,     0,     0,    46,    47,     0,    48,    49,
      50,     0,    51,    52,     0,    53,     0,     0,    54,    55,
      56,     0,    57,     0,    58,     0,    59,    60,     0,    61,
      62,     1,     0,     2,     3,     4,     5,     6,     0,     7,
       8,     9,    10,    11,     0,     0,     0,    12,     0,    13,
      14,     0,     0,     0,     0,    15,    16,     0,     0,    17,
       0,     0,     0,     0,     0,     0,    18,    19,     0,     0,
      20,     0,     0,     0,   221,     0,    21,    22,     0,     0,
       0,     0,    23,    24,    25,     0,     0,    26,     0,     0,
       0,     0,    27,    28,     0,    29,     0,     0,     0,    30,
      31,    32,     0,    33,    34,    35,     0,   552,    36,     0,
      37,     0,    38,    39,    40,     0,    41,    42,     0,    43,
       0,     0,     0,     0,     0,     0,     0,     0,    44,     0,
      45,     0,     0,     0,     0,     0,     0,     0,    46,    47,
       0,    48,    49,    50,     0,    51,    52,     0,    53,     0,
       0,    54,    55,    56,     0,    57,     0,    58,     0,    59,
      60,     0,    61,    62,     1,     0,     2,     3,     4,     5,
       6,     0,     7,     8,     9,    10,    11,     0,     0,     0,
      12,     0,    13,    14,     0,     0,     0,     0,    15,    16,
       0,     0,    17,     0,     0,     0,     0,     0,     0,    18,
      19,     0,     0,    20,     0,     0,     0,     0,     0,    21,
      22,     0,     0,     0,     0,    23,    24,    25,     0,     0,
      26,     0,     0,     0,     0,    27,    28,     0,    29,     0,
       0,     0,    30,    31,    32,     0,    33,    34,    35,     0,
     354,    36,     0,    37,     0,    38,    39,    40,     0,    41,
      42,     0,    43,     0,     0,     0,     0,     0,     0,     0,
       0,    44,     0,    45,     0,     0,     0,     0,     0,     0,
       0,    46,    47,   554,    48,    49,    50,     0,    51,    52,
       0,    53,     0,     0,    54,    55,    56,     0,    57,     0,
      58,     0,    59,    60,     0,    61,    62,     1,     0,     2,
       3,     4,     5,     6,     0,     7,     8,     9,    10,    11,
       0,     0,     0,    12,     0,    13,    14,     0,     0,     0,
       0,    15,    16,     0,     0,    17,     0,     0,     0,     0,
       0,     0,    18,    19,     0,     0,    20,     0,     0,     0,
     221,     0,    21,    22,     0,     0,     0,     0,    23,    24,
      25,     0,     0,    26,     0,     0,     0,     0,    27,    28,
       0,    29,     0,     0,     0,    30,    31,    32,     0,    33,
      34,    35,     0,   558,    36,     0,    37,     0,    38,    39,
      40,     0,    41,    42,     0,    43,     0,     0,     0,     0,
       0,     0,     0,     0,    44,     0,    45,     0,     0,     0,
       0,     0,     0,     0,    46,    47,     0,    48,    49,    50,
       0,    51,    52,     0,    53,     0,     0,    54,    55,    56,
       0,    57,     0,    58,     0,    59,    60,     0,    61,    62,
       1,     0,     2,     3,     4,     5,     6,     0,     7,     8,
       9,    10,    11,     0,     0,     0,    12,     0,    13,    14,
       0,     0,     0,     0,    15,    16,     0,     0,    17,     0,
       0,     0,     0,     0,     0,    18,    19,     0,     0,    20,
    -204,     0,     0,     0,     0,    21,    22,     0,     0,     0,
       0,    23,    24,    25,     0,     0,    26,     0,     0,     0,
       0,    27,    28,     0,    29,     0,     0,     0,    30,    31,
      32,     0,    33,    34,    35,     0,     0,    36,     0,    37,
       0,    38,    39,    40,     0,    41,    42,     0,    43,     0,
       0,     0,     0,     0,     0,     0,     0,    44,     0,    45,
       0,     0,     0,     0,     0,     0,     0,    46,    47,     0,
      48,    49,    50,     0,    51,    52,     0,    53,     0,     0,
      54,    55,    56,     0,    57,     0,    58,     0,    59,    60,
       0,    61,    62,     1,     0,     2,     3,     4,     5,     6,
       0,     7,     8,     9,    10,    11,     0,     0,     0,    12,
       0,    13,    14,     0,     0,     0,     0,    15,    16,     0,
       0,    17,     0,     0,     0,     0,     0,     0,    18,    19,
       0,     0,    20,     0,     0,     0,     0,     0,    21,    22,
       0,     0,     0,     0,    23,    24,    25,     0,     0,    26,
       0,     0,     0,     0,    27,    28,     0,    29,     0,     0,
       0,    30,    31,    32,     0,    33,    34,    35,   106,     0,
      36,     0,    37,     0,    38,    39,    40,     0,    41,    42,
       0,    43,     0,     0,     0,     0,     0,     0,     0,     0,
      44,     0,    45,     0,     0,     0,     0,     0,     0,     0,
      46,    47,     0,    48,    49,    50,     0,    51,    52,     0,
      53,     0,     0,    54,    55,    56,     0,    57,     0,    58,
       0,    59,    60,     0,    61,    62,     1,     0,     2,     3,
       4,     5,     6,     0,     7,     8,     9,    10,    11,     0,
       0,     0,    12,   192,    13,    14,     0,     0,     0,     0,
      15,    16,     0,     0,    17,     0,     0,     0,     0,     0,
       0,    18,    19,     0,     0,    20,     0,     0,     0,     0,
       0,    21,    22,     0,     0,     0,     0,    23,    24,    25,
       0,     0,    26,     0,     0,     0,     0,    27,    28,     0,
      29,     0,     0,     0,    30,    31,    32,     0,    33,    34,
      35,     0,     0,    36,     0,    37,     0,    38,    39,    40,
       0,    41,    42,     0,    43,     0,     0,     0,     0,     0,
       0,     0,     0,    44,     0,    45,     0,     0,     0,     0,
       0,     0,     0,    46,    47,     0,    48,    49,    50,     0,
      51,    52,     0,    53,     0,     0,    54,    55,    56,     0,
      57,     0,    58,     0,    59,    60,     0,    61,    62,     1,
       0,     2,     3,     4,     5,     6,     0,     7,     8,     9,
      10,    11,     0,     0,     0,    12,   194,    13,    14,     0,
       0,     0,     0,    15,    16,     0,     0,    17,     0,     0,
       0,     0,     0,     0,    18,    19,     0,     0,    20,     0,
       0,     0,     0,     0,    21,    22,     0,     0,     0,     0,
      23,    24,    25,     0,     0,    26,     0,     0,     0,     0,
      27,    28,     0,    29,     0,     0,     0,    30,    31,    32,
       0,    33,    34,    35,     0,     0,    36,     0,    37,     0,
      38,    39,    40,     0,    41,    42,     0,    43,     0,     0,
       0,     0,     0,     0,     0,     0,    44,     0,    45,     0,
       0,     0,     0,     0,     0,     0,    46,    47,     0,    48,
      49,    50,     0,    51,    52,     0,    53,     0,     0,    54,
      55,    56,     0,    57,     0,    58,     0,    59,    60,     0,
      61,    62,     1,     0,     2,     3,     4,     5,     6,     0,
       7,     8,     9,    10,    11,     0,     0,     0,    12,     0,
      13,    14,     0,     0,     0,     0,    15,    16,     0,     0,
      17,     0,     0,     0,     0,     0,     0,    18,    19,     0,
       0,    20,     0,     0,     0,  -202,     0,    21,    22,     0,
       0,     0,     0,    23,    24,    25,     0,     0,    26,     0,
       0,     0,     0,    27,    28,     0,    29,     0,     0,     0,
      30,    31,    32,     0,    33,    34,    35,     0,     0,    36,
       0,    37,     0,    38,    39,    40,     0,    41,    42,     0,
      43,     0,     0,     0,     0,     0,     0,     0,     0,    44,
       0,    45,     0,     0,     0,     0,     0,     0,     0,    46,
      47,     0,    48,    49,    50,     0,    51,    52,     0,    53,
       0,     0,    54,    55,    56,     0,    57,     0,    58,     0,
      59,    60,     0,    61,    62,     1,     0,     2,     3,     4,
       5,     6,     0,     7,     8,     9,    10,    11,     0,     0,
       0,    12,  -204,    13,    14,     0,     0,     0,     0,    15,
      16,     0,     0,    17,     0,     0,     0,     0,     0,     0,
      18,    19,     0,     0,    20,     0,     0,     0,     0,     0,
      21,    22,     0,     0,     0,     0,    23,    24,    25,     0,
       0,    26,     0,     0,     0,     0,    27,    28,     0,    29,
       0,     0,     0,    30,    31,    32,     0,    33,    34,    35,
       0,     0,    36,     0,    37,     0,    38,    39,    40,     0,
      41,    42,     0,    43,     0,     0,     0,     0,     0,     0,
       0,     0,    44,     0,    45,     0,     0,     0,     0,     0,
       0,     0,    46,    47,     0,    48,    49,    50,     0,    51,
      52,     0,    53,     0,     0,    54,    55,    56,     0,    57,
       0,    58,     0,    59,    60,     0,    61,    62,     1,     0,
       2,     3,     4,     5,     6,     0,     7,     8,     9,    10,
      11,     0,     0,     0,    12,   256,    13,    14,     0,     0,
       0,     0,    15,    16,     0,     0,    17,     0,     0,     0,
       0,     0,     0,    18,    19,     0,     0,    20,     0,     0,
       0,     0,     0,    21,    22,     0,     0,     0,     0,    23,
      24,    25,     0,     0,    26,     0,     0,     0,     0,    27,
      28,     0,    29,     0,     0,     0,    30,    31,    32,     0,
      33,    34,    35,     0,     0,    36,     0,    37,     0,    38,
      39,    40,     0,    41,    42,     0,    43,     0,     0,     0,
       0,     0,     0,     0,     0,    44,     0,    45,     0,     0,
       0,     0,     0,     0,     0,    46,    47,     0,    48,    49,
      50,     0,    51,    52,     0,    53,     0,     0,    54,    55,
      56,     0,    57,     0,    58,     0,    59,    60,     0,    61,
      62,     1,     0,     2,     3,     4,     5,     6,   287,     7,
       8,     9,    10,    11,     0,     0,     0,    12,     0,    13,
      14,     0,     0,     0,     0,    15,    16,     0,     0,    17,
       0,     0,     0,     0,     0,     0,    18,    19,     0,     0,
      20,     0,     0,     0,     0,     0,    21,    22,     0,     0,
       0,     0,    23,    24,    25,     0,     0,    26,     0,     0,
       0,     0,    27,    28,     0,    29,     0,     0,     0,    30,
      31,    32,     0,    33,    34,    35,     0,     0,    36,     0,
      37,     0,    38,    39,    40,     0,    41,    42,     0,    43,
       0,     0,     0,     0,     0,     0,     0,     0,    44,     0,
      45,     0,     0,     0,     0,     0,     0,     0,    46,    47,
       0,    48,    49,    50,     0,    51,    52,     0,    53,     0,
       0,    54,    55,    56,     0,    57,     0,    58,     0,    59,
      60,     0,    61,    62,     1,     0,     2,     3,     4,     5,
       6,     0,     7,     8,     9,    10,    11,     0,     0,     0,
      12,     0,    13,    14,     0,     0,     0,     0,    15,    16,
       0,     0,    17,     0,     0,     0,     0,     0,     0,    18,
      19,     0,     0,    20,     0,     0,     0,     0,     0,    21,
      22,     0,     0,     0,     0,    23,    24,    25,     0,     0,
      26,     0,     0,     0,     0,    27,    28,     0,    29,     0,
       0,     0,    30,    31,    32,     0,    33,    34,    35,     0,
       0,    36,   336,    37,     0,    38,    39,    40,     0,    41,
      42,     0,    43,     0,     0,     0,     0,     0,     0,     0,
       0,    44,     0,    45,     0,     0,     0,     0,     0,     0,
       0,    46,    47,     0,    48,    49,    50,     0,    51,    52,
       0,    53,     0,     0,    54,    55,    56,     0,    57,     0,
      58,     0,    59,    60,     0,    61,    62,     1,     0,     2,
       3,     4,     5,     6,     0,     7,     8,     9,    10,    11,
       0,     0,     0,    12,     0,    13,    14,     0,     0,     0,
       0,    15,    16,     0,     0,    17,     0,     0,     0,     0,
       0,     0,    18,    19,     0,     0,    20,     0,     0,     0,
       0,     0,    21,    22,     0,     0,     0,     0,    23,    24,
      25,     0,     0,    26,     0,     0,     0,     0,    27,    28,
       0,    29,     0,     0,     0,    30,    31,    32,     0,    33,
      34,    35,     0,   354,    36,     0,    37,     0,    38,    39,
      40,     0,    41,    42,     0,    43,     0,     0,     0,     0,
       0,     0,     0,     0,    44,     0,    45,     0,     0,     0,
       0,     0,     0,     0,    46,    47,     0,    48,    49,    50,
       0,    51,    52,     0,    53,     0,     0,    54,    55,    56,
       0,    57,     0,    58,     0,    59,    60,     0,    61,    62,
       1,     0,     2,     3,     4,     5,     6,     0,     7,     8,
       9,    10,    11,     0,     0,     0,    12,     0,    13,    14,
       0,     0,     0,     0,    15,    16,     0,     0,    17,     0,
       0,     0,     0,     0,     0,    18,    19,     0,     0,    20,
       0,     0,     0,     0,     0,    21,    22,     0,     0,     0,
       0,    23,    24,    25,     0,     0,    26,     0,     0,     0,
       0,    27,    28,     0,    29,     0,     0,     0,    30,    31,
      32,     0,    33,    34,    35,     0,   245,    36,     0,    37,
       0,    38,    39,    40,     0,    41,    42,     0,    43,     0,
       0,     0,     0,     0,     0,     0,     0,    44,     0,    45,
       0,     0,     0,     0,     0,     0,     0,    46,   246,     0,
      48,    49,    50,     0,    51,    52,     0,    53,     0,     0,
      54,    55,    56,     0,    57,     0,    58,     0,    59,    60,
       0,    61,    62,     1,     0,     2,     3,     4,     5,     6,
       0,     7,     8,     9,    10,    11,     0,     0,     0,    12,
       0,    13,    14,     0,     0,     0,     0,    15,    16,     0,
       0,    17,     0,     0,     0,     0,     0,     0,    18,    19,
       0,     0,    20,     0,     0,     0,     0,     0,    21,    22,
       0,     0,     0,   359,    23,    24,    25,     0,     0,    26,
       0,     0,     0,     0,    27,    28,     0,    29,     0,     0,
       0,    30,    31,    32,     0,    33,    34,    35,     0,     0,
      36,     0,    37,     0,    38,    39,    40,     0,    41,    42,
       0,    43,     0,     0,     0,     0,     0,     0,     0,     0,
      44,     0,    45,     0,     0,     0,     0,     0,     0,     0,
      46,    47,     0,    48,    49,    50,     0,    51,    52,     0,
      53,     0,     0,    54,    55,    56,     0,    57,     0,    58,
       0,    59,    60,     0,    61,    62,     1,     0,     2,     3,
       4,     5,     6,     0,     7,     8,     9,    10,    11,     0,
       0,     0,    12,     0,    13,    14,     0,     0,     0,     0,
      15,    16,     0,     0,    17,     0,     0,     0,     0,     0,
       0,    18,    19,     0,     0,    20,     0,     0,     0,     0,
       0,    21,    22,     0,     0,     0,     0,    23,    24,    25,
       0,     0,    26,     0,     0,     0,     0,    27,    28,     0,
      29,     0,     0,     0,    30,    31,    32,     0,    33,   477,
      35,   106,     0,    36,     0,    37,     0,    38,    39,    40,
       0,    41,    42,     0,    43,     0,     0,     0,     0,     0,
       0,     0,     0,    44,     0,    45,     0,     0,     0,     0,
       0,     0,     0,    46,    47,     0,    48,    49,    50,     0,
      51,    52,     0,    53,     0,     0,    54,    55,    56,     0,
      57,     0,    58,     0,    59,    60,     0,    61,    62,     1,
       0,     2,     3,     4,     5,     6,     0,     7,     8,     9,
      10,    11,     0,     0,     0,    12,  -202,    13,    14,     0,
       0,     0,     0,    15,    16,     0,     0,    17,     0,     0,
       0,     0,     0,     0,    18,    19,     0,     0,    20,     0,
       0,     0,     0,     0,    21,    22,     0,     0,     0,     0,
      23,    24,    25,     0,     0,    26,     0,     0,     0,     0,
      27,    28,     0,    29,     0,     0,     0,    30,    31,    32,
       0,    33,    34,    35,     0,     0,    36,     0,    37,     0,
      38,    39,    40,     0,    41,    42,     0,    43,     0,     0,
       0,     0,     0,     0,     0,     0,    44,     0,    45,     0,
       0,     0,     0,     0,     0,     0,    46,    47,     0,    48,
      49,    50,     0,    51,    52,     0,    53,     0,     0,    54,
      55,    56,     0,    57,     0,    58,     0,    59,    60,     0,
      61,    62,     1,     0,     2,     3,     4,     5,     6,     0,
       7,     8,     9,    10,    11,     0,     0,     0,    12,     0,
      13,    14,     0,     0,     0,     0,    15,    16,     0,     0,
      17,     0,     0,     0,     0,     0,     0,    18,    19,     0,
       0,    20,     0,     0,     0,     0,     0,    21,    22,     0,
       0,     0,     0,    23,    24,    25,     0,     0,    26,     0,
       0,     0,     0,    27,    28,     0,    29,     0,     0,     0,
      30,    31,    32,     0,    33,    34,    35,     0,     0,    36,
       0,    37,     0,    38,    39,    40,     0,    41,    42,     0,
      43,     0,     0,     0,     0,     0,     0,     0,     0,    44,
       0,    45,     0,     0,     0,     0,     0,     0,     0,    46,
      47,     0,    48,    49,    50,     0,    51,    52,     0,    53,
     514,     0,    54,    55,    56,     0,    57,     0,    58,     0,
      59,    60,     0,    61,    62,     1,     0,     2,     3,     4,
       5,     6,     0,     7,     8,     9,    10,    11,     0,     0,
       0,    12,     0,    13,    14,     0,     0,     0,     0,    15,
      16,     0,     0,    17,     0,     0,     0,     0,     0,     0,
      18,    19,     0,     0,    20,     0,     0,     0,     0,     0,
      21,    22,     0,     0,     0,     0,    23,    24,    25,     0,
       0,    26,     0,     0,     0,     0,    27,    28,     0,    29,
       0,     0,     0,    30,    31,    32,     0,    33,    34,    35,
       0,     0,    36,     0,    37,     0,    38,    39,    40,     0,
      41,    42,     0,    43,     0,     0,     0,     0,     0,     0,
       0,     0,    44,     0,    45,     0,     0,     0,     0,     0,
       0,     0,    46,    47,   534,    48,    49,    50,     0,    51,
      52,     0,    53,     0,     0,    54,    55,    56,     0,    57,
       0,    58,     0,    59,    60,     0,    61,    62,     1,     0,
       2,     3,     4,     5,     6,     0,     7,     8,     9,    10,
      11,     0,     0,     0,    12,     0,    13,    14,     0,     0,
       0,     0,    15,    16,     0,     0,    17,     0,     0,     0,
       0,     0,     0,    18,    19,     0,     0,    20,     0,     0,
       0,     0,     0,    21,    22,     0,     0,     0,     0,    23,
      24,    25,     0,     0,    26,     0,     0,     0,     0,    27,
      28,     0,    29,     0,     0,     0,    30,    31,    32,     0,
      33,    34,    35,     0,     0,    36,     0,    37,     0,    38,
      39,    40,     0,    41,    42,     0,    43,     0,     0,     0,
       0,     0,     0,     0,     0,    44,     0,    45,     0,     0,
       0,     0,     0,     0,     0,    46,    47,   556,    48,    49,
      50,     0,    51,    52,     0,    53,     0,     0,    54,    55,
      56,     0,    57,     0,    58,     0,    59,    60,     0,    61,
      62,     1,     0,     2,     3,     4,     5,     6,     0,     7,
       8,     9,    10,    11,     0,     0,     0,    12,     0,    13,
      14,     0,     0,     0,     0,    15,    16,     0,     0,    17,
       0,     0,     0,     0,     0,     0,    18,    19,     0,     0,
      20,     0,     0,     0,     0,     0,    21,    22,     0,     0,
       0,     0,    23,    24,    25,     0,     0,    26,     0,     0,
       0,     0,    27,    28,     0,    29,     0,     0,     0,    30,
      31,    32,     0,    33,    34,    35,     0,     0,    36,     0,
      37,     0,    38,    39,    40,     0,    41,    42,     0,    43,
       0,     0,     0,     0,     0,     0,     0,     0,    44,     0,
      45,     0,     0,     0,     0,     0,     0,     0,    46,    47,
       0,    48,    49,    50,     0,    51,    52,     0,    53,     0,
       0,    54,    55,    56,     0,    57,     0,    58,     0,    59,
      60,     0,    61,    62,     1,     0,     2,     3,     4,     5,
       6,     0,     7,     8,     9,    10,    82,     0,     0,     0,
      12,     0,    13,    14,     0,     0,     0,     0,    15,    16,
       0,     0,    17,     0,     0,     0,     0,     0,     0,    18,
      19,     0,     0,    20,     0,     0,     0,     0,     0,    21,
      22,     0,     0,     0,     0,    23,    24,    25,     0,     0,
      26,     0,     0,     0,     0,    27,    28,     0,    29,     0,
       0,     0,    30,    31,    32,     0,    33,    34,    35,     0,
       0,    83,     0,    37,     0,    38,    39,    40,     0,    41,
      42,     0,    43,     0,     0,     0,     0,     0,     0,     0,
       0,    44,     0,    45,     0,     0,     0,     0,     0,     0,
       0,    46,    47,     0,    48,    49,    50,     0,    51,    52,
       0,    53,     0,     0,    54,    55,    56,     0,    57,     0,
      58,     0,    59,    60,     0,    61,    62,     1,     0,     2,
       3,     4,     5,     6,     0,     7,     8,     9,    10,    11,
       0,     0,     0,    91,     0,    13,    14,     0,     0,     0,
       0,    15,    16,     0,     0,    17,     0,     0,     0,     0,
       0,     0,    18,    19,     0,     0,    20,     0,     0,     0,
       0,     0,    21,    22,     0,     0,     0,     0,    23,    24,
      25,     0,     0,    26,     0,     0,     0,     0,    27,    28,
       0,    29,     0,     0,     0,    30,    31,    32,     0,    33,
      34,    35,     0,     0,    36,     0,    37,     0,    38,    39,
      40,     0,    41,    42,     0,    43,     0,     0,     0,     0,
       0,     0,     0,     0,    44,     0,    45,     0,     0,     0,
       0,     0,     0,     0,    46,    47,     0,    48,    49,    50,
       0,    51,    52,     0,    53,     0,     0,    54,    55,    56,
       0,    57,     0,    58,     0,    59,    60,     0,    61,    62,
       1,     0,     2,     3,     4,     5,     6,     0,     7,     8,
       9,    10,    11,     0,     0,     0,    95,     0,    13,    14,
       0,     0,     0,     0,    15,    16,     0,     0,    17,     0,
       0,     0,     0,     0,     0,    18,    19,     0,     0,    20,
       0,     0,     0,     0,     0,    21,    22,     0,     0,     0,
       0,    23,    24,    25,     0,     0,    26,     0,     0,     0,
       0,    27,    28,     0,    29,     0,     0,     0,    30,    31,
      32,     0,    33,    34,    35,     0,     0,    36,     0,    37,
       0,    38,    39,    40,     0,    41,    42,     0,    43,     0,
       0,     0,     0,     0,     0,     0,     0,    44,     0,    45,
       0,     0,     0,     0,     0,     0,     0,    46,    47,     0,
      48,    49,    50,     0,    51,    52,     0,    53,     0,     0,
      54,    55,    56,     0,    57,     0,    58,     0,    59,    60,
       0,    61,    62,     1,     0,     2,     3,     4,     5,     6,
       0,     7,     8,     9,    10,    11,     0,     0,     0,   101,
       0,    13,    14,     0,     0,     0,     0,    15,    16,     0,
       0,    17,     0,     0,     0,     0,     0,     0,    18,    19,
       0,     0,    20,     0,     0,     0,     0,     0,    21,    22,
       0,     0,     0,     0,    23,    24,    25,     0,     0,    26,
       0,     0,     0,     0,    27,    28,     0,    29,     0,     0,
       0,    30,    31,    32,     0,    33,    34,    35,     0,     0,
      36,     0,    37,     0,    38,    39,    40,     0,    41,    42,
       0,    43,     0,     0,     0,     0,     0,     0,     0,     0,
      44,     0,    45,     0,     0,     0,     0,     0,     0,     0,
      46,    47,     0,    48,    49,    50,     0,    51,    52,     0,
      53,     0,     0,    54,    55,    56,     0,    57,     0,    58,
       0,    59,    60,     0,    61,    62,     1,     0,     2,   207,
       4,     5,     6,     0,     7,     8,     9,    10,    11,     0,
       0,     0,    12,     0,    13,    14,     0,     0,     0,     0,
      15,    16,     0,     0,    17,     0,     0,     0,     0,     0,
       0,    18,    19,     0,     0,    20,     0,     0,     0,     0,
       0,    21,    22,     0,     0,     0,     0,    23,    24,    25,
       0,     0,    26,     0,     0,     0,     0,    27,    28,     0,
      29,     0,     0,     0,    30,    31,    32,     0,    33,    34,
      35,     0,     0,    36,     0,    37,     0,    38,    39,    40,
       0,    41,    42,     0,    43,     0,     0,     0,     0,     0,
       0,     0,     0,    44,     0,    45,     0,     0,     0,     0,
       0,     0,     0,    46,    47,     0,    48,    49,    50,     0,
      51,    52,     0,    53,     0,     0,    54,    55,    56,     0,
      57,     0,    58,     0,    59,    60,     0,    61,    62,     1,
       0,     2,     3,     4,     5,     6,     0,     7,     8,     9,
      10,    11,     0,     0,     0,    12,     0,    13,    14,     0,
       0,     0,     0,    15,    16,     0,     0,    17,     0,     0,
       0,     0,     0,     0,    18,    19,     0,     0,   285,     0,
       0,     0,     0,     0,    21,    22,     0,     0,     0,     0,
      23,    24,    25,     0,     0,    26,     0,     0,     0,     0,
      27,    28,     0,    29,     0,     0,     0,    30,    31,    32,
       0,    33,    34,    35,     0,     0,    36,     0,    37,     0,
      38,    39,    40,     0,    41,    42,     0,    43,     0,     0,
       0,     0,     0,     0,     0,     0,    44,     0,    45,     0,
       0,     0,     0,     0,     0,     0,    46,    47,     0,    48,
      49,    50,     0,    51,    52,     0,    53,     0,     0,    54,
      55,    56,     0,    57,     0,    58,     0,    59,    60,     0,
      61,    62,     1,     0,     2,     3,     4,     5,     6,     0,
       7,     8,     9,    10,    11,     0,     0,     0,    12,     0,
      13,    14,     0,     0,     0,     0,    15,    16,     0,     0,
      17,     0,     0,     0,     0,     0,     0,    18,    19,     0,
       0,   429,     0,     0,     0,     0,     0,    21,    22,     0,
       0,     0,     0,    23,    24,    25,     0,     0,    26,     0,
       0,     0,     0,    27,    28,     0,    29,     0,     0,     0,
      30,    31,    32,     0,    33,    34,    35,     0,     0,    36,
       0,    37,     0,    38,    39,    40,     0,    41,    42,     0,
      43,     0,     0,     0,     0,     0,     0,     0,     0,    44,
       0,    45,     0,     0,     0,     0,     0,     0,     0,    46,
      47,     0,    48,    49,    50,     0,    51,    52,     0,    53,
       0,     0,    54,    55,    56,     0,    57,     0,    58,     0,
      59,    60,     0,    61,    62,     1,     0,     2,     3,     4,
       5,     6,     0,     7,     8,     9,    10,    11,     0,     0,
       0,    12,     0,    13,    14,     0,     0,     0,     0,    15,
      16,     0,     0,    17,     0,     0,     0,     0,     0,     0,
      18,    19,     0,     0,    20,     0,     0,     0,     0,     0,
      21,    22,     0,     0,     0,     0,    23,    24,    25,     0,
       0,    26,     0,     0,     0,     0,    27,    28,     0,    29,
       0,     0,     0,    30,    31,    32,     0,    33,    34,    35,
       0,     0,    36,     0,    37,     0,    38,    39,    40,     0,
      41,    42,     0,    43,     0,     0,     0,     0,     0,     0,
       0,     0,    44,     0,    45,     0,     0,     0,     0,     0,
       0,     0,    46,   512,     0,    48,    49,    50,     0,    51,
      52,     0,    53,     0,     0,    54,    55,    56,     0,    57,
       0,    58,     0,    59,    60,     0,    61,    62,     1,     0,
       2,     3,     4,     5,     6,     0,     7,     8,     9,    10,
      11,     0,     0,     0,    12,     0,    13,    14,     0,     0,
       0,     0,    15,    16,     0,     0,    17,     0,     0,     0,
       0,     0,     0,    18,    19,     0,     0,    20,     0,     0,
       0,     0,     0,    21,    22,     0,     0,     0,     0,    23,
      24,    25,     0,     0,    26,     0,     0,     0,     0,    27,
      28,     0,    29,     0,     0,     0,    30,    31,    32,     0,
      33,    34,    35,     0,     0,    36,     0,    37,     0,    38,
      39,    40,     0,    41,    42,     0,    43,     0,     0,     0,
       0,     0,     0,     0,     0,    44,     0,    45,     0,     0,
       0,     0,     0,     0,     0,    46,   538,     0,    48,    49,
      50,     0,    51,    52,     0,    53,     0,     0,    54,    55,
      56,     0,    57,     0,    58,   148,    59,    60,     0,    61,
      62,   149,   150,   151,   152,     0,   153,   154,   155,   156,
     157,   158,     0,     0,   159,   160,   161,   162,   163,   164,
     165,   166,   167,   168,   169,   170,     0,     0,     0,     0,
       0,   171,   172,     0,   208,     0,     0,   149,   150,   151,
     152,     0,   153,   154,   155,   156,   157,   158,     0,     0,
     159,   160,   161,   162,   163,   164,   165,   166,   167,   168,
     169,   170,     0,     0,     0,     0,     0,     0,   172,     0,
       0,     0,   173,     0,   149,   150,   151,   152,   209,   153,
     154,   155,   156,   157,   158,     0,     0,   159,   160,   161,
     162,   163,   164,   165,   166,   167,   168,   169,   170,     0,
       0,   174,     0,     0,   175,   172,     0,   176,   173,     0,
     203,   177,   178,     0,     0,   179,     0,     0,   180,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,   174,     0,     0,
     175,     0,     0,   176,     0,   173,     0,   177,   178,     0,
       0,   179,     0,     0,   180,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,   204,
       0,     0,     0,     0,   174,     0,     0,   175,     0,     0,
     176,     0,     0,     0,   177,   178,     0,     0,   179,     0,
       0,   180,   149,   150,   151,   152,   197,   153,   154,   155,
     156,   157,   158,     0,     0,   159,   160,   161,   162,   163,
     164,   165,   166,   167,   168,   169,   170,     0,     0,   149,
     150,   151,   152,   172,   153,   154,   155,   156,   157,   158,
       0,   200,   159,   160,   161,   162,   163,   164,   165,   166,
     167,   168,   169,   170,     0,     0,     0,     0,     0,     0,
     172,     0,     0,     0,   151,   152,     0,   153,   154,   155,
     156,   157,   158,   173,     0,   159,   160,   161,   162,   163,
     164,   165,   166,   167,   168,   169,   170,     0,     0,     0,
       0,     0,     0,   172,     0,     0,     0,     0,     0,     0,
     173,     0,   174,     0,     0,   175,     0,     0,   176,     0,
       0,     0,   177,   178,     0,     0,   179,     0,     0,   180,
       0,     0,     0,     0,     0,     0,     0,     0,     0,   174,
       0,     0,   175,   173,     0,   176,     0,     0,     0,   177,
     178,     0,     0,   179,     0,     0,   180,   149,   150,   151,
     152,     0,   153,   154,   155,   156,   157,   158,     0,     0,
     159,   160,   161,   162,   163,   164,   165,   166,   167,   168,
     169,   170,   177,   178,     0,     0,   179,     0,   172,   180,
       0,   149,   150,   151,   152,     0,   153,   154,   155,   156,
     157,   158,     0,   219,   159,   160,   161,   162,   163,   164,
     165,   166,   167,   168,   169,   170,     0,     0,     0,     0,
       0,     0,   172,     0,     0,     0,     0,     0,   173,     0,
       0,     0,     0,     0,     0,     0,     0,   220,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,   174,     0,     0,
     175,     0,   173,   176,     0,     0,     0,   177,   178,     0,
       0,   179,     0,     0,   180,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,   174,     0,     0,   175,     0,     0,   176,     0,     0,
       0,   177,   178,     0,     0,   179,     0,     0,   180,   149,
     150,   151,   152,     0,   153,   154,   155,   156,   157,   158,
       0,     0,   159,   160,   161,   162,   163,   164,   165,   166,
     167,   168,   169,   170,     0,     0,   149,   150,   151,   152,
     172,   153,   154,   155,   156,   157,   158,     0,     0,   159,
     160,   161,   162,   163,   164,   165,   166,   167,   168,   169,
     170,     0,     0,     0,     0,     0,     0,   172,     0,   149,
     150,   151,   152,   308,   153,   154,   155,   156,   157,   158,
     173,     0,   159,   160,   161,   162,   163,   164,   165,   166,
     167,   168,   169,   170,   242,     0,     0,     0,     0,     0,
     172,     0,     0,     0,     0,     0,     0,   173,     0,   174,
       0,     0,   175,     0,     0,   176,     0,     0,     0,   177,
     178,     0,     0,   179,     0,     0,   180,     0,     0,     0,
       0,   249,     0,     0,     0,     0,   174,     0,     0,   175,
     173,     0,   176,     0,     0,     0,   177,   178,     0,     0,
     179,     0,     0,   180,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,   174,
       0,     0,   175,     0,     0,   176,     0,     0,     0,   177,
     178,     0,     0,   179,     0,     0,   180,   149,   150,   151,
     152,   309,   153,   154,   155,   156,   157,   158,     0,     0,
     159,   160,   161,   162,   163,   164,   165,   166,   167,   168,
     169,   170,     0,     0,   149,   150,   151,   152,   172,   153,
     154,   155,   156,   157,   158,     0,     0,   159,   160,   161,
     162,   163,   164,   165,   166,   167,   168,   169,   170,     0,
       0,   310,     0,     0,     0,   172,     0,   149,   150,   151,
     152,   312,   153,   154,   155,   156,   157,   158,   173,     0,
     159,   160,   161,   162,   163,   164,   165,   166,   167,   168,
     169,   170,     0,     0,     0,     0,     0,     0,   172,     0,
       0,     0,     0,     0,   152,   173,     0,   174,     0,     0,
     175,   158,     0,   176,   159,     0,     0,   177,   178,     0,
       0,   179,   167,   168,   180,   170,     0,     0,     0,     0,
       0,     0,     0,     0,   174,     0,     0,   175,   173,     0,
     176,     0,   152,     0,   177,   178,     0,     0,   179,   158,
       0,   180,   159,     0,     0,     0,     0,     0,     0,     0,
     167,   168,     0,   170,     0,     0,     0,   174,     0,     0,
     175,     0,   173,   176,     0,     0,     0,   177,   178,     0,
       0,   179,     0,     0,   180,   149,   150,   151,   152,   316,
     153,   154,   155,   156,   157,   158,     0,     0,   159,   160,
     161,   162,   163,   164,   165,   166,   167,   168,   169,   170,
     173,     0,  -242,     0,     0,   179,   172,     0,   149,   150,
     151,   152,   317,   153,   154,   155,   156,   157,   158,     0,
       0,   159,   160,   161,   162,   163,   164,   165,   166,   167,
     168,   169,   170,     0,     0,     0,     0,     0,     0,   172,
     178,     0,     0,   179,     0,     0,   173,     0,     0,   149,
     150,   151,   152,   322,   153,   154,   155,   156,   157,   158,
       0,     0,   159,   160,   161,   162,   163,   164,   165,   166,
     167,   168,   169,   170,     0,   174,     0,     0,   175,   173,
     172,   176,     0,     0,     0,   177,   178,     0,     0,   179,
       0,     0,   180,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,   174,     0,
       0,   175,     0,     0,   176,     0,     0,     0,   177,   178,
     173,     0,   179,     0,     0,   180,   152,     0,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   160,   161,   162,
     163,   164,   165,     0,   167,   168,   169,   170,     0,   174,
       0,     0,   175,     0,     0,   176,     0,     0,     0,   177,
     178,     0,     0,   179,     0,     0,   180,   149,   150,   151,
     152,   331,   153,   154,   155,   156,   157,   158,     0,     0,
     159,   160,   161,   162,   163,   164,   165,   166,   167,   168,
     169,   170,     0,     0,   173,     0,     0,     0,   172,     0,
     149,   150,   151,   152,   342,   153,   154,   155,   156,   157,
     158,     0,     0,   159,   160,   161,   162,   163,   164,   165,
     166,   167,   168,   169,   170,     0,     0,     0,     0,     0,
       0,   172,     0,     0,   178,     0,     0,   179,   173,     0,
     180,   149,   150,   151,   152,   345,   153,   154,   155,   156,
     157,   158,     0,     0,   159,   160,   161,   162,   163,   164,
     165,   166,   167,   168,   169,   170,     0,   174,     0,     0,
     175,   173,   172,   176,     0,     0,     0,   177,   178,     0,
       0,   179,     0,     0,   180,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     174,     0,     0,   175,     0,     0,   176,     0,     0,     0,
     177,   178,   173,     0,   179,     0,     0,   180,   152,     0,
     153,   154,   155,   156,   157,   158,     0,     0,   159,   160,
     161,   162,   163,   164,   165,     0,   167,   168,     0,   170,
       0,   174,     0,     0,   175,     0,     0,   176,     0,     0,
       0,   177,   178,     0,     0,   179,     0,     0,   180,   149,
     150,   151,   152,   346,   153,   154,   155,   156,   157,   158,
       0,     0,   159,   160,   161,   162,   163,   164,   165,   166,
     167,   168,   169,   170,     0,     0,   173,     0,     0,     0,
     172,     0,   149,   150,   151,   152,   347,   153,   154,   155,
     156,   157,   158,     0,     0,   159,   160,   161,   162,   163,
     164,   165,   166,   167,   168,   169,   170,     0,     0,     0,
       0,     0,     0,   172,     0,     0,   178,     0,     0,   179,
     173,     0,   180,   149,   150,   151,   152,   348,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   160,   161,   162,
     163,   164,   165,   166,   167,   168,   169,   170,     0,   174,
       0,     0,   175,   173,   172,   176,     0,     0,     0,   177,
     178,     0,     0,   179,     0,     0,   180,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,   174,     0,     0,   175,     0,     0,   176,     0,
       0,     0,   177,   178,   173,     0,   179,     0,     0,   180,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,   174,     0,     0,   175,     0,     0,   176,
       0,     0,     0,   177,   178,     0,     0,   179,     0,     0,
     180,   149,   150,   151,   152,     0,   153,   154,   155,   156,
     157,   158,     0,     0,   159,   160,   161,   162,   163,   164,
     165,   166,   167,   168,   169,   170,     0,     0,   349,     0,
       0,     0,   172,     0,   149,   150,   151,   152,   351,   153,
     154,   155,   156,   157,   158,     0,     0,   159,   160,   161,
     162,   163,   164,   165,   166,   167,   168,   169,   170,     0,
       0,   149,   150,   151,   152,   172,   153,   154,   155,   156,
     157,   158,   173,     0,   159,   160,   161,   162,   163,   164,
     165,   166,   167,   168,   169,   170,     0,     0,   361,     0,
       0,     0,   172,     0,     0,     0,     0,     0,     0,     0,
       0,   174,     0,     0,   175,   173,     0,   176,     0,     0,
       0,   177,   178,     0,     0,   179,     0,     0,   180,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,   173,     0,   174,     0,     0,   175,     0,     0,
     176,     0,     0,     0,   177,   178,     0,     0,   179,     0,
       0,   180,     0,     0,     0,     0,     0,     0,     0,     0,
       0,   174,     0,     0,   175,     0,     0,   176,     0,     0,
       0,   177,   178,     0,     0,   179,     0,     0,   180,   149,
     150,   151,   152,     0,   153,   154,   155,   156,   157,   158,
       0,     0,   159,   160,   161,   162,   163,   164,   165,   166,
     167,   168,   169,   170,     0,     0,     0,     0,     0,     0,
     172,     0,     0,     0,     0,   362,   149,   150,   151,   152,
     364,   153,   154,   155,   156,   157,   158,     0,     0,   159,
     160,   161,   162,   163,   164,   165,   166,   167,   168,   169,
     170,     0,     0,     0,     0,     0,     0,   172,     0,     0,
     173,     0,     0,   149,   150,   151,   152,     0,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   160,   161,   162,
     163,   164,   165,   166,   167,   168,   169,   170,     0,   174,
     369,     0,   175,     0,   172,   176,     0,   173,     0,   177,
     178,     0,     0,   179,     0,     0,   180,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,   174,     0,     0,   175,
       0,     0,   176,     0,   173,     0,   177,   178,     0,     0,
     179,     0,     0,   180,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,   174,     0,     0,   175,     0,     0,   176,
       0,     0,     0,   177,   178,     0,     0,   179,     0,     0,
     180,   149,   150,   151,   152,   374,   153,   154,   155,   156,
     157,   158,     0,     0,   159,   160,   161,   162,   163,   164,
     165,   166,   167,   168,   169,   170,     0,     0,   149,   150,
     151,   152,   172,   153,   154,   155,   156,   157,   158,     0,
     375,   159,   160,   161,   162,   163,   164,   165,   166,   167,
     168,   169,   170,     0,     0,     0,     0,     0,     0,   172,
       0,     0,     0,   151,   152,     0,   153,   154,   155,   156,
     157,   158,   173,     0,   159,   160,   161,   162,   163,   164,
     165,   166,   167,   168,   169,   170,     0,     0,     0,     0,
       0,     0,   172,     0,     0,     0,     0,     0,     0,   173,
       0,   174,     0,     0,   175,     0,     0,   176,     0,     0,
       0,   177,   178,     0,     0,   179,     0,     0,   180,     0,
       0,     0,     0,     0,     0,     0,     0,     0,   174,     0,
       0,   175,   173,     0,   176,     0,     0,     0,   177,   178,
       0,     0,   179,     0,     0,   180,   149,   150,   151,   152,
       0,   153,   154,   155,   156,   157,   158,     0,     0,   159,
     160,   161,   162,   163,   164,   165,   166,   167,   168,   169,
     170,  -242,   178,     0,     0,   179,     0,   172,   180,     0,
     149,   150,   151,   152,     0,   153,   154,   155,   156,   157,
     158,     0,   391,   159,   160,   161,   162,   163,   164,   165,
     166,   167,   168,   169,   170,     0,     0,     0,     0,     0,
       0,   172,     0,     0,     0,   151,   152,   173,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   160,   161,   162,
     163,   164,   165,   166,   167,   168,   169,   170,     0,     0,
       0,     0,     0,   401,   172,     0,   174,     0,     0,   175,
       0,   173,   176,     0,     0,     0,   177,   178,     0,     0,
     179,     0,     0,   180,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     174,     0,     0,   175,   173,     0,   176,     0,     0,     0,
     177,   178,     0,     0,   179,     0,     0,   180,   149,   150,
     151,   152,     0,   153,   154,   155,   156,   157,   158,     0,
       0,   159,   160,   161,   162,   163,   164,   165,   166,   167,
     168,   169,   170,     0,   178,     0,     0,   179,   402,   172,
     180,   149,   150,   151,   152,     0,   153,   154,   155,   156,
     157,   158,     0,     0,   159,   160,   161,   162,   163,   164,
     165,   166,   167,   168,   169,   170,     0,     0,   427,     0,
       0,     0,   172,     0,     0,   149,   150,   151,   152,   173,
     153,   154,   155,   156,   157,   158,     0,     0,   159,   160,
     161,   162,   163,   164,   165,   166,   167,   168,   169,   170,
       0,     0,     0,     0,     0,   436,   172,     0,   174,     0,
       0,   175,   173,     0,   176,     0,     0,     0,   177,   178,
       0,     0,   179,     0,     0,   180,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,   174,     0,     0,   175,     0,   173,   176,     0,     0,
       0,   177,   178,     0,     0,   179,     0,     0,   180,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,   174,     0,     0,   175,     0,
       0,   176,     0,     0,     0,   177,   178,     0,     0,   179,
       0,     0,   180,   149,   150,   151,   152,     0,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   160,   161,   162,
     163,   164,   165,   166,   167,   168,   169,   170,     0,     0,
       0,     0,     0,     0,   172,     0,     0,     0,     0,   149,
     150,   151,   152,     0,   153,   154,   155,   156,   157,   158,
     329,     0,   159,   160,   161,   162,   163,   164,   165,   166,
     167,   168,   169,   170,     0,     0,     0,     0,     0,   455,
     172,     0,     0,     0,   173,     0,   149,   150,   151,   152,
     463,   153,   154,   155,   156,   157,   158,     0,     0,   159,
     160,   161,   162,   163,   164,   165,   166,   167,   168,   169,
     170,     0,     0,   174,     0,     0,   175,   172,     0,   176,
     173,     0,     0,   177,   178,     0,     0,   179,     0,     0,
     180,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,   174,
       0,     0,   175,     0,     0,   176,     0,   173,     0,   177,
     178,     0,     0,   179,     0,     0,   180,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,   174,     0,     0,   175,
       0,     0,   176,     0,     0,     0,   177,   178,     0,     0,
     179,     0,     0,   180,   149,   150,   151,   152,     0,   153,
     154,   155,   156,   157,   158,     0,     0,   159,   160,   161,
     162,   163,   164,   165,   166,   167,   168,   169,   170,     0,
       0,   476,     0,     0,     0,   172,     0,   149,   150,   151,
     152,     0,   153,   154,   155,   156,   157,   158,     0,     0,
     159,   160,   161,   162,   163,   164,   165,   166,   167,   168,
     169,   170,     0,     0,     0,     0,     0,     0,   172,     0,
       0,     0,     0,   485,     0,   173,     0,     0,   149,   150,
     151,   152,     0,   153,   154,   155,   156,   157,   158,     0,
       0,   159,   160,   161,   162,   163,   164,   165,   166,   167,
     168,   169,   170,     0,   174,     0,     0,   175,   173,   172,
     176,     0,     0,     0,   177,   178,     0,     0,   179,     0,
       0,   180,     0,     0,   498,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,   174,     0,     0,
     175,     0,     0,   176,     0,     0,     0,   177,   178,   173,
       0,   179,     0,     0,   180,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,   174,     0,
       0,   175,     0,     0,   176,     0,     0,     0,   177,   178,
       0,     0,   179,     0,     0,   180,   149,   150,   151,   152,
       0,   153,   154,   155,   156,   157,   158,     0,     0,   159,
     160,   161,   162,   163,   164,   165,   166,   167,   168,   169,
     170,     0,     0,     0,     0,     0,     0,   172,     0,     0,
     149,   150,   151,   152,     0,   153,   154,   155,   156,   157,
     158,     0,   500,   159,   160,   161,   162,   163,   164,   165,
     166,   167,   168,   169,   170,     0,     0,   519,     0,     0,
       0,   172,     0,   149,   150,   151,   152,   173,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   160,   161,   162,
     163,   164,   165,   166,   167,   168,   169,   170,     0,     0,
       0,     0,     0,   522,   172,     0,   174,     0,     0,   175,
       0,   173,   176,     0,     0,     0,   177,   178,     0,     0,
     179,     0,     0,   180,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
     174,     0,     0,   175,   173,     0,   176,     0,     0,     0,
     177,   178,     0,     0,   179,     0,     0,   180,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,   174,     0,     0,   175,     0,     0,   176,
       0,     0,     0,   177,   178,     0,     0,   179,     0,     0,
     180,   149,   150,   151,   152,     0,   153,   154,   155,   156,
     157,   158,     0,     0,   159,   160,   161,   162,   163,   164,
     165,   166,   167,   168,   169,   170,     0,     0,     0,     0,
       0,     0,   172,     0,     0,     0,     0,   524,   149,   150,
     151,   152,     0,   153,   154,   155,   156,   157,   158,     0,
       0,   159,   160,   161,   162,   163,   164,   165,   166,   167,
     168,   169,   170,     0,     0,     0,     0,     0,   561,   172,
       0,     0,   173,     0,   149,   150,   151,   152,     0,   153,
     154,   155,   156,   157,   158,     0,     0,   159,   160,   161,
     162,   163,   164,   165,   166,   167,   168,   169,   170,     0,
       0,   174,     0,     0,   175,   172,     0,   176,     0,   173,
       0,   177,   178,     0,     0,   179,     0,     0,   180,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,   174,     0,
       0,   175,     0,     0,   176,   173,     0,     0,   177,   178,
       0,     0,   179,     0,     0,   180,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,   174,     0,     0,   175,     0,     0,
     176,     0,     0,     0,   177,   178,     0,     0,   179,     0,
       0,   180,   243,   150,   151,   152,     0,   153,   154,   155,
     156,   157,   158,     0,     0,   159,   160,   161,   162,   163,
     164,   165,   166,   167,   168,   169,   170,     0,     0,   149,
     150,   151,   152,   172,   153,   154,   155,   156,   157,   158,
       0,     0,   159,   160,   161,   162,   163,   164,   165,   166,
     167,   168,   169,   170,     0,     0,     0,     0,     0,     0,
     172,     0,   149,  -242,   151,   152,     0,   153,   154,   155,
     156,   157,   158,   173,     0,   159,   160,   161,   162,   163,
     164,   165,   166,   167,   168,   169,   170,     0,     0,     0,
       0,     0,     0,   172,     0,     0,     0,     0,     0,     0,
     173,     0,   174,     0,     0,   175,     0,     0,   176,     0,
       0,     0,   177,   178,     0,     0,   179,     0,     0,   180,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,  -242,   173,     0,   176,     0,     0,     0,   177,
     178,     0,     0,   179,     0,     0,   180,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,   176,     0,
       0,     0,   177,   178,     0,     0,   179,     0,     0,   180,
     150,   151,   152,     0,   153,   154,   155,   156,   157,   158,
       0,     0,   159,   160,   161,   162,   163,   164,   165,   166,
     167,   168,   169,   170,     0,     0,     0,     0,     0,     0,
     172,     0,   151,   152,     0,   153,   154,   155,   156,   157,
     158,     0,     0,   159,   160,   161,   162,   163,   164,   165,
     166,   167,   168,   169,   170,     0,     0,     0,     0,     0,
       0,   172,     0,     0,     0,     0,     0,     0,  -242,   152,
     173,   153,   154,   155,   156,   157,   158,     0,     0,   159,
     160,   161,   162,   163,   164,   165,   166,   167,   168,   169,
     170,     0,     0,     0,     0,     0,     0,     0,     0,   174,
       0,   173,   175,     0,     0,   176,     0,     0,     0,   177,
     178,     0,     0,   179,     0,     0,   180,   415,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,   173,     0,     0,
     177,   178,     0,     0,   179,     0,   152,   180,   153,   154,
     155,   156,   157,   158,     0,     0,   159,     0,     0,   162,
     163,   164,   165,     0,   167,   168,   152,   170,   153,   154,
     155,   156,   157,   158,     0,     0,   159,   178,     0,   162,
     179,   164,   165,   180,   167,   168,     0,   170,   152,     0,
     153,   154,   155,   156,   157,   158,     0,     0,   159,     0,
       0,   162,     0,   164,  -242,     0,   167,   168,     0,   170,
       0,     0,     0,     0,   173,     0,     0,   152,     0,   153,
     154,   155,   156,   157,   158,     0,     0,   159,     0,     0,
     162,     0,     0,     0,   173,   167,   168,     0,   170,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,   178,     0,   173,   179,     0,     0,
     180,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,   178,     0,     0,   179,     0,     0,
     180,     0,     0,     0,     0,   173,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,   178,     0,     0,   179,
       0,     0,   180,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,   178,     0,     0,   179,     0,
       0,   180
};

#define yypact_value_is_default(yystate) \
  ((yystate) == (-486))

#define yytable_value_is_error(yytable_value) \
  ((yytable_value) == (-242))

static const yytype_int16 yycheck[] =
{
       0,   323,    24,    17,   120,    15,   227,    51,   407,     4,
      10,   471,    12,    13,    14,    10,    16,    17,    13,    19,
      20,    15,    22,    14,    44,    25,    47,    27,    28,     3,
       4,    26,   379,    46,    55,    18,    36,     4,   523,    13,
      60,     3,     4,    17,    44,    45,    46,    47,    10,    49,
      50,    13,    17,    44,    54,    17,   516,    57,    58,    26,
     545,    61,   461,    46,    74,   109,    10,     3,     4,    83,
      14,    17,    46,    73,    74,    75,    76,    13,     4,    17,
      74,    17,    17,    83,    46,    49,     4,     3,     4,   436,
      15,    91,    38,    93,    94,    95,     4,    13,    17,    99,
      38,   101,    67,    38,    29,   105,   128,    81,   108,   109,
      46,   111,   112,    52,   114,   115,   116,   117,    17,    38,
      17,   121,   343,   239,    15,   119,    17,   449,   128,    28,
      46,   131,   132,    58,    17,    76,    76,   137,   133,   139,
      81,   141,    67,    17,   144,   109,   146,   121,    10,   149,
     150,   151,   152,   153,   154,   155,   156,   157,   158,   121,
     160,   161,   162,   163,   164,   165,   166,   167,   168,   169,
     170,   171,   172,    54,    55,   175,   176,   177,   178,    44,
     180,   181,    47,    17,   184,   121,     3,     4,   152,   189,
      55,    59,    60,   414,    17,    63,    13,    17,    38,   199,
      17,    17,     0,    17,   204,   121,    59,    17,    61,    15,
      24,   432,   151,   213,   214,   215,   216,   181,    17,     4,
      41,    35,   222,    29,    38,   225,    72,    46,   228,    46,
      17,    17,   232,    19,    20,    21,    22,    23,    24,    45,
      46,    27,    17,   243,   244,   466,   246,    88,   109,    35,
      36,   251,    38,   253,    26,    17,   477,     3,   258,   128,
      17,     3,    34,   132,   203,   122,    48,    56,   232,    51,
      52,    85,    77,     4,     5,    47,    44,    81,    60,    10,
     244,    53,    17,    76,    18,   285,    18,    51,    18,   253,
      60,    79,    18,    18,    18,    24,    18,   297,    46,    85,
      17,    44,    29,    41,   121,    22,    18,    24,     3,     3,
      27,   311,   312,   313,   128,    46,    77,    45,    35,    36,
     320,    38,    18,   323,   106,    45,    18,    41,   328,   329,
      51,   331,   332,   333,   171,   109,   109,    55,   338,   125,
     112,   341,   128,   109,   109,   131,   392,   311,   436,   546,
     448,    -1,    -1,    -1,    -1,   355,   356,   357,    -1,    -1,
     142,    -1,    -1,   363,    -1,    -1,   366,   367,    85,    -1,
      -1,   371,   372,   312,    -1,    -1,    -1,   377,    -1,    -1,
     380,   250,   382,    -1,    -1,    -1,    -1,    -1,   160,    -1,
      -1,    -1,   331,    -1,   394,    -1,   396,   169,    -1,    -1,
      -1,    -1,   174,    -1,    -1,    -1,    -1,   371,   125,    -1,
      -1,   128,    -1,   377,    -1,    17,    -1,    -1,    -1,    -1,
     420,   203,    24,   423,    -1,    27,    -1,   209,    -1,   429,
      -1,   431,    -1,    35,    36,    -1,    38,   219,   220,   439,
      -1,   441,    -1,   443,   226,    -1,    -1,   386,   448,   449,
     450,    -1,    -1,   453,    -1,    -1,   456,   457,   458,   459,
      -1,    -1,    -1,    -1,    -1,   465,   248,   249,   250,    -1,
     470,    -1,   472,    -1,   246,    -1,    -1,    -1,   260,   251,
      -1,    -1,    -1,    85,    -1,    -1,   355,   356,   357,    -1,
     359,    -1,   431,    -1,    -1,    -1,    -1,    17,    -1,    -1,
     439,    21,    22,    23,    24,    -1,    -1,    27,   508,    -1,
      -1,   511,   512,   513,    -1,    35,    36,    -1,    38,    -1,
      -1,   521,    -1,   523,   463,    -1,   128,   527,   528,   529,
     530,    -1,   532,    -1,   534,   535,   536,    -1,   538,   539,
      -1,    -1,   542,    -1,    -1,   545,    -1,    -1,    -1,    -1,
     489,   420,    -1,    -1,   554,    -1,   556,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   336,    85,   566,    -1,   568,    -1,
     534,   571,   536,   573,   443,    -1,    -1,   359,    -1,    -1,
     362,    -1,    -1,    -1,    -1,    -1,   525,    -1,   527,    -1,
     554,    -1,   556,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   385,    -1,   125,   388,    -1,   128,   391,
     382,     3,     4,   395,     6,    -1,    -1,    -1,    10,    -1,
      -1,    13,    14,    -1,    -1,    -1,    -1,    19,    -1,    21,
      22,   413,    -1,    -1,    -1,   407,    -1,    -1,    -1,    -1,
      -1,    -1,   424,    14,   513,    16,    17,    -1,    19,    20,
      21,    22,    23,    24,    46,    -1,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,    -1,    -1,
     539,    -1,    -1,    17,    45,    -1,    -1,    21,    22,    23,
      24,    -1,   464,    27,    -1,   467,   468,    -1,    -1,   461,
      -1,    35,    36,    -1,    38,    -1,    -1,   566,   480,   568,
      -1,    -1,   571,   485,   573,   477,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    85,   497,   498,    -1,   500,    -1,
      -1,    -1,   114,    -1,    -1,    -1,    -1,    -1,   510,   121,
      -1,    -1,   514,   125,    -1,    -1,    -1,    -1,   520,    -1,
     512,    85,   524,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,
     131,    -1,    -1,   535,    -1,    -1,   538,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
     562,   125,   564,    -1,   128,   567,     1,   569,     3,     4,
       5,     6,     7,     8,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      25,    26,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    -1,    40,    41,    -1,    -1,    44,
      45,    46,    47,    -1,    49,    50,    51,    52,    53,    54,
      55,    56,    57,    58,    59,    60,    61,    62,    63,    -1,
      65,    66,    67,    -1,    69,    70,    71,    -1,    73,    74,
      75,    -1,    77,    78,    -1,    80,    -1,    82,    83,    84,
      85,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    98,    99,   100,   101,    -1,    -1,    -1,
      -1,    -1,    -1,   108,   109,   110,   111,   112,   113,   114,
     115,   116,   117,   118,    -1,   120,   121,   122,   123,   124,
     125,    -1,   127,   128,   129,   130,   131,   132,   133,     1,
      -1,     3,     4,     5,     6,     7,     8,     9,    10,    11,
      12,    13,    14,    15,    16,    17,    18,    19,    20,    21,
      22,    23,    24,    25,    26,    27,    28,    29,    30,    31,
      32,    33,    34,    35,    36,    37,    38,    -1,    40,    41,
      -1,    -1,    44,    45,    46,    47,    -1,    49,    50,    51,
      52,    53,    54,    55,    56,    57,    58,    59,    60,    61,
      62,    63,    -1,    65,    66,    67,    -1,    69,    70,    71,
      -1,    73,    74,    75,    -1,    77,    -1,    -1,    80,    -1,
      82,    83,    84,    85,    86,    87,    -1,    89,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    98,    99,   100,   101,
      -1,    -1,    -1,    -1,    -1,    -1,   108,   109,   110,   111,
     112,   113,   114,   115,   116,   117,   118,    -1,   120,   121,
     122,   123,   124,   125,    -1,   127,   128,   129,   130,   131,
     132,   133,     1,    -1,     3,     4,     5,     6,     7,     8,
       9,    10,    11,    12,    13,    14,    15,    16,    17,    18,
      19,    20,    21,    22,    23,    24,    25,    26,    27,    28,
      29,    30,    31,    32,    33,    34,    35,    36,    37,    38,
      -1,    40,    41,    -1,    -1,    44,    45,    46,    47,    -1,
      49,    50,    51,    52,    53,    54,    55,    56,    57,    58,
      59,    60,    61,    62,    63,    -1,    65,    66,    67,    -1,
      69,    70,    71,    -1,    73,    74,    75,    -1,    77,    78,
      -1,    80,    -1,    82,    83,    84,    85,    86,    87,    -1,
      89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,
      99,   100,   101,    -1,    -1,    -1,    -1,    -1,    -1,   108,
     109,   110,   111,   112,   113,   114,   115,   116,   117,   118,
      -1,   120,   121,   122,   123,   124,   125,    -1,    -1,   128,
     129,   130,   131,   132,   133,     1,    -1,     3,     4,     5,
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
      -1,    -1,   108,    -1,   110,   111,   112,   113,   114,   115,
     116,   117,   118,    -1,   120,   121,   122,   123,   124,   125,
      -1,   127,   128,   129,   130,   131,   132,   133,     1,    -1,
       3,     4,     5,     6,     7,    -1,     9,    10,    11,    12,
      13,    14,    15,    16,    17,    -1,    19,    20,    21,    22,
      23,    24,    25,    26,    27,    28,    29,    30,    31,    32,
      33,    34,    35,    36,    37,    38,    -1,    40,    -1,    -1,
      -1,    -1,    45,    46,    47,    -1,    -1,    50,    -1,    52,
      53,    54,    -1,    -1,    57,    -1,    -1,    -1,    -1,    62,
      63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,
      73,    74,    75,    76,    -1,    78,    -1,    80,    -1,    82,
      83,    84,    85,    86,    87,    -1,    89,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,
     113,   114,   115,   116,   117,   118,    -1,   120,   121,   122,
     123,   124,   125,    -1,   127,   128,   129,   130,   131,   132,
     133,     1,    -1,     3,     4,     5,     6,     7,    -1,     9,
      10,    11,    12,    13,    14,    15,    16,    17,    -1,    19,
      20,    21,    22,    23,    24,    25,    26,    27,    28,    29,
      30,    31,    32,    33,    34,    35,    36,    37,    38,    -1,
      40,    -1,    -1,    -1,    -1,    45,    46,    47,    -1,    -1,
      -1,    -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,
      60,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,
      70,    71,    -1,    73,    74,    75,    76,    -1,    78,    -1,
      80,    -1,    82,    83,    84,    85,    86,    87,    -1,    89,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,    -1,
     100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,   109,
      -1,   111,   112,   113,   114,   115,   116,   117,   118,    -1,
     120,   121,   122,   123,   124,   125,    -1,   127,   128,   129,
     130,   131,   132,   133,     1,    -1,     3,     4,     5,     6,
       7,    -1,     9,    10,    11,    12,    13,    14,    15,    16,
      17,    -1,    19,    20,    21,    22,    23,    24,    25,    26,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    -1,    40,    -1,    -1,    -1,    -1,    45,    46,
      47,    -1,    -1,    -1,    -1,    52,    53,    54,    -1,    -1,
      57,    -1,    -1,    -1,    -1,    62,    63,    -1,    65,    -1,
      -1,    -1,    69,    70,    71,    -1,    73,    74,    75,    -1,
      -1,    78,    -1,    80,    -1,    82,    83,    84,    85,    86,
      87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,   108,   109,    -1,   111,   112,   113,   114,   115,   116,
     117,   118,    -1,   120,   121,   122,   123,   124,   125,    -1,
     127,   128,   129,   130,   131,   132,   133,     1,    -1,     3,
       4,     5,     6,     7,    -1,     9,    10,    11,    12,    13,
      -1,    -1,    -1,    17,    -1,    19,    20,    -1,    -1,    -1,
      17,    25,    26,    -1,    -1,    29,    -1,    24,    -1,    -1,
      27,    -1,    36,    37,    -1,    -1,    40,    -1,    35,    36,
      44,    38,    46,    47,    -1,    49,    -1,    51,    52,    53,
      54,    -1,    -1,    57,    -1,    -1,    -1,    -1,    62,    63,
      -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,
      74,    75,    -1,    77,    78,    -1,    80,    -1,    82,    83,
      84,    -1,    86,    87,    -1,    89,    -1,    -1,    85,    -1,
      -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,   113,
      -1,   115,   116,    -1,   118,    -1,    -1,   121,   122,   123,
      -1,   125,    -1,   127,    -1,   129,   130,    -1,   132,   133,
       1,   128,     3,     4,     5,     6,     7,    -1,     9,    10,
      11,    12,    13,    -1,    -1,    -1,    17,    -1,    19,    20,
      -1,    -1,    -1,    -1,    25,    26,    -1,    -1,    29,    -1,
      -1,    -1,    -1,    -1,    -1,    36,    37,    -1,    -1,    40,
      -1,    -1,    -1,    -1,    -1,    46,    47,    -1,    49,    -1,
      51,    52,    53,    54,    -1,    -1,    57,    -1,    -1,    -1,
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
      -1,    -1,    -1,    -1,    52,    53,    54,    55,    56,    57,
      -1,    -1,    -1,    -1,    62,    63,    -1,    65,    -1,    -1,
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
      -1,    -1,    57,    -1,    -1,    -1,    -1,    62,    63,    -1,
      65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,
      75,    -1,    77,    78,    -1,    80,    -1,    82,    83,    84,
      -1,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   108,   109,   110,   111,   112,   113,    -1,
     115,   116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,
     125,    -1,   127,    -1,   129,   130,    -1,   132,   133,     1,
      -1,     3,     4,     5,     6,     7,    -1,     9,    10,    11,
      12,    13,    -1,    -1,    -1,    17,    -1,    19,    20,    -1,
      -1,    -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,
      -1,    -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,
      -1,    -1,    44,    -1,    46,    47,    -1,    -1,    -1,    -1,
      52,    53,    54,    -1,    -1,    57,    -1,    -1,    -1,    -1,
      62,    63,    -1,    65,    66,    -1,    -1,    69,    70,    71,
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
      -1,    -1,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,
      69,    70,    71,    -1,    73,    74,    75,    -1,    -1,    78,
      79,    80,    81,    82,    83,    84,    -1,    86,    87,    -1,
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
      -1,    57,    -1,    -1,    -1,    -1,    62,    63,    -1,    65,
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
      -1,    44,    -1,    46,    47,    -1,    -1,    -1,    51,    52,
      53,    54,    -1,    -1,    57,    -1,    -1,    -1,    -1,    62,
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
      40,    -1,    -1,    -1,    44,    -1,    46,    47,    -1,    -1,
      50,    -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,
      -1,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,
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
      37,    -1,    -1,    40,    -1,    -1,    -1,    44,    -1,    46,
      47,    -1,    -1,    -1,    -1,    52,    53,    54,    -1,    -1,
      57,    -1,    -1,    -1,    -1,    62,    63,    -1,    65,    -1,
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
      54,    -1,    -1,    57,    -1,    -1,    -1,    -1,    62,    63,
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
      -1,    52,    53,    54,    -1,    56,    57,    -1,    -1,    -1,
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
      -1,    -1,    -1,    -1,    52,    53,    54,    -1,    -1,    57,
      -1,    -1,    -1,    -1,    62,    63,    -1,    65,    -1,    -1,
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
      -1,    -1,    57,    -1,    -1,    -1,    -1,    62,    63,    -1,
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
      52,    53,    54,    -1,    -1,    57,    -1,    -1,    -1,    -1,
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
      -1,    -1,    51,    52,    53,    54,    -1,    -1,    57,    -1,
      -1,    -1,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,
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
      -1,    57,    -1,    -1,    -1,    -1,    62,    63,    -1,    65,
      -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,    75,
      76,    -1,    78,    79,    80,    -1,    82,    83,    84,    -1,
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
      53,    54,    -1,    -1,    57,    -1,    -1,    -1,    -1,    62,
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
      -1,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,
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
      57,    -1,    -1,    -1,    -1,    62,    63,    -1,    65,    -1,
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
      54,    -1,    -1,    57,    -1,    -1,    -1,    -1,    62,    63,
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
      -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,    -1,
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
      -1,    -1,    -1,    -1,    62,    63,    -1,    65,    -1,    -1,
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
      -1,    -1,    57,    -1,    -1,    -1,    -1,    62,    63,    -1,
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
      52,    53,    54,    -1,    -1,    57,    -1,    -1,    -1,    -1,
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
      -1,    -1,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,
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
      -1,    57,    -1,    -1,    -1,    -1,    62,    63,    -1,    65,
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
      53,    54,    -1,    -1,    57,    -1,    -1,    -1,    -1,    62,
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
      -1,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,
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
      37,    -1,    -1,    40,    -1,    -1,    -1,    -1,    -1,    46,
      47,    -1,    -1,    -1,    -1,    52,    53,    54,    -1,    -1,
      57,    -1,    -1,    -1,    -1,    62,    63,    -1,    65,    -1,
      -1,    -1,    69,    70,    71,    -1,    73,    74,    75,    -1,
      77,    78,    -1,    80,    -1,    82,    83,    84,    -1,    86,
      87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,   108,   109,   110,   111,   112,   113,    -1,   115,   116,
      -1,   118,    -1,    -1,   121,   122,   123,    -1,   125,    -1,
     127,    -1,   129,   130,    -1,   132,   133,     1,    -1,     3,
       4,     5,     6,     7,    -1,     9,    10,    11,    12,    13,
      -1,    -1,    -1,    17,    -1,    19,    20,    -1,    -1,    -1,
      -1,    25,    26,    -1,    -1,    29,    -1,    -1,    -1,    -1,
      -1,    -1,    36,    37,    -1,    -1,    40,    -1,    -1,    -1,
      44,    -1,    46,    47,    -1,    -1,    -1,    -1,    52,    53,
      54,    -1,    -1,    57,    -1,    -1,    -1,    -1,    62,    63,
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
      41,    -1,    -1,    -1,    -1,    46,    47,    -1,    -1,    -1,
      -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,    -1,
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
      -1,    -1,    -1,    -1,    62,    63,    -1,    65,    -1,    -1,
      -1,    69,    70,    71,    -1,    73,    74,    75,    76,    -1,
      78,    -1,    80,    -1,    82,    83,    84,    -1,    86,    87,
      -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      98,    -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
     108,   109,    -1,   111,   112,   113,    -1,   115,   116,    -1,
     118,    -1,    -1,   121,   122,   123,    -1,   125,    -1,   127,
      -1,   129,   130,    -1,   132,   133,     1,    -1,     3,     4,
       5,     6,     7,    -1,     9,    10,    11,    12,    13,    -1,
      -1,    -1,    17,    18,    19,    20,    -1,    -1,    -1,    -1,
      25,    26,    -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,
      -1,    36,    37,    -1,    -1,    40,    -1,    -1,    -1,    -1,
      -1,    46,    47,    -1,    -1,    -1,    -1,    52,    53,    54,
      -1,    -1,    57,    -1,    -1,    -1,    -1,    62,    63,    -1,
      65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,
      75,    -1,    -1,    78,    -1,    80,    -1,    82,    83,    84,
      -1,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   108,   109,    -1,   111,   112,   113,    -1,
     115,   116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,
     125,    -1,   127,    -1,   129,   130,    -1,   132,   133,     1,
      -1,     3,     4,     5,     6,     7,    -1,     9,    10,    11,
      12,    13,    -1,    -1,    -1,    17,    18,    19,    20,    -1,
      -1,    -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,
      -1,    -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,
      -1,    -1,    -1,    -1,    46,    47,    -1,    -1,    -1,    -1,
      52,    53,    54,    -1,    -1,    57,    -1,    -1,    -1,    -1,
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
      -1,    40,    -1,    -1,    -1,    44,    -1,    46,    47,    -1,
      -1,    -1,    -1,    52,    53,    54,    -1,    -1,    57,    -1,
      -1,    -1,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,
      69,    70,    71,    -1,    73,    74,    75,    -1,    -1,    78,
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
      -1,    57,    -1,    -1,    -1,    -1,    62,    63,    -1,    65,
      -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,    75,
      -1,    -1,    78,    -1,    80,    -1,    82,    83,    84,    -1,
      86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,   108,   109,    -1,   111,   112,   113,    -1,   115,
     116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,   125,
      -1,   127,    -1,   129,   130,    -1,   132,   133,     1,    -1,
       3,     4,     5,     6,     7,    -1,     9,    10,    11,    12,
      13,    -1,    -1,    -1,    17,    18,    19,    20,    -1,    -1,
      -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,    -1,
      -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,    -1,
      -1,    -1,    -1,    46,    47,    -1,    -1,    -1,    -1,    52,
      53,    54,    -1,    -1,    57,    -1,    -1,    -1,    -1,    62,
      63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,
      73,    74,    75,    -1,    -1,    78,    -1,    80,    -1,    82,
      83,    84,    -1,    86,    87,    -1,    89,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,
     113,    -1,   115,   116,    -1,   118,    -1,    -1,   121,   122,
     123,    -1,   125,    -1,   127,    -1,   129,   130,    -1,   132,
     133,     1,    -1,     3,     4,     5,     6,     7,     8,     9,
      10,    11,    12,    13,    -1,    -1,    -1,    17,    -1,    19,
      20,    -1,    -1,    -1,    -1,    25,    26,    -1,    -1,    29,
      -1,    -1,    -1,    -1,    -1,    -1,    36,    37,    -1,    -1,
      40,    -1,    -1,    -1,    -1,    -1,    46,    47,    -1,    -1,
      -1,    -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,
      -1,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,
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
      57,    -1,    -1,    -1,    -1,    62,    63,    -1,    65,    -1,
      -1,    -1,    69,    70,    71,    -1,    73,    74,    75,    -1,
      -1,    78,    79,    80,    -1,    82,    83,    84,    -1,    86,
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
      54,    -1,    -1,    57,    -1,    -1,    -1,    -1,    62,    63,
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
      -1,    -1,    -1,    -1,    -1,    46,    47,    -1,    -1,    -1,
      -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,    -1,
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
      -1,    -1,    -1,    51,    52,    53,    54,    -1,    -1,    57,
      -1,    -1,    -1,    -1,    62,    63,    -1,    65,    -1,    -1,
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
      -1,    -1,    57,    -1,    -1,    -1,    -1,    62,    63,    -1,
      65,    -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,
      75,    76,    -1,    78,    -1,    80,    -1,    82,    83,    84,
      -1,    86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   108,   109,    -1,   111,   112,   113,    -1,
     115,   116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,
     125,    -1,   127,    -1,   129,   130,    -1,   132,   133,     1,
      -1,     3,     4,     5,     6,     7,    -1,     9,    10,    11,
      12,    13,    -1,    -1,    -1,    17,    18,    19,    20,    -1,
      -1,    -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,
      -1,    -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,
      -1,    -1,    -1,    -1,    46,    47,    -1,    -1,    -1,    -1,
      52,    53,    54,    -1,    -1,    57,    -1,    -1,    -1,    -1,
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
      -1,    -1,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,
      69,    70,    71,    -1,    73,    74,    75,    -1,    -1,    78,
      -1,    80,    -1,    82,    83,    84,    -1,    86,    87,    -1,
      89,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    98,
      -1,   100,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   108,
     109,    -1,   111,   112,   113,    -1,   115,   116,    -1,   118,
     119,    -1,   121,   122,   123,    -1,   125,    -1,   127,    -1,
     129,   130,    -1,   132,   133,     1,    -1,     3,     4,     5,
       6,     7,    -1,     9,    10,    11,    12,    13,    -1,    -1,
      -1,    17,    -1,    19,    20,    -1,    -1,    -1,    -1,    25,
      26,    -1,    -1,    29,    -1,    -1,    -1,    -1,    -1,    -1,
      36,    37,    -1,    -1,    40,    -1,    -1,    -1,    -1,    -1,
      46,    47,    -1,    -1,    -1,    -1,    52,    53,    54,    -1,
      -1,    57,    -1,    -1,    -1,    -1,    62,    63,    -1,    65,
      -1,    -1,    -1,    69,    70,    71,    -1,    73,    74,    75,
      -1,    -1,    78,    -1,    80,    -1,    82,    83,    84,    -1,
      86,    87,    -1,    89,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    98,    -1,   100,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,   108,   109,   110,   111,   112,   113,    -1,   115,
     116,    -1,   118,    -1,    -1,   121,   122,   123,    -1,   125,
      -1,   127,    -1,   129,   130,    -1,   132,   133,     1,    -1,
       3,     4,     5,     6,     7,    -1,     9,    10,    11,    12,
      13,    -1,    -1,    -1,    17,    -1,    19,    20,    -1,    -1,
      -1,    -1,    25,    26,    -1,    -1,    29,    -1,    -1,    -1,
      -1,    -1,    -1,    36,    37,    -1,    -1,    40,    -1,    -1,
      -1,    -1,    -1,    46,    47,    -1,    -1,    -1,    -1,    52,
      53,    54,    -1,    -1,    57,    -1,    -1,    -1,    -1,    62,
      63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,
      73,    74,    75,    -1,    -1,    78,    -1,    80,    -1,    82,
      83,    84,    -1,    86,    87,    -1,    89,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   108,   109,   110,   111,   112,
     113,    -1,   115,   116,    -1,   118,    -1,    -1,   121,   122,
     123,    -1,   125,    -1,   127,    -1,   129,   130,    -1,   132,
     133,     1,    -1,     3,     4,     5,     6,     7,    -1,     9,
      10,    11,    12,    13,    -1,    -1,    -1,    17,    -1,    19,
      20,    -1,    -1,    -1,    -1,    25,    26,    -1,    -1,    29,
      -1,    -1,    -1,    -1,    -1,    -1,    36,    37,    -1,    -1,
      40,    -1,    -1,    -1,    -1,    -1,    46,    47,    -1,    -1,
      -1,    -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,
      -1,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,    69,
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
      57,    -1,    -1,    -1,    -1,    62,    63,    -1,    65,    -1,
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
      54,    -1,    -1,    57,    -1,    -1,    -1,    -1,    62,    63,
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
      -1,    52,    53,    54,    -1,    -1,    57,    -1,    -1,    -1,
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
      -1,    -1,    -1,    -1,    62,    63,    -1,    65,    -1,    -1,
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
      -1,    -1,    57,    -1,    -1,    -1,    -1,    62,    63,    -1,
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
      52,    53,    54,    -1,    -1,    57,    -1,    -1,    -1,    -1,
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
      -1,    -1,    -1,    62,    63,    -1,    65,    -1,    -1,    -1,
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
      -1,    57,    -1,    -1,    -1,    -1,    62,    63,    -1,    65,
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
      53,    54,    -1,    -1,    57,    -1,    -1,    -1,    -1,    62,
      63,    -1,    65,    -1,    -1,    -1,    69,    70,    71,    -1,
      73,    74,    75,    -1,    -1,    78,    -1,    80,    -1,    82,
      83,    84,    -1,    86,    87,    -1,    89,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    98,    -1,   100,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   108,   109,    -1,   111,   112,
     113,    -1,   115,   116,    -1,   118,    -1,    -1,   121,   122,
     123,    -1,   125,    -1,   127,     8,   129,   130,    -1,   132,
     133,    14,    15,    16,    17,    -1,    19,    20,    21,    22,
      23,    24,    -1,    -1,    27,    28,    29,    30,    31,    32,
      33,    34,    35,    36,    37,    38,    -1,    -1,    -1,    -1,
      -1,    44,    45,    -1,    11,    -1,    -1,    14,    15,    16,
      17,    -1,    19,    20,    21,    22,    23,    24,    -1,    -1,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    -1,    -1,    -1,    -1,    -1,    -1,    45,    -1,
      -1,    -1,    85,    -1,    14,    15,    16,    17,    55,    19,
      20,    21,    22,    23,    24,    -1,    -1,    27,    28,    29,
      30,    31,    32,    33,    34,    35,    36,    37,    38,    -1,
      -1,   114,    -1,    -1,   117,    45,    -1,   120,    85,    -1,
      50,   124,   125,    -1,    -1,   128,    -1,    -1,   131,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,
     117,    -1,    -1,   120,    -1,    85,    -1,   124,   125,    -1,
      -1,   128,    -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   109,
      -1,    -1,    -1,    -1,   114,    -1,    -1,   117,    -1,    -1,
     120,    -1,    -1,    -1,   124,   125,    -1,    -1,   128,    -1,
      -1,   131,    14,    15,    16,    17,    18,    19,    20,    21,
      22,    23,    24,    -1,    -1,    27,    28,    29,    30,    31,
      32,    33,    34,    35,    36,    37,    38,    -1,    -1,    14,
      15,    16,    17,    45,    19,    20,    21,    22,    23,    24,
      -1,    26,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    -1,    -1,    -1,    -1,    -1,    -1,
      45,    -1,    -1,    -1,    16,    17,    -1,    19,    20,    21,
      22,    23,    24,    85,    -1,    27,    28,    29,    30,    31,
      32,    33,    34,    35,    36,    37,    38,    -1,    -1,    -1,
      -1,    -1,    -1,    45,    -1,    -1,    -1,    -1,    -1,    -1,
      85,    -1,   114,    -1,    -1,   117,    -1,    -1,   120,    -1,
      -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,
      -1,    -1,   117,    85,    -1,   120,    -1,    -1,    -1,   124,
     125,    -1,    -1,   128,    -1,    -1,   131,    14,    15,    16,
      17,    -1,    19,    20,    21,    22,    23,    24,    -1,    -1,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,   124,   125,    -1,    -1,   128,    -1,    45,   131,
      -1,    14,    15,    16,    17,    -1,    19,    20,    21,    22,
      23,    24,    -1,    60,    27,    28,    29,    30,    31,    32,
      33,    34,    35,    36,    37,    38,    -1,    -1,    -1,    -1,
      -1,    -1,    45,    -1,    -1,    -1,    -1,    -1,    85,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    60,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,
     117,    -1,    85,   120,    -1,    -1,    -1,   124,   125,    -1,
      -1,   128,    -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,   114,    -1,    -1,   117,    -1,    -1,   120,    -1,    -1,
      -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,    14,
      15,    16,    17,    -1,    19,    20,    21,    22,    23,    24,
      -1,    -1,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    -1,    -1,    14,    15,    16,    17,
      45,    19,    20,    21,    22,    23,    24,    -1,    -1,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,    -1,    -1,    -1,    -1,    -1,    -1,    45,    -1,    14,
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      85,    -1,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    99,    -1,    -1,    -1,    -1,    -1,
      45,    -1,    -1,    -1,    -1,    -1,    -1,    85,    -1,   114,
      -1,    -1,   117,    -1,    -1,   120,    -1,    -1,    -1,   124,
     125,    -1,    -1,   128,    -1,    -1,   131,    -1,    -1,    -1,
      -1,   109,    -1,    -1,    -1,    -1,   114,    -1,    -1,   117,
      85,    -1,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,
     128,    -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,
      -1,    -1,   117,    -1,    -1,   120,    -1,    -1,    -1,   124,
     125,    -1,    -1,   128,    -1,    -1,   131,    14,    15,    16,
      17,    18,    19,    20,    21,    22,    23,    24,    -1,    -1,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    -1,    -1,    14,    15,    16,    17,    45,    19,
      20,    21,    22,    23,    24,    -1,    -1,    27,    28,    29,
      30,    31,    32,    33,    34,    35,    36,    37,    38,    -1,
      -1,    41,    -1,    -1,    -1,    45,    -1,    14,    15,    16,
      17,    18,    19,    20,    21,    22,    23,    24,    85,    -1,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    -1,    -1,    -1,    -1,    -1,    -1,    45,    -1,
      -1,    -1,    -1,    -1,    17,    85,    -1,   114,    -1,    -1,
     117,    24,    -1,   120,    27,    -1,    -1,   124,   125,    -1,
      -1,   128,    35,    36,   131,    38,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   114,    -1,    -1,   117,    85,    -1,
     120,    -1,    17,    -1,   124,   125,    -1,    -1,   128,    24,
      -1,   131,    27,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      35,    36,    -1,    38,    -1,    -1,    -1,   114,    -1,    -1,
     117,    -1,    85,   120,    -1,    -1,    -1,   124,   125,    -1,
      -1,   128,    -1,    -1,   131,    14,    15,    16,    17,    18,
      19,    20,    21,    22,    23,    24,    -1,    -1,    27,    28,
      29,    30,    31,    32,    33,    34,    35,    36,    37,    38,
      85,    -1,   125,    -1,    -1,   128,    45,    -1,    14,    15,
      16,    17,    18,    19,    20,    21,    22,    23,    24,    -1,
      -1,    27,    28,    29,    30,    31,    32,    33,    34,    35,
      36,    37,    38,    -1,    -1,    -1,    -1,    -1,    -1,    45,
     125,    -1,    -1,   128,    -1,    -1,    85,    -1,    -1,    14,
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      -1,    -1,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    -1,   114,    -1,    -1,   117,    85,
      45,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,   128,
      -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,
      -1,   117,    -1,    -1,   120,    -1,    -1,    -1,   124,   125,
      85,    -1,   128,    -1,    -1,   131,    17,    -1,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    28,    29,    30,
      31,    32,    33,    -1,    35,    36,    37,    38,    -1,   114,
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
     124,   125,    85,    -1,   128,    -1,    -1,   131,    17,    -1,
      19,    20,    21,    22,    23,    24,    -1,    -1,    27,    28,
      29,    30,    31,    32,    33,    -1,    35,    36,    -1,    38,
      -1,   114,    -1,    -1,   117,    -1,    -1,   120,    -1,    -1,
      -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,    14,
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      -1,    -1,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    -1,    -1,    85,    -1,    -1,    -1,
      45,    -1,    14,    15,    16,    17,    18,    19,    20,    21,
      22,    23,    24,    -1,    -1,    27,    28,    29,    30,    31,
      32,    33,    34,    35,    36,    37,    38,    -1,    -1,    -1,
      -1,    -1,    -1,    45,    -1,    -1,   125,    -1,    -1,   128,
      85,    -1,   131,    14,    15,    16,    17,    18,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,    -1,   114,
      -1,    -1,   117,    85,    45,   120,    -1,    -1,    -1,   124,
     125,    -1,    -1,   128,    -1,    -1,   131,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,   114,    -1,    -1,   117,    -1,    -1,   120,    -1,
      -1,    -1,   124,   125,    85,    -1,   128,    -1,    -1,   131,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   114,    -1,    -1,   117,    -1,    -1,   120,
      -1,    -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,
     131,    14,    15,    16,    17,    -1,    19,    20,    21,    22,
      23,    24,    -1,    -1,    27,    28,    29,    30,    31,    32,
      33,    34,    35,    36,    37,    38,    -1,    -1,    41,    -1,
      -1,    -1,    45,    -1,    14,    15,    16,    17,    18,    19,
      20,    21,    22,    23,    24,    -1,    -1,    27,    28,    29,
      30,    31,    32,    33,    34,    35,    36,    37,    38,    -1,
      -1,    14,    15,    16,    17,    45,    19,    20,    21,    22,
      23,    24,    85,    -1,    27,    28,    29,    30,    31,    32,
      33,    34,    35,    36,    37,    38,    -1,    -1,    41,    -1,
      -1,    -1,    45,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,   114,    -1,    -1,   117,    85,    -1,   120,    -1,    -1,
      -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    85,    -1,   114,    -1,    -1,   117,    -1,    -1,
     120,    -1,    -1,    -1,   124,   125,    -1,    -1,   128,    -1,
      -1,   131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,   114,    -1,    -1,   117,    -1,    -1,   120,    -1,    -1,
      -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,    14,
      15,    16,    17,    -1,    19,    20,    21,    22,    23,    24,
      -1,    -1,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    -1,    -1,    -1,    -1,    -1,    -1,
      45,    -1,    -1,    -1,    -1,    50,    14,    15,    16,    17,
      18,    19,    20,    21,    22,    23,    24,    -1,    -1,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,    -1,    -1,    -1,    -1,    -1,    -1,    45,    -1,    -1,
      85,    -1,    -1,    14,    15,    16,    17,    -1,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,    -1,   114,
      41,    -1,   117,    -1,    45,   120,    -1,    85,    -1,   124,
     125,    -1,    -1,   128,    -1,    -1,   131,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,   117,
      -1,    -1,   120,    -1,    85,    -1,   124,   125,    -1,    -1,
     128,    -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   114,    -1,    -1,   117,    -1,    -1,   120,
      -1,    -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,
     131,    14,    15,    16,    17,    18,    19,    20,    21,    22,
      23,    24,    -1,    -1,    27,    28,    29,    30,    31,    32,
      33,    34,    35,    36,    37,    38,    -1,    -1,    14,    15,
      16,    17,    45,    19,    20,    21,    22,    23,    24,    -1,
      26,    27,    28,    29,    30,    31,    32,    33,    34,    35,
      36,    37,    38,    -1,    -1,    -1,    -1,    -1,    -1,    45,
      -1,    -1,    -1,    16,    17,    -1,    19,    20,    21,    22,
      23,    24,    85,    -1,    27,    28,    29,    30,    31,    32,
      33,    34,    35,    36,    37,    38,    -1,    -1,    -1,    -1,
      -1,    -1,    45,    -1,    -1,    -1,    -1,    -1,    -1,    85,
      -1,   114,    -1,    -1,   117,    -1,    -1,   120,    -1,    -1,
      -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,
      -1,   117,    85,    -1,   120,    -1,    -1,    -1,   124,   125,
      -1,    -1,   128,    -1,    -1,   131,    14,    15,    16,    17,
      -1,    19,    20,    21,    22,    23,    24,    -1,    -1,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,   124,   125,    -1,    -1,   128,    -1,    45,   131,    -1,
      14,    15,    16,    17,    -1,    19,    20,    21,    22,    23,
      24,    -1,    60,    27,    28,    29,    30,    31,    32,    33,
      34,    35,    36,    37,    38,    -1,    -1,    -1,    -1,    -1,
      -1,    45,    -1,    -1,    -1,    16,    17,    85,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,    -1,    -1,
      -1,    -1,    -1,    77,    45,    -1,   114,    -1,    -1,   117,
      -1,    85,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,
     128,    -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
     114,    -1,    -1,   117,    85,    -1,   120,    -1,    -1,    -1,
     124,   125,    -1,    -1,   128,    -1,    -1,   131,    14,    15,
      16,    17,    -1,    19,    20,    21,    22,    23,    24,    -1,
      -1,    27,    28,    29,    30,    31,    32,    33,    34,    35,
      36,    37,    38,    -1,   125,    -1,    -1,   128,    44,    45,
     131,    14,    15,    16,    17,    -1,    19,    20,    21,    22,
      23,    24,    -1,    -1,    27,    28,    29,    30,    31,    32,
      33,    34,    35,    36,    37,    38,    -1,    -1,    41,    -1,
      -1,    -1,    45,    -1,    -1,    14,    15,    16,    17,    85,
      19,    20,    21,    22,    23,    24,    -1,    -1,    27,    28,
      29,    30,    31,    32,    33,    34,    35,    36,    37,    38,
      -1,    -1,    -1,    -1,    -1,    44,    45,    -1,   114,    -1,
      -1,   117,    85,    -1,   120,    -1,    -1,    -1,   124,   125,
      -1,    -1,   128,    -1,    -1,   131,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,   114,    -1,    -1,   117,    -1,    85,   120,    -1,    -1,
      -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,   117,    -1,
      -1,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,   128,
      -1,    -1,   131,    14,    15,    16,    17,    -1,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,    -1,    -1,
      -1,    -1,    -1,    -1,    45,    -1,    -1,    -1,    -1,    14,
      15,    16,    17,    -1,    19,    20,    21,    22,    23,    24,
      61,    -1,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    -1,    -1,    -1,    -1,    -1,    44,
      45,    -1,    -1,    -1,    85,    -1,    14,    15,    16,    17,
      18,    19,    20,    21,    22,    23,    24,    -1,    -1,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,    -1,    -1,   114,    -1,    -1,   117,    45,    -1,   120,
      85,    -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,
     131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,
      -1,    -1,   117,    -1,    -1,   120,    -1,    85,    -1,   124,
     125,    -1,    -1,   128,    -1,    -1,   131,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,   117,
      -1,    -1,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,
     128,    -1,    -1,   131,    14,    15,    16,    17,    -1,    19,
      20,    21,    22,    23,    24,    -1,    -1,    27,    28,    29,
      30,    31,    32,    33,    34,    35,    36,    37,    38,    -1,
      -1,    41,    -1,    -1,    -1,    45,    -1,    14,    15,    16,
      17,    -1,    19,    20,    21,    22,    23,    24,    -1,    -1,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    -1,    -1,    -1,    -1,    -1,    -1,    45,    -1,
      -1,    -1,    -1,    50,    -1,    85,    -1,    -1,    14,    15,
      16,    17,    -1,    19,    20,    21,    22,    23,    24,    -1,
      -1,    27,    28,    29,    30,    31,    32,    33,    34,    35,
      36,    37,    38,    -1,   114,    -1,    -1,   117,    85,    45,
     120,    -1,    -1,    -1,   124,   125,    -1,    -1,   128,    -1,
      -1,   131,    -1,    -1,    60,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,    -1,
     117,    -1,    -1,   120,    -1,    -1,    -1,   124,   125,    85,
      -1,   128,    -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,
      -1,   117,    -1,    -1,   120,    -1,    -1,    -1,   124,   125,
      -1,    -1,   128,    -1,    -1,   131,    14,    15,    16,    17,
      -1,    19,    20,    21,    22,    23,    24,    -1,    -1,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,    -1,    -1,    -1,    -1,    -1,    -1,    45,    -1,    -1,
      14,    15,    16,    17,    -1,    19,    20,    21,    22,    23,
      24,    -1,    60,    27,    28,    29,    30,    31,    32,    33,
      34,    35,    36,    37,    38,    -1,    -1,    41,    -1,    -1,
      -1,    45,    -1,    14,    15,    16,    17,    85,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,    -1,    -1,
      -1,    -1,    -1,    44,    45,    -1,   114,    -1,    -1,   117,
      -1,    85,   120,    -1,    -1,    -1,   124,   125,    -1,    -1,
     128,    -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
     114,    -1,    -1,   117,    85,    -1,   120,    -1,    -1,    -1,
     124,   125,    -1,    -1,   128,    -1,    -1,   131,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,   114,    -1,    -1,   117,    -1,    -1,   120,
      -1,    -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,
     131,    14,    15,    16,    17,    -1,    19,    20,    21,    22,
      23,    24,    -1,    -1,    27,    28,    29,    30,    31,    32,
      33,    34,    35,    36,    37,    38,    -1,    -1,    -1,    -1,
      -1,    -1,    45,    -1,    -1,    -1,    -1,    50,    14,    15,
      16,    17,    -1,    19,    20,    21,    22,    23,    24,    -1,
      -1,    27,    28,    29,    30,    31,    32,    33,    34,    35,
      36,    37,    38,    -1,    -1,    -1,    -1,    -1,    44,    45,
      -1,    -1,    85,    -1,    14,    15,    16,    17,    -1,    19,
      20,    21,    22,    23,    24,    -1,    -1,    27,    28,    29,
      30,    31,    32,    33,    34,    35,    36,    37,    38,    -1,
      -1,   114,    -1,    -1,   117,    45,    -1,   120,    -1,    85,
      -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,    -1,
      -1,   117,    -1,    -1,   120,    85,    -1,    -1,   124,   125,
      -1,    -1,   128,    -1,    -1,   131,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   114,    -1,    -1,   117,    -1,    -1,
     120,    -1,    -1,    -1,   124,   125,    -1,    -1,   128,    -1,
      -1,   131,    14,    15,    16,    17,    -1,    19,    20,    21,
      22,    23,    24,    -1,    -1,    27,    28,    29,    30,    31,
      32,    33,    34,    35,    36,    37,    38,    -1,    -1,    14,
      15,    16,    17,    45,    19,    20,    21,    22,    23,    24,
      -1,    -1,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    -1,    -1,    -1,    -1,    -1,    -1,
      45,    -1,    14,    15,    16,    17,    -1,    19,    20,    21,
      22,    23,    24,    85,    -1,    27,    28,    29,    30,    31,
      32,    33,    34,    35,    36,    37,    38,    -1,    -1,    -1,
      -1,    -1,    -1,    45,    -1,    -1,    -1,    -1,    -1,    -1,
      85,    -1,   114,    -1,    -1,   117,    -1,    -1,   120,    -1,
      -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,   117,    85,    -1,   120,    -1,    -1,    -1,   124,
     125,    -1,    -1,   128,    -1,    -1,   131,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   120,    -1,
      -1,    -1,   124,   125,    -1,    -1,   128,    -1,    -1,   131,
      15,    16,    17,    -1,    19,    20,    21,    22,    23,    24,
      -1,    -1,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    -1,    -1,    -1,    -1,    -1,    -1,
      45,    -1,    16,    17,    -1,    19,    20,    21,    22,    23,
      24,    -1,    -1,    27,    28,    29,    30,    31,    32,    33,
      34,    35,    36,    37,    38,    -1,    -1,    -1,    -1,    -1,
      -1,    45,    -1,    -1,    -1,    -1,    -1,    -1,    16,    17,
      85,    19,    20,    21,    22,    23,    24,    -1,    -1,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,   114,
      -1,    85,   117,    -1,    -1,   120,    -1,    -1,    -1,   124,
     125,    -1,    -1,   128,    -1,    -1,   131,   101,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    85,    -1,    -1,
     124,   125,    -1,    -1,   128,    -1,    17,   131,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,    -1,    -1,    30,
      31,    32,    33,    -1,    35,    36,    17,    38,    19,    20,
      21,    22,    23,    24,    -1,    -1,    27,   125,    -1,    30,
     128,    32,    33,   131,    35,    36,    -1,    38,    17,    -1,
      19,    20,    21,    22,    23,    24,    -1,    -1,    27,    -1,
      -1,    30,    -1,    32,    33,    -1,    35,    36,    -1,    38,
      -1,    -1,    -1,    -1,    85,    -1,    -1,    17,    -1,    19,
      20,    21,    22,    23,    24,    -1,    -1,    27,    -1,    -1,
      30,    -1,    -1,    -1,    85,    35,    36,    -1,    38,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   125,    -1,    85,   128,    -1,    -1,
     131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,   125,    -1,    -1,   128,    -1,    -1,
     131,    -1,    -1,    -1,    -1,    85,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,   125,    -1,    -1,   128,
      -1,    -1,   131,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,   125,    -1,    -1,   128,    -1,
      -1,   131
};

/* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
   symbol of state STATE-NUM.  */
static const yytype_uint8 yystos[] =
{
       0,     1,     3,     4,     5,     6,     7,     9,    10,    11,
      12,    13,    17,    19,    20,    25,    26,    29,    36,    37,
      40,    46,    47,    52,    53,    54,    57,    62,    63,    65,
      69,    70,    71,    73,    74,    75,    78,    80,    82,    83,
      84,    86,    87,    89,    98,   100,   108,   109,   111,   112,
     113,   115,   116,   118,   121,   122,   123,   125,   127,   129,
     130,   132,   133,   137,   138,   139,   140,   142,     4,     5,
      10,    46,    46,    15,    17,    17,    38,   139,     4,   139,
     139,   139,    13,    78,   139,   139,     4,   139,   139,   148,
       4,    17,   139,    17,    17,    17,   139,     3,    13,    17,
     140,    17,   139,   139,   139,   149,    76,   156,    17,    17,
     140,    17,   109,   139,    17,    17,    17,    38,    10,   150,
     151,    17,    83,   139,   139,   139,   139,   140,   149,   148,
     139,   149,   149,   156,   140,   139,   151,    38,   139,    17,
     139,    47,    55,   162,   149,   139,    17,     0,     8,    14,
      15,    16,    17,    19,    20,    21,    22,    23,    24,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,    44,    45,    85,   114,   117,   120,   124,   125,   128,
     131,    17,    24,   128,    17,     4,     4,    10,    13,    26,
     133,   139,    18,   139,    18,   139,   139,    18,    26,    26,
      26,    41,   139,    50,   109,   139,   139,     4,    11,    55,
     161,   139,   147,    15,    29,    58,    67,   153,   139,    60,
      60,    44,    66,   139,   163,    81,   141,   144,   149,    72,
     139,   148,    17,   139,   139,   139,   139,   139,    88,    14,
     150,   139,    99,    14,    17,    77,   109,   158,   109,   109,
      51,   109,   158,    17,   122,   139,    18,   139,   149,    77,
      50,   139,   139,   139,   139,   156,   148,   139,   139,   139,
     139,   139,   139,   139,   140,   139,   139,   139,   139,   139,
     139,   139,   139,   139,   140,    40,   139,     8,   138,   139,
       6,    10,    14,    19,    21,    22,   114,   125,   140,   139,
     139,   139,   139,   139,   148,     3,   139,   139,    18,    18,
      41,    17,    18,   149,   156,   139,    18,    18,    18,     3,
     149,    56,    18,    44,   139,   139,   139,   139,    59,    61,
     152,    18,   149,   149,   139,   139,    79,   143,   149,   141,
      77,    17,    18,    18,   148,    18,    18,    18,    18,    41,
     150,    18,   139,   148,    77,   149,   149,   149,   158,    51,
     148,    41,    50,   149,    18,    18,    28,    67,   139,    41,
     139,    17,    38,    18,    18,    26,   148,    17,   139,   156,
      49,    51,   109,   157,   158,   159,    51,    76,    60,   139,
     147,    60,   139,   139,    59,    60,    63,   139,   156,    77,
      77,    77,    44,     3,     4,    10,    13,    17,   140,   145,
     146,    77,   139,   141,    18,   101,    18,   158,   158,   158,
     149,   158,    18,   149,    51,   139,   139,    41,   148,    40,
     139,    15,    74,    24,    18,   139,    44,   109,   155,   159,
     139,    49,    51,   149,   156,    54,    55,   160,   149,    44,
     149,   152,   139,   149,   139,    44,    15,    29,    45,    46,
     146,    14,    44,    18,    76,   149,    76,   141,   119,   158,
      29,    77,   149,    41,    18,   139,    41,    74,   139,   156,
     141,     3,   155,   139,   156,    50,   139,   158,     3,    45,
      77,    11,   161,   147,    77,    44,    60,   154,    60,    77,
      60,   139,   139,   139,   139,    18,   146,   156,   149,    77,
     141,   149,   109,   149,   119,   139,    44,   162,    77,    41,
     141,   149,    44,   149,    50,    45,   156,    18,   149,   149,
     149,    77,   149,    77,   110,   109,   110,   158,   109,   149,
     162,    41,   149,    77,   157,   149,   156,   139,   156,    77,
      77,    77,    77,   148,   110,   148,   110,   158,    77,   157,
     160,    44,   109,   148,   109,   148,   149,   109,   149,   109,
     158,   149,   158,   149,   158,   158
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
   Once GCC version 2 has supplanted version 1, this can go.  However,
   YYFAIL appears to be in use.  Nevertheless, it is formally deprecated
   in Bison 2.4.2's NEWS entry, where a plan to phase it out is
   discussed.  */

#define YYFAIL		goto yyerrlab
#if defined YYFAIL
  /* This is here to suppress warnings from the GCC cpp's
     -Wunused-macros.  Normally we don't worry about that warning, but
     some users do, and we want to make it easy for users to remove
     YYFAIL uses, which will produce warnings from Bison 2.5.  */
#endif

#define YYRECOVERING()  (!!yyerrstatus)

#define YYBACKUP(Token, Value)					\
do								\
  if (yychar == YYEMPTY && yylen == 1)				\
    {								\
      yychar = (Token);						\
      yylval = (Value);						\
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


/* This macro is provided for backward compatibility. */

#ifndef YY_LOCATION_PRINT
# define YY_LOCATION_PRINT(File, Loc) ((void) 0)
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
yy_stack_print (yytype_int16 *yybottom, yytype_int16 *yytop)
#else
static void
yy_stack_print (yybottom, yytop)
    yytype_int16 *yybottom;
    yytype_int16 *yytop;
#endif
{
  YYFPRINTF (stderr, "Stack now");
  for (; yybottom <= yytop; yybottom++)
    {
      int yybot = *yybottom;
      YYFPRINTF (stderr, " %d", yybot);
    }
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
      YYFPRINTF (stderr, "   $%d = ", yyi + 1);
      yy_symbol_print (stderr, yyrhs[yyprhs[yyrule] + yyi],
		       &(yyvsp[(yyi + 1) - (yynrhs)])
		       		       , scanner);
      YYFPRINTF (stderr, "\n");
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

/* Copy into *YYMSG, which is of size *YYMSG_ALLOC, an error message
   about the unexpected token YYTOKEN for the state stack whose top is
   YYSSP.

   Return 0 if *YYMSG was successfully written.  Return 1 if *YYMSG is
   not large enough to hold the message.  In that case, also set
   *YYMSG_ALLOC to the required number of bytes.  Return 2 if the
   required number of bytes is too large to store.  */
static int
yysyntax_error (YYSIZE_T *yymsg_alloc, char **yymsg,
                yytype_int16 *yyssp, int yytoken)
{
  YYSIZE_T yysize0 = yytnamerr (0, yytname[yytoken]);
  YYSIZE_T yysize = yysize0;
  YYSIZE_T yysize1;
  enum { YYERROR_VERBOSE_ARGS_MAXIMUM = 5 };
  /* Internationalized format string. */
  const char *yyformat = 0;
  /* Arguments of yyformat. */
  char const *yyarg[YYERROR_VERBOSE_ARGS_MAXIMUM];
  /* Number of reported tokens (one for the "unexpected", one per
     "expected"). */
  int yycount = 0;

  /* There are many possibilities here to consider:
     - Assume YYFAIL is not used.  It's too flawed to consider.  See
       <http://lists.gnu.org/archive/html/bison-patches/2009-12/msg00024.html>
       for details.  YYERROR is fine as it does not invoke this
       function.
     - If this state is a consistent state with a default action, then
       the only way this function was invoked is if the default action
       is an error action.  In that case, don't check for expected
       tokens because there are none.
     - The only way there can be no lookahead present (in yychar) is if
       this state is a consistent state with a default action.  Thus,
       detecting the absence of a lookahead is sufficient to determine
       that there is no unexpected or expected token to report.  In that
       case, just report a simple "syntax error".
     - Don't assume there isn't a lookahead just because this state is a
       consistent state with a default action.  There might have been a
       previous inconsistent state, consistent state with a non-default
       action, or user semantic action that manipulated yychar.
     - Of course, the expected token list depends on states to have
       correct lookahead information, and it depends on the parser not
       to perform extra reductions after fetching a lookahead from the
       scanner and before detecting a syntax error.  Thus, state merging
       (from LALR or IELR) and default reductions corrupt the expected
       token list.  However, the list is correct for canonical LR with
       one exception: it will still contain any token that will not be
       accepted due to an error action in a later state.
  */
  if (yytoken != YYEMPTY)
    {
      int yyn = yypact[*yyssp];
      yyarg[yycount++] = yytname[yytoken];
      if (!yypact_value_is_default (yyn))
        {
          /* Start YYX at -YYN if negative to avoid negative indexes in
             YYCHECK.  In other words, skip the first -YYN actions for
             this state because they are default actions.  */
          int yyxbegin = yyn < 0 ? -yyn : 0;
          /* Stay within bounds of both yycheck and yytname.  */
          int yychecklim = YYLAST - yyn + 1;
          int yyxend = yychecklim < YYNTOKENS ? yychecklim : YYNTOKENS;
          int yyx;

          for (yyx = yyxbegin; yyx < yyxend; ++yyx)
            if (yycheck[yyx + yyn] == yyx && yyx != YYTERROR
                && !yytable_value_is_error (yytable[yyx + yyn]))
              {
                if (yycount == YYERROR_VERBOSE_ARGS_MAXIMUM)
                  {
                    yycount = 1;
                    yysize = yysize0;
                    break;
                  }
                yyarg[yycount++] = yytname[yyx];
                yysize1 = yysize + yytnamerr (0, yytname[yyx]);
                if (! (yysize <= yysize1
                       && yysize1 <= YYSTACK_ALLOC_MAXIMUM))
                  return 2;
                yysize = yysize1;
              }
        }
    }

  switch (yycount)
    {
# define YYCASE_(N, S)                      \
      case N:                               \
        yyformat = S;                       \
      break
      YYCASE_(0, YY_("syntax error"));
      YYCASE_(1, YY_("syntax error, unexpected %s"));
      YYCASE_(2, YY_("syntax error, unexpected %s, expecting %s"));
      YYCASE_(3, YY_("syntax error, unexpected %s, expecting %s or %s"));
      YYCASE_(4, YY_("syntax error, unexpected %s, expecting %s or %s or %s"));
      YYCASE_(5, YY_("syntax error, unexpected %s, expecting %s or %s or %s or %s"));
# undef YYCASE_
    }

  yysize1 = yysize + yystrlen (yyformat);
  if (! (yysize <= yysize1 && yysize1 <= YYSTACK_ALLOC_MAXIMUM))
    return 2;
  yysize = yysize1;

  if (*yymsg_alloc < yysize)
    {
      *yymsg_alloc = 2 * yysize;
      if (! (yysize <= *yymsg_alloc
             && *yymsg_alloc <= YYSTACK_ALLOC_MAXIMUM))
        *yymsg_alloc = YYSTACK_ALLOC_MAXIMUM;
      return 1;
    }

  /* Avoid sprintf, as that infringes on the user's name space.
     Don't have undefined behavior even if the translation
     produced a string with the wrong number of "%s"s.  */
  {
    char *yyp = *yymsg;
    int yyi = 0;
    while ((*yyp = *yyformat) != '\0')
      if (*yyp == '%' && yyformat[1] == 's' && yyi < yycount)
        {
          yyp += yytnamerr (yyp, yyarg[yyi++]);
          yyformat += 2;
        }
      else
        {
          yyp++;
          yyformat++;
        }
  }
  return 0;
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
  //yydebug=1;
/* The lookahead symbol.  */
int yychar;

/* The semantic value of the lookahead symbol.  */
YYSTYPE yylval;

    /* Number of syntax errors so far.  */
    int yynerrs;

    int yystate;
    /* Number of tokens to shift before error messages enabled.  */
    int yyerrstatus;

    /* The stacks and their tools:
       `yyss': related to states.
       `yyvs': related to semantic values.

       Refer to the stacks thru separate pointers, to allow yyoverflow
       to reallocate them elsewhere.  */

    /* The state stack.  */
    yytype_int16 yyssa[YYINITDEPTH];
    yytype_int16 *yyss;
    yytype_int16 *yyssp;

    /* The semantic value stack.  */
    YYSTYPE yyvsa[YYINITDEPTH];
    YYSTYPE *yyvs;
    YYSTYPE *yyvsp;

    YYSIZE_T yystacksize;

  int yyn;
  int yyresult;
  /* Lookahead token as an internal (translated) token number.  */
  int yytoken;
  /* The variables used to return semantic value and location from the
     action routines.  */
  YYSTYPE yyval;

#if YYERROR_VERBOSE
  /* Buffer for error messages, and its allocated size.  */
  char yymsgbuf[128];
  char *yymsg = yymsgbuf;
  YYSIZE_T yymsg_alloc = sizeof yymsgbuf;
#endif

#define YYPOPSTACK(N)   (yyvsp -= (N), yyssp -= (N))

  /* The number of symbols on the RHS of the reduced rule.
     Keep to zero when no symbol should be popped.  */
  int yylen = 0;

  yytoken = 0;
  yyss = yyssa;
  yyvs = yyvsa;
  yystacksize = YYINITDEPTH;

  YYDPRINTF ((stderr, "Starting parse\n"));

  yystate = 0;
  yyerrstatus = 0;
  yynerrs = 0;
  yychar = YYEMPTY; /* Cause a token to be read.  */

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
	YYSTACK_RELOCATE (yyss_alloc, yyss);
	YYSTACK_RELOCATE (yyvs_alloc, yyvs);
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

  if (yystate == YYFINAL)
    YYACCEPT;

  goto yybackup;

/*-----------.
| yybackup.  |
`-----------*/
yybackup:

  /* Do appropriate processing given the current state.  Read a
     lookahead token if we need one and don't already have one.  */

  /* First try to decide what to do without reference to lookahead token.  */
  yyn = yypact[yystate];
  if (yypact_value_is_default (yyn))
    goto yydefault;

  /* Not known => get a lookahead token if don't already have one.  */

  /* YYCHAR is either YYEMPTY or YYEOF or a valid lookahead symbol.  */
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
      if (yytable_value_is_error (yyn))
        goto yyerrlab;
      yyn = -yyn;
      goto yyreduce;
    }

  /* Count tokens shifted since error; after three, turn off error
     status.  */
  if (yyerrstatus)
    yyerrstatus--;

  /* Shift the lookahead token.  */
  YY_SYMBOL_PRINT ("Shifting", yytoken, &yylval, &yylloc);

  /* Discard the shifted token.  */
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

/* Line 1806 of yacc.c  */
#line 187 "input_parser.yy"
    {   const giac::context * contextptr = giac_yyget_extra(scanner);
			    if ((yyvsp[(1) - (1)])._VECTptr->size()==1)
			     parsed_gen((yyvsp[(1) - (1)])._VECTptr->front(),contextptr);
                          else
			     parsed_gen(gen(*(yyvsp[(1) - (1)])._VECTptr,_SEQ__VECT),contextptr);
			 }
    break;

  case 3:

/* Line 1806 of yacc.c  */
#line 195 "input_parser.yy"
    { (yyval)=vecteur(1,(yyvsp[(1) - (2)])); }
    break;

  case 4:

/* Line 1806 of yacc.c  */
#line 196 "input_parser.yy"
    { if ((yyvsp[(2) - (3)]).val==1) (yyval)=vecteur(1,symbolic(at_nodisp,(yyvsp[(1) - (3)]))); else (yyval)=vecteur(1,(yyvsp[(1) - (3)])); }
    break;

  case 5:

/* Line 1806 of yacc.c  */
#line 197 "input_parser.yy"
    { if ((yyvsp[(2) - (3)]).val==1) (yyval)=mergevecteur(makevecteur(symbolic(at_nodisp,(yyvsp[(1) - (3)]))),*(yyvsp[(3) - (3)])._VECTptr); else (yyval)=mergevecteur(makevecteur((yyvsp[(1) - (3)])),*(yyvsp[(3) - (3)])._VECTptr); }
    break;

  case 6:

/* Line 1806 of yacc.c  */
#line 200 "input_parser.yy"
    {(yyval) = (yyvsp[(1) - (1)]);}
    break;

  case 7:

/* Line 1806 of yacc.c  */
#line 201 "input_parser.yy"
    {if (is_one((yyvsp[(1) - (2)]))) (yyval)=(yyvsp[(2) - (2)]); else (yyval)=symbolic(at_prod,gen(makevecteur((yyvsp[(1) - (2)]),(yyvsp[(2) - (2)])),_SEQ__VECT));}
    break;

  case 8:

/* Line 1806 of yacc.c  */
#line 202 "input_parser.yy"
    {if (is_one((yyvsp[(1) - (4)]))) (yyval)=symb_pow((yyvsp[(2) - (4)]),(yyvsp[(4) - (4)])); else (yyval)=symbolic(at_prod,gen(makevecteur((yyvsp[(1) - (4)]),symb_pow((yyvsp[(2) - (4)]),(yyvsp[(4) - (4)]))),_SEQ__VECT));}
    break;

  case 9:

/* Line 1806 of yacc.c  */
#line 203 "input_parser.yy"
    {(yyval)=symbolic(at_prod,gen(makevecteur((yyvsp[(1) - (3)]),symb_pow((yyvsp[(2) - (3)]),(yyvsp[(3) - (3)]))) ,_SEQ__VECT));}
    break;

  case 10:

/* Line 1806 of yacc.c  */
#line 204 "input_parser.yy"
    {if (is_one((yyvsp[(1) - (2)]))) (yyval)=(yyvsp[(2) - (2)]); else	(yyval)=symbolic(at_prod,gen(makevecteur((yyvsp[(1) - (2)]),(yyvsp[(2) - (2)])) ,_SEQ__VECT));}
    break;

  case 11:

/* Line 1806 of yacc.c  */
#line 205 "input_parser.yy"
    { (yyval) =(yyvsp[(1) - (5)])*symbolic(*(yyvsp[(2) - (5)])._FUNCptr,(yyvsp[(4) - (5)])); }
    break;

  case 12:

/* Line 1806 of yacc.c  */
#line 206 "input_parser.yy"
    { (yyval) =(yyvsp[(1) - (7)])*symb_pow(symbolic(*(yyvsp[(2) - (7)])._FUNCptr,(yyvsp[(4) - (7)])),(yyvsp[(7) - (7)])); }
    break;

  case 13:

/* Line 1806 of yacc.c  */
#line 208 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 14:

/* Line 1806 of yacc.c  */
#line 209 "input_parser.yy"
    { if ((yyvsp[(1) - (1)]).type==_FUNC) (yyval)=symbolic(*(yyvsp[(1) - (1)])._FUNCptr,gen(vecteur(0),_SEQ__VECT)); else (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 15:

/* Line 1806 of yacc.c  */
#line 212 "input_parser.yy"
    {(yyval) = symb_program_sto((yyvsp[(3) - (6)]),(yyvsp[(3) - (6)])*zero,(yyvsp[(6) - (6)]),(yyvsp[(1) - (6)]),false,giac_yyget_extra(scanner));}
    break;

  case 16:

/* Line 1806 of yacc.c  */
#line 213 "input_parser.yy"
    {if (is_array_index((yyvsp[(1) - (6)]),(yyvsp[(3) - (6)]),giac_yyget_extra(scanner)) || (abs_calc_mode(giac_yyget_extra(scanner))==38 && (yyvsp[(1) - (6)]).type==_IDNT && strlen((yyvsp[(1) - (6)])._IDNTptr->id_name)==2 && check_vect_38((yyvsp[(1) - (6)])._IDNTptr->id_name))) (yyval)=symbolic(at_sto,gen(makevecteur((yyvsp[(6) - (6)]),symbolic(at_of,gen(makevecteur((yyvsp[(1) - (6)]),(yyvsp[(3) - (6)])) ,_SEQ__VECT))) ,_SEQ__VECT)); else { (yyval) = symb_program_sto((yyvsp[(3) - (6)]),(yyvsp[(3) - (6)])*zero,(yyvsp[(6) - (6)]),(yyvsp[(1) - (6)]),true,giac_yyget_extra(scanner)); (yyval)._SYMBptr->feuille.subtype=_SORTED__VECT;  } }
    break;

  case 17:

/* Line 1806 of yacc.c  */
#line 214 "input_parser.yy"
    {if (is_array_index((yyvsp[(3) - (6)]),(yyvsp[(5) - (6)]),giac_yyget_extra(scanner)) || (abs_calc_mode(giac_yyget_extra(scanner))==38 && (yyvsp[(3) - (6)]).type==_IDNT && check_vect_38((yyvsp[(3) - (6)])._IDNTptr->id_name))) (yyval)=symbolic(at_sto,gen(makevecteur((yyvsp[(1) - (6)]),symbolic(at_of,gen(makevecteur((yyvsp[(3) - (6)]),(yyvsp[(5) - (6)])) ,_SEQ__VECT))) ,_SEQ__VECT)); else (yyval) = symb_program_sto((yyvsp[(5) - (6)]),(yyvsp[(5) - (6)])*zero,(yyvsp[(1) - (6)]),(yyvsp[(3) - (6)]),false,giac_yyget_extra(scanner));}
    break;

  case 18:

/* Line 1806 of yacc.c  */
#line 215 "input_parser.yy"
    { 
         const giac::context * contextptr = giac_yyget_extra(scanner);
         gen g=symb_at((yyvsp[(3) - (6)]),(yyvsp[(5) - (6)]),contextptr); (yyval)=symb_sto((yyvsp[(1) - (6)]),g); 
        }
    break;

  case 19:

/* Line 1806 of yacc.c  */
#line 219 "input_parser.yy"
    { 
         const giac::context * contextptr = giac_yyget_extra(scanner);
         gen g=symbolic(at_of,gen(makevecteur((yyvsp[(3) - (8)]),(yyvsp[(6) - (8)])) ,_SEQ__VECT)); (yyval)=symb_sto((yyvsp[(1) - (8)]),g); 
        }
    break;

  case 20:

/* Line 1806 of yacc.c  */
#line 223 "input_parser.yy"
    { if ((yyvsp[(3) - (3)]).type==_IDNT && unit_conversion_map().find((yyvsp[(3) - (3)]).print(context0).c_str()+1) != unit_conversion_map().end()) (yyval)=symbolic(at_convert,gen(makevecteur((yyvsp[(1) - (3)]),symbolic(at_unit,makevecteur(1,(yyvsp[(3) - (3)])))) ,_SEQ__VECT)); else (yyval)=symb_sto((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])); }
    break;

  case 21:

/* Line 1806 of yacc.c  */
#line 224 "input_parser.yy"
    { (yyval)=symbolic(at_convert,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 22:

/* Line 1806 of yacc.c  */
#line 225 "input_parser.yy"
    { (yyval)=symbolic(at_convert,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 23:

/* Line 1806 of yacc.c  */
#line 226 "input_parser.yy"
    { (yyval)=symbolic(at_convert,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 24:

/* Line 1806 of yacc.c  */
#line 227 "input_parser.yy"
    { (yyval)=symbolic(at_convert,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 25:

/* Line 1806 of yacc.c  */
#line 228 "input_parser.yy"
    { (yyval)=symbolic(at_time,(yyvsp[(1) - (3)]));}
    break;

  case 26:

/* Line 1806 of yacc.c  */
#line 229 "input_parser.yy"
    { (yyval)=symbolic(at_POLYFORM,gen(makevecteur((yyvsp[(1) - (3)]),at_eval),_SEQ__VECT));}
    break;

  case 27:

/* Line 1806 of yacc.c  */
#line 230 "input_parser.yy"
    { (yyval)=symbolic(at_convert,gen(makevecteur((yyvsp[(1) - (4)]),symb_unit(plus_one,(yyvsp[(4) - (4)]),giac_yyget_extra(scanner))),_SEQ__VECT)); opened_quote(giac_yyget_extra(scanner)) &= 0x7ffffffd;}
    break;

  case 28:

/* Line 1806 of yacc.c  */
#line 231 "input_parser.yy"
    {(yyval) = check_symb_of((yyvsp[(1) - (4)]),(yyvsp[(3) - (4)]),giac_yyget_extra(scanner));}
    break;

  case 29:

/* Line 1806 of yacc.c  */
#line 232 "input_parser.yy"
    {(yyval) = check_symb_of((yyvsp[(1) - (4)]),(yyvsp[(3) - (4)]),giac_yyget_extra(scanner));}
    break;

  case 30:

/* Line 1806 of yacc.c  */
#line 233 "input_parser.yy"
    {(yyval) = (yyvsp[(1) - (1)]);}
    break;

  case 31:

/* Line 1806 of yacc.c  */
#line 234 "input_parser.yy"
    {(yyval) = (yyvsp[(1) - (1)]);}
    break;

  case 32:

/* Line 1806 of yacc.c  */
#line 235 "input_parser.yy"
    {(yyval) = (yyvsp[(1) - (1)]);}
    break;

  case 33:

/* Line 1806 of yacc.c  */
#line 236 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (3)])._FUNCptr,(yyvsp[(3) - (3)]));}
    break;

  case 34:

/* Line 1806 of yacc.c  */
#line 237 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (4)])._FUNCptr,(yyvsp[(3) - (4)]));}
    break;

  case 35:

/* Line 1806 of yacc.c  */
#line 238 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (3)])._FUNCptr,gen(vecteur(0),_SEQ__VECT));}
    break;

  case 36:

/* Line 1806 of yacc.c  */
#line 239 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(3) - (3)])._FUNCptr,(yyvsp[(1) - (3)]));}
    break;

  case 37:

/* Line 1806 of yacc.c  */
#line 240 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT));}
    break;

  case 38:

/* Line 1806 of yacc.c  */
#line 241 "input_parser.yy"
    {(yyval) = symb_and(symbolic(*(yyvsp[(2) - (5)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (5)]),(yyvsp[(3) - (5)])),_SEQ__VECT)),symbolic(*(yyvsp[(4) - (5)])._FUNCptr,gen(makevecteur((yyvsp[(3) - (5)]),(yyvsp[(5) - (5)])),_SEQ__VECT)));}
    break;

  case 39:

/* Line 1806 of yacc.c  */
#line 242 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(2) - (3)])._FUNCptr,makesequence((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]))); }
    break;

  case 40:

/* Line 1806 of yacc.c  */
#line 243 "input_parser.yy"
    { 
	if ((yyvsp[(2) - (2)]).type==_SYMB) (yyval)=(yyvsp[(2) - (2)]); else (yyval)=symbolic(at_nop,(yyvsp[(2) - (2)])); 
	(yyval).change_subtype(_SPREAD__SYMB); 
        const giac::context * contextptr = giac_yyget_extra(scanner);
       spread_formula(false,contextptr); 
	}
    break;

  case 41:

/* Line 1806 of yacc.c  */
#line 249 "input_parser.yy"
    { if ((yyvsp[(1) - (3)]).is_symb_of_sommet(at_plus) && (yyvsp[(1) - (3)])._SYMBptr->feuille.type==_VECT){ (yyvsp[(1) - (3)])._SYMBptr->feuille._VECTptr->push_back((yyvsp[(3) - (3)])); (yyval)=(yyvsp[(1) - (3)]); } else
  (yyval) =symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT));}
    break;

  case 42:

/* Line 1806 of yacc.c  */
#line 251 "input_parser.yy"
    {(yyval) = symb_plus((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]).type<_IDNT?-(yyvsp[(3) - (3)]):symbolic(at_neg,(yyvsp[(3) - (3)])));}
    break;

  case 43:

/* Line 1806 of yacc.c  */
#line 252 "input_parser.yy"
    {(yyval) = symb_plus((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]).type<_IDNT?-(yyvsp[(3) - (3)]):symbolic(at_neg,(yyvsp[(3) - (3)])));}
    break;

  case 44:

/* Line 1806 of yacc.c  */
#line 253 "input_parser.yy"
    {(yyval) =symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT));}
    break;

  case 45:

/* Line 1806 of yacc.c  */
#line 254 "input_parser.yy"
    {(yyval) =symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT));}
    break;

  case 46:

/* Line 1806 of yacc.c  */
#line 255 "input_parser.yy"
    {if ((yyvsp[(1) - (3)])==symbolic(at_exp,1) && (yyvsp[(2) - (3)])==at_pow) (yyval)=symbolic(at_exp,(yyvsp[(3) - (3)])); else (yyval) =symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT));}
    break;

  case 47:

/* Line 1806 of yacc.c  */
#line 256 "input_parser.yy"
    {if ((yyvsp[(2) - (3)]).type==_FUNC) (yyval)=symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT)); else (yyval) = symbolic(at_normalmod,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT));}
    break;

  case 48:

/* Line 1806 of yacc.c  */
#line 257 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 49:

/* Line 1806 of yacc.c  */
#line 260 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])),_SEQ__VECT));}
    break;

  case 50:

/* Line 1806 of yacc.c  */
#line 261 "input_parser.yy"
    {(yyval)= symbolic(at_deuxpoints,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT));}
    break;

  case 51:

/* Line 1806 of yacc.c  */
#line 262 "input_parser.yy"
    { 
					if ((yyvsp[(2) - (2)])==unsigned_inf)
						(yyval) = minus_inf;
					else { if ((yyvsp[(2) - (2)]).type==_INT_) (yyval)=(-(yyvsp[(2) - (2)]).val); else (yyval)=symbolic(at_neg,(yyvsp[(2) - (2)])); }
				}
    break;

  case 52:

/* Line 1806 of yacc.c  */
#line 267 "input_parser.yy"
    { 
					if ((yyvsp[(2) - (2)])==unsigned_inf)
						(yyval) = minus_inf;
					else { if ((yyvsp[(2) - (2)]).type==_INT_ || (yyvsp[(2) - (2)]).type==_DOUBLE_ || (yyvsp[(2) - (2)]).type==_FLOAT_) (yyval)=-(yyvsp[(2) - (2)]); else (yyval)=symbolic(at_neg,(yyvsp[(2) - (2)])); }
				}
    break;

  case 53:

/* Line 1806 of yacc.c  */
#line 272 "input_parser.yy"
    {
					if ((yyvsp[(2) - (2)])==unsigned_inf)
						(yyval) = plus_inf;
					else
						(yyval) = (yyvsp[(2) - (2)]);
				}
    break;

  case 54:

/* Line 1806 of yacc.c  */
#line 278 "input_parser.yy"
    {(yyval) = polynome_or_sparse_poly1((yyvsp[(2) - (5)]),(yyvsp[(4) - (5)]));}
    break;

  case 55:

/* Line 1806 of yacc.c  */
#line 279 "input_parser.yy"
    { 
           if ( ((yyvsp[(2) - (3)]).type==_SYMB) && ((yyvsp[(2) - (3)])._SYMBptr->sommet==at_deuxpoints) )
             (yyval) = algebraic_EXTension((yyvsp[(2) - (3)])._SYMBptr->feuille._VECTptr->front(),(yyvsp[(2) - (3)])._SYMBptr->feuille._VECTptr->back());
           else (yyval)=(yyvsp[(2) - (3)]);
        }
    break;

  case 56:

/* Line 1806 of yacc.c  */
#line 285 "input_parser.yy"
    { (yyval)=gen(at_of,2); }
    break;

  case 57:

/* Line 1806 of yacc.c  */
#line 286 "input_parser.yy"
    {(yyval) = symb_sto((yyvsp[(3) - (3)]),(yyvsp[(1) - (3)]),(yyvsp[(2) - (3)])==at_array_sto); if ((yyvsp[(3) - (3)]).is_symb_of_sommet(at_program)) *logptr(giac_yyget_extra(scanner))<<"// End defining "<<(yyvsp[(1) - (3)])<<endl;}
    break;

  case 58:

/* Line 1806 of yacc.c  */
#line 287 "input_parser.yy"
    { (yyval) = symbolic(*(yyvsp[(1) - (2)])._FUNCptr,(yyvsp[(2) - (2)]));}
    break;

  case 59:

/* Line 1806 of yacc.c  */
#line 288 "input_parser.yy"
    {(yyval) = symb_args((yyvsp[(3) - (4)]));}
    break;

  case 60:

/* Line 1806 of yacc.c  */
#line 289 "input_parser.yy"
    {(yyval) = symb_args((yyvsp[(3) - (4)]));}
    break;

  case 61:

/* Line 1806 of yacc.c  */
#line 290 "input_parser.yy"
    { (yyval)=symb_args(vecteur(0)); }
    break;

  case 62:

/* Line 1806 of yacc.c  */
#line 291 "input_parser.yy"
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

  case 63:

/* Line 1806 of yacc.c  */
#line 301 "input_parser.yy"
    {
	if ((yyvsp[(3) - (4)]).type==_VECT && (yyvsp[(3) - (4)])._VECTptr->empty())
          giac_yyerror(scanner,"void argument");
	(yyval) = symbolic(*(yyvsp[(1) - (4)])._FUNCptr,(yyvsp[(3) - (4)]));	
	}
    break;

  case 64:

/* Line 1806 of yacc.c  */
#line 306 "input_parser.yy"
    { 
          const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval)=symb_at((yyvsp[(1) - (4)]),(yyvsp[(3) - (4)]),contextptr);
        }
    break;

  case 65:

/* Line 1806 of yacc.c  */
#line 310 "input_parser.yy"
    {
	(yyval) = symbolic(*(yyvsp[(1) - (3)])._FUNCptr,gen(vecteur(0),_SEQ__VECT));
	if (*(yyvsp[(1) - (3)])._FUNCptr==at_rpn)
          rpn_mode(giac_yyget_extra(scanner))=1;
	if (*(yyvsp[(1) - (3)])._FUNCptr==at_alg)
          rpn_mode(giac_yyget_extra(scanner))=0;
	}
    break;

  case 66:

/* Line 1806 of yacc.c  */
#line 317 "input_parser.yy"
    {
	(yyval) = (yyvsp[(1) - (1)]);
	}
    break;

  case 67:

/* Line 1806 of yacc.c  */
#line 320 "input_parser.yy"
    {(yyval) = symbolic(at_derive,(yyvsp[(1) - (2)]));}
    break;

  case 68:

/* Line 1806 of yacc.c  */
#line 321 "input_parser.yy"
    { (yyval)=symbolic(*(yyvsp[(2) - (2)])._FUNCptr,(yyvsp[(1) - (2)])); }
    break;

  case 69:

/* Line 1806 of yacc.c  */
#line 322 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (6)])._FUNCptr,makevecteur(equaltosame((yyvsp[(2) - (6)])),symb_bloc((yyvsp[(4) - (6)])),symb_bloc((yyvsp[(6) - (6)]))));}
    break;

  case 70:

/* Line 1806 of yacc.c  */
#line 323 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (4)])._FUNCptr,makevecteur(equaltosame((yyvsp[(2) - (4)])),(yyvsp[(4) - (4)]),0));}
    break;

  case 71:

/* Line 1806 of yacc.c  */
#line 324 "input_parser.yy"
    {
	(yyval) = symbolic(*(yyvsp[(1) - (5)])._FUNCptr,makevecteur(equaltosame((yyvsp[(2) - (5)])),symb_bloc((yyvsp[(4) - (5)])),(yyvsp[(5) - (5)])));
	}
    break;

  case 72:

/* Line 1806 of yacc.c  */
#line 327 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (4)])._FUNCptr,(yyvsp[(3) - (4)]));}
    break;

  case 73:

/* Line 1806 of yacc.c  */
#line 328 "input_parser.yy"
    {(yyval) = (yyvsp[(1) - (1)]);}
    break;

  case 74:

/* Line 1806 of yacc.c  */
#line 329 "input_parser.yy"
    {(yyval) = symb_program((yyvsp[(3) - (4)]));}
    break;

  case 75:

/* Line 1806 of yacc.c  */
#line 330 "input_parser.yy"
    {(yyval) = gen(at_program,3);}
    break;

  case 76:

/* Line 1806 of yacc.c  */
#line 331 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
         (yyval) = symb_program((yyvsp[(1) - (3)]),zero*(yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]),contextptr);
        }
    break;

  case 77:

/* Line 1806 of yacc.c  */
#line 335 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
             if ((yyvsp[(3) - (3)]).type==_VECT) 
                (yyval) = symb_program((yyvsp[(1) - (3)]),zero*(yyvsp[(1) - (3)]),symb_bloc(makevecteur(at_nop,(yyvsp[(3) - (3)]))),contextptr); 
             else 
                (yyval) = symb_program((yyvsp[(1) - (3)]),zero*(yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]),contextptr);
		}
    break;

  case 78:

/* Line 1806 of yacc.c  */
#line 342 "input_parser.yy"
    {(yyval) = symb_bloc((yyvsp[(3) - (4)]));}
    break;

  case 79:

/* Line 1806 of yacc.c  */
#line 343 "input_parser.yy"
    {(yyval) = at_bloc;}
    break;

  case 80:

/* Line 1806 of yacc.c  */
#line 345 "input_parser.yy"
    { (yyval)=symbolic(*(yyvsp[(1) - (2)])._FUNCptr,(yyvsp[(2) - (2)])); }
    break;

  case 81:

/* Line 1806 of yacc.c  */
#line 346 "input_parser.yy"
    {(yyval) = gen(*(yyvsp[(1) - (1)])._FUNCptr,0);}
    break;

  case 82:

/* Line 1806 of yacc.c  */
#line 347 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (3)]);}
    break;

  case 83:

/* Line 1806 of yacc.c  */
#line 349 "input_parser.yy"
    {(yyval) = symbolic(at_break,zero);}
    break;

  case 84:

/* Line 1806 of yacc.c  */
#line 350 "input_parser.yy"
    {(yyval) = symbolic(at_continue,zero);}
    break;

  case 85:

/* Line 1806 of yacc.c  */
#line 351 "input_parser.yy"
    { 
	/*
	  gen kk(identificateur("index"));
	  vecteur v(*$6._VECTptr);
          const giac::context * contextptr = giac_yyget_extra(scanner);
	  v.insert(v.begin(),symb_sto(symb_at($4,kk,contextptr),$2));
	  $$=symbolic(*$1._FUNCptr,makevecteur(symb_sto(xcas_mode(contextptr)!=0,kk),symb_inferieur_strict(kk,symb_size($4)+(xcas_mode(contextptr)!=0)),symb_sto(symb_plus(kk,plus_one),kk),symb_bloc(v))); 
          */
          if ((yyvsp[(7) - (7)]).type==_INT_ && (yyvsp[(7) - (7)]).val && (yyvsp[(7) - (7)]).val!=2 && (yyvsp[(7) - (7)]).val!=9)
	    giac_yyerror(scanner,"missing loop end delimiter");
          if ((yyvsp[(4) - (7)]).is_symb_of_sommet(at_interval) &&	(yyvsp[(4) - (7)])._SYMBptr->feuille.type==_VECT 
             && (yyvsp[(4) - (7)])._SYMBptr->feuille._VECTptr->size()==2 &&
	     (yyvsp[(4) - (7)])._SYMBptr->feuille._VECTptr->front().type==_INT_ && 
	     (yyvsp[(4) - (7)])._SYMBptr->feuille._VECTptr->back().type==_INT_ )
            (yyval)=symbolic(*(yyvsp[(1) - (7)])._FUNCptr,makevecteur(symb_sto((yyvsp[(4) - (7)])._SYMBptr->feuille._VECTptr->front(),(yyvsp[(2) - (7)])),symb_inferieur_egal((yyvsp[(2) - (7)]),(yyvsp[(4) - (7)])._SYMBptr->feuille._VECTptr->back()),symb_sto(symb_plus((yyvsp[(2) - (7)]),1),(yyvsp[(2) - (7)])),symb_bloc((yyvsp[(6) - (7)]))));
          else 
            (yyval)=symbolic(*(yyvsp[(1) - (7)])._FUNCptr,makevecteur(1,symbolic(*(yyvsp[(1) - (7)])._FUNCptr,makevecteur((yyvsp[(2) - (7)]),(yyvsp[(4) - (7)]))),1,symb_bloc((yyvsp[(6) - (7)]))));
	  }
    break;

  case 86:

/* Line 1806 of yacc.c  */
#line 369 "input_parser.yy"
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

  case 87:

/* Line 1806 of yacc.c  */
#line 382 "input_parser.yy"
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

  case 88:

/* Line 1806 of yacc.c  */
#line 395 "input_parser.yy"
    { 
          if ((yyvsp[(7) - (7)]).type==_INT_ && (yyvsp[(7) - (7)]).val && (yyvsp[(7) - (7)]).val!=2 && (yyvsp[(7) - (7)]).val!=9) giac_yyerror(scanner,"missing loop end delimiter");
          (yyval)=symbolic(*(yyvsp[(1) - (7)])._FUNCptr,makevecteur(symb_sto((yyvsp[(3) - (7)]),(yyvsp[(2) - (7)])),plus_one,symb_sto(symb_plus((yyvsp[(2) - (7)]),(yyvsp[(4) - (7)])),(yyvsp[(2) - (7)])),symb_bloc((yyvsp[(6) - (7)])))); 
        }
    break;

  case 89:

/* Line 1806 of yacc.c  */
#line 399 "input_parser.yy"
    { 
          if ((yyvsp[(9) - (9)]).type==_INT_ && (yyvsp[(9) - (9)]).val && (yyvsp[(9) - (9)]).val!=2 && (yyvsp[(9) - (9)]).val!=9 && (yyvsp[(9) - (9)]).val!=8) giac_yyerror(scanner,"missing loop end delimiter");
          (yyval)=symbolic(*(yyvsp[(1) - (9)])._FUNCptr,makevecteur(symb_sto((yyvsp[(3) - (9)]),(yyvsp[(2) - (9)])),(yyvsp[(6) - (9)]),symb_sto(symb_plus((yyvsp[(2) - (9)]),(yyvsp[(4) - (9)])),(yyvsp[(2) - (9)])),symb_bloc((yyvsp[(8) - (9)])))); 
        }
    break;

  case 90:

/* Line 1806 of yacc.c  */
#line 403 "input_parser.yy"
    {(yyval) = gen(*(yyvsp[(1) - (1)])._FUNCptr,4);}
    break;

  case 91:

/* Line 1806 of yacc.c  */
#line 408 "input_parser.yy"
    { 
        vecteur v=gen2vecteur((yyvsp[(2) - (4)]));
        v.push_back(symb_ifte(equaltosame((yyvsp[(4) - (4)])),symbolic(at_break,zero),0));
	(yyval)=symbolic(*(yyvsp[(1) - (4)])._FUNCptr,makevecteur(zero,1,zero,symb_bloc(v))); 
	}
    break;

  case 92:

/* Line 1806 of yacc.c  */
#line 413 "input_parser.yy"
    { 
        if ((yyvsp[(5) - (5)]).type==_INT_ && (yyvsp[(5) - (5)]).val && (yyvsp[(5) - (5)]).val!=2 && (yyvsp[(5) - (5)]).val!=9) giac_yyerror(scanner,"missing loop end delimiter");
        vecteur v=gen2vecteur((yyvsp[(2) - (5)]));
        v.push_back(symb_ifte(equaltosame((yyvsp[(4) - (5)])),symbolic(at_break,zero),0));
	(yyval)=symbolic(*(yyvsp[(1) - (5)])._FUNCptr,makevecteur(zero,1,zero,symb_bloc(v))); 
	}
    break;

  case 93:

/* Line 1806 of yacc.c  */
#line 419 "input_parser.yy"
    {
          if ((yyvsp[(7) - (7)]).type==_INT_ && (yyvsp[(7) - (7)]).val && (yyvsp[(7) - (7)]).val!=4) giac_yyerror(scanner,"missing iferr end delimiter");
           (yyval)=symbolic(at_try_catch,makevecteur(symb_bloc((yyvsp[(2) - (7)])),0,symb_bloc((yyvsp[(4) - (7)])),symb_bloc((yyvsp[(6) - (7)]))));
        }
    break;

  case 94:

/* Line 1806 of yacc.c  */
#line 423 "input_parser.yy"
    {(yyval)=symbolic(at_piecewise,(yyvsp[(2) - (3)])); }
    break;

  case 95:

/* Line 1806 of yacc.c  */
#line 424 "input_parser.yy"
    { 
	(yyval)=(yyvsp[(1) - (1)]); 
	// $$.subtype=1; 
	}
    break;

  case 96:

/* Line 1806 of yacc.c  */
#line 428 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (3)]); /* $$.subtype=1; */ }
    break;

  case 97:

/* Line 1806 of yacc.c  */
#line 429 "input_parser.yy"
    { (yyval) = symb_dollar((yyvsp[(2) - (2)])); }
    break;

  case 98:

/* Line 1806 of yacc.c  */
#line 430 "input_parser.yy"
    {(yyval)=symb_dollar(gen(makevecteur((yyvsp[(1) - (5)]),(yyvsp[(3) - (5)]),(yyvsp[(5) - (5)])) ,_SEQ__VECT));}
    break;

  case 99:

/* Line 1806 of yacc.c  */
#line 431 "input_parser.yy"
    { (yyval) = symb_dollar(gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 100:

/* Line 1806 of yacc.c  */
#line 432 "input_parser.yy"
    { (yyval) = symb_dollar(gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 101:

/* Line 1806 of yacc.c  */
#line 433 "input_parser.yy"
    { (yyval)=symb_dollar((yyvsp[(2) - (2)])); }
    break;

  case 102:

/* Line 1806 of yacc.c  */
#line 434 "input_parser.yy"
    { (yyval) = symb_compose(gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 103:

/* Line 1806 of yacc.c  */
#line 435 "input_parser.yy"
    { (yyval) = symb_union(gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 104:

/* Line 1806 of yacc.c  */
#line 436 "input_parser.yy"
    { (yyval) = symb_intersect(gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 105:

/* Line 1806 of yacc.c  */
#line 437 "input_parser.yy"
    { (yyval) = symb_minus(gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); }
    break;

  case 106:

/* Line 1806 of yacc.c  */
#line 438 "input_parser.yy"
    { 
	(yyval)=symbolic(*(yyvsp[(2) - (3)])._FUNCptr,gen(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])) ,_SEQ__VECT)); 
	}
    break;

  case 107:

/* Line 1806 of yacc.c  */
#line 441 "input_parser.yy"
    { (yyval) = (yyvsp[(1) - (1)]); }
    break;

  case 108:

/* Line 1806 of yacc.c  */
#line 442 "input_parser.yy"
    {if ((yyvsp[(2) - (3)]).type==_FUNC) (yyval)=(yyvsp[(2) - (3)]); else { 
          // const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval)=symb_quote((yyvsp[(2) - (3)]));
          } 
        }
    break;

  case 109:

/* Line 1806 of yacc.c  */
#line 447 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
	  (yyval) = symb_at((yyvsp[(1) - (4)]),(yyvsp[(3) - (4)]),contextptr);
        }
    break;

  case 110:

/* Line 1806 of yacc.c  */
#line 451 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
	  (yyval) = symbolic(at_of,gen(makevecteur((yyvsp[(1) - (6)]),(yyvsp[(4) - (6)])) ,_SEQ__VECT));
        }
    break;

  case 111:

/* Line 1806 of yacc.c  */
#line 455 "input_parser.yy"
    {(yyval) = check_symb_of((yyvsp[(2) - (6)]),(yyvsp[(5) - (6)]),giac_yyget_extra(scanner));}
    break;

  case 112:

/* Line 1806 of yacc.c  */
#line 456 "input_parser.yy"
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

  case 113:

/* Line 1806 of yacc.c  */
#line 473 "input_parser.yy"
    { 
        // cerr << $2 << endl;
        (yyval) = gen(*((yyvsp[(2) - (3)])._VECTptr),(yyvsp[(1) - (3)]).val);
	if ((yyvsp[(2) - (3)])._VECTptr->size()==1 && (yyvsp[(2) - (3)])._VECTptr->front().is_symb_of_sommet(at_ti_semi) ) {
	  (yyval)=(yyvsp[(2) - (3)])._VECTptr->front();
        }
        // cerr << $$ << endl;

        }
    break;

  case 114:

/* Line 1806 of yacc.c  */
#line 482 "input_parser.yy"
    { 
         if ((yyvsp[(1) - (3)]).type==_VECT && (yyvsp[(1) - (3)]).subtype==_SEQ__VECT && !((yyvsp[(3) - (3)]).type==_VECT && (yyvsp[(2) - (3)]).subtype==_SEQ__VECT)){ (yyval)=(yyvsp[(1) - (3)]); (yyval)._VECTptr->push_back((yyvsp[(3) - (3)])); }
	 else
           (yyval) = makesuite((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])); 

        }
    break;

  case 115:

/* Line 1806 of yacc.c  */
#line 488 "input_parser.yy"
    { (yyval)=gen(vecteur(0),_SEQ__VECT); }
    break;

  case 116:

/* Line 1806 of yacc.c  */
#line 489 "input_parser.yy"
    {(yyval)=symb_findhelp((yyvsp[(2) - (2)]));}
    break;

  case 117:

/* Line 1806 of yacc.c  */
#line 490 "input_parser.yy"
    { (yyval)=symb_interrogation((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])); }
    break;

  case 118:

/* Line 1806 of yacc.c  */
#line 491 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval)=symb_unit(plus_one,(yyvsp[(2) - (2)]),contextptr); 
          opened_quote(giac_yyget_extra(scanner)) &= 0x7ffffffd;	
        }
    break;

  case 119:

/* Line 1806 of yacc.c  */
#line 496 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval)=symb_unit((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]),contextptr); 
          opened_quote(giac_yyget_extra(scanner)) &= 0x7ffffffd;        }
    break;

  case 120:

/* Line 1806 of yacc.c  */
#line 500 "input_parser.yy"
    { (yyval)=symb_pow((yyvsp[(1) - (2)]),(yyvsp[(2) - (2)])); }
    break;

  case 121:

/* Line 1806 of yacc.c  */
#line 501 "input_parser.yy"
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

  case 122:

/* Line 1806 of yacc.c  */
#line 510 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 123:

/* Line 1806 of yacc.c  */
#line 511 "input_parser.yy"
    { (yyval)=symbolic(*(yyvsp[(1) - (2)])._FUNCptr,(yyvsp[(2) - (2)])); }
    break;

  case 124:

/* Line 1806 of yacc.c  */
#line 512 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (1)])._FUNCptr,gen(vecteur(0),_SEQ__VECT));}
    break;

  case 125:

/* Line 1806 of yacc.c  */
#line 513 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (3)])._FUNCptr,gen(vecteur(0),_SEQ__VECT));}
    break;

  case 126:

/* Line 1806 of yacc.c  */
#line 514 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval) = symb_local((yyvsp[(3) - (4)]),contextptr);
        }
    break;

  case 127:

/* Line 1806 of yacc.c  */
#line 518 "input_parser.yy"
    {(yyval) = gen(at_local,2);}
    break;

  case 128:

/* Line 1806 of yacc.c  */
#line 519 "input_parser.yy"
    {
	(yyval) = symbolic(*(yyvsp[(1) - (6)])._FUNCptr,makevecteur(equaltosame((yyvsp[(3) - (6)])),symb_bloc((yyvsp[(5) - (6)])),(yyvsp[(6) - (6)])));
	}
    break;

  case 129:

/* Line 1806 of yacc.c  */
#line 522 "input_parser.yy"
    {
        vecteur v=makevecteur(equaltosame((yyvsp[(3) - (7)])),(yyvsp[(5) - (7)]),(yyvsp[(7) - (7)]));
	// *logptr(giac_yyget_extra(scanner)) << v << endl;
	(yyval) = symbolic(*(yyvsp[(1) - (7)])._FUNCptr,v);
	}
    break;

  case 130:

/* Line 1806 of yacc.c  */
#line 527 "input_parser.yy"
    { (yyval)=symb_rpn_prog((yyvsp[(2) - (3)])); }
    break;

  case 131:

/* Line 1806 of yacc.c  */
#line 528 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 132:

/* Line 1806 of yacc.c  */
#line 529 "input_parser.yy"
    { (yyval)=symbolic(at_maple_lib,makevecteur((yyvsp[(1) - (4)]),(yyvsp[(3) - (4)]))); }
    break;

  case 133:

/* Line 1806 of yacc.c  */
#line 530 "input_parser.yy"
    { 
          if ((yyvsp[(7) - (7)]).type==_INT_ && (yyvsp[(7) - (7)]).val && (yyvsp[(7) - (7)]).val!=3) giac_yyerror(scanner,"missing func/prog/proc end delimiter");
          const giac::context * contextptr = giac_yyget_extra(scanner);
           (yyval)=symb_program((yyvsp[(3) - (7)]),zero*(yyvsp[(3) - (7)]),symb_local((yyvsp[(5) - (7)]),(yyvsp[(6) - (7)]),contextptr),contextptr); 
        }
    break;

  case 134:

/* Line 1806 of yacc.c  */
#line 535 "input_parser.yy"
    { 
          if ((yyvsp[(8) - (8)]).type==_INT_ && (yyvsp[(8) - (8)]).val && (yyvsp[(8) - (8)]).val!=3) giac_yyerror(scanner,"missing func/prog/proc end delimiter");
          const giac::context * contextptr = giac_yyget_extra(scanner);
           (yyval)=symb_program_sto((yyvsp[(4) - (8)]),zero*(yyvsp[(4) - (8)]),symb_local((yyvsp[(6) - (8)]),(yyvsp[(7) - (8)]),contextptr),(yyvsp[(2) - (8)]),false,contextptr); 
        }
    break;

  case 135:

/* Line 1806 of yacc.c  */
#line 540 "input_parser.yy"
    { 
          if ((yyvsp[(9) - (9)]).type==_INT_ && (yyvsp[(9) - (9)]).val && (yyvsp[(9) - (9)]).val!=3) giac_yyerror(scanner,"missing func/prog/proc end delimiter");
          const giac::context * contextptr = giac_yyget_extra(scanner);
           (yyval)=symb_program_sto((yyvsp[(4) - (9)]),zero*(yyvsp[(4) - (9)]),symb_local((yyvsp[(7) - (9)]),(yyvsp[(8) - (9)]),contextptr),(yyvsp[(2) - (9)]),false,contextptr); 
        }
    break;

  case 136:

/* Line 1806 of yacc.c  */
#line 545 "input_parser.yy"
    { 
          if ((yyvsp[(8) - (8)]).type==_INT_ && (yyvsp[(8) - (8)]).val && (yyvsp[(8) - (8)]).val!=3) giac_yyerror(scanner,"missing func/prog/proc end delimiter");
          const giac::context * contextptr = giac_yyget_extra(scanner);
         (yyval)=symb_program((yyvsp[(3) - (8)]),zero*(yyvsp[(3) - (8)]),symb_local((yyvsp[(5) - (8)]),(yyvsp[(7) - (8)]),contextptr),contextptr); 
        }
    break;

  case 137:

/* Line 1806 of yacc.c  */
#line 550 "input_parser.yy"
    { 
          if ((yyvsp[(8) - (8)]).type==_INT_ && (yyvsp[(8) - (8)]).val && (yyvsp[(8) - (8)]).val!=3) giac_yyerror(scanner,"missing func/prog/proc end delimiter");
          const giac::context * contextptr = giac_yyget_extra(scanner);
           (yyval)=symb_program_sto((yyvsp[(3) - (8)]),zero*(yyvsp[(3) - (8)]),symb_local((yyvsp[(6) - (8)]),(yyvsp[(7) - (8)]),contextptr),(yyvsp[(1) - (8)]),false,contextptr); 
        }
    break;

  case 138:

/* Line 1806 of yacc.c  */
#line 555 "input_parser.yy"
    { 
          if ((yyvsp[(9) - (9)]).type==_INT_ && (yyvsp[(9) - (9)]).val && (yyvsp[(9) - (9)]).val!=3) giac_yyerror(scanner,"missing func/prog/proc end delimiter");
          const giac::context * contextptr = giac_yyget_extra(scanner);
           (yyval)=symb_program_sto((yyvsp[(3) - (9)]),zero*(yyvsp[(3) - (9)]),symb_local((yyvsp[(7) - (9)]),(yyvsp[(8) - (9)]),contextptr),(yyvsp[(1) - (9)]),false,contextptr); 
        }
    break;

  case 139:

/* Line 1806 of yacc.c  */
#line 560 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (9)])._FUNCptr,makevecteur((yyvsp[(3) - (9)]),equaltosame((yyvsp[(5) - (9)])),(yyvsp[(7) - (9)]),symb_bloc((yyvsp[(9) - (9)]))));}
    break;

  case 140:

/* Line 1806 of yacc.c  */
#line 561 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (10)])._FUNCptr,makevecteur((yyvsp[(3) - (10)]),equaltosame((yyvsp[(5) - (10)])),(yyvsp[(7) - (10)]),(yyvsp[(9) - (10)])));}
    break;

  case 141:

/* Line 1806 of yacc.c  */
#line 562 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (4)])._FUNCptr,gen2vecteur((yyvsp[(3) - (4)])));}
    break;

  case 142:

/* Line 1806 of yacc.c  */
#line 563 "input_parser.yy"
    { 
	vecteur v=makevecteur(zero,equaltosame((yyvsp[(3) - (5)])),zero,symb_bloc((yyvsp[(5) - (5)])));
	(yyval)=symbolic(*(yyvsp[(1) - (5)])._FUNCptr,v); 
	}
    break;

  case 143:

/* Line 1806 of yacc.c  */
#line 567 "input_parser.yy"
    { 
	(yyval)=symbolic(*(yyvsp[(1) - (6)])._FUNCptr,makevecteur(zero,equaltosame((yyvsp[(3) - (6)])),zero,(yyvsp[(5) - (6)]))); 
	}
    break;

  case 144:

/* Line 1806 of yacc.c  */
#line 570 "input_parser.yy"
    { 
          if ((yyvsp[(5) - (5)]).type==_INT_ && (yyvsp[(5) - (5)]).val && (yyvsp[(5) - (5)]).val!=9 && (yyvsp[(5) - (5)]).val!=8) giac_yyerror(scanner,"missing loop end delimiter");
	  (yyval)=symbolic(*(yyvsp[(1) - (5)])._FUNCptr,makevecteur(zero,equaltosame((yyvsp[(2) - (5)])),zero,symb_bloc((yyvsp[(4) - (5)])))); 
        }
    break;

  case 145:

/* Line 1806 of yacc.c  */
#line 574 "input_parser.yy"
    { 
          if ((yyvsp[(5) - (5)]).type==_INT_ && (yyvsp[(5) - (5)]).val && (yyvsp[(5) - (5)]).val!=9 && (yyvsp[(5) - (5)]).val!=8) giac_yyerror(scanner,"missing loop end delimiter");
          (yyval)=symbolic(*(yyvsp[(1) - (5)])._FUNCptr,makevecteur(zero,equaltosame((yyvsp[(2) - (5)])),zero,symb_bloc((yyvsp[(4) - (5)])))); 
        }
    break;

  case 146:

/* Line 1806 of yacc.c  */
#line 578 "input_parser.yy"
    { (yyval)=symb_try_catch(makevecteur(symb_bloc((yyvsp[(2) - (7)])),(yyvsp[(5) - (7)]),symb_bloc((yyvsp[(7) - (7)]))));}
    break;

  case 147:

/* Line 1806 of yacc.c  */
#line 579 "input_parser.yy"
    {(yyval)=symb_try_catch(gen2vecteur((yyvsp[(3) - (4)])));}
    break;

  case 148:

/* Line 1806 of yacc.c  */
#line 580 "input_parser.yy"
    {(yyval)=gen(at_try_catch,3);}
    break;

  case 149:

/* Line 1806 of yacc.c  */
#line 581 "input_parser.yy"
    { (yyval)=symb_case((yyvsp[(3) - (7)]),(yyvsp[(6) - (7)])); }
    break;

  case 150:

/* Line 1806 of yacc.c  */
#line 582 "input_parser.yy"
    { (yyval) = symb_case((yyvsp[(3) - (4)])); }
    break;

  case 151:

/* Line 1806 of yacc.c  */
#line 583 "input_parser.yy"
    { (yyval)=symb_case((yyvsp[(2) - (4)]),(yyvsp[(3) - (4)])); }
    break;

  case 152:

/* Line 1806 of yacc.c  */
#line 584 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (3)]); }
    break;

  case 153:

/* Line 1806 of yacc.c  */
#line 585 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 154:

/* Line 1806 of yacc.c  */
#line 586 "input_parser.yy"
    {(yyval) = gen(*(yyvsp[(1) - (2)])._FUNCptr,0);}
    break;

  case 155:

/* Line 1806 of yacc.c  */
#line 587 "input_parser.yy"
    { (yyval)=symbolic(*(yyvsp[(1) - (3)])._FUNCptr,makevecteur(zero,plus_one,zero,symb_bloc((yyvsp[(2) - (3)])))); }
    break;

  case 156:

/* Line 1806 of yacc.c  */
#line 588 "input_parser.yy"
    {(yyval) = symbolic(*(yyvsp[(1) - (4)])._FUNCptr,makevecteur(equaltosame((yyvsp[(2) - (4)])),(yyvsp[(4) - (4)]),0));}
    break;

  case 157:

/* Line 1806 of yacc.c  */
#line 589 "input_parser.yy"
    { (yyval)=symb_try_catch(makevecteur(symb_bloc((yyvsp[(2) - (5)])),at_break,symb_bloc((yyvsp[(4) - (5)])))); }
    break;

  case 158:

/* Line 1806 of yacc.c  */
#line 590 "input_parser.yy"
    { (yyval)=symb_try_catch(makevecteur(symb_bloc((yyvsp[(2) - (4)])),at_break,0)); }
    break;

  case 159:

/* Line 1806 of yacc.c  */
#line 591 "input_parser.yy"
    { (yyval)=symb_try_catch(makevecteur(symb_bloc((yyvsp[(2) - (6)])),at_break,symb_bloc((yyvsp[(5) - (6)])))); }
    break;

  case 160:

/* Line 1806 of yacc.c  */
#line 592 "input_parser.yy"
    { (yyval)=symb_try_catch(makevecteur(symb_bloc((yyvsp[(2) - (5)])),at_break,0)); }
    break;

  case 161:

/* Line 1806 of yacc.c  */
#line 593 "input_parser.yy"
    { vecteur v1(gen2vecteur((yyvsp[(1) - (3)]))),v3(gen2vecteur((yyvsp[(3) - (3)]))); (yyval)=symbolic(at_ti_semi,makevecteur(v1,v3)); }
    break;

  case 162:

/* Line 1806 of yacc.c  */
#line 594 "input_parser.yy"
    { 
          const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval)=symb_program_sto((yyvsp[(4) - (13)]),(yyvsp[(4) - (13)])*zero,symb_local((yyvsp[(10) - (13)]),mergevecteur(*(yyvsp[(7) - (13)])._VECTptr,*(yyvsp[(12) - (13)])._VECTptr),contextptr),(yyvsp[(2) - (13)]),false,contextptr); 
	}
    break;

  case 163:

/* Line 1806 of yacc.c  */
#line 598 "input_parser.yy"
    { 
          const giac::context * contextptr = giac_yyget_extra(scanner);
	(yyval)=symb_program_sto((yyvsp[(4) - (12)]),(yyvsp[(4) - (12)])*zero,symb_local((yyvsp[(9) - (12)]),mergevecteur(*(yyvsp[(7) - (12)])._VECTptr,*(yyvsp[(11) - (12)])._VECTptr),contextptr),(yyvsp[(2) - (12)]),false,contextptr); 
	}
    break;

  case 164:

/* Line 1806 of yacc.c  */
#line 602 "input_parser.yy"
    { 
          const giac::context * contextptr = giac_yyget_extra(scanner);
	(yyval)=symb_program_sto((yyvsp[(4) - (12)]),(yyvsp[(4) - (12)])*zero,symb_local((yyvsp[(9) - (12)]),(yyvsp[(11) - (12)]),contextptr),(yyvsp[(2) - (12)]),false,contextptr); 
	}
    break;

  case 165:

/* Line 1806 of yacc.c  */
#line 606 "input_parser.yy"
    { 
	(yyval)=symb_program_sto((yyvsp[(4) - (8)]),(yyvsp[(4) - (8)])*zero,symb_bloc((yyvsp[(7) - (8)])),(yyvsp[(2) - (8)]),false,giac_yyget_extra(scanner)); 
	}
    break;

  case 166:

/* Line 1806 of yacc.c  */
#line 609 "input_parser.yy"
    { (yyval)=symbolic(*(yyvsp[(1) - (3)])._FUNCptr,(yyvsp[(2) - (3)])); }
    break;

  case 167:

/* Line 1806 of yacc.c  */
#line 610 "input_parser.yy"
    { (yyval)=symbolic(*(yyvsp[(1) - (2)])._FUNCptr,(yyvsp[(2) - (2)])); }
    break;

  case 168:

/* Line 1806 of yacc.c  */
#line 611 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (2)]); }
    break;

  case 169:

/* Line 1806 of yacc.c  */
#line 612 "input_parser.yy"
    { (yyval)=symb_program_sto((yyvsp[(4) - (7)]),(yyvsp[(4) - (7)])*zero,(yyvsp[(7) - (7)]),(yyvsp[(2) - (7)]),false,giac_yyget_extra(scanner));}
    break;

  case 170:

/* Line 1806 of yacc.c  */
#line 613 "input_parser.yy"
    { 
          const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval)=symb_program_sto((yyvsp[(4) - (13)]),(yyvsp[(4) - (13)])*zero,symb_local((yyvsp[(10) - (13)]),(yyvsp[(12) - (13)]),contextptr),(yyvsp[(2) - (13)]),false,contextptr);
        }
    break;

  case 171:

/* Line 1806 of yacc.c  */
#line 617 "input_parser.yy"
    { (yyval)=symb_program_sto((yyvsp[(4) - (9)]),(yyvsp[(4) - (9)])*zero,symb_bloc((yyvsp[(8) - (9)])),(yyvsp[(2) - (9)]),false,giac_yyget_extra(scanner)); }
    break;

  case 172:

/* Line 1806 of yacc.c  */
#line 618 "input_parser.yy"
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

  case 173:

/* Line 1806 of yacc.c  */
#line 637 "input_parser.yy"
    { 
	vecteur v=makevecteur(zero,equaltosame((yyvsp[(2) - (5)])),zero,symb_bloc((yyvsp[(4) - (5)])));
	(yyval)=symbolic(*(yyvsp[(1) - (5)])._FUNCptr,v); 
	}
    break;

  case 174:

/* Line 1806 of yacc.c  */
#line 649 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 175:

/* Line 1806 of yacc.c  */
#line 650 "input_parser.yy"
    { 
	       gen tmp((yyvsp[(3) - (3)])); 
	       // tmp.subtype=1; 
	       (yyval)=symb_check_type(makevecteur(tmp,(yyvsp[(1) - (3)])),context0); 
          }
    break;

  case 176:

/* Line 1806 of yacc.c  */
#line 655 "input_parser.yy"
    { (yyval)=symb_double_deux_points(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]))); }
    break;

  case 177:

/* Line 1806 of yacc.c  */
#line 656 "input_parser.yy"
    { (yyval)=symb_double_deux_points(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]))); }
    break;

  case 178:

/* Line 1806 of yacc.c  */
#line 657 "input_parser.yy"
    { (yyval)=symb_double_deux_points(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]))); }
    break;

  case 179:

/* Line 1806 of yacc.c  */
#line 658 "input_parser.yy"
    { (yyval)=symb_double_deux_points(makevecteur((yyvsp[(1) - (5)]),(yyvsp[(4) - (5)]))); }
    break;

  case 180:

/* Line 1806 of yacc.c  */
#line 659 "input_parser.yy"
    { (yyval)=symb_double_deux_points(makevecteur(0,(yyvsp[(2) - (2)]))); }
    break;

  case 181:

/* Line 1806 of yacc.c  */
#line 660 "input_parser.yy"
    { (yyval)=symb_double_deux_points(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]))); }
    break;

  case 182:

/* Line 1806 of yacc.c  */
#line 668 "input_parser.yy"
    { 
	  gen tmp((yyvsp[(1) - (2)])); 
	  // tmp.subtype=1; 
	  (yyval)=symb_check_type(makevecteur(tmp,(yyvsp[(2) - (2)])),context0); 
	  }
    break;

  case 183:

/* Line 1806 of yacc.c  */
#line 673 "input_parser.yy"
    {(yyval)=symbolic(*(yyvsp[(1) - (2)])._FUNCptr,(yyvsp[(2) - (2)])); }
    break;

  case 184:

/* Line 1806 of yacc.c  */
#line 677 "input_parser.yy"
    { (yyval)=makevecteur(vecteur(0),vecteur(0)); }
    break;

  case 185:

/* Line 1806 of yacc.c  */
#line 678 "input_parser.yy"
    { vecteur v1 =gen2vecteur((yyvsp[(1) - (2)])); vecteur v2=gen2vecteur((yyvsp[(2) - (2)])); (yyval)=makevecteur(mergevecteur(gen2vecteur(v1[0]),gen2vecteur(v2[0])),mergevecteur(gen2vecteur(v1[1]),gen2vecteur(v2[1]))); }
    break;

  case 186:

/* Line 1806 of yacc.c  */
#line 679 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (2)]); }
    break;

  case 187:

/* Line 1806 of yacc.c  */
#line 683 "input_parser.yy"
    { if ((yyvsp[(3) - (4)]).type==_VECT) (yyval)=gen(*(yyvsp[(3) - (4)])._VECTptr,_RPN_STACK__VECT); else (yyval)=gen(vecteur(1,(yyvsp[(3) - (4)])),_RPN_STACK__VECT); }
    break;

  case 188:

/* Line 1806 of yacc.c  */
#line 684 "input_parser.yy"
    { (yyval)=gen(vecteur(0),_RPN_STACK__VECT); }
    break;

  case 189:

/* Line 1806 of yacc.c  */
#line 687 "input_parser.yy"
    { if (!(yyvsp[(1) - (3)]).val) (yyval)=makevecteur((yyvsp[(2) - (3)]),vecteur(0)); else (yyval)=makevecteur(vecteur(0),(yyvsp[(2) - (3)]));}
    break;

  case 190:

/* Line 1806 of yacc.c  */
#line 690 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (3)]); }
    break;

  case 191:

/* Line 1806 of yacc.c  */
#line 693 "input_parser.yy"
    { (yyval)=gen(vecteur(1,(yyvsp[(1) - (1)])),_SEQ__VECT); }
    break;

  case 192:

/* Line 1806 of yacc.c  */
#line 694 "input_parser.yy"
    { 
	       vecteur v=*(yyvsp[(1) - (3)])._VECTptr;
	       v.push_back((yyvsp[(3) - (3)]));
	       (yyval)=gen(v,_SEQ__VECT);
	     }
    break;

  case 193:

/* Line 1806 of yacc.c  */
#line 701 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 194:

/* Line 1806 of yacc.c  */
#line 702 "input_parser.yy"
    { (yyval)=symb_sto((yyvsp[(3) - (3)]),(yyvsp[(1) - (3)]),(yyvsp[(2) - (3)])==at_array_sto); }
    break;

  case 195:

/* Line 1806 of yacc.c  */
#line 703 "input_parser.yy"
    { (yyval)=symb_equal((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])); }
    break;

  case 196:

/* Line 1806 of yacc.c  */
#line 704 "input_parser.yy"
    { (yyval)=symbolic(at_deuxpoints,makesequence((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)])));  }
    break;

  case 197:

/* Line 1806 of yacc.c  */
#line 705 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (3)]); }
    break;

  case 198:

/* Line 1806 of yacc.c  */
#line 706 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); *logptr(giac_yyget_extra(scanner)) << "Error: reserved word "<< (yyvsp[(1) - (1)]) <<endl;}
    break;

  case 199:

/* Line 1806 of yacc.c  */
#line 707 "input_parser.yy"
    { (yyval)=symb_double_deux_points(makevecteur((yyvsp[(1) - (3)]),(yyvsp[(3) - (3)]))); *logptr(giac_yyget_extra(scanner)) << "Error: reserved word "<< (yyvsp[(1) - (3)]) <<endl; }
    break;

  case 200:

/* Line 1806 of yacc.c  */
#line 708 "input_parser.yy"
    { 
  const giac::context * contextptr = giac_yyget_extra(scanner);
  (yyval)=string2gen("_"+(yyvsp[(1) - (1)]).print(contextptr),false); 
  if (!giac::first_error_line(contextptr)){
    giac::first_error_line(giac::lexer_line_number(contextptr),contextptr);
    giac:: error_token_name((yyvsp[(1) - (1)]).print(contextptr)+ " (reserved word)",contextptr);
  }
}
    break;

  case 201:

/* Line 1806 of yacc.c  */
#line 716 "input_parser.yy"
    { 
  const giac::context * contextptr = giac_yyget_extra(scanner);
  (yyval)=string2gen("_"+(yyvsp[(1) - (1)]).print(contextptr),false);
  if (!giac::first_error_line(contextptr)){
    giac::first_error_line(giac::lexer_line_number(contextptr),contextptr);
    giac:: error_token_name((yyvsp[(1) - (1)]).print(contextptr)+ " reserved word",contextptr);
  }
}
    break;

  case 202:

/* Line 1806 of yacc.c  */
#line 726 "input_parser.yy"
    { (yyval)=plus_one;}
    break;

  case 203:

/* Line 1806 of yacc.c  */
#line 727 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 204:

/* Line 1806 of yacc.c  */
#line 730 "input_parser.yy"
    { (yyval)=gen(vecteur(0),_SEQ__VECT); }
    break;

  case 205:

/* Line 1806 of yacc.c  */
#line 731 "input_parser.yy"
    { (yyval)=makesuite((yyvsp[(1) - (1)])); }
    break;

  case 206:

/* Line 1806 of yacc.c  */
#line 734 "input_parser.yy"
    { (yyval) = makevecteur((yyvsp[(1) - (1)])); }
    break;

  case 207:

/* Line 1806 of yacc.c  */
#line 736 "input_parser.yy"
    { vecteur v(1,(yyvsp[(1) - (2)])); 
			  if ((yyvsp[(1) - (2)]).type==_VECT) v=*((yyvsp[(1) - (2)])._VECTptr); 
			  v.push_back((yyvsp[(2) - (2)])); 
			  (yyval) = v;
			}
    break;

  case 208:

/* Line 1806 of yacc.c  */
#line 741 "input_parser.yy"
    { (yyval) = (yyvsp[(1) - (2)]);}
    break;

  case 209:

/* Line 1806 of yacc.c  */
#line 744 "input_parser.yy"
    { (yyval)=vecteur(0); }
    break;

  case 210:

/* Line 1806 of yacc.c  */
#line 745 "input_parser.yy"
    { (yyval)=mergevecteur(vecteur(1,(yyvsp[(1) - (2)])),*((yyvsp[(2) - (2)])._VECTptr));}
    break;

  case 211:

/* Line 1806 of yacc.c  */
#line 746 "input_parser.yy"
    { (yyval)=mergevecteur(vecteur(1,(yyvsp[(1) - (3)])),*((yyvsp[(3) - (3)])._VECTptr));}
    break;

  case 212:

/* Line 1806 of yacc.c  */
#line 749 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 213:

/* Line 1806 of yacc.c  */
#line 819 "input_parser.yy"
    { (yyval)=plus_one; }
    break;

  case 214:

/* Line 1806 of yacc.c  */
#line 820 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (2)]); }
    break;

  case 215:

/* Line 1806 of yacc.c  */
#line 823 "input_parser.yy"
    { (yyval)=plus_one; }
    break;

  case 216:

/* Line 1806 of yacc.c  */
#line 824 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (2)]); }
    break;

  case 217:

/* Line 1806 of yacc.c  */
#line 825 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (2)]); }
    break;

  case 218:

/* Line 1806 of yacc.c  */
#line 826 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (2)]); }
    break;

  case 219:

/* Line 1806 of yacc.c  */
#line 829 "input_parser.yy"
    { (yyval)=plus_one; }
    break;

  case 220:

/* Line 1806 of yacc.c  */
#line 830 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 221:

/* Line 1806 of yacc.c  */
#line 833 "input_parser.yy"
    { (yyval)=0; }
    break;

  case 222:

/* Line 1806 of yacc.c  */
#line 834 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (3)]); }
    break;

  case 223:

/* Line 1806 of yacc.c  */
#line 835 "input_parser.yy"
    { (yyval)=symb_bloc((yyvsp[(2) - (2)])); }
    break;

  case 224:

/* Line 1806 of yacc.c  */
#line 839 "input_parser.yy"
    { 
	(yyval) = (yyvsp[(2) - (3)]);
	}
    break;

  case 225:

/* Line 1806 of yacc.c  */
#line 842 "input_parser.yy"
    {
          const giac::context * contextptr = giac_yyget_extra(scanner);
          (yyval) = symb_local((yyvsp[(2) - (4)]),(yyvsp[(3) - (4)]),contextptr);
         }
    break;

  case 226:

/* Line 1806 of yacc.c  */
#line 849 "input_parser.yy"
    { if ((yyvsp[(1) - (1)]).type==_INT_ && (yyvsp[(1) - (1)]).val && (yyvsp[(1) - (1)]).val!=4) giac_yyerror(scanner,"missing test end delimiter"); (yyval)=0; }
    break;

  case 227:

/* Line 1806 of yacc.c  */
#line 850 "input_parser.yy"
    {
          if ((yyvsp[(3) - (3)]).type==_INT_ && (yyvsp[(3) - (3)]).val && (yyvsp[(3) - (3)]).val!=4) giac_yyerror(scanner,"missing test end delimiter");
	(yyval)=symb_bloc((yyvsp[(2) - (3)])); 
	}
    break;

  case 228:

/* Line 1806 of yacc.c  */
#line 854 "input_parser.yy"
    { 
	  (yyval)=symb_ifte(equaltosame((yyvsp[(2) - (5)])),symb_bloc((yyvsp[(4) - (5)])),(yyvsp[(5) - (5)]));
	  }
    break;

  case 229:

/* Line 1806 of yacc.c  */
#line 857 "input_parser.yy"
    { 
	  (yyval)=symb_ifte(equaltosame((yyvsp[(3) - (6)])),symb_bloc((yyvsp[(5) - (6)])),(yyvsp[(6) - (6)]));
	  }
    break;

  case 230:

/* Line 1806 of yacc.c  */
#line 862 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;

  case 231:

/* Line 1806 of yacc.c  */
#line 863 "input_parser.yy"
    { (yyval)=(yyvsp[(2) - (2)]); }
    break;

  case 232:

/* Line 1806 of yacc.c  */
#line 866 "input_parser.yy"
    { (yyval)=0; }
    break;

  case 233:

/* Line 1806 of yacc.c  */
#line 867 "input_parser.yy"
    { (yyval)=0; }
    break;

  case 234:

/* Line 1806 of yacc.c  */
#line 870 "input_parser.yy"
    { (yyval)=vecteur(0); }
    break;

  case 235:

/* Line 1806 of yacc.c  */
#line 871 "input_parser.yy"
    { (yyval)=makevecteur(symb_bloc((yyvsp[(3) - (3)])));}
    break;

  case 236:

/* Line 1806 of yacc.c  */
#line 872 "input_parser.yy"
    { (yyval)=mergevecteur(makevecteur((yyvsp[(2) - (5)]),symb_bloc((yyvsp[(4) - (5)]))),*((yyvsp[(5) - (5)])._VECTptr));}
    break;

  case 237:

/* Line 1806 of yacc.c  */
#line 875 "input_parser.yy"
    { (yyval)=vecteur(0); }
    break;

  case 238:

/* Line 1806 of yacc.c  */
#line 876 "input_parser.yy"
    { (yyval)=vecteur(1,symb_bloc((yyvsp[(2) - (2)]))); }
    break;

  case 239:

/* Line 1806 of yacc.c  */
#line 877 "input_parser.yy"
    { (yyval)=mergevecteur(makevecteur((yyvsp[(2) - (5)]),symb_bloc((yyvsp[(4) - (5)]))),*((yyvsp[(5) - (5)])._VECTptr));}
    break;

  case 240:

/* Line 1806 of yacc.c  */
#line 880 "input_parser.yy"
    { (yyval)=vecteur(0); }
    break;

  case 241:

/* Line 1806 of yacc.c  */
#line 881 "input_parser.yy"
    { (yyval)=vecteur(1,symb_bloc((yyvsp[(2) - (2)]))); }
    break;

  case 242:

/* Line 1806 of yacc.c  */
#line 882 "input_parser.yy"
    { (yyval)=mergevecteur(makevecteur((yyvsp[(2) - (6)]),symb_bloc((yyvsp[(4) - (6)]))),gen2vecteur((yyvsp[(6) - (6)])));}
    break;

  case 243:

/* Line 1806 of yacc.c  */
#line 883 "input_parser.yy"
    { (yyval)=mergevecteur(makevecteur((yyvsp[(2) - (7)]),symb_bloc((yyvsp[(4) - (7)]))),gen2vecteur((yyvsp[(7) - (7)])));}
    break;

  case 244:

/* Line 1806 of yacc.c  */
#line 886 "input_parser.yy"
    { (yyval)=(yyvsp[(1) - (1)]); }
    break;



/* Line 1806 of yacc.c  */
#line 6722 "y.tab.c"
      default: break;
    }
  /* User semantic actions sometimes alter yychar, and that requires
     that yytoken be updated with the new translation.  We take the
     approach of translating immediately before every use of yytoken.
     One alternative is translating here after every semantic action,
     but that translation would be missed if the semantic action invokes
     YYABORT, YYACCEPT, or YYERROR immediately after altering yychar or
     if it invokes YYBACKUP.  In the case of YYABORT or YYACCEPT, an
     incorrect destructor might then be invoked immediately.  In the
     case of YYERROR or YYBACKUP, subsequent parser actions might lead
     to an incorrect destructor call or verbose syntax error message
     before the lookahead is translated.  */
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
  /* Make sure we have latest lookahead translation.  See comments at
     user semantic actions for why this is necessary.  */
  yytoken = yychar == YYEMPTY ? YYEMPTY : YYTRANSLATE (yychar);

  /* If not already recovering from an error, report this error.  */
  if (!yyerrstatus)
    {
      ++yynerrs;
#if ! YYERROR_VERBOSE
      yyerror (scanner, YY_("syntax error"));
#else
# define YYSYNTAX_ERROR yysyntax_error (&yymsg_alloc, &yymsg, \
                                        yyssp, yytoken)
      {
        char const *yymsgp = YY_("syntax error");
        int yysyntax_error_status;
        yysyntax_error_status = YYSYNTAX_ERROR;
        if (yysyntax_error_status == 0)
          yymsgp = yymsg;
        else if (yysyntax_error_status == 1)
          {
            if (yymsg != yymsgbuf)
              YYSTACK_FREE (yymsg);
            yymsg = (char *) YYSTACK_ALLOC (yymsg_alloc);
            if (!yymsg)
              {
                yymsg = yymsgbuf;
                yymsg_alloc = sizeof yymsgbuf;
                yysyntax_error_status = 2;
              }
            else
              {
                yysyntax_error_status = YYSYNTAX_ERROR;
                yymsgp = yymsg;
              }
          }
        yyerror (scanner, yymsgp);
        if (yysyntax_error_status == 2)
          goto yyexhaustedlab;
      }
# undef YYSYNTAX_ERROR
#endif
    }



  if (yyerrstatus == 3)
    {
      /* If just tried and failed to reuse lookahead token after an
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

  /* Else will try to reuse lookahead token after shifting the error
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
      if (!yypact_value_is_default (yyn))
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

#if !defined(yyoverflow) || YYERROR_VERBOSE
/*-------------------------------------------------.
| yyexhaustedlab -- memory exhaustion comes here.  |
`-------------------------------------------------*/
yyexhaustedlab:
  yyerror (scanner, YY_("memory exhausted"));
  yyresult = 2;
  /* Fall through.  */
#endif

yyreturn:
  if (yychar != YYEMPTY)
    {
      /* Make sure we have latest lookahead translation.  See comments at
         user semantic actions for why this is necessary.  */
      yytoken = YYTRANSLATE (yychar);
      yydestruct ("Cleanup: discarding lookahead",
                  yytoken, &yylval, scanner);
    }
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



/* Line 2067 of yacc.c  */
#line 893 "input_parser.yy"


#ifndef NO_NAMESPACE_GIAC
} // namespace giac


#endif // ndef NO_NAMESPACE_GIAC
int giac_yyget_column  (yyscan_t yyscanner);

// Error print routine (store error string in parser_error)
#if 1
int giac_yyerror(yyscan_t scanner,const char *s) {
 const giac::context * contextptr = giac_yyget_extra(scanner);
 int col = giac_yyget_column(scanner);
 int line = giac::lexer_line_number(contextptr);
 std::string token_name=string(giac_yyget_text(scanner));
 bool is_at_end = (token_name.size()==2 && (token_name[0]==char(0xC3)) && (token_name[1]==char(0xBF)));
 std::string suffix = " (reserved word)";
 if (token_name.size()>suffix.size() && token_name.compare(token_name.size()-suffix.size(),suffix.size(),suffix)) {
  if (col>=token_name.size()-suffix.size()) {
   col -= token_name.size()-suffix.size();
  }
 } else if (col>=token_name.size()) {
   col -= token_name.size();
 }
 giac::lexer_column_number(contextptr)=col;
 if (is_at_end) {
  parser_error(":" + giac::print_INT_(line) + ": " +string(s) + " at end of input\n",contextptr);
  giac::parsed_gen(giac::undef,contextptr);
 } else {
 parser_error( ":" + giac::print_INT_(line) + ": " + string(s) + " line " + giac::print_INT_(line) + " col " + giac::print_INT_(col) + " at " + token_name +"\n",contextptr);
 giac::parsed_gen(giac::string2gen(token_name,false),contextptr);
 }
 if (!giac::first_error_line(contextptr)) {
  giac::first_error_line(line,contextptr);
  if (is_at_end) {
   token_name="end of input";
  }
  giac:: error_token_name(token_name,contextptr);
 }
 return line;
}

#else

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
#endif

