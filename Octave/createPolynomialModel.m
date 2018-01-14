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

degree = 5; % How many degrees, 2->
% Set regularization parameter lambda (you should vary and test this)
lambda = 0.9;

X = mapFeature(X(:,1), X(:,2), degree);

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

