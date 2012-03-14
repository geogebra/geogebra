module list; % Define a list as a list of expressions in curly brackets.

% Author: Anthony C. Hearn.

% Copyright (c) 1987 The RAND Corporation.  All rights reserved.

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


fluid '(orig!* posn!*);

global '(cursym!* simpcount!* simplimit!*);

% Add to system table.

put('list,'tag,'list);

put('list,'rtypefn,'quotelist);

symbolic procedure quotelist u; 'list;

% Parsing interface.

symbolic procedure nconc2 (u,v);
  %. Destructive version of Append returning pointer to tail
  begin scalar w;
    if atom u then return v;
    w := u;
    while pairp cdr w do w := cdr w;
        rplacd (w,v);
        return w;
  end;

% using nconc2 here to allow for very long lists to be read
% nconc would always search the end of the list from the top

symbolic procedure xreadlist;
   % Expects a list of expressions enclosed by {, }.
   % Used to allow expressions separated by ; - treated these as progn.
   begin scalar cursym,delim,lst,lst2;
        if scan() eq '!*rcbkt!* then <<scan(); return list 'list>>;
    a:  if null lst then << lst := lst2  := aconc(lst,xread1 'group)>>
        else lst2 := nconc2 (lst2,list(xread1 ' group));;
        cursym := cursym!*;
        if cursym eq '!*semicol!*
          then symerr("Syntax error: semicolon in list",nil)
         else if scan() eq '!*rcbkt!* and cursym eq '!*comma!*
          then symerr("Syntax error: invalid comma in list",nil);
        if cursym eq '!*rcbkt!*
          then return % if delim eq '!*semicol!*
                      %   then 'progn . lst else
                      'list . lst
         else if null delim then delim := cursym;
%        else if not(delim eq cursym)
%         then symerr("Syntax error: mixed , and ; in list",nil);
        go to a
   end;

put('!*lcbkt!*,'stat,'xreadlist);

newtok '((!{) !*lcbkt!*);

newtok '((!}) !*rcbkt!*);

flag('(!*rcbkt!*),'delim);

flag('(!*rcbkt!*),'nodel);

% Evaluation interface.

put('list,'evfn,'listeval);

put('list,'simpfn,'simpiden);  % This is a little kludgey, but allows
                              % things like dms2deg to work.

symbolic procedure getrlist u;
   if eqcar(u,'list) then cdr u
    else typerr(if eqcar(u,'!*sq) then prepsq cadr u else u,"list");

symbolic procedure listeval(u,v);
   <<if (simpcount!* := simpcount!*+1)>simplimit!*
       then <<simpcount!* := 0;
              rerror(rlisp,18,"Simplification recursion too deep")>>;
     u := if atom u
            then listeval(if flagp(u,'share) then eval u
                           else if x then cadr x else typerr(u,'list),v)
                                  where x=get(u,'avalue)
           else if car u eq 'list
            then makelist for each x in cdr u collect reval1(x,v)
           else ((if x then apply2(x,cdr u,v)
                   else rerror(rlisp,19,"Illegal operation on lists"))
                 where x = get(car u,'listfn));
     simpcount!* := simpcount!* - 1;
     u>>;

symbolic procedure makelist u;
   % Make a list out of elements in u.
   'list . u;


% Length interface.

put('list,'lengthfn,'lengthcdr);

symbolic procedure lengthcdr u; length cdr u;


% Printing interface.

put('list,'prifn,'listpri);

symbolic procedure listpri l;
   % This definition is basically that of INPRINT, except that it
   % decides when to split at the comma by looking at the size of
   % the argument.
   begin scalar orig,split,u;
      u := l;
      l := cdr l;
      prin2!* get('!*lcbkt!*,'prtch);
         % Do it this way so table can change.
      orig := orig!*;
      orig!* := if posn!*<18 then posn!* else orig!*+3;
      if null l then go to b;
      split := treesizep(l,40);   % 40 is arbitrary choice.
   a: maprint(negnumberchk car l,0);
      l := cdr l;
      if null l then go to b;
      oprin '!*comma!*;
      if split then terpri!* t;
      go to a;
   b: prin2!* get('!*rcbkt!*,'prtch);
%     terpri!* nil;
      orig!* := orig;
      return u
   end;

symbolic procedure treesizep(u,n);
   % true if u has recursively more pairs than n.
   treesizep1(u,n)=0;

symbolic procedure treesizep1(u,n);
   if atom u then n - 1
    else if (n := treesizep1(car u,n))>0 then treesizep1(cdr u,n)
    else 0;

% Definitions of operations on lists.

symbolic procedure listeval0 u;
   begin scalar v;
     if (simpcount!* := simpcount!*+1)>simplimit!*
        then <<simpcount!* := 0;
                rerror(rlisp,20,"Simplification recursion too deep")>>;
     if idp u
       then if flagp(u,'share) then u := listeval0 eval u
             else if (v := get(u,'avalue)) and cadr v neq u
              then u := listeval0 cadr v;
     simpcount!* := simpcount!* - 1;
     return u
   end;

% First, second, third and rest are designed so that only the relevant
% elements need be fully evaluated.

symbolic smacro procedure rlistp u; eqcar(u,'list);

symbolic procedure rfirst u;
   begin scalar x;
      u := car u;
%     if null(getrtype(x := listeval0 u) eq 'list)
%        and null(getrtype(x := aeval u) eq 'list)
      if not rlistp(x := listeval0 u) and not rlistp(x := aeval u)
        then typerr(u,"list");
      if null cdr x then parterr(u,1) else return reval cadr x
   end;

put('first,'psopfn,'rfirst);

symbolic procedure parterr(u,v);
   msgpri("Expression",u,"does not have part",v,t);

symbolic procedure rsecond u;
   begin scalar x;
      u := car u;
      if not rlistp(x := listeval0 u) and not rlistp(x := aeval u)
        then typerr(u,"list");
      if null cdr x or null cddr x then parterr(u,2)
       else return reval caddr x
   end;

put('second,'psopfn,'rsecond);

symbolic procedure rthird u;
   begin scalar x;
      u := car u;
      if not rlistp(x := listeval0 u) and not rlistp(x := aeval u)
        then typerr(u,"list");
      if null cdr x or null cddr x or null cdddr x then parterr(u,3)
       else return reval cadddr x
   end;

put('third,'psopfn,'rthird);

deflist('((first (lambda (x) 'yetunknowntype))
          (second (lambda (x) 'yetunknowntype))
          (third (lambda (x) 'yetunknowntype))
          (part (lambda (x) 'yetunknowntype))),
        'rtypefn);

symbolic procedure rrest u;
   begin scalar x;
      argnochk('cdr . u);
      u := car u;
      if not rlistp(x := listeval0 u) and not rlistp(x := aeval u)
        then typerr(u,"list");
      if null cdr x then typerr(u,"non-empty list")
       else return 'list . for each y in cddr x collect reval y
   end;

put('rest,'psopfn,'rrest);

deflist('((first 1) (second 1) (third 1) (rest 1)),'number!-of!-args);

symbolic procedure rappend u;
   begin scalar x,y;
      argnochk('append . u);
      if null(getrtype(x := reval car u) eq 'list)
        then typerr(x,"list")
      else if null(getrtype(y := reval cadr u) eq 'list)
       then typerr(y,"list")
      else return 'list . append(cdr x,cdr y)
   end;

put('append,'psopfn,'rappend);

symbolic procedure rcons u;
   begin scalar x,y,z;
      argnochk('cons . u);
      if (y := getrtypeor(x := revlis u)) eq 'hvector
    then return if get('cons,'opmtch) and (z := opmtch('cons . x))
                   then reval z
                 else prepsq subs2 simpdot x
       else if not(getrtype cadr x eq 'list) then typerr(x,"list")
       else return 'list . car x . cdadr x
   end;

put('cons,'psopfn,'rcons);

symbolic procedure rreverse u;
   <<argnochk ('reverse . u);
     if null(getrtype(u := reval car u) eq 'list) then typerr(u,"list")
      else 'list . reverse cdr u>>;

put('reverse,'psopfn,'rreverse);

% Aggregate Property.

symbolic procedure listmap(u,v);
   begin scalar x;
      x := cadr u;
      if null eqcar(x,'list) and null eqcar(x := reval1(x,v),'list)
        then typerr(cadr u,"list");
      return 'list
              . for each j in cdr x collect reval1(car u . j . cddr u,v)
   end;

put('list,'aggregatefn,'listmap);

% Sorting.

fluid '(sortfcn!*);

symbolic procedure listsort u;
   begin scalar l,n,w;
     if length u neq 2 then goto err;
     l:=cdr listeval(car u,nil);
     sortfcn!*:=cadr u;
     if(w:=get(sortfcn!*,'boolfn)) then sortfcn!*:=w;
     if null getd sortfcn!* or
      (n:=get(sortfcn!*,'number!-of!-args)) and n neq 2
        then goto err;
     return 'list.sort(l,w or
        function(lambda(x,y);
           boolvalue!* reval {sortfcn!*,mkquote x,mkquote y}));
 err: rederr "illegal call to list sort";
   end;

put('sort,'psopfn,'listsort);

endmodule;

end;
