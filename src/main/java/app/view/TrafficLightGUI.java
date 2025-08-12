package app.view;

import app.model.*;
import app.sched.WeightedScheduler;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

public class TrafficLightGUI extends Application {

    private final Intersection x = new Intersection();
    private final WeightedScheduler sched = new WeightedScheduler(x, 1500, 1);
    private final Random rnd = new Random();

    private Label phaseLabel;
    private Circle northLight, southLight, eastLight, westLight;
    private ListView<String> northList, southList, eastList, westList;
    private Button autoBtn;
    private volatile boolean auto = false;
    private Timer timer;

    private Pane intersectionPane;

    private final Map<Direction, Point2D> entry = new HashMap<>();
    private final Map<Direction, Point2D> exit  = new HashMap<>();
    private Point2D center;

    public static void launchGUI() { launch(); }

    @Override
    public void start(Stage stage) {
        phaseLabel = new Label("Phase: NS");
        phaseLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        northLight = light(Color.GRAY);
        southLight = light(Color.GRAY);
        eastLight  = light(Color.GRAY);
        westLight  = light(Color.GRAY);

        northList = new ListView<>();
        southList = new ListView<>();
        eastList  = new ListView<>();
        westList  = new ListView<>();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        var controls = new HBox(10);
        var stepBtn = new Button("Step");
        autoBtn = new Button("Auto: OFF");
        var clearBtn = new Button("Clear");
        controls.getChildren().addAll(phaseLabel, stepBtn, autoBtn, clearBtn);
        controls.setAlignment(Pos.CENTER_LEFT);
        root.setTop(controls);

        intersectionPane = new Pane();
        intersectionPane.setPrefSize(420, 300);
        intersectionPane.setStyle("-fx-background-color: #2a2a2a; -fx-border-color: #555; -fx-border-width:1; -fx-background-insets: 0;");
        StackPane intersectionWrapper = new StackPane(intersectionPane);
        intersectionWrapper.setPadding(new Insets(12));
        intersectionPane.layoutBoundsProperty().addListener((obs, o, n) -> computePoints());

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(16);
        grid.add(roadPane("NORTH", Direction.NORTH, northList, northLight), 1, 0);
        grid.add(roadPane("WEST", Direction.WEST, westList, westLight), 0, 1);
        grid.add(intersectionWrapper, 1, 1);
        grid.add(roadPane("EAST", Direction.EAST, eastList, eastLight), 2, 1);
        grid.add(roadPane("SOUTH", Direction.SOUTH, southList, southLight), 1, 2);
        root.setCenter(grid);

        stepBtn.setOnAction(e -> doStep());
        autoBtn.setOnAction(e -> toggleAuto());
        clearBtn.setOnAction(e -> clearAll());

        updateLights(sched.getCurrent());

        Scene scene = new Scene(root, 900, 650);
        stage.setTitle("Intelligent Traffic Lights – GUI");
        stage.setScene(scene);
        stage.show();
    }

    private void computePoints() {
        double w = intersectionPane.getWidth();
        double h = intersectionPane.getHeight();
        center = new Point2D(w/2, h/2);
        entry.put(Direction.NORTH, new Point2D(w/2, 10));
        exit.put(Direction.NORTH,  new Point2D(w/2, -20));
        entry.put(Direction.SOUTH, new Point2D(w/2, h-10));
        exit.put(Direction.SOUTH,  new Point2D(w/2, h+20));
        entry.put(Direction.WEST,  new Point2D(10, h/2));
        exit.put(Direction.WEST,   new Point2D(-20, h/2));
        entry.put(Direction.EAST,  new Point2D(w-10, h/2));
        exit.put(Direction.EAST,   new Point2D(w+20, h/2));

        intersectionPane.getChildren().removeIf(n -> "road".equals(n.getId()));
        var roadNS = new javafx.scene.shape.Line(w/2, 0, w/2, h); roadNS.setStroke(Color.DARKGRAY); roadNS.setStrokeWidth(8); roadNS.setId("road");
        var roadEW = new javafx.scene.shape.Line(0, h/2, w, h/2); roadEW.setStroke(Color.DARKGRAY); roadEW.setStrokeWidth(8); roadEW.setId("road");
        intersectionPane.getChildren().addAll(roadNS, roadEW);
    }

    private VBox roadPane(String title, Direction d, ListView<String> list, Circle light) {
        var addBtn = new Button("Add vehicle");
        var rmBtn = new Button("Remove first");

        // Movement selector for this road
        var movement = new ComboBox<Turn>();
        movement.getItems().addAll(Turn.STRAIGHT, Turn.LEFT, Turn.RIGHT);
        movement.getSelectionModel().select(Turn.STRAIGHT);

        addBtn.setOnAction(e -> {
            Turn t = movement.getValue();
            Direction end = endFor(d, t);
            String id = title.charAt(0) + "-" + Integer.toHexString(1000 + rnd.nextInt(9000)) + "-" + t.name().charAt(0);
            x.by(d).add(new Vehicle(id, d, end));
            list.getItems().add(id + " (" + t.name().toLowerCase() + ")");
        });
        rmBtn.setOnAction(e -> {
            if (!x.by(d).isEmpty()) {
                var removed = x.by(d).poll();
                removeFromList(list, removed.getId());
            }
        });

        var box = new VBox(8,
                new HBox(8, new Label(title), new Label("Turn:"), movement),
                light,
                list,
                new HBox(8, addBtn, rmBtn));
        box.setPadding(new Insets(8));
        box.setPrefWidth(260);
        list.setPrefHeight(140);
        return box;
    }

