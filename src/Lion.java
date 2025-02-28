import java.awt.*;

public class Lion extends Critter {
    private Color color;
    private String str;
    public int counter = 0;

    // creates the lions
    public Lion() {
        setColor();
        setStr();
    }

    // sets the varying color of the lion
    private void setColor() {
        int start = (int)(Math.random() * 3);
        // changes the lions color ever three turns
        if (counter % 3 == 0 || counter == 0) {
            if (start == 0) {
                color = Color.RED;
            } else if (start == 1) {
                color = Color.GREEN;
            } else if (start == 2) {
                color = Color.BLUE;
            }
        }
        counter++;
    }

    // sets the string of the lion
    private void setStr() {
        str = "L";
    }

    // determines how the lion moves
    @Override
    public Action getMove(CritterInfo info) {
        setColor();
        setStr();

        if (info.getFront() == Neighbor.OTHER) {
            return Action.INFECT;
        } else if (info.getFront() == Neighbor.WALL || info.getRight() == Neighbor.WALL) {
            return Action.LEFT;
        } else if (info.getFront() == Neighbor.SAME) {
            return Action.RIGHT;
        } else {
            return Action.HOP;
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