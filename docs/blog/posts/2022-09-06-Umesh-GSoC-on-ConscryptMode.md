---
date: 2022-09-06
authors:
  - Umesh-01
slug: Umesh-GSoC-on-ConscryptMode
---

# GSoC 2022 - ConscryptMode

My name is [Umesh Singh](https://github.com/Umesh-01) and I was an open source contributor through Google Summer of Code this year. Google Summer of Code (GSoC) is a program where external participants can contribute to an open source project over a few months. We learn new computer science concepts, how to work on open source repositories, and create real code contributions to projects!

<!-- more -->

My project was `Switching Robolectric from BouncyCastle to Conscrypt as the default security provider`. Robolectric was using [BouncyCastle](https://www.bouncycastle.org/) as the Java Cryptography Extension (JCE) security provider. After the introduction of Android P, Android switched to using [Google Conscrypt](https://source.android.com/docs/core/architecture/modular-system/conscrypt) as the security provider. 
To be more consistent with Android, Robolectric needed to be updated to use Conscrypt as the default security provider.
 
When I encountered Robolectric in the GSoC organization list I know it would be a great fit. I had prior experience with Android development and was looking for growth opportunities in this area. Robolectric also offered a lot of learning opportunities in Java, security, and unit testing concepts. 

The initial approach for this project was to drop BouncyCastle entirely and switch to Conscrypt. This seemed appealing due to the prior issues with BouncyCastle, such as [#5456](https://github.com/robolectric/robolectric/issues/5456). However, we discovered that we couldn't completely switch Robolectric to Conscrypt. Conscrypt only supports the security primitives provided by BoringSSL, and there were some legacy security primitives that were still being used in Android. Because of this we realized that BouncyCastle was needed as the fallback security provider. We settled on having a `@ConscryptMode` annotation that lets users choose whether Conscrypt will be installed or not. When Conscrypt is enabled, Robolectric will search for a requested security feature from Conscrypt first. If it does not support it, the other security providers will be queried. This is more consistent with how Android does it.

- If ConscryptMode is `ON`, it will install Conscrypt and BouncyCastle.
- If ConscryptMode is `OFF`, it will only install BouncyCastle.

I have learned a lot about unit testing, such as how to test whether a piece of code is working according to the expected behavior, and about writing tests for specific scenarios. I have also learned about providing self-explanatory names to methods so that other developers can understand them easily in the future.

I gained experience debugging a large, complex codebase like Robolectric, and how to find the root cause of errors and test case failures. I learned about defining new Java runtime annotations. These get compiled into the bytecode and can later be inspected in other parts of the project. 

I have also learned about the tradeoffs that sometimes arise in software projects. For example, Conscrypt does not currently support Mac M1, and to some extent this is out of our control. We were aware of this issue yet decided to move forward with making Conscrypt the default security provider on Linux, Windows, and Mac x86_64. The increased fidelity for these environments outweighs the lack of M1 support. We also have opportunities to collaborate with Conscrypt for Mac M1 support, or explore alternatives like repackaging Conscrypt for Robolectric until Conscrypt supports M1.

You can see my changes here in this [pull request](https://github.com/robolectric/robolectric/pull/7492) and here is a short [documentation](https://github.com/robolectric/robolectric.github.io/pull/122) about it.
