//
//  FeedListScreen.swift
//  FeedFlow
//
//  Created by Marco Gomiero on 01/04/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
import KMPNativeCoroutinesAsync

struct FeedsScreen: View {
    
    @EnvironmentObject var appState: AppState
    @Environment(\.presentationMode) var presentationMode
    @StateObject var feedSourceViewModel: FeedSourceListViewModel = KotlinDependencies.shared.getFeedSourceListViewModel()
    
    @State private var showAddFeed = false
    @State var feedState: [FeedSource] = []
    
    var body: some View {
        NavigationStack {
            VStack {
                if (feedState.isEmpty) {
                    VStack {
                        Text("No feeds found. Please add a new feed!")
                            .font(.body)
                        
                        NavigationLink(value: SheetPage.addFeed) {
                            Text("Add feed")
                        }
                    }
                } else {
                    List {
                        ForEach(feedState, id: \.self.id) { feedSource in
                            
                            VStack(alignment: .leading) {
                                Text(feedSource.title)
                                    .font(.system(size: 16))
                                    .padding(.top, Spacing.xsmall)
                                
                                
                                Text(feedSource.url)
                                    .font(.system(size: 14))
                                    .padding(.top, Spacing.xxsmall)
                                    .padding(.bottom, Spacing.xsmall)
                                
                            }
                            .id(feedSource.id)
                            .contextMenu {
                                Button {
                                    feedSourceViewModel.deleteFeedSource(feedSource: feedSource)
                                } label: {
                                    Label("Delete", systemImage: "trash")
                                }
                            }
                            .onTapGesture {
                                // TODO: edit feed
                            }
                        }
                    }
                    .listStyle(PlainListStyle())
                }
            }
            .navigationDestination(for: SheetPage.self) { page in
                switch page {
                case .addFeed:
                    AddFeedScreen()
                }
            }
            .toolbar {
                
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(
                        action: {
                            self.presentationMode.wrappedValue.dismiss()
                        }
                    ) {
                        Image(systemName: "xmark")
                    }
                }
                
                ToolbarItem(placement: .navigationBarLeading) {
                    Text("Feeds")
                        .font(.title2)
                        .padding(.vertical, Spacing.medium)
                }
                
                ToolbarItem(placement: .primaryAction) {
                    NavigationLink(value: SheetPage.addFeed) {
                        Image(systemName: "plus")
                    }
                }
            }
        }
        .sheet(isPresented: $showAddFeed) {
            AddFeedScreen()
        }.task {
            do {
                let stream = asyncSequence(for: feedSourceViewModel.feedsStateFlow)
                for try await state in stream {
                    self.feedState = state
                }
            } catch {
                self.appState.snackbarQueue.append(
                    SnackbarData(
                        title: "Sorry, something went wrong :(",
                        subtitle: nil,
                        showBanner: true
                    )
                )
            }
            //            do {
            //                let stream = asyncSequence(for: settingsViewModel.isImportDoneStateFlow)
            //                for try await isImportDone in stream {
            //                    if isImportDone as! Bool {
            //                        self.appState.snackbarQueue.append(
            //                            SnackbarData(
            //                                title: "Import done",
            //                                subtitle: nil,
            //                                showBanner: true
            //                            )
            //                        )
            //                        presentationMode.wrappedValue.dismiss()
            //                    }
            //                }
            //            } catch {
            //                self.appState.snackbarQueue.append(
            //                    SnackbarData(
            //                        title: "Sorry, something went wrong :(",
            //                        subtitle: nil,
            //                        showBanner: true
            //                    )
            //                )
            //            }
        }
    }
}

struct SettingsScreen_Previews: PreviewProvider {
    static var previews: some View {
        FeedsScreen()
    }
}
