

public class Feladat {
    public static void main(String[] args) {
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 100; i ++) {
                    System.out.println("hello " + i);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 100; i ++) {
                    System.out.println("word " + i);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 100; i ++) {
                    System.out.println("other " + i);
                }
            }
        }).start();
        */

        String[] tomb = {"alma", "lakoma", "sajt", "eper"};
        for (String item : tomb) {
            new Thread(new Runnable() {
                int counter = 1;
                @Override
                public void run() {
                    while (counter <= 100)
                        System.out.println(item + counter++);
                }
            }).start();
        }
    }    
}
