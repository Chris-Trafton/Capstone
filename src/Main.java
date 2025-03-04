import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class Main {
    //TODO://///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Global Variables
    private static MouseTrackerPanel board;
    private static String ip;
    private static BufferedImage tile;
    private static BufferedImage tile_small;
    private static BufferedImage flat_tile;
    private static BufferedImage flat_tile_small;
    private static BufferedImage forest_tile;
    private static BufferedImage forest_tile_small;
    private static BufferedImage mount_tile;
    private static BufferedImage mount_tile_small;
    private static BufferedImage desert_tile;
    private static BufferedImage desert_tile_small;
    private static BufferedImage player_flat_tile;
    private static BufferedImage enemy_flat_tile;
    private static BufferedImage player_flat_tile_small;
    private static BufferedImage enemy_flat_tile_small;
    private static BufferedImage player_forest_tile;
    private static BufferedImage enemy_forest_tile;
    private static BufferedImage player_forest_tile_small;
    private static BufferedImage enemy_forest_tile_small;
    private static BufferedImage player_mount_tile;
    private static BufferedImage enemy_mount_tile;
    private static BufferedImage player_mount_tile_small;
    private static BufferedImage enemy_mount_tile_small;
    private static BufferedImage player_desert_tile;
    private static BufferedImage enemy_desert_tile;
    private static BufferedImage player_desert_tile_small;
    private static BufferedImage enemy_desert_tile_small;
    private static Vector<Tile> tiles;
    private static double twoPi;
    private static double tileFixX;
    private static double tileFixY;
    private static boolean mousePressed;
    private enum biome {Flatland,Forest,Mountain,Desert};
    private static Vector<Integer> biomeCounts;
    private static int numTiles;
    private static Boolean endgame;
    private static int actionsTaken;
    private static boolean hosting;
    private static boolean joined;
    private static BufferedImage background;
    private static boolean background_drawn;
    private static BufferedImage scrollBackground;
    private static int XOFFSET;
    private static int YOFFSET;
    private static int WINWIDTH;
    private static int WINHEIGHT;
    private static JFrame appFrame;
    private static JLabel metalLabel = new JLabel();
    private static JLabel woodLabel = new JLabel();
    private static JLabel foodLabel = new JLabel();
    private static JLabel laborLabel = new JLabel();
    private static JLabel educationLabel = new JLabel();
    private static JLabel actionLabel = new JLabel();
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    private static String[] nations = new String[]{"Stark", "Lannister", "Targaryen"};
    private static String currentNation;
    private static Nation Stark = new Nation();
    private static Nation Lannister = new Nation();
    private static Nation Targaryen = new Nation();
    private static Color colorBackground = Color.decode("#dec590");
    private static Color colorButton = Color.decode("#bda46f");
    private static Vector<MilitaryUnit> movingUnits = new Vector<>();
    private static int maxActions = 10;
    private static boolean attacking = false;
    private static Tile pathBeginning;
    private static Vector<Integer> startingResources = new Vector<>();

    //TODO://///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Custom Classes
    private static class ImageObject {
        protected double x;
        protected double y;
        protected double xwidth;
        protected double yheight;
        protected double angle;
        protected double internalangle;
        protected Vector<Double> coords;
        protected Vector<Double> triangles;
        protected double comX;
        protected double comY;
        protected boolean mouseHover;

        public ImageObject() {}
        public ImageObject(double xinput, double yinput, double xwidthinput, double yheightinput, double angleinput) {
            x = xinput;
            y = yinput;
            xwidth = xwidthinput;
            yheight = yheightinput;
            angle = angleinput;
            internalangle = 0.0;
            coords = new Vector<Double>();
            mouseHover = false;
        }
        public double getX() {
            return x;
        }
        public double getY() {
            return y;
        }
        public double getWidth() {
            return xwidth;
        }
        public double getHeight() {
            return yheight;
        }
        public double getAngle() {
            return angle;
        }
        public double getInternalangle() {
            return internalangle;
        }
        public void setAngle(double angleinput){
            angle = angleinput;
        }
        public void setInternalangle(double internalangleinput) {
            internalangle = internalangleinput;
        }
        public Vector<Double> getCoords() {
            return coords;
        }
        public void setCoords(Vector<Double> coordsinput) {
            coords =coordsinput;
            generateTriangles();
            //printTriangles();
        }
        public void generateTriangles() {
            triangles = new Vector<Double>();
            comX = getComX();
            comY = getComY();
            for (int i = 0; i < coords.size(); i = i + 2) {
                triangles.addElement(coords.elementAt(i));
                triangles.addElement(coords.elementAt(i+1));
                triangles.addElement(coords.elementAt((i+2) % coords.size()));
                triangles.addElement(coords.elementAt((i+3) % coords.size()));
                triangles.addElement(comX);
                triangles.addElement(comY);
            }
        }
        public void printTrianlges() {
            for (int i = 0; i < triangles.size(); i = i + 6) {
                System.out.print("p0x: " + triangles.elementAt(i) + ", p0y: " + triangles.elementAt(i+1));
                System.out.print(" p1x: " + triangles.elementAt(i+2) + ", p1y: " + triangles.elementAt(i+3));
                System.out.print(" p2x: " + triangles.elementAt(i+4) + ", p2y: " + triangles.elementAt(i+5));
            }
        }
        public double getComX() {
            double ret = 0;
            if (coords.size() > 0) {
                for (int i = 0;  i < coords.size(); i = i+2) {
                    ret = ret + coords.elementAt(i);
                }
                ret = ret / (coords.size() / 2.0);
            }
            return ret;
        }
        public double getComY() {
            double ret = 0;
            if (coords.size() > 0) {
                for (int i = 1; i < coords.size(); i = i+2) {
                    ret = ret + coords.elementAt(i);
                }
                ret = ret / (coords.size() / 2.0);
            }
            return ret;
        }
        public void move(double xinput, double yinput) {
            x = x + xinput;
            y = y + yinput;
        }
        public void moveto(double xinput, double yinput) {
            x = xinput;
            y = yinput;
        }
        public void rotate(double angleinput) {
            angle = angle + angleinput;
            while (angle > twoPi) {
                angle = angle - twoPi;
            }
            while (angle < 0) {
                angle = angle + twoPi;
            }
        }
        public String toString() {
            return "[" + x + "," + y + "]" + "  [" + xwidth + "," + yheight + "]";
        }
        public void screenContain() {
            if (x + xwidth > appFrame.getWidth()) {
                tileFixX = -((x+xwidth)-appFrame.getWidth());
            }
            if (x < 0) {
                tileFixX = -x;
            }
            if (y > appFrame.getHeight() - YOFFSET - 25) {
                tileFixY = -(y- appFrame.getHeight()+YOFFSET+25);
            }
            if (y - yheight < 0) {
                tileFixY = -(y-yheight);
            }
            if (tileFixX != 0 || tileFixY != 0) {
                move(tileFixX,tileFixY);
            }
        }
    }

    public static class Tile extends ImageObject {
        private String owner;
        private Vector<Tile> neighbors;
        private Vector<Integer> resourceRates;
        private biome myBiome;
        private boolean waterSide;
        private Vector<MilitaryUnit> occupyingUnits;
        private int mine;
        private int forge;
        private int lumberMill;
        private int deforestation;
        private int farm;
        private int plantation;
        private int school;
        private int college;

        public Tile(double xinput, double yinput, double width, double height, double angle) {
            super(xinput,yinput,width,height,angle);
            neighbors = new Vector<>();
            resourceRates = new Vector<>();
            occupyingUnits = new Vector<>();
            mine = 0;
            forge = 0;
            lumberMill = 0;
            deforestation = 0;
            farm = 0;
            plantation = 0;
            school = 0;
            college = 0;
        }

        public Tile(String owner, double xinput, double yinput, double width, double height, double angle, String myBiome, int mine, int forge, int lumberMill, int deforestation, int farm, int plantation, int school, int college, String[] military) {
            super(xinput,yinput,width,height,angle);
            neighbors = new Vector<>();
            resourceRates = new Vector<>();
            occupyingUnits = new Vector<>();
            this.owner = owner;
            this.myBiome = biome.valueOf(myBiome);
            if (this.myBiome == biome.Flatland) biomeCounts.set(0,biomeCounts.get(0)+1);
            if (this.myBiome == biome.Flatland) biomeCounts.set(1,biomeCounts.get(1)+1);
            if (this.myBiome == biome.Flatland) biomeCounts.set(2,biomeCounts.get(2)+1);
            if (this.myBiome == biome.Flatland) biomeCounts.set(3,biomeCounts.get(3)+1);
            this.mine = mine;
            this.forge = forge;
            this.lumberMill = lumberMill;
            this.deforestation = deforestation;
            this.farm = farm;
            this.plantation = plantation;
            this.school = school;
            this.college = college;
            for (int i = 0; i <= military.length-1; i+=2) {
                System.out.println(military[i] + " " + military[i+1]);
                this.setMilitary(military[i],1,military[i+1]);
            }
        }
        public String toString() {
            return owner + "- Labor: " + resourceRates.get(0) + " Wood: " + resourceRates.get(1) + " Metal: " + resourceRates.get(2) + " Food: " + resourceRates.get(3) + " Research: " + resourceRates.get(4);
        }
        public boolean equals(Tile a) {
            return x == a.x && y == a.y;
        }
        public void setOwner(int index) {
            if (index == 0) {
                owner = "Stark";
            } else if (index == numTiles - 1) {
                owner = "Lannister";
            } else {
                owner = "";
            }
        }
        public biome getBiome() {
            return myBiome;
        }
        public void isWaterSide() { waterSide = (neighbors.size() < 6); }
        public Point2D.Double getCenter() {
            return new Point2D.Double(x + (xwidth*0.5),y+(yheight*1.5));
        }
        public void setResourceRates() {
            resourceRates.clear();
            int labor, wood, metal, food, research;
            Random rand = new Random(System.currentTimeMillis() + (int)x + (int)y);
            if (myBiome == biome.Flatland) {
                labor = rand.nextInt(20) + 20;
                wood = rand.nextInt(5) + 5;
                metal = rand.nextInt(5);
                food = rand.nextInt(10) + 25;
                research = rand.nextInt(5);
            } else if (myBiome == biome.Forest) {
                labor = rand.nextInt(5);
                wood = rand.nextInt(20) + 30;
                metal = rand.nextInt(5);
                food = rand.nextInt(10) + 10;
                research = rand.nextInt(5) + 10;
            } else if (myBiome == biome.Mountain) {
                labor = rand.nextInt(5);
                wood = rand.nextInt(10);
                metal = rand.nextInt(20) + 40;
                food = rand.nextInt(5) + 5;
                research = rand.nextInt(5) + 5;
            } else {
                labor = rand.nextInt(10) + 20;
                wood = rand.nextInt(5) + 5;
                metal = rand.nextInt(5);
                food = rand.nextInt(5);
                research = rand.nextInt(10) + 25;
            }
            if (waterSide) {
                labor += rand.nextInt(10);
                food += rand.nextInt(10);
            }
            wood += 5 * lumberMill + 10 * deforestation;
            metal += 5 * mine + 10 * forge;
            food += 5 * farm + 10 * plantation;
            research += 5 * school  + 10 * college;
            resourceRates.add(labor);
            resourceRates.add(wood);
            resourceRates.add(metal);
            resourceRates.add(food);
            resourceRates.add(research);
        }
        public void generateBiome() {
            Vector<Integer> neighborBiomes = countNeighborBiomes();
            int flat_count = neighborBiomes.get(0) * 12;
            if (biomeCounts.get(0) > numTiles / 3) {
                flat_count = -25;
            }
            int forest_count = neighborBiomes.get(1) * 12;
            if (biomeCounts.get(1) > numTiles / 3) {
                forest_count = -25;
            }
            int mountain_count = neighborBiomes.get(2) * 12;
            if (biomeCounts.get(2) > numTiles / 3) {
                mountain_count = -25;
            }
            int desert_count = neighborBiomes.get(3) * 12;
            if (biomeCounts.get(3) > numTiles / 3) {
                desert_count = -25;
            }
            int total = 100 + flat_count + forest_count + mountain_count + desert_count;
            Random rand = new Random(System.currentTimeMillis());
            int selector = rand.nextInt(total) + 1;
            if (selector <= 25 + flat_count) {
                myBiome = biome.Flatland;
                biomeCounts.set(0,biomeCounts.get(0)+1);
            } else if (selector <= 50 + flat_count + forest_count) {
                myBiome = biome.Forest;
                biomeCounts.set(1,biomeCounts.get(1)+1);
            } else if (selector <= 75 + flat_count + forest_count + mountain_count) {
                myBiome = biome.Mountain;
                biomeCounts.set(2,biomeCounts.get(2)+1);
            } else if (selector <= 100 + flat_count + forest_count + mountain_count + desert_count) {
                myBiome = biome.Desert;
                biomeCounts.set(3,biomeCounts.get(3)+1);
            } else {
                throw new IllegalArgumentException();
            }
        }
        private Vector<Integer> countNeighborBiomes() {
            Vector<Integer> biomeCount = new Vector<>();
            biomeCount.add(0);
            biomeCount.add(0);
            biomeCount.add(0);
            biomeCount.add(0);
            for (int i = 0; i < neighbors.size(); i++) {
                biome neighborBiome = neighbors.get(i).getBiome();
                if (neighborBiome == biome.Flatland) {
                    biomeCount.set(0,biomeCount.get(0)+1);
                } else if (neighborBiome == biome.Forest) {
                    biomeCount.set(1,biomeCount.get(1)+1);
                } else if (neighborBiome == biome.Mountain) {
                    biomeCount.set(2,biomeCount.get(2)+1);
                } else if (neighborBiome == biome.Desert) {
                    biomeCount.set(3,biomeCount.get(3)+1);
                }
            }
            return biomeCount;
        }
        public void findNeighbors(Vector<Tile> tiles) {
            Point2D.Double up = new Point2D.Double(x + (xwidth * 0.5),y - (yheight * 1.5));
            Point2D.Double upRight = new Point2D.Double(x + (xwidth * 1.25),y - yheight);
            Point2D.Double downRight = new Point2D.Double(x + (xwidth * 1.25),y);
            Point2D.Double down = new Point2D.Double(x + (xwidth * 0.5),y + (yheight * 0.5));
            Point2D.Double downLeft = new Point2D.Double(x- (xwidth * 0.25),y);
            Point2D.Double upLeft = new Point2D.Double(x - (xwidth * 0.25),y - yheight);
            for (int i = 0; i < tiles.size(); i++) {
                Tile current = tiles.get(i);
                if (!(current.getX() == x && current.getY() == y)) {
                    if(isInside(up.getX(),up.getY(),current.getX(),current.getY(),current.getX() + current.getWidth(),current.getY() - current.getHeight())) {
                        neighbors.add(current);
                    }
                    if(isInside(upRight.getX(),upRight.getY(),current.getX(),current.getY(),current.getX() + current.getWidth(),current.getY() - current.getHeight())) {
                        neighbors.add(current);
                    }
                    if(isInside(downRight.getX(),downRight.getY(),current.getX(),current.getY(),current.getX() + current.getWidth(),current.getY() - current.getHeight())) {
                        neighbors.add(current);
                    }
                    if(isInside(downLeft.getX(),downLeft.getY(),current.getX(),current.getY(),current.getX() + current.getWidth(),current.getY() - current.getHeight())) {
                        neighbors.add(current);
                    }
                    if(isInside(down.getX(),down.getY(),current.getX(),current.getY(),current.getX() + current.getWidth(),current.getY() - current.getHeight())) {
                        neighbors.add(current);
                    }
                    if(isInside(upLeft.getX(),upLeft.getY(),current.getX(),current.getY(),current.getX() + current.getWidth(),current.getY() - current.getHeight())) {
                        neighbors.add(current);
                    }
                }
            }
        }

        public void setMilitary(String unit, int change, String nation) {
            if (nation.equals("Stark")) {
                if (change < 0) {
                    for (int i = 0; i < occupyingUnits.size(); i++) {
                        if (occupyingUnits.get(i).unitName.equals(unit) && occupyingUnits.get(i).owner.equals("Stark")) {
                            occupyingUnits.remove(i);
                        }
                    }
                } else {
                    occupyingUnits.add(0, new MilitaryUnit(nation, unit));
                    System.out.println("New occupying unit: " + occupyingUnits.get(0));
                    Stark.setMilitary(unit, change);
                }
            } else if (nation.equals("Lannister")) {
                if (change < 0) {
                    for (int i = 0; i < occupyingUnits.size(); i++) {
                        if (occupyingUnits.get(i).unitName.equals(unit) && occupyingUnits.get(i).owner.equals("Lannister")) {
                            occupyingUnits.remove(i);
                        }
                    }
                } else {
                    occupyingUnits.add(0, new MilitaryUnit(nation, unit));
                    System.out.println("New occupying unit: " + occupyingUnits.get(0));
                    Lannister.setMilitary(unit, change);
                }
            } else if (nation.equals("Targaryen")) {
                if (change < 0) {
                    for (int i = 0; i < occupyingUnits.size(); i++) {
                        if (occupyingUnits.get(i).unitName.equals(unit) && occupyingUnits.get(i).owner.equals("Targaryen")) {
                            occupyingUnits.remove(i);
                        }
                    }
                } else {
                    occupyingUnits.add(0, new MilitaryUnit(nation, unit));
                    System.out.println("New occupying unit: " + occupyingUnits.get(0));
                    Targaryen.setMilitary(unit, change);
                }
            }
            backgroundDraw();
        }

        public void setBuilding(String unit, int change) {
            if (owner.equals("Stark")) {
                if (unit.equals("mine")) {
                    mine += change;
                } else if (unit.equals("forge")) {
                    forge += change;
                } else if (unit.equals("lumberMill")) {
                    lumberMill += change;
                } else if (unit.equals("deforestation")) {
                    deforestation += change;
                } else if (unit.equals("farm")) {
                    farm += change;
                } else if (unit.equals("plantation")) {
                    plantation += change;
                } else if (unit.equals("school")) {
                    school += change;
                } else if (unit.equals("college")) {
                    college += change;
                }
                Stark.setBuilding(unit, change);
            } else if (owner.equals("Lannister")) {
                if (unit.equals("mine")) {
                    mine += change;
                } else if (unit.equals("forge")) {
                    forge += change;
                } else if (unit.equals("lumberMill")) {
                    lumberMill += change;
                } else if (unit.equals("deforestation")) {
                    deforestation += change;
                } else if (unit.equals("farm")) {
                    farm += change;
                } else if (unit.equals("plantation")) {
                    plantation += change;
                } else if (unit.equals("school")) {
                    school += change;
                } else if (unit.equals("college")) {
                    college += change;
                }
                Lannister.setBuilding(unit, change);
            } else if (owner.equals("Targaryen")) {
                if (unit.equals("mine")) {
                    mine += change;
                } else if (unit.equals("forge")) {
                    forge += change;
                } else if (unit.equals("lumberMill")) {
                    lumberMill += change;
                } else if (unit.equals("deforestation")) {
                    deforestation += change;
                } else if (unit.equals("farm")) {
                    farm += change;
                } else if (unit.equals("plantation")) {
                    plantation += change;
                } else if (unit.equals("school")) {
                    school += change;
                } else if (unit.equals("college")) {
                    college += change;
                }
                Targaryen.setBuilding(unit, change);
            }
            backgroundDraw();
        }
        public void addResources() {
            if (owner.equals("Stark")) {
                Stark.setResource("labor",resourceRates.get(0));
                Stark.setResource("wood",resourceRates.get(1));
                Stark.setResource("metal",resourceRates.get(2));
                Stark.setResource("food",resourceRates.get(3));
                Stark.setResource("education",resourceRates.get(4));
            } else if (owner.equals("Lannister")) {
                Lannister.setResource("labor",resourceRates.get(0));
                Lannister.setResource("wood",resourceRates.get(1));
                Lannister.setResource("metal",resourceRates.get(2));
                Lannister.setResource("food",resourceRates.get(3));
                Lannister.setResource("education",resourceRates.get(4));
            } else if (owner.equals("Targaryen")) {
                Targaryen.setResource("labor",resourceRates.get(0));
                Targaryen.setResource("wood",resourceRates.get(1));
                Targaryen.setResource("metal",resourceRates.get(2));
                Targaryen.setResource("food",resourceRates.get(3));
                Targaryen.setResource("education",resourceRates.get(4));
            }
        }
    }

    private static class MilitaryUnit {
        private String owner;
        String unitName;
        private enum unitType{melee, range, siege};
        unitType type;
        private int health;
        private int melee;
        private int range;
        private int siege;

        public MilitaryUnit(String unitOwner, String unit) {
            owner = unitOwner;
            /*       Melee:              Range:            Siege:
            Tier 1 - Swordmen            Javelinmen        Siege Tower
            Tier 2 - Shieldmen           Javelinmen            Catapult
            Tier 3 - Spearmen            Crossbowmen       Ballista
            Tier 4 - Mounted Calvalry    Mounted Javelinmen    Trebuchet */
            if (unit.equals("Swordmen")) {
                unitName = "Swordmen";
                type = unitType.melee;
                health = 5;
                melee = 5;
                range = 5;
                siege = 0;
            } else if (unit.equals("Shieldmen")) {
                unitName = "Shieldmen";
                type = unitType.melee;
                health = 10;
                melee = 5;
                range = 15;
                siege = 0;
            } else if (unit.equals("Spearmen")) {
                unitName = "Spearmen";
                type = unitType.melee;
                health = 15;
                melee = 10;
                range = 20;
                siege = 0;
            } else if (unit.equals("Mounted Calvalry")) {
                unitName = "Mounted Calvalry";
                type = unitType.melee;
                health = 20;
                melee = 15;
                range = 25;
                siege = 0;
            } else if (unit.equals("Javelinmen")) {
                unitName = "Javelinmen";
                type = unitType.range;
                health = 5;
                melee = 0;
                range = 5;
                siege = 5;
            } else if (unit.equals("Archer")) {
                unitName = "Archer";
                type = unitType.range;
                health = 10;
                melee = 0;
                range = 5;
                siege = 15;
            } else if (unit.equals("Crossbowmen")) {
                unitName = "Crossbowmen";
                type = unitType.range;
                health = 15;
                melee = 0;
                range = 10;
                siege = 20;
            } else if (unit.equals("Mounted Javelinmen")) {
                unitName = "Mounted Javelinmen";
                type = unitType.range;
                health = 20;
                melee = 0;
                range = 15;
                siege = 25;
            } else if (unit.equals("Siege Tower")) {
                unitName = "Siege Tower";
                type = unitType.siege;
                health = 5;
                melee = 5;
                range = 0;
                siege = 5;
            } else if (unit.equals("Catapult")) {
                unitName = "Catapult";
                type = unitType.siege;
                health = 10;
                melee = 15;
                range = 0;
                siege = 5;
            } else if (unit.equals("Ballista")) {
                unitName = "Ballista";
                type = unitType.siege;
                health = 15;
                melee = 20;
                range = 0;
                siege = 10;
            } else if (unit.equals("Trebuchet")) {
                unitName = "Trebuchet";
                type = unitType.siege;
                health = 20;
                melee = 25;
                range = 0;
                siege = 15;
            }
        }
    }

    private static class Nation {
        private String nationName;
        private boolean currentStatus;
        private JTabbedPane inboxTabs = new JTabbedPane();
        private JTabbedPane sentTabs = new JTabbedPane();
        private JTabbedPane archivedTabs = new JTabbedPane();
        private int metal;
        private int wood;
        private int food;
        private int labor;
        private int education;
        private int swordmen;
        private int shieldmen;
        private int spearmen;
        private int mountedCalvalry;
        private int javelinmen;
        private int archer;
        private int crossbowmen;
        private int mountedArcher;
        private int siegeTower;
        private int catapult;
        private int ballista;
        private int trebuchet;
        private int mine;
        private int forge;
        private int lumberMill;
        private int deforestation;
        private int farm;
        private int plantation;
        private int school;
        private int college;
        private boolean mineResearched;
        private boolean forgeResearched;
        private boolean lumberMillResearched;
        private boolean deforestationResearched;
        private boolean farmResearched;
        private boolean plantationResearched;
        private boolean schoolResearched;
        private boolean collegeResearched;

        private boolean swordmenResearched;

        private boolean shieldmenResearched;
        private boolean spearmenResearched;
        private boolean mountedCalvalryResearched;
        private boolean javelinmenResearched;

        private boolean archerResearched;
        private boolean crossbowmenResearched;
        private boolean mountedArcherResearched;
        private boolean siegeTowerResearched;
        private boolean catapultResearched;
        private boolean ballistaResearched;
        private boolean trebuchetResearched;

        public Nation() {
            inboxTabs.setTabPlacement(JTabbedPane.LEFT);
            inboxTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            sentTabs.setTabPlacement(JTabbedPane.LEFT);
            sentTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            archivedTabs.setTabPlacement(JTabbedPane.LEFT);
            archivedTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            metal = 0;
            wood = 0;
            food = 0;
            labor = 0;
            education = 0;
            swordmen = 0;
            shieldmen = 0;
            spearmen = 0;
            mountedCalvalry = 0;
            javelinmen = 0;
            archer = 0;
            crossbowmen = 0;
            mountedArcher = 0;
            siegeTower = 0;
            catapult = 0;
            ballista = 0;
            trebuchet = 0;
            mine = 0;
            forge = 0;
            lumberMill = 0;
            deforestation = 0;
            farm = 0;
            plantation = 0;
            school = 0;
            college = 0;
            mineResearched = false;
            forgeResearched = false;
            lumberMillResearched = false;
            deforestationResearched = false;
            farmResearched = false;
            plantationResearched = false;
            schoolResearched = false;
            collegeResearched = false;
            swordmenResearched = true;
            shieldmenResearched = false;
            spearmenResearched = false;
            mountedCalvalryResearched = false;
            javelinmenResearched = true;
            archerResearched = false;
            crossbowmenResearched = false;
            mountedArcherResearched = false;
            siegeTowerResearched = true;
            catapultResearched = false;
            ballistaResearched = false;
            trebuchetResearched = false;
        }

        public void setActive(boolean status) {
            currentStatus = status;
        }

        public boolean isActive() {
            return currentStatus;
        }

        public void setResource(String unit, int change) {
            if (unit.equals("metal")) {
                metal += change;
            } else if (unit.equals("wood")) {
                wood += change;
            } else if (unit.equals("food")) {
                food += change;
            } else if (unit.equals("labor")) {
                labor += change;
            } else if (unit.equals("education")) {
                education += change;
            }
            backgroundDraw();
        }

        public void setMilitary(String unit, int change) {
            if (change > 0) {
                if (unit.equals("Swordmen")) {
                    swordmen += change;
                    metal -= 10 * change;
                    food -= 10 * change;
                    labor -= 10 * change;
                } else if (unit.equals("Shieldmen")) {
                    shieldmen += change;
                    metal -= 10 * change;
                    food -= 10 * change;
                    labor -= 20 * change;
                } else if (unit.equals("Spearmen")) {
                    spearmen += change;
                    metal -= 20 * change;
                    food -= 10 * change;
                    labor -= 10 * change;
                } else if (unit.equals("Mounted Cavalry")) {
                    mountedCalvalry += change;
                    metal -= 20 * change;
                    food -= 10 * change;
                    labor -= 20 * change;
                } else if (unit.equals("Javelinmen")) {
                    javelinmen += change;
                    metal -= 10 * change;
                    food -= 10 * change;
                    labor -= 10 * change;
                } else if (unit.equals("Archer")) {
                    archer += change;
                    food -= 10 * change;
                    labor -= 10 * change;
                } else if (unit.equals("Crossbowmen")) {
                    crossbowmen += change;
                    metal -= 10 * change;
                    wood -= 20 * change;
                    food -= 10 * change;
                    labor -= 10 * change;
                } else if (unit.equals("Mounted Archer")) {
                    mountedArcher += change;
                    metal -= 20 * change;
                    wood -= 30 * change;
                    food -= 10 * change;
                    labor -= 10 * change;
                } else if (unit.equals("Siege Tower")) {
                    siegeTower += change;
                    food -= 50 * change;
                    labor -= 25 * change;
                } else if (unit.equals("Catapult")) {
                    catapult += change;
                    metal -= 10 * change;
                    wood -= 100 * change;
                    food -= 10 * change;
                    labor -= 50 * change;
                } else if (unit.equals("Ballista")) {
                    ballista += change;
                    food -= 100 * change;
                    labor -= 100 * change;
                }
                else if (unit.equals("Trebuchet")) {
                    trebuchet += change;
                    food -= 100 * change;
                    labor -= 100 * change;
                }
            } else if (change < 0) {
                if (unit.equals("Swordmen")) {
                    swordmen += change;
                } else if (unit.equals("Shieldmen")) {
                    shieldmen += change;
                } else if (unit.equals("Spearmen")) {
                    spearmen += change;
                } else if (unit.equals("Mounted Cavalry")) {
                    mountedCalvalry += change;
                } else if (unit.equals("Javelinmen")) {
                    javelinmen += change;
                } else if (unit.equals("Archer")) {
                    archer += change;
                } else if (unit.equals("Crossbowmen")) {
                    crossbowmen += change;
                } else if (unit.equals("Mounted Archer")) {
                    mountedArcher += change;
                } else if (unit.equals("Siege Tower")) {
                    siegeTower += change;
                } else if (unit.equals("Catapult")) {
                    catapult += change;
                } else if (unit.equals("Ballista")) {
                    ballista += change;
                } else if (unit.equals("Trebuchet")) {
                    trebuchet += change;
                }
            }
            backgroundDraw();
        }

        public void setBuilding(String unit, int change) {
            if (unit.equals("mine")) {
                mine += change;
                metal -= 10 * change;
                wood -= 50 * change;
                food -= 20 * change;
                labor -= 20 * change;
            } else if (unit.equals("forge")) {
                forge += change;
                metal -= 50 * change;
                wood -= 40 * change;
                food -= 20 * change;
                labor -= 40 * change;
            } else if (unit.equals("lumberMill")) {
                lumberMill += change;
                metal -= 50 * change;
                wood -= 10 * change;
                food -= 20 * change;
                labor -= 20 * change;
            } else if (unit.equals("deforestation")) {
                deforestation += change;
                metal -= 20 * change;
                wood -= 40 * change;
                food -= 50 * change;
                labor -= 40 * change;
            } else if (unit.equals("farm")) {
                farm += change;
                metal -= 20 * change;
                wood -= 40 * change;
                food -= 20 * change;
                labor -= 20 * change;
            } else if (unit.equals("plantation")) {
                plantation += change;
                metal -= 40 * change;
                wood -= 50 * change;
                food -= 20 * change;
                labor -= 40 * change;
            } else if (unit.equals("school")) {
                school += change;
                metal -= 40 * change;
                wood -= 20 * change;
                food -= 20 * change;
                labor -= 20 * change;
            } else if (unit.equals("college")) {
                college += change;
                metal -= 40 * change;
                wood -= 40 * change;
                food -= 30 * change;
                labor -= 40 * change;
            }
            backgroundDraw();
        }

        public void setResearch(String unit, boolean change) {
            if (unit.equals("mine")) {
                mineResearched = change;
                education -= 50;
                labor -= 10;
            } else if (unit.equals("forge")) {
                forgeResearched = change;
                education -= 75;
                labor -= 25;
            } else if (unit.equals("lumberMill")) {
                lumberMillResearched = change;
                education -= 50;
                labor -= 10;
            } else if (unit.equals("deforestation")) {
                deforestationResearched = change;
                education -= 75;
                labor -= 25;
            } else if (unit.equals("farm")) {
                farmResearched = change;
                education -= 50;
                labor -= 10;
            } else if (unit.equals("plantation")) {
                plantationResearched = change;
                education -= 75;
                labor -= 25;
            } else if (unit.equals("school")) {
                schoolResearched = change;
                education -= 50;
                labor -= 10;
            } else if (unit.equals("college")) {
                collegeResearched = change;
                education -= 75;
                labor -= 25;
            } else if (unit.equals("swordmen")) {
                spearmenResearched = change;
                education -= 50;
                labor -= 10;
            } else if (unit.equals("shieldmen")) {
                spearmenResearched = change;
                education -= 50;
                labor -= 10;
            } else if (unit.equals("spearmen")) {
                spearmenResearched = change;
                education -= 75;
                labor -= 25;
            } else if (unit.equals("mountedCalvalry")) {
                mountedCalvalryResearched = change;
                education -= 100;
                labor -= 50;
            } else if (unit.equals("javelinmen")) {
                javelinmenResearched = change;
                education -= 50;
                labor -= 10;
            } else if (unit.equals("archer")) {
                archerResearched = change;
                education -= 50;
                labor -= 10;
            } else if (unit.equals("crossbowmen")) {
                crossbowmenResearched = change;
                education -= 75;
                labor -= 25;
            } else if (unit.equals("mountedArcher")) {
                mountedArcherResearched = change;
                education -= 100;
                labor -= 50;
            } else if (unit.equals("siegeTower")) {
                siegeTowerResearched = change;
                education -= 50;
                labor -= 10;
            } else if (unit.equals("catapult")) {
                catapultResearched = change;
                education -= 50;
                labor -= 10;
            } else if (unit.equals("ballista")) {
                education -= 75;
                labor -= 25;
            } else if (unit.equals("trebuchet")) {
                trebuchetResearched = change;
                education -= 100;
                labor -= 50;
            }
            backgroundDraw();
        }
    }

    //TODO://///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Open Menus
    private static void openStartScreen() {
        JFrame frame = new JFrame("Start Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(300, 200);

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenSetupMenu();
                frame.dispose();
            }
        });

        JButton joinGameButton = new JButton("Join Game");
        joinGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenJoinMenu();
                frame.dispose();
            }
        });

        JButton loadGameButton = new JButton("Load Game");
        loadGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenLoadMenu();
                frame.dispose();
            }
        });
        JButton quitGameButton = new JButton("Quit Game");
        quitGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                endgame = true;
                frame.setVisible(false);
                appFrame.setVisible(false);
                frame.dispose();
                appFrame.dispose();
            }
        });

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(4, 1));
        jPanel.setBackground(colorBackground);
        newGameButton.setBackground(colorButton);
        joinGameButton.setBackground(colorButton);
        loadGameButton.setBackground(colorButton);
        quitGameButton.setBackground(colorButton);
        frame.setBackground(colorBackground);
        jPanel.add(newGameButton);
        jPanel.add(joinGameButton);
        jPanel.add(loadGameButton);
        jPanel.add(quitGameButton);
        frame.add(jPanel);
        frame.setSize(700, 300);
        frame.setVisible(true);
    }

    public static void OpenSetupMenu() {
        JFrame frame = new JFrame("Setup Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(300, 200);

        JLabel nationL = new JLabel("Nation:");
        JComboBox nationComboBox = new JComboBox<>(nations);
        JLabel metalL = new JLabel("Metal:");
        JTextField metalT = new JTextField(8);
        JLabel woodL = new JLabel("Wood:");
        JTextField woodT = new JTextField(8);
        JLabel foodL = new JLabel("Food:");
        JTextField foodT = new JTextField(8);
        JLabel laborL = new JLabel("Labor:");
        JTextField laborT = new JTextField(8);
        JLabel educationL = new JLabel("Education:");
        JTextField educationT = new JTextField(8);
        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                appFrame.setVisible(true);
                board.revalidate();
                board.repaint();
                endgame = true;
                tileFixX = 0;
                tileFixY = 0;
                biomeCounts = new Vector<>();
                numTiles = 100;
                biomeCounts.add(0);
                biomeCounts.add(0);
                biomeCounts.add(0);
                biomeCounts.add(0);
                tiles = new Vector<Tile>();
                tiles = CreateBoard(numTiles);
                tileFixX = 0;
                tileFixY = 0;
                background_drawn = false;
                currentNation = (String) nationComboBox.getSelectedItem();
                actionsTaken = 0;
                if (currentNation.equals("Stark")) {
                    Stark.setActive(true);
                    Lannister.setActive(false);
                    Targaryen.setActive(false);
                } else if (currentNation.equals("Lannister")) {
                    Stark.setActive(false);
                    Lannister.setActive(true);
                    Targaryen.setActive(false);
                } else if (currentNation.equals("Targaryen")) {
                    Stark.setActive(false);
                    Lannister.setActive(false);
                    Targaryen.setActive(true);
                }
                Stark.setResource("metal", Integer.parseInt(metalT.getText()));
                Stark.setResource("wood", Integer.parseInt(woodT.getText()));
                Stark.setResource("food", Integer.parseInt(foodT.getText()));
                Stark.setResource("labor", Integer.parseInt(laborT.getText()));
                Stark.setResource("education", Integer.parseInt(educationT.getText()));
                Lannister.setResource("metal", Integer.parseInt(metalT.getText()));
                Lannister.setResource("wood", Integer.parseInt(woodT.getText()));
                Lannister.setResource("food", Integer.parseInt(foodT.getText()));
                Lannister.setResource("labor", Integer.parseInt(laborT.getText()));
                Lannister.setResource("education", Integer.parseInt(educationT.getText()));
                Targaryen.setResource("metal", Integer.parseInt(metalT.getText()));
                Targaryen.setResource("wood", Integer.parseInt(woodT.getText()));
                Targaryen.setResource("food", Integer.parseInt(foodT.getText()));
                Targaryen.setResource("labor", Integer.parseInt(laborT.getText()));
                Targaryen.setResource("education", Integer.parseInt(educationT.getText()));
                startingResources.add(Integer.parseInt(metalT.getText()));
                startingResources.add(Integer.parseInt(woodT.getText()));
                startingResources.add(Integer.parseInt(foodT.getText()));
                startingResources.add(Integer.parseInt(laborT.getText()));
                startingResources.add(Integer.parseInt(educationT.getText()));
                endgame = false;
                Thread t1 = new Thread(new Animate());
                Thread t2 = new Thread(new TileMover());
                Thread t3 = new Thread(new MouseOverChecker());
                Thread t4 = new Thread(new ServerRunner());
                Thread t5 = new Thread(new ClientRunner());
                t1.start();
                t2.start();
                t3.start();
                t4.start();
                t5.start();
                frame.dispose();
            }
        });
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                openStartScreen();
            }
        });

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(7, 2));
        jPanel.setBackground(colorBackground);
        nationComboBox.setBackground(colorButton);
        metalT.setBackground(colorButton);
        woodT.setBackground(colorButton);
        foodT.setBackground(colorButton);
        laborT.setBackground(colorButton);
        educationT.setBackground(colorButton);
        backButton.setBackground(colorButton);
        startButton.setBackground(colorButton);
        frame.setBackground(colorBackground);
        jPanel.add(nationL);
        jPanel.add(nationComboBox);
        jPanel.add(metalL);
        jPanel.add(metalT);
        jPanel.add(woodL);
        jPanel.add(woodT);
        jPanel.add(foodL);
        jPanel.add(foodT);
        jPanel.add(laborL);
        jPanel.add(laborT);
        jPanel.add(educationL);
        jPanel.add(educationT);
        jPanel.add(backButton);
        jPanel.add(startButton);
        frame.add(jPanel);
        frame.setSize(700, 300);
        frame.setVisible(true);
    }

    public static void OpenJoinMenu() {
        JFrame frame = new JFrame("Join Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(300, 200);

        JLabel nationL = new JLabel("Nation:");
        JComboBox nationComboBox = new JComboBox<>(nations);
        JLabel ipL = new JLabel("IP:");
        JTextField ipT = new JTextField(24);
        JLabel metalL = new JLabel("Metal:");
        JTextField metalT = new JTextField(8);
        JLabel woodL = new JLabel("Wood:");
        JTextField woodT = new JTextField(8);
        JLabel foodL = new JLabel("Food:");
        JTextField foodT = new JTextField(8);
        JLabel laborL = new JLabel("Labor:");
        JTextField laborT = new JTextField(8);
        JLabel educationL = new JLabel("Education:");
        JTextField educationT = new JTextField(8);
        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                appFrame.setVisible(true);
                board.revalidate();
                board.repaint();
                endgame = true;
                tileFixX = 0;
                tileFixY = 0;
                biomeCounts = new Vector<>();
                numTiles = 100;
                biomeCounts.add(0);
                biomeCounts.add(0);
                biomeCounts.add(0);
                biomeCounts.add(0);
                tiles = new Vector<Tile>();
                tileFixX = 0;
                tileFixY = 0;
                background_drawn = false;
                currentNation = (String) nationComboBox.getSelectedItem();
                ip = ipT.getText();
                actionsTaken = 11;
                if (currentNation.equals("Stark")) {
                    Stark.setActive(true);
                    Lannister.setActive(false);
                    Targaryen.setActive(false);
//                    Stark.setResource("metal", Integer.parseInt(metalT.getText()));
//                    Stark.setResource("wood", Integer.parseInt(woodT.getText()));
//                    Stark.setResource("food", Integer.parseInt(foodT.getText()));
//                    Stark.setResource("labor", Integer.parseInt(laborT.getText()));
//                    Stark.setResource("education", Integer.parseInt(educationT.getText()));
                } else if (currentNation.equals("Lannister")) {
                    Stark.setActive(false);
                    Lannister.setActive(true);
                    Targaryen.setActive(false);
//                    Lannister.setResource("metal", Integer.parseInt(metalT.getText()));
//                    Lannister.setResource("wood", Integer.parseInt(woodT.getText()));
//                    Lannister.setResource("food", Integer.parseInt(foodT.getText()));
//                    Lannister.setResource("labor", Integer.parseInt(laborT.getText()));
//                    Lannister.setResource("education", Integer.parseInt(educationT.getText()));
                } else if (currentNation.equals("Targaryen")) {
                    Stark.setActive(false);
                    Lannister.setActive(false);
                    Targaryen.setActive(true);
//                    Targaryen.setResource("metal", Integer.parseInt(metalT.getText()));
//                    Targaryen.setResource("wood", Integer.parseInt(woodT.getText()));
//                    Targaryen.setResource("food", Integer.parseInt(foodT.getText()));
//                    Targaryen.setResource("labor", Integer.parseInt(laborT.getText()));
//                    Targaryen.setResource("education", Integer.parseInt(educationT.getText()));
                }
                endgame = false;
                Thread t1 = new Thread(new Animate());
                Thread t2 = new Thread(new TileMover());
                Thread t3 = new Thread(new MouseOverChecker());
                Thread t4 = new Thread(new ClientRunner());
                t4.start();
                while (tiles.isEmpty()) {
//                    System.out.println("Loading...");
                }
                t1.start();
                t2.start();
                t3.start();
                frame.dispose();
            }
        });
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                openStartScreen();
            }
        });

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(3, 2));
        jPanel.setBackground(colorBackground);
        ipT.setBackground(colorButton);
        nationComboBox.setBackground(colorButton);
        metalT.setBackground(colorButton);
        woodT.setBackground(colorButton);
        foodT.setBackground(colorButton);
        laborT.setBackground(colorButton);
        educationT.setBackground(colorButton);
        backButton.setBackground(colorButton);
        startButton.setBackground(colorButton);
        frame.setBackground(colorBackground);
        jPanel.add(ipL);
        jPanel.add(ipT);
        jPanel.add(nationL);
        jPanel.add(nationComboBox);
