dataset format:
playerBestPips[1-20], dealerBestPips[1-11], playerMinPips[1-20], shouldHitOrStay [hit=1,stay=0]

dataset-simulated1.txt contains results from monte carlo simulation where we only peek the next card, never more than one. This results that sometimes model suggest to stay even if player pips < 11 (when you should never stay) and also it seems to prefer to stay more than it wants to hit. TODO => simulate multiple depths

