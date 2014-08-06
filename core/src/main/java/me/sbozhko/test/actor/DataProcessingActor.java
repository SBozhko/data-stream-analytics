package me.sbozhko.test.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.BroadcastRoutingLogic;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import me.sbozhko.test.entity.DataEntity;

import java.util.ArrayList;
import java.util.List;

public class DataProcessingActor extends UntypedActor {
    public static final long STATISTICS_INTERVAL_MILLISECONDS = 60 * 1000;
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private Long lastTimestamp = 0l;
    private final ActorRef dbActor;
    private Router roundRobinRouter;

    public DataProcessingActor(ActorRef dbActor) {
        this.dbActor = dbActor;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof DataEntity) {
            DataEntity dataEntity = (DataEntity) o;
            if (lastTimestamp == 0) {
                lastTimestamp = dataEntity.getTimestamp();
            }
            if (dataEntity.getTimestamp() - lastTimestamp >= STATISTICS_INTERVAL_MILLISECONDS) {
                lastTimestamp = dataEntity.getTimestamp();
                if (roundRobinRouter != null) {
                    Router broadcastRouter = new Router(new BroadcastRoutingLogic(), roundRobinRouter.routees());
                    broadcastRouter.route(StatisticsCounterActor.Command.INTERVAL_FINISHED, getSelf());
                }
                createStatisticsCounterActor();
            } else {
                if (roundRobinRouter == null) {
                    createStatisticsCounterActor();
                }
            }
            roundRobinRouter.route(dataEntity, getSelf());
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void createStatisticsCounterActor() {
        ActorRef statisticsAggregatorActor = getContext().actorOf(Props.create(StatisticsAggregatorActor.class, dbActor, AVAILABLE_PROCESSORS));
        List<Routee> workers = new ArrayList<Routee>(AVAILABLE_PROCESSORS);
        for (int i = 0; i < AVAILABLE_PROCESSORS; i++) {
            ActorRef ref = getContext().actorOf(Props.create(StatisticsCounterActor.class, statisticsAggregatorActor));
            workers.add(new ActorRefRoutee(ref));
        }
        roundRobinRouter = new Router(new RoundRobinRoutingLogic(), workers);
    }

}
