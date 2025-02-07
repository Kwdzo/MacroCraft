package dev.boxadactle.macrocraft.listeners;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public interface EntityInvoker {
  @Invoker("setRot")
  public boolean invokeSetRot(float x, float y);
}
