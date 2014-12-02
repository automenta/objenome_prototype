/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome.problem;

import java.lang.reflect.Parameter;
import java.util.List;

/**
 *
 * @author me
 */
abstract public class DecideNumericValue implements Problem {

    public final Parameter parameter;
    public final List path;

    public DecideNumericValue(Parameter p, List path) {
        this.parameter = p;
        this.path = path;
    }
    
    
    
    public static class DecideBooleanValue extends DecideNumericValue  {
        public DecideBooleanValue(Parameter p, List path) {
            super(p, path);
        }    
    }
    
    public static class DecideIntegerValue extends DecideNumericValue  {
        public int min;
        public int max;

        public DecideIntegerValue(Parameter p, List path, int min, int max) {
            super(p, path);
            this.min = min;
            this.max = max;
            
            Between between = p.getDeclaredAnnotation(Between.class);
            if (between!=null) {
                this.min = (int)between.min();
                this.max = (int)between.max();
            }
            
        }    
        
        public DecideIntegerValue(Parameter p, List path) {
            this(p, path, 0, 1);
        }        
    }
    public static class DecideDoubleValue extends DecideNumericValue  {
        public double min;
        public double max;

        public DecideDoubleValue(Parameter p, List path, double min, double max) {
            super(p, path);
            this.min = min;
            this.max = max;
        
            Between between = p.getDeclaredAnnotation(Between.class);
            if (between!=null) {
                this.min = between.min();
                this.max = between.max();
            }

        }    
        
        public DecideDoubleValue(Parameter p, List path) {
            this(p, path, 0, 1);
        }

    }

}
