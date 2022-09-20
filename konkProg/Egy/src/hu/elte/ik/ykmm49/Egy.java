package hu.elte.ik.ykmm49;

public class Egy {
    public static void main(String[] args) {
        System.out.println("hello world");
        A a = new C();
        System.out.println(a.a());
    }

    static interface A {
        int a();
    }

    abstract static class B implements A {
        public abstract String b();
    }

    static class C extends B {

        @Override
        public int a() {
            return 2;
        }

        @Override
        public String b() {
            return "alma";
        }
    }
}
