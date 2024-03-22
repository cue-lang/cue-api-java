// Copyright 2024 The CUE Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.cuelang.cue;

import org.cuelang.libcue.cue_bopt;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.ref.Cleaner;

import static org.cuelang.libcue.cue_h.*;

public final class CueContext {
    private static final Cleaner cleaner = Cleaner.create();
    private final CueResource ctx;

    public CueContext() {
        this.ctx = new CueResource(cleaner, cue_newctx());
    }

    private static MemorySegment encodeBuildOptions(@NotNull Arena arena, BuildOption @NotNull ... opts) {
        if (opts.length == 0) {
            return MemorySegment.ofAddress(0);
        }

        var options = cue_bopt.allocateArray(opts.length + 1, arena);

        // add end of array marker.
        var eoa = cue_bopt.asSlice(options, opts.length);
        cue_bopt.tag(eoa, CUE_BUILD_NONE());

        for (int i = 0; i < opts.length; i++) {
            var elem = cue_bopt.asSlice(options, i);
            switch (opts[i]) {
                case BuildOption.FileName f -> {
                    var cString = arena.allocateFrom(f.name());

                    cue_bopt.tag(elem, CUE_BUILD_FILENAME());
                    cue_bopt.str(elem, cString);
                }

                case BuildOption.ImportPath p -> {
                    var cString = arena.allocateFrom(p.path());

                    cue_bopt.tag(elem, CUE_BUILD_IMPORT_PATH());
                    cue_bopt.str(elem, cString);
                }

                case BuildOption.InferBuiltins b -> {
                    cue_bopt.tag(elem, CUE_BUILD_INFER_BUILTINS());
                    cue_bopt.b(elem, b.b());
                }
                case BuildOption.Scope s -> {
                    cue_bopt.tag(elem, CUE_BUILD_SCOPE());
                    cue_bopt.value(elem, s.v().handle());
                }
            }
        }

        return options;
    }

    long handle() {
        return ctx.handle();
    }

    Cleaner cleaner() {
        return cleaner;
    }

    public @NotNull Value top() {
        var res = new CueResource(cleaner, cue_top(ctx.handle()));
        return new Value(this, res);
    }

    public @NotNull Value bottom() {
        var res = new CueResource(cleaner, cue_bottom(ctx.handle()));
        return new Value(this, res);
    }

    @Contract("_, _ -> new")
    public @NotNull Value compile(@NotNull String s, BuildOption... opts) throws CueError {
        try (Arena arena = Arena.ofConfined()) {
            var cString = arena.allocateFrom(s);
            var bOpts = encodeBuildOptions(arena, opts);
            var ptr = arena.allocate(ValueLayout.JAVA_LONG);

            var err = cue_compile_string(ctx.handle(), cString, bOpts, ptr);
            if (err != 0) {
                throw new CueError(this, err);
            }

            var res = ptr.get(ValueLayout.JAVA_LONG, 0);
            return new Value(this, new CueResource(cleaner, res));
        }
    }

    @Contract("_, _ -> new")
    public @NotNull Value compile(byte[] buf, BuildOption... opts) throws CueError {
        try (Arena arena = Arena.ofConfined()) {
            var mem = arena.allocateFrom(ValueLayout.JAVA_BYTE, buf);
            var bOpts = encodeBuildOptions(arena, opts);
            var ptr = arena.allocate(ValueLayout.JAVA_LONG);

            var err = cue_compile_bytes(ctx.handle(), mem, buf.length, bOpts, ptr);
            if (err != 0) {
                throw new CueError(this, err);
            }

            var res = ptr.get(ValueLayout.JAVA_LONG, 0);
            return new Value(this, new CueResource(cleaner, res));
        }
    }

    @Contract("_ -> new")
    public @NotNull Value toValue(long n) {
        return new Value(this, n);
    }

    public @NotNull Value toValueAsUnsigned(long n) {
        var res = cue_from_uint64(ctx.handle(), n);
        return new Value(this, new CueResource(cleaner, res));
    }

    @Contract("_ -> new")
    public @NotNull Value toValue(boolean b) {
        return new Value(this, b);
    }

    @Contract("_ -> new")
    public @NotNull Value toValue(double v) {
        return new Value(this, v);
    }

    @Contract("_ -> new")
    public @NotNull Value toValue(String s) {
        return new Value(this, s);
    }

    @Contract("_ -> new")
    public @NotNull Value toValue(byte[] buf) {
        return new Value(this, buf);
    }
}
