# SimpleDroidColorPicker ![icon](https://github.com/mata1/SimpleDroidColorPicker/blob/master/screenshots/icon.png)
**SimpleDroidColorPicker** is a simple, easy-to-use color picker library for Android.

Currently it is in pre-release and supports **ring**, **circle** and **linear** color pickers.

Current version **v0.3** supports:
* **ring** color picker, **circle** color picker, three **linear** color pickers
* **HSV** linear color picker
* changing colors by either: 
  * touching
  * dragging handle
  * calling `setColor(int)` method
* creating color picker alert dialog
* animations
* XML attributes
* onColorSelected and onColorChanged listeners
* attaching linear color pickers to ring and circle color picker (for hue/saturation)
* setters/getters

**v0.2**

![ColorPickers](https://github.com/mata1/SimpleDroidColorPicker/blob/master/screenshots/v0.2.png)

**v0.3**

![ColorPickers](https://github.com/mata1/SimpleDroidColorPicker/blob/master/screenshots/v0.3.png)

# How to use
Add this to your `build.gradle` file:

```Gradle
repositories {
  maven {
    url "https://jitpack.io"
  }
}

dependencies {
  compile 'com.github.mata1:SimpleDroidColorPicker:v0.3'
}
```

*TODO: write how to use views, ...* 

# Licence
Copyright 2015 Matej Biberović

Licenced under  [GNU GENERAL PUBLIC LICENSE](https://www.gnu.org/licenses/gpl-2.0.html)
