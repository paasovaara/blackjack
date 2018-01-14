%% Initialization
clear ; close all; clc

%% Load Data
% dataset format:
% playerBestPips[1-20], dealerBestPips[1-11], playerMinPips[1-20], shouldHitOrStay [hit=1,stay=0]

data = load('dataset-simulated2.txt');
% let's ignore column 3 (playerMinPips) for now
X = data(:, [1, 2]); y = data(:, 4);

plotData(X, y);

fprintf(['Plotting data with + indicating (y = 1) examples and o ' ...
         'indicating (y = 0) examples.\n']);

plotData(X, y);

% Put some labels 
hold on;
% Labels and Legend
xlabel('Player best hand')
ylabel('Dealer best hand')

% Specified in plot order
legend('Should hit', 'Should stay')
hold off;


%% Regularized Logistic Regression
%  Let's add more polynomial features our data matrix (similar to polynomial
%  regression).
%

% Add Polynomial Features

% Note that mapFeature also adds a column of ones for us, so the intercept
% term is handled
degree = 6; % How many degrees, 2->
X = mapFeature(X(:,1), X(:,2), degree);

% Initialize fitting parameters
initial_theta = zeros(size(X, 2), 1);

% Set regularization parameter lambda to 1
lambda = 1;

% Compute and display initial cost and gradient for regularized logistic
% regression
[cost, grad] = costFunctionReg(initial_theta, X, y, lambda);

fprintf('Cost at initial theta (zeros): %f\n', cost);
fprintf('Gradient at initial theta (zeros) - first five values only:\n');
fprintf(' %f \n', grad(1:5));

%fprintf('\nProgram paused. Press enter to continue.\n');
%pause;

% Compute and display cost and gradient
% with all-ones theta and lambda = 10
test_theta = ones(size(X,2),1);
[cost, grad] = costFunctionReg(test_theta, X, y, 10);

fprintf('\nCost at test theta (with lambda = 10): %f\n', cost);
fprintf('Expected cost (approx): 3.16\n');
fprintf('Gradient at test theta - first five values only:\n');
fprintf(' %f \n', grad(1:5));
fprintf('Expected gradients (approx) - first five values only:\n');
fprintf(' 0.3460\n 0.1614\n 0.1948\n 0.2269\n 0.0922\n');

%fprintf('\nProgram paused. Press enter to continue.\n');
%pause;

%% ============= Part 2: Regularization and Accuracies =============
%  Optional Exercise:
%  In this part, you will get to try different values of lambda and
%  see how regularization affects the decision coundart
%
%  Try the following values of lambda (0, 1, 10, 100).
%
%  How does the decision boundary change when you vary lambda? How does
%  the training set accuracy vary?
%

% Initialize fitting parameters
initial_theta = zeros(size(X, 2), 1);

% Set regularization parameter lambda to 1 (you should vary this)
lambda = 1;

% Set Options
options = optimset('GradObj', 'on', 'MaxIter', 400);

% Optimize
[theta, J, exit_flag] = ...
	fminunc(@(t)(costFunctionReg(t, X, y, lambda)), initial_theta, options);

  
% Save the model
filename = strcat("model-polynomial-", num2str(degree), ".csv")
csvwrite(filename, theta);

% Plot Boundary
plotDecisionBoundary(theta, X, y, degree);
hold on;
title(sprintf('lambda = %g', lambda))

% Labels and Legend
xlabel('Player best hand')
ylabel('Dealer best hand')

legend('y = 1', 'y = 0', 'Decision boundary', 'location', 'southwest')
hold off;

% Compute accuracy on our training set
p = predict(theta, X);

fprintf('Train Accuracy: %f\n', mean(double(p == y)) * 100);

