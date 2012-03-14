module pm;   % The PM Pattern Matcher.

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


create!-package('(pm pmpatch pattdefn pmintrfc pattperm unify pmrules),
                '(contrib pm));

remflag('(i),'reserved);  % This package uses I as a global index!!

remprop('gamma,'simpfn);  % These routines clash with SPECFN.


Comment This is a fairly basic set of definitions for Ap, Map and Ar.
        It needs some work. The routine Ar is particularly bad;

% Pattern directed application.

symbolic operator ap;

symbolic procedure ap(f,v);
   if car v neq 'list then typerr(v,'ap)
   else if not genexp f then
      if atom f then f . cdr v
      else append(f,cdr v)
   else
   begin scalar nv;
      nv := idsort union(findnewvars f,nil);
      v  := cdr v;
      f := sublis(npair(nv, v), f);
      if length nv < length v then f := append(f,pnth(v,length nv +1));
      return f
    end;

symbolic procedure npair(u, v);
   % Forms list of pairs from unequal length list. Terminates at end of
   % shortest list.
   if u and v then (car u . car v) . npair(cdr u, cdr v) else nil;

%Pattern directed MAP

put('map,'psopfn,'map0);

symbolic procedure map0 arg;
   if length arg < 2 then nil
   else map1(car arg,cadr arg,if length arg >= 3 then caddr arg else 1);

symbolic procedure map1(fn,v,dep);
   if dep>0 then car v . for each j in cdr v collect map1(fn,j,dep-1)
   else ap(fn,if atom v or car v neq 'list then list('list, v) else v);

put('ar, 'psopfn, 'ar0);

% ARange of ARray statement.

symbolic procedure ar0 arg;
   if length arg <= 1 then nil
    else ar1(car arg, if length arg >= 2 then cadr arg else 'list);

symbolic procedure ar1(arg,fn);
if fixp arg then ar4(list(list(1,arg,1)),fn)
else if atom arg or car arg neq 'list then typerr(arg,'ar)
else ar4(for each j in cdr arg collect aarg(j), fn);

symbolic procedure aarg(arg);
   revlis(
   if fixp arg or genp(arg) then list(1, arg, 1)
   else if atom arg  or car arg neq 'list then typerr(arg,'ar)
   else begin scalar l;
      arg := cdr arg;
      l := length arg;
      return if l = 1 then list(1, car arg, 1)
              else if l = 2 then list(car arg, cadr arg, 1)
              else if l = 3 then list(car arg, cadr arg, caddr arg)
              else typerr(arg,"Ar")
     end);

symbolic procedure ar4(lst,fn);
   begin scalar s, u, v, w;
      u := caar lst; v := cadar lst; w := caddar lst; lst := cdr lst;
      while u <= v do
      << s := append(s,list u);
         u := u + w>>;
         return if length(lst)=0 then
            if fn eq 'list then 'list . s
            else  map1(fn, 'list . s, 1)
         else 'list . for each j in cdr map1(list(lst, fn),'list . s, 1)
                         collect ar4(car j, cdr j);
   end;

put('cat, 'psopfn, 'catx);

symbolic procedure catx u;
   % Concatenate two lists.
   (if not eqcar(x,'list) then typerr(car u,"list")
     else if not eqcar(y,'list) then typerr(cadr u,"list")
     else 'list . append(cdr x,cdr y))
   where x=reval car u, y=reval cadr u;


%Relational operators.

symbolic procedure simpeq(arg);
   begin scalar x;
      if length arg < 2 then typerr('equal . arg,"relation");
      arg := reval('difference . arg);
      arg := if numberp arg then reval(arg = 0)
              else <<arg := list('equal,arg, 0);
                     if x := opmtch(arg) then x else arg>>;
      return mksq(arg,1)
   end;

symbolic procedure simpgt(arg);
   begin scalar x;
      if length arg < 2 then typerr('greaterp . arg,"relation");
      arg := reval('difference . arg);
      arg := if numberp arg then reval(arg > 0)
              else <<arg := list('greaterp,arg, 0);
                     if x := opmtch(arg) then x else arg>>;
      return mksq(arg,1)
   end;

symbolic procedure simpge(arg);
   begin scalar x;
      if length arg < 2 then typerr('geq . arg,"relation");
      arg := reval('difference . arg);
      arg := if numberp arg then reval(arg >= 0)
              else <<arg :=  list('geq,arg, 0);
                     if x := opmtch(arg) then x else arg>>;
      return mksq(arg,1)
   end;

symbolic procedure simplt(arg);
   simpgt(list(cadr arg,car arg));

symbolic procedure simple(arg);
   simpge(list(cadr arg,car arg));

put('equal, 'simpfn, 'simpeq);

put('greaterp, 'simpfn, 'simpgt);

put('geq, 'simpfn, 'simpge);

put('lessp, 'simpfn, 'simplt);

put('leq, 'simpfn, 'simple);


% Form function for !?.

symbolic procedure formgen(u,vars,mode);
   begin scalar x;
     u := cadr u;
     if atom u
       then if u eq '!?
             then <<u := intern '!?!?;
                    x := list(mkquote u,mkquote 'mgen,t)>>
             else <<u := intern compress('!! . '!? . explode u);
                    x := list(mkquote u,mkquote 'gen,t)>>
     else if car u neq '!?
      then <<u := intern compress('!! . '!? . explode car u) . cdr u;
             x := list(mkquote car u,mkquote 'gen,t)>>
     else if car u eq '!? and atom cadr u
      then <<u := intern compress('!! . '!? . '!! . '!?
                                      . explode cadr u);
             x := list(mkquote u,mkquote 'mgen,t)>>
     else
     <<u := cadr u;
       u := intern compress('!! . '!? . '!! . '!? . explode car u)
               . cdr u;
       x := list(mkquote car u,mkquote 'gen,t)>>;
      return list('progn,'put . x,form1(u,vars,mode))
   end;

put('!?,'formfn,'formgen)$

endmodule;

end;



endmodule;

end;
