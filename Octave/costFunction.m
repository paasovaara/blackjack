function [J, grad] = costFunction(theta, X, y)
%COSTFUNCTION Compute cost and gradient for logistic regression
%   J = COSTFUNCTION(theta, X, y) computes the cost of using theta as the
%   parameter for logistic regression and the gradient of the cost
%   w.r.t. to the parameters.

% Initialize some useful values
m = length(y); % number of training examples

% Return values are J and grad 
J = 0;
grad = zeros(size(theta));

% Computes the cost of a particular choice of theta and set J to the cost.
% Computes the partial derivatives and sets grad to the partial
% derivatives of the cost w.r.t. each parameter in theta
%
% Note: grad should have the same dimensions as theta

h = sigmoid(X * theta); 
firstTerm = -y' * log(h);
secondTerm = (1-y')*log(1-h);
sum = firstTerm - secondTerm;

J = (1/m)*sum;

gradSum = (h - y)'*X; % h and y are mx1, x is mxn

grad = (1/m)*gradSum;

% =============================================================

end
