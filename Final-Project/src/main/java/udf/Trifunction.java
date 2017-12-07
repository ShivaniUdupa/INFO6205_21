package udf;

import java.util.Objects;
import java.util.function.Function;

/**
 * Created by deveshkandpal on 12/7/17.
 */
@FunctionalInterface
public interface Trifunction<A,B,C,R> extends UserDefinedFunction {

    R apply(A a, B b, C c);

    default <V> Trifunction<A, B, C, V> andThen(
            Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (A a, B b, C c) -> after.apply(apply(a, b, c));
    }

}
