// Copyright 2024 The CUE Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//	http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.cuelang.cue;

import org.cuelang.libcue.cue_eopt;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.foreign.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.cuelang.libcue.cue_h.*;

public final class Value {
    private static final Map<Integer, CueKind> toKind = new HashMap<>();

    static {
        toKind.put(CUE_KIND_BOTTOM(), CueKind.BOTTOM);
        toKind.put(CUE_KIND_NULL(), CueKind.NULL);
        toKind.put(CUE_KIND_BOOL(), CueKind.BOOL);
        toKind.put(CUE_KIND_INT(), CueKind.INT);
        toKind.put(CUE_KIND_FLOAT(), CueKind.FLOAT);
        toKind.put(CUE_KIND_STRING(), CueKind.STRING);
        toKind.put(CUE_KIND_BYTES(), CueKind.BYTES);
        toKind.put(CUE_KIND_STRUCT(), CueKind.STRUCT);
        toKind.put(CUE_KIND_LIST(), CueKind.LIST);
        toKind.put(CUE_KIND_NUMBER(), CueKind.NUMBER);
        toKind.put(CUE_KIND_TOP(), CueKind.TOP);
    }

    private final CueContext ctx;
    private final CueResource res;

    Value(CueContext ctx, CueResource res) {
        this.ctx = ctx;
        this.res = res;
    }

    public Value(@NotNull CueContext ctx, long n) {
        var res = cue_from_int64(ctx.handle(), n);
        this.res = new CueResource(ctx.cleaner(), res);
        this.ctx = ctx;
    }

    public Value(@NotNull CueContext ctx, boolean b) {
        var res = cue_from_bool(ctx.handle(), b);
        this.res = new CueResource(ctx.cleaner(), res);
        this.ctx = ctx;
    }

    public Value(@NotNull CueContext ctx, double v) {
        var res = cue_from_double(ctx.handle(), v);
        this.res = new CueResource(ctx.cleaner(), res);
        this.ctx = ctx;
    }

    public Value(@NotNull CueContext ctx, String s) {
        try (Arena arena = Arena.ofConfined()) {
            var cString = arena.allocateFrom(s);
            var res = cue_from_string(ctx.handle(), cString);
            this.res = new CueResource(ctx.cleaner(), res);
            this.ctx = ctx;
        }
    }

    public Value(@NotNull CueContext ctx, byte[] buf) {
        try (Arena arena = Arena.ofConfined()) {
            var mem = arena.allocateFrom(ValueLayout.JAVA_BYTE, buf);
            var res = cue_from_bytes(ctx.handle(), mem, buf.length);
            this.res = new CueResource(ctx.cleaner(), res);
            this.ctx = ctx;
        }
    }

