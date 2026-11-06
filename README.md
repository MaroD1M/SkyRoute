# SkyRoute · 天际航路

> A sci-fi flight-themed network tunnel for Android.
> Every connection is a flight. Every node is an airport. Every rule is a flight plan.

SkyRoute is a **GPL-3.0 derivative work** based on
[ClashMetaForAndroid](https://github.com/MetaCubeX/ClashMetaForAndroid)
by the ClashMetaForAndroid contributors. It is **not** an official
MetaCubeX/Clash.Meta product and is not endorsed by the original authors.

The proxy kernel is [mihomo](https://github.com/MetaCubeX/mihomo)
(formerly Clash.Meta), used as a git submodule.

---

## Flight deck

- Live airspace radar: origin → destination route with animated takeoff/landing
- Flight status HUD: `STANDBY`, `IN FLIGHT`, `LANDED`
- Four switchable launcher icons: Cyan Interceptor, Amber Sunset, Violet Orbit, System Themed (Material You)
- In-app launcher icon preview, with simplified 48/72 px artwork
- Three switchable cockpit styles: Cyan, Amber, Violet (HUD, radar, accents)
- Animated flight transition on takeoff/landing, plus haptic toggle feedback
- Android 12+ animated takeoff splash screen
- Flight plans (profiles), air routes (proxy groups), airlines (providers)
- Black box (logs), flight recorder (logcat), control tower (settings)
- Passenger manifest (per-app access control), airspace control (TUN/VPN)
- Night-cockpit dark theme with animated radar, pulsing telemetry and HUD cards
- All original settings and configuration capabilities, re-themed as flight ops
- All mihomo kernel features: rule-based routing, TUN, DNS, proxies, providers

## Identity

| Item | Value |
|---|---|
| App name | SkyRoute / 天际航路 |
| Application ID | `com.skyroute.flightdeck` |
| URL schemes | `clash://install-config`, `clashmeta://install-config` (kept for compatibility) |
| External actions | `com.skyroute.flightdeck.action.TAKEOFF`, `.LAND`, `.TOGGLE_FLIGHT` |
| License | GNU GPL v3.0 |

## Requirements

- Android 5.0+ (Android 7.0+ recommended)
- `armeabi-v7a`, `arm64-v8a`, `x86`, or `x86_64`

## Build

1. Update submodules:

   ```bash
   git submodule update --init --recursive
   ```

2. Install **OpenJDK 21**, **Android SDK**, **CMake**, **NDK 29**, and **Go**.

3. Create `local.properties`:

   ```properties
   sdk.dir=/path/to/android-sdk
   ```

   Optional custom application id:

   ```properties
   custom.application.id=com.example.skyroute
   remove.suffix=true
   ```

4. Optional signing config in `signing.properties`:

   ```properties
   keystore.password=<password>
   key.alias=<alias>
   key.password=<password>
   ```

   Place your own keystore at `release.keystore` in the repository root
   (ignored by git). For GitHub Actions, set `SIGNING_KEYSTORE_BASE64`,
   `SIGNING_STORE_PASSWORD`, `SIGNING_KEY_ALIAS`, and `SIGNING_KEY_PASSWORD`
   repository secrets.

5. Build:

   ```bash
   ./gradlew app:assembleMetaRelease
   ```

## Automation

Package name: `com.skyroute.flightdeck`

- Toggle flight: intent action `com.skyroute.flightdeck.action.TOGGLE_FLIGHT`
  to `com.skyroute.flightdeck.ExternalControlActivity`
- Take off: intent action `com.skyroute.flightdeck.action.TAKEOFF`
- Land: intent action `com.skyroute.flightdeck.action.LAND`
- Import a flight plan: `clash://install-config?url=<encoded URI>`
  or `clashmeta://install-config?url=<encoded URI>`
  (these original scheme names are intentionally kept for compatibility with
  existing subscription links and automation)

## Upstream and license

This repository is a modified version of ClashMetaForAndroid:

- Upstream: <https://github.com/MetaCubeX/ClashMetaForAndroid>
- Kernel: <https://github.com/MetaCubeX/mihomo>
- License: <https://www.gnu.org/licenses/gpl-3.0.html>

Because SkyRoute is distributed under GPL-3.0, the complete corresponding
source code for any distributed APK must remain available under GPL-3.0.
Copyright notices, the `LICENSE` file, and the `NOTICE` file are preserved.
Modifications are documented in `NOTICE` and in the git history.

SkyRoute is provided **without warranty**. It does not provide any proxy,
VPN, or network service. You are responsible for complying with the laws and
network policies that apply to you.
