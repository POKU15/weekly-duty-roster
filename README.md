# Weekly Duty Roster

This repository contains a Kotlin Compose Desktop starter application for the Dunkwa FPU Weekly Duty Roster system.

## Features

- Login / Signup workflow with role-based access:
  - `Station Officer` with full edit and roster generation access
  - `Secretary` with view-only roster preview access
- Personnel registration module with add/delete actions
- Duty point management with editable PW-allowed duty points
- Weekly roster preview with a generated assignment grid
- Preview roster screen for verification before printing

## Getting Started

### Requirements

- Java 17 or later
- Gradle

### Run

```bash
gradle run
```

### Build Native Distributions

```bash
gradle package
```

## Notes

This initial version provides a combined skeleton for the two requested modules (authentication and duty roster management) and can be extended with the full rotation rules, advanced schedule generation, and persistent storage.
