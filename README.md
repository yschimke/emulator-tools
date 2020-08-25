# emulator-tools
Android Emulator Tools

See individual proto files for appropriate licenses

Borrowed from 

https://github.com/aosp-mirror/platform_external_qemu/tree/58add8934660d8c8984d76b40a6b01c08dfbab61/android/android-grpc
https://github.com/aosp-mirror/platform_external_qemu/tree/58add8934660d8c8984d76b40a6b01c08dfbab61/android/android-webrtc/android-webrtc

See licenses in individual proto files

otherwise any test code is available under this license

# Run headless emulator

```
$ emulator -no-window @pixel5 -grpc 8554
```

# Running from checkout

```
$ ./emulator-tools --screenshot
```

![image](https://user-images.githubusercontent.com/231923/79718336-b84d3780-82d3-11ea-9f40-4f48b0ccc8bf.png)

