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

linuxMachine: "ubuntu-22.04"

// Use a specific latest version for release builds.
// Note that we don't want ".x" for the sake of reproducibility,
// so we instead pin a specific Go release.
goVersion: "1.22.2"
