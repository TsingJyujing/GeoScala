clear;clc;close all;
figure(1)
subplot(2,1,1)
[px,py] = read_points('fetch_set.csv');
[x,y,c] = read_labeled_points('fetch_result.csv');
plot(px,py,'ro',x,y,'b.')
subplot(2,1,2)
plot(c)