module rend; % CL REDUCE "back-end".

% Copyright (c) 1993 RAND.  All Rights Reserved.

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


fluid '(lispsystem!*);

lispsystem!* := '(cl);

symbolic procedure delcp u;
   % Returns true if U is a semicolon, dollar sign, or other delimiter.
   % This definition replaces one in the BOOT file.
   u eq '!; or u eq '!$;

symbolic procedure seprp u;
   % Returns true if U is a blank or other separator (eg, tab or ff).
   % This definition replaces one in the BOOT file.
     u eq '!  or u eq '!	 or u eq !$eol!$;

% Common LISP specific definitions.

flag('(load),'opfn);

% The next one is added since it is a familiar name for this operation.

symbolic procedure prop u; symbol!-plist u;

% A machine independent traceset. Tr and untr are defined in clend.lisp.

symbolic procedure traceset1 u;
   if atom u then u
    else if car u eq 'setq
     then list('progn,
               list('prin2,mkquote cadr u),
               '(prin2 " := "),
               u,
               list('prin2t,cadr u))
    else traceset1 car u . traceset1 cdr u;

symbolic procedure traceset u;
   if get(u,'original!-defn) then lprim list(u,"already traceset")
    else (if not x or not(eqcar(cdr x,'lambda)
                       or eqcar(cdr x,'lambda!-closure))
            then lprim list(u,"has wrong form for traceset")
           else <<put(u,'original!-defn,x);
                  remd u;   % To prevent spurious messages.
                  putd(u,car x,traceset1 cdr x)>>)
          where x=getd u;

symbolic procedure untraceset u;
   (if x
      then <<remprop(u,'original!-defn);
             remd u;   % To prevent spurious messages.
             putd(u,car x,cdr x)>>
     else lprim list(u,"not traceset"))
    where x=get(u,'original!-defn);

symbolic procedure trst u; for each x in u do traceset x;

symbolic procedure untrst u; for each x in u do untraceset x;

deflist('((tr rlis) (untr rlis) (trst rlis) (untrst rlis)),'stat);


% The following function is necessary in Common Lisp startup sequence,
% since initial packages are not loaded with load-package.

symbolic procedure fixup!-packages!*;
   for each x in '(rlisp clrend entry poly arith alg mathpr) do
      if not(x memq loaded!-packages!*)
        then <<loaded!-packages!* := x . loaded!-packages!*;
               if (x := get(x,'patchfn)) then eval list x>>;


% The FACTOR module also requires a definition for GCTIME. Since this
% is currently undefined in CL, we provide the following definition.

symbolic procedure gctime; 0;

% yesp1 is more or less equivalent to y-or-n-p.

remflag('(yesp1),'lose);

symbolic procedure yesp1; y!-or!-n!-p();

flag('(yesp1),'lose);

% The Common Lisp TOKEN function returns tokens rather than characters,
% so CEDIT must be modified.

remflag('(cedit),'lose);

symbolic procedure cedit n;
   begin scalar x,ochan;
      if null terminalp() then rederr "Edit must be from a terminal";
      ochan := wrs nil;
      if n eq 'fn then x := reversip crbuf!*
       else if null n
        then if null crbuflis!*
               then <<statcounter := statcounter-1;
                      rederr "No previous entry">>
              else x := cdar crbuflis!*
       else if (x := assoc(car n,crbuflis!*))
        then x := cedit0(cdr x,car n)
       else <<statcounter := statcounter-1;
              rederr list("Entry",car n,"not found")>>;
      crbuf!* := nil;
      % Following line changed for CL version.
      x := foreach y in x conc explodec y;
      terpri();
      editp x;
      terpri();
      x := cedit1 x;
      wrs ochan;
      if x eq 'failed then nil
      % Following changed for CL version.
      else
        crbuf1!* := compress(append('(!") ,
                                       append(x, '(!" ))));
   end;

flag('(cedit),'lose);

% FLOOR is already defined.

flag('(floor),'lose);

% CL doesn't like '(function ...) in defautoload (module entry).

remflag('(mkfunction),'lose);

smacro procedure mkfunction u; mkquote u;

flag('(mkfunction),'lose);

% This function is used in Rlisp '88.

symbolic procedure igetv(u,v); getv(u,v);

endmodule;

end;
