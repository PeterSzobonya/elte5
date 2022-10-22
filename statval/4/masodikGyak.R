library(combinat)

### 1. feladat ###
allStud <- 14
(ncol(combn(allStud, 1)) * ncol(combn((allStud - 1), 2)))

### 2. feladat ###
alap <- 0.5
((alap + alap) - (alap * alap))

### 3. feladat ###
ncol(combn(10,10))

pnorm(97, mean = 100, sd = 2)

qnorm(0.25, mean = 100, sd = 2)

pnorm(98.65102, mean = 100, sd = 2)

(1 - pnorm(97.9, mean = 100, sd = 1.2))

pexp(3, rate = 1/4)

f <- function(x){4 * exp(-2 * x)}
integrate(f, lower = 0, upper = 0.3465738)
