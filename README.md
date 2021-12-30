# Mouse-Game
A rudimentary game where several agents attempt to complete a course in the shortest amount of time. Maze functionality is incomplete due to time constraints when originally completing the project for school. 

The game works by agents completing a course made up of tiles that can either be moved to or not, then having the reward divided by the total amount of moves they made. This value is then added to each tile's information rating once, increasing the liklihood of the agents choosing that tile again. After each agent has completed the course the two best agents have their paths spliced together to potentially create an even shorter path. The game continues until the shortest path through the course is found.
