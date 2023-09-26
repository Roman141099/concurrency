package course.concurrency.m3_shared.immutable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class OrderService {

    private final Map<Long, Order> currentOrders = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(0L);

    private long nextId() {
        return nextId.getAndIncrement();
    }

    public long createOrder(List<Item> items) {
        long id = nextId();
        Order order = new Order(items, id);
        currentOrders.put(id, order);
        return id;
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        currentOrders.computeIfPresent(orderId, (id, order) -> {
            if (!isDelivered(id)) {
                Order payed = order.withPaymentInfo(paymentInfo);
                if (payed.checkStatus()) {
                    deliver(payed);
                } else {
                    return payed;
                }
            }
            return currentOrders.get(orderId);
        });
    }

    public void setPacked(long orderId) {
        currentOrders.computeIfPresent(orderId, (id, order) -> {
            if (!isDelivered(id)) {
                Order packed = order.packedOrder();
                if (packed.checkStatus()) {
                    deliver(packed);
                } else {
                    return packed;
                }
            }
            return currentOrders.get(orderId);
        });
    }

    public static void main(String[] args) throws InterruptedException {
        OrderService orderService = new OrderService();

        long order = orderService.createOrder(List.of());

        ExecutorService exec = Executors.newFixedThreadPool(10);

        IntStream.range(0, 10).forEach(value -> exec.execute(() -> {
            orderService.updatePaymentInfo(order, new PaymentInfo());
            orderService.setPacked(order);
        }));

        Thread.sleep(2000);
    }

    private void deliver(Order order) {
        System.out.println(order.getId());
        currentOrders.computeIfPresent(order.getId(), (id, toDeliver) -> order.delivered());
    }

    public boolean isDelivered(long orderId) {
        return currentOrders.get(orderId).getStatus().equals(Order.Status.DELIVERED);
    }
}