    private static MemorySegment encodeEvalOptions(Arena arena, EvalOption @NotNull ... opts) {
        if (opts.length == 0) {
            return MemorySegment.ofAddress(0);
        }

        var options = cue_eopt.allocateArray(opts.length + 1, arena);

        // add end of array marker.
        var eoa = cue_eopt.asSlice(options, opts.length);
        cue_eopt.tag(eoa, CUE_OPT_NONE());

        for (int i = 0; i < opts.length; i++) {
            var elem = cue_eopt.asSlice(options, i);
            switch (opts[i]) {
                case Eval.All _ -> cue_eopt.tag(elem, CUE_OPT_ALL());

                case Eval.Attributes e -> {
                    cue_eopt.tag(elem, CUE_OPT_ATTR());
                    cue_eopt.value(elem, e.b());
                }

                case Eval.Concrete e -> {
                    cue_eopt.tag(elem, CUE_OPT_CONCRETE());
                    cue_eopt.value(elem, e.b());
                }

                case Eval.Definitions e -> {
                    cue_eopt.tag(elem, CUE_OPT_DEFS());
                    cue_eopt.value(elem, e.b());
                }

                case Eval.DisallowCycles e -> {
                    cue_eopt.tag(elem, CUE_OPT_DISALLOW_CYCLES());
                    cue_eopt.value(elem, e.b());
                }

                case Eval.Docs e -> {
                    cue_eopt.tag(elem, CUE_OPT_DOCS());
                    cue_eopt.value(elem, e.b());
                }

                case Eval.ErrorsAsValues e -> {
                    cue_eopt.tag(elem, CUE_OPT_ERRORS_AS_VALUES());
                    cue_eopt.value(elem, e.b());
                }

                case Eval.Final _ -> cue_eopt.tag(elem, CUE_OPT_FINAL());

                case Eval.Hidden e -> {
                    cue_eopt.tag(elem, CUE_OPT_HIDDEN());
                    cue_eopt.value(elem, e.b());
                }

                case Eval.InlineImports e -> {
                    cue_eopt.tag(elem, CUE_OPT_INLINE_IMPORTS());
                    cue_eopt.value(elem, e.b());
                }

                case Eval.Optionals e -> {
                    cue_eopt.tag(elem, CUE_OPT_OPTIONALS());
                    cue_eopt.value(elem, e.b());
                }

                case Eval.Raw _ -> cue_eopt.tag(elem, CUE_OPT_RAW());

                case Eval.Schema _ -> cue_eopt.tag(elem, CUE_OPT_SCHEMA());
            }
        }

        return options;
    }

    long handle() {
        return this.res.handle();
    }

    public CueContext context() {
        return this.ctx;
    }

    public Result<Value, String> error() {
        var err = cue_value_error(this.handle());
        if (err != 0) {
            var cString = cue_error_string(err);
            return new Result.Err<>(cString.getString(0));
        }
        return new Result.Ok<>(this);
    }

    public @NotNull Value unify(@NotNull Value v) {
        var res = new CueResource(ctx.cleaner(), cue_unify(this.handle(), v.handle()));
        return new Value(ctx, res);
    }

    public boolean equals(@NotNull Value v) {
        return cue_is_equal(this.handle(), v.handle());
    }

    public CueKind kind() {
        return toKind.get(cue_concrete_kind(this.handle()));
    }

    public CueKind incompleteKind() {
        return toKind.get(cue_incomplete_kind(this.handle()));
    }

    public void validate(EvalOption... opts) throws CueError {
        try (Arena arena = Arena.ofConfined()) {
            var eOpts = encodeEvalOptions(arena, opts);
            var res = cue_validate(this.handle(), eOpts);
            if (res != 0) {
                throw new CueError(this.ctx, res);
            }
        }
    }

    public void checkSchema(Value v, EvalOption... opts) throws CueError {
        try (Arena arena = Arena.ofConfined()) {
            var eOpts = encodeEvalOptions(arena, opts);
            var err = cue_instance_of(this.handle(), v.handle(), eOpts);
            if (err != 0) {
                throw new CueError(this.ctx, err);
            }
        }
    }

    @Contract("_ -> new")
    public @NotNull Value lookup(String path) throws CueError {
        try (Arena arena = Arena.ofConfined()) {
            var cString = arena.allocateFrom(path);
            var ptr = arena.allocate(ValueLayout.JAVA_LONG);

            var err = cue_lookup_string(this.handle(), cString, ptr);
            if (err != 0) {
                throw new CueError(this.ctx, err);
            }

            var res = ptr.get(ValueLayout.JAVA_LONG, 0);
            var cueRes = new CueResource(this.context().cleaner(), res);
            return new Value(this.context(), cueRes);
        }
    }

    @Contract(" -> new")
    public @NotNull Pair<Value, Boolean> defaultValue() {
        try (Arena arena = Arena.ofConfined()) {
            var ptr = arena.allocate(ValueLayout.JAVA_LONG);
            var res = cue_default(this.handle(), ptr);
            var cueRes = new CueResource(ctx.cleaner(), res);

            if (ptr.get(ValueLayout.JAVA_LONG, 0) == 1) {
                return new Pair<>(new Value(ctx, cueRes), true);
            }
            return new Pair<>(new Value(ctx, cueRes), false);
        }
    }

