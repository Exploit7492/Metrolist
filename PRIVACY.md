# Privacy Policy — Metrolist

Last updated: 2026-10-24

## Summary

Metrolist is a music client. It has no account system of its own, no analytics or
telemetry SDK, no crash-reporting service, and no advertising. Nothing is
uploaded to a server operated by this project. Your library, playlists, history,
and downloads are stored on the device and leave it only when you use a feature
that talks to a service you chose.

## What the app stores on your device

- Playlists, liked songs, listening history, and queue state.
- Settings and preferences.
- Downloaded audio and cached artwork or metadata.
- Optional API keys you enter for optional features (for example an AI provider
  key or a DeepL key for translation).

## What is sent, and only when you use those features

- Music, artwork, and metadata requests go to the service serving the content
  (for example YouTube Music, Spotify, Deezer, or Apple Music).
- Optional song recognition sends a short audio sample to the recognition
  service (for example Shazam) when you tap the recognise button.
- Optional lyrics and metadata lookups go to the configured provider.
- Optional AI features send the text or prompt you submit to the AI provider you
  configured (for example OpenAI, Anthropic, Mistral, or a local model).
- Optional translation sends the text you ask to translate to the configured
  translation provider.

No data is sent to a server operated by this project.

## Permissions

| Permission | Why it is declared | Required? |
| --- | --- | --- |
| `INTERNET` | All network access (music, artwork, metadata) | Yes |
| `ACCESS_NETWORK_STATE` | Offline detection and retry policy | Yes |
| `WAKE_LOCK` | Background playback and downloads | Yes |
| `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`, `FOREGROUND_SERVICE_DATA_SYNC` | Playback and download services | Yes |
| `RECORD_AUDIO`, `FOREGROUND_SERVICE_MICROPHONE` | Song recognition, only when you tap recognise | No |
| `POST_NOTIFICATIONS` | Playback notification and download progress | No |
| `RECEIVE_BOOT_COMPLETED` | Restore scheduled tasks after a restart | No |
| `SCHEDULE_EXACT_ALARM` | On-time scheduled tasks you set | No |

## Data deletion

Uninstalling the app removes everything it stored on the device. If you signed in
to a third-party service or entered an API key, revoke it from that service's
account settings.

## Children

The app is not directed at children and does not knowingly collect personal
information from them.

## Changes

Material changes to this policy are recorded in this file's history in the
repository.

## Contact

Open an issue at https://github.com/Exploit7492/Metrolist.
