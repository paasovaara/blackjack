function [J, grad] = costFunctionReg(theta, X, y, lambda)
%COSTFUNCTIONREG Compute cost and gradient for logistic regression with regularization
%   J = COSTFUNCTIONREG(theta, X, y, lambda) computes the cost of using
%   theta as the parameter for regularized logistic regression and the
%   gradient of the cost w.r.t. to the parameters. 

% Initialize some useful values
m = length(y); % number of training examples

% You need to return the following variables correctly 
J = 0;
grad = zeros(size(theta));

% Computes the cost of a particular choice of theta and set J to the cost.
% Computes the partial derivatives and sets grad to the partial
% derivatives of the cost w.r.t. each parameter in theta

[J0, grad0] = costFunction(theta, X, y);
%size(theta)

%L = eye(size(theta,1));
L = ones(size(theta)); % nx1
L(1) =0; % We don't regulize the first term theta0

%J1_sum = sum(theta.^2); % theta is nx1
J1_sum = sum(L'*(theta.^2)); % let's multiply with L so we can leave theta0 as is
J1 = lambda * J1_sum / (2 * m);

J = J0 + J1;

% =============================================================
% Gradient:
multiplier = lambda / m;
grad1 = multiplier .* (L .* theta); 
%print("grad sizes: ");
%size(grad0) % 1xn
%size(grad1) % nx1 
grad = grad0 + grad1'; % have to transpose grad1 since we've used different dimensions with it

% =============================================================
end
