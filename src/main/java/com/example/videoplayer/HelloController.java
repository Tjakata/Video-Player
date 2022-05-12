package com.example.videoplayer;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class HelloController implements Initializable {

    //private String path; //store the path of the selected media
    private MediaPlayer mediaPlayer;
    private ImageView ivplay;
    private ImageView ivpause;
    private ImageView ivstop;
    private Boolean isPlaying = true;

    @FXML
    private HBox hBox;

    @FXML
    private MediaView mediaView;

    @FXML
    private Button play_button;

    @FXML
    private Button stop_button;

    @FXML
    private Slider prograssBar;

    @FXML
    private Slider volumeController;


    @FXML
    private Label label_duration;

    @FXML
    void Play() { }
    @FXML
    void Stop() {mediaPlayer.stop();}
    Tooltip tooltip = new Tooltip();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        final int IV_SIZE = 15; //size of image icon

        //image play icon
        Image imagePlay = new Image(Objects.requireNonNull(getClass().getResource("/play-button-arrowhead.png")).toExternalForm()); //fetch image from its location
        ivplay = new ImageView(imagePlay); //setting icon
        ivplay.setFitHeight(IV_SIZE); //setting height of the icon
        ivplay.setFitWidth(IV_SIZE);  //setting width of the icon

        //image pause icon
        Image imagePause = new Image(Objects.requireNonNull(getClass().getResource("/pause.png")).toExternalForm()); //fetch image from its location
        ivpause = new ImageView(imagePause);
        ivpause.setFitHeight(IV_SIZE);
        ivpause.setFitWidth(IV_SIZE);

        //image stop icon
        Image imageStop = new Image(Objects.requireNonNull(getClass().getResource("/stop.png")).toExternalForm()); //fetch image from its location
        ivstop = new ImageView(imageStop);
        ivstop.setFitHeight(IV_SIZE);
        ivstop.setFitWidth(IV_SIZE);

        play_button.setGraphic(ivplay); //set play icon to play button
        stop_button.setGraphic(ivstop); //set stop icon to stop button

        //fetching the video from its location
        String src = Objects.requireNonNull(getClass().getResource("/Vusi Thembekwayo.mp4")).toExternalForm();
        Media media = new Media(src);
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        //setting width and height of the mediaview
        DoubleProperty widthProp = mediaView.fitWidthProperty();
        DoubleProperty heightProp = mediaView.fitHeightProperty();

        widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

        hBox.setAlignment(Pos.CENTER); //hbox children to center

        //method to play and pause a video
        play_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Button buttonPlay = (Button) event.getSource();
                if (isPlaying){
                    buttonPlay.setGraphic(ivplay);
                    tooltip.setText("Play");
                    buttonPlay.setTooltip(tooltip);
                    mediaPlayer.pause();
                    isPlaying = false;
                } else {
                    buttonPlay.setGraphic(ivpause);
                    tooltip.setText("Pause");
                    buttonPlay.setTooltip(tooltip);
                    mediaPlayer.play();
                    isPlaying = true;
                }

            }
        });

        //volume controller
        volumeController.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(volumeController.getValue());
            }
        });

        //video progass bar
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldDuration, Duration newDuration) {
                prograssBar.setValue(newDuration.toSeconds());
            }
        });
        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                Duration total = media.getDuration();
                prograssBar.setMax(total.toSeconds());
            }
        });

        //prograss seek handler
        prograssBar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                double presentTime = mediaPlayer.getCurrentTime().toSeconds();
                if (Math.abs(presentTime - newValue.doubleValue()) > 0.5){
                    mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                }
            }
        });

        bindCurrentTimeLabel(); //Method call

    }

    //PLAYBACK TIME SHOW
    public void bindCurrentTimeLabel(){
        label_duration.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getTime(mediaPlayer.getCurrentTime()) + " / " + getTime(mediaPlayer.getTotalDuration());
            }
        }, mediaPlayer.currentTimeProperty()));
    }

    public  String getTime(Duration time){
        int hours = (int) time.toHours();
        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();

        if (seconds > 59) seconds = seconds % 60;
        if (minutes > 59) minutes = minutes % 60;
        if (hours > 59) hours = hours % 60;

        if (hours > 0) return  String.format("%d:%02d:%02d",
                hours, minutes, seconds);
        else return String.format("%02d:%02d",
                minutes,seconds);
    }

}


    //Other methods to show playback time
     /*public static String getTimeString(double millis) {
        millis /= 1000;
        String s = formatTime(millis % 60);
        millis /= 60;
        String m = formatTime(millis % 60);
        millis /= 60;
        String h = formatTime(millis % 24);
        return h + ":" + m + ":" + s;
    }
    public static String formatTime(double time) {
        int t = (int)time;
        if (t > 9) {
            return String.valueOf(t); }
        return "0" + t; }*/


    //SHOW PLAY BACK TIME
    /*mediaPlayer.currentTimeProperty().addListener(observable -> {
            if (!prograssBar.isValueChanging()) {
            double total = mediaPlayer.getTotalDuration().toMillis();
            double current = mediaPlayer.getCurrentTime().toMillis();
            prograssBar.setMax(total);
            prograssBar.setValue(current);
            label_duration.setText(getTimeString(current) + "/" + getTimeString(total));
            }
            });

            prograssBar.valueProperty().addListener(observable -> {
            if (prograssBar.isValueChanging()) {
            mediaPlayer.seek(new Duration(prograssBar.getValue()));
            }
            });*/



//method to select media from computer
   /* public void media(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        path = file.toURI().toString();

        if (path != null){
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            mediaPlayer.play();
        }
    }*/