/*
 *
 *  * Copyright 2021 New Relic Corporation. All rights reserved.
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.nr.agent.mongo;

import com.mongodb.internal.async.SingleResultCallback;
import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.DatastoreParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class NRCallbackWrapper<T> implements SingleResultCallback<T> {

    private static final AtomicBoolean isNotTransformed = new AtomicBoolean(true);
    public Token token = null;
    public Segment segment = null;
    public DatastoreParameters params = null;
    private SingleResultCallback<T> delegate = null;

    public NRCallbackWrapper(SingleResultCallback<T> d) {
        delegate = d;
        if (isNotTransformed.get()) {
            AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
            isNotTransformed.set(false);
        }
    }

    @Override
    @Trace(async = true)
    public void onResult(T result, Throwable t) {
        NewRelic.getAgent().getLogger().log(Level.INFO, "----- onResult");

        // could be NoOpToken if a txn wasn't started
        if (token != null) {
            NewRelic.getAgent().getLogger().log(Level.INFO, "----- token link/expire");
            token.linkAndExpire();
            token = null;
        }
        // could be NoOpSegment if a txn wasn't started
        if (segment != null) {
            if (params != null) {
                NewRelic.getAgent().getLogger().log(Level.INFO, "----- report external");

                segment.reportAsExternal(params);
            }
            segment.end();
        } else if (params != null) {
            NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
            NewRelic.getAgent().getLogger().log(Level.INFO, "----- token link/expire 2");

        }
        delegate.onResult(result, t);
    }

}
