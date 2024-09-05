// Copyright 2024 The CUE Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package github

import (
	"list"

	"github.com/SchemaStore/schemastore/src/schemas/json"
)

// The trybot workflow.
workflows: trybot: _repo.bashWorkflow & {
	name: _repo.trybot.name

	on: {
		push: {
			branches: list.Concat([[_repo.testDefaultBranch], _repo.protectedBranchPatterns]) // do not run PR branches
		}
		pull_request: {}
	}

	jobs: {
		test: {
			strategy:  _testStrategy
			"runs-on": "${{ matrix.runner }}"

			let _setupGoActionsCaches = _repo.setupGoActionsCaches & {
				#goVersion: goVersionVal
				#os:        runnerOSVal
				_
			}

			// Only run the trybot workflow if we have the trybot trailer, or
			// if we have no special trailers. Note this condition applies
			// after and in addition to the "on" condition above.
			if: "\(_repo.containsTrybotTrailer) || ! \(_repo.containsDispatchTrailer)"

			steps: [
				for v in _repo.checkoutCode {v},

				for v in _installGo {v},
				_installJava,

				// cachePre must come after installing Go,
				// because the cache locations
				// are established by running each tool.
				for v in _setupGoActionsCaches {v},

				_checkoutLibcue,
				_buildLibcue,

				_repo.earlyChecks,
				_mavenTest,

				_goGenerate,
				_repo.checkGitClean,
			]
		}
	}

	let runnerOS = "runner.os"
	let runnerOSVal = "${{ \(runnerOS) }}"
	let goVersion = "matrix.go-version"
	let goVersionVal = "${{ \(goVersion) }}"

	_testStrategy: {
		"fail-fast": false
		matrix: {
			"go-version": [_repo.latestStableGo]

			// TODO: Windows is missing because of issue #3016.
			runner: [_repo.linuxMachine, _repo.macosMachine]
		}
	}

	_installGo: _repo.installGo & {
		#setupGo: with: "go-version": goVersionVal
		_
	}

	_installJava: json.#step & {
		name: "Install Java"
		uses: "actions/setup-java@v4"
		with: {
			distribution:   "temurin"
			"java-version": "22"
		}
	}

	_checkoutLibcue: json.#step & {
		name: "Checkout libcue"
		uses: "actions/checkout@v4"
		with: {
			repository: "cue-lang/libcue"
			path:       "libcue"
		}
	}

	_buildLibcue: json.#step & {
		name:                "Build libcue"
		"working-directory": "libcue"
		// The name of the shared library is target-dependent.
		// Build libcue with all possible names so we're covered
		// in all cases.
		run: """
			go build -o libcue.so -buildmode=c-shared
			cp libcue.so libcue.dylib
			cp libcue.so cue.dll
			"""
	}

	_mavenTest: json.#step & {
		name: "Test"
		env: LD_LIBRARY_PATH:   "${{ github.workspace }}/libcue"
		env: DYLD_LIBRARY_PATH: "${{ github.workspace }}/libcue"
		run: "mvn clean install package"
	}

	_goGenerate: json.#step & {
		name: "go generate"
		run:  "go generate ./..."
	}
}
