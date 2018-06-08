clear;clc;close all;
figure(1);
hold on;
[x,y] = read_points('dzy_raw.csv');
plot(x,y,'-');
[x,y] = read_points('dzy_compress_0.01.csv');
plot(x,y,'-');
[x,y] = read_points('dzy_compress_0.1.csv');
plot(x,y,'-');
[x,y] = read_points('dzy_compress_0.5.csv');
plot(x,y,'-');
[x,y] = read_points('dzy_compress_1.0.csv');
plot(x,y,'-');
legend('raw','0.01','0.1','0.5','1.0')
hold off;
