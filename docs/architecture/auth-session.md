# Auth session

## Storage

`SessionRepository` (`core/auth`) owns the signed-in `Session` (user and tokens). A user is signed in
if and only if a session exists.

- **What is stored:** one preferences key, `auth.session`, in its own DataStore file `session`
  (qualifier `@SessionStore`), separate from the `settings` file.
- **Encryption:** the session is serialized to JSON, encrypted with AES-256/GCM using a non-exportable
  key (`kotlinstarter_session`) generated in the AndroidKeyStore on first use, and stored as Base64
  (12-byte IV followed by ciphertext). `SessionCipher` is the seam; tests use a fake because JVM
  tests have no Keystore.
- **Backups:** `datastore/session.preferences_pb` is excluded from cloud backup and device transfer
  (`backup_rules.xml`, `data_extraction_rules.xml`). The Keystore key never leaves the device, so a
  restored ciphertext could not be decrypted anyway.
- **Failure handling:** a value that cannot be decoded or decrypted (corruption, lost key after a
  restore or lock-screen reset) is treated as signed out and the key is removed. Startup never crashes on it.
- **Startup:** the session is read before the first frame. `AppViewModel.ready` waits for it and the
  theme, and the splash stays up until then, so a signed-in cold start never flashes sign-in.
- **Not persisted:** `signOutReason` lives in memory only, so the "session expired" message is not
  repeated on cold start.
