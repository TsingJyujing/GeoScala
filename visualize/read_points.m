function [x,y] = read_points(filename)
delimiter = ',';
formatSpec = '%f%f%[^\n\r]';
fileID = fopen(filename,'r');
dataArray = textscan(fileID, formatSpec, 'Delimiter', delimiter, 'EmptyValue' ,NaN, 'ReturnOnError', false);
fclose(fileID);
x = dataArray{:, 1};
y = dataArray{:, 2};
end