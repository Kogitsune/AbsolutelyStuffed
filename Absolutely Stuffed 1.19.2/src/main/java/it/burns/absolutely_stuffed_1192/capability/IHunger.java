package it.burns.absolutely_stuffed_1192;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface HungerInterface extends INBTSerializable<CompoundTag> {
    float getValue();
    void setMyValue(float v);
}