package me.sbozhko.test.actor;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.UntypedActor;

public class StatisticsAggregatorActor extends UntypedActor {
    private Long maxValue;
    private Long minValue;
    private Long accumulator = 0l;
    private long nOfDataEntities = 0l;
    private final ActorRef dbActor;
    private final int expectedNOfMessages;
    private int actualNOfMessages;

    public StatisticsAggregatorActor(ActorRef dbActor, int expectedNOfMessages) {
        this.dbActor = dbActor;
        this.expectedNOfMessages = expectedNOfMessages;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Aggregate) {
            Aggregate aggregateMsg = (Aggregate) o;
            if (actualNOfMessages == 0) {
                minValue = aggregateMsg.minValue;
                maxValue = aggregateMsg.maxValue;
            } else {
                if (maxValue < aggregateMsg.maxValue) {
                    maxValue = aggregateMsg.maxValue;
                }
                if (minValue > aggregateMsg.minValue) {
                    minValue = aggregateMsg.minValue;
                }
            }

            accumulator += aggregateMsg.accumulator;
            nOfDataEntities += aggregateMsg.nOfMessages;
            actualNOfMessages++;

            if (expectedNOfMessages == actualNOfMessages) {
                double averageValue = accumulator / nOfDataEntities;
                DbActor.StoreStatistics storeStatisticsЬып = new DbActor.StoreStatistics(maxValue, minValue, averageValue);
                dbActor.tell(storeStatisticsЬып, getSelf());
                getSelf().tell(PoisonPill.getInstance(), ActorRef.noSender());
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static class Aggregate {
        private final Long maxValue;
        private final Long minValue;
        private final Long accumulator;
        private final long nOfMessages;

        public Aggregate(Long maxValue, Long minValue, Long accumulator, long nOfMessages) {
            this.maxValue = maxValue;
            this.minValue = minValue;
            this.accumulator = accumulator;
            this.nOfMessages = nOfMessages;
        }
    }
}
