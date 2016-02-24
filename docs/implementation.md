## Structure of the program

The project is divided in clearly named packages and *plugins*. The important thing to know about the project is the way simulations are built. The main project consists of only the core classes and user interface classes.

All maze generation, rule and path finding related classes are in separate projects. These projects are packaged in separate Jar files and then loaded by the main project when put in the appropriate directory. More specifically a simulation consists of an implementation of the `MazeGenerator`, `Scenario` and `Runner` classes.

- A `MazeGenerator` generates the maze, or the arena of the simulation.
- A `Scenario` is the ruleset of the simulation, and specifies things like the allowed number of runners, what should happen when a runner collides with an entity (such as some other runner), scoring and so on. A scenario is allowed to modify the maze, and in some cases, such as adding/removing goals, removing runners on collison etc., it's required to do so.
- A `Runner` is an entity in the maze that can move according to its own will. A runner is the actual algorithmic focus point of this project.
