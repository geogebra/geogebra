 %---------------------------------------------------------------------------
%
% these programs are written to sort the Taylor problem out; namely, the
% problem of extracting the leading exponent together with its sign from
% a taylor expression.
%
%----------------------------------------------------------------------------

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


module expon;
load_package taylor;
algebraic;
expr procedure mrv_split(exp);
begin scalar temp,current,ans;
off mcd;
ans:={};
   if lisp(atom exp) then temp:={exp} else
   temp:=for k:=1:arglength(exp) collect part(exp,k); write "temp is ", temp;
   for k:=1:arglength(temp) do
   << current:=part(temp,k);
      if (lisp atom current) then
      << if(not freeof(current,ww)) then
               ans:=append(ans,{current}) else nil;
      >>
  else <<
    if(not freeof(current,expt)) then
          ans:=append(ans,{current}) else nil; >>;
   >>;

return ans;
end;


%load_package taylor;

expr procedure collect_power(li);
begin scalar ans;
on rational; on exp;
   ans:=for k:=1:length(li) collect lpower(part(li,k),ww);
return ans;
end;

%collect_power(mrv_split(1/2*(ww+x*ww^-1+2)));

expr procedure conv(li); % converts our list of powers to exponents
begin scalar ans,current;
ans:={};
  for k:=1:length(li) do
  <<
    current:=part(li,k); %write "current is ", current;
    if(lisp atom current) then << if(not freeof(current,ww))
             then ans:=append(ans,{1}) else nil; >>
    else <<
           if(part(current,0)=expt) then
           ans:=append(ans,{part(current,2)}) else nil;
          >>;
    >>;
return ans;
end;

%collect_power(mrv_split(1/2*(ww+x*ww^-1+2)));

%conv(ws);

%load_package assist;

expr procedure find_expt(exp);
begin scalar spli, coll, con, ans,ans2;
 %spli:=mrv_split(exp); %write "split is ", spli;
 coll:=collect_power(spli); write "collect is "; coll;
 con:=conv(coll); write "con is ", con;
 ans:=sortnumlist(con); write "ans is ", ans;
 ans2:=part(ans,1);
return ans2;
end;

