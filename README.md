# Smart Pantry Manager

A Java Android application that helps users reduce food waste by tracking the ingredients they have at home and suggesting recipes they can cook using strictly those ingredients.

## Features

- Pantry management (add, edit, delete)
- Pantry list with RecyclerView bound to SQLite
- 20 seeded recipes with full ingredient lists
- Strict-matching suggested recipes
- Recipe detail screen
- Settings screen
- Bottom navigation

## Database Choice: SQLite

Chosen because:
- Runs entirely on-device, no internet required
- Perfect for a pantry app that must work offline
- Uses the SQLiteOpenHelper pattern from the module
- Full CRUD verified to persist across restarts

## Strict-Matching Algorithm

A recipe appears in suggestions ONLY if every required ingredient is present in sufficient quantity, with plural and unit normalization handled in util/RecipeMatcher.java.

## Setup

git clone https://github.com/groupnmss/SmartPantry.git
cd SmartPantry

Open in Android Studio, sync Gradle, run on a device or emulator (Pixel 5 API 33 tested).

## Author

Mobile App Development 700 - October 2026
