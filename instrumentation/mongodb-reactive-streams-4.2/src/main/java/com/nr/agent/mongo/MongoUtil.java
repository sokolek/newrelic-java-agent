/*
 *
 *  * Copyright 2021 New Relic Corporation. All rights reserved.
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.nr.agent.mongo;

import com.mongodb.internal.async.SingleResultCallback;
import com.newrelic.agent.bridge.datastore.DatastoreVendor;
import com.newrelic.api.agent.DatastoreParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;

import java.util.logging.Level;

public class MongoUtil {

    public static final String OP_FIND = "find";
    public static final String OP_INSERT = "insert";
    public static final String OP_UPDATE = "update";
    public static final String OP_AGGREGATE = "aggregate";
    public static final String OP_REMOVE = "remove";
    public static final String OP_PARALLEL_SCAN = "parallelCollectionScan";
    public static final String OP_CREATE_INDEX = "createIndex";
    public static final String OP_CREATE_INDEXES = "createIndexes";
    public static final String OP_CREATE_LIST_INDEXES = "createListIndexes";
    public static final String OP_CREATE_VIEW = "createView";
    public static final String OP_CREATE_COLLECTION = "createCollection";

    public static final String OP_RENAME_COLLECTION = "renameCollection";
    public static final String OP_FIND_AND_UPDATE = "findAndUpdate";
    public static final String OP_FIND_ONE_AND_UPDATE = "findOneAndUpdate";

    public static final String OP_FIND_AND_REPLACE = "findAndReplace";
    public static final String OP_FIND_ONE_AND_REPLACE = "findOneAndReplace";

    public static final String OP_FIND_AND_DELETE = "findAndDelete";
    public static final String OP_FIND_ONE_AND_DELETE = "findOneAndDelete";

    public static final String OP_DROP_INDEX = "dropIndex";
    public static final String OP_DROP_INDEXES = "dropIndexes";
    public static final String OP_DROP_COLLECTION = "drop";
    public static final String OP_DROP_DATABASE = "dropDatabase";
    public static final String OP_DISTINCT = "distinct";
    public static final String OP_COUNT = "count";
    public static final String OP_MAP_REDUCE = "mapReduce";
    public static final String OP_REPLACE = "replace";
    public static final String OP_REPLACE_ONE = "replaceOne";
    public static final String OP_LIST_INDEXES = "listIndexes";
    public static final String OP_LIST_DATABASES = "listDatabases";
    public static final String OP_LIST_COLLECTIONS = "listCollections";
    public static final String OP_BULK_WRITE = "bulkWrite";
    public static final String OP_INSERT_ONE = "insertOne";
    public static final String OP_INSERT_MANY = "insertMany";
    public static final String OP_UPDATE_MANY = "updateMany";
    public static final String OP_UPDATE_ONE = "updateOne";

    public static final String OP_GET_MORE = "getMore";
    public static final String OP_GROUP = "group";
    public static final String UNKNOWN = "Unknown";

    /**
    * "delete" commands are different from {@link DBCollection#remove}.
    */
    public static final String OP_DELETE = "delete";
    public static final String OP_DELETE_ONE = "deleteOne";
    public static final String OP_DELETE_MANY = "deleteMany";

    public static final String REACTIVE_MONGO_COLLECTION = "MongoCollection";
    public static final String CUSTOM = "Custom";

    /**
     * What to use when you can't get the operation.
     */
    public static final String DEFAULT_OPERATION = "other";

    /**
     * What to use when you can't get the collection name.
     */
    public static final String DEFAULT_COLLECTION = "other";

    public static final String OP_DEFAULT = "other";

    public static <T> SingleResultCallback<T> instrumentSingleResultCallback(SingleResultCallback<T> callback, String collectionName,
            String operationName, String databaseName) {
        if (callback instanceof NRCallbackWrapper) {
            return callback;
        }

        NRCallbackWrapper<T> wrapper = new NRCallbackWrapper<T>(callback);
        wrapper.params = DatastoreParameters
                .product(DatastoreVendor.MongoDB.name())
                .collection(collectionName)
                .operation(operationName)
                .noInstance()
                .databaseName(databaseName)
                .build();

        wrapper.token = NewRelic.getAgent().getTransaction().getToken();
        wrapper.segment = NewRelic.getAgent().getTransaction().startSegment(operationName);
        return wrapper;
    }

    public static String getOperation(String classname) {
        if (classname.equalsIgnoreCase("MixedBulkWriteOperation")) {
            return OP_INSERT;
        }
        if (classname.equalsIgnoreCase("FindOperation")) {
            return OP_FIND;
        }
        if (classname.equalsIgnoreCase("DeleteOperation")) {
            return OP_DELETE;
        }
        if (classname.equalsIgnoreCase("UpdateOperation")) {
            return OP_UPDATE;
        }
        if (classname.equalsIgnoreCase("AggregateOperation")) {
            return OP_AGGREGATE;
        }
        if (classname.equalsIgnoreCase("CountOperation")) {
            return OP_COUNT;
        }
        NewRelic.getAgent().getLogger().log(Level.FINE, "Did not find operation name for {0}", classname);
        return DEFAULT_OPERATION;
    }

}
