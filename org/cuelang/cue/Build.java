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

public final class Build {
	public static final class FileName extends BuildOption {
		public final String name;

		public FileName(String s) {
			this.name = s;
		}
	}

	public static final class ImportPath extends BuildOption {
		public final String path;

		public ImportPath(String s) {
			this.path = s;
		}
	}

	public static final class InferBuiltins extends BuildOption {
		public final boolean infer;

		public InferBuiltins(boolean b) {
			this.infer = b;
		}
	}

	public static final class Scope extends BuildOption {
		public final CueValue scope;

		public Scope(CueValue scope) {
			this.scope = scope;
		}
	}
}
