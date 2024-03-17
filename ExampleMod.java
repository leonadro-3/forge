// 중요 
// 주석을 번역한 것

package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// 1~32번 줄에 있는 것들은 현재 패키지의 위치와 이 패키지를 사용하기 위해 import한 여러가지 것들을 의미한다.
// net.minecraft.client 마크 클라이언트
// net.minecraft.core 마크 코어 레지스트리
// net.minecraft.world 마크 월드
// net.minecraftforge 마크 포지(모드)
// slf4j vs log4j (서버 로거 - 기록)


// The value here should match an entry in the META-INF/mods.toml file
// toml 파일은 gradle의 버전관리를 위해 사용된다.
// modes.toml 파일을 보면 modLoader는 javafml로 로드하고, 로더의 버전, 라이센스와 같은 것들이 뜬다.
// 여러가지 모드의 대상이 되는 것들을 pubic static final로 예시를 두었다. 모드 이름, 로거, 블록, 아이템, UI 등등이 예시로 작성되어 있다.
@Mod(ExampleMod.MODID)
public class ExampleMod
{

    // Define mod id in a common place for everything to reference
    // 모드의 아이디를 정한다.
    public static final String MODID = "examplemod";


    // Directly reference a slf4j logger
    // slf4j 로거의 레퍼런스를 직접 연결한다.
    private static final Logger LOGGER = LogUtils.getLogger();


    //forge는 여러가지 마인크래프트의 객체들을 가져와 커스텀하는데 이때 만들어진 모드의 객체를 레지스트리(등록)로 마크 클라에 알려준다.
    // 다음과 같이 blcks, items, creativemodetabs가 있다. 3개의 namespace를 등록한다.는 의미이다.
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    // LogUtils
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);


    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);


    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);





    // namespace, path를 결합한 아이디가 "examplemod:example_block(모드 이름 : 모드 블럭)"인 Block과 BlockItem을 만든다.
    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));



    // 음식 아이템의 수치인 nutrition, saturation을 설정하는 새로운 아이템을 만든다. 그 아이템의 ID는 "examplemod:example_id"이다.
    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));

    // 마인크래프트에서 tab을 눌러서 서버 사용자의 정보를 간략하게 표시하는 UI를 킬 수 있는데 이것과 관련한 코드이다.
    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    // 위에서 작성된 public class ExampleMod을 ExampleMod() 함수로 실행한 것이다.
    // IEventBus는 net.minecraftforge.eventbus.api에 있는 public interface IEventBus를 의미하고
    // 해당 코드는 다음과 같은 파일에 존재한다. Gradle: net.minecraftforge:eventbus:6.2.0 (eventbus-6.2.0.jar)

    public ExampleMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 모드로딩 방법을 commonSetup으로 등록한다.
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // 모드 이벤트 버스에 위에서 작성한 BLOCKS, ITEMS, CREATIVE_MODE_TABS를 등록한다.
        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // 관심 있는 서버 및 기타 게임 이벤트에 등록하세요.
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // 크리에이티브 탭에 아이템을 등록
        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Forge가 구성 파일을 생성하고 로드할 수 있도록 모드의 ForgeConfigSpec을 등록합니다.
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }
    
    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
