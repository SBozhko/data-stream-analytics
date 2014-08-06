package me.sbozhko.test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import me.sbozhko.test.actor.DataProcessingActor;
import me.sbozhko.test.actor.DbActor;
import me.sbozhko.test.dao.StatisticsDao;
import me.sbozhko.test.entity.DataEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Bootstrap {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/context.xml");
        StatisticsDao statisticsDao = (StatisticsDao) context.getBean("mongoDbStatisticsDao");

        ActorSystem system = ActorSystem.create();
        ActorRef dbActor = system.actorOf(Props.create(DbActor.class, statisticsDao), "dbActor");
        final ActorRef endpointActor = system.actorOf(Props.create(DataProcessingActor.class, dbActor), "dataProcessingActor");

        FiniteDuration delay = Duration.create(1000, TimeUnit.MILLISECONDS);
        FiniteDuration interval = Duration.create(50, TimeUnit.MILLISECONDS);
        final Random random = new Random(System.currentTimeMillis());
        Runnable dataGenerationTask = new Runnable() {
            @Override
            public void run() {
                DataEntity msg = new DataEntity((long) random.nextInt(10000), System.currentTimeMillis());
                endpointActor.tell(msg, ActorRef.noSender());
            }
        };
        system.scheduler().schedule(delay, interval, dataGenerationTask, system.dispatcher());
    }
}
