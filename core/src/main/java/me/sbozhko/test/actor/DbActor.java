package me.sbozhko.test.actor;

import akka.actor.UntypedActor;

import java.util.Date;

public class DbActor extends UntypedActor {
    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof StoreStatistics) {
            // TODO: store statistics
            System.out.println(new Date() + " " + getSelf() + " " + o);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static class StoreStatistics {
        private final Long maxValue;
        private final Long minValue;
        private final double averageValue;

        public StoreStatistics(Long maxValue, Long minValue, double averageValue) {
            this.maxValue = maxValue;
            this.minValue = minValue;
            this.averageValue = averageValue;
        }

        @Override
        public String toString() {
            return "StoreStatistics{" +
                    "maxValue=" + maxValue +
                    ", minValue=" + minValue +
                    ", averageValue=" + averageValue +
                    '}';
        }
    }
}
