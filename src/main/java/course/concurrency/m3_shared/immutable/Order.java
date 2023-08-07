package course.concurrency.m3_shared.immutable;

import java.util.ArrayList;
import java.util.List;

import static course.concurrency.m3_shared.immutable.Order.Status.DELIVERED;
import static course.concurrency.m3_shared.immutable.Order.Status.NEW;

public final class Order {

    public enum Status { NEW, IN_PROGRESS, DELIVERED }

    private final Long id;
    private final List<Item> items;
    private PaymentInfo paymentInfo;
    private boolean isPacked;
    private Status status;

    public Order(List<Item> items, Long id) {
        this.items = items;
        this.status = NEW;
        this.id = id;
    }

    public boolean checkStatus() {
        return paymentInfo != null && isPacked;
    }

    public Long getId() {
        return id;
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public Order withPaymentInfo(PaymentInfo paymentInfo) {
        Order order = new Order(this.getItems(), this.id);
        order.paymentInfo = paymentInfo;
        order.status = Status.IN_PROGRESS;
        order.isPacked = this.isPacked;
        return order;
    }

    public boolean isPacked() {
        return isPacked;
    }

    public synchronized Order packedOrder() {
        Order order = new Order(this.getItems(), this.id);
        order.isPacked = true;
        order.status = Status.IN_PROGRESS;
        order.paymentInfo = this.paymentInfo;
        return order;
    }

    public Status getStatus() {
        return status;
    }

    public Order withStatus(Status status) {
        Order order = new Order(this.getItems(), this.id);
        order.status = status;
        order.isPacked = this.isPacked;
        order.paymentInfo = this.paymentInfo;
        return order;
    }

    public Order delivered() {
        return withStatus(DELIVERED);
    }
}
