package io.enotes.sdk.core;

import io.enotes.sdk.repository.base.Resource;

public interface Callback<T> {
    void onBack(Resource<T> resource);
}
