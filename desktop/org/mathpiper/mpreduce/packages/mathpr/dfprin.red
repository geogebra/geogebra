module dfprin;     % Printing for derivatives plus other options
                   % suggested by the Twente group

% Author: A. C. Norman,  reconstructing ideas from Ben Hulshof,
%                      Pim van den Heuvel and Hans van Hulzen.

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


fluid '(!*fort !*nat !*noarg depl!* posn!*);

global '(!*dfprint farglist!*);

switch dfprint,noarg;

!*dfprint := nil;  % This is OFF by default because switching it on
                   % changes Reduce output in a way that might upset
                   % customers who have not found out about this switch.
                   % Perhaps in later releases of the code (and when the
                   % manual reflects this upgrade) it will be possible
                   % to make 'on dfprint' the default. Some sites may of
                   % course wish to arrange things otherwise...

!*noarg := t;      % If dfprint is enabled I am happy for noarg to be
                   % the expected option.

farglist!* := nil;

symbolic procedure dfprintfn u;
% Display derivatives - if suitable flags are set this uses
% subscripts to denote differentiation and loses the arguments to
% functions.
   if not !*nat or !*fort or not !*dfprint then 'failed
   else begin
      scalar w;
      w := layout!-formula('!!df!! . cdr u, 0, nil);
      if w = nil then return 'failed
      else putpline w
   end;

put('df, 'prifn, 'dfprintfn);

symbolic procedure dflayout u;
% This is a prifn for !!df!!, which is used internally when I am
% formatting derivatives, but which should only ever be seen in
% testing!-width!* mode and never at all by the end-user.
  (begin
      scalar op, args, w;
      w := car (u := cdr u);
      u := cdr u;
      % Noarg must be off if an integral occurs.
      if smember('int,w) then !*noarg := nil;
      % Treat plus, times, etc. differently from REDUCE operators.
      if !*noarg and (atom w or not get(car w, 'op)) then <<
         if atom w then <<
            op := w;
            args := assoc(op, depl!*);         % Implicit args
            if args then args := cdr args >>
         else <<
            op := car w;
            args := cdr w >>;                  % Explicit args
         remember!-args(op, args);
         w := op >>;
      maprin w;
      if u then <<
         u := layout!-formula('!!dfsub!! . u, 0, nil); % subscript line
         if null u then return 'failed;
         w := 1 + cddr u;
         putpline((update!-pline(0, -w, caar u) . cdar u) .
                  ((cadr u - w) . (cddr u - w))) >>
   end) where !*noarg = !*noarg;

symbolic procedure dfsublayout u;
% This is a prifn for !!dfsub!!, which is used internally when I am
% formatting derivatives, but which should only ever be seen in
% testing!-width!* mode and never at all by the end-user.
   begin
      scalar dfcase, firstflag, w;
% This is used as a prifn for both df and other things with
% subscripts - dfcase remembers which.
      dfcase := (car u = '!!dfsub!!);
      u := cdr u;
      firstflag := t;
      while u do <<
         w := car u;
         u := cdr u;
         if firstflag then firstflag := nil
          else prin2!* ",";
         if dfcase and u and numberp car u then <<
            prin2!* car u;
            u := cdr u >>;
         maprin w >>
   end;

put('!!df!!, 'prifn, 'dflayout);
put('!!dfsub!!, 'prifn, 'dfsublayout);

symbolic procedure remember!-args(op, args);
% This records information that can be displayed by the user
% issuing the command 'FARG'.
   begin
      scalar w;
      w := assoc(op, farglist!*);
      if null w then farglist!* := (op . args) . farglist!*
   end;

symbolic procedure farg;
% Implementation of FARG: display implicit argument data
   begin
      scalar newname;
      prin2!* "The operators have the following ";
      prin2!* "arguments or dependencies";
      terpri!* t;
      for each p in farglist!* do <<
         prin2!* car p;
         prin2!* "=";
% To avoid clever pieces of code getting rid of argument displays
% here I convert the name of the function into a string so that
% maprin produces a simple but complete display. Since I expect
% farg to be called but rarely this does not seem overexpensive
         newname := compress ('!" . append(explode2 car p, '(!")));
         maprin(newname . cdr p);
         terpri!* t >>
   end;

put('farg, 'stat, 'endstat);

symbolic procedure clfarg;
% Clear record of implicit args
   farglist!* := nil;

put('clfarg, 'stat, 'endstat);

symbolic procedure setprifn(u, fn);
% Establish (or clear) prifn property for a list of symbols
   for each n in u do
      if idp n then <<
% Things listed here will be declared operators now if they have
% not been so declared earlier.
         if not operatorp n then mkop n;
         if fn then put(n, 'prifn, fn)
          else remprop(n, 'prifn) >>
       else lprim list(n, "not an identifier");

symbolic procedure indexprin u;
% Print helper-function when integer-valued arguments are to be shown as
% subscripts
   if not !*nat or !*fort then 'failed
   else begin
      scalar w;
      w := layout!-formula('!!index!! . u, 0, nil);
      if w = nil then return 'failed
      else putpline w
   end;

symbolic procedure indexpower(u, n);
% Print helper-function when integer-valued arguments are to be shown as
% subscripts with exponent n
    begin
      scalar w;
      w := layout!-formula('!!indexpower!! . n . u, 0, nil);
      if w = nil then return 'failed
      else putpline w
   end;

symbolic procedure indexlayout u;
% This is a prifn for !!index!!, which is used internally when I am
% formatting index forms, but which should only ever be seen in
% testing!-width!* mode and never at all by the end-user.
   begin
      scalar w;
      w := car (u := cdr u);
      u := cdr u;
      maprin w;
      if u then <<
         u := layout!-formula('!!indexsub!! . u, 0, nil);
            % subscript line
         if null u then return 'failed;
         w := 1 + cddr u;
         putpline((update!-pline(0, -w, caar u) . cdar u) .
                  ((cadr u - w) . (cddr u - w))) >>
   end;

symbolic procedure indexpowerlayout u;
% Format a subscripted object raised to some power.
   begin
      scalar n, w, pos, maxpos;
      n := car (u := cdr u);  % The exponent
      w := car (u := cdr u);
      u := cdr u;
      maprin w;
      w := layout!-formula(n, 0, nil);
      pos := posn!*;
      putpline((update!-pline(0, 1 - cadr w, caar w) . cdar w) .
               (1 . (1 + cddr w - cadr w)));
      maxpos := posn!*;
      posn!* := pos;
      if u then <<
         u := layout!-formula('!!indexsub!! . u, 0,nil);
             % subscript line
         if null u then return 'failed;
         w := 1 + cddr u;
         putpline((update!-pline(0, -w, caar u) . cdar u) .
                  ((cadr u - w) . (cddr u - w))) >>;
      posn!* := max(posn!*, maxpos)
   end;

put('!!index!!, 'prifn, 'indexlayout);
put('!!indexpower!!, 'prifn, 'indexpowerlayout);
put('!!indexsub!!, 'prifn, 'dfsublayout);

symbolic procedure noargsprin u;
% Print helper-function when arguments for a function are to be hidden,
% but remembered for display via farg
   if not !*nat or !*fort then 'failed
    else <<
       remember!-args(car u, cdr u);
       maprin car u >>;

symbolic procedure doindex u;
% Establish some function names to have args treated as index values
   setprifn(u, 'indexprin);

symbolic procedure offindex u;
% Clear effect of doindex
   setprifn(u, nil);

symbolic procedure donoargs u;
% Identify functions where args are to be hidden
   setprifn(u, 'noargsprin);

symbolic procedure offnoargs u;
% Clear effect of donoargs
   setprifn(u, nil);

put('doindex, 'stat, 'rlis);
put('offindex, 'stat, 'rlis);
put('donoargs, 'stat, 'rlis);
put('offnoargs, 'stat, 'rlis);

endmodule;

end;