    public long getLong() throws CueError {
        try (Arena arena = Arena.ofConfined()) {
            var ptr = arena.allocate(ValueLayout.JAVA_LONG);
            var err = cue_dec_int64(this.handle(), ptr);
            if (err != 0) {
                throw new CueError(this.ctx, err);
            }
            return ptr.get(ValueLayout.JAVA_LONG, 0);
        }
    }

    public long getLongAsUnsigned() throws CueError {
        try (Arena arena = Arena.ofConfined()) {
            var ptr = arena.allocate(ValueLayout.JAVA_LONG);
            var err = cue_dec_uint64(this.handle(), ptr);
            if (err != 0) {
                throw new CueError(this.ctx, err);
            }
            return ptr.get(ValueLayout.JAVA_LONG, 0);
        }
    }

    public boolean getBoolean() throws CueError {
        try (Arena arena = Arena.ofConfined()) {
            var ptr = arena.allocate(ValueLayout.JAVA_LONG);
            var err = cue_dec_bool(this.handle(), ptr);
            if (err != 0) {
                throw new CueError(this.ctx, err);
            }

            return ptr.get(ValueLayout.JAVA_LONG, 0) == 1;
        }
    }

    public double getDouble() throws CueError {
        try (Arena arena = Arena.ofConfined()) {
            var ptr = arena.allocate(ValueLayout.JAVA_DOUBLE);
            var err = cue_dec_double(this.handle(), ptr);
            if (err != 0) {
                throw new CueError(this.ctx, err);
            }
            return ptr.get(ValueLayout.JAVA_DOUBLE, 0);
        }
    }

    public String getString() throws CueError {
        try (Arena arena = Arena.ofConfined()) {
            var ptr = arena.allocate(ValueLayout.ADDRESS);
            var err = cue_dec_string(this.handle(), ptr);
            if (err != 0) {
                throw new CueError(this.ctx, err);
            }

            var strPtr = ptr.get(ValueLayout.ADDRESS.withTargetLayout(AddressLayout.ADDRESS), 0);
            var str = strPtr.getString(0);
            libc_free(strPtr);

            return str;
        }
    }

    public byte[] getBytes() throws CueError {
        try (Arena arena = Arena.ofConfined()) {
            var bufPtrPtr = arena.allocate(ValueLayout.ADDRESS);
            var lenPtr = arena.allocate(ValueLayout.JAVA_LONG);
            var err = cue_dec_bytes(this.handle(), bufPtrPtr, lenPtr);
            if (err != 0) {
                throw new CueError(this.ctx, err);
            }

            var len = lenPtr.get(ValueLayout.JAVA_LONG, 0);
            var bufLayout = ValueLayout.ADDRESS.withTargetLayout(
                    MemoryLayout.sequenceLayout(len, ValueLayout.JAVA_BYTE));
            var buf = bufPtrPtr.get(bufLayout, 0);

            var a = buf.toArray(ValueLayout.JAVA_BYTE);
            libc_free(buf);

            return a;
        }
    }

    public String getJSON() throws CueError {
        try (Arena arena = Arena.ofConfined()) {
            var bufPtrPtr = arena.allocate(ValueLayout.ADDRESS);
            var lenPtr = arena.allocate(ValueLayout.JAVA_LONG);
            var err = cue_dec_json(this.handle(), bufPtrPtr, lenPtr);
            if (err != 0) {
                throw new CueError(this.ctx, err);
            }

            var len = lenPtr.get(ValueLayout.JAVA_LONG, 0);
            var bufLayout = ValueLayout.ADDRESS.withTargetLayout(
                    MemoryLayout.sequenceLayout(len, ValueLayout.JAVA_BYTE));
            var buf = bufPtrPtr.get(bufLayout, 0);

            var str = new String(buf.toArray(ValueLayout.JAVA_BYTE), StandardCharsets.UTF_8);
            libc_free(buf);

            return str;
        }
    }
}
