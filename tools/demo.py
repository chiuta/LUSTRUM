#!/usr/bin/env python3
"""Generează fișiere de probă (poze, duplicate, resturi, videoclipuri) pentru capturile și videoclipurile din prezentare."""
import os, random, subprocess, sys
from PIL import Image, ImageDraw, ImageFilter

out = sys.argv[1]
os.makedirs(f"{out}/DCIM/Camera", exist_ok=True)
os.makedirs(f"{out}/Download", exist_ok=True)
os.makedirs(f"{out}/Pictures/Screenshots", exist_ok=True)
random.seed(4)

def scene(seed, w=2400, h=1600):
    r = random.Random(seed)
    im = Image.new("RGB", (w, h))
    px = ImageDraw.Draw(im)
    top = (r.randint(20, 120), r.randint(80, 180), r.randint(160, 255))
    bot = (r.randint(120, 255), r.randint(60, 160), r.randint(20, 120))
    for y in range(h):
        t = y / h
        px.line([(0, y), (w, y)], fill=tuple(int(top[i] * (1 - t) + bot[i] * t) for i in range(3)))
    for _ in range(14):
        x, y, s = r.randint(0, w), r.randint(0, h), r.randint(80, 420)
        px.ellipse([x, y, x + s, y + s], fill=(r.randint(0, 255), r.randint(0, 255), r.randint(0, 255)))
    for _ in range(900):  # textură fină ca poza să fie "mare" și clară
        x, y = r.randint(0, w), r.randint(0, h)
        px.point((x, y), fill=(r.randint(0, 255),) * 3)
    return im

for i in range(6):
    scene(i).save(f"{out}/DCIM/Camera/IMG_{20260100 + i}.jpg", quality=96)
# identice
for i in (0, 1):
    open(f"{out}/Download/copie_IMG_{i}.jpg", "wb").write(open(f"{out}/DCIM/Camera/IMG_{20260100 + i}.jpg", "rb").read())
# asemănătoare: redimensionată + recomprimată
scene(2).resize((1200, 800)).save(f"{out}/Download/IMG_2_mica.jpg", quality=70)
# neclară și întunecată
scene(3).filter(ImageFilter.GaussianBlur(40)).save(f"{out}/DCIM/Camera/IMG_blurry.jpg", quality=90)
Image.new("RGB", (1600, 1200), (6, 6, 8)).save(f"{out}/DCIM/Camera/IMG_dark.jpg", quality=90)
# capturi de ecran
for i in range(3):
    scene(10 + i, 1080, 2400).save(f"{out}/Pictures/Screenshots/Screenshot_2026{i}.png")
# resturi
for n in ("setup.tmp", "cache.chk", "descarcare.crdownload", "old.part"):
    open(f"{out}/Download/{n}", "wb").write(os.urandom(300_000))
# videoclipuri: două aproape identice + unul diferit
def vid(path, color, secs=4):
    subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-f", "lavfi", "-i",
                    f"testsrc2=size=640x360:rate=24:duration={secs}", "-vf", f"hue=h={color}", "-pix_fmt", "yuv420p", path], check=True)
vid(f"{out}/DCIM/Camera/VID_a.mp4", 0)
vid(f"{out}/DCIM/Camera/VID_b_copie.mp4", 0, 4)
vid(f"{out}/DCIM/Camera/VID_c.mp4", 120, 6)
