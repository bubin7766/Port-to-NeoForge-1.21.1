package com.gmail.rawlxxxviii.visual_keybinder.ui_list;


import com.gmail.rawlxxxviii.visual_keybinder.KeybindingPreset;
import com.gmail.rawlxxxviii.visual_keybinder.screen.AlternativeKeybindScreen;
import com.gmail.rawlxxxviii.visual_keybinder.screen.PresetsScreen;
import com.gmail.rawlxxxviii.visual_keybinder.util.FileUtil;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class KeyPresetOptionsList extends ContainerObjectSelectionList<KeyPresetOptionsList.Entry> {


    private final PresetsScreen parentScreen;
    protected final Options options;

    private final int left;

    public KeyPresetOptionsList(PresetsScreen parentScreen, Minecraft minecraft, Options options, int left, int top, int width, int height) {

        super(minecraft, width, height, top, 26);
        this.options = options;

        this.height = height;

        this.left = left;
//        this.x0 = left;
//        this.x1 = width + this.x0;

        this.parentScreen = parentScreen;

        buildEntries();

    }

    @Override
    public int getX() {
        return left;
    }

    private void buildEntries(){
        clearEntries();

        var presets = FileUtil.getPresets();
        if(presets == null){
            return;
        }

        for (var a : presets){
            addEntry(new PresetEntry(a, parentScreen.isPresetActive(a)));
        }

        addEntry( new EmptyEntry() );
        addEntry( new ResetAllEntry() );
        addEntry( new UnbindAllEntry() );
    }

    public void onBindingsUpdated(){
        buildEntries();
        setScrollAmount(getScrollAmount());
    }

    @Override
    public int getRowWidth() {
        return width;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width + this.getRowLeft() - 8;
    }


    @OnlyIn(Dist.CLIENT)
    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
    }

    @OnlyIn(Dist.CLIENT)
    public class PresetEntry extends Entry {
        private final KeybindingPreset keybindingPreset;
        private final Button saveButton;
        private final Button loadButton;
        private final Button deleteButton;
        private final boolean isPresetActive;

        PresetEntry(KeybindingPreset keybindingPreset, boolean isPresetActive) {
            this.keybindingPreset = keybindingPreset;
            this.isPresetActive = isPresetActive;

            int buttonWidth = 50;

            this.saveButton = new Button.Builder(Component.literal("Save"),
                    (p_193939_) -> parentScreen.savePreset(keybindingPreset.getName()))
                    .pos(getRight() - (buttonWidth + 5) * 3 , 0)
                    .size(buttonWidth , 20)
                    .build();
            this.saveButton.active = !keybindingPreset.isReadOnly();

            this.loadButton = new Button.Builder(Component.literal("Load"),
                    (p_193939_) -> parentScreen.loadPreset(keybindingPreset.getName()))
                    .pos(getRight() - (buttonWidth + 5) * 2, 0)
                    .size(buttonWidth , 20)
                    .build();
            this.loadButton.active = !isPresetActive;


            this.deleteButton = new Button.Builder(
                    Component.literal("Delete"),
                    (p_193935_) -> parentScreen.deletePreset(keybindingPreset.getName())
                )
                .pos(getRight() - buttonWidth - 5, 0)
                .size(buttonWidth, 20)
                .build()
            ;
            this.deleteButton.active = !keybindingPreset.isReadOnly();
        }

        public Button getLoadButton() {
            return loadButton;
        }

        public Button getSaveButton() {
            return saveButton;
        }

        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.saveButton, this.loadButton, this.deleteButton);
        }

        public void render(GuiGraphics guiGraphics, int p_193924_, int p_193925_, int p_193926_, int p_193927_, int p_193928_, int p_193929_, int p_193930_, boolean p_193931_, float p_193932_) {

            guiGraphics.enableScissor(getRowLeft(), getRowTop(0),getRight(), getBottom());

                guiGraphics.drawString(
                        Minecraft.getInstance().font,
                        Component.literal(this.keybindingPreset.getName()),
                        getRowLeft() + 5,
                        getRowTop(p_193924_) + 6,
                        isPresetActive ? AlternativeKeybindScreen.ACTIVE_COLOR : Color.white.getRGB());

                this.saveButton.setY(p_193925_);
                this.saveButton.render(guiGraphics, p_193929_, p_193930_, p_193932_);

                this.loadButton.setY(p_193925_ );
                this.loadButton.render(guiGraphics, p_193929_, p_193930_, p_193932_);

                this.deleteButton.setY(p_193925_ );
                this.deleteButton.setFGColor( this.deleteButton.active ? AlternativeKeybindScreen.DANGER_COLOR : Color.GRAY.getRGB());
                this.deleteButton.render(guiGraphics, p_193929_, p_193930_, p_193932_);

            guiGraphics.disableScissor();
        }


        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }

    }


    @OnlyIn(Dist.CLIENT)
    public class UnbindAllEntry extends Entry {
        private final Button button;

        UnbindAllEntry() {

            int buttonWidth = 50;
            this.button = new Button.Builder(
                    Component.literal("Unbind"),
                    (p_193939_) -> parentScreen.unbindAll()
            )
            .pos(getRight() - (buttonWidth + 5) * 3 , 0)
            .size(100,20)
            .build();
            this.button.active = parentScreen.hasBoundBindings();

        }

        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.button);
        }

        public void render(GuiGraphics guiGraphics, int p_193924_, int p_193925_, int p_193926_, int p_193927_, int p_193928_, int p_193929_, int p_193930_, boolean p_193931_, float p_193932_) {
            guiGraphics.enableScissor(getRowLeft(), getRowTop(0), getRight(), getBottom());

            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    Component.literal("Unbind all keybindings"),
                    getRowLeft() + 5,
                    getRowTop(p_193924_) + 6,
                    parentScreen.hasBoundBindings() ? Color.LIGHT_GRAY.getRGB() : AlternativeKeybindScreen.ACTIVE_COLOR
            );

            this.button.setY(p_193925_);
            this.button.render(guiGraphics, p_193929_, p_193930_, p_193932_);

            guiGraphics.disableScissor();
        }


        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }

    }

    @OnlyIn(Dist.CLIENT)
    public class EmptyEntry extends Entry {

        EmptyEntry() {

        }

        public List<? extends GuiEventListener> children() {
            return ImmutableList.of();
        }

        public void render(GuiGraphics poseStack, int p_193924_, int p_193925_, int p_193926_, int p_193927_, int p_193928_, int p_193929_, int p_193930_, boolean p_193931_, float p_193932_) {

        }


        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }

    }


    @OnlyIn(Dist.CLIENT)
    public class ResetAllEntry extends Entry {
        private final Button button;

        ResetAllEntry() {

            int buttonWidth = 50;
            this.button = new Button.Builder(
                    Component.literal("Reset"),
                    (p_193939_) ->parentScreen.resetAll()
            )
            .pos(getRight() - (buttonWidth + 5) * 3 , 0)
            .size(100 , 20)
            .build()
            ;

            this.button.active = parentScreen.hasNonDefaultBindings();

        }

        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.button);
        }

        public void render(GuiGraphics guiGraphics, int p_193924_, int p_193925_, int p_193926_, int p_193927_, int p_193928_, int p_193929_, int p_193930_, boolean p_193931_, float p_193932_) {
            guiGraphics.enableScissor(getRowLeft(), getRowTop(0), getRight(), getBottom());

            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    Component.literal("Reset to defaults"),
                    getRowLeft() + 5,
                    getRowTop(p_193924_) + 6,
                     parentScreen.hasNonDefaultBindings() ? Color.LIGHT_GRAY.getRGB() : AlternativeKeybindScreen.ACTIVE_COLOR
            );

            this.button.setY(p_193925_);
            this.button.render(guiGraphics, p_193929_, p_193930_, p_193932_);

            guiGraphics.disableScissor();
        }


        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }

    }



}
