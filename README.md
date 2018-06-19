# The SENSE project
Welcome to the website of the SENSE project, an open source software platform for the synthesis of Evolutionarily Stable and Effective normative systems for Multi-Agent Systems (MAS). By considering basic information about a MAS, such as the composition of its agent population and the type of situations that the agents can interact in, SENSE allows to predict (to synthesise) norms to achieve successful  coordination between the agents.   

## What is SENSE and how does it work?
SENSE synthesises norms by employing [Evolutionary Game Theory (EGT)](https://en.wikipedia.org/wiki/Evolutionary_game_theory), a mathematical framework that allows to simulate the evolution of strategy adoption in Multi-Agent Systems. In EGT, strategy evolution allows to predict which strategies are [Evolutionarily Stable Strategies (ESS)](https://en.wikipedia.org/wiki/Evolutionary_game_theory#The_evolutionarily_stable_strategy), that is, strategies that, once established in a population, every agent prefers to conform on the assumption that everyone else does. 

SENSE considers a representation of a MAS as a game-theoretic setting in which the agents can play one or more games. Each game represents a particular situation in which two or more agents interact, and comes along with a payoff matrix that characterises the coordination success (from a policy maker point of view) for every possible combination of strategies performed by the agents in such a situation. For instance, consider a traffic scenario in which autonomous cars can interact in a road. Say that our goal, as policy makers, is to ensure that cars safely interact without collisions. The figure below illustrates two of these situations represented as games with a global utility matrix. Game A represents a situation in which two cars approach each other in a junction, and game B represents a situation in which two cars are doing a queue. In game A, both cars can choose either to go forward, stop (giving way), or accelerate, anticipating to the other car and goind through the junction before it. The matrix below game A specifies the utility of the MAS for each combination of these actions that the two cars can perform. 

In game A

<img src="https://github.com/NormSynthesis/SENSE/blob/master/games.png" width="30%" align="middle">

![alt text](https://github.com/NormSynthesis/SENSE/blob/master/sense_model.png)

## Downloading and installing SENSE

### Downloading and installing Eclipse

## Configuring and running SENSE

### Example: coordination of cars in a junction
