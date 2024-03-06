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

import java.util.HashMap;
import java.util.Map;
import static org.cuelang.libcue.cue_h.*;

public final class CueValue {
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

    CueValue(CueContext ctx, CueResource res) {
        this.ctx = ctx;
        this.res = res;
    }

    long handle() {
        return this.res.handle();
    }

    public CueContext cueContext() {
        return this.ctx;
    }

    public CueValue unify(CueValue v) {
        var res = new CueResource(ctx.cleaner(), cue_unify(this.handle(), v.handle()));
        return new CueValue(ctx, res);
    }

    public boolean equals(CueValue v) {
        return cue_is_equal(this.handle(), v.handle());
    }

    public CueKind kind() {
        return toKind.get(cue_concrete_kind(this.handle()));
    }

    public CueKind incompleteKind() {
        return toKind.get(cue_incomplete_kind(this.handle()));
    }
}
