// Zelda.java Copyright (C) 2020 Ben Sanders
//import java.lang.invoke.DelegatingMethodHandle$Holder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

import javax.swing.*;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class Main {
    // global variables for the game
    private static MouseTrackerPanel board;
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
    private static double pi;
    private static double twoPi;
    private static double tileFixX;
    private static double tileFixY;
    private enum biome {Flatland,Forest,Mountain,Desert};
    private static Vector<Integer> biomeCounts;
    private static int numTiles;
    private static Boolean endgame;
    private static BufferedImage background;
    private static boolean background_drawn;
    private static BufferedImage scrollBackground;
    private static Boolean upPressed;
    private static Boolean downPressed;
    private static Boolean leftPressed;
    private static Boolean rightPressed;
    private static Long audiolifetime;
    private static Long lastAudioStart;
    private static Clip clip;
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
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    private static String nations[] = new String[]{"Stark", "Lannister", "Targaryen"};;
    private static String currentNation;
    private static Nation Stark = new Nation();
    private static Nation Lannister = new Nation();
    private static Nation Targaryen = new Nation();

    //  Don't know where this even came from?
//    public Main() {
//        setup();
//    }

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
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {}
                    lastPosition = null;
                    deltaX = 0;
                    deltaY = 0;
                }
            });
        }

        public double getMouseX() {return mouseX;}
        public double getMouseY() {return mouseY;}

    }


    public static void setup() {
        appFrame = new JFrame("Capstone");
        pi = 3.14159265358979;
        twoPi = 2.0 * 3.14159265358979;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        XOFFSET = 0;
        YOFFSET = 30; //30
        WINWIDTH = (int)screenSize.getWidth(); //338
        WINHEIGHT = (int)screenSize.getHeight() - 40; //271
        endgame = false;
        audiolifetime = 78000L; // 78 seconds for KI.WAV, was new Long(78000)

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
            player_flat_tile = ImageIO.read(new File("images\\player_flat_tile.png"));
            enemy_flat_tile = ImageIO.read(new File("images\\enemy_flat_tile.png"));
            player_flat_tile_small = ImageIO.read(new File("images\\player_flat_tile_small.png"));
            enemy_flat_tile_small = ImageIO.read(new File("images\\enemy_flat_tile_small.png"));
            player_forest_tile = ImageIO.read(new File("images\\player_forest_tile.png"));
            enemy_forest_tile = ImageIO.read(new File("images\\enemy_forest_tile.png"));
            player_forest_tile_small = ImageIO.read(new File("images\\player_forest_tile_small.png"));
            enemy_forest_tile_small = ImageIO.read(new File("images\\enemy_forest_tile_small.png"));
            player_mount_tile = ImageIO.read(new File("images\\player_mount_tile.png"));
            enemy_mount_tile = ImageIO.read(new File("images\\enemy_mount_tile.png"));
            player_mount_tile_small = ImageIO.read(new File("images\\player_mount_tile_small.png"));
            enemy_mount_tile_small = ImageIO.read(new File("images\\enemy_mount_tile_small.png"));
            player_desert_tile = ImageIO.read(new File("images\\player_desert_tile.png"));
            enemy_desert_tile = ImageIO.read(new File("images\\enemy_desert_tile.png"));
            player_desert_tile_small = ImageIO.read(new File("images\\player_desert_tile_small.png"));
            enemy_desert_tile_small = ImageIO.read(new File("images\\enemy_desert_tile_small.png"));
        } catch (IOException ioe) { }
    }

    private static class AudioLooper implements Runnable {
        public void run() {
            while (endgame == false) {
                Long currTime = Long.valueOf(System.currentTimeMillis()); // was new Long(System.currentTimeMillis()
//                if (currTime - lastAudioStart > audiolifetime) {
//                    playAudio(backgroundState);
//                }
            }
        }
    }

    private static void playAudio(String backgroundState) {
        try {
            clip.stop();
        } catch (Exception e) {
            // NOP
        }

        try {
            if (backgroundState.substring(0, 2).equals("KI")) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(new File("audio\\TitleKI.wav").getAbsoluteFile());
                clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
                lastAudioStart = System.currentTimeMillis();
                audiolifetime = Long.valueOf(78000); // was new Long(78000)
            } else if (backgroundState.substring(0, 2).equals("TC")) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(new File("audio\\TC.wav").getAbsoluteFile());
                clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
                lastAudioStart = System.currentTimeMillis();
                audiolifetime = Long.valueOf(219000); // was new Long(191000)
            }
        } catch (Exception e) {
            // NOP
        }
    }

    private static class Animate implements Runnable {
        public void run() {
//            backgroundDraw();
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
            metalLabel.setText("    Metal: " + Stark.getResource("metal"));
            woodLabel.setText("    Wood: " + Stark.getResource("wood"));
            foodLabel.setText("    Food: " + Stark.getResource("food"));
            laborLabel.setText("    Labor: " + Stark.getResource("labor"));
            educationLabel.setText("    Education: " + Stark.getResource("education"));
        } else if (Lannister.isActive()) {
            metalLabel.setText("    Metal: " + Lannister.getResource("metal"));
            woodLabel.setText("    Wood: " + Lannister.getResource("wood"));
            foodLabel.setText("    Food: " + Lannister.getResource("food"));
            laborLabel.setText("    Labor: " + Lannister.getResource("labor"));
            educationLabel.setText("    Education: " + Lannister.getResource("education"));
        } else if (Targaryen.isActive()) {
            metalLabel.setText("    Metal: " + Targaryen.getResource("metal"));
            woodLabel.setText("    Wood: " + Targaryen.getResource("wood"));
            foodLabel.setText("    Food: " + Targaryen.getResource("food"));
            laborLabel.setText("    Labor: " + Targaryen.getResource("labor"));
            educationLabel.setText("    Education: " + Targaryen.getResource("education"));
        }
    }

    private static void tileDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        for (int i = 0; i < tiles.size(); i++) {
            Tile current = tiles.get(i);
            if (current.mouseHover) {
                System.out.println((current.x + current.getWidth() / 2) + " " + (current.y - current.getHeight() / 2) + "\n" + board.mouseX + " " + board.mouseY);
                if (current.myBiome == biome.Flatland) {
                    g2d.drawImage(rotateImageObject(current).filter(flat_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                } else if (current.myBiome == biome.Forest) {
                    g2d.drawImage(rotateImageObject(current).filter(forest_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                } else if (current.myBiome == biome.Mountain) {
                    g2d.drawImage(rotateImageObject(current).filter(mount_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                } else if (current.myBiome == biome.Desert) {
                    g2d.drawImage(rotateImageObject(current).filter(desert_tile, null), (int)(current.getX() + XOFFSET + 0.5), (int)(current.getY() + YOFFSET + 0.5), null);
                }
                System.out.println(current.toString());
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
        }
    }


//    private static class EnemyMover implements Runnable {
//        private double bluepigvelocitystep;
//        private double bluepigvelocity;
//
//        public EnemyMover() {
//            bluepigvelocitystep = 2;
//        }
//
//        public void run() {
//            Random randomNumbers = new Random(LocalTime.now().getNano());
//            while (endgame == false) {
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    // NOP
//                }
//
//                // TODO
//                try {
//                    for (int i = 0; i < bluepigEnemies.size(); i++) {
//                        int state = randomNumbers.nextInt(1000);
//                        if (state < 5) {
//                            bluepigvelocity = bluepigvelocitystep;
//                            bluepigEnemies.elementAt(i).setInternalAngle(0);
//                        } else if (state < 10) {
//                            bluepigvelocity = bluepigvelocitystep;
//                            bluepigEnemies.elementAt(i).setInternalAngle(halfPi);
//                        } else if (state < 15) {
//                            bluepigvelocity = bluepigvelocitystep;
//                            bluepigEnemies.elementAt(i).setInternalAngle(pi);
//                        } else if (state < 20) {
//                            bluepigvelocity = bluepigvelocitystep;
//                            bluepigEnemies.elementAt(i).setInternalAngle(threehavlesPi);
//                        } else if (state < 250) {
//                            bluepigvelocity = bluepigvelocitystep;
//                        } else {
//                            bluepigvelocity = 0;
//                        }
//
//                        bluepigEnemies.elementAt(i).updateBounce();
//                        bluepigEnemies.elementAt(i).move(bluepigvelocity *
//                                        Math.cos(bluepigEnemies.elementAt(i).getInternalAngle()),
//                                bluepigvelocity * Math.sin(bluepigEnemies.elementAt(i).getInternalAngle()));
//                    }
//
//                    for (int i = 0; i < bubblebossEnemies.size(); i++) {
//
//                    }
//                } catch (java.lang.NullPointerException jlnpe) {
//                    // NOP
//                }
//            }
//        }
//    }

//    private static void playerDraw() {
//        Graphics g = appFrame.getGraphics();
//        Graphics2D g2D = (Graphics2D) g;
//        p1.setMaxFrames(10);
//
//        if (upPressed || downPressed || leftPressed || rightPressed) {
//            if (upPressed == true) {
//                if (p1.getCurrentFrame() < 5) {
////                    g2D.drawImage(rotateImageObject(p1).filter(link[0], null),
//                    g2D.drawImage(link[0],
//                            (int) (p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
//                } else if (p1.getCurrentFrame() > 5) {
////                    g2D.drawImage(rotateImageObject(p1).filter(link[1], null),
//                    g2D.drawImage(link[1],
//                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
//                }
//                p1.updateCurrentFrame();
//            }
//            if (downPressed == true) {
//                if (p1.getCurrentFrame() < 5) {
////                    g2D.drawImage(rotateImageObject(p1).filter(link[2], null),
//                    g2D.drawImage(link[2],
//                            (int)(p1.getX() + 0.5), (int) (p1.getY() + 0.5), null);
//                } else if (p1.getCurrentFrame() > 5) {
////                    g2D.drawImage(rotateImageObject(p1).filter(link[3], null),
//                    g2D.drawImage(link[3],
//                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
//                }
//                p1.updateCurrentFrame();
//            }
//            if (leftPressed == true) {
//                if (p1.getCurrentFrame() < 5) {
//
////                    g2D.drawImage(rotateImageObject(p1).filter(link[4], null),
//                    g2D.drawImage(link[4],
//                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
//                } else if (p1.getCurrentFrame() > 5) {
////                    g2D.drawImage(rotateImageObject(p1).filter(link[5], null),
//                    g2D.drawImage(link[5],
//                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
//                }
//                p1.updateCurrentFrame();
//            }
//            if (rightPressed == true) {
//                if (p1.getCurrentFrame() < 5) {
////                    g2D.drawImage(rotateImageObject(p1).filter(link[6], null),
//                    g2D.drawImage(link[6],
//                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
//                } else if (p1.getCurrentFrame() > 5) {
////                    g2D.drawImage(rotateImageObject(p1).filter(link[7], null),
//                    g2D.drawImage(link[7],
//                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
//                }
//                p1.updateCurrentFrame();
//            }
//        } else {
//            if (Math.abs(lastPressed - 90.0) < 1.0) {
////                g2D.drawImage(rotateImageObject(p1).filter(link[0], null),
//                g2D.drawImage(link[0],
//                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
//            }
//            if (Math.abs(lastPressed - 270.0) < 1.0) {
////                g2D.drawImage(rotateImageObject(p1).filter(link[2], null),
//                g2D.drawImage(link[2],
//                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
//            }
//            if (Math.abs(lastPressed - 0.0) < 1.0) {
////                g2D.drawImage(rotateImageObject(p1).filter(link[6], null),
//                g2D.drawImage(link[6],
//                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
//            }
//            if (Math.abs(lastPressed - 180.0) < 1.0) {
////                g2D.drawImage(rotateImageObject(p1).filter(link[4], null),
//                g2D.drawImage(link[4],
//                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
//            }
//        }
//
//        // g2D.drawImage(rotateImageObject(p1).filter(player, null), (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
//    }
//
//    private static void healthDraw() {
//        Graphics g = appFrame.getGraphics();
//        Graphics2D g2D = (Graphics2D) g;
//
//        int leftscale = 10;
//        int leftoffset = 10;
//        int rightoffset = 9;
//        int interioroffset = 2;
//        int halfinterioroffset = 1;
//        for (int i = 0; i < p1.getMaxLife(); i++) {
//            if (i % 2 == 0) {
//                g2D.drawImage(leftHeartOutline, leftscale * i + leftoffset + XOFFSET, YOFFSET, null);
////                g2D.drawImage(rotateImageObject(p1).filter(leftHeartOutline, null),
////                        leftscale * i + leftoffset + XOFFSET, YOFFSET, null);
//            } else {
//                g2D.drawImage(rightHeartOutline, leftscale * i + rightoffset + XOFFSET, YOFFSET, null);
////                g2D.drawImage(rotateImageObject(p1).filter(rightHeartOutline, null),
////                        leftscale * i + rightoffset + XOFFSET, YOFFSET, null);
//            }
//        }
//
//        for (int i = 0; i < p1.getLife(); i++) {
//            if (i % 2 == 0) {
//                g2D.drawImage(leftHeart, leftscale * i + leftoffset + interioroffset + XOFFSET,
//                        interioroffset + YOFFSET, null);
////                g2D.drawImage(rotateImageObject(p1).filter(leftHeart, null),
////                        leftscale * i + leftoffset + interioroffset + XOFFSET, interioroffset + YOFFSET, null);
//            } else {
//                g2D.drawImage(rightHeart, leftscale * i + leftoffset - halfinterioroffset + XOFFSET,
//                        interioroffset + YOFFSET, null);
////                g2D.drawImage(rotateImageObject(p1).filter(rightHeart, null),
////                        leftscale * i + leftoffset - halfinterioroffset + XOFFSET, interioroffset + YOFFSET, null);
//            }
//        }
//    }
//
//    private static void enemiesDraw() {
//        Graphics g = appFrame.getGraphics();
//        Graphics2D g2D = (Graphics2D) g;
//
//        for (int i = 0; i < bluepigEnemies.size(); i++) {
//            if (Math.abs(bluepigEnemies.elementAt(i).getInternalAngle() - 0.0) < 1.0) {
//                if (bluepigEnemies.elementAt(i).getCurrentFrame() < bluepigEnemies.elementAt(i).getMaxFrames() / 2) {
//                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(6), null),
//                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
//                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
//                } else {
//                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(7), null),
//                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
//                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
//                }
//                bluepigEnemies.elementAt(i).updateCurrentFrame();;
//            }
//            if(Math.abs(bluepigEnemies.elementAt(i).getInternalAngle() - pi) < 1.0) {
//                if (bluepigEnemies.elementAt(i).getCurrentFrame() < bluepigEnemies.elementAt(i).getMaxFrames() / 2) {
//                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(4), null),
//                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
//                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
//                } else {
//                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(5), null),
//                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
//                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
//                }
//                bluepigEnemies.elementAt(i).updateCurrentFrame();
//            }
//            if (Math.abs(bluepigEnemies.elementAt(i).getInternalAngle() - halfPi) < 1.0) {
//                if (bluepigEnemies.elementAt(i).getCurrentFrame() < bluepigEnemies.elementAt(i).getMaxFrames() / 2) {
//                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(2), null),
//                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
//                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
//                } else {
//                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(3), null),
//                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
//                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
//                }
//                bluepigEnemies.elementAt(i).updateCurrentFrame();
//            }
//            if (Math.abs(bluepigEnemies.elementAt(i).getInternalAngle() - threehavlesPi) < 1.0) {
//                if (bluepigEnemies.elementAt(i).getCurrentFrame() < bluepigEnemies.elementAt(i).getMaxFrames() / 2) {
//                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(0), null),
//                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
//                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
//                } else {
//                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(1), null),
//                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
//                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
//                }
//                bluepigEnemies.elementAt(i).updateCurrentFrame();
//            }
//        }
//    }

    private static class KeyPressed extends AbstractAction {
        private String action;

        public KeyPressed() { action = ""; }
        public KeyPressed(String input) { action = input; }

        public void actionPerformed(ActionEvent e) {
            if (action.equals("UP")) {
                upPressed = true;
            }
            if (action.equals("DOWN")) {
                downPressed = true;
            }
            if (action.equals("LEFT")) {
                leftPressed = true;
            }
            if (action.equals("RIGHT")) {
                rightPressed = true;
            }
        }
    }

    private static class KeyReleased extends AbstractAction {
        private String action;

        public KeyReleased() { action = ""; }
        public KeyReleased(String input) { action = input; }

        public void actionPerformed(ActionEvent e) {
            if(action.equals("UP")) {
                upPressed = false;
            }
            if (action.equals("DOWN")) {
                downPressed = false;
            }
            if (action.equals("LEFT")) {
                leftPressed = false;
            }
            if (action.equals("RIGHT")) {
                rightPressed = false;
            }
        }
    }

    private static void openStartScreen() {
        JFrame frame = new JFrame("Start Screen");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocation(300, 200);

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                OpenSetupMenu();
            }
        });
        JButton loadGameButton = new JButton("Load Game");
        loadGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                OpenLoadMenu();
            }
        });
        JButton quitGameButton = new JButton("Quit Game");
        quitGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                endgame = true;
                frame.setVisible(false);
//                appFrame.setVisible(false);
            }
        });

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(3, 1));
        jPanel.add(newGameButton);
        jPanel.add(loadGameButton);
        jPanel.add(quitGameButton);
        frame.add(jPanel);
        frame.setSize(700, 300);
        frame.setVisible(true);
    }

    public static void OpenSetupMenu() {
        JFrame frame = new JFrame("Setup Menu");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
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
                upPressed = false;
                downPressed = false;
                leftPressed = false;
                rightPressed = false;
                background_drawn = false;
                lastAudioStart = System.currentTimeMillis();
//                    playAudio(backgroundState);
                currentNation = (String) nationComboBox.getSelectedItem();
                if (currentNation == "Stark") {
                    Stark.setActive(true);
                    Lannister.setActive(false);
                    Targaryen.setActive(false);
                    Stark.setResource("metal", Integer.parseInt(metalT.getText()));
                    Stark.setResource("wood", Integer.parseInt(woodT.getText()));
                    Stark.setResource("food", Integer.parseInt(foodT.getText()));
                    Stark.setResource("labor", Integer.parseInt(laborT.getText()));
                    Stark.setResource("education", Integer.parseInt(educationT.getText()));
                } else if (currentNation == "Lannister") {
                    Stark.setActive(false);
                    Lannister.setActive(true);
                    Targaryen.setActive(false);
                    Lannister.setResource("metal", Integer.parseInt(metalT.getText()));
                    Lannister.setResource("wood", Integer.parseInt(woodT.getText()));
                    Lannister.setResource("food", Integer.parseInt(foodT.getText()));
                    Lannister.setResource("labor", Integer.parseInt(laborT.getText()));
                    Lannister.setResource("education", Integer.parseInt(educationT.getText()));
                } else if (currentNation == "Targaryen") {
                    Stark.setActive(false);
                    Lannister.setActive(false);
                    Targaryen.setActive(true);
                    Targaryen.setResource("metal", Integer.parseInt(metalT.getText()));
                    Targaryen.setResource("wood", Integer.parseInt(woodT.getText()));
                    Targaryen.setResource("food", Integer.parseInt(foodT.getText()));
                    Targaryen.setResource("labor", Integer.parseInt(laborT.getText()));
                    Targaryen.setResource("education", Integer.parseInt(educationT.getText()));
                }
                endgame = false;
                Thread t1 = new Thread(new Animate());
                Thread t2 = new Thread(new TileMover());
                Thread t3 = new Thread(new MouseOverChecker());
//                    Thread t4 = new Thread(new AudioLooper());
//                    Thread t5 = new Thread(new EnemyMover());
                t1.start();
                t2.start();
                t3.start();
//                    t4.start();
//                    t5.start();
                frame.setVisible(false);
            }
        });
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
            }
        });

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(8, 2));
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
                if (checkPoint.getX() < 0 || checkPoint.getX() + tile_small.getWidth() > WINWIDTH || checkPoint.getY() > WINHEIGHT || checkPoint.getY() - tile_small.getHeight() < 40 || isInside(checkPoint.getX() + (tile_small.getWidth() * 0.5),checkPoint.getY() - (tile_small.getHeight() * 0.5),tiles.get(j).getX(),tiles.get(j).getY(),tiles.get(j).getX()+tile_small.getWidth(),tiles.get(j).getY()-tile_small.getHeight())) {
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
                    upPressed = false;
                    downPressed = false;
                    leftPressed = false;
                    rightPressed = false;
                    background_drawn = false;
                    lastAudioStart = System.currentTimeMillis();
//                    playAudio(backgroundState);
                    currentNation = (String) nationComboBox.getSelectedItem();
                    if (currentNation == "Stark") {
                        Stark.setActive(true);
                        Lannister.setActive(false);
                        Targaryen.setActive(false);
                    } else if (currentNation == "Lannister") {
                        Stark.setActive(false);
                        Lannister.setActive(true);
                        Targaryen.setActive(false);
                    } else if (currentNation == "Targaryen") {
                        Stark.setActive(false);
                        Lannister.setActive(false);
                        Targaryen.setActive(true);
                    }
                    endgame = false;
                    Thread t1 = new Thread(new Animate());
//                    Thread t4 = new Thread(new AudioLooper());
//                    Thread t5 = new Thread(new EnemyMover());
                    t1.start();
//                    t4.start();
//                    t5.start();
                    frame.setVisible(false);
                }
            });

            JButton quitButton = new JButton("Quit");
            quitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    endgame = true;
                    frame.setVisible(false);
                }
            });

            JPanel jPanel = new JPanel();
            jPanel.setLayout(new GridLayout(3, 1));
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
                nationMilitary = new JLabel("Military Strength: " + (Stark.swordmen + Stark.shieldmen + Stark.spearmen + Stark.mountedCalvalry + Stark.archer + Stark.scoutRaven + Stark.transportShip + Stark.warShip + Stark.wyvern + Stark.dreadnought + Stark.dragon));
            } else if (Lannister.isActive()) {
                nationMilitary = new JLabel("Military Strength: " + (Lannister.swordmen + Lannister.shieldmen + Lannister.spearmen + Lannister.mountedCalvalry + Lannister.archer + Lannister.scoutRaven + Lannister.transportShip + Lannister.warShip + Lannister.wyvern + Lannister.dreadnought + Lannister.dragon));
            } else if (Targaryen.isActive()) {
                nationMilitary = new JLabel("Military Strength: " + (Targaryen.swordmen + Targaryen.shieldmen + Targaryen.spearmen + Targaryen.mountedCalvalry + Targaryen.archer + Targaryen.scoutRaven + Targaryen.transportShip + Targaryen.warShip + Targaryen.wyvern + Targaryen.dreadnought + Targaryen.dragon));
            }
            JLabel nationLand = new JLabel("Total Land: ");

            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
            jPanel.add(nationName);
            jPanel.add(nationTeam);
            jPanel.add(nationMilitary);
            jPanel.add(nationLand);
            frame.add(jPanel);
            frame.setSize(1000, 200);
            frame.setVisible(true);
        }
    }

    private static class OpenMilitaryMenu implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //  Old method for making the page, just ignore it for now
