module reform; % Reformulate expressions using C-constant substitution.

% Authors: Mary Ann Moore and Arthur C. Norman.

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


fluid '(!*trint cmap cval loglist ulist);

exports logstosq,substinulist;

imports prepsq,mksp,nth,multsq,addsq,domainp,invsq,plusdf;

symbolic procedure substinulist ulst;
   % Substitutes for the C-constants in the values of the U's given in
   % ULST. Result is a D.F.
   if null ulst then nil
    else begin scalar temp,lcu;
       lcu:=lc ulst;
       temp:=evaluateuconst numr lcu;
       if null numr temp then temp:=nil
        else temp:=((lpow ulst) .*
                   !*multsq(temp,!*invsq(denr lcu ./ 1))) .+ nil;
       return plusdf(temp,substinulist red ulst)
     end;

symbolic procedure evaluateuconst coefft;
% Substitutes for the C-constants into COEFFT (=S.F.). Result is S.Q.;
    if null coefft or domainp coefft then coefft ./ 1
    else begin scalar temp;
      if null(temp:=assoc(mvar coefft,cmap)) then
            temp:=(!*p2f lpow coefft) ./ 1
      else temp:=getv(cval,cdr temp);
      temp:=!*multsq(temp,evaluateuconst(lc coefft));
   % Next line had addsq previously
      return !*addsq(temp,evaluateuconst(red coefft))
    end;

symbolic procedure logstosq;
% Converts LOGLIST to sum of the log terms as a S.Q.;
   begin scalar lglst,logsq,i,temp;
      i:=1;
      lglst:=loglist;
      logsq:=nil ./ 1;
loop: if null lglst then return logsq;
      temp:=cddr car lglst;
%%      if !*trint
%%        then <<printc "SF arg for log etc ="; printc temp>>;
      if not (caar lglst='iden) then <<
          temp:=prepsq temp; %convert to prefix form.
          temp:=list(caar lglst,temp); %function name.
          temp:=((mksp(temp,1) .* 1) .+ nil) ./ 1 >>;
      temp:=!*multsq(temp,getv(cval,i));
      % Next line had addsq previously
      logsq:=!*addsq(temp,logsq);
      lglst:=cdr lglst;
      i:=i+1;
      go to loop
   end;

endmodule;

end;
