module ctintro;

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


fluid('(dummy_id!* g_dvnames));

% g_dvnames is a vector.


% patches and extensions of some functions of the packages ASSIST and
% DUMMY

%
load_package dummy;
%


% function REMSYM is generalised to take account of partial symmetries

symbolic procedure remsym u;
% ALLOWS TO ELIMINATE THE DECLARED SYMMETRIES.
 for each j in u do
   if flagp(j,'symmetric) then remflag(list j,'symmetric)
     else
   if flagp(j,'antisymmetric) then remflag(list j,'antisymmetric)
     else remprop(j,'symtree);

% function SYMMETRIZE is generalized for total antisymmetrization
% and for lists of (cyclic-)permutations.

symbolic procedure sym_sign u;
% u is a standard form for the kernel of a tensor.
% if the permutation sign  of indices is + then returns u else
% returns negf u.
 (if permp!:(ordn y,y) then u else negf u)where y=car select_vars mvar u;

symbolic procedure simpsumsym(u);
% The use is SYMMETRIZE(LIST(A,B,...J),operator,perm_function,[perm_sign])
% or SYMMETRIZE(LIST(LIST(A,B,C...)),operator,perm_function,[perm_sign]).
% [perm_sign] is optional for antisymmetric sums.
% works even if tensors depend explicitly on variables.
% Works both for OPFN and symbolic procedure functions.
% Is not valid for general expressions.
 if length u geq 5 then rederr("less than 5 arguments required for symmetrize")
 else
 begin scalar ut,uu,x,res,oper,fn,sym,bool,boolfn;
  integer n, thesign;
  thesign := 1;
  fn:= caddr u;
  oper:=cadr u;
  if not idp oper then typerr(oper,"operator") else
  if null flagp(oper,'opfn) then
     if null get(oper,'simpfn) then put(oper,'simpfn,'simpiden);
     flag(list oper, 'listargp);
  sym:=if cdddr u then
          if cadddr u eq 'perm_sign then t;
  if sym and null permp!:(cdar u, ordn cdar u) then thesign:=-thesign;
if not(gettype fn eq 'procedure) then typerr(fn,"procedure");
  ut:= select_vars car u;
  uu:=(if flagp(fn,'opfn) then <<boolfn:=t; reval x>>
          else  if car reval x eq 'minus then cdadr reval x
                 else cdr reval x) where x=oper . car ut;
   n:=length uu;
  x:=if listp  car uu and null flagp(oper,'tensor) and not boolfn then
                <<bool:=t;apply1(fn, cdar uu)>> else
     if boolfn and listp cadr uu and null flagp(oper,'tensor) then
                <<bool:=t;apply1(fn,cadr uu)>>
       else  apply1(fn,uu); % this applies to tensors
  if flagp(fn,'opfn) then x:=alg_to_symb x;
  n:=length x -1;
  if not bool then <<
     res:= if sym then sym_sign((
                 if cadr ut then oper . (cadr ut . car x)
                   else oper . car x) .** 1 .* 1 .+ nil)
             else
          (if cadr ut then  oper . (cadr ut . car x)
            else oper . car x) .** 1 .* 1 .+ nil ;
  for i:=1:n do
   << uu:=cadr x; aconc(res, if sym then  car sym_sign(
                   (if cadr ut then oper . (cadr ut . uu)
                     else oper . uu) .** 1 .* 1 .+ nil)
                              else
      (if cadr ut then  oper . (cadr ut . uu)
          else oper . uu) .** 1 .* 1); delqip(uu,x);>>;
                    >>
  else
 << res:=if sym then sym_sign((oper . list('list .
      for each i in car x collect mk!*sq simp!* i)) .** 1 .* 1 .+ nil)
           else
        (oper . list('list .
         for each i in car x collect mk!*sq simp!* i)) .** 1 .* 1 .+ nil;
   for i:=1:n do << uu:=cadr x;
    aconc(res, if sym then car sym_sign((oper . list('list .
                  for each j in uu collect simp!* j)) .** 1 .* 1 .+ nil)
                else (oper . list('list .
                 for each i in uu collect mk!*sq simp!* i)) .** 1 .* 1 );
     delqip(uu,x);>>;
 >>;
  return
  if get(oper,'tag) eq 'list then
        simp!*('list . for each w in res collect caar w)
   else
     resimp (multf(!*n2f thesign,res) ./ 1)
end;

%load_package dummyn;

% modifications to dummy.red:

% patch to dummy.red

symbolic procedure dummy_nam u;
% creates the required global vector for dummy.red
% A variant of dummy_names from  DUMMY.
% No declaration flag(..,'dummy) here since
% it is done inside 'mk_dummy_ids'
 <<g_dvnames := list2vect!*(ordn u,'symbolic);t>>;


% This part redefines some of the dummy procedures
% to make it tolerate the covariant-contravariant indices.
% and tensors with NO indices.

symbolic procedure dv_skelsplit(camb);
  begin scalar  var_camb,skel, stree, subskels;
        integer count, ind, maxind, thesign;
  thesign := 1;
  var_camb:=if listp camb  then
              if listp cadr camb and caadr camb = 'list then cadr camb;
    if (ind := dummyp(camb)) then
      return {1, ind, ('!~dv . {'!*, ind})}
     else
    if not listp camb  or (var_camb and null cddr camb)
                                      then  return {1, 0, (camb . nil)};
  stree := get(car camb, 'symtree);
   if not stree then
    <<
    stree := for count := 1 : length(if var_camb then cddr camb      %%
                                       else cdr camb) collect count;  %%
    if flagp(car  camb, 'symmetric) then
      stree := '!+ . stree
    else if flagp(car camb, 'antisymmetric) then
      stree := '!- . stree
    else
      stree := '!* . stree
    >>;
  subskels := mkve(length(if var_camb then cddr camb else cdr camb)); %%
  count := 0;
  for each arg in (if var_camb then cddr camb else cdr camb) do   %%
    <<
    count := count + 1;
    if (ind := dummyp(arg)) then
      <<
      maxind := max(maxind, ind);
    if idp arg then  putve(subskels, count, ('!~dv . {'!*, ind}))
                else putve(subskels, count, ('!~dva . {'!*, ind}))
      >>
    else
      putve(subskels, count, (arg . nil));
    >>;
  stree := st_sorttree(stree, subskels, function idcons_ordp);
  if stree and (car stree = 0) then return nil;
  thesign := car stree;
  skel := dv_skelsplit1(cdr stree, subskels);
  stree := st_consolidate(cdr skel);
  skel := if var_camb then (car camb) . var_camb . car skel    %%
           else car camb . car skel;                            %%
  return {thesign, maxind, skel . stree};
  end;


symbolic procedure dummyp(var);
% takes into account the new features i.e.
% some indices may be !0, !1 ....
% others are covariant indices i.e. (minus !<integer>), (minus a) etc ...
  begin scalar varsplit;
        integer count, res;
  if listp var then
    if ( careq_minus var) then var:= cadr var
      else return nil;
  if numberp(var) or (!*id2num var)
    then return nil;
  count := 1;
  while count <= upbve(g_dvnames) do
    <<
   if var = venth(g_dvnames, count) then
    <<
      res := count;
      count := upbve(g_dvnames) + 1
      >>
    else
      count := count + 1;
    >>;
  if res = 0 then
    <<
    varsplit := ad_splitname(var);
    if (car varsplit eq g_dvbase) then
      return cdr varsplit
    >>
  else return res;
  end;


symbolic procedure dv_skel2factor1(skel_kern, dvars);
% Take into account of the two sets of generic dummy variables.
% One for the ordinary and contravariant dummy variables, another for
% covariant variables.
% !~dva regenerate COVARIANT dummy variables.
 begin scalar dvar,scr;
   if null skel_kern then return nil;
  return
   if listp skel_kern then
    <<scr:=dv_skel2factor1(car skel_kern, dvars);
         scr:=scr . dv_skel2factor1(cdr skel_kern, dvars)
    >>
    else
   if skel_kern eq '!~dv then
       <<
         dvar := car dvars;
         if cdr dvars then
           <<
               rplaca(dvars, cadr dvars); rplacd(dvars, cddr dvars);
            >>;
       dvar
       >>
    else
   if skel_kern eq '!~dva then
      <<
        dvar := car dvars;
        if cdr dvars then
          <<
            rplaca(dvars, cadr dvars); rplacd(dvars, cddr dvars);
        >>;
      ('minus . dvar . nil)
      >>
    else
       skel_kern;
  end;


% end of patch to dummy

endmodule;
end;
