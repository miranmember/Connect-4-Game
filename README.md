# Connect-4-Game
This project is an implementation of the classic game Connect 4 using JavaFX. The game allows users to play against each other by dropping colored discs into a grid, with the goal of getting four discs of the same color in a row either horizontally, vertically, or diagonally. The game also includes a menu bar with options to start a new game, exit the game, and change the theme.

## Getting Started
To run the Connect 4 game, ensure that you have Java 8 or higher installed on your system. Then, clone the repository and run
```
mvn clean
mvn compile
mvn exec:java
```

## Gameplay
To play the game, click on the column and the row where you want to place your disc. The game alternates between two players, with one player dropping red discs and the other dropping yellow discs. The first player to get four discs of their color in a row wins the game. If the grid becomes full without a player achieving a winning combination, the game ends in a draw.

## Menu Option
The game includes a menu bar with the following options:

+ New Game: starts a new game with the same players and theme.
+ Exit: exits the game.
+ Theme: allows the user to change the theme of the game by selecting one of the following options:
  + Light: changes the background color to white and the disc colors to red and yellow.
  + Dark: changes the background color to black and the disc colors to red and green.

## Customization
The game includes several customization options, including the ability to change the colors of the discs and the background, as well as the text displayed at the beginning and end of the game. These options can be modified in the start method of the **JavaFXTemplate class**.

## Additional Features
The game includes the following additional features:
+ A pop-up message is displayed at the beginning of each turn, indicating which player's turn it is.
+ The game includes a stack that stores the locations of each disc placed on the board, allowing users to undo their last move by clicking the Reverse Move button found on the menu.
