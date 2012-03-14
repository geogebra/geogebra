module df2q;   % Conversion from distributive to standard forms.

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


fluid '(indexlist zlist);

exports df2q;

imports addf,gcdf,mksp,!*multf,quotf;

Comment We assume that results already have reduced powers, so
        that no power substitution is necessary;

symbolic procedure df2q p;
   % Converts distributed form P to standard quotient.
   begin scalar n,d,w,x,y,z;
      if null p then return nil ./ 1;
      d:=denr lc p;
      w:=red p;
      while w do
       <<% Get denominator of answer as lcm of denoms in input.
         d := multf(d,quotf(denr lc w,gcdf(d,denr lc w)));
         w := red w>>;
      while p do begin
           w := sqrt2top lc p;
           x := multf(xl2f(lpow p,zlist,indexlist),multf(numr w,d));
           if null x then return (p := red p);  % Shouldn't occur.
           y := denr w;
           z := quotf(x,y);
           if null z
             then <<z := rationalizesq(x ./ y);
                    if denr z neq 1
                      then <<d := multf(denr z,d); n := multf(denr z,n)>>;
                    z := numr z>>;
           n := addf(n,z);
           p := red p
        end;
      return tidy!-powersq (n ./ d)
   end;

symbolic procedure tidy!-powersq x;
   % This tries to clean up by turning eg (a^(1/3))^3 into a.
   begin scalar expts,!*precise,!*keepsqrts;
      % I rebind *precise to nil so that things like sqrt(a)^2 simplify
      % to a rather than abs(a).
    !*keepsqrts := t;
    x := subs2q x;
    expts := find!-expts(numr x,find!-expts(denr x,nil));
    if null expts then return x; % Nothing to worry about here!
    x := subsq(x,for each v in expts collect
                    (car v . list('expt,cadr v,cddr v)));
    x := subsq(x,for each v in expts collect
                    (cadr v
                        . list('expt,car v,list('quotient,1,cddr v))));
    return x
  end;

symbolic procedure find!-expts(ff,l);
   begin scalar w;
      if domainp ff then return l;
      l := find!-expts(lc ff,find!-expts(red ff, l));
      ff := mvar ff;
      if eqcar(ff,'sqrt)
        then ff := list('expt, cadr ff,'(quotient 1 2))
       else if eqcar(ff,'expt) and eqcar(caddr ff,'quotient)
          and numberp caddr caddr ff
        then <<w := assoc(cadr ff,l);
               if null w
                 then <<w := cadr ff . gensym() . 1; l := w . l >>;
               rplacd(cdr w,lcm(cddr w,caddr caddr ff))>>;
      return l
   end;

symbolic procedure xl2f(l,z,il);
% L is an exponent list from a D.F., Z is the Z-list,
% IL is the list of indices.
% Value is L converted to standard form. ;
    if null z then 1
        else if car l=0 then xl2f(cdr l,cdr z,cdr il)
        else if not atom car l then
            begin       scalar temp;
                if caar l=0 then temp:= car il
                else temp:=list('plus,car il,caar l);
                temp:=mksp(list('expt,car z,temp),1);
                return !*multf(((temp .* 1) .+ nil),
                               xl2f(cdr l,cdr z,cdr il))
            end
%       else if minusp car l then                                     ;
%            multsq(invsq (((mksp(car z,-car l) .* 1) .+ nil)),       ;
%                  xl2f(cdr l,cdr z,cdr il))                          ;
        else !*multf((mksp(car z,car l) .* 1) .+ nil,
                    xl2f(cdr l,cdr z,cdr il));

endmodule;

end;
