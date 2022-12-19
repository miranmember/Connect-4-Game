import javafx.scene.control.Button;
import javafx.scene.shape.Circle;

public class GameButton extends Button {
    private final int row;
    private final int col;
    private boolean is_clicked;
    private int player;

    GameButton(int row, int col) {
        this.row = row;
        this.col = col;
        player = 0;
        getStyleClass().clear();
        setStyle("-fx-background-color:grey;");
        setShape(new Circle(100));
        setMinSize(60, 60);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setIs_clicked(boolean value){
        is_clicked = value;
    }

    public boolean getIs_clicked() {
        return is_clicked;
    }

    public void setPlayer(int Player, String Color) {
        this.player = Player;
        this.getStyleClass().clear();
        this.setStyle("-fx-background-color:" + Color + ";");
    }

    public int getPlayer() {
        return player;
    }
}
