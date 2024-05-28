# Gemini Android Client

This project is a third-party Android client for Gemini, based on API Token authentication. The
client does not rely on official library Google provides, but a private server instead. It is
implemented using Jetpack Compose, a
modern toolkit for building native Android UI. The UI
widgets are built using the Google Material3 library.

## Current Progress

The following features have been implemented:

- Main interface and settings interface UI framework.
- Login and registration page navigation and UI construction.
- An example of handling network requests in a Jetpack Compose project.

## Future Work

The next steps for this project are:

- Connect with the server API.
- Establish a complete front-end and back-end communication.

## Getting Started

- This client does not use official library Google prepared for Android.
    - For reason: Gemini does not offer service in my region, so I have to use a private server to
      access the API.
- You need a server to use this client. The server is not provided in this project.
    - [My server](https://github.com/wxxsfxyzm/API_Backend) has been working in progress
- For a server implementation, please refer to
  the [Document](https://ai.google.dev/gemini-api/docs/get-started/tutorial?hl=zh-cn&lang=python)

## Built With

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - The modern toolkit for building
  native Android UI.
- [Google Material3](https://m3.material.io/) - Material Design 3 components for Android.

## License

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.

## Authors

- [wxxsfxyzm](https://github.com/wxxsfxyzm) - Initial work

## Acknowledgments

- Thanks to the open-source community for providing such great tools and libraries.
- Thanks to the Gemini team for providing the API.
