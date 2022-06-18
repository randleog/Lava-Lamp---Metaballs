import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * @author William Randle
 */
public class Particle {





    private static final double GRAVITY = 1;

    private static final double DRAG = 0.99;

    boolean finished = false;

    private double velY;
    private double velX;


    private double size;


    private double accelX;
    private double accelY;


    private double x;
    private double y;





    private static final double DRIFT_SPEED = 0.5;
    private static final double DRIFT_TENDANCY = 4;

    public Particle(double x, double y, double size) {



        this.velX = 0;
        this.velY = 0;
        this.size = size;


        this.x =x;
        this.y = y;
        accelX = 0;
        accelY = 0;


    }




    public void drift() {


            if ((int)(Math.random()*DRIFT_TENDANCY) == 1) {
            //    this.velY = Math.random() * DRIFT_SPEED - DRIFT_SPEED / 2;
               // this.velX = Math.random() * DRIFT_SPEED - DRIFT_SPEED / 2;

                this.accelX = Math.random() * DRIFT_SPEED - DRIFT_SPEED / 2;
                this.accelY = Math.random() * DRIFT_SPEED - DRIFT_SPEED / 2;
            }
        }






    protected void physics() {

        drift();

        velY = velY * DRAG;


        velX+=accelX;
        velY+=accelY;

        velX =velX*DRAG;
        velY = velY*DRAG;

        x += velX;
        y += velY;
    }


    public void tick() {


        physics();
        clamp();


    }


    private void clamp() {
        if (this.x < 0) {
            this.velX = 0;
            this.x = 0;
        }
        if (this.x > Main.canvas.getWidth()) {
            this.velX = 0;
            x =  Main.canvas.getWidth();
        }

        if (this.y < 0) {
            this.velY =0;
            this.y = 0;
        }

        if (this.y > Main.canvas.getHeight()) {
            this.velY = 0;
            this.y = Main.canvas.getHeight();
        }
    }



    public double getSize() {
        return size;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }





}
