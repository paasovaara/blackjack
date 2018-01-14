# Octave scripts for AI

## Prerequisites:
Developed using Octave 4.2.1 (https://www.gnu.org/software/octave/) 

## Usage

Execute script "createModel". It will read the input file (training samples) and produce an output file containing the model parameters (Theta). Give that file as input to the game. 


## Development Notes

dataset format:
playerBestPips[1-20], dealerBestPips[1-11], playerMinPips[1-20], shouldHitOrStay [hit=1,stay=0]

Create the dataset using classes from package blackjack.ai in the [Java-project](/Java/blackjack) and use that file as input for the scripts in this folder.

### Simple model ver 1.

dataset-simulated1.txt contains results from monte carlo simulation where we only peek the next card, never more than one. This could result that more complicated model would suggest to stay even if player pips < 11 (when you should never stay) and also it seems to prefer to stay more than it wants to hit. TODO => simulate multiple depths

Also first iteration only uses playerBestPips and dealerBestPips


## TODO

- now the model and input/output filenames hardcoded, refactor and take them as parameter.
- use also playerMinPips-column from data
- add polynomials (and regularization) to the model 
- distribute dataset to test and cross-validation set.
- use neural networks
- add more output states (double, split, fold)
