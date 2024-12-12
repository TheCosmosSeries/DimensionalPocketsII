package com.tcn.dimensionalpocketsii;


import com.tcn.dimensionalpocketsii.client.screen.ScreenConfiguration;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = DimensionalPockets.MOD_ID, dist = Dist.CLIENT)
public class DimensionalPocketsClient {

	public DimensionalPocketsClient(ModContainer container) {
		container.registerExtensionPoint(IConfigScreenFactory.class, ScreenConfiguration::new);
	}
}