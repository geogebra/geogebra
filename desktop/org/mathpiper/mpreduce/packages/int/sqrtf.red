module sqrtf;   % Square root of standard forms.

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


fluid '(!*noextend zlist);

exports nrootn,domainp,minusf;  % minusdfp,sqrtdf

imports contentsmv,gcdf,interr,!*multf,partialdiff,printdf,quotf,
   simpsqrt2,vp2;

% symbolic procedure minusdfp a;
%   % Test sign of leading coedd of d.f.
%     if null a then interr "Minusdfp 0 illegal"
%     else minusf numr lc a;

% symbolic procedure sqrtdf l;
%    % Takes square root of distributive form. "Failed" usually means
%    % that the square root is not among already existing objects.
%     if null l then nil
%     else begin scalar c;
%         if lpow l=vp2 zlist then go to ok;
%         c:=sqrtsq df2q l;
%         if numr c eq 'failed
%           then return 'failed;
%         if denr c eq 'failed
%           then return 'failed;
%         return for each u in f2df numr c
%                  collect (car u).!*multsq(cdr u,1 ./ denr c);
%     ok: c:=sqrtsq lc l;
%         if  numr c eq 'failed or
%             denr c eq 'failed
%           then return 'failed
%           else return (lpow l .* c) .+nil
%     end;

% symbolic procedure sqrtsq a;
%     sqrtf numr a ./ sqrtf denr a;

symbolic procedure sqrtf p;
    begin       scalar ip,qp;
        if null p then return nil;
        ip:=sqrtf1 p;
        qp:=cdr ip;
        ip:=car ip; %respectable and nasty parts of the sqrt.
        if qp=1 then return ip; %exact root found.
         if !*noextend then return 'failed;
            % We cannot add new square roots in this case, since it is
            % then impossible to determine if one square root depends
            % on another if new ones are being added all the time.
         if zlistp qp then return 'failed;
            % Liouville's theorem tells you that you never need to add
            % new algebraics depending on the variable of integration.
        qp:=simpsqrt2 qp;
        return !*multf(ip,qp)
    end;

symbolic procedure zlistp qp;
if atom qp then member(qp,zlist)
  else or(member(mvar qp,zlist),zlistp lc qp,zlistp red qp);

symbolic procedure sqrtf1 p;
  % Returns a . b with p=a**2*b.
    if domainp p
     then if fixp p then nrootn(p,2)
           else !*q2f simpsqrt list prepf p . 1
    else begin scalar co,pp,g,pg;
        co:=contentsmv(p,mvar p,nil); %contents of p.
        pp:=quotf(p,co); %primitive part.
        co:=sqrtf1(co); %process contents via recursion.
        g:=gcdf(pp,partialdiff(pp,mvar pp));
        pg:=quotf(pp,g);
        g:=gcdf(g,pg); %a repeated factor of pp.
        if g=1 then pg:=1 . pp
        else <<
            pg:= quotf(pp,!*multf(g,g)); %what is still left.
            pg:=sqrtf1(pg); %split that up.
            rplaca(pg,!*multf(car pg,g))>>;
                 %put in the thing found here.
        rplaca(pg, !*multf(car pg,car co));
        rplacd(pg, !*multf(cdr pg,cdr co));
        return pg
    end;

% NROOTN removed as in REDUCE base.

endmodule;

end;
