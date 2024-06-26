
# dex-diff

![latestVersion](https://img.shields.io/github/v/release/theapache64/dex-diff)
<a href="https://twitter.com/theapache64" target="_blank">
<img alt="Twitter: theapache64" src="https://img.shields.io/twitter/follow/theapache64.svg?style=social" />
</a>

> A tool to compare two APK files at the dex level. Useful for checking the impact of things like [fullMode](https://r8.googlesource.com/r8/+/refs/heads/master/compatibility-faq.md#r8-full-mode) and [dex optimisations](https://developer.android.com/topic/performance/baselineprofiles/dex-layout-optimizations).

### ⌨️ Install
```bash
sudo npm install -g dex-diff
```

### ✨ Usage
```bash
dex-diff before.apk after.apk [com.my.app.packageName]
```

### 🤖 Example

```bash
❯ ls
with-fullmode.apk    without-fullmode.apk

❯ dex-diff without-fullmode.apk with-fullmode.apk com.example.flowobjectrepro
⚔️ dex-diff v0.0.7
🚀 Initialising...
➡️ Deleting old results (dex-diff-result)...
✅ Deleted old results
➡️ Decompiling before APK... (this may take some time)
✅ Decompiling before APK finished
➡️ Decompiling after APK... (this may take some time)
✅ Decompiling after APK finished
✅ Decompile finished (6645ms)
➡️ Comparing before and after... (this may take some time)
✅ Comparing finished (2030ms)
➡️ Making report...
✅ Report ready (10.66s) -> file:///../dex-diff-result/report.html 

```

### 💻 Output

**Summary**
![image](https://github.com/theapache64/dex-diff/assets/9678279/4af3027b-8d26-42c2-ab0b-96789d05f059)

**App files' addition, deletion, and removals**
![image](https://github.com/theapache64/dex-diff/assets/9678279/e9e6b466-d599-4833-8fd6-1b30d9299c91)

**Libary files' addition, deletion, and removals**
![image](https://github.com/theapache64/dex-diff/assets/9678279/de89e79a-191b-4cec-9901-2160b0c893f5)

**Framework files' addition, deletion, and removals**
![image](https://github.com/theapache64/dex-diff/assets/9678279/f3dbd2bf-645b-451c-b4bd-063db3e86b89)

**Change view**
![image](https://github.com/theapache64/dex-diff/assets/9678279/b31f1e76-6a1f-4932-b3df-1a0fb2321512)


## ✍️ Author

👤 **theapache64**

* Twitter: <a href="https://twitter.com/theapache64" target="_blank">@theapache64</a>
* Email: theapache64@gmail.com

Feel free to ping me 😉

## 🤝 Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any
contributions you make are **greatly appreciated**.

1. Open an issue first to discuss what you would like to change.
1. Fork the Project
1. Create your feature branch (`git checkout -b feature/amazing-feature`)
1. Commit your changes (`git commit -m 'Add some amazing feature'`)
1. Push to the branch (`git push origin feature/amazing-feature`)
1. Open a pull request

Please make sure to update tests as appropriate.

## ❤ Show your support

Give a ⭐️ if this project helped you!

<a href="https://www.patreon.com/theapache64">
  <img alt="Patron Link" src="https://c5.patreon.com/external/logo/become_a_patron_button@2x.png" width="160"/>
</a>

<a href="https://www.buymeacoffee.com/theapache64" target="_blank">
    <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" width="160">
</a>


## 📝 License

```
Copyright © 2024 - theapache64

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

_This README was generated by [readgen](https://github.com/theapache64/readgen)_ ❤