//        jPanel.add(metalL);
//        jPanel.add(metalT);
//        jPanel.add(woodL);
//        jPanel.add(woodT);
//        jPanel.add(foodL);
//        jPanel.add(foodT);
//        jPanel.add(laborL);
//        jPanel.add(laborT);
//        jPanel.add(educationL);
//        jPanel.add(educationT);
        jPanel.add(backButton);
        jPanel.add(startButton);
        frame.add(jPanel);
        frame.setSize(700, 300);
        frame.setVisible(true);
    }

    public static void OpenLoadMenu() {
        JFrame frame = new JFrame("Load Menu");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocation(300, 200);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(3, 1));
        frame.add(jPanel);
        frame.setSize(700, 300);
        frame.setVisible(true);
    }

    private static class OpenPauseMenu implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("Pause Menu");
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.setLocation(300, 200);

            JComboBox nationComboBox = new JComboBox<>(nations);

            JButton startButton = new JButton("Start");
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    endgame = true;
                    background_drawn = false;
                    currentNation = (String) nationComboBox.getSelectedItem();
                    if (currentNation.equals("Stark")) {
                        Stark.setActive(true);
                        Lannister.setActive(false);
                        Targaryen.setActive(false);
                    } else if (currentNation.equals("Lannister")) {
                        Stark.setActive(false);
                        Lannister.setActive(true);
                        Targaryen.setActive(false);
                    } else if (currentNation.equals("Targaryen")) {
                        Stark.setActive(false);
                        Lannister.setActive(false);
                        Targaryen.setActive(true);
                    }
                    endgame = false;
                    Thread t1 = new Thread(new Animate());
                    t1.start();
                    frame.dispose();
                }
            });

            JButton quitButton = new JButton("Quit");
            quitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    endgame = true;
                    frame.dispose();
                }
            });

            JPanel jPanel = new JPanel();
            jPanel.setLayout(new GridLayout(3, 1));
            jPanel.setBackground(colorBackground);
            startButton.setBackground(colorButton);
            quitButton.setBackground(colorButton);
            nationComboBox.setBackground(colorButton);
            frame.setBackground(colorBackground);
            jPanel.add(startButton);
            jPanel.add(quitButton);
            jPanel.add(nationComboBox);
            frame.add(jPanel);
            frame.setSize(30, 100);
            frame.setVisible(true);
        }
    }

    private static class OpenNationMenu implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("Nation Menu");
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.setLocation(300, 200);

            JLabel nationName = new JLabel("Nation: " + currentNation);
            JLabel nationTeam = new JLabel("Team: ");
            JLabel nationMilitary = new JLabel("error");
            if (Stark.isActive()) {
                nationMilitary = new JLabel("Military Strength: " + (Stark.swordmen + Stark.shieldmen + Stark.spearmen + Stark.mountedCalvalry + Stark.javelinmen + Stark.archer + Stark.crossbowmen + Stark.mountedArcher + Stark.siegeTower + Stark.catapult + Stark.ballista + Stark.trebuchet));
            } else if (Lannister.isActive()) {
                nationMilitary = new JLabel("Military Strength: " + (Lannister.swordmen + Lannister.shieldmen + Lannister.spearmen + Lannister.mountedCalvalry + Lannister.javelinmen + Lannister.archer + Lannister.crossbowmen + Lannister.mountedArcher + Lannister.siegeTower + Lannister.catapult + Lannister.ballista + Lannister.trebuchet));
            } else if (Targaryen.isActive()) {
                nationMilitary = new JLabel("Military Strength: " + (Targaryen.swordmen + Targaryen.shieldmen + Targaryen.spearmen + Targaryen.mountedCalvalry + Targaryen.javelinmen + Targaryen.archer + Targaryen.crossbowmen + Targaryen.mountedArcher + Targaryen.siegeTower + Targaryen.catapult + Targaryen.ballista + Targaryen.trebuchet));
            }
            JLabel nationLand = new JLabel("Total Land: ");

            DefaultTableModel dm = new DefaultTableModel();
            if (Stark.isActive()) {
                dm.setDataVector(new Object[][]{{"Swordmen", Stark.swordmen, Stark.swordmenResearched, "Mine", Stark.mine, Stark.mineResearched},
                                {"Shieldmen", Stark.shieldmen, Stark.shieldmenResearched, "Forge", Stark.forge, Stark.forgeResearched},
                                {"Spearmen", Stark.spearmen, Stark.spearmenResearched, "Lumber Mill", Stark.lumberMill, Stark.lumberMillResearched},
                                {"Mounted Cavalry", Stark.mountedCalvalry, Stark.mountedCalvalryResearched, "Deforestation", Stark.deforestation, Stark.deforestationResearched},
                                {"Javelinmen", Stark.javelinmen, Stark.javelinmenResearched, "Farm", Stark.farm, Stark.farmResearched},
                                {"Archer", Stark.archer, Stark.archerResearched, "Plantation", Stark.plantation, Stark.plantationResearched},
                                {"Crossbowmen", Stark.crossbowmen, Stark.crossbowmenResearched, "School", Stark.school, Stark.schoolResearched},
                                {"Mounted Archer", Stark.mountedArcher, Stark.mountedArcherResearched, "College", Stark.college, Stark.collegeResearched},
                                {"Siege Tower", Stark.siegeTower, Stark.siegeTowerResearched, "", "", ""},
                                {"Catapult", Stark.catapult, Stark.catapultResearched, "", "", ""},
                                {"Ballista", Stark.ballista, Stark.ballistaResearched, "", "", ""},
                                {"Trebuchet", Stark.trebuchet, Stark.trebuchetResearched, "", "", ""}},
                        new Object[]{"", "Owned", "Unit Researched", "Building", "Owned", "Building Research"});
            } else if (Lannister.isActive()) {
                dm.setDataVector(new Object[][]{{"Swordmen", Lannister.swordmen, Lannister.swordmenResearched, "Mine", Lannister.mine, Lannister.mineResearched},
                                {"Shieldmen", Lannister.shieldmen, Lannister.shieldmenResearched, "Forge", Lannister.forge, Lannister.forgeResearched},
                                {"Spearmen", Lannister.spearmen, Lannister.spearmenResearched, "Lumber Mill", Lannister.lumberMill, Lannister.lumberMillResearched},
                                {"Mounted Cavalry", Lannister.mountedCalvalry, Lannister.mountedCalvalryResearched, "Deforestation", Lannister.deforestation, Lannister.deforestationResearched},
                                {"Javelinmen", Lannister.javelinmen, Lannister.javelinmenResearched, "Farm", Lannister.farm, Lannister.farmResearched},
                                {"Archer", Lannister.archer, Lannister.archerResearched, "Plantation", Lannister.plantation, Lannister.plantationResearched},
                                {"Crossbowmen", Lannister.crossbowmen, Lannister.crossbowmenResearched, "School", Lannister.school, Lannister.schoolResearched},
                                {"Mounted Archer", Lannister.mountedArcher, Lannister.mountedArcherResearched, "College", Lannister.college, Lannister.collegeResearched},
                                {"Siege Tower", Lannister.siegeTower, Lannister.siegeTowerResearched, "", "", ""},
                                {"Catapult", Lannister.catapult, Lannister.catapultResearched, "", "", ""},
                                {"Ballista", Lannister.ballista, Lannister.ballistaResearched, "", "", ""},
                                {"Trebuchet", Lannister.trebuchet, Lannister.trebuchetResearched, "", "", ""}},
                        new Object[]{"Unit", "Owned", "Unit Researched", "Building", "Owned", "Building Research"});
            } else if (Targaryen.isActive()) {
                dm.setDataVector(new Object[][]{{"Swordmen", Targaryen.swordmen, Targaryen.swordmenResearched, "Mine", Targaryen.mine, Targaryen.mineResearched},
                                {"Shieldmen", Targaryen.shieldmen, Targaryen.shieldmenResearched, "Forge", Targaryen.forge, Targaryen.forgeResearched},
                                {"Spearmen", Targaryen.spearmen, Targaryen.spearmenResearched, "Lumber Mill", Targaryen.lumberMill, Targaryen.lumberMillResearched},
                                {"Mounted Cavalry", Targaryen.mountedCalvalry, Targaryen.mountedCalvalryResearched, "Deforestation", Targaryen.deforestation, Targaryen.deforestationResearched},
                                {"Javelinmen", Targaryen.javelinmen, Targaryen.javelinmenResearched, "Farm", Targaryen.farm, Targaryen.farmResearched},
                                {"Archer", Targaryen.archer, Targaryen.archerResearched, "Plantation", Targaryen.plantation, Targaryen.plantationResearched},
                                {"Crossbowmen", Targaryen.crossbowmen, Targaryen.crossbowmenResearched, "School", Targaryen.school, Targaryen.schoolResearched},
                                {"Mounted Archer", Targaryen.mountedArcher, Targaryen.mountedArcherResearched, "College", Targaryen.college, Targaryen.collegeResearched},
                                {"Siege Tower", Targaryen.siegeTower, Targaryen.siegeTowerResearched, "", "", ""},
                                {"Catapult", Targaryen.catapult, Targaryen.catapultResearched, "", "", ""},
                                {"Ballista", Targaryen.ballista, Targaryen.ballistaResearched, "", "", ""},
                                {"Trebuchet", Targaryen.trebuchet, Targaryen.trebuchetResearched, "", "", ""}},
                        new Object[]{"Unit", "Owned", "Unit Researched", "Building", "Owned", "Building Research"});
            }

            JTable militaryTable = new JTable(dm);
            militaryTable.getTableHeader().setBackground(colorButton);
            militaryTable.setBackground(colorBackground);
            JScrollPane scroll = new JScrollPane(militaryTable);
            militaryTable.setPreferredScrollableViewportSize(militaryTable.getPreferredSize());

            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
            jPanel.setBackground(colorBackground);
            frame.setBackground(colorBackground);
            jPanel.add(nationName);
            jPanel.add(nationTeam);
            jPanel.add(nationMilitary);
            jPanel.add(nationLand);
            jPanel.add(scroll);

            frame.add(jPanel);
            frame.setSize(1000, 300);
            frame.setVisible(true);
        }
    }

    private static class OpenResearchMenu implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("Research Menu");
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.setLocation(300, 200);

            DefaultTableModel dm = new DefaultTableModel();
            dm.setDataVector(new Object[][]{{"Build Mines", "Unlocks construction of Mines.", "E:50  L:10", "Buy Build Mines"},
                            {"Build Forges", "Unlocks construction of Forges.", "E:75  L:25", "Buy Build Forges"},
                            {"Build Lumber Mills", "Unlocks construction of Lumber Mills.", "E:50  L:10", "Buy Build Lumber Mills"},
                            {"Build Deforestation", "Unlocks construction of Deforestation.", "E:75  L:25", "Buy Build Deforestation"},
                            {"Build Farms", "Unlocks construction of Farms.", "E:50  L:10", "Buy Build Farms"},
                            {"Build Plantations", "Unlocks construction of Plantations.", "E:75  L:25", "Buy Build Plantations"},
                            {"Build Schools", "Unlocks construction of Schools.", "E:50  L:10", "Buy Build Schools"},
                            {"Build Colleges", "Unlocks construction of Colleges.", "E:75  L:25", "Buy Build Colleges"},
                            {"Train Swordmen", "Unlocks training of Swordmen.", "E:50  L:10", "Buy Train Swordmen"},
                            {"Train Shieldmen", "Unlocks training of Shieldmen.", "E:50  L:10", "Buy Train Shieldmen"},
                            {"Train Spearmen", "Unlocks training of Spearmen.", "E:75  L:25", "Buy Train Spearmen"},
                            {"Train Mounted Cavalry", "Unlocks training of Mounted Calvalry.", "E:100  L:50", "Buy Train Mounted Cavalry"},
                            {"Train Javelinmen", "Unlocks training of Javelinmen.", "E:50  L:10", "Buy Train Javelinmen"},
                            {"Train Archer", "Unlocks training of Archer.", "E:50  L:10", "Buy Train Archer"},
                            {"Train Crossbowmen", "Unlocks training of Crossbowmen.", "E:75  L:25", "Buy Train Crossbowmen"},
                            {"Train Mounted Archers", "Unlocks training of Mounted Archers.", "E:100  L:50", "Buy Train Mounted Archers"},
                            {"Build Siege Towers", "Unlocks construction of Siege Towers.", "E:50  L:10", "Buy Build Siege Towers"},
                            {"Build Catapults", "Unlocks construction of Catapults.", "E:50  L:10", "Buy Build Catapults"},
                            {"Build Ballistas", "Unlocks construction of Ballistas.", "E:75  L:25", "Buy Build Ballistas"},
                            {"Build Trebuchets", "Unlocks construction of Trebuchets.", "E:100  L:50", "Buy Build Trebuchets"}},
                    new Object[]{"Name", "Description", "Price", ""});

            JTable table = new JTable(dm);
            table.getColumn("").setCellRenderer(new ButtonRenderer());
            table.getColumn("").setCellEditor(new ButtonEditor(new JCheckBox()));
            table.getTableHeader().setBackground(colorButton);
            table.setBackground(colorBackground);
            frame.setBackground(colorBackground);

            JScrollPane scroll = new JScrollPane(table);
            JPanel temp = new JPanel();
            temp.setBackground(colorBackground);
            scroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, temp);
            scroll.getVerticalScrollBar().setBackground(colorBackground);
            table.setPreferredScrollableViewportSize(table.getPreferredSize());
            table.getColumnModel().getColumn(0).setPreferredWidth(200);
            table.getColumnModel().getColumn(1).setPreferredWidth(400);
            table.getColumnModel().getColumn(2).setPreferredWidth(200);
            table.getColumnModel().getColumn(3).setPreferredWidth(200);
            table.setRowHeight(25);
            frame.setSize(1000, 300);
            frame.add(scroll);
            frame.setVisible(true);
        }
    }

    private static class OpenInfoMenu implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("Info Menu");
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.setLocation(300, 200);

            DefaultTableModel dm = new DefaultTableModel();
            if (Stark.isActive()) {
                dm.setDataVector(new Object[][]{{"Swordmen", Stark.swordmen, "H:5", "M:5", "R:5", "S:0"},
                                {"Shieldmen", Stark.shieldmen, "H:10", "M:5", "R:15", "S:0"},
                                {"Spearmen", Stark.spearmen, "H:15", "M:10", "R:20", "S:0"},
                                {"Mounted Cavalry", Stark.mountedCalvalry, "H:20", "M:15", "R:25", "S:0"},
                                {"Javelinmen", Stark.javelinmen, "H:5", "M:0", "R:5", "S:5"},
                                {"Archer", Stark.archer, "H:10", "M:0", "R:5", "S:15"},
                                {"Crossbowmen", Stark.crossbowmen, "H:15", "M:0", "R:10", "S:20"},
                                {"Mounted Archer", Stark.mountedArcher, "H:20", "M:0", "R:15", "S:25"},
                                {"Siege Tower", Stark.siegeTower, "H:5", "M:5", "R:0", "S:5"},
                                {"Catapult", Stark.catapult, "H:10", "M:15", "R:0", "S:5"},
                                {"Ballista", Stark.ballista, "H:15", "M:20", "R:0", "S:10"},
                                {"Trebuchet", Stark.trebuchet, "H:20", "M:25", "R:0", "S:15"}},
                        new Object[]{"Unit", "Quantity", "Health", "Melee", "Range", "Siege"});
            } else if (Lannister.isActive()) {
                dm.setDataVector(new Object[][]{{"Swordmen", Lannister.swordmen, "H:5", "M:5", "R:5", "S:0"},
                                {"Shieldmen", Lannister.shieldmen, "H:10", "M:5", "R:15", "S:0"},
                                {"Spearmen", Lannister.spearmen, "H:15", "M:10", "R:20", "S:0"},
                                {"Mounted Cavalry", Lannister.mountedCalvalry, "H:20", "M:15", "R:25", "S:0"},
                                {"Javelinmen", Lannister.javelinmen, "H:5", "M:0", "R:5", "S:5"},
                                {"Archer", Lannister.archer, "H:10", "M:0", "R:5", "S:15"},
                                {"Crossbowmen", Lannister.crossbowmen, "H:15", "M:0", "R:10", "S:20"},
                                {"Mounted Archer", Lannister.mountedArcher, "H:20", "M:0", "R:15", "S:25"},
                                {"Siege Tower", Lannister.siegeTower, "H:5", "M:5", "R:0", "S:5"},
                                {"Catapult", Lannister.catapult, "H:10", "M:15", "R:0", "S:5"},
                                {"Ballista", Lannister.ballista, "H:15", "M:20", "R:0", "S:10"},
                                {"Trebuchet", Lannister.trebuchet, "H:20", "M:25", "R:0", "S:15"}},
                        new Object[]{"Unit", "Quantity", "Health", "Ground", "Air", "Sea"});
            } else if (Targaryen.isActive()) {
                dm.setDataVector(new Object[][]{{"Swordmen", Targaryen.swordmen, "H:5", "M:5", "R:5", "S:0"},
                                {"Shieldmen", Targaryen.shieldmen, "H:10", "M:5", "R:15", "S:0"},
                                {"Spearmen", Targaryen.spearmen, "H:15", "M:10", "R:20", "S:0"},
                                {"Mounted Cavalry", Targaryen.mountedCalvalry, "H:20", "M:15", "R:25", "S:0"},
                                {"Javelinmen", Targaryen.javelinmen, "H:5", "M:0", "R:5", "S:5"},
                                {"Archer", Targaryen.archer, "H:10", "M:0", "R:5", "S:15"},
                                {"Crossbowmen", Targaryen.crossbowmen, "H:15", "M:0", "R:10", "S:20"},
                                {"Mounted Archer", Targaryen.mountedArcher, "H:20", "M:0", "R:15", "S:25"},
                                {"Siege Tower", Targaryen.siegeTower, "H:5", "M:5", "R:0", "S:5"},
                                {"Catapult", Targaryen.catapult, "H:10", "M:15", "R:0", "S:5"},
                                {"Ballista", Targaryen.ballista, "H:15", "M:20", "R:0", "S:10"},
                                {"Trebuchet", Targaryen.trebuchet, "H:20", "M:25", "R:0", "S:15"}},
                        new Object[]{"Unit", "Quantity", "Health", "Ground", "Air", "Sea"});
            }

            JTable table = new JTable(dm);
            table.getTableHeader().setBackground(colorButton);
            table.setBackground(colorBackground);
            frame.setBackground(colorBackground);

            JScrollPane scroll = new JScrollPane(table);
            table.setPreferredScrollableViewportSize(table.getPreferredSize());
            table.getColumnModel().getColumn(0).setPreferredWidth(200);
            table.getColumnModel().getColumn(1).setPreferredWidth(100);
            table.getColumnModel().getColumn(2).setPreferredWidth(100);
            table.getColumnModel().getColumn(3).setPreferredWidth(100);
            table.getColumnModel().getColumn(4).setPreferredWidth(100);
            table.getColumnModel().getColumn(5).setPreferredWidth(100);
            table.setRowHeight(25);
            frame.setSize(700, 300);
            frame.add(scroll);
            frame.setVisible(true);
        }
    }

    private static class OpenMailMenu implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("Mail Menu");
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.setLocation(300, 200);

            JMenuBar jMenuBar = new JMenuBar();
            jMenuBar.setBackground(colorBackground);
            frame.setJMenuBar(jMenuBar);
            JButton newMail = new JButton("New");
            newMail.addActionListener(new NewMailMenu());
            newMail.setBackground(colorButton);
            jMenuBar.add(newMail);

            //  Throws all the tabs into the JTabbedPane
            JTabbedPane tabPanel = new JTabbedPane();
            tabPanel.setBackground(colorBackground);
            if (Stark.isActive()) {
                tabPanel.addTab("Inbox", Stark.inboxTabs);
                tabPanel.addTab("Sent", Stark.sentTabs);
                tabPanel.addTab("Archived", Stark.archivedTabs);
            } else if (Lannister.isActive()) {
                tabPanel.addTab("Inbox", Lannister.inboxTabs);
                tabPanel.addTab("Sent", Lannister.sentTabs);
                tabPanel.addTab("Archived", Lannister.archivedTabs);
            } else if (Targaryen.isActive()) {
                tabPanel.addTab("Inbox", Targaryen.inboxTabs);
                tabPanel.addTab("Sent", Targaryen.sentTabs);
                tabPanel.addTab("Archived", Targaryen.archivedTabs);
            }
            frame.add(tabPanel);
            frame.setSize(1000, 300);
            frame.setVisible(true);
        }
    }

    private static class NewMailMenu implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("New Mail Menu");
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.setLocation(325, 225);

            //  Makes the ComboBox for choosing a nation
            JComboBox nationComboBox = new JComboBox<>(nations);

            //  Makes the TextArea to put a message with the trade
            JLabel messageL = new JLabel("Message:    ");
            JTextArea messageT = new JTextArea(12, 85);

            //  Makes JButton to send mail
            JButton sendButton = new JButton("Send");
            sendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //  Adds mail to sent tab
                    JLabel mailTo1 = new JLabel("To: " + nationComboBox.getSelectedItem());
                    JLabel mailTo2 = new JLabel("To: " + nationComboBox.getSelectedItem());
                    JLabel mailFrom1 = new JLabel("From: " + currentNation);
                    JLabel mailFrom2 = new JLabel("From: " + currentNation);
                    Date d = new Date();
                    JLabel mailTime1 = new JLabel(String.valueOf(d));
                    JLabel mailTime2 = new JLabel(String.valueOf(d));
                    JLabel space1 = new JLabel(" ");
                    JLabel space2 = new JLabel(" ");
                    JLabel mailBody1 = new JLabel(messageT.getText());
                    JLabel mailBody2 = new JLabel(messageT.getText());

                    JPanel sendMail = new JPanel();
                    sendMail.setLayout(new BoxLayout(sendMail, BoxLayout.Y_AXIS));
                    sendMail.setBackground(colorBackground);
                    sendMail.add(mailTo1);
                    sendMail.add(mailFrom1);
                    sendMail.add(mailTime1);
                    sendMail.add(space1);
                    sendMail.add(mailBody1);
                    JPanel inboxMail = new JPanel();
                    inboxMail.setLayout(new BoxLayout(inboxMail, BoxLayout.Y_AXIS));
                    inboxMail.setBackground(colorBackground);
                    inboxMail.add(mailTo2);
                    inboxMail.add(mailFrom2);
                    inboxMail.add(mailTime2);
                    inboxMail.add(space2);
                    inboxMail.add(mailBody2);
                    if (Stark.isActive()) {
                        Stark.sentTabs.addTab((String) nationComboBox.getSelectedItem(), sendMail);
                    } else if (Lannister.isActive()) {
                        Lannister.sentTabs.addTab((String) nationComboBox.getSelectedItem(), sendMail);
                    } else if (Targaryen.isActive()) {
                        Targaryen.sentTabs.addTab((String) nationComboBox.getSelectedItem(), sendMail);
                    }
                    if (nationComboBox.getSelectedItem().equals("Stark")) {
                        Stark.inboxTabs.addTab((String) nationComboBox.getSelectedItem(), inboxMail);
                    } else if (nationComboBox.getSelectedItem().equals("Lannister")) {
                        Lannister.inboxTabs.addTab((String) nationComboBox.getSelectedItem(), inboxMail);
                    } else if (nationComboBox.getSelectedItem().equals("Targaryen")) {
                        Targaryen.inboxTabs.addTab((String) nationComboBox.getSelectedItem(), inboxMail);
                    }
                    frame.dispose();
                }
            });

            //  Makes a JPanel to combine the message Label and TextArea together
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BorderLayout());
            centerPanel.setBackground(colorBackground);
            messageT.setBackground(colorButton);
            centerPanel.add(messageL, BorderLayout.WEST);
            centerPanel.add(messageT, BorderLayout.CENTER);

            //  Throws everything into the JPanel
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            jPanel.setBackground(colorBackground);
            nationComboBox.setBackground(colorButton);
            sendButton.setBackground(colorButton);
            jPanel.add(nationComboBox);
            jPanel.add(centerPanel);
            jPanel.add(sendButton);
            frame.add(jPanel);
            frame.setSize(1000, 300);
            frame.setVisible(true);
        }
    }

    private static class OpenTradeMenu implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("Trade Menu");
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.setLocation(300, 200);

            //  Makes the ComboBox for choosing a nation
            JComboBox nationComboBox = new JComboBox<>(nations);

            //  Groups the RadioButtons so only one can be selected at a time
            ButtonGroup G = new ButtonGroup();
            JRadioButton giveRadioButton = new JRadioButton();
            giveRadioButton.setText("Give");
            G.add(giveRadioButton);
            JRadioButton requestRadioButton = new JRadioButton();
            requestRadioButton.setText("Request");
            G.add(requestRadioButton);

            //  Makes all the TextFields for filing in resources
            JLabel metalL = new JLabel("Metal:");
            JTextField metalT = new JTextField(8);
            JLabel woodL = new JLabel("Wood:");
            JTextField woodT = new JTextField(8);
            JLabel foodL = new JLabel("Food:");
            JTextField foodT = new JTextField(8);
            JLabel laborL = new JLabel("Labor:");
            JTextField laborT = new JTextField(8);
            JLabel educationL = new JLabel("Education:");
            JTextField educationT = new JTextField(8);

            //  Makes the TextArea to put a message with the trade
            JLabel messageL = new JLabel("Message:    ");
            JTextArea messageT = new JTextArea(11, 85);

            //  Makes the send button to send the trade
            JButton sendButton = new JButton("Send");
            sendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int tempMetal = 0;
                    int tempWood = 0;
                    int tempFood = 0;
                    int tempLabor = 0;
                    int tempEducation = 0;
                    if (Integer.parseInt(metalT.getText()) > 0) {
                        tempMetal = Integer.parseInt(metalT.getText());
                    }
                    if (Integer.parseInt(woodT.getText()) > 0) {
                        tempWood = Integer.parseInt(woodT.getText());
                    }
                    if (Integer.parseInt(foodT.getText()) > 0) {
                        tempFood = Integer.parseInt(foodT.getText());
                    }
                    if (Integer.parseInt(laborT.getText()) > 0) {
                        tempLabor = Integer.parseInt(laborT.getText());
                    }
                    if (Integer.parseInt(educationT.getText()) > 0) {
                        tempEducation = Integer.parseInt(educationT.getText());
                    }
                    if (Stark.isActive() && giveRadioButton.isSelected()) {
                        if (nationComboBox.getSelectedItem().equals("Lannister")) {
                            Stark.setResource("metal", tempMetal * -1);
                            Stark.setResource("wood", tempWood * -1);
                            Stark.setResource("food", tempFood * -1);
                            Stark.setResource("labor", tempLabor * -1);
                            Stark.setResource("education", tempEducation * -1);

                            Lannister.setResource("metal", tempMetal);
                            Lannister.setResource("wood", tempWood);
                            Lannister.setResource("food", tempFood);
                            Lannister.setResource("labor", tempLabor);
                            Lannister.setResource("education", tempEducation);
                        } else if (nationComboBox.getSelectedItem().equals("Targaryen")) {
                            Stark.setResource("metal", tempMetal * -1);
                            Stark.setResource("wood", tempWood * -1);
                            Stark.setResource("food", tempFood * -1);
                            Stark.setResource("labor", tempLabor * -1);
                            Stark.setResource("education", tempEducation * -1);

                            Targaryen.setResource("metal", tempMetal);
                            Targaryen.setResource("wood", tempWood);
                            Targaryen.setResource("food", tempFood);
                            Targaryen.setResource("labor", tempLabor);
                            Targaryen.setResource("education", tempEducation);
                        }
                    } else if (Lannister.isActive() && giveRadioButton.isSelected()) {
                        if (nationComboBox.getSelectedItem().equals("Stark")) {
                            Lannister.setResource("metal", tempMetal * -1);
                            Lannister.setResource("wood", tempWood * -1);
                            Lannister.setResource("food", tempFood * -1);
                            Lannister.setResource("labor", tempLabor * -1);
                            Lannister.setResource("education", tempEducation * -1);

                            Stark.setResource("metal", tempMetal);
                            Stark.setResource("wood", tempWood);
                            Stark.setResource("food", tempFood);
                            Stark.setResource("labor", tempLabor);
                            Stark.setResource("education", tempEducation);
                        } else if (nationComboBox.getSelectedItem().equals("Targaryen")) {
                            Lannister.setResource("metal", tempMetal * -1);
                            Lannister.setResource("wood", tempWood * -1);
                            Lannister.setResource("food", tempFood * -1);
                            Lannister.setResource("labor", tempLabor * -1);
                            Lannister.setResource("education", tempEducation * -1);

                            Targaryen.setResource("metal", tempMetal);
                            Targaryen.setResource("wood", tempWood);
                            Targaryen.setResource("food", tempFood);
                            Targaryen.setResource("labor", tempLabor);
                            Targaryen.setResource("education", tempEducation);
                        }
                    } else if (Targaryen.isActive() && giveRadioButton.isSelected()) {
                        if (nationComboBox.getSelectedItem().equals("Stark")) {
                            Targaryen.setResource("metal", tempMetal * -1);
                            Targaryen.setResource("wood", tempWood * -1);
                            Targaryen.setResource("food", tempFood * -1);
                            Targaryen.setResource("labor", tempLabor * -1);
                            Targaryen.setResource("education", tempEducation * -1);

                            Stark.setResource("metal", tempMetal);
                            Stark.setResource("wood", tempWood);
                            Stark.setResource("food", tempFood);
                            Stark.setResource("labor", tempLabor);
                            Stark.setResource("education", tempEducation);
                        } else if (nationComboBox.getSelectedItem().equals("Lannister")) {
                            Targaryen.setResource("metal", tempMetal * -1);
                            Targaryen.setResource("wood", tempWood * -1);
                            Targaryen.setResource("food", tempFood * -1);
                            Targaryen.setResource("labor", tempLabor * -1);
                            Targaryen.setResource("education", tempEducation * -1);

                            Lannister.setResource("metal", tempMetal);
                            Lannister.setResource("wood", tempWood);
                            Lannister.setResource("food", tempFood);
                            Lannister.setResource("labor", tempLabor);
                            Lannister.setResource("education", tempEducation);
                        }
                    }

                    JLabel mailTo1 = new JLabel("To: " + nationComboBox.getSelectedItem());
                    JLabel mailTo2 = new JLabel("To: " + nationComboBox.getSelectedItem());
                    JLabel mailFrom1 = new JLabel("From: " + currentNation);
                    JLabel mailFrom2 = new JLabel("From: " + currentNation);
                    Date d = new Date();
                    JLabel mailTime1 = new JLabel(String.valueOf(d));
                    JLabel mailTime2 = new JLabel(String.valueOf(d));
                    JLabel space1 = new JLabel(" ");
                    JLabel space2 = new JLabel(" ");
                    JLabel mailBody1 = new JLabel();
                    JLabel mailBody2 = new JLabel();
                    if (giveRadioButton.isSelected()) {
                        mailBody1 = new JLabel("You payed M: " + tempMetal + "    W: " + tempWood + "    F: " + tempFood + "    L: " + tempLabor + "    E: " + tempEducation);
                        mailBody2 = new JLabel("You've been payed M: " + tempMetal + "    W: " + tempWood + "    F: " + tempFood + "    L: " + tempLabor + "    E: " + tempEducation);
                    } else if (requestRadioButton.isSelected()) {
                        mailBody1 = new JLabel("You requested M: " + tempMetal + "    W: " + tempWood + "    F: " + tempFood + "    L: " + tempLabor + "    E: " + tempEducation);
                        mailBody2 = new JLabel("You've been requested M: " + tempMetal + "    W: " + tempWood + "    F: " + tempFood + "    L: " + tempLabor + "    E: " + tempEducation);
                    }
                    JLabel space3 = new JLabel(" ");
                    JLabel space4 = new JLabel(" ");
                    JLabel mailBody3 = new JLabel(messageT.getText());
                    JLabel mailBody4 = new JLabel(messageT.getText());

                    JPanel sendMail = new JPanel();
                    sendMail.setLayout(new BoxLayout(sendMail, BoxLayout.Y_AXIS));
                    sendMail.add(mailTo1);
                    sendMail.add(mailFrom1);
                    sendMail.add(mailTime1);
                    sendMail.add(space1);
                    sendMail.add(mailBody1);
                    sendMail.add(space3);
                    sendMail.add(mailBody3);
                    JPanel inboxMail = new JPanel();
                    inboxMail.setLayout(new BoxLayout(inboxMail, BoxLayout.Y_AXIS));
                    inboxMail.add(mailTo2);
                    inboxMail.add(mailFrom2);
                    inboxMail.add(mailTime2);
                    inboxMail.add(space2);
                    inboxMail.add(mailBody2);
                    inboxMail.add(space4);
                    inboxMail.add(mailBody4);
                    if (Stark.isActive()) {
                        Stark.sentTabs.addTab((String) nationComboBox.getSelectedItem(), sendMail);
                    } else if (Lannister.isActive()) {
                        Lannister.sentTabs.addTab((String) nationComboBox.getSelectedItem(), sendMail);
                    } else if (Targaryen.isActive()) {
                        Targaryen.sentTabs.addTab((String) nationComboBox.getSelectedItem(), sendMail);
                    }
                    if (nationComboBox.getSelectedItem().equals("Stark")) {
                        Stark.inboxTabs.addTab((String) nationComboBox.getSelectedItem(), inboxMail);
                    } else if (nationComboBox.getSelectedItem().equals("Lannister")) {
                        Lannister.inboxTabs.addTab((String) nationComboBox.getSelectedItem(), inboxMail);
                    } else if (nationComboBox.getSelectedItem().equals("Targaryen")) {
                        Targaryen.inboxTabs.addTab((String) nationComboBox.getSelectedItem(), inboxMail);
                    }
                    backgroundDraw();
                    frame.dispose();
                }
            });

            //  Some spaces just to spread stuff out, probably a better way to do this
            JLabel space1 = new JLabel("          ");
            JLabel space2 = new JLabel("          ");
            JLabel space3 = new JLabel("          ");

            //  Makes a JPanel to combine the message Label and TextArea together
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BorderLayout());
            centerPanel.setBackground(colorBackground);
            messageT.setBackground(colorButton);
            centerPanel.add(space1, BorderLayout.NORTH);
            centerPanel.add(messageL, BorderLayout.WEST);
            centerPanel.add(messageT, BorderLayout.CENTER);

            //  Throws everything into the JPanel
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            jPanel.setBackground(colorBackground);
            nationComboBox.setBackground(colorButton);
            giveRadioButton.setBackground(colorBackground);
            requestRadioButton.setBackground(colorBackground);
            metalT.setBackground(colorButton);
            woodT.setBackground(colorButton);
            foodT.setBackground(colorButton);
            laborT.setBackground(colorButton);
            educationT.setBackground(colorButton);
            sendButton.setBackground(colorButton);
            frame.setBackground(colorBackground);
            jPanel.add(nationComboBox);
            jPanel.add(space2);
            jPanel.add(giveRadioButton);
            jPanel.add(requestRadioButton);
            jPanel.add(space3);
            jPanel.add(metalL);
            jPanel.add(metalT);
            jPanel.add(woodL);
            jPanel.add(woodT);
            jPanel.add(foodL);
            jPanel.add(foodT);
            jPanel.add(laborL);
            jPanel.add(laborT);
            jPanel.add(educationL);
            jPanel.add(educationT);
            jPanel.add(centerPanel);
            jPanel.add(sendButton);
            frame.add(jPanel);
            frame.setSize(1000, 300);
            frame.setVisible(true);
        }
    }

    private static void OpenTileMenu(Tile tile) {
        JFrame frame = new JFrame("Tile Menu");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocation((int)(tile.getX() + tile.getWidth()),(int)tile.getY());

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JLabel tileOwner = new JLabel(tile.owner);
        DefaultTableModel dm = new DefaultTableModel();
        dm.setDataVector(new Object[][]{{"Metal: " + tile.resourceRates.get(2), "Mines: " + tile.mine},
                        {"Wood: " + tile.resourceRates.get(1), "Forges: " + tile.forge},
                        {"Food: " + tile.resourceRates.get(3), "Lumber Mills: " + tile.lumberMill},
                        {"Labor: " + tile.resourceRates.get(0), "Deforestation: " + tile.deforestation},
                        {"Education: " + tile.resourceRates.get(4), "Farms: " + tile.farm},
                        {"", "Plantations: " + tile.plantation},
                        {"", "Schools: " + tile.school},
                        {"", "Colleges: " + tile.college}},
                new Object[]{"Resource", "Building"});
        JTable table = new JTable(dm);

        JButton claimTile = new JButton("Claim Tile");
        claimTile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                actionsTaken+=5;
                if (actionsTaken > maxActions) {
                    JOptionPane.showMessageDialog(claimTile,"You need 5 action points to claim a tile");
                    actionsTaken-=5;
                } else if (actionsTaken == maxActions + 1) {
                    JOptionPane.showMessageDialog(claimTile,"It is not your turn");
                    actionsTaken-=5;
                } else {
                    tile.owner = currentNation;
                    tileOwner.setText(currentNation);
                    if (currentNation.equals("Stark")) {
                        Stark.labor += tile.resourceRates.get(0);
                        Stark.wood += tile.resourceRates.get(1);
                        Stark.metal += tile.resourceRates.get(2);
                        Stark.food += tile.resourceRates.get(3);
                        Stark.education += tile.resourceRates.get(4);
                    } else if (currentNation.equals("Lannister")) {
                        Lannister.labor += tile.resourceRates.get(0);
                        Lannister.wood += tile.resourceRates.get(1);
                        Lannister.metal += tile.resourceRates.get(2);
                        Lannister.food += tile.resourceRates.get(3);
                        Lannister.education += tile.resourceRates.get(4);
                    } else {
                        Targaryen.labor += tile.resourceRates.get(0);
                        Targaryen.wood += tile.resourceRates.get(1);
                        Targaryen.metal += tile.resourceRates.get(2);
                        Targaryen.food += tile.resourceRates.get(3);
                        Targaryen.education += tile.resourceRates.get(4);
                    }
                    jPanel.remove(claimTile);
                    jPanel.repaint();
                    backgroundDraw();
                }
            }
        });

        JButton buildButton = new JButton("Build");
        buildButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                OpenConstructionMenu(tile);
            }
        });
        JButton trainButton = new JButton("Train");
        trainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                OpenMilitaryMenu(tile);
            }
        });

        JButton commandButton = new JButton("Command");
        commandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                OpenCommandMenu(tile);
            }
        });

        Boolean hasAllies = false;
        Boolean hasEnemies = false;
        for (int i = 0; i < tile.occupyingUnits.size(); i++) {
            if (tile.occupyingUnits.get(i).owner == currentNation) {
                hasAllies = true;
            } else {
                hasEnemies = true;
            }
        }

        jPanel.setBackground(colorBackground);
        table.getTableHeader().setBackground(colorButton);
        table.setBackground(colorBackground);
        claimTile.setBackground(colorButton);
        buildButton.setBackground(colorButton);
        trainButton.setBackground(colorButton);
        commandButton.setBackground(colorButton);
        frame.setBackground(colorBackground);
        jPanel.add(tileOwner);
        jPanel.add(table);
        if (tile.owner.equals("") && hasAllies) {
            jPanel.add(claimTile);
        } else if (!tile.owner.equals(currentNation) && hasAllies & !hasEnemies) {
            jPanel.add(claimTile);
        } else if (tile.owner.equals(currentNation)) {
            jPanel.add(buildButton);
            jPanel.add(trainButton);
        }
        if ((tile.owner.equals(currentNation) && hasEnemies) || hasAllies) {
            jPanel.add(commandButton);
        }
        frame.add(jPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public static void OpenConstructionMenu(Tile tile) {
        JFrame frame = new JFrame("Construction Menu");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocation(300, 200);

        DefaultTableModel dm = new DefaultTableModel();
        dm.setDataVector(new Object[][]{{"Mine", "Mine to boost metal production.", "M:10  W:50  F:20  L:20", "Buy Mine"},
                        {"Forge", "Forge to greatly boost metal production.", "M:50  W:40  F:20  L:40", "Buy Forge"},
                        {"Lumber Mill", "Mill to boost wood production.", "M:50  W:10  F:20  L:20", "Buy Lumber Mill"},
                        {"Deforestation", "Deforestation to greatly boost wood production.", "M:20  W:40  F:50  L:40", "Buy Deforestation"},
                        {"Farm", "Farm to boost food production.", "M:20  W:40  F:20  L:20", "Buy Farm"},
                        {"Plantation", "Plantation to greatly boost food production.", "M:40  W:50  F:20  L:40", "Buy Plantation"},
                        {"School", "School to boost education production.", "M:40  W:20  F:20  L:20", "Buy School"},
                        {"College", "College to greatly boost education production.", "M:40  W:40  F:30  L:40", "Buy College"}},
                new Object[]{"Name", "Description", "Price", ""});

        JTable table = new JTable(dm);
        table.getColumn("").setCellRenderer(new ButtonRenderer());
        table.getColumn("").setCellEditor(new ButtonEditor(new JCheckBox(), tile));
        table.getTableHeader().setBackground(colorButton);
        table.setBackground(colorBackground);
        frame.setBackground(colorBackground);

        JScrollPane scroll = new JScrollPane(table);
        JPanel temp = new JPanel();
        temp.setBackground(colorBackground);
        scroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, temp);
        scroll.getVerticalScrollBar().setBackground(colorBackground);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.setRowHeight(25);
        frame.setSize(1000, 300);
        frame.add(scroll);
        frame.setVisible(true);
    }

    private static void OpenMilitaryMenu(Tile tile) {
        JFrame frame = new JFrame("Military Menu");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocation(300, 200);

        DefaultTableModel dm = new DefaultTableModel();
        dm.setDataVector(new Object[][]{{"Swordmen", "Tier 1 melee unit", "M:10  W:10  F:0  L:10", "Buy Swordmen"},
                        {"Shieldmen", "Tier 2 melee unit", "M:25  W:10  F:10  L:10", "Buy Shieldmen"},
                        {"Spearmen", "Tier 3 melee unit", "M:30  W:15  F:15  L:15", "Buy Spearmen"},
                        {"Mounted Cavalry", "Tier 4 melee unit", "M:35  W:20  F:15  L:20", "Buy Mounted Calvalry"},
                        {"Javelinmen", "Tier 1 range unit", "M:10  W:0  F:10  L:10", "Buy Javelinmen"},
                        {"Archer", "Tier 2 range unit", "M:10  W:10  F:25  L:10", "Buy Archer"},
                        {"Crossbowmen", "Tier 3 range unit", "M:15  W:15  F:30  L:15", "Buy Crossbowmen"},
                        {"Mounted Archer", "Tier 4 range unit", "M:20  W:15  F:35  L:20", "Buy Mounted Archer"},
                        {"Siege Tower", "Tier 1 siege unit", "M:0  W:10  F:10  L:10", "Buy Siege Tower"},
                        {"Catapult", "Tier 2 siege unit", "M:10  W:25  F:10  L:10", "Buy Catapult"},
                        {"Ballista", "Tier 3 siege unit", "M:15  W:30  F:15  L:15", "Buy Ballista"},
                        {"Trebuchet", "Tier 4 siege unit", "M:15  W:35  F:20  L:20", "Buy Trebuchet"}},
                new Object[]{"Name", "Description", "Price", ""});

        JTable table = new JTable(dm);
        table.getColumn("").setCellRenderer(new ButtonRenderer());
        table.getColumn("").setCellEditor(new ButtonEditor(new JCheckBox(), tile));
        table.getTableHeader().setBackground(colorButton);
        table.setBackground(colorBackground);
        frame.setBackground(colorBackground);

        JScrollPane scroll = new JScrollPane(table);
        JPanel temp = new JPanel();
        temp.setBackground(colorBackground);
        scroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, temp);
        scroll.getVerticalScrollBar().setBackground(colorBackground);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.setRowHeight(25);
        frame.setSize(1000, 300);
        frame.add(scroll);
        frame.setVisible(true);
    }

    private static void OpenCommandMenu(Tile tile) {
        JFrame frame = new JFrame("Command Menu");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocation(300, 200);

        DefaultTableModel dm1 = new DefaultTableModel();
        dm1.setDataVector(null, new Object[]{"Ally", "Owner", "Move"});
        for (int i = 0; i < tile.occupyingUnits.size(); i++) {
            if (tile.occupyingUnits.get(i).owner == currentNation) {
                dm1.addRow(new Object[]{tile.occupyingUnits.get(i).unitName, tile.occupyingUnits.get(i).owner, false});
            }
        }
        JTable allyTable = new JTable(dm1) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                    default:
                        return Boolean.class;
                }
            }
        };
        JLabel selectedUnits = new JLabel();
        Vector<MilitaryUnit> movingUnitsTemp = new Vector<>();
        allyTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (actionsTaken < maxActions) {
                    String temp = "<html>";
                    movingUnitsTemp.clear();
                    for(int i = 0; i < allyTable.getModel().getRowCount(); i++) {
                        if ((Boolean) allyTable.getModel().getValueAt(i,2)) {
                            temp += allyTable.getValueAt(i, 0) + "<br/>";
                            movingUnitsTemp.add(0, new MilitaryUnit(currentNation, (String) allyTable.getValueAt(i, 0)));
                        }
                    }
                    temp += "</html>";
                    selectedUnits.setText(temp);
                }
            }
        });

        Boolean hasEnemies = false;
        DefaultTableModel dm2 = new DefaultTableModel();
        dm2.setDataVector(null, new Object[]{"Enemy", "Owner", "Move"});
        for (int i = 0; i < tile.occupyingUnits.size(); i++) {
            if (tile.occupyingUnits.get(i).owner != currentNation) {
                dm2.addRow(new Object[]{tile.occupyingUnits.get(i).unitName, tile.occupyingUnits.get(i).owner, ""});
                hasEnemies = true;
            }
        }
        JTable enemyTable = new JTable(dm2);

        JButton moveButton = new JButton("Move Units");
        moveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pathBeginning = tile;
                movingUnits.clear();
                for (int i = 0; i < movingUnitsTemp.size(); i++) {
                    tile.setMilitary(movingUnitsTemp.get(i).unitName, -1, movingUnitsTemp.get(i).owner);
                    movingUnits.add(0, new MilitaryUnit(movingUnitsTemp.get(i).owner, movingUnitsTemp.get(i).unitName));
                }
                frame.dispose();
            }
        });

        JButton attackButton = new JButton("Attack");
        attackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //OpenAttackAnimation(tile);
                OpenAttackMenu(tile);
                attacking = true;
                frame.dispose();
            }
        });

        allyTable.getTableHeader().setBackground(colorButton);
        JScrollPane scroll1 = new JScrollPane(allyTable);
        allyTable.setPreferredScrollableViewportSize(allyTable.getPreferredSize());
        scroll1.getViewport().setBackground(colorBackground);
        JPanel temp1 = new JPanel();
        temp1.setBackground(colorBackground);
        scroll1.setCorner(JScrollPane.UPPER_RIGHT_CORNER, temp1);
        scroll1.getVerticalScrollBar().setBackground(colorBackground);

        enemyTable.getTableHeader().setBackground(colorButton);
        JScrollPane scroll2 = new JScrollPane(enemyTable);
        enemyTable.setPreferredScrollableViewportSize(enemyTable.getPreferredSize());
        scroll2.getViewport().setBackground(colorBackground);
        JPanel temp2 = new JPanel();
        temp2.setBackground(colorBackground);
        scroll2.setCorner(JScrollPane.UPPER_RIGHT_CORNER, temp2);
        scroll2.getVerticalScrollBar().setBackground(colorBackground);

        JLabel allyL = new JLabel("Ally Units");
        JLabel enemyL = new JLabel("Enemy Units");

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(2, 2));
        jPanel.setBackground(colorBackground);
        allyTable.setBackground(colorBackground);
        enemyTable.setBackground(colorBackground);
        moveButton.setBackground(colorButton);
        attackButton.setBackground(colorButton);
        frame.setBackground(colorBackground);
