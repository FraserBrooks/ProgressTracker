package com.fraserbrooks.progresstracker.trackers.domain.model;



import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import com.fraserbrooks.progresstracker.BuildConfig;

import java.security.InvalidParameterException;
import java.util.UUID;

/**
 * Created by Fraser on 07/04/2018.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@Entity(tableName = "trackers")
public class Tracker{

    public static final int TYPE_LEVEL_UP = 1000;
    public static final int TYPE_GRAPH    = 2000;
    public static final int TYPE_BOOLEAN  = 3000;

    public static final int GRAPH_TYPE_DAY = 1;
    public static final int GRAPH_TYPE_WEEK = 2;
    public static final int GRAPH_TYPE_MONTH = 3;
    public static final int GRAPH_TYPE_YEAR = 4;

    public static final int GRAPH_FORMAT_ONE = 51;
    public static final int GRAPH_FORMAT_TWO = 52;
    public static final int GRAPH_FORMAT_THREE = 53;
    public static final int GRAPH_FORMAT_FOUR = 54;

    public static final int ICON_LEVEL = 101;
    public static final int ICON_TEXT = 102;
    public static final int ICON_HEART = 103;
    public static final int ICON_STUDY = 104;
    public static final int ICON_COMPUTER = 105;
    public static final int ICON_BOOK = 106;
    public static final int ICON_PENCIL = 107;
    public static final int ICON_APPLE = 108;
    public static final int ICON_PHONE = 109;
    public static final int ICON_PEOPLE = 110;

    private static final int DEFAULT_INDEX = 999;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "tracker_id")
    private final String mId;

    @NonNull
    @ColumnInfo(name = "title")
    private String mTitle;

    @NonNull
    @ColumnInfo(name = "counter_label")
    private String mCounterLabel;



    @ColumnInfo(name = "score")
    private int mScoreSoFar;

    // Score needed to reach level 8
    @ColumnInfo(name = "level_rate")
    private int mLevelRate;



    @ColumnInfo(name = "time_tracker")
    private final boolean mTimeTracker;

    @ColumnInfo(name = "timer_start")
    private long mTimerStartTime;

    @ColumnInfo(name = "currently_timing")
    private boolean mCurrentlyTiming;


    @ColumnInfo(name = "button_one_add_amount")
    private int mButtonOneAddAmount;

    @ColumnInfo(name = "button_two_add_amount")
    private int mButtonTwoAddAmount;


    @ColumnInfo(name = "archived")
    private boolean mArchived;

    @ColumnInfo(name = "index")
    private int mIndex;

    @ColumnInfo(name = "tracker_type")
    private int mType;

    @ColumnInfo(name = "graph_type")
    private int mGraphType;

    @ColumnInfo(name = "graph_format")
    private int mGraphFormat;

    @ColumnInfo(name = "icon_number")
    private int mIcon;

    @ColumnInfo(name = "icon_text")
    private String mIconText;

    @ColumnInfo(name = "color_base")
    private int mColorBase;

    @ColumnInfo(name = "last_update")
    private long  mLastUpdate;

    // Values used by the UI -----------------------
    @Ignore
    private int mLevel;
    @Ignore
    private float mProgress;
    @Ignore
    private String mLevelToDisplay = "";
    @Ignore
    private int[] mPastEightDaysCounts;
    @Ignore
    private int[] mPastEightWeeksCounts;
    @Ignore
    private int[] mPastEightMonthsCounts;



    /**
     * Use this constructor to create a new Level Up Tracker that tracks time (eg. minutes/hours).
     *
     * @param title           title of the tracker
     * @param levelRate       number of minutes to get to max level
     * @param icon            tracker icon number
     * @param iconText        two letter string to display if this tracker's icon is TEXT
     * @param index           index used for user defined ordering in the UI
     */
    @Ignore
    public static Tracker TimeLevelUpTracker(@NonNull String title, int levelRate, int  icon,
                   @NonNull String iconText, int index){
        return new Tracker(UUID.randomUUID().toString(), title, "hours", 0,
                levelRate, true, 0, false,15,
                60, false, index, TYPE_LEVEL_UP, GRAPH_TYPE_DAY,
                GRAPH_FORMAT_ONE, icon, iconText, 0, 0);
    }


    /**
     * Use this constructor to create a new Level-Up Tracker that tracks something other than time
     *
     * @param title           title of the tracker
     * @param counterLabel    thing being counted (eg. 'minutes' 'projects done', 'miles ran', etc.)
     * @param levelRate       number of minutes to get to max level
     * @param icon            tracker icon number
     * @param iconText        two letter string to display if this tracker's icon is TEXT
     * @param index           index used for user defined ordering in the UI
     */
    @Ignore
    public static Tracker NonTimeLevelUpTracker(@NonNull String title, @NonNull String counterLabel,
                                                int levelRate, int  icon, @NonNull String iconText,
                                                int index){
        return new Tracker(UUID.randomUUID().toString(), title, counterLabel, 0,
                levelRate, false, 0, false,
                1,5, false, index, TYPE_LEVEL_UP,
                GRAPH_TYPE_DAY, GRAPH_FORMAT_ONE, icon, iconText, 0, 0);

    }


    /**
     * Use this method to create a new graph Tracker that tracks time (minutes/hours).
     *
     * @param title           title of the tracker
     * @param graphType       default graph for this tracker in the ui (day/week/month/year)
     * @param graphFormat     value for customizable ui elements
     * @param icon            tracker icon number
     * @param iconText        two letter string to display if this tracker's icon is TEXT
     * @param color           int that is combined with theme values to create this icons color
     * @param index           index used for user defined ordering in the UI
     */
    @Ignore
    public static Tracker TimeGraphTracker(@NonNull String title, int graphType, int graphFormat,
                   int  icon, @NonNull String iconText, int color, int index){
        return new Tracker(UUID.randomUUID().toString(), title, "hours", 0,
                -1, true, 0, false, 15, 60,
                false, index, TYPE_GRAPH, graphType, graphFormat, icon, iconText, color, 0);

    }

    /**
     * Use this method to create a new graph Tracker that tracks something other than time
     *
     * @param title           title of the tracker
     * @param counterLabel    thing being counted (eg. 'minutes' 'projects done', 'miles ran', etc.)
     * @param graphType       default graph for this tracker in the ui (day/week/month/year)
     * @param graphFormat     value for customizable ui elements
     * @param icon            tracker icon number
     * @param iconText        two letter string to display if this tracker's icon is TEXT
     * @param color           int that is combined with theme values to create this icons color
     * @param index           index used for user defined ordering in the UI
     */
    @Ignore
    public static Tracker NonTimeGraphTracker(@NonNull String title, @NonNull String counterLabel,
                                          int graphType, int graphFormat,
                                          int icon, @NonNull String iconText, int color, int index){
        return new Tracker(UUID.randomUUID().toString(), title, counterLabel, 0, -1,
                false, 0, false, 1,5,
                false, index, TYPE_GRAPH, graphType, graphFormat, icon, iconText, color, 0);
    }

    /**
     * Use this method to create a new boolean Tracker
     *
     * @param title           title of the tracker
     * @param counterLabel    thing being counted (eg. 'minutes' 'projects done', 'miles ran', etc.)
     * @param graphType       default graph for this tracker in the ui (day/week/month/year)
     * @param graphFormat     value for customizable ui elements
     * @param icon            tracker icon number
     * @param iconText        two letter string to display if this tracker's icon is TEXT
     * @param color           int that is combined with theme values to create this icons color
     * @param index           index used for user defined ordering in the UI
     */
    @Ignore
    public static Tracker BooleanTracker(@NonNull String title, @NonNull String counterLabel,
                                       int graphType, int graphFormat,
                                       int icon, @NonNull String iconText, int color, int index){
        return new Tracker(UUID.randomUUID().toString(), title, counterLabel, 0, -1,
                false, 0, false, 1,1,
                false, index, TYPE_BOOLEAN, graphType, graphFormat, icon, iconText, color, 0);
    }


    /**
     * Full constructor
     * This constructor should only ever be used by room
     *
     * @param id                  id of the tracker
     * @param title               title of the tracker
     * @param counterLabel        thing being counted (eg. 'minutes' 'projects done', 'miles ran', etc.)
     * @param scoreSoFar          count of minutes/'counterName' so far
     * @param levelRate           number of 'counterLabel's (minutes if time tracker) to get to max level
     * @param timeTracker         this tracker will track time (i.e hours/minutes spent on an activity)
     * @param timerStartTime      time that the user started timing. Used only if 'isTimeTracker' = true
     * @param currentlyTiming     is this tracker currently being timed
     * @param buttonOneAddAmount  how much to add to score on button 1 click
     * @param buttonTwoAddAmount  how much to add to score on button 2 click
     * @param archived            boolean: whether or not the tracker has been archived
     * @param index               index used for user defined ordering in the UI
     * @param type                type of tracker (LEVEL_UP, GRAPH, or BOOLEAN)
     * @param graphType           the graph to show (DAY, WEEK, MONTH, or YEAR)
     * @param graphFormat         value for customizable ui elements
     * @param icon                enum representing the icon for this particular tracker
     * @param iconText            String containing only once character/emoji for use as icon
     * @param colorBase           int that marks the difference in color of trackers for a theme
     * @param lastUpdate          time of last update to this particular tracker
     */
    public Tracker(@NonNull String id, @NonNull String title, @NonNull String counterLabel,
                   int scoreSoFar, int levelRate, boolean timeTracker, long timerStartTime,
                   boolean currentlyTiming, int buttonOneAddAmount, int buttonTwoAddAmount,
                      boolean archived, int index, int type, int graphType,
                   int graphFormat, int icon, @NonNull String iconText, int colorBase, long lastUpdate
                   ){

        this.mId = id;
        this.mTitle = title;
        this.mCounterLabel = counterLabel;
        this.mScoreSoFar = scoreSoFar;
        this.mLevelRate = levelRate;
        this.mTimeTracker = timeTracker;
        this.mTimerStartTime = timerStartTime;
        this.mCurrentlyTiming = currentlyTiming;
        this.mButtonOneAddAmount = buttonOneAddAmount;
        this.mButtonTwoAddAmount = buttonTwoAddAmount;
        this.mArchived = archived;
        this.mIndex = index;
        this.mType = type;
        this.mGraphType = graphType;
        this.mGraphFormat = graphFormat;
        this.mIcon = icon;
        this.mIconText = iconText;
        this.mColorBase = colorBase;
        this.mLastUpdate = lastUpdate;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getLevelRate() {
        return mLevelRate;
    }

    public void setLevelRate(int progressionRate){
        mLevelRate = progressionRate;
    }

    public int getScoreSoFar() {
        return mScoreSoFar;
    }

    public void setScoreSoFar(int countSoFar){
        mScoreSoFar = countSoFar;
    }

    public long getTimerStartTime() {
        return mTimerStartTime;
    }

    public void setTimerStartTime(Long timerStartTime){
        mTimerStartTime = timerStartTime;
    }

    public boolean isCurrentlyTiming() {
        return mCurrentlyTiming;
    }

    public boolean isTimeTracker() {
        return mTimeTracker;
    }

    @NonNull
    public String getCounterLabel() {
        return mCounterLabel;
    }

    public void setCounterLabel(String counterLabel){
        mCounterLabel = counterLabel;
    }

    public long getLastUpdate() {
        return mLastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.mLastUpdate = lastUpdate;
    }

    public int getButtonOneAddAmount() {
        return mButtonOneAddAmount;
    }

    public void setButtonOneAddAmount(int buttonOneAddAmount) {
        this.mButtonOneAddAmount = buttonOneAddAmount;
    }

    public int getButtonTwoAddAmount() {
        return mButtonTwoAddAmount;
    }

    public void setButtonTwoAddAmount(int buttonTwoAddAmount) {
        this.mButtonTwoAddAmount = buttonTwoAddAmount;
    }

    public boolean isArchived() {
        return mArchived;
    }

    public void setArchived(boolean b){
        mArchived = b;
    }

    public void startTimingAt(long time){

        setTimerStartTime(time);
        this.mCurrentlyTiming = true;

    }

    public void resetTimer(){
        setTimerStartTime(0L);
        this.mCurrentlyTiming = false;
    }

    public int getMinutesSinceTimerStart(long now){
        long timePassedMilli = now - getTimerStartTime();
        long timePassedMinutes = (timePassedMilli / 1000) / 60;
        return (int) timePassedMinutes;
    }


    public void setUiValues(){
        int level = 1;
        float x = getScoreSoFar();
        int countToMax = getLevelRate();
        while((x / (float) countToMax) > (1/8f)){
            level += 1;
            x -= (float) countToMax / 8;
        }
        mLevel = level;

        float soFar = getScoreSoFar();
        while((soFar / ((float) countToMax)) > (1/8f)){
            soFar -= (float) countToMax / 8;
        }
        float decimal = soFar / ((float) countToMax / 8);
        if (decimal >= 1f){
            decimal = 1f;
        }
        mProgress = decimal;

        String levelToDisplay;

        // Only display level if past max level and if level up tracker
        if (getLevel() > 8 && getType() ==TYPE_LEVEL_UP){
            int l = getLevel();
            levelToDisplay = "" + l;
        } else{
            levelToDisplay = ""; // Don't display level
        }
        mLevelToDisplay = levelToDisplay;
    }

    public int getLevel(){
        return mLevel;
    }

    public float getPercentageToNextLevel(){
        return mProgress;
    }

    public String getLevelToDisplay(){
        return mLevelToDisplay;
    }

    @Override
    public String toString() {
        return "Tracker with title: " + mTitle + ", and score: " + mScoreSoFar;
    }


    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }

    public int[] getPastEightDaysCounts() {
        return mPastEightDaysCounts;
    }

    public void setPastEightDaysCounts(int[] pastEightDaysCounts) {

        if(pastEightDaysCounts.length != 8) throw new RuntimeException();

        this.mPastEightDaysCounts = pastEightDaysCounts;
    }

    public int[] getPastEightWeeksCounts() {
        return mPastEightWeeksCounts;
}

    public void setPastEightWeekCounts(int[] pastEightWeekCounts) {

        if(pastEightWeekCounts.length != 8) throw new RuntimeException();

        this.mPastEightWeeksCounts = pastEightWeekCounts;
    }

    public int[] getPastEightMonthsCounts() {
        return mPastEightMonthsCounts;
    }

    public void setPastEightMonthCounts(int[] pastEightMonthCounts) {

        if(pastEightMonthCounts.length != 8) throw new RuntimeException();

        this.mPastEightMonthsCounts = pastEightMonthCounts;
    }


    public int getType() {
        return mType;
    }

    public void setType(int type) {
        if(BuildConfig.DEBUG){
            if(type > 999 && type < 10000){
                throw new InvalidParameterException();
            }
        }
        this.mType = type;
    }

    public int getGraphType() {
        return mGraphType;
    }

    public void setGraphType(int graphType) {
        if(BuildConfig.DEBUG){
            if(graphType > 10){
                throw new InvalidParameterException();
            }
        }
        this.mGraphType = graphType;
    }

    public int getGraphFormat(){
        return mGraphFormat;
    }


    public void setGraphFormat(int format){
        if(BuildConfig.DEBUG){
            if(format < 51 || format > 54){
                throw new InvalidParameterException();
            }
        }
        this.mGraphFormat = format;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int icon) {
        if(BuildConfig.DEBUG){
            if(icon < 100 || icon > 999){
                throw new InvalidParameterException();
            }
        }
        this.mIcon = icon;
    }

    public String getIconText() {
        return mIconText;
    }

    public void setIconText(String mIconText) {
        this.mIconText = mIconText;
    }

    public int getColorBase() {
        return mColorBase;
    }

    public void setColorBase(int colorBase) {
        if(BuildConfig.DEBUG){
            if(colorBase < 0 || colorBase > 360){
                throw new InvalidParameterException();
            }
        }
        this.mColorBase = colorBase;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tracker)) return false;
        if (this == o) return true;
        Tracker other = (Tracker) o;
        return
                this.getLastUpdate() == other.getLastUpdate() && this.getId().equals(other.getId()) && this.getScoreSoFar() == other.getScoreSoFar();
    }



}
