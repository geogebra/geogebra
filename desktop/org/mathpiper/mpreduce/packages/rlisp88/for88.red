module for88;   % Definition of Rlisp88 FOR statement.

% Author: Anthony C. Hearn.

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


fluid '(!*fastfor binops!* loopdelimslist!*);

global '(forkeywords!*);

flag('(fastfor),'switch);     % Since switch may not yet be defined.

Comment The FOR statement defined here has a very rich syntax with many
different options. The parsing and macro expansion are under the control
of keywords that are activated during parsing once FOR has been read.
The keywords are deactivated at the end of the FOR statement, enabling
them to be used as regular ID's in other parts of the program.

The next ID after FOR may define a different type of FOR loop. Such
different loops are indicated by the presence of the ID in the list
forloops!*;

deflist('((all forallstat)),'forloops!*);

Comment
Keywords are defined by their presence in the global list FORKEYWORDS!*.
For each keyword, a parsing construct is also defined under the
indicator FOR-KEYWORD.

The parsing phase of the analysis returns a form:

(FOR (<keyword> . <expression>) ... (<keyword> . <expression>));

forkeywords!* := '(collect count do each every finally in initially
                   join on product returns some step sum unless until
                   when with maximize minimize);

% Note: append used to be on the above list, but was removed since it
%       couldn't be distinguished from the function "append".

remflag(forkeywords!*,'delim);    % For bootstrapping purposes.

Comment some of the keywords denote actions (e.g., PRODUCT, SUM) with
which a binary function is associated. To associate such a function with
an action, one says;

forbinops!* := '((append append) (collect cons) (count plus2)
                 (join nconc) (maximize max2!*) (minimize min2!*)
                 (product times2) (sum plus2));

% NB:  We need to reset FOR and LET delims if an error occurs.  It's
% probably best to do this in the begin1 loop.

