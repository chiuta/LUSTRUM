#!/usr/bin/env python3
"""Ajutor adb pentru demo: tap pe text (din uiautomator), scroll, swipe. Rulat doar în CI, pe emulator."""
import re, subprocess, sys, time, xml.etree.ElementTree as ET

def adb(*a, check=True):
    return subprocess.run(["adb", *a], capture_output=True, text=True, check=check).stdout

def dump():
    adb("shell", "uiautomator", "dump", "/sdcard/ui.xml", check=False)
    xml = adb("shell", "cat", "/sdcard/ui.xml", check=False)
    try:
        return ET.fromstring(xml[xml.index("<?xml"):] if "<?xml" in xml else xml)
    except Exception:
        return None

def find(text, exact=False):
    root = dump()
    if root is None: return None
    best = None
    for n in root.iter("node"):
        t = (n.get("text") or n.get("content-desc") or "")
        if (t == text) if exact else (text.lower() in t.lower()):
            m = re.match(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", n.get("bounds") or "")
            if m:
                x1, y1, x2, y2 = map(int, m.groups())
                best = best or ((x1 + x2) // 2, (y1 + y2) // 2)
    return best

def tap(text, tries=6, exact=False):
    if text.startswith("="):
        text, exact = text[1:], True
    for _ in range(tries):
        p = find(text, exact)
        if p:
            adb("shell", "input", "tap", str(p[0]), str(p[1]))
            time.sleep(1.2)
            return True
        time.sleep(1.5)
    print("nu am găsit:", text, file=sys.stderr)
    return False

def size():
    m = re.search(r"(\d+)x(\d+)", adb("shell", "wm", "size"))
    return int(m.group(1)), int(m.group(2))

def swipe(dx_frac, y_frac=0.5, ms=250):
    w, h = size()
    y = int(h * y_frac); x0 = w // 2
    adb("shell", "input", "swipe", str(x0), str(y), str(int(x0 + dx_frac * w)), str(y), str(ms))
    time.sleep(0.8)

def scroll_down():
    w, h = size()
    adb("shell", "input", "swipe", str(w // 2), str(int(h * .75)), str(w // 2), str(int(h * .3)), "400")
    time.sleep(0.8)

def wait_for(text, secs=90):
    t0 = time.time()
    while time.time() - t0 < secs:
        if find(text): return True
        time.sleep(2)
    return False

if __name__ == "__main__":
    cmd, *a = sys.argv[1:]
    if cmd == "tap": sys.exit(0 if tap(a[0]) else 1)
    if cmd == "swipe": swipe(float(a[0]))
    if cmd == "scroll": scroll_down()
    if cmd == "wait": sys.exit(0 if wait_for(a[0], int(a[1]) if len(a) > 1 else 90) else 1)