//        jPanel.add(allyL);
//        jPanel.add(enemyL);
        jPanel.add(scroll1);
        jPanel.add(scroll2);
//        jPanel.add(selectedUnits);
        if (actionsTaken < maxActions) {
            jPanel.add(moveButton);
        }
        if (hasEnemies) {
            jPanel.add(attackButton);
        }

        frame.add(jPanel);
        frame.setSize(600, 300);
        frame.setVisible(true);
    }

    public static void OpenAttackAnimation(Tile tile) {
        AttackFrame frame = new AttackFrame(80, 40);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocation(300, 200);

        frame.add(30, Lannister.class);
        frame.add(30, Stark.class);

        frame.start();

        frame.setVisible(true);
    }

    public static void OpenConfirmMovementMenu(Tile tile) {
        JFrame frame = new JFrame("");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocation((int)(tile.getX() + tile.getWidth()),(int)tile.getY());

        int price = movingUnits.size() * (getPath(pathBeginning, tile).size() - 1);
        JLabel costL = new JLabel("Pay " + price + " Labor");
        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Stark.isActive()) {
                    Stark.setResource("labor", -1 * price);
                } else if (Lannister.isActive()) {
                    Lannister.setResource("labor", -1 * price);
                } else if (Targaryen.isActive()) {
                    Targaryen.setResource("labor", -1 * price);
                }
                for (int i = 0; i < movingUnits.size(); i++) {
                    tile.setMilitary(movingUnits.get(i).unitName, 0, currentNation);
                }
                movingUnits.clear();
                frame.dispose();
            }
        });

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(2, 1));
        jPanel.setBackground(colorBackground);
        confirmButton.setBackground(colorButton);
        frame.setBackground(colorBackground);
        jPanel.add(costL);
        jPanel.add(confirmButton);

        frame.add(jPanel);
        frame.setSize(300, 100);
        frame.setVisible(true);
    }

    private static void OpenAttackMenu(Tile tile) {
        JFrame frame = new JFrame("Attack Menu");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocation(300, 200);

        Vector<MilitaryUnit> starkUnits = new Vector<>();
        Vector<MilitaryUnit> lannisterUnits = new Vector<>();
        Vector<MilitaryUnit> targaryenUnits = new Vector<>();
        for (int i = 0; i < tile.occupyingUnits.size(); i++) {
            if (tile.occupyingUnits.get(i).owner.equals("Stark")) {
                starkUnits.add(0, new MilitaryUnit(tile.occupyingUnits.get(i).owner, tile.occupyingUnits.get(i).unitName));
            } else if (tile.occupyingUnits.get(i).owner.equals("Lannister")) {
                lannisterUnits.add(0, new MilitaryUnit(tile.occupyingUnits.get(i).owner, tile.occupyingUnits.get(i).unitName));
            } else if (tile.occupyingUnits.get(i).owner.equals("Targaryen")) {
                targaryenUnits.add(0, new MilitaryUnit(tile.occupyingUnits.get(i).owner, tile.occupyingUnits.get(i).unitName));
            }
        }

        DefaultTableModel dmStark = new DefaultTableModel();
        dmStark.setDataVector(null, new Object[]{"Unit", "Owner", "Melee", "Range", "Siege", "Health", "Status"});
        DefaultTableModel dmLannister = new DefaultTableModel();
        dmLannister.setDataVector(null, new Object[]{"Unit", "Owner", "Melee", "Range", "Siege", "Health", "Status"});

        double starkMeleePower = 0;
        double starkRangePower = 0;
        double starkSiegePower = 0;
        double starkMeleeHealth = 0;
        double starkRangeHealth = 0;
        double starkSiegeHealth = 0;

        double lannisterMeleePower = 0;
        double lannisterRangePower = 0;
        double lannisterSiegePower = 0;
        double lannisterMeleeHealth = 0;
        double lannisterRangeHealth = 0;
        double lannisterSiegeHealth = 0;

        for (int i = 0; i < starkUnits.size(); i++) {
            starkMeleePower += starkUnits.get(i).melee;
            starkRangePower += starkUnits.get(i).range;
            starkSiegePower += starkUnits.get(i).siege;
            if (starkUnits.get(i).type == MilitaryUnit.unitType.melee) {
                starkMeleeHealth += starkUnits.get(i).health;
            } else if (starkUnits.get(i).type == MilitaryUnit.unitType.range) {
                starkRangeHealth += starkUnits.get(i).health;
            } else if (starkUnits.get(i).type == MilitaryUnit.unitType.siege) {
                starkSiegeHealth += starkUnits.get(i).health;
            }
            dmStark.addRow(new Object[]{starkUnits.get(i).unitName, starkUnits.get(i).owner, starkUnits.get(i).melee, starkUnits.get(i).range, starkUnits.get(i).siege, starkUnits.get(i).health, "Alive"});
        }
        for (int i = 0; i < lannisterUnits.size(); i++) {
            lannisterMeleePower += lannisterUnits.get(i).melee;
            lannisterRangePower += lannisterUnits.get(i).range;
            lannisterSiegePower += lannisterUnits.get(i).siege;
            if (lannisterUnits.get(i).type == MilitaryUnit.unitType.melee) {
                lannisterMeleeHealth += lannisterUnits.get(i).health;
            } else if (lannisterUnits.get(i).type == MilitaryUnit.unitType.range) {
                lannisterRangeHealth += lannisterUnits.get(i).health;
            } else if (lannisterUnits.get(i).type == MilitaryUnit.unitType.siege) {
                lannisterSiegeHealth += lannisterUnits.get(i).health;
            }
            dmLannister.addRow(new Object[]{lannisterUnits.get(i).unitName, lannisterUnits.get(i).owner, lannisterUnits.get(i).melee, lannisterUnits.get(i).range, lannisterUnits.get(i).siege, lannisterUnits.get(i).health, "Alive"});
        }

        starkMeleeHealth -= (lannisterMeleePower * 0.5) + (lannisterRangePower * 0.25) + (lannisterSiegePower * 1.0);
        starkRangeHealth -= (lannisterMeleePower * 1.0) + (lannisterRangePower * 0.5) + (lannisterSiegePower * 0.25);
        starkSiegeHealth -= (lannisterMeleePower * 0.25) + (lannisterRangePower * 1.0) + (lannisterSiegePower * 0.5);
        double starkHealth = starkMeleeHealth + starkRangeHealth + starkSiegeHealth;
        lannisterMeleeHealth -= (starkMeleePower * 0.5) + (starkRangePower * 0.25) + (starkSiegePower * 1.0);
        lannisterRangeHealth -= (starkMeleePower * 1.0) + (starkRangePower * 0.5) + (starkSiegePower * 0.25);
        lannisterSiegeHealth -= (starkMeleePower * 0.25) + (starkRangePower * 1.0) + (starkSiegePower * 0.5);
        double lannisterHealth = lannisterMeleeHealth + lannisterRangeHealth + lannisterSiegeHealth;

        String winner = "";
        if (starkHealth > lannisterHealth) {
            winner = "Stark Wins";
            for (int i = 0; i < lannisterUnits.size(); i++) {
                tile.setMilitary(lannisterUnits.get(i).unitName, -1, "Lannister");
                Lannister.setMilitary(lannisterUnits.get(i).unitName, -1);
                dmLannister.setValueAt("Dead", i, 6);
            }
        } else if (lannisterHealth > starkHealth) {
            winner = "Lannister Wins";
            for (int i = 0; i < starkUnits.size(); i++) {
                tile.setMilitary(starkUnits.get(i).unitName, -1, "Stark");
                Stark.setMilitary(starkUnits.get(i).unitName, -1);
                dmStark.setValueAt("Dead", i, 6);
            }
        }
        JTable starkT = new JTable(dmStark);
        starkT.getTableHeader().setBackground(colorButton);
        JScrollPane scroll1 = new JScrollPane(starkT);
        starkT.setPreferredScrollableViewportSize(starkT.getPreferredSize());
        scroll1.getViewport().setBackground(colorBackground);
        JPanel temp1 = new JPanel();
        temp1.setBackground(colorBackground);
        scroll1.setCorner(JScrollPane.UPPER_RIGHT_CORNER, temp1);
        scroll1.getVerticalScrollBar().setBackground(colorBackground);

        JTable lannisterT = new JTable(dmLannister);
        lannisterT.getTableHeader().setBackground(colorButton);
        JScrollPane scroll2 = new JScrollPane(lannisterT);
        lannisterT.setPreferredScrollableViewportSize(lannisterT.getPreferredSize());
        scroll2.getViewport().setBackground(colorBackground);
        JPanel temp2 = new JPanel();
        temp2.setBackground(colorBackground);
        scroll2.setCorner(JScrollPane.UPPER_RIGHT_CORNER, temp2);
        scroll2.getVerticalScrollBar().setBackground(colorBackground);

        JLabel starkL = new JLabel("[STARK] Melee: " + starkMeleePower + " Range: " + starkRangePower + " Siege: " + starkSiegePower + " Health: " + starkHealth);
        JLabel lannisterL = new JLabel("[LANNISTER] Melee: " + lannisterMeleePower + " Range: " + lannisterRangePower + " Siege: " + lannisterSiegePower + " Health: " + lannisterHealth);
        JLabel winnerL = new JLabel(winner);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(2, 2));
        jPanel.setBackground(colorBackground);
        starkT.setBackground(colorBackground);
        lannisterT.setBackground(colorBackground);
        frame.setBackground(colorBackground);
        jPanel.add(starkL);
        jPanel.add(lannisterL);
        jPanel.add(scroll1);
        jPanel.add(scroll2);
