clear;clc;close all;
figure(1)
hold on

[px,py] = read_points('polygon.polygon.csv');
for i = 1:length(px)
    if i==1
        plot([px(end),px(1)],[py(end),py(1)],'b-')
    else
        plot([px(i-1),px(i)],[py(i-1),py(i)],'b-')
    end
end

[px,py] = read_points('polygon.hole.csv');
for i = 1:length(px)
    if i==1
        plot([px(end),px(1)],[py(end),py(1)],'g-')
    else
        plot([px(i-1),px(i)],[py(i-1),py(i)],'g-')
    end
end

[x,y,c] = read_labeled_points('polygon.test.data.csv');
plot(x(c==0),y(c==0),'r.')
plot(x(c~=0),y(c~=0),'b.')
hold off