// Generated by jextract

package org.cuelang.libcue;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.lang.foreign.*;
import static java.lang.foreign.ValueLayout.*;
final class constants$1 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$1() {}
    static final StructLayout const$0 = MemoryLayout.structLayout(
        JAVA_INT.withName("tag"),
        MemoryLayout.paddingLayout(4),
        JAVA_LONG.withName("value"),
        RuntimeHelper.POINTER.withName("str"),
        JAVA_BOOLEAN.withName("b"),
        MemoryLayout.paddingLayout(7)
    ).withName("cue_bopt");
    static final VarHandle const$1 = constants$1.const$0.varHandle(MemoryLayout.PathElement.groupElement("tag"));
    static final VarHandle const$2 = constants$1.const$0.varHandle(MemoryLayout.PathElement.groupElement("value"));
    static final VarHandle const$3 = constants$1.const$0.varHandle(MemoryLayout.PathElement.groupElement("str"));
    static final VarHandle const$4 = constants$1.const$0.varHandle(MemoryLayout.PathElement.groupElement("b"));
    static final FunctionDescriptor const$5 = FunctionDescriptor.of(JAVA_LONG);
    static final MethodHandle const$6 = RuntimeHelper.downcallHandle(
        "cue_newctx",
        constants$1.const$5
    );
}

