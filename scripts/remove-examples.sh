#!/usr/bin/env bash
# Removes the example features from a checkout and leaves a runnable app (placeholder Home, one tab).
# Only the seven examples listed below are touched; your own feature modules stay.
#
#   bash scripts/remove-examples.sh [dir]     # default: the current directory
#
# Run it on a copy first (e.g. `git worktree add ../no-examples`) and build there:
#   ./gradlew test assembleDevDebug
# What it does:
#   1. deletes feature/<example>/ for each example and its include lines in settings.gradle.kts;
#   2. deletes every `// region example:<feature>` ... `// endregion` block in Kotlin and Gradle files
#      (nested regions included) - that is all the wiring :app has for the examples;
#   3. deletes :app tests that exercise the examples: tests in a `feature.*` package, tests importing an example
#      module's package, and tests that start with the line comment `// remove-examples: ...` (add it to an :app test
#      that drives example screens);
#   4. runs ktlintFormat, which drops the imports the deleted blocks used.
set -euo pipefail

EXAMPLES=(catalog products todos account settings devices animation gestures)

root="${1:-.}"
cd "$root"
[ -f settings.gradle.kts ] || { echo "not a project root: $(pwd)" >&2; exit 1; }

for feature in "${EXAMPLES[@]}"; do
    rm -rf "feature/$feature"
    sed -i "/include(\":feature:$feature:/d" settings.gradle.kts
done
rmdir feature 2>/dev/null || true # only when no feature of your own is left

strip_regions() {
    # A depth counter so a region inside an example region goes with it. Markers are whole lines, so the text
    # "// region example:" quoted inside a comment is not a marker.
    awk '
        /^[ \t]*\/\/ region example:[^ ]*\r?$/ { depth++; next }
        depth > 0 && /^[ \t]*\/\/ region / { depth++; next }
        depth > 0 && /^[ \t]*\/\/ endregion\r?$/ { depth--; next }
        depth > 0 { next }
        { print }
    ' "$1" > "$1.tmp" && mv "$1.tmp" "$1"
}

{ grep -rlE --include='*.kt' --include='*.kts' "^[[:space:]]*// region example:" . --exclude-dir=build --exclude-dir=.gradle || true; } |
    while IFS= read -r file; do strip_regions "$file"; done

if [ -d app/src/test ]; then
    modules=$(IFS='|'; echo "${EXAMPLES[*]}")
    # Example code keeps the package feature.example(.<sub>); core:ui's helpers share that root package, so a plain
    # `feature.example` import is not enough to delete a test: it takes an example sub-package or module package.
    pattern="^package com\.primestrider\.kotlinstarter\.feature\.|^// remove-examples: |kotlinstarter\.feature\.($modules)\.|kotlinstarter\.feature\.example\.(products|todos|auth|settings|sdk|animation|gestures|components|styling|form)\."
    { grep -rlE "$pattern" app/src/test || true; } | while IFS= read -r file; do rm -f "$file"; done
fi

# Never from the build cache: the cached result would not touch the edited sources.
./gradlew --quiet --no-build-cache ktlintFormat --rerun-tasks
echo "Examples removed from $(pwd). Build with: ./gradlew test assembleDevDebug"
