## How to use the main program

After running the program from the command line, or double-clicking the executable Jar, the main menu pops up. Here you can find the following sections, from top to bottom:

- Maze Generator
- Scenario
- Runners
- Rate of simulation
- End simulation at score
- Start
 
#### Maze Generator

Maze Generator is the component that generates the playfield. The playfield might be an empty arena, having walls only in the edges of the field. It might be a maze with complex paths from one point to another. Whatever it is, the chosen maze generator determines how the field is constructed.

To choose a maze generator, click on the menu icon on the right edge below the **Maze Generator** label.

#### Scenario

Scenario is the ruleset of the simulation. The scenario defines the following things:

- Minimum and maximum allowed runners.
- Is collision allowed between two entities.
- Is a runner allowed to visit a specific position in a maze.
- What is done to a runner when it wishes to move to some direction.
- How runners should be placed in the maze initially.
- What are the goals of each runner.
- How are runners scored.
 
A scenario might also have additional responsibilities, such as inserting entities in the maze.

To choose a scenario, click on the menu icon on the right edge below the **Maze Generator** label. Depending on the scenario chosen, a settings icon might appear next to the menu icon. Click on it to change scenario-specific settings.

#### Runners

A runner is an artificial intelligence moving in the maze. In most cases it's nothing more than some path finding algorithm moving towards the closest goal it can find, but depending on the scenario it's intended for, it might be a little more complicated than that.

To add a runner, click on the **Add Runner** button. To specify a runner's algorithm, click on the menu icon on the appropriate row. To remove a runner, click on the delete icon on the appropriate row.

#### Rate of simulation

You can specify the delay, in milliseconds, between each move in this field. So a smaller value makes the simulation go faster.

#### End simulation at score

You can specify a score cap in this field. When some runner attains this score (or higher), the simulation stops. This is by default 200. A value of 0 or lower means no cap.

#### Start

Clicking on this button starts the simulation, given that all required components are in place. A new window will open showcasing the simulation. Below the simulation panel are each runners' current score, labeled by the associated runner's color and title.

To end the simulation abruptly, simply close the simulation window.

## Implementing plugins

Implementing plugins requires **Java 8** and the main program as a dependency. After these are met, implementing plugins is quite straight forward.

#### MazeGenerator

To implement a `MazeGenerator`, refer to the below example:

```java
public class ArenaMazeGenerator extends AbstractMazeGenerator {

    @Override
    public Maze generateMaze(int width, int height) {
        MazeBlock[][] layout = new MazeBlock[height][width];
        
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                layout[i][j] = isBoundary(j, i, width, height) ?
                        MazeBlock.WALL :
                        MazeBlock.FLOOR;
        
        return new Maze(layout);
    }
    
    private boolean isBoundary(int x, int y, int w, int h) {
        return x == 0 || x == w - 1 || y == 0 || y == h - 1;
    }

}
```

The above example generates an arena-like maze, everything being floor except for the boundaries, which are wall.

The `generateMaze` returns a `Maze` object, which can be constructed with a 2D `MazeBlock` matrix. `MazeBlock` is an enum class containing the `Mazeblock.FLOOR` and `MazeBlock.WALL` enums. What these signify should be self-explanatory.

All maze generator implementations should at least implement the `MazeGenerator` interface, though it is recommended to simply extend the `AbstractMazeGenerator` class to make things easier, as in the example above.

Further examples can be found [here](../plugins).

#### Scenario

All implementations of `Scenario` should implement the `Scenario` interface, though it is again recommended to extends the `AbstractScenario` to simplify this process.

Giving a small example for a `Scenario` implementation is a bit harder, so please refer to [examples](../plugins) and the javadoc.

#### Runner

All implementations of `Runner` should extend the `Runner` class.

To implement a runner, refer to the below example:

```Java
public class SimpletonRunner extends Runner {
    
    private static final Direction dirs[] = new Direction[] {
        Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT
    };

    @Override
    public Direction getNextMove(Maze maze, Collection<MazeEntity> goals, 
            Predicate<Position> positionPredicate) {
            
        // Determine the closest goal and distance to it.
        MazeEntity goal = getClosestGoal(goals);
        int bestDist = getDistanceTo(goal);
        
        // It is still unknown which direction leads to the least distance to the closest goal.
        Direction bestDir = Direction.NONE;
        
        for (Direction dir : dirs) {
        
            // Calculate the new position.
            int nx = getPosition().x + dir.deltaX;
            int ny = getPosition().y + dir.deltaY;
            Position newPos = new Position(nx, ny);
            
            // Is visiting the new position allowed?
            if (!positionPredicate.test(newPos))
                continue;
            
            // What is the distance to the closest goal from the new position?
            int dist = getManhattanDistance(newPos, goal.getPosition());
            
            // If the distance is better than the best known, update the info.
            if (dist < bestDist) {
                bestDist = dist;
                bestDir = dir;
            }
        }
        
        // Return the best direction.
        return bestDir;
    }
    
    private MazeEntity getClosestGoal(Collection<MazeEntity> goals) {
    
        // Init best known distance to infinity
        int bestDist = Integer.MAX_VALUE;
        
        // Closest known goal.
        MazeEntity bestGoal = null;
        
        for (MazeEntity goal : goals) {
            int dist = getDistanceTo(goal);      
            
            /* If distance to this goal is smaller than the best known,
             * update the best known distance.
             */
            if (dist < bestDist) {
                bestDist = dist;
                bestGoal = goal;
            }
        }
        
        return bestGoal;
    }
    
    private int getDistanceTo(MazeEntity ent) {
        return getManhattanDistance(this.getPosition(), ent.getPosition());
    }
    
    private int getManhattanDistance(Position p1, Position p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

}
```

The runner in the above example is very simple and probably gets eventually stuck in every playfield that has even a single wall... In any case, what is happening in the above example is choosing the direction that leads to a position with the least distance to the closest goal. The distance is determined by a simple heuristic, namely *manhattan distance*.

A runner makes its choice of direction according to the given maze, collection of goals and position predicate.

The `Maze` object can be used to obtain precise information about the playfield, such as the width and height of the maze and what exactly is located in some position.

The collection of goals is what the runner should try to obtain, or where the runner should try to go.

The `Predicate` object is used to determine if the runner is allowed to visit a specific position in the maze. This *positionPredicate* is generated by the simulation's scenario, and should be obeyed. If a runner tries to go to a location it is not allowed to go, in most cases the runner ends up being stopped or removed entirely from the simulation. So usually a runner should avoid going to illegal positions. What happens when a runner tries to go to an illegal position is determined by the simulation's scenario.

Further examples of `Runner`s can be found [here](../plugins).
