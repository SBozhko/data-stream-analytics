package me.sbozhko.test.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import me.sbozhko.test.entity.DataEntity;

public class DataProcessingActor extends UntypedActor {
    public static final long STATISTICS_INTERVAL = 60000;
    private Long lastTimestamp = 0l;
    private ActorRef statisticsCounterActor;
    private final ActorRef dbActor;

    public DataProcessingActor(ActorRef dbActor) {
        this.dbActor = dbActor;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof DataEntity) {
            DataEntity dataEntity = (DataEntity) o;
            if (dataEntity.getTimestamp() - lastTimestamp >= STATISTICS_INTERVAL) {
                if (statisticsCounterActor != null) {
                    statisticsCounterActor.tell(StatisticsCounterActor.Command.INTERVAL_FINISHED, getSelf());
                }
                createStatiscicsCounterActor();
            } else {
                if (statisticsCounterActor == null) {
                    createStatiscicsCounterActor();
                }
            }
            statisticsCounterActor.tell(dataEntity, getSelf());
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void createStatiscicsCounterActor() {
        statisticsCounterActor = getContext().actorOf(Props.create(StatisticsCounterActor.class, dbActor));
    }

}
