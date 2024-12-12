package com.tcn.dimensionalpocketsii.core.advancement;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tcn.dimensionalpocketsii.DimensionalPockets;
import com.tcn.dimensionalpocketsii.core.management.ModRegistrationManager;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class UseShifterTrigger extends SimpleCriterionTrigger<UseShifterTrigger.TriggerInstance> {
	static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(DimensionalPockets.MOD_ID, "use_shifter");

	@Override
	public Codec<TriggerInstance> codec() {
		return UseShifterTrigger.TriggerInstance.CODEC;
	}
	
    public void trigger(ServerPlayer player, ItemStack item) {
        this.trigger(player, instance -> instance.matches(item));
    }

	public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleInstance {
		public static final Codec<UseShifterTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(UseShifterTrigger.TriggerInstance::player),
                ItemPredicate.CODEC.optionalFieldOf("item").forGetter(UseShifterTrigger.TriggerInstance::item)
            ).apply(instance, UseShifterTrigger.TriggerInstance::new)
        );

        public static Criterion<UseShifterTrigger.TriggerInstance> lookingAt(EntityPredicate.Builder player, ItemPredicate.Builder item) {
            return ModRegistrationManager.SHIFTER_TRIGGER.get().createCriterion(new UseShifterTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(player)), Optional.of(item.build())));
        }

        public boolean matches(ItemStack item) {
            return !this.item.isPresent() || this.item.get().test(item);
        }
	}
}