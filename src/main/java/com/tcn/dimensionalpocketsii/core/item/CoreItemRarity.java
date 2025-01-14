package com.tcn.dimensionalpocketsii.core.item;

import java.util.function.UnaryOperator;

import com.tcn.cosmoslibrary.common.lib.ComponentColour;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class CoreItemRarity {
    public static final EnumProxy<Rarity> POCKET = new EnumProxy<>(
        Rarity.class, -1, "dimensionalpocketsii:pocket", (UnaryOperator<Style>) style -> style.withColor(TextColor.fromRgb(ComponentColour.POCKET_PURPLE_LIGHT.dec()))
    );
    
    public static final EnumProxy<Rarity> MODULE_ARMOUR = new EnumProxy<>(
        Rarity.class, -1, "dimensionalpocketsii:module_armour", (UnaryOperator<Style>) style -> style.withColor(ChatFormatting.GOLD)
    );
    
    public static final EnumProxy<Rarity> MODULE_POCKET = new EnumProxy<>(
        Rarity.class, -1, "dimensionalpocketsii:module_pocket", (UnaryOperator<Style>) style -> style.withColor(ChatFormatting.AQUA)
    );
    
    public static final EnumProxy<Rarity> CREATIVE = new EnumProxy<>(
        Rarity.class, -1, "dimensionalpocketsii:creative", (UnaryOperator<Style>) style -> style.withColor(TextColor.fromRgb(ComponentColour.MAGENTA.dec()))
    );
}