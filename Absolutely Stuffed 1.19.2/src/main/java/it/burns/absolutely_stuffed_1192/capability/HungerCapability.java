package it.burns.absolutely_stuffed_1192;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class HungerCapability {
    public static final Capability<IHunger> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event ){
        event.register( HungerCapability.class);
    }

    private HungerCapability( ){ }
}