symbolic procedure forstat88;
   begin scalar !*blockp,x;
      if x := get(scan(),'forloops!*) then return lispapply(x,nil);
      loopdelimslist!* := forkeywords!* . loopdelimslist!*;
      flag(forkeywords!*,'delim);
      return 'for . if cursym!* neq 'each
                      then progn(x := forfrag(), x . fortail())
                     else fortail()
   end;

symbolic procedure forfrag;
   begin scalar incr,var,x;
      x := erroreval '(xread1 'for);
      if not eqcar(x,'setq) or not idp(var := cadr x)
        then symerr('for,t);
      x := caddr x;
      if cursym!* eq 'step
        then <<incr := erroreval '(xread t);
               if not(cursym!* eq 'until) then symerr('for,t)>>
       else if cursym!* eq '!*colon!* then incr := 1
       else symerr('for,t);
      return list('incr,var,x,erroreval '(xread t),incr)
%     if numberp incr and incr>0
%       then incr := list('from,var,x,erroreval '(xread t),incr)
%      else if eqcar(incr,'minus) and numberp cadr incr and cadr incr>0
%       then incr := list('down,var,x,erroreval '(xread t),cadr incr)
%      else rederr list("Increment",incr,"not supported");
%     return incr
   end;

symbolic procedure erroreval u;
   begin scalar x;
      x := errorset!*(u,t);
      if errorp x then error1() else return car x
   end;

symbolic procedure eachfrag;
   begin scalar x,y;
        if not idp(x := scan()) or not((y := scan()) memq '(in on))
          then symerr("For each",t);
        return list(y,x,erroreval '(xread t));
   end;

symbolic procedure fortail;
   begin scalar x,y,z,z1;
   a:   z1 := cursym!*;
        if z1 eq 'each
          then if not idp(x := scan())
                   or not((y := scan()) memq '(in on))
                 then symerr("FOR EACH",t)
                else <<z := list(y,x,erroreval '(xread t)) . z;
                       go to a>>
         else if z1 eq 'with
          then z := (z1 . erroreval '(xread 'lambda)) . z
         else if z1 eq '!*semicol!* then symerr("FOR EACH",t)
         else z := (z1 . erroreval '(xread t)) . z;
        if cursym!* memq forkeywords!* then go to a;
        remflag(car loopdelimslist!*,'delim);
        loopdelimslist!* := cdr loopdelimslist!*;
        if loopdelimslist!* then flag(car loopdelimslist!*,'delim);
        return reversip z
   end;

symbolic procedure formfor88(u,vars,mode);
   begin scalar x,y,z;
      u := z := cdr u;
      % First check for local vars.
   a: if null z then go to b;
      x := car z;
      if car x memq '(down from incr in on)
        then vars := (cadr x . 'scalar) . vars;
      if null(car x eq 'with) then progn(z := cdr z,go to a);
      x := remcomma cdr x;
  a0: if x then progn(y := (car x . 'scalar) . y, x := cdr x, go to a0);
      vars := nconc(reversip!* y,vars);
      z := cdr z;
      go to a;
      % Now do actual analysis.
   b: if null u then return 'for . reversip z;
      x := car u;
      if car x memq '(down from incr)
        % We could optimize this by recognizing integers.
        then z := (car x . cadr x . formclis(cddr x,vars,mode)) . z
       else if car x eq 'with then z := (car x . remcomma cdr x) . z
       else if car x memq '(in on)
        then z := (car x . list(cadr x,formc(caddr x,vars,mode))) . z
       else z := (car x . formc(cdr x,vars,mode)) . z;
      u := cdr u;
      go to b
   end;

symbolic macro procedure for88 x;
   begin scalar lvars,init,init2,final,body,!$cond,rets,cur,!$when,
                !*maxminflag,next,!$label2,!$while,cx,iv,action,curvar,
                valuevar,y;
      x := cdr x;
      action := caar x;
      !$label2 := gensym();
   loop:
      if null x
        then <<final := mkfn(final,'progn);
               next := mkfn(next,'progn);
               !$cond := mkfn(!$cond,'or);
               cur := mkfn(cur,'progn);
               body := mkfn(body,'progn);
               if !$while
                 then !$while := forcond
                                   sublis(pair('(!$while final rets),
                                             list(mkfn(!$while,'or),
                                                  final,rets)),
                                                '(!$while final
                                                  (return rets)));
               if !$when
                 then body := forcond list(!$when,body);
               if !*maxminflag then rets := list('null2zero,rets);
               return forprog(lvars .
                          nconc(init,
                                nconc(init2,
                      sublis(pair('(final body !$cond rets cur next
                                    !$label !$label2 !$while),
                                  list(final,body,!$cond,rets,cur,next,
                                       gensym(),!$label2,!$while)),
                               if final then
                                '(!$label
                                     (cond (!$cond
                                           (progn final (return rets))))
                                     cur
                                     !$while
                                     body
                                  !$label2
                                     next
                                     (go !$label))
                               else
                                '(!$label
                                     (cond (!$cond (return rets)))
                                     cur
                                     !$while
                                     body
                                  !$label2
                                     next
                                     (go !$label))))))>>;
      cx := car x;
      if atom cx then rederr list(cx,"invalid in FOR form")
      % WITH tacks its variables onto the !$LVARS list
       else if car cx eq 'with
        then lvars := append(lvars,cdr cx)
      % INITIALLY takes its expressions and tacks them onto the list of
      % INIT.  This will later be built into a PROGN.
       else if car cx eq 'initially
        then init := aconc(init,cdr cx)
      % FINALLY puts its expressions on the list of FINAL.
      % This becomes a PROGN that is created just before the RETURN.
       else if car cx eq 'finally
        then final := aconc(final,cdr cx)
      % ON
       else if car cx eq 'on
        then <<valuevar := cadr cx;
               lvars := valuevar . lvars;
               !$cond := list('null,valuevar) . !$cond;
               init := list('setq,valuevar,caddr cx) . init;
               if cdddr cx
                 then next := list('setq,valuevar,cadddr x) . next
                else next := list('setq, valuevar,list('cdr,valuevar))
                                  . next>>
      % IN
       else if car cx eq 'in
        then <<valuevar := gensym();
               iv := cadr cx;
               lvars := valuevar . iv . lvars;
               init := list('setq,valuevar,caddr cx) . init;
               !$cond := list('null,valuevar) . !$cond;
               cur := list('setq,iv,list('car,valuevar)) . cur;
               if cdddr cx
                 then next := list('setq,valuevar,list cadddr cx) . next
                else next := list('setq,valuevar,list('cdr,valuevar))
                                . next>>
       % INCR
       else if car cx eq 'incr
        then begin scalar incr,incrvar;
                valuevar := cadr cx;
                cx := cddr cx;
                lvars := valuevar . lvars;
                init := list('setq,valuevar,car cx) . init;
                incr := caddr cx;
                if numberp incr then nil             % Assume positive?
                 else if eqcar(incr,'minus) and numberp cadr incr
                  then incr := - cadr incr
                 else <<incrvar := gensym();
                        lvars := incrvar . lvars;
                        init := list('setq,incrvar,incr) . init;
                        incr := incrvar>>;
                !$cond :=
                   (if incrvar
                      then list('cond,list(list('minusp,incr),
                                         list('lessp,valuevar,cadr cx)),
                                        list('t,list('greaterp,valuevar,
                                                     cadr cx)))
                     else if minusp incr
                      then if !*fastfor
                             then list('ilessp,valuevar,cadr cx)
                            else list('lessp,valuevar,cadr cx)
                     else if !*fastfor
                      then list('igreaterp,valuevar,cadr cx)
                     else list('greaterp,valuevar,cadr cx))
                    . !$cond;
                next := list('setq,valuevar,
                             list(if incrvar or not !*fastfor
                                    then 'plus2
                                   else 'iplus2,
                                  valuevar,incr)) . next
              end
      % SUM, PRODUCT etc.
       else if car cx memq '(sum product append join count collect
                             maximize minimize)
        then <<curvar := gensym();
               lvars := curvar . lvars;
               % Set up initial value for loop.
               if car cx eq 'product
                 then init := aconc!*(init,list('setq,curvar,1))
                else if car cx memq '(count sum)
                 then init := aconc!*(init,list('setq,curvar,0))
                else if car cx memq '(maximize minimize)
                 then <<!*maxminflag := t;
                        %y := list(list('setq,curvar,cdr cx),
                        %            list('go,!$label2));
                        if action eq 'in
                          then y :=
                              list('setq,iv,list('car,valuevar)); % . y;
                        if action memq '(in on)
                         then y :=
                                 list('cond,list(list('null,valuevar),
                                      '(return 0)))
                               . y;
                       nconc!*(init,y)>>;
               if car cx eq 'collect
                 then rets := list('reversip,curvar)
                else rets := curvar;
               body := list('setq,curvar,
                            list(get(car cx,'bin),
                if car cx memq '(append count join) then curvar
                 else cdr cx,
                if car cx memq '(append join) then cdr cx
                 else if car cx eq 'count
                  then list('cond,list(cdr cx,1),'(t 0))
                 else curvar))
                       . body>>
      % RETURNS
       else if car cx eq 'returns then rets := cdr cx
      % DO
       else if car cx eq 'do then body := aconc(body,cdr cx)
      % WHEN
       else if car cx eq 'when
        then if !$when
               then symerr("Redundant WHEN or UNLESS in FOR statement",
                           nil)
              else !$when := cdr cx
      % UNLESS
       else if car cx eq 'unless
        then if !$when
               then symerr("Redundant WHEN or UNLESS in FOR statement",
                           nil)
              else !$when := list('not,cdr cx)
      % WHILE
%      else if car cx eq 'while
%       then !$while := append(!$while,list list('not,cdr cx))
      % UNTIL
       else if car cx eq 'until
        then !$while := append(!$while,list cdr cx)
      % SOME
       else if car cx eq 'some
        then cur := append(cur,
                          list list('cond,list(cdr cx,list('return,t))))
      % EVERY
       else if car cx eq 'every
        then <<if not rets then rets := t;
               cur := append(cur,
                             list list('cond,list(list('null,cdr cx),
                                                  list('return,nil))))>>
       else rederr list(car cx,"invalid in FOR form");
      x := cdr x;
      go to loop
   end;

symbolic procedure forcond u;
   list('cond,list(car u,if cddr u then 'progn . cdr u else cadr u));

symbolic procedure forprog u;
   'prog . fornilchk u;

symbolic procedure fornilchk u;
   if null u then nil
    else if null car u then fornilchk cdr u
    else car u . fornilchk cdr u;

symbolic procedure max2!*(u,v); if null v then u else max2(u,v);

symbolic procedure min2!*(u,v); if null v then u else min2(u,v);

symbolic procedure null2zero u; if null u then 0 else u;

symbolic procedure mkfn(x,fn);
  if atom x then x else if length x>1 then fn . x else car x;

endmodule;

end;
