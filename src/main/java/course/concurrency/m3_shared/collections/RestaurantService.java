package course.concurrency.m3_shared.collections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RestaurantService {

    private Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private ConcurrentHashMap<String, LongAdder> stat = new ConcurrentHashMap<>();

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    public static void main(String[] args) throws InterruptedException {
        Tester tester = new Tester();

        ExecutorService exec = Executors.newFixedThreadPool(100);

        IntStream.range(0, 100)
                .forEach(value -> exec.submit(() -> tester.go()));

        Thread.sleep(2000);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 123;
    }

    interface FuncExecutor {

        void execute();

    }

    static class Tester {

        FuncExecutor func = new StandardFuncExecutor();
        AtomicInteger callCount = new AtomicInteger(0);

        public void go() {
            if(callCount.incrementAndGet() > 3){
                throw new RuntimeException();
            }
            func.execute();
        }
    }



    static class StandardFuncExecutor implements FuncExecutor {

        @Override
        public void execute() {
            System.out.println("Hello");
        }
    }

    static class ExceptionallyFuncExecutor implements FuncExecutor {

        @Override
        public void execute() {
            throw new RuntimeException();
        }
    }


    public void addToStat(String restaurantName) {
//        stat.merge(restaurantName, 1L, (f, s) -> f + s);
        stat.computeIfAbsent(restaurantName, val -> new LongAdder()).add(1L);
    }

    public Set<String> printStat() {
        return stat.entrySet().stream()
                .map(entry -> entry.getKey() + " - " + entry.getValue().sum())
                .collect(Collectors.toSet());
    }
}
