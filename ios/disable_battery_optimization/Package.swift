// swift-tools-version: 5.9
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "disable_battery_optimization",
    platforms: [
        .iOS("13.0")
    ],
    products: [
        .library(name: "disable-battery-optimization", targets: ["disable_battery_optimization"])
    ],
    dependencies: [
        .package(name: "FlutterFramework", path: "../FlutterFramework")
    ],
    targets: [
        .target(
            name: "disable_battery_optimization",
            dependencies: [
                .product(name: "FlutterFramework", package: "FlutterFramework")
            ],
            resources: []
        )
    ]
)
