module triang;

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


COMMENT

                ##########################################
                ##                                      ##
                ##    Solving zerodimensional systems   ##
                ##          Triangular systems          ##
                ##                                      ##
                ##########################################


Zerosolve returns lists of dpmats in prefix form, that consist of
triangular systems in the sense of Lazard, provided the input is
radical. For the corresponding definitions and concepts see

[Lazard] D. Lazard: Solving zero dimensional algebraic systems.
                J. Symb. Comp. 13 (1992), 117 - 131.
and

[EFGB] H.-G. Graebe: Triangular systems and factorized Groebner
                bases. Report Nr. 7 (1995), Inst. f. Informatik,
                        Univ. Leipzig.


The triangularization of zerodim. ideal bases is done by Moeller's
approach, see

[Moeller] H.-M. Moeller : On decomposing systems of polynomial
        equations with finitely many solutions.
        J. AAECC 4 (1993), 217 - 230.

We present three implementations :
        -- the pure lex gb (zerosolve)
        -- the "slow turn to pure lex" (zerosolve1)
and
        -- the mix with [FGLM] (zerosolve2)

END COMMENT;

symbolic procedure triang!=trsort(a,b);
  mo_dlexcomp(dp_lmon a,dp_lmon b);

symbolic procedure triang!=makedpmat x;
  makelist for each p in x collect dp_2a p;

% =================================================================
% The pure lex approach.

symbolic operator zerosolve;
symbolic procedure zerosolve m;
  if !*mode='algebraic then makelist zerosolve!* dpmat_from_a m
  else zerosolve!* m;

symbolic procedure zerosolve!* m;
% Solve a zerodimensional dpmat ideal m, first groebfactor it and then
% triangularize it. Returns a list of dpmats in prefix form.
 if (dpmat_cols m>0) or (dim!* m>0) then
        rederr"ZEROSOLVE only for zerodimensional ideals"
 else if not !*noetherian or ring_degrees cali!=basering then
        rederr"ZEROSOLVE only for pure lex. term orders"
 else for each x in groebfactor!*(m,nil) join triang_triang car x;

symbolic procedure triang_triang m;
% m must be a zerodim. ideal gbasis (recommended to be radical)
% wrt. a pure lex term order.
% Returns a list l of dpmats in triangular form.
 if (dpmat_cols m>0) or (dim!* m>0) then
        rederr"Triangularization only for zerodimensional ideals"
 else if not !*noetherian or ring_degrees cali!=basering then
        rederr"Triangularization only for pure lex. term orders"
 else for each x in triang!=triang(m,ring_names cali!=basering) collect
                triang!=makedpmat x;

symbolic procedure triang!=triang(A,vars);
% triang!=triang(A,vars)={f1.x for x in triang!=triang(B,cdr vars)}
%                       \union triang!=triang(A:<B>,vars)
% where A={f1,...,fr}, B={f2~,...fr~}, see [Moeller].
% Returns a list of polynomial lists.
  if dpmat_unitideal!? A then nil
  else begin scalar x,f1,m1,m2,B;
    x:=car vars;
    m1:=sort(for each x in dpmat_list A collect bas_dpoly x,
                function triang!=trsort);
    if length m1 = length vars then return {m1};
    f1:=car m1;
    m2:=for each y in cdr m1 collect bas_make(1,dp_xlt(y,x));
    B:=interreduce!* dpmat_make(length m2,0,m2,nil,nil);
    return append(
    for each u in triang!=triang(B,cdr vars) collect (f1 . u),
                triang!=triang(matstabquot!*(A,B),vars));
    end;

% =================================================================
% Triangularization wrt. an arbitrary term order

symbolic operator zerosolve1;
symbolic procedure zerosolve1 m;
  if !*mode='algebraic then makelist zerosolve1!* dpmat_from_a m
  else zerosolve1!* m;

symbolic procedure zerosolve1!* m;
   for each x in groebfactor!*(m,nil) join triang_triang1 car x;

symbolic procedure triang_triang1 m;
% m must be a zerodim. ideal gbasis (recommended to be radical)
% Returns a list l of dpmats in triangular form.
 if (dpmat_cols m>0) or (dim!* m>0) then
        rederr"Triangularization only for zerodimensional ideals"
 else if not !*noetherian then
        rederr"Triangularization only for noetherian term orders"
 else for each x in triang!=triang1(m,ring_names cali!=basering) collect
                triang!=makedpmat x;

