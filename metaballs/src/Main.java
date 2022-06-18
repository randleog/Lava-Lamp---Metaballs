

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
    private static double resolution = 2; //determines the number of pixels per pixel of the final image. too low means less performance.



    private static ArrayList<Particle> particles = new ArrayList<>();

    private static final int PARTICLE_SIZE = 200;   //this + particle min size = the max possible particle size.
    private static final int PARTICLE_MIN_SIZE = 125; //the minimum size of a particle

    private static int thresholdAmount = 220; //the cutoff point (in terms of 0-255, not yet factoring EDGE_ACCURACY)
    private static double edgeAccuracy = 50; //prevents edges being wrinkled after thresholding due to colour banding


    private static int colour = 0;

    //position of the mouse
    private static double mouseX = 0;
    private static double mouseY = 0;


    @Override
    public void start(Stage primaryStage) throws Exception {

        canvas = new Canvas(800, 800);

        canvas.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });


        Slider adjustEdgeAccuracy = new Slider();
        adjustEdgeAccuracy.setBlockIncrement(0.5);
        adjustEdgeAccuracy.setMax(50);
        adjustEdgeAccuracy.setMin(1);

        adjustEdgeAccuracy.setValue(50);



        adjustEdgeAccuracy.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number >
                                        observable, Number oldValue, Number newValue) {

                edgeAccuracy = newValue.doubleValue();



            }
        });


        Slider adjustThreshold = new Slider();
        adjustThreshold.setBlockIncrement(1);
        adjustThreshold.setMax(254);
        adjustThreshold.setMin(1);

        adjustThreshold.setValue(220);



        adjustThreshold.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number >
                                        observable, Number oldValue, Number newValue) {

                thresholdAmount = newValue.intValue();



            }
        });

        Slider adjustResolution = new Slider();
        adjustResolution.setBlockIncrement(0.1);
        adjustResolution.setMax(8);
        adjustResolution.setMin(1.4);

        adjustResolution.setValue(19);



        adjustResolution.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number >
                                        observable, Number oldValue, Number newValue) {

                resolution = adjustResolution.getMax() -newValue.doubleValue() + adjustResolution.getMin();



            }
        });


        Slider adjustColor = new Slider();
        adjustColor.setBlockIncrement(1);
        adjustColor.setMax(510);
        adjustColor.setMin(1);

        adjustColor.setValue(100);



        adjustColor.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number >
                                        observable, Number oldValue, Number newValue) {

                colour =newValue.intValue();



            }
        });


        VBox sliders = new VBox();
        sliders.getChildren().add(new Text("thresholding accuracy:"));
        sliders.getChildren().add(adjustEdgeAccuracy);

        sliders.getChildren().add(new Text("cutoff amount:"));
        sliders.getChildren().add(adjustThreshold);

        sliders.getChildren().add(new Text("resolution:"));
        sliders.getChildren().add(adjustResolution);
        sliders.getChildren().add(new Text("colour:"));
        sliders.getChildren().add(adjustColor);

        //  canvas.getGraphicsContext2D().setImageSmoothing(true);

        BorderPane pane = new BorderPane();
        pane.setCenter(canvas);
        pane.setRight(sliders);
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
        WritableImage image = new WritableImage((int)(canvas.getWidth() / resolution), (int)(canvas.getHeight() / resolution));

        for (int x = 0; x < image.getWidth(); x ++) {
            for (int y = 0; y < image.getHeight(); y ++) {

                int total = 0;

                for (Particle particle : particles) {
                    total += 17.5* edgeAccuracy * (particle.getSize() / getDistance(x* resolution, y* resolution, particle.getX(), particle.getY()));

                }

                total += 17.5 * edgeAccuracy *  (150 / getDistance(x* resolution, y* resolution, mouseX, mouseY));

                if (total > (int)(thresholdAmount * edgeAccuracy)) {
                    if (total > (int)(255* edgeAccuracy)) {
                        total = (int)(255* edgeAccuracy);
                    }
                    image.getPixelWriter().setColor(x, y ,  getColor(total/(255.0* edgeAccuracy)));

                }

            }
        }


        g.drawImage(image, 0,0, g.getCanvas().getWidth(), g.getCanvas().getHeight());

    }

    public static Color getColor(double opacity) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (colour <= 85) {


            r= 255;
            g = colour*3;


        } else if (colour <= 170) {

            g = 255;
            r= 255- (colour-85)*3;



        }else if (colour <= 255) {


            g = 255;
            b = (colour-170)*3;


        }else if (colour <= 340) {

            b= 255;
            g = 255- (colour-255)*3;

        }else if (colour <= 425) {

            b= 255;
            r = (colour-340)*3;

        }else if (colour <= 510) {


            r = 255;
            b= 255- (colour-425)*3;

        }



        return Color.rgb(r,g,b, opacity);
    }


}


