function g = sigmoid(z)
%SIGMOID Compute sigmoid function
%   g = SIGMOID(z) computes the sigmoid of z (z can be a matrix,
%               vector or scalar).

g = zeros(size(z));

denominator = 1 + exp(-z);

g = 1 ./ denominator;

end
