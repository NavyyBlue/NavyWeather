import SwiftUI
import Shared

@main
struct NavyWeatherApp: App {

    init() {
        // Koin must start once, before any Compose screen asks for a ViewModel.
        MainViewControllerKt.startKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
