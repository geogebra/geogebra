module fourplus;

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


%% ARITHMETIC

%% Addition of Fourier expressionsis really a merge operation

symbolic procedure fs!:plus!:(x,y);
 %% Top level addition of two fourier series
    if fs!:zerop!: y then x
    else if fs!:zerop!: x then y
    else get('fourier,'tag)
            . fs!:plus(copy!-tree cdr x, copy!-tree cdr y);

% I cannot rely on the CAMAL selective copy, so I take the coward's way
% out.
symbolic procedure copy!-tree x;
   if null x then nil
   else begin scalar ans;
      ans := mkvect 3;
      fs!:set!-coeff(ans,fs!:coeff x);
      fs!:set!-fn(ans,fs!:fn x);
      fs!:set!-angle(ans,fs!:angle x);
      fs!:set!-next(ans, copy!-tree fs!:next x);
      return ans
   end;

symbolic procedure fs!:plus(x, y);
  %% The real addition.  x is a new tree to which y must be merged.
  if null y then x
  else if null x then y
  else if fs!:fn x = fs!:fn y
     and angles!-equal(fs!:angle x, fs!:angle y) then
        begin scalar coef;
            coef := addsq(fs!:coeff x, fs!:coeff y);
        % Really I should deal with the zero case here
            if null car coef
              then return fs!:plus(fs!:next x, fs!:next y);
            fs!:set!-coeff(x, coef);
            fs!:set!-next(x, fs!:plus(fs!:next x, fs!:next y));
            return x
        end
    else if fs!:angle!-order(x, y) then <<
          fs!:set!-next(x, fs!:plus(fs!:next x, y));
          x >>
    else <<
          fs!:set!-next(y, fs!:plus(fs!:next y,x));
          y >>;

symbolic procedure angles!-equal(x, y);
% Are all angles the same?
begin scalar i;
    i := 0;
top:
    if not(getv!.unsafe(x,i)=getv!.unsafe(y,i)) then return nil;
    i := i+1;
    if (i<8) then go to top;
    return t;
end;

symbolic procedure fs!:angle!-order(x, y);
% Ordering function for angle expressions, also taking account of angle.
begin scalar ans, i, xx, yy;
    i := 0;
    xx := fs!:angle x;
    yy := fs!:angle y;
top:
    ans := (getv!.unsafe(xx,i)-getv!.unsafe(yy,i));
    if not(ans = 0) then return ans>0;
    i := i+1;
    if (i<8) then go to top;
    return
      if fs!:fn x = fs!:fn y then nil
       else if fs!:fn x = 'sin then nil else t;
end;

endmodule;

end;
