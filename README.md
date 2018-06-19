# The SENSE project
Welcome to the website of the SENSE project, an open source software platform for the synthesis of Evolutionarily Stable and Effective normative systems for Multi-Agent Systems (MAS). By considering basic information about a MAS, such as the composition of its agent population and the type of situations that the agents can interact in, SENSE allows to predict (to synthesise) norms to achieve successful  coordination between the agents.   

## What is SENSE and how does it work?
SENSE synthesises norms by employing [Evolutionary Game Theory (EGT)](https://en.wikipedia.org/wiki/Evolutionary_game_theory), a mathematical framework that allows to simulate the evolution of strategy adoption in Multi-Agent Systems. In EGT, strategy evolution allows to predict which strategies are [Evolutionarily Stable Strategies (ESS)](https://en.wikipedia.org/wiki/Evolutionary_game_theory#The_evolutionarily_stable_strategy), that is, strategies that, once established in a population, every agent prefers to conform on the assumption that everyone else does. 

The architecture and operation of SENSE is graphically illustrated in the figure below. SENSE considers a representation of a MAS in a game-theoretic setting in which the agents can play one or more games. Each game represents a particular strategic situation in which two or more agents interact and require coordination. Each game comes along with a payoff matrix that characterises the coordination success of the agents (from a policy maker point of view) for every possible combination of strategies that they can perform in such a situation. Also, SENSE 

![alt text](https://github.com/NormSynthesis/SENSE/blob/master/sense_model.png)

## Downloading and installing SENSE

### Downloading and installing Eclipse

## Configuring and running SENSE

### Example: coordination of cars in a junction

For instance, consider a traffic scenario in which autonomous cars can interact in a road. Say that our goal, as policy makers, is to ensure that cars safely interact without collisions. The figure below illustrates two of these situations represented as games. Game A has two roles (players). It represents a situation in which two cars approach each other in a junction. Similarly, game B (with two roles) represents a situation in which two cars are doing a queue. Beneath each game is its utility matrix specifying the global system utility for each combination of these actions that the two cars can perform. Briefly, the best option from a policy maker's point of view is that one of the cars stops, giving way to the other one, thus deriving utility 1. Other action combinations are less useful. 

<img src="https://github.com/NormSynthesis/SENSE/blob/master/games.png" width="30%" align="middle">

