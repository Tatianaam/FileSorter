package sorter;

public class Run {

    public static void main(String[] args) {

        Sorter sorter = new Sorter();

        try {
            sorter.sort();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            System.exit(1);
        }

    }


}
