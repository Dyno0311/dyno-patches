# 🧩 Dyno Patches

Personal Android patches for apps I own, built with the Morphe patcher.

## ❓ About

Two kinds of patches live here:

- **Universal** — `patches/src/main/kotlin/dev/dyno0311/patches/universal/`: apply across many apps (ad SDK blocking, telemetry removal, and similar).
- **App-targeted** — `patches/src/main/kotlin/dev/dyno0311/patches/apps/<appname>/`: written for one package and pinned to the exact version that was tested.

Shared compatibility declarations and cross-patch constants belong in `patches/src/main/kotlin/dev/dyno0311/patches/shared/`.

These are private, personal-use patches. No patched APKs are distributed from this repository.

### How to use these patches

**This repository is currently private**, and Morphe Manager cannot fetch a private repo as a patch source. Until it is published, build the bundle locally and apply the `.mpp` by hand:

```sh
./gradlew buildAndroid   # -> patches/build/libs/patches-*.mpp
```

Once the repo is made public (only on the owner's explicit decision), it can be added as a patch source: https://github.com/Dyno0311/dyno-patches

<a href="https://morphe.software/add-source?github=Dyno0311/dyno-patches">
  <img src=".github/assets/add-to-morphe.svg" alt="Add to Morphe" height="48">
</a>

## 🩹 Patches list

<!-- PATCHES_START EXPANDED -->

<!-- Do not modify this section by hand. The patch list is generated when release.yml creates a new release.
     
     If you wish for the patches list to be collapsed, then remove the word 'EXPANDED' from the comment tag above.

     If you wish to manually keep this list updated then remove the PATCHES_START and PATCHES_END 
     comment blocks entirely. -->

#### A list of your patches will automatically be shown here after your first patches release is created.

&nbsp;

## 🚀 Adding a patch

Layout under `patches/src/main/kotlin/dev/dyno0311/patches/`:

```text
universal/            cross-app patches (ad SDKs, telemetry, selection, orientation)
apps/<appname>/       one app, pinned to the tested version
shared/               shared Compatibility declarations and constants
```

Rules used in this repo:

- One patch per file, named for what it does. Keep the change minimal.
- Universals must fail soft: skip when a fingerprint does not match, log what matched, and never edit a method the fingerprint cannot prove.
- App-targeted patches pin the exact `version` (and `versionCodes` when architecture builds share a version name) that was tested.
- Prefer `returnEarly`/`returnLate`, `resourceXml`, or one narrow instruction edit over injected smali blocks.
- Build a stable fingerprint from non-obfuscated types, method shape, durable strings, literals, and calls. Port behaviour from community patches, not their API shape.

### Development workflow

- **Make all changes to the `dev` branch.**
- Local build: `./gradlew buildAndroid` produces `patches/build/libs/patches-*.mpp`; apply it with Morphe Desktop like any other bundle.
- Use semantic commit messages only:
  - `feat: Added a new feature`
  - `fix: Some problem now fixed`
  - `chore: Random change you do not want in the user facing changelog`
- `fix:` and `feat:` produce pre-releases from `dev`; `chore:` does not.
- When `dev` is ready, merge it into `main` (merge, do not squash) for a stable release.
- **Always use semantic release (release.yml).** Do not hand-create releases; do not hand-edit `patches-list.json`, `patches-bundle.json`, or `CHANGELOG.md`.
- Enable "Allow GitHub Actions to create and approve pull requests" in Settings > Actions > General > Workflow permissions.
- Optional signing so Morphe Manager rejects swapped releases:
  1. `./gradlew generateSigningKey` creates `morphe-signing.pub` and `morphe-signing.key`.
  2. Commit `morphe-signing.pub`. Never commit `morphe-signing.key`; it is git-ignored.
  3. Add the contents of `morphe-signing.key` as the `MORPHE_SIGNING_KEY` repo secret, back it up, then delete the file.

## 🤓 Tips
- See the [patcher documentation](https://github.com/MorpheApp/morphe-patcher/blob/main/docs/1_patcher_intro.md) for more examples of creating patches and fingerprints.
- Do not use AI to create new release scripts. The `release.yml` here already handles everything.
  If you need something custom with your releases then modify the existing `release.yml`
  and `.releaserc` instead of writing everything new from scratch.
- Do not manually edit or manually commit any generated files such as: `patches-list.json`,
  `patches-bundle.json`, `CHANGELOG.md`.  These files will be automatically updated by `release.yml`.
- Do not force push any semantic release commits as that will break all future releases.
  If you need to fix a broken release, it's always easiest to create a new release instead of 
  fixing an existing release.


<!-- The patches end tag is intentionally placed here so the first release will clean up 
     this readme of all developer instructions above. -->
<!-- PATCHES_END -->

### 🛠️ Building locally

Requirements:

- **JDK 21.** The `app.morphe.patches` plugin will not run on JDK 17.
- **A GitHub token with `read:packages`.** The Morphe patcher artifacts live on GitHub Packages, which requires this scope *even for public packages*. Without it Gradle reports the plugin as "not found" (it is really a 401).

```sh
# gh CLI: add the scope once
gh auth refresh -h github.com -s read:packages

# Build. Note: a pre-set GITHUB_TOKEN environment variable shadows `gh auth token`,
# and an older token without read:packages produces a confusing 401 — hence `env -u`.
export JAVA_HOME=/path/to/jdk-21
T=$(env -u GITHUB_TOKEN -u GITHUB_ACTOR gh auth token)
GITHUB_ACTOR=<github-user> GITHUB_TOKEN="$T" ./gradlew --no-daemon buildAndroid
```

Output:

- The built patches `.mpp` file is found in `patches/build/libs/patches-*.mpp`
- Patch the mpp file using [Morphe-Desktop](https://github.com/MorpheApp/morphe-desktop)
  like any other patch bundle.

**No `extensions/` module.** The template's `extensions/extension` project was removed: any `.mpe` runtime extension is an Android library and therefore needs a full Android SDK (`ANDROID_HOME`), which a plain headless JDK build does not have. No patch here uses one. If a future patch needs an `.mpe`, copy `extensions/extension` back from
[MorpheApp/morphe-patches-template](https://github.com/MorpheApp/morphe-patches-template)
and install the Android command-line tools + platform + build-tools first.

See the [Morphe documentation](https://github.com/MorpheApp/morphe-documentation) for more information.

## 📜 License

Dyno Patches are licensed under the [GNU General Public License v3.0](LICENSE)
