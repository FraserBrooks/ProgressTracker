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

/**
 * Use cases are the entry points to the domain layer.
 *
 * @param <Request> the request type
 * @param <Response> the response type
 */
public abstract class UseCase<Request extends UseCase.RequestValues,
        Response extends UseCase.ResponseValue, ErrorCode extends Enum> {

    private Request mRequestValues;

    private UseCaseCallback<Response, ErrorCode> mUseCaseCallback;

    void setRequestValues(Request requestValues) {
        mRequestValues = requestValues;
    }

    public Request getRequestValues() {
        return mRequestValues;
    }

    protected UseCaseCallback<Response, ErrorCode> getUseCaseCallback() {
        return mUseCaseCallback;
    }

    void setUseCaseCallback(UseCaseCallback<Response, ErrorCode> useCaseCallback) {
        mUseCaseCallback = useCaseCallback;
    }

    void run() {
       executeUseCase(mRequestValues);
    }

    protected abstract void executeUseCase(Request requestValues);

    /**
     * Data passed to a request.
     */
    public interface RequestValues {
    }

    /**
     * Data received from a request.
     */
    public interface ResponseValue {
    }

    public interface UseCaseCallback<R, E> {
        void onSuccess(R response);
        void onError(E errorCode);
    }
}
