public class SynchronizedRGBClient {

    private static final int ITERATIONS = 500_000;

    private static final SynchronizedRGB color =
            new SynchronizedRGB(255, 0, 0, "Red");

    public static void main(String[] args) throws InterruptedException {

        Thread writer = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                if (i % 2 == 0) {
                    color.set(255, 0, 0, "Red");
                } else {
                    color.set(0, 0, 255, "Blue");
                }
            }
        }, "Writer");

        Thread reader = new Thread(() -> {
            int inconsistencies = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                int rgb  = color.getRGB();
                String name = color.getName();

                boolean redRgb   = (rgb == 0xFF0000);
                boolean blueRgb  = (rgb == 0x0000FF);
                boolean redName  = name.equals("Red");
                boolean blueName = name.equals("Blue");

                if ((redRgb && !redName) || (blueRgb && !blueName)) {
                    inconsistencies++;
                }
            }
            System.out.println("[Reader] Inconsistencies detected: " + inconsistencies);
            if (inconsistencies > 0) {
                System.out.println("[Reader] => Race condition observed: getRGB() and getName() " +
                        "are individually synchronized but NOT atomically composed.");
            } else {
                System.out.println("[Reader] => No inconsistency detected in this run " +
                        "(race condition is non-deterministic; try again or increase ITERATIONS).");
            }
        }, "Reader");

        System.out.println("Starting SynchronizedRGB test...");
        writer.start();
        reader.start();
        writer.join();
        reader.join();
        System.out.println("Done.");
    }
}
