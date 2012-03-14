I_setting(x,y,z);

torder revgradlex;

u := I(x*z-y**2, x**3-y*z);
y member I(x,y^2);
x member I(x,y^2);
I(x,y^2) subset I(x,y);         % yes
I(x,y) subset I(x,y^2);         % no

% examples taken from Cox, Little, O'Shea: "Ideals, Varieties and Algorithms"




q1 := u .: I(x);                        % quotient ideal

q2 := u .+ I(x^2 * y - z^2);            % sum ideal

if q1 .= q2 then write "same ideal";    % test equality

intersection(u,I(y));                   % ideal intersection

u .: I(y);

u .: I(x,y);

%-----------------------------------------------------

u1 := I(x,y^2);
u1u1:= u1 .* u1;                       % square ideal
u0 :=I(x,y);

% test equality/inclusion for u1,u1u1,u0

u1 .= u1u1;     % no

u1 subset u1u1; % no

u1u1 subset u1; % yes

u1 .= u0;       % no

u1 subset u0;   % yes

intersection (I(x) , I(x^2,x*y,y^2)) .= intersection(I(x) , I(x^2,y)); 

end;
