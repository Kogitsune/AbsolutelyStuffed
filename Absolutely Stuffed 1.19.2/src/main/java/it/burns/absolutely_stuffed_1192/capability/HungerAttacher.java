package it.burns.absolutely_stuffed_1192;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HungerAttacher {
    private static class HungerProvider implements ICapabilityProvider, INBTSerializable<CompoundTag>{

        public static final ResourceLocation IDENTIFIER = new ResourceLocation( "mod_id", AbsolutelyStuffed.MODID );

        private final IHunger backend = new HungerImplementation();
        private final LazyOptional<IHunger> optionalData = LazyOptional.of( () -> backend );

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return HungerCapability.INSTANCE.orEmpty( cap, this.optionalData);
        }

        void invalidate( ){
            this.optionalData.invalidate();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.backend.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.backend.deserializeNBT( nbt );
        }
    }

    public static void attach(final AttachCapabilitiesEvent<LivingEntity> event){
        final HungerProvider provider = new HungerProvider();

        event.addCapability( HungerProvider.IDENTIFIER, provider );
    }

    private HungerAttacher( ){ }
}
