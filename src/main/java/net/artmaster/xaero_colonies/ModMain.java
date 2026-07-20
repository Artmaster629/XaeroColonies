package net.artmaster.xaero_colonies;

import com.mojang.logging.LogUtils;
import net.artmaster.xaero_colonies.client.overlay.ClaimsHighlighter;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import xaero.map.highlight.HighlighterRegistry;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ModMain.MODID)
public class ModMain {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "xaero_colonies";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();







    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ModMain(IEventBus modEventBus) {
        // Register the commonSetup method for modloading


        modEventBus.addListener(this::commonSetup);
    }



    public static void registerHighlighters(HighlighterRegistry registry) {
        LOGGER.info("Registering Era x Xaero highlighter");
        registry.register(new ClaimsHighlighter());
    }


    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("XaeroColonies mod is ready");
    }

}
