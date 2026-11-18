#!/usr/bin/env bash
# Turns the template into your project: renames the package and the app, picks phone or TV, and removes itself.
#
#   ./customize.sh com.acme.notes "Acme Notes"          # phone
#   ./customize.sh com.acme.notes "Acme Notes" --tv     # Android TV / Google TV
set -euo pipefail

OLD_PACKAGE="com.example.template"
OLD_APP_NAME="AppTemplate"
OLD_PROJECT_NAME="android-template"

usage() {
  echo "usage: $0 <package> <app name> [--tv]" >&2
  exit 1
}

[[ $# -ge 2 ]] || usage
package="$1"
app_name="$2"
variant="phone"
if [[ $# -ge 3 ]]; then
  [[ "$3" == "--tv" ]] || usage
  variant="tv"
fi

if [[ ! "$package" =~ ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$ ]]; then
  echo "\"$package\" isn't a valid package name, e.g. com.acme.notes" >&2
  exit 1
fi
if [[ -z "${app_name// /}" || "$app_name" == *[\<\>\&\"\'\\]* ]]; then
  echo "The app name must not be empty or contain < > & \" ' \\" >&2
  exit 1
fi
cd "$(dirname "$0")"
if [[ ! -d variants ]]; then
  echo "This project was customized already." >&2
  exit 1
fi

project_name="$(printf '%s' "$app_name" | tr '[:upper:]' '[:lower:]' | sed -E 's/[^a-z0-9]+/-/g; s/^-+|-+$//g')"
old_path="${OLD_PACKAGE//.//}"
new_path="${package//.//}"

if [[ "$variant" == "tv" ]]; then
  cp -R variants/tv/. .
fi
rm -rf variants

# move every source set from com/example/template to the new package, through a temporary folder,
# so a new package inside the old one (com.example.template.app) works too
while IFS= read -r source_root; do
  temporary="$(mktemp -d "${TMPDIR:-/tmp}/customize.XXXXXX")"
  mv "$source_root/$old_path"/* "$temporary"/
  rm -rf "${source_root:?}/${old_path%%/*}"
  mkdir -p "$source_root/$new_path"
  mv "$temporary"/* "$source_root/$new_path"/
  rmdir "$temporary"
done < <(find . -type d -path "*/src/*/kotlin" -not -path "./.git/*" -not -path "*/build/*" | while read -r dir; do
  [[ -d "$dir/$old_path" ]] && echo "$dir"
done)

# the package, the app name and the project name in every text file
export OLD_PACKAGE OLD_APP_NAME OLD_PROJECT_NAME package app_name project_name
while IFS= read -r -d '' file; do
  perl -pi -e 's/\Q$ENV{OLD_PACKAGE}\E/$ENV{package}/g; s/\Q$ENV{OLD_APP_NAME}\E/$ENV{app_name}/g; s/\Q$ENV{OLD_PROJECT_NAME}\E/$ENV{project_name}/g' "$file"
done < <(find . -type f \
  \( -name "*.kt" -o -name "*.kts" -o -name "*.xml" -o -name "*.toml" -o -name "*.properties" -o -name "*.json" -o -name "*.md" -o -name "*.yml" -o -name "justfile" \) \
  -not -path "./.git/*" -not -path "*/build/*" -not -path "./.gradle/*" -not -name "gradle-wrapper.properties" -print0)

rm -f .github/workflows/template.yml
mv PROJECT_README.md README.md
rm -- "$0"

echo "Customized as $app_name ($package, $variant)."
echo "Next: ./gradlew :app:assembleDebug"
