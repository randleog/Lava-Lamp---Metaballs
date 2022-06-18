

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

/**
* @author William Randle
 */
public class Main extends Application {

    public static Canvas canvas;

    public static Timeline loop;

    private static final int FPS = 25; //determines the frames per second
    private static final int RESOLUTION = 2; //determines the number of pixels per pixel of the final image. too low means less performance.
    private static final double EDGE_ACCURACY = 100; //prevents edges being wrinkled after thresholding due to colour banding


    private static ArrayList<Particle> particles = new ArrayList<>();

    private static final int PARTICLE_SIZE = 200;   //this + particle min size = the max possible particle size.
    private static final int PARTICLE_MIN_SIZE = 125; //the minimum size of a particle

    private static final int THRESHOLD_AMOUNT = 220; //the cutoff point (in terms of 0-255, not yet factoring EDGE_ACCURACY)


    //position of the mouse
    private static double mouseX = 0;
    private static double mouseY = 0;


    @Override
    public void start(Stage primaryStage) throws Exception {

        canvas = new Canvas(800, 800);
        canvas.getGraphicsContext2D().setImageSmoothing(true);

        canvas.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });
      //  canvas.getGraphicsContext2D().setImageSmoothing(true);

        BorderPane pane = new BorderPane();
        pane.setCenter(canvas);
        primaryStage.setScene(new Scene(pane));

        primaryStage.show();

        double fpstime = 1000.0 / FPS;

        loop = new Timeline(new KeyFrame(Duration.millis(fpstime), (ActionEvent event) -> {
            tick();
            render(canvas.getGraphicsContext2D());
        }));

        loop.setCycleCount(Animation.INDEFINITE);

        loop.play();


        for (int i = 0; i < 17; i++) {
            double size = Math.random() * PARTICLE_SIZE + PARTICLE_MIN_SIZE;
            particles.add(new Particle(Math.random() * canvas.getWidth(), Math.random() * canvas.getHeight(), size));
        }


    }

    //progresses the logic of the system with time
    public void tick() {
        for (Particle particle : particles) {
            particle.tick();
        }
    }

    //renders the canvas background and any details needed on top
    public void render(GraphicsContext g) {
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());

        renderSplash(g);
    }


    //obtains the distance between two points.
    public static double getDistance(double x, double y, double x1, double y1) {
        return Math.sqrt(Math.pow(x - x1, 2) + Math.pow(y - y1, 2));
    }


    //draws the metaballs
    public static void renderSplash(GraphicsContext g) {
        //goes through each pixel and adds up the total distances to each particle (factoring their size) then thresholding this total to create an image. then draw this image to canvas.
        WritableImage image = new WritableImage((int)(canvas.getWidth() / RESOLUTION), (int)(canvas.getHeight() / RESOLUTION));

        for (int x = 0; x < image.getWidth(); x ++) {
            for (int y = 0; y < image.getHeight(); y ++) {

                int total = 0;

                for (Particle particle : particles) {
                    total += 17.5*EDGE_ACCURACY * (particle.getSize() / getDistance(x* RESOLUTION, y* RESOLUTION, particle.getX(), particle.getY()));

                }

                total += 17.5*EDGE_ACCURACY * (1.5*EDGE_ACCURACY / getDistance(x* RESOLUTION, y* RESOLUTION, mouseX, mouseY));

                if (total > THRESHOLD_AMOUNT*EDGE_ACCURACY) {
                    if (total > (int)(255*EDGE_ACCURACY)) {
                        total = (int)(255*EDGE_ACCURACY);
                    }
                    image.getPixelWriter().setColor(x, y , Color.rgb((int)(total/EDGE_ACCURACY), 50, 0, total/(255.0*EDGE_ACCURACY)));

                }

            }
        }


        g.drawImage(image, 0,0, g.getCanvas().getWidth(), g.getCanvas().getHeight());

    }


}

