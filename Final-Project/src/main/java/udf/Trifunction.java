package udf;

import java.util.Objects;
import java.util.function.Function;

/**
 * Created by deveshkandpal on 12/7/17.
 *
 * Java supports only function and bifunction
 * TriFunction is a custom functional interface
 * that defines an apply method which accepts
 * 3 arguments A,B,C and returns an object of type R
 *
 */
@FunctionalInterface
public interface TriFunction<A,B,C,R> extends UserDefinedFunction {

    R apply(A a, B b, C c);

}
