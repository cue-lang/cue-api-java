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

public sealed interface EvalOption permits
        EvalOption.All,
        EvalOption.Attributes,
        EvalOption.Concrete,
        EvalOption.Definitions,
        EvalOption.DisallowCycles,
        EvalOption.Docs,
        EvalOption.ErrorsAsValues,
        EvalOption.Final,
        EvalOption.Hidden,
        EvalOption.InlineImports,
        EvalOption.Optionals,
        EvalOption.Raw,
        EvalOption.Schema {
    record All() implements EvalOption {}

    record Attributes(boolean b) implements EvalOption {}

    record Concrete(boolean b) implements EvalOption {}

    record Definitions(boolean b) implements EvalOption {}

    record DisallowCycles(boolean b) implements EvalOption {}

    record Docs(boolean b) implements EvalOption {}

    record ErrorsAsValues(boolean b) implements EvalOption {}

    record Final() implements EvalOption {}

    record Hidden(boolean b) implements EvalOption {}

    record InlineImports(boolean b) implements EvalOption {}

    record Optionals(boolean b) implements EvalOption {}

    record Raw() implements EvalOption {}

    record Schema() implements EvalOption {}
}
