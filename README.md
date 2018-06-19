# The SENSE project
Welcome to the website of the SENSE project, an open source software platform for the synthesis of Evolutionarily Stable and Effective normative systems for Multi-Agent Systems (MAS). By considering basic information about a MAS, such as the composition of its agent population and the type of situations that the agents can interact in, SENSE allows to predict (to synthesise) norms to achieve successful  coordination between the agents.   

## What is SENSE and how does it work?
SENSE synthesises norms by employing [Evolutionary Game Theory (EGT)](https://en.wikipedia.org/wiki/Evolutionary_game_theory), a mathematical framework that allows to simulate the evolution of strategy adoption in Multi-Agent Systems. In EGT, strategy evolution allows to predict which strategies are [Evolutionarily Stable Strategies (ESS)](https://en.wikipedia.org/wiki/Evolutionary_game_theory#The_evolutionarily_stable_strategy), that is, strategies that, once established in a population, every agent prefers to conform on the assumption that everyone else does. 

SENSE considers a representation of a MAS in a game-theoretic setting in which a given population of agents can play a collection of games. Each game represents a particular strategic situation in which two or more agents might require coordination in order to successfully interact. Each game comes along with a payoff matrix that characterises the coordination success of the agents (from a policy maker point of view) for every possible combination of strategies that they can perform in such a situation. The architecture and operation of SENSE is graphically illustrated in the figure below. It takes as input a collection of games (1) along with a population composed of agents (2) with different preferences and behaviours over the actions to perform in these games, namely with different profiles. Additionally, SENSE considers a set of possible sanctions that can be employed to regulate the agents' behaviours in these games once they perform actions that are undesirable (from our perspective as policy makers). 

![SENSE's architecture and computational model](https://github.com/NormSynthesis/SENSE/blob/master/sense_model.png)

With these elements at hand, SENSE carries out an evolutionary process that outputs a set of norms aimed at coordinating the agents in each possible situation they might face once they play different combinations of the games specified in (1). If stability can be achieved, SENSE will find the combination of norms that are evolutionarily stable (that the agents will comply with) and that maximise the system's effectiveness (utility) at the same time.

## Download and install

At the moment, SENSE needs to be imported and executed as a software project in Eclipse. We are currently working on the development of a Graphical User Interface (GUI) that will allow to configure and run SENSE very easily and without requiring Eclipse. Next, we detail how to download and install Eclipse, and how to import, configure and run SENSE. 

### Install Eclipse

First, go to http://www.eclipse.org/downloads/ and download the latest Eclipse version that suits your computer. You just need to follow the instructions specified in the [Eclipse guide](http://help.eclipse.org/oxygen/index.jsp?nav=%2F0) in order to install it. 

### Configure and run SENSE

In order to download SENSE, you just need to click here and download the source code of the project as a zip file. Once you open Eclipse, you 

### Example: cars' coordination

For instance, consider a traffic scenario in which autonomous cars can interact in a road. Say that our goal, as policy makers, is to ensure that cars safely interact without collisions. The figure below illustrates two of these situations represented as games. Game A has two roles (players). It represents a situation in which two cars approach each other in a junction. Similarly, game B (with two roles) represents a situation in which two cars are doing a queue. Beneath each game is its utility matrix specifying the global system utility for each combination of these actions that the two cars can perform. Briefly, the best option from a policy maker's point of view is that one of the cars stops, giving way to the other one, thus deriving utility 1. Other action combinations are less useful. 

<img src="https://github.com/NormSynthesis/SENSE/blob/master/games.png" width="30%" align="middle">

