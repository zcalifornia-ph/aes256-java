import java.util.Scanner;

/**
 * Minimal CLI entrypoint for educational invocation paths.
 *
 * <p>This BOLT-2.3 version focuses on selftest routing (`--selftest` and menu option `5`).
 * Full encrypt/decrypt menu wiring is scheduled for Unit-03.
 */
public final class Main {

    private Main() {}

    /**
     * Runs the command-line entrypoint.
     *
     * @param args command-line args
     */
    public static void main(String[] args) {
        if (hasArg(args, "--selftest") || hasArg(args, "--selftest-large")) {
            boolean includeLarge = hasArg(args, "--selftest-large");
            int code = SelfTest.run(System.out, includeLarge);
            System.exit(code);
            return;
        }

        runMenu();
    }

    private static void runMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                case "2":
                case "3":
                case "4":
                case "6":
                    System.out.println("not implemented yet for this bolt");
                    break;
                case "5":
                    int code = SelfTest.runDefault(System.out);
                    System.out.println("selftest exit code=" + code);
                    break;
                case "0":
                    System.out.println("bye");
                    return;
                default:
                    System.out.println("invalid menu choice");
                    break;
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("=== aes256-java ===");
        System.out.println("1) Encrypt text");
        System.out.println("2) Decrypt text");
        System.out.println("3) Encrypt file");
        System.out.println("4) Decrypt file");
        System.out.println("5) SelfTest");
        System.out.println("6) About");
        System.out.println("0) Quit");
        System.out.print("Select option: ");
    }

    private static boolean hasArg(String[] args, String flag) {
        for (String arg : args) {
            if (flag.equals(arg)) {
                return true;
            }
        }
        return false;
    }
}
