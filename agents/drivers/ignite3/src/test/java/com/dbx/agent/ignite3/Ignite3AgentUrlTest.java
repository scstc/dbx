package com.dbx.agent.ignite3;

import com.dbx.agent.ConnectParams;
import java.sql.Driver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class Ignite3AgentUrlTest {

    @Test
    void buildUrlUsesExplicitConnectionString() {
        ConnectParams params = new ConnectParams(
            "127.0.0.1", 10800, "PUBLIC", "", "", "",
            "jdbc:ignite:thin://10.0.0.1:10800/PUBLIC?connectionTimeout=5000", false
        );

        Assertions.assertEquals(
            "jdbc:ignite:thin://10.0.0.1:10800/PUBLIC?connectionTimeout=5000",
            Ignite3Agent.buildUrl(params)
        );
    }

    @Test
    void buildUrlWithoutDatabaseOmitsSchemaSegment() {
        ConnectParams params = new ConnectParams("127.0.0.1", 10800, "", "", "", "", "", false);

        Assertions.assertEquals("jdbc:ignite:thin://127.0.0.1:10800", Ignite3Agent.buildUrl(params));
    }

    @Test
    void buildUrlWithoutUrlParamsStaysUnchanged() {
        ConnectParams params = new ConnectParams("127.0.0.1", 10800, "PUBLIC", "", "", null, "", false);

        Assertions.assertEquals("jdbc:ignite:thin://127.0.0.1:10800/PUBLIC", Ignite3Agent.buildUrl(params));
    }

    @Test
    void buildUrlAppendsAdvancedUrlParamsAfterSchema() {
        ConnectParams params = new ConnectParams(
            "127.0.0.1", 10800, "PUBLIC", "", "",
            "connectionTimeout=5000&queryTimeout=30", "", false
        );

        Assertions.assertEquals(
            "jdbc:ignite:thin://127.0.0.1:10800/PUBLIC?connectionTimeout=5000&queryTimeout=30",
            Ignite3Agent.buildUrl(params)
        );
    }

        // Ignite 3 treats ';' after '?' as part of the value (validated against a live 3.1.0 node),
        // so '&' is the separator users should type. This asserts verbatim passthrough, not driver support.
    @Test
    void buildUrlPassesThroughUserParamStringVerbatim() {
        ConnectParams params = new ConnectParams(
            "127.0.0.1", 10800, "PUBLIC", "", "",
            "sslEnabled=true;trustStorePath=/etc/ignite/trust.jks", "", false
        );

        Assertions.assertEquals(
            "jdbc:ignite:thin://127.0.0.1:10800/PUBLIC?sslEnabled=true;trustStorePath=/etc/ignite/trust.jks",
            Ignite3Agent.buildUrl(params)
        );
    }

    @Test
    void buildUrlStripsLeadingParamSeparators() {
        ConnectParams params = new ConnectParams("127.0.0.1", 10800, "", "", "", "?sslEnabled=true", "", false);

        Assertions.assertEquals("jdbc:ignite:thin://127.0.0.1:10800?sslEnabled=true", Ignite3Agent.buildUrl(params));
    }

    @Test
    void builtUrlsAreAcceptedByTheIgniteDriver() throws Exception {
        Driver driver = new org.apache.ignite.jdbc.IgniteJdbcDriver();

        Assertions.assertTrue(driver.acceptsURL(
            Ignite3Agent.buildUrl(new ConnectParams("127.0.0.1", 10800, "PUBLIC", "", "", "", "", false))
        ));
        Assertions.assertTrue(driver.acceptsURL(
            Ignite3Agent.buildUrl(
                new ConnectParams("127.0.0.1", 10800, "PUBLIC", "", "", "sslEnabled=false&connectionTimeout=5000", "", false)
            )
        ));
        Assertions.assertTrue(driver.acceptsURL(
            Ignite3Agent.buildUrl(
                new ConnectParams("127.0.0.1", 10800, "PUBLIC", "", "", "username=admin&password=secret", "", false)
            )
        ));
    }
}
