package com.gtlugo.ampere.std;

import com.gtlugo.ampere.std.common.block.BlockEntities;
import com.gtlugo.ampere.std.common.block.FurnaceBlock;
import com.gtlugo.ampere.std.common.block.ModBlock;
import com.gtlugo.ampere.std.common.block.PowerPlantBlock;
import com.gtlugo.ampere.std.common.block.wire.WireBlock;
import com.gtlugo.ampere.std.common.item.ModItem;
import com.gtlugo.ampere.std.common.item.ProbeItem;
import com.gtlugo.ampere.std.common.item.ScrewdriverItem;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(AmpereStd.MOD_ID)
public class AmpereStd {
  public static final String MOD_ID = "ampere_std";
  public static final DeferredRegister.Blocks BLOCK_REGISTRY = DeferredRegister.createBlocks(
    AmpereStd.MOD_ID);
  public static final DeferredRegister.Items ITEM_REGISTRY = DeferredRegister.createItems(AmpereStd.MOD_ID);
  // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "ampere" namespace
  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
    Registries.CREATIVE_MODE_TAB,
    MOD_ID
  );
  // Creates a creative tab with the id "ampere:example_tab" for the example item, that is placed after the combat tab
  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TOOLS_TAB = CREATIVE_MODE_TABS.register(
    MOD_ID + "_tools",
    () -> CreativeModeTab
      .builder()
      .title(Component.translatable("itemGroup." + MOD_ID + ".tools")) //The language key for the title of your CreativeModeTab
      .withTabsBefore(CreativeModeTabs.OP_BLOCKS)
      .icon(() -> Items.SCREWDRIVER.get().getDefaultInstance())
      .displayItems((parameters, output) -> {
        output.accept(Items.SCREWDRIVER.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
        output.accept(Items.PROBE.get());
      })
      .build()
  );
  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCKS_TAB = CREATIVE_MODE_TABS.register(
    MOD_ID + "_blocks",
    () -> CreativeModeTab
      .builder()
      .title(Component.translatable("itemGroup." + MOD_ID + ".items")) //The language key for the title of your CreativeModeTab
      .withTabsBefore(TOOLS_TAB.getId())
      .icon(() -> Blocks.FURNACE.get().asItem().getDefaultInstance())
      .displayItems((parameters, output) -> {
        output.accept(Blocks.WIRE.get());
        output.accept(Blocks.FURNACE.get());
        output.accept(Blocks.POWER_PLANT.get());
      })
      .build()
  );
  private static final Logger LOGGER = LogUtils.getLogger();

  // The constructor for the mod class is the first code that is run when your mod is loaded.
  // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
  public AmpereStd(IEventBus modEventBus, ModContainer modContainer) {
    NeoForge.EVENT_BUS.register(this);
    modEventBus.addListener(this::commonSetup);

    Blocks.register(modEventBus);
    Items.register(modEventBus);
    BlockEntities.register(modEventBus);
    CREATIVE_MODE_TABS.register(modEventBus);

    modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
  }

  public static ResourceLocation itemLocation(String name) {
    return ResourceLocation.fromNamespaceAndPath(AmpereStd.MOD_ID, "item/" + name);
  }

  public static ResourceLocation blockLocation(String name) {
    return ResourceLocation.fromNamespaceAndPath(AmpereStd.MOD_ID, "block/" + name);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    // Some common setup code
    LOGGER.info("Ampere Common Setup");
  }

  // You can use SubscribeEvent and let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
    // Do something when the server starts
    LOGGER.info("Ampere Server Startup");
  }

  public enum Blocks {
    FURNACE("furnace", FurnaceBlock::new),
    POWER_PLANT("power_plant", PowerPlantBlock::new),
    WIRE("wire", WireBlock::new);

    private final DeferredBlock<ModBlock> block;

    Blocks(String name, Supplier<ModBlock> block) {
      this.block = registerBlock(name, block);
    }

    public static void register(IEventBus event_bus) {
      AmpereStd.BLOCK_REGISTRY.register(event_bus);
    }

    static <T extends ModBlock> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
      DeferredBlock<T> b = AmpereStd.BLOCK_REGISTRY.register(name, block);
      AmpereStd.ITEM_REGISTRY.registerSimpleBlockItem(b);
      return b;
    }

    public ModBlock get() {
      return this.block.get();
    }

    public DeferredBlock<?> deferred() {
      return this.block;
    }
  }

  public enum Items {
    SCREWDRIVER("screwdriver", ScrewdriverItem::new),
    PROBE("probe", ProbeItem::new);

    private final DeferredItem<ModItem> item;

    Items(String name, Supplier<ModItem> item) {
      this.item = AmpereStd.ITEM_REGISTRY.register(name, item);
    }

    public static void register(IEventBus event_bus) {
      AmpereStd.ITEM_REGISTRY.register(event_bus);
    }

    public ModItem get() {
      return this.item.get();
    }
  }
}
