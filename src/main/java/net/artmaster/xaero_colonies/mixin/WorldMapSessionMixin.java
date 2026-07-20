package net.artmaster.xaero_colonies.mixin;

import net.artmaster.xaero_colonies.ModMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xaero.map.highlight.HighlighterRegistry;
import xaero.map.WorldMapSession;




@Mixin(value = WorldMapSession.class, remap = false)
public class WorldMapSessionMixin {

    @Redirect(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lxaero/map/highlight/HighlighterRegistry;end()V"
            ),
            remap = false
    )
    private void injectBeforeEnd(HighlighterRegistry instance) {
        ModMain.registerHighlighters(instance);
        instance.end();
    }


}

