// Zelda.java Copyright (C) 2020 Ben Sanders
//import java.lang.invoke.DelegatingMethodHandle$Holder;
import java.awt.*;
import java.util.Vector;

import javax.swing.*;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Main {
    // global variables for the game
    private static Boolean endgame;
    private static BufferedImage background;
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
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    private static int p1Metal;
    private static int p1Wood;
    private static int p1Food;
    private static int p1Labor;
    private static int p1Education;

    public Main() {
        setup();
    }

    public static void setup() {
        appFrame = new JFrame("Capstone");
        XOFFSET = 0;
        YOFFSET = 80; //30
        WINWIDTH = 1920; //338
        WINHEIGHT = 1080; //271
        endgame = false;
        audiolifetime = 78000L; // 78 seconds for KI.WAV, was new Long(78000)
        p1Metal = 0;
        p1Wood = 0;
        p1Food = 0;
        p1Labor = 0;
        p1Education = 0;

        try {
            background = ImageIO.read(new File("images\\game-map.png"));
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
            backgroundDraw();
            while (endgame == false) {
//                backgroundDraw();

                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) { }
            }
        }
    }

    private static void backgroundDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;

        g2D.drawImage(background, XOFFSET, YOFFSET, null);
        g2D.drawString("Metal: " + p1Metal + "        Wood: " + p1Wood + "        Food: " + p1Food + "        Labor: " + p1Labor + "        Education: " + p1Education, 20, 75);
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

    private static class StartGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            endgame = true;
            upPressed = false;
            downPressed = false;
            leftPressed = false;
            rightPressed = false;
            lastAudioStart = System.currentTimeMillis();
//            playAudio(backgroundState);
            endgame = false;
            Thread t1 = new Thread(new Animate());
            Thread t4 = new Thread(new AudioLooper());
//            Thread t5 = new Thread(new EnemyMover());
            t1.start();
            t4.start();
//            t5.start();
        }
    }

    private static class QuitGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            endgame = true;
        }
    }

    private static class ClosePopup implements ActionListener {
        JPopupMenu popupMenu;
        public ClosePopup(JPopupMenu currentMenu) {
            popupMenu = currentMenu;
        }

        public void actionPerformed(ActionEvent e) {
            popupMenu.setVisible(false);
        }
    }

    private static class OpenMilitaryMenu implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JPopupMenu militaryMenu = new JPopupMenu("Military");

            JButton close = new JButton("Close");
            close.addActionListener(new ClosePopup(militaryMenu));
            JLabel l = new JLabel("Military popup");
            JTable table = new JTable(5, 3);
            table.setValueAt("-- Name --", 0, 0);
            table.setValueAt("-- Description --", 0, 1);
            table.setValueAt("-- Price --", 0, 2);
            table.setValueAt("Swordmen", 1, 0);
            table.setValueAt("Foot soldiers armed with swords.", 1, 1);
            table.setValueAt("M:10  F:10  L:10", 1, 2);
            table.setValueAt("Shieldmen", 2, 0);
            table.setValueAt("Foot soldiers armed with shields.", 2, 1);
            table.setValueAt("M:10  F:10  L:20", 2, 2);
            table.setValueAt("Spearmen", 3, 0);
            table.setValueAt("Foot soldiers armed with spears.", 3, 1);
            table.setValueAt("M:20  F:10  L:10", 3, 2);
            table.setValueAt("Mounted Cavalry", 4, 0);
            table.setValueAt("Soldiers mounted on horseback.", 4, 1);
            table.setValueAt("M:20  F:10  L:20", 4, 2);
            militaryMenu.add(close);
            militaryMenu.add(l);
            militaryMenu.add(table);

            militaryMenu.setPopupSize(500, 500);
            militaryMenu.show(appFrame, 125, 55);

