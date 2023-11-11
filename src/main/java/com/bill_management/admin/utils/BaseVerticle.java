package com.bill_management.admin.utils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(BaseVerticle.class);

  protected static void deployVerticle(String verticleName, int instances, boolean worker, Vertx vertx) {

    vertx.exceptionHandler(err -> {
      LOG.error("Error: {}  when deploying verticle {}", err, verticleName);
    });

    vertx.deployVerticle(verticleName, new DeploymentOptions()
        .setInstances(instances)
        .setWorker(worker),
      stringAsyncResult -> {
        if (stringAsyncResult.failed()) {
          LOG.error("Error to deploy {} cause: {}", verticleName, stringAsyncResult.cause());
          return;
        }
        LOG.info("Verticle {} Deployed successfully", verticleName);
      });
  }

}
