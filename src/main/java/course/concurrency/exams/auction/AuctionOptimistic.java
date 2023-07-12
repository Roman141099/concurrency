package course.concurrency.exams.auction;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private AtomicReference<Bid> latestBid = new AtomicReference<>(new Bid(-1L, -1L, -1L));

    public boolean propose(Bid bid) {
        Bid toCompare = latestBid.get();
        do {
            if(bid.getPrice() <= latestBid.get().getPrice()) {
                return false;
            }

        } while (!latestBid.compareAndSet(toCompare, bid));
        notifier.sendOutdatedMessage(bid);
        return true;
    }


    public Bid getLatestBid() {
        return latestBid.get();
    }
}
