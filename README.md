# 🐄 KsheeraSagara – Dairy Farm Management App

An Android app that helps small dairy farmers track milk income, expenses and cattle, and see their monthly profit or loss at a glance. Built with **Kotlin** and **Jetpack Compose**, works **offline**, and supports **5 languages**.

## ✨ Features

- **Milk income log:** record litres, fat percentage and rate per litre; total income is calculated automatically
- **Expense tracking:** categories for Fodder, Medical, Labour, Electricity and Others
- **Cow management:** add cows with name and breed, mark them active or inactive, and view income and expenses per cow
- **Monthly summary:** total income, total expense, net profit, litres sold and a pie-chart breakdown
- **Milk calendar and vaccination screens** to keep daily records and health schedules organized
- **Financial literacy tips** with expandable cards
- **PDF monthly report** that can be shared through WhatsApp or any app
- **Daily reminders** using WorkManager notifications
- **4 languages:** English, Kannada (ಕನ್ನಡ), Hindi (हिन्दी), Marathi (मराठी)
- **Dark mode** and a first-run onboarding flow

## 🏗️ Architecture

The app follows **MVVM** with a repository layer:

```
Compose Screens (UI)
      │  observe StateFlow
      ▼
ViewModel (FarmViewModel, AuthViewModel, ThemeViewModel)
      │
      ▼
FarmRepository
      │
      ▼
Room DAOs (MilkDao, ExpenseDao, CowDao) ──► SQLite (ksheera_sagara_db)
```

- **Room** entities: `MilkEntry`, `ExpenseEntry`, `CowEntry`
- **Kotlin Flow and StateFlow** keep the UI in sync with the database
- **DataStore (Preferences)** stores settings such as language, theme and onboarding state
- **WorkManager** schedules the daily reminder notification
- **PdfDocument and FileProvider** generate and share the monthly report

## 🛠️ Tech Stack

| Area | Tools |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| Local database | Room 2.6.1 (KSP) |
| Async | Coroutines, Flow |
| Storage | DataStore Preferences |
| Background work | WorkManager |
| Min / Target SDK | 24 / 34 |

## 📂 Project Structure

```
app/src/main/java/com/ksheera/sagaraaa/
├── data/          # Room entities, DAOs, AppDatabase, UserPreferences
├── repository/    # FarmRepository
├── viewmodel/     # FarmViewModel, AuthViewModel, ThemeViewModel
├── ui/theme/      # Compose screens and theme
├── utils/         # LanguageManager, AppPreferences
├── PdfExporter.kt
├── NotificationHelper.kt
└── MainActivity.kt
```

## 🚀 Getting Started

1. Clone the repo
   ```bash
   git clone https://github.com/ManishaNaik1111/KsheeraSagaraaa.git
   ```
2. Open it in **Android Studio** (Hedgehog or newer)
3. Let Gradle sync, then run on an emulator or a device with Android 7.0 (API 24) or higher

## 📸 Screenshots

_Screenshots coming soon._

## 🔮 Future Improvements

- Replace destructive database migrations with proper Room migrations
- Hash stored passwords or move sign-in to a secure backend
- Cloud backup and sync across devices
- AI-powered tips based on a farmer's own income and expense data

## 👩‍💻 Author

**Manisha Mahesh Naik**
GitHub: [@ManishaNaik1111](https://github.com/ManishaNaik1111) · [LinkedIn](https://linkedin.com/in/manisha-naik-83270a2a0)