//            JLayeredPane testMenu = new JLayeredPane();
//            testMenu.add(close);
//            testMenu.add(l);
//            testMenu.setSize(500, 500);
//            testMenu.setLayer(appFrame, 125, 55);
//            testMenu.setVisible(true);
        }
    }

    private static class OpenConstructionMenu implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JPopupMenu constructionMenu = new JPopupMenu("Construction");

            JButton close = new JButton("Close");
            close.addActionListener(new ClosePopup(constructionMenu));
            JLabel l = new JLabel("Construction popup");
            JTable table = new JTable(5, 3);
            table.setValueAt("-- Name --", 0, 0);
            table.setValueAt("-- Description --", 0, 1);
            table.setValueAt("-- Price --", 0, 2);
            table.setValueAt("Mine", 1, 0);
            table.setValueAt("Mine to boost metal production.", 1, 1);
            table.setValueAt("M:10  W:50  F:20  L:20", 1, 2);
            table.setValueAt("Lumber Mill", 2, 0);
            table.setValueAt("Mill to boost wood production.", 2, 1);
            table.setValueAt("M:50  W:10  F:20  L:20", 2, 2);
            table.setValueAt("Farm", 3, 0);
            table.setValueAt("Farm to boost food production.", 3, 1);
            table.setValueAt("M:20  W:40  F:20  L:20", 3, 2);
            table.setValueAt("School", 4, 0);
            table.setValueAt("School to boost education production.", 4, 1);
            table.setValueAt("M:40  W:20  F:20  L:20", 4, 2);
            constructionMenu.add(close);
            constructionMenu.add(l);
            constructionMenu.add(table);

            constructionMenu.setPopupSize(500, 500);
            constructionMenu.show(appFrame, 200, 55);
        }
    }

    private static class OpenResearchMenu implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JPopupMenu researchMenu = new JPopupMenu("Research");

            JButton close = new JButton("Close");
            close.addActionListener(new ClosePopup(researchMenu));
            JLabel l = new JLabel("Research popup");
            JTable table = new JTable(7, 3);
            table.setValueAt("-- Name --", 0, 0);
            table.setValueAt("-- Description --", 0, 1);
            table.setValueAt("-- Price --", 0, 2);
            table.setValueAt("Build Mines", 1, 0);
            table.setValueAt("Unlocks construction of Mines.", 1, 1);
            table.setValueAt("E:50  L:10", 1, 2);
            table.setValueAt("Build Lumber Mills", 2, 0);
            table.setValueAt("Unlocks construction of Lumber Mills.", 2, 1);
            table.setValueAt("E:50  L:10", 2, 2);
            table.setValueAt("Build Farms", 3, 0);
            table.setValueAt("Unlocks construction of Farms.", 3, 1);
            table.setValueAt("E:50  L:10", 3, 2);
            table.setValueAt("Build Schools", 4, 0);
            table.setValueAt("Unlocks construction of Schools.", 4, 1);
            table.setValueAt("E:50  L:10", 4, 2);
            table.setValueAt("Train Spearmen", 5, 0);
            table.setValueAt("Unlocks training of Spearmen.", 5, 1);
            table.setValueAt("E:50  L:10", 5, 2);
            table.setValueAt("Train Mounted Cavalry", 6, 0);
            table.setValueAt("Unlocks training of Mounted Calvalry.", 6, 1);
            table.setValueAt("E:50  L:10", 6, 2);
            researchMenu.add(close);
            researchMenu.add(l);
            researchMenu.add(table);

            researchMenu.setPopupSize(500, 500);
            researchMenu.show(appFrame, 308, 55);
        }
    }

    private static class ImageObject {
        // vars of ImageObject
        private double x;
        private double y;
        private double lastposx;
        private double lastposy;
        private double xwidth;
        private double yheight;
        private double angle; // in Radians
        private double internalangle; // in Radians
        private Vector<Double> coords;
        private Vector<Double> triangles;
        private double comX;
        private double comY;

        private int maxFrames;
        private int currentFrame;

        private int life;
        private int maxLife;
        private int dropLife;

        private Boolean bounce;

        public ImageObject() {
            maxFrames = 1;
            currentFrame = 0;
            bounce = false;
            life = 1;
            maxLife = 1;
            dropLife = 0;
        }

        // pgs 139-146
        public ImageObject(double xinput, double yinput, double xwidthinput, double yheightinput, double angleinput) {
            this();
            x = xinput;
            y = yinput;
            lastposx = x;
            lastposy = y;
            xwidth = xwidthinput;
            yheight = yheightinput;
            angle = angleinput;
            internalangle = 0.0;
            coords = new Vector<Double>();
        }

        public double getX() { return x; }
        public double getY() { return y; }
        public double getlastposx() { return lastposx; }
        public double getlastposy() { return lastposy; }
        public void setlastposx (double input) { lastposx = input; }
        public void setlastposy (double input) { lastposy = input; }
        public double getWidth() { return xwidth; }
        public double getHeight() { return yheight; }
        public double getAngle() { return angle; }
        public double getInternalAngle() { return internalangle; }
        public void setAngle(double angleinput) { angle = angleinput; }
        public void setInternalAngle(double internalangleinput) { internalangle = internalangleinput; }
        public Vector<Double> getCoords() { return coords; }
        public void setCoords(Vector<Double> coordsinput) {
            coords = coordsinput;
            generateTriangles();
            // printTriangles();
        }
        public int getMaxFrames() { return maxFrames; }
        public void setMaxFrames(int input) { maxFrames = input; }
        public int getCurrentFrame() { return currentFrame; }
        public void setCurrentFrame(int input) { currentFrame = input; }
        public Boolean getBounce() { return bounce; }
        public void setBounce(Boolean input) { bounce = input; }
        public int getLife() { return life; }
        public void setLife(int input) { life = input; }
        public int getMaxLife() { return maxLife; }
        public void setMaxLife(int input) { maxLife = input; }
        public int getDropLife() { return dropLife; }
        public void setDropLife(int input) { dropLife = input; }

        public void updateBounce() {
            if (getBounce()) {
                moveto(getlastposx(), getlastposy());
            } else {
                setlastposx(getX());
                setlastposy(getY());
            }
            setBounce(false);
        }

        public void updateCurrentFrame() {
            currentFrame = (currentFrame + 1) % maxFrames;
        }

        public void generateTriangles() {
            triangles = new Vector<Double>();
            // format: (0, 1), (2, 3), (4, 5) is the (x, y) coords of a triangle

            // get center point of all coordinates
            comX = getComX();
            comY = getComY();

            for (int i = 0; i < coords.size(); i = i + 2) {
                triangles.addElement(coords.elementAt(i));
                triangles.addElement(coords.elementAt(i + 1));

                triangles.addElement(coords.elementAt((i+2) % coords.size()));
                triangles.addElement(coords.elementAt((i+3) % coords.size()));

                triangles.addElement(comX);
                triangles.addElement(comY);
            }
        }

        public void printTriangles() {
            for (int i = 0; i < triangles.size(); i = i + 6) {
                System.out.println("p0x: " + triangles.elementAt(i) + ", p0y: " + triangles.elementAt(i+1));
                System.out.println("p1x: " + triangles.elementAt(i+2) + ", p1y: " + triangles.elementAt(i+3)
                        + triangles.elementAt(i+3));
                System.out.println("p2x: " + triangles.elementAt(i+4) + ", p2y: " + triangles.elementAt(i+5));
            }
        }

        public double getComX() {
            double ret = 0;
            if (coords.size() > 0) {
                for (int i = 0; i < coords.size(); i = i + 2) {
                    ret = ret + coords.elementAt(i);
                }
                ret = ret / (coords.size() / 2.0);
            }
            return ret;
        }

        public double getComY() {
            double ret = 0;
            if (coords.size() > 0) {
                for (int i = 1; i < coords.size(); i = i + 2) {
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

        public int screenWrap(double leftEdge, double rightEdge, double topEdge, double bottomEdge) {
            int ret = 0;
            if (x > rightEdge) {
                moveto(leftEdge, getY());
                ret = 1;
            }
            if (x < leftEdge) {
                moveto(rightEdge, getY());
                ret = 2;
            }
            if (y > bottomEdge) {
                moveto(getX(), topEdge);
                ret = 3;
            }
            if (y < topEdge) {
                moveto(getX(), bottomEdge);
                ret = 4;
            }

            return ret;
        }

//        public void rotate(double angleinput) {
//            angle = angle + angleinput;
//            while (angle > twoPi) {
//                angle = angle - twoPi;
//            }
//
//            while (angle < 0) {
//                angle = angle + twoPi;
//            }
//        }
//
//        public void spin(double internalangleinput) {
//            internalangle = internalangle + internalangleinput;
//            while (internalangle > twoPi) {
//                internalangle = internalangle - twoPi;
//            }
//
//            while (internalangle < 0) {
//                internalangle = internalangle + twoPi;
//            }
//        }

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
        appFrame.setSize(WINWIDTH + 1, WINHEIGHT + 85);

        JPanel myPanel = new JPanel();

        JMenuBar jMenuBar = new JMenuBar();
        appFrame.setJMenuBar(jMenuBar);

        JButton newGameButton = new JButton("Start");
        newGameButton.addActionListener(new StartGame());

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new QuitGame());

        JButton militaryButton = new JButton("Military");
        militaryButton.addActionListener(new OpenMilitaryMenu());

        JButton constructionButton = new JButton("Construction");
        constructionButton.addActionListener(new OpenConstructionMenu());

        JButton researchButton = new JButton("Research");
        researchButton.addActionListener(new OpenResearchMenu());

//        JMenu MilitaryMenu = new JMenu("Military");
//        JMenuItem LandItem = new JMenuItem("Land");
//        JMenuItem AirItem = new JMenuItem("Air");
//        JMenuItem SeaItem = new JMenuItem("Sea");
//        MilitaryMenu.add(LandItem);
//        MilitaryMenu.add(AirItem);
//        MilitaryMenu.add(SeaItem);

        jMenuBar.add(newGameButton);
        jMenuBar.add(quitButton);
        jMenuBar.add(militaryButton);
        jMenuBar.add(constructionButton);
        jMenuBar.add(researchButton);

//        JButton militaryButton = new JButton("Military");
//        newGameButton.addActionListener(new OpenMilitaryMenu());
//        myPanel.add(militaryButton);
//
//        JButton constructionButton = new JButton("Construction");
//        newGameButton.addActionListener(new OpenConstructionMenu());
//        myPanel.add(constructionButton);
//
//        JButton researchButton = new JButton("Research");
//        newGameButton.addActionListener(new OpenResearchMenu());
//        myPanel.add(researchButton);

        bindKey(myPanel, "UP");
        bindKey(myPanel, "DOWN");
        bindKey(myPanel, "LEFT");
        bindKey(myPanel, "RIGHT");

        appFrame.getContentPane().add(myPanel, "North");
        appFrame.setVisible(true);

//        MenuBar menuBar = new MenuBar();
//        appFrame.setMenuBar(menuBar);
//
//        Menu startGame = new Menu("Start");
//        startGame.addActionListener(new StartGame());
//
//        Menu militaryMenu = new Menu("Military");
//        MenuItem landItem = new MenuItem("Land");
//        MenuItem airItem = new MenuItem("Air");
//        MenuItem seaItem = new MenuItem("Sea");
//        militaryMenu.add(landItem);
//        militaryMenu.add(airItem);
//        militaryMenu.add(seaItem);
//
//        menuBar.add(startGame);
//        menuBar.add(militaryMenu);
    }
}
