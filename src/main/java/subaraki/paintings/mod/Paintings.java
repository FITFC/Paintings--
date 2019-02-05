package subaraki.paintings.mod;

import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mcf.davidee.paintinggui.gui.PaintingButton;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import subaraki.paintings.config.ConfigurationHandler;
import subaraki.paintings.mod.entity.EntityHandler;
import subaraki.paintings.mod.network.NetworkHandler;
import subaraki.paintings.mod.server.proxy.CommonProxy;


@Mod(modid = Paintings.MODID, name = Paintings.NAME, version = Paintings.VERSION, dependencies = "after:paintingselgui")
public class Paintings {

    public static final String MODID = "morepaintings";
    public static final String RESOURCE_DOMAIN = "subaraki";
    public static final String VERSION = "$version";
    public static final String NAME = "Paintings++";
    public static final String[] AUTHORS = {"Subaraki", "MurphysChaos"};

    @SidedProxy(serverSide = "subaraki.paintings.mod.server.proxy.CommonProxy", clientSide = "subaraki.paintings.mod.client.proxy.ClientProxy")
    public static CommonProxy proxy;
    public static Logger log;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();

        ModMetadata modMeta = event.getModMetadata();
        modMeta.authorList = Arrays.asList(AUTHORS);
        modMeta.autogenerated = false;
        modMeta.credits = "Subaraki";
        modMeta.description = "More Paintings! Check the config file for options.";
        modMeta.url = "http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1287285-/";

        ConfigurationHandler.instance.loadConfig(event.getSuggestedConfigurationFile());

        new EntityHandler();
        
        loadPattern();
        proxy.registerRenderInformation();
     
        new NetworkHandler();
    }

    public static void loadPattern() {
        JsonObject patternObject = proxy.getPatternJson(ConfigurationHandler.instance.texture);
        PaintingsPattern.instance = new Gson().fromJson(patternObject, PaintingsPattern.class);

        if (!ConfigurationHandler.instance.texture.equals("vanilla")) {
            try {
                PaintingsPattern.instance.parsePattern();

                // Since PaintingsGui is a permanent part of Paintings++, we don't need those helper methods
                PaintingButton.TEXTURE = new ResourceLocation(Paintings.RESOURCE_DOMAIN,"art/" + ConfigurationHandler.instance.texture + ".png");
                PaintingButton.KZ_WIDTH = PaintingsPattern.instance.getSize().width * 16;
                PaintingButton.KZ_HEIGHT = PaintingsPattern.instance.getSize().height * 16;

            } catch (Exception e) {
                Paintings.log.warn(e.getLocalizedMessage());
            }
        }
    }
}