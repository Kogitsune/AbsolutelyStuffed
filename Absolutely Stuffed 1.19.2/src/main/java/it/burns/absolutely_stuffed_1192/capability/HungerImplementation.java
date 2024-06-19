package it.burns.absolutely_stuffed_1192;

import net.minecraft.nbt.CompoundTag;

public class HungerImplementation implements IHunger {
    private static final String NBT_KEY = AbsolutelyStuffed.MODID + ".hunger_pool";
    private float value;

    @Override
    public float getValue() {
        return value;
    }

    @Override
    public void setMyValue(float v) {
        value = v;
    }

    @Override
    public CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();

        tag.putFloat( HungerImplementation.NBT_KEY, this.value );
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.value = nbt.getFloat( HungerImplementation.NBT_KEY );
    }
}
