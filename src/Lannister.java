import java.awt.*;

public class Lannister extends Critter {
    private Color color;
    private String str;
    public int counter = 1;

    // creates the bears
    public Lannister() {
        setColor();
        setStr();
    }

    // sets the varying color of the bear
    private void setColor() {
        color = Color.RED;
    }

    // sets the varying string of the bear
    private void setStr() {
        str = "X";
    }

    // determines how the bear moves
    @Override
    public Action getMove(CritterInfo info) {
        setColor();
        setStr();

        if (info.getFront() == Neighbor.OTHER) {
            return Action.INFECT;
        } else if (info.getFront() == Neighbor.EMPTY) {
            return Action.HOP;
        } else {
            return Action.LEFT;
        }
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return str;
    }
}