#!/usr/bin/env bash
# Removes every example feature from a checkout and leaves a runnable app (placeholder Home, one tab).
#
#   bash scripts/remove-examples.sh [dir]     # default: the current directory
#
# Run it on a copy first (e.g. `git worktree add ../no-examples`) and build there:
#   ./gradlew test assembleDebug
# What it does:
#   1. deletes feature/ (every :feature:*:api and :impl module) and their include lines;
#   2. deletes every `// region example:<feature>` ... `// endregion` block in Kotlin and Gradle files
#      (nested regions included) - that is all the wiring :app has for the examples;
#   3. deletes :app tests that exercise example features (they reference feature.example / feature.* packages);
#   4. runs ktlintFormat, which drops the imports the deleted blocks used.
set -euo pipefail

root="${1:-.}"
cd "$root"
[ -f settings.gradle.kts ] || { echo "not a project root: $(pwd)" >&2; exit 1; }

rm -rf feature
sed -i '/include(":feature:/d' settings.gradle.kts

strip_regions() {
    # A depth counter so a region inside an example region goes with it.
    awk '
        /\/\/ region example:/ { depth++; next }
        depth > 0 && /\/\/ region / { depth++; next }
        depth > 0 && /\/\/ endregion/ { depth--; next }
        depth > 0 { next }
        { print }
    ' "$1" > "$1.tmp" && mv "$1.tmp" "$1"
}

grep -rl --include='*.kt' --include='*.kts' "// region example:" . --exclude-dir=build --exclude-dir=.gradle |
    while IFS= read -r file; do strip_regions "$file"; done

if [ -d app/src/test ]; then
    grep -rlE "kotlinstarter\.feature\.(example|catalog|products|todos|account|settings|devices|animation)" app/src/test |
        while IFS= read -r file; do rm -f "$file"; done
fi

./gradlew --quiet ktlintFormat
echo "Examples removed from $(pwd). Build with: ./gradlew test assembleDebug"
