module ideals;          % operators for polynomial ideals.

% Author: Herbert Melenk.

% Copyright (c) 1992 The RAND Corporation and Konrad-Zuse-Zentrum.
% All rights reserved.

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


create!-package('(ideals),'(contrib groebner));

imports groebner;

load!-package 'groebner;

fluid '(gb!-list!*);

global '(id!-vars!*);

share id!-vars!*;

imports idquotienteval, groebnereval, preduceeval, torder ;

exports gb, gb!-equal, gb!-itersect, gb!-member, gb!-quotient, gb!-plus,
gb!-subset, gb!-times, i!-setting, idealp, ideal2list, id!-equal, id!-quotient,
intersection, member, over, subset ;

symbolic procedure i!-setting u;
  begin scalar o;
    o := id!-vars!*;
    id!-vars!* := 'list . for each x in u collect reval x;
    gb!-list!* := nil; return o end;

put('i_setting,'psopfn,'i!-setting);

algebraic operator i;

symbolic procedure ideal2list u; 'list . cdr test!-ideal u;

symbolic operator ideal2list;

symbolic procedure gb u;
  begin scalar v,w;
    u:= test!-ideal reval u;
    v:={u,id!-vars!*,vdpsortmode!*};
    w:=assoc(v,gb!-list!*);
    return if w then cdr w else gb!-new u end;

symbolic procedure gb!-new u;
  begin scalar v,w;
    u:= test!-ideal reval u;
    v:={u,id!-vars!*,vdpsortmode!*};
    w:='I . cdr groebnereval{'list . cdr u,id!-vars!*};
    gb!-list!* := (v.w) . gb!-list!*;
    gb!-list!* := ((w.cdr v).w) . gb!-list!*; return w end;

symbolic operator gb;

symbolic procedure test!-ideal u;
  if not eqcar(id!-vars!*,'list) then
      typerr(id!-vars!*,"ideal setting; set variables first") else
  if eqcar(u,'list) then 'i.cdr u else
  if not eqcar(u,'i) then typerr(u,"polynomial ideal") else u;

symbolic procedure idealp u; eqcar(u,'i) or eqcar(u,'list);

symbolic operator idealp;

newtok '((!. !=) id!-equal);
algebraic operator id!-equal;
infix id!-equal;
precedence id!-equal,=;

symbolic procedure gb!-equal(a,b); if gb a = gb b then 1 else 0;

symbolic operator gb!-equal;

algebraic <<let (~a .= ~b) => gb!-equal(a,b) when idealp a and idealp b>>;

symbolic procedure gb!-member(p,u);
 if 0=preduceeval{p,ideal2list gb u,id!-vars!*} then 1 else 0;

symbolic operator gb!-member;

algebraic operator member;

algebraic <<let ~a member ~b => gb!-member(a,b) when idealp b>>;

symbolic procedure gb!-subset(a,b);
begin scalar q; q:= t; a:=cdr test!-ideal reval a;
 b:=ideal2list gb b; for each p in a do
  q:=q and 0=preduceeval{p,b,id!-vars!*};
 return if q then 1 else 0 end;

symbolic operator gb!-subset;

algebraic operator subset;

infix subset;
precedence subset,member;

algebraic <<let (~a subset ~b) => gb!-subset(a,b) when idealp a and idealp b>>;

symbolic procedure gb!-plus(a,b);
<<a := cdr test!-ideal reval a;
 b := cdr test!-ideal reval b; gb ('i.append(a,b)) >>;

symbolic operator gb!-plus;

algebraic operator .+;

algebraic << let (~a .+ ~b) => gb!-plus(a,b) when idealp a and idealp b>>;

symbolic procedure gb!-times(a,b);
<<a := cdr test!-ideal reval a; b := cdr test!-ideal reval b;
 gb ('i.  for each p in a join for each q in b collect {'times,p,q}) >>;

symbolic operator gb!-times;

algebraic operator .*;

algebraic << let (~a .* ~b) => gb!-times(a,b) when idealp a and idealp b>>;

symbolic procedure gb!-intersect(a,b);
   begin scalar tt,oo,q,v;
      tt:='!-!-t; v:= id!-vars!*;
      oo := eval '(torder '(lex));
      a := cdr test!-ideal reval a;
      b := cdr test!-ideal reval b;
      q:='i. append(
       for each p in a collect {'times,tt,p},
       for each p in b collect {'times,{'difference,1,tt},p});
      id!-vars!* := 'list . tt. cdr id!-vars!*;
      q:= errorset({'gb,mkquote q},nil,!*backtrace);
      id!-vars!* := v;
      eval{'torder,mkquote{oo}};
      if errorp q then rederr "ideal intersection failed";
      q:=for each p in cdar q join if not smemq(tt,p) then {p};
      return gb('i . q) end;

symbolic operator gb!-intersect;

algebraic operator intersection;

algebraic <<let intersection (~a , ~b) => gb!-intersect(a,b)
               when idealp a and idealp b>>;

newtok '((!. !:) id!-quotient);
algebraic operator id!-quotient;
infix id!-quotient;
precedence id!-quotient,/;

symbolic procedure gb!-quotient(a,b);
<<a := test!-ideal reval a; b := test!-ideal reval b; gb!-quotient1(a,cdr b)>>;

symbolic procedure gb!-quotient1(a,b);
begin scalar q; q:='i.cdr idquotienteval{ideal2list a,car b,id!-vars!*};
 return if null cdr b then q else gb!-intersect(q,gb!-quotient1(a,cdr b)) end;

symbolic operator gb!-quotient;
algebraic operator over;

algebraic <<let (~a ./ ~b) => gb!-quotient(a,b) when idealp a and idealp b>>;

algebraic <<let (~a .: ~b) => gb!-quotient(a,b) when idealp a and idealp b>>;

endmodule;;end;
