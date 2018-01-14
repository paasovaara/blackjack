%% Initialization
clear ; close all; clc

%% Load Data
% dataset format:
% playerBestPips[1-20], dealerBestPips[1-11], playerMinPips[1-20], shouldHitOrStay [hit=1,stay=0]

data = load('dataset-simulated1.txt');
% let's ignore column 3 (playerMinPips) for now
X = data(:, [1, 2]); y = data(:, 4);

%% ==================== Part 1: Plotting ====================
%  We start the exercise by first plotting the data to understand the 
%  the problem we are working with.

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

%% ============ Part 2: Compute Cost and Gradient ============
%  Let's implement the cost and gradient for logistic regression. 

%  Setup the data matrix appropriately, and add ones for the intercept term
[m, n] = size(X);

% Add intercept term to x and X_test
X = [ones(m, 1) X];

% Initialize fitting parameters
initial_theta = zeros(n + 1, 1);

% Compute and display initial cost and gradient
[cost, grad] = costFunction(initial_theta, X, y);

fprintf('Cost at initial theta (zeros): %f\n', cost);
fprintf('Gradient at initial theta (zeros): \n');
fprintf(' %f \n', grad);


%% ============= Part 3: Optimizing using fminunc  =============
%  Let's use a built-in function (fminunc) to find the optimal parameters theta.

%  Set options for fminunc
options = optimset('GradObj', 'on', 'MaxIter', 400);

%  Run fminunc to obtain the optimal theta
%  This function will return theta and the cost 
[theta, cost] = ...
	fminunc(@(t)(costFunction(t, X, y)), initial_theta, options);

% Print theta to screen
fprintf('Cost at theta found by fminunc: %f\n', cost);
fprintf('theta: \n');
fprintf(' %f \n', theta);

% Plot Boundary
plotDecisionBoundary(theta, X, y);

% Put some labels 
hold on;
% Labels and Legend
xlabel('Player best hand')
ylabel('Dealer best hand')

% Specified in plot order
legend('Should hit', 'Should stay')
hold off;

fprintf('\nProgram paused. Press enter to continue.\n');
pause;

csvwrite('model-simple.csv', theta);

%% ============== Part 4: Predict and Accuracies ==============
%  After learning the parameters, you'll like to use it to predict the outcomes
%  on unseen data. In this part, you will use the logistic regression model
%  to predict the probability of some specific hand.
%
%  Furthermore, we will compute the training and test set accuracies of 
%  our model.
%

%  Predict probability for a hand with pips 17 and dealer 10

prob = sigmoid([1 17 10] * theta);
hit = prob >= 0.5;
fprintf(['For hand 17 against dealer 10 we predict ' ...
         'probability of %f for hitting, so action is: %f\n'], prob, hit);

% Compute accuracy on our training set
p = predict(theta, X);

fprintf('Train Accuracy: %f\n', mean(double(p == y)) * 100);
fprintf('Expected accuracy (approx): 89.0\n');
fprintf('\n');