symbolic procedure triang!=triang1(A,vars);
% triang!=triang(A,vars)={f1.x for x in triang!=triang1(B,cdr vars)}
%                       \union triang!=triang1(A:<B>,vars)
% where A={f1,...,fr}, B={f2~,...fr~}, see [Moeller].
% Returns a list of polynomial lists.
  if dpmat_unitideal!? A then nil
  else if length vars = 1 then {{bas_dpoly first dpmat_list A}}
  else (begin scalar u,x,f1,m1,m2,B,vars1,res;
    x:=car vars; vars1:=ring_names cali!=basering;
    setring!* ring_define(vars1,eliminationorder!*(vars1,{x}),
                        'revlex,ring_ecart cali!=basering);
    a:=groebfactor!*(dpmat_neworder(a,nil),nil);
    % Constraints in dimension zero may be skipped :
    a:=for each x in a collect car x;
    for each u in a do
    << m1:=sort(for each x in dpmat_list u collect bas_dpoly x,
                function triang!=trsort);
       f1:=car m1;
       m2:=for each y in cdr m1 collect bas_make(1,dp_xlt(y,x));
       B:=interreduce!* dpmat_make(length m2,0,m2,nil,nil);
       res:=nconc(append(
       for each v in triang!=triang1(B,cdr vars) collect (f1 . v),
                triang!=triang1a(matstabquot!*(u,B),vars)),res);
    >>;
    return res;
    end) where cali!=basering=cali!=basering;

symbolic procedure triang!=triang1a(A,vars);
% triang!=triang(A,vars)={f1.x for x in triang!=triang1(B,cdr vars)}
%                       \union triang!=triang1(A:<B>,vars)
% where A is already a gr basis wrt. the elimination order.
% Returns a list of polynomial lists.
  if dpmat_unitideal!? A then nil
  else if length vars = 1 then {{bas_dpoly first dpmat_list A}}
  else begin scalar u,x,f1,m1,m2,B;
    x:=car vars;
    m1:=sort(for each x in dpmat_list a collect bas_dpoly x,
                function triang!=trsort);
    f1:=car m1;
    m2:=for each y in cdr m1 collect bas_make(1,dp_xlt(y,x));
    B:=interreduce!* dpmat_make(length m2,0,m2,nil,nil);
    return append(
       for each u in triang!=triang1(B,cdr vars) collect (f1 . u),
                triang!=triang1a(matstabquot!*(A,B),vars));
    end;

% =================================================================
% Triangularization wrt. an arbitrary term order and FGLM approach.

symbolic operator zerosolve2;
symbolic procedure zerosolve2 m;
  if !*mode='algebraic then makelist zerosolve2!* dpmat_from_a m
  else zerosolve2!* m;

symbolic procedure zerosolve2!* m;
% Solve a zerodimensional dpmat ideal m, first groebfactoring it and
% secondly triangularizing it.
   for each x in groebfactor!*(m,nil) join triang_triang2 car x;

symbolic procedure triang_triang2 m;
% m must be a zerodim. ideal gbasis (recommended to be radical)
% Returns a list l of dpmats in triangular form.
 if (dpmat_cols m>0) or (dim!* m>0) then
        rederr"Triangularization only for zerodimensional ideals"
 else if not !*noetherian then
        rederr"Triangularization only for noetherian term orders"
 else for each x in triang!=triang2(m,ring_names cali!=basering)
        collect triang!=makedpmat x;

symbolic procedure triang!=triang2(A,vars);
% triang!=triang(A,vars)={f1.x for x in triang!=triang2(B,cdr vars)}
%                       \union triang!=triang2(A:<B>,vars)
% where A={f1,...,fr}, B={f2~,...fr~}, see [Moeller].
% Returns a list of polynomial lists.
  if dpmat_unitideal!? A then nil
  else if length vars = 1 then {{bas_dpoly first dpmat_list A}}
  else (begin scalar u,x,f1,m1,m2,B,vars1,vars2,extravars,res,c1;
    x:=car vars; vars1:=ring_names cali!=basering;
    extravars:=dpmat_from_a('list . (vars2:=setdiff(vars1,vars)));
    % We need this to make A truely zerodimensional.
    c1:=ring_define(vars1,eliminationorder!*(vars1,{x}),
                        'revlex,ring_ecart cali!=basering);
    a:=matsum!* {extravars,a};
    u:=change_termorder!*(a,c1);
    a:=groebfactor!*(dpmat_sieve(u,vars2,nil),nil);
    % Constraints in dimension zero may be skipped :
    a:=for each x in a collect car x;
    for each u in a do
    << m1:=sort(for each x in dpmat_list u collect bas_dpoly x,
                function triang!=trsort);
       f1:=car m1;
       m2:=for each y in cdr m1 collect bas_make(1,dp_xlt(y,x));
       B:=interreduce!* dpmat_make(length m2,0,m2,nil,nil);
       res:=nconc(append(
       for each v in triang!=triang2(B,cdr vars) collect (f1 . v),
                triang!=triang2a(matstabquot!*(u,B),vars)),res);
    >>;
    return res;
    end) where cali!=basering=cali!=basering;

symbolic procedure triang!=triang2a(A,vars);
% triang!=triang(A,vars)={f1.x for x in triang!=triang2(B,cdr vars)}
%                       \union triang!=triang2(A:<B>,vars)
% where A is already a gr basis wrt. the elimination order.
% Returns a list of polynomial lists.
  if dpmat_unitideal!? A then nil
  else if length vars = 1 then {{bas_dpoly first dpmat_list A}}
  else begin scalar u,x,f1,m1,m2,B;
    x:=car vars;
    m1:=sort(for each x in dpmat_list a collect bas_dpoly x,
                function triang!=trsort);
    f1:=car m1;
    m2:=for each y in cdr m1 collect bas_make(1,dp_xlt(y,x));
    B:=interreduce!* dpmat_make(length m2,0,m2,nil,nil);
    return append(
       for each u in triang!=triang2(B,cdr vars) collect (f1 . u),
                triang!=triang2a(matstabquot!*(A,B),vars));
    end;

endmodule; % triang

end;
