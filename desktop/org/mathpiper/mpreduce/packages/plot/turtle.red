module turtle;

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


% REDUCE implementation of Turtle Graphics

% Caroline Cotter, ZIB,Berlin, 1998.

% The main user commands for drawing pictures follow.

load_package gnuplot;

%USER SETTING FUNCTIONS
% The following allow the user to reset the position of the turtle whilst
% drawing a %picture - that is, these commands are to be used when the pen is
% to be lifted and set down at a new point, or to change the direction
% (heading) of the turtle. Since these do not actually draw anything,
% nothing is returned. However, in order %to allow a continuous drawing,
% the latest  position of the turtle must be returned
%(otherwise an error is incurred when trying to plot the points).

algebraic<<

procedure setheading(mu);             %Redirects the turtle
begin scalar w;
      w:=if mu=() then heading
          else ck(mu);                %remember ck sets between 0 and 360
      heading:=w;
      return {x_coord,y_coord}
end;


procedure setx(i);                  %Relocates the turtle in the x-direction
begin;
      x_coord:=i;
      return {x_coord,y_coord}
end;


procedure sety(j);                 %Relocates the turtle in the y-direction
begin;
      y_coord:=j;
      return {x_coord,y_coord}
end;


procedure setposition(q);             %Repositions the turtle (takes cartesian
begin;                                %coordinate as its argument)
      x_coord:=first q;
      y_coord:=second q;
      return {x_coord,y_coord}
end;


% Both turnleft and turnright redirect the turtle,
% dependent on its current direction

procedure turnleft(gamma);
begin;
      heading:=ck(heading + gamma);
      return {x_coord,y_coord}
end;


procedure turnright(delta);
begin;
      heading:=ck(heading - delta);
      return {x_coord,y_coord}
end;


procedure setheadingtowards(q);   %This takes a cartesian coordinate point as
                                  %its argument and redirects the turtle towards
begin scalar x,y,f;               %the point specified.
      x:=first q - x_coord;
      y:=second q - y_coord;
      f:=polar({x,y});
      heading:= ck(second f);
      return {x_coord,y_coord}
end;


%We also need to use forward/back without drawing a line
%(in addition to the other set commands). These have the effect
% of penup/pendown commands used in conjunction with forward/back.


procedure setforward(m);
begin scalar theta,s,u;
      theta:=heading;
      s:={m,theta};
      u:=cartesian(s);
      return setposition(u)
end;


procedure setback(n);
begin scalar theta,v,w;
      theta:=ck(heading+180);
      v:={n,theta};
      w:=cartesian(v);
      heading:=ck(theta-180);
      return setposition(w)
end;


%LINE-DRAWING FUNCTIONS
% The following functions are used when an actual line is to be drawn
% between two points on the graph. They each return a list of two points which,
% when used within the draw function, are joined together by a line.
% In addition, they reset the position of the turtle,
% but do not alter the direction stored.


procedure move(p);                %This takes a cartesian coordinate point as
                                  %its argument and draws a line towards the
begin scalar x,y,line;            %specified point
      x:=first p;
      y:=second p;
      line:={{x_coord,y_coord},{x,y}};
      x_coord:=x;
      y_coord:=y;
      return line
end;


procedure forward(c);           %The turtle is moved c units in the direction
begin scalar theta,s,u,fl;      %of the current heading setting
      theta:=heading;
      s:={c,theta};
      u:=cartesian(s);
     fl:= {{x_coord,y_coord},{x_coord+first(u),y_coord+second(u)}};
      x_coord:=x_coord+first(u);
      y_coord:=y_coord+second(u);
      return fl
end;


procedure back(d);               %The turtle is moved d units in the opposite
begin scalar theta,v,w,bl;       %direction to heading
      theta:=ck(heading+180);
      v:={d,theta};
      w:=cartesian(v);
     bl:= {{x_coord,y_coord},{x_coord+first(w),y_coord+second(w)}};
      x_coord:=x_coord+first(w);
      y_coord:=y_coord+second(w);
      heading:=ck(theta-180);
      return bl
end;



%PLOTTING PICTURES
% The next functions gather the commands input by the user and turn
% them into a graph output in a gnuplot window;


