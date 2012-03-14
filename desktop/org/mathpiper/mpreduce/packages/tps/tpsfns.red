module tpsfns;
% Expansion of elementary functions as power series using DOMAINVALCHK
% Example sin a where a is a power series will now be expanded
%
% Author: Alan Barnes, March 1989
% Currently only ps!:expt!: ever gets called and that only for
% integer exponents

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


fluid '(!*numval);

put('exp, '!:ps!:, 'ps!:exp!:);
put('log, '!:ps!:, 'ps!:log!:);

put('sin, '!:ps!:, 'ps!:sin!:);
put('cos, '!:ps!:, 'ps!:cos!:);
put('tan, '!:ps!:, 'ps!:tan!:);

put('asin, '!:ps!:, 'ps!:asin!:);
put('acos, '!:ps!:, 'ps!:acos!:);
put('atan, '!:ps!:, 'ps!:atan!:);

put('sinh, '!:ps!:, 'ps!:sinh!:);
put('cosh, '!:ps!:, 'ps!:cosh!:);
put('tanh, '!:ps!:, 'ps!:tanh!:);

put('asinh, '!:ps!:, 'ps!:asinh!:);
put('acosh, '!:ps!:, 'ps!:acosh!:);
put('atanh, '!:ps!:, 'ps!:atanh!:);

put('expt, '!:ps!:, 'ps!:expt!:);

% the above is grotty but necessary as unfortunately DOMAINVALCHK
% passes arglist of sin (rather than sin . arglist) to ps!:sin!: etc

symbolic procedure ps!:expt!:(base,exp);
% currently this only gets called when exp is an integer
% but it should work in general
 begin scalar depvar,about, knownps, ps!:level;
% begin scalar !*numval, depvar,about, knownps;
% NB binding of !*numval avoids infinite loop. Not necessary now -- AB?
   ps!:level := 0;
   about:= ps!:expansion!-point base;
   if null about then <<
       about:= ps!:expansion!-point exp;
       depvar:=ps!:depvar exp>>
   else depvar:=ps!:depvar base;
   return
    if null about then    % we have two constant power series
      << if ps!:p base then base := ps!:value base;
         if ps!:p exp then exp := ps!:value exp;
         about := simp!* list('expt, base, exp);
         make!-constantps (about, prepsqxx about, depvar) >>
    else
      ps!:expt!-crule(list('expt, base,exp),depvar,about)
 end;


symbolic procedure ps!:unary!:fn(fn, arg);
 begin scalar !*numval, knownps, ps!:level;
% NB binding of !*numval avoids infinite loop
   ps!:level := 0;
   return ps!:compile(list(fn, arg),
                      ps!:depvar arg,
                      ps!:expansion!-point arg)
 end;

symbolic procedure ps!:cos!: arg;
  ps!:unary!:fn('cos,arg);

symbolic procedure ps!:sin!: arg;
  ps!:unary!:fn('sin,arg);

symbolic procedure ps!:tan!: arg;
  ps!:unary!:fn('tan,arg);

symbolic procedure ps!:log!: arg;
  ps!:unary!:fn('log,arg);

symbolic procedure ps!:exp!: arg;
  ps!:unary!:fn('exp,arg);

symbolic procedure ps!:cosh!: arg;
  ps!:unary!:fn('cosh,arg);

symbolic procedure ps!:sinh!: arg;
  ps!:unary!:fn('sinh,arg);

symbolic procedure ps!:tanh!: arg;
  ps!:unary!:fn('tanh,arg);

symbolic procedure ps!:asin!: arg;
  ps!:unary!:fn('asin,arg);

symbolic procedure ps!:acos!: arg;
  ps!:unary!:fn('acos,arg);

symbolic procedure ps!:atan!: arg;
  ps!:unary!:fn('atan,arg);

symbolic procedure ps!:asinh!: arg;
  ps!:unary!:fn('asinh,arg);

symbolic procedure ps!:acosh!: arg;
  ps!:unary!:fn('acosh,arg);

symbolic procedure ps!:atanh!: arg;
  ps!:unary!:fn('atanh,arg);

endmodule;

end;
