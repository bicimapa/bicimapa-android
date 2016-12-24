Colazo
======

Android aplication for cyclists in Bogotá


How to contribute
=================

First clone the repository:

```bash
git clone https://github.com/ylecuyer/colazo.git
```

Then open `eclipse` and `File > Import... > Android > Existing Android Code Into Workspace` choose the newly cloned repository and press `Finish`.

Import the compat library `File > Import... > Android > Existing Android Code Into Workspace` choosse `<android-sdk>/extras/android/support/v7/appcompat`.

Download [Google Play Services, revision 12.7](http://www.mediafire.com/download/d96we7iqt9pi308/google_play_services_12.7z).

Import the Google play services library `File > Import... > Android > Existing Android Code Into Workspace` choose `<path-to-downloaded-file>/google_play_services/libproject/google-play-services_lib`.

Git clone [Android Sliding Up Panel](https://github.com/umano/AndroidSlidingUpPanel).

Import the project library `File > Import... > Android > Existing Android Code Into Workspace` choose `<path-to-cloned-project>/libary`.

Now link all libraries to the project `Project > Properties > Android > Add`.
