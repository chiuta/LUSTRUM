#!/usr/bin/env bash
# Rulează APK-ul debug pe emulator cu fișiere de probă și înregistrează capturi + videoclipuri pentru prezentare.
# Doar în CI (workflow „Demo media”). Pașii care nu reușesc nu opresc restul.
set -uo pipefail
PKG=tf.trom.lustrum
UI="python3 tools/ui.py"
mkdir -p build-demo/sdcard build-demo/raw media

python3 tools/demo.py build-demo/sdcard
adb push build-demo/sdcard/. /sdcard/ >/dev/null
for f in Download/setup.tmp Download/cache.chk Download/descarcare.crdownload Download/old.part \
         DCIM/Camera/IMG_20260100.jpg DCIM/Camera/IMG_20260101.jpg DCIM/Camera/IMG_20260102.jpg \
         DCIM/Camera/IMG_20260103.jpg DCIM/Camera/IMG_20260104.jpg DCIM/Camera/IMG_20260105.jpg \
         DCIM/Camera/IMG_blurry.jpg DCIM/Camera/IMG_dark.jpg; do
  adb shell touch -d "2026-0$((RANDOM % 8 + 1))-1$((RANDOM % 9)) 10:00:00" "/sdcard/$f" || true
done

adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell appops set --uid $PKG MANAGE_EXTERNAL_STORAGE allow
for perm in READ_MEDIA_IMAGES READ_MEDIA_VIDEO READ_MEDIA_AUDIO POST_NOTIFICATIONS; do
  adb shell pm grant $PKG android.permission.$perm || true
done
adb shell appops set $PKG GET_USAGE_STATS allow || true
adb shell settings put global window_animation_scale 1
adb shell cmd uimode night no || true

shot() { adb exec-out screencap -p > "build-demo/raw/$1.png"; }
REC_PID=""
rec_start() { adb shell "screenrecord --bit-rate 3000000 --time-limit 60 /sdcard/$1.mp4" & REC_PID=$!; sleep 2; }
rec_stop() { sleep 2; adb shell pkill -2 screenrecord || true; wait "$REC_PID" 2>/dev/null || true; sleep 2; adb pull "/sdcard/$1.mp4" "build-demo/raw/$1.mp4" >/dev/null || true; }

adb shell am start -W -n $PKG/.MainActivity >/dev/null
sleep 4
shot home-before

# 1. Scanare
rec_start scan
$UI tap "Scan phone"
$UI wait "Scan again" 150 || true
sleep 2
shot home-after
$UI scroll; sleep 1; shot home-storage
$UI scroll; sleep 1
rec_stop scan

# 2. Fișiere (resturi, mari, capturi)
$UI tap "=Files"; sleep 2; shot files
$UI tap "Junk" || true; sleep 2; shot files-junk

# 3. Duplicate: identice, asemănătoare, videoclipuri
rec_start dupes
$UI tap "=Duplicates"; sleep 2
$UI tap "Find identical files"; $UI wait "Select" 90 || sleep 8; shot dupes-exact
$UI tap "=Similar images" || true; sleep 1
$UI tap "Find similar images"; sleep 25; shot dupes-similar
$UI tap "=Similar videos" || true; sleep 1
$UI tap "Find similar videos"; sleep 20; shot dupes-video
rec_stop dupes

# 4. Swiper
rec_start swiper
$UI tap "=Swipe"; sleep 2
$UI tap "Start (oldest first)"; sleep 4; shot swiper
$UI swipe -0.6; sleep 1
$UI swipe 0.6; sleep 1
$UI swipe -0.6; sleep 1
shot swiper-marked
$UI swipe 0.6; sleep 1
rec_stop swiper

# 5. Curățare programată și coș
rec_start schedule
$UI tap "=Home"; sleep 2
for i in 1 2 3; do $UI scroll; done
$UI tap "=Weekly" || true; sleep 2; shot schedule
rec_stop schedule

# 6. Aplicații
$UI tap "=Apps"; sleep 6; shot apps

# Conversie: PNG 540 px lățime, MP4 360 px lățime (H.264, fără sunet, fast-start)
for f in build-demo/raw/*.png; do
  ffmpeg -y -loglevel error -i "$f" -vf scale=540:-1 "media/$(basename "$f")"
done
for f in build-demo/raw/*.mp4; do
  ffmpeg -y -loglevel error -i "$f" -vf "scale=360:-2" -c:v libx264 -preset veryfast -crf 31 -pix_fmt yuv420p -movflags +faststart -an "media/$(basename "$f")"
done
adb shell cat /sdcard/ui.xml > media/last-ui.xml || true
ls -la media
