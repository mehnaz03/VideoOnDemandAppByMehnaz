# 🎬 Video On Demand App

A Kotlin-based Android application that allows users to browse and search for movies using the [OMDb API](https://www.omdbapi.com/).  
Built with **MVVM architecture**, **Retrofit**, **Hilt Dependency Injection**, and **Jetpack Components**.

---

## 🚀 Features
- 🔍 Search movies by title
- 📜 View detailed movie information
- 🖼 Display movie posters with placeholder support
- 🧭 Bottom Navigation with multiple fragments
- 🌙 Modern UI with Poppins font
- 📡 API integration using Retrofit & Gson
- 🔐 Dependency injection using Hilt
- 🐛 Logging with OkHttp Logging Interceptor

---

## 🛠 Tech Stack
- **Language**: Kotlin  
- **Architecture**: MVVM  
- **Network**: Retrofit, Gson, OkHttp Logging Interceptor  
- **Dependency Injection**: Hilt  
- **UI Components**: RecyclerView, ViewPager2, Material Components  
- **Image Loading**: Glide  

---

## 📂 Project Structure
app/
├── data/
│ ├── remote/ # API service and network layer
│ ├── di/ # Hilt modules
├── ui/
│ ├── home/ # Homepage
│ ├── movielist/ # Movie list screen
│ ├── moviedetails/ # Movie details screen
├── utils/ # Utility classes and extensions

## ⚙️ Setup
1. Clone the repository:

   git clone https://github.com/mehnaz03/VideoOnDemandAppByMehnaz.git

2.Open the project in Android Studio.

3.Add your OMDb API key in local.properties:

  OMDB_API_KEY=your_api_key_here
  
4.Sync the project and run it. here are some images
<img width="134" height="299" alt="home" src="https://github.com/user-attachments/assets/639d46b8-af47-4149-a43b-0d885ac6c67f" />
<img width="134" height="299" alt="details" src="https://github.com/user-attachments/assets/1fe0cf96-7624-426e-ab90-670234cbb72d" />
<img width="134" height="299" alt="movielist" src="https://github.com/user-attachments/assets/0c553574-b13b-4921-99c9-289977e1c952" />



