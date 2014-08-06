package me.sbozhko.test.actor;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.UntypedActor;
import me.sbozhko.test.entity.DataEntity;

public class StatisticsCounterActor extends UntypedActor {
    private Long maxValue;
    private Long minValue;
    private Long accumulator = 0l;
    private long nOfDataEntities = 0l;
    private final ActorRef statisticsAggregatorActor;

    public StatisticsCounterActor(ActorRef statisticsAggregatorActor) {
        this.statisticsAggregatorActor = statisticsAggregatorActor;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (Command.INTERVAL_FINISHED.equals(o)) {
            StatisticsAggregatorActor.Aggregate aggregateMsg = new StatisticsAggregatorActor.Aggregate(maxValue, minValue, accumulator, nOfDataEntities);
            statisticsAggregatorActor.tell(aggregateMsg, getSelf());
            getSelf().tell(PoisonPill.getInstance(), ActorRef.noSender());
        } else if (o instanceof DataEntity) {
            DataEntity dataEntity = (DataEntity) o;
            if (nOfDataEntities == 0) {
                maxValue = dataEntity.getValue();
                minValue = dataEntity.getValue();
            } else {
                if (maxValue < dataEntity.getValue()) {
                    maxValue = dataEntity.getValue();
                }
                if (minValue > dataEntity.getValue()) {
                    minValue = dataEntity.getValue();
                }
            }
            accumulator += dataEntity.getValue();
            nOfDataEntities++;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static enum Command {
        INTERVAL_FINISHED
    }
}
