module pattperm;   % Rest of unify --- argument permutation, etc.

% Author: Kevin McIsaac.

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


% When sym!-assoc is off, PM does not force normal generic variables to
% take more than one argument if a multi-generic symbol is present. This
% makes the patterns much more efficient but not fully searched. Sane
% patterns do not require this.  For example
% m(a+b+c,?a+??c) will return {?a -> a, ??c -> null!-fn(b,c)} but not
% {?a -> a+b, ??c -> c} or {?a -> a+b+c, ??c -> null!-fn()}

fluid '(symm op r p i upb identity expand acontract mcontract comb);

global('(!*sym!-assoc))$

global('(!*udebug))$      %print out next information

symbolic procedure first0(u,n);
   if n>0 then car u . first0(cdr u,n-1) else nil;

symbolic procedure last0(u,n);
   if n<1 then u else last0(cdr u,n-1);

symbolic procedure list!-mgen u;
   % Count the number of top level mgen atoms.
   begin integer i;
      for each j in u do if atom j and mgenp(j) then i := i+1;
      return i
   end;

symbolic procedure initarg(u);
   begin scalar  assoc, mgen, flex, filler; integer n, lmgen;
      symm := flagp(op,'symmetric);
      n := length(p) - length(r) + 1;
      identity := ident(op);
      mgen  := mgenp(car r);
      lmgen := list!-mgen(cdr r);
      assoc := flagp(op,'assoc)
                  and not(symm and(lmgen > 0) and not !*sym!-assoc);
      flex :=  (length(r)>1) and (assoc or lmgen);
      filler:= n > 1 or (identity and length p > 0);
      %
      mcontract := mgen and filler;
      acontract := assoc and filler and not mgen;
      expand := identity and (n < 1 or flex);
      %
      i := if flex or n < 1 then
              if mgen then 0
              else 1
           else n;
      upb := if identity then length p else n + lmgen;
      if symm then comb := initcomb u
   end;

symbolic procedure nextarg u;
   if symm then s!-nextarg u else o!-nextarg u;

symbolic procedure o!-nextarg u;
   begin scalar args;
      if !*udebug then uprint(nil);
      args :=
         if (i = 1)   and (i <= upb) then u
         else if (i = 0)   and (i <= upb) then '(null!-fn).u
         else if acontract and (i <= upb)
          then mval((op . first0(u,i)) . last0(u,i))
         else if mcontract and (i <= upb)
          then ('null!-fn . first0(u,i)) . last0(u,i)
         else if expand then <<expand := nil; identity . u>>;
      i := i + 1;
      return args
   end;

symbolic procedure s!-nextarg u;
   begin scalar v, args;
      if !*udebug then uprint(nil);
           if null comb then<< i := i + 1; comb := initcomb u>>;
      args :=
      if (v := getcomb(u,comb) ) then
         if (i = 1)   and (i <= upb) then caar v . cdr v
         else if (i = 0)   and (i <= upb) then '(null!-fn).u
         else if acontract and (i <= upb) then mval((op.car(v)).cdr v)
         else if mcontract and (i <= upb) then ('null!-fn.car(v)).cdr v
         else if expand then <<expand := nil; identity . u>>
         else nil
       else if (i = 0)   and (i <= upb) then '(null!-fn).u
       else if expand then <<expand := nil; identity.u>>;
      return args
   end;

symbolic procedure getcomb(u,v);
   begin scalar group;
      comb :=  nextcomb(v,i);
      group := car comb;
      comb := cdr comb;
      return if group then group . setdiff(u,group) else nil
   end$

symbolic procedure uprint(u);
   <<if expand then <<prin2('expand);prin2(" ")>>;
     if mcontract then <<prin2('mcontract);prin2(" ")>>;
     if acontract then <<prin2('acontract);prin2("  ")>>;
        prin2(" upb = ");prin2(upb); prin2(" i = ");prin2(i);
     if symm then <<prin2('symmetric);prin2(comb)>>;
     terpri()>>$


symbolic procedure initcomb(u); u.nil$

symbolic procedure nextcomb(env,n);
   % Env is of the form args . env, where args is a list of arguments.
   % Value is list of all combinations of n elements from the list u.
   begin scalar args, nenv, v; integer i;
      args := car env; nenv := cdr env;
      return
         if n=0 then nil.nil
         else if (i:=length(args) - n)<0 then list(nil)
         else if i = 0 then args.nil
         else if nenv then <<v := nextcomb(nenv,n - 1);
                             (car(args) . car(v)) .
                                (if cdr v then args . cdr v
                                  else list cdr(args))>>
         else <<v := nextcomb(initcomb(cdr args),n - 1);
                (car(args) . car(v)) . (if cdr v then args . cdr v
                                        else list cdr(args))>>
   end;

endmodule;

end;
