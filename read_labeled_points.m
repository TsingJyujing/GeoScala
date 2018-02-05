function [x,y,c] = read_labeled_points(filename)
delimiter = ',';
formatSpec = '%f%f%f%[^\n\r]';
fileID = fopen(filename,'r');
dataArray = textscan(fileID, formatSpec, 'Delimiter', delimiter, 'EmptyValue' ,NaN, 'ReturnOnError', false);
fclose(fileID);
c = dataArray{:, 1};
x = dataArray{:, 2};
y = dataArray{:, 3};
end