import java.awt.*;

//Eagle color should be R, R, B, B, R, R, B, B
//Eagle string should be <>, ><, <>, ><, ><, <>, ><, ><, ><, <>, ><, ><, ><, ><
public class Eagle extends Critter {
    private Color color;
    private String str;
    private int colorCount;
    private int strCount;
    private int divideBy;

    public Eagle(){
        colorCount = 0;
        strCount = 0;
        divideBy = 2;
        updateColor();
        updateString();
    }

    private void updateColor(){
        if(colorCount % 4 <= 1) {
            color = Color.RED;
        }else{
            color = Color.BLUE;
        }
        colorCount = (colorCount + 1) % 4;
    }

    private void updateString(){
        int r = strCount % divideBy;
        if(r == 0){
            str = "<>";
        }else{
            str = "><";
        }
        strCount = (strCount + 1) % divideBy;
        if(strCount == 0){
            divideBy++;
        }
    }

    @Override
    public Action getMove(CritterInfo info) {
        updateString();
        updateColor();
        Critter.Neighbor n = info.getFront(); //get my neighbor
        if( n == Neighbor.EMPTY) { //try to hop whenever possible
            return Action.HOP;
        } else if( n == Neighbor.WALL) { //if there is a wall turn right
            return Action.RIGHT;
        } else { //all other cases try to infect
            return Action.INFECT;
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