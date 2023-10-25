package course.concurrency.tests.ping_pong;

public class PingPongTest {

    private static final Object lock = new Object();
    private static boolean isPing = true;

    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    while (!isPing) {
                        waitPlease();
                    }
                    System.out.println("Ping");
                    isPing = false;
                    lock.notify();
                }
            }
        }).start();
        new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    while (isPing) {
                        waitPlease();
                    }
                    System.out.println("Pong");
                    isPing = true;
                    lock.notify();
                }
            }
        }).start();
    }

    private static void waitPlease() {
        try {
            lock.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
