package me.sbozhko.test.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import me.sbozhko.test.entity.DataEntity;

public class StatisticsCounterActor extends UntypedActor {
    private Long maxValue;
    private Long minValue;
    private Long accumulator = 0l;
    private long nOfMessages = 0l;
    private final ActorRef dbActor;

    public StatisticsCounterActor(ActorRef dbActor) {
        this.dbActor = dbActor;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (Command.INTERVAL_FINISHED.equals(o)) {
            double averageValue = accumulator / nOfMessages;
            DbActor.StoreStatistics storeStatisticsMsg = new DbActor.StoreStatistics(maxValue, minValue, averageValue);
            dbActor.tell(storeStatisticsMsg, getSelf());
            // TODO: kill yourself
        } else if (o instanceof DataEntity) {
            DataEntity dataEntity = (DataEntity) o;
            if (maxValue < dataEntity.getValue()) {
                maxValue = dataEntity.getValue();
            }
            if (minValue > dataEntity.getValue()) {
                minValue = dataEntity.getValue();
            }
            accumulator += dataEntity.getValue();
            nOfMessages++;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static enum Command {
        INTERVAL_FINISHED
    }
}
