package com.dburyak.vertx.test;

import com.dburyak.vertx.core.VerticleDeploymentDescriptor;
import com.dburyak.vertx.core.VertxDiApp;
import io.vertx.core.DeploymentOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DeleteMeApp extends VertxDiApp {

    public static void main(String[] args) {
        log.info("starting ....");
        var app = new DeleteMeApp();
        app.start()
                .delay(10, TimeUnit.SECONDS)
                .andThen(app.stop())
                .blockingAwait();
    }

    @Override
    protected Collection<VerticleDeploymentDescriptor> verticlesDeploymentDescriptors() {
        return List.of(
                VerticleDeploymentDescriptor.builder()
                        .verticleClass(HelloVerticle1.class)
                        .build(),
                VerticleDeploymentDescriptor.builder()
                        .verticleClass(HelloVerticle2.class)
                        .deploymentOptions(new DeploymentOptions().setInstances(40))
                        .build()
        );
    }
}
