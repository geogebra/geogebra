module block;   % Block statement and related operators.

% Author: Anthony C. Hearn.

% Copyright (c) 1993 RAND.  All rights reserved.

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


fluid '(!*blockp !*novarmsg !*rlisp88);

global '(!*vars!* cursym!* nxtsym!*);

flag('(novarmsg),'switch);

% ***** GO statement *****

symbolic procedure gostat;
   begin scalar var;
        var := if eq(scan(),'to) then scan() else cursym!*;
        scan();
        return list('go,var)
   end;

put('go,'stat,'gostat);

put('goto,'newnam,'go);


% ***** Declaration Statement *****

symbolic procedure decl u;
   begin scalar varlis,w;
   a: if cursym!* eq '!*semicol!* then go to c
       else if cursym!* eq 'local and !*reduce4 then nil
       else if not flagp(cursym!*,'type) then return varlis
       else if !*reduce4 then typerr(cursym!*,"local declaration");
      w := cursym!*;
      scan();
      if null !*reduce4
        then if cursym!* eq 'procedure then return procstat1 w
              else if cursym!* eq '!*semicol!*
               then progn(lprim list("Empty variable list in",w,"declaration"),
                          return nil)
              else varlis
                  := append(varlis,pairvars(remcomma xread1 nil,nil,w))
       else varlis := append(varlis,read_param_list nil);
      if not(cursym!* eq '!*semicol!*) or null u then symerr(nil,t);
   c: scan();
      go to a
   end;

put('integer,'initvalue!*,0);

symbolic procedure decstat;
  % Called if a declaration occurs at the top level or not first
  % in a block.
  begin scalar x,y,z;
     if !*blockp then symerr('block,t);
     x := cursym!*;
     y := nxtsym!*;
     z := decl nil;
     if y neq 'procedure
       then rerror('rlisp,7,list(x,"invalid outside block"));
     return z
  end;

flag('(integer real scalar),'type);

symbolic procedure blocktyperr u;
   % Type declaration found at wrong position.
   rerror('rlisp,8,list(u,"invalid except at head of block"));


% ***** Block Statement *****

symbolic procedure mapovercar u;
   begin scalar x;
 a: if u then progn(x := caar u . x, u := cdr u, go to a);
   return reversip!* x
   end;

symbolic procedure blockstat;
   begin scalar hold,varlis,x,!*blockp;
        !*blockp := t;
        scan();
        if cursym!* memq '(nil !*rpar!*)
          then rerror('rlisp,9,"BEGIN invalid");
        varlis := decl t;
    a:  if cursym!* eq 'end and not(nxtsym!* eq '!:) then go to b;
        x := xread1 nil;
        if eqcar(x,'end) then go to c;
        not(cursym!* eq 'end) and scan();
        if x
          then progn((if eqcar(x,'equal)
                 then lprim list("top level",cadr x,"= ... in block")),
                      hold := aconc!*(hold,x));
        go to a;
    b:  comm1 'end;
    c:  return mkblock(varlis,hold)
   end;

symbolic procedure mkblock(u,v); 'rblock . (u . v);

putd('rblock,'macro,
 '(lambda (u) (cons 'prog (cons (mapovercar (cadr u)) (cddr u)))));

symbolic procedure symbvarlst(vars,body,mode);
   begin scalar x,y;
      if null(mode eq 'symbolic) then return nil;
      y := vars;
   a: if null y then return nil;
      x := if pairp car y then caar y else car y;
      if not fluidp x and not globalp x and not smemq(x,body)
         and not !*novarmsg
        then lprim list("local variable",x,"in procedure",
                        fname!*,"not used");
      y := cdr y;
      go to a
   end;

symbolic procedure make_prog_declares(v, b);
   begin
!#if (memq 'csl lispsystem!*)
% This detects any bound variables that are fluid (or global) at the
% time I process this code and adds in a DECLARE to remind us about
% that fact.
     scalar w, r;
     w := v;
  l: if null w then go to x;
     if fluidp car w or globalp car w then r := car w . r;
     w := cdr w;
     go to l;
  x: if r then b := list('declare, 'special . r) . b;
!#endif
     return ('prog . v . b)
   end;

symbolic procedure formblock(u,vars,mode);
   begin scalar w;
     symbvarlst(cadr u,cddr u,mode); % Merely report on any unused vars
     w := initprogvars cadr u;
     return make_prog_declares(car w,
                      append(cdr w,
                             formprog1(cddr u,append(cadr u,vars),mode)));
   end;

symbolic procedure initprogvars u;
   begin scalar x,y,z;
    a: if null u then return(reversip!* x . reversip!* y)
       else if null caar u or caar u eq 't then rsverr caar u
       else if (z := get(caar u,'initvalue!*))
          or (z := get(cdar u,'initvalue!*))
        then y := mksetq(caar u,z) . y;
      x := caar u . x;
      u := cdr u;
      go to a
   end;

symbolic procedure formprog(u,vars,mode);
   make_prog_declares(cadr u, formprog1(cddr u,pairvars(cadr u,vars,mode),mode));


symbolic procedure formprog1(u,vars,mode);
   if null u then nil
    else if null car u then formprog1(cdr u,vars,mode)
        % remove spurious NILs, probably generated by FOR statements.
    else if atom car u then car u . formprog1(cdr u,vars,mode)
    else if idp caar u and flagp(caar u,'modefn)
           then if !*rlisp88 and null(caar u eq 'symbolic)
                  then typerr("algebraic expression","Rlisp88 form")
          else formc(cadar u,vars,caar u) . formprog1(cdr u,vars,mode)
    else formc(car u,vars,mode) . formprog1(cdr u,vars,mode);

put('rblock,'formfn,'formblock);

put('prog,'formfn,'formprog);

put('begin,'stat,'blockstat);

flag('(declare), 'noform);

% ***** Return Statement *****

symbolic procedure retstat;
   if not !*blockp then symerr(nil,t)
    else begin scalar !*blockp;  % To prevent RETURN within a RETURN.
            return list('return,
                if flagp(scan(),'delim) then nil else xread1 t)
      end;

put('return,'stat,'retstat);

endmodule;

end;
