package com.mochi_753.tconstructmtk.common.registry;

import com.mochi_753.tconstructmtk.TConstructMTK;
import com.mochi_753.tconstructmtk.common.item.TinkerArrowMTKItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TConstructMTKItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,TConstructMTK.MOD_ID);

    public static final RegistryObject<Item> TINKER_ARROW_MTK = ITEMS.register("tinker_arrow_mtk", ()->new TinkerArrowMTKItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
