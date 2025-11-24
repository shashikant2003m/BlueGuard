# 🌊 BlueGuard - AI-Powered Beach Safety App

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-MIT-orange.svg)](LICENSE)

BlueGuard is an intelligent Android application that provides comprehensive beach safety information, real-time condition analysis, and AI-powered recommendations for beach visitors across India. With coverage of 60+ beaches, BlueGuard helps you make informed decisions about beach activities while prioritizing safety.

## 📱 Screenshots

<div align="center">
  <img src="https://i.postimg.cc/mgBVgzR3/Screenshot-20251124-125136.png" width="200" alt="Splash Screen"/>
  <img src="https://i.postimg.cc/cLhhgx47/Screenshot-20251124-125207.png" width="200" alt="Phone Login"/>
  <img src="https://i.postimg.cc/T300LdYJ/Screenshot-20251124-125230.png" width="200" alt="OTP Verification"/>
  <img src="https://i.postimg.cc/Dw66JvyN/Screenshot-20251124-125306.png" width="200" alt="Registration"/>
  <img src="https://i.postimg.cc/9QbbRmFv/Screenshot-20251124-125318.png" width="200" alt="Dashboard"/>
  <img src="https://i.postimg.cc/tgg5FKYm/Screenshot-20251124-125326.png" width="200" alt="Beach Selection"/>
  <img src="https://i.postimg.cc/wBBVDC1K/Screenshot-20251124-125353.png" width="200" alt="AI Analysis"/>
  <img src="https://i.postimg.cc/xddRLwcw/Screenshot-20251124-125420.png" width="200" alt="Explore Beach"/>
  <img src="https://i.postimg.cc/4335pDYG/Screenshot-20251124-125509.png" width="200" alt="Beach Details"/>
  <img src="https://i.postimg.cc/1zzHDk85/Screenshot-20251124-125549.png" width="200" alt="Alerts & Warnings"/>
</div>

> **Note**: Add your app screenshots to the `screenshots/` folder

## ✨ Features

### 🎯 Core Features
- **AI-Powered Analysis**: Real-time beach condition assessment using Google Gemini AI
- **Safety Ratings**: Color-coded suitability levels (Safe 🟢 / Moderate 🟡 / Extreme 🔴)
- **Comprehensive Beach Info**: Detailed information about 60+ Indian beaches
- **Smart Recommendations**: Personalized suggestions based on current conditions
- **Beautiful UI**: Modern glassmorphism design with smooth animations

### 🏖️ Beach Information
- **Overview**: Detailed beach descriptions and tourist information
- **Highlights**: Top 5 attractions and unique features
- **Best Time to Visit**: Seasonal recommendations and weather insights
- **Safety Tips**: Swimming precautions and emergency guidelines
- **Nearby Attractions**: Local tourist spots with distance information

### 🔔 Safety Features
- Real-time alerts and warnings
- Water quality monitoring
- Toughness level indicators (1-5 stars)
- Weather-based forecasts
- Emergency contact information

### 🌏 Beach Coverage
**60+ Beaches Across India:**
- **Goa**: Baga, Calangute, Anjuna, Vagator, Palolem, Miramar
- **Maharashtra**: Juhu, Girgaum Chowpatty, Aksa, Kashid, Ganpatipule, Tarkarli
- **Kerala**: Kovalam, Varkala, Marari, Cherai, Bekal
- **Tamil Nadu**: Marina, Elliot's, Mahabalipuram, Kanyakumari, Dhanushkodi
- **Andaman & Nicobar**: Radhanagar, Elephant Beach, Vijaynagar, Corbyn's Cove
- **Lakshadweep**: Agatti, Bangaram, Kadmat, Minicoy
- **Odisha**: Puri, Chandrabhaga, Gopalpur
- **West Bengal**: Digha, Mandarmani, Shankarpur
- **Karnataka**: Gokarna, Murudeshwar, Karwar

## 🛠️ Technology Stack

### Development
- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Minimum SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34 (Android 14)

### Libraries & Dependencies
```gradle
dependencies {
    // Core Android
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // Material Design
    implementation 'com.google.android.material:material:1.11.0'
    
    // Firebase
    implementation 'com.google.firebase:firebase-auth:22.3.1'
    implementation 'com.google.firebase:firebase-firestore:24.10.1'
    
    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    
    // JSON Parsing
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // Animation
    implementation 'com.airbnb.android:lottie:6.0.0'
    
    // CardView
    implementation 'androidx.cardview:cardview:1.0.0'
}
```

