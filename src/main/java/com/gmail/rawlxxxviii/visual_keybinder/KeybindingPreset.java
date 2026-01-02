package com.gmail.rawlxxxviii.visual_keybinder;

import java.util.List;

public class KeybindingPreset {

    private final String name;
    private final boolean readOnly;
    private final List<String> lines;

    public KeybindingPreset(String name, boolean readOnly, List<String> lines) {
        this.name = name;
        this.readOnly = readOnly;
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public List<String> getLines() {
        return lines;
    }
}