    private Direction endFor(Direction start, Turn t) {
        return switch (start) {
            case NORTH -> switch (t) {
                case STRAIGHT -> Direction.SOUTH;
                case LEFT     -> Direction.EAST;
                case RIGHT    -> Direction.WEST;
            };
            case SOUTH -> switch (t) {
                case STRAIGHT -> Direction.NORTH;
                case LEFT     -> Direction.WEST;
                case RIGHT    -> Direction.EAST;
            };
            case EAST -> switch (t) {
                case STRAIGHT -> Direction.WEST;
                case LEFT     -> Direction.NORTH;
                case RIGHT    -> Direction.SOUTH;
            };
            case WEST -> switch (t) {
                case STRAIGHT -> Direction.EAST;
                case LEFT     -> Direction.SOUTH;
                case RIGHT    -> Direction.NORTH;
            };
        };
    }

    private void doStep() {
        Phase p = sched.decideNextPhase();
        var left = x.passThroughAxisWithVehicles(p);
        left.forEach(v -> {
            removeEverywhere(v.getId());
            animateVehicle(v);
        });
        updateLights(p);
    }

    private void animateVehicle(Vehicle v) {
        if (entry.isEmpty()) computePoints();
        Point2D start = entry.get(v.getStart());
        Point2D end = exit.get(v.getEnd());
        if (start == null || end == null) return;

        Circle dot = new Circle(6, randomColor());
        dot.setStroke(Color.BLACK);
        intersectionPane.getChildren().add(dot);

        double ctrlOffset = (v.getTurn() == Turn.LEFT) ? -60 : (v.getTurn() == Turn.RIGHT ? 60 : 0);
        Point2D control = controlPointFor(v.getStart(), ctrlOffset);

        Path path = new Path();
        path.getElements().add(new MoveTo(start.getX(), start.getY()));
        path.getElements().add(new QuadCurveTo(control.getX(), control.getY(), end.getX(), end.getY()));

        PathTransition t = new PathTransition(Duration.millis(2000), path, dot);
        t.setOnFinished(e -> intersectionPane.getChildren().remove(dot));
        t.play();
    }

    private Point2D controlPointFor(Direction from, double offset) {
        switch (from) {
            case NORTH: return center.add(offset, 40);
            case SOUTH: return center.add(offset, -40);
            case EAST:  return center.add(-40, offset);
            case WEST:  return center.add(40, offset);
        }
        return center;
    }

    private Color randomColor() { return Color.hsb(rnd.nextInt(360), 0.75, 0.95); }

    private void toggleAuto() {
        auto = !auto;
        autoBtn.setText(auto ? "Auto: ON" : "Auto: OFF");
        if (auto) {
            timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override public void run() { Platform.runLater(() -> doStep()); }
            }, 0, 800);
        } else if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void clearAll() {
        Stream.of(northList, southList, eastList, westList).forEach(l -> l.getItems().clear());
        x.north.getQueue().clear();
        x.south.getQueue().clear();
        x.east.getQueue().clear();
        x.west.getQueue().clear();
        intersectionPane.getChildren().removeIf(n -> !(n instanceof javafx.scene.shape.Line)); // keep roads
        updateLights(sched.getCurrent());
    }

    private void updateLights(Phase p) {
        if (p == Phase.NS) {
            setLight(northLight, Color.LIMEGREEN);
            setLight(southLight, Color.LIMEGREEN);
            setLight(eastLight, Color.RED);
            setLight(westLight, Color.RED);
            phaseLabel.setText("Phase: NS");
        } else {
            setLight(northLight, Color.RED);
            setLight(southLight, Color.RED);
            setLight(eastLight, Color.LIMEGREEN);
            setLight(westLight, Color.LIMEGREEN);
            phaseLabel.setText("Phase: EW");
        }
    }

    private static Circle light(Color c) { var r = new Circle(10, c); r.setStroke(Color.DARKGRAY); return r; }
    private static void setLight(Circle c, Color color) { c.setFill(color); }

    private void removeEverywhere(String id) {
        removeFromList(northList, id);
        removeFromList(southList, id);
        removeFromList(eastList, id);
        removeFromList(westList, id);
    }
    private void removeFromList(ListView<String> list, String id) { list.getItems().removeIf(s -> s.startsWith(id)); }

    private static Direction opposite(Direction d) {
        return switch (d) {
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
            case EAST -> Direction.WEST;
            case WEST -> Direction.EAST;
        };
    }
}
