/*
 * Copyright 2016, The Android Open Source Project
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

package com.fraserbrooks.progresstracker;
import android.util.Log;

import com.fraserbrooks.progresstracker.util.EspressoIdlingResource;


/**
 * Runs {@link UseCase}s using a {@link UseCaseScheduler}.
 */
public class UseCaseHandler {

    private static final String TAG = "UseCaseHandler";
    
    private static UseCaseHandler INSTANCE;

    private final UseCaseScheduler mUseCaseScheduler;

    private UseCaseHandler(UseCaseScheduler useCaseScheduler) {
        mUseCaseScheduler = useCaseScheduler;
    }

    public <Request extends UseCase.RequestValues,
            Response extends UseCase.ResponseValue,
            ErrMsg extends Enum> void execute(
            final UseCase<Request, Response, ErrMsg>   useCase,
                                             Request  values,
            UseCase.UseCaseCallback<Response, ErrMsg> callback) {
        useCase.setRequestValues(values);
        useCase.setUseCaseCallback(new UiCallbackWrapper<>(callback, this));

        // The network request might be handled in a different thread so make sure
        // Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mUseCaseScheduler.execute(() -> {

            useCase.run();
            // This callback may be called twice, once for the cache and once for loading
            // the data from the server API, so we check before decrementing, otherwise
            // it throws "Counter has been corrupted!" exception.
            if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                EspressoIdlingResource.decrement(); // Set app as idle.
            }
        });
    }

    private <Response extends UseCase.ResponseValue, ErrMsg extends Enum>
    void notifyResponse(final Response response,
                        final UseCase.UseCaseCallback<Response, ErrMsg> useCaseCallback) {
        mUseCaseScheduler.notifyResponse(response, useCaseCallback);
    }

    private <Response extends UseCase.ResponseValue, ErrMsg extends Enum>
    void notifyError(final ErrMsg error,
                     final UseCase.UseCaseCallback<Response, ErrMsg> useCaseCallback) {
        mUseCaseScheduler.onError(useCaseCallback, error);
    }

    private static final class UiCallbackWrapper
            <Response extends UseCase.ResponseValue, ErrMsg extends Enum>
            implements UseCase.UseCaseCallback<Response, ErrMsg> {

        private final UseCase.UseCaseCallback<Response, ErrMsg> mCallback;
        private final UseCaseHandler mUseCaseHandler;

        UiCallbackWrapper(UseCase.UseCaseCallback<Response, ErrMsg> callback,
                UseCaseHandler useCaseHandler) {
            mCallback = callback;
            mUseCaseHandler = useCaseHandler;
        }

        @Override
        public void onSuccess(Response response) {
            mUseCaseHandler.notifyResponse(response, mCallback);
        }

        @Override
        public void onError(ErrMsg e) {
            mUseCaseHandler.notifyError(e, mCallback);
        }
    }

    public static UseCaseHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UseCaseHandler(new UseCaseThreadPoolScheduler());
        }
        return INSTANCE;
    }
}
