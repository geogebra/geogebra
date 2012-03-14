module constre;

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


algebraic procedure constantRE(cap_R,leadcoeff,dffpointer,k,x);

% solve  constant RE
%
%   a(k+1) = cap_R(k) * a(k)
%
%  where leadcoeff is the leading coefficient of the RE
%  and DF is a table where DF(dffpointer+i) = df(f,x,i)

  begin scalar denr,fract,ii,m0,m1,c0,ck,S,c,df2,q,r2,lterm,nn,
        s0, leadcoeff2;
        denr := solve(leadcoeff,k);
        m0 := {};
        foreach xx in denr do
                if type_rational rhs xx then  m0 := ((rhs xx)+1) . m0;
        if not(m0 = {}) then m0 := max(m0) else m0 := 0;

        if symbolic !*traceFPS then
                 << write ">>> m0 = ",m0;
                    write "RE is constant";
                    write "RE: for all k >= ",m0,": a (k + 1) = "
                        ,cap_R * a(k);
                    write "leadcoeff := ",leadcoeff; >>;

        fract := {};
        foreach xx in denr do
                if type_fraction(rhs xx) then
                         fract := den(rhs xx) . fract;
        if not(fract = {}) then
                << q := first fract;
                   dff(dffpointer + 10) := sub(x=x^q,dff(dffpointer));
                   if symbolic !*traceFPS then <<
                        write "RE modified to nn= ",k/q;
                        write "=> f := ",dff(dffpointer + 10)>>;
                   S := constantRE(sub(k=k/q,cap_R),
                        sub(k=k/q,leadcoeff),dffpointer + 10,k,x);
                   return sub(x=x^(1/q),S); >>;

        if m0 < 0  then <<
                nn:= -m0 + 1;
                dff(dffpointer + 10) := df2 := x^nn * dff(dffpointer);
                if symbolic !*traceFPS then <<
                        write "working with ",x^nn,"*f";
                        write "=> f :=" ,df2 >>;
                S := constantRE(sub(k=k-nn,cap_R),sub(k=k-nn,leadcoeff),
                                        dffpointer + 10,k,x);
                return update_coeff(S,x,-nn);
        >>;

        if m0 = 0 then <<
                if symbolic !*traceFPS then write "PS does not exist";
                return failed>>;

        if m0 > 0  then <<
        m1 := {};
        foreach xx in denr do if type_rational rhs xx then
                         m1 := append(list (rhs xx +1),m1);
        m1 := min m1;
        if m1 < 0 then <<
                dff(dffpointer + 10) := df2 := x^(-m1)*dff(dffpointer);
        if symbolic !*traceFPS then <<
                        write "working with ",x^(-m1),"*f";
                        write "=> f :=" ,df2 >>;
                S := constantRE(sub(k=k+m1,cap_R),sub(k=k+m1,leadcoeff),
                                        dffpointer + 10,k,x);
                return update_coeff(S,x,m1)
                >>;
        >>;

        % { m1 >= 0 }

        S := 0; S0 := 0;
        for i:=0:m0 do
                <<
                if i > m1 then
                  dff(dffpointer + i) := df(dff(dffpointer + i-1),x);
                c0 := limit(dff(dffpointer + i),x,0);
                if (not numberp c0 and part(c0,0) = limit) then
                        <<  write "Could not find the limit of: "
                                ,dff(dffpointer + i),",",x,",",0;
                    rederr("problem using limit operator") >> else
                <<
                c0 := c0/factorial (i);
                S := S + c0*x^i ;
                if symbolic !*traceFPS then write " S = ",s;
                >> >>;
  return (S);

end;

endmodule;

end;

