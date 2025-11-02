# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview
Palermo is a Minecraft Paper plugin (version 1.21.10) built with Gradle and Java 21.

## Common Commands

### Building
```pwsh
.\gradlew.bat build
```

### Running Test Server
```pwsh
.\gradlew.bat runServer
```
Starts a Minecraft 1.21 test server with the plugin loaded.

## Architecture

**Main Class:** `at.zobiii.palermo.Palermo` extends `JavaPlugin`
- Package: `at.zobiii.palermo`
- Paper API: 1.21.10-R0.1-SNAPSHOT
- Java: 21
