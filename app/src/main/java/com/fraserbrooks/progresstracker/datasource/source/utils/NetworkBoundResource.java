package com.fraserbrooks.progresstracker.datasource.source.utils;

/*
 * Copyright (C) 2017 The Android Open Source Project
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


import android.util.Log;

import com.fraserbrooks.progresstracker.util.AppExecutors;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

/**
 * A generic class that can provide a resource backed by both ROOM and the network.
 * <p>
 * You can read more about it in the <a href="https://developer.android.com/arch">Architecture
 * Guide</a>.
 * @param <ResultType>
 * @param <RequestType>
 */
public abstract class NetworkBoundResource<ResultType, RequestType> {
    
    private final String TAG = "NetworkBoundResource";
    
    private final AppExecutors appExecutors;

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();


    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        result.postValue(Resource.loading(null));
        LiveData<ResultType> dbSource = loadFromDb();

        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource);
            } else {
                result.addSource(dbSource, newData -> postValue(Resource.success(newData)));
            }
        });
    }

    private void postValue(Resource<ResultType> newValue) {
        Log.d(TAG, "asdff postValue: new value posted");
        if (!Objects.equals(result.getValue(), newValue)) {
            Log.d(TAG, "asdff postValue: new value is new");
            result.postValue(newValue);
        }else{
            Log.d(TAG, "qwert postValue: new value is same");

            Log.d(TAG, "qwert postValue: result.value = " + result.getValue());
            Log.d(TAG, "qwert postValue: new value    = " + newValue );

        }
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource, newData -> postValue(Resource.loading(newData)));
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            //noinspection ConstantConditions
            if (response.isSuccessful()) {
                appExecutors.diskIO().execute(() -> {
                    saveCallResult(processResponse(response));
                    appExecutors.mainThread().execute(() ->
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb(),
                                    newData -> postValue(Resource.success(newData)))
                    );
                });
            } else {
                onFetchFailed();
                result.addSource(dbSource,
                        newData -> postValue(Resource.error(response.errorMessage, newData)));
            }
        });
    }

    protected void onFetchFailed() {
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @WorkerThread
    protected RequestType processResponse(ApiResponse<RequestType> response) {
        return response.body;
    }

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);


    protected abstract boolean shouldFetch(@Nullable ResultType data);

    @NonNull
    protected abstract LiveData<ResultType> loadFromDb();


    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();


}