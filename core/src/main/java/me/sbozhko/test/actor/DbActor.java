package me.sbozhko.test.actor;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import me.sbozhko.test.dao.StatisticsDao;
import me.sbozhko.test.entity.StatisticsEntity;

public class DbActor extends UntypedActor {
    private final StatisticsDao dao;
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public DbActor(StatisticsDao dao) {
        this.dao = dao;
    }

    @Override
    public void onReceive(Object o) {
        if (o instanceof StoreStatistics) {
            StoreStatistics storeStatisticsMsg = (StoreStatistics) o;
            StatisticsEntity statisticsEntity = new StatisticsEntity();
            statisticsEntity.setAvgValue(storeStatisticsMsg.averageValue);
            statisticsEntity.setMaxValue(storeStatisticsMsg.maxValue);
            statisticsEntity.setMinValue(storeStatisticsMsg.minValue);
            try {
                dao.save(statisticsEntity);
                log.info("successfully inserted into db: {}", statisticsEntity);
            } catch (Exception e) {
                // just log error and continue working; at present no sense to rethrow this exception to the next level
                log.error(e, "oops. something goes wrong");
            }
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
    }
}
