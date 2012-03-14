module symint;  % Improved simplification of symbolic integrals

% Author: Francis J. Wright

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


% An extension of simpint1 in module driver (by Mary Ann Moore, Arthur
% C. Norman and John P. Fitch) to provide better simplification of
% integrals of symbolic derivatives and integrals.  (Originally
% motivated by the needs of the CRACK package.)

% Change Log:

%  7/1/98: Partial integration for integrals of integrals
% 10/1/98: Extended partial integration for integrals of integrals
% 11/1/98: Commutation of integrals
% 21/2/98: df(y,x,2) etc. handling corrected

fluid '(!*failhard !*IntDfFound);

switch CommuteInt;                      % off by default (for now)
deflist('((CommuteInt ((t (rmsubs))))), 'simpfg);

% If the switch CommuteInt is turned on then the top-level integration
% in a symbolic integral is commuted into the integrand to try to
% simplify it, and if that fails and the result is a symbolic multiple
% integral then it is left in canonical nesting order (as is already
% done automatically for multiple derivatives).  However, an
% integrable nested derivative is integrated regardless of this
% switch.

switch PartialInt, PartialIntDf, PartialIntInt; % off by default
deflist('((PartialInt ((t   (on  '(PartialIntDf PartialIntInt)))
                       (nil (off '(PartialIntDf PartialIntInt)))))
          (PartialIntDf ((t (rmsubs))))
          (PartialIntInt ((t (rmsubs))))), 'simpfg);

% If the switch PartialIntDf is turned on then integration by parts is
% performed if the result simplifies in the sense that it integrates a
% symbolic derivative and does not introduce new symbolic derivatives.
% However, because the initial integral contains an unevaluated
% derivative then the result must still contain an unevaluated
% integral.

% If the switch PartialIntInt is turned on then integration by parts
% is performed if the result simplifies in the sense that it removes a
% symbolic integral from the integrand and does not introduce new
% symbolic integrals.  However, because the initial integral contains
% an unevaluated integral then the result must still contain an
% unevaluated integral.

% The switch PartialInt is just a convenience to turn both the above
% switches on or off together.

switch XPartialInt, XPartialIntDf, XPartialIntInt; % off by default
deflist('((XPartialInt ((t   (on  '(XPartialIntDf XPartialIntInt)))
                       (nil (off '(XPartialIntDf XPartialIntInt)))))
          (XPartialIntDf ((t (rmsubs))))
          (XPartialIntInt ((t (rmsubs))))), 'simpfg);

% These switches control extended partial integration of integrals of
% the form int( int(u(x,z),z) * v(x), x ), which is experimental,
% somewhat heuristic and may be slow.

symbolic procedure symint u;
   % u has the form (int y x).
   % At this point linearity has been applied.
   begin scalar v, y, x;
      y := cadr u;  x := caddr u;
      % Check for a directly integrable derivative:
      if (v := NestedIntDf(y,x,nil)) then return mksq(v,1);
      if !*failhard then rerror(int,4,"FAILHARD switch set");
      if (!*PartialIntDf or !*PartialIntInt) and
         % Integrate by parts if the result simplifies:
         % DO WE NEED TO CALL SIMPINT1 RECURSIVELY ON THE RESULT?
         (v := PartialInt(y,x)) then return mksq(v,1);
      if (!*XPartialIntDf or !*XPartialIntInt) and
         % EXPERIMENTAL!  Try extended partial integration:
         (v := XPartialInt(y,x)) then return mksq(v,1);
      return mksq(u,1)
   end;

%% symbolic procedure NestedIntDf(y, x);
%%    %% int( ... df(f,A,x,B) ..., x) -> ... df(f,A,B) ...
%%    %% Find a df(f,A,x,B) among possibly nested int's and df's within
%%    %% the integrand y in int(y,x), and return the whole structure y
%%    %% but with the derivative integrated; otherwise return nil.
%%    %% [A,B are arbitrary sequences of kernels.]
%%    not atom y and
%%    begin scalar car_y, nested;
%%       return
%%          if (car_y := car y) eq 'df and memq(x, cddr y) then
%%             %% int( df(f, A, x, B), x ) -> df(f, A, B)
%%             'df . cadr y . delete(x, cddr y)
%%                %% use delete for portability!
%%                %% deleq is defined in CSL, delq in PSL -- oops!
%%          else if memq(car_y, '(df int)) and
%%             (nested := NestedIntDf(cadr y, x)) then
%%             %% int( df(int(df(f, A, x, B), c), C), x ) ->
%%             %%      df(int(df(f, A, B), c), C)
%%             %% int( int(df(f, A, x, B), c), x ) ->
%%             %%      int(df(f, A, B), c)
%%             car_y . nested . cddr y
%%    end;

symbolic procedure NestedIntDf(y, x, !*recursive);
   %% In order to simplify a symbolic integral int(y,x), commute the
   %% integral through integrals and derivatives in the integrand to
   %% try to find an integrable integrand.  Return the result if
   %% successful; otherwise return nil.  [A,B are arbitrary sequences
   %% of kernels or "kernels followed by integers".]  If the integral
   %% does not simplify, optionally commute multiple integrals into
   %% canonical nesting order, as is done in the standard
   %% differentiator code for multiple derivatives.  !*recursive is
   %% nil in the top-level call, t in recursive calls.  [NB: The
   %% top-level call of this procedure makes redundant the let rule in
   %% the standard integrator code to integrate derivatives.]
   not atom y and
   begin scalar fn, nested;
      return
         if (fn := car y) eq 'df then   % integrating a derivative
            if (nested := IntDf(y, x)) then nested
               %% int( ... df(f, A, x, B) ... , x ) -> df(f, A, B)
            else if (nested := NestedIntDf(cadr y, x, t)) then
               %% recursing into the integrand
               fn . nested . cddr y
            else nil
         else if !*failhard then nil    % give up!
         else if fn eq 'int then        % integrating an integral
            if eq(x, caddr y) then
               %% int( ... int(f, x) ... , x ) -> stop
               nil
            else if (nested := NestedIntDf(cadr y, x, t)) then
               %% recursing into the integrand
               fn . nested . cddr y
            else if !*CommuteInt and ordp(x, caddr y) then
               %% Commute integrals into canonical nesting order:
               %% int( ... int(f, b) ... , a ) ->
               %%    int( ... int(f, a) ... , b )
               %% Successive calls of the integrator by the simplifier
               %% to integrate nested integrals causes this code to
               %% sort the integrands into canonical order.
               {'int, {'int, cadr y, x}, caddr y}
            else nil
         else if !*recursive and !*CommuteInt and
            %% y is not an integral or a derivative -- try to
            %% integrate it unless at top level:
            not eqcar(nested := reval {'int,y,x}, 'int) then nested
   end;

symbolic procedure IntDf(y, x);
   % y = df(f, u, nu, v, nv, ...) where nu, nv, ... optional
   % if x = u, v, ... then return int(y, x)
   begin scalar !*IntDfFound;
      x := IntDfVars(cddr y, x);
      if !*IntDfFound then return
         if x then 'df . cadr y . x else cadr y
   end;

symbolic procedure IntDfVars(y, x);
   if y then
      if car y eq x then
      begin scalar n;
         !*IntDfFound := t;
         return
            if (y := cdr y) and fixp(n := car y) then <<
               y := cdr y;
               if n > 2 then y := (n-1) . y;
               x . y
            >> else y
      end
      else car y . IntDfVars(cdr y, x);


symbolic procedure PartialInt(y, x);
   %% Integrate by parts if the resulting integral simplifies;
   %% otherwise return nil.  Split integrand into a derivative or
   %% integral and a second factor and call the appropriate procedure.
   %% Try all possible allowed partial integrations in turn.
   not atom y and
   begin scalar denlist, faclist, facs, df_or_int, result;
      % Process any quotient:
      if car y eq 'quotient then <<
         denlist := cddr y;
         % y := numerator:
         if atom(y := cadr y) then return % no derivative or integral
      >>;
      % y := list of factors:
      if car y eq 'times then y := cdr y
      else if denlist or !*PartialIntInt then y := y . nil
         % Can do double integral int(int(u(x),x),x) as a special case
      else return;
      faclist := y;
      % Loop through all integrable derivatives or differentiable
      % integrals among the factors:
   continue:
      while faclist and ( atom(df_or_int := car faclist) or
         not (memq(car df_or_int, '(df int)) and
            memq(x, cddr df_or_int)) ) do
               faclist := cdr faclist;
      % Finally, break the loop if there is no integrable derivative
      % or differentiable integral:
      if null faclist then return;
      facs := delete(df_or_int, y);     % list of factors
      facs := if null facs then 1
         else if cdr facs then 'times . facs else car facs;
      if denlist then facs := 'quotient . facs . denlist;
      if car df_or_int eq 'df then
         (if !*PartialIntDf and
            (result := PartialIntDf(facs, df_or_int, x))
         then return result)
      else
         (if !*PartialIntInt and
            (result := PartialIntInt(df_or_int, facs, x))
         then return result);
      % Continue the loop through the factors in faclist:
      faclist := cdr faclist;
      goto continue
   end;

symbolic procedure PartialIntDf(u, df_v, x);
   %% int(u(x)*df(v(x),x), x) -> u(x)*v(x) - int(df(u(x),x)*v(x), x)
   %% Integrate by parts if the resulting integral simplifies [to
   %% avoid infinite loops], which means that df(u(x),x) may not
   %% contain any unevaluated derivatives; otherwise return nil.
   begin scalar v;
      v := IntDf(df_v, x);
      % Check that df(u(x),x) simplifies:
      if smemq('df, df_v := reval {'df,u,x}) then return;
      return reval {'difference,
         {'times,u,v}, {'int, {'times, df_v, v}, x}}
   end;

symbolic procedure PartialIntInt(int_u, v, x);
   %% int(int(u(x),x) * v(x), x) ->
   %%    int(u(x),x) * int(v(x),x) - int( u(x) * int(v(x),x), x )
   %% Integrate by parts if the resulting integral simplifies [to
   %% avoid infinite loops], which means that int(v(x),x) may not
   %% remain an unevaluated integral; otherwise return nil.
   begin scalar u;
      u := cadr int_u;                  % kernel being integrated
      % Check that int(v(x),x) simplifies:
      if eqcar(v := reval {'int,v,x}, 'int) then return;
      return reval {'difference,
         {'times,int_u,v}, {'int, {'times,u,v}, x}}
   end;


symbolic procedure XPartialInt(y, x);
   %% Extended partial integration.  This code is somewhat heuristic
   %% and may be slow.  The problem is to try to simplify
   %%    int( int(u(x,z),z) * v(x), x ).
   %% Integrate by parts if the resulting integral simplifies;
   %% otherwise return nil.  Split integrand into an integral NOT wrt
   %% x and a second factor and call the appropriate procedure.  Try
   %% all possible allowed partial integrations in turn.
   not atom y and
   begin scalar denlist, faclist, facs, int, result;
      % Process any quotient:
      if car y eq 'quotient then <<
         denlist := cddr y;
         % y := numerator:
         if atom(y := cadr y) then return % no derivative or integral
      >>;
      % y := list of factors:
      if car y eq 'times then y := cdr y
      else if denlist then y := y . nil
      else return;
      faclist := y;
      % Loop through all integrals among the factors:
   continue:
      while faclist and ( not eqcar(int := car faclist, 'int) or
         eq(x, caddr int) ) do
            faclist := cdr faclist;
      % Finally, break the loop if there is no appropriate integral:
      if null faclist then return;
      facs := delete(int, y);           % list of factors
      facs := if null facs then 1
         else if cdr facs then 'times . facs else car facs;
      if denlist then facs := 'quotient . facs . denlist;
      if facs = 1 then return;          % ?????
      if (result :=
         (!*XPartialIntDf and XPartialIntDf(int, facs, x)) or
            (!*XPartialIntInt and XPartialIntInt(int, facs, x)))
      then return result;
      % Continue the loop through the factors in faclist:
      faclist := cdr faclist;
      goto continue
   end;

symbolic procedure XPartialIntDf(int_u, v, x);
   %% int(int(u(x,z),z)*v(x), x) ->
   %%    int(int(u(x,z),x),z) * v(x) -
   %%       int( int(int(u(x,z),x),z) * df(v(x),x), x )
   %% provided df(v(x),z) and int(u(x,z),x) simplify.
   begin scalar df_v, z;
      % Check that df(v(x),x) simplifies:
      if smemq('df, df_v := reval {'df, v, x}) then return;
      z := caddr int_u;
      int_u := reval {'int, cadr int_u, x}; % int(u(x,z),x)
      % Check that int(u(x,z),x) simplifies:
      if eqcar(int_u, 'int) then return;
      int_u := reval {'int, int_u, z};  % int(int(u(x,z),x),z)
      return reval {'difference,
         {'times,int_u,v}, {'int, {'times, int_u, df_v}, x}}
   end;

symbolic procedure XPartialIntInt(int_u, v, x);
   %% int(int(u(x,z),z) * v(x), x) ->
   %%    int(u(x,z),z) * int(v(x),x) -
   %%       int( int(df(u(x,z),x),z) * int(v(x),x), x )
   %% provided int(v(x),x) and int(df(u(x,z),x),z) simplify.
   %% If x = z then this reduces to PartialIntInt.
   begin scalar u;
      u := cadr int_u;                  % kernel being integrated
      % Check that int(v(x),x) simplifies:
      if eqcar(v := reval {'int, v, x}, 'int) then return;
      % Check that df(u(x),x) simplifies:
      if smemq('df, u := reval {'df, u, x}) then return;
      % Check that int(df(u(x,z),x),z) simplifies:
      if eqcar(u := reval {'int, u, caddr int_u}, 'int) then return;
      return reval {'difference,
         {'times,int_u,v}, {'int, {'times,u,v}, x}}
   end;

endmodule;

end;
