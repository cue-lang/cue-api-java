// Generated by jextract

package org.cuelang.libcue;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.lang.foreign.*;
import static java.lang.foreign.ValueLayout.*;
final class constants$8 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$8() {}
    static final FunctionDescriptor const$0 = FunctionDescriptor.of(MemoryLayout.structLayout(
        JAVA_INT.withName("tag"),
        MemoryLayout.paddingLayout(4),
        JAVA_LONG.withName("value"),
        RuntimeHelper.POINTER.withName("str"),
        JAVA_BOOLEAN.withName("b"),
        MemoryLayout.paddingLayout(7)
    ).withName("cue_bopt"),
        RuntimeHelper.POINTER
    );
    static final MethodHandle const$1 = RuntimeHelper.downcallHandle(
        "cue_filename",
        constants$8.const$0
    );
    static final MethodHandle const$2 = RuntimeHelper.downcallHandle(
        "cue_import_path",
        constants$8.const$0
    );
    static final FunctionDescriptor const$3 = FunctionDescriptor.of(MemoryLayout.structLayout(
        JAVA_INT.withName("tag"),
        MemoryLayout.paddingLayout(4),
        JAVA_LONG.withName("value"),
        RuntimeHelper.POINTER.withName("str"),
        JAVA_BOOLEAN.withName("b"),
        MemoryLayout.paddingLayout(7)
    ).withName("cue_bopt"),
        JAVA_BOOLEAN
    );
    static final MethodHandle const$4 = RuntimeHelper.downcallHandle(
        "cue_infer_builtins",
        constants$8.const$3
    );
    static final FunctionDescriptor const$5 = FunctionDescriptor.of(MemoryLayout.structLayout(
        JAVA_INT.withName("tag"),
        MemoryLayout.paddingLayout(4),
        JAVA_LONG.withName("value"),
        RuntimeHelper.POINTER.withName("str"),
        JAVA_BOOLEAN.withName("b"),
        MemoryLayout.paddingLayout(7)
    ).withName("cue_bopt"),
        JAVA_LONG
    );
    static final MethodHandle const$6 = RuntimeHelper.downcallHandle(
        "cue_scope",
        constants$8.const$5
    );
}

