package me.sbozhko.test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Scheduler;
import me.sbozhko.test.actor.DataProcessingActor;
import me.sbozhko.test.actor.DbActor;
import me.sbozhko.test.entity.DataEntity;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Bootstrap {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create();
        ActorRef dbActor = system.actorOf(Props.create(DbActor.class), "dbActor");
        final ActorRef endpointActor = system.actorOf(Props.create(DataProcessingActor.class, dbActor), "dataProcessingActor");
        ExecutionContextExecutor dispatcher = system.dispatcher();
        Scheduler scheduler = system.scheduler();
        FiniteDuration delay = Duration.create(1000, TimeUnit.MILLISECONDS);
        FiniteDuration interval = Duration.create(50, TimeUnit.MILLISECONDS);
        final Random random = new Random(System.currentTimeMillis());
        Runnable task = new Runnable() {
            @Override
            public void run() {
                DataEntity msg = new DataEntity((long) random.nextInt(10000), System.currentTimeMillis());
                endpointActor.tell(msg, ActorRef.noSender());
            }
        };
        scheduler.schedule(delay, interval, task, dispatcher);
    }
}
