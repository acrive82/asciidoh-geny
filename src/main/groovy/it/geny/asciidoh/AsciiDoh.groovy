package it.geny.asciidoh

import io.vertx.groovy.core.Vertx

/**
 * Created by luigi on 29/11/15.
 */
public class AsciiDoh {

    static void main(String[] args) {

        def vertx = Vertx.vertx()
//        vertx.deployVerticle("groovy:it.geny.asciidoh.verticles.SampleVerticle")
        vertx.deployVerticle("groovy:it.geny.asciidoh.verticles.AsciiDocVerticle")
    }
}
