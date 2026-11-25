import SwiftUI
import UIKit
import Shared

/// Hosts the shared Compose Multiplatform UI inside SwiftUI.
struct ComposeView: UIViewControllerRepresentable {

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Compose owns its view hierarchy.
    }
}

struct ContentView: View {
    var body: some View {
        // Compose draws edge-to-edge and handles the safe area and keyboard insets itself.
        ComposeView()
            .ignoresSafeArea()
    }
}
