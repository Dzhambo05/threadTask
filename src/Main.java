import java.io.*;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    static String path = System.getenv("path");
    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread write1 = new Thread(() -> new Main().writeNumber(true), "EVEN");
        Thread write2 = new Thread(() -> new Main().writeNumber(false), "ODD");
        Thread read = new Thread(Main::readNumber, "READ");

        write1.start();
        write2.start();
        read.start();

        write1.join();
        write2.join();
        read.join();

    }

    private void writeNumber(boolean isValueEven) {
        Random random = new Random();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(System.getenv("path"), true))){
            while (true) {
                synchronized (Main.lock) {
                    int value = isValueEven ? random.nextInt() * 2 : random.nextInt() * 2 + 1;
                    writer.write(value + "\n");
                    writer.flush();
                }
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void readNumber() {
        while (true) {
            try (BufferedReader reader = new BufferedReader(new FileReader(System.getenv("path")))) {
                String value;
                while ((value = reader.readLine()) != null) {
                    System.out.println(Thread.currentThread().getName() + ": " + value);
                    Thread.sleep(500);
                }
            } catch (IOException io) {
                System.out.println(io.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}