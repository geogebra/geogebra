module ring;

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

               ##################
               ##              ##
               ##    RINGS     ##
               ##              ##
               ##################


Informal syntax :

Ring = ('RING (name list) ((degree list list)) deg_type ecart)
        with deg_type = 'lex or 'revlex.

The term order is defined at first comparing successively degrees and
then by the name list lex. or revlex. For details consult the manual.

(name list) contains a phantom name cali!=mk for the module
component, see below in module mo.

The variable cali!=basering contains the actual base ring.

The ecart is a list of positive integers (the ecart vector for the
given ring) and has
        length = length names cali!=basering.
It is used in several places for optimal strategies (noetherina term
orders ) or to guarantee termination (local term orders).
All homogenizations are executed with respect to that list.

END COMMENT;

symbolic procedure ring_define(n,to1,type,ecart);
    list('ring,'cali!=mk . n, to1, type,ecart);

symbolic procedure setring!* c;
  begin
     if !*noetherian and not ring_isnoetherian c then
        rederr"term order is not noetherian";
     cali!=basering:=c;
     setkorder ring_all_names c;
     return c;
  end;

symbolic procedure setecart!* e;
  begin scalar r; r:=cali!=basering;
    if not ring_checkecart(e,ring_names r) then
        typerr(e,"ecart vector")
    else cali!=basering:=
        ring_define(ring_names r,ring_degrees r,ring_tag r,e)
  end;

symbolic procedure ring_2a c;
    makelist {makelist ring_names c,
          makelist for each x in ring_degrees c collect makelist x,
          ring_tag c, makelist ring_ecart c};

symbolic procedure ring_from_a u;
  begin scalar vars,tord,c,r,tag,ecart;
  if not eqcar(u,'list) then typerr(u,"ring") else u:=cdr u;
  vars:=reval car u; tord:=reval cadr u; tag:=reval caddr u;
  if length u=4 then ecart:=reval cadddr u;
  if not(tag memq '(lex revlex)) then typerr(tag,"term order tag");
  if not eqcar(vars,'list) then typerr(vars,"variable list")
  else vars:=cdr vars;
  if tord={'list} then c:=nil
  else if not (c:=ring!=testtord(vars,tord)) then
        typerr(tord,"term order degrees");
  if null ecart then
     if (null tord)or not ring_checkecart(car tord,vars) then
                ecart:=for each x in vars collect 1
     else ecart:=car tord
  else if not ring_checkecart(cdr ecart,vars) then
        typerr(ecart,"ecart list")
  else ecart:=cdr ecart;
  r:=ring_define(vars,c,tag,ecart);
  if !*noetherian and not(ring_isnoetherian r) then
        rederr"Term order is non noetherian";
  return r
  end;

symbolic procedure ring!=testtord(vars,u);
% Test the non empty term order degrees for consistency and return
% the symbolic equivalent of u.
  if (ring!=lengthtest(cdr u,length vars +1)
        and ring!=contenttest cdr u)
            then for each x in cdr u collect cdr x
  else nil;

symbolic procedure ring!=lengthtest(m,v);
% Test, whether m is a list of (algebraic) lists of the length v.
  if null m then t
  else eqcar(car m,'list)
        and (length car m = v)
        and ring!=lengthtest(cdr m,v);

symbolic procedure ring!=contenttest m;
% Test, whether m is a list of (algebraic) number lists.
  if null m then t
  else numberlistp cdar m and ring!=contenttest cdr m;

symbolic procedure ring_names r; % User names only
        cdadr r;

symbolic procedure ring_all_names r; cadr r; % All names

symbolic procedure ring_degrees r; caddr r;

symbolic procedure ring_tag r; cadddr r;

symbolic procedure ring_ecart r; nth(r,5);

% --- Test the term order for the chain condition ------

symbolic procedure ring!=trans d;
% Transpose the degree matrix.
  if (null d)or(null car d) then nil
  else (for each x in d collect car x) .
                ring!=trans(for each x in d collect cdr x);

symbolic procedure ring!=testlex d;
   if null d then t
   else ring!=testlex1(car d) and ring!=testlex(cdr d);

symbolic procedure ring!=testlex1 d;
   if null d then t
   else if car d=0 then ring!=testlex1(cdr d)
   else (car d>0);

symbolic procedure ring!=testrevlex d;
   if null d then t
   else ring!=testrevlex1(car d) and ring!=testrevlex(cdr d);

symbolic procedure ring!=testrevlex1 d;
   if null d then nil
   else if car d=0 then ring!=testrevlex1(cdr d)
   else (car d>0);

symbolic procedure ring_isnoetherian r;
% Test, whether the term order of the ring r satisfies the chain
% condition.
  if ring_tag r ='revlex then
                ring!=testrevlex ring!=trans ring_degrees r
  else ring!=testlex ring!=trans ring_degrees r;

symbolic procedure ring!=degpos d;
  if null d then t
  else (car d>0) and ring!=degpos cdr d;

symbolic procedure ring_checkecart(e,vars);
  (length e=length vars) and ring!=degpos e;

% ---- Test noetherianity switching noetherian on :

put('noetherian,'simpfg,'((t (ring!=test))));

symbolic procedure ring!=test;
  if not ring_isnoetherian cali!=basering then
    << !*noetherian:=nil;
       rederr"Current term order is not noetherian"
    >>;

% ---- Different term orders -------------

symbolic operator eliminationorder;
symbolic procedure eliminationorder(v1,v2);
% Elimination order : v1 = all variables; v2 = variables to eliminate.
  if !*mode='algebraic then
        makelist for each x in
                eliminationorder!*(cdr reval v1,cdr reval v2)
                        collect makelist x
  else eliminationorder!*(v1,v2);

symbolic operator degreeorder;
symbolic procedure degreeorder(vars);
  if !*mode='algebraic then
        makelist for each x in degreeorder!*(cdr reval vars) collect
                makelist x
  else degreeorder!*(vars);

symbolic operator localorder;
symbolic procedure localorder(vars);
  if !*mode='algebraic then
        makelist for each x in localorder!*(cdr reval vars) collect
                makelist x
  else localorder!*(vars);

symbolic operator blockorder;
symbolic procedure blockorder(v1,v2);
  if !*mode='algebraic then
        makelist for each x in
                blockorder!*(cdr reval v1,cdr reval v2)
                        collect makelist x
  else blockorder!*(v1,v2);

symbolic procedure blockorder!*(vars,l);
% l is a list of integers, that sum up to |vars|.
% Returns the degree vector for the corresponding block order.
  if neq(for each x in l sum x,length vars) then
        rederr"block lengths sum doesn't match variable number"
  else begin scalar u; integer pre,post;
  pre:=0; post:=length vars;
  for each x in l do
  << u:=(append(append(for i:=1:pre collect 0,for i:=1:x collect 1),
                for i:=1:post-x collect 0)) . u;
     pre:=pre+x; post:=post-x
  >>;
  return reversip u;
  end;

symbolic procedure eliminationorder!*(v1,v2);
% Elimination order : v1 = all variables
% v2 = variables to eliminate.
  { for each x in v1 collect
        if x member v2 then 1 else 0,
    for each x in v1 collect
        if x member v2 then 0 else 1};

symbolic procedure degreeorder!*(vars);
  {for each x in vars collect 1};

symbolic procedure localorder!*(vars);
  {for each x in vars collect -1};

% ---------- Ring constructors -----------------

symbolic procedure ring_rlp(r,u);
% u is a subset of ring_names r. Returns the ring r with the block order
% "first degrevlex on u, then the order on r"
  ring_define(ring_names r,
  (for each x in ring_names r collect if x member u then 1 else 0)
  . append(reverse for each x in u collect
        for each y in ring_names r collect if x=y then -1 else 0,
                ring_degrees r), ring_tag r, ring_ecart r);

symbolic procedure ring_lp(r,u);
% u is a subset of ring_names r. Returns the ring r with the block order
% "first lex on u, then the order on r"
  ring_define(ring_names r,
  append(for each x in u collect for each y in ring_names r collect
                if x=y then 1 else 0, ring_degrees r),
        ring_tag r, ring_ecart r);

symbolic procedure ring_sum(a,b);
% Returns the direct sum of two base rings with degree matrix at
% first b then a and ecart=appended ecart lists.
  begin scalar vars,zeroa,zerob,degs,ecart;
  if not disjoint(ring_names a,ring_names b) then
    rederr"RINGSUM only for disjoint variable sets";
  vars:=append(ring_names a,ring_names b);
  ecart:=append(ring_ecart a,ring_ecart b);
  zeroa:=for each x in ring_names a collect 0;
  zerob:=for each x in ring_names b collect 0;
  degs:=append(
    for each x in ring_degrees b collect append(zeroa,x),
    for each x in ring_degrees a collect append(x,zerob));
  return ring_define(vars, degs, ring_tag a,ecart);
  end;

% --------- First initialization :

setring!* ring_define('(t x y z),'((1 1 1 1)),'revlex,'(1 1 1 1));

!*noetherian:=t;

% -------- End of first initialization ----------------

endmodule;  % ring

end;
