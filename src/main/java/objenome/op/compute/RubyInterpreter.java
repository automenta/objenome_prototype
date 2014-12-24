/*
 * Copyright 2007-2013
 * Licensed under GNU Lesser General Public License
 * 
 * This file is part of EpochX: genetic programming software for research
 * 
 * EpochX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EpochX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with EpochX. If not, see <http://www.gnu.org/licenses/>.
 * 
 * The latest version is available from: http://www.epochx.org
 */
package objenome.op.compute;

import javax.script.Invocable;
import javax.script.ScriptException;
import objenome.solver.evolve.Individual;
import objenome.solver.evolve.source.SourceGenerator;

/**
 * A <code>RubyInterpreter</code> provides the facility to evaluate individual
 * Ruby expressions and execute multi-line Ruby statements.
 *
 * <p>
 * <code>RubyInterpreter</code> extends from the
 * <code>ScriptingInterpreter</code>, adding ruby specific enhancements,
 * including optimized performance.
 *
 * @since 2.0
 */
public class RubyInterpreter<T extends Individual> extends ScriptingInterpreter<Object,T,Object> {

    /**
     * Constructs a <code>RubyInterpreter</code> with a source generator
     *
     * @param generator the SourceGenerator to use to convert individuals to
     * Ruby source code
     */
    public RubyInterpreter(SourceGenerator<T> generator) {
        super(generator, "ruby");
    }

    /**
     * Evaluates any valid Ruby expression which may optionally contain the use
     * of any argument named in the <code>argNames</code> array which will be
     * pre-declared and assigned to the associated value taken from the
     * <code>argValues</code> array.
     *
     * The expression will be evaluated once for each set of
     * <code>argValues</code>. The object array returned will contain the result
     * of each of these evaluations in order.
     *
     * @param expression an individual representing a valid Ruby expression that
     * is to be evaluated.
     * @param argNames {@inheritDoc}
     * @param argValues {@inheritDoc}
     * @return the return values from evaluating the expression. The runtime
     * type of the returned Objects may vary from program to program. If the
     * program does not return a value then this method will return an array of
     * nulls.
     */
    @Override
    public Object[] eval(T program, String[] argNames, Object[][] argValues)
            throws MalformedProgramException {
        int noParamSets = argNames.length;

        String expression = getSourceGenerator().getSource(program);
        String code = getEvalCode(expression, argNames);

        Object[] result = new Object[noParamSets];

        final Invocable invocableEngine = (Invocable) getEngine();
        try {
            getEngine().eval(code);

            for (int i = 0; i < noParamSets; i++) {
                result[i] = invocableEngine.invokeFunction("expr", argValues[i]);
            }
        } catch (final ScriptException ex) {
            throw new MalformedProgramException();
        } catch (final NoSuchMethodException ex) {
            throw new MalformedProgramException();
        }

        return result;
    }

    /**
     * Executes any valid Ruby program which may optionally contain the use of
     * any argument named in the <code>argNames</code> array which will be
     * pre-declared and assigned to the associated value taken from the
     * <code>argValues</code> array. The program will be executed once for each
     * set of <code>argValues</code>.
     *
     * @param program an individual representing a valid Ruby program.
     * @param argNames {@inheritDoc}
     * @param argValues {@inheritDoc}
     */
    @Override
    public void exec(T program, String[] argNames, Object[][] argValues)
            throws MalformedProgramException {
        int noParamSets = argValues.length;

        String source = getSourceGenerator().getSource(program);
        String code = getExecCode(source, argNames);

        Invocable invocableEngine = (Invocable) getEngine();
        try {
            getEngine().eval(code);

            for (int i = 0; i < noParamSets; i++) {
                invocableEngine.invokeFunction("expr", argValues[i]);
            }
        } catch (ScriptException ex) {
            throw new MalformedProgramException();
        } catch (NoSuchMethodException ex) {
            throw new MalformedProgramException();
        }
    }

    /*
     * Helper method to eval. Constructs a string representing source code of a Ruby 
     * method containing a return statement that returns the result of evaluating
     * the given expression.
     */
    private String getEvalCode(String expression, String[] argNames) {
        StringBuffer code = new StringBuffer();

        code.append("def expr(");
        for (int i = 0; i < argNames.length; i++) {
            if (i > 0) {
                code.append(',');
            }
            code.append(argNames[i]);
        }
        code.append(")\n");

        code.append("return ");
        code.append(expression);
        code.append(";\n");
        code.append("end\n");

        return code.toString();
    }

    /*
     * Helper method to exec. Constructs a string representing source code of a Ruby 
     * method containing the given program.
     */
    private String getExecCode(String program, String[] argNames) {
        final StringBuffer code = new StringBuffer();

        // code.append("class Evaluation\n");
        code.append("def expr(");
        for (int i = 0; i < argNames.length; i++) {
            if (i > 0) {
                code.append(',');
            }
            code.append(argNames[i]);
        }
        code.append(")\n");

        code.append(program);
        code.append("\n");
        code.append("end\n");

        return code.toString();
    }
}
