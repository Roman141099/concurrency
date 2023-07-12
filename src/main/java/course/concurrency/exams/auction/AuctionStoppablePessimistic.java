package course.concurrency.exams.auction;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private Notifier notifier;
    private volatile boolean isAlive = true;
    private final Object lock = new Object();

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(-1L, -1L, -1L);

    public boolean propose(Bid bid) {
        if (isAlive && bid.getPrice() > latestBid.getPrice()) {
            synchronized (lock) {
                if (isAlive && bid.getPrice() > latestBid.getPrice()) {
                    notifier.sendOutdatedMessage(latestBid);
                    latestBid = bid;
                    return true;
                }
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    public Bid stopAuction() {
        synchronized (lock) {
            isAlive = false;
            return latestBid;
        }
    }
}
