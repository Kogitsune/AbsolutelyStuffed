package it.burns.absolutely_stuffed;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AbsolutelyStuffed.MODID)
public class AbsolutelyStuffed {

    @AutoRegisterCapability
    public interface IAmHungry {

    }



    // Define mod id in a common place for everything to reference
    public static final String MODID = "absolutely_stuffed";

    public AbsolutelyStuffed() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void FinishEat(LivingEntityUseItemEvent.Finish event, ItemStack what ){
        if ( event.getEntity() instanceof Player ){
            Player pl = (Player)event.getEntity();

            if (what.isEdible()){
                FoodData cur = pl.getFoodData();
                @Nullable FoodProperties delta = what.getFoodProperties(pl);

                if ( delta != null ){
                    int hunger_over = 0;
                    float saturation_over = 0f;

                    hunger_over = Math.max(0, cur.getFoodLevel() + delta.getNutrition() - 20);
                    saturation_over = Math.max(0f, cur.getSaturationLevel() + delta.getSaturationModifier() - 20f);
                }

            }
        }
    }


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}
