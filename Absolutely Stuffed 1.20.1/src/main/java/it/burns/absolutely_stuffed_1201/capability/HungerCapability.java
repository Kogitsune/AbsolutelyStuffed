package it.burns.absolutely_stuffed_1192.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class HungerCapability {
    public static final Capability<IHunger> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    private HungerCapability( ){ }
}