procedure draw(p);             %This is the function the user calls to draw
                               %the list of commands as a picture. It takes
begin scalar g;                %a list as its argument. The items in the list
                               %are expected to be any of the setting or plot-
                                %ting functions already outlined.
      g:=for each a in p collect a;
      plot g
end;


%SUMMARY
% The main variables:
%           x_coord
%           y_coord
%           heading
%are global, so it is advised that these are not altered directly.

% The following functions have been used in order to create the user commands, but they
%cannot be used directly in the draw function:
%           degree, rad,
%           polar, cartesian,
%           ck, try.
%(also the info command is designed to be used outside of a call to draw)

% The following functions are all user commands which can be placed in the list to be
%executed by the draw command:
%           clearscreen
%           home
%           setheading
%           setx
%           sety
%           setposition
%           turnleft
%           turnright
%           setheadingtowards
%           setforward
%           setback
%           move
%           forward
%           back

% The most important function is the draw function. It takes the list
% of commands and plots the points given.

%NOTE
% When using conditional statements under a call to draw, the final else
% statement must return a point or at least {x_coord,y_coord} if the picture
% is to be continued. Also for statements must include 'collect ' with a
% list of drawing commands. (The variable needs to begin counting from 0
% if it is to be joined onto the previous list %of drawing commands,
% e.g. for i:=0:10 collect{.......}).


% This program is designed to take the "Turtle Graphics" commands and implement
% them in REDUCE.
% Where possible, commands have remained the same, but pen-up & pen-down
% commands are not used. Instead the commands either set the variables
%(which are the x and y coordinates and heading variable), or draw a line.


%STARTING UP
% Many commands have either ordinary cartesian arguments or polar coordinate
% arguments, so we need to be able to transform them all into cartesian to
% plot on an x-y plane. This invovles the use of the pi function, so we
% need floating point accuracy:

%% on rounded;

% The following are the main variables of the program, but the user should not
%attempt to alter them directly. The functions
% 'setx,sety,setposition,setheading' are for that purpose.

x_coord:=0;
y_coord:=0;
heading:=0;

procedure clearscreen();           % This function resets the variables to the
begin;                             %original position.
      plotreset;                   %If the user has plotkeep on then this will
                                   %clear the current gnuplot window.
      x_coord:=0;
      y_coord:=0;
      heading:=0;
      return {x_coord,y_coord}
end;


procedure home();              % This also resets the variables and in general
begin;                         %is sufficient since gnuplot automatically rep-
      x_coord:=0;              %laces its windows each time it plots a new
      y_coord:=0;              %graph.
      heading:=0;
      return {x_coord,y_coord}
end;



%DEGREE-RADIAN TRANSFORMS
% These functions are called in the commands for drawing graphs.
% The user need not call on either degree or rad for drawing purposes.


procedure degree(theta);
begin scalar a;
      a:=theta*180/pi;
      return a
end;


procedure rad(mu);
begin scalar b;
      b:=mu*pi/180;
      return b
end;



%POLAR-CARTESIAN TRANSFORMS
% Again, there is no use for these functions in drawing, but they are needed to
% turn user inputs into x-y coordinate points.


procedure polar(p);
begin scalar x,y,r,theta;
      x:=first p;
      y:=second p;
      r:=(x^2+y^2)^(1/2);
      if x>0 then theta:=atan(y/x)
             else if x=0 then theta:=sign(y)*pi/2
                         else theta:=pi+atan(y/x);
      return(list(r,degree(theta)))
end;


procedure cartesian(p);
begin scalar r,theta,x,y;
      r:=first p;
      theta:=rad(second p);
      x:=r*cos(theta);
      y:=r*sin(theta);
      return(list(x,y))
end;


procedure ck(m);                %This is a useful function to keep the heading
                                %variable within the 0-360 range. It is used
begin;                          %within other functions for controlling the
                                %heading size - it is not necessary for the
                                %user to call ck.
      if numberp m then <<
                   if (m>=0 and m<360) then heading:=m;
                   if (m<0) then ck(360+m);
                   if (m>=360) then ck(m-360) >>
      else rederr "error:ck needs numeric argument";
      return heading
end;

>>;

endmodule;

end;

