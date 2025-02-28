import java.awt.*;

public class Stark extends Critter {
    private Color color;
    private String str;
    public int counter = 1;

    // creates the giants
    public Stark() {
        setColor();
        setStr();
    }

    // sets the color of the giant
    private void setColor() {
        color = Color.GRAY;
    }

    // sets the varying string of the giant
    private void setStr() {
        str = "O";
    }

    // determines how the giant moves
    @Override
    public Action getMove(CritterInfo info) {
        setColor();
        setStr();

        if (info.getFront() == Neighbor.OTHER) {
            return Action.INFECT;
        } else if (info.getFront() == Neighbor.EMPTY) {
            return Action.HOP;
        } else {
            return Action.RIGHT;
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