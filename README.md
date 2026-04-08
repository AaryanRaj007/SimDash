# SimDash

> [!WARNING]
> ⚠️ **Early development — buggy and unfinished. Not ready for daily use.**|
> SimDash is unfinished, unstable, and missing core functionality.
> Do not use this as your primary data tracker. Expect crashes, 
> wrong numbers, and incomplete screens.
A clean, minimal Android widget that shows your prepaid SIM's data usage, 
balance, and plan validity at a glance — no more dialing *121#, no more 
opening carrier apps full of ads.

Built for Indian users on Jio, Airtel, Vi, and BSNL.

---

## What it will do (when finished)

- 🔵 Home screen widget with a circular data usage ring
- 🔵 Automatic carrier detection (Jio, Airtel, Vi, BSNL)
- 🔵 Smart alerts when you hit 40%, 60%, or 80% of your daily data
- 🔵 Plan validity countdown — alerts you the day before expiry
- 🔵 Daily morning summary notification
- 🔵 Works with zero internet — everything stays on your phone
- 🔵 Follows your phone's dark/light mode automatically

---

## Current state

This project is in early development. Large parts of the app are 
incomplete or not working correctly:

- Widget may show incorrect or stale data
- USSD balance fetch is not reliable on all devices
- Alert system is partially implemented
- Onboarding flow is incomplete
- UI screens are placeholder quality
- Not tested on most devices yet

**Do not use this as your primary data tracker right now.**

---

## Planned support

- Android 8.0 and above (API 26+)
- Jio · Airtel · Vi · BSNL
- Dual SIM (active data SIM auto-detected)

---

## Tech stack

Kotlin · Jetpack Compose · Material 3 · Room · Hilt · DataStore

---

## Contributing

Too early for contributions right now — the core architecture is 
still being figured out. Watch the repo and check back soon.



*Made by Aaryan · India*
