import SwiftUI

struct ContentView: View {
    @StateObject private var coordinator = LivenessCoordinator()
    @State private var mobileToken = ""
    @State private var personId = ""

    private var canStart: Bool {
        !mobileToken.trimmingCharacters(in: .whitespaces).isEmpty &&
        !personId.trimmingCharacters(in: .whitespaces).isEmpty
    }

    var body: some View {
        NavigationView {
            VStack(spacing: 16) {
                credentialsCard
                logCard
            }
            .padding()
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
            .background(Color(.systemGroupedBackground))
            .navigationTitle("FaceLiveness Lite")
        }
        .navigationViewStyle(.stack)
    }

    private var credentialsCard: some View {
        VStack(spacing: 12) {
            field("Mobile Token", text: $mobileToken)
            field("Person ID", text: $personId)

            Toggle("Enable Security", isOn: $coordinator.enableSecurity)
                .padding(.vertical, 2)

            Button {
                coordinator.start(
                    mobileToken: mobileToken.trimmingCharacters(in: .whitespaces),
                    personId: personId.trimmingCharacters(in: .whitespaces)
                )
            } label: {
                Text("Start Liveness")
                    .fontWeight(.semibold)
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .controlSize(.large)
            .disabled(!canStart)
        }
        .padding()
        .background(
            Color(.secondarySystemGroupedBackground),
            in: RoundedRectangle(cornerRadius: 16)
        )
    }

    private func field(_ title: String, text: Binding<String>) -> some View {
        TextField(title, text: text)
            .autocorrectionDisabled()
            .textInputAutocapitalization(.never)
            .padding(12)
            .background(
                Color(.systemGroupedBackground),
                in: RoundedRectangle(cornerRadius: 10)
            )
    }

    private var logCard: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack {
                Text("Log")
                    .font(.headline)
                    .foregroundStyle(.secondary)
                Spacer()
                if !coordinator.logs.isEmpty {
                    Button("Clear") { coordinator.clearLogs() }
                        .font(.caption)
                }
            }
            .padding([.horizontal, .top])
            .padding(.bottom, 8)

            if coordinator.logs.isEmpty {
                Spacer()
                Text("No activity yet")
                    .font(.callout)
                    .foregroundStyle(.tertiary)
                    .frame(maxWidth: .infinity)
                Spacer()
            } else {
                ScrollViewReader { proxy in
                    ScrollView {
                        LazyVStack(alignment: .leading, spacing: 4) {
                            ForEach(coordinator.logs) { entry in
                                Text(entry.message)
                                    .font(.system(.caption, design: .monospaced))
                                    .foregroundStyle(entry.kind.color)
                                    .textSelection(.enabled)
                                    .frame(maxWidth: .infinity, alignment: .leading)
                                    .padding(.horizontal)
                                    .id(entry.id)
                            }
                        }
                        .padding(.bottom, 8)
                    }
                    .onChange(of: coordinator.logs.count) { _ in
                        if let last = coordinator.logs.last {
                            withAnimation { proxy.scrollTo(last.id, anchor: .bottom) }
                        }
                    }
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(
            Color(.secondarySystemGroupedBackground),
            in: RoundedRectangle(cornerRadius: 16)
        )
    }
}

private extension LogEntry.Kind {
    var color: Color {
        switch self {
        case .success: return .green
        case .failure: return .red
        case .info:    return .primary
        }
    }
}

#Preview {
    ContentView()
}
