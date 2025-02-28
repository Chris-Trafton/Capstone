import java.awt.*;

public class Lancer extends Critter {
    private Color color;
    private String str;
    public int counter = 0;

    public Lancer() {
        setColor();
        setStr();
    }

    private void setColor() {
        color = Color.MAGENTA;
    }

    private void setStr() {
        // changes the string every turn
        if (counter % 4 == 1) {
            str = ">";
        } else if (counter % 4 == 2) {
            str = "^";
        } else if (counter % 4 == 3) {
            str = "<";
        } else {
            str = "v";
        }
        counter++;
    }

    // determines how the lancer moves
    @Override
    public Action getMove(CritterInfo info) {
        setColor();
        setStr();

        // always infects if an other is in front
        if (info.getFront() == Neighbor.OTHER) {
            return Action.INFECT;
        }

        // lines the lancers up on the right wall
        if (info.getDirection().toString() == "WEST" && info.getBack() == Neighbor.WALL && info.getLeft() == Neighbor.SAME) {
            return Action.INFECT;
        } else if (info.getDirection().toString() == "EAST" && info.getFront() == Neighbor.WALL) {
            return Action.RIGHT;
        } else if (info.getDirection().toString() == "SOUTH" && info.getLeft() == Neighbor.WALL && info.getFront() == Neighbor.EMPTY) {
            return Action.HOP;
        } else if (info.getDirection().toString() == "SOUTH" && info.getLeft() == Neighbor.WALL && info.getFront() == Neighbor.SAME) {
            return Action.RIGHT;
        } else if (info.getDirection().toString() == "EAST" && info.getFront() == Neighbor.EMPTY) {
            return Action.HOP;
        } else if (info.getDirection().toString() == "EAST" && info.getFront() == Neighbor.SAME) {
            return Action.LEFT;
        }

        // lines the lancers up on the top wall
        if (info.getDirection().toString() == "SOUTH" && info.getBack() == Neighbor.WALL && info.getLeft() == Neighbor.SAME) {
            return Action.INFECT;
        } else if (info.getDirection().toString() == "NORTH" && info.getRight() == Neighbor.SAME && info.getFront() == Neighbor.WALL) {
            return Action.LEFT;
        } else if (info.getDirection().toString() == "NORTH" && info.getRight() == Neighbor.SAME && info.getFront() == Neighbor.SAME) {
            return Action.LEFT;
        } else if (info.getDirection().toString() == "WEST" && info.getRight() == Neighbor.WALL && info.getBack() == Neighbor.SAME) {
            return Action.LEFT;
        } else if (info.getDirection().toString() == "WEST" && info.getRight() == Neighbor.SAME) {
            return Action.HOP;
        } else if (info.getDirection().toString() == "WEST" && info.getRight() == Neighbor.EMPTY && info.getBack() == Neighbor.SAME) {
            return Action.RIGHT;
        } else if (info.getDirection().toString() == "NORTH" && info.getRight() == Neighbor.SAME) {
            return Action.HOP;
        }

        // moves the lancers towards the right wall
        if (info.getDirection().toString() == "NORTH") {
            return Action.RIGHT;
        } else if (info.getDirection().toString() == "SOUTH") {
            return Action.LEFT;
        } else if (info.getDirection().toString() == "WEST") {
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