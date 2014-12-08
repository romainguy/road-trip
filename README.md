RoadTrip
========

Copyright 2013 Romain Guy
[http://www.curious-creature.org](http://www.curious-creature.org)

RoadTrip is a sample application for Android that demonstrates how to implement several visual effects:

* Animated paths tracing
* Black & white to color conversion, used when scrolling a row of image to the left (the first image starts in B&W and turns into colors)
* Pinned scrolling, used to create a “stacked cards” effect when scrolling up and down the list
* Parallax scrolling, used to scroll the various maps at a different speed than other items
* Animated action bar opacity, as seen in Google Music

![RoadTrip running on Android 4.4](art/RoadTrip.png)

More [detailed explanations](http://www.curious-creature.org/2013/12/21/android-recipe-4-path-tracing) can be found online.

Watch a [video of the application](http://www.youtube.com/watch?v=-NxG3BE9QCg) running on a Moto X/Android 4.4.

How to use this source code
===========================

The road-trip project can be opened in Android Studio 1.0 or later. It contains a single module
called **application** in the `app/` folder.

The project can be compiled from the command line using Gradle.

The actual source code and resources can be found in `app/src/main/`. The only dependency is in `app/lib/`.

Source code license
===================

This project is subject to the [Apache License, Version 2.0](http://apache.org/licenses/LICENSE-2.0.html).

Artwork licenses
================

The file __map\_usa.svg__ is derived from [an original work by Theshibboleth](http://commons.wikimedia.org/wiki/File:Blank_US_Map.svg).
This file is licensed under [GNU Free Documentation License, Version 1.2](http://en.wikipedia.org/wiki/GNU_Free_Documentation_License) and [Creative Commons Attribution-Share Alike 3.0 Unported](http://creativecommons.org/licenses/by-sa/3.0/deed.en).

Other __map\_*.svg__ files are derived from _map_usa.svg_ and subject to the same licenses.

The __ic\_launcher*.png__ files are derived from _map_usa.svg_ and subject to the same licenses.

All other images (__.png__ or __.jpg__) are copyright Romain Guy and licensed under [Creative Commons Attribution-Noncommercial-Share Alike 2.0](http://creativecommons.org/licenses/by-nc-sa/2.0/deed.en).

Library licenses
================

__androidsvg-1.2.0__ is subject to the [Apache License, Version 2.0](http://apache.org/licenses/LICENSE-2.0.html).
More information on [the official web site](https://code.google.com/p/androidsvg/).
