# 🐄 KsheeraSagara – Dairy Farm Management App

KsheeraSagara is an Android application developed to help small-scale dairy farmers manage their daily dairy operations, track milk income and expenses, monitor cattle records, and calculate monthly profit or loss.

The application provides an organized way to maintain dairy-related financial records through a simple and user-friendly interface. It supports offline data storage and multiple languages.

## ✨ Key Features

- **Milk Income Tracking:** Record milk quantity, fat percentage, and rate per litre with automatic income calculation.
- **Expense Management:** Track daily expenses across categories such as fodder, medical, labour, electricity, and others.
- **Cattle Management:** Add and manage cow details, including name, breed, and active status.
- **Profit & Loss Calculation:** View monthly income, expenses, net profit or loss, and milk sales summary.
- **Monthly Reports:** Generate PDF reports and share them through WhatsApp or other applications.
- **Milk Calendar:** Maintain daily milk records and view milk-related activities.
- **Vaccination Tracking:** Organize cattle vaccination schedules.
- **Daily Reminders:** Receive notifications for daily dairy-related activities.
- **Multilingual Support:** Supports English, Kannada, Hindi, and Marathi.
- **Dark Mode:** Includes a dark theme for a comfortable viewing experience.
- **Offline Support:** Store and access records locally without requiring an internet connection.

## 🛠️ Technology Stack

| Category | Technologies |
|---|---|
| Programming Language | Kotlin |
| UI Development | Jetpack Compose |
| UI Components | Material 3 |
| Architecture | MVVM |
| Local Database | Room Database (SQLite) |
| Asynchronous Operations | Kotlin Coroutines, Flow |
| Navigation | Navigation Compose |
| Preferences | DataStore |
| Background Tasks | WorkManager |
| PDF Generation | Android PdfDocument |
| Development Tool | Android Studio |

## 🏗️ Application Architecture

The application follows the Model-View-ViewModel (MVVM) architecture with a repository layer.

```text
Compose UI Screens
       |
       v
    ViewModel
       |
       v
 Farm Repository
       |
       v
 Room Database
       |
       v
    SQLite
```

**Architecture components:**
- **UI Layer:** Jetpack Compose screens for user interaction.
- **ViewModel Layer:** Manages UI state and application logic.
- **Repository Layer:** Handles data operations.
- **Data Layer:** Room entities and DAOs manage local database operations.
- **DataStore:** Stores user preferences such as language and theme settings.
- **WorkManager:** Schedules background reminders.

## 📂 Project Structure

```text
app/src/main/java/com/ksheera/sagaraaa/
│
├── data/
│   ├── entities
│   ├── dao
│   └── AppDatabase
│
├── repository/
│   └── FarmRepository
│
├── viewmodel/
│   ├── FarmViewModel
│   ├── AuthViewModel
│   └── ThemeViewModel
│
├── ui/
│   └── theme/
│
├── utils/
│
├── PdfExporter.kt
├── NotificationHelper.kt
└── MainActivity.kt
```

## 🚀 Getting Started

### Prerequisites
- Android Studio (Hedgehog or newer)
- Android SDK
- Android device or emulator running Android 7.0 (API 24) or above

### Installation

1. Clone the repository:

```bash
git clone https://github.com/ManishaNaik1111/KsheeraSagaraaa.git
```

2. Open the project in Android Studio.
3. Allow Gradle to sync the project.
4. Run the application on an Android emulator or physical device.

## 🔮 Future Enhancements

- Cloud backup and data synchronization.
- Improved database migration support.
- Enhanced account security.
- Personalized financial insights for dairy farmers.

## 👩‍💻 Developer

**Manisha Mahesh Naik**

- GitHub: [ManishaNaik1111](https://github.com/ManishaNaik1111)

---

*Developed as an individual Android application project.*
