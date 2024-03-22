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

import org.jetbrains.annotations.NotNull;

import static org.cuelang.libcue.cue_h.cue_error_string;
import static org.cuelang.libcue.cue_h.libc_free;

public final class CueError extends Exception {
    private final CueResource res;

    CueError(@NotNull CueResource res) {
        super(getErrorStringFromHandle(res.handle()));
        this.res = res;
    }

    CueError(@NotNull CueContext ctx, long res) {
        super(getErrorStringFromHandle(res));
        this.res = new CueResource(ctx.cleaner(), res);
    }

    private static String getErrorStringFromHandle(long handle) {
        var cString = cue_error_string(handle);
        var str = cString.getString(0);
        libc_free(cString);

        return str;
    }

    long handle() {
        return this.res.handle();
    }
}
