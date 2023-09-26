package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class PriceAggregator {

    public PriceAggregator() {
        System.out.println("Constructor " + Thread.currentThread());
    }

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        // place for your code
        return 0;
    }

    public static void main(String[] args) {

        System.out.println(t.get());
        System.out.println(i);
        System.out.println(t.get());
        System.out.println(t.get());
        System.out.println(t.get());
    }

    static int i = 0;

    static ThreadLocal<Integer> t = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return i++;
        }
    };

}