package com.fraserbrooks.progresstracker;

import android.view.View;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by Fraser on 25/12/2017.
 */

public class Tracker implements Serializable{

    private String name;
    private int minutes_to_max_level;
    private int time_score;
    private float progressPercentage;
    private boolean expanded;
    private long timerStartTime;
    private boolean isBeingTimed;
    private boolean noDifficulty;
    private ArrayList<Target> targets;

    public Tracker(String name, int hours_to_max_level, int time_score) {

        if(hours_to_max_level == -1){
            noDifficulty = true;
            minutes_to_max_level = 4800;
        } else{
            noDifficulty = false;
            this.minutes_to_max_level = hours_to_max_level * 60;
        }

        this.name = name;
        this.time_score = time_score;
        this.expanded = false;

        updateProgressPercentage();

        this.targets = new ArrayList<>();
    }

    public void setName(String name){
        this.name = name;
        for (Target t:targets) {
            t.updateTrackerName(name);
        }
    }

    public void setTimer(long milliseconds){
        if(!isBeingTimed){
            timerStartTime = milliseconds;
            isBeingTimed = true;
        }
    }

    public void addTarget(Target t){
        this.targets.add(t);
    }

    public void endTimerCount(){
        isBeingTimed = false;
        addMinutes(getTimerMinutes());
    }

    public int getTimerMinutes(){
        long timePassedMilli = System.currentTimeMillis() - timerStartTime;
        long timePassedMinutes = (timePassedMilli / 1000) / 60;
        return (int) timePassedMinutes;
    }

    public boolean isBeingTimed(){
        return isBeingTimed;
    }

    public int getLevel(){
        int level = 0;
        float score = time_score;
        int bm = 0;
        if(minutes_to_max_level != 0){
            bm = minutes_to_max_level;
        }else{
            bm = 600;
        }
        while((score / (float) bm) > (1/8f)){
            level += 1;
            score -= (float) bm / 8;
        }
        return level;
    }

    public void updateProgressPercentage(){
        float score = time_score;
        int bm = 0;
        if(minutes_to_max_level != 0){
            bm = minutes_to_max_level;
        }else{
            bm = 600;
        }
        while((score / ((float) bm)) > (1/8f)){
            score -= (float) bm / 8;
        }
        float decimal = score / ((float) bm / 8);
        if (decimal >= 1f){
            decimal = 0.999f;
        }
        this.progressPercentage = decimal;
    }

    public void addMinutes(int minutes){
        this.time_score += minutes;
        if(time_score < 0){
            time_score = 0;
        }
        updateProgressPercentage();
        int l = getLevel();
        for (Target t: targets) {
            t.updateTime(minutes);
            t.updateLevel(l);
        }
    }

    public float getProgressPercentage(){
        return progressPercentage;
    }

    public String getName(){
        return name;
    }

    public int getMinutes(){
        return time_score;
    }

    public int getMinutesToMaxLevel(){
        return minutes_to_max_level;
    }

    public void expand(){
        this.expanded = true;
    }

    public void collapse(){
        this.expanded = false;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public boolean isNoDifficulty(){
        return noDifficulty;
    }

    public void setNoDifficulty(boolean noDifficulty) {
        this.noDifficulty = noDifficulty;
    }

    public void setMinutesToMaxLevel(int minutes){
        if(minutes == -1){
            noDifficulty = true;
            minutes_to_max_level = 4800;
        } else{
            noDifficulty = false;
            minutes_to_max_level = minutes;
        }
        updateProgressPercentage();
    }

    public boolean setColourAndIcon(ImageView gemImage, View colourRect){
        if(noDifficulty){
            gemImage.setImageResource(R.drawable.heart_red);
            colourRect.setBackgroundResource(R.drawable.heart_colour_rect);
        }else{
            setLevelColour(gemImage, colourRect);
        }
        return true;
    }

    public boolean setColourAndIcon(View colourRect){
        if(noDifficulty){
            colourRect.setBackgroundResource(R.drawable.heart_colour_rect);
        }else{
            setLevelColour(null, colourRect);
        }
        return true;
    }

    public boolean setLevelColour( ImageView gemImage, View colourRect) {
        switch (getLevel()){
            case 0:
                if(gemImage != null){
                    gemImage.setImageResource(R.drawable.gem_blank);
                }
                colourRect.setBackgroundResource(R.drawable.level1_colour_rect);
                break;
            case 1:
                if(gemImage != null) {
                    gemImage.setImageResource(R.drawable.gem_yellow);
                }
                colourRect.setBackgroundResource(R.drawable.level2_colour_rect);
                break;
            case 2:
                if(gemImage != null) {
                    gemImage.setImageResource(R.drawable.gem_orange);
                }
                colourRect.setBackgroundResource(R.drawable.level3_colour_rect);
                break;
            case 3:
                if(gemImage != null) {
                    gemImage.setImageResource(R.drawable.gem_green);
                }
                colourRect.setBackgroundResource(R.drawable.level4_colour_rect);
                break;
            case 4:
                if(gemImage != null) {
                    gemImage.setImageResource(R.drawable.gem_purple);
                }
                colourRect.setBackgroundResource(R.drawable.level5_colour_rect);
                break;
            case 5:
                if(gemImage != null) {
                    gemImage.setImageResource(R.drawable.gem_lightblue);
                }
                colourRect.setBackgroundResource(R.drawable.level6_colour_rect);
                break;
            case 6:
                if(gemImage != null) {
                    gemImage.setImageResource(R.drawable.gem_blue);
                }
                colourRect.setBackgroundResource(R.drawable.level7_colour_rect);
                break;
            case 7:
                if(gemImage != null) {
                    gemImage.setImageResource(R.drawable.gem_brown);
                }
                colourRect.setBackgroundResource(R.drawable.level8_colour_rect);
                break;
            case 8:
                if(gemImage != null) {
                    gemImage.setImageResource(R.drawable.gem_black);
                }
                colourRect.setBackgroundResource(R.drawable.level8_colour_rect);
                break;
            default:
                if(gemImage != null) {
                    gemImage.setImageResource(R.drawable.gem_black);
                }
                colourRect.setBackgroundResource(R.drawable.level8_colour_rect);
                return false;

        }
        return true;

    }

    public ArrayList<Target> getTargets() {
        return targets;
    }
}
