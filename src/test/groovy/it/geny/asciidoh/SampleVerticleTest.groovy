/**
 * Created by luigi on 29/11/15.
 */
package it.geny.asciidoh

import io.vertx.groovy.core.Vertx
import io.vertx.groovy.ext.unit.TestContext
import io.vertx.groovy.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by luigi on 29/11/15.
 */
@RunWith(VertxUnitRunner.class)
class SampleVerticleTest {

    Vertx vertx

    @Before
    def void setUp(TestContext context) {
        def async = context.async()
        vertx = Vertx.vertx()
        vertx.deployVerticle("groovy:it.geny.asciidoh.verticles.SampleVerticle")
    }

    @After
    public void tearDown(TestContext context) {
        def async = context.async()
        vertx.close(context.assertTrue(async.succeeded))
    }

    @Test
    public void testMyApplication(TestContext context) {

        def async = context.async()

        vertx.createHttpClient().getNow(8080, "localhost", "/", { response ->
            response.handler { body ->
                context.assertTrue(body.toString().contains("Spacca"))
                async.complete()
            }
        })

    }
}
