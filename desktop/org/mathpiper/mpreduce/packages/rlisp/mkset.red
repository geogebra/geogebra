module mkset;  % Define a set as a list of expressions enclosed by
               % curly brackets.

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


fluid '(orig!* posn!*);

global '(cursym!*);

% Add to system table.

put('set,'tag,'set);

put('set,'rtypefn,'quoteset);

symbolic procedure quoteset u; 'set;

% Parsing interface.

symbolic procedure mkset;
   % expects a list of expressions enclosed by {, }.
   % also allows expressions separated by ; --- treats these as progn.
   begin scalar cursym,delim,lst;
        if scan() eq '!*rcbkt!* then <<scan(); return list 'set>>;
    a:      lst := aconc(lst,xread1 'group);
        cursym := cursym!*;
        scan();
        if cursym eq '!*rcbkt!*
          then return if delim eq '!*semicol!* then 'progn . lst
                       else 'set . trim lst
         else if null delim then delim := cursym
         else if not(delim eq cursym)
          then symerr("syntax error: mixed , and ; in set",nil);
        go to a
   end;

put('!*lcbkt!*,'stat,'mkset);

newtok '((!{) !*lcbkt!*);

newtok '((!}) !*rcbkt!*);

flag('(!*rcbkt!*),'delim);

flag('(!*rcbkt!*),'nodel);

% Evaluation interface.

put('set,'evfn,'seteval);

symbolic procedure seteval(u,v);
   if atom u then seteval get(u,'set)
    else car u . trim for each x in cdr u collect reval1(x,v);

symbolic procedure trim u;
   % Remove repeated elements from u.
   if null u then nil
    else if car u member cdr u then trim cdr u
    else car u . trim cdr u;

% Length interface.

put('set,'lengthfn,'length);


% Printing interface.

put('set,'prifn,'setpri);

symbolic procedure setpri l;
   % This definition is basically that of INPRINT, except that it
   % decides when to split at the comma by looking at the size of
   % the argument.
   begin scalar orig,split;
      l := cdr l;
      prin2!* "{";
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
   b: prin2!* "}";
      terpri!* nil;
      orig!* := orig
   end;

symbolic procedure treesizep(u,n);
   % true if u has recursively more pairs than n.
   treesizep1(u,n)=0;

symbolic procedure treesizep1(u,n);
   if atom u then n-1
    else if (n := treesizep1(car u,n))>0 then treesizep1(cdr u,n)
    else 0;

endmodule;

end;
