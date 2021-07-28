package objenome.op;

public class MutableLiteral<X> extends Literal<X> {

    /**
     * Constructs a new <code>Literal</code> node with the given value.
     * Evaluation of this literal will return the value set here. The data-type
     * of this node will be determined by the type of the object specified here.
     *
     * @param value the value of this literal
     */
    public MutableLiteral(X value) {
        super(value);
    }

    /**
     * Sets the value of this literal. Implementations of this class may wish to
     * use this method to delay the setting of a literal's value.
     *
     * @param value the value to set for this literal
     */
    public void setValue(X value) {
        this.value = value;
    }

}