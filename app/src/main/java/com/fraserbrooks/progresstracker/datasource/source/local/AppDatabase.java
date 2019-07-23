package com.fraserbrooks.progresstracker.datasource.source.local;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import com.fraserbrooks.progresstracker.BuildConfig;
import com.fraserbrooks.progresstracker.trackers.domain.model.ScoreEntry;
import com.fraserbrooks.progresstracker.targets.domain.model.Target;
import com.fraserbrooks.progresstracker.trackers.domain.model.Tracker;
import com.fraserbrooks.progresstracker.settings.domain.model.UserSetting;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import static java.util.Arrays.asList;


/**
 * Created by Fraser on 07/04/2018.
 *
 * The Room Database that contains the Task table.
 */
@Database(entities = {Tracker.class, Target.class, ScoreEntry.class, UserSetting.class}, version = 7)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract TrackersDao trackersDao();

    public abstract TargetsDao targetsDao();

    abstract ScoreEntryDao entriesDao();

    public abstract UserSettingsDao userSettingsDao();

    private static final Object sLock = new Object();

    public static AppDatabase getInstance(Context context, AppExecutors executors){
        synchronized (sLock){
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "ProgressTracker.db")
                        .addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                if(BuildConfig.DEBUG){
                                    executors.diskIO().execute(() ->
                                            addExampleData(getInstance(context, executors)));
                                }
                            }
                        })
                        .fallbackToDestructiveMigration()
                        .build();
            }
            return INSTANCE;
        }
    }

    private static void addExampleData(AppDatabase db){
        TrackersDao trackersDao = db.trackersDao();
        TargetsDao targetsDao   = db.targetsDao();
        ScoreEntryDao entriesDao = db.entriesDao();
        //UserSettingsDao settingsDao = db.userSettingsDao();

        Tracker tracker1 = Tracker.TimeLevelUpTracker("Guitar Practice", 250*60,
                Tracker.ICON_LEVEL, "GP", 1);

        Tracker tracker2 = Tracker.BooleanTracker("Up by 8am",  "days",
                Tracker.GRAPH_TYPE_DAY, Tracker.GRAPH_FORMAT_ONE,
                Tracker.ICON_STUDY, "8", 0, 2);

        Tracker tracker3 = Tracker.BooleanTracker("Weekly Ting", "weeks",
                Tracker.GRAPH_TYPE_WEEK, Tracker.GRAPH_FORMAT_ONE,
                Tracker.ICON_STUDY, "8", 265, 3);

        Tracker tracker4 = Tracker.NonTimeGraphTracker("Maths Revision",
                "past papers", Tracker.GRAPH_TYPE_WEEK, Tracker.GRAPH_FORMAT_ONE,
                Tracker.ICON_BOOK, "MR", 120, 4);

        Tracker tracker5 = Tracker.TimeGraphTracker("Java",
                Tracker.GRAPH_TYPE_WEEK, Tracker.GRAPH_FORMAT_ONE,
                Tracker.ICON_HEART, "JV", 240, 5);

        Tracker tracker6 = Tracker.NonTimeGraphTracker("OCaml Textbook",
                "chapters",Tracker.GRAPH_TYPE_WEEK, Tracker.GRAPH_FORMAT_ONE,
                Tracker.ICON_BOOK, "OC", 295, 6);

        Tracker tracker7 = Tracker.TimeLevelUpTracker("Android App", 250*60,
                Tracker.ICON_HEART, "AA", 7);

        Tracker tracker8 = Tracker.TimeLevelUpTracker("Exercise", 250*60,
                Tracker.ICON_LEVEL, "EX", 8);

        Tracker tracker9 = Tracker.TimeLevelUpTracker("Jiu Jitsu", 100*60, Tracker.ICON_HEART,
                "JJ", 9);

        Tracker tracker10 = Tracker.TimeGraphTracker("JavaScript",
                Tracker.GRAPH_TYPE_DAY, Tracker.GRAPH_FORMAT_ONE,
                Tracker.ICON_LEVEL, "JV", 190, 10);

        Tracker tracker11 = Tracker.TimeLevelUpTracker("ADooDa", 250*60,
                Tracker.ICON_LEVEL, "DD", 11);

        Tracker tracker12 = Tracker.TimeLevelUpTracker("ADooDa2", 30*60,
                Tracker.ICON_LEVEL, "01", 12);


        Tracker tracker13 = Tracker.TimeLevelUpTracker("ADooDa3", 30*60,
                Tracker.ICON_LEVEL, "02", 13);


        Tracker tracker14 = Tracker.TimeLevelUpTracker("ADooDa4", 30*60,
                Tracker.ICON_LEVEL, "03", 14);

        Tracker tracker15 = Tracker.TimeLevelUpTracker("ADooDa5", 30*60,
                Tracker.ICON_LEVEL, "04", 15);


        Tracker tracker16 = Tracker.TimeLevelUpTracker("ADooDa6", 30*60,
                Tracker.ICON_LEVEL, "05", 16);

        Tracker tracker17 = Tracker.TimeLevelUpTracker("ADooDa7", 30*60,
                Tracker.ICON_LEVEL, "06", 17);

        Tracker tracker18 = Tracker.TimeLevelUpTracker("ADooDa8", 30*60,
                Tracker.ICON_LEVEL, "07", 18);

        Tracker tracker19 = Tracker.TimeLevelUpTracker("ADooDa9", 30*60,
                Tracker.ICON_LEVEL, "08", 19);

        Tracker tracker20 = Tracker.TimeLevelUpTracker("ADooDa10", 30*60,
                Tracker.ICON_LEVEL, "09", 20);

        Tracker tracker21 = Tracker.TimeLevelUpTracker("ADooDa11", 30*60,
                Tracker.ICON_LEVEL, "10", 21);

        Tracker tracker22 = Tracker.TimeLevelUpTracker("ADooDa12", 250*60,
                Tracker.ICON_LEVEL, "11", 22);


        Tracker tracker23 = Tracker.BooleanTracker("ADooDa13","xoxo",
                Tracker.GRAPH_TYPE_DAY, Tracker.GRAPH_FORMAT_ONE,
                Tracker.ICON_STUDY, "12", 330, 23);

        Tracker tracker24 = Tracker.TimeLevelUpTracker("ADooDa14", 30*60,
                Tracker.ICON_LEVEL, "10", 24);

        Tracker tracker25 = Tracker.TimeLevelUpTracker("ADooDa15", 50*60,
                Tracker.ICON_HEART, "11", 25);

        Tracker tracker26 = Tracker.BooleanTracker("ADooDa16","xoxo",
                Tracker.GRAPH_TYPE_DAY, Tracker.GRAPH_FORMAT_ONE,
                Tracker.ICON_LEVEL, "12", 58, 26);


        Tracker tracker27 = Tracker.TimeLevelUpTracker("ADooDa17", 30*60,
                Tracker.ICON_LEVEL, "09", 27);

        Tracker tracker28 = Tracker.BooleanTracker("ADooDa18","xoxo",
                Tracker.GRAPH_TYPE_DAY, Tracker.GRAPH_FORMAT_ONE,
                Tracker.ICON_HEART, "12", 113, 28);

        Tracker tracker29 = Tracker.TimeLevelUpTracker("ADooDa19", 30*60,
                Tracker.ICON_LEVEL, "10", 29);

        Tracker tracker30 = Tracker.TimeGraphTracker("ADooDa20",
                Tracker.GRAPH_TYPE_DAY, Tracker.GRAPH_FORMAT_ONE,
                Tracker.ICON_STUDY, "AD", 350, 30);

        Tracker tracker31 = Tracker.TimeLevelUpTracker("ADooDa21", 25*60,
                Tracker.ICON_LEVEL, "11", 31);

        ArrayList<Tracker> trackers = new ArrayList<>(asList(tracker1, tracker2, tracker3, tracker4,
                tracker5, tracker6, tracker7, tracker8, tracker9, tracker10, tracker11
                ,tracker12, tracker13, tracker14, tracker15, tracker16, tracker17, tracker18
                ,tracker19, tracker20, tracker21, tracker22, tracker23, tracker24, tracker25
                ,tracker26, tracker27, tracker28, tracker29, tracker30, tracker31));

        trackersDao.insertItems(trackers);

        Target target1 = new Target(tracker1.getId(), 2*60, Target.EVERY_DAY, 1);
        Target target2 = new Target(tracker2.getId(), 200*60, Target.EVERY_YEAR, 2);
        Target target3 = new Target(tracker3.getId(), 2, Target.EVERY_WEEK, 3);
        Target target4 = new Target(tracker4.getId(), 60, Target.EVERY_DAY,4 );
        Target target5 = new Target(tracker5.getId(), 10*60, Target.EVERY_WEEK, 5);
        Target target6 = new Target(tracker1.getId(), 1000*60, Target.EVERY_YEAR, 6);
        Target target7 = new Target(tracker6.getId(), 2*60, Target.EVERY_WEEK,7 );
        Target target8 = new Target(tracker7.getId(), 30, Target.EVERY_DAY, 8);

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.YEAR, -3);

        target1.setStartDate(startDate);
        target2.setStartDate(startDate);
        target3.setStartDate(startDate);
        target4.setStartDate(startDate);
        target5.setStartDate(startDate);
        target6.setStartDate(startDate);
        target7.setStartDate(startDate);
        target8.setStartDate(startDate);

        Random rand = new Random();

        ArrayList<ScoreEntry> entries = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        for(Tracker t : trackers){
            for(int i = 0; i < 150; i++){
                cal.add(Calendar.DAY_OF_YEAR, -i);

                trackersDao.incrementOrAddTrackerScore(t.getId(), rand.nextInt(181), cal);

                cal.add(Calendar.DAY_OF_YEAR, i);
            }
        }

        targetsDao.insertItems(new ArrayList<>(asList(target1, target2, target3, target4, target5,
                target6, target7, target8)));

        entriesDao.insertItems(entries);

    }

}
