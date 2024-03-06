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
import java.lang.ref.Cleaner;
import static org.cuelang.libcue.cue_h.*;
import org.cuelang.libcue.cue_bopt;

public final class CueContext {
	private static final Cleaner cleaner = Cleaner.create();
	private final CueResource ctx;

	public CueContext() {
		this.ctx = new CueResource(cleaner, cue_newctx());
	}

	public CueValue top() {
		return new CueValue(new CueResource(cleaner, cue_top(ctx.handle())));
	}

	public CueValue bottom() {
		return new CueValue(new CueResource(cleaner, cue_bottom(ctx.handle())));
	}

	public CueValue compileString(String s, BuildOption... opts) {
		try (Arena arena = Arena.ofConfined()) {
			var cString = arena.allocateUtf8String(s);
			var bOpts = encodeBuildOptions(arena, opts);
			var res = cue_compile_string(ctx.handle(), cString, bOpts);

			return new CueValue(new CueResource(cleaner, res));
		}
	}

	public CueValue compileBytes(byte[] buf, BuildOption... opts) {
		try (Arena arena = Arena.ofConfined()) {
			var mem = arena.allocateArray(ValueLayout.JAVA_BYTE, buf);
			var bOpts = encodeBuildOptions(arena, opts);
			var res = cue_compile_bytes(ctx.handle(), mem, buf.length, bOpts);

			return new CueValue(new CueResource(cleaner, res));
		}
	}

	private static MemorySegment encodeBuildOptions(Arena arena, BuildOption... opts) {
		var options = cue_bopt.allocateArray(opts.length + 1, arena);

		for (int i = 0; i < opts.length; i++) {
			switch (opts[i]) {
				case Build.FileName f -> {
					var cString = arena.allocateUtf8String(f.name());

					cue_bopt.tag$set(options, i, CUE_BUILD_FILENAME());
					cue_bopt.str$set(options, i, cString);
				}

				case Build.ImportPath p -> {
					var cString = arena.allocateUtf8String(p.path());

					cue_bopt.tag$set(options, i, CUE_BUILD_IMPORT_PATH());
					cue_bopt.str$set(options, i, cString);
				}

				case Build.InferBuiltins b -> {
					cue_bopt.tag$set(options, i, CUE_BUILD_INFER_BUILTINS());
					cue_bopt.b$set(options, i, b.b());
				}
				case Build.Scope s -> {
					cue_bopt.tag$set(options, i, CUE_BUILD_SCOPE());									cue_bopt.value$set(options, i, s.v().handle());
				}
			}
		}

		// add end of array marker.
		cue_bopt.tag$set(options, opts.length, CUE_BUILD_NONE());

		return options;
	}
}