% we get something like
% (expt(ww -1)
%--------------------------------------------------------------------------
symbolic procedure find(u);
begin
off mcd; off factor; off exp;
if(atom u) then
<<
 if(freeof(u,'ww)) then
        << if(numberp(u)) then return list('number,u) else
              <<
                if(u='e) then return list('number,'e) else return
                list('x_exp,u)
              >>;
         >>
 else return list('expt,'ww,1)
>>
else <<
if(car u='expt) then return list('expt, cadr u, caddr u)
else <<
           if(car u='plus) then
           <<
              if(atom cadr u and atom caddr u) then
              <<
                 if(length(cdr u)>2) then <<
                 if((cadr u='ww) and freeof(caddr u,'ww)) then
                 return
                 append({'expt,cadr u,1},find(append({'plus},cddr u))) else
                 return append(find(cadr u), append({'plus},cddr u))
                                           >>
                  else
                  <<   if(caddr u='ww) and freeof(cadr u,'ww) then
                             return list('x_exp,cadr u,'expt,'ww,1)
                       else <<
                        if(cadr u='ww and freeof(caddr u,'ww)) then
                return append({'expt,cadr u,1},find caddr u)
                      else return append(find(cadr u),find(caddr u))
                             >>
                   >>
                >>
            else <<
                if(atom cadr u and pairp caddr u) then
                   <<
                 if(length(cdr u)>2) then <<
                 if(cadr u='ww) then return
                   append({'expt,'ww,1},find(append({'plus},cddr u)))
                    else return
                append(find(cadr u),find(append({'plus},cddr u)))
                                          >>
                    else return append(find(cadr u),find(caddr u))
                     >>
                else <<
                 if(pairp cadr u and pairp caddr u) then
                 <<
                   if(length(cdr u)>2) then % plus has more than 2 args
                return append(find(cadr u),find(append({'plus},cddr u)))
                 else return
                  append(find(cadr u),find(caddr u))
                  >>
                       else <<
                  if(pairp cadr u and atom caddr u) then
                    <<
                     if(length(cdr u)>2) then %plus has more than two args
                      <<
                           if(caddr u='ww) then
                           <<
                             return find(cadr u).list('expt,'ww,1).
                             find(append({'plus},cddr u))
                            >>
                                 else <<
                                        if(numberp(caddr u)) then return
 find(cadr u).(list('number,caddr u).find(append({'plus},cdr u)))
      else nil
                                      >>

                         >>    else return
                               append(find(cadr u),find(caddr u))
                    >>
                    else return append(find(cadr u),{caddr u})
                                  >> % else nil % unneccesary ?
                               >>
                        >>
                    >>
          else <<
      if(car u='lminus) then
          <<
             if(numberp cadr u) then return list('number,u)
              else return find(cadr u)
           >>
      else <<
      if(car u='quotient) then <<
          if(numberp(cadr u) and numberp(caddr u)) then
                return list('number,cadr u, caddr u) else
                 return append(find(cadr u), find(caddr u))
                                >>
              else <<
                     if(car u='minus) then <<
                     if(atom cdr u) then return find(cadr u) else
                                     <<
                     if(cadr u='expt and caddr u='ww) then
        return append(append({'minus},find(cadr u)),find(caddr u))
          else return append({'minus},find(cadr u))
                                     >>
                                             >>

      else <<
      if(car u='times) then
              <<
                if(atom cadr u and atom caddr u) then
                 <<
                 if(not freeof(cadr u,'ww)) then return
                    list('expt,cadr u,1) else
                       << if(not freeof(caddr u,'ww)) then
                          return list(nil,caddr u) else nil
                        >>
                     >>
                  else <<
                          if(atom cadr u and pairp caddr u) then
                     <<
                        if(not freeof(cadr u,'ww)) then return
                        list('expt,cadr u,1) else return find(caddr u)
                             >>
                        else
                             << if(pairp cadr u and pairp caddr u) then
                                <<
                                if(length(cdr u))>2 then % times has +2 args
                                   return
                        append(find(cadr u),find(append({'times},cddr u)))
                        else return append(find(cadr u),find(caddr u))
                                 >>
                        else <<
                                if(pairp cadr u and atom caddr u) then
                                         <<
                               if(freeof(cadr u,'ww) and caddr u='ww) then
                                          return list('expt,'ww,1) else
                            return append(find(cadr u),find(caddr u))
                                          >> %else nil
                              >>

                              >>
                        >>
                 >>  %else return find(cdr u)
              >>
          >>
        >>
      >>
    >>
     >>% ;
end;

 algebraic;
algebraic procedure fin(u); lisp ('list.find(u));
%----------------------------------------------------------------------------
% input to this procedure is a list
% output is a list, containing all the exponents, marked expt, and any
% numbers, flagged number
% e.g.  ww^-1+ww^-2 +2
% apply fin yields  {expt,ww,-1,expt,ww,-2,number,2}
% now apply find_numbers to this gives
% {expt,-1,expt,-2,number,2}
% the presence of the number means that there is a power of ww^0 present,
% ie a constant term. If any of the expoents in the list are less than zero,
% then we require the lowest one; if they are all positive, then zero is the
% answer to be returned

expr procedure find_numbers(li);
begin scalar current,expt_list,ans,l,finished; % first, second, third;
off mcd; on rational; on exp;
li:=li;
expt_list:={}; ans:={};

   for k:=1:(length(li)-1) do <<
       current:=part(li,k);
       if(current=expt) then
        <<
       if(part(li,k+1)=ww) then
       expt_list:=append(expt_list,{expt,part(li,k+2)});
        >>
        else <<
        if(current=number) then
        expt_list:=append(expt_list,{number,part(li,k+1)}) else
                  <<
                if(current=x_expt) then
                expt_list:=append(expt_list,{x_part,part(li,k+1)})
                      else   <<
         if((current=lisp mk!*sq simp 'minus) and part(li,k+1)=expt) then
 expt_list:=append(expt_list,append({lisp reval 'minus},{expt,part(li,k+3)}));
              %else nil;
                         >>;
                    >>;
              >>;
                                >>;
 % there is no x terms or numbers in the series exp
return expt_list;
end;

%----------------------------------------------------------------------------
expr procedure find_least_expt(exp);
begin scalar ans,find, current,result,expt_list,expt_list2,num_list, x_list;
off mcd; % this causes a lot of problems when on, and some problems when off,
         % so I don't think I can win!!!

expt_list:={};
num_list:={};   % initialisations
x_list:={};

find:=fin(exp);
ans:=find_numbers(find);
if(lisp !*tracelimit) then write "exponent list is ", ans;
%ans:=delete_all(-x,ans);

if(freeof(ans,number)) then % there were no numbers in series exp, only
                            % exponents
<<
for k:=1:(arglength(ans)-1) do
   <<
      if(part(ans,k)=lisp mk!*sq simp 'minus) then
                <<
          if(numberp(part(ans,k+2)) and part(ans,k+2)<0) then
          expt_list:=append(expt_list,{lisp 'minus,part(ans,k+2)});
           %else     <<
           %      if(freeof(part(ans,k+2),x)) then
           %     expt_list:=append(expt_list,{minus,part(ans,k+2)});
           %          >>;
                 >>
          else <<
   if((part(ans,k)=expt) and part(ans,k-1) neq (lisp mk!*sq simp 'minus)) then
                  <<
                   if(numberp(part(ans,k+1))) then
                  expt_list:=append(expt_list,{part(ans,k+1)});
                   >>
                  else nil; >>;
    >>;
%ans:=sortnumlist(ans);
%result:=part(ans,1);
%write "got up to here OK";
>>
 else <<
     for k:=1:arglength(ans)-1 do
        <<
          current:=part(ans,k);
       if((current=expt)) then % and part(ans,k+1)=lisp mk!*sq simp 'ww) then
              <<
        if(freeof(part(ans,k+1),x)) then
        expt_list:=append(expt_list,{part(ans,k+1)});
              >>
          else
            <<
          if(current=number) then num_list:=append(num_list,{part(ans,k+1)})
         else   <<if((current=lisp mk!*sq simp 'minus)
                  and numberp(part(ans,k+2)) and part(ans,k+2)<0) then
     expt_list:=append(expt_list,{lisp mk!*sq simp 'minus,part(ans,k+2)})                                else nil; >>;
             >>;
        >>;
      >>;
if(expt_list={}) then % we have only a number to deal with; ie power of
                      % ww in series is 0
return append({number},num_list) else
<< if(num_list={}) then
   <<
      if(freeof(expt_list,(lisp mk!*sq simp 'minus))) then
      <<
      expt_list:=sortnumlist(expt_list);
      expt_list:={expt,part(expt_list,1)};
      return expt_list;
      >> %
      else <<  % our list contains a power with a minus sign
               % want to find the least exponent, and then see if it is tagged
               % with a minus sign
      expt_list2:=expt_list;
      expt_list2:=delete_all((lisp mk!*sq simp 'minus),expt_list2);
                    % list is now without minus
      expt_list2:=sortnumlist(expt_list2);
      expt_list2:=part(expt_list2,1); % smallest element, this is our expt
      % now want to check the sign of w with this exponent

      l:=0; finished:=0;
      while (l<=(arglength(expt_list)-1) and finished=0) do
                               <<
      if((part(expt_list,l)=(lisp mk!*sq simp 'minus))
                     and (part(expt_list,l+1)=expt_list2))
         then
         <<
            finished:=1;
            expt_list2:=append({lisp 'minus},{expt_list2});
         >> else l:=l+1;
                         >>;
            return expt_list2;

                >>;
   >>
   else <<
          if(freeof(expt_list,lisp mk!*sq simp 'minus)) then
          <<
          expt_list:=sortnumlist(expt_list);
          expt_list:={part(expt_list,1)}; % smallest element in the list
          if(part(expt_list,1)<0) then %%%%%% this is the value of e0 returned
          return append({expt},{part(expt_list,1)}) else
            return append({number},num_list);
           >>
           else << % doesn't matter what is in the number list, as minus is
                   % present, meaning there is a negative exponent here
           expt_list:=delete_all(lisp mk!*sq simp 'minus, expt_list);
           expt_list:=sortnumlist(expt_list);
           return {lisp 'minus,part(expt_list,1)}; >>;
         >>;


>>;
end;
endmodule;

end;