//            JPopupMenu militaryMenu = new JPopupMenu("Military");
//
//            JButton close = new JButton("Close");
//            close.addActionListener(new ClosePopup(militaryMenu));
//            JLabel l = new JLabel("Military popup");
//            JTable table = new JTable(5, 3);
//            table.setValueAt("-- Name --", 0, 0);
//            table.setValueAt("-- Description --", 0, 1);
//            table.setValueAt("-- Price --", 0, 2);
//            table.setValueAt("Swordmen", 1, 0);
//            table.setValueAt("Foot soldiers armed with swords.", 1, 1);
//            table.setValueAt("M:10  F:10  L:10", 1, 2);
//            table.setValueAt("Shieldmen", 2, 0);
//            table.setValueAt("Foot soldiers armed with shields.", 2, 1);
//            table.setValueAt("M:10  F:10  L:20", 2, 2);
//            table.setValueAt("Spearmen", 3, 0);
//            table.setValueAt("Foot soldiers armed with spears.", 3, 1);
//            table.setValueAt("M:20  F:10  L:10", 3, 2);
//            table.setValueAt("Mounted Cavalry", 4, 0);
//            table.setValueAt("Soldiers mounted on horseback.", 4, 1);
//            table.setValueAt("M:20  F:10  L:20", 4, 2);
//            militaryMenu.add(close);
//            militaryMenu.add(l);
//            militaryMenu.add(table);
//            militaryMenu.setPopupSize(500, 500);
//            militaryMenu.show(appFrame, 125, 55);
//            JLayeredPane testMenu = new JLayeredPane();
//            testMenu.add(close);
//            testMenu.add(l);
//            testMenu.setSize(500, 500);
//            testMenu.setLayer(appFrame, 125, 55);
//            testMenu.setVisible(true);

            JFrame frame = new JFrame("Military Menu");
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.setLocation(300, 200);

            DefaultTableModel dm = new DefaultTableModel();
            dm.setDataVector(new Object[][]{{"Swordmen", "Foot soldiers armed with swords.", "M:10  F:10  L:10", "Buy Swordmen"},
                    {"Shieldmen", "Foot soldiers armed with shields.", "M:10  F:10  L:20", "Buy Shieldmen"},
                    {"Spearmen", "Foot soldiers armed with spears.", "M:20  F:10  L:10", "Buy Spearmen"},
                    {"Mounted Cavalry", "Soldiers mounted on horseback.", "M:20  F:10  L:20", "Buy Mounted Calvalry"},
                    {"Archer", "Foot soldiers armed with bows and arrows.", "M:10  F:10  L:10", "Buy Archer"},
                    {"Scout Raven", "Air unit used for zone reconnaissance.", "F:10  L:10", "Buy Scout Raven"},
                    {"Transport Ship", "Ship used for transporting ground units across water.", "M:10  W:20  F:10  L:10", "Buy Transport Ship"},
                    {"War Ship", "Ship used for battle and naval combat.", "M:20  W:30  F:10  L:10", "Buy War Ship"},
                    {"Wyvern", "Air unit used for attack ground units.", "F:50  L:25", "Buy Wyvern"},
                    {"Dreadnought", "Large Ship which excels in naval combat.", "M:10  W:100  F:10  L:50", "Buy Dreadnought"},
                    {"Dragon", "Ground and air unit which excels in ground combat.", "F:100  L:100", "Buy Dragon"}},
                    new Object[]{"Name", "Description", "Price", ""});

            JTable table = new JTable(dm);
            table.getColumn("").setCellRenderer(new ButtonRenderer());
            table.getColumn("").setCellEditor(new ButtonEditor(new JCheckBox()));

            JScrollPane scroll = new JScrollPane(table);
            table.setPreferredScrollableViewportSize(table.getPreferredSize());
            table.getColumnModel().getColumn(0).setPreferredWidth(200);
            table.getColumnModel().getColumn(1).setPreferredWidth(400);
            table.getColumnModel().getColumn(2).setPreferredWidth(200);
            table.getColumnModel().getColumn(3).setPreferredWidth(200);
            table.setRowHeight(25);
            frame.setSize(1000, 300);
            frame.add(scroll);
