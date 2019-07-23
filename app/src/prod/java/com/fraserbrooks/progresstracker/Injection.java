package com.fraserbrooks.progresstracker;
/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import androidx.annotation.NonNull;

import com.fraserbrooks.progresstracker.datasource.TargetRepository;
import com.fraserbrooks.progresstracker.datasource.TrackerRepository;
import com.fraserbrooks.progresstracker.datasource.UserSettingsRepository;
import com.fraserbrooks.progresstracker.datasource.source.TrackerDataSource;
import com.fraserbrooks.progresstracker.datasource.source.local.AppDatabase;
import com.fraserbrooks.progresstracker.datasource.source.local.TargetsLocalDataSource;
import com.fraserbrooks.progresstracker.datasource.source.local.TrackersLocalDataSource;
import com.fraserbrooks.progresstracker.datasource.source.local.UserSettingsLocalDataSource;
import com.fraserbrooks.progresstracker.trackers.domain.filter.TrackerFilterFactory;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.ClearRange;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.DeleteTracker;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.GetTracker;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.GetTrackers;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.IncrementTracker;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.SaveTracker;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.StartStopTrackerTimer;
import com.fraserbrooks.progresstracker.trackers.domain.usecase.UpdateTracker;
import com.fraserbrooks.progresstracker.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Enables injection of production implementations for
 * {@link TrackerDataSource} at compile time.
 */
public class Injection {

    //  ---------------------------------------------------------------------------------
    //  Repositories ------------------------------------------------------------------

    public static TrackerRepository provideTrackerRepository(@NonNull Context context) {
        checkNotNull(context);
        AppExecutors executors = AppExecutors.getInstance();
        AppDatabase database = AppDatabase.getInstance(context, executors);
        return TrackerRepository.getInstance(TrackersLocalDataSource.getInstance(executors, database.trackersDao()),
                TrackersLocalDataSource.getInstance(executors, database.trackersDao()),
                executors);
    }

    public static TargetRepository provideTargetRepository(@NonNull Context context){
        checkNotNull(context);
        AppExecutors executors = AppExecutors.getInstance();
        AppDatabase database = AppDatabase.getInstance(context, executors);
        return TargetRepository.getInstance(
                TargetsLocalDataSource.getInstance(executors, database.targetsDao(), database.trackersDao()),
                TargetsLocalDataSource.getInstance(executors, database.targetsDao(), database.trackersDao()),
                executors);
    }

    public static UserSettingsRepository provideUserSettingsRepository(@NonNull Context context) {
        checkNotNull(context);
        AppExecutors executors = AppExecutors.getInstance();
        AppDatabase database = AppDatabase.getInstance(context, executors);
        return UserSettingsRepository.getInstance(UserSettingsLocalDataSource.getInstance(executors, database.userSettingsDao()),
                UserSettingsLocalDataSource.getInstance(executors, database.userSettingsDao()),
                executors);
    }

    //  --------------------------------------------------------------------------------
    //  Use Cases ---------------------------------------------------------------------


    public static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }

    public static GetTrackers provideGetTrackers(@NonNull Context context) {
        return new GetTrackers(provideTrackerRepository(context), new TrackerFilterFactory());
    }

    public static GetTracker provideGetTracker(@NonNull Context context){
        return new GetTracker(provideTrackerRepository(context));
    }

    public static ClearRange provideClearRange(@NonNull Context context) {
        return new ClearRange(provideTrackerRepository(context));
    }

    public static DeleteTracker provideDeleteTracker(@NonNull Context context) {
        return new DeleteTracker(provideTrackerRepository(context));
    }

    public static IncrementTracker provideIncrementTracker(@NonNull Context context) {
        return new IncrementTracker(provideTrackerRepository(context));
    }

    public static SaveTracker provideSaveTracker(@NonNull Context context) {
        return new SaveTracker(provideTrackerRepository(context));
    }

    public static StartStopTrackerTimer provideStartStopTrackerTimer(@NonNull Context context) {
        return new StartStopTrackerTimer(provideTrackerRepository(context));
    }

    public static UpdateTracker provideUpdateTracker(@NonNull Context context) {
        return new UpdateTracker(provideTrackerRepository(context));
    }



}