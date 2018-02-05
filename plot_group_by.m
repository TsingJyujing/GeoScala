clear;clc;close all;
[x,y,c] = read_labeled_points('dbscan_result.csv');
close all
figure(1)
hold on;
cs = unique(c);
for i = 1:length(cs)
    index = c==cs(i);
    if sum(index)<100
        shape = 'o';
    else
        shape = '.';
    end
    plot(x(index),y(index),shape)
end
hold off;