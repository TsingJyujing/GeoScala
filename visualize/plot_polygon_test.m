clear;clc;close all;
figure(1)
hold on
[px,py] = read_points('polygon.csv');
for i = 1:length(px)
    if i==1
        plot([px(end),px(1)],[py(end),py(1)],'b-')
    else
        plot([px(i-1),px(i)],[py(i-1),py(i)],'b-')
    end
end
[x,y,c] = read_labeled_points('test_polygon.csv');
plot(x(c==0),y(c==0),'r.')
plot(x(c~=0),y(c~=0),'b.')
hold off