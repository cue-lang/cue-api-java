// Generated by jextract

package org.cuelang.libcue;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.lang.foreign.*;
import static java.lang.foreign.ValueLayout.*;
final class constants$12 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$12() {}
    static final MethodHandle const$0 = RuntimeHelper.downcallHandle(
        "cue_attr_name",
        constants$11.const$3
    );
    static final MethodHandle const$1 = RuntimeHelper.downcallHandle(
        "cue_attr_value",
        constants$11.const$3
    );
    static final FunctionDescriptor const$2 = FunctionDescriptor.ofVoid(
        JAVA_LONG
    );
    static final MethodHandle const$3 = RuntimeHelper.downcallHandle(
        "cue_free",
        constants$12.const$2
    );
    static final FunctionDescriptor const$4 = FunctionDescriptor.ofVoid(
        RuntimeHelper.POINTER
    );
    static final MethodHandle const$5 = RuntimeHelper.downcallHandle(
        "cue_free_all",
        constants$12.const$4
    );
}


