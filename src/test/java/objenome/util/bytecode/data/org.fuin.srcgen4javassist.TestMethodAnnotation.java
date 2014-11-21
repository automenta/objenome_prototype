package objenome.util.bytecode.data;

public class TestMethodAnnotation {
    private int count = 0;

    @org.fuin.srcgen4javassist.XMethodAnnotation
    public int getCount() {
        return count;
    }
}
