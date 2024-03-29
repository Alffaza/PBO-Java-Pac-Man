package pacman;

import javax.swing.*;import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;
import java.io.FileWriter;
import java.util.Vector;


public class Ghost extends Entity{

    public boolean isDead;
    public boolean isVulnerable;
    private Cooldown deathCooldown = new Cooldown(3);
    private Image vulnerableImage;
    private Image eyesImage;

    private URL loadImage(String fileName){
        return getClass().getResource("/images/ghost/" + fileName);
    }
    private URL urlGhostLeft = loadImage("ghostLeft.gif");
    private URL urlGhostRight = loadImage("ghostRight.gif");
    private URL urlGhostUp = loadImage("ghostUp.gif");
    private URL urlGhostDown = loadImage("ghostDown.gif");
    private URL urlGhostVulnerable = loadImage("vulnerable.png");
    private URL urlGhostEyes = loadImage("ghostEyes.png");
    int[] dy_,dx_;

    public Ghost(int x, int y, int dx, int dy, int speed){
        super(x,y,dx,dy,speed);
        dx_ = new int[4];
        dy_ = new int[4];
        isFacing = Direction.LEFT;
        this.imgs[0] = new ImageIcon(urlGhostLeft).getImage();
        this.imgs[1] = new ImageIcon(urlGhostRight).getImage();
        this.imgs[2] = new ImageIcon(urlGhostUp).getImage();
        this.imgs[3] = new ImageIcon(urlGhostDown).getImage();
        vulnerableImage = new ImageIcon(urlGhostVulnerable).getImage();
        eyesImage = new ImageIcon(urlGhostEyes).getImage();
        isDead = false;
    }

    @Override
    public Image getCurrentImage() {
        if(isDead)
            return eyesImage;
        if(isVulnerable)
            return vulnerableImage;
        return super.getCurrentImage();
    }

    public boolean detectPlayerCollision(Player pacman){
        if(pacman != null){
            checkPlayer(pacman);
            return pacman.x > (x - 24) && pacman.x < (x + 24)
                    && pacman.y > (y - 24) && pacman.y < (y + 24);
        }
        else{
            System.out.println("Player object null!");
        }
        return false;
    }

    private void checkPlayer(Player pacman){
        isVulnerable = pacman.canEatGhosts;
    }

    public boolean deathCooldownFinished(){
        deathCooldown.updateTimer();
        return deathCooldown.isReady();
    }

    public void death(){
        deathCooldown.start();
        isDead = true;
    }

    public void respawn(){
        isDead = false;
    }

    public void respawnIfReady(){
        if(deathCooldownFinished()){
            respawn();
        }
    }

    public int getScreenPos(int gridSize, int gridN){
        return x/gridSize + gridN * (y/gridSize);
    }

    public void moveGhost(short screenData){
        int count;

        count = 0;

        if ((screenData & 1) == 0 && dx != 1) {
            dx_[count] = -1;
            dy_[count] = 0;
            count++;
        }

        if ((screenData & 2) == 0 && dy != 1) {
            dx_[count] = 0;
            dy_[count] = -1;
            count++;
        }

        if ((screenData & 4) == 0 && dx != -1) {
            dx_[count] = 1;
            dy_[count] = 0;
            count++;
        }

        if ((screenData & 8) == 0 && dy != -1) {
            dx_[count] = 0;
            dy_[count] = 1;
            count++;
        }

        if (count == 0) {

            if ((screenData & 15) == 15) {
                dx = 0;
                dx = 0;
            } else {
                dx = -dx;
                dy = -dy;
            }

        } else {

            count = (int) (Math.random() * count);

            if (count > 3) {
                count = 3;
            }

            dx = dx_[count];
            dy = dy_[count];
        }

        //update ghost facing direction
        if(dx < 0) isFacing = Direction.LEFT;
        if(dx > 0) isFacing = Direction.RIGHT;
        if(dy < 0) isFacing = Direction.UP;
        if(dy > 0) isFacing = Direction.DOWN;
    }
}
