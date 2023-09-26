package course.concurrency.m3_shared.immutable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class OrderServiceImmutable extends OrderService {

    private final ConcurrentHashMap<Long, OrderImmutable> currentOrders = new ConcurrentHashMap<>();

    public long createOrder(List<Item> items) {
        OrderImmutable order = new OrderImmutable(items);
        currentOrders.put(order.getId(), order);
        return order.getId();
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        OrderImmutable paid = currentOrders.compute(orderId, (key, o) -> o.withPaymentInfo(paymentInfo));

        // main profit of immutable variables is locality
        if (paid.checkStatus()) {
            deliver(paid);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        OrderService orderService = new OrderServiceImmutable();

        long order = orderService.createOrder(List.of());

        ExecutorService exec = Executors.newFixedThreadPool(10);

        IntStream.range(0, 10).forEach(value -> exec.execute(() -> {
            orderService.updatePaymentInfo(order, new PaymentInfo());
            orderService.setPacked(order);
        }));

        Thread.sleep(2000);
    }

    public void setPacked(long orderId) {
        OrderImmutable packed = currentOrders.compute(orderId, (key, o) -> o.packed());

        if (packed.checkStatus()) {
            deliver(packed);
        }
    }

    private void deliver(OrderImmutable order) {
        /* ... */
        System.out.println(order.getId());
        currentOrders.compute(order.getId(), (key, o) -> o.withStatus(OrderImmutable.Status.DELIVERED));
    }

    public boolean isDelivered(long orderId) {
        return currentOrders.get(orderId).getStatus().equals(OrderImmutable.Status.DELIVERED);
    }
}
