package com.dbx.agent.ignite;

import com.dbx.agent.DatabaseAgent;
import com.dbx.agent.test.JdbcFakeExecutionBehaviorTest;

class IgniteAgentTest extends JdbcFakeExecutionBehaviorTest {
    @Override
    protected DatabaseAgent createAgent() {
        return new IgniteAgent();
    }

    @Override
    protected String resultSetSql() {
        return "SELECT 1";
    }
}