### AI Integration
- **Provider**: Google Gemini via OpenRouter API
- **Model**: Claude Sonnet 4
- **API**: RESTful API with JSON responses

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 11 or higher
- Android SDK 21+
- Firebase account (for authentication)
- OpenRouter/Gemini API key

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/shashikant2003m/BlueGuard
cd blueguard
```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Configure Firebase**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Download `google-services.json`
   - Place it in the `app/` directory
   - Enable Firebase Authentication (Phone Auth)

4. **Add API Key**
   - Create a `local.properties` file in the root directory (if not exists)
   - Add your API key:
   ```properties
   GEMINI_API_KEY=your_api_key_here
   ```

5. **Configure BuildConfig**
   - In `build.gradle (app)`, the API key is already configured:
   ```gradle
   android {
       defaultConfig {
           buildConfigField "String", "GEMINI_API_KEY", "\"${project.property('GEMINI_API_KEY')}\""
       }
   }
   ```

6. **Sync and Build**
   - Click "Sync Project with Gradle Files"
   - Build and run the app on your device/emulator

### Project Structure
```
blueguard/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/blueguard/
│   │   │   │   ├── DashboardActivity.kt
│   │   │   │   ├── ExploreBeachActivity.kt
│   │   │   │   ├── AlertsWarningsActivity.kt
│   │   │   │   ├── WaterQualityActivity.kt
│   │   │   │   ├── PhoneLoginActivity.kt
│   │   │   │   ├── OtpVerificationActivity.kt
│   │   │   │   ├── SplashActivity.kt
│   │   │   │   └── data/
│   │   │   │       ├── OpenRouterApi.kt
│   │   │   │       ├── OpenRouterClient.kt
│   │   │   │       └── OpenRouterModels.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   ├── drawable/
│   │   │   │   ├── values/
│   │   │   │   └── ...
│   │   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── google-services.json
├── gradle/
├── build.gradle
├── settings.gradle
├── local.properties (gitignored)
└── README.md
```

## 📖 Usage Guide

### 1. User Authentication
- Launch the app
- Enter your phone number
- You can use 
Phone number	Verification code	
### +91 98765 43210	And Default OTP: 000000
- Complete registration with your name

### 2. Beach Selection
- On the dashboard, click on the beach dropdown
- Search or scroll through 60+ beaches
- Select your desired beach location

### 3. View Safety Analysis
- AI automatically analyzes the selected beach
- View suitability rating, conditions, and toughness level
- Check the forecast for planning

### 4. Explore Beach Details
- Tap "Explore Beach" card
- Read comprehensive information about:
  - Beach overview
  - Highlights and attractions
  - Best visiting times
  - Safety tips
  - Nearby places

### 5. Check Alerts & Water Quality
- Tap respective cards for detailed information
- Stay updated with real-time alerts
- Monitor water quality status

## 🎨 Design Philosophy

### UI/UX Principles
- **Glassmorphism**: Modern semi-transparent card designs
- **Gradient Backgrounds**: Calming ocean-themed gradients
- **Color Coding**: Intuitive safety level indicators
- **Smooth Animations**: Lottie animations for loading states
- **Responsive Design**: Works on all screen sizes

## 🔐 Security & Privacy

- **API Key Protection**: Keys stored in local.properties (gitignored)
- **Firebase Authentication**: Secure phone number verification
- **Local Storage**: User data stored locally using SharedPreferences
- **No Data Collection**: No personal data sent to external servers
- **Secure Communication**: HTTPS for all API calls

## 🤝 Contributing

We welcome contributions! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/AmazingFeature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add some AmazingFeature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/AmazingFeature
   ```
5. **Open a Pull Request**

### Contribution Guidelines
- Follow Kotlin coding conventions
- Write clear commit messages
- Add comments for complex logic
- Test thoroughly before submitting
- Update documentation as needed

## 🐛 Bug Reports & Feature Requests

Found a bug or have a feature idea? Please:
1. Check if it's already reported in [Issues](https://github.com/shashikant2003m/BlueGuard/issues)
2. If not, create a new issue with:
   - Clear description
   - Steps to reproduce (for bugs)
   - Expected vs actual behavior
   - Screenshots (if applicable)
   - Device and Android version

## 📋 Roadmap

### Version 1.1 (Planned)
- [ ] Offline mode with cached data
- [ ] Push notifications for alerts
- [ ] User favorites and bookmarks
- [ ] Multi-language support (Hindi, Tamil, Malayalam)
- [ ] Dark mode

### Version 1.2 (Future)
- [ ] Real-time weather API integration
- [ ] Social sharing features
- [ ] User reviews and ratings
- [ ] Interactive maps with Google Maps
- [ ] Emergency SOS button

### Version 2.0 (Long-term)
- [ ] AR beach navigation
- [ ] Community forum
- [ ] Tide prediction algorithms
- [ ] Wildlife spotting tracker
- [ ] Beach cleanliness reports

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2025 BlueGuard Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 🙏 Acknowledgments

- [Google Gemini AI](https://deepmind.google/technologies/gemini/) for AI-powered analysis
- [OpenRouter](https://openrouter.ai/) for API integration
- [Lottie](https://airbnb.design/lottie/) for beautiful animations
- [Material Design](https://material.io/) for design guidelines
- [Firebase](https://firebase.google.com/) for authentication services
- All open-source contributors

## ⭐ Show Your Support

If you like this project, please give it a ⭐ on GitHub!

---

<div align="center">
  <p>Made with ❤️ for Beach Lovers</p>
  <p>🌊 Stay Safe, Enjoy the Waves 🌊</p>
</div>