//        jPanel.add(winnerL);

        frame.add(jPanel);
        frame.setSize(1000, 300);
        frame.setVisible(true);
    }

    //TODO://///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Button Code
    public static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(colorButton);
            } else {
                setForeground(table.getForeground());
                setBackground(colorButton);
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    public static class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private Tile selectedTile;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public ButtonEditor(JCheckBox checkBox, Tile tile) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
            selectedTile = tile;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                actionsTaken++;
                if (actionsTaken > maxActions) {
                    actionsTaken--;
                    JOptionPane.showMessageDialog(button,"You are out of action points");
                } else if (actionsTaken == maxActions + 1) {
                    actionsTaken--;
                    JOptionPane.showMessageDialog(button,"It is not your turn");
                } else {
                    if (Stark.isActive()) {
                        if (label.equals("Buy Swordmen") && Stark.swordmenResearched && Stark.metal >= 10 && Stark.wood >= 10 && Stark.food >= 0 && Stark.labor >= 10) {
                            selectedTile.setMilitary("Swordmen", 1, "Stark");
                            JOptionPane.showMessageDialog(button, "Swordmen Trained");
                        } else if (label.equals("Buy Shieldmen") && Stark.shieldmenResearched && Stark.metal >= 25 && Stark.wood >= 10 && Stark.food >= 10 && Stark.labor >= 10) {
                            selectedTile.setMilitary("Shieldmen", 1, "Stark");
                            JOptionPane.showMessageDialog(button, "Shieldmen Trained");
                        } else if (label.equals("Buy Spearmen") && Stark.spearmenResearched && Stark.metal >= 35 && Stark.wood >= 15 && Stark.food >= 15 && Stark.labor >= 15) {
                            selectedTile.setMilitary("Spearmen", 1, "Stark");
                            JOptionPane.showMessageDialog(button, "Spearmen Trained");
                        } else if (label.equals("Buy Mounted Calvalry") && Stark.mountedCalvalryResearched && Stark.metal >= 35 && Stark.wood >= 20 && Stark.food >= 15 && Stark.labor >= 20) {
                            selectedTile.setMilitary("Mounted Calvalry", 1, "Stark");
                            JOptionPane.showMessageDialog(button, "Mounted Calvalry Trained");
                        } else if (label.equals("Buy Javelinmen") && Stark.javelinmenResearched && Stark.metal >= 10 && Stark.wood >= 0 && Stark.food >= 10 && Stark.labor >= 10) {
                            selectedTile.setMilitary("Javelinmen", 1, "Stark");
                            JOptionPane.showMessageDialog(button, "Javelinmen Trained");
                        } else if (label.equals("Buy Archer") && Stark.archerResearched && Stark.metal >= 10 && Stark.wood >= 10 && Stark.food >= 25 && Stark.labor >= 10) {
                            selectedTile.setMilitary("Archer", 1, "Stark");
                            JOptionPane.showMessageDialog(button, "Archer Trained");
                        } else if (label.equals("Buy Crossbowmen") && Stark.crossbowmenResearched && Stark.metal >= 15 && Stark.wood >= 15 && Stark.food >= 30 && Stark.labor >= 15) {
                            selectedTile.setMilitary("Crossbowmen", 1, "Stark");
                            JOptionPane.showMessageDialog(button, "Crossbowmen Built");
                        } else if (label.equals("Buy Mounted Archer") && Stark.mountedArcherResearched && Stark.metal >= 20 && Stark.wood >= 15 && Stark.food >= 35 && Stark.labor >= 20) {
                            selectedTile.setMilitary("Mounted Archer", 1, "Stark");
                            JOptionPane.showMessageDialog(button, "Mounted Archer Built");
                        } else if (label.equals("Buy Siege Tower") && Stark.siegeTowerResearched && Stark.metal >= 0 && Stark.wood >= 10 && Stark.food >= 10 && Stark.labor >= 10) {
                            selectedTile.setMilitary("Siege Tower", 1, "Stark");
                            JOptionPane.showMessageDialog(button, "Siege Tower Trained");
                        } else if (label.equals("Buy Catapult") && Stark.catapultResearched && Stark.metal >= 10 && Stark.wood >= 25 && Stark.food >= 10 && Stark.labor >= 10) {
                            selectedTile.setMilitary("Catapult", 1, "Stark");
                            JOptionPane.showMessageDialog(button, "Catapult Built");
                        } else if (label.equals("Buy Ballista") && Stark.ballistaResearched && Stark.metal >= 15 && Stark.wood >= 30 && Stark.food >= 15 && Stark.labor >= 15) {
                            selectedTile.setMilitary("Ballista", 1, "Stark");
                            JOptionPane.showMessageDialog(button, "Ballista Trained");
                        } else if (label.equals("Buy Trebuchet") && Stark.trebuchetResearched && Stark.metal >= 15 && Stark.wood >= 35 && Stark.food >= 20 && Stark.labor >= 20) {
                            selectedTile.setMilitary("Trebuchet", 1, "Stark");
                            JOptionPane.showMessageDialog(button, "Trebuchet Trained");
                        } else if (label.equals("Buy Mine") && Stark.mineResearched && Stark.metal >= 10 && Stark.wood >= 50 && Stark.food >= 20 && Stark.labor >= 20) {
                            selectedTile.setBuilding("mine", 1);
                            JOptionPane.showMessageDialog(button, "Mine Built");
                        } else if (label.equals("Buy Forge") && Stark.forgeResearched && Stark.metal >= 50 && Stark.wood >= 40 && Stark.food >= 20 && Stark.labor >= 20) {
                            selectedTile.setBuilding("forge", 1);
                            JOptionPane.showMessageDialog(button, "Forge Built");
                        } else if (label.equals("Buy Lumber Mill") && Stark.lumberMillResearched && Stark.metal >= 50 && Stark.wood >= 10 && Stark.food >= 20 && Stark.labor >= 20) {
                            selectedTile.setBuilding("lumberMill", 1);
                            JOptionPane.showMessageDialog(button, "Lumber Mill Built");
                        } else if (label.equals("Buy Deforestation") && Stark.deforestationResearched && Stark.metal >= 20 && Stark.wood >= 40 && Stark.food >= 50 && Stark.labor >= 40) {
                            selectedTile.setBuilding("deforestation", 1);
                            JOptionPane.showMessageDialog(button, "Deforestation Built");
                        } else if (label.equals("Buy Farm") && Stark.farmResearched && Stark.metal >= 20 && Stark.wood >= 40 && Stark.food >= 20 && Stark.labor >= 20) {
                            selectedTile.setBuilding("farm", 1);
                            JOptionPane.showMessageDialog(button, "Farm Built");
                        } else if (label.equals("Buy Plantation") && Stark.plantationResearched && Stark.metal >= 40 && Stark.wood >= 50 && Stark.food >= 20 && Stark.labor >= 40) {
                            selectedTile.setBuilding("plantation", 1);
                            JOptionPane.showMessageDialog(button, "Plantation Built");
                        } else if (label.equals("Buy School") && Stark.schoolResearched && Stark.metal >= 40 && Stark.wood >= 20 && Stark.food >= 20 && Stark.labor >= 20) {
                            selectedTile.setBuilding("school", 1);
                            JOptionPane.showMessageDialog(button, "School Built");
                        } else if (label.equals("Buy College") && Stark.collegeResearched && Stark.metal >= 40 && Stark.wood >= 40 && Stark.food >= 30 && Stark.labor >= 40) {
                            selectedTile.setBuilding("college", 1);
                            JOptionPane.showMessageDialog(button, "College Built");
                        } else if (label.equals("Buy Build Mines") && Stark.education >= 50 && Stark.labor >= 10) {
                            Stark.setResearch("mine", true);
                            JOptionPane.showMessageDialog(button, "Mines Researched");
                        } else if (label.equals("Buy Build Forges") && Stark.education >= 75 && Stark.labor >= 25) {
                            Stark.setResearch("forge", true);
                            JOptionPane.showMessageDialog(button, "Forges Researched");
                        } else if (label.equals("Buy Build Lumber Mills") && Stark.education >= 50 && Stark.labor >= 10) {
                            Stark.setResearch("lumberMill", true);
                            JOptionPane.showMessageDialog(button, "Lumber Mills Researched");
                        } else if (label.equals("Buy Build Deforestation") && Stark.education >= 75 && Stark.labor >= 25) {
                            Stark.setResearch("deforestation", true);
                            JOptionPane.showMessageDialog(button, "Deforestation Researched");
                        } else if (label.equals("Buy Build Farms") && Stark.education >= 50 && Stark.labor >= 10) {
                            Stark.setResearch("farm", true);
                            JOptionPane.showMessageDialog(button, "Farms Researched");
                        } else if (label.equals("Buy Build Plantations") && Stark.education >= 75 && Stark.labor >= 25) {
                            Stark.setResearch("plantation", true);
                            JOptionPane.showMessageDialog(button, "Plantations Researched");
                        } else if (label.equals("Buy Build Schools") && Stark.education >= 50 && Stark.labor >= 10) {
                            Stark.setResearch("school", true);
                            JOptionPane.showMessageDialog(button, "Schools Researched");
                        } else if (label.equals("Buy Build Colleges") && Stark.education >= 75 && Stark.labor >= 25) {
                            Stark.setResearch("college", true);
                            JOptionPane.showMessageDialog(button, "Colleges Researched");
                        } else if (label.equals("Buy Train Swordmen") && Stark.education >= 50 && Stark.labor >= 10) {
                            Stark.setResearch("swordmen", true);
                            JOptionPane.showMessageDialog(button, "Swordmen Researched");
                        } else if (label.equals("Buy Train Shieldmen") && Stark.education >= 50 && Stark.labor >= 10) {
                            Stark.setResearch("shieldmen", true);
                            JOptionPane.showMessageDialog(button, "Shieldmen Researched");
                        } else if (label.equals("Buy Train Spearmen") && Stark.education >= 75 && Stark.labor >= 25) {
                            Stark.setResearch("spearmen", true);
                            JOptionPane.showMessageDialog(button, "Spearmen Researched");
                        } else if (label.equals("Buy Train Mounted Calvalry") && Stark.education >= 100 && Stark.labor >= 50) {
                            Stark.setResearch("mountedCalvalry", true);
                            JOptionPane.showMessageDialog(button, "Mounted Calvalry Researched");
                        } else if (label.equals("Buy Train Javelinmen") && Stark.education >= 50 && Stark.labor >= 10) {
                            Stark.setResearch("javelinmen", true);
                            JOptionPane.showMessageDialog(button, "Javelinmen Researched");
                        } else if (label.equals("Buy Train Archer") && Stark.education >= 50 && Stark.labor >= 10) {
                            Stark.setResearch("archer", true);
                            JOptionPane.showMessageDialog(button, "Archer Researched");
                        } else if (label.equals("Buy Train Crossbowmen") && Stark.education >= 75 && Stark.labor >= 25) {
                            Stark.setResearch("crossbowmen", true);
                            JOptionPane.showMessageDialog(button, "Crossbowmen Researched");
                        } else if (label.equals("Buy Train Mounted Archers") && Stark.education >= 100 && Stark.labor >= 50) {
                            Stark.setResearch("mountedArcher", true);
                            JOptionPane.showMessageDialog(button, "Mounted Archers Researched");
                        } else if (label.equals("Buy Build Siege Towers") && Stark.education >= 50 && Stark.labor >= 10) {
                            Stark.setResearch("siegeTower", true);
                            JOptionPane.showMessageDialog(button, "Siege Towers Researched");
                        } else if (label.equals("Buy Build Catapults") && Stark.education >= 50 && Stark.labor >= 10) {
                            Stark.setResearch("catapult", true);
                            JOptionPane.showMessageDialog(button, "Catapults Researched");
                        } else if (label.equals("Buy Build Ballistas") && Stark.education >= 75 && Stark.labor >= 25) {
                            Stark.setResearch("ballista", true);
                            JOptionPane.showMessageDialog(button, "Ballistas Researched");
                        } else if (label.equals("Buy Build Trebuchets") && Stark.education >= 100 && Stark.labor >= 50) {
                            Stark.setResearch("trebuchet", true);
                            JOptionPane.showMessageDialog(button, "Trebuchets Researched");
                        } else {
                            JOptionPane.showMessageDialog(button, "Unable to Buy");
                            actionsTaken--;
                        }
                    } else if (Lannister.isActive()) {
                        if (label.equals("Buy Swordmen") && Lannister.swordmenResearched && Lannister.metal >= 10 && Lannister.wood >= 10 && Lannister.food >= 0 && Lannister.labor >= 10) {
                            selectedTile.setMilitary("Swordmen", 1, "Lannister");
                            JOptionPane.showMessageDialog(button, "Swordmen Trained");
                        } else if (label.equals("Buy Shieldmen") && Lannister.shieldmenResearched && Lannister.metal >= 25 && Lannister.wood >= 10 && Lannister.food >= 10 && Lannister.labor >= 10) {
                            selectedTile.setMilitary("Shieldmen", 1, "Lannister");
                            JOptionPane.showMessageDialog(button, "Shieldmen Trained");
                        } else if (label.equals("Buy Spearmen") && Lannister.spearmenResearched && Lannister.metal >= 30 && Lannister.wood >= 15 && Lannister.food >= 15 && Lannister.labor >= 15) {
                            selectedTile.setMilitary("Spearmen", 1, "Lannister");
                            JOptionPane.showMessageDialog(button, "Spearmen Trained");
                        } else if (label.equals("Buy Mounted Calvalry") && Lannister.mountedCalvalryResearched && Lannister.metal >= 35 && Lannister.wood >= 20 && Lannister.food >= 15 && Lannister.labor >= 20) {
                            selectedTile.setMilitary("Mounted Calvalry", 1, "Lannister");
                            JOptionPane.showMessageDialog(button, "Mounted Calvalry Trained");
                        } else if (label.equals("Buy Javelinmen") && Lannister.javelinmenResearched && Lannister.metal >= 10 && Lannister.wood >= 0 && Lannister.food >= 10 && Lannister.labor >= 10) {
                            selectedTile.setMilitary("Javelinmen", 1, "Lannister");
                            JOptionPane.showMessageDialog(button, "Javelinmen Trained");
                        } else if (label.equals("Buy Archer") && Lannister.archerResearched && Lannister.metal >= 10 && Lannister.wood >= 10 && Lannister.food >= 25 && Lannister.labor >= 10) {
                            selectedTile.setMilitary("Archer", 1, "Lannister");
                            JOptionPane.showMessageDialog(button, "Archer Trained");
                        } else if (label.equals("Buy Crossbowmen") && Lannister.crossbowmenResearched && Lannister.metal >= 15 && Lannister.wood >= 15 && Lannister.food >= 30 && Lannister.labor >= 15) {
                            selectedTile.setMilitary("Crossbowmen", 1, "Lannister");
                            JOptionPane.showMessageDialog(button, "Crossbowmen Built");
                        } else if (label.equals("Buy Mounted Archer") && Lannister.mountedArcherResearched && Lannister.metal >= 20 && Lannister.wood >= 15 && Lannister.food >= 35 && Lannister.labor >= 20) {
                            selectedTile.setMilitary("Mounted Archer", 1, "Lannister");
                            JOptionPane.showMessageDialog(button, "Mounted Archer Built");
                        } else if (label.equals("Buy Siege Tower") && Lannister.siegeTowerResearched && Lannister.metal >= 0 && Lannister.wood >= 10 && Lannister.food >= 10 && Lannister.labor >= 10) {
                            selectedTile.setMilitary("Siege Tower", 1, "Lannister");
                            JOptionPane.showMessageDialog(button, "Siege Tower Trained");
                        } else if (label.equals("Buy Catapult") && Lannister.catapultResearched && Lannister.metal >= 10 && Lannister.wood >= 25 && Lannister.food >= 10 && Lannister.labor >= 10) {
                            selectedTile.setMilitary("Catapult", 1, "Lannister");
                            JOptionPane.showMessageDialog(button, "Catapult Built");
                        } else if (label.equals("Buy Ballista") && Lannister.ballistaResearched && Lannister.metal >= 15 && Lannister.wood >= 30 && Lannister.food >= 15 && Lannister.labor >= 15) {
                            selectedTile.setMilitary("Ballista", 1, "Lannister");
                            JOptionPane.showMessageDialog(button, "Ballista Trained");
                        } else if (label.equals("Buy Trebuchet") && Lannister.trebuchetResearched && Lannister.metal >= 15 && Lannister.wood >= 35 && Lannister.food >= 20 && Lannister.labor >= 20) {
                            selectedTile.setMilitary("Trebuchet", 1, "Lannister");
                            JOptionPane.showMessageDialog(button, "Trebuchet Trained");
                        } else if (label.equals("Buy Mine") && Lannister.mineResearched && Lannister.metal >= 10 && Lannister.wood >= 50 && Lannister.food >= 20 && Lannister.labor >= 20) {
                            selectedTile.setBuilding("mine", 1);
                            JOptionPane.showMessageDialog(button, "Mine Built");
                        } else if (label.equals("Buy Forge") && Lannister.forgeResearched && Lannister.metal >= 50 && Lannister.wood >= 40 && Lannister.food >= 20 && Lannister.labor >= 20) {
                            selectedTile.setBuilding("forge", 1);
                            JOptionPane.showMessageDialog(button, "Forge Built");
                        } else if (label.equals("Buy Lumber Mill") && Lannister.lumberMillResearched && Lannister.metal >= 50 && Lannister.wood >= 10 && Lannister.food >= 20 && Lannister.labor >= 20) {
                            selectedTile.setBuilding("lumberMill", 1);
                            JOptionPane.showMessageDialog(button, "Lumber Mill Built");
                        } else if (label.equals("Buy Deforestation") && Lannister.deforestationResearched && Lannister.metal >= 20 && Lannister.wood >= 40 && Lannister.food >= 50 && Lannister.labor >= 40) {
                            selectedTile.setBuilding("deforestation", 1);
                            JOptionPane.showMessageDialog(button, "Deforestation Built");
                        } else if (label.equals("Buy Farm") && Lannister.farmResearched && Lannister.metal >= 20 && Lannister.wood >= 40 && Lannister.food >= 20 && Lannister.labor >= 20) {
                            selectedTile.setBuilding("farm", 1);
                            JOptionPane.showMessageDialog(button, "Farm Built");
                        } else if (label.equals("Buy Plantation") && Lannister.plantationResearched && Lannister.metal >= 40 && Lannister.wood >= 50 && Lannister.food >= 20 && Lannister.labor >= 40) {
                            selectedTile.setBuilding("plantation", 1);
                            JOptionPane.showMessageDialog(button, "Plantation Built");
                        } else if (label.equals("Buy School") && Lannister.schoolResearched && Lannister.metal >= 40 && Lannister.wood >= 20 && Lannister.food >= 20 && Lannister.labor >= 20) {
                            selectedTile.setBuilding("school", 1);
                            JOptionPane.showMessageDialog(button, "School Built");
                        } else if (label.equals("Buy College") && Lannister.collegeResearched && Lannister.metal >= 40 && Lannister.wood >= 40 && Lannister.food >= 30 && Lannister.labor >= 40) {
                            selectedTile.setBuilding("college", 1);
                            JOptionPane.showMessageDialog(button, "College Built");
                        } else if (label.equals("Buy Build Mines") && Lannister.education >= 50 && Lannister.labor >= 10) {
                            Lannister.setResearch("mine", true);
                            JOptionPane.showMessageDialog(button, "Mines Researched");
                        } else if (label.equals("Buy Build Forges") && Lannister.education >= 75 && Lannister.labor >= 25) {
                            Lannister.setResearch("forge", true);
                            JOptionPane.showMessageDialog(button, "Forges Researched");
                        } else if (label.equals("Buy Build Lumber Mills") && Lannister.education >= 50 && Lannister.labor >= 10) {
                            Lannister.setResearch("lumberMill", true);
                            JOptionPane.showMessageDialog(button, "Lumber Mills Researched");
                        } else if (label.equals("Buy Build Deforestation") && Lannister.education >= 75 && Lannister.labor >= 25) {
                            Lannister.setResearch("deforestation", true);
                            JOptionPane.showMessageDialog(button, "Deforestation Researched");
                        } else if (label.equals("Buy Build Farms") && Lannister.education >= 50 && Lannister.labor >= 10) {
                            Lannister.setResearch("farm", true);
                            JOptionPane.showMessageDialog(button, "Farms Researched");
                        } else if (label.equals("Buy Build Plantations") && Lannister.education >= 75 && Lannister.labor >= 25) {
                            Lannister.setResearch("plantation", true);
                            JOptionPane.showMessageDialog(button, "Plantations Researched");
                        } else if (label.equals("Buy Build Schools") && Lannister.education >= 50 && Lannister.labor >= 10) {
                            Lannister.setResearch("school", true);
                            JOptionPane.showMessageDialog(button, "Schools Researched");
                        } else if (label.equals("Buy Build Colleges") && Lannister.education >= 75 && Lannister.labor >= 25) {
                            Lannister.setResearch("college", true);
                            JOptionPane.showMessageDialog(button, "Colleges Researched");
                        } else if (label.equals("Buy Train Swordmen") && Lannister.education >= 50 && Lannister.labor >= 10) {
                            Lannister.setResearch("swordmen", true);
                            JOptionPane.showMessageDialog(button, "Swordmen Researched");
                        } else if (label.equals("Buy Train Shieldmen") && Lannister.education >= 50 && Lannister.labor >= 10) {
                            Lannister.setResearch("shieldmen", true);
                            JOptionPane.showMessageDialog(button, "Shieldmen Researched");
                        } else if (label.equals("Buy Train Spearmen") && Lannister.education >= 75 && Lannister.labor >= 25) {
                            Lannister.setResearch("spearmen", true);
                            JOptionPane.showMessageDialog(button, "Spearmen Researched");
                        } else if (label.equals("Buy Train Mounted Calvalry") && Lannister.education >= 100 && Lannister.labor >= 50) {
                            Lannister.setResearch("mountedCalvalry", true);
                            JOptionPane.showMessageDialog(button, "Mounted Calvalry Researched");
                        } else if (label.equals("Buy Train Javelinmen") && Lannister.education >= 50 && Lannister.labor >= 10) {
                            Lannister.setResearch("javelinmen", true);
                            JOptionPane.showMessageDialog(button, "Javelinmen Researched");
                        } else if (label.equals("Buy Train Archer") && Lannister.education >= 50 && Lannister.labor >= 10) {
                            Lannister.setResearch("crcher", true);
                            JOptionPane.showMessageDialog(button, "Archer Researched");
                        } else if (label.equals("Buy Train Crossbowmen") && Lannister.education >= 75 && Lannister.labor >= 25) {
                            Lannister.setResearch("crossbowmen", true);
                            JOptionPane.showMessageDialog(button, "Crossbowmen Researched");
                        } else if (label.equals("Buy Train Mounted Archers") && Lannister.education >= 100 && Lannister.labor >= 50) {
                            Lannister.setResearch("mountedArcher", true);
                            JOptionPane.showMessageDialog(button, "Mounted Archers Researched");
                        } else if (label.equals("Buy Build Siege Towers") && Lannister.education >= 50 && Lannister.labor >= 10) {
                            Lannister.setResearch("siegeTower", true);
                            JOptionPane.showMessageDialog(button, "Siege Towers Researched");
                        } else if (label.equals("Buy Build Catapults") && Lannister.education >= 50 && Lannister.labor >= 10) {
                            Lannister.setResearch("catapult", true);
                            JOptionPane.showMessageDialog(button, "Catapults Researched");
                        } else if (label.equals("Buy Build Ballistas") && Lannister.education >= 75 && Lannister.labor >= 25) {
                            Lannister.setResearch("ballista", true);
                            JOptionPane.showMessageDialog(button, "Ballistas Researched");
                        } else if (label.equals("Buy Build Trebuchets") && Lannister.education >= 100 && Lannister.labor >= 50) {
                            Lannister.setResearch("trebuchet", true);
                            JOptionPane.showMessageDialog(button, "Trebuchets Researched");
                        } else {
                            JOptionPane.showMessageDialog(button, "Unable to Buy");
                            actionsTaken--;
                        }
                    } else if (Targaryen.isActive()) {
                        if (label.equals("Buy Swordmen") && Targaryen.swordmenResearched && Targaryen.metal >= 10 && Targaryen.wood >= 10 && Targaryen.food >= 0 && Targaryen.labor >= 10) {
                            selectedTile.setMilitary("Swordmen", 1, "Targaryen");
                            JOptionPane.showMessageDialog(button, "Swordmen Trained");
                        } else if (label.equals("Buy Shieldmen") && Targaryen.shieldmenResearched && Targaryen.metal >= 25 && Targaryen.wood >= 10 && Targaryen.food >= 10 && Targaryen.labor >= 10) {
                            selectedTile.setMilitary("Shieldmen", 1, "Targaryen");
                            JOptionPane.showMessageDialog(button, "Shieldmen Trained");
                        } else if (label.equals("Buy Spearmen") && Targaryen.spearmenResearched && Targaryen.metal >= 30 && Targaryen.wood >= 15 && Targaryen.food >= 15 && Targaryen.labor >= 15) {
                            selectedTile.setMilitary("Spearmen", 1, "Targaryen");
                            JOptionPane.showMessageDialog(button, "Spearmen Trained");
                        } else if (label.equals("Buy Mounted Calvalry") && Targaryen.mountedCalvalryResearched && Targaryen.metal >= 35 && Targaryen.wood >= 20 && Targaryen.food >= 15 && Targaryen.labor >= 20) {
                            selectedTile.setMilitary("Mounted Calvalry", 1, "Targaryen");
                            JOptionPane.showMessageDialog(button, "Mounted Calvalry Trained");
                        } else if (label.equals("Buy Javelinmen") && Targaryen.javelinmenResearched && Targaryen.metal >= 10 && Targaryen.wood >= 0 && Targaryen.food >= 10 && Targaryen.labor >= 10) {
                            selectedTile.setMilitary("Javelinmen", 1, "Targaryen");
                            JOptionPane.showMessageDialog(button, "Javelinmen Trained");
                        } else if (label.equals("Buy Archer") && Targaryen.archerResearched && Targaryen.metal >= 10 && Targaryen.wood >= 10 && Targaryen.food >= 25 && Targaryen.labor >= 10) {
                            selectedTile.setMilitary("Archer", 1, "Targaryen");
                            JOptionPane.showMessageDialog(button, "Archer Trained");
                        } else if (label.equals("Buy Crossbowmen") && Targaryen.crossbowmenResearched && Targaryen.metal >= 15 && Targaryen.wood >= 15 && Targaryen.food >= 30 && Targaryen.labor >= 15) {
                            selectedTile.setMilitary("Crossbowmen", 1, "Targaryen");
                            JOptionPane.showMessageDialog(button, "Crossbowmen Built");
                        } else if (label.equals("Buy Mounted Archer") && Targaryen.mountedArcherResearched && Targaryen.metal >= 20 && Targaryen.wood >= 15 && Targaryen.food >= 35 && Targaryen.labor >= 20) {
                            selectedTile.setMilitary("Mounted Archer", 1, "Targaryen");
                            JOptionPane.showMessageDialog(button, "Mounted Archer Built");
                        } else if (label.equals("Buy Siege Tower") && Targaryen.siegeTowerResearched && Targaryen.metal >= 0 && Targaryen.wood >= 10 && Targaryen.food >= 10 && Targaryen.labor >= 10) {
                            selectedTile.setMilitary("Siege Tower", 1, "Targaryen");
                            JOptionPane.showMessageDialog(button, "Siege Tower Trained");
                        } else if (label.equals("Buy Catapult") && Targaryen.catapultResearched && Targaryen.metal >= 10 && Targaryen.wood >= 25 && Targaryen.food >= 10 && Targaryen.labor >= 10) {
                            selectedTile.setMilitary("Catapult", 1, "Targaryen");
                            JOptionPane.showMessageDialog(button, "Catapult Built");
                        } else if (label.equals("Buy Ballista") && Targaryen.ballistaResearched && Targaryen.metal >= 15 && Targaryen.wood >= 30 && Targaryen.food >= 15 && Targaryen.labor >= 15) {
                            selectedTile.setMilitary("Ballista", 1, "Targaryen");
                            JOptionPane.showMessageDialog(button, "Ballista Trained");
                        } else if (label.equals("Buy Trebuchet") && Targaryen.trebuchetResearched && Targaryen.metal >= 15 && Targaryen.wood >= 35 && Targaryen.food >= 20 && Targaryen.labor >= 20) {
                            selectedTile.setMilitary("Trebuchet", 1, "Targaryen");
                            JOptionPane.showMessageDialog(button, "Trebuchet Trained");
                        } else if (label.equals("Buy Mine") && Targaryen.mineResearched && Targaryen.metal >= 10 && Targaryen.wood >= 50 && Targaryen.food >= 20 && Targaryen.labor >= 20) {
                            selectedTile.setBuilding("mine", 1);
                            JOptionPane.showMessageDialog(button, "Mine Built");
                        } else if (label.equals("Buy Forge") && Targaryen.forgeResearched && Targaryen.metal >= 50 && Targaryen.wood >= 40 && Targaryen.food >= 20 && Targaryen.labor >= 20) {
                            selectedTile.setBuilding("forge", 1);
                            JOptionPane.showMessageDialog(button, "Forge Built");
                        } else if (label.equals("Buy Lumber Mill") && Targaryen.lumberMillResearched && Targaryen.metal >= 50 && Targaryen.wood >= 10 && Targaryen.food >= 20 && Targaryen.labor >= 20) {
                            selectedTile.setBuilding("lumberMill", 1);
                            JOptionPane.showMessageDialog(button, "Lumber Mill Built");
                        } else if (label.equals("Buy Deforestation") && Targaryen.deforestationResearched && Targaryen.metal >= 20 && Targaryen.wood >= 40 && Targaryen.food >= 50 && Targaryen.labor >= 40) {
                            selectedTile.setBuilding("deforestation", 1);
                            JOptionPane.showMessageDialog(button, "Deforestation Built");
                        } else if (label.equals("Buy Farm") && Targaryen.farmResearched && Targaryen.metal >= 20 && Targaryen.wood >= 40 && Targaryen.food >= 20 && Targaryen.labor >= 20) {
                            selectedTile.setBuilding("farm", 1);
                            JOptionPane.showMessageDialog(button, "Farm Built");
                        } else if (label.equals("Buy Plantation") && Targaryen.plantationResearched && Targaryen.metal >= 40 && Targaryen.wood >= 50 && Targaryen.food >= 20 && Targaryen.labor >= 40) {
                            selectedTile.setBuilding("plantation", 1);
                            JOptionPane.showMessageDialog(button, "Plantation Built");
                        } else if (label.equals("Buy School") && Targaryen.schoolResearched && Targaryen.metal >= 40 && Targaryen.wood >= 20 && Targaryen.food >= 20 && Targaryen.labor >= 20) {
                            selectedTile.setBuilding("school", 1);
                            JOptionPane.showMessageDialog(button, "School Built");
                        } else if (label.equals("Buy College") && Targaryen.collegeResearched && Targaryen.metal >= 40 && Targaryen.wood >= 40 && Targaryen.food >= 30 && Targaryen.labor >= 40) {
                            selectedTile.setBuilding("college", 1);
                            JOptionPane.showMessageDialog(button, "College Built");
                        } else if (label.equals("Buy Build Mines") && Targaryen.education >= 50 && Targaryen.labor >= 10) {
                            Targaryen.setResearch("mine", true);
                            JOptionPane.showMessageDialog(button, "Mines Researched");
                        } else if (label.equals("Buy Build Forges") && Targaryen.education >= 75 && Targaryen.labor >= 25) {
                            Targaryen.setResearch("forge", true);
                            JOptionPane.showMessageDialog(button, "Forges Researched");
                        } else if (label.equals("Buy Build Lumber Mills") && Targaryen.education >= 50 && Targaryen.labor >= 10) {
                            Targaryen.setResearch("lumberMill", true);
                            JOptionPane.showMessageDialog(button, "Lumber Mills Researched");
                        } else if (label.equals("Buy Build Deforestation") && Targaryen.education >= 75 && Targaryen.labor >= 25) {
                            Targaryen.setResearch("deforestation", true);
                            JOptionPane.showMessageDialog(button, "Deforestation Researched");
                        } else if (label.equals("Buy Build Farms") && Targaryen.education >= 50 && Targaryen.labor >= 10) {
                            Targaryen.setResearch("farm", true);
                            JOptionPane.showMessageDialog(button, "Farms Researched");
                        } else if (label.equals("Buy Build Plantations") && Targaryen.education >= 75 && Targaryen.labor >= 25) {
                            Targaryen.setResearch("plantation", true);
                            JOptionPane.showMessageDialog(button, "Plantations Researched");
                        } else if (label.equals("Buy Build Schools") && Targaryen.education >= 50 && Targaryen.labor >= 10) {
                            Targaryen.setResearch("school", true);
                            JOptionPane.showMessageDialog(button, "Schools Researched");
                        } else if (label.equals("Buy Build Colleges") && Targaryen.education >= 75 && Targaryen.labor >= 25) {
                            Targaryen.setResearch("college", true);
                            JOptionPane.showMessageDialog(button, "Colleges Researched");
                        } else if (label.equals("Buy Train Swordmen") && Targaryen.education >= 50 && Targaryen.labor >= 10) {
                            Targaryen.setResearch("swordmen", true);
                            JOptionPane.showMessageDialog(button, "Swordmen Researched");
                        } else if (label.equals("Buy Train Shieldmen") && Targaryen.education >= 50 && Targaryen.labor >= 10) {
                            Targaryen.setResearch("shieldmen", true);
                            JOptionPane.showMessageDialog(button, "Shieldmen Researched");
                        } else if (label.equals("Buy Train Spearmen") && Targaryen.education >= 75 && Targaryen.labor >= 25) {
                            Targaryen.setResearch("spearmen", true);
                            JOptionPane.showMessageDialog(button, "Spearmen Researched");
                        } else if (label.equals("Buy Train Mounted Calvalry") && Targaryen.education >= 100 && Targaryen.labor >= 50) {
                            Targaryen.setResearch("mountedCalvalry", true);
                            JOptionPane.showMessageDialog(button, "Mounted Calvalry Researched");
                        } else if (label.equals("Buy Train Javelinmen") && Targaryen.education >= 50 && Targaryen.labor >= 10) {
                            Targaryen.setResearch("javelinmen", true);
                            JOptionPane.showMessageDialog(button, "Javelinmen Researched");
                        } else if (label.equals("Buy Train Archer") && Targaryen.education >= 50 && Targaryen.labor >= 10) {
                            Targaryen.setResearch("crcher", true);
                            JOptionPane.showMessageDialog(button, "Archer Researched");
                        } else if (label.equals("Buy Train Crossbowmen") && Targaryen.education >= 75 && Targaryen.labor >= 25) {
                            Targaryen.setResearch("crossbowmen", true);
                            JOptionPane.showMessageDialog(button, "Crossbowmen Researched");
                        } else if (label.equals("Buy Train Mounted Archers") && Targaryen.education >= 100 && Targaryen.labor >= 50) {
                            Targaryen.setResearch("mountedArcher", true);
                            JOptionPane.showMessageDialog(button, "Mounted Archers Researched");
                        } else if (label.equals("Buy Build Siege Towers") && Targaryen.education >= 50 && Targaryen.labor >= 10) {
                            Targaryen.setResearch("siegeTower", true);
                            JOptionPane.showMessageDialog(button, "Siege Towers Researched");
                        } else if (label.equals("Buy Build Catapults") && Targaryen.education >= 50 && Targaryen.labor >= 10) {
                            Targaryen.setResearch("catapult", true);
                            JOptionPane.showMessageDialog(button, "Catapults Researched");
                        } else if (label.equals("Buy Build Ballistas") && Targaryen.education >= 75 && Targaryen.labor >= 25) {
                            Targaryen.setResearch("ballista", true);
                            JOptionPane.showMessageDialog(button, "Ballistas Researched");
                        } else if (label.equals("Buy Build Trebuchets") && Targaryen.education >= 100 && Targaryen.labor >= 50) {
                            Targaryen.setResearch("trebuchet", true);
                            JOptionPane.showMessageDialog(button, "Trebuchets Researched");
                        } else {
                            JOptionPane.showMessageDialog(button, "Unable to Buy");
                            actionsTaken--;
                        }
                    }
                }
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    //TODO://///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Server Code
    private static class ServerRunner implements Runnable {
        private static Map<String, PlayerHandler> players = new ConcurrentHashMap<>();
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(12345)) {
                System.out.println("DND Server is running on port 12345");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    PlayerHandler playerHandler = new PlayerHandler(clientSocket);
                    new Thread(playerHandler).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static synchronized void broadcast(String message, PlayerHandler sender) {
            for (PlayerHandler player : players.values()) {
                if (player != sender) {
                    player.sendMessage(message);
                }
            }
        }

        public static synchronized void updatePlayerMove(String playerName, String move) {
            String message = "UPDATE: " + move;
            broadcast(message, players.get(playerName));
        }

        public static synchronized void sendBoard(String playerName, String move) {
            String message = "BOARD" + move;
            broadcast(message, players.get(playerName));
        }

        public static synchronized void addPlayer(String playerName, PlayerHandler playerHandler) {
            players.put(playerName, playerHandler);
        }

        public static synchronized void removePlayer(String playerName) {
            players.remove(playerName);
        }
    }

    private static class PlayerHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String playerName;

        public PlayerHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(),true);

                playerName = currentNation;

                //Deal with moves here
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("MOVE:")) {
                        String move = message.substring(6);
                        String[] parts = move.split("\\|");
                        playerName = parts[0];
                        System.out.println("Server: received move from " + playerName);
                        ServerRunner.sendBoard(playerName,parts[1].substring(5));
                    } else if (message.startsWith("JOIN:")) {
                        playerName = message.substring(6);
                        ServerRunner.addPlayer(playerName, this);
                        System.out.println(playerName + " has joined the game.");
                        if (!playerName.equals(currentNation)) {
                            System.out.println("Server: Requesting board");
                            ServerRunner.sendBoard(playerName,": Request");
                            //ServerRunner.broadcast("BOARD: Request",ServerRunner.players.get(playerName));
                        }
                    } else if (message.startsWith("BOARD[")) {
                        System.out.println("Server: Recieved board");
                        ServerRunner.sendBoard(playerName,message.substring(5));
                        //ServerRunner.broadcast(message,ServerRunner.players.get(playerName));
                    } else if (message.startsWith("INITIALIZE:")){
                        String values = message.substring(11);
                        String[] parts = values.split(",");
                        ServerRunner.broadcast(message,ServerRunner.players.get(parts[0]));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    ServerRunner.removePlayer(playerName);
                    socket.close();
                    System.out.println(playerName + " has left the game.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }

    private static class ClientRunner implements Runnable {
        public void run() {
            try {
                Socket socket;
                if (ip == null) {
                    socket = new Socket("localhost",12345);
                } else {
                    socket = new Socket(ip,12345);
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

                new Thread(() -> {
                    String serverMessage;
                    try {
                        while ((serverMessage = in.readLine()) != null) {
                            if (serverMessage.startsWith("UPDATE:")) {
                                String[] parts = serverMessage.split(",");
                                System.out.println("Client: " + serverMessage);
                            } else if (serverMessage.equals("BOARD: Request")) {
                                System.out.println("Client: Sending board");
                                out.println(getGameState());
                                out.println("INITIALIZE:" + currentNation + "," + startingResources.get(0) + "," + startingResources.get(1) + "," + startingResources.get(2) + "," + startingResources.get(3) + "," + startingResources.get(4));

                            } else if (serverMessage.startsWith("BOARD[")) {
                                System.out.println("Client: Received board\n" + serverMessage);
                                tiles = CreateBoard(serverMessage);
                                actionsTaken = 0;
                            } else if (serverMessage.startsWith("INITIALIZE:")) {
                                String initilizeStr = serverMessage.substring(11);
                                Initialize(initilizeStr);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        appFrame.setVisible(false);
                        openStartScreen();
                    }
                }).start();

                String move = "";
                String lastMove = "";
                if (!joined) {
                    move = "JOIN: " + currentNation;
                    out.println(move);
                    joined = true;
                }
                while (true) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ie) {}
                    if (actionsTaken == maxActions && joined) {
                        System.out.println("Client: Sending moves");
                        actionsTaken = maxActions + 1;
                        out.println("MOVE: " + currentNation + "|" + getGameState());
                    } else if (attacking) {
                        out.println("ATTACK");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                appFrame.setVisible(false);
                openStartScreen();
            }
        }
    }

    //TODO://///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Path Code
    private static class PathItem {
        Tile node;
        Vector<Tile> path;
        int cost;
        public PathItem(Tile node, Vector<Tile> path, int cost) {
            this.node = node;
            this.path = path;
            this.cost = cost;
        }
    }

    private static Vector<Tile> getPath(Tile a, Tile b) {
        Line2D.Double straight = new Line2D.Double(a.getCenter(),b.getCenter());
        Vector<Tile> explored = new Vector<>();
        Comparator<PathItem> comp = new Comparator<PathItem>() {
            @Override
            public int compare(PathItem o1, PathItem o2) {
                if (o1.node.getCenter().distance(b.getCenter()) + o1.cost > o2.node.getCenter().distance(b.getCenter()) + o2.cost) {
                    return 1;
                } else if (o1.node.getCenter().distance(b.getCenter()) + o1.cost < o2.node.getCenter().distance(b.getCenter()) + o2.cost) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
        PriorityQueue<PathItem> fringe = new PriorityQueue<>(comp);
        fringe.add(new PathItem(a,new Vector<Tile>(),0));
        while(true) {
            if (fringe.isEmpty()) return null;
            PathItem current = fringe.remove();
            Tile node = current.node;
            Vector<Tile> path = current.path;
//            System.out.println(fringe.size());
            int totalCost = current.cost;
            if (node.equals(b)) {
                path.add(node);
                return path;
            }
            if (!explored.contains(node)) {
                explored.add(node);
                for (int i = 0; i < node.neighbors.size(); i++) {
                    Vector<Tile> newPath = new Vector<>(path);
                    newPath.add(node);
                    totalCost+=10;
                    PathItem next = new PathItem(node.neighbors.get(i),newPath,totalCost);
                    fringe.add(next);
                }
            }
        }





//        Tile current = a;
//        while (!path.get(path.size()-1).equals(b.getCenter())) {
//            Tile bestNext = current.neighbors.get(0);
//            double bestDist = straight.ptLineDist(bestNext.getCenter());
//            for (int i = 0; i < current.neighbors.size(); i++) {
//                double dist = straight.ptLineDist(current.neighbors.get(i).getCenter());
//                if (dist < bestDist) {
//                    if (path.contains(current.neighbors.get(i).getCenter())) continue;
//                    bestNext = current.neighbors.get(i);
//                    bestDist = dist;
//
//                }
//            }
//            path.add(bestNext.getCenter());
//        }
//        return path;
    }

    private static void drawPath(Graphics2D g2d, Tile current) {
        Vector<Tile> path = getPath(pathBeginning,current);
        for (int i = 0; i < path.size()-1; i++) {
            g2d.drawLine((int)path.get(i).getCenter().getX(),(int)path.get(i).getCenter().getY(),(int)path.get(i+1).getCenter().getX(),(int)path.get(i+1).getCenter().getY());
        }
    }

//    private static Line2D.Double getPath(Tile a, Tile b) {
//        Line2D.Double straight = new Line2D.Double(a.getCenter(),b.getCenter());
//        return straight;
//    }

    //TODO://///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Board Code
    private static class Animate implements Runnable {
        public void run() {
            while (endgame == false) {
                tileDraw();
                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) {
                    if (!background_drawn) {
                        backgroundDraw();
                    }
                }
            }
        }
    }

    //  Throws an image as the background and creates the resource labels on the MenuBar
    private static void backgroundDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(background, XOFFSET, YOFFSET + 25, null);
        background_drawn = true;
        if (Stark.isActive()) {
            metalLabel.setText("    Metal: " + Stark.metal);
            woodLabel.setText("    Wood: " + Stark.wood);
            foodLabel.setText("    Food: " + Stark.food);
            laborLabel.setText("    Labor: " + Stark.labor);
            educationLabel.setText("    Education: " + Stark.education);
        } else if (Lannister.isActive()) {
            metalLabel.setText("    Metal: " + Lannister.metal);
            woodLabel.setText("    Wood: " + Lannister.wood);
            foodLabel.setText("    Food: " + Lannister.food);
            laborLabel.setText("    Labor: " + Lannister.labor);
            educationLabel.setText("    Education: " + Lannister.education);
        } else if (Targaryen.isActive()) {
            metalLabel.setText("    Metal: " + Targaryen.metal);
            woodLabel.setText("    Wood: " + Targaryen.wood);
            foodLabel.setText("    Food: " + Targaryen.food);
            laborLabel.setText("    Labor: " + Targaryen.labor);
            educationLabel.setText("    Education: " + Targaryen.education);
        }
        actionLabel.setText("    Actions: " + (maxActions-actionsTaken));
    }

    private static void tileDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        for (int i = 0; i < tiles.size(); i++) {
            Tile current = tiles.get(i);
            if (current.owner.equals(currentNation)) {
                if (current.mouseHover) {
//                    System.out.println((current.x + current.getWidth() / 2) + " " + (current.y - current.getHeight() / 2) + "\n" + board.mouseX + " " + board.mouseY);
                    if (current.myBiome == biome.Flatland) {
                        g2d.drawImage(rotateImageObject(current).filter(player_flat_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Forest) {
                        g2d.drawImage(rotateImageObject(current).filter(player_forest_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Mountain) {
                        g2d.drawImage(rotateImageObject(current).filter(player_mount_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Desert) {
                        g2d.drawImage(rotateImageObject(current).filter(player_desert_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    }
//                    System.out.println(current.toString());
                } else {
                    if (current.myBiome == biome.Flatland) {
                        g2d.drawImage(rotateImageObject(current).filter(player_flat_tile_small, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Forest) {
                        g2d.drawImage(rotateImageObject(current).filter(player_forest_tile_small, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Mountain) {
                        g2d.drawImage(rotateImageObject(current).filter(player_mount_tile_small, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Desert) {
                        g2d.drawImage(rotateImageObject(current).filter(player_desert_tile_small, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    }
                }
            } else if (current.owner.equals("")) {
                if (current.mouseHover) {
//                    System.out.println((current.x + current.getWidth() / 2) + " " + (current.y - current.getHeight() / 2) + "\n" + board.mouseX + " " + board.mouseY);
                    if (!movingUnits.isEmpty()) {
                        drawPath(g2d,current);
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ie) {}
                    if (current.myBiome == biome.Flatland) {
                        g2d.drawImage(rotateImageObject(current).filter(flat_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Forest) {
                        g2d.drawImage(rotateImageObject(current).filter(forest_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Mountain) {
                        g2d.drawImage(rotateImageObject(current).filter(mount_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Desert) {
                        g2d.drawImage(rotateImageObject(current).filter(desert_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    }
//                    System.out.println(current.toString());
                } else {
                    if (current.myBiome == biome.Flatland) {
                        g2d.drawImage(rotateImageObject(current).filter(flat_tile_small, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Forest) {
                        g2d.drawImage(rotateImageObject(current).filter(forest_tile_small, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Mountain) {
                        g2d.drawImage(rotateImageObject(current).filter(mount_tile_small, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Desert) {
                        g2d.drawImage(rotateImageObject(current).filter(desert_tile_small, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    }
                }
            } else {
                if (current.mouseHover) {
//                    System.out.println((current.x + current.getWidth() / 2) + " " + (current.y - current.getHeight() / 2) + "\n" + board.mouseX + " " + board.mouseY);
                    if (current.myBiome == biome.Flatland) {
                        g2d.drawImage(rotateImageObject(current).filter(enemy_flat_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Forest) {
                        g2d.drawImage(rotateImageObject(current).filter(enemy_forest_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Mountain) {
                        g2d.drawImage(rotateImageObject(current).filter(enemy_mount_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Desert) {
                        g2d.drawImage(rotateImageObject(current).filter(enemy_desert_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    }
//                    System.out.println(current.toString());
                } else {
                    if (current.myBiome == biome.Flatland) {
                        g2d.drawImage(rotateImageObject(current).filter(enemy_flat_tile_small, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Forest) {
                        g2d.drawImage(rotateImageObject(current).filter(enemy_forest_tile_small, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Mountain) {
                        g2d.drawImage(rotateImageObject(current).filter(enemy_mount_tile_small, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    } else if (current.myBiome == biome.Desert) {
                        g2d.drawImage(rotateImageObject(current).filter(enemy_desert_tile_small, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                    }
                }
            }
        }
    }

    private static void Initialize(String resources) {
        actionsTaken = 11;
        String parts[] = resources.split(",");
        if (currentNation.equals("Stark")) {
            Stark.setResource("metal", Integer.parseInt(parts[1]));
            Stark.setResource("wood", Integer.parseInt(parts[2]));
            Stark.setResource("food", Integer.parseInt(parts[3]));
            Stark.setResource("labor", Integer.parseInt(parts[4]));
            Stark.setResource("education", Integer.parseInt(parts[5]));
        }
        if (currentNation.equals("Lannister")) {
            Lannister.setResource("metal", Integer.parseInt(parts[1]));
            Lannister.setResource("wood", Integer.parseInt(parts[2]));
            Lannister.setResource("food", Integer.parseInt(parts[3]));
            Lannister.setResource("labor", Integer.parseInt(parts[4]));
            Lannister.setResource("education", Integer.parseInt(parts[5]));
        }
        if (currentNation.equals("Targaryen")) {
            Targaryen.setResource("metal", Integer.parseInt(parts[1]));
            Targaryen.setResource("wood", Integer.parseInt(parts[2]));
            Targaryen.setResource("food", Integer.parseInt(parts[3]));
            Targaryen.setResource("labor", Integer.parseInt(parts[4]));
            Targaryen.setResource("education", Integer.parseInt(parts[5]));
        }
    }

    private static String getGameState() {
        String gameState = "BOARD[";
        for (Tile tile: tiles) {
            biome biomeEnum = tile.getBiome();
            gameState = gameState + "(" + tile.owner + "," + tile.getX() + "," + tile.getY() + "," + biomeEnum.name() + "," + tile.mine + "," + tile.forge + "," + tile.lumberMill + "," + tile.deforestation + "," + tile.farm + "," + tile.plantation + "," + tile.school + "," + tile.college;
            for (int i = 0; i < tile.occupyingUnits.size(); i++) {
                gameState += "," + tile.occupyingUnits.get(i).unitName + "," + tile.occupyingUnits.get(i).owner;
            }
            gameState += ")";
        }
        gameState = gameState + "]";
        return gameState;
    }

    private static Vector<Tile> CreateBoard(String board) {
        Vector<Tile> tiles = new Vector<>();
        board = board.substring(6);
//        System.out.println(board);
        String[] tileArr = board.split("\\(");
        for (String tile: tileArr) {
            tile = tile.replace("]","");
            if (tile.equals(tileArr[0])) continue;
            String[] items = tile.split(",");
            items[items.length-1] = items[items.length-1].replace(")","");
//            System.out.println(tileArr[1]);
            Tile current = new Tile(items[0],Double.parseDouble(items[1]),Double.parseDouble(items[2]),tile_small.getWidth(),tile_small.getHeight(),0,items[3],Integer.parseInt(items[4]),Integer.parseInt(items[5]),Integer.parseInt(items[6]),Integer.parseInt(items[7]),Integer.parseInt(items[8]),Integer.parseInt(items[9]),Integer.parseInt(items[10]),Integer.parseInt(items[11]),Arrays.copyOfRange(items,12,items.length));
            tiles.add(current);
        }
        for (Tile tile: tiles) {
            tile.findNeighbors(tiles);
            tile.isWaterSide();
            tile.setResourceRates();
            tile.addResources();
        }
        return tiles;
    }

    private static Vector<Tile> CreateBoard(int numTiles) {
        Vector<Tile> tiles = new Vector<>();
        Random rand = new Random(System.currentTimeMillis());
        int loc;
        int fails = 0;
        tiles.addElement(new Tile(WINWIDTH/2,WINHEIGHT/2,tile_small.getWidth(),tile_small.getHeight(),0));
        for (int i = 0; i < numTiles-1; i++) {
            ImageObject current = tiles.get(i);
            Point2D.Double checkPoint;
            boolean createTile = true;
            loc = rand.nextInt(6);
            if (fails > 6) {
                current = tiles.get(rand.nextInt(tiles.size()-1));
            }
            if (loc == 0) {
                checkPoint = new Point2D.Double(current.getX(),(current.getY() - current.getHeight() - 2));
            } else if (loc == 1) {
                checkPoint = new Point2D.Double((current.getX() + (current.getWidth()*0.75) + Math.sqrt(2)),(current.getY() - (current.getHeight() * 0.5) - Math.sqrt(2)));
            } else if (loc == 2) {
                checkPoint = new Point2D.Double((current.getX() + (current.getWidth() * 0.75) + Math.sqrt(2)),(current.getY() + (current.getHeight() * 0.5) + Math.sqrt(2)));
            } else if (loc == 3) {
                checkPoint = new Point2D.Double(current.getX(),(current.getY() + current.getHeight() + 2));
            } else if (loc == 4) {
                checkPoint = new Point2D.Double((current.getX() - (current.getWidth() * 0.75) - Math.sqrt(2)),(current.getY() + (current.getHeight() * 0.5) + Math.sqrt(2)));
            } else if (loc == 5) {
                checkPoint = new Point2D.Double((current.getX() - (current.getWidth() * 0.75) - Math.sqrt(2)),(current.getY() - (current.getHeight() * 0.5) - Math.sqrt(2)));
            } else {
                checkPoint = new Point2D.Double();
            }
            for (int j = 0; j < tiles.size(); j++) {
                if (checkPoint.getX() < 0 || checkPoint.getX() + tile_small.getWidth() > WINWIDTH || checkPoint.getY() > appFrame.getHeight() - YOFFSET - 25 || checkPoint.getY() - tile_small.getHeight() < 40 || isInside(checkPoint.getX() + (tile_small.getWidth() * 0.5),checkPoint.getY() - (tile_small.getHeight() * 0.5),tiles.get(j).getX(),tiles.get(j).getY(),tiles.get(j).getX()+tile_small.getWidth(),tiles.get(j).getY()-tile_small.getHeight())) {
                    createTile = false;
                    i--;
                    fails++;
                    break;
                }
            }
            if (createTile) {
                tiles.addElement(new Tile(checkPoint.getX(),checkPoint.getY(),tile_small.getWidth(),tile_small.getHeight(),0));
                fails = 0;
            }
        }
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).findNeighbors(tiles);
            tiles.get(i).generateBiome();
            tiles.get(i).isWaterSide();
            tiles.get(i).setResourceRates();
            tiles.get(i).setOwner(i);
            tiles.get(i).addResources();
        }
        return tiles;
    }

    private static class TileMover implements Runnable {
        public void run() {
            int off_tile = -1;
            while (!endgame) {
                for (int i = 0; i < tiles.size(); i++) {
                    tiles.get(i).screenContain();
                    if (off_tile == -1 && (tileFixX != 0 || tileFixY != 0)) {
                        off_tile = i;
                    }
                }
                if (off_tile != -1) {
                    for (int i = 0; i < off_tile; i++) {
                        tiles.get(i).screenContain();
                    }
                    off_tile = -1;
                    tileFixY = 0;
                    tileFixX = 0;
                }
            }
        }
    }

    private static AffineTransformOp rotateImageObject(ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getAngle(), obj.getWidth()/2.0, obj.getHeight()/2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

    private static Boolean isInside(double p1x, double p1y, double p2x1, double p2y1, double p2x2, double p2y2) {
        Boolean ret = false;
        if (p1x > p2x1 && p1x < p2x2) {
            if (p1y > p2y1 && p1y < p2y2) {
                ret = true;
            }
            if (p1y > p2y2 && p1y < p2y1) {
                ret = true;
            }
        }
        if (p1x > p2x2 && p1x < p2x1) {
            if (p1y > p2y1 && p1y < p2y2) {
                ret = true;
            }
            if (p1y > p2y2 && p1y < p2y1) {
                ret = true;
            }
        }
        return ret;
    }

    public static class MouseTrackerPanel extends JPanel {

        private double mouseX = 0;
        private double mouseY = 0;
        private Point lastPosition;
        private double deltaX = 0;
        private double deltaY = 0;

        public MouseTrackerPanel() {
            // Add the MouseMotionListener to track mouse movement
            addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    // Update the coordinates whenever the mouse moves
                    mouseX = e.getX();
                    mouseY = e.getY();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (lastPosition != null) {
                        mousePressed = false;
                        deltaX = e.getX() - lastPosition.getX();
                        deltaY = e.getY() - lastPosition.getY();
                        if (deltaX != 0 || deltaY != 0) {
                            for (int i = 0; i < tiles.size(); i++) {
                                if (deltaX <= 0 && deltaY <= 0) {
                                    tiles.get(i).move(Math.max(deltaX,-5),Math.max(deltaY,-5));
                                } else if (deltaX <= 0 && deltaY >= 0) {
                                    tiles.get(i).move(Math.max(deltaX,-5),Math.min(deltaY,5));
                                } else if (deltaX >= 0 && deltaY <= 0) {
                                    tiles.get(i).move(Math.min(deltaX,5),Math.max(deltaY,-5));
                                } else {
                                    tiles.get(i).move(Math.min(deltaX,5),Math.min(deltaY,5));
                                }
                            }
                        }
                        backgroundDraw();
                        lastPosition = e.getPoint();
                    }
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    lastPosition = e.getPoint();
                    mousePressed = true;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {}
                    lastPosition = null;
                    mousePressed = false;
                    deltaX = 0;
                    deltaY = 0;
                }
            });
        }

        public double getMouseX() {return mouseX;}
        public double getMouseY() {return mouseY;}

    }

    private static class MouseOverChecker implements Runnable {
        public void run() {
            while (!endgame) {
                try {
                    Thread.sleep(32);
                    for (int i = 0; i < tiles.size(); i++) {
                        if (MouseOver(tiles.get(i))) {
                            tiles.get(i).mouseHover = true;
//                            System.out.println(tiles.get(i).toString());
                            if (mousePressed) {
                                mousePressed = false;
                                if (movingUnits.isEmpty()) {
                                    OpenTileMenu(tiles.get(i));
                                } else {
                                    OpenConfirmMovementMenu(tiles.get(i));
                                }
                            }

                        } else if (tiles.get(i).mouseHover){
                            backgroundDraw();
                            tiles.get(i).mouseHover = false;
                        }
                    }
                } catch (java.lang.InterruptedException jlaioobe) {}
            }
        }
    }

    private static Boolean MouseOver(ImageObject p2) {
        Boolean ret = false;
        double p1x = board.getMouseX();
        double p1y = board.getMouseY();
        double p2x1 = p2.getX();
        double p2x2 = p2.getX() + p2.getWidth();
        double p2y1 = p2.getY();
        double p2y2 = p2.getY() - p2.getHeight();
        ret = isInside(p1x,p1y,p2x1,p2y1,p2x2,p2y2);
        return ret;
    }

    //TODO://///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Attack Animation Code
    public static class AttackFrame extends JFrame {
        private CritterModel myModel;
        private AttackPanel myPicture;
        private javax.swing.Timer myTimer;
        private JButton[] counts;
        private JButton countButton;
        private boolean started;
        private static boolean created;

        public AttackFrame(int width, int height) {
            // this prevents someone from trying to create their own copy of
            // the GUI components
            if (created)
                throw new RuntimeException("Only one world allowed");
            created = true;

            // create frame and model
            setTitle("EGR222 critter simulation");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            myModel = new CritterModel(width, height);

            // set up critter picture panel
            myPicture = new AttackPanel(myModel);
            add(myPicture, BorderLayout.CENTER);

            addTimer();

            constructSouth();

            // initially it has not started
            started = false;
        }

        // construct the controls and label for the southern panel
        private void constructSouth() {
            // add timer controls to the south
            JPanel p = new JPanel();

            final JSlider slider = new JSlider();
            slider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    double ratio = 1000.0 / (1 + Math.pow(slider.getValue(), 0.3));
                    myTimer.setDelay((int) (ratio - 180));
                }
            });
            slider.setValue(20);
            p.add(new JLabel("slow"));
            p.add(slider);
            p.add(new JLabel("fast"));

            JButton b1 = new JButton("start");
            b1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    myTimer.start();
                }
            });
            p.add(b1);
            add(p, BorderLayout.SOUTH);
        }

        // starts the simulation...assumes all critters have already been added
        public void start() {
            // don't let anyone start a second time and remember if we have started
            if (started) {
                return;
            }
            // if they didn't add any critters, then nothing to do
            if (myModel.getCounts().isEmpty()) {
                System.out.println("nothing to simulate--no critters");
                return;
            }
            started = true;
            myModel.updateColorString();
            pack();
            setVisible(true);
        }

        // add a certain number of critters of a particular class to the simulation
        public void add(int number, Class<? extends Critter> c) {
            // don't let anyone add critters after simulation starts
            if (started) {
                return;
            }
            // temporarily turning on started flag prevents critter constructors
            // from calling add
            started = true;
            myModel.add(number, c);
            started = false;
        }

        // post: creates a timer that calls the model's update
        //       method and repaints the display
        private void addTimer() {
            ActionListener updater = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doOneStep();
                }
            };
            myTimer = new javax.swing.Timer(0, updater);
            myTimer.setCoalesce(true);
        }

        // one step of the simulation
        private void doOneStep() {
            myModel.update();
            myPicture.repaint();
        }
    }

    public static class AttackPanel extends JPanel {
        private CritterModel myModel;
        private Font myFont;
        private static boolean created;

        public static final int FONT_SIZE = 12;

        public AttackPanel(CritterModel model) {
            // this prevents someone from trying to create their own copy of
            // the GUI components
            if (created)
                throw new RuntimeException("Only one world allowed");
            created = true;

            myModel = model;
            // construct font and compute char width once in constructor
            // for efficiency
            myFont = new Font("Monospaced", Font.BOLD, FONT_SIZE + 4);
            setBackground(colorBackground);
            setPreferredSize(new Dimension(FONT_SIZE * model.getWidth() + 20,
                    FONT_SIZE * model.getHeight() + 20));
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setFont(myFont);
            Iterator<Critter> i = myModel.iterator();
            while (i.hasNext()) {
                Critter next = i.next();
                Point p = myModel.getPoint(next);
                String appearance = myModel.getAppearance(next);
                g.setColor(Color.BLACK);
                g.drawString("" + appearance, p.x * FONT_SIZE + 11,
                        p.y * FONT_SIZE + 21);
                g.setColor(myModel.getColor(next));
                g.drawString("" + appearance, p.x * FONT_SIZE + 10,
                        p.y * FONT_SIZE + 20);
            }
        }
    }

    //TODO://///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //     Startup Code
    public static void setup() {
        appFrame = new JFrame("Capstone");
        twoPi = 2.0 * 3.14159265358979;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        XOFFSET = 0;
        YOFFSET = 30; //30
        WINWIDTH = (int)screenSize.getWidth(); //338
        WINHEIGHT = (int)screenSize.getHeight() - 40; //271
        endgame = false;
        hosting = false;

        try {
            background = ImageIO.read(new File("images\\ocean.png"));
            scrollBackground = ImageIO.read(new File("images\\file.png"));
            tile = ImageIO.read(new File("images\\tile.png"));
            tile_small = ImageIO.read(new File("images\\tile_small.png"));
            flat_tile = ImageIO.read(new File("images\\flatland_tile.png"));
            flat_tile_small = ImageIO.read(new File("images\\flatland_tile_small.png"));
            forest_tile = ImageIO.read(new File("images\\forest_tile.png"));
            forest_tile_small = ImageIO.read(new File("images\\forest_tile_small.png"));
            mount_tile = ImageIO.read(new File("images\\mountain_tile.png"));
            mount_tile_small = ImageIO.read(new File("images\\mountain_tile_small.png"));
            desert_tile = ImageIO.read(new File("images\\desert_tile.png"));
            desert_tile_small = ImageIO.read(new File("images\\desert_tile_small.png"));
            player_flat_tile = ImageIO.read(new File("images\\player_flatland_tile.png"));
            enemy_flat_tile = ImageIO.read(new File("images\\enemy_flatland_tile.png"));
            player_flat_tile_small = ImageIO.read(new File("images\\player_flatland_tile_small.png"));
            enemy_flat_tile_small = ImageIO.read(new File("images\\enemy_flatland_tile_small.png"));
            player_forest_tile = ImageIO.read(new File("images\\player_forest_tile.png"));
            enemy_forest_tile = ImageIO.read(new File("images\\enemy_forest_tile.png"));
            player_forest_tile_small = ImageIO.read(new File("images\\player_forest_tile_small.png"));
            enemy_forest_tile_small = ImageIO.read(new File("images\\enemy_forest_tile_small.png"));
            player_mount_tile = ImageIO.read(new File("images\\player_mountain_tile.png"));
            enemy_mount_tile = ImageIO.read(new File("images\\enemy_mountain_tile.png"));
            player_mount_tile_small = ImageIO.read(new File("images\\player_mountain_tile_small.png"));
            enemy_mount_tile_small = ImageIO.read(new File("images\\enemy_mountain_tile_small.png"));
            player_desert_tile = ImageIO.read(new File("images\\player_desert_tile.png"));
            enemy_desert_tile = ImageIO.read(new File("images\\enemy_desert_tile.png"));
            player_desert_tile_small = ImageIO.read(new File("images\\player_desert_tile_small.png"));
            enemy_desert_tile_small = ImageIO.read(new File("images\\enemy_desert_tile_small.png"));
        } catch (IOException ioe) { }
    }

    public static void main(String[] args) {
        setup();
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(WINWIDTH, WINHEIGHT);

        JMenuBar jMenuBar = new JMenuBar();
        appFrame.setJMenuBar(jMenuBar);

        //  Makes all the JButtons for the JMenuBar
        JButton pauseButton = new JButton("Menu");
        pauseButton.addActionListener(new OpenPauseMenu());
        JButton nationButton = new JButton("Nation");
        nationButton.addActionListener(new OpenNationMenu());
        JButton researchButton = new JButton("Research");
        researchButton.addActionListener(new OpenResearchMenu());
        JButton infoButton = new JButton("Info");
        infoButton.addActionListener(new OpenInfoMenu());
        JButton mailButton = new JButton("Mail");
        mailButton.addActionListener(new OpenMailMenu());
        JButton tradeButton = new JButton("Trade");
        tradeButton.addActionListener(new OpenTradeMenu());
        JButton endTurnButton = new JButton("End Turn");
        endTurnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionsTaken = maxActions;
            }
        });

        pauseButton.setBackground(colorButton);
        nationButton.setBackground(colorButton);
        researchButton.setBackground(colorButton);
        infoButton.setBackground(colorButton);
        mailButton.setBackground(colorButton);
        tradeButton.setBackground(colorButton);
        endTurnButton.setBackground(colorButton);
        jMenuBar.setBackground(colorBackground);
        appFrame.setBackground(colorBackground);

        //  Throws all the JButtons into the JMenuBar
        jMenuBar.add(pauseButton);
        jMenuBar.add(nationButton);
        jMenuBar.add(researchButton);
        jMenuBar.add(infoButton);
        jMenuBar.add(mailButton);
        jMenuBar.add(tradeButton);
        jMenuBar.add(endTurnButton);
        jMenuBar.add(metalLabel);
        jMenuBar.add(woodLabel);
        jMenuBar.add(foodLabel);
        jMenuBar.add(laborLabel);
        jMenuBar.add(educationLabel);
        jMenuBar.add(actionLabel);

        board = new MouseTrackerPanel();
        appFrame.getContentPane().add(board);
        openStartScreen();
    }
}