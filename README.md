# WordPress-Lint-Android
Pluggable lint module for WordPress-Android.

## Use the library in your project

* In your build.gradle:
```groovy
dependencies {
    compile 'org.wordpress:lint:1.0.2' // use version 1.0.2
}
```

## Publishing a new version

In the following cases, the CI will publish a new version with the following format to our remote Maven repo:

* For each commit in an open PR: `<PR-number>-<commit full SHA1>`
* Each time a PR is merged to `trunk`: `trunk-<commit full SHA1>`
* Each time a new tag is created: `{tag-name}`

## License ##

WordPress-Lint-Android is an Open Source project covered by the
[GNU General Public License version 2](LICENSE.md).

[1]: https://github.com/wordpress-mobile/WordPress-Lint-Android/blob/develop/WordPressLint/build.gradle#L15
