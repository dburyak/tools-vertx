package com.dburyak.vertx.test;

import com.dburyak.vertx.core.DiVerticle;
import io.micronaut.context.annotation.Bean;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.vertx.rxjava3.core.eventbus.EventBus;
import io.vertx.rxjava3.core.eventbus.Message;
import io.vertx.rxjava3.core.eventbus.MessageConsumer;
import jakarta.inject.Inject;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Bean
@Slf4j
public class HelloVerticle2 extends DiVerticle {
    @Setter(onMethod_ = {@Inject})
    private SampleEventLoopBean sampleEventLoopBean;

    @Setter(onMethod_ = {@Inject})
    private SampleVerticleBean sampleVerticleBean;

    @Setter(onMethod_ = {@Inject})
    private EventBus eventBus;

    private Disposable ticker;
    private MessageConsumer echoMsgConsumer;

    @Override
    public Completable rxStart() {
        return Completable.fromRunnable(() -> {
            log.info("hello from verticle 2: instance={}, elBean={}, vBean={}",
                    this, sampleEventLoopBean, sampleVerticleBean);
            ticker = Observable.interval(1, TimeUnit.SECONDS)
                    .doOnNext(tick -> {
                        log.info("tick verticle 2: {}", tick);
                        sampleEventLoopBean.hello();
                        sampleVerticleBean.hello();
                    })
                    .subscribe();
            echoMsgConsumer = eventBus.consumer("com.dburyak.vertx.test.HelloVerticle2", this::echo);
            new Thread(() -> {
                var bean = appCtx.getBean(SampleThreadLocalBean.class);
                log.info("thread local bean injected: bean={}", bean);
            }).start();
        });
    }

    @Override
    public Completable rxStop() {
        return Completable.fromRunnable(() -> {
            log.info("stop verticle 2: instance={}, elBean={}, vBean={}",
                    this, sampleEventLoopBean, sampleVerticleBean);
            ticker.dispose();
        });
    }

    private void echo(Message<Object> msg) {
        log.info("got echo message: {}", msg.body());
    }
}
