/*
 *
 * Copyright 2015-2018 Vladimir Bukhtoyarov
 *
 *       Licensed under the Apache License, Version 2.0 (the "License");
 *       you may not use this file except in compliance with the License.
 *       You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package io.github.bucket4j.distributed.remote.commands;

import io.github.bucket4j.BucketState;
import io.github.bucket4j.distributed.remote.CommandResult;
import io.github.bucket4j.distributed.remote.MutableBucketEntry;
import io.github.bucket4j.distributed.remote.RemoteBucketState;
import io.github.bucket4j.distributed.remote.RemoteCommand;
import io.github.bucket4j.serialization.DeserializationAdapter;
import io.github.bucket4j.serialization.SerializationAdapter;
import io.github.bucket4j.serialization.SerializationHandle;
import io.github.bucket4j.util.ComparableByContent;

import java.io.IOException;

public class CreateSnapshotCommand implements RemoteCommand<BucketState>, ComparableByContent<CreateSnapshotCommand> {

    public static SerializationHandle<CreateSnapshotCommand> SERIALIZATION_HANDLE = new SerializationHandle<CreateSnapshotCommand>() {
        @Override
        public <S> CreateSnapshotCommand deserialize(DeserializationAdapter<S> adapter, S input) throws IOException {
            return new CreateSnapshotCommand();
        }

        @Override
        public <O> void serialize(SerializationAdapter<O> adapter, O output, CreateSnapshotCommand command) throws IOException {
            // do nothing
        }

        @Override
        public int getTypeId() {
            return 26;
        }

        @Override
        public Class<CreateSnapshotCommand> getSerializedType() {
            return CreateSnapshotCommand.class;
        }

    };

    @Override
    public CommandResult<BucketState> execute(MutableBucketEntry mutableEntry, long currentTimeNanos) {
        if (!mutableEntry.exists()) {
            return CommandResult.bucketNotFound();
        }

        RemoteBucketState state = mutableEntry.get();
        state.refillAllBandwidth(currentTimeNanos);
        return CommandResult.success(state.copyBucketState(), RemoteBucketState.SERIALIZATION_HANDLE);
    }

    @Override
    public SerializationHandle getSerializationHandle() {
        return SERIALIZATION_HANDLE;
    }

    @Override
    public boolean equalsByContent(CreateSnapshotCommand other) {
        return true;
    }

}
