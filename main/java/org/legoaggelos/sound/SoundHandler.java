package org.legoaggelos.sound;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

public class SoundHandler {
    private double volume = 0.3;
    private final List<MediaPlayer> tracks = new ArrayList<>();
    public SoundHandler(List<String> tracks) {
        for (String track : tracks) {
            //TODO: add mute option
            Media sound = new Media(Objects.requireNonNull(getClass().getResource(track)).toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.stop();
            mediaPlayer.setVolume(volume);
            this.tracks.add(mediaPlayer);
        }
    }
    public void muteAll() {
        for (MediaPlayer mediaPlayer : tracks) {
            mediaPlayer.setVolume(0);
            mediaPlayer.setMute(true);
        }
    }
    public void unmuteAll() {
        for (MediaPlayer mediaPlayer : tracks) {
            mediaPlayer.setVolume(volume);
            mediaPlayer.setMute(false);
        }
    }
    public int playRandomTrack(RandomGenerator random) {
        int index = random.nextInt(0, tracks.size());
        tracks.get(index).play();
        return index;
    }
    public void muteTrack(int index) {
        tracks.get(index).setMute(true);
    }
    public void unmuteTrack(int index) {
        tracks.get(index).setMute(false);
    }
    public void setTrackToRepeat(int index) {
        var track = tracks.get(index);
        track.play();
        track.setAutoPlay(true);
        track.setCycleCount(MediaPlayer.INDEFINITE);
        track.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                track.seek(Duration.seconds(3));
                track.play();
            }
        });
    }
    public void playTrack(int index) {
        tracks.get(index).play();
    }
    public boolean isTrackPlaying(int index) {
        return tracks.get(index).getStatus() == MediaPlayer.Status.PLAYING;
    }
    public void stopTrack(int index) {
        this.tracks.get(index).stop();
    }
    public void stopAndRepeat(int index) {
        this.tracks.get(index).stop();
        this.tracks.get(index).play();
    }
    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public void changeVolume(double change) {
        this.volume += 0.1 * Math.floor(change*10);
        if (volume < 0) {
            volume = 0;
        } else if(volume > 1) {
            volume = 1;
        }
    }
}
