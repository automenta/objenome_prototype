package objenome.util.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.junit.Test;

public class FunctionalRangeTest {

    public static interface FooBar extends Cloneable {

        void setFoo(int foo);

        int getFoo();

        void setBar(String bar);

        String getBar();

        Object clone() throws CloneNotSupportedException;

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

        final int minLength = new HashMap<Object, Object>().toString().length();
        assertTrue("toString.length <= " + minLength, fooBar.toString().length() > minLength); //$NON-NLS-1$
        assertFalse("hashCode returned 0", 0 == fooBar.hashCode()); //$NON-NLS-1$

        assertEquals(fooBar, fooBar);

        // variable because PMD does not like equals(null) very much
        final Object nullObject = null;
        assertFalse("fooBar equals null", fooBar.equals(nullObject)); //$NON-NLS-1$
        assertFalse("fooBar equals java.lang.String", fooBar.equals("Classes are not compatible")); //$NON-NLS-1$ //$NON-NLS-2$

        final FooBar secondInstance = BeanProxyBuilder.on(FooBar.class).build();
        assertFalse("fooBar equals unfilled secondInstance", fooBar.equals(secondInstance)); //$NON-NLS-1$
        secondInstance.setFoo(fooBar.getFoo());
        secondInstance.setBar(fooBar.getBar());
        assertEquals("fooBar equals filled secondInstance", fooBar, secondInstance); //$NON-NLS-1$

        assertFalse("fooBar equals foo", fooBar.equals(BeanProxyBuilder.on(Foo.class).build())); //$NON-NLS-1$
    }

    @Test
    public void testExternalize() throws FileNotFoundException, IOException, ClassNotFoundException {
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
    public void testClone() throws CloneNotSupportedException {
        final FooBar ifaceOrig = BeanProxyBuilder.on(FooBar.class).build();
        final FooBar ifaceCloned = (FooBar) ifaceOrig.clone();

        assertEquals("clone and original are not equal", ifaceOrig, ifaceCloned); //$NON-NLS-1$
        assertNotSame("clone and original are same", ifaceOrig, ifaceCloned); //$NON-NLS-1$
    }

    @Test
    public void testCloneContent() throws CloneNotSupportedException {
        final FooBar ifaceOrig = BeanProxyBuilder.on(FooBar.class).build();
        ifaceOrig.setFoo(4711);

        final FooBar ifaceCloned = (FooBar) ifaceOrig.clone();
        ifaceOrig.setFoo(ifaceOrig.getFoo() + 1);
        assertFalse("clone content changed", ifaceOrig.getFoo() == ifaceCloned.getFoo()); //$NON-NLS-1$
    }

    private interface Foo {

        void setFoo(int foo);

        int getFoo();
    }

}
