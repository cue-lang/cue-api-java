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

import org.cuelang.libcue.cue_attr_arg;
import org.jetbrains.annotations.NotNull;

import java.lang.foreign.Arena;

import static org.cuelang.libcue.cue_h.*;

public class Attribute {
    private final CueResource res;

    Attribute(@NotNull CueResource res) {
        this.res = res;
    }

    public int argCount() {
        return (int)cue_attr_numargs(res.handle());
    }

    public @NotNull String name() {
        var cString = cue_attr_name(res.handle());
        var str = cString.getString(0);
        libc_free(cString);

        return str;
    }

    public @NotNull String value() {
        var cString = cue_attr_value(res.handle());
        var str = cString.getString(0);
        libc_free(cString);

        return str;
    }

    public @NotNull Attribute.Arg arg(int i) {
        if (i >= this.argCount())
            throw new IndexOutOfBoundsException();

        try (Arena arena = Arena.ofConfined()) {
            var argPtr = arena.allocate(cue_attr_arg.layout());
            cue_attr_getarg(res.handle(), i, argPtr);

            var key = cue_attr_arg.key(argPtr);
            var val = cue_attr_arg.val(argPtr);
            var keyStr = key.getString(0);
            var valStr = val.getString(0);

            Arg arg;
            if (valStr.isEmpty())
                arg = new Arg.Value(keyStr);
            else
                arg = new Arg.KeyValue(keyStr, valStr);

            libc_free(key);
            libc_free(val);
            return arg;
        }
    }

    public @NotNull Arg[] args() {
        var args = new Arg[this.argCount()];

        for (int i = 0; i < args.length; i++)
            args[i] = this.arg(i);

        return args;
    }

    public sealed interface Arg permits
            Arg.Value,
            Arg.KeyValue {
        record Value(String val) implements Arg {}

        record KeyValue(String key, String val) implements Arg {}
    }
}
