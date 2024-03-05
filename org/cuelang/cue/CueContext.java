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

import java.lang.ref.Cleaner;
import org.cuelang.libcue.CueNative;

public final class CueContext implements AutoCloseable {
	private static final Cleaner cleaner = Cleaner.create();
	private final CueResource ctx;

	public CueContext() {
		this.ctx = new CueResource(cleaner, CueNative.cue_newctx());
	}

	public CueValue top() {
		return new CueValue(new CueResource(cleaner, CueNative.cue_top(ctx.res)));
	}

	public CueValue bottom() {
		return new CueValue(new CueResource(cleaner, CueNative.cue_bottom(ctx.res)));
	}

    public void close() {
        ctx.close();
    }
}