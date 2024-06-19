package it.burns.absolutely_stuffed_1192;

import it.burns.absolutely_stuffed_1192.capability.HungerAttacher;
import it.burns.absolutely_stuffed_1192.capability.HungerCapability;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AbsolutelyStuffed.MODID)
public class AbsolutelyStuffed {
    static int Clamp( int a, int b, int c ){
        if ( a < b ) return b;
        return Math.min(a, c);
    }

    static float Clamp( float a, float b, float c ){
        if ( a < b ) return b;
        return Math.min(a, c);
    }

    private static class Config {
        public static final ForgeConfigSpec SPECIFICATION;

        public static ForgeConfigSpec.IntValue MAX_POOL_SIZE;
        public static ForgeConfigSpec.IntValue POOL_TICK_RATE;

        static {
            ForgeConfigSpec.Builder bldr = new ForgeConfigSpec.Builder();
            Setup( bldr );
            SPECIFICATION = bldr.build();
        }

        static void Setup( ForgeConfigSpec.Builder b ){
            b.comment( "Absolutely Stuffed! Just Desserts");
            b.push( "Tuning");
                MAX_POOL_SIZE = b
                        .comment( "How much can fit in the player's pool (0 = 2^31-1). Keep in mind that 40 is a full hunger and saturation bar.")
                        .defineInRange( "max_pool_size", 0, 0, 160);

                POOL_TICK_RATE = b
                        .comment( "How many ticks to wait before attempting to fill the hunger / saturation bars from the pool (20 = 1 second).")
                        .defineInRange( "pool_tick_rate", 5, 1, 200);
            b.pop( );
        }
    }
    // Define mod id in a common place for everything to reference
    public static final String MODID = "absolutely_stuffed";

    public AbsolutelyStuffed() {
        // Register ourselves for server and other game events we are interested in

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register( new HungerAttacher() );

        MinecraftForge.EVENT_BUS.addListener( this::FinishEat );
        MinecraftForge.EVENT_BUS.addListener( this::Digest );

        ModLoadingContext.get( ).registerConfig( ModConfig.Type.SERVER, Config.SPECIFICATION, MODID + ".toml");
    }

    @SubscribeEvent
    public void FinishEat(LivingEntityUseItemEvent.@NotNull Finish event ){
        ItemStack what = event.getItem();

        if (event.getEntity() instanceof Player pl){

            if (what.isEdible()){
                FoodData cur = pl.getFoodData();
                @Nullable FoodProperties delta = what.getFoodProperties(pl);

                if ( delta != null ){
                    int hunger_over;
                    float saturation_over;

                    hunger_over = Math.max(0, cur.getFoodLevel() + delta.getNutrition() - 20);
                    saturation_over = Math.max(0f, cur.getSaturationLevel() + delta.getSaturationModifier() - 20f);

                    saturation_over += hunger_over;

                    float finalSaturation_over = saturation_over;
                    pl.getCapability( HungerCapability.INSTANCE ).ifPresent(cap -> {
                        float newv = cap.getValue() + finalSaturation_over;

                        if ( Config.MAX_POOL_SIZE.get()!=0) newv = Clamp(newv, 0, Config.MAX_POOL_SIZE.get());

                        cap.setMyValue( newv );
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public void Digest(TickEvent.@NotNull PlayerTickEvent event){
        MinecraftServer srv = event.player.getServer();

        if ( srv != null && srv.overworld().getDayTime() % Config.POOL_TICK_RATE.get( ) == 0 ){
            event.player.getCapability( HungerCapability.INSTANCE ).ifPresent( cap -> {
                FoodData gut = event.player.getFoodData();

                if ( cap.getValue() == 0f ){ return; }

                if ( gut.needsFood() ){
                    if ( cap.getValue() >= 1f ){
                        gut.setFoodLevel(gut.getFoodLevel() + 1);
                        cap.setMyValue( cap.getValue()-1f);
                    }
                } else if (gut.getSaturationLevel() < 20f) {
                    float delta = Math.min( cap.getValue(), 20f - gut.getSaturationLevel() );

                    gut.setSaturation( gut.getSaturationLevel() + delta );
                    cap.setMyValue( cap.getValue() - delta );
                }
            });
        }
    }
}
