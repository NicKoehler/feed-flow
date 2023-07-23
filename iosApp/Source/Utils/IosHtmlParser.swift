//
//  IosHtmlParser.swift
//  FeedFlow
//
//  Created by Marco Gomiero on 01/04/23.
//  Copyright © 2023 FeedFlow. All rights reserved.
//

import Foundation
import shared
import SwiftSoup

class IosHtmlParser: HtmlParser {

    func getTextFromHTML(html: String) -> String? {
        do {
            let doc: Document = try SwiftSoup.parse(html)
            return try doc.text()
        } catch {
            KotlinDependencies.shared.getLogger(tag: "IosHtmlParser").e(messageString: "Error during html parsing: \(error)")
            return nil
        }
    }
}
