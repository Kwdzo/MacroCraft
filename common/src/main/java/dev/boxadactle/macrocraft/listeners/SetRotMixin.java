package dev.boxadactle.macrocraft.listeners;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;


@Mixin(Entity.class)
public interface EntityInvoker {
  @Invoker("setRot")
  public boolean invokeSetRot(float x, float y);
}
