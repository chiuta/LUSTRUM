#!/usr/bin/env bash
# Test de fum pe emulator: instalează APK-ul RELEASE (minificat cu R8), îl pornește și verifică că nu se prăbușește.
set -euo pipefail
PKG=tf.trom.lustrum
MAP=app/build/outputs/mapping/release/mapping.txt
grep -E "^tf\.trom\.lustrum\.CleanWorker -> tf\.trom\.lustrum\.CleanWorker:" "$MAP" \
  || { echo "CleanWorker a fost redenumit de R8"; exit 1; }
APK=$(ls app/build/outputs/apk/release/*.apk | head -1)
adb uninstall "$PKG" >/dev/null 2>&1 || true
adb uninstall "$PKG.test" >/dev/null 2>&1 || true
adb install -r "$APK"
adb shell appops set --uid "$PKG" MANAGE_EXTERNAL_STORAGE allow
adb shell pm grant "$PKG" android.permission.POST_NOTIFICATIONS || true
adb logcat -c
adb shell am start -W -n "$PKG/.MainActivity"
sleep 10
adb shell pidof "$PKG" >/dev/null || { echo "aplicația nu rulează"; adb logcat -d | tail -80; exit 1; }
if adb logcat -d | grep -E "FATAL EXCEPTION|ClassNotFoundException|NoSuchMethodException"; then
  adb logcat -d | tail -80
  exit 1
fi
echo "smoke OK (release minificat)"
