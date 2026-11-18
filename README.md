# android-template

A multi-module Android app to start from, for a phone or for Android TV / Google TV. It builds, has tests, and keeps
itself up to date: Renovate bumps the dependencies, and CI customizes the template as both variants and builds them
before anything is merged.

- **Kotlin + Jetpack Compose**, Material 3 (phone) or Compose for TV (TV)
- **Metro** for compile-time DI, with bindings contributed from the module that owns them
- **Navigation3** with `@Serializable` destinations, so features never reference each other
- **Modules** `:app → :features:* → :navigation → :core:*`, and a build that fails when a module breaks the rules
- **Convention plugins** in `build-logic/`, every version in `gradle/libs.versions.toml`
- **Tests** with kotlin.test + power-assert, mockk, Turbine, Molecule, Robolectric
- **ktlint** with the Compose rules, configuration cache, build cache

## Start a project

```sh
gh repo create my-app --template NuclleaR/android-template --private --clone
cd my-app
./customize.sh com.acme.myapp "My App"          # phone
./customize.sh com.acme.myapp "My App" --tv     # Android TV / Google TV
./gradlew :app:assembleDebug
```

Without a GitHub repository: `git clone --depth 1 https://github.com/NuclleaR/android-template.git my-app`, then
`rm -rf my-app/.git` and customize.

`customize.sh` renames the package and the source folders, sets the app name and the project name, applies the TV
variant when asked, and deletes itself, the `variants/` folder and the template-only workflow. With Claude Code and the
`android` plugin from `NuclleaR/skills`, `/android:new` does all of this.

Opening the project in Claude Code offers the `android` plugin from `NuclleaR/skills` (`.claude/settings.json`): skills
for architecture, Gradle, tests, Compose and the agent emulator, and `/android:just` for `just` recipes.

You need JDK 21 (Gradle downloads it through the foojay toolchain resolver when it's missing) and the Android SDK in
`ANDROID_HOME` or `local.properties` (`sdk.dir=…`). No Android Studio needed.

## Layout

```
app/                 Application, Metro graph, MainActivity with NavDisplay
navigation/          Destination (sealed, @Serializable), Navigator, NavEntryProvider
core/common/         DispatcherProvider, root graph access
core/data/api/       ItemRepository: what features depend on
core/data/impl/      InMemoryItemRepository: only :app depends on it
core/strings/        every user-visible string
core/ui/             AppTheme, icons
features/home/       list screen: HomeViewModel + stateless HomeView
features/detail/     detail screen, a ViewModel with an argument (@AssistedInject)
build-logic/         convention plugins: convention.app, convention.library, convention.compose, convention.ktlint
variants/tv/         what --tv puts over the phone app: views, theme, manifest, banner
```

The ViewModels are plain classes with a `@Composable fun viewState()`, kept across recompositions and configuration
changes with `retain`, and tested with Molecule. The phone and TV variants share them, the navigation, the data and the
DI. Only the views differ.

## Commands

```sh
./gradlew :app:assembleDebug        # debug APK
./gradlew :app:installDebug         # on the device in ANDROID_SERIAL
./gradlew appUnitTest               # every unit test
./gradlew formatKotlin lintKotlin   # format, then check
./gradlew :app:lintDebug            # Android lint
```

Debug builds open any back stack from an intent extra:

```sh
adb shell am start -n com.example.template.debug/com.example.template.MainActivity \
  --es debugBackStack '[{"type":"Home"},{"type":"Detail","itemId":"1"}]'
```

## Keeping it current

- Renovate (`renovate.json`) opens pull requests for new versions every Monday. Patch and minor updates merge
  themselves once CI is green. Major AGP and Gradle updates wait for a review, because they change the DSL.
- `Template` CI customizes the template as a phone and as a TV app and builds both, so an update that breaks a
  generated project never reaches `main`.
- A generated project keeps `renovate.json` and `CI`, so it stays current the same way.

## License

MIT
