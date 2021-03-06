/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.replication.it;

import org.apache.sling.replication.communication.ReplicationActionType;
import org.apache.sling.replication.communication.ReplicationHeader;
import org.junit.Ignore;
import org.junit.Test;

import static org.apache.sling.replication.it.ReplicationUtils.*;

/**
 * Integration test for commands on {@link org.apache.sling.replication.agent.ReplicationAgent}s
 */
public class ReplicationAgentCommandsIntegrationTest extends ReplicationIntegrationTestBase {

    @Test
    public void testAddCommand() throws Exception {
        String agentUrl= agentUrl("publish");

        replicate(author, agentUrl, ReplicationActionType.ADD, "dummy");
    }

    @Test
    @Ignore
    public void testPollCommand() throws Exception {
        String agentUrl = agentUrl("publish");
        replicate(author, agentUrl, ReplicationActionType.POLL);
    }

    @Test
    public void testDeleteCommand() throws Exception {
        String agentUrl= agentUrl("publish");

        replicate(author, agentUrl, ReplicationActionType.DELETE, "dummy");
    }

}