//            frame.pack();
            frame.setVisible(true);
        }
    }

    private static class OpenConstructionMenu implements ActionListener {
        public void actionPerformed(ActionEvent e) {
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
            table.getColumn("").setCellEditor(new ButtonEditor(new JCheckBox()));

            JScrollPane scroll = new JScrollPane(table);
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
                    {"Train Spearmen", "Unlocks training of Spearmen.", "E:50  L:10", "Buy Train Spearmen"},
                    {"Train Mounted Cavalry", "Unlocks training of Mounted Calvalry.", "E:50  L:10", "Buy Train Mounted Cavalry"},
                    {"Build War Ships", "Unlocks construction of War Ships.", "E:50  L:10", "Buy Build War Ships"},
                    {"Tame Wyverns", "Unlocks taming of Wyverns.", "E:50  L:10", "Buy Tame Wyverns"},
                    {"Build Dreadnoughts", "Unlocks construction of Dreadnoughts.", "E:100  L:50", "Buy Build Dreadnoughts"},
                    {"Tame Dragons", "Unlocks taming of Dragons.", "E:100  L:50", "Buy Tame Dragons"}},
                    new Object[]{"Name", "Description", "Price", ""});

            JTable table = new JTable(dm);
            table.getColumn("").setCellRenderer(new ButtonRenderer());
            table.getColumn("").setCellEditor(new ButtonEditor(new JCheckBox()));

            JScrollPane scroll = new JScrollPane(table);
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
                dm.setDataVector(new Object[][]{{"Swordmen", Stark.swordmen, "H:5", "G:5-10", "A:0", "S:0"},
                                {"Shieldmen", Stark.shieldmen, "H:10", "G:3-8", "A:0", "S:0"},
                                {"Spearmen", Stark.spearmen, "H:5", "G:7-12", "A:0", "S:0"},
                                {"Mounted Cavalry", Stark.mountedCalvalry, "H:10", "G:7-12", "A:0", "S:0"},
                                {"Archer", Stark.archer, "H:5", "G:0-5", "A:5-10", "S:0"},
                                {"Scout Raven", Stark.scoutRaven, "H:1", "G:0", "A:0", "S:0"},
                                {"Transport Ship", Stark.transportShip, "H:10", "G:0", "A:0", "S:0"},
                                {"War Ship", Stark.warShip, "H:20", "G:3-8", "A:3-8", "S:5-10"},
                                {"Wyvern", Stark.wyvern, "H:5", "G:5-10", "A:5-10", "S:3-8"},
                                {"Dreadnought", Stark.dreadnought, "H:50", "G:10-15", "A:10-15", "S:15-20"},
                                {"Dragon", Stark.dragon, "H:50", "G:15-20", "A:10-20", "S:5-10"}},
                                new Object[]{"Unit", "Quantity", "Health", "Ground", "Air", "Sea"});
            } else if (Lannister.isActive()) {
                dm.setDataVector(new Object[][]{{"Swordmen", Lannister.swordmen, "H:5", "G:5-10", "A:0", "S:0"},
                                {"Shieldmen", Lannister.shieldmen, "H:10", "G:3-8", "A:0", "S:0"},
                                {"Spearmen", Lannister.spearmen, "H:5", "G:7-12", "A:0", "S:0"},
                                {"Mounted Cavalry", Lannister.mountedCalvalry, "H:10", "G:7-12", "A:0", "S:0"},
                                {"Archer", Lannister.archer, "H:5", "G:0-5", "A:5-10", "S:0"},
                                {"Scout Raven", Lannister.scoutRaven, "H:1", "G:0", "A:0", "S:0"},
                                {"Transport Ship", Lannister.transportShip, "H:10", "G:0", "A:0", "S:0"},
                                {"War Ship", Lannister.warShip, "H:20", "G:3-8", "A:3-8", "S:5-10"},
                                {"Wyvern", Lannister.wyvern, "H:5", "G:5-10", "A:5-10", "S:3-8"},
                                {"Dreadnought", Lannister.dreadnought, "H:50", "G:10-15", "A:10-15", "S:15-20"},
                                {"Dragon", Lannister.dragon, "H:50", "G:15-20", "A:10-20", "S:5-10"}},
                                new Object[]{"Unit", "Quantity", "Health", "Ground", "Air", "Sea"});
            } else if (Targaryen.isActive()) {
                dm.setDataVector(new Object[][]{{"Swordmen", Targaryen.swordmen, "H:5", "G:5-10", "A:0", "S:0"},
                                {"Shieldmen", Targaryen.shieldmen, "H:10", "G:3-8", "A:0", "S:0"},
                                {"Spearmen", Targaryen.spearmen, "H:5", "G:7-12", "A:0", "S:0"},
                                {"Mounted Cavalry", Targaryen.mountedCalvalry, "H:10", "G:7-12", "A:0", "S:0"},
                                {"Archer", Targaryen.archer, "H:5", "G:0-5", "A:5-10", "S:0"},
                                {"Scout Raven", Targaryen.scoutRaven, "H:1", "G:0", "A:0", "S:0"},
                                {"Transport Ship", Targaryen.transportShip, "H:10", "G:0", "A:0", "S:0"},
                                {"War Ship", Targaryen.warShip, "H:20", "G:3-8", "A:3-8", "S:5-10"},
                                {"Wyvern", Targaryen.wyvern, "H:5", "G:5-10", "A:5-10", "S:3-8"},
                                {"Dreadnought", Targaryen.dreadnought, "H:50", "G:10-15", "A:10-15", "S:15-20"},
                                {"Dragon", Targaryen.dragon, "H:50", "G:15-20", "A:10-20", "S:5-10"}},
                                new Object[]{"Unit", "Quantity", "Health", "Ground", "Air", "Sea"});
            }

            JTable table = new JTable(dm);

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
            frame.setJMenuBar(jMenuBar);
            JButton newMail = new JButton("New");
            newMail.addActionListener(new NewMailMenu());
            jMenuBar.add(newMail);

            //  Throws all the tabs into the JTabbedPane
            JTabbedPane tabPanel = new JTabbedPane();
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
//            nationComboBox.setSize(100, 50);

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
                    sendMail.add(mailTo1);
                    sendMail.add(mailFrom1);
                    sendMail.add(mailTime1);
                    sendMail.add(space1);
                    sendMail.add(mailBody1);
                    JPanel inboxMail = new JPanel();
                    inboxMail.setLayout(new BoxLayout(inboxMail, BoxLayout.Y_AXIS));
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
                    if (nationComboBox.getSelectedItem() == "Stark") {
                        Stark.inboxTabs.addTab((String) nationComboBox.getSelectedItem(), inboxMail);
                    } else if (nationComboBox.getSelectedItem() == "Lannister") {
                        Lannister.inboxTabs.addTab((String) nationComboBox.getSelectedItem(), inboxMail);
                    } else if (nationComboBox.getSelectedItem() == "Targaryen") {
                        Targaryen.inboxTabs.addTab((String) nationComboBox.getSelectedItem(), inboxMail);
                    }
                    frame.setVisible(false);
                }
            });

            //  Makes a JPanel to combine the message Label and TextArea together
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BorderLayout());
            centerPanel.add(messageL, BorderLayout.WEST);
            centerPanel.add(messageT, BorderLayout.CENTER);

            //  Throws everything into the JPanel
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
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
                        if (nationComboBox.getSelectedItem() == "Lannister") {
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
                        } else if (nationComboBox.getSelectedItem() == "Targaryen") {
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
                        if (nationComboBox.getSelectedItem() == "Stark") {
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
                        } else if (nationComboBox.getSelectedItem() == "Targaryen") {
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
                        if (nationComboBox.getSelectedItem() == "Stark") {
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
                        } else if (nationComboBox.getSelectedItem() == "Lannister") {
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
                    if (nationComboBox.getSelectedItem() == "Stark") {
                        Stark.inboxTabs.addTab((String) nationComboBox.getSelectedItem(), inboxMail);
                    } else if (nationComboBox.getSelectedItem() == "Lannister") {
                        Lannister.inboxTabs.addTab((String) nationComboBox.getSelectedItem(), inboxMail);
                    } else if (nationComboBox.getSelectedItem() == "Targaryen") {
                        Targaryen.inboxTabs.addTab((String) nationComboBox.getSelectedItem(), inboxMail);
                    }
                    frame.setVisible(false);
                }
            });

            //  Some spaces just to spread stuff out, probably a better way to do this
            JLabel space1 = new JLabel("          ");
            JLabel space2 = new JLabel("          ");
            JLabel space3 = new JLabel("          ");

            //  Makes a JPanel to combine the message Label and TextArea together
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BorderLayout());
            centerPanel.add(space1, BorderLayout.NORTH);
            centerPanel.add(messageL, BorderLayout.WEST);
            centerPanel.add(messageT, BorderLayout.CENTER);

            //  Throws everything into the JPanel
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
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

    //  Makes a button in a cell for a Table
    public static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Makes the Table button clickable
    public static class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

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
                JOptionPane.showMessageDialog(button, label + " complete");
                if (Stark.isActive()) {
                    if (label == "Buy Swordmen") {
                        Stark.setMilitary("swordmen", 1);
                    } else if (label == "Buy Shieldmen") {
                        Stark.setMilitary("shieldmen", 1);
                    } else if (label == "Buy Spearmen") {
                        Stark.setMilitary("spearmen", 1);
                    } else if (label == "Buy Mounted Calvalry") {
                        Stark.setMilitary("mountedCavalry", 1);
                    } else if (label == "Buy Archer") {
                        Stark.setMilitary("archer", 1);
                    } else if (label == "Buy Scout Raven") {
                        Stark.setMilitary("scoutRaven", 1);
                    } else if (label == "Buy Transport Ship") {
                        Stark.setMilitary("transportShip", 1);
                    } else if (label == "Buy War Ship") {
                        Stark.setMilitary("warShip", 1);
                    } else if (label == "Buy Wyvern") {
                        Stark.setMilitary("wyvern", 1);
                    } else if (label == "Buy Dreadnought") {
                        Stark.setMilitary("dreadnought", 1);
                    } else if (label == "Buy Dragon") {
                        Stark.setMilitary("dragon", 1);
                    }
                } else if (Lannister.isActive()) {
                    if (label == "Buy Swordmen") {
                        Lannister.setMilitary("swordmen", 1);
                    } else if (label == "Buy Shieldmen") {
                        Lannister.setMilitary("shieldmen", 1);
                    } else if (label == "Buy Spearmen") {
                        Lannister.setMilitary("spearmen", 1);
                    } else if (label == "Buy Mounted Calvalry") {
                        Lannister.setMilitary("mountedCavalry", 1);
                    } else if (label == "Buy Archer") {
                        Lannister.setMilitary("archer", 1);
                    } else if (label == "Buy Scout Raven") {
                        Lannister.setMilitary("scoutRaven", 1);
                    } else if (label == "Buy Transport Ship") {
                        Lannister.setMilitary("transportShip", 1);
                    } else if (label == "Buy War Ship") {
                        Lannister.setMilitary("warShip", 1);
                    } else if (label == "Buy Wyvern") {
                        Lannister.setMilitary("wyvern", 1);
                    } else if (label == "Buy Dreadnought") {
                        Lannister.setMilitary("dreadnought", 1);
                    } else if (label == "Buy Dragon") {
                        Lannister.setMilitary("dragon", 1);
                    }
                } else if (Targaryen.isActive()) {
                    if (label == "Buy Swordmen") {
                        Targaryen.setMilitary("swordmen", 1);
                    } else if (label == "Buy Shieldmen") {
                        Targaryen.setMilitary("shieldmen", 1);
                    } else if (label == "Buy Spearmen") {
                        Targaryen.setMilitary("spearmen", 1);
                    } else if (label == "Buy Mounted Calvalry") {
                        Targaryen.setMilitary("mountedCavalry", 1);
                    } else if (label == "Buy Archer") {
                        Targaryen.setMilitary("archer", 1);
                    } else if (label == "Buy Scout Raven") {
                        Targaryen.setMilitary("scoutRaven", 1);
                    } else if (label == "Buy Transport Ship") {
                        Targaryen.setMilitary("transportShip", 1);
                    } else if (label == "Buy War Ship") {
                        Targaryen.setMilitary("warShip", 1);
                    } else if (label == "Buy Wyvern") {
                        Targaryen.setMilitary("wyvern", 1);
                    } else if (label == "Buy Dreadnought") {
                        Targaryen.setMilitary("dreadnought", 1);
                    } else if (label == "Buy Dragon") {
                        Targaryen.setMilitary("dragon", 1);
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
        private int archer;
        private int scoutRaven;
        private int transportShip;
        private int warShip;
        private int wyvern;
        private int dreadnought;
        private int dragon;

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
            archer = 0;
            scoutRaven = 0;
            transportShip = 0;
            warShip = 0;
            wyvern = 0;
            dreadnought = 0;
            dragon = 0;
        }

        public void setActive(boolean status) {
            currentStatus = status;
        }

        public boolean isActive() {
            return currentStatus;
        }

        public void setResource(String unit, int change) {
            if (unit == "metal") {
                metal += change;
            } else if (unit == "wood") {
                wood += change;
            } else if (unit == "food") {
                food += change;
            } else if (unit == "labor") {
                labor += change;
            } else if (unit == "education") {
                education += change;
            }
        }

        public int getResource(String unit) {
            if (unit == "metal") {
                return metal;
            } else if (unit == "wood") {
                return wood;
            } else if (unit == "food") {
                return food;
            } else if (unit == "labor") {
                return labor;
            } else if (unit == "education") {
                return education;
            } else {
                return 0;
            }
        }

        public void setMilitary(String unit, int change) {
            if (unit == "swordmen") {
                swordmen += change;
            } else if (unit == "shieldmen") {
                shieldmen += change;
            } else if (unit == "spearmen") {
                spearmen += change;
            } else if (unit == "mountedCavalry") {
                mountedCalvalry += change;
            } else if (unit == "archer") {
                archer += change;
            } else if (unit == "scoutRaven") {
                scoutRaven += change;
            } else if (unit == "transportShip") {
                transportShip += change;
            } else if (unit == "warShip") {
                warShip += change;
            } else if (unit == "wyvern") {
                wyvern += change;
            } else if (unit == "dreadnought") {
                dreadnought += change;
            } else if (unit == "dragon") {
                dragon += change;
            }
        }

        public int getMilitary(String unit) {
            if (unit == "swordmen") {
                return swordmen;
            } else if (unit == "shieldmen") {
                return shieldmen;
            } else if (unit == "spearmen") {
                return spearmen;
            } else if (unit == "mountedCavalry") {
                return mountedCalvalry;
            } else if (unit == "archer") {
                return archer;
            } else if (unit == "scoutRaven") {
                return scoutRaven;
            } else if (unit == "transportShip") {
                return transportShip;
            } else if (unit == "warShip") {
                return warShip;
            } else if (unit == "wyvern") {
                return wyvern;
            } else if (unit == "dreadnought") {
                return dreadnought;
            } else if (unit == "dragon") {
                return dragon;
            } else {
                return 0;
            }
        }
    }

    //  Potential class for handing military unit info?
    private static class MilitaryUnit {
        private int health;
        private int ground;
        private int air;
        private int sea;
        private int location;

        public MilitaryUnit(String type) {
            if (type == "Buy Swordmen") {
                health = 5;
                ground = 10;
                air = 0;
                sea = 0;
            } else if (type == "Buy Shieldmen") {
                health = 10;
                ground = 8;
                air = 0;
                sea = 0;
            } else if (type == "Buy Spearmen") {
                health = 5;
                ground = 12;
                air = 0;
                sea = 0;
            } else if (type == "Buy Mounted Calvalry") {
                health = 10;
                ground = 12;
                air = 0;
                sea = 0;
            } else if (type == "Buy Archer") {
                health = 5;
                ground = 5;
                air = 10;
                sea = 0;
            } else if (type == "Buy Scout Raven") {
                health = 1;
                ground = 0;
                air = 0;
                sea = 0;
            } else if (type == "Buy Transport Ship") {
                health = 10;
                ground = 0;
                air = 0;
                sea = 0;
            } else if (type == "Buy War Ship") {
                health = 20;
                ground = 8;
                air = 8;
                sea = 10;
            } else if (type == "Buy Wyvern") {
                health = 5;
                ground = 10;
                air = 10;
                sea = 8;
            } else if (type == "Buy Dreadnought") {
                health = 50;
                ground = 15;
                air = 15;
                sea = 20;
            } else if (type == "Buy Dragon") {
                health = 50;
                ground = 20;
                air = 20;
                sea = 10;
            }
        }

        public int getHealth() {
            return health;
        }
        public int getGround() {
            return ground;
        }
        public int getAir() {
            return air;
        }
        public int getSea() {
            return sea;
        }
        public int getLocation() {
            return location;
        }
        public void moveTo(int newLocation) {
            location = newLocation;
        }
    }

    private static class MouseOverChecker implements Runnable {
        public void run() {
            while (!endgame) {
                try {
                    Thread.sleep(32);
                    for (int i = 0; i < tiles.size(); i++) {
                        if (MouseOver(tiles.get(i))) {
                            tiles.get(i).mouseHover = true;
                            System.out.println(tiles.get(i).toString());

                        } else if (tiles.get(i).mouseHover){
                            backgroundDraw();
                            tiles.get(i).mouseHover = false;
                        }
                    }
                } catch (java.lang.InterruptedException jlaioobe) {}
            }
        }
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

    private static AffineTransformOp rotateImageObject(ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getAngle(), obj.getWidth()/2.0, obj.getHeight()/2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

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
        private Vector<Tile> neighbors;
        private Vector<Integer> resourceRates;
        private Vector<Integer> buildings;
        private biome myBiome;
        private boolean waterSide;
        public Tile(double xinput, double yinput, double width, double height, double angle) {
            super(xinput,yinput,width,height,angle);
            neighbors = new Vector<>();
            resourceRates = new Vector<>();
            buildings = new Vector<>();
            buildings.add(0);
            buildings.add(0);
            buildings.add(0);
            buildings.add(0);
            buildings.add(0);
        }
        public String toString() {
            return "Labor: " + resourceRates.get(0) + " Wood: " + resourceRates.get(1) + " Metal: " + resourceRates.get(2) + " Food: " + resourceRates.get(3) + " Research: " + resourceRates.get(4);
        }
        public biome getBiome() {
            return myBiome;
        }
        public void isWaterSide() { waterSide = (neighbors.size() < 6); }
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
            labor += 5 * buildings.get(0);
            wood += 5 * buildings.get(1);
            metal += 5 * buildings.get(2);
            food += 5 * buildings.get(3);
            research += 5 * buildings.get(4);
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
    }


    private static void bindKey(JPanel myPanel, String input) {
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new KeyPressed(input));

        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new KeyReleased(input));
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
        JButton militaryButton = new JButton("Military");
        militaryButton.addActionListener(new OpenMilitaryMenu());
        JButton constructionButton = new JButton("Construction");
        constructionButton.addActionListener(new OpenConstructionMenu());
        JButton researchButton = new JButton("Research");
        researchButton.addActionListener(new OpenResearchMenu());
        JButton infoButton = new JButton("Info");
        infoButton.addActionListener(new OpenInfoMenu());
        JButton mailButton = new JButton("Mail");
        mailButton.addActionListener(new OpenMailMenu());
        JButton tradeButton = new JButton("Trade");
        tradeButton.addActionListener(new OpenTradeMenu());

        //  Throws all the JButtons into the JMenuBar
        jMenuBar.add(pauseButton);
        jMenuBar.add(nationButton);
        jMenuBar.add(militaryButton);
        jMenuBar.add(constructionButton);
        jMenuBar.add(researchButton);
        jMenuBar.add(infoButton);
        jMenuBar.add(mailButton);
        jMenuBar.add(tradeButton);
        jMenuBar.add(metalLabel);
        jMenuBar.add(woodLabel);
        jMenuBar.add(foodLabel);
        jMenuBar.add(laborLabel);
        jMenuBar.add(educationLabel);

        //  Don't even know if we need keys?
//        bindKey(myPanel, "UP");
//        bindKey(myPanel, "DOWN");
//        bindKey(myPanel, "LEFT");
//        bindKey(myPanel, "RIGHT");
        board = new MouseTrackerPanel();

        appFrame.getContentPane().add(board);
        appFrame.setVisible(true);

        openStartScreen();
    }
}