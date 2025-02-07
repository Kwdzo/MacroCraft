package dev.boxadactle.macrocraft.gui;

import dev.boxadactle.boxlib.gui.config.BOptionButton;
import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.widget.BSpacingEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BConfigScreenButton;
import dev.boxadactle.boxlib.gui.config.widget.button.BCustomButton;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.gui.config.widget.label.BCenteredLabel;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.WorldUtils;
import dev.boxadactle.macrocraft.MacroCraft;
import dev.boxadactle.macrocraft.macro.MacroState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public class MacroPlayScreen extends BOptionScreen {



    public MacroPlayScreen(Screen parent) {
        super(parent);
    }

    @Override
    protected Component getName() {
        return Component.translatable("screen.macrocraft.play.title");
    }

    @Override
    protected void initFooter(int startX, int startY) {
        addRenderableWidget(createBackButton(startX, startY, parent));
    }

    @Override
    protected void initConfigButtons() {
        // loaded macro
        addConfigLine(new BCenteredLabel(Component.translatable("screen.macrocraft.play.loaded", MacroState.MACRO_NAME)));

        // macro size
        addConfigLine(new BCenteredLabel(Component.translatable("screen.macrocraft.play.size", MacroState.LOADED_MACRO.actions.size())));

        // load different macro
        addConfigLine(new BConfigScreenButton(Component.translatable("screen.macrocraft.play.loadDifferent"), new MacroPlayScreen(parent), MacroListScreen::new));
        

        BBooleanButton syncViewButton = new BBooleanButton(
                "Should Sync View Direction",
                MacroCraft.CONFIG.get().shouldSyncViewDirection,
                (value) -> {
                    MacroCraft.CONFIG.get().shouldSyncViewDirection = value;
                    MacroCraft.CONFIG.save();
                }
        );
        BCustomButton previewButton = new BCustomButton(Component.literal("Preview Direction")) {
                    @Override
                    protected void buttonClicked(BOptionButton<?> button) {
                        var player = WorldUtils.getPlayer();
                        player.setYHeadRot(MacroState.LOADED_MACRO.startingYRot);
                        Method method = Entity.class.getDeclaredMethod("setRot");
                        method.setAccessible(true);
                        Object r = method.invoke(player, MacroState.LOADED_MACRO.startingYRot, MacroState.LOADED_MACRO.startingXRot);
                        //player.setRot(MacroState.LOADED_MACRO.startingYRot, MacroState.LOADED_MACRO.startingXRot);
                    }
                };
        if(!MacroState.hasLoadedMacro()){
            previewButton.active = false;
            previewButton.setTooltip(Tooltip.create(Component.literal("No macro loaded")));
        }
        else if((MacroState.LOADED_MACRO.startingXRot == 0) && (MacroState.LOADED_MACRO.startingYRot == 0)){
            previewButton.active = false;
            previewButton.setTooltip(Tooltip.create(Component.literal("Macro has no X and Y rotation saved")));
        }
        else {
            previewButton.active = true;
            previewButton.setTooltip(Tooltip.create(Component.literal("Applies the starting rotation without playing the macro")));
        }

        
        addConfigLine(syncViewButton, previewButton);

        addConfigLine(new BSpacingEntry());

        StopButton stopButton = new StopButton();
        PlayButton playButton = new PlayButton(stopButton);

        addConfigLine(playButton, stopButton);
    }

    public static class PlayButton extends BCustomButton {
        boolean isPlaying;

        StopButton stopButton;

        public PlayButton(StopButton stopButton) {
            super(Component.translatable("screen.macrocraft.play.play"));

            isPlaying = MacroState.LOADED_MACRO.isPlaying;

            this.stopButton = stopButton;

            if (!isPlaying) {
                stopButton.active = false;
            }
        }

        @Override
        protected void buttonClicked(BOptionButton<?> button) {
            if (!isPlaying) {
                boolean flag = MacroState.LOADED_MACRO.playMacro();

                if (!flag) {
                    return;
                }
                MacroState.LOADED_MACRO.repetitionsDone = 0;

                button.setMessage(Component.translatable("screen.macrocraft.play.pause"));

                stopButton.active = true;

                ClientUtils.setScreen(null);
            } else {
                MacroState.LOADED_MACRO.pauseMacro();

                button.setMessage(Component.translatable("screen.macrocraft.play.play"));

                stopButton.active = false;
            }
        }
    }

    public static class StopButton extends BCustomButton {

        public StopButton() {
            super(Component.translatable("screen.macrocraft.play.stop"));
        }

        @Override
        protected void buttonClicked(BOptionButton<?> button) {
            MacroState.LOADED_MACRO.endMacro();
        }
    }
}
