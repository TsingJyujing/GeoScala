clear;clc;close all;
syms beta theta theta_max
alpha = cos(theta)-beta*cos(theta_max);
solve_result = solve(1 - (alpha*alpha + beta*beta + 2*alpha*beta*cos(theta_max)), beta);
solve_result
pretty(solve_result)

%{
solve_result =

  ((cos(theta) - 1)*(cos(theta) + 1)*(cos(theta_max) - 1)*(cos(theta_max) + 1))^(1/2)/(cos(theta_max)^2 - 1)
 -((cos(theta) - 1)*(cos(theta) + 1)*(cos(theta_max) - 1)*(cos(theta_max) + 1))^(1/2)/(cos(theta_max)^2 - 1)

/  sqrt((cos(theta) - 1) (cos(theta) + 1) (cos(theta_max) - 1) (cos(theta_max) + 1))  \
|  ---------------------------------------------------------------------------------  |
|                                               2                                     |
|                                 cos(theta_max)  - 1                                 |
|                                                                                     |
|   sqrt((cos(theta) - 1) (cos(theta) + 1) (cos(theta_max) - 1) (cos(theta_max) + 1)) |
| - --------------------------------------------------------------------------------- |
|                                                2                                    |
\                                  cos(theta_max)  - 1                                /
%}