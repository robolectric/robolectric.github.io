---
date: 2017-11-13
authors:
  - xian
hide:
  - toc
slug: resources-for-real
---

# Keepin' It Real With Resources

Up until now, Robolectric has relied on rough approximations of the Android framework's resource handling logic, backwards-engineered to be 'good enough' for many testing purposes.

<!-- more -->

We've gotten to the point where 'good enough' is no longer good enough. We've completely reimplemented Robolectric's resources system, transliterating the real native Android resource handling code from the original C++ to Java, and in the coming several releases we'll be rolling it out. The new code will fix numerous bugs in the old Robolectric simulation, which we expect will expose places where your tests rely on incorrect Robolectric behavior.

We're doing this in three separate releases to give you a chance to fix your tests in smaller batches. We strongly recommend you apply each upgrade individually and watch the release notes for migration hints.

The releases will be:

**3.6:** Resource selection based on transliterated framework code. Configuration properties set from `@Config(qualifiers=...)`.

**3.7:** Manifest parsing based on transliterated framework code.

**3.8:** Resource loading from `aapt`-processed binary resource files.

We'll keep the changes in each release focused and narrow, and provide lots of notes on migration. Stick with us through this, we think you'll be happy with the improvements.

Going forward, we're applying a higher standard for accuracy in Robolectric's implementations of native code. We'll keep you posted as we revamp more parts of Robolectric's framework simulation.

As always, thanks for your pull requests, bug reports, ideas and questions! &#x1f4af;

_Your Robolectric maintainers,_
<br/>
[jongerrish@google.com](mailto:jongerrish@google.com), [brettchabot@google.com](mailto:brettchabot@google.com), and [christianw@google.com](mailto:christianw@google.com).