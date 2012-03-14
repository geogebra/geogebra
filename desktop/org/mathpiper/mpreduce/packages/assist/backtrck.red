module backtrck;

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


fluid '(g_skip_to_level);

symbolic procedure generate_next_choice(sc, partial_perm, canon);
  begin scalar
              next_perm, comparison, extensions;
       integer
              n_points,  len, next_img;
  n_points := upbve(car sc);
  g_skip_to_level := len := upbve(partial_perm) + 1;
  next_img := 1;
  sc_setbase(sc, partial_perm);
  repeat
    <<
    extensions := candidate_extensions(sc, partial_perm);
    if (member(next_img, extensions)) then
      <<
      next_perm := vectappend1(partial_perm, next_img);
      if acceptable(next_perm) then
        <<
        assign(next_perm);
        comparison := compare(next_perm, canon);
        if comparison = 0 then           % 0 = indifferent
          <<
          if len < n_points then
            canon := car generate_next_choice(sc, next_perm, canon)
        else if canon then
        process_new_automorphism(sc,
                           pe_mult(pe_inv(canon), next_perm));
          >>
        else if comparison = 1 then      % 1 = better
          if len < n_points then
            canon := car generate_next_choice(sc, next_perm, canon)
          else
            canon := copy(next_perm);
        deassign(next_perm)
        >>
      >>;
    next_img := next_img + 1
    >>
  until (next_img > n_points) or (len > g_skip_to_level);
  return canon . sc;
  end;

symbolic procedure candidate_extensions(sc, partial_perm);
  begin   scalar extensions;
 %        integer count;
    if null sc_stabdesc(sc, upbve(partial_perm) + 1) then
    extensions := for count := 1:upbve(car sc) collect count
    else
    extensions := venth(venth(cdr sc, upbve(partial_perm) +1), 5);
 % remove elts of partial_perm from extensions
    for count := 1: upbve(partial_perm) do
    extensions := delete(venth(partial_perm, count), extensions);
  return extensions;
  end;


symbolic procedure process_new_automorphism(sc, new_aut);
  begin scalar inv_new_aut, sd;
        integer count;
   inv_new_aut := pe_inv(new_aut);
%% update stab chain
   count := 0;
   repeat
    <<
    count := count + 1;
    sd := sc_stabdesc(sc, count);
    if null sd then
      sd := sd_create(upbve(car sc), venth(car sc, count));
    sd_addgen(sd, new_aut, inv_new_aut);
    putve(cdr sc, count, sd)
    >>
   until (pe_apply(new_aut, venth(car sc, count)) neq
                                          venth(car sc, count));
    g_skip_to_level := count;
  end;

symbolic procedure canon_order(n);
  begin scalar aut_sc;
  aut_sc := sc_create(n);
  return generate_next_choice(aut_sc, mkve(0), nil);
  end;

endmodule;

end;
