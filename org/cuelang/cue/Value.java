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

import java.lang.foreign.*;
import java.util.HashMap;
import java.util.Map;
import static org.cuelang.libcue.cue_h.*;
import org.cuelang.libcue.cue_eopt;

public final class Value {
    private final CueContext ctx;
    private final CueResource res;

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
        toKind.put(CUE_KIND_NUMBER(), CueKind.TOP);
    }

    Value(CueContext ctx, CueResource res) {
        this.ctx = ctx;
        this.res = res;
    }

    public Value(CueContext ctx, long n) {
        var res = cue_from_int64(ctx.handle(), n);
        this.res = new CueResource(ctx.cleaner(), res);
        this.ctx = ctx;
    }

    public Value(CueContext ctx, boolean b) {
        var res = cue_from_bool(ctx.handle(), b);
        this.res = new CueResource(ctx.cleaner(), res);
        this.ctx = ctx;
    }

    public Value(CueContext ctx, double v) {
        var res = cue_from_double(ctx.handle(), v);
        this.res = new CueResource(ctx.cleaner(), res);
        this.ctx = ctx;
    }

    public Value(CueContext ctx, String s) {
        try (Arena arena = Arena.ofConfined()) {
            var cString = arena.allocateUtf8String(s);
            var res = cue_from_string(ctx.handle(), cString);
            this.res = new CueResource(ctx.cleaner(), res);
            this.ctx = ctx;
        }
    }

    public Value(CueContext ctx, byte[] buf) {
        try (Arena arena = Arena.ofConfined()) {
            var mem = arena.allocateArray(ValueLayout.JAVA_BYTE, buf);
            var res = cue_from_bytes(ctx.handle(), mem, buf.length);
            this.res = new CueResource(ctx.cleaner(), res);
            this.ctx = ctx;
        }
    }

    long handle() {
        return this.res.handle();
    }

    public CueContext context() {
        return this.ctx;
    }

    public Value unify(Value v) {
        var res = new CueResource(ctx.cleaner(), cue_unify(this.handle(), v.handle()));
        return new Value(ctx, res);
    }

    public boolean equals(Value v) {
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

    public Value lookup(Value v, String path) throws CueError {
        try (Arena arena = Arena.ofConfined()) {
            var cString = arena.allocateUtf8String(path);
            var ptr = arena.allocate(ValueLayout.JAVA_LONG, 0);

            var res = cue_lookup_string(v.handle(), cString, ptr);
            if (res != 0) {
                throw new CueError(this.ctx, res);
            }

            var cueRes = new CueResource(v.context().cleaner(), res);
            return new Value(v.context(), cueRes);
        }
    }

	private static MemorySegment encodeEvalOptions(Arena arena, EvalOption... opts) {
	   var options = cue_eopt.allocateArray(opts.length + 1, arena);

		// add end of array marker.
		cue_eopt.tag$set(options, opts.length, CUE_OPT_NONE());

	   for (int i = 0; i < opts.length; i++) {
	       switch (opts[i]) {
	           case Eval.All e -> {
	               cue_eopt.tag$set(options, i, CUE_OPT_ALL());
	           }

	           case Eval.Attributes e -> {
	               cue_eopt.tag$set(options, i, CUE_OPT_ATTR());
	               cue_eopt.value$set(options, i, e.b());
	           }

	           case Eval.Concrete e -> {
	               cue_eopt.tag$set(options, i, CUE_OPT_CONCRETE());
	               cue_eopt.value$set(options, i, e.b());
	           }

	           case Eval.Definitions e -> {
	               cue_eopt.tag$set(options, i, CUE_OPT_DEFS());
	               cue_eopt.value$set(options, i, e.b());
	           }

	           case Eval.DisallowCycles e -> {
	               cue_eopt.tag$set(options, i, CUE_OPT_DISALLOW_CYCLES());
	               cue_eopt.value$set(options, i, e.b());
	           }

	           case Eval.Docs e -> {
	               cue_eopt.tag$set(options, i, CUE_OPT_DOCS());
	               cue_eopt.value$set(options, i, e.b());
	           }

	           case Eval.ErrorsAsValues e -> {
	               cue_eopt.tag$set(options, i, CUE_OPT_ERRORS_AS_VALUES());
	               cue_eopt.value$set(options, i, e.b());
	           }

	           case Eval.Final e -> {
	               cue_eopt.tag$set(options, i, CUE_OPT_FINAL());
	           }

	           case Eval.Hidden e -> {
	               cue_eopt.tag$set(options, i, CUE_OPT_HIDDEN());
	               cue_eopt.value$set(options, i, e.b());
	           }

	           case Eval.InlineImports e -> {
	               cue_eopt.tag$set(options, i, CUE_OPT_INLINE_IMPORTS());
	               cue_eopt.value$set(options, i, e.b());
	           }

	           case Eval.Optionals e -> {
	               cue_eopt.tag$set(options, i, CUE_OPT_OPTIONALS());
	               cue_eopt.value$set(options, i, e.b());
	           }

	           case Eval.Raw e -> {
	               cue_eopt.tag$set(options, i, CUE_OPT_RAW());
	           }

	           case Eval.Schema e -> {
	               cue_eopt.tag$set(options, i, CUE_OPT_SCHEMA());
	           }
	       }
	   }

		return options;
	}
}
