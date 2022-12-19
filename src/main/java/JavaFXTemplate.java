import javafx.animation.PauseTransition;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Stack;

public class JavaFXTemplate extends Application {

    // All the variables we had to use for the game.
    int currCol, currRow, Theme = 0;
    Button startGame, newGame = new Button("New Game"), exit = new Button("Exit");
    Text welcome, End, message;
    BorderPane pane;
    GridPane gridpane;
    VBox root, welcomeRoot, endRoot;
    MenuBar menu;
    HashMap<String,Scene> sceneMap;
    EventHandler<ActionEvent> returnButtons, popupHandler;
    GameButton[][] Barr = new GameButton[6][7];
    int playerTurn = 1, winningPlayer;
    boolean winCondition;
    PauseTransition pause = new PauseTransition(Duration.seconds(5));
    Stack<int[]> moves = new Stack<>();
    Popup popup;


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);
    }

    // Created a hashmap where we store all our scenes and change them using event actions depending on
    // the situation.
    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        primaryStage.setTitle("Connect4");
        sceneMap = new HashMap<>();

        popupHandler =
                e -> {
                    if (!popup.isShowing())
                        popup.show(primaryStage);
                };

        sceneMap.put("WelcomeScreen", WelcomeScreen());
        sceneMap.put("GameScreen", GameScreen());

        startGame.setOnAction(e -> primaryStage.setScene(sceneMap.get("GameScreen")));

        // since we need to pause for 5 seconds after a win condition we use pausetransition.
        pause.setOnFinished(e->primaryStage.setScene(sceneMap.get("EndScreen")));

        newGame.setOnAction(e -> {resetBoard(); primaryStage.setScene(sceneMap.get("GameScreen"));});
        exit.setOnAction(e -> Platform.exit());


        // start out with welcome screen.
        primaryStage.setScene(sceneMap.get("WelcomeScreen"));
        primaryStage.show();
    }

    // this will be the screen the game starts out with. It will have one button and text.
    public Scene WelcomeScreen() {
        startGame = new Button("Start");
        // the buttons uses a lot of css for styling. added a green border with the same background color and added padding for styling.
        startGame.setStyle("-fx-background-color: #373737 ;-fx-border-color: green; -fx-text-fill: green; -fx-border-width: 2px; -fx-font-size: 14px; -fx-padding: 10 20 10 20;");

        welcome = new Text();
        welcome.setText("Welcome to Connect Four\nClick on Start Game button to play\nEnjoy!!");
        // set the text fonts and color to match the background gray.
        welcome.setFont(Font.font("Helvetica", 18));
        welcome.setFill(Color.GRAY);
        welcome.setTextAlignment(TextAlignment.CENTER);

        // using vbox and adding the vbox to center of a borderpane to create the scene.
        welcomeRoot = new VBox(20, welcome, startGame);
        welcomeRoot.setStyle("-fx-background-color: #373737");
        welcomeRoot.setAlignment(Pos.CENTER);

        pane = new BorderPane(welcomeRoot);

        return new Scene(pane, 400,350);
    }

    // This will be scene displayed after someone wins with who won and an option to start a new game or exit.
    public Scene EndScreen() {
        End = new Text();
        // setting the style for the buttons at the end.
        newGame.setStyle( "-fx-background-color: #373737 ;-fx-border-color: green; -fx-text-fill: green; -fx-border-width: 2px; -fx-font-size: 14px; -fx-padding: 10 20 10 20;");
        newGame.setMinWidth(100);
        exit.setStyle( "-fx-background-color: #373737 ;-fx-border-color: green; -fx-text-fill: green; -fx-border-width: 2px; -fx-font-size: 14px; -fx-padding: 10 20 10 20;");
        exit.setMinWidth(100);

        // setting up the text based on who won or if it was a tie
        if (IsWinning()) {
            End.setText("Player " + winningPlayer + " won!!");
        } else {
            End.setText("The game ended in a tie");
        }

        // setting up the fons and alignment for test.
        End.setTextAlignment(TextAlignment.CENTER);
        End.setFont(Font.font("Helvetica", 18));
        End.setFill(Color.GRAY);
        HBox buttons = new HBox(30, newGame, exit);
        buttons.setAlignment(Pos.CENTER);
        endRoot = new VBox(20, End, buttons);
        endRoot.setStyle("-fx-background-color: #373737");
        endRoot.setAlignment(Pos.CENTER);
        BorderPane pane = new BorderPane(endRoot);
        return new Scene(pane, 400,350);
    }

    // main game screen
    public Scene GameScreen() {
        gridpane = new GridPane();
        message = new Text();

        message.setFont(Font.font("Helvetica", 18));
        message.setFill(Color.GRAY);

        // setting style for the gridpane this will later change depending on the theme.
        gridpane.setStyle("-fx-background-color: #373737;");
        gridpane.setMaxSize(350, 350);
        gridpane.setPadding(new Insets(10,10,10,10));
        gridpane.setVgap(5);
        gridpane.setHgap(5);
        gridpane.setAlignment(Pos.CENTER);

        popup = new Popup();
        Label howTo = new Label();
        howTo.setFont(new Font(15));
        howTo.setText("Start out by clicking on the buttons row of the board. Each player takes turns to do so.\n" +
                "The Goal for the player is to get four matching colors in a row either horizontally, vertically or diagonally.\n" +
                "If you manage to fill up the board than its a tie.\n" +
                "Click back on the game board to close this popup.");
        howTo.setTextAlignment(TextAlignment.CENTER);
        howTo.setStyle("-fx-background-color: gray;");

        popup.getContent().add(howTo);
        popup.setAutoHide(true);
        popup.centerOnScreen();

        // if a button is clicked on the board of the game than disable it and change
        // its color, and check for winning conditions and also add it to stack
        // so when reverse move is called it can be utilized.
        returnButtons = actionEvent -> {
            GameButton B = (GameButton) actionEvent.getSource();
            currRow = B.getRow();
            currCol = B.getCol();
            MakeMove();
            if (IsWinning()) {
                DisableButtons();
                sceneMap.put("EndScreen", EndScreen());
                pause.play();
            } else if (isTie()) {
                sceneMap.put("EndScreen", EndScreen());
                pause.play();
            }
        };

        // making a 2d array of buttons and adding them to the gridpane
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                GameButton B = new GameButton(i, j);
                B.setOnAction(returnButtons);
                Barr[i][j] = B;
                gridpane.add(Barr[i][j], j, i);
            }
        }

        // adding menu items like gameplay, themes, options
        menu = new MenuBar();

        Menu gamePlay = new Menu("Game Play");
        Menu themes = new Menu("Themes");
        Menu options = new Menu("Options");

        // adding options
        MenuItem reverse = new MenuItem("Reverse Move");
        MenuItem originalTheme = new MenuItem("Original Theme");
        MenuItem themeOne = new MenuItem("Dark Mode");
        MenuItem themeTwo = new MenuItem("Light Mode");
        MenuItem howToPlay = new MenuItem("How to Play");
        MenuItem newGame = new MenuItem("New Game");
        MenuItem exit = new MenuItem("Exit");

        // all the menu items sorted in each menu
        gamePlay.getItems().addAll(reverse);
        themes.getItems().addAll(originalTheme, themeOne, themeTwo);
        options.getItems().addAll(howToPlay, newGame, exit);

        menu.getMenus().addAll(gamePlay, themes, options);

        // all the items need a function and these are the funtions.
        reverse.setOnAction(actionEvent -> undoMove());
        newGame.setOnAction(actionEvent -> resetBoard());
        howToPlay.setOnAction(popupHandler);
        exit.setOnAction(actionEvent -> System.exit(0));

        // apply themes
        originalTheme.setOnAction(actionEvent -> applyOriginalTheme());
        themeOne.setOnAction(actionEvent ->  applyThemeOne());
        themeTwo.setOnAction(actionEvent ->  applyThemeTwo());

        root = new VBox(20, menu, gridpane, message);
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #373737");
        return new Scene(root, 700, 550);
    }

    // resetting the board is used whenever newgame needs to be created for example
    // when the game ends or when the user cliks new game menu option
    // the function will need to reset the entire 2d array to default
    // also keeping in mind the theme of the board.
    private void resetBoard() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Barr[row][col].setDisable(false);
                Barr[row][col].setIs_clicked(false);
                if (Theme == 0) {
                    Barr[row][col].setPlayer(0, "gray");
                } else if (Theme == 1) {
                    Barr[row][col].setPlayer(0, "#006666");
                } else if (Theme == 2) {
                    Barr[row][col].setPlayer(0, "#027DFF");
                }
            }
        }

        message.setText(null);
        playerTurn = 1;
        while (!moves.empty()) {
            moves.pop();
        }
    }

    // everytime a player makes move it is stored in the stack and whenever
    // reverse move is clicked it will pop that element from the stack and reset
    // that index to default values.
    private void undoMove() {
        // ensures that reverse move is not made if the stack is empty
        if (!moves.empty()) {
            int tempRow, tempCol;
            tempRow = moves.peek()[0];
            tempCol = moves.peek()[1];
            Barr[tempRow][tempCol].setDisable(false);
            Barr[tempRow][tempCol].setIs_clicked(false);

            // changes the color depenging on the theme
            if (Theme == 0) {
                Barr[tempRow][tempCol].setPlayer(0, "gray");
            } else if (Theme == 1) {
                Barr[tempRow][tempCol].setPlayer(0, "#006666");
            } else if (Theme == 2) {
                Barr[tempRow][tempCol].setPlayer(0, "#027DFF");
            }
            moves.pop();
            playerTurn = (playerTurn == 1) ? 2 : 1;
            message.setText("Player " + playerTurn + " reversed their move at " + tempRow + ", " + tempCol + ". Player " + playerTurn + " pick again.");
        } else {
            message.setText("Please make a move first");
        }
    }

    // when user clicks the board buttons this function is called to make a move.
    public void MakeMove() {
        // checks weather or not the move is valid by checking if the row below
        // that index was clicked on and if thats true than its a valid move
        if (currRow == 5 || Barr[currRow+1][currCol].getIs_clicked()) {
            message.setText("Player " + playerTurn + " moved to " + currRow + "," + currCol + "\n");
            // changes the color to whatever the theme has.
            if (Theme == 0) {
                String color = (playerTurn == 1) ? "green" : "#001E00";
                Barr[currRow][currCol].setPlayer(playerTurn, color);
            } else if (Theme == 1) {
                String color = (playerTurn == 1) ? "#5e1ff0" : "pink";
                Barr[currRow][currCol].setPlayer(playerTurn, color);
            } else if (Theme == 2) {
                String color = (playerTurn == 1) ? "#FA8072" : "#7C0A02";
                Barr[currRow][currCol].setPlayer(playerTurn, color);
            }
            // disable the button and add it on to the stack.
            playerTurn = (playerTurn == 1) ? 2 : 1;
            Barr[currRow][currCol].setDisable(true);
            Barr[currRow][currCol].setIs_clicked(true);
            moves.push(new int[]{currRow, currCol});
        } else {
            message.setText("Player " + playerTurn + " moved to " + currRow + "," + currCol + ". This is NOT valid move. Player " + playerTurn + " pick again.");
        }
    }

    // winning conditions for the board.
    public boolean IsWinning() {
        int prevPlayer = (playerTurn == 1) ? 2 : 1;
        winCondition = (isWinningHorizontal(prevPlayer) || isWinningVertical(prevPlayer) || isWinningDiagonalRight(prevPlayer) || isWinningDiagonalLeft(prevPlayer));
        return winCondition;
    }

    // checks weather any of the buttons have the neutral of 0 if they do than there is still
    // moves player can make to win in that case return false as it is not a tie.
    // otherwise if the baord is filled than its a tie so return true.
    public boolean isTie() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                if (Barr[row][col].getPlayer() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    // checks weather a player has won horizontally by checking each index and adding on to currentstreak and if
    // that current streak reaches four than someone won so it returns true.
    public boolean isWinningHorizontal(int prevPlayer) {
        for (int row = 0; row < 6; row++) {
            int currentStreak = 0;
            for (int col = 0; col < 7; col++) {
                if (Barr[row][col].getPlayer() == prevPlayer) {
                    currentStreak++;
                    if (currentStreak == 4) {
                        // also have to change the color of that button to highlight how the player won.
                        Barr[row][col].getStyleClass().clear();
                        Barr[row][col].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: white");
                        Barr[row][col-1].getStyleClass().clear();
                        Barr[row][col-1].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color:white");
                        Barr[row][col-2].getStyleClass().clear();
                        Barr[row][col-2].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color:white");
                        Barr[row][col-2].getStyleClass().clear();
                        Barr[row][col-3].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color:white");
                        winningPlayer = prevPlayer;
                        return true;
                    }
                } else {
                    currentStreak = 0;
                }
            }
        }
        return false;
    }

    // checks weather a player has won Vertically by checking each index and adding on to currentstreak and if
    // that current streak reaches four than someone won so it returns true.
    public boolean isWinningVertical(int prevPlayer) {
        for (int col = 0; col < 7; col++) {
            int currentStreak = 0;
            for (int row = 0; row < 6; row++) {
                if (Barr[row][col].getPlayer() == prevPlayer) {
                    currentStreak++;
                    if (currentStreak == 4) {

                        // also have to change the color of that button to highlight how the player won.
                        Barr[row][col].getStyleClass().clear();
                        Barr[row][col].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color:white");
                        Barr[row-1][col].getStyleClass().clear();
                        Barr[row-1][col].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color:white");
                        Barr[row-2][col].getStyleClass().clear();
                        Barr[row-2][col].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color:white");
                        Barr[row-3][col].getStyleClass().clear();
                        Barr[row-3][col].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color:white");
                        winningPlayer = prevPlayer;
                        return true;
                    }
                } else {
                    currentStreak = 0;
                }
            }
        }
        return false;
    }
    // checks weather a player has won Diagonally going up by checking each index and adding on to currentStreak and if
    // that current streak reaches four than someone won so it returns true.
    public boolean isWinningDiagonalRight(int prevPlayer) {
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 6; row++) {
                int currentStreak = 0;
                int i, j;
                for (i = row, j = col; i < 6 && j < 7; i++,j++) {
                    if (Barr[i][j].getPlayer() == prevPlayer) {
                        currentStreak++;
                        if (currentStreak == 4) {
                            Barr[i][j].getStyleClass().clear();


                            Barr[i][j].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color:white");
                            Barr[i-1][j-1].getStyleClass().clear();
                            Barr[i-1][j-1].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color:white");
                            Barr[i-2][j-2].getStyleClass().clear();
                            Barr[i-2][j-2].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color:white");
                            Barr[i-3][j-3].getStyleClass().clear();
                            Barr[i-3][j-3].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color:white");
                            winningPlayer = prevPlayer;
                            return true;
                        }
                    } else {
                        currentStreak = 0;
                    }
                }
            }
        }
        return false;
    }

    // checks weather a player has won Diagnally going down by checking each index and adding on to currentstreak and if
    // that current streak reaches four than someone won so it returns true.
    public boolean isWinningDiagonalLeft(int prevPlayer) {
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 6; row++) {
                int currentStreak = 0;
                int i, j;
                for (i = row, j = col; i < 6 && j >= 0; i++,j--) {
                    if (Barr[i][j].getPlayer() == prevPlayer) {
                        currentStreak++;
                        if (currentStreak == 4) {
                            Barr[i][j].getStyleClass().clear();
                            // also have to change the color of that button to highlight how the player won.
                            Barr[i][j].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color:white");
                            Barr[i-1][j+1].getStyleClass().clear();
                            Barr[i-1][j+1].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color:white");
                            Barr[i-2][j+2].getStyleClass().clear();
                            Barr[i-2][j+2].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color:white");
                            Barr[i-3][j+3].getStyleClass().clear();
                            Barr[i-3][j+3].setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color:white");
                            winningPlayer = prevPlayer;
                            return true;
                        }
                    } else {
                        currentStreak = 0;
                    }
                }
            }
        }
        return false;
    }

    // when you win a player wins all the buttons are disabled because the game
    // highlights who won and stays on that sence for 5 seconds.
    public void DisableButtons() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Barr[row][col].setDisable(true);
            }
        }
    }

    // when the user clicks on the menu opting change theme to original all the css styling is applied to change the
    // theme and the global variable theme changes to 0 stating its on the 0th theme, which allows reverse move to
    // function properly.
    public void applyOriginalTheme() {
        Theme = 0;
        gridpane.setStyle("-fx-background-color: #373737;");
        root.setStyle("-fx-background-color: #373737");
        message.setFill(Color.GRAY);
        // accessing every button in 2d array to change the colors to gray, green and dark green.
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 6; row++) {
                if (Barr[row][col].getPlayer() == 0) {
                    Barr[row][col].setPlayer(0, "gray");
                } else if (Barr[row][col].getPlayer() == 1) {
                    Barr[row][col].setPlayer(1, "green");
                } else if (Barr[row][col].getPlayer() == 2) {
                    Barr[row][col].setPlayer(2, "#001E00");
                }

            }
        }

    }

    // same as above but this time it applies dark theme. changing fonts to white so players can see them better.
    public void applyThemeOne() {
        Theme = 1;
        gridpane.setStyle("-fx-background-color: #003333;");
        root.setStyle("-fx-background-color: #003333");
        message.setFill(Color.WHITE);
        // changing buttons on the board to pink, blue, cyan.
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 6; row++) {
                if (Barr[row][col].getPlayer() == 0) {
                    Barr[row][col].setPlayer(0, "#006666");
                } else if (Barr[row][col].getPlayer() == 1) {
                    Barr[row][col].setPlayer(1, "#5e1ff0");
                } else if (Barr[row][col].getPlayer() == 2) {
                    Barr[row][col].setPlayer(2, "pink");
                }

            }
        }
    }

    // last theme is light mode.
    public void applyThemeTwo() {
        Theme = 2;
        gridpane.setStyle("-fx-background-color: #FFFAFA;");
        root.setStyle("-fx-background-color: #FFFAFA");
        message.setFill(Color.BLACK);
        // applying blue, pink and purple
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 6; row++) {
                if (Barr[row][col].getPlayer() == 0) {
                    Barr[row][col].setPlayer(0, "#027DFF");
                } else if (Barr[row][col].getPlayer() == 1) {
                    Barr[row][col].setPlayer(1, "#FA8072");
                } else if (Barr[row][col].getPlayer() == 2) {
                    Barr[row][col].setPlayer(2, "#7C0A02");
                }
            }
        }
    }
}
