% ----- print  -----

% In this version, all these print functions are defined to use the
% standard REDUCE two dimensional format for algebraic expressions.
% This means that "write" can be viewed as a set of recursive calls
% on prin2.


% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%

ranks print u : {bool} -> bool,
      print u : {list} -> list,
      print u : {noval} -> noval,
      print u : {kernel} -> kernel,
      print u : {variable} -> variable,
      print u : {xkernel} -> xkernel,
      print u : {poly} -> poly,
      print u : {ratpol} -> ratpol,
      print u : {sint} -> sint,
      print u : {string} -> string,
      print u : {generic} -> generic;

symbolic procedure print_sint u;
   print_algebraic u;

symbolic procedure print_poly u;
   <<sqprint(value u ./ 1); terpri!* nil; u>>;

symbolic procedure print_ratpol u;
   <<sqprint value u; terpri!* nil; u>>;

symbolic procedure print_algebraic u;
   <<if null x then prin2!* nil else maprin x; terpri!* nil; u>>
     where x=value u;

symbolic procedure print_list u;
   print_list1(u,t);

symbolic procedure print_list1(u,bool);
   % This definition is basically that of INPRINT, except that it
   % decides when to split at the comma by looking at the size of
   % the argument.
   begin scalar l,orig,split,u;
      l := value u;
      prin2!* get('!*lcbkt!*,'prtch);
         % Do it this way so table can change.
      orig := orig!*;
      orig!* := if posn!*<18 then posn!* else orig!*+3;
      if null l then go to b;
      split := treesizep(l,40);   % 40 is arbitrary choice.
   a: rapply('prin2,list car l);
      l := cdr l;
% print list ("l:",l);
      if null l then go to b;
      oprin '!*comma!*;
      if split then terpri!* t;
      go to a;
   b: prin2!* get('!*rcbkt!*,'prtch);
      if bool then terpri!* nil;
      orig!* := orig;
      return u
   end;

symbolic procedure print_bool u;
   print_algebraic u;

symbolic procedure print_noval u; nil;

symbolic procedure print_string u;
   print_algebraic u;

symbolic procedure print_kernel u;
   print_algebraic u;

symbolic procedure print_xkernel u;
   print_algebraic u;

symbolic procedure print_variable u;
   print_algebraic u;

symbolic procedure print_generic u;
   print_algebraic u;

% ------ prin2 -----

ranks prin2 u : {bool} -> bool,
      prin2 u : {list} -> list,
      prin2 u : {noval} -> noval,
      prin2 u : {kernel} -> kernel,
      prin2 u : {variable} -> variable,
      prin2 u : {xkernel} -> xkernel,
      prin2 u : {poly} -> poly,
      prin2 u : {ratpol} -> ratpol,
      prin2 u : {sint} -> sint,
      prin2 u : {string} -> string,
      prin2 u : {generic} -> generic;

symbolic procedure prin2_sint u;
   prin2_algebraic u;

symbolic procedure prin2_poly u;
   <<sqprint(value u ./ 1); u>>;

symbolic procedure prin2_ratpol u;
   <<sqprint value u; u>>;

symbolic procedure prin2_algebraic u;
   <<if null x then prin2!* nil else maprin x; u>> where x=value u;

symbolic procedure prin2_list u;
   print_list1(u,nil);

symbolic procedure prin2_bool u;
   prin2_algebraic u;

symbolic procedure prin2_noval u; nil;

symbolic procedure prin2_string u;
   prin2_algebraic u;

symbolic procedure prin2_variable u;
   prin2_algebraic u;

symbolic procedure prin2_xkernel u;
   prin2_algebraic u;

symbolic procedure prin2_generic u;
   prin2_algebraic u;


% ------ in ------

% Would need to remove the parse properties first.
%ranks in u : {non_empty_list} -> noval;


% ------------------ on --- off -------------------

remprop('on,'stat);

remprop('off,'stat);

remflag('(on off),'ignore);

ranks on u : {list} -> noval,
      off u : {list} -> noval;

rlistat '(on off);

% This is a messy way to handle a PSL alias problem.

!#if (member 'psl lispsystem!*)
   symbolic procedure !~on_list u; onoff_list(u,t);
   symbolic procedure !~off_list u; onoff_list(u,nil);
 !#else
   symbolic procedure on_list u; onoff_list(u,t);
   symbolic procedure off_list u; onoff_list(u,nil);
 !#endif

symbolic procedure onoff_list(u,bool);
   <<for each j in value u do onoff(value j,bool);
     mknovalobj()>>;

% ------------  trace --- traceset ----------------

remprop('tr,'stat);

remprop('untr,'stat);

remprop('trst,'stat);

remprop('untrst,'stat);

rlistat '(tr trst untr untrst);

ranks tr u : {list} -> noval,
      untr u : {list} -> noval,
      trst u : {list} -> noval,
      untrst u : {list} -> noval;

symbolic procedure tr_list u;
  trfn(u,'tr);

symbolic procedure untr_list u;
  trfn(u,'untr);

symbolic procedure trst_list u;
  trfn(u,'trst);

symbolic procedure untrst_list u;
  trfn(u,'untrst);

symbolic procedure trfn(u,v);
   <<eval(v . for each j in value u collect value j);
     mknovalobj()>>;

% ---------------------  write -----------------------------

remprop('write,'stat);

rlistat '(write);

ranks write u : {list} -> noval;

symbolic procedure write_list u;
   <<for each x in value u do rapply('prin2,list x);
     terpri!* t;
     mknovalobj()>>;

% ---------------------  factor ----------------------------

remprop('factor,'stat);

remprop('remfac,'stat);

rlistat '(factor remfac);

ranks factor u : {list} -> noval,
      remfac u : {list} -> noval;

symbolic procedure factor_list u;
   factor_list1(u,t);

symbolic procedure remfac_list u;
   factor_list1(u,nil);

symbolic procedure factor_list1(u,v);
   <<for each x in value u do factor1(list value x,v,'factors!*);
     mknovalobj()>>;

end;

ranks num u: {ratpol} -> poly,
      %lc u : {xpoly} -> poly,
      %ldeg u: {xpoly} -> int,
      %red u: {xpoly} -> poly,
      %idp u: {kernel} -> bool,
      %domainp u: {xpoly} -> bool,
      %zerop u: {xpoly} -> bool,
      u = v: {poly,poly} -> bool,

%ranks u:ratpol -> zero when num u = 0,

symbolic procedure equal_poly_poly(u,v); {'bool,value u = value v};

% An xpoly can't be zero!!
% symbolic procedure zerop_xpoly u; mkobject(null value u,'bool);

symbolic procedure poly!>zero u; mkobject(0,'zero);

%ranks u:poly -> kernel when not domainp u and
%                            ((lc u = 1 and ldeg u = 1) and red u = 0),
%      u:kernel -> variable when idp u;

symbolic procedure lc_xpoly u; mkobject(lc value u,'poly);

symbolic procedure ldeg_xpoly u; mkobject(ldeg value u,'int);

symbolic procedure red_xpoly u;
   mkobject(if red value u then red value u else 0,'poly);

symbolic procedure idp_kernel u; mkobject(idp value u,'bool);

% An xpoly can't become a kernel!!
% symbolic procedure xpoly!>kernel u; mkobject(mvar u,'kernel);

symbolic procedure domainp_poly u; mkobject(domainp value u,'bool);


% ------ LIST -----

subtypes non_empty_list empty_list < list;

ranks u neq v : {list,list} -> bool;

%ranks u:list -> non_empty_list when u neq {};

symbolic procedure neq_list_list(u,v); mkobject(null(value u = value v),'bool);

