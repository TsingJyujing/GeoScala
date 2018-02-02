filename = 'dbscan_result.csv';
delimiter = ',';
formatSpec = '%f%f%f%[^\n\r]';
fileID = fopen(filename,'r');
dataArray = textscan(fileID, formatSpec, 'Delimiter', delimiter, 'EmptyValue' ,NaN, 'ReturnOnError', false);
fclose(fileID);
c = dataArray{:, 1};
x = dataArray{:, 2};
y = dataArray{:, 3};
clearvars filename delimiter formatSpec fileID dataArray ans;
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