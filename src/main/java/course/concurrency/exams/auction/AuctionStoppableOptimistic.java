package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private AtomicMarkableReference<Bid> latestBid = new AtomicMarkableReference<>(new Bid(-1L, -1L, -1L), true);

    public boolean propose(Bid bid) {
        Bid toCompare;
        do {
            toCompare = latestBid.getReference();
            if (latestBid.isMarked() || bid.getPrice() <= latestBid.getReference().getPrice()) {
                return false;
            }
        } while (!latestBid.compareAndSet(toCompare, bid, false, false));
        notifier.sendOutdatedMessage(bid);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        if(latestBid.isMarked()){
            return latestBid.getReference();
        }
        latestBid.attemptMark(latestBid.getReference(), true);
        return latestBid.getReference();
    }
}
