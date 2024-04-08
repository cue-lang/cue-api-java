// package repo contains data values that are common to all CUE configurations
// in this repo. The list of configurations includes GitHub workflows, but also
// things like gerrit configuration etc.
package repo

import (
	"github.com/cue-lang/cue-api-java/internal/ci/base"
)

base

githubRepositoryPath: "cue-lang/cue-api-java"

botGitHubUser:      "cueckoo"
botGitHubUserEmail: "cueckoo@gmail.com"

defaultBranch: "main"

linuxMachine:   "ubuntu-22.04"
macosMachine:   "macos-14"
windowsMachine: "windows-2022"

previousStableGo: "1.21.x"
latestStableGo: "1.22.x"
