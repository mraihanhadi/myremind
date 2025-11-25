# Code Review

## Overview
Fresh review of the current branch focusing on correctness, security, and UX. Items below call out risky patterns and concrete remediation ideas.

## Findings

1. **Sensitive credentials stored in app state** – `UserController` builds a `User` with the plaintext password on both sign-in and sign-up and keeps it in `currentUser`. Persisting credentials in memory risks accidental logging or exposure during crashes. Remove the password field from `User` and only store non-secret identifiers (uid/email/username).【F:app/src/main/java/com/example/myremind/controller/UserController.kt†L33-L110】【F:app/src/main/java/com/example/myremind/model/User.kt†L1-L10】

2. **Sign-up flow leaves view model empty** – After creating an account, `signUp` never populates `currentUser`, so downstream screens using `userController.currentUser` behave as if signed out until a manual login. Build and store a sanitized `User` (no password) on successful sign-up so navigation and data loading stay consistent.【F:app/src/main/java/com/example/myremind/controller/UserController.kt†L72-L110】【F:app/src/main/java/com/example/myremind/navigation/AppNavHost.kt†L121-L220】

3. **Add-alarm inputs are incomplete and unvalidated** – `AddAlarmScreen` allocates state for a description and date (`desc`, `dateFmt`, `showDatePicker`) but never renders or submits them, and the Save action forwards whatever is in state without ensuring a title or time was chosen. Users can save empty/invalid alarms or get confused by missing fields. Either surface the description/date fields in the UI and wire them into the payload, or remove the dead state; add client-side validation for required fields before calling `onSave`.【F:app/src/main/java/com/example/myremind/ui/view/AddAlarmScreen.kt†L49-L220】

4. **Alarm toggle does nothing** – In the alarm grid, the toggle is rendered but its `onCheckedChange` is an empty lambda, so users cannot enable/disable alarms and changes are never persisted. Wire the toggle to controller logic (and update Firestore) or remove it to avoid misleading UI affordances.【F:app/src/main/java/com/example/myremind/ui/view/Alarm.kt†L166-L225】

5. **View models are not lifecycle-scoped** – `AppNavHost` instantiates controllers with `remember { UserController() }`/`GroupController()`/`AlarmController()`. These aren’t tied to the activity/VM lifecycle, so configuration changes can drop state, and instances may leak if the composition tree changes. Use `viewModel()` or DI-provided view models so state survives rotations and is cleared appropriately.【F:app/src/main/java/com/example/myremind/navigation/AppNavHost.kt†L14-L220】

6. **Email enumeration on reset** – `resetPassword` first calls `fetchSignInMethodsForEmail` and surfaces "Email tidak terdaftar" for unknown addresses. This leaks which emails exist. Consider always showing a generic success message (“If an account exists…”) and avoid pre-checks that expose existence, relying on Firebase’s consistent responses instead.【F:app/src/main/java/com/example/myremind/controller/UserController.kt†L112-L151】

7. **Group membership/role not enforced client-side** – Group detail and add-member flows accept a `groupId` from navigation and directly call `addUser`/`removeUser` on that document without checking that the current user belongs to the group or is an admin (admin is implied only by list order). Without server-side rules, a tampered client could add/remove members or leave arbitrary groups. Gate these actions on membership/role in the client and ensure Firestore rules enforce the same constraints.【F:app/src/main/java/com/example/myremind/navigation/AppNavHost.kt†L460-L579】【F:app/src/main/java/com/example/myremind/controller/GroupController.kt†L30-L199】

## Recommendations
- Avoid storing passwords in any app model; rely on Firebase session tokens and minimal profile fields.
- Populate `currentUser` after sign-up and strip secrets before persisting to view state.
- Expose or remove unused Add Alarm fields and validate title/time before saving; block submission until required data is set.
- Connect the alarm toggle to enable/disable logic and persistence, or remove it to prevent broken affordances.
- Instantiate controllers via `viewModel()`/DI to align with lifecycle and preserve state across configuration changes.
- Return generic reset-password messaging to reduce email enumeration risk.
- Restrict group mutations to members/admins on both client flows and Firestore rules.
