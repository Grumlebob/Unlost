# Unlost

A historical Java/JavaFX desktop application for drawing OpenStreetMap data,
searching addresses and planning routes. This team project was made for the
First Year Project course at the IT University of Copenhagen in spring 2022.

This repository imports the recovered working files, including their existing
local edits. The original school Git history is not included. The application
has not been rebuilt or tested as part of this import.

## Running the application

The checked-in wrapper uses Gradle 7.3.3; the application declares JavaFX 18.
Use a compatible JDK, such as JDK 17. From the repository root:

```powershell
.\gradlew.bat :app:run
```

On Linux or macOS, use `bash gradlew :app:run`. The existing Gradle configuration
requests substantial JVM heap sizes, including a 32 GiB maximum for JavaExec.
Adjust those settings for your machine if necessary.

## Map data

Large regional maps, ZIP archives and generated `.map` caches are excluded from
Git and remain local under `app/data/`. The historical `app/data/small.osm`
fixture (approximately 9.5 MiB) is retained because the existing spatial and
pathfinding tests depend on this exact extract.

To use another region:

1. Download an **OSM XML** extract from [BBBike](https://download.bbbike.org/)
   or export a small area from [OpenStreetMap](https://www.openstreetmap.org/export).
2. Decompress `.osm.gz` or `.osm.bz2` downloads to a plain `.osm` file.
   The parser accepts `.osm`, a ZIP containing an `.osm` or application `.map`
   file, or an application-generated `.map` cache. It does not read `.pbf`.
3. Select the file with the application's file picker. The predefined Bornholm
   and Fyn buttons expect `app/data/bornholm.zip` and `app/data/fyn.zip` when
   launched through Gradle; these archives are not bundled.

Replacement downloads can run the application but do not replace the exact
historical fixture used by the existing tests.

Map data: © OpenStreetMap contributors, distributed under the
[Open Database License](https://www.openstreetmap.org/copyright). Source comments
retain attribution for referenced algorithms and third-party visual assets.
