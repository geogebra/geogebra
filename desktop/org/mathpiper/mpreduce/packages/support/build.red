% module build;  % Code to help with bootstrapping REDUCE from Lisp.


% Author: Anthony C. Hearn.
% Modified by ACN for the Sourceforge version

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


% The baroque syntax in this file is a consequence of the bootstrapping
% process.

global '(loaded!-packages!*);

% A few functions will appear again in remake.red, and xslrend.red but
% they are needed at this stage during the bootstrap build of REDUCE.
% So perhaps to avoid repetition I could find somewhere even earlier to
% include it, but for now there is repetition to should be kept in step.


% Since some of the early modules may have tabs in them, we must redefine
% seprp.

symbolic procedure seprp u;
    or(eq(u,'! ),eq(u,'!	),eq(u,!$eol!$));

symbolic procedure mkfil u;
   % Converts file descriptor U into valid system filename.
   if stringp u then u
    else if not idp u then typerr(u,"file name")
    else string!-downcase u;

symbolic procedure string!-downcase u;
   begin scalar z;
      if not stringp u then u := '!" . append(explode2 u,'(!"))
      else u := explode u;
% This has to be written in the bootstrap kernel of the RLISP language
% and so looks a little ugly.
   a: if null u then return compress reverse z;
      z := red!-char!-downcase car u . z;
      u := cdr u;
      go to a;
   end;

fluid '(charassoc!*);

symbolic procedure red!-char!-downcase u;
   begin scalar x;
      x := atsoc(u,charassoc!*);
      if x then return cdr x
      else return u;
   end;

charassoc!* :=
         '((!A . !a) (!B . !b) (!C . !c) (!D . !d) (!E . !e) (!F . !f)
           (!G . !g) (!H . !h) (!I . !i) (!J . !j) (!K . !k) (!L . !l)
           (!M . !m) (!N . !n) (!O . !o) (!P . !p) (!Q . !q) (!R . !r)
           (!S . !s) (!T . !t) (!U . !u) (!V . !v) (!W . !w) (!X . !x)
           (!Y . !y) (!Z . !z));

symbolic procedure concat(u,v);
   compress('!" . append(explode2 u,nconc(explode2 v,list '!")));

% End of fudges. Note that this file is only used while bootstrapping so the
% redundant or non-optimised versions here do not persist into the final image.

symbolic procedure module2!-to!-file(u,v);
   % Converts the module u in package directory v to a fully rooted file
   % name.
   if memq('vsl, lispsystem!*) then
     concat("../packages/",concat(mkfil v,
            concat("/",concat(mkfil u,".red"))))
   else
     concat("$reduce/packages/",concat(mkfil v,
            concat("/",concat(mkfil u,".red"))));

symbolic procedure inmodule(u,v);
   begin
      u := open(module2!-to!-file(u,v),'input);
      v := rds u;
      cursym!* := '!*semicol!*;
   a: if eq(cursym!*,'end) then return progn(rds v, close u);
      prin2 eval form xread nil;
      go to a;
   end;

symbolic procedure load!-package!-sources(u,v);
   begin scalar !*int,!*echo,w;
      inmodule(u,v);
      if (w := get(u,'package)) then w := cdr w;
   a: if w then progn(inmodule(car w,v), w := cdr w, go to a);
      loaded!-packages!* := u . loaded!-packages!*;
   end;

% endmodule;

end;
