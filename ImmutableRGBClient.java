public class ImmutableRGBClient {

    private static final int ITERATIONS = 500_000;

    private static volatile ImmutableRGB color =
            new ImmutableRGB(255, 0, 0, "Red");

    public static void main(String[] args) throws InterruptedException {

        Thread writer = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                color = color.invert();
            }
        }, "Writer");

        Thread reader = new Thread(() -> {
            int inconsistencies = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                ImmutableRGB snapshot = color;

                int rgb  = snapshot.getRGB();
                String name = snapshot.getName();

                boolean valid =
                        (rgb == 0xFF0000 && name.equals("Red"))            ||
                        (rgb == 0x00FFFF && name.startsWith("Inverse of")) ||
                        (snapshot.getRGB() == rgb && snapshot.getName().equals(name));

                if (snapshot.getRGB() != rgb || !snapshot.getName().equals(name)) {
                    inconsistencies++;
                }
            }
            System.out.println("[Reader] Inconsistencies detected: " + inconsistencies);
            if (inconsistencies == 0) {
                System.out.println("[Reader] => Thread safety confirmed: every snapshot is " +
                        "internally consistent because the object is immutable.");
            }
        }, "Reader");

        System.out.println("Starting ImmutableRGB test...");
        writer.start();
        reader.start();
        writer.join();
        reader.join();
        System.out.println("Done.");
    }
}
