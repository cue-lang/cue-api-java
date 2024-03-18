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
import static org.cuelang.libcue.cue_h.*;

final class CueResource {
    private final Cleaner.Cleanable cleanable;
    private final long res;

    CueResource(Cleaner c, long res) {
        this.res = res;
        this.cleanable = c.register(this, () -> cue_free(res));
    }

    long handle() {
        return this.res;
    }
}
