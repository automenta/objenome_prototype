package objenome.util.bean;

import org.junit.Test;

import java.io.*;
import java.util.HashMap;

import static org.junit.Assert.*;

public class FunctionalRangeTest {

    public interface FooBar extends Cloneable {

        void setFoo(int foo);

        int getFoo();

        void setBar(String bar);

        String getBar();

        Object clone();

    }

    @Test
    public void test1() {
        final FooBar fooBar = BeanProxyBuilder.on(FooBar.class).build();
        assertEquals("Wrong value of unset primitive type", 0, fooBar.getFoo()); //$NON-NLS-1$

        final int testInt = 3;
        fooBar.setFoo(testInt);
        assertEquals("Wrong value of set primitive type", testInt, fooBar.getFoo()); //$NON-NLS-1$

        assertNull("Wrong value of unset type", fooBar.getBar()); //$NON-NLS-1$
        final String testString = "Hallo"; //$NON-NLS-1$
        fooBar.setBar(testString);
        assertEquals("Wrong value of set type", testString, fooBar.getBar()); //$NON-NLS-1$ 

        final int minLength = new HashMap<>().toString().length();
        assertTrue("toString.length <= " + minLength, fooBar.toString().length() > minLength); //$NON-NLS-1$
        assertNotEquals("hashCode returned 0", 0, fooBar.hashCode()); //$NON-NLS-1$

        assertEquals(fooBar, fooBar);

        // variable because PMD does not like equals(null) very much
        final Object nullObject = null;
        assertNotEquals("fooBar equals null", nullObject, fooBar); //$NON-NLS-1$
        assertNotEquals("fooBar equals java.lang.String", "Classes are not compatible", fooBar); //$NON-NLS-1$ //$NON-NLS-2$

        final FooBar secondInstance = BeanProxyBuilder.on(FooBar.class).build();
        assertNotEquals("fooBar equals unfilled secondInstance", fooBar, secondInstance); //$NON-NLS-1$
        secondInstance.setFoo(fooBar.getFoo());
        secondInstance.setBar(fooBar.getBar());
        assertEquals("fooBar equals filled secondInstance", fooBar, secondInstance); //$NON-NLS-1$

        assertNotEquals("fooBar equals foo", fooBar, BeanProxyBuilder.on(Foo.class).build()); //$NON-NLS-1$
    }

    @Test
    public void testExternalize() throws IOException, ClassNotFoundException {
        final String testString = "Externalizable is working!"; //$NON-NLS-1$

        final FooBar ifaceOut = BeanProxyBuilder.on(FooBar.class).build();
        ifaceOut.setBar(testString);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream oStream = new ObjectOutputStream(byteArrayOutputStream);
        oStream.writeObject(ifaceOut);
        oStream.close();

        final ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray()));
        final FooBar ifaceIn = (FooBar) iStream.readObject();
        iStream.close();

        assertEquals("testString of read object has wrong value", testString, ifaceIn.getBar()); //$NON-NLS-1$
        assertEquals("written and read objects are not equal", ifaceOut, ifaceIn); //$NON-NLS-1$
    }

    @Test
    public void testClone() {
        final FooBar ifaceOrig = BeanProxyBuilder.on(FooBar.class).build();
        final FooBar ifaceCloned = (FooBar) ifaceOrig.clone();

        assertEquals("clone and original are not equal", ifaceOrig, ifaceCloned); //$NON-NLS-1$
        assertNotSame("clone and original are same", ifaceOrig, ifaceCloned); //$NON-NLS-1$
    }

    @Test
    public void testCloneContent() {
        final FooBar ifaceOrig = BeanProxyBuilder.on(FooBar.class).build();
        ifaceOrig.setFoo(4711);

        final FooBar ifaceCloned = (FooBar) ifaceOrig.clone();
        ifaceOrig.setFoo(ifaceOrig.getFoo() + 1);
        assertNotEquals("clone content changed", ifaceOrig.getFoo(), ifaceCloned.getFoo()); //$NON-NLS-1$
    }

    private interface Foo {

        void setFoo(int foo);

        int getFoo();
    }

}