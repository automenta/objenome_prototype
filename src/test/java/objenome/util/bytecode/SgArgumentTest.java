// CHECKSTYLE:OFF
package objenome.util.bytecode;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SgArgumentTest extends SgVariableTest {

    @Before
    public void setup() {
        super.setup();
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testConstruction() {
        final String modifiers = "final";
        final SgClass type = SgClass.INT;
        final String name = "arg";
        final SgMethod method = new SgMethod(getDummyClass(), "public", SgClass.VOID, "setArg");
        final SgArgument arg = new SgArgument(method, modifiers, type, name);
        Assert.assertSame(arg.getOwner(), method);
        Assert.assertSame(arg.getType(), type);
        Assert.assertEquals(arg.getName(), name);
        Assert.assertEquals(arg.getModifiers(), modifiers);
        Assert.assertEquals(arg.getAnnotations().size(), 0);
        Assert.assertEquals(arg.toString(), "final int arg");
    }

}
// CHECKSTYLE:ON
